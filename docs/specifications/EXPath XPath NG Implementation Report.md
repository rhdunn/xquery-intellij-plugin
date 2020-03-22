---
layout: page
title: EXPath XPath NG Implementation Report
---

| REF | Name                                                                                           | Lexer    | Parser   | Data Model | Static Context | Error Conditions |
|----:|------------------------------------------------------------------------------------------------|----------|----------|------------|----------------|------------------|
|   1 | [Variadic Function Arguments](https://github.com/expath/xpath-ng/pull/1)                       | 1.4      | 1.4      | 1.4        | 1.4            | 1.4              |
|   2 | [Conditional Expressions](https://github.com/expath/xpath-ng/pull/2)                           | 1.3      | 1.3      | n/a        | n/a            | No               |
|   3 | [Extensions to Unary and Binary Lookup Expressions](https://github.com/expath/xpath-ng/pull/3) | No       | No       | No         | No             | No               |
|   5 | [Concise Inline Function Syntax](https://github.com/expath/xpath-ng/pull/5)                    | 1.3      | 1.3      | No         | n/a            | No               |
|   6 | [Anonymous Union Types](https://github.com/expath/xpath-ng/pull/6)                             | 1.0      | 1.0      | No         | n/a            | No               |
|   7 | [If Without Else](https://github.com/expath/xpath-ng/pull/7)                                   | 1.3      | 1.3      | n/a        | n/a            | No               |
|   8 | [Sequence, Map, and Array Decomposition](https://github.com/expath/xpath-ng/pull/8)            | No       | No       | No         | No             | No               |
|   9 | [Annotation Declarations](https://github.com/expath/xpath-ng/pull/9)                           | No       | No       | No         | No             | No               |
|  10 | [Annotation Sequence Types](https://github.com/expath/xpath-ng/pull/10)                        | No       | No       | No         | No             | No               |
|  11 | [Restricted Sequences](https://github.com/expath/xpath-ng/pull/11)                             | 1.3      | 1.3      | No         | No             | No               |
|  12 | [Lift Single-Item Restrictions on Switch Cases](https://github.com/expath/xpath-ng/pull/12)    | No       | No       | No         | No             | No               |
|  13 | [Extended Element and Attribute Tests](https://github.com/expath/xpath-ng/pull/13)             | n/a      | 1.7      | 1.7        | n/a            | n/a              |

The extended element and attribute tests implementation only supports the ability
to use any wildcard names in `ElementTest` and `AttributeTest`. It does not currently
support the unioned name syntax.
