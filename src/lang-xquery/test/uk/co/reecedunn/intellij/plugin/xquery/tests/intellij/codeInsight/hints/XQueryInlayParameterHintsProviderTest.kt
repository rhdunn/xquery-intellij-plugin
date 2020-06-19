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
}
