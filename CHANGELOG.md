# Change Log

## 1.10.0 - (In Development)

Inlay Parameters:

1. Don't display inlay parameter hints when a `UnaryLookup` or `Lookup` name matches the parameter name.
1. Don't display inlay parameter hints when the name at the end of a `SimpleMapExpr` matches the parameter name.

Syntax Highlighting:

1. Default to the XML entity reference colours for entity references in XQuery.
1. Workaround [IDEA-234709](https://youtrack.jetbrains.com/issue/IDEA-234709) to correctly highlight XML namespace
   prefices when using the Darcula theme.

XPath:

1. Fix parsing entity references before closing string quotes, such as `'&amp;'`.

References and Resolve:

1. Fix locating the function name when the function declaration contains a compatibility annotation.
1. Fix locating the variable name when the variable declaration contains a compatibility annotation.

Code Folding:

1. Fix detecting empty enclosed expressions that contain an XQuery comment.
1. Fix the code folding placeholder text for some enclosed expressions.

Code Completion:

1. Fix a crash when autocompleting in a `StringLiteral` from an arrow function call.

Syntax Validation:

1. Don't warn if using an `InlineFunctionExpr` with parameters when targetting XQuery 3.0 or 3.1.

XRay Tests:

1. Fix running the XRay tests on IntelliJ 2019.3 due to a missing "Console" message string.
1. Fix various crashes when parsing XRay test result output.
1. Fix test suites and test suite statistics for module-level errors in the HTML and XUnit XML output formats.
1. Fix serializing XML expected/actual values in Text, HTML, and XRay XML output formats.

MarkLogic:

1. Display the MarkLogic vendor code in query errors when no standard code is provided.

## 2021

*  [1.9.0 - 2021-03-31](docs/_posts/2021-03-31-release-1.9.0.md)

## 2020

*  [1.8.1 - 2020-12-11](docs/_posts/2020-12-11-release-1.8.1.md)
*  [1.8.0 - 2020-11-12](docs/_posts/2020-11-12-release-1.8.0.md)
*  [1.7.2 - 2020-08-31](docs/_posts/2020-08-31-release-1.7.2.md)
*  [1.7.1 - 2020-07-25](docs/_posts/2020-07-25-release-1.7.1.md)
*  [1.7 - 2020-07-10](docs/_posts/2020-07-10-release-1.7.md)
*  [1.6.2 - 2020-05-04](docs/_posts/2020-05-04-release-1.6.2.md)
*  [1.6.1 - 2020-04-09](docs/_posts/2020-04-09-release-1.6.1.md)
*  [1.6 - 2020-03-21](docs/_posts/2020-03-21-release-1.6.md)

## 2019

*  [1.5.2 - 2019-11-19](docs/_posts/2019-11-19-release-1.5.2.md)
*  [1.5.1 - 2019-08-01](docs/_posts/2019-08-01-release-1.5.1.md)
*  [1.5 - 2019-07-18](docs/_posts/2019-07-18-release-1.5.md)
*  [1.4.1 - 2019-03-27](docs/_posts/2019-03-27-release-1.4.1.md)
*  [1.4 - 2019-03-24](docs/_posts/2019-03-24-release-1.4.md)

## 2018

*  [1.3 - 2018-11-10](docs/_posts/2018-11-10-release-1.3.md)
*  [1.2 - 2018-08-27](docs/_posts/2018-08-27-release-1.2.md)
*  [1.1 - 2018-04-10](docs/_posts/2018-04-10-release-1.1.md)

## 2017

*  [1.0 - 2017-11-30](docs/_posts/2017-11-30-release-1.0.md)
*  [0.5 - 2017-07-18](docs/_posts/2017-07-18-release-0.5.md)
*  [0.4 - 2017-01-03](docs/_posts/2017-01-03-release-0.4.md)

## 2016

*  [0.3 - 2016-11-30](docs/_posts/2016-11-30-release-0.3.md)
*  [0.2.1 - 2016-11-05](docs/_posts/2016-11-05-release-0.2.1.md)
*  [0.2 - 2016-10-30](docs/_posts/2016-10-30-release-0.2.md)
*  [0.1 - 2016-09-10](docs/_posts/2016-09-10-release-0.1.md)
