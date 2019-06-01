# Violation Comments To GitHub Command Line

[![Build Status](https://travis-ci.org/tomasbjerre/violation-comments-to-github-command-line.svg?branch=master)](https://travis-ci.org/tomasbjerre/violation-comments-to-github-command-line)
[![Bintray](https://api.bintray.com/packages/tomasbjerre/tomasbjerre/se.bjurr.violations%3Aviolation-comments-to-github-command-line/images/download.svg) ](https://bintray.com/tomasbjerre/tomasbjerre/se.bjurr.violations%3Aviolation-comments-to-github-command-line/_latestVersion)
[![NPM](https://img.shields.io/npm/v/violation-comments-to-github-command-line.svg?style=flat-square) ](https://www.npmjs.com/package/violation-comments-to-github-command-line)

Report static code analysis to GitHub. It uses the [Violations Lib](https://github.com/tomasbjerre/violations-lib).

The runnable can be found in [NPM](https://www.npmjs.com/package/violation-comments-to-github-command-line).

Run it with:

```shell
npx violation-comments-to-github-command-line \
 -ot TOKENHERE \
 -ro tomasbjerre \
 -rn violations-test \
 -prid 3 \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle" \
 -v "JSHINT" "." ".*jshint/report\.xml$" "JSHint"
```

Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Reporter | Parser | Notes
| --- | --- | ---
| [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)               | `CLANG`              | 
| [_AndroidLint_](http://developer.android.com/tools/help/lint.html)                    | `ANDROIDLINT`        | 
| [_AnsibleLint_](https://github.com/willthames/ansible-lint)                           | `FLAKE8`             | With `-p`
| [_CLang_](https://clang-analyzer.llvm.org/)                                           | `CLANG`              | 
| [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)                                | `CPD`                | 
| [_CPPCheck_](http://cppcheck.sourceforge.net/)                                        | `CPPCHECK`           | 
| [_CPPLint_](https://github.com/theandrewdavis/cpplint)                                | `CPPLINT`            | 
| [_CSSLint_](https://github.com/CSSLint/csslint)                                       | `CSSLINT`            | 
| [_Checkstyle_](http://checkstyle.sourceforge.net/)                                    | `CHECKSTYLE`         | 
| [_CodeNarc_](http://codenarc.sourceforge.net/)                                        | `CODENARC`           | 
| [_Detekt_](https://github.com/arturbosch/detekt)                                      | `CHECKSTYLE`         | With `--output-format xml`.
| [_DocFX_](http://dotnet.github.io/docfx/)                                             | `DOCFX`              | 
| [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)                                   | `CLANG`              | 
| [_ERB_](https://www.puppetcookbook.com/posts/erb-template-validation.html)            | `CLANG`              | With `erb -P -x -T '-' "${it}" \| ruby -c 2>&1 >/dev/null \| grep '^-' \| sed -E 's/^-([a-zA-Z0-9:]+)/${filename}\1 ERROR:/p' > erbfiles.out`.
| [_ESLint_](https://github.com/sindresorhus/grunt-eslint)                              | `CHECKSTYLE`         | With `format: 'checkstyle'`.
| [_Findbugs_](http://findbugs.sourceforge.net/)                                        | `FINDBUGS`           | 
| [_Flake8_](http://flake8.readthedocs.org/en/latest/)                                  | `FLAKE8`             | 
| [_FxCop_](https://en.wikipedia.org/wiki/FxCop)                                        | `FXCOP`              | 
| [_GCC_](https://gcc.gnu.org/)                                                         | `CLANG`              | 
| [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)        | `GENDARME`           | 
| [_GoLint_](https://github.com/golang/lint)                                            | `GOLINT`             | 
| [_GoVet_](https://golang.org/cmd/vet/)                                                | `GOLINT`             | Same format as GoLint.
| [_GolangCI-Lint_](https://github.com/golangci/golangci-lint/)                         | `CHECKSTYLE`         | With `--out-format=checkstyle`.
| [_GoogleErrorProne_](https://github.com/google/error-prone)                           | `GOOGLEERRORPRONE`   | 
| [_IAR_](https://www.iar.com/iar-embedded-workbench/)                                  | `IAR`                | With `--no_wrap_diagnostics`
| [_Infer_](http://fbinfer.com/)                                                        | `PMD`                | Facebook Infer. With `--pmd-xml`.
| [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)                         | `JCREPORT`           | 
| [_JSHint_](http://jshint.com/)                                                        | `JSHINT`             | 
| [_KTLint_](https://github.com/shyiko/ktlint)                                          | `CHECKSTYLE`         | 
| [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)  | `KLOCWORK`           | 
| [_KotlinGradle_](https://github.com/JetBrains/kotlin)                                 | `KOTLINGRADLE`       | Output from Kotlin Gradle Plugin.
| [_KotlinMaven_](https://github.com/JetBrains/kotlin)                                  | `KOTLINMAVEN`        | Output from Kotlin Maven Plugin.
| [_Lint_]()                                                                            | `LINT`               | A common XML format, used by different linters.
| [_MSCpp_](https://visualstudio.microsoft.com/vs/features/cplusplus/)                  | `MSCPP`              | 
| [_Mccabe_](https://pypi.python.org/pypi/mccabe)                                       | `FLAKE8`             | 
| [_MyPy_](https://pypi.python.org/pypi/mypy-lang)                                      | `MYPY`               | 
| [_NullAway_](https://github.com/uber/NullAway)                                        | `GOOGLEERRORPRONE`   | Same format as Google Error Prone.
| [_PCLint_](http://www.gimpel.com/html/pcl.htm)                                        | `PCLINT`             | PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
| [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer)                               | `CHECKSTYLE`         | With `phpcs api.php --report=checkstyle`.
| [_PHPPMD_](https://phpmd.org/)                                                        | `PMD`                | With `phpmd api.php xml ruleset.xml`.
| [_PMD_](https://pmd.github.io/)                                                       | `PMD`                | 
| [_Pep8_](https://github.com/PyCQA/pycodestyle)                                        | `FLAKE8`             | 
| [_PerlCritic_](https://github.com/Perl-Critic)                                        | `PERLCRITIC`         | 
| [_PiTest_](http://pitest.org/)                                                        | `PITEST`             | 
| [_Puppet-Lint_](http://puppet-lint.com/)                                              | `CLANG`              | With `-log-format %{fullpath}:%{line}:%{column}: %{kind}: %{message}`
| [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)                               | `PYDOCSTYLE`         | 
| [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)                                   | `FLAKE8`             | 
| [_PyLint_](https://www.pylint.org/)                                                   | `PYLINT`             | With `pylint --output-format=parseable`.
| [_ReSharper_](https://www.jetbrains.com/resharper/)                                   | `RESHARPER`          | 
| [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/)                      | `CLANG`              | With `rubycop -f clang file.rb`
| [_SbtScalac_](http://www.scala-sbt.org/)                                              | `SBTSCALAC`          | 
| [_Scalastyle_](http://www.scalastyle.org/)                                            | `CHECKSTYLE`         | 
| [_Simian_](http://www.harukizaemon.com/simian/)                                       | `SIMIAN`             | 
| [_Sonar_](https://www.sonarqube.org/)                                                 | `SONAR`              | With `mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json`. Removed in 7.7, see [SONAR-11670](https://jira.sonarsource.com/browse/SONAR-11670).
| [_Spotbugs_](https://spotbugs.github.io/)                                             | `FINDBUGS`           | 
| [_StyleCop_](https://stylecop.codeplex.com/)                                          | `STYLECOP`           | 
| [_SwiftLint_](https://github.com/realm/SwiftLint)                                     | `CHECKSTYLE`         | With `--reporter checkstyle`.
| [_TSLint_](https://palantir.github.io/tslint/usage/cli/)                              | `CHECKSTYLE`         | With `-t checkstyle`
| [_XMLLint_](http://xmlsoft.org/xmllint.html)                                          | `XMLLINT`            | 
| [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)                    | `YAMLLINT`           | With `-f parsable`
| [_ZPTLint_](https://pypi.python.org/pypi/zptlint)                                     | `ZPTLINT`            |

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!

# Usage

```shell
-comment-only-changed-content, -cocc <boolean>          <boolean>: true or false
                                                        Default: true
-comment-template <string>                              See https://github.
                                                        com/tomasbjerre/violation-comments-lib
                                                        <string>: any string
                                                        Default: 
-create-comment-with-all-single-file-comments, -        <boolean>: true or false
ccwasfc <boolean>                                       Default: false
-create-single-file-comments, -csfc <boolean>           <boolean>: true or false
                                                        Default: true
-github-url, -ghu <string>                              <string>: any string
                                                        Default: https://api.github.com/
-h, --help <argument-to-print-help-for>                 <argument-to-print-help-for>: an argument to print help for
                                                        Default: If no specific parameter is given the whole usage text is given
-keep-old-comments <boolean>                            <boolean>: true or false
                                                        Default: false
-max-number-of-violations, -max <integer>               <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 2,147,483,647
-oauth2-token, -ot <string>                             <string>: any string
                                                        Default: 
-password, -p <string>                                  <string>: any string
                                                        Default: 
-pull-request-id, -prid <string>                        <string>: any string [Required]
-repository-name, -rn <string>                          Example: 'violations-
                                                        test' [Required]
                                                        <string>: any string
-repository-owner, -ro <string>                         Example: 'tomasbjerre' [Required]
                                                        <string>: any string
-severity, -s <SEVERITY>                                Minimum severity level 
                                                        to report.
                                                        <SEVERITY>: {INFO | WARN | ERROR}
                                                        Default: INFO
-show-debug-info                                        Please run your 
                                                        command with this parameter 
                                                        and supply output when 
                                                        reporting bugs.
                                                        Default: disabled
-username, -u <string>                                  <string>: any string
                                                        Default: 
--violations, -v <string>                               The violations to look 
                                                        for. <PARSER> <FOLDER> 
                                                        <REGEXP PATTERN> <NAME> where 
                                                        PARSER is one of: 
                                                        ANDROIDLINT, CHECKSTYLE, CODENARC, 
                                                        CLANG, CPD, CPPCHECK, 
                                                        CPPLINT, CSSLINT, FINDBUGS, 
                                                        FLAKE8, FXCOP, GENDARME, IAR, 
                                                        JCREPORT, JSHINT, LINT, 
                                                        KLOCWORK, KOTLINMAVEN, 
                                                        KOTLINGRADLE, MSCPP, MYPY, GOLINT, 
                                                        GOOGLEERRORPRONE, PERLCRITIC, PITEST, 
                                                        PMD, PYDOCSTYLE, PYLINT, 
                                                        RESHARPER, SBTSCALAC, SIMIAN, 
                                                        SONAR, STYLECOP, XMLLINT, 
                                                        YAMLLINT, ZPTLINT, DOCFX, PCLINT
                                                        
                                                         Example: -v "JSHINT" 
                                                        "." ".*/jshint.xml$" 
                                                        "JSHint" [Supports Multiple occurrences]
                                                        <string>: any string
                                                        Default: Empty list
```

Checkout the [Violations Lib](https://github.com/tomasbjerre/violations-lib) for more documentation.