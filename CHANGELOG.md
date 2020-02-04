# Change Log

## 1.6 - (In Development)

References, Resolving and Find Usages:

1.  Support `BracedURILiteral` references (`Q{...}`).
1.  Support navigating to Java classes specified by `java:` and unprefixed classpath URIs
    (BaseX, eXist-db, Saxon).
1.  Fix resolving `URIQualifiedName` functions and variables.
1.  Display the correct type name in the find usages UI for EQNames that are annotations,
    attributes, decimal formats, elements, functions, namespaces, options, parameters,
    pragmas, types, and variables.

IntelliJ Integration:

1.  Rename refactoring support for variables and functions.
1.  Implement inlay parameter hints for XQuery function calls and named arrow function expressions.
1.  Support quick documentation (Ctrl+Q) for W3C XQuery functions.
1.  Support quick documentation (Ctrl+Q) for MarkLogic modules.
1.  Support displaying the XQuery file structure view in the navigation bar on
    IntelliJ 2020.1.
1.  Display the namespace signature and module path when holding Ctrl over a module namespace URI or prefix.
1.  Support find usage type names in XPath.
1.  Fixed QName prefix syntax highlighting and keyword in QName highlighting removal when the QName contains a space.

XPath and XSLT:

1.  Make XSLT elements use the template language colour style by default.

Run Configurations:

1.  Don't crash on the run configurations page if there are no query processors.

## 1.5.2 - 2019-11-19

Code Completion:

1.  Don't crash when editing a `StringLiteral` inside an `NodeType` expression, e.g. using MarkLogic's
    `object-node("...")`.

Run Configurations:

1.  Fix saving the selected query processor in the run configuration.
1.  Improve the error handling and reporting when connecting to BaseX servers.
1.  Improve responsiveness when populating the query processor list, especially
    for servers that are not responding.
1.  Don't use HTML formatting when displaying the Context column of the Flat Profile table.

Log Viewer:

1.  When selecting a BaseX processor, default to the last (most recent) log file.
1.  When selecting a MarkLogic processor, default to the `ErrorLog.txt` log file.

## 1.5.1 - 2019-08-01

References, Resolving and Find Usages:

1.  Specify variable declarations and parameters as write access, including highlighting them in the editor.
1.  Fix an issue where some built-in functions and namespaces don't always resolve properly.

Code Completion:

1.  Don't crash when editing a `StringLiteral` inside an `AxisStep` expression, e.g. from a `PITest`.
1.  Don't list function completions in `@...`-style `AbbrevForwardStep` expressions.
1.  Don't display code completions when typing numeric literals.

IntelliJ Integration:

1.  Display the namespace signature when holding Ctrl over a `QName` prefix.
1.  Display the parameter information for a `FunctionCall` and named arrow expression function calls.
1.  Breadcrumbs support for XQuery functions, inline functions, and direct/computed element constructors.

XPath and XQuery:

1.  Support Saxon's `fn{...}` and `tuple(...)` syntax in XPath expressions.
1.  Report "Incomplete double exponent" errors for `IntegerLiteral` in addition to `DecimalLiteral`.
1.  Report an error when an NCName follows a number without whitespace.

## 1.5 - 2019-07-18

References, Resolving and Find Usages:

1.  Fix resolving and finding usages of variables declared in imported modules.
1.  Fix find usages of locally declared and imported functions.
1.  Support the MarkLogic behaviour for resolving `NCName`-based `FunctionDecl`s.
1.  Fix resolving to imported functions from declarations other than the one declaring the namespace.
1.  Provide better type names in the find usages pane.
1.  Display the `EQName` type signature in the find usages pane.

IntelliJ Integration:

1.  File structure support, listing the declared functions, variables, and types,
    and the query body in a file.
1.  Display the functions, variables, and types when "Show Members" is enabled
    in the project pane.
