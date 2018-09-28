# XQuery IntelliJ Plugin Data Model

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
- [A References](#a-references)
  - [A.1 W3C References](#a1-w3c-references)

## 1 Introduction
The XQuery IntelliJ plugin provides language support for XQuery, W3C extensions
to XQuery, and vendor extensions.

This document describes the data model extensions to XPath and XQuery to support
static analysis.

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
of values the sequence can contain.\] The value space is restricted to `0` and
`1`.

\[Definition: The *upper bound* of a sequence type specifies the maximum number
of values the sequence can contain.\] The value space is restricted to `0`,
`1`, and `n`. Here, `n` signifies *many* items, an unbounded number of items
and is mapped to the maximum integer value.

\[Definition: The *cardinality* of a sequence is the *lower bound* and *upper
bound* pair.\] This constrains the number of items in the sequence.

\[Definition: The *item type* of a sequence type is the single item type
associated with the values in the sequence.\]

The *lower bound*, *upper bound*, and *item type* values are mapped as follows:

| Type        | lower bound | upper bound | item type     | Description             |
|-------------|-------------|-------------|---------------|-------------------------|
| `()`        | `0`         | `0`         | `xs:untyped`  | An empty sequence.      |
| `T?`        | `0`         | `1`         | `T`           | An optional item.       |
| `T*`        | `0`         | `n`         | `T`           | An optional sequence.   |
| *list type* | `0`         | `n`         | *atomic type* | An XMLSchema list type. |
| `T`         | `1`         | `1`         | `T`           | A single item.          |
| `T+`        | `1`         | `n`         | `T`           | A sequence.             |

## 3 Type Manipulation

### 3.1 Upper and Lower Bounds

#### 3.1.1 Minimum of two bounds
The minimum of two bounds is determined by taking the minimum numerical value
of each bound, and converting the result back to a bound. This gives the
following results:

|     | `0` | `1` | `n` |
|-----|-----|-----|-----|
| `0` | `0` | `0` | `0` |
| `1` | `0` | `1` | `1` |
| `n` | `0` | `1` | `n` |

#### 3.1.2 Maximum of two bounds
The maximum of two bounds is determined by taking the maximum numerical value
of each bound, and converting the result back to a bound. This gives the
following results:

|     | `0` | `1` | `n` |
|-----|-----|-----|-----|
| `0` | `0` | `1` | `n` |
| `1` | `1` | `1` | `n` |
| `n` | `n` | `n` | `n` |

#### 3.1.3 Sum of two bounds
The sum of two bounds `Ab` and `Bb` is computed using the following rules:
1.  If `Ab` and `Bb` are `0`, the sum is `0`.
1.  If `Ab` is `1` and `Bb` is `0`, the sum is `1`.
1.  If `Ab` is `0` and `Bb` is `1`, the sum is `1`.
1.  Otherwise, the sum is `n`.

This gives the following results:

|     | `0` | `1` | `n` |
|-----|-----|-----|-----|
| `0` | `0` | `1` | `n` |
| `1` | `1` | `n` | `n` |
| `n` | `n` | `n` | `n` |

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
