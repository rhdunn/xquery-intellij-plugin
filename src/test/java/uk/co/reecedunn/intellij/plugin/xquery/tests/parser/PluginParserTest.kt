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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class PluginParserTest : ParserTestCase() {
    // region BaseX 7.8 :: UpdateExpr

    @Test
    fun testUpdateExpr() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_MissingExpr() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Multiple() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_Multiple.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region BaseX 8.5 :: UpdateExpr

    @Test
    fun testUpdateExpr_Block() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_MissingExpr() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_MissingClosingBrace() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_Multiple() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_Multiple.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
