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

import com.intellij.xdebugger.frame.XFullValueEvaluator
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicVariable
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import javax.swing.Icon

@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Debugger - Variables")
class MarkLogicVariableTest : XValueNode {
    companion object {
        val DEBUG_XML_NAMESPACES: Map<String, String> = mapOf("dbg" to "http://marklogic.com/xdmp/debug")
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
        it.renderValue(ValueTextRenderer)
        ValueTextRenderer.rendered
    }

    override fun setFullValueEvaluator(fullValueEvaluator: XFullValueEvaluator): Unit = TODO()

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
            assertThat(v.name, `is`("\$ipsum"))
            assertThat(v.evaluationExpression, `is`("\$ipsum"))

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
            assertThat(v.name, `is`("\$lorem:ipsum"))
            assertThat(v.evaluationExpression, `is`("\$lorem:ipsum"))

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
            assertThat(v.name, `is`("\$Q{http://www.example.co.uk/test}ipsum"))
            assertThat(v.evaluationExpression, `is`("\$Q{http://www.example.co.uk/test}ipsum"))

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
        private fun check_value(value: String, type: String?, presentationValue: String, presentationClass: Class<*>?) {
            val escapedValue = value.replace("&", "&amp;").replace("<", "&lt;")

            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="">x</name>
                    <prefix/>
                    <value>$escapedValue</value>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)

            computePresentation(v, XValuePlace.TREE)
            if (presentationClass == null) {
                assertThat(icon, `is`(nullValue()))
                assertThat(presentation, `is`(nullValue()))
            } else {
                assertThat(icon, `is`(sameInstance(XPathIcons.Nodes.Variable)))
                assertThat(presentation, `is`(instanceOf(presentationClass)))
                assertThat(presentation?.type, `is`(type))
                assertThat(presentation?.separator, `is`(" := "))
                assertThat(renderValue(), `is`(presentationValue))
            }
            assertThat(hasChildren, `is`(false))

