/*
 * Copyright (C) 2017 Reece H. Dunn
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FullTextParserTest extends ParserTestCase {
    // region Full Text 1.0 :: FTOptionDecl + FTMatchOptions

    public void testFTOptionDecl() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOptionDecl_MissingFTMatchOptions() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOptionDecl_MissingFTOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMatchOptions_MissingFTMatchOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMatchOptions_MissingFTMatchOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOptionDecl_NoKeywordOnly() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMatchOptions_NoKeywordOnly.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMatchOptions_NoKeywordOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: ForBinding + FTScoreVar

    public void testForBinding_FTScoreVar() {
        final String expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForBinding_FTScoreVar_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForBinding_FTScoreVar_MissingInKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTScoreVar

    public void testFTScoreVar() {
        final String expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScoreVar_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScoreVar_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScoreVar_MissingVarIndicator.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScoreVar_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScoreVar_MissingVarName() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScoreVar_MissingVarName.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScoreVar_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: LetBinding + FTScoreVar

    public void testLetBinding_FTScoreVar() {
        final String expected = loadResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testLetBinding_FTScoreVar_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTContainsExpr

    public void testFTContainsExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContainsExpr_FTIgnoreOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContainsExpr_MissingTextKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContainsExpr_MissingTextKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContainsExpr_MissingTextKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContainsExpr_MissingFTSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContainsExpr_MissingFTSelection.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContainsExpr_MissingFTSelection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTSelection

    public void testFTSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTSelection_FTPosFilter_Single() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOrder.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOrder.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTSelection_FTPosFilter_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTSelection_FTPosFilter_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTSelection_FTPosFilter_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTWeight

    public void testFTWeight() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWeight.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWeight.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWeight_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWeight_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWeight_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWeight_MissingExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWeight_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWeight_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWeight_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWeight_MissingClosingBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWeight_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTOr

    public void testFTOr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOr_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOr_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOr_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOr_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOr_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTOr_MissingFTAnd() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOr_MissingFTAnd.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOr_MissingFTAnd.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTAnd

    public void testFTAnd() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnd.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnd.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnd_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnd_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnd_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnd_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnd_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnd_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnd_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnd_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnd_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnd_MissingFTMildNot() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnd_MissingFTMildNot.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnd_MissingFTMildNot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTMildNot

    public void testFTMildNot() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTMildNot_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTMildNot_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTMildNot_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTMildNot_MissingInKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot_MissingInKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTMildNot_MissingFTUnaryNot() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMildNot_MissingFTUnaryNot.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMildNot_MissingFTUnaryNot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTUnaryNot

    public void testFTUnaryNot() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTUnaryNot.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTUnaryNot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTPrimaryWithOptions

    public void testFTPrimaryWithOptions_MatchOptions() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptions.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptions.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimaryWithOptions_FTWeight() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWeight.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWeight.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimaryWithOptions_MatchOptionsAndWeight() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTPrimary

    public void testFTPrimary_Words() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_WordsTimes() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_Parenthesis() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_Parenthesis_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_Parenthesis_MissingFTSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_Parenthesis_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTPrimary_ExtensionSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTWordsValue

    public void testFTWordsValue() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWordsValue_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWordsValue_Expr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWordsValue_Expr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWordsValue_Expr_MissingExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWordsValue_Expr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTExtensionSelection

    public void testFTExtensionSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionSelection_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionSelection_MultiplePragmas() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MultiplePragmas.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MultiplePragmas.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionSelection_MissingOpenBrace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingOpenBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingOpenBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionSelection_MissingFTSelection() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingFTSelection.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingFTSelection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionSelection_MissingCloseBrace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingCloseBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingCloseBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTAnyallOption

    public void testFTAnyallOption_Any() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_Any.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_Any.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnyallOption_AnyWord() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_AnyWord.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_AnyWord.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnyallOption_All() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_All.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_All.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnyallOption_AllWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_AllWords.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_AllWords.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTAnyallOption_Phrase() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_Phrase.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_Phrase.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTTimes

    public void testFTTimes() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTTimes_MissingFTRange() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTTimes_MissingFTRange.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTTimes_MissingFTRange.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTTimes_MissingTimesKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTTimes_MissingTimesKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTTimes_MissingTimesKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTRange

    public void testFTRange_Exactly() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_Exactly.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_Exactly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_Exactly_MissingAdditiveExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_At_MissingQualifier() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_At_MissingQualifier.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_At_MissingQualifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_AtLeast() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_AtLeast_MissingAdditiveExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_AtMost() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtMost.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtMost.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_AtMost_MissingAdditiveExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_FromTo() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_FromTo_MissingFromExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingFromExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingFromExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_FromTo_MissingToKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTRange_FromTo_MissingToExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTOrder

    public void testFTOrder() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOrder.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOrder.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTWindow

    public void testFTWindow() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWindow.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWindow.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWindow_MissingAdditiveExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWindow_MissingAdditiveExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWindow_MissingAdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWindow_MissingFTUnit() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWindow_MissingFTUnit.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWindow_MissingFTUnit.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTDistance

    public void testFTDistance() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDistance.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDistance.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTDistance_MissingFTRange() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDistance_MissingFTRange.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDistance_MissingFTRange.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTDistance_MissingFTUnit() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDistance_MissingFTUnit.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDistance_MissingFTUnit.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTUnit

    public void testFTUnit_Words() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWindow.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWindow.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTUnit_Sentences() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTUnit_Sentences.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTUnit_Sentences.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTUnit_Paragraphs() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTUnit_Paragraphs.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTUnit_Paragraphs.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTScope

    public void testFTScope_Same() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScope_Same.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScope_Same.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScope_Same_MissingFTBigUnit() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScope_Same_MissingFTBigUnit.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScope_Same_MissingFTBigUnit.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScope_Different() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScope_Different.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScope_Different.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTScope_Different_MissingFTBigUnit() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScope_Different_MissingFTBigUnit.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScope_Different_MissingFTBigUnit.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTBigUnit

    public void testFTBigUnit_Sentence() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTScope_Same.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTScope_Same.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTBigUnit_Paragraph() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTBigUnit_Paragraph.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTBigUnit_Paragraph.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTContent

    public void testFTContent_AtStart() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContent_AtStart.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContent_AtStart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContent_AtEnd() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContent_AtEnd.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContent_AtEnd.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContent_At_MissingStartEndKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContent_At_MissingStartEndKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContent_At_MissingStartEndKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContent_EntireContent() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContent_EntireContent.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContent_EntireContent.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTContent_EntireContent_MissingContentKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTContent_EntireContent_MissingContentKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTContent_EntireContent_MissingContentKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTCaseOption

    public void testFTCaseOption_LowerCase() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTCaseOption_UpperCase() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTCaseOption_Case_Sensitive() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_Sensitive.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_Sensitive.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTCaseOption_Case_Insensitive() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_Insensitive.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_Insensitive.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTCaseOption_Case_MissingSensitivityKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTDiacriticsOption

    public void testFTDiacriticsOption_Sensitive() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_Sensitive.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_Sensitive.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTDiacriticsOption_Insensitive() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_Insensitive.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_Insensitive.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTDiacriticsOption_MissingSensitivityKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTStemOption

    public void testFTStemOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStemOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStemOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStemOption_NoStemming() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStemOption_NoStemming.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStemOption_NoStemming.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTThesaurusOption

    public void testFTThesaurusOption_Default() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Default.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_ThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_NoThesaurus() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_MissingThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_Default() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Default.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_ThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_MissingThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_TwoItems() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_NoParenthesis_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusOption_UnexpectedClosingParenthesis() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTThesaurusID (FTThesaurusOption)

    public void testFTThesaurusID() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_MissingUriLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_MissingUriLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_MissingUriLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_Relationship() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_Relationship_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_Relationship_MissingStringLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_LiteralRange() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTThesaurusID_LiteralRange_MissingLevelsKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTLiteralRange (FTThesaurusOption)

    public void testFTLiteralRange_Exactly() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_Exactly_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_Exactly_MissingIntegerLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_At_MissingQualifier() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_At_MissingQualifier.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_At_MissingQualifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtLeast() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtLeast_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtLeast_MissingIntegerLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtMost() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtMost_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_AtMost_MissingIntegerLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_FromTo() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_FromTo_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_FromTo_MissingFromLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_FromTo_MissingToKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLiteralRange_FromTo_MissingToLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTStopWordOption

    public void testFTStopWordOption_Default() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_Default_MissingWordsKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_Default_FTStopWordsInclExcl() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_Default_FTStopWordsInclExcl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_Default_FTStopWordsInclExcl_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_FTStopWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_FTStopWords_FTStopWordsInclExcl() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_MissingStopWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_MissingStopWords.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_MissingStopWords.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_NoStopWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordOption_NoStopWords_MissingWordsKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTStopWords (FTStopWordOption)

    public void testFTStopWords_Location() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_Location_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_Location_MissingUriLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location_MissingUriLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location_MissingUriLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_MissingStopWord() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_MissingStopWord.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_MissingStopWord.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_Single() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_Single_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_TwoWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_TwoWords_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_TwoWords_MissingStopWord() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_Multiple() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWords_List_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTStopWordsInclExcl (FTStopWordOption)

    public void testFTStopWordsInclExcl_Include() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordsInclExcl_Include_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordsInclExcl_Exclude() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordsInclExcl_Exclude_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTStopWordsInclExcl_MissingStopWords() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_MissingStopWords.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_MissingStopWords.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTLanguageOption

    public void testFTLanguageOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLanguageOption_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTLanguageOption_MissingStringLiteral() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption_MissingStringLiteral.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption_MissingStringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTWildCardOption

    public void testFTWildCardOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTWildCardOption_NoWildCards() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTExtensionOption

    public void testFTExtensionOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionOption_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionOption_MissingOptionName() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionName.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTExtensionOption_MissingOptionValue() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionValue.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTIgnoreOption

    public void testFTIgnoreOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTIgnoreOption_CompactWhitespace() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTIgnoreOption_MissingContentKeyword() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingContentKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingContentKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testFTIgnoreOption_MissingUnionExpr() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingUnionExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingUnionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
