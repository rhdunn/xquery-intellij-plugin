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

_Supported IntelliJ Versions:_ 2017.1 - 2018.2

_Supported XQuery Implementations:_ BaseX, MarkLogic, Saxonica Saxon, W3C Specifications

## Features

### Language Support

A robust, standard conforming XQuery syntax highlighter and parser with file encoding
detection and error recovery. It supports the following W3C specifications:

*  XQuery 1.0, 3.0 and 3.1 core language;
*  XQuery and XPath Full Text 1.0 and 3.0 for XQuery;
*  XQuery Update Facility 1.0 and 3.0;
*  XQuery Scripting Extension 1.0.

![Syntax Highlighting](images/syntax-highlighting.png)

It supports the following XQuery syntax extensions:

*  [BaseX](docs/XQuery%20IntelliJ%20Plugin.md#c1-basex-vendor-extensions) 7.8 and 8.5;
*  [MarkLogic](docs/XQuery%20IntelliJ%20Plugin.md#c2-marklogic-vendor-extensions) 6.0, 7.0, and 8.0;
*  [Saxon](docs/XQuery%20IntelliJ%20Plugin.md#c3-saxon-vendor-extensions) 9.4, and 9.8.

It has support for xqDoc documentation comments.

The plugin provides control over how XQuery dialects are interpreted.

![XQuery Settings](images/xquery-settings.png)

### Warnings and Errors

Helpful error messages for invalid XQuery constructs.

![Error Messages](images/error-messages.png)

Warnings for XQuery constructs that are valid in a different version or extension
to the one configured in the project.

![Require Different Version](images/require-different-version.png)

### IntelliJ Integration

Resolve URI string literals to the files they reference.

![Resolve URI Literals](images/resolve-uriliteral.png)

Resolve namespaces, functions and variables to their corresponding declarations.

Code folding is supported for the following elements:

*  Comment;
*  DirElemConstructor;
*  EnclosedExpr (including function bodies).

Other supported IntelliJ features:

1.  Find usages.
2.  Paired brace matching.
3.  Commenting code support.

## Documents

[XQuery IntelliJ Plugin](docs/XQuery%20IntelliJ%20Plugin.md) &mdash;
The specification document for vendor and plugin extensions for XPath
and XQuery supported by the XQuery IntelliJ Plugin.

[XQuery IntelliJ Plugin Data Model](docs/XQuery%20IntelliJ%20Plugin%20Data%20Model.md)
&mdash; The specification document for XPath and XQuery Data Model
extensions used by the XQuery IntelliJ Plugin.

## License Information

Copyright (C) 2016-2018 Reece H. Dunn

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE)
license.
