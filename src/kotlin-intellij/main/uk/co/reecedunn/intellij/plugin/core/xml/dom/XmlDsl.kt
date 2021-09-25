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

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.StringWriter
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

val Document.xml: String
    get() {
        val writer = StringWriter()
        XmlFormatter.format(DOMSource(this), StreamResult(writer))
        return writer.buffer.toString()
    }

fun document(init: Document.() -> Unit): Document {
    val doc = XmlBuilder.newDocument()
    doc.init()
    return doc
}

fun Document.element(namespaceUri: String, localname: String, init: Element.() -> Unit) {
    val e = createElementNS(namespaceUri, localname)
    e.init()
    appendChild(e)
}

fun Node.element(namespaceUri: String, localname: String, init: Element.() -> Unit) {
    val e = ownerDocument.createElementNS(namespaceUri, localname)
    e.init()
    appendChild(e)
}

fun Element.cdata(text: String) {
    appendChild(ownerDocument.createCDATASection(text))
}

fun Element.text(text: String) {
    appendChild(ownerDocument.createTextNode(text))
}
