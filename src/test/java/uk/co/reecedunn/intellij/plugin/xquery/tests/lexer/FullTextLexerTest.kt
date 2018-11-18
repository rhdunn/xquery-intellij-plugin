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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery and XPath Full Text 3.0 - Lexer")
class FullTextLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (24) FTOptionDecl")
    fun testFTOptionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "ft-option", XQueryTokenType.K_FT_OPTION)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (37) FTScoreVar")
    fun testFTScoreVar() {
        val lexer = createLexer()

        matchSingleToken(lexer, "score", XQueryTokenType.K_SCORE)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (51) FTContainsExpr")
    fun testFTContainsExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "contains", XQueryTokenType.K_CONTAINS)
        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (145) FTWeight")
    fun testFTWeight() {
        val lexer = createLexer()

        matchSingleToken(lexer, "weight", XQueryTokenType.K_WEIGHT)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (146) FTOr")
    fun testFTOr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftor", XQueryTokenType.K_FTOR)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (147) FTAnd")
    fun testFTAnd() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftand", XQueryTokenType.K_FTAND)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (148) FTMildNot")
    fun testFTMildNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "not", XQueryTokenType.K_NOT)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (149) FTUnaryNot")
    fun testFTUnaryNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftnot", XQueryTokenType.K_FTNOT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (151) FTPrimary")
    fun testFTPrimary() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (153) FTWordsValue")
    fun testFTWordsValue() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (154) FTExtensionSelection")
    fun testFTExtensionSelection() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (155) FTAnyallOption")
    fun testFTAnyallOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "any", XQueryTokenType.K_ANY)
        matchSingleToken(lexer, "all", XQueryTokenType.K_ALL)

        matchSingleToken(lexer, "word", XQueryTokenType.K_WORD)
        matchSingleToken(lexer, "words", XQueryTokenType.K_WORDS)
        matchSingleToken(lexer, "phrase", XQueryTokenType.K_PHRASE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (156) FTTimes")
    fun testFTTimes() {
        val lexer = createLexer()

        matchSingleToken(lexer, "occurs", XQueryTokenType.K_OCCURS)
        matchSingleToken(lexer, "times", XQueryTokenType.K_TIMES)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (157) FTRange")
    fun testFTRange() {
        val lexer = createLexer()

        matchSingleToken(lexer, "exactly", XQueryTokenType.K_EXACTLY)
        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, "least", XQueryTokenType.K_LEAST)
        matchSingleToken(lexer, "most", XQueryTokenType.K_MOST)
        matchSingleToken(lexer, "from", XQueryTokenType.K_FROM)
        matchSingleToken(lexer, "to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (159) FTOrder")
    fun testFTOrder() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ordered", XQueryTokenType.K_ORDERED)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (160) FTWindow")
    fun testFTWindow() {
        val lexer = createLexer()

        matchSingleToken(lexer, "window", XQueryTokenType.K_WINDOW)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (161) FTDistance")
    fun testFTDistance() {
        val lexer = createLexer()

        matchSingleToken(lexer, "distance", XQueryTokenType.K_DISTANCE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (162) FTUnit")
    fun testFTUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "words", XQueryTokenType.K_WORDS)
        matchSingleToken(lexer, "sentences", XQueryTokenType.K_SENTENCES)
        matchSingleToken(lexer, "paragraphs", XQueryTokenType.K_PARAGRAPHS)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (163) FTScope")
    fun testFTScope() {
        val lexer = createLexer()

        matchSingleToken(lexer, "same", XQueryTokenType.K_SAME)
        matchSingleToken(lexer, "different", XQueryTokenType.K_DIFFERENT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (164) FTBigUnit")
    fun testFTBigUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "sentence", XQueryTokenType.K_SENTENCE)
        matchSingleToken(lexer, "paragraph", XQueryTokenType.K_PARAGRAPH)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (165) FTContent")
    fun testFTContent() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, "start", XQueryTokenType.K_START)
        matchSingleToken(lexer, "end", XQueryTokenType.K_END)

        matchSingleToken(lexer, "entire", XQueryTokenType.K_ENTIRE)
        matchSingleToken(lexer, "content", XQueryTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (166) FTMatchOptions")
    fun testFTMatchOptions() {
        val lexer = createLexer()

        matchSingleToken(lexer, "using", XQueryTokenType.K_USING)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (168) FTCaseOption")
    fun testFTCaseOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "case", XQueryTokenType.K_CASE)
        matchSingleToken(lexer, "sensitive", XQueryTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XQueryTokenType.K_INSENSITIVE)

        matchSingleToken(lexer, "lowercase", XQueryTokenType.K_LOWERCASE)
        matchSingleToken(lexer, "uppercase", XQueryTokenType.K_UPPERCASE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (169) FTDiacriticsOption")
    fun testFTDiacriticsOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "diacritics", XQueryTokenType.K_DIACRITICS)
        matchSingleToken(lexer, "sensitive", XQueryTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XQueryTokenType.K_INSENSITIVE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (170) FTStemOption")
    fun testFTStemOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XQueryTokenType.K_NO)
        matchSingleToken(lexer, "stemming", XQueryTokenType.K_STEMMING)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (171) FTThesaurusOption")
    fun testFTThesaurusOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "thesaurus", XQueryTokenType.K_THESAURUS)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)

        matchSingleToken(lexer, "no", XQueryTokenType.K_NO)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (172) FTThesaurusID")
    fun testFTThesaurusID() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, "relationship", XQueryTokenType.K_RELATIONSHIP)
        matchSingleToken(lexer, "levels", XQueryTokenType.K_LEVELS)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (173) FTLiteralRange")
    fun testFTLiteralRange() {
        val lexer = createLexer()

        matchSingleToken(lexer, "exactly", XQueryTokenType.K_EXACTLY)

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, "least", XQueryTokenType.K_LEAST)
        matchSingleToken(lexer, "most", XQueryTokenType.K_MOST)

        matchSingleToken(lexer, "from", XQueryTokenType.K_FROM)
        matchSingleToken(lexer, "to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (174) FTStopWordOption")
    fun testFTStopWordOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "stop", XQueryTokenType.K_STOP)
        matchSingleToken(lexer, "words", XQueryTokenType.K_WORDS)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "no", XQueryTokenType.K_NO)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (175) FTStopWords")
    fun testFTStopWords() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (176) FTStopWordsInclExcl")
    fun testFTStopWordsInclExcl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XPathTokenType.K_UNION)
        matchSingleToken(lexer, "except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (177) FTLanguageOption")
    fun testFTLanguageOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "language", XQueryTokenType.K_LANGUAGE)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (178) FTWildcardOption")
    fun testFTWildCardOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XQueryTokenType.K_NO)
        matchSingleToken(lexer, "wildcards", XQueryTokenType.K_WILDCARDS)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (179) FTExtensionOption")
    fun testFTExtensionOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "option", XQueryTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XQuery and XPath Full Text 1.0 EBNF (180) FTIgnoreOption")
    fun testFTIgnoreOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "without", XQueryTokenType.K_WITHOUT)
        matchSingleToken(lexer, "content", XQueryTokenType.K_CONTENT)
    }
}
