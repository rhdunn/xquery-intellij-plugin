[![Build Status](https://travis-ci.org/rhdunn/xquery-intellij-plugin.svg?branch=master)](https://travis-ci.org/rhdunn/xquery-intellij-plugin/master)
[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/rhdunn/xquery-intellij-plugin.svg)](https://github.com/rhdunn/xquery-intellij-plugin/issues)

<img src="images/xquery-intellij-plugin.png" alt="Syntax Highlighting" width="60%" align="right"/>

## IntelliJ XQuery Plugin

This is a plugin for IntelliJ IDE 2018.1 &ndash; 2019.1 that adds support
for the XML Query (XQuery) language. This covers support for:
1.  XQuery 1.0, 3.0, and 3.1;
1.  XQuery and XPath Full Text extension;
1.  XQuery Update Facility 1.0, and 3.0 extension;
1.  XQuery Scripting extension;
1.  EXPath extensions;
1.  BaseX, MarkLogic, and Saxon vendor extensions.

This plugin provides support for the following implementations of XQuery:

1.  BaseX 7.0 &ndash; 9.1;
1.  eXist-db 4.4;
1.  MarkLogic 8.0 &ndash; 9.0;
1.  Saxon 9.2 &ndash; 9.9.

See https://rhdunn.github.io/xquery-intellij-plugin/ for the plugin documentation
and tutorials.

#### IntelliJ Integration

<img src="images/resolve-uriliteral.png" alt="Resolve URI Literals" width="60%" align="right"/>

This plugin provides integration with the following IntelliJ features:
1.  Resolving URI string literal, function, and variable references;
1.  Code folding;
1.  Find usages;
1.  Paired brace matching;
1.  Commenting code.

#### Run Configurations

<img src="images/run-query.png" alt="Running Queries" width="50%" align="right"/>

This plugin supports running the following query types on the supported databases and processors:

1.  BaseX &ndash; XQuery
1.  eXist-db &ndash; XQuery
1.  MarkLogic &ndash; XQuery, XSLT, SPARQL, SQL, JavaScript
1.  Saxon &ndash; XPath, XQuery, XSLT

MarkLogic also supports profiling XQuery and XSLT queries.

-----

Copyright (C) 2016-2019 Reece H. Dunn

This software and document includes material copied from or derived from the
XPath and XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE) license.
