/*
 * Copyright (C) 2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.ast.basex.BaseXUpdateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.extensions.descendants

class BaseXPsiTest : ParserTestCase() {
    // region XQueryConformanceCheck
    // region UpdateExpr

    fun testUpdateExpr() {
        val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")!!

        val updateExpr = file.descendants().filterIsInstance<BaseXUpdateExpr>().first()
        val versioned = updateExpr as XQueryConformanceCheck

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), `is`(false))

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1-update")), `is`(true))

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1-update")), `is`(true))

        assertThat(versioned.conformanceErrorMessage,
                `is`("XPST0003: This expression requires BaseX 8.4 or later."))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_UPDATE))
    }

    fun testUpdateExpr_Block() {
        val file = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")!!

        val updateExpr = file.descendants().filterIsInstance<BaseXUpdateExpr>().first()
        val versioned = updateExpr as XQueryConformanceCheck

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), `is`(false))

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0-update")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1-update")), `is`(false))

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0-update")), `is`(true))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1")), `is`(false))
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1-update")), `is`(true))

        assertThat(versioned.conformanceErrorMessage,
                `is`("XPST0003: This expression requires BaseX 8.5 or later."))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // endregion
}
