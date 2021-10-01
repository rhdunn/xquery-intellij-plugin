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
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.frame.presentation.XErrorValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.QueryResultsValue
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.presentation.QueryValuePresentation
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsQName
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons

class MarkLogicVariable private constructor(
    val variableName: XsQNameValue,
    var value: Any?,
    private val evaluator: XDebuggerEvaluator?
) : XNamedValue("\$${qname_presentation(variableName)!!}") {

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        val presentation = createPresentation(value as? String)
        if (presentation != null) {
            node.setPresentation(XPathIcons.Nodes.Variable, presentation, false)
        } else {
            evaluator?.evaluate(evaluationExpression, object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(result: XValue) {
                    value = result
                    (result as? QueryResultsValue)?.icon = XPathIcons.Nodes.Variable
                    result.computePresentation(node, place)
                }

                override fun errorOccurred(errorMessage: String) {
                    node.setPresentation(XPathIcons.Nodes.Variable, XErrorValuePresentation(errorMessage), false)
                }
            }, null)
        }
    }

    private fun createPresentation(value: String?): XValuePresentation? = when {
        value == null -> null
        value == "()" -> QueryValuePresentation.EmptySequence
        value.startsWith("(") -> null // Compute the children in the sequence, and the sequence type.
        value.startsWith("<!--") -> QueryValuePresentation.forValue(value, "comment()")
        value.startsWith("<?") -> QueryValuePresentation.forValue(value, "processing-instruction()")
        value.startsWith("<") -> QueryValuePresentation.forValue(value, "element()")
        value.startsWith("\"") -> QueryValuePresentation.forValue(value.substring(1, value.length - 1), "xs:string")
        value.startsWith("document{") -> QueryValuePresentation.forValue(value, "document-node()")
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
        value.matches(NODE) -> {
            val matched = NODE.find(value)!!
            QueryValuePresentation.forValue(value, "${matched.groupValues[1]}()")
        }
        else -> QueryValuePresentation.forValue(value)
    }

    override fun getEvaluationExpression(): String = name

    override fun computeChildren(node: XCompositeNode) {
        (value as? XValue)?.computeChildren(node)
    }

    @Suppress("RegExpAnonymousGroup")
    companion object {
        private val CONSTRUCTED_FROM_STRING = "^([a-zA-Z\\-]+:[a-zA-Z\\-]+)\\(\"([^\"]+)\"\\)$".toRegex()
        private val CONSTRUCTED = "^([a-zA-Z\\-]+:[a-zA-Z\\-]+)\\(.*$".toRegex()
        private val NODE = "^([a-zA-Z\\-]+)\\{.*$".toRegex()
        private val XS_BOOLEAN = "^fn:(true|false)\\(\\)$".toRegex()
        private val XS_DECIMAL = "^[-]?([0-9]+\\.[0-9]*|\\.[0-9]+)$".toRegex()
        private val XS_INTEGER = "^[-]?[0-9]+$".toRegex()

        fun create(variable: XmlElement, evaluator: XDebuggerEvaluator? = null): MarkLogicVariable {
            val name = variable.child("*:name")
            val localName = XsNCName(name?.text()!!)
            val namespace = name.element.namespaceURI?.nullize()?.let {
                XsAnyUri(it, XdmUriContext.Namespace, XdmModuleType.XPATH_OR_XQUERY)
            }
            val prefix = variable.child("dbg:prefix")?.text()?.let { XsNCName(it) }
            val qname = XsQName(namespace, prefix, localName, prefix != null || namespace == null)
            val value = variable.child("dbg:value")?.text()?.nullize()
            return MarkLogicVariable(qname, value, evaluator)
        }
    }
}
