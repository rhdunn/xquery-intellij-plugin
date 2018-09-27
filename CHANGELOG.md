# Change Log

## 1.3 - (In Development)

Language Support:

1.  Recognise Saxon `UnionType` constructs in `TypedMapTest` sequence types.
1.  Support `TupleType` constructs with names only, not associated sequence types.

Module Import Resolution:

1.  Use project source roots to resolve module import URIs.
1.  Locate built-in definitions from `http://`-based `URILiteral` nodes.
1.  Resolve all location URIs in a `ModuleImport`, not just the first valid location.
1.  Resolve `ModuleImport` declarations that don't specify `at` paths.

EQName Resolution:

1.  Expand `NCName`-based `EQName` nodes, using the default element or function
    namespaces where appropriate.
1.  Resolve EQNames bound to `NamespaceDecl` declarations.
1.  Resolve EQNames bound to `DefaultNamespaceDecl` declarations.
1.  Resolve EQNames bound to `DirAttributeList` attributes.

Function Resolution:

1.  Matching statically-known functions against expanded QNames.
1.  Search the `MainModule`/`LibraryModule` prolog for function declarations.

## 1.2 - 2018-08-27

Language Support:

1.  Support BaseX 8.4 `non-deterministic` function calls.
1.  Support mixing arrow and transform with expressions, allowed in BaseX.
1.  Improved performance by not creating PSI (AST) nodes for EBNF symbols that
    are just forwarding to a sub-expression.

Bug Fixes:

1.  `TransformWithExpr` containing an empty expression body should not report
    a parser error as it is allowed by the Update Facility 3.0 specification.
1.  Parse `UpdateExpr` correctly, according to how it is handled in BaseX.
1.  Don't crash when computing folding for a partial direct element node inside
    an outer direct element node.

## 1.1 - 2018-04-10

Language Support:

1.  Support using MarkLogic 8.0 `array-node()` and `object-node()` tests in `document-node()` tests.
1.  Fix parsing Saxon 9.8 `tuple` types with whitespace or comments after the
    item type with an occurrence indicator and before the comma.
1.  Fix parsing Saxon 9.8 `union` types with whitespace or comments after the
    QName and before the comma.
1.  Fix parsing `TypedFunctionTest` constructs with whitespace or comments after the
    item type with an occurrence indicator and before the comma.
1.  Improve error recovery when parsing incomplete QNames in direct element constructor
    start and end tags, e.g. `<a:></a:>`.
