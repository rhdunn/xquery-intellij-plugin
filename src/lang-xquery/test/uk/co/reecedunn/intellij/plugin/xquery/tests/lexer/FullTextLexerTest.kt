/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCaseEx
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery 3.1 with Full Text 3.0 - Lexer")
class FullTextLexerTest : LexerTestCaseEx() {
    override val lexer: Lexer = run {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0,
            XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0,
            XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (24) FTOptionDecl")
    fun ftOptionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("ft-option", XQueryTokenType.K_FT_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (37) FTScoreVar")
    fun ftScoreVar() {
        token("score", XPathTokenType.K_SCORE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (51) FTContainsExpr")
    fun ftContainsExpr() {
        token("contains", XPathTokenType.K_CONTAINS)
        token("text", XPathTokenType.K_TEXT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (145) FTWeight")
    fun ftWeight() {
        token("weight", XPathTokenType.K_WEIGHT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (146) FTOr")
    fun ftOr() {
        token("ftor", XPathTokenType.K_FTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (147) FTAnd")
    fun ftAnd() {
        token("ftand", XPathTokenType.K_FTAND)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (148) FTMildNot")
    fun ftMildNot() {
        token("not", XPathTokenType.K_NOT)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (149) FTUnaryNot")
    fun ftUnaryNot() {
        token("ftnot", XPathTokenType.K_FTNOT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (151) FTPrimary")
    fun ftPrimary() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (153) FTWordsValue")
    fun ftWordsValue() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (154) FTExtensionSelection")
    fun ftExtensionSelection() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (155) FTAnyallOption")
    fun ftAnyallOption() {
        token("any", XPathTokenType.K_ANY)
        token("all", XPathTokenType.K_ALL)

        token("word", XPathTokenType.K_WORD)
        token("words", XPathTokenType.K_WORDS)
        token("phrase", XPathTokenType.K_PHRASE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (156) FTTimes")
    fun ftTimes() {
        token("occurs", XPathTokenType.K_OCCURS)
        token("times", XPathTokenType.K_TIMES)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (157) FTRange")
    fun ftRange() {
        token("exactly", XPathTokenType.K_EXACTLY)
        token("at", XPathTokenType.K_AT)
        token("least", XPathTokenType.K_LEAST)
        token("most", XPathTokenType.K_MOST)
        token("from", XPathTokenType.K_FROM)
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (159) FTOrder")
    fun ftOrder() {
        token("ordered", XPathTokenType.K_ORDERED)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (160) FTWindow")
    fun ftWindow() {
        token("window", XPathTokenType.K_WINDOW)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (161) FTDistance")
    fun ftDistance() {
        token("distance", XPathTokenType.K_DISTANCE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (162) FTUnit")
    fun ftUnit() {
        token("words", XPathTokenType.K_WORDS)
        token("sentences", XPathTokenType.K_SENTENCES)
        token("paragraphs", XPathTokenType.K_PARAGRAPHS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (163) FTScope")
    fun ftScope() {
        token("same", XPathTokenType.K_SAME)
        token("different", XPathTokenType.K_DIFFERENT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (164) FTBigUnit")
    fun ftBigUnit() {
        token("sentence", XPathTokenType.K_SENTENCE)
        token("paragraph", XPathTokenType.K_PARAGRAPH)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (165) FTContent")
    fun ftContent() {
        token("at", XPathTokenType.K_AT)
        token("start", XPathTokenType.K_START)
        token("end", XPathTokenType.K_END)

        token("entire", XPathTokenType.K_ENTIRE)
        token("content", XPathTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (166) FTMatchOptions")
    fun ftMatchOptions() {
        token("using", XPathTokenType.K_USING)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (168) FTCaseOption")
    fun ftCaseOption() {
        token("case", XPathTokenType.K_CASE)
        token("sensitive", XPathTokenType.K_SENSITIVE)
        token("insensitive", XPathTokenType.K_INSENSITIVE)

        token("lowercase", XPathTokenType.K_LOWERCASE)
        token("uppercase", XPathTokenType.K_UPPERCASE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (169) FTDiacriticsOption")
    fun ftDiacriticsOption() {
        token("diacritics", XPathTokenType.K_DIACRITICS)
        token("sensitive", XPathTokenType.K_SENSITIVE)
        token("insensitive", XPathTokenType.K_INSENSITIVE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (170) FTStemOption")
    fun ftStemOption() {
        token("no", XPathTokenType.K_NO)
        token("stemming", XPathTokenType.K_STEMMING)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (171) FTThesaurusOption")
    fun ftThesaurusOption() {
        token("thesaurus", XPathTokenType.K_THESAURUS)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)

        token("no", XPathTokenType.K_NO)
        token("default", XPathTokenType.K_DEFAULT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (172) FTThesaurusID")
    fun ftThesaurusID() {
        token("at", XPathTokenType.K_AT)
        token("relationship", XPathTokenType.K_RELATIONSHIP)
        token("levels", XPathTokenType.K_LEVELS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (173) FTLiteralRange")
    fun ftLiteralRange() {
        token("exactly", XPathTokenType.K_EXACTLY)

        token("at", XPathTokenType.K_AT)
        token("least", XPathTokenType.K_LEAST)
        token("most", XPathTokenType.K_MOST)

        token("from", XPathTokenType.K_FROM)
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (174) FTStopWordOption")
    fun ftStopWordOption() {
        token("stop", XPathTokenType.K_STOP)
        token("words", XPathTokenType.K_WORDS)
        token("default", XPathTokenType.K_DEFAULT)
        token("no", XPathTokenType.K_NO)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (175) FTStopWords")
    fun ftStopWords() {
        token("at", XPathTokenType.K_AT)

        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (176) FTStopWordsInclExcl")
    fun ftStopWordsInclExcl() {
        token("union", XPathTokenType.K_UNION)
        token("except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (177) FTLanguageOption")
    fun ftLanguageOption() {
        token("language", XPathTokenType.K_LANGUAGE)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (178) FTWildcardOption")
    fun ftWildCardOption() {
        token("no", XPathTokenType.K_NO)
        token("wildcards", XPathTokenType.K_WILDCARDS)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (179) FTExtensionOption")
    fun ftExtensionOption() {
        token("option", XPathTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 with Full Text EBNF (180) FTIgnoreOption")
    fun ftIgnoreOption() {
        token("without", XPathTokenType.K_WITHOUT)
        token("content", XPathTokenType.K_CONTENT)
    }
}
