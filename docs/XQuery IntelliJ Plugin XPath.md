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
- [A XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [A.1 EBNF for XPath 3.1 with Vendor Extensions](#a1-ebnf-for-xpath-31-with-vendor-extensions)
  - [A.2 Reserved Function Names](#a3-reserved-function-names)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
  - [B.2 MarkLogic References](#b2-marklogic-references)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XPath 3.1, and XQuery and XPath Full Text 3.0.

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

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF for XPath 3.1 with Vendor Extensions

This EBNF grammar includes and modifies the EBNF grammar for the following
specifications:
1.  XPath 3.1 (W3C Recommendation 21 March 2017);
1.  XQuery and XPath Full Text 3.0 (W3C Recommendation 24 November 2015).

When an EBNF symbol is modified in an extension, that modified symbol is used
in preference to the base symbol definition. When the symbol is modified below,
that modified symbol is used in preference to the base or extension symbol
definition. When the EBNF symbol is a list such as `PrimaryExpr`, the result
used is the combination of the definitions of that symbol across all
specifications, including this document.

The EBNF symbols below only include new and modified symbols. Missing reference
numbers are for symbols that only apply to XQuery.

| Ref     | Symbol                  |     | Expression                          | Options              |
|---------|-------------------------|-----|-------------------------------------|----------------------|

### A.2 Reserved Function Names

| keyword                  | XQuery                          |
|--------------------------|---------------------------------|
| `attribute`              | XPath 2.0                      |
| `comment`                | XPath 1.0                      |
| `document-node`          | XPath 2.0                      |
| `element`                | XPath 2.0                      |
| `empty-sequence`         | XPath 2.0                      |
| `function`               | XPath 3.0                      |
| `if`                     | XPath 2.0                      |
| `item`                   | XPath 2.0                      |
| `namespace-node`         | XPath 3.0                      |
| `node`                   | XPath 1.0                      |
| `processing-instruction` | XPath 1.0                      |
| `schema-attribute`       | XPath 2.0                      |
| `schema-element`         | XPath 2.0                      |
| `switch`                 | XPath 3.0                      |
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
