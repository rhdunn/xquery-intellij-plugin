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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTOptionDecl
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTScoreVar
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryForBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryForClause
import uk.co.reecedunn.intellij.plugin.intellij.lang.FullTextSpec
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 with Full Text 3.0 - Implementation Conformance Checks")
private class FullTextConformanceTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("FullTextConformanceTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    @Test
    @DisplayName("XQuery 3.1 with Full Text EBNF (51) FTContainsExpr")
    fun testFTContainsExpr() {
        val file = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")

        val ftcontainsExprPsi = file.descendants().filterIsInstance<FTContainsExpr>().first()
        val conformance = ftcontainsExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(FullTextSpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_CONTAINS))
    }

    @Test
    @DisplayName("XQuery 3.1 with Full Text EBNF (37) FTScoreVar")
    fun testFTScoreVar() {
        val file = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.xq")

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val scoreVarPsi = forBindingPsi.children().filterIsInstance<FTScoreVar>().first()
        val conformance = scoreVarPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(FullTextSpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_SCORE))
    }

    @Test
    @DisplayName("XQuery 3.1 with Full Text EBNF (24) FTOptionDecl")
    fun testFTOptionDecl() {
        val file = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq")

        val ftoptionDeclPsi = file.descendants().filterIsInstance<FTOptionDecl>().first()
        val conformance = ftoptionDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(FullTextSpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_FT_OPTION))
    }
}