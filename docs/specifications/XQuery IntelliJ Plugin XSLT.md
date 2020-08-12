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

| Schema Type         | Start Symbol   | Reference                                                         |
|---------------------|----------------|-------------------------------------------------------------------|
| `xsl:EQName`        | `EQName`       | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-EQName]()\]       |
| `xsl:expression`    | `XPath`        | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-XPath]()\]        |
| `xsl:item-type`     | `ItemType`     | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-ItemType]()\]     |
| `xsl:pattern`       | `XPath`        | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-XPath]()\]        |
| `xsl:QName`         | `QName`        | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-QName]()\]        |
| `xsl:sequence-type` | `SequenceType` | \[[https://www.w3.org/TR/xpath-31/#prod-xpath31-SequenceType]()\] |

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
