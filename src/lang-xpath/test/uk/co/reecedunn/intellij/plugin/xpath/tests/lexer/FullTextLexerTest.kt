/*
 * Copyright (C) 2016-2019, 2021 Reece H. Dunn
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
    override val lexer: Lexer = XPathLexer(CodePointRangeImpl())

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (6) FTScoreVar")
    fun ftScoreVar() {
        token("score", XPathTokenType.K_SCORE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (12) FTContainsExpr")
    fun ftContainsExpr() {
        token("contains", XPathTokenType.K_CONTAINS)
        token("text", XPathTokenType.K_TEXT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (36) Pragma ; XPath 2.0 with Full Text EBNF (37) PragmaContents")
    fun pragma() {
        token("(#", 8, XPathTokenType.PRAGMA_BEGIN)
        token("#)", 0, XPathTokenType.PRAGMA_END)

        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("#", XPathTokenType.FUNCTION_REF_OPERATOR)

        tokenize("(#  let:for  6^gkgw~*#g#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("  ", XPathTokenType.WHITE_SPACE)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(":", XPathTokenType.QNAME_SEPARATOR)
            token("for", XPathTokenType.NCNAME)
            token("  ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("6^gkgw~*#g", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let ##)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("#", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let 2") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("2", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("(#let ") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
        }

        tokenize("(#let~~~#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(10)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let~~~") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("(#:let 2#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token(":", XPathTokenType.QNAME_SEPARATOR)
            state(9)
            token("let", XPathTokenType.NCNAME)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("2", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#~~~#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(10)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#~~~") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (77) FTWeight")
    fun ftWeight() {
        token("weight", XPathTokenType.K_WEIGHT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (78) FTOr")
    fun ftOr() {
        token("ftor", XPathTokenType.K_FTOR)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (79) FTAnd")
    fun ftAnd() {
        token("ftand", XPathTokenType.K_FTAND)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (80) FTMildNot")
    fun ftMildNot() {
        token("not", XPathTokenType.K_NOT)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (81) FTUnaryNot")
    fun ftUnaryNot() {
        token("ftnot", XPathTokenType.K_FTNOT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (83) FTPrimary")
    fun ftPrimary() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (85) FTWordsValue")
    fun ftWordsValue() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (86) FTExtensionSelection")
    fun ftExtensionSelection() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (87) FTAnyallOption")
    fun ftAnyallOption() {
        token("any", XPathTokenType.K_ANY)
        token("all", XPathTokenType.K_ALL)

        token("word", XPathTokenType.K_WORD)
        token("words", XPathTokenType.K_WORDS)
        token("phrase", XPathTokenType.K_PHRASE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (88) FTTimes")
    fun ftTimes() {
        token("occurs", XPathTokenType.K_OCCURS)
        token("times", XPathTokenType.K_TIMES)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (89) FTRange")
    fun ftRange() {
        token("exactly", XPathTokenType.K_EXACTLY)
        token("at", XPathTokenType.K_AT)
        token("least", XPathTokenType.K_LEAST)
        token("most", XPathTokenType.K_MOST)
        token("from", XPathTokenType.K_FROM)
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (91) FTOrder")
    fun ftOrder() {
        token("ordered", XPathTokenType.K_ORDERED)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (92) FTWindow")
    fun ftWindow() {
        token("window", XPathTokenType.K_WINDOW)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (93) FTDistance")
    fun ftDistance() {
        token("distance", XPathTokenType.K_DISTANCE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (94) FTUnit")
    fun ftUnit() {
        token("words", XPathTokenType.K_WORDS)
        token("sentences", XPathTokenType.K_SENTENCES)
        token("paragraphs", XPathTokenType.K_PARAGRAPHS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (95) FTScope")
    fun ftScope() {
        token("same", XPathTokenType.K_SAME)
        token("different", XPathTokenType.K_DIFFERENT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (96) FTBigUnit")
    fun ftBigUnit() {
        token("sentence", XPathTokenType.K_SENTENCE)
        token("paragraph", XPathTokenType.K_PARAGRAPH)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (97) FTContent")
    fun ftContent() {
        token("at", XPathTokenType.K_AT)
        token("start", XPathTokenType.K_START)
        token("end", XPathTokenType.K_END)

        token("entire", XPathTokenType.K_ENTIRE)
        token("content", XPathTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (99) FTMatchOptions")
    fun ftMatchOptions() {
        token("using", XPathTokenType.K_USING)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (100) FTCaseOption")
    fun ftCaseOption() {
        token("case", XPathTokenType.K_CASE)
        token("sensitive", XPathTokenType.K_SENSITIVE)
        token("insensitive", XPathTokenType.K_INSENSITIVE)

        token("lowercase", XPathTokenType.K_LOWERCASE)
        token("uppercase", XPathTokenType.K_UPPERCASE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (101) FTDiacriticsOption")
    fun ftDiacriticsOption() {
        token("diacritics", XPathTokenType.K_DIACRITICS)
        token("sensitive", XPathTokenType.K_SENSITIVE)
        token("insensitive", XPathTokenType.K_INSENSITIVE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (102) FTStemOption")
    fun ftStemOption() {
        token("no", XPathTokenType.K_NO)
        token("stemming", XPathTokenType.K_STEMMING)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (103) FTThesaurusOption")
    fun ftThesaurusOption() {
        token("thesaurus", XPathTokenType.K_THESAURUS)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)

        token("no", XPathTokenType.K_NO)
        token("default", XPathTokenType.K_DEFAULT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (104) FTThesaurusID")
    fun ftThesaurusID() {
        token("at", XPathTokenType.K_AT)
        token("relationship", XPathTokenType.K_RELATIONSHIP)
        token("levels", XPathTokenType.K_LEVELS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (105) FTLiteralRange")
    fun ftLiteralRange() {
        token("exactly", XPathTokenType.K_EXACTLY)

        token("at", XPathTokenType.K_AT)
        token("least", XPathTokenType.K_LEAST)
        token("most", XPathTokenType.K_MOST)

        token("from", XPathTokenType.K_FROM)
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (106) FTStopWordOption")
    fun ftStopWordOption() {
        token("stop", XPathTokenType.K_STOP)
        token("words", XPathTokenType.K_WORDS)
        token("default", XPathTokenType.K_DEFAULT)
        token("no", XPathTokenType.K_NO)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (107) FTStopWords")
    fun ftStopWords() {
        token("at", XPathTokenType.K_AT)

        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (108) FTStopWordsInclExcl")
    fun ftStopWordsInclExcl() {
        token("union", XPathTokenType.K_UNION)
        token("except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (109) FTLanguageOption")
    fun ftLanguageOption() {
        token("language", XPathTokenType.K_LANGUAGE)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (110) FTWildcardOption")
    fun ftWildCardOption() {
        token("no", XPathTokenType.K_NO)
        token("wildcards", XPathTokenType.K_WILDCARDS)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (111) FTExtensionOption")
    fun ftExtensionOption() {
        token("option", XPathTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XPath 2.0 with Full Text EBNF (112) FTIgnoreOption")
    fun ftIgnoreOption() {
        token("without", XPathTokenType.K_WITHOUT)
        token("content", XPathTokenType.K_CONTENT)
    }

    @Test
    @DisplayName("XPath 3.0 with Full Text EBNF (36) Pragma ; XPath 3.0 with Full Text EBNF (142) BracedURILiteral")
    fun bracedURILiteral_Pragma() {
        tokenize("Q", 0, 1, 8) {
            token("Q", XPathTokenType.NCNAME)
            state(9)
        }

        tokenize("Q{", 0, 2, 8) {
            token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
            state(31)
        }

        tokenize("Q{Hello World}", 0, 14, 8) {
            token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
            state(31)
            token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("}", XPathTokenType.BRACED_URI_LITERAL_END)
            state(9)
        }

        // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
        tokenize("Q{A\"\"B''C{{D}}E}", 0, 16, 8) {
            token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
            state(31)
            token("A\"\"B''C", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("{", XPathTokenType.BAD_CHARACTER)
            token("{", XPathTokenType.BAD_CHARACTER)
            token("D", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("}", XPathTokenType.BRACED_URI_LITERAL_END)
            state(9)
            token("}E}", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }
    }
}
