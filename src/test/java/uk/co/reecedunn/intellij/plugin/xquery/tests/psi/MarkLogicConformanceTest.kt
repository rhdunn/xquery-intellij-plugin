/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginAttributeDeclTest
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class MarkLogicConformanceTest : ParserTestCase() {
    // region AnyKindTest

    @Test
    fun testAnyKindTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq")

        val anyKindTestPsi = file.walkTree().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.STRING_LITERAL))
    }

    @Test
    fun testAnyKindTest_Wildcard() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq")

        val anyKindTestPsi = file.walkTree().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.STAR))
    }

    // endregion
    // region ArrayConstructor

    @Test
    fun testArrayConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/ArrayConstructor.xq")

        val arrayConstructorPsi = file.descendants().filterIsInstance<XPathArrayConstructor>().first()
        val versioned = arrayConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region ArrayTest

    @Test
    fun testArrayTest() {
        val file = parseResource("tests/parser/marklogic-8.0/ArrayTest.xq")

        val arrayTestPsi = file.walkTree().filterIsInstance<MarkLogicArrayTest>().first()
        val conformance = arrayTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region BooleanConstructor

    @Test
    fun testBooleanConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")

        val booleanConstructorPsi = file.descendants().filterIsInstance<MarkLogicBooleanConstructor>().first()
        val conformance = booleanConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region BooleanTest

    @Test
    fun testBooleanTest() {
        val file = parseResource("tests/parser/marklogic-8.0/BooleanTest.xq")

        val booleanTestPsi = file.walkTree().filterIsInstance<MarkLogicBooleanTest>().first()
        val conformance = booleanTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region MapConstructor

    @Test
    fun testMapConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/MapConstructor.xq")

        val objectConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val versioned = objectConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_OBJECT_NODE))
    }

    // endregion
    // region MapTest

    @Test
    fun testMapTest() {
        val file = parseResource("tests/parser/marklogic-8.0/MapTest.xq")

        val objectTestPsi = file.walkTree().filterIsInstance<MarkLogicMapTest>().first()
        val conformance = objectTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_OBJECT_NODE))
    }

    // endregion
    // region NullConstructor

    @Test
    fun testNullConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")

        val nullKindTestPsi = file.descendants().filterIsInstance<MarkLogicNullConstructor>().first()
        val conformance = nullKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NullTest

    @Test
    fun testNullTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NullTest.xq")

        val nullTestPsi = file.walkTree().filterIsInstance<MarkLogicNullTest>().first()
        val conformance = nullTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NumberConstructor

    @Test
    fun testNumberConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")

        val numberConstructorPsi = file.descendants().filterIsInstance<MarkLogicNumberConstructor>().first()
        val conformance = numberConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region NumberTest

    @Test
    fun testNumberTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberTest.xq")

        val numberTestPsi = file.walkTree().filterIsInstance<MarkLogicNumberTest>().first()
        val conformance = numberTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region SchemaFacetTest

    @Test
    fun testSchemaFacetTest() {
        val file = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest.xq")

        val schemaFacetTestPsi = file.walkTree().filterIsInstance<MarkLogicSchemaFacetTest>().first()
        val conformance = schemaFacetTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_FACET))
    }

    // endregion
    // region TextTest

    @Test
    fun testTextTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq")

        val textTestPsi = file.walkTree().filterIsInstance<XPathTextTest>().first()
        val versioned = textTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.STRING_LITERAL))
    }

    // endregion
}