1.  Fix tokenizing ``` ``[...`]`` ```.

Syntax Highlighting:

1.  Highlight NCNames in `processing-instruction` StringLiterals.

References and Resolving:

1.  Only the variable for the active `typeswitch` `case`/`default` clause should be in scope.
1.  Fix `for`/`let` bindings so previous binding variables are visible from the `in` expression.
1.  Fix checking multiple declared variables in a Scripting Extensions `BlockVarDecl` node.

Inspections:

1.  XQST0118: Mismatched open and close XML tag in direct element constructors.

## 1.0 - 2017-11-30

Language Support:

1.  Full Text 1.0 and 3.0 support.
1.  MarkLogic 7.0 and 8.0 schema-components `KindType` tests.
1.  Saxon 9.8 `tuple` and `union` types, and `declare type` prolog statements.
1.  BaseX Full Text `fuzzy` option.
1.  Recover when `=` is used instead of `:=`.
1.  Improved multi-statement expression parsing and error reporting for MarkLogic 6.0, and
    W3C Scripting XQuery extensions.

Bug Fixes:

1.  Fix tokenizing `` `{...}` `` expressions outside XQuery 3.1 string interpolation contexts.
1.  Report a parser error when a `TypedFunctionTest` does not have a return type clause.
1.  Report a parse error when an unpaired `}` is used in direct element contents.
1.  Fix parsing pragmas with EQNames, and optional pragma contents.
1.  Fix resolving functions referenced in the current library module.
1.  Fix support for `ProcessCanceledException` thrown when creating PSI nodes.

Inspections:

1.  XPST0003: Final statement semicolon inspection for MarkLogic and Scripting Extension
    syntax differences.
1.  XPST0003: Reserved function inspection. This is split out from the unsupported construct
    inspection.
1.  XPST0003: Saxon/XQuery map key-value separators. This is split out from the unsupported
    construct inspection.
1.  XPST0017: Undefined function inspection. This is experimental, as it
    currently reports many false positives.
1.  XQST0031: Extend the inspection to check unsupported XQuery versions in different MarkLogic
    transactions, and check for `XDMP-XQUERYVERSIONSWITCH` errors.
1.  XQST0033: Duplicate namespace prefix.
1.  XPST0081: Unbound QName prefix.

IntelliJ Integration:

1.  Moved the MarkLogic run configuration to a separate plugin.

## 0.5 - 2017-07-18

Language Support:

1.  MarkLogic 9.0 support.
2.  Interpret `expr <ncname` as part of an expression, not as a
    `DirElemConstructor` when not followed by an attribute list or closing
    element tag. XQuery engines support this usage.
3.  Allow empty `EnclosedExpr` elements in `CompTextConstructor`, `DirAttributeValue`
    and `DirElemContent` for MarkLogic.

Bug Fixes:

1.  Fix processing NCNames in `map` keys and values separated by spaces.
2.  Fix a crash when checking conformance of `map` constructs without a `:`.

IntelliJ Integration:

1.  Support code folding for `EnclosedExpr`, `DirElemConstructor` and `Comment`
    elements;
2.  Initial support for MarkLogic run configurations.

## 0.4 - 2017-01-03

Language Support:

1.  Complete support for XQuery Scripting Extensions 1.0.
2.  Support highlighting xqDoc parameter names.
3.  Support highlighting XML `PredefinedEntityRef` and `CharRef` tokens in
    xqDoc comments.
4.  Support BaseX 7.8 and 8.5 `update` expressions.

Bug Fixes:

1.  Don't crash when resolving an empty `URILiteral` in an import statement.
2.  Fix highlighting `:` at the start of a line in xqDoc comments.
3.  `array-node {}` is valid in MarkLogic 8.0.

## 0.3 - 2016-11-30

Language Support:

1.  Complete support for XQuery 3.1 constructs.
2.  Complete support for XQuery Update Facility 3.0.
3.  Improved error reporting on unbalanced XML (`DirElemConstructor` parsing).
4.  Support for xqDoc documentation comments.

IntelliJ Integration:

1.  Paired brace matching.
2.  Commenting code support.
3.  Fixed `VersionDecl` conformance checks on invalid version strings.

## 0.2.1 - 2016-11-05

1.  Fix a NullPointerException when resolving NCName-based function calls.
2.  Fix an IndexOutOfBoundsException when the charset detection logic is called
    from multiple threads (e.g. during indexing).

## 0.2 - 2016-10-30

1.  Use an inspection -- not the parser -- to check and report constructs from a
    different XQuery version or extension.
2.  Improved error recovery when parsing library modules.
3.  Context-based highlighting of identifiers (QName prefices, annotations and
    keyword-based identifiers).

Language Support:

1.  Complete support for XQuery 3.0 constructs.
2.  Complete support for XQuery Update Facility 1.0.
3.  Complete support for MarkLogic 1.0-ml extensions in MarkLogic 6.0 and 8.0.
4.  Fix `InstanceofExpr` and `TreatExpr` -- they expect a `SequenceType`, not a
    `SingleType`.

IntelliJ Integration:

1.  Resolve URILiteral string references.
2.  Resolve QName prefices to namespace declarations.
3.  Resolve function usage to the corresponding function declaration.
4.  Resolve variable usage to the corresponding variable declaration.
5.  Find usages.

## 0.1 - 2016-09-10

1.  XQuery file type support, including encoding detection.
2.  Conforming XQuery 1.0 syntax highlighter and parser.
