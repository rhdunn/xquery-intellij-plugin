/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - XPath Parser")
private class PluginParserTest : ParserTestCase() {
    fun parseResource(resource: String): XPath {
        val file = ResourceVirtualFile(PluginParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile(PluginParserTest::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (92) TernaryIfExpr")
    internal inner class TernaryIfExpr {
        @Test
        @DisplayName("ternary if")
        fun ternaryIf() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("ternary if; compact whitespace")
        fun ternaryIf_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing then Expr")
            fun missingThenExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingThenExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingThenExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else operator")
            fun missingElseOperator() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseOperator.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseOperator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (93) ElvisExpr")
    internal inner class ElvisExpr {
        @Test
        @DisplayName("elvis")
        fun elvis() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("elvis; compact whitespace")
        fun elvis_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_MissingElseExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }
}
