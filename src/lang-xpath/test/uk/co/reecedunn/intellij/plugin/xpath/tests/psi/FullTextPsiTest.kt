/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTScoreVar
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName", "RedundantVisibilityModifier")
@DisplayName("XPath 3.1 with Full Text 3.0 - IntelliJ Program Structure Interface (PSI)")
private class FullTextPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (2.2) Full-Text Contains Expression")
    internal inner class FullTextContainsExpression {
        @Test
        @DisplayName("Full Text 3.0 XPath EBNF (20) FTContainsExpr")
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
        @DisplayName("Full Text 3.0 XPath EBNF (14) FTScoreVar")
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
            @DisplayName("Full Text 3.0 XPath EBNF (127) FTThesaurusID")
            internal inner class FTThesaurusIDTest {
                @Test
                @DisplayName("thesaurus uri")
                fun uri() {
                    val thesaurus = parse<FTThesaurusID>(
                        "x contains text 'test' using thesaurus at 'http://www.example.com'"
                    )[0]
                    assertThat(thesaurus.source!!.data, `is`("http://www.example.com"))
                    assertThat(thesaurus.source!!.context, `is`(XdmUriContext.Thesaurus))
                    assertThat(thesaurus.source!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
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
            @DisplayName("Full Text 3.0 XPath EBNF (130) FTStopWords")
            internal inner class FTStopWordsTest {
                @Test
                @DisplayName("stop word uri")
                fun uri() {
                    val words =
                        parse<FTStopWords>("x contains text 'test' using stop words at 'http://www.example.com'")[0]
                    assertThat(words.source!!.data, `is`("http://www.example.com"))
                    assertThat(words.source!!.context, `is`(XdmUriContext.StopWords))
                    assertThat(words.source!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
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

    @Nested
    @DisplayName("XQuery and XPath Full Text 3.0 (3.8) Extension Selections")
    internal inner class ExtensionSelections {
        @Nested
        @DisplayName("Full Text 3.0 XPath EBNF (36) Pragma")
        internal inner class PragmaTest {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() contains text (# test #) {}")[0] as XsQNameValue
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Pragma))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }
}
