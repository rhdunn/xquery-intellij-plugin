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
package uk.co.reecedunn.intellij.plugin.xquery.tests.intellij.lang

import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPathParameterInfoHandler
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPathInlayParameterHintsProvider
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("UnstableApiUsage")
@DisplayName("IntelliJ - Custom Language Support - Inlay Hints - XPath Parameter Hints Provider")
private class XQueryInlayParameterHintsProviderTest : ParserTestCase() {
    val provider = XPathInlayParameterHintsProvider()

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
    internal inner class FunctionCall {
        @Test
        @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
        fun ncname() {
            val args = parse<XPathArgumentList>("abs(2)")[0]

            val info = provider.getHintInfo(args)!!
            assertThat(info.fullyQualifiedName, `is`("abs"))
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
            assertThat(info.fullyQualifiedName, `is`("abs"))
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
            assertThat(info.getMethodName(), `is`("org/2005/xpath-functions}abs"))

            assertThat(info.paramNames.size, `is`(1))
            assertThat(info.paramNames[0], `is`("arg"))
        }
    }
}
