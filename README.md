[![Build Status](https://travis-ci.org/rhdunn/xquery-intellij-plugin.svg?branch=master)](https://travis-ci.org/rhdunn/xquery-intellij-plugin/master)
[![codecov.io](https://codecov.io/github/rhdunn/xquery-intellij-plugin/coverage.svg)](https://codecov.io/github/rhdunn/xquery-intellij-plugin)
[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![Apache 2.0 License](https://img.shields.io/github/license/rhdunn/xquery-intellij-plugin.svg)](LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/rhdunn/xquery-intellij-plugin.svg)](https://github.com/rhdunn/xquery-intellij-plugin/issues)

<img src="images/syntax-highlighting.png" alt="Syntax Highlighting" width="70%" align="right"/>

## IntelliJ XQuery Plugin

This is a plugin for the IntelliJ IDE 2018.1 &ndash; 2018.3 that adds support
for the XML Query (XQuery) language. This covers support for:
1.  XQuery 1.0, 3.0, and 3.1;
1.  XQuery and XPath Full Text;
1.  XQuery Scripting Extension;
1.  XQuery Update Facility 1.0, and 3.0;
1.  Vendor XQuery extensions.

##### Supported XQuery Implementations

<img src="images/xquery-settings.png" alt="XQuery Settings" width="60%" align="right"/>

This plugin provides support for the following implementations of XQuery:

1.  [BaseX](docs/XQuery%20IntelliJ%20Plugin.md#c1-basex-vendor-extensions)
    7.0 &ndash; 9.1
1.  [eXist-db](docs/XQuery%20IntelliJ%20Plugin.md#c5-exist-db-extensions)
    4.4
1.  [MarkLogic](docs/XQuery%20IntelliJ%20Plugin.md#c2-marklogic-vendor-extensions)
    8.0 &ndash; 9.0
1.  [Saxon](docs/XQuery%20IntelliJ%20Plugin.md#c3-saxon-vendor-extensions)
    9.4 &ndash; 9.9

The XQuery implementation, implementation version, XQuery version and dialect
can be configured to provide checks for supported vendor extensions.

##### Robust Language Support

<img src="images/error-messages.png" alt="Error Messages" width="60%" align="right"/>

The plugin provides rock solid standards conforming XQuery language support.
This features vendor extension support, recovery from invalid XQuery, and xqDoc
syntax highlighting.

The plugin can be configured to check that only XQuery syntax supported by an
XQuery processor is used.

##### IntelliJ Integration

<img src="images/resolve-uriliteral.png" alt="Resolve URI Literals" width="60%" align="right"/>

The plugin provides integration with the following IntelliJ features:
1.  Resolving URI string literal, function, and variable references.
1.  Code folding.
1.  TODO highlighting for XQuery and XML comments.
1.  Find usages.
1.  Paired brace matching.
1.  Commenting code.

-----

Copyright (C) 2016-2018 Reece H. Dunn

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE) license.