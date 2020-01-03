/*
 * Copyright (C) 2017-2020 Reece H. Dunn
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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
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

fun String.toQName(namespaces: Map<String, String>): QName {
    return when {
        contains(':') -> split(":").let { QName(namespaces[it[0]], it[1], it[0]) }
        else -> QName(this)
    }
}

class XmlElement(val element: Element, private val namespaces: Map<String, String>) {
    fun children(): Sequence<XmlElement> {
        return element.childNodes.elements { XmlElement(it, namespaces) }
    }

    fun children(qname: String): Sequence<XmlElement> = children(qname.toQName(namespaces))

    fun children(qname: QName): Sequence<XmlElement> {
        return when {
            qname.namespaceURI.isEmpty() -> element.getElementsByTagName(qname.localPart)
            else -> element.getElementsByTagNameNS(qname.namespaceURI, qname.localPart)
        }.elements { XmlElement(it, namespaces) }
    }

    fun child(qname: String): XmlElement? = children(qname).firstOrNull()

    fun child(qname: QName): XmlElement? = children(qname).firstOrNull()

    fun text(): String? = element.firstChild?.nodeValue

    fun attribute(qname: String): String? = attribute(qname.toQName(namespaces))

    fun attribute(qname: QName): String? {
        return when {
            qname.namespaceURI.isEmpty() -> element.getAttribute(qname.localPart)?.nullize()
            else -> element.getAttributeNS(qname.namespaceURI, qname.localPart)?.nullize()
        }
    }

    fun appendChild(child: Node): Node? = element.appendChild(child)

    fun `is`(qname: String): Boolean = `is`(qname.toQName(namespaces))

    fun `is`(qname: QName): Boolean {
        return element.localName == qname.localPart && element.namespaceURI == qname.namespaceURI
    }

    fun xml(): String {
        val buffer = StringWriter()
        transformer.transform(DOMSource(element), StreamResult(buffer))
        return buffer.toString()
    }

    fun innerXml(): String {
        val buffer = StringWriter()
        val result = StreamResult(buffer)
        element.childNodes.asSequence().forEach {
            transformer.transform(DOMSource(it), result)
        }
        return buffer.toString()
    }

    companion object {
        private val transformer by lazy {
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            transformer
        }
    }
}

class XmlDocument internal constructor(val doc: Document, namespaces: Map<String, String>) {
    val root: XmlElement = XmlElement(doc.documentElement, namespaces)

    fun toXmlString(): String {
        val writer = StringWriter()
        formatter.transform(DOMSource(doc), StreamResult(writer))
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

        fun parse(xml: String, namespaces: Map<String, String>): XmlDocument {
            return parse(InputSource(StringReader(xml)), namespaces)
        }

        fun parse(file: VirtualFile, namespaces: Map<String, String>): XmlDocument {
            return parse(InputSource(file.inputStream), namespaces)
        }

        fun parse(xml: InputSource, namespaces: Map<String, String>): XmlDocument {
            return XmlDocument(builder.parse(xml), namespaces)
        }
    }
}
