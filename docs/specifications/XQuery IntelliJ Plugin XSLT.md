---
layout: page
title: XQuery IntelliJ Plugin 1.8 XSLT
---

This document includes material copied from or derived from the XPath
and XSLT specifications. Copyright Â© 1999-2017 W3CÂ® (MIT, ERCIM, Keio,
Beihang).

## Abstract
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XSLT and associated W3C extensions.

## Table of Contents

{: .toc.toc-numbered }
- [Introduction](#1-introduction)
- {: .toc-letter } [XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [EBNF Start Symbol for Schema Types](#a1-ebnf-start-symbol-for-schema-types)
  - [EBNF for XSLT Schema Types](#a2-ebnf-for-xslt-schema-types)
  - [EBNF External Symbols](#a3-ebnf-external-symbols)
- {: .toc-letter } [References](#b-references)
  - [W3C References](#b1-w3c-references)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XSLT 2.0 and 3.0. The syntax described here is the
syntax that is supported by the XQuery IntelliJ Plugin.

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF Start Symbol for Schema Types
The EBNF symbol used to start parsing from is dependent on the schema type of the
attribute or text node. This is determined by the following table, which includes
schema types from XSLT 1.0, 2.0, and 3.0:

| Schema Type               | Start Symbol             |
|---------------------------|--------------------------|
| `xsl:avt`                 | `AttributeValueTemplate` |
| `xsl:element-names`       | `NameTestList`           |
| `xsl:EQName`              | `EQName`                 |
| `xsl:EQName-in-namespace` | `EQName`                 |
| `xsl:EQNames`             | `EQNames`                |
| `xsl:expr-avt`            | `AttributeValueTemplate` |
| `xsl:expression`          | `XPath`                  |
| `xsl:item-type`           | `ItemType`               |
| `xsl:mode`                | `ModeOrCurrent`          |
| `xsl:modes`               | `ModeListOrAll`          |
| `xsl:nametests`           | `NameTestList`           |
| `xsl:pattern`             | `XPath`                  |
| `xsl:prefix`              | `PrefixOrDefault`        |
| `xsl:prefix-list`         | `PrefixList`             |
| `xsl:prefix-list-or-all`  | `PrefixListOrAll`        |
| `xsl:prefix-or-default`   | `PrefixOrDefault`        |
| `xsl:prefixes`            | `NCNameList`             |
| `xsl:QName`               | `QName`                  |
| `xsl:QNames`              | `QNameList`              |
| `xsl:sequence-type`       | `SequenceType`           |
| `xsl:tokens`              | `NCNameList`             |

### A.2 EBNF for XSLT Schema Types
The EBNF symbols here define the syntax for the schema types not covered by the
XPath and XSLT pattern syntax grammars. This is a formal definition of the syntax
described in the XML schema files.

{: .ebnf-symbols }
| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[1\]    | `AttributeValueTemplate`       | ::= | `AttrContentChar | "{{" | "}}" | EnclosedExpr` |            |
| \[2\]    | `AttrContentChar`              | ::= | `(Char - [{}])`                     |                       |
| \[3\]    | `NCNameList`                   | ::= | `S? NCName (S NCName)* S?`          |                       |
| \[4\]    | `QNameList`                    | ::= | `S? QName (S QName)* S?`            |                       |
| \[5\]    | `EQNameList`                   | ::= | `S? EQName (S EQName)* S?`          |                       |
| \[6\]    | `NameTestList`                 | ::= | `S? NameTest (S NameTest)* S?`      |                       |
| \[7\]    | `PrefixOrDefault`              | ::= | `NCName | "#default"`               |                       |
| \[8\]    | `PrefixList`                   | ::= | `S? PrefixOrDefault (S PrefixOrDefault)* S?` |              |
| \[9\]    | `PrefixListOrAll`              | ::= | `PrefixList | "#all"`               |                       |
| \[10\]   | `Mode`                         | ::= | `EQName | "#default"`               |                       |
| \[11\]   | `ModeList`                     | ::= | `S? Mode (S Mode)* S?`              |                       |
| \[12\]   | `ModeOrCurrent`                | ::= | `Mode | "#current"`                 |                       |
| \[13\]   | `ModeListOrAll`                | ::= | `ModeList | "#all"`                 |                       |

### A.3 EBNF External Symbols

__Extensible Markup Language (XML) 1.0 (Fifth Edition)__
{: .ebnf-symbols }
| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
|          | `Char`                         | ::= | \[[https://www.w3.org/TR/REC-xml/#NT-Char]()\] |            |
|          | `S`                            | ::= | \[[https://www.w3.org/TR/REC-xml/#NT-S]()\] |               |

__XML Path Language (XPath) 3.1__
{: .ebnf-symbols }
| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
|          | `EnclosedExpr`                 | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-EnclosedExpr]()\] | |
|          | `EQName`                       | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-EQName]()\] | |
|          | `ItemType`                     | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-ItemType]()\] | |
|          | `NameTest`                     | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-NameTest]()\] | |
|          | `NCName`                       | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-NCName]()\] | |
|          | `QName`                        | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-QName]()\] | |
|          | `SequenceType`                 | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-SequenceType]()\] | |
|          | `XPath`                        | ::= | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-XPath]()\] | |

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XSL Transformations (XSLT) Version 3.0*. W3C Recommendation 8 June 2017.
   See [https://www.w3.org/TR/2017/REC-xslt-30-20170608/]().

__XML Schema__
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 1: Structures*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-1-20120405/]().
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 2: Datatypes*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/]().
