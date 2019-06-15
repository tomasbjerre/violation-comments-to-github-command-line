package se.bjurr.violations.main;

import static se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi.violationCommentsToGitHubApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.softhouse.jargo.Arguments.booleanArgument;
import static se.softhouse.jargo.Arguments.enumArgument;
import static se.softhouse.jargo.Arguments.helpArgument;
import static se.softhouse.jargo.Arguments.integerArgument;
import static se.softhouse.jargo.Arguments.optionArgument;
import static se.softhouse.jargo.Arguments.stringArgument;
import static se.softhouse.jargo.CommandLineParser.withArguments;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import se.bjurr.violations.comments.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

public class Runner {

  private String repositoryOwner;
  private String repositoryName;
  /**
   * Travis will define TRAVIS_PULL_REQUEST as "false" if not a PR, and an integer if a PR. Having
   * this as String makes life easier =)
   */
  private String pullRequestId;

  private String oAuth2Token;
  private String username;
  private String password;
  private String gitHubUrl;
  private List<List<String>> violations = new ArrayList<>();
  private boolean createCommentWithAllSingleFileComments = false;
  private boolean createSingleFileComments = true;
  private boolean commentOnlyChangedContent = true;
  private SEVERITY minSeverity;
  private boolean keepOldComments;
  private String commentTemplate;
  private Integer maxNumberOfViolations;
  private boolean commentOnlyChangedFiles = true;