1.  Display the function signature when holding Ctrl over a function call.
1.  Display the variable signature when holding Ctrl over a variable reference.
1.  Use the QName annotator on Wildcard elements to correctly highlight the
    namespace prefix and local name parts.

Code Completion:

1.  In-scope variable completion support in `VarRef` expressions for XQuery.
1.  Statically-known function completion support in `FunctionCall` and `ArrowExpr` expressions for XQuery.
1.  XML Schema and `union()` type completion support in `AtomicOrUnionType` and `SimpleTypeName` for XPath and XQuery.
1.  Namespace prefix completion support in `QName`s for XPath and XQuery.
1.  Keyword completion support in `ForwardAxis` and `ReverseAxis` for XPath and XQuery.
1.  Keyword completion support in `KindTest` based `NodeTest`s and `ItemType`s for XPath and XQuery.
1.  Keyword completion support in `SequenceType`s for XPath and XQuery.

Run Configurations:

1.  Link to the files when displaying query errors in the console.
1.  Support running XPath queries as XSLT patterns (XPath subset) on the Saxon
    query processor.
1.  Support profiling Saxon XSLT and XQuery scripts.
1.  Display the elapsed time and number of items returned by the query in the
    results console.
1.  Include the console output when profiling queries.

Query Processor Integration:

1.  Add a query log viewer for BaseX and MarkLogic log files.

Saxon:

1.  Fix using Saxon 9.2 to 9.8 JAR files.
1.  Disable Saxon EE optimizations to prevent the processor throwing a
    `NoClassDefFoundError` looking for `com/saxonica/ee/bytecode/GeneratedCode`.
1.  Support `union(...)` types in XPath expressions.
1.  Support `union(...)` types in `SingleType`s for XPath and XQuery.

BaseX:

1.  Add definitions for the BaseX 9.2 built-in functions.
1.  Add support for the `perm` annotations introduced in BaseX 9.0.

MarkLogic:

1.  Add definitions for the MarkLogic 10.0-1 built-in functions.
1.  Fix MarkLogic 6.0 `binary()` used as a `NodeTest`.
1.  Support the MarkLogic 6.0 `validate full` syntax extension.
1.  Improve support for the MarkLogic 7.0 schema syntax extensions.

XPath and XSLT:

1.  Enable XPath syntax validation of expressions and patterns in XSLT when the
    XPath View + XSLT plugin is disabled.
1.  Full Text 1.0 and 3.0 extensions support in the XPath lexer and parser.

XPath and XQuery:

1.  Fix parsing decimal `CharRef`s with a single digit, e.g. `"&#9;"`.
1.  Fix parsing a NameTest that is a named computed constructor keyword like
    `element` followed by a keyword that is part of a containing expression
    (e.g. `return`) and does not have an `EnclosedExpr`.
1.  Report an error when the `EnclosedExpr` is missing from named computed
    constructors.
1.  Report a parser error when a `NumericLiteral` is followed by an `NCName` or
    `URIQualifiedName` without whitespace or comment tokens, due to them being
    non-delimiting terminal symbols.
1.  Report an error if an unknown axis name is followed by the `::` axis indicator.
1.  When recovering parser errors in a main module with a prolog, preserve the
    prolog context in the subsequent partial expressions.

Inspections:

1.  XPST0017: Enable the undefined function inspection by default.
1.  XQST0047: Don't generate an error on `ModuleImport`s without a namespace prefix.

## 1.4.1 - 2019-03-27

IntelliJ Integration:

1.  Support code folding for `DirCommentConstructor` elements (`<!--...-->`).

XPath and XQuery:

1.  Removed support for the plugin-specific annotated sequence type syntax.

Static Context:

1.  Fixed the builtin function definitions to be valid XQuery after the ItemType
    and SequenceType changes for annotated and union sequence types in 1.4.

Run Configurations:

1.  Fixed running queries on MarkLogic prior to 8.0-7 and 9.0-2.
1.  Persist the updating option in the run configuration settings.
1.  Display the name of the query being run instead of an empty string in stack traces.

