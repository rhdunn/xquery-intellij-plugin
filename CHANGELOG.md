# Change Log

## 1.9.0 - (In Development)

XPath, XQuery, and XSLT:

1. Better error recovery when an `AxisStep` is used in `CatchClause` or `xsl:nametests`.

eXist-db:

1. Fix returning all results from a query when there are more than 10 results.
1. Support error messages that return html pages.

MarkLogic:

1. Support running queries with lower privileges when the module root matches the server configuration.

Code Completion:

1. Fix completing `AtomicOrUnionType` names when a prefix is specified, and the
   default type namespace is the XMLSchema namespace.

Inspections:

1. Don't crash when the conformance element is empty (e.g. it is on a parser error).

Query Log Viewer:

1. Support colouring MarkLogic ErrorLog.txt, BaseX, and eXist-db log files based on log level.
1. Support navigating to files referenced in MarkLogic ErrorLog.txt exceptions.
1. Support filtering logs by the log level.
1. Remember the server, log file, and log level selection between sessions.
1. Fix displaying the log viewer when opening a new project into the IDE window.
1. Don't clear the log viewer when an error is from a previously selected query processor.
1. Update the server list when the query processors are modified.

## 2020

*  [1.8.0 - 2020-11-12](docs/_posts/2020-11-12-release-1.8.0.md)
*  [1.7.2 - 2020-08-31](docs/_posts/2020-08-31-release-1.7.2.md)
*  [1.7.1 - 2020-07-25](docs/_posts/2020-07-25-release-1.7.1.md)
*  [1.7 - 2020-07-10](docs/_posts/2020-07-10-release-1.7.md)
*  [1.6.2 - 2020-05-04](docs/_posts/2020-05-04-release-1.6.2.md)
*  [1.6.1 - 2020-04-09](docs/_posts/2020-04-09-release-1.6.1.md)
*  [1.6 - 2020-03-21](docs/_posts/2020-03-21-release-1.6.md)

## 2019

*  [1.5.2 - 2019-11-19](docs/_posts/2019-11-19-release-1.5.2.md)
*  [1.5.1 - 2019-08-01](docs/_posts/2019-08-01-release-1.5.1.md)
*  [1.5 - 2019-07-18](docs/_posts/2019-07-18-release-1.5.md)
*  [1.4.1 - 2019-03-27](docs/_posts/2019-03-27-release-1.4.1.md)
*  [1.4 - 2019-03-24](docs/_posts/2019-03-24-release-1.4.md)

## 2018

*  [1.3 - 2018-11-10](docs/_posts/2018-11-10-release-1.3.md)
*  [1.2 - 2018-08-27](docs/_posts/2018-08-27-release-1.2.md)
*  [1.1 - 2018-04-10](docs/_posts/2018-04-10-release-1.1.md)

## 2017

*  [1.0 - 2017-11-30](docs/_posts/2017-11-30-release-1.0.md)
*  [0.5 - 2017-07-18](docs/_posts/2017-07-18-release-0.5.md)
*  [0.4 - 2017-01-03](docs/_posts/2017-01-03-release-0.4.md)

## 2016

*  [0.3 - 2016-11-30](docs/_posts/2016-11-30-release-0.3.md)
*  [0.2.1 - 2016-11-05](docs/_posts/2016-11-05-release-0.2.1.md)
*  [0.2 - 2016-10-30](docs/_posts/2016-10-30-release-0.2.md)
*  [0.1 - 2016-09-10](docs/_posts/2016-09-10-release-0.1.md)
