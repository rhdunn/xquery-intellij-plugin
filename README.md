[![Build Status](https://github.com/rhdunn/xquery-intellij-plugin/workflows/build/badge.svg)](https://github.com/rhdunn/xquery-intellij-plugin/actions)
[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![GitHub Issues](https://img.shields.io/github/issues/rhdunn/xquery-intellij-plugin.svg)](https://github.com/rhdunn/xquery-intellij-plugin/issues)

<img src="images/xquery-intellij-plugin.png" alt="Syntax Highlighting" width="60%" align="right"/>

## XQuery and XSLT

This is a plugin for the IntelliJ IDE that adds support for the XML Query (XQuery) and
XML Path (XPath) languages. This covers support for:
1.  XPath 2.0, 3.0, and 3.1;
1.  XQuery 1.0, 3.0, and 3.1;
1.  XQuery and XPath Full Text extension;
1.  XQuery Update Facility 1.0, and 3.0 extension;
1.  XQuery Scripting extension;
1.  EXPath extensions;
1.  BaseX, MarkLogic, and Saxon vendor extensions.

This plugin also has limited support for the following XML-based
languages that use XPath:
1.  XSLT 1.0-3.0 (active when the IntelliJ XPathView plugin is disabled);
1.  XProc 1.0-3.0.

See https://rhdunn.github.io/xquery-intellij-plugin/ for the plugin documentation
and tutorials.

The latest development version of this plugin supports IntelliJ 2020.3 &ndash; 2021.3 EAP.
Older versions of the plugin are compatible with older versions of IntelliJ.

### Query Processor and Database Integration

This plugin provides support for the following implementations of XQuery:
1.  BaseX 7.0 &ndash; 9.3;
1.  eXist-db 4.4 &ndash; 5.3;
1.  FusionDB alpha;
1.  MarkLogic 8.0 &ndash; 10.0;
1.  Saxon 9.2 &ndash; 10.0.

For those XQuery implementations, this plugin supports:
1.  Running XQuery, XSLT, XPath, SPARQL, SQL, and JavaScript queries where
    supported by the implementation;
1.  Profiling XQuery and XSLT queries;
1.  Debugging MarkLogic XQuery-based queries, with expression breakpoint
    support;
1.  Viewing access and error log files.

This plugin provides additional integration support for the following query
processor file types and standards:
1.  MarkLogic rewriter XML files;
1.  EXQuery RESTXQ 1.0.

### Libraries and Frameworks

This plugin adds support for the following project frameworks:
1.  MarkLogic Roxy &ndash; source root detection;
1.  MarkLogic ml-gradle &ndash; source root detection.
1.  Support running and profiling XRay unit tests.

### IntelliJ Integration

This plugin provides support for the following IntelliJ features:
1.  Resolving URI string literal, function, and variable references;
1.  Code folding;
1.  Find usages and semantic usage highlighting;
1.  Rename refactoring for variables;
1.  Code completion;
1.  Parameter information;
1.  Parameter inlay hints;
1.  Structure view;
1.  Breadcrumb navigation, including highlighting XML tags in the editor like
    the IntelliJ XML plugin;
1.  Paired brace matching;
1.  Commenting code;
1.  Integrated function documentation ("Quick Documentation", Ctrl+Q);
1.  Context information (Alt+Q) for XQuery function declarations;
1.  Spellchecking support with bundle dictionaries with XPath, XQuery, and XSLT
    terms.
1.  Language injection support on various elements, including string literals.

The plugin also supports the following IntelliJ Ultimate features:
1.  Support displaying MarkLogic rewriter files in the Endpoints tool window;
1.  Support displaying EXQuery RESTXQ endpoints in the Endpoints tool window.

-----

Copyright (C) 2016-2021 Reece H. Dunn

This software and document includes material copied from or derived from the
XPath and XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE) license.
