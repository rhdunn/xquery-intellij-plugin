# Change Log

## 0.2 - (In Development)

1.  Use an inspection -- not the parser -- to check and report constructs from a
    different XQuery version or extension.
2.  Improved error recovery when parsing library modules.
3.  Context-based highlighting of identifiers (QName prefices, annotations and
    keyword-based identifiers).

Language Support:

1.  Complete support for XQuery 3.0 constructs.
2.  Complete support for Update Facility 1.0 extensions.
3.  Complete support for MarkLogic 1.0-ml extensions in MarkLogic 6.0 and 8.0.
4.  Fix `InstanceofExpr` and `TreatExpr` -- they expect a `SequenceType`, not a
    `SingleType`.

Reference and Resolve Support:

1.  Resolve URILiteral string references.
2.  Resolve QName prefices to namespace declarations.
3.  Resolve function usage to the corresponding function declaration.
4.  Resolve variable usage to the corresponding variable declaration.

## 0.1 - 2016-09-10

1.  XQuery file type support, including encoding detection.
2.  Conforming XQuery 1.0 syntax highlighter and parser.
