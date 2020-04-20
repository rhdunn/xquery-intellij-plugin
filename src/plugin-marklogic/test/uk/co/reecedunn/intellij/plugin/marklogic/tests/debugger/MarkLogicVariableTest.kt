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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.debugger

import com.intellij.util.ui.TextTransferable
import com.intellij.xdebugger.frame.XFullValueEvaluator
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueTextRendererImpl
import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicVariable
import javax.swing.Icon

@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Debugger - Variables")
class MarkLogicVariableTest : XValueNode {
    companion object {
        val DEBUG_XML_NAMESPACES = mapOf("dbg" to "http://marklogic.com/xdmp/debug")
    }

    // region XValueNode

    private var icon: Icon? = null
    private var presentation: XValuePresentation? = null
    private var hasChildren: Boolean = false

    fun computePresentation(value: XNamedValue, place: XValuePlace) {
        icon = null
        presentation = null
        value.computePresentation(this, place)
    }

    fun renderValue(): String? = presentation?.let {
        val text = TextTransferable.ColoredStringBuilder()
        val renderer = XValueTextRendererImpl(text)
        it.renderValue(renderer)
        text.builder.toString()
    }

    override fun setFullValueEvaluator(fullValueEvaluator: XFullValueEvaluator) = TODO()

    override fun setPresentation(icon: Icon?, type: String?, value: String, hasChildren: Boolean) {
        setPresentation(icon, XRegularValuePresentation(value, type), hasChildren)
    }

    override fun setPresentation(icon: Icon?, presentation: XValuePresentation, hasChildren: Boolean) {
        this.icon = icon
        this.presentation = presentation
        this.hasChildren = hasChildren
    }

    override fun setPresentation(icon: Icon?, type: String?, separator: String, value: String?, hasChildren: Boolean) {
        TODO("Don't call this deprecated API.")
    }

    // endregion

    @Nested
    @DisplayName("variable name")
    internal inner class VariableName {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="">ipsum</name>
                    <prefix/>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.namespace, `is`(nullValue()))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(true))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="http://www.example.co.uk/test">ipsum</name>
                    <prefix>lorem</prefix>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("lorem:ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix!!.data, `is`("lorem"))
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk/test"))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(true))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="http://www.example.co.uk/test">ipsum</name>
                    <prefix/>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("Q{http://www.example.co.uk/test}ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk/test"))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(false))
        }
    }

    @Nested
    @DisplayName("values and types")
    internal inner class ValuesAndTypes {
        private fun check_value(value: String, type: String?, presentationClass: Class<*>) {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="">x</name>
                    <prefix/>
                    <value>$value</value>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)

            computePresentation(v, XValuePlace.TREE)
            assertThat(icon, `is`(sameInstance(XPathIcons.Nodes.Variable)))
            assertThat(presentation, `is`(instanceOf(presentationClass)))
            assertThat(presentation?.type, `is`(type))
            assertThat(renderValue(), `is`(value))
            assertThat(hasChildren, `is`(false))

            computePresentation(v, XValuePlace.TOOLTIP)
            assertThat(icon, `is`(sameInstance(XPathIcons.Nodes.Variable)))
            assertThat(presentation, `is`(instanceOf(presentationClass)))
            assertThat(presentation?.type, `is`(type))
            assertThat(renderValue(), `is`(value))
            assertThat(hasChildren, `is`(false))
        }

        @Test
        @DisplayName("computable value")
        fun computableValue() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="">x</name>
                    <prefix/>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)

            computePresentation(v, XValuePlace.TREE)
            assertThat(icon, `is`(nullValue()))
            assertThat(presentation, `is`(nullValue()))
            assertThat(hasChildren, `is`(false))

            computePresentation(v, XValuePlace.TOOLTIP)
            assertThat(icon, `is`(nullValue()))
            assertThat(presentation, `is`(nullValue()))
            assertThat(hasChildren, `is`(false))
        }

        @Test
        @DisplayName("xs:string")
        fun string() {
            check_value("\"test\"", null, XRegularValuePresentation::class.java)
        }
    }
}
