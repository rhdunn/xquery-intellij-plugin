[![Build Status](https://github.com/rhdunn/xquery-intellij-plugin/workflows/build/badge.svg)](https://github.com/rhdunn/xquery-intellij-plugin/actions)
[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/8612-xquery-intellij-plugin.svg)](https://plugins.jetbrains.com/plugin/8612-xquery-intellij-plugin)
[![GitHub Issues](https://img.shields.io/github/issues/rhdunn/xquery-intellij-plugin.svg)](https://github.com/rhdunn/xquery-intellij-plugin/issues)

<img src="images/xquery-intellij-plugin.png" alt="Syntax Highlighting" width="60%" align="right"/>

- [XQuery and XSLT](#xquery-and-xslt)
  - [Overview](#overview)
  - [Query Processor and Database Integration](#query-processor-and-database-integration)
  - [Libraries and Frameworks](#libraries-and-frameworks)
  - [IntelliJ Integration](#intellij-integration)
- [Building](#building)
  - [Gradle](#gradle)
  - [Java](#java)
  - [IntelliJ](#intellij)
- [License](#license)

## XQuery and XSLT

### Overview

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

The latest development version of this plugin supports IntelliJ 2024.1 &ndash; 2025.3.
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

## Building

### Gradle

This project uses `gradle`. It requires gradle 8.5 or later. You can then use:

- `gradle ...`.

If you open the project in IntelliJ it will install and configure the gradle wrapper
for you. You can then use:

- `./gradlew ...` if using a bash or similar shell;
- `gradlew.bat ...` if using Windows.

### Java

The Java version depends on the version of IntelliJ being targeted:

- IntelliJ 2022.3 - 2024.1 require Java 17;
- IntelliJ 2024.2 - 2025.2 require Java 21.

In IntelliJ you need to specify the `Gradle JVM` property (File | Settings |
Build, Execution, Deployment | Build Tools | Gradle) to that Java version.

### IntelliJ

The version of IntelliJ to build can be configured as follows:

| Environment Variable | System Property    | Default  | Description             |
|----------------------|--------------------|----------|-------------------------|
| `IDEA_TYPE`          | `platform.type`    | `IU`     | IntelliJ platform type. |
| `IDEA_VERSION`       | `platform.version` | `2025.1` | IntelliJ version.       |

The following are some useful gradle tasks:

- `clean` -- clear previous build artifacts;
- `:buildPlugin` -- build the plugin to the `build/distributions` directory;
- `:runIde` -- run an instance of the specified IntelliJ IDE with the plugin installed;
- `check` -- run the tests;
- `:verifyPlugin` -- run the IntelliJ plugin verifier to check for compatibility.

## License

Copyright (C) 2016-2025 Reece H. Dunn

This software and document includes material copied from or derived from the
XPath and XQuery specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio,
Beihang).

The IntelliJ XQuery Plugin is licensed under the [Apache 2.0](LICENSE) license.
