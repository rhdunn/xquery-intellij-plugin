/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.intellij.codeInsight.hints

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.intellij.codeInsight.hints.XPathInlayParameterHintsProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("UnstableApiUsage")
@DisplayName("IntelliJ - Custom Language Support - Inlay Hints - XPath Parameter Hints Provider")
private class XQueryInlayParameterHintsProviderTest : ParserTestCase() {
    val provider = XPathInlayParameterHintsProvider()

    @Nested
    @DisplayName("get hint info")
    internal inner class GetHintInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val args = parse<XPathArgumentList>("abs(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val args = parse<XPathArgumentList>("fn:abs(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val args = parse<XPathArgumentList>("Q{http://www.w3.org/2005/xpath-functions}abs(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val args = parse<XPathArgumentList>("let \$a := abs#1 return 2 => \$a()")[0]

                val info = provider.getHintInfo(args)
                assertThat(info, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val args = parse<XPathArgumentList>("2 => abs()")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val args = parse<XPathArgumentList>("2 => fn:abs()")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val args = parse<XPathArgumentList>("2 => Q{http://www.w3.org/2005/xpath-functions}abs()")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("arg"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val args = parse<XPathArgumentList>("2 => (fn:abs#1)()")[0]

                val info = provider.getHintInfo(args)
                assertThat(info, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        internal inner class Param {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val args = parse<XPathArgumentList>("declare function local:test(\$x) {}; local:test(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val args = parse<XPathArgumentList>("declare function local:test(\$fn:x) {}; local:test(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val args = parse<XPathArgumentList>("declare function local:test(\$Q{http://www.example.com}x) {}; local:test(2)")[0]

                val info = provider.getHintInfo(args)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }
        }
    }

    @Nested
    @DisplayName("get parameter hints")
    internal inner class GetParameterHints {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
            fun integerLiteral() {
                val args = parse<XPathArgumentList>("declare function local:f(\$arg1, \$arg2) {}; local:f(3, 6)")[0]

                val hints = provider.getParameterHints(args)
                assertThat(hints.size, `is`(2))

                assertThat(hints[0].text, `is`("arg1"))
                assertThat(hints[0].offset, `is`(51))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                assertThat(hints[1].text, `is`("arg2"))
                assertThat(hints[1].offset, `is`(54))
                assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            internal inner class VarRef {
                @Test
                @DisplayName("variable names not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}one, ${'$'}two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(57))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("variable names matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}one, ${'$'}arg2, ${'$'}three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(71))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest ; XQuery 3.1 EBNF (119) NameTest")
            internal inner class NameTest {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/one, ${'$'}x/lorem//two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(59))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/one, ${'$'}x/arg2, ${'$'}x/lorem//three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
            internal inner class AbbrevForwardStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/@one, ${'$'}x/lorem//@two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(60))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/@one, ${'$'}x/@arg2, ${'$'}x/lorem//@three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(77))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep")
            internal inner class ForwardStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/child::one, ${'$'}x/lorem//child::two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(66))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/child::one, ${'$'}x/child::arg2, ${'$'}x/lorem//child::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(89))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (115) ReverseStep")
            internal inner class ReverseStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/parent::one, ${'$'}x/lorem//parent::two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(67))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/parent::one, ${'$'}x/parent::arg2, ${'$'}x/lorem//parent::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(91))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
            fun integerLiteral() {
                val args = parse<XPathArgumentList>("declare function local:f(\$arg1, \$arg2) {}; 3 => local:f(6)")[0]

                val hints = provider.getParameterHints(args)
                assertThat(hints.size, `is`(1))

                assertThat(hints[0].text, `is`("arg2"))
                assertThat(hints[0].offset, `is`(56))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            internal inner class VarRef {
                @Test
                @DisplayName("variable names not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}two, ${'$'}three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(72))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("variable names matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}two, ${'$'}arg3, ${'$'}four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(86))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest ; XQuery 3.1 EBNF (119) NameTest")
            internal inner class NameTest {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/two, ${'$'}x/lorem//three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(74))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/two, ${'$'}x/arg3, ${'$'}x/lorem//four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(90))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
            internal inner class AbbrevForwardStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/@two, ${'$'}x/lorem//@three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/@two, ${'$'}x/@arg3, ${'$'}x/lorem//@four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep")
            internal inner class ForwardStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/child::two, ${'$'}x/lorem//child::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(81))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/child::two, ${'$'}x/child::arg3, ${'$'}x/lorem//child::four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(104))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (115) ReverseStep")
            internal inner class ReverseStep {
                @Test
                @DisplayName("name test not matching the parameter names")
                fun different() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/parent::two, ${'$'}x/lorem//parent::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(82))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name test matching the parameter names")
                fun same() {
                    val args = parse<XPathArgumentList>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/parent::two, ${'$'}x/parent::arg3, ${'$'}x/lorem//parent::four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(106))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
        internal inner class ParamList {
            @Test
            @DisplayName("incomplete variable name")
            fun incompleteVariableName() {
                val args = parse<XPathArgumentList>("declare function local:f(\$, \$arg2) {}; local:f(3, 4)")[0]

                val hints = provider.getParameterHints(args)
                assertThat(hints.size, `is`(1))

                assertThat(hints[0].text, `is`("arg2"))
                assertThat(hints[0].offset, `is`(50))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("variadic")
            internal inner class Variadic {
                @Test
                @DisplayName("zero")
                fun zero() {
                    val args =
                        parse<XPathArgumentList>("declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3)")[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(1))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("one")
                fun one() {
                    val args =
                        parse<XPathArgumentList>("declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3, 6)")[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(58))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("many")
                fun many() {
                    val args =
                        parse<XPathArgumentList>("declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3, 6, 9, 12)")[0]

                    val hints = provider.getParameterHints(args)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(58))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }
    }
}
