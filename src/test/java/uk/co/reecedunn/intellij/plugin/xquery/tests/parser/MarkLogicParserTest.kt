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
    // region MarkLogic 8.0 :: MapConstructor

    @Test
    fun testMapConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: MapConstructorEntry + MapConstructor

    @Test
    fun testMapConstructorEntry() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingSeparator() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingValueExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_MissingEntry() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName_WhitespaceAfterColon() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_KeyExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_ValueExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_VarRef_NCName() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    @Test
    fun testAnyKindTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
