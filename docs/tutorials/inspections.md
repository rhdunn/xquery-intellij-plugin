---
layout: page
title: Inspections
image:
  url: /images/tutorials/inspections.png
  width: 813px
  height: 314px
  alt: Inspections
---

To configure the XQuery inspections (warnings and errors), you can:

1.  Select the *File* menu.
1.  Select the *Settings* menu.
1.  In the left navigation pane of the settings menu, select and expand the
    *Editor* item.
1.  Select the *Inspections* item.
1.  In the inspections list, locate and expand the *XPath and XQuery* entry.

To configure an inspection:

1.  Enable or disable the inspection by clicking on the checkbox to the right
    of the inspection name in the list.
1.  The description box on the right provides a detailed description of the
    inspection. __NOTE:__ This describes both what the plugin is checking for
    and what the behaviour will be when running the query on a XQuery processor,
    which may vary between processors.
1.  You can change the severity (error, warning, etc.) by selecting the severity
    dropdown below the inspection description on the right.

Once you are finished, press the *Apply* button to make the changes and keep
the dialog open, or *OK* to make the changes and close the dialog.

### Inspections

The inspections use the name identifier format used by the XPath and XQuery
error conditions. These are grouped by the four letters at the start of the
identifier.

The XQuery IntelliJ plugin defines several custom inspections in the following
groups:

1.  *IJVS* -- IntelliJ vendor specific warnings and errors. These control the
    reporting of vendor syntax extensions that cannot be done in the parser.

The XPath and XQuery specifications define many error conditions in the
following groups:

1.  *XPST* -- XPath static errors.
1.  *XQST* -- XQuery static errors.

__NOTE:__ Not all possible error condition groups are listed here, only the
ones that are supported by the plugin.
