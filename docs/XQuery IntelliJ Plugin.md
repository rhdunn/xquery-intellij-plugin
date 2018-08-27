# XPath and XQuery Grammar

- [1 Introduction](#1-introduction)
- [2 Basics](#2-basics)
- [3 Expressions](#3-expressions)
  - [3.1 Node Constructors](#31-node-constructors)
  - [3.2 Quantified Expressions](#32-quantified-expressions)
- [A XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [A.1 EBNF for XPath 3.1](#a1-ebnf-for-xpath-31)
  - [A.2 EBNF for XQuery 3.1](#a2-ebnf-for-xquery-31)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)

## 1 Introduction
The XQuery IntelliJ plugin provides language support for XQuery, W3C extensions
to XQuery, and vendor extensions.

This document describes the vendor and plugin specific extensions supported in
by the XQuery IntelliJ plugin.

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

## 3 Expressions

### 3.1 Node Constructors

| Ref   | Symbol             |     | Expression                          | Options              |
|-------|--------------------|-----|-------------------------------------|----------------------|
| \[1\] | `DirAttributeList` | ::= | `(S DirAttribute?)*`                | /\* ws: explicit \*/ |
| \[2\] | `DirAttribute`     | ::= | `QName S? "=" S? DirAttributeValue` | /\* ws: explicit \*/ |

This follows the grammar production pattern used in other constructs like
`ParamList`, making it easier to support namespace declaration lookup on
`xmlns` attributes.

### 3.2 Quantified Expressions

| Ref   | Symbol                  |     | Expression                          | Options              |
|-------|-------------------------|-----|-------------------------------------|----------------------|
| \[3\] | `QuantifiedExpr`        | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[4\] | `QuantifiedExprBinding` | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` | |

This follows the grammar production pattern used in other constructs like
`LetClause` and `ForClause`, making it easier to support variable bindings.

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF for XPath 3.1

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

These changes include support for:
1.  XQuery IntelliJ Plugin changes to make it easier to implement the plugin.

| Ref     | Symbol                  |     | Expression                          | Options              |
|---------|-------------------------|-----|-------------------------------------|----------------------|
| \[3\]   | `QuantifiedExpr`        | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[4\]   | `QuantifiedExprBinding` | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` |          |

### A.2 EBNF for XQuery 3.1

This EBNF grammar includes and modifies the EBNF grammar for the following
specifications:
1.  XQuery 3.1 (W3C Recommendation 21 March 2017);
1.  XQuery and XPath Full Text 3.0 (W3C Recommendation 24 November 2015);
1.  XQuery Update Facility 3.0 (W3C Working Group Note 24 January 2017);
1.  XQuery Scripting Extension 1.0 (W3C Working Group Note 18 September 2014).

When an EBNF symbol is modified in an extension, that modified symbol is used
in preference to the base symbol definition. When the symbol is modified below,
that modified symbol is used in preference to the base or extension symbol
definition. When the EBNF symbol is a list such as `PrimaryExpr`, the result
used is the combination of the definitions of that symbol across all
specifications, including this document.

The EBNF symbols below only include new and modified symbols.

These changes include support for:
1.  XQuery IntelliJ Plugin changes to make it easier to implement the plugin.

| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[1\]    | `DirAttributeList`             | ::= | `(S DirAttribute?)*`                | /\* ws: explicit \*/  |
| \[2\]    | `DirAttribute`                 | ::= | `QName S? "=" S? DirAttributeValue` | /\* ws: explicit \*/  |
| \[3\]    | `QuantifiedExpr`               | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[4\]    | `QuantifiedExprBinding`        | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` |            |

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XML Path Language (XPath) 3.1*. W3C Recommendation 21 March 2017.
   See [https://www.w3.org/TR/2017/REC-xpath-31-20170321/]().
*  W3C. *XQuery 3.1: An XML Query Language*. W3C Recommendation 21 March 2017.
   See [https://www.w3.org/TR/2017/REC-xquery-31-20170321/]().
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
*  W3C. *XQuery Update Facility 3.0*. W3C Working Group Note 24 January 2017.
   See [https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/]().
*  W3C. *XQuery Scripting Extension 1.0*. W3C Working Group Note 18 September 2014.
   See [http://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/]().

__XML Schema__
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 1: Structures*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-1-20120405/]().
*  W3C. *W3C XML Schema Definition Language (XSD) 1.1 Part 2: Datatypes*. W3C
   Recommendation 5 April 2012. See
   [http://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/]().
