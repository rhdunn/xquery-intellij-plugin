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
