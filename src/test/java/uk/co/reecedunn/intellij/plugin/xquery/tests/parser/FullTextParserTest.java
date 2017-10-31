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
}
