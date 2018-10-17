# XQuery IntelliJ Plugin Functions and Operators

## Abstract
This document defines the functions, operators, annotations, and options
used by the XQuery IntelliJ Plugin to implement the XQuery integration with
JetBrain's IntelliJ IDEs.

## Table of Contents
- [1 Introduction](#1-introduction)
  - [1.1 Namespaces and prefixes](#11-namespaces-and-prefixes)
  - [1.2 Common definitions](#12-common-definitions)
    - [1.2.1 implementation](#121-implementation)
    - [1.2.2 specification](#122-specification)
    - [1.2.3 version](#123-version)
- [2 Options](#2-options)
  - [2.1 o:implements-module](#21-oimplements-module)
  - [2.2 o:requires](#22-orequires)
  - [2.3 o:requires-import](#23-orequires-import)
- [A References](#a-references)
  - [A.1 W3C References](#a1-w3c-references)
  - [A.2 EXPath References](#a2-expath-references)
  - [A.3 EXQuery References](#a3-exquery-references)

## 1 Introduction
The purpose of this document is to catalog the options, annotations, functions,
and operators used by the XQuery IntelliJ Plugin to implement support for XQuery
in the IntelliJ family of Integrated Development Environments (IDEs). This is
in addition to the options, annotations, functions, and operators defined by
XQuery and the different vendor implementations of XQuery supported by the
plugin.

These extensions can be grouped into two categories. The first is the options
and annotations used to provide context such as versioning to the built-in
function definitions. The second is the functions and operators used internally
to provide static analysis and other functionality for the plugin.

This document does not describe the options, annotations, functions, and
operators supported by the different XQuery vendors.

### 1.1 Namespaces and prefixes
This document uses the following namespace prefixes to represent the namespace
URIs with which they are listed. These namespace prefixes are not predeclared
and their use in this document is not normative.

*  `a = http://reecedunn.co.uk/xquery/annotations`
*  `o = http://reecedunn.co.uk/xquery/options`

Functions defined with the `op` prefix are described here to underpin the
definitions of the operators in XQuery vendor extensions. These functions
are not available directly to users, and there is no requirement that
implementations should actually provide these functions. For this reason,
no namespace is associated with the `op` prefix.

### 1.2 Common definitions
These values are used by different 

#### 1.2.1 implementation
An *implementation* is one of:
1. `basex`
1. `exist-db`
1. `marklogic`
1. `saxon`

It specifies which XQuery processor the module definitions apply to.

#### 1.2.2 specification
A *specification* is one of:
1. `expath-binary` -- EXPath Binary Module
1. `expath-crypto` -- EXPath Cryptographic Module
1. `expath-file` -- EXPath File Module
1. `expath-geo` -- EXPath Geo Module
1. `expath-http` -- EXPath HTTP Client Module
1. `expath-webapp` -- EXPath Web Applications
1. `expath-zip` -- EXPath ZIP Module
1. `exquery-restxq` -- RESTXQ 1.0: RESTful Annotations for XQuery
1. `xpath-functions` -- XPath and XQuery Functions and Operators

It specifies which XQuery specification the module applies to.

#### 1.2.3 version
A *version* can be a major/minor (e.g. `7.0`), major/minor/patch (e.g.
`7.7.2`), MarkLogic major/minor/patch (e.g. `8.0-6.2`), or major/minor/date
(e.g. `1.0-20131203`) version string.

## 2 Options

### 2.1 o:implements-module
###### Summary
Specifies the version of a specification supported by an implementation.
###### Syntax
>     implementation "/" version S "as" S specification "/" version
###### Example
>     declare option o:implements-module "basex/7.8 as expath-binary/1.0-20131203";

### 2.2 o:requires
###### Summary
Specifies the specification or implementation, and minimum version of those
that is required for the given built-in module definitions.
###### Syntax
>     (implementation|specification) "/" version
###### Example
>     declare option o:requires "marklogic/7.0";

### 2.3 o:requires-import
###### Summary
Specifies the specification or implementation, and minimum version of those
that is required for the given imported module definitions.
###### Syntax
>     (implementation|specification) "/" version ";" S "location-uri" "=" "(none)"
>     (implementation|specification) "/" version ";" S "location-uri" "=" AnyURI
###### Example
>     declare option o:requires-import "basex/7.5; location-uri=(none)";

## A References

### A.1 W3C References
*  W3C. *XPath and XQuery Functions and Operators 3.1*. W3C Recommendation 21
   March 2017. See [https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/]().

### A.2 EXPath References
*  EXPath. *Binary Module 1.0*. EXPath Module 3 December 2013. See
   [http://expath.org/spec/binary/1.0]().
*  EXPath. *Cryptographic Module*. EXPath Candidate Module 14 February 2015.
   See [http://expath.org/spec/crypto/20150214](https://web.archive.org/web/20170227073403/http://expath.org/spec/crypto/20150214).
*  EXPath. *File Module 1.0*. EXPath Module 20 February 2015. See
   [http://expath.org/spec/file/1.0]().
*  EXPath. *Geo Module*. EXPath Candidate Module 30 September 2010.
   See [http://expath.org/spec/geo/20100930]().
*  EXPath. *HTTP Client Module*. EXPath Candidate Module 9 January 2010.
   See [http://expath.org/spec/http-client/20100109]().
*  EXPath. *Web Applications*. EXPath Candidate Module 1 April 2013.
   See [http://expath.org/spec/webapp/20130401]().
*  EXPath. *ZIP Module*. EXPath Candidate Module 12 October 2010.
   See [http://expath.org/spec/zip/20101012]().

### A.3 EXQuery References
*  EXQuery. *RESTXQ 1.0: RESTful Annotations for XQuery*. Unofficial Draft 21
   March 2016. See [http://exquery.github.io/exquery/exquery-restxq-specification/restxq-1.0-specification.html]().
