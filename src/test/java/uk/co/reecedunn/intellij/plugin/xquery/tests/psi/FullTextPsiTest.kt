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
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import uk.co.reecedunn.intellij.plugin.core.extensions.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTOptionDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.FullText
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class FullTextPsiTest : ParserTestCase() {
    // region XQueryConformance
    // region FTOptionDecl

    fun testRevalidationDecl() {
        val file = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq")!!

        val ftoptionDeclPsi = file.descendants().filterIsInstance<FTOptionDecl>().first()
        val conformance = ftoptionDeclPsi as XQueryConformance

        MatcherAssert.assertThat(conformance.requiresConformance.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(conformance.requiresConformance[0], CoreMatchers.`is`<Version>(FullText.REC_1_0_20110317))

        MatcherAssert.assertThat(conformance.conformanceElement, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        MatcherAssert.assertThat(conformance.conformanceElement.node.elementType,
                CoreMatchers.`is`<IElementType>(XQueryTokenType.K_FT_OPTION))
    }

    // endregion
    // endregion
}