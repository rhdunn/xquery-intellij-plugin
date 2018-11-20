# XQuery IntelliJ Plugin Data Model

This document includes material copied from or derived from the XPath and
XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

## Abstract
This document defines the data model for vendor and plugin specific
functionality that extends XQuery and associated W3C extensions. The
plugin data model extensions are to fill in gaps within the type
system and to provide static type analysis.

## Table of Contents
- [1 Introduction](#1-introduction)
- [2 Concepts](#2-concepts)
  - [2.1 Type System](#21-type-system)
    - [2.1.1 Part 1: Items](#211-part-1-items)
    - [2.1.2 Part 2: Simple and Complex Types](#212-part-2-simple-and-complex-types)
    - [2.1.3 Part 3: Atomic Types](#213-part-3-atomic-types)
    - [2.1.4 Part 4: Sequences](#214-part-4-sequences)
- [3 Type Manipulation](#3-type-manipulation)
  - [3.1 Upper and Lower Bounds](#31-upper-and-lower-bounds)
    - [3.1.1 Minimum of two bounds](#311-minimum-of-two-bounds)
    - [3.1.2 Maximum of two bounds](#312-maximum-of-two-bounds)
    - [3.1.3 Sum of two bounds](#313-sum-of-two-bounds)
  - [3.2 Aggregate Types](#32-aggregate-types)
    - [3.2.1 Item Type Union](#321-item-type-union)
    - [3.2.2 Sequence Type Union](#322-sequence-type-union)
    - [3.3.3 Sequence Type Addition](#333-sequence-type-addition)
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

#### 2.1.1 Part 1: Items

<pre><code>item()
├─── node()
│    ├─── attribute()
│    │    └─── <em>user-defined attribute types</em>
│    ├─── document-node()
│    │    └─── <em>document types with more precise content type</em>
│    ├─── element()
│    │    └─── <em>user-defined element types</em>
│    ├─── comment()
│    ├─── namespace-node()
│    ├─── processing-instruction()
│    └─── text()
├──── function(*)
│    ├─── map(*)
│    └─── array(*)
└─── xs:anyAtomicType ──────────────────────────────────── See Part 3
</code></pre>

#### 2.1.2 Part 2: Simple and Complex Types

<pre><code>xs:anyType
├─── xdm:anyComplexType
│    ├─── xs:untyped
│    └─── <em>user-defined complex types</em>
└─── xs:anySimpleType
     ├─── xs:anyAtomicType ─────────────────────────────── See Part 3
     ├─── xdm:anyListType
     │    ├─── xs:IDREFS
     │    ├─── xs:NMTOKENS
     │    ├─── xs:ENTITIES
     │    └─── <em>user-defined list types</em>
     └─── xdm:anyUnionType
          ├─── xs:numeric
          ├─── xs:error<sup>1</sup>
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

<pre><code>xs:anyAtomicType<sup>1</sup>
├─── xs:anyURI
├─── xs:base64Binary
├─── xs:boolean
├─── xs:date
├─── xs:dateTime
│    └─── xs:dateTimeStamp<sup>2</sup>
├─── xs:decimal
│    └─── xs:integer
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
├─── xs:double
├─── xs:duration
│    ├─── xs:dayTimeDuration<sup>1</sup>
│    └─── xs:yearMonthDuration<sup>1</sup>
├─── xs:float
├─── xs:gDay
├─── xs:gMonth
├─── xs:gMonthDay
├─── xs:gYear
├─── xs:gYearMonth
├─── xs:hexBinary
├─── xs:NOTATION
├─── xs:QName
├─── xs:string
│    └─── xs:normalizedString
│         ├─── xs:token
│         │    ├─── xs:language
│         │    └─── xs:Name
│         │         └─── xs:NCName
│         │              ├─── xs:ENTITY
│         │              ├─── xs:ID
│         │              ├─── xs:IDREF
│         │              └─── xdm:wildcard
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
| `xs:error?`          | `0`         | `0`         | `item()`          | An optional XSD error item.      |
| `xs:error*`          | `0`         | `0`         | `item()`          | An optional XSD error sequence.  |
| `()`                 | `0`         | `0`         | `item()`          | An empty sequence.               |
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
*  EXPath. *Proposal for Restricted Sequences*. EXPath Proposal. See
   [https://github.com/expath/xpath-ng/pull/11]().
