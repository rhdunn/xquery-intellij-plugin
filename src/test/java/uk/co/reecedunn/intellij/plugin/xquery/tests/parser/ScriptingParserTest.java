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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScriptingParserTest extends ParserTestCase {
    // region Scripting Extension 1.0 :: VarDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    public void testVarDecl_Assignable() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    public void testVarDecl_Unassignable() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Scripting Extension 1.0 :: FunctionDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Simple() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Simple_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Sequential() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Sequential_Block() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Scripting Extension 1.0 :: BlockExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr_MissingBlockBody() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockDecls (Block)

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_Empty() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_Single() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_Multiple() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockDecls (WhileBody)

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_WhileBody_Empty() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_WhileBody_Single() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls_WhileBody_Multiple() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregions
    // region Scripting Extension 1.0 :: BlockVarDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_DeclareAsFunctionCall_NCName() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_DeclareAsFunctionCall_QNamePrefix() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_TypeDeclaration_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_Assignment() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_Assignment_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_MultipleVars() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl_MultipleVars_NothingAfterComma() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Scripting Extension 1.0 :: AssignmentExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr_Nested() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Scripting Extension 1.0 :: ExitExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    public void testExitExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    public void testExitExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    public void testExitExpr_MissingReturningKeyword() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    public void testExitExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Scripting Extension 1.0 :: WhileExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr_MissingConditionExpr() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr_MissingBlock() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingBlock.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingBlock.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
