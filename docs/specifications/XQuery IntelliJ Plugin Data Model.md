---
layout: page
title: XQuery IntelliJ Plugin 1.6 Data Model
---

This document includes material copied from or derived from the XPath and
XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

## Abstract
This document defines the data model for vendor and plugin specific
functionality that extends XQuery and associated W3C extensions. The
plugin data model extensions are to fill in gaps within the type
system and to provide static type analysis.

This document also documents the internals of how the plugin models and
processes XPath and XQuery constructs. These are used to implement
IntelliJ integration such as inlay parameters, and static analysis for
various inspections.

## Table of Contents

{: .toc }
- [1 Introduction](#1-introduction)
  - [1.1 PSI Tree and Data Model Construction](#11-psi-tree-and-data-model-construction)
- [2 Basics](#2-basics)
  - [2.1 Type System](#21-type-system)
    - [2.1.1 Part 1: Items](#211-part-1-items)
    - [2.1.2 Part 2: Simple and Complex Types](#212-part-2-simple-and-complex-types)
    - [2.1.3 Part 3: Atomic Types](#213-part-3-atomic-types)
    - [2.1.4 Part 4: Sequences](#214-part-4-sequences)
    - [2.1.5 Sequence Types](#215-sequence-types)
- [3 Type Manipulation](#3-type-manipulation)
  - [3.1 Upper and Lower Bounds](#31-upper-and-lower-bounds)
    - [3.1.1 Minimum of two bounds](#311-minimum-of-two-bounds)
    - [3.1.2 Maximum of two bounds](#312-maximum-of-two-bounds)
    - [3.1.3 Sum of two bounds](#313-sum-of-two-bounds)
  - [3.2 Aggregate Types](#32-aggregate-types)
    - [3.2.1 Item Type Union](#321-item-type-union)
    - [3.2.2 Sequence Type Union](#322-sequence-type-union)
    - [3.3.3 Sequence Type Addition](#333-sequence-type-addition)
- [4 Data Model](#4-data-model)
  - [4.1 Literals](#41-literals)
  - [4.2 EQNames and Wildcards](#42-eqnames-and-wildcards)
  - [4.3 Nodes](#43-nodes)
- [A References](#a-references)
  - [A.1 W3C References](#a1-w3c-references)
  - [A.2 XPath NG Proposals](#a2-xpath-ng-proposals)

## 1 Introduction
This document defines the data model for vendor and plugin specific functionality
that extends XQuery 3.1, XQuery and XPath Full Text 3.0, XQuery Update Facility
3.0, and XQuery Scripting Extension 1.0.

The plugin supports BaseX, eXist-db, MarkLogic, and Saxon vendor extensions. The
data model needed to support these extensions is detailed in this document.

The plugin data model extensions are to fill in gaps within the type system and
to provide static type analysis.

### 1.1 PSI Tree and Data Model Construction

An XQuery document is parsed according to the combined XQuery, Full Text,
Updating, Scripting, and Vendor syntax described in the
[XQuery IntelliJ Plugin 1.6 XQuery](XQuery%20IntelliJ%20Plugin%20XQuery.md)
document. The XQuery parser extends the XPath 2.0 parser to avoid duplicating
code between the two parsers.

An XPath 2.0 and later *expression*, *pattern*, and *sequence-type* are parsed
according to the combined XPath, Full Text, and Vendor syntax described in the
[XQuery IntelliJ Plugin 1.6 XPath](XQuery%20IntelliJ%20Plugin%20XPath.md)
document. The *pattern* syntax is validated using the conformance checking
intention used to check vendor specific, extension, or later version syntax
constructs.¹

An XPath 1.0 *expression*, *pattern*, and *sequence-type* is parsed according to
the [XPath 1.0 as 2.0 EBNF Grammar](XPath%201.0%20as%202.0%20EBNF%20Grammar.md)
document.¹ The extra-grammatical constraints and *pattern* syntax are validated
using the conformance checking intention used to check vendor specific, extension,
or later version syntax constructs.¹

The XPath expression or XQuery file is parsed into an AST tree called a PSI tree
by IntelliJ. These nodes are modelled on the XPath and XQuery symbols with a
concrete class and corresponding interface. The XQuery symbols that are also
XPath symbols are modelled as XPath PSI elements to avoid duplicating logic
between the XPath and XQuery PSI trees.

Symbols that are just a list of possible types, such as `ExprSingle` are only
defined as an interface, to avoid adding unnecessary nodes to the PSI tree.
If a symbol is only forwarding to another symbol, such as when a `RangeExpr`
does not have a `to` part, the corresponding PSI element is omitted from the
PSI tree to further simplify the model in memory.

The PSI tree elements implement the model described in this document. These
symbols provide the statically known information for that given context. This
is used for static analysis and IDE integration such as symbol navigation and
code completion.

1.  This functionality is not currently supported in the XQuery IntelliJ Plugin.
    This is a specification for how that functionality is intended to be
    implemented.

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
*  `xdm = http://reecedunn.co.uk/xquery-datamodel`

### 2.1 Type System

The names in square brackets in the type diagrams are the Java/Kotlin interfaces
in the `uk.co.reecedunn.intellij.plugin.xdm.model` package of `lang-xdm` that
are used to model the specified type.

#### 2.1.1 Part 1: Items

<pre><code>item() [XdmItem]
├─── node() [XdmNode]
│    ├─── attribute() [XdmAttributeNode]
│    │    └─── <em>user-defined attribute types</em>
│    ├─── document-node() [XdmDocumentNode]
│    │    └─── <em>document types with more precise content type</em>
│    ├─── element() [XdmElementNode]
│    │    └─── <em>user-defined element types</em>
│    ├─── comment() [XdmCommentNode]
│    ├─── namespace-node() [XdmNamespaceNode]
│    ├─── processing-instruction() [XdmProcessingInstructionNode]
│    ├─── text() [XdmTextNode]
│    ├─── array-node() [XdmArrayNode]
│    ├─── boolean-node() [XdmBooleanNode]
│    ├─── null-node() [XdmNullNode]
│    ├─── number-node() [XdmNumberNode]
│    └─── object-node() [XdmObjectNode]
├─── attribute-decl() [XdmAttributeDecl]
├─── binary() [XdmBinary]
├─── complex-type() [XdmComplexType]
├─── element-decl() [XdmElementDecl]
├─── model-group() [XdmModelGroup]
├─── schema-component() [XdmSchemaComponent]
├─── schema-facet() [XdmSchemaFacet]
├─── schema-particle() [XdmSchemaParticle]
├─── schema-root() [XdmSchemaRoot]
├─── schema-type() [XdmSchemaType]
├─── schema-wildcard() [XdmSchemaWildcard]
├─── simple-type() [XdmSimpleType]
├─── function(*) [XdmFunction]
│    ├─── map(*) [XdmMap]
│    └─── array(*) [XdmArray]
├─── annotation(*) [XdmAnnotation]
└─── xs:anyAtomicType [XsAnyAtomicType] ────────────────── See Part 3
</code></pre>

The `array-node()`, `boolean-node()`, `null-node()`, `number-node()`, and
`object-node()` types are MarkLogic JSON types.

The `attribute-decl()`, `complex-type()`, `element-decl()`, `model-group()`,
`schema-component()`, `schema-facet()`, `schema-particle()`, `schema-root()`,
`schema-type()`, `schema-wildcard()`, and `simple-type()` types are MarkLogic
schema types.

The `binary()` type is a MarkLogic item type.

The `annotation(*)` type is from the XPath NG annotation sequence types
proposal.

#### 2.1.2 Part 2: Simple and Complex Types

<pre><code>xs:anyType [XsAnyType]
├─── xdm:anyComplexType [XdmAnyComplexType]
│    ├─── xs:untyped
│    └─── <em>user-defined complex types</em>
└─── xs:anySimpleType [XsAnySimpleType]
     ├─── xs:anyAtomicType [XsAnyAtomicType] ───────────── See Part 3
     ├─── xdm:anyListType [XdmAnyListType]
     │    ├─── xs:IDREFS
     │    ├─── xs:NMTOKENS
     │    ├─── xs:ENTITIES
     │    └─── <em>user-defined list types</em>
     └─── xdm:anyUnionType [XdmAnyUnionType]
          ├─── xs:numeric
          ├─── xs:error¹
          └─── <em>user-defined union types</em>
</code></pre>

1.  `xs:error` is defined in XML Schema 1.1 Part 2, and in the Types section of
    the XPath 3.1 and XQuery 3.1 specifications, but not in XQuery and XPath
    3.1 Data Model. Support for this type is dependent on whether the
    implementation supports XML Schema 1.1.

The data model defines three additional simple and complex types:
`xdm:anyComplexType`, `xdm:anyListType`, and `xdm:anyUnionType`. These types
are defined in an XQuery IntelliJ Plugin specific namespace.

__xdm:anyComplexType__
> The datatype __xdm:anyComplexType__ is a complex type that includes all
> complex types (and no values that are not complex). Its base type is
> `xs:anyType` from which all schema types, including simple, and complex
> types are derived.

__xdm:anyListType__
> The datatype __xdm:anyListType__ is a list type that includes all
> list types (and no values that are not lists). Its base type is
> `xs:anySimpleType` from which all simple types, including atomic, list,
> and union types are derived.

__xdm:anyUnionType__
> The datatype __xdm:anyUnionType__ is a union type that includes all
> union types (and no values that are not unions). Its base type is
> `xs:anySimpleType` from which all simple types, including atomic, list,
> and union types are derived.

#### 2.1.3 Part 3: Atomic Types

<pre><code>xs:anyAtomicType¹ [XsAnyAtomicType]
├─── xs:anyURI [XsAnyUriValue]
├─── xs:base64Binary
├─── xs:boolean
├─── xs:date
├─── xs:dateTime
│    └─── xs:dateTimeStamp²
├─── xs:decimal [XsDecimalValue³]
│    └─── xs:integer [XsIntegerValue³]
│         ├─── xs:long
│         │    └─── xs:int
│         │         └─── xs:short
│         │              └─── xs:byte
│         ├─── xs:nonNegativeInteger
│         │    ├─── xs:positiveInteger
│         │    └─── xs:unsignedLong
│         │         └─── xs:unsignedInt
│         │              └─── xs:unsignedShort
│         │                   └─── xs:unsignedByte
│         └─── xs:nonPositiveInteger
│              └─── xs:negativeInteger
├─── xs:double [XsDoubleValue]
├─── xs:duration [XsDurationValue]
│    ├─── xs:dayTimeDuration¹
│    └─── xs:yearMonthDuration¹
├─── xs:float
├─── xs:gDay
├─── xs:gMonth
├─── xs:gMonthDay
├─── xs:gYear
├─── xs:gYearMonth
├─── xs:hexBinary
├─── xs:NOTATION
├─── xs:QName [XsQNameValue]
├─── xs:string [XsStringValue]
│    └─── xs:normalizedString [XsNormalizedValue]
│         ├─── xs:token [XsTokenValue]
│         │    ├─── xs:language
│         │    └─── xs:Name [XsNameValue]
│         │         └─── xs:NCName [XsNCNameValue]
│         │              ├─── xs:ENTITY
│         │              ├─── xs:ID
│         │              ├─── xs:IDREF
│         │              └─── xdm:wildcard [XdmWildcardValue]
│         └─── xs:NMTOKEN
├─── xs:time
└─── xs:untypedAtomic
</code></pre>

1.  `xs:anyAtomicType`, `xs:yearMonthDuration`, and `xs:dayTimeDuration` are
    defined in XML Schema 1.1 Part 2, and in XQuery and XPath 3.1 Data Model.
    Support for these types is available on any conforming implementation.

1.  `xs:dateTimeStamp` is defined in XML Schema 1.1 Part 2, but not in XQuery
    and XPath 3.1 Data Model. Support for this type is dependent on whether the
    implementation supports XML Schema 1.1.

1.  In the interface hierarchy, `XsIntegerValue` is a base class of
    `XsAnyAtomicType`, not `XsDecimalValue`. This is because `XsIntegerValue`
    and `XsDecimalValue` are modelled as having different data value types,
    so are incompatible in the API.

The data model defines one additional atomic type: `xdm:wildcard`. This type is
defined in an XQuery IntelliJ Plugin specific namespace.

__xdm:wildcard__
> The type `xdm:wildcard` is derived from `xs:NCName`. The lexical
> representation of `xdm:wildcard` is `*`. The value space of `xdm:wildcard`
> is the empty set.
>
> The unspecified prefix or local name of a `Wildcard` is an instance of
> `xdm:wildcard`.

#### 2.1.4 Part 4: Sequences
\[Definition: The *lower bound* of a sequence type specifies the minimum number
of values the sequence can contain.\] The value space is restricted to `null`,
`0` and `1`.

\[Definition: The *upper bound* of a sequence type specifies the maximum number
of values the sequence can contain.\] The value space is either `null`,
a non-negative integer, or `infinity`. Here, `infinity` signifies *many* items,
an unbounded number of items and is mapped to the maximum integer value.

\[Definition: The *cardinality* of a sequence is the *lower bound* and *upper
bound* pair.\] This constrains the number of items in the sequence.

\[Definition: The *item type* of a sequence type is the single item type
associated with the values in the sequence.\]

The *lower bound*, *upper bound*, and *item type* values are mapped as follows:

| Type                 | lower bound | upper bound | item type         | Description                      |
|----------------------|-------------|-------------|-------------------|----------------------------------|
| `xs:error`           | `null`      | `null`      | `xs:error`        | An XSD error item.               |
| `xs:error+`          | `null`      | `null`      | `xs:error`        | An XSD error sequence.           |
| `xs:error?`          | `0`         | `0`         | `null`            | An optional XSD error item.      |
| `xs:error*`          | `0`         | `0`         | `null`            | An optional XSD error sequence.  |
| `()`                 | `0`         | `0`         | `null`            | An empty sequence.               |
| `T?`                 | `0`         | `1`         | `T`               | An optional item.                |
| `T*`                 | `0`         | `infinity`  | `T`               | An optional sequence.            |
| *list type*          | `0`         | `infinity`  | *atomic type*     | An XMLSchema list type.          |
| `T`                  | `1`         | `1`         | `T`               | A single item.                   |
| `T+`                 | `1`         | `infinity`  | `T`               | A sequence.                      |
| `(T1, T2, ..., Tn)?` | `0`         | `n`         | *item type union* | An optional restricted sequence. |
| `(T1, T2, ..., Tn)`  | `1`         | `n`         | *item type union* | A restricted sequence.           |

> Note:
>
> The list types are only valid in `cast as` expressions, it cannot be used as
> the type of a variable. Specifying it here is useful for specifying the static
> type of the cast expression on a list type, and for providing a replacement
> suggestion in the IDE.

#### 2.1.5 Sequence Types

<pre><code>XdmSequenceType
├─── XdmItemType
├─── XdmSequenceTypeList
└─── XdmSequenceTypeUnion
</code></pre>

An `XdmSequenceType` is any of the `SequenceType` symbols. It provides access
to the *lower bound*, *upper bound*, and *item type* values of that sequence
type, as well as a *type name* used for presenting the type to the user.

An `XdmItemType` is any of the `ItemType` symbols that represent a sequence of
size one whose item type is itself. An item type has a *type class* property
that is the Java `Class` of the item. This is one of the interfaces defined in
parts 1 to 3 of this section.

An `XdmSequenceTypeList` is an instance of the `SequenceTypeList` symbol used
in a `TypedFunctionTest`. It is a comma-separated list of *sequence types* that
define the type of each item in an `ArgumentList`.

An `XdmSequenceTypeUnion` is an instance of the `SequenceTypeUnion` symbol used
in a `typeswitch` expression or parenthesized sequence type. It is a `|`-separated
list of *sequence types* that define the possible types of a sequence type such
as the *operand expression* in a `TypeswitchExpr`.

Each PSI element in the `SequenceType` EBNF implements one of these interfaces,
defining the properties according to the specific sequence type. This is limited
to the information available at parse time, so precise values for the lower and
upper bounds of XMLSchema types are not defined.¹ It should be possible to calculate
the precise bounds as needed on the resolved types.

1.  This is primarily for performance reasons. Identifying the precise XMLSchema
    type requires expanding the namespace of the `AtomicOrUnionType`, locating the
    bound namespace, locating the type within the schema files, and then
    calculating the bounds from that type. If any of the context above the schema
    type, such as the schema namespace, changes then the type and everything it
    depends on need to be recalculated.

## 3 Type Manipulation

### 3.1 Upper and Lower Bounds

#### 3.1.1 Minimum of two bounds
The minimum of two bounds is determined by taking the minimum numerical value
of each bound. This gives the following results:

|            | `0`        | `1`        | `m`        | `infinity` |
|------------|------------|------------|------------|------------|
| `0`        | `0`        | `0`        | `0`        | `0`        |
| `1`        | `0`        | `1`        | `1`        | `1`        |
| `n`        | `0`        | `1`        | `min(m,n)` | `n`        |
| `infinity` | `0`        | `1`        | `m`        | `infinity` |

> Note:
>
> This does not currently handle the rules for xs:error (`null` bound).

#### 3.1.2 Maximum of two bounds
The maximum of two bounds is determined by taking the maximum numerical value
of each bound. This gives the following results:

|            | `0`        | `1`        | `m`        | `infinity` |
|------------|------------|------------|------------|------------|
| `0`        | `0`        | `1`        | `m`        | `infinity` |
| `1`        | `1`        | `1`        | `m`        | `infinity` |
| `n`        | `n`        | `n`        | `max(m,n)` | `infinity` |
| `infinity` | `infinity` | `infinity` | `infinity` | `infinity` |

> Note:
>
> This does not currently handle the rules for xs:error (`null` bound).

#### 3.1.3 Sum of two bounds
The sum of two bounds `Ab` and `Bb` is computed using the following rules:
1.  If `Ab` is `infinity`, the sum is `infinity`.
1.  If `Bb` is `infinity`, the sum is `infinity`.
1.  If the sum of `Ab` and `Bb` overflows (is greater than the maximum
    representable integer), the sum is `infinity`.
1.  Otherwise, the sum is `Ab + Bb`.

> Note:
>
> This does not currently handle the rules for xs:error (`null` bound).

### 3.2 Aggregate Types
\[Definition: The *aggregate type* of an expression is the type that best
matches the type of each part of that expression, such that the expression
could be assigned to a variable set to that aggregate type and not raise a
type error.\]

\[Definition: A *disjoint expression* is an expression that consists of
different values depending on the evaluation of conditions, such as in `if`,
`typeswitch`, or `switch` expressions.\] The aggregate type of a disjoint
expression is the union of the type of each conditional value in that
disjoint expression.

> __Example__
>
> The expression `if ($x instance of xs:string) then 2 else ()` has the types
> `xs:integer` and `empty-sequence()` in the then and else clauses that form
> the conditional values of the `if` expression. This expression has an
> *aggregate type* of `xs:integer?` that is the union of the conditional types.

\[Definition: A *sequence expression* is an expression that consists of a
list of expressions that computes the values in the resulting sequence.\]
The aggregate type of a sequence expression is the addition of the type of
each expression used to construct the sequence.

> __Example__
>
> The expression `(2, (), "test" cast as xs:NCName)` has the types
> `xs:integer`, `empty-sequence()`, and `xs:NCName` for each item in the
> sequence, and an *aggregate type* of `union(xs:integer, xs:NCName)+` that
> is the combination of the types of the items in the sequence.

#### 3.2.1 Item Type Union
The *item type union* is the union of the ItemType Ai and the ItemType Bi. It
is determined as follows:
1.  If `subtype-itemtype(Ai, Bi)`, then the union is `Bi`.
1.  If `subtype-itemtype(Bi, Ai)`, then the union is `Ai`.
1.  If `Ai` and `Bi` are union types, then the union is a union type with the
    member types of `Ai` and the member types of `Bi` as its member types.
1.  If `Ai` is a union type, and `Bi` is a simple type, then the union is a
    union type with the member types of `Ai` and the simple type `Bi` as its
    member types.
1.  If `Ai` is a simple type, and `Bi` is a union type, then the union is a
    union type with the simple type `Ai` and the member types of `Bi` as its
    member types.
1.  If `Ai` and `Bi` are simple types, then the union is a union type with
    the simple types `Ai` and `Bi` as its member types.
1.  If `Ai` and `Bi` are KindTest types, then the union is `node()`.
1.  Otherwise, the union is the `item()` type.

#### 3.2.2 Sequence Type Union
When computing the union of *BT* with *TT*, the resulting sequence type is
computed as follows:
1.  The *lower bound* is the minimum of the *lower bound* for *BT* and *TT*.
1.  The *upper bound* is the maximum of the *upper bound* for *BT* and *TT*.
1.  The *item type* is determined as follows:
    1.  If *BT* is the empty sequence, then *item type* is the *item type* for
        *TT*.
    1.  If *TT* is the empty sequence, then *item type* is the *item type* for
        *BT*.
    1.  The *item type* is the *[item type union](#321-item-type-union)* of the
        *item type* for *BT* and *TT*.

The union is then:
1.  *BT* if the computed *lower bound*, *upper bound*, and *item type* are the
    same as those for *BT*.
1.  *TT* if the computed *lower bound*, *upper bound*, and *item type* are the
    same as those for *TT*.
1.  A new sequence type using the computed *lower bound*, *upper bound*, and
    *item type* values. See the mapping table in the type system part 4:
    [sequences](#214-part-4-sequences) section.

#### 3.3.3 Sequence Type Addition
When computing the addition of *BT* with *TT*, the resulting sequence type is
computed as follows:
1.  The *lower bound* is the maximum of the *lower bound* for *BT* and *TT*.
1.  The *upper bound* is the sum of the *upper bound* for *BT* and *TT*. 
1.  The *item type* is determined as follows:
    1.  If *BT* is the empty sequence, then *item type* is the *item type* for
        *TT*.
    1.  If *TT* is the empty sequence, then *item type* is the *item type* for
        *BT*.
    1.  The *item type* is the *[item type union](#321-item-type-union)* of the
        *item type* for *BT* and *TT*.

The addition is then:
1.  *BT* if the computed *lower bound*, *upper bound*, and *item type* are the
    same as those for *BT*.
1.  *TT* if the computed *lower bound*, *upper bound*, and *item type* are the
    same as those for *TT*.
1.  A new sequence type using the computed *lower bound*, *upper bound*, and
    *item type* values. See the mapping table in the type system part 4:
    [sequences](#214-part-4-sequences) section.

## 4 Data Model

### 4.1 Literals

| Symbol           | Type         | Interface         | Representation |
|------------------|--------------|-------------------|----------------|
| `DecimalLiteral` | `xs:decimal` | `XsDecimalValue`  | `BigDecimal`   |
| `DoubleLiteral`  | `xs:double`  | `XsDoubleLiteral` | `Double`       |
| `IntegerLiteral` | `xs:integer` | `XsIntegerValue`  | `BigInteger`   |
| `StringLiteral`  | `xs:string`  | `XsStringValue`   | `String`       |
| `URILiteral`     | `xs:anyURI`  | `XsAnyUriValue`   | `String`       |

The PSI elements for the literal symbols implement the interface corresponding
to their associated atomic type defined above. These have a *data* property
that is the literal's value as represented by the given Java type.

The `xs:anyURI` representation is `String` as the content is not validated at
the point the PSI tree is constructed. This is to permit partially typed URIs,
or incorrectly typed URIs, to be represented correctly without throwing
malformed URI exceptions.

### 4.2 EQNames and Wildcards

| Symbol                            | Type           | Interface          | Representation |
|-----------------------------------|----------------|--------------------|----------------|
| `NCName`<sup><em>Names</em></sup> | `xs:NCName`    | `XsNCNameValue`    | `String`       |
| `BracedURILiteral`                | `xs:anyURI`    | `XsAnyUriValue`    | `String`       |
| `WildcardIndicator`               | `xdm:wildcard` | `XdmWildcardValue` | `String`       |

The parts that make up an EQName implement the interface corresponding to their
associated atomic type defined above. These have a *data* property that is the
atomic type's value as represented by the given Java type.

The `xs:anyURI` representation is `String` as the content is not validated at
the point the PSI tree is constructed. This is to permit partially typed URIs,
or incorrectly typed URIs, to be represented correctly without throwing
malformed URI exceptions.

The `XsQNameValue` interface has the following properties:
1.  *namespace*;
1.  *prefix*;
1.  *local name*;
1.  *is lexical qname*.

| Symbol             | namespace | prefix | local name | is lexical qname |
|--------------------|-----------|--------|------------|------------------|
| `QName`            | no        | yes    | yes        | true             |
| `NCName`           | no        | no     | yes        | true             |
| `URIQualifiedName` | yes       | no     | yes        | false            |

The `Wildcard` symbol is also an `XsQNameValue`, with the properties mirroring
the `NCName`, `QName`, or `URIQualifiedName`. The prefix or local parts can be
an instance of `xdm:wildcard` to indicate that any value matches.

A QName can be expanded, where a sequence of expanded QNames is returned with
the namespace bound to the namespace of the matching namespace declarations in
the prolog or any direct elements.

### 4.3 Nodes

| Symbol                | Interface        | node name |
|-----------------------|------------------|-----------|
| `CompElemConstructor` | `XdmElementNode` | yes       |
| `DirElemConstructor`  | `XdmElementNode` | yes       |

The `XdmElementNode` interface has an additional *closing tag* property. This
is used for the `err:XQST0118` error condition inspection. For a
`CompElemConstructor` or self-closing `DirElemConstructor` the *closing tag*
property is the same as the*node name* property.

## A References

### A.1 W3C References
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

### A.2 XPath NG Proposals
*  EXPath. *Proposal for Annotation Sequence Types*. EXPath Proposal. See
   [https://github.com/expath/xpath-ng/pull/10]().
*  EXPath. *Proposal for Restricted Sequences*. EXPath Proposal. See
   [https://github.com/expath/xpath-ng/pull/11]().
