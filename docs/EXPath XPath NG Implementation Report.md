# EXPath XPath NG Implementation Report

This document describes the implementation of the xpath-ng syntax extension
proposals from the EXPath group in the XQuery IntelliJ Plugin.

| REF | Name                                                                                           | Lexer               | Parser              | Static Context | Inspections |
|----:|------------------------------------------------------------------------------------------------|---------------------|---------------------|----------------|-------------|
|   1 | [Variadic Function Arguments](https://github.com/expath/xpath-ng/pull/1)                       | 1.4<sup>\[4\]</sup> | 1.4<sup>\[4\]</sup> | No             | No          |
|   2 | [Conditional Expressions](https://github.com/expath/xpath-ng/pull/2)                           | 1.3                 | 1.3<sup>\[1\]</sup> | n/a            | No          |
|   3 | [Extensions to Unary and Binary Lookup Expressions](https://github.com/expath/xpath-ng/pull/3) | No                  | No                  | No             | No          |
|   5 | [Concise Inline Function Syntax](https://github.com/expath/xpath-ng/pull/5)                    | 1.3<sup>\[2\]</sup> | 1.3<sup>\[2\]</sup> | n/a            | No          |
|   6 | [Anonymous Union Types](https://github.com/expath/xpath-ng/pull/6)                             | 1.0                 | 1.0                 | n/a            | No          |
|   7 | [If Without Else](https://github.com/expath/xpath-ng/pull/7)                                   | 1.3                 | 1.3                 | n/a            | No          |
|   8 | [Sequence, Map, and Array Decomposition](https://github.com/expath/xpath-ng/pull/8)            | No                  | No                  | No             | No          |
|   9 | [Annotation Declarations](https://github.com/expath/xpath-ng/pull/9)                           | No                  | No                  | No             | No          |
|  10 | [Annotation Sequence Types](https://github.com/expath/xpath-ng/pull/10)                        | No                  | No                  | No             | No          |
|  11 | [Restricted Sequences](https://github.com/expath/xpath-ng/pull/11)                             | 1.3<sup>\[3\]</sup> | 1.3<sup>\[3\]</sup> | No             | No          |
|  12 | [Lift Single-Item Restrictions on Switch Cases](https://github.com/expath/xpath-ng/pull/12)    | No                  | No                  | No             | No          |
|  13 | [Extended Element and Attribute Tests](https://github.com/expath/xpath-ng/pull/13)             | No                  | No                  | No             | No          |

1.  The *Conditional Expressions* proposal defines an elvis operator token
    (`?:`) that causes an ambiguity with the Saxon 9.9 optional `TupleField`
    syntax extension with compact whitespace. The plugin resolves this when
    parsing a `TupleField` by treating the elvis operator as a `?` token
    followed by a `:` token.

1.  Only the `fn{ EXPR }` syntax for the focus function option of the *Concise
    Inline Function Syntax* proposal is implemented in XQuery IntelliJ Plugin
    1.3. This matches the Saxon 9.8 syntax extension.

1.  XQuery IntelliJ 1.3 implements version 1 of the *Restricted Sequences*
    proposal, based on the XQuery Formal Semantics 1.0 `Type` syntax.

1.  XQuery IntelliJ 1.4 implements version 2 of the *Variadic Function Arguments*
    proposal.

## Lexer

The lexer is a Kotlin-based hand-written state-based lexer that is built up of
the following parts:
1.  `CodePointRange` -- save/restore position; enumerate UTF-32 Unicode
    codepoints on a UTF-16 Java `CharSequence`.
1.  `CharacterClass` -- classifies UTF-32 Unicode codepoints.
1.  `XQueryLexer` -- the state-based XQuery lexer.

Keyword and NCName tokens are derived from `INCNameType`. This allows keywords
to be used correctly where an NCName can be. Keywords are further derived from
`IXQueryKeywordOrNCNameType`, which specifies if and where the keyword is a
reserved keyword (e.g. it is an XQuery 3.0 reserved function name).

## Parser

The `XQueryParser` is a Kotlin-based hand-written recursive descent parser.
This parser makes use of unbounded look-ahead.

This uses the IntelliJ parser support to mark token ranges as belonging to
element types that are used to construct a PSI (Program Structure Interface)
tree. The PSI tree is similar to an AST (Abstract Syntax Tree), which is how
the plugin treats the PSI tree.

Each node in the PSI tree corresponds to an EBNF symbol in the XPath/XQuery or
extension grammar. If a symbol is only used as a pass-through to a child symbol
then that symbol is omitted (e.g. if an `AndExpr` does not have any `and ...`
parts).

The PSI tree nodes implement a conformance interface that describes which
specifications and vendor implementations support the language construct.

## Static Context

The plugin uses the elements of the PSI tree to provide information about the
static context that they relate to. For example, a `NamespaceDecl` PSI node
will provide information about the namespace it declares.

The static context is implemented as a function for each of the static
context types (statically known functions, etc.). These functions start at
the current location in the PSI tree and walk the tree in reverse looking for
the relevant static context information.

## Inspections

The plugin implements inspections for various error conditions defined in the
XPath/XQuery specifications. These use the IntelliJ inspection framework to
traverse the PSI tree and report any errors that are found. 
