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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTOptionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTScoreVar
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryForBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryForClause
import uk.co.reecedunn.intellij.plugin.xquery.lang.FullText
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class FullTextPsiTest : ParserTestCase() {
    // region XQueryConformance
    // region FTContainsExpr

    fun testFTContainsExpr() {
        val file = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")

        val ftcontainsExprPsi = file.descendants().filterIsInstance<FTContainsExpr>().first()
        val conformance = ftcontainsExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(FullText.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_CONTAINS))
    }

    // endregion
    // region FTScoreVar

    fun testFTScoreVar() {
        val file = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.xq")

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val scoreVarPsi = forBindingPsi.children().filterIsInstance<FTScoreVar>().first()
        val conformance = scoreVarPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(FullText.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCORE))
    }

    // endregion
    // region FTOptionDecl

    fun testFTOptionDecl() {
        val file = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq")

        val ftoptionDeclPsi = file.descendants().filterIsInstance<FTOptionDecl>().first()
        val conformance = ftoptionDeclPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(FullText.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FT_OPTION))
    }

    // endregion
    // endregion
}