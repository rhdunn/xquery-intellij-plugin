---
layout: page
title: XQuery settings
image:
  url: /images/tutorials/xquery-settings.png
  width: 811px
  height: 291px
  alt: XQuery settings
---

To configure the syntax highlighting, you can:

1.  Select the *File* menu.
1.  Select the *Settings* menu.
1.  In the left navigation pane of the settings menu, select and expand the
    *Languages & Frameworks* item.
1.  Select the *XQuery* item.

Once you are finished, press the *Apply* button to make the changes and keep
the dialog open, or *OK* to make the changes and close the dialog.

__NOTE:__ Inspections such as the *XPST0081: Unbound namespace prefix* inspection
don't update on the open files after making changes to these settings. To update
them you need to close the file then reopen it.

### Built-in namespaces and functions

You can configure which static context (built-in namespaces and functions) the
plugin will use by:
1.  Setting the *Implementation* to the XQuery vendor you want the static context
    to match. For the *Saxon* implementation, you can select between the *Home*,
    *Professional*, and *Enterprise* editions. __NOTE:__ The *W3C Specifications*
    option is for the standard XQuery namespaces and functions.
1.  Setting the *Implementation version* to the version that matches the version
    you are targetting.

__NOTE:__ *Implementation version* only currently effects XQuery syntax. It is
intended to also verify namespace and function usage, but those checks are not
yet implemented.

__NOTE:__ To have errors about unknown functions, you need to enable the
*XPST0017: Undefined function* inspection in this settings dialog. This inspection
is not enabled by default as there are some cases where locating functions does
not work.

### XQuery syntax
The *Implementation* option defines which XQuery dialects are available.

The *Implementation version* option determines which syntax extensions are valid,
based on which version the extension was introduced.

The *Default XQuery version* option specifies which version of XQuery to use if
there is no version declaration in the XQuery file.

The *Dialect for* options specify which syntax extensions to enable for the given
version of XQuery. __NOTE:__ There are no settings for the MarkLogic `0.9-ml` and
`1.0-ml` XQuery versions, as those always use the MarkLogic dialect. See the note
below for more information.

The possible dialects, depending on which *Implementation* and *Implementation
version* have been specified, are:
1.  *BaseX* -- The BaseX syntax extensions, Full Text extensions, and Update
    Facility extensions.
1.  *eXist-db* -- The eXist-db syntax extension. __NOTE:__ There is currently
    only a partial support for these syntax extensions.
1.  *Saxon* -- The Saxon syntax extensions, and Update Facility extensions.
1.  *XQuery* -- The core XQuery syntax without any extensions.
1.  *XQuery and XPath Full Text*.
1.  *XQuery Update Facility*.
1.  *XQuery Scripting Extension* -- Update Facility extensions, and Scripting
    extensions.

__NOTE:__ The MarkLogic syntax extensions depend on the specified XQuery version.
As such, there is no *MarkLogic* option in the *Dialect for* settings. The XQuery
versions available for MarkLogic are:
1.  `0.9-ml` -- Uses the old 02 May 2003 draft XQuery syntax with MarkLogic
    extensions. __NOTE:__ The draft XQuery syntax is not yet supported.
1.  `1.0` -- The core XQuery 1.0 syntax without any extensions.
1.  `1.0-ml` -- The core XQuery 1.0 syntax with MarkLogic syntax extensions and
    some XQuery 3.0 and 3.1 syntax.
