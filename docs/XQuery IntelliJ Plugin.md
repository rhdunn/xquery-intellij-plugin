# XQuery IntelliJ Plugin

## Abstract
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XQuery and associated W3C extensions. The
plugin-specific extensions are provided to support IntelliJ integration.

## Table of Contents
- [1 Introduction](#1-introduction)
- [2 Basics](#2-basics)
  - [2.1 Types](#21-types)
    - [2.1.1 SequenceType Syntax](#211-sequencetype-syntax)
    - [2.1.2 SequenceType Matching](#212-sequencetype-matching)
      - [2.1.2.1 Union Type](#2121-union-type)
      - [2.1.2.2 Tuple Type](#2122-tuple-type)
      - [2.1.2.3 Binary Test](#2123-binary-test)
      - [2.1.2.4 Schema Kind Tests](#2124-schema-kind-tests)
      - [2.1.2.5 Boolean Node Test](#2125-boolean-node-test)
      - [2.1.2.6 Number Node Test](#2126-number-node-test)
      - [2.1.2.7 Null Node Test](#2127-null-node-test)
      - [2.1.2.8 Array Node Test](#2128-array-node-test)
      - [2.1.2.9 Map Node Test](#2129-map-node-test)
      - [2.1.2.10 Specialised Sequence Types](#21210-specialised-sequence-types)
        - [2.1.2.10.1 Item Type Union](#212101-item-type-union)
        - [2.1.2.10.2 Tuple Sequence Types](#212102-tuple-sequence-types)
      - [2.1.2.11 Annotated Function Tests and Sequence Types](#21211-annotated-function-tests-and-sequence-types)
- [3 Expressions](#3-expressions)
  - [3.1 Node Constructors](#31-node-constructors)
  - [3.2 Quantified Expressions](#32-quantified-expressions)
  - [3.3 Expressions on SequenceTypes](#33-expressions-on-sequencetypes)
    - [3.3.1 Typeswitch](#331-typeswitch)
    - [3.3.2 Cast](#332-cast)
  - [3.4 Block Expressions](#34-block-expressions)
  - [3.5 Update Expressions](#35-update-expressions)
  - [3.6 Full Text Selections](#36-full-text-selections)
    - [3.6.1 Match Options](#361-match-options)
      - [3.6.1.1 Fuzzy Option](#3611-fuzzy-option)
  - [3.7 Primary Expressions](#37-primary-expressions)
    - [3.7.1 Non-Deterministic Function Calls](#371-non-deterministic-function-calls)
    - [3.7.2 Simple Inline Function Expressions](#372-simple-inline-function-expressions)
    - [3.7.3 Literals](#373-literals)
  - [3.8 JSON Constructors](#38-json-constructors)
    - [3.8.1 Maps](#381-maps)
    - [3.8.2 Arrays](#382-arrays)
    - [3.8.3 Booleans](#383-booleans)
    - [3.8.4 Numbers](#384-numbers)
    - [3.8.5 Nulls](#385-nulls)
  - [3.9 Path Expressions](#39-path-expressions)
    - [3.9.1 Axes](#391-axes)
  - [3.10 Validate Expressions](#310-validate-expressions)
  - [3.11 Try/Catch Expressions](#311-trycatch-expressions)
  - [3.12 Binary Constructors](#312-binary-constructors)
  - [3.13 Logical Expressions](#313-logical-expressions)
  - [3.14 Conditional Expressions](#314-conditional-expressions)
- [4 Modules and Prologs](#4-modules-and-prologs)
  - [4.1 Type Declaration](#41-type-declaration)
  - [4.2 Annotations](#42-annotations)
  - [4.3 Stylesheet Import](#43-stylesheet-import)
  - [4.4 Transactions](#44-transactions)
- [A XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [A.1 EBNF for XPath 3.1 with Vendor Extensions](#a1-ebnf-for-xpath-31-with-vendor-extensions)
  - [A.2 EBNF for XQuery 3.1 with Vendor Extensions](#a2-ebnf-for-xquery-31-with-vendor-extensions)
  - [A.3 Reserved Function Names](#a3-reserved-function-names)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
  - [B.2 BaseX References](#b2-basex-references)
  - [B.3 MarkLogic References](#b3-marklogic-references)
  - [B.4 Saxon References](#b4-saxon-references)
- [C Vendor Extensions](#c-vendor-extensions)
  - [C.1 BaseX Vendor Extensions](#c1-basex-vendor-extensions)
  - [C.2 MarkLogic Vendor Extensions](#c2-marklogic-vendor-extensions)
  - [C.3 Saxon Vendor Extensions](#c3-saxon-vendor-extensions)
  - [C.4 IntelliJ Plugin Extensions](#c4-intellij-plugin-extensions)
  - [C.5 eXist-db Extensions](#c5-exist-db-extensions)
  - [C.6 XPath 2.0 Working Draft 02 May 2003](#c6-xpath-20-working-draft-02-may-2003)
  - [C.7 XQuery 1.0 Working Draft 02 May 2003](#c7-xquery-10-working-draft-02-may-2003)
  - [C.8 EXPath Syntax Extensions](#c8-expath-syntax-extensions)
- [D Error and Warning Conditions](#d-error-and-warning-conditions)
  - [D.1 Vendor-Specific Behaviour](#d1-vendor-specific-behaviour)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XQuery 3.1, XQuery and XPath Full Text 3.0, XQuery
Update Facility 3.0, and XQuery Scripting Extension 1.0.

The plugin supports BaseX, eXist-db, MarkLogic, and Saxon vendor extensions.
These are listed in appendix [C Vendor Extensions](#c-vendor-extensions),
grouped by the XQuery vendor, with links to the relevant parts of this document
that describe the specific extensions.

The plugin also provides plugin-specific extensions to support IntelliJ
integration. These are listed in appendix
[C.4 IntelliJ Plugin Extensions](#c4-intellij-plugin-extensions), with links to
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
| \[20\] | `ItemType`              | ::= | `KindTest \| AnyItemType \| AnnotatedFunctionOrSequence \| MapTest \| ArrayTest \| TupleType \| UnionType \| AtomicOrUnionType \| ParenthesizedSequenceType` | |
| \[78\] | `SequenceType`          | ::= | `(("empty-sequence" \| "empty") "(" ")") \| (ItemType OccurrenceIndicator?)` | |
| \[21\] | `TypedMapTest`          | ::= | `"map" "(" (UnionType \| AtomicOrUnionType) "," SequenceType ")"` | |
| \[28\] | `KindTest`              | ::= | `DocumentTest \| ElementTest \| AttributeTest \| SchemaElementTest \| SchemaAttributeTest \| PITest \| CommentTest \| TextTest \| NamespaceNodeTest \| AnyKindTest \| NamedKindTest \| BinaryTest \| SchemaKindTest \| JsonKindTest` | |
| \[46\] | `JsonKindTest`          | ::= | `BooleanNodeTest \| NumberNodeTest \| NullNodeTest \| ArrayNodeTest \| MapNodeTest` | |
| \[67\] | `AnyKindTest`           | ::= | `"node" "(" ("*")? ")"`             |         |
| \[68\] | `NamedKindTest`         | ::= | `"node" "(" StringLiteral ")"`      |         |
| \[69\] | `TextTest`              | ::= | `AnyTextTest \| NamedTextTest`      |         |
| \[70\] | `AnyTextTest`           | ::= | `"text" "(" ")"`                    |         |
| \[71\] | `NamedTextTest`         | ::= | `"text" "(" StringLiteral ")"`      |         |
| \[72\] | `DocumentTest`          | ::= | `"document-node" "(" (ElementTest \| SchemaElementTest \| AnyArrayNodeTest \| AnyMapNodeTest)? ")"` | |
| \[88\] | `AnyItemType`           | ::= | `"item" "(" ")"`                    |         |

MarkLogic 8.0 supports `node(*)` and `NamedKindTest` for selecting any JSON node
in objects by the key name.

MarkLogic 8.0 supports `NamedTextTest` for selecting JSON text nodes in objects
by the key name.

MarkLogic 8.0 supports `document-node(array-node())` for JSON documents with an
array at the top level, and `document-node(object-node())` for JSON documents
with an object (map) at the top level.

XQuery 1.0 Working Draft 02 May 2003 uses `empty()` for `empty-sequence()`.
This is supported by eXist-db prior to 4.0 and the MarkLogic `0.9-ml` XQuery
version.

#### 2.1.2 SequenceType Matching

##### 2.1.2.1 Union Type

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[22\] | `UnionType`             | ::= | `"union" "(" EQName ("," EQName)* ")"` |                    |

The `UnionType` is a new sequence type supported by Saxon 9.8. It is
[proposal 6](https://github.com/expath/xpath-ng/pull/6) of the EXPath
syntax extensions for XPath and XQuery.

A `UnionType` defines a union type whose members are the `EQName` types listed
in the type definition. These types are restricted to being atomic types (that
is, they cannot be list, union, or other complex types).

If a member type has a namespace prefix, the namespace prefix is resolved to a
namespace URI using the
[statically known namespaces](https://www.w3.org/TR/xquery-31/#dt-static-namespaces)<sup><em>XQ31</em></sup>.
If the member type has no namespace prefix, it is implicitly qualified by the
[default element/type namespace](https://www.w3.org/TR/xquery-31/#dt-def-elemtype-ns)<sup><em>XQ31</em></sup>.

> __Example:__
>
> `xs:numeric` can be defined as:
>
>     declare type xs:numeric = union(xs:float, xs:double, xs:decimal);

##### 2.1.2.2 Tuple Type

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[23\] | `TupleType`             | ::= | `"tuple" "(" TupleField ("," TupleField)* ("," "*")? ")"` | |
| \[24\] | `TupleField`            | ::= | `NCName "?" (":" SequenceType)?`    |                       |

The `TupleType` is a new sequence type supported by Saxon 9.8.

In Saxon 9.9, a `TupleField` can be optional by adding a `?` after the field name.

\[Definition: An *extensible* tuple is a tuple that has some fields specified,
but allows other fields to be included in the map object.\] An *extensible*
tuple is specified by having the last tuple field be the `*` wildcard operator.
This is supported by Saxon 9.9.

##### 2.1.2.3 Binary Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[29\]  | `BinaryTest`            | ::= | `"binary" "(" ")"`                  |         |

This is a MarkLogic vendor extension to select or check for a binary data
object.

> __Note:__
>
> This does not select the binary XML schema types `xs:hexBinary` or
> `xs:base64Binary`. They need to be wrapped in a binary node constructor
> as a type convertible to `xs:hexBinary`.

##### 2.1.2.4 Schema Kind Tests

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[36\]  | `SchemaKindTest`        | ::= | `AttributeDeclTest \| ComplexTypeTest \| ElementDeclTest \| SchemaComponentTest \| SchemaParticleTest \| SchemaRootTest \| SchemaTypeTest \| SimpleTypeTest \| SchemaFacetTest` | |
| \[37\]  | `AttributeDeclTest`     | ::= | `"attribute-decl" "(" ")"`          |         |
| \[38\]  | `ComplexTypeTest`       | ::= | `"complex-type" "(" ")"`            |         |
| \[39\]  | `ElementDeclTest`       | ::= | `"element-decl" "(" ")"`            |         |
| \[40\]  | `SchemaComponentTest`   | ::= | `"schema-component" "(" ")"`        |         |
| \[41\]  | `SchemaParticleTest`    | ::= | `"schema-particle" "(" ")"`         |         |
| \[42\]  | `SchemaRootTest`        | ::= | `"schema-root" "(" ")"`             |         |
| \[43\]  | `SchemaTypeTest`        | ::= | `"schema-type" "(" ")"`             |         |
| \[44\]  | `SimpleTypeTest`        | ::= | `"simple-type" "(" ")"`             |         |
| \[45\]  | `SchemaFacetTest`       | ::= | `"schema-facet" "(" ")"`            |         |

MarkLogic 7.0 provides `SchemaKindTest` types for working with XML Schema defined
types as part of its schema components built-in functions. MarkLogic 8.0 adds
support for `SchemaFacetTest`.

##### 2.1.2.5 Boolean Node Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[47\]  | `BooleanNodeTest`       | ::= | `AnyBooleanNodeTest \| NamedBooleanNodeTest` | |
| \[48\]  | `AnyBooleanNodeTest`    | ::= | `"boolean-node" "(" ")"`            |         |
| \[49\]  | `NamedBooleanNodeTest`  | ::= | `"boolean-node" "(" StringLiteral ")"` |      |

MarkLogic 8.0 provides `BooleanNodeTest` types for working with boolean (`true`
and `false`) JSON values. The `NamedBooleanNodeTest` variant selects JSON
boolean nodes in objects by the key name.

##### 2.1.2.6 Number Node Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[51\]  | `NumberNodeTest`        | ::= | `AnyNumberNodeTest \| NamedNumberNodeTest` |  |
| \[52\]  | `AnyNumberNodeTest`     | ::= | `"number-node" "(" ")"`             |         |
| \[53\]  | `NamedNumberNodeTest`   | ::= | `"number-node" "(" StringLiteral ")"` |       |

MarkLogic 8.0 provides `NumberNodeTest` types for working with numeric JSON
values. The `NamedNumberNodeTest` variant selects JSON number nodes in objects
by the key name.

##### 2.1.2.7 Null Node Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[55\]  | `NullNodeTest`          | ::= | `AnyNullNodeTest \| NamedNullNodeTest` |      |
| \[56\]  | `AnyNullNodeTest`       | ::= | `"null-node" "(" ")"`               |         |
| \[57\]  | `NamedNullNodeTest`     | ::= | `"null-node" "(" StringLiteral ")"` |         |

MarkLogic 8.0 provides `NullNodeTest` types for working with `null` JSON values.
The `NamedNullNodeTest` variant selects JSON null nodes in objects by the key name.

##### 2.1.2.8 Array Node Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[59\]  | `ArrayNodeTest`         | ::= | `AnyArrayNodeTest \| NamedArrayNodeTest` |     |
| \[60\]  | `AnyArrayNodeTest`      | ::= | `"array-node" "(" ")"`              |         |
| \[61\]  | `NamedArrayNodeTest`    | ::= | `"array-node" "(" StringLiteral ")"` |        |

MarkLogic 8.0 provides `ArrayNodeTest` types for working with JSON arrays. The
`NamedArrayNodeTest` variant selects JSON array nodes in objects by the key name.

##### 2.1.2.9 Map Node Test

| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[63\]  | `MapNodeTest`           | ::= | `AnyMapNodeTest \| NamedMapNodeTest` |        |
| \[64\]  | `AnyMapNodeTest`        | ::= | `"object-node" "(" ")"`             |         |
| \[65\]  | `NamedMapNodeTest`      | ::= | `"object-node" "(" StringLiteral ")"` |       |

MarkLogic 8.0 provides `MapNodeTest` types for working with JSON objects. The
`NamedMapNodeTest` variant selects JSON object nodes in objects by the key name.

##### 2.1.2.10 Specialised Sequence Types

| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[85\] | `ParenthesizedSequenceType`    | ::= | `ParenthesizedItemType \| ItemTypeUnion \| TupleSequenceType` | |

###### 2.1.2.10.1 Item Type Union

| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[86\] | `ItemTypeUnion`                | ::= | `"(" ItemType ("\|" ItemType)* ")"` |                       |

The `ItemTypeUnion` construct is an XQuery IntelliJ Plugin extension that is
based on the XQuery Formal Semantics specification. This is used in the
definition of built-in functions for parameters and return types that can have
one of multiple disjoint types.

> __Example:__
>
>     declare function load-json($filename as xs:string) as (map(*) | array(*)) external;

###### 2.1.2.10.2 Tuple Sequence Types

| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[87\] | `TupleSequenceType`            | ::= | `"(" ItemType ("," ItemType)* ")"`  |                       |

The `TupleSequenceType` construct is an XQuery IntelliJ Plugin extension.
This is used in the definition of built-in functions for parameters and
return types that return sequence-based tuples.

A typed sequence defines the type of each item in a sequence of a specified
length. This is useful for defining sequence-based tuple return types such
as rational or complex numbers.

> __Example:__
>
> `complex` can be defined as:
>
>     declare type complex = (xs:double, xs:double);

##### 2.1.2.11 Annotated Function Tests and Sequence Types

| Ref    | Symbol                        |     | Expression                          | Options |
|--------|-------------------------------|-----|-------------------------------------|---------|
| \[89\] | `AnnotatedFunctionOrSequence` | ::= | `AnnotatedSequenceType \| FunctionTest` |     |
| \[90\] | `AnnotatedSequenceType`       | ::= | `Annotation Annotation* "for" SequenceType` | |

The XQuery IntelliJ Plugin provides a vendor extension to support annotations
on a type itself, not just on function signatures. This is used in the
definition of built-in functions for parameters and return types to provide
information to the plugin on how those types are used.

> __Example:__
>
>     declare function f() as (%since("3.2") %until("4.0") for item() |
>                              %since("4.0") for node()) external;

The construction of annotations on item types is designed to be unambiguous
with XQuery 3.1 `FunctionTest` that may specify annotations on function signatures.

> __Example:__
>
>     declare function f() as %test for %public function() as item() external;
>
> In this example, `%test` applies to the type and `%public` applies to the
> function signature.

## 3 Expressions

| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[73\] | `Expr`                  | ::= | `ApplyExpr`                         |           |
| \[74\] | `ApplyExpr`             | ::= | `ConcatExpr (";" (ConcatExpr ";")*)?` |         |
| \[75\] | `ConcatExpr`            | ::= | `ExprSingle ("," ExprSingle)*`      |           |
| \[91\] | `ExprSingle`            | ::= | `FLWORExpr \| QuantifiedExpr \| SwitchExpr \| TypeswitchExpr \| IfExpr \| TryCatchExpr \| TernaryIfExpr` | | 

The `Expr`, `ApplyExpr` and `ConcatExpr` definitions are identical to the
W3C Scripting Extension. The XPath grammar has the following modifications:

| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[74\] | `ApplyExpr`             | ::= | `ConcatExpr`                        |           |

This is done to keep the XPath and XQuery grammars consistent in the presence
of the W3C Scripting Extension. It does not alter XPath as multiple
`ConcatExpr` constructs are not permitted, so it is equivalent to the original
XPath `Expr` grammar definition.

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

### 3.3 Expressions on SequenceTypes

#### 3.3.1 Typeswitch

| Ref   | Symbol                  |     | Expression                          | Options               |
|-------|-------------------------|-----|-------------------------------------|-----------------------|
| \[5\] | `TypeswitchExpr`        | ::= | `"typeswitch" "(" Expr ")" CaseClause+ DefaultCaseClause` | |
| \[6\] | `DefaultCaseClause`     | ::= | `"default" ("$" VarName)? "return" ExprSingle` |            |

The default case expression is factored out here into a separate grammar
production similar to the `CaseClause` expression, making it easier to
support variable bindings.

#### 3.3.2 Cast

| Ref   | Symbol                  |     | Expression                          | Options               |
|-------|-------------------------|-----|-------------------------------------|-----------------------|
| \[7\] | `CastExpr`              | ::= | `TransformWithExpr ( "cast" "as" SingleType )?` |           |
| \[8\] | `TransformWithExpr`     | ::= | `ArrowExpr ("transform" "with" "{" Expr? "}")?` |           |

The XQuery 3.1 and Update Facility 3.0 specifications both define constructs
for use between `CastExpr` and `UnaryExpr`. These are `ArrowExpr` for XQuery
3.1 and `TransformWithExpr` for Update Facility 3.0.

This plugin follows the precedence of `CastExpr`, `TransformWithExpr`, and
`ArrowExpr` described in the
[proposed official resolution](https://www.w3.org/Bugs/Public/show_bug.cgi?id=30015).
While XQuery 3.1 with UpdateFacility 3.0 is not an official W3C combination,
it is supported by BaseX.

> __Example:__
>
> Arrow expressions can be combined with `transform with` expressions, such as:
>
>     <lorem>ipsum</lorem>/text() => root() transform with { rename node . as "test" }
>     (: returns: <test>ipsum</test> :)

### 3.4 Block Expressions

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[9\]  | `BlockVarDecl`          | ::= | `"declare" BlockVarDeclEntry ("," BlockVarDeclEntry)*` |    |
| \[10\] | `BlockVarDeclEntry`     | ::= | `"$" VarName TypeDeclaration? (":=" ExprSingle)?` |         |

This follows the grammar production pattern used in other constructs like
`LetClause` and `ForClause`, making it easier to support variable declarations.

### 3.5 Update Expressions

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[12\] | `UpdateExpr`            | ::= | `ComparisonExpr ("update" (EnclosedExpr \| ExprSingle))*` | |

BaseX 7.8 supports update expressions with a single inline expression.

> __Example:__
>
>     <text>hello</text> update rename node . as "test"
>     (: returns: <test>hello</test> :)

BaseX 8.5 extends this to support multiple update operations in a block
expression.

> __Example:__
>
>     <text>hello</text> update { rename node . as "test" }
>     (: returns: <test>hello</test> :)

> __Note__
>
> While both forms can be chained, the single inline expression form will apply
> the updates from right to left instead of left to right. That is, given the
> chained single inline expression form of `UpdateExpr`:
>
> ```N update A update B```
>
> that expression is equivalent to:
>
> ```N update (A update B)```

The update expression is optional. If it is missing, the update expression has
no effect.

> __Example:__
>
>     <text>hello</text> update (), <text>hello</text> update {}
>     (: returns: ( <text>hello</text>, <text>hello</text> ) :)

The expressions on the right hand side of the update expression must either be
an `empty-sequence()`, or an updating expression. If this is not the case, an
`err:XUST0002` error is raised.

If N and U are arbitrary expressions, then the following inline `UpdateExpr`:

    N update U

and the following block `UpdateExpr`:

    N update { U }

are equivalent to the `TransformWithExpr`:

    N transform with { U }

### 3.6 Full Text Selections

#### 3.6.1 Match Options

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[13\] | `FTMatchOption`         | ::= | `FTLanguageOption \| FTWildCardOption \| FTThesaurusOption \| FTStemOption \| FTCaseOption \| FTDiacriticsOption \| FTStopWordOption \| FTExtensionOption \| FTFuzzyOption` | |

The `FTFuzzyOption` is a new option that is supported by BaseX.

##### 3.6.1.1 Fuzzy Option

| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[14\] | `FTFuzzyOption`         | ::= | `fuzzy`                             |                       |

\[Definition: A *fuzzy option* enables approximate text matching using the
Levenshtein distance algorithm.\]

This is a BaseX Full Text extension.

### 3.7 Primary Expressions

| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[15\] | `PrimaryExpr`           | ::= | `Literal \| VarRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| NonDeterministicFunctionCall \| OrderedExpr \| UnorderedExpr \| NodeConstructor \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| BooleanConstructor \| NumberConstructor \| NullConstructor \| BinaryConstructor \| StringConstructor \| UnaryLookup` | |
| \[80\] | `FunctionItemExpr`      | ::= | `NamedFunctionRef \| InlineFunctionExpr \| SimpleInlineFunctionExpr` | | 

### 3.7.1 Non-Deterministic Function Calls

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[16\] | `NonDeterministicFunctionCall` | ::= | `"non-deterministic" VarRef ArgumentList` |         |

\[Definition: A *non-deterministic* function call is a function that has side
effects.\] This is used to disable various query optimizations that would be
applied if the function call is deterministic.

This is a BaseX 8.4 extension to help the query compiler identify
non-deterministic function calls, where the non-deterministic property cannot
be determined statically.

#### 3.7.2 Simple Inline Function Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[81\] | `SimpleInlineFunctionExpr`     | ::= | `"fn" "{" Expr "}"`                       |         |

This is a Saxon 9.8 extension. It is a syntax variant of the focus
function alternative for inline functions in
[proposal 5](https://github.com/expath/xpath-ng/pull/5) of the EXPath
syntax extensions for XPath and XQuery.

The expression `fn{E}` is equivalent to:
>     function ($arg as item()) as item()* { $arg ! (E) }

#### 3.7.3 Literals

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[82\] | `PredefinedEntityRef`          | ::= | `EntityRef`                               |         |
| \[83\] | `EntityRef`                    | ::= | \[[https://www.w3.org/TR/xml/#NT-EntityRef]()\] |   |
| \[84\] | `Name`                         | ::= | \[[https://www.w3.org/TR/xml/#NT-Name]()\] |        |

MarkLogic 6.0 supports HTML4 and HTML5 predefined entity references in addition
to XML entity references. Other XQuery processors only support XML entity
references (`&lt;`, `&gt;`, `&amp;`, `&quot;`, and `&apos;`). If the predefined
entity reference is not supported by the XQuery processor, an `ije:IJVS0003`
error is raised.

### 3.8 JSON Constructors

#### 3.8.1 Maps

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[66\] | `MapConstructor`               | ::= | `("map" \| "object-node") "{" (MapConstructorEntry ("," MapConstructorEntry)*)? "}"` | |
| \[17\] | `MapConstructorEntry`          | ::= | `MapKeyExpr (":" \| ":=") MapValueExpr`   |         |

MarkLogic 8.0 uses the `object-node` keyword for defining JSON objects, and the
XQuery 3.1 syntax (`:`) for map entries.

Saxon versions 9.4 to 9.6 used `:=` to separate the key and value in a map entry.
From 9.7, the XQuery 3.1 syntax (`:`) is used.

#### 3.8.2 Arrays

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[62\] | `CurlyArrayConstructor`        | ::= | `("array" \| "array-node") EnclosedExpr`  |         |

MarkLogic 8.0 uses the `array-node` keyword for defining JSON arrays. It does
not support the square array style constructors.

#### 3.8.3 Booleans

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[50\] | `BooleanConstructor`           | ::= | `"boolean-node" "{" Expr "}"`             |         |

MarkLogic 8.0 provides `BooleanTest` types for working with boolean (`true` and
`false`) JSON values.

A boolean constructor is evaluated using the supplied content expression as
follows:
1.  If the expression is an empty sequence, the result is an empty sequence.
1.  Otherwise, the result is a boolean node with the expression cast to
    `xs:boolean` as its content. If the cast fails, an `err:FORG0001` (invalid
    cast) error is raised.

A boolean node follows the rules for casting from an `xs:boolean` type, using
the content of the boolean node in the cast. However, a boolean node is not an
instance of `xs:boolean`.

#### 3.8.4 Numbers

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[54\] | `NumberConstructor`            | ::= | `"number-node" "{" Expr "}"`              |         |

MarkLogic 8.0 provides `NumberTest` types for working with numeric JSON values.

A numeric constructor is evaluated using the supplied content expression as
follows:
1.  If the expression is an empty sequence, the result is an empty sequence.
1.  Otherwise, the result is a number node with the expression cast to
    `xs:double` as its content. If the cast fails, an `err:FORG0001` (invalid
    cast) error is raised.

A number node follows the rules for casting from an `xs:double` type, using
the content of the number node in the cast. However, a number node is not an
instance of `xs:double`.

#### 3.8.5 Nulls

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[58\] | `NullConstructor`              | ::= | `"null-node" "{" "}"`                     |         |

MarkLogic 8.0 provides `NullTest` types for working with `null` JSON values.

Null nodes are not removed from sequences, such as when used in arrays and maps.

### 3.9 Path Expressions

#### 3.9.1 Axes

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[25\] | `ForwardAxis`                  | ::= | `("child" "::") \| ("descendant" "::") \| ("attribute" "::") \| ("self" "::") \| ("descendant-or-self" "::") \| ("following-sibling" "::") \| ("following" "::") \| ("namespace" "::") \| ("property" "::")` | |

XQuery IntelliJ Plugin supports the axes from XQuery and XPath. This includes
support for the `namespace` axis as part of XQuery, as MarkLogic supports
that axis within XQuery expressions. The following additional axes are
supported:

*  The `property` axis is a MarkLogic specific axis that contains all the
   element nodes of the properties XML file associated with the document
   the context node belongs to. The `property::node` expression is equivalent
   to `. ! xdmp:document-properties(base-uri(.))/prop:properties/node`.

The *principal node kind* is determined as per the XPath specification. Thus:

*  For the `attribute` axis, the principal node kind is *attribute*.
*  For the `namespace` axis, the principal node kind is *namespace*.
*  For all other axes (including the MarkLogic `property` axis), the principal
   node kind is *element*.

> __Note__
>
> This means that the `property` axis uses the default element namespace to
> resolve an unprefixed QName into an expanded QName.

#### 3.9.2 Node Tests

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[76\] | `Wildcard`                     | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[77\] | `WildcardIndicator`            | ::= | `"*"`                                     |         |

The changes to `Wildcard` are to make it work like the `EQName` grammar
productions. This is such that `WildcardIndicator` is placed wherever an
`NCName` can occur in an `EQName`. That is, either the prefix or local
name (but not both) can be `WildcardIndicator`.

A `WildcardIndicator` is an instance of `xdm:wildcard`.

### 3.10 Validate Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[27\] | `ValidateExpr`                 | ::= | `"validate" ( ValidationMode \| ( ( "type" \| "as" ) TypeName ) )? "{" Expr "}"` | |

MarkLogic uses the `at` keyword instead of the XQuery 3.0 `type` keyword for
typed validation expressions.

### 3.11 Try/Catch Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[31\] | `CatchClause`                  | ::= | `"catch" (CatchErrorList \| ("(" "$" VarName ")")) EnclosedExpr` | |

MarkLogic only allows a single `CatchClause` for a given try/catch expression,
using the parenthesis style catch clause. It does not support the XQuery 3.0
style catch clauses using a `CatchErrorList`.

The variable name in the MarkLogic style catch clause has the type
`element(error:error)`, which contains the details of the error.

### 3.12 Binary Constructors

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[30\] | `BinaryConstructor`            | ::= | `"binary" EnclosedExpr`                   |         |

This is a MarkLogic extension for working with `xs:hexBinary` encoded binary
data. Specifically, MarkLogic only stores `node()` types in the database and
this is used when storing binary data in the database. Other XQuery processors
use `xs:hexBinary` and/or `xs:base64Binary` directly.

A binary constructor is evaluated using the supplied content expression as
follows:
1.  If the expression is an empty sequence, the result is an empty sequence.
1.  If the expression is an `xs:hexBinary`, the result is a binary node with
    the expression as its content.
1.  If the expression is an `xs:untypedAtomic`, `xs:string`, or a type derived,
    from `xs:string`, the result is a binary node with the expression cast to
    `xs:hexBinary` as its content.
1.  Otherwise, an `err:FORG0001` (invalid cast) error is raised.

> __Note__
>
> A binary constructor expression does not follow the standard casting rules for
> `xs:hexBinary` in that `xs:base64Binary` content cannot be assigned to a binary
> constructor without an explicit cast.

Casting from a binary node to a target type is performed as follows:
1.  If the target type is castable from `xs:hexBinary`, the content of the
    binary node is cast to the target type.
1.  If the target type is `xs:boolean`, the result is `false()`.
1.  Otherwise, an `err:FORG0001` (invalid cast) error is raised.

A binary node is not an instance of `xs:boolean`.

### 3.13 Logical Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[79\] | `OrExpr`                       | ::= | `AndExpr (("or" \| "orElse") AndExpr)*`   |         |
| \[11\] | `AndExpr`                      | ::= | `UpdateExpr (("and" \| "andAlso") UpdateExpr)*` |   |

The `orElse` and `andAlso` expressions are new logical expression supported by Saxon 9.9.

The `orElse` expression evaluates the left hand side (`lhs`) first, and only
evaluates the right hand side (`rhs`) if the left hand side is false. This is
equivalent to:
>     if (lhs) then fn:true() else xs:boolean(rhs)

The `andAlso` expression evaluates the left hand side (`lhs`) first, and only
evaluates the right hand side (`rhs`) if the left hand side is true. This is
equivalent to:
>     if (lhs) then xs:boolean(rhs) else fn:false()

### 3.14 Conditional Expressions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[92\] | `TernaryIfExpr`                | ::= | `ElvisExpr "??" ElvisExpr "!!" ElvisExpr` |         |
| \[93\] | `ElvisExpr`                    | ::= | `OrExpr "?!" OrExpr`                      |         |
| \[94\] | `IfExpr`                       | ::= | `"if" "(" Expr ")" "then" ExprSingle ("else" ExprSingle)?` | |

The `IfExpr` without the else branch is supported by BaseX 9.1. It is defined
in [proposal 7](https://github.com/expath/xpath-ng/pull/7) of the EXPath syntax
extensions for XPath and XQuery.

When the else branch of an `IfExpr` is not present, an empty sequence is returned
if the effective boolean value of the`IfExpr` condition evaluates to false.

The `TernaryIfExpr` and `ElvisExpr` are new expressions supported by BaseX 9.1.
They are defined in [proposal 2](https://github.com/expath/xpath-ng/pull/2) of
the EXPath syntax extensions for XPath and XQuery.

Given the `TernaryIfExpr`:

    C ?? A !! B

the equivalent `IfExpr` is:

    if (C) then A else B

Given the `ElvisExpr`:

    A ?: B

the equivalent `IfExpr` is:

    let $a := A
    return if (exists($a)) then $a else B

## 4 Modules and Prologs

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[18\] | `Prolog`                       | ::= | `((DefaultNamespaceDecl \| Setter \| NamespaceDecl \| Import \| TypeDecl) Separator)* ((ContextItemDecl \| AnnotatedDecl \| OptionDecl) Separator)*` | |

### 4.1 Type Declaration

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[19\] | `TypeDecl`                     | ::= | `"declare" "type" QName "=" ItemType`     |         |

\[Definition: A *type declaration* declares a type alias name and associates
it with an item type, adding the (name, type) pair to the
[in-scope schema types](https://www.w3.org/TR/xquery-31/#dt-is-types).\] The
type declaration is in scope throughout the query in which it is declared.

If the type name in a type declaration has a namespace prefix, the namespace
prefix is resolved to a namespace URI using the
[statically known namespaces](https://www.w3.org/TR/xquery-31/#dt-static-namespaces)<sup><em>XQ31</em></sup>.
If the type name has no namespace prefix, it is implicitly qualified by the
[default element/type namespace](https://www.w3.org/TR/xquery-31/#dt-def-elemtype-ns)<sup><em>XQ31</em></sup>.

### 4.2 Annotations

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[26\] | `CompatibilityAnnotation`      | ::= | `"assignable" \| "private" \| "sequential" \| "simple" \| "unassignable" \| "updating"` | |

The bare keyword `private` is a MarkLogic extension that is allowed on a
function or variable declared in the prolog for backwards compatibility with
XQuery 1.0, and behaves exactly as if the `%private` annotation was specified
instead.

The `updating` compatibility annotation is defined in XQuery Update Facility 3.0.

The other compatibility annotations are defined in Scripting Extension 1.0.

### 4.3 Stylesheet Import

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[32\] | `Import`                       | ::= | `SchemaImport \| ModuleImport \| StylesheetIport` | |
| \[33\] | `StylesheetImport`             | ::= | `"import" "stylesheet" "at" URILiteral`   |         |

MarkLogic supports importing the functions and variables from an XLST stylesheet.

### 4.4 Transactions

| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[34\] | `Module`                       | ::= | `VersionDecl? (LibraryModule \| (MainModule (TransactionSeparator VersionDecl? MainModule)* ))` | |
| \[35\] | `TransactionSeparator`         | ::= | `";"`                                     |         |

MarkLogic supports transactions. These are `MainModule` expressions that are
separated by a semicolon.

If specified, the `VersionDecl` must be the same in all transactions; if missing,
the current `MainModule` uses the version from the first `MainModule`. If the
version is different, an `XDMP-XQUERYVERSIONSWITCH` error is raised.

> __Note__
>
> Only version values `0.9-ml` and `1.0-ml` support transactions.

Unlike Scripting Extensions, MarkLogic transactions have a separate prolog for
each transaction. Due to the way the syntax is constructed, MarkLogic
transactions without a prolog will be parsed according to the Scripting Extension
syntax.

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

These changes include support for:
1.  XQuery 1.0 Working Draft 02 May 2003 syntax;
1.  Saxon Vendor Extensions;
1.  XQuery IntelliJ Plugin Vendor Extensions.

__NOTE:__ This is an incomplete list of EBNF symbols, as the XQuery IntelliJ
Plugin does not currently support XPath (it only supports XPath exprssions
that are a part of XQuery).

| Ref     | Symbol                  |     | Expression                          | Options              |
|---------|-------------------------|-----|-------------------------------------|----------------------|
| \[3\]   | `QuantifiedExpr`        | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[4\]   | `QuantifiedExprBinding` | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` |          |
| \[11\]  | `AndExpr`               | ::= | `ComparisonExpr (("and" \| "andAlso") ComparionExpr)*` |  |
| \[73\]  | `Expr`                  | ::= | `ApplyExpr`                         |                     |
| \[74\]  | `ApplyExpr`             | ::= | `ConcatExpr`                        |                     |
| \[75\]  | `ConcatExpr`            | ::= | `ExprSingle ("," ExprSingle)*`      |                     |
| \[76\]  | `Wildcard`              | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[77\]  | `WildcardIndicator`     | ::= | `"*"`                               |                     |
| \[78\]  | `SequenceType`          | ::= | `(("empty-sequence" \| "empty") "(" ")") \| (ItemType OccurrenceIndicator?)` | |
| \[79\]  | `OrExpr`                | ::= | `AndExpr (("or" \| "orElse") AndExpr)*`   |               |

### A.2 EBNF for XQuery 3.1 with Vendor Extensions

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
1.  XQuery 1.0 Working Draft 02 May 2003 syntax;
1.  BaseX Vendor Extensions;
1.  MarkLogic Vendor Extensions;
1.  Saxon Vendor Extensions;
1.  XQuery IntelliJ Plugin Vendor Extensions.

| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[1\]    | `DirAttributeList`             | ::= | `(S DirAttribute?)*`                | /\* ws: explicit \*/  |
| \[2\]    | `DirAttribute`                 | ::= | `QName S? "=" S? DirAttributeValue` | /\* ws: explicit \*/  |
| \[3\]    | `QuantifiedExpr`               | ::= | `("some" \| "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle` | |
| \[4\]    | `QuantifiedExprBinding`        | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` |            |
| \[5\]    | `TypeswitchExpr`               | ::= | `"typeswitch" "(" Expr ")" CaseClause+ DefaultCaseClause` | |
| \[6\]    | `DefaultCaseClause`            | ::= | `"default" ("$" VarName)? "return" ExprSingle` |            |
| \[7\]    | `CastExpr`                     | ::= | `TransformWithExpr ( "cast" "as" SingleType )?` |           |
| \[8\]    | `TransformWithExpr`            | ::= | `ArrowExpr ("transform" "with" "{" Expr? "}")?` |           |
| \[9\]    | `BlockVarDecl`                 | ::= | `"declare" BlockVarDeclEntry ("," BlockVarDeclEntry)*` |    |
| \[10\]   | `BlockVarDeclEntry`            | ::= | `"$" VarName TypeDeclaration? (":=" ExprSingle)?` |         |
| \[11\]   | `AndExpr`                      | ::= | `UpdateExpr (("and" \| "andAlso") UpdateExpr)*` |           |
| \[12\]   | `UpdateExpr`                   | ::= | `ComparisonExpr ("update" (EnclosedExpr \| ExprSingle))*` | |
| \[13\]   | `FTMatchOption`                | ::= | `FTLanguageOption \| FTWildCardOption \| FTThesaurusOption \| FTStemOption \| FTCaseOption \| FTDiacriticsOption \| FTStopWordOption \| FTExtensionOption \| FTFuzzyOption` | |
| \[14\]   | `FTFuzzyOption`                | ::= | `fuzzy`                             |                       |
| \[15\]   | `PrimaryExpr`                  | ::= | `Literal \| VarRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| NonDeterministicFunctionCall \| OrderedExpr \| UnorderedExpr \| NodeConstructor \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| BooleanConstructor \| NumberConstructor \| NullConstructor \| BinaryConstructor \| StringConstructor \| UnaryLookup` | |
| \[16\]   | `NonDeterministicFunctionCall` | ::= | `"non-deterministic" VarRef ArgumentList` |                 |
| \[17\]   | `MapConstructorEntry`          | ::= | `MapKeyExpr (":" \| ":=") MapValueExpr` |                   |
| \[18\]   | `Prolog`                       | ::= | `((DefaultNamespaceDecl \| Setter \| NamespaceDecl \| Import \| TypeDecl) Separator)* ((ContextItemDecl \| AnnotatedDecl \| OptionDecl) Separator)*` | |
| \[19\]   | `TypeDecl`                     | ::= | `"declare" "type" QName "=" ItemType` |                     |
| \[20\]   | `ItemType`                     | ::= | `KindTest \| AnyItemType \| AnnotatedFunctionOrSequence \| MapTest \| ArrayTest \| UnionType \| AtomicOrUnionType \| ParenthesizedSequenceType` | |
| \[21\]   | `TypedMapTest`                 | ::= | `"map" "(" (UnionType \| AtomicOrUnionType) "," SequenceType ")"` | |
| \[22\]   | `UnionType`                    | ::= | `"union" "(" EQName ("," EQName)* ")"` |                      |
| \[23\]   | `TupleType`                    | ::= | `"tuple" "(" TupleField ("," TupleField)* ("," "*")? ")"` | |
| \[24\]   | `TupleField`                   | ::= | `NCName "?" (":" SequenceType)?`    |                       |
| \[25\]   | `ForwardAxis`                  | ::= | `("child" "::") \| ("descendant" "::") \| ("attribute" "::") \| ("self" "::") \| ("descendant-or-self" "::") \| ("following-sibling" "::") \| ("following" "::") \| ("namespace" "::") \| ("property" "::")` | |
| \[26\]   | `CompatibilityAnnotation`      | ::= | `"assignable" \| "private" \| "sequential" \| "simple" \| "unassignable" \| "updating"` | |
| \[27\]   | `ValidateExpr`                 | ::= | `"validate" ( ValidationMode \| ( ( "type" \| "as" ) TypeName ) )? "{" Expr "}"` | |
| \[28\]   | `KindTest`                     | ::= | `DocumentTest \| ElementTest \| AttributeTest \| SchemaElementTest \| SchemaAttributeTest \| PITest \| CommentTest \| TextTest \| NamespaceNodeTest \| AnyKindTest \| NamedKindTest \| BinaryTest \| SchemaKindTest \| JsonKindTest` | |
| \[29\]   | `BinaryTest`                   | ::= | `"binary" "(" ")"`                  |                       |
| \[30\]   | `BinaryConstructor`            | ::= | `"binary" EnclosedExpr`             |                       |
| \[31\]   | `CatchClause`                  | ::= | `"catch" (CatchErrorList \| ("(" "$" VarName ")")) EnclosedExpr` | |
| \[32\]   | `Import`                       | ::= | `SchemaImport \| ModuleImport \| StylesheetIport` |         |
| \[33\]   | `StylesheetImport`             | ::= | `"import" "stylesheet" "at" URILiteral`   |                 |
| \[34\]   | `Module`                       | ::= | `VersionDecl? (LibraryModule \| (MainModule (TransactionSeparator VersionDecl? MainModule)* ))` | |
| \[35\]   | `TransactionSeparator`         | ::= | `";"`                                     |                 |
| \[36\]   | `SchemaKindTest`               | ::= | `AttributeDeclTest \| ComplexTypeTest \| ElementDeclTest \| SchemaComponentTest \| SchemaParticleTest \| SchemaRootTest \| SchemaTypeTest \| SimpleTypeTest \| SchemaFacetTest` | |
| \[37\]   | `AttributeDeclTest`            | ::= | `"attribute-decl" "(" ")"`                |                 |
| \[38\]   | `ComplexTypeTest`              | ::= | `"complex-type" "(" ")"`                  |                 |
| \[39\]   | `ElementDeclTest`              | ::= | `"element-decl" "(" ")"`                  |                 |
| \[40\]   | `SchemaComponentTest`          | ::= | `"schema-component" "(" ")"`              |                 |
| \[41\]   | `SchemaParticleTest`           | ::= | `"schema-particle" "(" ")"`               |                 |
| \[42\]   | `SchemaRootTest`               | ::= | `"schema-root" "(" ")"`                   |                 |
| \[43\]   | `SchemaTypeTest`               | ::= | `"schema-type" "(" ")"`                   |                 |
| \[44\]   | `SimpleTypeTest`               | ::= | `"simple-type" "(" ")"`                   |                 |
| \[45\]   | `SchemaFacetTest`              | ::= | `"schema-facet" "(" ")"`                  |                 |
| \[46\]   | `JsonKindTest`                 | ::= | `BooleanNodeTest \| NumberNodeTest \| NullNodeTest \| ArrayNodeTest \| MapNodeTest` | |
| \[47\]   | `BooleanNodeTest`              | ::= | `AnyBooleanNodeTest \| NamedBooleanNodeTest` |              |
| \[48\]   | `AnyBooleanNodeTest`           | ::= | `"boolean-node" "(" ")"`                  |                 |
| \[49\]   | `NamedBooleanNodeTest`         | ::= | `"boolean-node" "(" StringLiteral ")"`    |                 |
| \[50\]   | `BooleanConstructor`           | ::= | `"boolean-node" "{" Expr "}"`             |                 |
| \[51\]   | `NumberNodeTest`               | ::= | `AnyNumberNodeTest \| NamedNumberNodeTest` |                |
| \[52\]   | `AnyNumberNodeTest`            | ::= | `"number-node" "(" ")"`                   |                 |
| \[53\]   | `NamedNumberNodeTest`          | ::= | `"number-node" "(" StringLiteral ")"`     |                 |
| \[54\]   | `NumberConstructor`            | ::= | `"number-node" "{" Expr "}"`              |                 |
| \[55\]   | `NullNodeTest`                 | ::= | `AnyNullNodeTest \| NamedNullNodeTest`    |                 |
| \[56\]   | `AnyNullNodeTest`              | ::= | `"null-node" "(" ")"`                     |                 |
| \[57\]   | `NamedNullNodeTest`            | ::= | `"null-node" "(" StringLiteral ")"`       |                 |
| \[58\]   | `NullConstructor`              | ::= | `"null-node" "{" "}"`                     |                 |
| \[59\]   | `ArrayNodeTest`                | ::= | `AnyArrayNodeTest \| NamedArrayNodeTest`  |                 |
| \[60\]   | `AnyArrayNodeTest`             | ::= | `"array-node" "(" ")"`                    |                 |
| \[61\]   | `NamedArrayNodeTest`           | ::= | `"array-node" "(" StringLiteral ")"`      |                 |
| \[62\]   | `CurlyArrayConstructor`        | ::= | `("array" \| "array-node") EnclosedExpr`  |                 |
| \[63\]   | `MapNodeTest`                  | ::= | `AnyMapNodeTest \| NamedMapNodeTest`      |                 |
| \[64\]   | `AnyMapNodeTest`               | ::= | `"object-node" "(" ")"`                   |                 |
| \[65\]   | `NamedMapNodeTest`             | ::= | `"object-node" "(" StringLiteral ")"`     |                 |
| \[66\]   | `MapConstructor`               | ::= | `("map" \| "object-node") "{" (MapConstructorEntry ("," MapConstructorEntry)*)? "}"` | |
| \[67\]   | `AnyKindTest`                  | ::= | `"node" "(" ("*")? ")"`                   |                 |
| \[68\]   | `NamedKindTest`                | ::= | `"node" "(" StringLiteral ")"`            |                 |
| \[69\]   | `TextTest`                     | ::= | `AnyTextTest \| NamedTextTest`            |                 |
| \[70\]   | `AnyTextTest`                  | ::= | `"text" "(" ")"`                          |                 |
| \[71\]   | `NamedTextTest`                | ::= | `"text" "(" StringLiteral ")"`            |                 |
| \[72\]   | `DocumentTest`                 | ::= | `"document-node" "(" (ElementTest \| SchemaElementTest \| AnyArrayNodeTest \| AnyMapNodeTest)? ")"` | |
| \[73\]   | `Expr`                         | ::= | `ApplyExpr`                               |                 |
| \[74\]   | `ApplyExpr`                    | ::= | `ConcatExpr (";" (ConcatExpr ";")*)?`     |                 |
| \[75\]   | `ConcatExpr`                   | ::= | `ExprSingle ("," ExprSingle)*`            |                 |
| \[76\]   | `Wildcard`                     | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[77\]   | `WildcardIndicator`            | ::= | `"*"`                                     |                 |
| \[78\]   | `SequenceType`                 | ::= | `(("empty-sequence" \| "empty") "(" ")") \| (ItemType OccurrenceIndicator?)` | |
| \[79\]   | `OrExpr`                       | ::= | `AndExpr (("or" \| "orElse") AndExpr)*`   |                 |
| \[80\]   | `FunctionItemExpr`             | ::= | `NamedFunctionRef \| InlineFunctionExpr \| SimpleInlineFunctionExpr` | | 
| \[81\]   | `SimpleInlineFunctionExpr`     | ::= | `"fn" "{" Expr "}"`                       |                 |
| \[82\]   | `PredefinedEntityRef`          | ::= | `EntityRef`                               |                 |
| \[83\]   | `EntityRef`                    | ::= | \[[https://www.w3.org/TR/xml/#NT-EntityRef]()\] |           |
| \[84\]   | `Name`                         | ::= | \[[https://www.w3.org/TR/xml/#NT-Name]()\] |                |
| \[85\]   | `ParenthesizedSequenceType`    | ::= | `ParenthesizedItemType \| ItemTypeUnion \| TupleSequenceType` | |
| \[86\]   | `ItemTypeUnion`                | ::= | `"(" ItemType ("\|" ItemType)* ")"`       |                 |
| \[87\]   | `TupleSequenceType`            | ::= | `"(" ItemType ("," ItemType)* ")"`  |                       |
| \[88\]   | `AnyItemType`                  | ::= | `"item" "(" ")"`                    |                       |
| \[89\]   | `AnnotatedFunctionOrSequence`  | ::= | `AnnotatedSequenceType \| FunctionTest` |                   |
| \[90\]   | `AnnotatedSequenceType`        | ::= | `Annotation Annotation* "for" SequenceType` |               |
| \[91\]   | `ExprSingle`                   | ::= | `FLWORExpr \| QuantifiedExpr \| SwitchExpr \| TypeswitchExpr \| IfExpr \| TryCatchExpr \| TernaryIfExpr` | | 
| \[92\]   | `TernaryIfExpr`                | ::= | `ElvisExpr "??" ElvisExpr "!!" ElvisExpr` |                 |
| \[93\]   | `ElvisExpr`                    | ::= | `OrExpr "?!" OrExpr`                |                       |
| \[94\]   | `IfExpr`                       | ::= | `"if" "(" Expr ")" "then" ExprSingle ("else" ExprSingle)?` | |

### A.3 Reserved Function Names

| keyword                  | XQuery                          |
|--------------------------|---------------------------------|
| `array-node`             | MarkLogic 8.0                   |
| `attribute`              | XQuery 1.0                      |
| `attribute-decl`         | MarkLogic 7.0                   |
| `binary`                 | MarkLogic 6.0                   |
| `boolean-node`           | MarkLogic 8.0                   |
| `comment`                | XQuery 1.0                      |
| `complex-type`           | MarkLogic 7.0                   |
| `document-node`          | XQuery 1.0                      |
| `element`                | XQuery 1.0                      |
| `element-decl`           | MarkLogic 7.0                   |
| `empty-sequence`         | XQuery 1.0                      |
| `function`               | XQuery 3.0                      |
| `if`                     | XQuery 1.0                      |
| `item`                   | XQuery 1.0                      |
| `namespace-node`         | XQuery 3.0                      |
| `node`                   | XQuery 1.0                      |
| `null-node`              | MarkLogic 8.0                   |
| `number-node`            | MarkLogic 8.0                   |
| `object-node`            | MarkLogic 8.0                   |
| `processing-instruction` | XQuery 1.0                      |
| `schema-attribute`       | XQuery 1.0                      |
| `schema-component`       | MarkLogic 7.0                   |
| `schema-element`         | XQuery 1.0                      |
| `schema-facet`           | MarkLogic 8.0                   |
| `schema-particle`        | MarkLogic 7.0                   |
| `schema-root`            | MarkLogic 7.0                   |
| `schema-type`            | MarkLogic 7.0                   |
| `simple-type`            | MarkLogic 7.0                   |
| `switch`                 | XQuery 3.0                      |
| `text`                   | XQuery 1.0                      |
| `typeswitch`             | XQuery 1.0                      |
| `while`                  | XQuery Scripting Extension 1.0  |

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

__Working Drafts__
*  W3C. *XML Path Language (XPath) 2.0*. W3C Working Draft 02 May 2003.
   See [https://www.w3.org/TR/2003/WD-xpath20-20030502/]().
*  W3C. *XQuery 1.0: An XML Query Language*. W3C Working Draft 02 May 2003.
   See [https://www.w3.org/TR/2003/WD-xquery-20030502]().

### B.2 BaseX References
*  BaseX. *XQuery Extensions: Non-determinism*. See
   [http://docs.basex.org/wiki/XQuery_Extensions#Non-determinism](). 
*  BaseX. *Full-Text: Fuzzy Querying*. See
   [http://docs.basex.org/wiki/Full-Text#Fuzzy_Querying]().
*  BaseX. *Updates: update*. See [http://docs.basex.org/wiki/Updates#update]().

### B.3 MarkLogic References
*  MarkLogic. *MarkLogic Server Enhanced XQuery Language*. See
   [https://docs.marklogic.com/guide/xquery/enhanced]().
*  MarkLogic. *Schema Components*. See [https://docs.marklogic.com/sc]().
*  MarkLogic. *Working With JSON (Application Developer's Guide)*. See
   [https://docs.marklogic.com/guide/app-dev/json]().

### B.4 Saxon References
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

## C Vendor Extensions

### C.1 BaseX Vendor Extensions
The BaseX XQuery Processor supports the following vendor extensions described
in this document:
1.  [Cast Expressions](#332-cast) -- Combining XQuery 3.1 and XQuery Update Facility.
1.  [Full Text Fuzzy Option](#3611-fuzzy-option)
1.  [Non-Deterministic Function Calls](#371-non-deterministic-function-calls) \[BaseX 8.4\]
1.  [Update Expressions](#35-update-expressions) \[BaseX 7.8\]

BaseX implements the following [EXPath Syntax Extensions](https://github.com/expath/xpath-ng):
1.  [Elvis](#314-conditional-expressions) expressions \[BaseX 9.1\]
1.  [Ternary If](#314-conditional-expressions) expressions \[BaseX 9.1\]
1.  [If Without Else](#314-conditional-expressions) expressions \[BaseX 9.1\]

### C.2 MarkLogic Vendor Extensions
The MarkLogic XQuery Processor supports the following vendor extensions described
in this document:
1.  [Annotations](#42-annotations) -- `private` compatibility annotation
1.  [Binary Test](#2123-binary-test) and [Binary Constructors](#312-binary-constructors)
1.  [Forward Axes](#391-axes) -- `namespace` and `property` forward axes
1.  [Schema Kind Tests](#2124-schema-kind-tests) \[MarkLogic 7.0\] -- schema components type system
1.  [Stylesheet Import](#43-stylesheet-import)
1.  [Transactions](#44-transactions)
1.  [Predefined Entity References](#373-literals) -- HTML4 and HTML5 predefined entities

MarkLogic also supports the following syntax for XQuery 3.0 constructs:
1.  [Try/Catch Expressions](#311-trycatch-expressions)
1.  [Validate Expressions](#310-validate-expressions)

MarkLogic 8.0 supports the following JSON syntax extensions:
1.  [Array Node Test](#2128-array-node-test) and [Array Constructors](#382-arrays)
1.  [Boolean Node Test](#2125-boolean-node-test) and [Boolean Constructors](#383-booleans)
1.  [Document Tests](#211-sequencetype-syntax)
1.  [Map Node Test](#2129-map-node-test) and [Map Constructors](#381-maps)
1.  [Named Kind Tests](#211-sequencetype-syntax) \[MarkLogic 8.0\]
1.  [Null Node Test](#2127-null-node-test) and [Null Constructors](#385-nulls)
1.  [Number Node Test](#2126-number-node-test) and [Number Constructors](#384-numbers)
1.  [Text Tests](#211-sequencetype-syntax)

### C.3 Saxon Vendor Extensions
The Saxon XQuery Processor supports the following vendor extensions described
in this document:
1.  [Tuple Type](#2122-tuple-type) \[Saxon 9.8\]
1.  [Type Declaration](#41-type-declaration) \[Saxon 9.8\]
1.  [Logical Expressions](#313-logical-expressions) \[Saxon 9.9\] -- `orElse` and `andAlso`

Saxon implements the following [EXPath Syntax Extensions](https://github.com/expath/xpath-ng):
1.  [Union Type](#2121-union-type) \[Saxon 9.8\]
1.  [Simple Inline Function Expressions](#372-simple-inline-function-expressions) \[Saxon 9.8\]

Older versions of Saxon support the following working draft syntax:
1.  [Maps](#381-maps) \[Saxon 9.4\] -- `map` support using `:=` to separate keys and values

### C.4 IntelliJ Plugin Extensions
The following constructs have had their grammar modified to make it easier to
implement features such as variable lookup. These changes do not modify the
behaviour of those constructs:
1.  [Node Constructors](#31-node-constructors)
1.  [Quantified Expressions](#32-quantified-expressions)
1.  [Typeswitch](#331-typeswitch)
1.  [Block Expressions](#34-block-expressions)
1.  [Node Tests](#392-node-tests)
1.  [Any Item Type](#21211-item-type)

The XQuery IntelliJ Plugin supports the following vendor extensions described
in this document:
1.  [Cast Expressions](#332-cast) -- Combining XQuery 3.1 and XQuery Update Facility 3.0.
1.  [Item Type Union](#212101-item-type-union)
1.  [Tuple Sequence Types](#212102-tuple-sequence-types)
1.  [Annotated Sequence Types](#21211-annotated-function-tests-and-sequence-types)

### C.5 eXist-db Extensions
Older versions of eXist-db support the following working draft syntax:
1.  [Empty Sequences](#211-sequencetype-syntax) -- `empty-sequence()` in 4.0 and
    later; `empty()` in older versions.

### C.6 XPath 2.0 Working Draft 02 May 2003
The XPath 2.0 Working Draft 02 May 2003 specification supports the following
differences to the XPath 2.0 Recommendation, described in this document:
1.  [Empty Sequences](#211-sequencetype-syntax) -- `empty()`

### C.7 XQuery 1.0 Working Draft 02 May 2003
The XQuery 1.0 Working Draft 02 May 2003 specification supports the following
differences to the XQuery 1.0 Recommendation, described in this document:
1.  [Empty Sequences](#211-sequencetype-syntax) -- `empty()`

### C.8 EXPath Syntax Extensions
The EXPath group have a collection of proposed
[EXPath Syntax Extensions](https://github.com/expath/xpath-ng) for XPath and
XQuery. The following proposals are supported by this plugin:
1.  [Ternary If](#314-conditional-expressions) and [Elvis](#314-conditional-expressions)
    expressions \[[Proposal 2](https://github.com/expath/xpath-ng/pull/2)\]
1.  [Simple Inline Function Expressions](#372-simple-inline-function-expressions)
    \[[Proposal 5](https://github.com/expath/xpath-ng/pull/5)\] -- focus functions
1.  [Union Type](#2121-union-type)
    \[[Proposal 6](https://github.com/expath/xpath-ng/pull/6)\]
1.  [If Without Else](#314-conditional-expressions) expressions
    \[[Proposal 7](https://github.com/expath/xpath-ng/pull/7)\]

## D Error and Warning Conditions

### D.1 Vendor-Specific Behaviour

__ijw:IJVS0001__
> It is a *static warning* if the query contains any constructs that are not
> supported by the XQuery processor.

__ije:IJVS0002__
> It is a *static error* if an unprefixed function name contains a reserved
> function name for constructs supported by the XQuery processor.
>
> See [A.3 Reserved Function Names](#a3-reserved-function-names).

__ije:IJVS0003__
> It is a *static error* if a PredefinedEntityRef name is not supported by
> the XQuery processor.
>
> Standard conforming processors only support XML entity names. MarkLogic
> allows HTML4 and HTML5 entity names in addition to the XML entity names.

__ije:IJVS0004__
> It is a *static error* if `:=` is used to separate map keys and values on
> an XQuery 3.1 conforming XQuery processor.
>
> The `:=` separator is used by older versions of the Saxon XQuery processor.

__ije:IJVS0005__
> It is a *static error* if a multi-statement query body contains a semicolon
> at the end of the last statement when targeting the MarkLogic XQuery
> processor, or is missing a semicolon at the end of the last statement when
> targeting an XQuery processor supporting Scripting Extension 1.0.
