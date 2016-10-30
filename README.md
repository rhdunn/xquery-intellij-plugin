# IntelliJ XQuery Plugin

[![Build Status](https://travis-ci.org/rhdunn/xquery-intellij-plugin.svg)](https://travis-ci.org/rhdunn/xquery-intellij-plugin)
[![codecov.io](https://codecov.io/github/rhdunn/xquery-intellij-plugin/coverage.svg)](https://codecov.io/github/rhdunn/xquery-intellij-plugin)

- [Features](#features)
  - [Language Support](#language-support)
  - [Warnings and Errors](#warnings-and-errors)
  - [IntelliJ Integration](#intellij-integration)
- [License Information](#license-information)

----------

This project provides XQuery support for the IntelliJ IDE.

_Supported IntelliJ Platforms:_ IntelliJ IDEA Community, IntelliJ IDEA Ultimate,
PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, Rider, Android Studio

_Supported IntelliJ Versions:_ 2016.1 - 2016.2

_Supported XQuery Versions:_ 1.0, 3.0

_Supported XQuery Extensions:_ Update Facility 1.0

_Supported XQuery Implementations:_ MarkLogic, Saxonica, W3C Specifications

## Features

### Language Support

Standard conforming XQuery syntax highlighter and parser with file encoding
detection. Supports:

*  XQuery 1.0 and 3.0 core language;
*  XQuery Update Facility 1.0 extension;
*  MarkLogic 6.0 to 8.0 vendor extensions.

![Syntax Highlighting](images/syntax-highlighting.png)

Control over how XQuery dialects are interpreted.

![XQuery Settings](images/xquery-settings.png)

## Warnings and Errors

Helpful error messages for invalid XQuery constructs.

![Error Messages](images/error-messages.png)

Warnings for XQuery constructs that are valid in a different version or extension
to the one configured in the project.

![Require Different Version](images/require-different-version.png)

## IntelliJ Integration

Resolve URI string literals to the files they reference.

![Resolve URI Literals](images/resolve-uriliteral.png)

Resolve namespaces, functions and variables to their corresponding declarations.

Other supported IntelliJ features:

1.  Find usages.

## License Information

Copyright (C) 2016 Reece H. Dunn

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE)
license.
