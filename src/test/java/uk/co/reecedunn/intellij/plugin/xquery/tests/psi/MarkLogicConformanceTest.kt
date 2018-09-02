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
