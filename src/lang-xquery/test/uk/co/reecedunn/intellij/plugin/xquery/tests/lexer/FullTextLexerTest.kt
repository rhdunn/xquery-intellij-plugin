/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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

@DisplayName("XQuery 3.1 with Full Text 3.0 - Lexer")
class FullTextLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (24) FTOptionDecl")
    fun testFTOptionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "ft-option", XQueryTokenType.K_FT_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (37) FTScoreVar")
    fun testFTScoreVar() {
        val lexer = createLexer()

        matchSingleToken(lexer, "score", XPathTokenType.K_SCORE)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (51) FTContainsExpr")
    fun testFTContainsExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "contains", XPathTokenType.K_CONTAINS)
        matchSingleToken(lexer, "text", XPathTokenType.K_TEXT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (145) FTWeight")
    fun testFTWeight() {
        val lexer = createLexer()

        matchSingleToken(lexer, "weight", XPathTokenType.K_WEIGHT)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (146) FTOr")
    fun testFTOr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftor", XPathTokenType.K_FTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (147) FTAnd")
    fun testFTAnd() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftand", XPathTokenType.K_FTAND)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (148) FTMildNot")
    fun testFTMildNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "not", XPathTokenType.K_NOT)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (149) FTUnaryNot")
    fun testFTUnaryNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftnot", XPathTokenType.K_FTNOT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (151) FTPrimary")
    fun testFTPrimary() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (153) FTWordsValue")
    fun testFTWordsValue() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (154) FTExtensionSelection")
    fun testFTExtensionSelection() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (155) FTAnyallOption")
    fun testFTAnyallOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "any", XPathTokenType.K_ANY)
        matchSingleToken(lexer, "all", XPathTokenType.K_ALL)

        matchSingleToken(lexer, "word", XPathTokenType.K_WORD)
        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "phrase", XPathTokenType.K_PHRASE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (156) FTTimes")
    fun testFTTimes() {
        val lexer = createLexer()

        matchSingleToken(lexer, "occurs", XPathTokenType.K_OCCURS)
        matchSingleToken(lexer, "times", XPathTokenType.K_TIMES)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (157) FTRange")
    fun testFTRange() {
        val lexer = createLexer()

        matchSingleToken(lexer, "exactly", XPathTokenType.K_EXACTLY)
        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "least", XPathTokenType.K_LEAST)
        matchSingleToken(lexer, "most", XPathTokenType.K_MOST)
        matchSingleToken(lexer, "from", XPathTokenType.K_FROM)
        matchSingleToken(lexer, "to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (159) FTOrder")
    fun testFTOrder() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ordered", XPathTokenType.K_ORDERED)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (160) FTWindow")
    fun testFTWindow() {
        val lexer = createLexer()

        matchSingleToken(lexer, "window", XPathTokenType.K_WINDOW)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (161) FTDistance")
    fun testFTDistance() {
        val lexer = createLexer()

        matchSingleToken(lexer, "distance", XPathTokenType.K_DISTANCE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (162) FTUnit")
    fun testFTUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "sentences", XPathTokenType.K_SENTENCES)
        matchSingleToken(lexer, "paragraphs", XPathTokenType.K_PARAGRAPHS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (163) FTScope")
    fun testFTScope() {
        val lexer = createLexer()

        matchSingleToken(lexer, "same", XPathTokenType.K_SAME)
        matchSingleToken(lexer, "different", XPathTokenType.K_DIFFERENT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (164) FTBigUnit")
    fun testFTBigUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "sentence", XPathTokenType.K_SENTENCE)
        matchSingleToken(lexer, "paragraph", XPathTokenType.K_PARAGRAPH)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (165) FTContent")
    fun testFTContent() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "start", XPathTokenType.K_START)
        matchSingleToken(lexer, "end", XPathTokenType.K_END)

        matchSingleToken(lexer, "entire", XPathTokenType.K_ENTIRE)
        matchSingleToken(lexer, "content", XPathTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (166) FTMatchOptions")
    fun testFTMatchOptions() {
        val lexer = createLexer()

        matchSingleToken(lexer, "using", XPathTokenType.K_USING)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (168) FTCaseOption")
    fun testFTCaseOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "case", XPathTokenType.K_CASE)
        matchSingleToken(lexer, "sensitive", XPathTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XPathTokenType.K_INSENSITIVE)

        matchSingleToken(lexer, "lowercase", XPathTokenType.K_LOWERCASE)
        matchSingleToken(lexer, "uppercase", XPathTokenType.K_UPPERCASE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (169) FTDiacriticsOption")
    fun testFTDiacriticsOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "diacritics", XPathTokenType.K_DIACRITICS)
        matchSingleToken(lexer, "sensitive", XPathTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XPathTokenType.K_INSENSITIVE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (170) FTStemOption")
    fun testFTStemOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
        matchSingleToken(lexer, "stemming", XPathTokenType.K_STEMMING)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (171) FTThesaurusOption")
    fun testFTThesaurusOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "thesaurus", XPathTokenType.K_THESAURUS)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)

        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
        matchSingleToken(lexer, "default", XPathTokenType.K_DEFAULT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (172) FTThesaurusID")
    fun testFTThesaurusID() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "relationship", XPathTokenType.K_RELATIONSHIP)
        matchSingleToken(lexer, "levels", XPathTokenType.K_LEVELS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (173) FTLiteralRange")
    fun testFTLiteralRange() {
        val lexer = createLexer()

        matchSingleToken(lexer, "exactly", XPathTokenType.K_EXACTLY)

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "least", XPathTokenType.K_LEAST)
        matchSingleToken(lexer, "most", XPathTokenType.K_MOST)

        matchSingleToken(lexer, "from", XPathTokenType.K_FROM)
        matchSingleToken(lexer, "to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (174) FTStopWordOption")
    fun testFTStopWordOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "stop", XPathTokenType.K_STOP)
        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "default", XPathTokenType.K_DEFAULT)
        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (175) FTStopWords")
    fun testFTStopWords() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (176) FTStopWordsInclExcl")
    fun testFTStopWordsInclExcl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XPathTokenType.K_UNION)
        matchSingleToken(lexer, "except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (177) FTLanguageOption")
    fun testFTLanguageOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "language", XPathTokenType.K_LANGUAGE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (178) FTWildcardOption")
    fun testFTWildCardOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
        matchSingleToken(lexer, "wildcards", XPathTokenType.K_WILDCARDS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (179) FTExtensionOption")
    fun testFTExtensionOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "option", XPathTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (180) FTIgnoreOption")
    fun testFTIgnoreOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "without", XPathTokenType.K_WITHOUT)
        matchSingleToken(lexer, "content", XPathTokenType.K_CONTENT)
    }
}