## 1.4 - 2019-03-24

IntelliJ Integration:

1.  Register the XSLT 3.0 schema with IntelliJ.
1.  Display the first comment line when the comment is folded.
1.  Support profiling MarkLogic queries.
1.  Support configuring the namespace prefix colour.

Bug Fixes:

1.  Fix an issue with run configuration settings not saving in IntelliJ 2018.3
    and later.
1.  Don't highlight xqDoc tags in non-xqDoc comments.
1.  Don't throw an IllegalCharsetNameException if the encoding string spans to
    the next line.

XPath and XQuery:

1.  Support the expath-ng variadic function arguments proposal.
1.  Fix parsing QNames in NameTests where the prefix is `attribute`,
    `element`, `every`, `exit`, `namespace`, `processing-instruction`,
    `return`, or `some`.
1.  Fix parsing NCNames in FunctionCalls and NameTests where the
    prefix is `some`, or `every`.
1.  Fix parsing  ``` ``[`{...}`...`]`` ``` -- `StringConstructorInterpolation`
    at the start of a `StringConstructor`.
1.  Split out the core XPath 2.0, 3.0, and 3.1 syntax into an XPath lexer and
    parser.

Static Context:

1.  Add support for the eXist-db built-in functions and static context. Thanks
    to Bridger Dyson-Smith for providing the built-in function definitions.
1.  Add the `err` and `output` namespaces to the BaseX static context.

Function Resolution:

1.  Correctly match functions with variadic function arguments.

Inspections:

1.  XPST0017: Fix the arity check for variadic functions.
1.  XPST0118: Don't crash on code like `</<test/>`.

## 1.3 - 2018-11-10

1.  Added support for the eXist-db XQuery database.
1.  Initial support for running queries on the BaseX, eXist-db, MarkLogic and
    Saxon XQuery processors.
1.  Support for Saxon 9.9 and BaseX 9.1 syntax extensions (see below).
1.  Support for the BaseX and Saxon built-in functions. Thanks to Bridger
    Dyson-Smith for adding these.
1.  Improved resolving imported modules, names, and functions (see below).

Saxon Vendor Extensions:

1.  Recognise Saxon `UnionType` constructs in `TypedMapTest` sequence types.
1.  Support `TupleType` constructs with names only, not associated sequence types.
1.  Support Saxon 9.8 simple inline function expressions.
1.  Support Saxon 9.9 `orElse` and `andAlso` logical expressions.
1.  Support Saxon 9.9 optional field names for tuples, and extensible tuples.
1.  Support `NCName` and `URIQualifiedName` types in the `union()` syntax.

BaseX Vendor Extensions:

1.  Support the BaseX 9.1 `TernaryIfExpr` conditional expressions.
1.  Support the BaseX 9.1 `ElvisExpr` conditional expressions.
1.  Support the BaseX 9.1 if expressions without else branches.

XQuery Working Draft 02 May 2003 and MarkLogic 0.9-ml Support:

1.  Support `empty()` syntax for `empty-sequence()`.

Invalid Syntax Recovery:

1.  Recover parsing when the `CompElemConstructor`, `CompAttrConstructor`,
    `CompPIConstructor`, or `CompNamespaceConstructor` tag name is a `StringLiteral`.
1.  Recover parsing when a `SequenceType` is used in a `ParenthesizedItemType`.

Static Context:

1.  Add the `sql` namespace to the MarkLogic static context, added in MarkLogic 7.0.
1.  Add support for the BaseX built-in functions and static context. Thanks to
    Bridger Dyson-Smith for providing the built-in function definitions.
1.  Add support for the Saxon built-in functions and static context. Thanks to
    Bridger Dyson-Smith for providing the built-in function definitions.
1.  Add support for EXPath and EXQuery module functions used by BaseX and Saxon
    as built-in functions and importable modules.

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

Inspections:

1.  IJVS0001: Report warnings for constructs that require a different XQuery version.

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
