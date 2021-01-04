---
layout: page
title: XQuery IntelliJ Plugin 1.9 XPath
---

This document includes material copied from or derived from the XPath
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XPath and associated W3C extensions. The
plugin-specific extensions are provided to support IntelliJ integration.

## Table of Contents

{: .toc.toc-numbered }
- [Introduction](#1-introduction)
- [Basics](#2-basics)
  - [Types](#21-types)
    - [SequenceType Syntax](#211-sequencetype-syntax)
    - [SequenceType Matching](#212-sequencetype-matching)
      - [Local Union Types](#2121-local-union-types)
      - [Tuple Type](#2122-tuple-type)
      - [Element Test](#2123-element-test)
      - [Attribute Test](#2124-attribute-test)
      - [Type Alias](#2125-type-alias)
- [Expressions](#3-expressions)
  - [FLWOR Expressions](#31-flwor-expressions)
    - [For Member Expressions](#311-for-member-expressions)
  - [Logical Expressions](#32-logical-expressions)
  - [Conditional Expressions](#33-conditional-expressions)
    - [Otherwise Expressions](#331-otherwise-expressions)
  - [Primary Expressions](#34-primary-expressions)
    - [Inline Function Expressions](#341-inline-function-expressions)
      - [Context Item Function Expressions](#3411-context-item-function-expressions)
      - [Lambda Function Expressions](#3412-lambda-function-expressions)
  - [Arrow Operator (=>)](#35-arrow-operator-)
- {: .toc-letter } [XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [EBNF for XPath 3.1 with Vendor Extensions](#a1-ebnf-for-xpath-31-with-vendor-extensions)
  - [Reserved Function Names](#a2-reserved-function-names)
- {: .toc-letter } [References](#b-references)
  - [W3C References](#b1-w3c-references)
  - [MarkLogic References](#b2-marklogic-references)
  - [EXPath References](#b3-expath-references)
  - [Saxon References](#b4-saxon-references)
- {: .toc-letter } [Vendor Extensions](#c-vendor-extensions)
  - [Saxon Vendor Extensions](#c1-saxon-vendor-extensions)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XPath 2.0. The syntax described here is the syntax
that is supported by the XQuery IntelliJ Plugin.

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

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options |
|--------|-------------------------|-----|-------------------------------------|---------|
| \[5\]  | `ItemType`              | ::= | `KindTest \| AnyItemTest \| FunctionTest \| MapTest \| ArrayTest \| LocalUnionType \| TupleType \| TypeAlias \| AtomicOrUnionType \| ParenthesizedItemType` | |
| \[6\]  | `AnyItemTest`           | ::= | `"item" "(" ")"`                    |         |
| \[14\] | `SequenceTypeList`      | ::= | `SequenceType ("," SequenceType)*`  |         |
| \[15\] | `TypedFunctionTest`     | ::= | `"function" "(" SequenceTypeList? ")" "as" SequenceType` | |
| \[16\] | `LocalUnionType`        | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |  |
| \[17\] | `TypedMapTest`          | ::= | `"map" "(" ItemType "," SequenceType ")"` |   |
| \[18\] | `SingleType`            | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |         |

Using `SequenceTypeList` in `TypedFunctionTest` follows the grammar production
pattern of using `ParamList` in `FunctionCall`. This is done to make it easier
to differentiate the parameter types from the return type.

#### 2.1.2 SequenceType Matching

##### 2.1.2.1 Local Union Types

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[16\] | `LocalUnionType`        | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |                |
| \[17\] | `TypedMapTest`          | ::= | `"map" "(" ItemType "," SequenceType ")"` |                 |
| \[18\] | `SingleType`            | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |                  |

The `LocalUnionType` is a new XPath 4.0 Editor's Draft item type supported by
Saxon 9.8.

A `LocalUnionType` defines a union type whose members are the `ItemType` types
listed in the type definition. These types are restricted to being generalized
atomic types (that is, they cannot be list, union, or other complex types).

If a member type has a namespace prefix, the namespace prefix is resolved to a
namespace URI using the
[statically known namespaces](https://www.w3.org/TR/xquery-31/#dt-static-namespaces)<sup><em>XQ31</em></sup>.
If the member type has no namespace prefix, it is implicitly qualified by the
[default type namespace](https://www.w3.org/TR/xquery-31/#dt-def-elemtype-ns)<sup><em>XQ31</em></sup>.

> __Example:__
>
> `xs:numeric` can be specified in its expanded form as:
>
>     1 instance of union(xs:float, xs:double, xs:decimal)

##### 2.1.2.2 Tuple Type

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[25\] | `TupleType`             | ::= | `"tuple" "(" TupleField ("," TupleField)* ("," "*")? ")"` | |
| \[26\] | `TupleField`            | ::= | `TupleFieldName "?"? ( ( ":" | "as" ) SequenceType )?` |    |
| \[33\] | `TupleFieldName`        | ::= | `NCName | StringLiteral`            |                       |

The `TupleType` is a new sequence type supported by Saxon 9.8. Saxon 9.8 uses `:`
to specify the tuple item's sequence type, while Saxon 10.0 uses `as`.

In Saxon 9.8, a tuple field name can only be an `NCName`. In Saxon 10, it can
also be a `StringLiteral`.

In Saxon 9.9, a `TupleField` can be optional by adding a `?` after the field name.

In Saxon 9.8, tuple fields are optional by default (that is, they have a default
type of `item()*`). In Saxon 10.0, tuple fields are required by default (that is,
they have a default type of `item()+`). To specify an optional field in Saxon 10.0,
the sequence type must be optional (i.e. using either the `?` or `*` occurrence
indicator for the specified sequence type).

\[Definition: An *extensible* tuple is a tuple that has some fields specified,
but allows other fields to be included in the map object.\] An *extensible*
tuple is specified by having the last tuple field be the `*` wildcard operator.
This is supported by Saxon 9.9.

##### 2.1.2.3 Element Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[12\]  | `NillableTypeName`      | ::= | `TypeName "?"`                      |         |
| \[13\]  | `ElementTest`           | ::= | `"element" "(" (NameTest ("," (NillableTypeName | TypeName))?)? ")"` | |

This is a Saxon 10.0 extension. The element tests have been relaxed to support
all wildcard forms, not just `*`.

> __Example:__
>
>     $a instance of element(*:thead) and
>     $b instance of element(xhtml:*, xs:string)

##### 2.1.2.4 Attribute Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[29\]  | `AttrbiuteTest`         | ::= | `"attribute" "(" (NameTest ("," TypeName)?)? ")"` | |

This is a Saxon 10.0 extension. The attribute tests have been relaxed to support
all wildcard forms, not just `*`.

> __Example:__
>
>     $a instance of attribute(xlink:*) and
>     $b instance of attribute(*:id, xs:ID)

##### 2.1.2.5 Type Alias

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[34\] | `TypeAlias`             | ::= | `( "~" EQName ) | ( "type" "(" EQName ")" )` |  |

This is a Saxon 9.8 extension. This is used to reference XSLT type aliases
declared using `saxon:type-alias` XSLT elements.

Saxon 9.8 uses the `~type` syntax, while Saxon 10.0 uses the `type(...)` syntax.

## 3 Expressions

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[9\]  | `ExprSingle`            | ::= | `ForExpr \| LetExpr \| QuantifiedExpr \| IfExpr \| TernaryConditionalExpr` | |

### 3.1 FLWOR Expressions

#### 3.1.1 For Member Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[7\]  | `ForExpr`                      | ::= | `SimpleForClause ReturnClause`            |         |
| \[41\] | `SimpleForBinding`             | ::= | `"member"? "$" VarName "in" ExprSingle`   |         |
| \[8\]  | `ReturnClause`                 | ::= | `"return" ExprSingle`                     |         |

This is a new XPath 4.0 Editor's Draft extension to for expressions that is
supported as a Saxon 10.0 vendor extension. It makes it easier to iterate over
the  members in an `array()`.

The expression:

    for member $m in E return R

is equivalent to:

    let $a as array(*) := E
    for $i in 1 to (array:size($a) + 1)
    let $m := array:get($a, $i)
    return R

> __Note:__
>
> This means that the for member expression will produce an XPTY0004 error if
> `E` is not a single array.

The expression:

    for member $m_1 in E_1, $m_2 in E_2, ..., $m_n in E_n
    return R

is equivalent to:

           for member $m_1 in E_1
    return for member $m_2 in E_2
    ...
    return for member $m_n in E_n
    return R

### 3.2 Logical Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[19\] | `OrExpr`                       | ::= | `AndExpr (("or" \| "orElse") AndExpr)*`   |         |
| \[20\] | `AndExpr`                      | ::= | `ComparisonExpr (("and" \| "andAlso") ComparisonExpr)*` | |

The `orElse` and `andAlso` expressions are new logical expression supported by Saxon 9.9.

The `orElse` expression evaluates the left hand side (`lhs`) first, and only
evaluates the right hand side (`rhs`) if the left hand side is false. This is
equivalent to:
>     if (lhs) then fn:true() else xs:boolean(rhs)

The `andAlso` expression evaluates the left hand side (`lhs`) first, and only
evaluates the right hand side (`rhs`) if the left hand side is true. This is
equivalent to:
>     if (lhs) then xs:boolean(rhs) else fn:false()

### 3.3 Conditional Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[10\] | `TernaryConditionalExpr`       | ::= | `OrExpr "??" TernaryConditionalExpr "!!" TernaryConditionalExpr` | |

The `TernaryConditionalExpr` expression is a new expressions defined in proposal
2 of the EXPath syntax extensions for XPath and XQuery.

Given the `TernaryConditionalExpr`:

    C ?? A !! B

the equivalent `IfExpr` is:

    if (C) then A else B

#### 3.3.1 Otherwise Expressions

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[31\]  | `MultiplicativeExpr`           | ::= | `OtherwiseExpr ( ("*" | "div" | "idiv" | "mod") OtherwiseExpr )*` | |
| \[32\]  | `OtherwiseExpr`                | ::= | `UnionExpr ( "otherwise" UnionExpr )*`    |         |

The `OtherwiseExpr` expression is a new XPath 4.0 Editor's Draft expression
supported as a Saxon 10.0 vendor extension that returns the first non-empty
sequence in the otherwise expression.

For two items or empty sequences `A` and `B`, the expression `A otherwise B`
is equivalent to:

    (A, B)[1]

Otherwise, if either `A` or `B` have more than one item, the expression
`A otherwise B` is equivalent to:

    let $a := A
    return if (exists($a)) then $a else B

> __Note:__
>
> For sequences with more than one item `(A, B)[1]` will only return the first
> item in the non-empty sequence, not the entire sequence. This is why the more
> complicated expression is needed for that case.

### 3.4 Primary Expressions

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[36\] | `PrimaryExpr`           | ::= | `Literal \| VarRef \| ParamRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| UnaryLookup` | |
| \[23\] | `FunctionItemExpr`      | ::= | `NamedFunctionRef \| InlineFunctionExpr \| ContextItemFunctionExpr \| LambdaFunctionExpr` | |

#### 3.4.1 Inline Function Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[22\] | `ParamList`                    | ::= | `ParamList ::= Param ("," Param)* "..."?` |         |

\[Definition: *Variadic function arguments* match zero or more arguments at the
end of the non-variadic arguments.\] Variadic function arguments are supported
in proposal 1, version 2 of the EXPath syntax extensions for XPath and XQuery.

When `...` is added after the last parameter in a parameter list, that parameter
contains the arguments passed after the previous parameter as an `array`. If the
variadic parameter has a type, the elements in that array have that type.

##### 3.4.1.1 Context Item Function Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[24\] | `ContextItemFunctionExpr`      | ::= | `(( "fn" "{" ) | ".{" ) Expr "}"`         |         |

This is a Saxon 9.8 extension. It is a syntax variant of the focus
function alternative for inline functions in proposal 5 of the EXPath
syntax extensions for XPath and XQuery.

The expressions `fn{E}` (from Saxon 9.8) and `.{E}` (from Saxon 10.0)
are equivalent to:
>     function ($arg as item()) as item()* { $arg ! (E) }

##### 3.4.1.2 Lambda Function Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[35\] | `LambdaFunctionExpr`           | ::= | `"_" "{" Expr "}"`                        |         |
| \[37\] | `ParamRef`                     | ::= | `"$" Digits`                              |         |

This is a Saxon 10.0 extension. This is a simplified syntax for *inline
function expressions*.

The parameters of a lambda function expression are not defined at the function
signature. Instead, they are referenced in the expression itself using the
`$number` syntax.

> __Example:__
>
> The lambda function `_{ $1 + $2 }` is equivalent to the inline function
> expression:
>
>     function ($arg1 as item()*, $arg2 as item()*) as item()* {
>       $arg1 + $arg2
>     }

The arity of the lambda function is the maximum value of the `ParamRef` primary
expressions in the lambda function body expression.

> __Example:__
>
> The lambda function `_{ $2 mod 2 = 0 }` has an arity of 2.

> __Note:__
>
> If there are no `ParamRef` primary expressions in the lambda function body
> expression, the lambda function has an arity of 0.

### 3.5 Arrow Operator (=>)

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[27\] | `ArrowExpr`                    | ::= | `UnaryExpr ( "=>" ( ArrowFunctionCall | ArrowDynamicFunctionCall ) )*` | |
| \[28\] | `ArrowFunctionCall`            | ::= | `EQName ArgumentList`                     |         |
| \[38\] | `ArrowDynamicFunctionCall`     | ::= | `( VarRef \| ParamRef \| ParenthesizedExpr ) PositionalArgumentList` | |

The `ParamRef` is for [Lambda Function Expressions](#3412-lambda-function-expressions)
support in Saxon 10.0.

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF for XPath 3.1 with Vendor Extensions

This EBNF grammar includes and modifies the EBNF grammar for the following
specifications:
1.  XPath 3.1 (W3C Recommendation 21 March 2017).

When an EBNF symbol is modified in an extension, that modified symbol is used
in preference to the base symbol definition. When the symbol is modified below,
that modified symbol is used in preference to the base or extension symbol
definition. When the EBNF symbol is a list such as `PrimaryExpr`, the result
used is the combination of the definitions of that symbol across all
specifications, including this document.

The EBNF symbols below only include new and modified symbols.

These changes include support for:
1.  XPath 4.0 Editor's Draft 12 December 2020 syntax;
1.  Saxon Vendor Extensions.

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                          | Options              |
|---------|--------------------------------|-----|-------------------------------------|----------------------|
| \[1\]   | `QuantifiedExpr`               | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[2\]   | `QuantifiedExprBinding`        | ::= | `"$" VarName "in" ExprSingle`       |                      |
| \[3\]   | `Wildcard`                     | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[4\]   | `WildcardIndicator`            | ::= | `"*"`                               |                      |
| \[5\]   | `ItemType`                     | ::= | `KindTest \| AnyItemTest \| FunctionTest \| MapTest \| ArrayTest \| LocalUnionType \| TupleType \| TypeAlias \| AtomicOrUnionType \| ParenthesizedItemType` | |
| \[6\]   | `AnyItemTest`                  | ::= | `"item" "(" ")"`                    |                      |
| \[7\]   | `ForExpr`                      | ::= | `SimpleForClause ReturnClause`      |                      |
| \[8\]   | `ReturnClause`                 | ::= | `"return" ExprSingle`               |                      |
| \[9\]   | `ExprSingle`                   | ::= | `ForExpr \| LetExpr \| QuantifiedExpr \| IfExpr \| TernaryConditionalExpr` | |
| \[10\]  | `TernaryConditionalExpr`       | ::= | `OrExpr "??" TernaryConditionalExpr "!!" TernaryConditionalExpr` | |
| \[11\]  | `PositionalArgumentList`       | ::= | `"(" PositionalArguments? ")"`      |                      |
| \[12\]  | `NillableTypeName`             | ::= | `TypeName "?"`                      |                      |
| \[13\]  | `ElementTest`                  | ::= | `"element" "(" (NameTest ("," (NillableTypeName | TypeName))?)? ")"` | |
| \[14\]  | `SequenceTypeList`             | ::= | `SequenceType ("," SequenceType)*`  |                      |
| \[15\]  | `TypedFunctionTest`            | ::= | `"function" "(" SequenceTypeList? ")" "as" SequenceType` | |
| \[16\]  | `LocalUnionType`               | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |               |
| \[17\]  | `TypedMapTest`                 | ::= | `"map" "(" ItemType "," SequenceType ")"` |                |
| \[18\]  | `SingleType`                   | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |                 |
| \[19\]  | `OrExpr`                       | ::= | `AndExpr (("or" \| "orElse") AndExpr)*`   |                |
| \[20\]  | `AndExpr`                      | ::= | `ComparisonExpr (("and" \| "andAlso") ComparisonExpr)*` |  |
| \[21\]  | `FunctionSignature`            | ::= | `"(" ParamList? ")" TypeDeclaration?` |                    |
| \[22\]  | `ParamList`                    | ::= | `ParamList ::= Param ("," Param)* "..."?` |                |
| \[23\]  | `FunctionItemExpr`             | ::= | `NamedFunctionRef \| InlineFunctionExpr \| ContextItemFunctionExpr \| LambdaFunctionExpr` | |
| \[24\]  | `ContextItemFunctionExpr`      | ::= | `(( "fn" "{" ) | ".{" ) Expr "}"`   |                      |
| \[25\]  | `TupleType`                    | ::= | `"tuple" "(" TupleField ("," TupleField)* ("," "*")? ")"` | |
| \[26\]  | `TupleField`                   | ::= | `TupleFieldName "?"? ( ( ":" | "as" ) SequenceType )?` |   |
| \[27\]  | `ArrowExpr`                    | ::= | `UnaryExpr ( "=>" ( ArrowFunctionCall | ArrowDynamicFunctionCall ) )*` | |
| \[28\]  | `ArrowFunctionCall`            | ::= | `EQName ArgumentList`               |                      |
| \[29\]  | `AttrbiuteTest`                | ::= | `"attribute" "(" (NameTest ("," TypeName)?)? ")"` |        |
| \[30\]  | `InlineFunctionExpr`           | ::= | `(("function" FunctionSignature) | ("->" FunctionSignature?)) FunctionBody` | |
| \[31\]  | `MultiplicativeExpr`           | ::= | `OtherwiseExpr ( ("*" | "div" | "idiv" | "mod") OtherwiseExpr )*` | |
| \[32\]  | `OtherwiseExpr`                | ::= | `UnionExpr ( "otherwise" UnionExpr )*` |                   |
| \[33\]  | `TupleFieldName`               | ::= | `NCName | StringLiteral`            |                      |
| \[34\]  | `TypeAlias`                    | ::= | `( "~" EQName ) | ( "type" "(" EQName ")" )` |             |
| \[35\]  | `LambdaFunctionExpr`           | ::= | `"_{" Expr "}"`                     |                      |
| \[36\]  | `PrimaryExpr`                  | ::= | `Literal \| VarRef \| ParamRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| UnaryLookup` | |
| \[37\]  | `ParamRef`                     | ::= | `"$" Digits`                        |                      |
| \[38\]  | `ArrowDynamicFunctionCall`     | ::= | `( VarRef \| ParamRef \| ParenthesizedExpr ) PositionalArgumentList` | |
| \[39\]  | `LetExpr`                      | ::= | `SimpleLetClause ReturnClause`      |                      |
| \[40\]  |                                | ::= |                                     |                      |
| \[41\]  | `SimpleForBinding`             | ::= | `"member"? "$" VarName "in" ExprSingle` |                  |
| \[42\]  | `PathExpr`                     | ::= | `("/" RelativePathExpr?) \| (AbbrevDescendantOrSelfStep RelativePathExpr) \| RelativePathExpr` | /\* xgc: leading-lone-slash \*/ |
| \[43\]  | `RelativePathExpr`             | ::= | `StepExpr (("/" \| AbbrevDescendantOrSelfStep) StepExpr)*` | |
| \[44\]  | `AbbrevDescendantOrSelfStep`   | ::= | `"//"`                              |                      |
| \[45\]  | `PostfixExpr`                  | ::= | `FilterExpr \| DynamicFunctionCall \| PostfixLookup \| PrimaryExpr` | |
| \[46\]  | `FilterExpr`                   | ::= | `PostfixExpr Predicate`             |                      |
| \[47\]  | `DynamicFunctionCall`          | ::= | `PostfixExpr PositionalArgumentList` |                     |
| \[48\]  | `PostfixLookup`                | ::= | `PostfixExpr Lookup`                |                      |
| \[49\]  | `AxisStep`                     | ::= | `FilterStep \| ReverseStep \| ForwardStep` |               |
| \[50\]  | `FilterStep`                   | ::= | `AxisStep Predicate`                |                      |
| \[51\]  | `ParenthesizedExpr`            | ::= | `EmptyExpr | ( "(" Expr ")" )`      |                      |
| \[52\]  | `EmptyExpr`                    | ::= | `"(" ")"`                           |                      |

### A.2 Reserved Function Names

| keyword                  | XPath Version                  |
|--------------------------|--------------------------------|
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
*  W3C. *XML Path Language (XPath) 4.0*. W3C Editor's Draft 16 December 2020.
   See [https://qt4cg.org/branch/master/xquery-40/xpath-40.html]().

### B.2 MarkLogic References
*  MarkLogic. *XPath Axes and Syntax*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_39877]().
*  MarkLogic. *Indexable Path Expression Grammar*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_17642]().
*  MarkLogic. *Patch and Extract Path Expression Grammar*. See
   [https://docs.marklogic.com/guide/xquery/xpath#id_98286]().

### B.3 EXPath References
__XPath NG__
*  EXPath. *Variadic Function Arguments*. Proposal 1, version 2. See
   [https://github.com/expath/xpath-ng/blob/0dded843cf1e7e21d357c9360bf5faf5b9e1e129/variadic-function-arguments.md]().
   Reece H. Dunn, 67 Bricks.
*  EXPath. *Conditional Expressions*. Proposal 2, version 1. See
   [https://github.com/expath/xpath-ng/blob/d2421975caacba75f0c9bd7fe017cc605e56b00f/conditional-expressions.md]().
   Michael Kay, Saxonica.
*  EXPath. *Extensions to unary and binary lookup expressions*. Proposal 3, version 1. See
   [https://github.com/expath/xpath-ng/blob/19a56aa6c01cf195306fcdd47a136dcab496cfc8/lookup-operator-extensions.md]().
   Michael Kay, Saxonica.
*  EXPath. *Concise Inline Function Syntax*. Proposal 5, version 1. See
   [https://github.com/expath/xpath-ng/blob/95676fd84266c13c5a4ace01af69783dd017a5c9/concise-inline-functions.md]().
   Michael Kay, Saxonica.
*  EXPath. *Anonymous Union Types*. Proposal 6, version 1. See
   [https://github.com/expath/xpath-ng/blob/9ff8fbf3bbc1f2f24b81671881154f35cb01bf28/union-types.md]().
   Michael Kay, Saxonica.
*  EXPath. *If Without Else*. Proposal 7, version 1. See
   [https://github.com/expath/xpath-ng/blob/ef330f640be3617ecc5ec53868de84f7d34e0ac6/if-without-else-ChristianGruen.md]().
   Christian Grün, BaseX GmbH.
*  EXPath. *Sequence, Map, and Array Decomposition*. Proposal 8, version 2. See
   [https://github.com/expath/xpath-ng/blob/bc6cb1b579d688ba0088abfe0e73b7e633f964aa/sequence-map-array-decomposition.md]().
   Reece H. Dunn, 67 Bricks.
*  EXPath. *Restricted sequences*. Proposal 11, version 3. See
   [https://github.com/expath/xpath-ng/blob/b9800b9882ded23d812641ef82209cc59861d6cc/restricted-sequences.md]().
   Reece H. Dunn, 67 Bricks.
*  EXPath. *Extended Element and Attribute Tests*. Proposal 13, version 1. See
   [https://github.com/expath/xpath-ng/blob/5b482550d164c8bf54e17b92f3e1d55e9f77bc6d/extended-element-attribute-tests.md]().
   Reece H. Dunn, 67 Bricks.

### B.4 Saxon References
__Saxon Documentation__
*  Saxonica. *Union types*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/union-types]().
*  Saxonica. *Tuple types*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/tuple-types]().
*  Saxonica. *Type aliases*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/type-aliases]().
*  Saxonica. *Simple inline functions. See
   [http://saxonica.com/documentation/#!extensions/syntax-extensions/simple-inline-functions]().
*  Saxonica. *Short-circuit boolean operators*. See
   [http://saxonica.com/documentation/#!extensions/syntax-extensions/short-circuit]().
*  Saxonica. *Otherwise operator*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/otherwise]().
*  Saxonica. *KindTests*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/kindtests]().
*  Saxonica. *For-member expressions*. See
   [http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/for-member-expressions]().

__Papers:__
*  XML Prague 2020. *A Proposal for XSLT 4.0*. See [http://www.saxonica.com/papers/xmlprague-2020mhk.pdf]().
   Michael Kay, Saxonica.

## C Vendor Extensions

### C.1 Saxon Vendor Extensions
The Saxon XQuery Processor supports the following vendor extensions described
in this document:
1.  [Tuple Type](#2122-tuple-type) \[Saxon 9.8\]
1.  [Type Alias](#2125-type-alias) \[Saxon 9.8\]
1.  [Logical Expressions](#32-logical-expressions) \[Saxon 9.9\] -- `orElse` and `andAlso`
1.  [Otherwise Expressions](#331-otherwise-expressions) \[Saxon 10.0\]
1.  [For Member Expressions](#311-for-member-expressions) \[Saxon 10.0\]

Saxon implements the following [EXPath Syntax Extensions](https://github.com/expath/xpath-ng):
1.  [Local Union Types](#2121-local-union-types) \[Saxon 9.8\]
1.  [Context Item Function Expressions](#3411-context-item-function-expressions) \[Saxon 9.8\]
1.  [Lambda Function Expressions](#3412-lambda-function-expressions) \[Saxon 10.0\]
1.  [Element Test](#2123-element-test) and [Attribute Test](#2124-attribute-test) \[Saxon 10.0\] -- wildcard names
