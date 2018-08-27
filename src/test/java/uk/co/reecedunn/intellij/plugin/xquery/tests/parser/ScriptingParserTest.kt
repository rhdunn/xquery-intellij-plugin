/*
 * Copyright (C) 2016 Reece H. Dunn
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class ScriptingParserTest : ParserTestCase() {
    // region Scripting Extension 1.0 :: ApplyExpr

    @Test
    fun testApplyExpr_Single_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_Single_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_Single_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_TwoExpr_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_TwoExpr_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_NoSemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_NoSemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_Multiple_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_Multiple_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_NoSemicolonBetweenExpr_First() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_First.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_First.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_NoSemicolonBetweenExpr_Last() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_Last.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_Last.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_EmptyStatement() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyStatement.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testApplyExpr_EmptyMiddleStatement() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyMiddleStatement.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyMiddleStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: QueryBody (ApplyExpr)

    @Test
    fun testQueryBody_Single_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_Single_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_Single_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_TwoExpr_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_TwoExpr_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_Multiple_SemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_Multiple_SemicolonAtEnd_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_Multiple_NoSemicolonAtEnd() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_NoSemicolonAtEnd.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_NoSemicolonAtEnd.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_NoSemicolonBetweenExpr_First() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_First.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_First.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_NoSemicolonBetweenExpr_Last() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_Last.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_Last.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_EmptyStatement() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyStatement.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQueryBody_EmptyMiddleStatement() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyMiddleStatement.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyMiddleStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: VarDecl

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    fun testVarDecl_Assignable() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    fun testVarDecl_Unassignable() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: FunctionDecl

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Simple() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Simple_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Updating() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Updating_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Sequential() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Sequential_Block() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: BlockExpr

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    fun testBlockExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    fun testBlockExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    fun testBlockExpr_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    fun testBlockExpr_MissingBlockBody() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    fun testBlockExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockDecls (Block)

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_Empty() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_Single() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_Multiple() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockDecls (WhileBody)

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_WhileBody_Empty() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_WhileBody_Single() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    fun testBlockDecls_WhileBody_Multiple() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockVarDecl

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_DeclareAsFunctionCall_NCName() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_DeclareAsFunctionCall_QNamePrefix() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_TypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_TypeDeclaration_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_Assignment() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_Assignment_Equals() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_Equals.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_Equals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_Assignment_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_MultipleVars() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    fun testBlockVarDecl_MultipleVars_NothingAfterComma() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: AssignmentExpr

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    fun testAssignmentExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    fun testAssignmentExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    fun testAssignmentExpr_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    fun testAssignmentExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    fun testAssignmentExpr_Nested() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: ExitExpr

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    fun testExitExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    fun testExitExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    fun testExitExpr_MissingReturningKeyword() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    fun testExitExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Scripting Extension 1.0 :: WhileExpr

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testWhileExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testWhileExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testWhileExpr_MissingConditionExpr() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testWhileExpr_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testFunctionCall_WhileKeyword_NoParams() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testFunctionCall_WhileKeyword_SingleParam() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_SingleParam.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_SingleParam.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @Specification(name = "XQuery Scripting Extension 1.0", reference = "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    fun testFunctionCall_WhileKeyword_MultipleParams() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_MultipleParams.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_MultipleParams.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
