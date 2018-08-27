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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class MarkLogicPsiTest : ParserTestCase() {
    // region XPathMapConstructorEntry

    @Test
    fun testMapConstructorEntry() {
        val file = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")

        val mapConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XPathMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`(XQueryTokenType.QNAME_SEPARATOR))
    }

    // endregion
}
