---
layout: page
title: XPath WD 2003 Syntax
---

This document includes material copied from or derived from the XPath
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document describes the XPath 2.0 working draft (WD) syntax in terms of
the finalised recommendation (REC) syntax. This syntax is supported in older
versions of XQuery processors.

## Table of Contents

{: .toc.toc-numbered }
- {: .toc-letter } [XPath Grammar](#a-xpath-grammar)
  - [EBNF](#a1-ebnf)
  - [Terminal Symbols](#a2-terminal-symbols)
- {: .toc-letter } [References](#b-references)
  - [W3C References](#b1-w3c-references)
- {: .toc-letter } [Differences Between WD and REC](#c-differences-between-wd-and-rec)

## A XPath Grammar

### A.1 EBNF

{: .ebnf-symbols }
| REC     | WD        | Symbol                            |     | Expression                                 |
|---------|-----------|-----------------------------------|-----|--------------------------------------------|
| \[1\]   | \[15\]    | `XPath`                           | ::= | `Expr?`                                    |
| \[9\]   | \[23\]    | `AndExpr`                         | ::= | `InstanceofExpr ( "and" InstanceofExpr )*` |
| \[13\]  | \[31\]    | `MultiplicativeExpr`              | ::= | `UnaryExpr ( ("*" | "div" | "idiv" | "mod") UnaryExpr )*` |
| \[20\]  | \[32\]    | `UnaryExpr`                       | ::= | `("-" | "+")* UnionExpr`                   |
| \[15\]  | \[34\]    | `IntersectExceptExpr`             | ::= | `ValueExpr ( ("intersect" | "except") ValueExpr )*` |
| \[24\]  | \[45-46\] | `NodeComp`                        | ::= | `"is" | "isnot" | "<<" | ">>"`             |
| \[31\]  | \[49\]    | `AbbreviatedForwardStep`          | ::= | `("@" NameTest) | NodeTest`                |
| \[50\]  | \[61\]    | `SequenceType`                    | ::= | `("empty" "(" ")") | (ItemType OccurrenceIndicator?)` |
| \[54\]  | \[64\]    | `KindTest`                        | ::= | `DocumentTest | ElementTest | AttributeTest | PITest | CommentTest | TextTest | AnyKindTest` |
| \[56\]  | \[68\]    | `DocumentTest`                    | ::= | `"document-node" "(" ElementTest? ")"`     |
| \[59\]  | \[67\]    | `PITest`                          | ::= | `"processing-instruction" "(" StringLiteral? ")"` |
| \[60\]  | \[66\]    | `AttributeTest`                   | ::= | `"attribute" "(" ((SchemaContextPath "@" LocalName) | ("@" NodeName ("," TypeName)?))? ")"` |
| \[64\]  | \[65\]    | `ElementTest`                     | ::= | `"element" "(" ((SchemaContextPath LocalName) | (NodeName ("," TypeName "nillable"?)?))? ")"` |
|         | \[72\]    | `SchemaContextPath`               | ::= | `SchemaGlobalContext "/" SchemaContextStep "/"*` |
|         | \[73\]    | `LocalName`                       | ::= | `QName`                                    |
|         | \[74\]    | `NodeName`                        | ::= | `QName | "*"`                              |
|         | \[75\]    | `TypeName`                        | ::= | `QName | "*"`                              |

### A.2 Terminal Symbols

{: .ebnf-symbols }
| REC     | WD        | Symbol                            |     | Expression                                 |
|---------|-----------|-----------------------------------|-----|--------------------------------------------|
|         | \[7\]     | `SchemaGlobalTypeName`            | ::= | `"type" "(" QName ")"`                     |
|         | \[8\]     | `SchemaGlobalContext`             | ::= | `QName | SchemaGlobalTypeName`             |
|         | \[9\]     | `SchemaContextStep`               | ::= | `QName`                                    |

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XML Path Language (XPath) 2.0*. W3C Working Draft 02 May 2010.
   See [https://www.w3.org/TR/2003/WD-xpath20-20030502/]().
*  W3C. *XML Path Language (XPath) 2.0*. W3C Recommendation 14 December 2010.
   See [https://www.w3.org/TR/2010/REC-xpath20-20101214/]().

## C Differences Between WD and REC

The working draft syntax of XPath 2.0 has the following differences compared to
the recommendation syntax.

1.  `XPath` expression is optional.
1.  Precedence order from `AndExpr`, `MultiplicativeExpr`, `UnaryExpr`, and `IntersectExceptExpr`.
1.  `NodeComp` has an extra `isnot` comparison operator.
1.  `AbbreviatedForwardStep` restricts attributes to name tests.
1.  `SequenceType` uses `empty` instead of `empty-sequence` for the empty sequence type.
1.  `KindTest` does not support `SchemaElementTest` and `SchemaAttributeTest`.
1.  `ElementTest` and `AttributeTest` kind tests support a schema path.
1.  `PITest` does not support using `NCName`s.
