# XQuery IntelliJ Plugin 1.4 XPath

This document includes material copied from or derived from the XPath
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XPath and associated W3C extensions. The
plugin-specific extensions are provided to support IntelliJ integration.

## Table of Contents
- [1 Introduction](#1-introduction)
- [2 Basics](#2-basics)
  - [2.1 Types](#21-types)
    - [2.1.1 SequenceType Syntax](#211-sequencetype-syntax)
- [3 Expressions](#3-expressions)
  - [3.1 Quantified Expressions](#31-quantified-expressions)
  - [3.2 Path Expressions](#32-path-expressions)
    - [3.2.1 Node Tests](#321-node-tests)
  - [3.3 For Expressions](#33-for-expressions)
  - [3.4 Conditional Expressions](#34-conditional-expressions)
- [A XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [A.1 EBNF for XPath 3.1 with Vendor Extensions](#a1-ebnf-for-xpath-31-with-vendor-extensions)
  - [A.2 Reserved Function Names](#a3-reserved-function-names)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
  - [B.2 MarkLogic References](#b2-marklogic-references)
  - [B.3 EXPath References](#b3-expath-references)
- [C Vendor Extensions](#c-vendor-extensions)
  - [C.1 IntelliJ Plugin Extensions](#c1-intellij-plugin-extensions)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XPath 2.0. The syntax described here is the syntax
that is supported by the XQuery IntelliJ Plugin.

The plugin provides plugin-specific extensions to support IntelliJ
integration. These are listed in appendix
[C.1 IntelliJ Plugin Extensions](#c1-intellij-plugin-extensions), with links to
the relevant parts of this document that describe the specific extensions.

## 2 Basics
This document uses the following namespace prefixes to represent the namespace
URIs with which they are listed. Although these prefixes are used within this
specification to refer to the corresponding namespaces, not all of these
bindings will necessarily be present in the static context of every expression,
and authors are free to use different prefixes for these namespaces, or to bind
these prefixes to different namespaces.

*  `xs = http://www.w3.org/2001/XMLSchema`
*  `fn = http://www.w3.org/2005/xpath-functions`

In addition to the prefixes in the above list, this document uses the following
namespace prefixes to represent the namespace URIs with which they are listed.
These namespace prefixes are not predeclared and their use in this document is
not normative.

*  `err = http://www.w3.org/2005/xqt-errors`
*  `error = http://marklogic.com/xdmp/error`
*  `ije = http://reecedunn.co.uk/xquery/xqt-errors`
*  `ijw = http://reecedunn.co.uk/xquery/xqt-warnings`
*  `xdm = http://reecedunn.co.uk/xquery-datamodel`

### 2.1 Types

#### 2.1.1 SequenceType Syntax

| Ref    | Symbol                  |     | Expression                          | Options |
|--------|-------------------------|-----|-------------------------------------|---------|
| \[5\]  | `ItemType`              | ::= | `KindTest \| AnyItemType \| AtomicType` |     |
| \[6\]  | `AnyItemType`           | ::= | `"item" "(" ")"`                    |         |

## 3 Expressions

| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[9\]  | `ExprSingle`            | ::= | `ForExpr \| LetExpr \| QuantifiedExpr \| IfExpr \| TernaryIfExpr` | |

### 3.1 Quantified Expressions

| Ref   | Symbol                  |     | Expression                          | Options              |
|-------|-------------------------|-----|-------------------------------------|----------------------|
| \[1\] | `QuantifiedExpr`        | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[2\] | `QuantifiedExprBinding` | ::= | `"$" VarName "in" ExprSingle` | |

This follows the grammar production pattern used in other constructs like
`LetClause` and `ForClause`, making it easier to support variable bindings.

### 3.2 Path Expressions

#### 3.2.1 Node Tests

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[3\]  | `Wildcard`                     | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[4\]  | `WildcardIndicator`            | ::= | `"*"`                                     |         |

The changes to `Wildcard` are to make it work like the `EQName` grammar
productions. This is such that `WildcardIndicator` is placed wherever an
`NCName` can occur in an `EQName`. That is, either the prefix or local
name (but not both) can be `WildcardIndicator`.

A `WildcardIndicator` is an instance of `xdm:wildcard`.

### 3.3 For Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[7\]  | `ForExpr`                      | ::= | `SimpleForClause ReturnClause`            |         |
| \[8\]  | `ReturnClause`                 | ::= | `"return" ExprSingle`                     |         |

The `ForExpr` follows the grammar production pattern used in XQuery 3.0 for
`FLWORExpr` grammar productions.

### 3.4 Conditional Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[10\] | `TernaryIfExpr`                | ::= | `ElvisExpr "??" ElvisExpr "!!" ElvisExpr` |         |
| \[11\] | `ElvisExpr`                    | ::= | `OrExpr "?!" OrExpr`                      |         |

The `TernaryIfExpr` and `ElvisExpr` expressions are new expressions defined in
proposal 2 of the EXPath syntax extensions for XPath and XQuery.

Given the `TernaryIfExpr`:

    C ?? A !! B

the equivalent `IfExpr` is:

    if (C) then A else B

Given the `ElvisExpr`:

    A ?: B

the equivalent `IfExpr` is:

    let $a := A
    return if (exists($a)) then $a else B

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF for XPath 3.1 with Vendor Extensions

This EBNF grammar includes and modifies the EBNF grammar for the following
specifications:
1.  XPath 2.0 (W3C Recommendation 14 December 2010).

When an EBNF symbol is modified in an extension, that modified symbol is used
in preference to the base symbol definition. When the symbol is modified below,
that modified symbol is used in preference to the base or extension symbol
definition. When the EBNF symbol is a list such as `PrimaryExpr`, the result
used is the combination of the definitions of that symbol across all
specifications, including this document.

The EBNF symbols below only include new and modified symbols.

These changes include support for:
1.  XQuery IntelliJ Plugin Vendor Extensions.

| Ref     | Symbol                  |     | Expression                          | Options              |
|---------|-------------------------|-----|-------------------------------------|----------------------|
| \[1\]   | `QuantifiedExpr`        | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[2\]   | `QuantifiedExprBinding` | ::= | `"$" VarName "in" ExprSingle`       |                      |
| \[3\]   | `Wildcard`              | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[4\]   | `WildcardIndicator`     | ::= | `"*"`                               |                      |
| \[5\]   | `ItemType`              | ::= | `KindTest \| AnyItemType \| AtomicType` |                  |
| \[6\]   | `AnyItemType`           | ::= | `"item" "(" ")"`                    |                      |
| \[7\]   | `ForExpr`               | ::= | `SimpleForClause ReturnClause`      |                      |
| \[8\]   | `ReturnClause`          | ::= | `"return" ExprSingle`               |                      |
| \[9\]   | `ExprSingle`            | ::= | `ForExpr \| LetExpr \| QuantifiedExpr \| IfExpr \| TernaryIfExpr` | |
| \[10\]  | `TernaryIfExpr`         | ::= | `ElvisExpr "??" ElvisExpr "!!" ElvisExpr` |                |
| \[11\]  | `ElvisExpr`             | ::= | `OrExpr "?!" OrExpr`                |                      |

### A.2 Reserved Function Names

| keyword                  | XQuery                          |
|--------------------------|---------------------------------|
| `attribute`              | XPath 2.0                      |
| `comment`                | XPath 1.0                      |
| `document-node`          | XPath 2.0                      |
| `element`                | XPath 2.0                      |
| `empty-sequence`         | XPath 2.0                      |
| `if`                     | XPath 2.0                      |
| `item`                   | XPath 2.0                      |
| `node`                   | XPath 1.0                      |
| `processing-instruction` | XPath 1.0                      |
| `schema-attribute`       | XPath 2.0                      |
| `schema-element`         | XPath 2.0                      |
| `text`                   | XPath 1.0                      |
| `typeswitch`             | XPath 2.0                      |

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XML Path Language (XPath) 3.1*. W3C Recommendation 21 March 2017.
   See [https://www.w3.org/TR/2017/REC-xpath-31-20170321/]().
*  W3C. *XQuery and XPath Data Model 3.1*. W3C Recommendation 21 March 2017.
   See [https://www.w3.org/TR/2017/REC-xpath-datamodel-31-20170321/]().
*  W3C. *XPath and XQuery Functions and Operators 3.1*. W3C Recommendation 21
   March 2017. See [https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/]().
*  W3C. *XQuery 1.0 and XPath 2.0 Formal Semantics (Second Edition)*. W3C
   Recommendation 14 December 2010. See
   [http://www.w3.org/TR/2010/REC-xquery-semantics-20101214/]().

__W3C Language Extensions__
*  W3C. *XQuery and XPath Full Text 3.0*. W3C Recommendation 24 November 2015.
   See [http://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/]().

__XML Schema__
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 1: Structures*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-1-20120405/]().
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 2: Datatypes*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/]().

__Working Drafts__
*  W3C. *XML Path Language (XPath) 2.0*. W3C Working Draft 02 May 2003.
   See [https://www.w3.org/TR/2003/WD-xpath20-20030502/]().

### B.2 MarkLogic References
*  MarkLogic. *XPath Axes and Syntax*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_39877]().
*  MarkLogic. *Indexable Path Expression Grammar*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_17642]().
*  MarkLogic. *Patch and Extract Path Expression Grammar*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_98286]().

### B.3 EXPath References
__XPath NG__
*  EXPath. *Conditional Expressions*. Proposal 2, version 1. See
   [https://github.com/expath/xpath-ng/blob/d2421975caacba75f0c9bd7fe017cc605e56b00f/conditional-expressions.md]().
   Michael Kay, Saxonica.

## C Vendor Extensions

### C.1 IntelliJ Plugin Extensions
The following constructs have had their grammar modified to make it easier to
implement features such as variable lookup. These changes do not modify the
behaviour of those constructs:
1.  [Quantified Expressions](#31-quantified-expressions)
1.  [Node Tests](#321-node-tests)
1.  [For Expressions](#33-for-expressions)
1.  [Any Item Type](#21211-item-type)
