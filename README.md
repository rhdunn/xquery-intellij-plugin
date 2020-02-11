[![Build Status](https://travis-ci.org/rhdunn/xquery-intellij-plugin.svg?branch=master)](https://travis-ci.org/rhdunn/xquery-intellij-plugin/master)
[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/rhdunn/xquery-intellij-plugin.svg)](https://github.com/rhdunn/xquery-intellij-plugin/issues)

<img src="images/xquery-intellij-plugin.png" alt="Syntax Highlighting" width="60%" align="right"/>

## IntelliJ XQuery Plugin

This is a plugin for IntelliJ IDE 2019.1 &ndash; 2019.3 that adds support
for the XML Query (XQuery) language. This covers support for:
1.  XQuery 1.0, 3.0, and 3.1;
1.  XQuery and XPath Full Text extension;
1.  XQuery Update Facility 1.0, and 3.0 extension;
1.  XQuery Scripting extension;
1.  EXPath extensions;
1.  BaseX, MarkLogic, and Saxon vendor extensions.

See https://rhdunn.github.io/xquery-intellij-plugin/ for the plugin documentation
and tutorials.

### Query Processor and Database Integration

This plugin provides support for the following implementations of XQuery:
1.  BaseX 7.0 &ndash; 9.2;
1.  eXist-db 4.4 &ndash; 4.7;
1.  MarkLogic 8.0 &ndash; 10.0;
1.  Saxon 9.2 &ndash; 9.9.

For those XQuery implementations, this plugin supports:
1.  Running XQuery, XSLT, XPath, SPARQL, SQL, ans JavaScript queries where
    supported by the implementation;
1.  Profiling XQuery and XSLT queries;
1.  Viewing access and error log files.

### IntelliJ Integration

This plugin provides integration with the following IntelliJ features:
1.  Resolving URI string literal, function, and variable references;
1.  Code folding;
1.  Find usages;
1.  Rename refactoring for variables;
1.  Code completion;
1.  Parameter information;
1.  Parameter inlay hints;
1.  Structure view;
1.  Breadcrumb navigation;
1.  Paired brace matching;
1.  Commenting code;
1.  Integrated function documentation ("Quick Documentation", Ctrl+Q);
1.  Bundle a dictionary with XPath terms.

-----

Copyright (C) 2016-2020 Reece H. Dunn

This software and document includes material copied from or derived from the
XPath and XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE) license.
