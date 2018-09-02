/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
private class MarkLogicParserTest : ParserTestCase() {
    // region MarkLogic 8.0 :: TextTest

    @Test
    fun testTextTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: DocumentTest + ArrayTest

    @Test
    fun testDocumentTest_ArrayTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_ArrayTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_ArrayTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_ArrayTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: DocumentTest + MapTest

    @Test
    fun testDocumentTest_MapTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_MapTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_MapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_MapTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_MapTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
