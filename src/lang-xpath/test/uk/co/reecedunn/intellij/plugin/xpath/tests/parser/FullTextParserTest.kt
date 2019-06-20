/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 with Full Text 3.0 - Lexer")
private class FullTextParserTest : ParserTestCase() {
    fun parseResource(resource: String): XPath {
        val file = ResourceVirtualFile(FullTextParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile(FullTextParserTest::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (5) SimpleForClause ; XPath Full Text 1.0 EBNF (6) FTScoreVar")
    internal inner class SimpleForClause_FTScoreVar {
        @Test
        @DisplayName("score")
        fun ftScoreVar() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("score; compact whitespace")
        fun ftScoreVar_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable indicator")
        fun missingVarIndicator() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingVarIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingVarName.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword from ForClause")
        fun forClause_MissingInKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (12) FTContainsExpr")
    internal inner class FTContainsExpr {
        @Test
        @DisplayName("contains text expression")
        fun ftContainsExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("with FTIgnoreOption")
        fun withFTIgnoreOption() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'text' keyword")
        fun missingTextKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContainsExpr_MissingTextKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContainsExpr_MissingTextKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTSelection")
        fun missingFTSelection() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContainsExpr_MissingFTSelection.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContainsExpr_MissingFTSelection.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (76) FTSelection")
    internal inner class FTSelection {
        @Test
        @DisplayName("selection")
        fun ftSelection() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("with single position filter")
        fun ftPosFilter_Single() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOrder.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOrder.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("with multiple position filters")
        fun ftPosFilter_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTSelection_FTPosFilter_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTSelection_FTPosFilter_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (78) FTOr")
    internal inner class FTOr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOr_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTAnd")
        fun missingFTAnd() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOr_MissingFTAnd.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOr_MissingFTAnd.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (79) FTAnd")
    internal inner class FTAnd {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnd.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnd.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnd_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnd_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnd_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnd_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnd_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTMildNot")
        fun missingFTMildNot() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnd_MissingFTMildNot.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnd_MissingFTMildNot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (80) FTMildNot")
    internal inner class FTMildNot {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot_MissingInKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnaryNot")
        fun missingFTUnaryNot() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTMildNot_MissingFTUnaryNot.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTMildNot_MissingFTUnaryNot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("XPath Full Text 1.0 EBNF (81) FTUnaryNot")
    fun ftUnaryNot() {
        val expected = loadResource("tests/parser/xpath-full-text-1.0/FTUnaryNot.txt")
        val actual = parseResource("tests/parser/xpath-full-text-1.0/FTUnaryNot.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (82) FTPrimaryWithOptions ; XPath Full Text 1.0 EBNF (77) FTWeight")
    internal inner class FTPrimaryWithOptions_FTWeight {
        @Test
        @DisplayName("weight")
        fun weight() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWeight.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWeight.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("weight; compact whitespace")
        fun weight_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWeight_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWeight_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWeight_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWeight_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWeight_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWeight_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (83) FTPrimary")
    internal inner class FTPrimary {
        @Nested
        @DisplayName("XPath Full Text 1.0 EBNF (84) FTWords ; XPath Full Text 1.0 EBNF (88) FTTimes")
        internal inner class FTWords_FTTimes {
            @Test
            @DisplayName("words")
            fun words() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurs")
            fun times() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTRange")
            fun missingFTRange() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTTimes_MissingFTRange.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTTimes_MissingFTRange.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'times' keyword")
            fun missingTimesKeyword() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTTimes_MissingTimesKeyword.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTTimes_MissingTimesKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath Full Text 1.0 EBNF (76) FTSelection")
        internal inner class FTSelection {
            @Test
            @DisplayName("parenthesis")
            fun parenthesis() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("parenthesis; compact whitespace")
            fun parenthesis_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTSelection")
            fun missingFTSelection() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath Full Text 1.0 EBNF (86) FTExtensionSelection")
        internal inner class FTExtensionSelection {
            @Test
            @DisplayName("single pragma")
            fun singlePragma() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single pragma; compact whitespace")
            fun singlePragma_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple pragmas")
            fun multiplePragmas() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MultiplePragmas.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MultiplePragmas.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening brace")
            fun missingOpenBrace() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingOpenBrace.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingOpenBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTSelection")
            fun missingFTSelection() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingFTSelection.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingFTSelection.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingCloseBrace() {
                val expected = loadResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingCloseBrace.txt")
                val actual = parseResource("tests/parser/xpath-full-text-1.0/FTExtensionSelection_MissingCloseBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (84) FTWords ; XPath Full Text 1.0 EBNF (87) FTAnyallOption")
    internal inner class FTWords_FTAnyallOption {
        @Test
        @DisplayName("any")
        fun any() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_Any.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_Any.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("any word")
        fun anyWord() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_AnyWord.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_AnyWord.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all")
        fun all() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_All.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_All.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all words")
        fun allWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_AllWords.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_AllWords.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("phrase")
        fun phrase() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_Phrase.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTAnyallOption_Phrase.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (85) FTWordsValue")
    internal inner class FTWordsValue {
        @Test
        @DisplayName("word")
        fun ftWordsValue() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("word; compact whitespace")
        fun ftWordsValue_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("expression")
        fun expr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("expression; compact whitespace")
        fun expr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr from expression")
        fun expr_MissingExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace from expression")
        fun expr_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (89) FTRange")
    internal inner class FTRange {
        @Test
        @DisplayName("exactly")
        fun exactly() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_Exactly.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_Exactly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("exactly; missing AdditiveExpr")
        fun exactly_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: 'at' keyword without least/most qualifier")
        fun at_MissingQualifier() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_At_MissingQualifier.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_At_MissingQualifier.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at least")
        fun atLeast() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at least; missing AdditiveExpr")
        fun atLeast_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at most")
        fun atMost() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_AtMost.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_AtMost.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at most; missing AdditiveExpr")
        fun atMost_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("from/to")
        fun fromTo() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing from AdditiveExpr")
        fun fromTo_MissingFromExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingFromExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingFromExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' keyword")
        fun fromTo_MissingToKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingToKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingToKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing to AdditiveExpr")
        fun fromTo_MissingToExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingToExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTRange_FromTo_MissingToExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("XPath Full Text 1.0 EBNF (91) FTOrder")
    fun ftOrder() {
        val expected = loadResource("tests/parser/xpath-full-text-1.0/FTOrder.txt")
        val actual = parseResource("tests/parser/xpath-full-text-1.0/FTOrder.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (92) FTWindow")
    internal inner class FTWindow {
        @Test
        @DisplayName("window")
        fun window() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AdditiveExpr")
        fun missingAdditiveExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnit")
        fun missingFTUnit() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow_MissingFTUnit.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow_MissingFTUnit.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (112) FTIgnoreOption")
    internal inner class FTIgnoreOption {
        @Test
        @DisplayName("ignore option")
        fun ftIgnoreOption() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("ignore option; compact whitespace")
        fun ftIgnoreOption_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'content' keyword")
        fun missingContentKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_MissingContentKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_MissingContentKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTIgnoreOption_MissingUnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
