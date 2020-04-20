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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger

import com.intellij.util.text.nullize
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.presentation.QueryValuePresentation
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsQName

class MarkLogicVariable private constructor(val variableName: XsQNameValue, val value: String?) :
    XNamedValue("\$${op_qname_presentation(variableName)!!}") {

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        createPresentation()?.let {
            node.setPresentation(XPathIcons.Nodes.Variable, it, false)
        }
    }

    private fun createPresentation(): XValuePresentation? = when {
        value == null -> null
        value.startsWith("(") -> QueryValuePresentation.forValue(value, "item()+")
        value.startsWith("<!--") -> QueryValuePresentation.forValue(value, "comment()")
        value.startsWith("<") -> QueryValuePresentation.forValue(value, "element()")
        value.startsWith("\"") -> QueryValuePresentation.forValue(value.substring(1, value.length - 1), "xs:string")
        value.startsWith("text{\"") -> QueryValuePresentation.forValue(value.substring(6, value.length - 2), "text()")
        value.matches(XS_BOOLEAN) -> QueryValuePresentation.forValue(value, "xs:boolean")
        value.matches(XS_DECIMAL) -> QueryValuePresentation.forValue(value, "xs:decimal")
        value.matches(XS_INTEGER) -> QueryValuePresentation.forValue(value, "xs:integer")
        value.matches(CONSTRUCTED_FROM_STRING) -> {
            val matched = CONSTRUCTED_FROM_STRING.matchEntire(value)!!
            QueryValuePresentation.forValue(matched.groupValues[2], matched.groupValues[1])
        }
        value.matches(CONSTRUCTED) -> {
            val matched = CONSTRUCTED.find(value)!!
            QueryValuePresentation.forValue(value, matched.groupValues[1])
        }
        else -> QueryValuePresentation.forValue(value)
    }

    companion object {
        private val CONSTRUCTED_FROM_STRING = "^([a-zA-Z\\-]+:[a-zA-Z\\-]+)\\(\"([^\"]+)\"\\)$".toRegex()
        private val CONSTRUCTED = "^([a-zA-Z\\-]+:[a-zA-Z\\-]+)\\(.*$".toRegex()
        private val XS_BOOLEAN = "^fn:(true|false)\\(\\)$".toRegex()
        private val XS_DECIMAL = "^[-]?([0-9]+\\.[0-9]*|\\.[0-9]+)$".toRegex()
        private val XS_INTEGER = "^[-]?[0-9]+$".toRegex()

        fun create(variable: XmlElement): MarkLogicVariable {
            val localName = XsNCName(variable.child("name")?.text()!!)
            val namespace = variable.child("name")?.element?.namespaceURI?.nullize()?.let {
                XsAnyUri(it, XdmUriContext.Namespace, XdmModuleType.XPATH_OR_XQUERY)
            }
            val prefix = variable.child("prefix")?.text()?.let { XsNCName(it) }
            val qname = XsQName(namespace, prefix, localName, prefix != null || namespace == null)
            val value = variable.child("value")?.text()?.nullize()
            return MarkLogicVariable(qname, value)
        }
    }
}
