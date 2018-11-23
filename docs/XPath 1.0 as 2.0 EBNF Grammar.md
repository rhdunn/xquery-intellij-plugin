# XPath 1.0 as 2.0 EBNF Grammar

This document includes material copied from or derived from the XPath
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document describes the XPath 1.0 EBNF grammar using the XPath 2.0
EBNF symbols. This is to make it easier to implement an XPath 1.0 parser
as a subset of an XPath 2.0 parser with minor modifications to account
for precedence changes.

## Table of Contents
- [A XPath Grammar](#a-xpath-grammar)
  - [A.1 EBNF](#a1-ebnf)
  - [A.2 Terminal Symbols](#a2-terminal-symbols)
  - [A.3 Reserved Function Names](#a3-reserved-function-names)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
- [C Change Log](#c-change-log)
- [D Differences Between XPath 1.0 and XPath 2.0](#d-differences-between-xpath-10-and-xpath-20)

## A XPath Grammar

### A.1 EBNF

The following EBNF symbols are defined in terms of the XPath 1.0 grammar:

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[1\]   | `LocationPath`                    | ::= | `("/" RelativeLocationPath?) \| ("//" RelativeLocationPath) \| RelativeLocationPath` | /* xgs: leading-lone-slash */ |
| \[3\]   | `RelativeLocationPath`            | ::= | `Step (("/" \| "//") Step)*`        |                      |
| \[4\]   | `Step`                            | ::= | `AxisStep \| AbbreviatedStep`       |                      |
| \[12\]  | `AbbreviatedStep`                 | ::= | `'.' \| '..'`                       |                      |
| \[15\]  | `PrimaryExpr`                     | ::= | `Literal \| VarRef \| ParenthesizedExpr \| FunctionCall` | |
| \[18\]  | `UnionExpr`                       | ::= | `PathExpr \| UnionExpr '|' PathExpr` |                     |	
| \[19\]  | `PathExpr`                        | ::= | `LocationPath \| FilterExpr \| FilterExpr '/' RelativeLocationPath \| FilterExpr '//' RelativeLocationPath` | |
| \[22\]  | `AndExpr`                         | ::= | `EqualityExpr \| AndExpr 'and' EqualityExpr` |             |
| \[23\]  | `EqualityExpr`                    | ::= | `RelationalExpr \| EqualityExpr '=' RelationalExpr \| EqualityExpr '!=' RelationalExpr` | |
| \[24\]  | `RelationalExpr`                  | ::= | `AdditiveExpr \| RelationalExpr '<' AdditiveExpr \| RelationalExpr '>' AdditiveExpr \| RelationalExpr '<=' AdditiveExpr \| RelationalExpr '>=' AdditiveExpr` | |
| \[25\]  | `AdditiveExpr`                    | ::= | `MultiplicativeExpr \| AdditiveExpr '+' MultiplicativeExpr \| AdditiveExpr '-' MultiplicativeExpr` | |
| \[26\]  | `MultiplicativeExpr`              | ::= | `UnaryExpr \| MultiplicativeExpr '*' UnaryExpr \| MultiplicativeExpr 'div' UnaryExpr \| MultiplicativeExpr 'mod' UnaryExpr` | |
| \[27\]  | `UnaryExpr`                       | ::= | `UnionExpr \| '-' UnaryExpr`        |                      |

The following EBNF symbols are defined in terms of the XPath 2.0 grammar:

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[42\]  | `XPath`                           | ::= | `Expr`                              |                      |
| \[14\]  | `Expr`                            | ::= | `ExprSingle`                        |                      |
| \[46\]  | `ExprSingle`                      | ::= | `OrExpr`                            |                      |
| \[21\]  | `OrExpr`                          | ::= | `AndExpr ( "or" AndExpr )*`         |                      |
| \[5\]   | `AxisStep`                        | ::= | `(ReverseStep \| ForwardStep) PredicateList` |             |
| \[49\]  | `ForwardStep`                     | ::= | `(ForwardAxis NodeTest) \| AbbrevForwardStep` |            |
| \[47\]  | `ForwardAxis`                     | ::= | `("child" "::") \| ("descendant" "::") \| ("attribute" "::") \| ("self" "::") \| ("descendant-or-self" "::") \| ("following-sibling" "::") \| ("following" "::") \| ("namespace" "::")` | |
| \[13\]  | `AbbrevForwardStep`               | ::= | `'@'? NodeTest`                     |                      |
| \[50\]  | `ReverseStep`                     | ::= | `ReverseAxis NodeTest`              |                      |
| \[48\]  | `ReverseAxis`                     | ::= | `("parent" "::") \| ("ancestor" "::") \| ("preceding-sibling" "::") \| ("preceding" "::") \| ("ancestor-or-self" "::")` | |
| \[7\]   | `NodeTest`                        | ::= | `KindTest \| NameTest`              |                      |
| \[37\]  | `NameTest`                        | ::= | `QName \| Wildcard`                 | /* ws: explicit */   |
| \[56\]  | `Wildcard`                        | ::= | `"*" \| (NCName ":" "*")`           |                      |
| \[20\]  | `FilterExpr`                      | ::= | `PrimaryExpr PredicateList`         |                      |
| \[46\]  | `PredicateList`                   | ::= | `Predicate*`                        |                      |
| \[8\]   | `Predicate`                       | ::= | `"[" Expr "]"`                      |                      |
| \[43\]  | `Literal`                         | ::= | `NumericLiteral \| StringLiteral`   |                      |
| \[28\]  | `NumericLiteral`                  | ::= | `IntegerLiteral \| DoubleLiteral`   |                      |
| \[36\]  | `VarRef`                          | ::= | `"$" VarName`                       |                      |
| \[44\]  | `VarName`                         | ::= | `QName`                             |                      |
| \[45\]  | `ParenthesizedExpr`               | ::= | `"(" Expr ")"`                      |                      |
| \[16\]  | `FunctionCall`                    | ::= | `QName "(" ( ExprSingle ( "," ExprSingle )* )? ')'` | /\* xgs: reserved-function-names \*/ /\* gn: parens \*/ |
| \[51\]  | `KindTest`                        | ::= | `PITest \| CommentTest \| TextTest \| AnyKindTest` |       |
| \[52\]  | `AnyKindTest`                     | ::= | `"node" "(" ")"`                    |                      |
| \[53\]  | `TextTest`                        | ::= | `"text" "(" ")"`                    |                      |
| \[54\]  | `CommentTest`                     | ::= | `"comment" "(" ")"`                 |                      |
| \[55\]  | `PITest`                          | ::= | `"processing-instruction" "(" StringLiteral? ")"` |        |

### A.2 Terminal Symbols

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[29\]  | `StringLiteral`                   | ::= | `('"' \[^"\]* '"') \| ("'" \[^'\]* "'")` | /* ws: explicit */ |
| \[32\]  | `IntegerLiteral`                  | ::= | `Digits`                            |                      |
| \[33\]  | `DecimalLiteral`                  | ::= | `("." Digits) \| (Digits "." \[0-9\]*)` | /* ws: explicit */ |
| \[39\]  | `S`                               | ::= | \[[http://www.w3.org/TR/REC-xml#NT-S]()\]<sup><em>XML</em></sup> | /* xgc: xml-version */ |
| \[40\]  | `QName`                           | ::= | \[[http://www.w3.org/TR/REC-xml-names/#NT-QName]()\]<sup><em>Names</em></sup> | /* xgc: xml-version */ |
| \[41\]  | `NCName`                          | ::= | \[[http://www.w3.org/TR/REC-xml-names/#NT-NCName]()\]<sup><em>Names</em></sup> | /* xgc: xml-version */ |

The following symbols are used only in the definition of terminal symbols; they
are not terminal symbols in the grammar of A.1 EBNF.

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[31\]  | `Digits`                          | ::= | `[0-9]+`                            |                      |

### A.3 Reserved Function Names

The following names are not allowed as function names in an unprefixed form
because expression syntax takes precedence.

*  `comment`
*  `node`
*  `processing-instruction`
*  `text`

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XML Path Language (XPath) 1.0*. W3C Recommendation 16 November 1999.
   See [https://www.w3.org/TR/1999/REC-xpath-19991116/]().
*  W3C. *XML Path Language (XPath) 2.0*. W3C Recommendation 14 December 2010.
   See [https://www.w3.org/TR/2010/REC-xpath20-20101214/]().

## C Change Log
This section documents the changes from the XPath 1.0 to XPath 2.0 EBNF
grammar.

__XPath__
1. Renamed `Expr` to `ExprSingle`.
1. Added the `XPath` and `Expr` symbols from XPath 2.0.

__Path Expressions__
1. Inlined the `AbbreviatedAbsoluteLocationPath` and `AbsoluteLocationPath` symbols into `LocationPath`.
1. Inlined the `AbbreviatedRelativeLocationPath` symbol into `RelativeLocationPath`.

__Step Expressions__
1. Moved `Predicate*` from `Step` into a `PredicateList` symbol.

__Axis Steps__
1. Split `AxisName` into `ForwardAxis` and `ReverseAxis`, and combine the keywords with the `::` token.
1. Moved `NodeTest` from `Step` to `AxisSpecifier`.
1. Renamed `AbbreviatedAxisSpecifier` to `AbbrevForwardStep`.
1. Added the `ForwardStep` symbol from XPath 2.0.
1. Moved `ReverseAxis NodeTest` into a `ReverseStep` symbol.
1. Moved `PredicateList` into `AxisSpecifier`.
1. Renamed `AxisSpecifier` to `AxisStep`.

__Node Tests__
1. Move the `node` `NodeType` into an `AnyKindTest` symbol.
1. Move the `text` `NodeType` into a `TextTest` symbol.
1. Move the `comment` `NodeType` into a `CommentTest` symbol.
1. Move the `processing-instruction` `NodeType` into a `PITest` symbol.
1. Move the `processing-instruction` with a `StringLiteral` from `NodeTest` into the `PITest` symbol.
1. Move `AnyKindTest`, `TextTest`, `CommentTest`, and `PITest` into a `KindTest` symbol.
1. Split out the wildcard syntax from `NameTest` into a `Wildcard` symbol.

__Filter Expressions__
1. Moved `Predicate*` from `FilterExpr` into a `PredicateList` symbol.
1. Inlined the `PredicateExpr` symbol into `Predicate`.

__Primary Expressions__
1. Renamed `VariableReference` to `VarRef`.
1. Added the `Literal` and `VarName` symbols from XPath 2.0.
1. Moved the parenthesized primary expression into a `ParenthesizedExpr` symbol.
1. Inlined the `Argument` symbol into `FunctionCall`.

__Terminal Symbols__
1. Renamed `Literal` to `StringLiteral`.
1. Replaced `Number` with `NumericLiteral`, `IntegerLiteral`, and `DecimalLiteral`
   from XPath 2.0.
1. Replaced the `ExprWhitespace` symbol with a link to the `S` symbol from the
   *XML* specification.
1. Added links to the `NCName` and `QName` symbols from the *Namespaces in XML*
   specification.
1. Removed the `ExprToken`, `Operator`, and `OperatorName` symbols that are not
   referenced elsewhere in the XPath 1.0 grammar.
1. Inlined the `MultiplyOperator` symbol into `MultiplicativeExpr`.
1. Replaced the `FunctionName` symbol with a reserved function names section.

### D Differences Between XPath 1.0 and XPath 2.0
The following is the list of features added to XPath 2.0 that are not present
in XPath 1.0:

1. Allow multiple comma separated expressions in `Expr`.
1. Support `ForExpr`, `QuantifiedExpr`, and `IfExpr` in single expressions.
1. Support predicates on `..` steps; use `parent::*` in XPath 1.0.
1. Added support for `DoubleLiteral` in `NumericLiteral`.
1. Made the `Expr` in `ParenthesizedExpr` optional.
1. Added `DocumentTest`, `ElementTest`, `AttributeTest`, `SchemaElementTest`,
   and `SchemaAttributeTest`.
1. Added support for `NCName` in `PITest`.
1. Added support for `*:NCName` wildcards.

The following keywords have been added to the *Reserved Function Names* list:

* `attribute`
* `document-node`
* `element`
* `empty-sequence`
* `if`
* `item`
* `schema-attribute`
* `schema-element`
* `typeswitch`
