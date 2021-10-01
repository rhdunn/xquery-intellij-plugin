/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml.dom

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import org.w3c.dom.*
import org.xml.sax.InputSource
import java.io.*
import javax.xml.namespace.QName
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private class NodeListIterator(val nodes: NodeList) : Iterator<Node> {
    private var current: Int = 0

    override fun hasNext(): Boolean = current != nodes.length

    override fun next(): Node = nodes.item(current++)
}

fun NodeList.asSequence(): Sequence<Node> = NodeListIterator(this).asSequence()

fun <E> NodeList.elements(map: (Element) -> E): Sequence<E> = asSequence().filterIsInstance<Element>().map(map)

class NamedNodeMapIterator(val nodes: NamedNodeMap) : Iterator<Node> {
    private var current: Int = 0

    override fun hasNext(): Boolean = current != nodes.length

    override fun next(): Node = nodes.item(current++)
}

inline fun <reified E> NamedNodeMap.asSequence(): Sequence<E> {
    return NamedNodeMapIterator(this).asSequence().filterIsInstance<E>()
}

fun String.toQName(namespaces: Map<String, String>): QName = when {
    startsWith("*:") -> split(":").let { QName(it[0], it[1], it[0]) }
    contains(':') -> split(":").let { QName(namespaces[it[0]], it[1], it[0]) }
    else -> QName(this)
}

class XmlElement(val element: Element, private val namespaces: Map<String, String>) {
    fun children(): Sequence<XmlElement> = element.childNodes.elements { XmlElement(it, namespaces) }

    fun children(qname: String): Sequence<XmlElement> = children(qname.toQName(namespaces))

    fun children(qname: QName): Sequence<XmlElement> = children().filter { it.`is`(qname) }

    fun child(qname: String): XmlElement? = children(qname).firstOrNull()

    fun child(qname: QName): XmlElement? = children(qname).firstOrNull()

    fun text(): String? = element.firstChild?.nodeValue

    val attributes: Sequence<Attr>
        get() = element.attributes.asSequence()

    fun attribute(qname: String): String? = attribute(qname.toQName(namespaces))

    fun attribute(qname: QName): String? = when {
        qname.namespaceURI.isEmpty() -> element.getAttribute(qname.localPart)?.nullize()
        else -> element.getAttributeNS(qname.namespaceURI, qname.localPart)?.nullize()
    }

    fun appendChild(child: Node): Node? = element.appendChild(child)

    fun `is`(qname: String): Boolean = `is`(qname.toQName(namespaces))

    fun `is`(qname: QName): Boolean = when (qname.namespaceURI) {
        "*" -> element.localName == qname.localPart
        else -> element.localName == qname.localPart && (element.namespaceURI ?: "") == qname.namespaceURI
    }

    fun xml(): String {
        val buffer = StringWriter()
        XmlFormatter.format(DOMSource(element), StreamResult(buffer), omitXmlDeclaration = true)
        return buffer.toString()
    }

    fun innerXml(): String {
        val buffer = StringWriter()
        val result = StreamResult(buffer)
        element.childNodes.asSequence().forEach { node ->
            XmlFormatter.format(DOMSource(node), result, omitXmlDeclaration = true)
        }
        return buffer.toString()
    }
}

class XmlDocument internal constructor(val doc: Document, namespaces: Map<String, String>) {
    val root: XmlElement = XmlElement(doc.documentElement, namespaces)

    fun save(file: File) {
        FileWriter(file).use {
            XmlFormatter.format(DOMSource(doc), StreamResult(it))
        }
    }

    companion object {
        fun parse(xml: String, namespaces: Map<String, String>): XmlDocument {
            return parse(InputSource(StringReader(xml)), namespaces)
        }

        fun parse(file: VirtualFile, namespaces: Map<String, String>): XmlDocument {
            return parse(InputSource(file.inputStream), namespaces)
        }

        fun parse(xml: InputStream, namespaces: Map<String, String>): XmlDocument {
            return parse(InputSource(xml), namespaces)
        }

        fun parse(xml: InputSource, namespaces: Map<String, String>): XmlDocument {
            return XmlDocument(XmlBuilder.parse(xml), namespaces)
        }
    }
}

fun Sequence<XmlElement>.children(name: String): Sequence<XmlElement> = this.flatMap { it.children(name) }

fun Sequence<XmlElement>.children(name: QName): Sequence<XmlElement> = this.flatMap { it.children(name) }
