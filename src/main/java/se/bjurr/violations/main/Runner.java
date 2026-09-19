package se.bjurr.violations.main;

import static se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi.violationCommentsToGitHubApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import se.bjurr.violations.lib.FilteringViolationsLogger;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;

@Command(name = "violation-comments-to-github-command-line")
public class Runner {

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Show this help message and exit.")
  private boolean help;

  @Option(
      names = {"--violations", "-v"},
      arity = "4",
      description =
          "The violations to look for. <PARSER> <FOLDER> <REGEXP PATTERN> <NAME> where PARSER"
              + " is one of the values of se.bjurr.violations.lib.reports.Parser (see supported"
              + " formats table in README for the full list).\nExample: -v \"JSHINT\" \".\""
              + " \".*/jshint.xml$\" \"JSHint\"")
  private List<String> violations = new ArrayList<>(); // NOPMD picocli reflection

  @Option(
      names = {"-severity", "-s"},
      description = "Minimum severity level to report.")
  private SEVERITY minSeverity = INFO; // NOPMD picocli reflection

  @Option(
      names = "-show-debug-info",
      description =
          "Please run your command with this parameter and supply output when reporting bugs.")
  private boolean showDebugInfo;

  @Option(
      names = {"-comment-only-changed-content", "-cocc"},
      arity = "1")
  private boolean commentOnlyChangedContent = true; // NOPMD picocli reflection

  @Option(
      names = {"-comment-only-changed-files", "-cocf"},
      arity = "1",
      description =
          "True if only changed files should be commented. False if all findings should be commented.")
  private boolean commentOnlyChangedFiles = true; // NOPMD picocli reflection

  @Option(
      names = {"-create-comment-with-all-single-file-comments", "-ccwasfc"},
      arity = "1")
  private boolean createCommentWithAllSingleFileComments = false; // NOPMD picocli reflection

  @Option(
      names = {"-create-single-file-comments", "-csfc"},
      arity = "1")
  private boolean createSingleFileComments = true; // NOPMD picocli reflection

  @Option(
      names = {"-use-review-comments", "-urc"},
      arity = "1",
      description =
          "True if single file comments should be batched into one pull request review"
              + " instead of one HTTP request per comment. GitHub applies this atomically:"
              + " if any comment in the batch has an invalid diff position, none of them are"
              + " created.")
  private boolean useReviewComments = false; // NOPMD picocli reflection

  @Option(names = "-keep-old-comments", arity = "1")
  private boolean keepOldComments; // NOPMD picocli reflection

  @Option(
      names = "-comment-template",
      description = "See https://github.com/tomasbjerre/violation-comments-lib")
  private String commentTemplate = ""; // NOPMD picocli reflection

  @Option(
      names = {"-repository-owner", "-ro"},
      required = true,
      description = "Example: 'tomasbjerre'")
  private String repositoryOwner;

  @Option(
      names = {"-repository-name", "-rn"},
      required = true,
      description = "Example: 'violations-test'")
  private String repositoryName;

  /**
   * Travis will define TRAVIS_PULL_REQUEST as "false" if not a PR, and an integer if a PR. Having
   * this as String makes life easier =)
   */
  @Option(
      names = {"-pull-request-id", "-prid"},
      required = true)
  private String pullRequestId;

  @Option(names = {"-oauth2-token", "-ot"})
  private String oAuth2Token = ""; // NOPMD picocli reflection

  @Option(names = {"-username", "-u"})
  private String username = ""; // NOPMD picocli reflection

  @Option(names = {"-password", "-p"})
  private String password = ""; // NOPMD picocli reflection

  @Option(names = {"-github-url", "-ghu"})
  private String gitHubUrl = "https://api.github.com/"; // NOPMD picocli reflection

  @Option(names = {"-max-number-of-violations", "-max"})
  private Integer maxNumberOfViolations = Integer.MAX_VALUE; // NOPMD picocli reflection

