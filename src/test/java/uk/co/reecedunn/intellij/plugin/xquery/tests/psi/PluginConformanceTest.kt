/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTMatchOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTPrimaryWithOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTSelection
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.BaseX
import uk.co.reecedunn.intellij.plugin.xquery.lang.Saxon
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class PluginConformanceTest : ParserTestCase() {
    // region FTFuzzyOption

    @Test
    fun testFTFuzzyOption() {
        val file = parseResource("tests/parser/basex-6.1/FTFuzzyOption.xq")

        val containsExpr = file.descendants().filterIsInstance<FTContainsExpr>().first()
        val selection = containsExpr.children().filterIsInstance<FTSelection>().first()
        val primaryWithOptions = selection.descendants().filterIsInstance<FTPrimaryWithOptions>().first()
        val matchOptions = primaryWithOptions.children().filterIsInstance<FTMatchOptions>().first()
        val fuzzyOption = matchOptions.children().filterIsInstance<PluginFTFuzzyOption>().first()
        val conformance = fuzzyOption as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_6_1))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_FUZZY))
    }

    // endregion
    // region NonDeterministicFunctionCall

    @Test
    fun testNonDeterministicFunctionCall() {
        val file = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.xq")

        val nonDeterministicFunctionCall = file.walkTree().filterIsInstance<PluginNonDeterministicFunctionCall>().first()
        val conformance = nonDeterministicFunctionCall as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_8_4))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NON_DETERMINISTIC))
    }

    // endregion
    // region TypeDecl

    @Test
    fun testTypeDecl() {
        val file = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")

        val typeDeclPsi = file.descendants().filterIsInstance<PluginTypeDecl>().first()
        val conformance = typeDeclPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_TYPE))
    }

    // endregion
    // region UnionType

    @Test
    fun testUnionType() {
        val file = parseResource("tests/parser/saxon-9.8/UnionType.xq")

        val unionTypePsi = file.walkTree().filterIsInstance<PluginUnionType>().first()
        val conformance = unionTypePsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UNION))
    }

    // endregion
    // region UpdateExpr

    @Test
    fun testUpdateExpr() {
        val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

        val updateExpr = file.descendants().filterIsInstance<PluginUpdateExpr>().first()
        val conformance = updateExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_7_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UPDATE))
    }

    @Test
    fun testUpdateExpr_Block() {
        val file = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")

        val updateExpr = file.descendants().filterIsInstance<PluginUpdateExpr>().first()
        val conformance = updateExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_8_5))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
}