            computePresentation(v, XValuePlace.TOOLTIP)
            if (presentationClass == null) {
                assertThat(icon, `is`(nullValue()))
                assertThat(presentation, `is`(nullValue()))
            } else {
                assertThat(icon, `is`(sameInstance(XPathIcons.Nodes.Variable)))
                assertThat(presentation, `is`(instanceOf(presentationClass)))
                assertThat(presentation?.type, `is`(type))
                assertThat(presentation?.separator, `is`(" := "))
                assertThat(renderValue(), `is`(presentationValue))
            }
            assertThat(hasChildren, `is`(false))
        }

        private fun check_value(value: String, type: String?, presentationClass: Class<*>?) {
            check_value(value, type, value, presentationClass)
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
        @DisplayName("cts:and-query")
        fun andQuery() {
            check_value("cts:and-query((), ())", "cts:and-query", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("map:map")
        fun map() {
            check_value("map:map()", "map:map", XRegularValuePresentation::class.java)

            check_value(
                "map:map(<map:map xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" .../>)",
                "map:map",
                XRegularValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:anyURI")
        fun anyURI() {
            check_value(
                "xs:anyURI(\"http://www.example.co.uk\")", "xs:anyURI", "http://www.example.co.uk",
                XRegularValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:byte")
        fun byte() {
            check_value("xs:byte(\"123\")", "xs:byte", "123", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:boolean")
        fun boolean() {
            check_value("fn:true()", "xs:boolean", XRegularValuePresentation::class.java)
            check_value("fn:false()", "xs:boolean", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:date")
        fun date() {
            check_value("xs:date(\"2001-03-22\")", "xs:date", "2001-03-22", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:decimal")
        fun decimal() {
            check_value("1234.", "xs:decimal", XNumericValuePresentation::class.java)
            check_value("-321.", "xs:decimal", XNumericValuePresentation::class.java)

            check_value("12.34", "xs:decimal", XNumericValuePresentation::class.java)
            check_value("-3.21", "xs:decimal", XNumericValuePresentation::class.java)

            check_value(".1234", "xs:decimal", XNumericValuePresentation::class.java)
            check_value("-.321", "xs:decimal", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:double")
        fun double() {
            check_value("xs:double(\"2.99e8\")", "xs:double", "2.99e8", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:float")
        fun float() {
            check_value("xs:float(\"2.99e8\")", "xs:float", "2.99e8", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:hexBinary")
        fun hexBinary() {
            check_value("xs:hexBinary(\"ABCD\")", "xs:hexBinary", "ABCD", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:int")
        fun int() {
            check_value("xs:int(\"1234\")", "xs:int", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:integer")
        fun integer() {
            check_value("1234", "xs:integer", XNumericValuePresentation::class.java)
            check_value("-321", "xs:integer", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:language")
        fun language() {
            check_value("xs:language(\"fr-BE\")", "xs:language", "fr-BE", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:long")
        fun long() {
            check_value("xs:long(\"1234\")", "xs:long", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:negativeInteger")
        fun negativeInteger() {
            check_value(
                "xs:negativeInteger(\"1234\")", "xs:negativeInteger", "1234", XNumericValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:nonNegativeInteger")
        fun nonNegativeInteger() {
            check_value(
                "xs:nonNegativeInteger(\"1234\")", "xs:nonNegativeInteger", "1234",
                XNumericValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:nonPositiveInteger")
        fun nonPositiveInteger() {
            check_value(
                "xs:nonPositiveInteger(\"-1234\")", "xs:nonPositiveInteger", "-1234",
                XNumericValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:normalizedString")
        fun normalizedString() {
            check_value(
                "xs:normalizedString(\"Lorem &amp; ipsum\")", "xs:normalizedString", "Lorem &amp; ipsum",
                XStringValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:positiveInteger")
        fun positiveInteger() {
            check_value(
                "xs:positiveInteger(\"1234\")", "xs:positiveInteger", "1234", XNumericValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:short")
        fun short() {
            check_value("xs:short(\"1234\")", "xs:short", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:string")
        fun string() {
            check_value(
                "\"Lorem &amp; ipsum\"", "xs:string", "Lorem &amp; ipsum", XStringValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("xs:token")
        fun token() {
            check_value("xs:token(\"test\")", "xs:token", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:unsignedByte")
        fun unsignedByte() {
            check_value("xs:unsignedByte(\"123\")", "xs:unsignedByte", "123", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:unsignedInt")
        fun unsignedInt() {
            check_value("xs:unsignedInt(\"1234\")", "xs:unsignedInt", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:unsignedLong")
        fun unsignedLong() {
            check_value("xs:unsignedLong(\"1234\")", "xs:unsignedLong", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:unsignedShort")
        fun unsignedShort() {
            check_value("xs:unsignedShort(\"1234\")", "xs:unsignedShort", "1234", XNumericValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:ENTITY")
        fun entity() {
            check_value("xs:ENTITY(\"test\")", "xs:ENTITY", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:ID")
        fun id() {
            check_value("xs:ID(\"test\")", "xs:ID", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:IDREF")
        fun idref() {
            check_value("xs:IDREF(\"test\")", "xs:IDREF", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:Name")
        fun name() {
            check_value("xs:Name(\"test\")", "xs:Name", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:NCName")
        fun ncname() {
            check_value("xs:NCName(\"test\")", "xs:NCName", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("xs:NMTOKEN")
        fun nmtoken() {
            check_value("xs:NMTOKEN(\"test\")", "xs:NMTOKEN", "test", XStringValuePresentation::class.java)
        }

        @Test
        @DisplayName("array-node()")
        fun arrayNode() {
            check_value("array-node{}", "array-node()", XRegularValuePresentation::class.java)
            check_value("array-node{number-node{1}}", "array-node()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("attribute()")
        fun attribute() {
            check_value(
                "attribute{fn:QName(\"\",\"test\")}{\"123\"}", "attribute()", XRegularValuePresentation::class.java
            )
        }

        @Test
        @DisplayName("comment()")
        fun comment() {
            check_value("<!-- Lorem ipsum -->", "comment()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("document-node()")
        fun documentNode() {
            check_value("document{<lorem>ipsum</lorem>}", "document-node()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("element()")
        fun element() {
            check_value("<lorem>ipsum</lorem>", "element()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("empty-sequence()")
        fun emptySequence() {
            check_value("()", "empty-sequence()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("object-node()")
        fun objectNode() {
            check_value("object-node{}", "object-node()", XRegularValuePresentation::class.java)
            check_value("object-node{\"one\":number-node{1}}", "object-node()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("processing-instruction()")
        fun processingInstruction() {
            check_value("<?test Lorem ipsum?>", "processing-instruction()", XRegularValuePresentation::class.java)
        }

        @Test
        @DisplayName("item()+")
        fun sequence() {
            check_value("(1, 2, 3)", null, null)
            check_value("(3, 6, 9, ...)", null, null)
        }

        @Test
        @DisplayName("text()")
        fun text() {
            check_value(
                "text{\"Lorem &amp; ipsum\"}", "text()", "Lorem &amp; ipsum", XStringValuePresentation::class.java
            )
        }
    }
}
