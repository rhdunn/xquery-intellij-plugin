/*
 * Copyright (C) 2021 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xdm.xml

import javax.xml.namespace.QName

// region Accessors (5.1) attributes

fun XmlAccessors.attribute(node: Any, namespaceUri: String, localName: String): Any? {
    return attributes(node).find { hasNodeName(it, namespaceUri, localName) }
}

fun XmlAccessors.attributeStringValue(node: Any, namespaceUri: String, localName: String): String? {
    return attribute(node, namespaceUri, localName)?.let { stringValue(it) }
}

// endregion
// region Accessors (5.10) node-name

fun XmlAccessors.qname(node: Any): QName? = namespaceUri(node)?.let { QName(it, localName(node)) }

fun XmlAccessors.hasNodeName(node: Any, namespaceUri: String, localName: String): Boolean {
    return localName(node) == localName && namespaceUri(node) == namespaceUri
}

fun XmlAccessors.hasNodeName(node: Any, namespaceUri: String, localName: Set<String>): Boolean {
    return localName.contains(localName(node)) && namespaceUri(node) == namespaceUri
}

// endregion
