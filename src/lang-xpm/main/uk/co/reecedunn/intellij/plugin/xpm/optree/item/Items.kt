// Copyright (C) 2021, 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.optree.item

import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpm.context.expand

// region XQuery and XPath 3.1 Data Model (2.7.4) : attribute()

val XdmAttributeNode.namespacePrefix: String?
    get() = nodeName?.let { it.prefix?.data ?: "" }

val XdmAttributeNode.namespaceUri: String?
    get() = nodeName?.expand()?.firstOrNull()?.namespace?.data

val XdmAttributeNode.localName: String?
    get() = nodeName?.localName?.data

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : element()

val XdmElementNode.namespacePrefix: String?
    get() = nodeName?.let { it.prefix?.data ?: "" }

val XdmElementNode.namespaceUri: String?
    get() = nodeName?.expand()?.firstOrNull()?.namespace?.data

val XdmElementNode.localName: String?
    get() = nodeName?.localName?.data

fun XdmElementNode.getAttribute(ns: String, localName: String): XdmAttributeNode? {
    return attributes.find { it.localName == localName && it.namespaceUri == ns }
}

fun XdmElementNode.getAttributeValue(ns: String, localName: String): String? {
    return attributes.find { it.localName == localName && it.namespaceUri == ns }?.stringValue
}

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : map(*)

val XpmMapEntry.keyName: String?
    get() = when (val name = keyExpression) {
        is XsNCNameValue -> name.data
        else -> null
    }

// endregion