  public void main(final String args[]) throws Exception {
    final Argument<?> helpArgument = helpArgument("-h", "--help");
    final String parsersString =
        Arrays.asList(Parser.values())
            .stream()
            .map((it) -> it.toString())
            .collect(Collectors.joining(", "));
    final Argument<List<List<String>>> violationsArg =
        stringArgument("--violations", "-v")
            .variableArity()
            .repeated()
            .description(
                "The violations to look for. <PARSER> <FOLDER> <REGEXP PATTERN> <NAME> where PARSER is one of: "
                    + parsersString
                    + "\n Example: -v \"JSHINT\" \".\" \".*/jshint.xml$\" \"JSHint\"")
            .build();
    final Argument<SEVERITY> minSeverityArg =
        enumArgument(SEVERITY.class, "-severity", "-s")
            .defaultValue(INFO)
            .description("Minimum severity level to report.")
            .build();
    final Argument<Boolean> showDebugInfo =
        optionArgument("-show-debug-info")
            .description(
                "Please run your command with this parameter and supply output when reporting bugs.")
            .build();

    final Argument<Boolean> commentOnlyChangedContentArg =
        booleanArgument("-comment-only-changed-content", "-cocc").defaultValue(true).build();
    final Argument<Boolean> commentOnlyChangedFilesArg =
        booleanArgument("-comment-only-changed-files", "-cocf").defaultValue(true).build();
    final Argument<Boolean> createCommentWithAllSingleFileCommentsArg =
        booleanArgument("-create-comment-with-all-single-file-comments", "-ccwasfc")
            .defaultValue(false)
            .build();
    final Argument<Boolean> createSingleFileCommentsArg =
        booleanArgument("-create-single-file-comments", "-csfc").defaultValue(true).build();
    final Argument<Boolean> keepOldCommentsArg =
        booleanArgument("-keep-old-comments").defaultValue(false).build();
    final Argument<String> commentTemplateArg =
        stringArgument("-comment-template")
            .defaultValue("")
            .description("See https://github.com/tomasbjerre/violation-comments-lib")
            .build();
    final Argument<String> repositoryOwnerArg =
        stringArgument("-repository-owner", "-ro")
            .description("Example: 'tomasbjerre'")
            .required()
            .build();
    final Argument<String> repositoryNameArg =
        stringArgument("-repository-name", "-rn")
            .required()
            .description("Example: 'violations-test'")
            .build();
    final Argument<String> pullRequestIdArg =
        stringArgument("-pull-request-id", "-prid").required().build();
    final Argument<String> oAuth2TokenArg =
        stringArgument("-oauth2-token", "-ot").defaultValue("").build();
    final Argument<String> usernameArg = stringArgument("-username", "-u").defaultValue("").build();
    final Argument<String> passwordArg = stringArgument("-password", "-p").defaultValue("").build();
    final Argument<String> gitHubUrlArg =
        stringArgument("-github-url", "-ghu").defaultValue("https://api.github.com/").build();
    final Argument<Integer> maxNumberOfViolationsArg =
        integerArgument("-max-number-of-violations", "-max")
            .defaultValue(Integer.MAX_VALUE)
            .build();

    try {
      final ParsedArguments parsed =
          withArguments( //
                  helpArgument, //
                  violationsArg, //
                  minSeverityArg, //
                  showDebugInfo, //
                  commentOnlyChangedContentArg, //
                  commentOnlyChangedFilesArg, //
                  createCommentWithAllSingleFileCommentsArg, //
                  createSingleFileCommentsArg, //
                  keepOldCommentsArg, //
                  commentTemplateArg, //
                  repositoryOwnerArg, //
                  repositoryNameArg, //
                  pullRequestIdArg, //
                  oAuth2TokenArg, //
                  usernameArg, //
                  passwordArg, //
                  gitHubUrlArg, //
                  maxNumberOfViolationsArg //
                  ) //
              .parse(args);

      this.violations = parsed.get(violationsArg);
      this.minSeverity = parsed.get(minSeverityArg);
      this.commentOnlyChangedContent = parsed.get(commentOnlyChangedContentArg);
      this.commentOnlyChangedFiles = parsed.get(commentOnlyChangedFilesArg);
      this.createCommentWithAllSingleFileComments =
          parsed.get(createCommentWithAllSingleFileCommentsArg);
      this.createSingleFileComments = parsed.get(createSingleFileCommentsArg);
      this.keepOldComments = parsed.get(keepOldCommentsArg);
      this.commentTemplate = parsed.get(commentTemplateArg);

      this.repositoryOwner = parsed.get(repositoryOwnerArg);
      this.repositoryName = parsed.get(repositoryNameArg);
      this.pullRequestId = parsed.get(pullRequestIdArg);
      this.oAuth2Token = parsed.get(oAuth2TokenArg);
      this.username = parsed.get(usernameArg);
      this.password = parsed.get(passwordArg);
      this.gitHubUrl = parsed.get(gitHubUrlArg);
      this.maxNumberOfViolations = parsed.get(maxNumberOfViolationsArg);

      if (parsed.wasGiven(showDebugInfo)) {
        System.out.println(
            "Given parameters:\n"
                + Arrays.asList(args)
                    .stream()
                    .map((it) -> it.toString())
                    .collect(Collectors.joining(", "))
                + "\n\nParsed parameters:\n"
                + this.toString());
      }

    } catch (final ArgumentException exception) {
      System.out.println(exception.getMessageAndUsage());
      System.exit(1);
    }

    if (pullRequestId == null || pullRequestId.equalsIgnoreCase("false")) {
      System.out.println("No pull request id defined, will not send violation comments to GitHub.");
      return;
    }
    final Integer pullRequestIdInt = Integer.valueOf(pullRequestId);
    if (oAuth2Token != null) {
      System.out.println("Using OAuth2Token");
    } else if (username != null && password != null) {
      System.out.println("Using username/password: " + username.substring(0, 1) + ".../*********");
    } else {
      System.err.println(
          "No OAuth2 token and no username/email specified. Will not comment any pull request.");
      return;
    }

    System.out.println(
        "Will comment PR "
            + repositoryOwner
            + "/"
            + repositoryName
            + "/"
            + pullRequestId
            + " on "
            + gitHubUrl);

    List<Violation> allParsedViolations = new ArrayList<>();
    for (final List<String> configuredViolation : violations) {
      final String reporter = configuredViolation.size() >= 4 ? configuredViolation.get(3) : null;
      final List<Violation> parsedViolations =
          violationsApi() //
              .findAll(Parser.valueOf(configuredViolation.get(0))) //
              .inFolder(configuredViolation.get(1)) //
              .withPattern(configuredViolation.get(2)) //
              .withReporter(reporter) //
              .violations();
      if (minSeverity != null) {
        allParsedViolations = Filtering.withAtLEastSeverity(allParsedViolations, minSeverity);
      }
      allParsedViolations.addAll(parsedViolations);
    }

    try {
      violationCommentsToGitHubApi() //
          .withoAuth2Token(oAuth2Token) //
          .withUsername(username) //
          .withPassword(password) //
          .withPullRequestId(pullRequestIdInt) //
          .withRepositoryName(repositoryName) //
          .withRepositoryOwner(repositoryOwner) //
          .withGitHubUrl(gitHubUrl) //
          .withViolations(allParsedViolations) //
          .withCreateCommentWithAllSingleFileComments(createCommentWithAllSingleFileComments) //
          .withCreateSingleFileComments(createSingleFileComments) //
          .withCommentOnlyChangedContent(commentOnlyChangedContent) //
          .withCommentOnlyChangedFiles(commentOnlyChangedFiles) //
          .withKeepOldComments(keepOldComments) //
          .withCommentTemplate(commentTemplate) //
          .withMaxNumberOfViolations(maxNumberOfViolations) //
          .withViolationsLogger(
              new ViolationsLogger() {
                @Override
                public void log(final Level level, final String string) {
                  System.out.println(level + " " + string);
                }

                @Override
                public void log(final Level level, final String string, final Throwable t) {
                  final StringWriter sw = new StringWriter();
                  t.printStackTrace(new PrintWriter(sw));
                  System.out.println(level + " " + string + "\n" + sw.toString());
                }
              }) //
          .toPullRequest();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return "Runner [repositoryOwner="
        + repositoryOwner
        + ", repositoryName="
        + repositoryName
        + ", pullRequestId="
        + pullRequestId
        + ", oAuth2Token="
        + oAuth2Token
        + ", username="
        + username
        + ", password="
        + password
        + ", gitHubUrl="
        + gitHubUrl
        + ", violations="
        + violations
        + ", createCommentWithAllSingleFileComments="
        + createCommentWithAllSingleFileComments
        + ", createSingleFileComments="
        + createSingleFileComments
        + ", commentOnlyChangedContent="
        + commentOnlyChangedContent
        + ", minSeverity="
        + minSeverity
        + ", keepOldComments="
        + keepOldComments
        + ", commentTemplate="
        + commentTemplate
        + ", maxNumberOfViolations="
        + maxNumberOfViolations
        + "]";
  }
}