  public void main(final String... args) throws Exception {
    final CommandLine commandLine = new CommandLine(this);
    try {
      commandLine.parseArgs(args);
    } catch (final ParameterException exception) {
      System.out.println(exception.getMessage()); // NOPMD
      exception.getCommandLine().usage(System.out);
      System.exit(1); // NOPMD
      return;
    }

    if (commandLine.isUsageHelpRequested()) {
      commandLine.usage(System.out);
      return;
    }

    if (this.showDebugInfo) {
      System.out.println( // NOPMD
          "Given parameters:\n"
              + Arrays.asList(args).stream()
                  .map((it) -> it.toString())
                  .collect(Collectors.joining(", "))
              + "\n\nParsed parameters:\n"
              + this.toString());
    }

    ViolationsLogger violationsLogger =
        new ViolationsLogger() {
          @Override
          public void log(final Level level, final String string) {
            System.out.println(level + " " + string); // NOPMD
          }

          @Override
          @SuppressFBWarnings(
              value = "INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE",
              justification =
                  "Printing the stack trace to this CLI's own stdout is the intended behavior")
          public void log(final Level level, final String string, final Throwable t) {
            final StringWriter sw = new StringWriter();
            t.printStackTrace(
                new PrintWriter(sw)); // NOPMD writes to an in-memory buffer, not System.err
            System.out.println(level + " " + string + "\n" + sw.toString()); // NOPMD
          }
        };
    if (!this.showDebugInfo) {
      violationsLogger = FilteringViolationsLogger.filterLevel(violationsLogger);
    }

    if (this.pullRequestId == null || this.pullRequestId.equalsIgnoreCase("false")) {
      System.out.println( // NOPMD
          "No pull request id defined, will not send violation comments to GitHub.");
      return;
    }
    if (this.oAuth2Token != null) {
      System.out.println("Using OAuth2Token"); // NOPMD
    } else if (this.username != null && this.password != null) {
      System.out.println( // NOPMD
          "Using username/password: " + maskUsername(this.username) + ".../*********");
    } else {
      System.err.println( // NOPMD
          "No OAuth2 token and no username/email specified. Will not comment any pull request.");
      return;
    }

    System.out.println( // NOPMD
        "Will comment PR "
            + this.repositoryOwner
            + "/"
            + this.repositoryName
            + "/"
            + this.pullRequestId
            + " on "
            + this.gitHubUrl);

    Set<Violation> allParsedViolations = new TreeSet<>();
    for (int i = 0; i < this.violations.size(); i += 4) {
      final List<String> configuredViolation = this.violations.subList(i, i + 4);
      final String reporter = configuredViolation.get(3);
      final Set<Violation> parsedViolations =
          violationsApi() //
              .withViolationsLogger(violationsLogger) //
              .findAll(Parser.valueOf(configuredViolation.get(0))) //
              .inFolder(configuredViolation.get(1)) //
              .withPattern(configuredViolation.get(2)) //
              .withReporter(reporter) //
              .violations();
      if (this.minSeverity != null) {
        allParsedViolations = Filtering.withAtLEastSeverity(allParsedViolations, this.minSeverity);
      }
      allParsedViolations.addAll(parsedViolations);
    }

    try {
      violationCommentsToGitHubApi()
          .withoAuth2Token(this.oAuth2Token)
          .withUsername(this.username)
          .withPassword(this.password)
          .withPullRequestId(Integer.parseInt(this.pullRequestId))
          .withRepositoryName(this.repositoryName)
          .withRepositoryOwner(this.repositoryOwner)
          .withGitHubUrl(this.gitHubUrl)
          .withViolations(allParsedViolations)
          .withCreateCommentWithAllSingleFileComments(
              this.createCommentWithAllSingleFileComments) //
          .withCreateSingleFileComments(this.createSingleFileComments) //
          .withUseReviewComments(this.useReviewComments) //
          .withCommentOnlyChangedContent(this.commentOnlyChangedContent) //
          .withCommentOnlyChangedFiles(this.commentOnlyChangedFiles) //
          .withKeepOldComments(this.keepOldComments) //
          .withCommentTemplate(this.commentTemplate) //
          .withMaxNumberOfViolations(this.maxNumberOfViolations) //
          .withViolationsLogger(violationsLogger) //
          .toPullRequest();
    } catch (final Exception e) {
      e.printStackTrace(); // NOPMD
    }
  }

  private static String maskUsername(final String username) {
    return username.isEmpty() ? "" : username.substring(0, 1);
  }

  @Override
  public String toString() {
    return "Runner [repositoryOwner="
        + this.repositoryOwner
        + ", repositoryName="
        + this.repositoryName
        + ", pullRequestId="
        + this.pullRequestId
        + ", oAuth2Token="
        + this.oAuth2Token
        + ", username="
        + this.username
        + ", password="
        + this.password
        + ", gitHubUrl="
        + this.gitHubUrl
        + ", violations="
        + this.violations
        + ", createCommentWithAllSingleFileComments="
        + this.createCommentWithAllSingleFileComments
        + ", createSingleFileComments="
        + this.createSingleFileComments
        + ", useReviewComments="
        + this.useReviewComments
        + ", commentOnlyChangedContent="
        + this.commentOnlyChangedContent
        + ", minSeverity="
        + this.minSeverity
        + ", keepOldComments="
        + this.keepOldComments
        + ", commentTemplate="
        + this.commentTemplate
        + ", maxNumberOfViolations="
        + this.maxNumberOfViolations
        + "]";
  }
}
