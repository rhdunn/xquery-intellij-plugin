/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTScoreVar
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFLWORExpr
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 with Full Text 3.0 - IntelliJ Program Structure Interface (PSI)")
class FullTextPsiTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("FullTextPsiTest")

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (2.2) Full-Text Contains Expression")
    internal inner class FullTextContainsExpression {
        @Test
        @DisplayName("Full Text 3.0 EBNF (88) FTContainsExpr")
        fun ftContainsExpr() {
            val expr = parse<FTContainsExpr>("1 contains text \"test\"")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_CONTAINS))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (2.3) Score Variables")
    internal inner class ScoreVariables {
        @Nested
        @DisplayName("Full Text 3.0 EBNF (49) FTScoreVar")
        internal inner class FTScoreVarTest {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<FTScoreVar>(
                    "for \$x score \$y in () return \$z"
                )[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(CoreMatchers.instanceOf(XQueryFLWORExpr::class.java)))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<FTScoreVar>(
                    "for \$a:x score \$a:y in () return \$a:z"
                )[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(CoreMatchers.instanceOf(XQueryFLWORExpr::class.java)))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<FTScoreVar>(
                    "for \$Q{http://www.example.com}x score \$Q{http://www.example.com}y in () return \$Q{http://www.example.com}z"
                )[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(CoreMatchers.instanceOf(XQueryFLWORExpr::class.java)))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<FTScoreVar>("for \$x score \$")[0] as XpmVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (3.4) Match Options")
    internal inner class MatchOptions {
        @Nested
        @DisplayName("XQuery and XPath Full Text 3.0 (3.4.3) Thesaurus Option")
        internal inner class ThesaurusOption {
            @Nested
            @DisplayName("Full Text 3.0 EBNF (227) FTThesaurusID")
            internal inner class FTThesaurusIDTest {
                @Test
                @DisplayName("thesaurus uri")
                fun uri() {
                    val thesaurus = parse<FTThesaurusID>(
                        "x contains text 'test' using thesaurus at 'http://www.example.com'"
                    )[0]
                    assertThat(thesaurus.source!!.data, `is`("http://www.example.com"))
                    assertThat(thesaurus.source!!.context, `is`(XdmUriContext.Thesaurus))
                    assertThat(thesaurus.source!!.moduleTypes, `is`(CoreMatchers.sameInstance(XdmModuleType.NONE)))
                }

                @Test
                @DisplayName("missing thesaurus uri")
                fun wordList_single() {
                    val thesaurus = parse<FTThesaurusID>("x contains text 'test' using thesaurus at")[0]
                    assertThat(thesaurus.source, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery and XPath Full Text 3.0 (3.4.7) Stop Word Option")
        internal inner class StopWordOption {
            @Nested
            @DisplayName("Full Text 3.0 EBNF (230) FTStopWords")
            internal inner class FTStopWordsTest {
                @Test
                @DisplayName("stop word uri")
                fun uri() {
                    val words =
                        parse<FTStopWords>("x contains text 'test' using stop words at 'http://www.example.com'")[0]
                    assertThat(words.source!!.data, `is`("http://www.example.com"))
                    assertThat(words.source!!.context, `is`(XdmUriContext.StopWords))
                    assertThat(words.source!!.moduleTypes, `is`(CoreMatchers.sameInstance(XdmModuleType.NONE)))
                }

                @Test
                @DisplayName("stop words; single")
                fun wordList_single() {
                    val words = parse<FTStopWords>("x contains text 'test' using stop words ('lorem'))")[0]
                    assertThat(words.source, `is`(nullValue()))
                }

                @Test
                @DisplayName("stop words; multiple")
                fun wordList_multiple() {
                    val words = parse<FTStopWords>(
                        "x contains text 'test' using stop words ('lorem', 'ipsum', 'dolor'))"
                    )[0]
                    assertThat(words.source, `is`(nullValue()))
                }
            }
        }
    }
}
