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
    // region MarkLogic 8.0 :: ArrayConstructor

    @Test
    fun testArrayConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanConstructor

    @Test
    fun testBooleanConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NullConstructor

    @Test
    fun testNullConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NumberConstructor

    @Test
    fun testNumberConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region MarkLogic 8.0 :: ArrayTest

    @Test
    fun testArrayTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NullTest

    @Test
    fun testNullTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NumberTest

    @Test
    fun testNumberTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: MapTest

    @Test
    fun testMapTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_KeyName() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_KeyName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_KeyName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_KeyName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_KeyName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_KeyName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NodeTest + KindTest

    @Test
    fun testKindTest_ArrayTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_ArrayTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_ArrayTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NullTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NullTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NullTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NullTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NullTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NullTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NullTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NullTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NullTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NumberTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NumberTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NumberTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NumberTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NumberTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NumberTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_NumberTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NumberTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NumberTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_MapTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_MapTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_MapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_MapTest_StringLiteral() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_MapTest_StringLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_MapTest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_MapTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_MapTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_MapTest_FunctionCallLike.xq")
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
