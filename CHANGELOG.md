# Change Log

## 1.9.0 - (In Development)

XPath, XQuery, and XSLT 4.0 Editor's Draft 13 January 2021:

1. Add the XSLT 4.0 Editor's Draft XMLSchema file.
1. Support any `ItemType` on `LocalUnionType` and `TypedMapTest`.
1. Support nesting `TernaryConditionalExpr` expressions.
1. Support `->` in addition to `function` in `InlineFunctionExpr` expressions,
   with optional function signatures.
1. Support `->` based `ArrowExpr` expressions.
1. Support named arguments in function calls.
1. Support `RecordTest` and `EnumerationType` item types.
1. Support `StringLiteral` and `VarRef` for `Lookup` and `UnaryLookup`.
1. Support `WithExpr` expressions.
1. Support `type` in `DefaultNamespaceDecl` declarations in XQuery.
1. Support item type declarations in XQuery.

XRay Unit Tests:

1. Add an XRay run configuration.
1. Support running, profiling, and debugging XRay unit tests.
1. Add run actions in the line marker area.
1. Report test status in the run console.

Semantic Highlighting:

1. Fix highlighting `xmlns` namespace prefix in contexts other than XML attributes.
1. Highlight the `CompNamespaceConstructor` name as a namespace prefix.
1. Highlight `KeySeparator` names and `RecordTest` field names as map keys.
1. Highlight `KeywordArgument` names as parameters.
1. Highlight `WithExpr` using the same logic as `DirAttribute`.

Run Configuration:

1. Support using the modules database for selected MarkLogic app-server.
1. Filter the app-servers to the ones that match the selected database.
1. Fix some encoding issues in handling UTF-8 output for MarkLogic.
1. Fix reformatting the output when the option is specified in the configuration.

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
