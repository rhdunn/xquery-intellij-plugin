/*
 * Copyright (C) 2016-2022 Reece H. Dunn
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
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("Reformat", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Implementation Conformance Checks")
class XQueryConformanceTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryConformanceTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    // region AnyMapTest

    @Test
    fun testAnyMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq")

        val anyMapTestPsi = file.walkTree().filterIsInstance<XPathAnyMapTest>().first()
        val versioned = anyMapTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region MapConstructor

    @Test
    fun testMapConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/MapConstructor.xq")

        val objectConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val versioned = objectConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region ParenthesizedItemType

    @Nested
    @DisplayName("XQuery 3.1 EBNF (216) ParenthesizedItemType")
    internal inner class ParenthesizedItemType {
        @Test
        @DisplayName("item type")
        fun itemType() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
            assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }

        @Test
        @DisplayName("empty sequence")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(FormalSemanticsSpec.REC_1_0_20070123))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }

        @Test
        @DisplayName("occurrence indicator")
        fun occurrenceIndicator() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(FormalSemanticsSpec.REC_1_0_20070123))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }
    }

    // endregion
    // region TypedMapTest

    @Test
    fun testTypedMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq")

        val typedMapTestPsi = file.walkTree().filterIsInstance<XPathTypedMapTest>().first()
        val versioned = typedMapTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; working draft syntax")
        fun workingDraft() {
            val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(3))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.WD_1_0_20030502))
            assertThat(versioned.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(versioned.requiresConformance[2], `is`(until(EXistDB.VERSION_4_0)))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY))
        }

        @Test
        @DisplayName("empty sequence; recommendation syntax")
        fun rec() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_1_0_20070123))
            assertThat(versioned.requiresConformance[1], `is`(EXistDB.VERSION_4_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY_SEQUENCE))
        }
    }
}
