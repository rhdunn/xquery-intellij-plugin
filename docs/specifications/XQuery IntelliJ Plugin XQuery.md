---
layout: page
title: XQuery IntelliJ Plugin 1.9 XQuery
---

This document includes material copied from or derived from the XQuery
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XQuery and associated W3C extensions. The
plugin-specific extensions are provided to support IntelliJ integration.

## Table of Contents

{: .toc.toc-numbered.toc-numbered-5 }
- [Introduction](#1-introduction)
- [Basics](#2-basics)
  - [Types](#21-types)
    - [SequenceType Syntax](#211-sequencetype-syntax)
    - [SequenceType Matching](#212-sequencetype-matching)
      - [Local Union Types](#2121-local-union-types)
      - [Tuple Type](#2122-tuple-type)
      - [Binary Test](#2123-binary-test)
      - [Schema Kind Tests](#2124-schema-kind-tests)
      - [JSON Node Tests](#2125-json-node-test)
        - [Boolean Node Test](#21251-boolean-node-test)
        - [Number Node Test](#21252-number-node-test)
        - [Null Node Test](#21253-null-node-test)
        - [Array Node Test](#21254-array-node-test)
        - [Map Node Test](#21255-map-node-test)
      - [Sequence Types](#2126-sequence-types)
        - [Union](#21261-union)
        - [List](#21262-list)
      - [Element Test](#2127-element-test)
      - [Attribute Test](#2128-attribute-test)
      - [Type Alias](#2129-type-alias)
- [Expressions](#3-expressions)
  - [Expressions on SequenceTypes](#31-expressions-on-sequencetypes)
    - [Cast](#311-cast)
  - [Update Expressions](#32-update-expressions)
  - [Full Text Selections](#33-full-text-selections)
    - [Match Options](#331-match-options)
      - [Fuzzy Option](#3311-fuzzy-option)
  - [Primary Expressions](#34-primary-expressions)
    - [Non-Deterministic Function Calls](#341-non-deterministic-function-calls)
    - [Inline Function Expressions](#342-inline-function-expressions)
      - [Context Item Function Expressions](#3421-context-item-function-expressions)
      - [Lambda Function Expressions](#3422-lambda-function-expressions)
    - [Literals](#343-literals)
  - [JSON Constructors](#35-json-constructors)
    - [Maps](#351-maps)
    - [Arrays](#352-arrays)
    - [Booleans](#353-booleans)
    - [Numbers](#354-numbers)
    - [Nulls](#355-nulls)
  - [Path Expressions](#36-path-expressions)
    - [Axes](#361-axes)
  - [Validate Expressions](#37-validate-expressions)
  - [Try/Catch Expressions](#38-trycatch-expressions)
  - [Binary Constructors](#39-binary-constructors)
  - [Logical Expressions](#310-logical-expressions)
  - [Conditional Expressions](#311-conditional-expressions)
    - [Otherwise Expressions](#3111-otherwise-expressions)
  - [Arrow Operator (=>)](#312-arrow-operator-)
  - [FLWOR Expressions](#313-flwor-expressions)
    - [For Member Clause](#3131-for-member-clause)
- [Modules and Prologs](#4-modules-and-prologs)
  - [Item Type Declaration](#41-item-type-declaration)
  - [Annotations](#42-annotations)
  - [Stylesheet Import](#43-stylesheet-import)
  - [Transactions](#44-transactions)
  - [Function Declaration](#45-function-declaration)
  - [Using Declaration](#46-using-declaration)
- {: .toc-letter } [XQuery IntelliJ Plugin Grammar](#a-xquery-intellij-plugin-grammar)
  - [EBNF for XQuery 3.1 with Vendor Extensions](#a1-ebnf-for-xquery-31-with-vendor-extensions)
  - [Reserved Function Names](#a2-reserved-function-names)
- {: .toc-letter } [References](#b-references)
  - [W3C References](#b1-w3c-references)
  - [BaseX References](#b2-basex-references)
  - [MarkLogic References](#b3-marklogic-references)
  - [Saxon References](#b4-saxon-references)
  - [EXPath References](#b5-expath-references)
- {: .toc-letter } [Vendor Extensions](#c-vendor-extensions)
  - [BaseX Vendor Extensions](#c1-basex-vendor-extensions)
  - [MarkLogic Vendor Extensions](#c2-marklogic-vendor-extensions)
  - [Saxon Vendor Extensions](#c3-saxon-vendor-extensions)
  - [eXist-db Extensions](#c4-exist-db-extensions)
  - [EXPath Syntax Extensions](#c5-expath-syntax-extensions)
- {: .toc-letter } [Error and Warning Conditions](#d-error-and-warning-conditions)
  - [Vendor-Specific Behaviour](#d1-vendor-specific-behaviour)

## 1 Introduction
This document defines the syntax and semantics for vendor and plugin specific
functionality that extends XQuery 3.1, XQuery and XPath Full Text 3.0, XQuery
Update Facility 3.0, and XQuery Scripting Extension 1.0. The syntax described
here is the syntax that is supported by the XQuery IntelliJ Plugin.

The plugin supports BaseX, eXist-db, MarkLogic, and Saxon vendor extensions.
These are listed in appendix [C Vendor Extensions](#c-vendor-extensions),
grouped by the XQuery vendor, with links to the relevant parts of this document
that describe the specific extensions.

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
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[20\]  | `ItemType`              | ::= | `KindTest \| AnyItemTest \| AnnotatedFunctionOrSequence \| MapTest \| ArrayTest \| RecordTest \| TypeAlias \| LocalUnionType \| EnumerationType \| AtomicOrUnionType \| ParenthesizedItemType` | |
| \[21\]  | `TypedMapTest`          | ::= | `"map" "(" ItemType "," SequenceType ")"` |   |
| \[22\]  | `LocalUnionType`        | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |  |
| \[28\]  | `KindTest`              | ::= | `DocumentTest \| ElementTest \| AttributeTest \| SchemaElementTest \| SchemaAttributeTest \| PITest \| CommentTest \| TextTest \| NamespaceNodeTest \| AnyKindTest \| NamedKindTest \| BinaryTest \| SchemaKindTest \| JsonKindTest` | |
| \[46\]  | `JsonKindTest`          | ::= | `BooleanNodeTest \| NumberNodeTest \| NullNodeTest \| ArrayNodeTest \| MapNodeTest` | |
| \[67\]  | `AnyKindTest`           | ::= | `"node" "(" ("*")? ")"`             |         |
| \[68\]  | `NamedKindTest`         | ::= | `"node" "(" StringLiteral ")"`      |         |
| \[69\]  | `TextTest`              | ::= | `AnyTextTest \| NamedTextTest`      |         |
| \[70\]  | `AnyTextTest`           | ::= | `"text" "(" ")"`                    |         |
| \[71\]  | `NamedTextTest`         | ::= | `"text" "(" StringLiteral ")"`      |         |
| \[72\]  | `DocumentTest`          | ::= | `"document-node" "(" (ElementTest \| SchemaElementTest \| AnyArrayNodeTest \| AnyMapNodeTest)? ")"` | |
| \[87\]  | `SequenceTypeList`      | ::= | `SequenceType ("," SequenceType)*`  |         |
| \[88\]  | `AnyItemTest`           | ::= | `"item" "(" ")"`                    |         |
| \[96\]  | `NillableTypeName`      | ::= | `TypeName "?"`                      |         |
| \[97\]  | `ElementTest`           | ::= | `"element" "(" (NameTest ("," (NillableTypeName | TypeName)?)? ")"` | |
| \[99\]  | `TypedFunctionTest`     | ::= | `"function" "(" SequenceTypeList? ")" "as" SequenceType` | |
| \[100\] | `SingleType`            | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |    |

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

Using `SequenceTypeList` in `TypedFunctionTest` follows the grammar production
pattern of using `ParamList` in `FunctionCall`. This is done to make it easier
to differentiate the parameter types from the return type.

#### 2.1.2 SequenceType Matching

##### 2.1.2.1 Local Union Types

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options               |
|---------|-------------------------|-----|-------------------------------------|-----------------------|
| \[21\]  | `TypedMapTest`          | ::= | `"map" "(" ItemType "," SequenceType ")"` |                 |
| \[22\]  | `LocalUnionType`        | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |                |
| \[100\] | `SingleType`            | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |                  |

The `LocalUnionType` is a new XQuery 4.0 Editor's Draft item type supported by
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
> `xs:numeric` can be defined as:
>
>     declare type xs:numeric = union(xs:float, xs:double, xs:decimal);

##### 2.1.2.2 Tuple Type

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options               |
|---------|-------------------------|-----|-------------------------------------|-----------------------|
| \[23\]  | `RecordTest`            | ::= | `( "tuple" | "record" ) "(" FieldDeclaration ("," FieldDeclaration)* ExtensibleFlag? ")"` | |
| \[24\]  | `FieldDeclaration`      | ::= | `FieldName "?"? ( ( ":" | "as" ) (SequenceType | SelfReference) )?` | |
| \[115\] | `FieldName`             | ::= | `NCName | StringLiteral`            |                       |
| \[140\] | `ExtensibleFlag`        | ::= | `"," "*"`                           |                       |
| \[142\] | `SelfReference`         | ::= | `".." OccurrenceIndicator?`         |                       |

The `RecordTest` is a new sequence type supported by Saxon 9.8. Saxon 9.8 uses
`:` to specify the tuple item's sequence type, while Saxon 10.0 uses `as`.

In Saxon 9.8, a field name can only be an `NCName`. In Saxon 10, it can also
be a `StringLiteral`.

In Saxon 9.9, a `FieldDeclaration` can be optional by adding a `?` after the
field name.

In Saxon 9.8, field declarations are optional by default (that is, they have
a default  type of `item()*`). In Saxon 10.0, they are required by default
(that is,  they have a default type of `item()+`). To specify an optional field
in Saxon 10.0, the sequence type must be optional (i.e. using either the `?` or
`*` occurrence indicator for the specified sequence type).

\[Definition: An *extensible* tuple is a tuple that has some fields specified,
but allows other fields to be included in the map object.\] An *extensible*
tuple is specified by having the last tuple field be the `*` wildcard operator.
This is supported by Saxon 9.9.

##### 2.1.2.3 Binary Test

{: .ebnf-symbols }
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

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[36\]  | `SchemaKindTest`        | ::= | `AttributeDeclTest \| ComplexTypeTest \| ElementDeclTest \| SchemaComponentTest \| SchemaParticleTest \| SchemaRootTest \| SchemaTypeTest \| SimpleTypeTest \| SchemaFacetTest \| SchemaWildcardTest \| ModelGroupTest` | |
| \[37\]  | `AttributeDeclTest`     | ::= | `"attribute-decl" "(" AttribNameOrWildcard? ")"` | |
| \[38\]  | `ComplexTypeTest`       | ::= | `"complex-type" "(" TypeNameOrWildcard? ")"` | |
| \[39\]  | `ElementDeclTest`       | ::= | `"element-decl" "(" ElementNameOrWildcard? ")"` | |
| \[40\]  | `SchemaComponentTest`   | ::= | `"schema-component" "(" ")"`        |         |
| \[41\]  | `SchemaParticleTest`    | ::= | `"schema-particle" "(" ElementNameOrWildcard? ")"` | |
| \[42\]  | `SchemaRootTest`        | ::= | `"schema-root" "(" ")"`             |         |
| \[43\]  | `SchemaTypeTest`        | ::= | `"schema-type" "(" TypeNameOrWildcard? ")"` | |
| \[44\]  | `SimpleTypeTest`        | ::= | `"simple-type" "(" TypeNameOrWildcard? ")"` | |
| \[45\]  | `SchemaFacetTest`       | ::= | `"schema-facet" "(" ElementNameOrWildcard? ")"` | |
| \[102\] | `TypeNameOrWildcard`    | ::= | `TypeName | "*"`                    |         |
| \[103\] | `SchemaWildcardTest`    | ::= | `"schema-wildcard" "(" ")"`         |         |
| \[104\] | `ModelGroupTest`        | ::= | `"model-group" "(" ElementNameOrWildcard? ")"` | |

MarkLogic 7.0 provides `SchemaKindTest` types for working with XML Schema defined
types as part of its schema components built-in functions.

If the `TypeName` in `ComplexTypeTest` is not a complex type, or is not present
in the static context, an `XDMP-UNDCOMTYP` error is raised.

If the `TypeName` in `SchemaTypeTest` is not present in the static context, an
`XDMP-UNDTYP` error is raised.

If the `TypeName` in `SimpleTypeTest` is not a simple type (atomic, union, or
list type), or is not present in the static context, an `XDMP-UNDSIMTYP` error
is raised.

If the `ElementName` in `ModelGroupTest` is not a valid group type (e.g.
`xs:sequence`), an `XDMP-BADPARTICLETEST` error is raised.

##### 2.1.2.5 JSON Node Test

###### 2.1.2.5.1 Boolean Node Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[47\]  | `BooleanNodeTest`       | ::= | `AnyBooleanNodeTest \| NamedBooleanNodeTest` | |
| \[48\]  | `AnyBooleanNodeTest`    | ::= | `"boolean-node" "(" ")"`            |         |
| \[49\]  | `NamedBooleanNodeTest`  | ::= | `"boolean-node" "(" StringLiteral ")"` |      |

MarkLogic 8.0 provides `BooleanNodeTest` types for working with boolean (`true`
and `false`) JSON values. The `NamedBooleanNodeTest` variant selects JSON
boolean nodes in objects by the key name.

###### 2.1.2.5.2 Number Node Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[51\]  | `NumberNodeTest`        | ::= | `AnyNumberNodeTest \| NamedNumberNodeTest` |  |
| \[52\]  | `AnyNumberNodeTest`     | ::= | `"number-node" "(" ")"`             |         |
| \[53\]  | `NamedNumberNodeTest`   | ::= | `"number-node" "(" StringLiteral ")"` |       |

MarkLogic 8.0 provides `NumberNodeTest` types for working with numeric JSON
values. The `NamedNumberNodeTest` variant selects JSON number nodes in objects
by the key name.

###### 2.1.2.5.3 Null Node Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[55\]  | `NullNodeTest`          | ::= | `AnyNullNodeTest \| NamedNullNodeTest` |      |
| \[56\]  | `AnyNullNodeTest`       | ::= | `"null-node" "(" ")"`               |         |
| \[57\]  | `NamedNullNodeTest`     | ::= | `"null-node" "(" StringLiteral ")"` |         |

MarkLogic 8.0 provides `NullNodeTest` types for working with `null` JSON values.
The `NamedNullNodeTest` variant selects JSON null nodes in objects by the key name.

###### 2.1.2.5.4 Array Node Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[59\]  | `ArrayNodeTest`         | ::= | `AnyArrayNodeTest \| NamedArrayNodeTest` |     |
| \[60\]  | `AnyArrayNodeTest`      | ::= | `"array-node" "(" ")"`              |         |
| \[61\]  | `NamedArrayNodeTest`    | ::= | `"array-node" "(" StringLiteral ")"` |        |

MarkLogic 8.0 provides `ArrayNodeTest` types for working with JSON arrays. The
`NamedArrayNodeTest` variant selects JSON array nodes in objects by the key name.

###### 2.1.2.5.5 Map Node Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[63\]  | `MapNodeTest`           | ::= | `AnyMapNodeTest \| NamedMapNodeTest` |        |
| \[64\]  | `AnyMapNodeTest`        | ::= | `"object-node" "(" ")"`             |         |
| \[65\]  | `NamedMapNodeTest`      | ::= | `"object-node" "(" StringLiteral ")"` |       |

MarkLogic 8.0 provides `MapNodeTest` types for working with JSON objects. The
`NamedMapNodeTest` variant selects JSON object nodes in objects by the key name.

##### 2.1.2.6 Sequence Types

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[86\] | `SequenceTypeUnion`            | ::= | `SequenceTypeList ("\|" SequenceTypeList)*` |               |
| \[87\] | `SequenceTypeList`             | ::= | `SequenceType ("," SequenceType)*`  |                       |
| \[78\] | `SequenceType`                 | ::= | `EmptySequenceType \| (ItemType OccurrenceIndicator?) \| ParenthesizedSequenceType` | |
| \[85\] | `ParenthesizedSequenceType`    | ::= | `"(" SequenceTypeUnion ")"`         |                       |
| \[98\] | `EmptySequenceType`            | ::= | `("empty-sequence" \| "empty") "(" ")"` |                   |

###### 2.1.2.6.1 Union

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[86\] | `SequenceTypeUnion`            | ::= | `SequenceTypeList ("\|" SequenceTypeList)*` |               |

A `SequenceTypeUnion` that is used in the case clause of a `TypeswitchExpr`
and only contains sequence types is supported by XQuery 3.0.

The extended uses of `SquenceTypeUnion` is an XQuery IntelliJ Plugin extension
that is based on the XQuery Formal Semantics specification. This is used in the
definition of built-in functions for parameters and return types that can have
one of multiple disjoint types.

> __Example:__
>
>     declare function load-json($filename as xs:string) as (map(*) | array(*)) external;

###### 2.1.2.6.2 List

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                          | Options               |
|--------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[87\] | `SequenceTypeList`             | ::= | `SequenceType ("," SequenceType)*`  |                       |

The `SequenceTypeList` construct is an XQuery IntelliJ Plugin extension that is
based on the XQuery Formal Semantics specification. This is used in the
definition of built-in functions for parameters and return types that return
restricted sequence types.

A restricted sequence defines the type of each item in a sequence of a specified
length. This is useful for defining fixed-length sequence return types such as
rational or complex numbers.

> __Example:__
>
> `complex` can be defined as:
>
>     declare type complex = (xs:double, xs:double);

##### 2.1.2.7 Element Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[96\]  | `NillableTypeName`      | ::= | `TypeName "?"`                      |         |
| \[97\]  | `ElementTest`           | ::= | `"element" "(" (NameTest ("," (NillableTypeName | TypeName)?)? ")"` | |

This is a Saxon 10.0 extension. The element tests have been relaxed to support
all wildcard forms, not just `*`.

> __Example:__
>
>     $a instance of element(*:thead) and
>     $b instance of element(xhtml:*, xs:string)

##### 2.1.2.8 Attribute Test

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[111\] | `AttributeTest`         | ::= | `"attribute" "(" (NameTest ("," TypeName?)? ")"` | |

This is a Saxon 10.0 extension. The attribute tests have been relaxed to support
all wildcard forms, not just `*`.

> __Example:__
>
>     $a instance of attribute(xlink:*) and
>     $b instance of attribute(*:id, xs:ID)

##### 2.1.2.9 Type Alias

{: .ebnf-symbols }
| Ref     | Symbol                  |     | Expression                          | Options |
|---------|-------------------------|-----|-------------------------------------|---------|
| \[116\] | `TypeAlias`             | ::= | `( "~" EQName ) | ( "type" "(" EQName ")" )` | |

This is a Saxon 9.8 extension. This is used to reference XQuery
[type declarations](#41-type-declaration).

Saxon 9.8 uses the `~type` syntax, while Saxon 10.0 uses the `type(...)` syntax.

## 3 Expressions

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[91\] | `ExprSingle`            | ::= | `FLWORExpr \| QuantifiedExpr \| SwitchExpr \| TypeswitchExpr \| IfExpr \| TryCatchExpr \| TernaryConditionalExpr` | | 

### 3.1 Expressions on SequenceTypes

#### 3.1.1 Cast

{: .ebnf-symbols }
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

### 3.2 Update Expressions

{: .ebnf-symbols }
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

### 3.3 Full Text Selections

#### 3.3.1 Match Options

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[13\] | `FTMatchOption`         | ::= | `FTLanguageOption \| FTWildCardOption \| FTThesaurusOption \| FTStemOption \| FTCaseOption \| FTDiacriticsOption \| FTStopWordOption \| FTExtensionOption \| FTFuzzyOption` | |

The `FTFuzzyOption` is a new option that is supported by BaseX.

##### 3.3.1.1 Fuzzy Option

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options               |
|--------|-------------------------|-----|-------------------------------------|-----------------------|
| \[14\] | `FTFuzzyOption`         | ::= | `fuzzy`                             |                       |

\[Definition: A *fuzzy option* enables approximate text matching using the
Levenshtein distance algorithm.\]

This is a BaseX Full Text extension.

### 3.4 Primary Expressions

{: .ebnf-symbols }
| Ref    | Symbol                  |     | Expression                          | Options   |
|--------|-------------------------|-----|-------------------------------------|-----------|
| \[15\] | `PrimaryExpr`           | ::= | `Literal \| VarRef \| ParamRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| NonDeterministicFunctionCall \| OrderedExpr \| UnorderedExpr \| NodeConstructor \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| BooleanConstructor \| NumberConstructor \| NullConstructor \| BinaryConstructor \| StringConstructor \| UnaryLookup` | |
| \[80\] | `FunctionItemExpr`      | ::= | `NamedFunctionRef \| InlineFunctionExpr \| ContextItemFunctionExpr \| LambdaFunctionExpr` | |

#### 3.4.1 Non-Deterministic Function Calls

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                  | Options |
|---------|--------------------------------|-----|-----------------------------|---------|
| \[16\]  | `NonDeterministicFunctionCall` | ::= | `"non-deterministic" VarRef PositionalArgumentList` | |
| \[112\] | `PositionalArgumentList`       | ::= | `"(" PositionalArguments? ")"` |      |
| \[120\] | `PositionalArguments`          | ::= | `Argument ("," Argument)*`  |         |

\[Definition: A *non-deterministic* function call is a function that has side
effects.\] This is used to disable various query optimizations that would be
applied if the function call is deterministic.

This is a BaseX 8.4 extension to help the query compiler identify
non-deterministic function calls, where the non-deterministic property cannot
be determined statically.

#### 3.4.2 Inline Function Expressions

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

##### 3.4.2.1 Context Item Function Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[81\] | `ContextItemFunctionExpr`      | ::= | `(( "fn" "{" ) | ".{" ) Expr "}"`         |         |

This is a Saxon 9.8 extension. It is a syntax variant of the focus
function alternative for inline functions in proposal 5 of the EXPath
syntax extensions for XPath and XQuery.

The expressions `fn{E}` (from Saxon 9.8) and `.{E}` (from Saxon 10.0)
are equivalent to:
>     function ($arg as item()) as item()* { $arg ! (E) }

##### 3.4.2.2 Lambda Function Expressions

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[117\] | `LambdaFunctionExpr`           | ::= | `"_" "{" Expr "}"`                        |         |
| \[118\] | `ParamRef`                     | ::= | `"$" Digits`                              |         |

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

#### 3.4.3 Literals

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[82\] | `PredefinedEntityRef`          | ::= | `EntityRef`                               |         |
| \[83\] | `EntityRef`                    | ::= | \[[https://www.w3.org/TR/xml/#NT-EntityRef]()\] |   |
| \[84\] | `Name`                         | ::= | \[[https://www.w3.org/TR/xml/#NT-Name]()\] |        |

MarkLogic 6.0 supports HTML4 and HTML5 predefined entity references in addition
to XML entity references. Other XQuery processors only support XML entity
references (`&lt;`, `&gt;`, `&amp;`, `&quot;`, and `&apos;`). An `XPST0003`
static error is raised if the XQuery processor does not support the predefined
entity reference, which this plugin reports as an `ije:IJVS0003` static error.

### 3.5 JSON Constructors

#### 3.5.1 Maps

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[66\] | `MapConstructor`               | ::= | `("map" \| "object-node") "{" (MapConstructorEntry ("," MapConstructorEntry)*)? "}"` | |
| \[17\] | `MapConstructorEntry`          | ::= | `MapKeyExpr (":" \| ":=") MapValueExpr`   |         |

MarkLogic 8.0 uses the `object-node` keyword for defining JSON objects, and the
XQuery 3.1 syntax (`:`) for map entries.

Saxon versions 9.4 to 9.6 used `:=` to separate the key and value in a map entry.
From 9.7, the XQuery 3.1 syntax (`:`) is used.

#### 3.5.2 Arrays

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[62\] | `CurlyArrayConstructor`        | ::= | `("array" \| "array-node") EnclosedExpr`  |         |

MarkLogic 8.0 uses the `array-node` keyword for defining JSON arrays. It does
not support the square array style constructors.

#### 3.5.3 Booleans

{: .ebnf-symbols }
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

#### 3.5.4 Numbers

{: .ebnf-symbols }
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

#### 3.5.5 Nulls

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[58\] | `NullConstructor`              | ::= | `"null-node" "{" "}"`                     |         |

MarkLogic 8.0 provides `NullTest` types for working with `null` JSON values.

Null nodes are not removed from sequences, such as when used in arrays and maps.

### 3.6 Path Expressions

#### 3.6.1 Axes

{: .ebnf-symbols }
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

### 3.7 Validate Expressions

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[27\]  | `ValidateExpr`                 | ::= | `"validate" ( ValidationMode \| ( ( "type" \| "as" ) TypeName ) )? "{" Expr "}"` | |
| \[101\] | `ValidationMode`               | ::= | `"lax" | "strict" | "full"`               |         |

MarkLogic uses the `as` keyword instead of the XQuery 3.0 `type` keyword for
typed validation expressions.

MarkLogic supports the `full` validation mode.

### 3.8 Try/Catch Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[31\] | `CatchClause`                  | ::= | `"catch" (CatchErrorList \| ("(" "$" VarName ")")) EnclosedExpr` | |

MarkLogic only allows a single `CatchClause` for a given try/catch expression,
using the parenthesis style catch clause. It does not support the XQuery 3.0
style catch clauses using a `CatchErrorList`.

The variable name in the MarkLogic style catch clause has the type
`element(error:error)`, which contains the details of the error.

### 3.9 Binary Constructors

{: .ebnf-symbols }
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
1.  If the expression is an `xs:untypedAtomic`, `xs:string`, or a type derived
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

A binary node is not an instance of `xs:hexBinary`.

### 3.10 Logical Expressions

{: .ebnf-symbols }
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

### 3.11 Conditional Expressions

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[92\] | `TernaryConditionalExpr`       | ::= | `ElvisExpr "??" TernaryConditionalExpr "!!" TernaryConditionalExpr` | |
| \[94\] | `IfExpr`                       | ::= | `"if" "(" Expr ")" "then" ExprSingle ("else" ExprSingle)?` | |

The `IfExpr` without the else branch is supported by BaseX 9.1. It is defined
in proposal 7 of the EXPath syntax extensions for XPath and XQuery.

When the else branch of an `IfExpr` is not present, an empty sequence is
returned if the effective boolean value of the`IfExpr` condition evaluates
to false.

The `TernaryConditionalExpr` expression is a BaseX 9.1 extension defined in
proposal 2  of the EXPath syntax extensions for XPath and XQuery.

Given the `TernaryConditionalExpr`:

    C ?? A !! B

the equivalent `IfExpr` is:

    if (C) then A else B

#### 3.11.1 Otherwise Expressions

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[93\]  | `ElvisExpr`                    | ::= | `OrExpr "?!" OrExpr`                      |         |
| \[113\] | `MultiplicativeExpr`           | ::= | `OtherwiseExpr ( ("*" | "div" | "idiv" | "mod") OtherwiseExpr )*` | |
| \[114\] | `OtherwiseExpr`                | ::= | `UnionExpr ( "otherwise" UnionExpr )*`    |         |

The `ElvisExpr` expression is a BaseX 9.1 extension defined in proposal 2 of the
EXPath syntax extensions for XPath and XQuery.

The `OtherwiseExpr` expression is a new XPath 4.0 Editor's Draft expression
supported as a Saxon 10.0 vendor extension that returns the first non-empty
sequence in the otherwise expression.

For two items or empty sequences `A` and `B`, the expressions `A otherwise B`
and `A ?: B` are equivalent to:

    (A, B)[1]

Otherwise, if either `A` or `B` have more than one item, the expressions
`A otherwise B` and `A ?: B` are equivalent to:

    let $a := A
    return if (exists($a)) then $a else B

> __Note:__
>
> For sequences with more than one item `(A, B)[1]` will only return the first
> item in the non-empty sequence, not the entire sequence. This is why the more
> complicated expression is needed for that case.

### 3.12 Arrow Operator (=>)

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[109\] | `ArrowExpr`                    | ::= | `UnaryExpr ( FatArrowTarget | ThinArrowTarget )*` | |
| \[137\] | `FatArrowTarget`               | ::= | `"=>" ( ArrowFunctionCall | ArrowDynamicFunctionCall )` | |
| \[138\] | `ThinArrowTarget`              | ::= | `"->" ( ArrowFunctionCall | ArrowDynamicFunctionCall | ArrowInlineFunctionCall )` | |
| \[110\] | `ArrowFunctionCall`            | ::= | `EQName ArgumentList`                     |         |
| \[119\] | `ArrowDynamicFunctionCall`     | ::= | `( VarRef \| ParamRef \| ParenthesizedExpr ) PositionalArgumentList` | |
| \[139\] | `ArrowInlineFunctionCall`      | ::= | `EnclosedExpr`                            |         |
| \[112\] | `PositionalArgumentList`       | ::= | `"(" PositionalArguments? ")"`            |         |
| \[120\] | `PositionalArguments`          | ::= | `Argument ("," Argument)*`                |         |

The `ParamRef` is for [Lambda Function Expressions](#3422-lambda-function-expressions)
support in Saxon 10.0.

The `ThinArrowTarget` is a new XPath 4.0 Editor's Draft extension that applies
the function to each item in `UnaryExpr`.

### 3.13 FLWOR Expressions

#### 3.13.1 For Member Clause

{: .ebnf-symbols }
| Ref     | Symbol                        |     | Expression                                | Options |
|---------|-------------------------------|-----|-------------------------------------------|---------|
| \[121\] | `ForMemberBinding`            | ::= | `"$" VarName TypeDeclaration? PositionalVar? "in" ExprSingle` | |
| \[148\] | `ForMemberClause`             | ::= | `"for" "member" ForMemberBinding ("," ForMemberBinding)*` |  |
| \[149\] | `InitialClause`               | ::= | `ForClause | ForMemberClause | LetClause | WindowClause` | |

This is a new XPath 4.0 Editor's Draft extension to for expressions that is
supported as a Saxon 10.0 vendor extension. It makes it easier to iterate over
the  members in an `array()`.

Saxon 10.0 currently supports this in the initial clause only. An XPST0003 error
is raised if it is in an intermediate clause.

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

## 4 Modules and Prologs

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[18\] | `Prolog`                       | ::= | `((DefaultNamespaceDecl \| Setter \| NamespaceDecl \| Import \| UsingDecl) Separator)* ((ContextItemDecl \| AnnotatedDecl \| OptionDecl) Separator)*` | |

### 4.1 Item Type Declaration

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[141\] | `AnnotatedDecl`                | ::= | `"declare" Annotation* (VarDecl | FunctionDecl | ItemTypeDecl)` | |
| \[19\]  | `ItemTypeDecl`                 | ::= | `("type" EQName "=" | "item-type" EQName "as") ItemType` | |

This is a new XPath 4.0 Editor's Draft extension using the `item-type` keyword.
Saxon 9.8 supports it using the `type` keyword.

\[Definition: A *type declaration* declares a type alias name and associates
it with an item type, adding the (name, type) pair to the
[in-scope schema types](https://www.w3.org/TR/xquery-31/#dt-is-types).\] The
type declaration is in scope throughout the query in which it is declared.

If the type name in a type declaration has a namespace prefix, the namespace
prefix is resolved to a namespace URI using the
[statically known namespaces](https://www.w3.org/TR/xquery-31/#dt-static-namespaces)<sup><em>XQ31</em></sup>.
If the type name has no namespace prefix, it is implicitly qualified by the
[default element/type namespace](https://www.w3.org/TR/xquery-31/#dt-def-elemtype-ns)<sup><em>XQ31</em></sup>.

A type declaration is not resolvable as an atomic or union type. It needs to
be referenced as a [type alias](#2129-type-alias).

### 4.2 Annotations

{: .ebnf-symbols }
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

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[32\] | `Import`                       | ::= | `SchemaImport \| ModuleImport \| StylesheetImport` | |
| \[33\] | `StylesheetImport`             | ::= | `"import" "stylesheet" "at" URILiteral`   |         |

MarkLogic supports importing the functions and variables from an XLST stylesheet.

### 4.4 Transactions

{: .ebnf-symbols }
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

### 4.5 Function Declaration

{: .ebnf-symbols }
| Ref    | Symbol                         |     | Expression                                | Options |
|--------|--------------------------------|-----|-------------------------------------------|---------|
| \[95\] | `ParamList`                    | ::= | `ParamList ::= Param ("," Param)* "..."?` |         |

\[Definition: *Variadic function arguments* match zero or more arguments at the
end of the non-variadic arguments.\] Variadic function arguments are supported
in proposal 1, version 2 of the EXPath syntax extensions for XPath and XQuery.

When `...` is added after the last parameter in a parameter list, that parameter
contains the arguments passed after the previous parameter as an `array`. If the
variadic parameter has a type, the elements in that array have that type.

### 4.6 Using Declaration

{: .ebnf-symbols }
| Ref     | Symbol                         |     | Expression                                | Options |
|---------|--------------------------------|-----|-------------------------------------------|---------|
| \[105\] | `UsingDecl`                    | ::= | `"using" "namespace" URILiteral`          |         |

MarkLogic supports importing the functions and variables from an XQuery module
without setting the default element/type or function namespace.

## A XQuery IntelliJ Plugin Grammar

### A.1 EBNF for XQuery 3.1 with Vendor Extensions

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
1.  XQuery 4.0 Editor's Draft 12 December 2020 syntax;
1.  BaseX Vendor Extensions;
1.  MarkLogic Vendor Extensions;
1.  Saxon Vendor Extensions.

{: .ebnf-symbols }
| Ref      | Symbol                         |     | Expression                          | Options               |
|----------|--------------------------------|-----|-------------------------------------|-----------------------|
| \[1\]    | `DirAttributeList`             | ::= | `(S DirAttribute?)*`                | /\* ws: explicit \*/  |
| \[2\]    | `DirAttribute`                 | ::= | `QName S? "=" S? DirAttributeValue` | /\* ws: explicit \*/  |
| \[3\]    | `QuantifiedExpr`               | ::= | `("some" \| "every") QuantifierBinding ("," QuantifierBinding)* "satisfies" ExprSingle` | |
| \[4\]    | `QuantifierBinding`            | ::= | `"$" VarName TypeDeclaration? "in" ExprSingle` |            |
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
| \[15\]   | `PrimaryExpr`                  | ::= | `Literal \| VarRef \| ParamRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall \| NonDeterministicFunctionCall \| OrderedExpr \| UnorderedExpr \| NodeConstructor \| FunctionItemExpr \| MapConstructor \| ArrayConstructor \| BooleanConstructor \| NumberConstructor \| NullConstructor \| BinaryConstructor \| StringConstructor \| UnaryLookup` | |
| \[16\]   | `NonDeterministicFunctionCall` | ::= | `"non-deterministic" VarRef PositionalArgumentList` |       |
| \[17\]   | `MapConstructorEntry`          | ::= | `MapKeyExpr (":" \| ":=") MapValueExpr` |                   |
| \[18\]   | `Prolog`                       | ::= | `((DefaultNamespaceDecl \| Setter \| NamespaceDecl \| Import \| UsingDecl) Separator)* ((ContextItemDecl \| AnnotatedDecl \| OptionDecl) Separator)*` | |
| \[19\]   | `ItemTypeDecl`                 | ::= | `("type" EQName "=" | "item-type" EQName "as") ItemType` |  |
| \[20\]   | `ItemType`                     | ::= | `KindTest \| AnyItemTest \| AnnotatedFunctionOrSequence \| MapTest \| ArrayTest \| RecordTest \| TypeAlias \| LocalUnionType \| EnumerationType \| AtomicOrUnionType \| ParenthesizedItemType` | |
| \[21\]   | `TypedMapTest`                 | ::= | `"map" "(" ItemType "," SequenceType ")"` |                 |
| \[22\]   | `LocalUnionType`               | ::= | `"union" "(" ItemType ("," ItemType)* ")"` |                |
| \[23\]   | `RecordTest`                   | ::= | `( "tuple" | "record" ) "(" FieldDeclaration ("," FieldDeclaration)* ExtensibleFlag? ")"` | |
| \[24\]   | `FieldDelaration`              | ::= | `FieldName "?"? ( ( ":" | "as" ) (SequenceType | SelfReference) )?` | |
| \[25\]   | `ForwardAxis`                  | ::= | `("child" "::") \| ("descendant" "::") \| ("attribute" "::") \| ("self" "::") \| ("descendant-or-self" "::") \| ("following-sibling" "::") \| ("following" "::") \| ("namespace" "::") \| ("property" "::")` | |
| \[26\]   | `CompatibilityAnnotation`      | ::= | `"assignable" \| "private" \| "sequential" \| "simple" \| "unassignable" \| "updating"` | |
| \[27\]   | `ValidateExpr`                 | ::= | `"validate" ( ValidationMode \| ( ( "type" \| "as" ) TypeName ) )? "{" Expr "}"` | |
| \[28\]   | `KindTest`                     | ::= | `DocumentTest \| ElementTest \| AttributeTest \| SchemaElementTest \| SchemaAttributeTest \| PITest \| CommentTest \| TextTest \| NamespaceNodeTest \| AnyKindTest \| NamedKindTest \| BinaryTest \| SchemaKindTest \| JsonKindTest` | |
| \[29\]   | `BinaryTest`                   | ::= | `"binary" "(" ")"`                  |                       |
| \[30\]   | `BinaryConstructor`            | ::= | `"binary" EnclosedExpr`             |                       |
| \[31\]   | `CatchClause`                  | ::= | `"catch" (CatchErrorList \| ("(" "$" VarName ")")) EnclosedExpr` | |
| \[32\]   | `Import`                       | ::= | `SchemaImport \| ModuleImport \| StylesheetImport` |        |
| \[33\]   | `StylesheetImport`             | ::= | `"import" "stylesheet" "at" URILiteral` |                   |
| \[34\]   | `Module`                       | ::= | `VersionDecl? (LibraryModule \| (MainModule (TransactionSeparator VersionDecl? MainModule)* ))` | |
| \[35\]   | `TransactionSeparator`         | ::= | `";"`                               |                       |
| \[36\]   | `SchemaKindTest`               | ::= | `AttributeDeclTest \| ComplexTypeTest \| ElementDeclTest \| SchemaComponentTest \| SchemaParticleTest \| SchemaRootTest \| SchemaTypeTest \| SimpleTypeTest \| SchemaFacetTest \| SchemaWildcardTest \| ModelGroupTest` | |
| \[37\]   | `AttributeDeclTest`            | ::= | `"attribute-decl" "(" AttribNameOrWildcard? ")"` |          |
| \[38\]   | `ComplexTypeTest`              | ::= | `"complex-type" "(" TypeNameOrWildcard? ")"` |              |
| \[39\]   | `ElementDeclTest`              | ::= | `"element-decl" "(" ElementNameOrWildcard? ")"` |           |
| \[40\]   | `SchemaComponentTest`          | ::= | `"schema-component" "(" ")"`        |                       |
| \[41\]   | `SchemaParticleTest`           | ::= | `"schema-particle" "(" ElementNameOrWildcard? ")"` |        |
| \[42\]   | `SchemaRootTest`               | ::= | `"schema-root" "(" ")"`             |                       |
| \[43\]   | `SchemaTypeTest`               | ::= | `"schema-type" "(" TypeNameOrWildcard? ")"` |               |
| \[44\]   | `SimpleTypeTest`               | ::= | `"simple-type" "(" TypeNameOrWildcard? ")"` |               |
| \[45\]   | `SchemaFacetTest`              | ::= | `"schema-facet" "(" ElementNameOrWildcard? ")"` |           |
| \[46\]   | `JsonKindTest`                 | ::= | `BooleanNodeTest \| NumberNodeTest \| NullNodeTest \| ArrayNodeTest \| MapNodeTest` | |
| \[47\]   | `BooleanNodeTest`              | ::= | `AnyBooleanNodeTest \| NamedBooleanNodeTest` |              |
| \[48\]   | `AnyBooleanNodeTest`           | ::= | `"boolean-node" "(" ")"`            |                       |
| \[49\]   | `NamedBooleanNodeTest`         | ::= | `"boolean-node" "(" StringLiteral ")"` |                    |
| \[50\]   | `BooleanConstructor`           | ::= | `"boolean-node" "{" Expr "}"`       |                       |
| \[51\]   | `NumberNodeTest`               | ::= | `AnyNumberNodeTest \| NamedNumberNodeTest` |                |
| \[52\]   | `AnyNumberNodeTest`            | ::= | `"number-node" "(" ")"`             |                       |
| \[53\]   | `NamedNumberNodeTest`          | ::= | `"number-node" "(" StringLiteral ")"` |                     |
| \[54\]   | `NumberConstructor`            | ::= | `"number-node" "{" Expr "}"`        |                       |
| \[55\]   | `NullNodeTest`                 | ::= | `AnyNullNodeTest \| NamedNullNodeTest` |                    |
| \[56\]   | `AnyNullNodeTest`              | ::= | `"null-node" "(" ")"`               |                       |
| \[57\]   | `NamedNullNodeTest`            | ::= | `"null-node" "(" StringLiteral ")"` |                       |
| \[58\]   | `NullConstructor`              | ::= | `"null-node" "{" "}"`               |                       |
| \[59\]   | `ArrayNodeTest`                | ::= | `AnyArrayNodeTest \| NamedArrayNodeTest` |                  |
| \[60\]   | `AnyArrayNodeTest`             | ::= | `"array-node" "(" ")"`              |                       |
| \[61\]   | `NamedArrayNodeTest`           | ::= | `"array-node" "(" StringLiteral ")"` |                      |
| \[62\]   | `CurlyArrayConstructor`        | ::= | `("array" \| "array-node") EnclosedExpr` |                  |
| \[63\]   | `MapNodeTest`                  | ::= | `AnyMapNodeTest \| NamedMapNodeTest` |                      |
| \[64\]   | `AnyMapNodeTest`               | ::= | `"object-node" "(" ")"`             |                       |
| \[65\]   | `NamedMapNodeTest`             | ::= | `"object-node" "(" StringLiteral ")"` |                     |
| \[66\]   | `MapConstructor`               | ::= | `("map" \| "object-node") "{" (MapConstructorEntry ("," MapConstructorEntry)*)? "}"` | |
| \[67\]   | `AnyKindTest`                  | ::= | `"node" "(" ("*")? ")"`             |                       |
| \[68\]   | `NamedKindTest`                | ::= | `"node" "(" StringLiteral ")"`      |                       |
| \[69\]   | `TextTest`                     | ::= | `AnyTextTest \| NamedTextTest`      |                       |
| \[70\]   | `AnyTextTest`                  | ::= | `"text" "(" ")"`                    |                       |
| \[71\]   | `NamedTextTest`                | ::= | `"text" "(" StringLiteral ")"`      |                       |
| \[72\]   | `DocumentTest`                 | ::= | `"document-node" "(" (ElementTest \| SchemaElementTest \| AnyArrayNodeTest \| AnyMapNodeTest)? ")"` | |
| \[76\]   | `Wildcard`                     | ::= | `WildcardIndicator \| (NCName ":" WildcardIndicator) \| (WildcardIndicator ":" NCName) \| (BracedURILiteral WildcardIndicator)` | /\* ws: explicit \*/ |
| \[77\]   | `WildcardIndicator`            | ::= | `"*"`                               |                       |
| \[78\]   | `SequenceType`                 | ::= | `(("empty-sequence" \| "empty") "(" ")") \| (ItemType OccurrenceIndicator?) \| ParenthesizedSequenceType` | |
| \[79\]   | `OrExpr`                       | ::= | `AndExpr (("or" \| "orElse") AndExpr)*` |                   |
| \[80\]   | `FunctionItemExpr`             | ::= | `NamedFunctionRef \| InlineFunctionExpr \| ContextItemFunctionExpr \| LambdaFunctionExpr` | | 
| \[81\]   | `ContextItemFunctionExpr`      | ::= | `(( "fn" "{" ) | ".{" ) Expr "}"`   |                       |
| \[82\]   | `PredefinedEntityRef`          | ::= | `EntityRef`                         |                       |
| \[83\]   | `EntityRef`                    | ::= | \[[https://www.w3.org/TR/xml/#NT-EntityRef]()\] |           |
| \[84\]   | `Name`                         | ::= | \[[https://www.w3.org/TR/xml/#NT-Name]()\] |                |
| \[85\]   | `ParenthesizedSequenceType`    | ::= | `"(" SequenceTypeUnion ")"`         |                       |
| \[86\]   | `SequenceTypeUnion`            | ::= | `SequenceTypeList ("\|" SequenceTypeList)* ")"` |           |
| \[87\]   | `SequenceTypeList`             | ::= | `SequenceType ("," SequenceType)*`  |                       |
| \[88\]   | `AnyItemTest`                  | ::= | `"item" "(" ")"`                    |                       |
| \[91\]   | `ExprSingle`                   | ::= | `FLWORExpr \| QuantifiedExpr \| SwitchExpr \| TypeswitchExpr \| IfExpr \| TryCatchExpr \| TernaryConditionalExpr` | | 
| \[92\]   | `TernaryConditionalExpr`       | ::= | `ElvisExpr "??" TernaryConditionalExpr "!!" TernaryConditionalExpr` | |
| \[93\]   | `ElvisExpr`                    | ::= | `OrExpr "?!" OrExpr`                |                       |
| \[94\]   | `IfExpr`                       | ::= | `"if" "(" Expr ")" "then" ExprSingle ("else" ExprSingle)?` | |
| \[95\]   | `ParamList`                    | ::= | `ParamList ::= Param ("," Param)* "..."?` |                 |
| \[96\]   | `NillableTypeName`             | ::= | `TypeName "?"`                      |                       |
| \[97\]   | `ElementTest`                  | ::= | `"element" "(" (NameTest ("," (NillableTypeName | TypeName)?)? ")"` | |
| \[98\]   | `EmptySequenceType`            | ::= | `("empty-sequence" \| "empty") "(" ")"` |                   |
| \[99\]   | `TypedFunctionTest`            | ::= | `"function" "(" SequenceTypeList? ")" "as" SequenceType` |  |
| \[100\]  | `SingleType`                   | ::= | `(LocalUnionType | SimpleTypeName) "?"?` |                  |
| \[101\]  | `ValidationMode`               | ::= | `"lax" | "strict" | "full"`         |                       |
| \[102\]  | `TypeNameOrWildcard`           | ::= | `TypeName | "*"`                    |                       |
| \[103\]  | `SchemaWildcardTest`           | ::= | `"schema-wildcard" "(" ")"`         |                       |
| \[104\]  | `ModelGroupTest`               | ::= | `"model-group" "(" ElementNameOrWildcard? ")"` |            |
| \[105\]  | `UsingDecl`                    | ::= | `"using" "namespace" URILiteral`    |                       |
| \[106\]  | `SchemaImport`                 | ::= | `"import" "schema" SchemaPrefix? URILiteral LocationURIList?` | |
| \[107\]  | `LocationURIList`              | ::= | `"at" URILiteral ("," URILiteral)*` |                       |
| \[108\]  | `ModuleImport`                 | ::= | `"import" "module" ("namespace" NCName "=")? URILiteral LocationURIList?` | |
| \[109\]  | `ArrowExpr`                    | ::= | `UnaryExpr ( FatArrowTarget | ThinArrowTarget )*` |         |
| \[110\]  | `ArrowFunctionCall`            | ::= | `EQName ArgumentList`               |                       |
| \[111\]  | `AttributeTest`                | ::= | `"attribute" "(" (NameTest ("," TypeName?)? ")"` |          |
| \[112\]  | `PositionalArgumentList`       | ::= | `"(" PositionalArguments? ")"`      |                       |
| \[113\]  | `MultiplicativeExpr`           | ::= | `OtherwiseExpr ( ("*" | "div" | "idiv" | "mod") OtherwiseExpr )*` | |
| \[114\]  | `OtherwiseExpr`                | ::= | `UnionExpr ( "otherwise" UnionExpr )*` |                    |
| \[115\]  | `FieldName`                    | ::= | `NCName | StringLiteral`            |                       |
| \[116\]  | `TypeAlias`                    | ::= | `( "~" EQName ) | ( "type" "(" EQName ")" )` |              |
| \[117\]  | `LambdaFunctionExpr`           | ::= | `"_{" Expr "}"`                     |                       |
| \[118\]  | `ParamRef`                     | ::= | `"$" Digits`                        |                       |
| \[119\]  | `ArrowDynamicFunctionCall`     | ::= | `( VarRef \| ParamRef \| ParenthesizedExpr ) PositionalArgumentList` | |
| \[120\]  | `PositionalArguments`          | ::= | `Argument ("," Argument)*`          |                       |
| \[121\]  | `ForMemberBinding`             | ::= | `"$" VarName TypeDeclaration? PositionalVar? "in" ExprSingle` | |
| \[122\]  | `DirElemContent`               | ::= | `DirectConstructor \| EnclosedExpr \| DirTextConstructor` | |
| \[123\]  | `DirTextConstructor`           | ::= | `ElementContents \| PredefinedEntityRef \| CharRef \| "{{" \| "}}" \| CDataSection` | |
| \[124\]  | `PathExpr`                     | ::= | `("/" RelativePathExpr?) \| (AbbrevDescendantOrSelfStep RelativePathExpr) \| RelativePathExpr` | /\* xgc: leading-lone-slash \*/ |
| \[125\]  | `RelativePathExpr`             | ::= | `StepExpr (("/" \| AbbrevDescendantOrSelfStep) StepExpr)*` | |
| \[126\]  | `AbbrevDescendantOrSelfStep`   | ::= | `"//"`                              |                       |
| \[127\]  | `PostfixExpr`                  | ::= | `FilterExpr \| DynamicFunctionCall \| PostfixLookup \| PrimaryExpr` | |
| \[128\]  | `FilterExpr`                   | ::= | `PostfixExpr Predicate`             |                       |
| \[129\]  | `DynamicFunctionCall`          | ::= | `PostfixExpr ArgumentList`          |                       |
| \[130\]  | `PostfixLookup`                | ::= | `PostfixExpr Lookup`                |                       |
| \[131\]  | `AxisStep`                     | ::= | `FilterStep \| ReverseStep \| ForwardStep` |                |
| \[132\]  | `FilterStep`                   | ::= | `AxisStep Predicate`                |                       |
| \[133\]  | `ParenthesizedExpr`            | ::= | `EmptyExpr | ( "(" Expr ")" )`      |                       |
| \[134\]  | `EmptyExpr`                    | ::= | `"(" ")"`                           |                       |
| \[135\]  | `FunctionSignature`            | ::= | `"(" ParamList? ")" TypeDeclaration?` |                     |
| \[136\]  | `InlineFunctionExpr`           | ::= | `Annotation* (("function" FunctionSignature) | ("->" FunctionSignature?)) FunctionBody ` | |
| \[137\]  | `FatArrowTarget`               | ::= | `"=>" ( ArrowFunctionCall | ArrowDynamicFunctionCall )` |   |
| \[138\]  | `ThinArrowTarget`              | ::= | `"->" ( ArrowFunctionCall | ArrowDynamicFunctionCall | ArrowInlineFunctionCall )` |   |
| \[139\]  | `ArrowInlineFunctionCall`      | ::= | `EnclosedExpr`                      |                       |
| \[140\]  | `ExtensibleFlag`               | ::= | `"," "*"`                           |                       |
| \[141\]  | `AnnotatedDecl`                | ::= | `"declare" Annotation* (VarDecl | FunctionDecl | ItemTypeDecl)` | |
| \[142\]  | `SelfReference`                | ::= | `".." OccurrenceIndicator?`         |                       |
| \[143\]  | `KeySpecifier`                 | ::= | `NCName | IntegerLiteral | StringLiteral | VarRef | ParenthesizedExpr | "*"` | |
| \[144\]  | `EnumerationType`              | ::= | `"enum" "(" StringLiteral ("," StringLiteral)* ")"` |       |
| \[145\]  | `ArgumentList`                 | ::= | `"(" ((PositionalArguments ("," KeywordArguments)?) | KeywordArguments)? ")"` | | 	
| \[146\]  | `KeywordArguments`             | ::= | `KeywordArgument ("," KeywordArgument)*` |                  |
| \[147\]  | `KeywordArgument`              | ::= | `NCName ":" ExprSingle`             |                       |
| \[148\]  | `ForMemberClause`              | ::= | `"for" "member" ForMemberBinding ("," ForMemberBinding)*` | |
| \[149\]  | `InitialClause`                | ::= | `ForClause | ForMemberClause | LetClause | WindowClause` |  |
| \[150\]  | `WithExpr`                     | ::= | `"with" NamespaceDeclaration ("," NamespaceDeclaration)* EnclosedExpr` | |
| \[151\]  | `NamespaceDeclaration`         | ::= | `QName "=" URILiteral`              |                       |
| \[152\]  | `CopyModifyExpr`               | ::= | `"copy" CopyModifyExprBinding ("," CopyModifyExprBinding)* "modify" ExprSingle "return" ExprSingle` | |
| \[153\]  | `CopyModifyExprBinding`        | ::= | `"$" VarName ":=" ExprSingle`       |                       |
| \[156\]  | `ElementContents`              | ::= | `ElementContentChar*`               |                       |

### A.2 Reserved Function Names

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
| `model-group`            | MarkLogic 7.0                   |
| `namespace-node`         | XQuery 3.0                      |
| `node`                   | XQuery 1.0                      |
| `null-node`              | MarkLogic 8.0                   |
| `number-node`            | MarkLogic 8.0                   |
| `object-node`            | MarkLogic 8.0                   |
| `processing-instruction` | XQuery 1.0                      |
| `schema-attribute`       | XQuery 1.0                      |
| `schema-component`       | MarkLogic 7.0                   |
| `schema-element`         | XQuery 1.0                      |
| `schema-facet`           | MarkLogic 7.0                   |
| `schema-particle`        | MarkLogic 7.0                   |
| `schema-root`            | MarkLogic 7.0                   |
| `schema-type`            | MarkLogic 7.0                   |
| `schema-wildcard`        | MarkLogic 7.0                   |
| `simple-type`            | MarkLogic 7.0                   |
| `switch`                 | XQuery 3.0                      |
| `text`                   | XQuery 1.0                      |
| `typeswitch`             | XQuery 1.0                      |
| `while`                  | XQuery Scripting Extension 1.0  |

## B References

### B.1 W3C References
__Core Specifications__
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
*  W3C. *XQuery 1.0: An XML Query Language*. W3C Working Draft 02 May 2003.
   See [https://www.w3.org/TR/2003/WD-xquery-20030502]().
*  W3C. *XQuery 4.0: An XML Query Language*. W3C Editor's Draft 13 January 2021.
   See [https://qt4cg.org/branch/master/xquery-40/xquery-40.html]().

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

### B.5 EXPath References
__XPath NG__
*  EXPath. *Variadic Function Arguments*. Proposal 1, version 2. See
   [https://github.com/expath/xpath-ng/blob/0dded843cf1e7e21d357c9360bf5faf5b9e1e129/variadic-function-arguments.md]().
   Reece H. Dunn, 67 Bricks.
*  EXPath. *Conditional Expressions*. Proposal 2, version 1. See
   [https://github.com/expath/xpath-ng/blob/d2421975caacba75f0c9bd7fe017cc605e56b00f/conditional-expressions.md]().
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
*  EXPath. *Extended Element and Attribute Tests*. Proposal 13, version 1. See
   [https://github.com/expath/xpath-ng/blob/5b482550d164c8bf54e17b92f3e1d55e9f77bc6d/extended-element-attribute-tests.md]().
   Reece H. Dunn, 67 Bricks.

## C Vendor Extensions

### C.1 BaseX Vendor Extensions
The BaseX XQuery Processor supports the following vendor extensions described
in this document:
1.  [Cast Expressions](#311-cast) -- Combining XQuery 3.1 and XQuery Update Facility.
1.  [Full Text Fuzzy Option](#3311-fuzzy-option)
1.  [Non-Deterministic Function Calls](#341-non-deterministic-function-calls) \[BaseX 8.4\]
1.  [Update Expressions](#32-update-expressions) \[BaseX 7.8\]

BaseX implements the following [EXPath Syntax Extensions](https://github.com/expath/xpath-ng):
1.  [Elvis](#311-conditional-expressions) expressions \[BaseX 9.1\]
1.  [Ternary If](#311-conditional-expressions) expressions \[BaseX 9.1\]
1.  [If Without Else](#311-conditional-expressions) expressions \[BaseX 9.1\]

### C.2 MarkLogic Vendor Extensions
The MarkLogic XQuery Processor supports the following vendor extensions described
in this document:
1.  [Annotations](#42-annotations) -- `private` compatibility annotation
1.  [Binary Test](#2123-binary-test) and [Binary Constructors](#39-binary-constructors)
1.  [Forward Axes](#361-axes) -- `namespace` and `property` forward axes
1.  [Predefined Entity References](#343-literals) -- HTML4 and HTML5 predefined entities
1.  [Schema Kind Tests](#2124-schema-kind-tests) \[MarkLogic 7.0\] -- schema components type system
1.  [Stylesheet Import](#43-stylesheet-import)
1.  [Transactions](#44-transactions)
1.  [Using Declaration](#46-using-declaration)
1.  [Validate Expressions](#37-validate-expressions) -- full validation mode

MarkLogic also supports the following syntax for XQuery 3.0 constructs:
1.  [Try/Catch Expressions](#38-trycatch-expressions)
1.  [Validate Expressions](#37-validate-expressions) -- alternate syntax for typed validations

MarkLogic 8.0 supports the following JSON syntax extensions:
1.  [Array Node Test](#21254-array-node-test) and [Array Constructors](#352-arrays)
1.  [Boolean Node Test](#21251-boolean-node-test) and [Boolean Constructors](#353-booleans)
1.  [Document Tests](#211-sequencetype-syntax)
1.  [Map Node Test](#21255-map-node-test) and [Map Constructors](#351-maps)
1.  [Named Kind Tests](#211-sequencetype-syntax) \[MarkLogic 8.0\]
1.  [Null Node Test](#21253-null-node-test) and [Null Constructors](#355-nulls)
1.  [Number Node Test](#21252-number-node-test) and [Number Constructors](#354-numbers)
1.  [Text Tests](#211-sequencetype-syntax)

### C.3 Saxon Vendor Extensions
The Saxon XQuery Processor supports the following vendor extensions described
in this document:
1.  [Tuple Type](#2122-tuple-type) \[Saxon 9.8\]
1.  [Item Type Declaration](#41-item-type-declaration) and [Type Alias](#2129-type-alias) \[Saxon 9.8\]
1.  [Logical Expressions](#310-logical-expressions) \[Saxon 9.9\] -- `orElse` and `andAlso`
1.  [Otherwise Expressions](#3111-otherwise-expressions) \[Saxon 10.0\]
1.  [For Member Expressions](#3131-for-member-clause) \[Saxon 10.0\]

Saxon implements the following [EXPath Syntax Extensions](https://github.com/expath/xpath-ng):
1.  [Local Union Types](#2121-local-union-types) \[Saxon 9.8\]
1.  [Context Item Function Expressions](#3421-context-item-function-expressions) \[Saxon 9.8\]
1.  [Lambda Function Expressions](#3422-lambda-function-expressions) \[Saxon 10.0\]
1.  [Element Test](#2127-element-test) and [Attribute Test](#2128-attribute-test) \[Saxon 10.0\] -- wildcard names

Older versions of Saxon support the following working draft syntax:
1.  [Maps](#351-maps) \[Saxon 9.4\] -- `map` support using `:=` to separate keys and values

### C.4 eXist-db Extensions
Older versions of eXist-db support the following working draft syntax:
1.  [Empty Sequences](#211-sequencetype-syntax) -- `empty-sequence()` in 4.0 and
    later; `empty()` in older versions.

### C.5 EXPath Syntax Extensions
The EXPath group have a collection of proposed
[EXPath Syntax Extensions](https://github.com/expath/xpath-ng) for XPath and
XQuery. The following proposals are supported by this plugin:
1.  [Variadic Function Arguments](#45-function-declaration) \[Proposal 1, version 2\]
1.  [Ternary If](#311-conditional-expressions) and [Elvis](#311-conditional-expressions)
    expressions \[Proposal 2\]
1.  [Simple Inline Function Expressions](#342-inline-function-expressions)
    \[Proposal 5\] -- focus functions
1.  [Union Type](#2121-union-type) \[Proposal 6\]
1.  [If Without Else](#311-conditional-expressions) expressions \[Proposal 7\]

## D Error and Warning Conditions

### D.1 Vendor-Specific Behaviour

__ijw:IJVS0001__
> It is a *static warning* if the query contains any constructs that are not
> supported by the XQuery processor.

__ije:IJVS0002__
> It is a *static error* if an unprefixed function name contains a reserved
> function name for constructs supported by the XQuery processor.
>
> See [A.2 Reserved Function Names](#a2-reserved-function-names).

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
