/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import org.w3c.dom.Node
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.toStreamSource

class XdmNode(`object`: Any, `class`: Class<*>) : XdmItem(`object`, `class`) {
    companion object {
        fun newInstance(value: Any?, processor: Processor): XdmItem {
            val builder = processor.newDocumentBuilder()
            return when (value) {
                is XmlDocument -> builder.wrap(value.doc)
                is XmlElement -> builder.wrap(value.element)
                is Node -> builder.wrap(value)
                else -> builder.build(value.toString().toStreamSource())
            }
        }
    }
}
