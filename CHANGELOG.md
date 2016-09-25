# Change Log

## 0.2 - (In Development)

1.  Use an annotator -- not the parser -- to check and report constructs from a
    different XQuery version or extension.
2.  Improved error recovery when parsing library modules.

Language Support:

1.  Partial support for XQuery 3.0 constructs.
2.  Complete support for Update Facility 1.0 extensions.
3.  Partial support MarkLogic 1.0-ml extensions.
4.  Fix `InstanceofExpr` and `TreatExpr` -- they expect a `SequenceType`, not a
    `SingleType`.

Reference and Resolve Support:

1.  Support for resolving URILiteral string references.

## 0.1 - 2016-09-10

1.  XQuery file type support, including encoding detection.
2.  Conforming XQuery 1.0 syntax highlighter and parser.
