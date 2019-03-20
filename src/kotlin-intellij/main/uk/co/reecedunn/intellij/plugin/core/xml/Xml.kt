/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private class NodeListIterator(val nodes: NodeList) : Iterator<Node> {
    private var current: Int = 0

    override fun hasNext(): Boolean = current != nodes.length

    override fun next(): Node = nodes.item(current++)
}

fun NodeList.asSequence(): Sequence<Node> = NodeListIterator(this).asSequence()

fun <E> NodeList.elements(map: (Element) -> E): Sequence<E> = asSequence().filterIsInstance<Element>().map(map)

class XmlElement(val element: Element) {
    fun children(): Sequence<XmlElement> {
        return element.childNodes.elements { XmlElement(it) }
    }

    fun children(localname: String): Sequence<XmlElement> {
        return element.getElementsByTagName(localname).elements { XmlElement(it) }
    }

    fun children(namespace: String, localname: String): Sequence<XmlElement> {
        return element.getElementsByTagNameNS(namespace, localname).elements { XmlElement(it) }
    }

    val firstChild: Node? = element.firstChild

    fun getAttribute(attribute: String): String? = element.getAttribute(attribute)

    fun getAttributeNS(ns: String, attribute: String): String? = element.getAttributeNS(ns, attribute)

    fun appendChild(child: Node): Node? = element.appendChild(child)
}

class XmlDocument internal constructor(val doc: Document) {
    val root: XmlElement = XmlElement(doc.documentElement)

    fun toXmlString(): String {
        val writer = StringWriter()
        XmlDocument.formatter.transform(DOMSource(doc), StreamResult(writer))
        return writer.buffer.toString()
    }

    companion object {
        private val builder by lazy {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            factory.newDocumentBuilder()
        }

        private val formatter by lazy {
            val factory = TransformerFactory.newInstance()
            factory.newTransformer()
        }

        fun parse(xml: String): XmlDocument = XmlDocument(builder.parse(InputSource(StringReader(xml))))
    }
}
