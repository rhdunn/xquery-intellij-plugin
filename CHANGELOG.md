# Change Log

## 0.6 - (In Development)

Language Support:

1.  MarkLogic 7.0 and 8.0 schema-components `KindType` tests.

Bug Fixes:

1.  Report a parser error when a `TypedFunctionTest` does not have a return type clause.

Inspections:

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
