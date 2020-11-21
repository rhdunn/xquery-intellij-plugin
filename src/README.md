# XQuery IntelliJ Plugin Source Code

## Table of Contents
1. [Core](#core)
1. [Language Support](#language-support)
1. [Language Implementations](#language-implementations)
1. [Importable and Declarable Module Support](#importable-and-declarable-module-support)
1. [Plugin](#plugin)

## Core
| Package           | Description |
|-------------------|-------------|
| `intellij-compat` | Compatibility APIs for supporting IntelliJ 2018.1 and later. |
| `intellij-test`   | Mocks, stubs, and test helpers for IntelliJ components used in the unit and integration tests. |
| `kotlin-hamcrest` | Helpers for using hamcrest in kotlin projects. |
| `kotlin-intellij` | Helpers for using IntelliJ components in kotlin projects. |

## Language Support
| Package           | Description |
|-------------------|-------------|
| `lang-core`       | Custom language support helper classes and functions. |
| `lang-xdm`        | XQuery and XPath data model. |
| `lang-xpm`        | XQuery and XPath processing model. |
| `lang-xqdoc`      | xqDoc documentation support. |

## Language Implementations
| Package           | Description |
|-------------------|-------------|
| `lang-xpath`      | XPath language support for XSLT and XQuery. |
| `lang-xquery`     | XQuery language support. |
| `lang-xslt`       | XSLT language support. |

## Importable and Declarable Module Support
| Package           | Description |
|-------------------|-------------|
| `lang-java`       | Support for Java modules (BaseX, eXist-db, Saxon). |

## Plugin
| Package            | Description |
|--------------------|-------------|
| `plugin-api`       | An API for integrating thirdparty applications and libraries. |
| `plugin-basex`     | Support for the BaseX database. |
| `plugin-existdb`   | Support for the eXist database. |
| `plugin-expath`    | Support for the EXPath modules. |
| `plugin-exquery`   | Support for the EXQuery modules. |
| `plugin-marklogic` | Support for the MarkLogic database. |
| `plugin-saxon`     | Support for the Saxon XSLT, XPath, and XQuery processor. |
| `plugin-w3`        | Support for the W3C modules. |
