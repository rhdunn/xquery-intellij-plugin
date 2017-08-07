/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.core.extensions.descendants

class SaxonPsiTest : ParserTestCase() {
    // region XQueryConformanceCheck
    // region MapConstructorEntry

    fun testMapConstructorEntry() {
        val file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")!!

        val mapConstructorPsi = file.descendants().filterIsInstance<XQueryMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XQueryMapConstructorEntry>().first()
        val versioned = mapConstructorEntryPsi as XQueryConformanceCheck

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), `is`(false))

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), `is`(false))

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), `is`(false))

        assertThat(versioned.conformanceErrorMessage,
                `is`("XPST0003: Use ':=' for Saxon 9.4 to 9.6, and ':' for XQuery 3.1 and MarkLogic."))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.ASSIGN_EQUAL))
    }

    // endregion
    // endregion
}
