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
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
- [C Change Log](#c-change-log)

## A XPath Grammar

### A.1 EBNF

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[1\]   | `LocationPath`                    | ::= | `RelativeLocationPath \| AbsoluteLocationPath` |           |
| \[2\]   | `AbsoluteLocationPath`            | ::= | `'/' RelativeLocationPath? \| AbbreviatedAbsoluteLocationPath` | |
| \[3\]   | `RelativeLocationPath`            | ::= | `Step \| RelativeLocationPath '/' Step \| AbbreviatedRelativeLocationPath` | |
| \[4\]   | `Step`                            | ::= | `AxisSpecifier NodeTest Predicate* \| AbbreviatedStep` |   |
| \[5\]   | `AxisSpecifier`                   | ::= | `AxisName '::' \| AbbreviatedAxisSpecifier` |              |
| \[6\]   | `AxisName`                        | ::= | `'ancestor' \| 'ancestor-or-self' \| 'attribute' \| 'child' \| 'descendant' \| 'descendant-or-self' \| 'following' \| 'following-sibling' \| 'namespace' \| 'parent' \| 'preceding' \| 'preceding-sibling' \| 'self'` | |
| \[7\]   | `NodeTest`                        | ::= | `NameTest \| NodeType '(' ')' \| 'processing-instruction' '(' Literal ')'` | |
| \[8\]   | `Predicate`                       | ::= | `'[' PredicateExpr ']'`             |                      | 
| \[9\]   | `PredicateExpr`                   | ::= | `Expr`                              |                      |
| \[10\]  | `AbbreviatedAbsoluteLocationPath` | ::= | `'//' RelativeLocationPath`         |                      |
| \[11\]  | `AbbreviatedRelativeLocationPath` | ::= | `RelativeLocationPath '//' Step`    |                      |	
| \[12\]  | `AbbreviatedStep`                 | ::= | `'.' \| '..'`                       |                      |
| \[13\]  | `AbbreviatedAxisSpecifier`        | ::= | `'@'?`                              |                      |
| \[14\]  | `Expr`                            | ::= | `OrExpr`                            |                      |
| \[15\]  | `PrimaryExpr`                     | ::= | `VariableReference \| '(' Expr ')' \| Literal \| Number \| FunctionCall` | |
| \[16\]  | `FunctionCall`                    | ::= | `FunctionName '(' ( Argument ( ',' Argument )* )? ')'` |   |
| \[17\]  | `Argument`                        | ::= | `Expr`                              |                      |
| \[18\]  | `UnionExpr`                       | ::= | `PathExpr \| UnionExpr '|' PathExpr` |                     |	
| \[19\]  | `PathExpr`                        | ::= | `LocationPath \| FilterExpr \| FilterExpr '/' RelativeLocationPath \| FilterExpr '//' RelativeLocationPath` | |
| \[20\]  | `FilterExpr`                      | ::= | `PrimaryExpr \| FilterExpr Predicate` |                    |
| \[21\]  | `OrExpr`                          | ::= | `AndExpr \| OrExpr 'or' AndExpr`    |                      |
| \[22\]  | `AndExpr`                         | ::= | `EqualityExpr \| AndExpr 'and' EqualityExpr` |             |
| \[23\]  | `EqualityExpr`                    | ::= | `RelationalExpr \| EqualityExpr '=' RelationalExpr \| EqualityExpr '!=' RelationalExpr` | |
| \[24\]  | `RelationalExpr`                  | ::= | `AdditiveExpr \| RelationalExpr '<' AdditiveExpr \| RelationalExpr '>' AdditiveExpr \| RelationalExpr '<=' AdditiveExpr \| RelationalExpr '>=' AdditiveExpr` | |
| \[25\]  | `AdditiveExpr`                    | ::= | `MultiplicativeExpr \| AdditiveExpr '+' MultiplicativeExpr \| AdditiveExpr '-' MultiplicativeExpr` | |
| \[26\]  | `MultiplicativeExpr`              | ::= | `UnaryExpr \| MultiplicativeExpr '*' UnaryExpr \| MultiplicativeExpr 'div' UnaryExpr \| MultiplicativeExpr 'mod' UnaryExpr` | |
| \[27\]  | `UnaryExpr`                       | ::= | `UnionExpr \| '-' UnaryExpr`        |                      |
| \[37\]  | `NameTest`                        | ::= | `'*' \| NCName ':' '*' \| QName`    |                      |

### A.2 Terminal Symbols

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[29\]  | `Literal`                         | ::= | `'"' \[^"\]* '"' \| "'" \[^'\]* "'"` |                     |
| \[30\]  | `Number`                          | ::= | `Digits ('.' Digits?)? \| '.' Digits |                     |
| \[35\]  | `FunctionName`                    | ::= | `QName - NodeType`                  |                      |
| \[36\]  | `VariableReference`               | ::= | `'$' QName`                         |                      |
| \[38\]  | `NodeType`                        | ::= | `'comment' \| 'text' \| 'processing-instruction' \| 'node' | |
| \[39\]  | `ExprWhitespace`                  | ::= | `S`                                 |                      |

The following symbols are used only in the definition of terminal symbols; they
are not terminal symbols in the grammar of A.1 EBNF.

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[31\]  | `Digits`                          | ::= | `[0-9]+`                            |                      |

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

1. Remove the `ExprToken`, `Operator`, and `OperatorName` symbols that are not
   referenced elsewhere in the XPath 1.0 grammar.
1. Inline the `MultiplyOperator` symbol into `MultiplicativeExpr`.
