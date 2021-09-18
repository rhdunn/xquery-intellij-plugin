# Change Log

## 1.10.0 - (In Development)

IntelliJ Integration:

1. Add paired brace matching for Saxon context item and lambda functions.
2. Add paired brace matching for XPath expressions.
3. Add code commenting support for XPath.

Code Folding:

1. Add code folding for XQuery string interpolations.
2. Add code folding for Full Text, Updating, Scripting, and vendor extension expressions containing braces (`{...}`).

Syntax and Semantic Highlighting:

1. Highlight `=` in XML attributes as attribute values to match IntelliJ's highlighting behaviour.
2. Highlight direct processing instruction node (`<?xml ...?>`) tokens.

References and Resolving:

1. Fix resolving elements to `xmlns` nodes where the namespace URI is not also bound to a prefix.

Refactoring:

1. Support in-place rename refactoring for variables, except for private variable declarations.

XPath and XQuery:

1. Fix parsing `FTContainsExpr` in the RHS of a `ComparisonExpr` in XPath expressions.
2. Improve the error message when a `return` is used without a `ForExpr` or `LetExpr` in XPath expressions.
3. Don't generate an error when parsing `?$` and `?($)`, where the `VarName` is missing.
4. Improve the error message when a `QName` is used in a `DirPIConstructor`.

MarkLogic Rewriter XML Integration:

1. Use the regular expression language for `matches` attributes in XQuery direct and computed XML.
2. Add code completion for `match-accept`, `match-content-type`, `match-execute-privilege`, `match-header`,
   and `match-method` XQuery direct XML elements.
3. Support resolving `dispatch`, `set-error-handler`, and `set-path` elements to the XQuery/MJS/SJS
   file they reference in XQuery direct elements.
4. Cache the endpoints to improve the line marker performance when the data has been previously calculated.
5. In the Endpoints tool window navigate to the module file, not the rewriter XML element that references it.

MarkLogic Search Options XML Integration:

1. Support resolving `parse/@at`, `start-facet/@at`, and `finish-facet/@at` attribute values to the
   XQuery/MJS/SJS file they reference in XML files and XQuery direct attributes.
2. Support resolving `parse/@apply`, `start-facet/@apply`, and `finish-facet/@apply` attribute values
   to the XQuery function they reference in XML files and XQuery direct attributes.
3. Add line markers to functions referenced by custom facets in search options XML files and XQuery direct
   and constructed elements.

## 2021

*  [1.9.1 - 2021-07-23](docs/_posts/2021-07-23-release-1.9.1.md)
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
