# XQuery IntelliJ Plugin Source Code

## Table of Contents
1. [Core](#core)
1. [Custom Language Support](#custom-language-support)
1. [Plugin](#plugin)

## Core
| Package           | Description |
|-------------------|-------------|
| `intellij-compat` | Compatibility APIs for supporting IntelliJ 2018.1 and later. |
| `intellij-mock`   | Mocks and stubs for IntelliJ components used in the unit and integration tests. |
| `kotlin-hamcrest` | Helpers for using hamcrest in kotlin projects. |
| `kotlin-intellij` | Helpers for using IntelliJ components in kotlin projects. |

## Custom Language Support
| Package           | Description |
|-------------------|-------------|
| `lang-core`       | Custom language support helper classes and functions. |
| `lang-xdm`        | XQuery and XPath data model. |
| `lang-xpath`      | XPath language support for XSLT and XQuery. |
| `lang-xqdoc`      | xqDoc documentation support. |
| `lang-xquery`     | XQuery language support. |

## Plugin
| Package            | Description |
|--------------------|-------------|
| `plugin-api`       | An API for integrating thirdparty applications and libraries. |
| `plugin-basex`     | Support for the BaseX database. |
| `plugin-existdb`   | Support for the eXist database. |
| `plugin-marklogic` | Support for the MarkLogic database. |
| `plugin-saxon`     | Support for the Saxon XSLT, XPath, and XQuery processor. |
