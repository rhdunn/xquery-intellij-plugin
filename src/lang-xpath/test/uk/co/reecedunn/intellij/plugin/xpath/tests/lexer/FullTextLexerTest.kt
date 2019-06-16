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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("XPath 3.1 with Full Text 3.0 - Lexer")
class FullTextLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XPathLexer(CodePointRangeImpl())

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (6) FTScoreVar")
    fun testFTScoreVar() {
        val lexer = createLexer()

        matchSingleToken(lexer, "score", XPathTokenType.K_SCORE)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (12) FTContainsExpr")
    fun testFTContainsExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "contains", XPathTokenType.K_CONTAINS)
        matchSingleToken(lexer, "text", XPathTokenType.K_TEXT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (77) FTWeight")
    fun testFTWeight() {
        val lexer = createLexer()

        matchSingleToken(lexer, "weight", XPathTokenType.K_WEIGHT)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (78) FTOr")
    fun testFTOr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftor", XPathTokenType.K_FTOR)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (79) FTAnd")
    fun testFTAnd() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftand", XPathTokenType.K_FTAND)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (80) FTMildNot")
    fun testFTMildNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "not", XPathTokenType.K_NOT)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (81) FTUnaryNot")
    fun testFTUnaryNot() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ftnot", XPathTokenType.K_FTNOT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (83) FTPrimary")
    fun testFTPrimary() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (85) FTWordsValue")
    fun testFTWordsValue() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (86) FTExtensionSelection")
    fun testFTExtensionSelection() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (87) FTAnyallOption")
    fun testFTAnyallOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "any", XPathTokenType.K_ANY)
        matchSingleToken(lexer, "all", XPathTokenType.K_ALL)

        matchSingleToken(lexer, "word", XPathTokenType.K_WORD)
        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "phrase", XPathTokenType.K_PHRASE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (88) FTTimes")
    fun testFTTimes() {
        val lexer = createLexer()

        matchSingleToken(lexer, "occurs", XPathTokenType.K_OCCURS)
        matchSingleToken(lexer, "times", XPathTokenType.K_TIMES)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (89) FTRange")
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
    @DisplayName("XPath 2.0 with Full Text EBNF (91) FTOrder")
    fun testFTOrder() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ordered", XPathTokenType.K_ORDERED)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (92) FTWindow")
    fun testFTWindow() {
        val lexer = createLexer()

        matchSingleToken(lexer, "window", XPathTokenType.K_WINDOW)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (93) FTDistance")
    fun testFTDistance() {
        val lexer = createLexer()

        matchSingleToken(lexer, "distance", XPathTokenType.K_DISTANCE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (94) FTUnit")
    fun testFTUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "sentences", XPathTokenType.K_SENTENCES)
        matchSingleToken(lexer, "paragraphs", XPathTokenType.K_PARAGRAPHS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (95) FTScope")
    fun testFTScope() {
        val lexer = createLexer()

        matchSingleToken(lexer, "same", XPathTokenType.K_SAME)
        matchSingleToken(lexer, "different", XPathTokenType.K_DIFFERENT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (96) FTBigUnit")
    fun testFTBigUnit() {
        val lexer = createLexer()

        matchSingleToken(lexer, "sentence", XPathTokenType.K_SENTENCE)
        matchSingleToken(lexer, "paragraph", XPathTokenType.K_PARAGRAPH)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (97) FTContent")
    fun testFTContent() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "start", XPathTokenType.K_START)
        matchSingleToken(lexer, "end", XPathTokenType.K_END)

        matchSingleToken(lexer, "entire", XPathTokenType.K_ENTIRE)
        matchSingleToken(lexer, "content", XPathTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (99) FTMatchOptions")
    fun testFTMatchOptions() {
        val lexer = createLexer()

        matchSingleToken(lexer, "using", XPathTokenType.K_USING)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (100) FTCaseOption")
    fun testFTCaseOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "case", XPathTokenType.K_CASE)
        matchSingleToken(lexer, "sensitive", XPathTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XPathTokenType.K_INSENSITIVE)

        matchSingleToken(lexer, "lowercase", XPathTokenType.K_LOWERCASE)
        matchSingleToken(lexer, "uppercase", XPathTokenType.K_UPPERCASE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (101) FTDiacriticsOption")
    fun testFTDiacriticsOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "diacritics", XPathTokenType.K_DIACRITICS)
        matchSingleToken(lexer, "sensitive", XPathTokenType.K_SENSITIVE)
        matchSingleToken(lexer, "insensitive", XPathTokenType.K_INSENSITIVE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (102) FTStemOption")
    fun testFTStemOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
        matchSingleToken(lexer, "stemming", XPathTokenType.K_STEMMING)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (103) FTThesaurusOption")
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
    @DisplayName("XPath 2.0 with Full Text EBNF (104) FTThesaurusID")
    fun testFTThesaurusID() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)
        matchSingleToken(lexer, "relationship", XPathTokenType.K_RELATIONSHIP)
        matchSingleToken(lexer, "levels", XPathTokenType.K_LEVELS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (105) FTLiteralRange")
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
    @DisplayName("XPath 2.0 with Full Text EBNF (106) FTStopWordOption")
    fun testFTStopWordOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "stop", XPathTokenType.K_STOP)
        matchSingleToken(lexer, "words", XPathTokenType.K_WORDS)
        matchSingleToken(lexer, "default", XPathTokenType.K_DEFAULT)
        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (107) FTStopWords")
    fun testFTStopWords() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XPathTokenType.K_AT)

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (108) FTStopWordsInclExcl")
    fun testFTStopWordsInclExcl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XPathTokenType.K_UNION)
        matchSingleToken(lexer, "except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (109) FTLanguageOption")
    fun testFTLanguageOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "language", XPathTokenType.K_LANGUAGE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (110) FTWildcardOption")
    fun testFTWildCardOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "no", XPathTokenType.K_NO)
        matchSingleToken(lexer, "wildcards", XPathTokenType.K_WILDCARDS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (111) FTExtensionOption")
    fun testFTExtensionOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "option", XPathTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (112) FTIgnoreOption")
    fun testFTIgnoreOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "without", XPathTokenType.K_WITHOUT)
        matchSingleToken(lexer, "content", XPathTokenType.K_CONTENT)
    }
}
