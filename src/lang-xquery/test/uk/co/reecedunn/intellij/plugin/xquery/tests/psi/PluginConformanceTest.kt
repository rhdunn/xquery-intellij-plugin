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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("XQuery IntelliJ Plugin - Implementation Conformance Checks")
private class PluginConformanceTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("PluginConformanceTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; working draft syntax")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(3))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.WD_1_0_20030502))
            assertThat(versioned.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(versioned.requiresConformance[2], `is`(until(EXistDB.VERSION_4_0)))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    fun testSequenceTypeUnion() {
        val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion.xq")
        val conformance = file.walkTree().filterIsInstance<XQuerySequenceTypeUnion>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.UNION))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    inner class SequenceTypeList {
        @Test
        @DisplayName("sequence type list")
        fun sequenceTypeList() {
            val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.COMMA))
        }

        @Test
        @DisplayName("in typed function test")
        fun inTypedFunctionTest() {
            val file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.ANY_ITEM_TYPE))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    fun paramList() {
        val file = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped.xq")
        val conformance = file.walkTree().filterIsInstance<XPathParamList>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_4))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ELLIPSIS))
    }
}
