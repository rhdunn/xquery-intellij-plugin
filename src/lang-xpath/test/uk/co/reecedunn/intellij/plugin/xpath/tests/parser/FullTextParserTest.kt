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
    internal inner class FTPrimaryWithOptions {
        @Nested
        @DisplayName("XPath Full Text 1.0 EBNF (77) FTWeight")
        internal inner class FTWeight {
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

        @Test
        @DisplayName("match options and weight")
        fun matchOptionsAndWeight() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.xq")
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
        @DisplayName("error recovery: exactly; missing AdditiveExpr")
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
    @DisplayName("XPath Full Text 1.0 EBNF (92) FTWindow ; XPath Full Text 1.0 EBNF (94) FTUnit")
    internal inner class FTWindow {
        @Test
        @DisplayName("words")
        fun words() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Words.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Words.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("sentences")
        fun sentences() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Sentences.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Sentences.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("paragraphs")
        fun paragraphs() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Paragraphs.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWindow_FTUnit_Paragraphs.xq")
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
    @DisplayName("XPath Full Text 1.0 EBNF (93) FTDistance ; XPath Full Text 1.0 EBNF (94) FTUnit")
    internal inner class FTDistance {
        @Test
        @DisplayName("distance")
        fun distance() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDistance.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDistance.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTRange")
        fun missingFTRange() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDistance_MissingFTRange.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDistance_MissingFTRange.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnit")
        fun missingFTUnit() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDistance_MissingFTUnit.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDistance_MissingFTUnit.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (95) FTScope ; XPath Full Text 1.0 EBNF (96) FTBigUnit")
    internal inner class FTScope {
        @Test
        @DisplayName("same sentence")
        fun sameSentence() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTScope_SameSentence.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTScope_SameSentence.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("same paragraph")
        fun sameParagraph() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTScope_SameParagraph.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTScope_SameParagraph.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: same; missing FTBigUnit")
        fun same_MissingFTBigUnit() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTScope_Same_MissingFTBigUnit.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTScope_Same_MissingFTBigUnit.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("different sentence")
        fun differentSentence() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTScope_DifferentSentence.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTScope_DifferentSentence.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: different; missing FTBigUnit")
        fun different_MissingFTBigUnit() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTScope_Different_MissingFTBigUnit.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTScope_Different_MissingFTBigUnit.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (97) FTContent")
    internal inner class FTContent {
        @Test
        @DisplayName("at start")
        fun atStart() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContent_AtStart.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContent_AtStart.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at end")
        fun atEnd() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContent_AtEnd.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContent_AtEnd.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at; missing 'start'/'end' keyword")
        fun at_MissingStartEndKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContent_At_MissingStartEndKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContent_At_MissingStartEndKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("entire content")
        fun entireContent() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContent_EntireContent.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContent_EntireContent.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: entire; missing 'content' keyword")
        fun entireContent_MissingContentKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTContent_EntireContent_MissingContentKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTContent_EntireContent_MissingContentKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (100) FTCaseOption")
    internal inner class FTCaseOption {
        @Test
        @DisplayName("lower case")
        fun lowerCase() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_LowerCase.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_LowerCase.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: lower case; missing 'using' keyword")
        fun lowerCase_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_LowerCase_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_LowerCase_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("upper case")
        fun upperCase() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_UpperCase.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_UpperCase.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: upper case; missing 'using' keyword")
        fun upperCase_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_UpperCase_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_UpperCase_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("case sensitive")
        fun caseSensitive() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_Sensitive.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_Sensitive.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("case insensitive")
        fun caseInsensitive() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_Insensitive.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_Insensitive.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: case; missing 'sensitivity' keyword")
        fun case_MissingSensitivityKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: case; missing 'using' keyword")
        fun case_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTCaseOption_Case_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (101) FTDiacriticsOption")
    internal inner class FTDiacriticsOption {
        @Test
        @DisplayName("sensitive")
        fun sensitive() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_Sensitive.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_Sensitive.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("insensitive")
        fun insensitive() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_Insensitive.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_Insensitive.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'sensitivity' keyword")
        fun missingSensitivityKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTDiacriticsOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (102) FTStemOption")
    internal inner class FTStemOption {
        @Test
        @DisplayName("stemming")
        fun stemming() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStemOption.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStemOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: stemming; missing 'using' keyword")
        fun stemming_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStemOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStemOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no stemming")
        fun noStemming() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStemOption_NoStemming.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStemOption_NoStemming.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stemming; missing 'using' keyword")
        fun noStemming_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStemOption_NoStemming_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStemOption_NoStemming_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (103) FTThesaurusOption")
    internal inner class FTThesaurusOption {
        @Test
        @DisplayName("default")
        fun default() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Default.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Default.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: thesaurus; missing 'using' keyword")
        fun default_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no thesaurus")
        fun noThesaurus() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoThesaurus.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoThesaurus.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no thesaurus; missing 'using' keyword")
        fun noThesaurus_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoThesaurus_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoThesaurus_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("thesaurus id")
        fun thesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTThesaurusID")
        fun missingThesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_MissingThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: single; default")
        fun parenthesized_Default() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Default.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Default.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: single; thesaurus id")
        fun parenthesized_ThesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: compact whitespace")
        fun parenthesized_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: missing FTThesaurusID")
        fun parenthesized_MissingThesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: missing closing parenthesis")
        fun parenthesized_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: two items")
        fun parenthesized_TwoItems() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: two items; compact whitespace")
        fun parenthesized_TwoItems_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: two items; missing FTThesaurusID")
        fun parenthesized_TwoItems_MissingThesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: multiple")
        fun parenthesized_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: multiple; compact whitespace")
        fun parenthesized_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: multiple; default after first item")
        fun parenthesized_Multiple_DefaultAfterFirstItem() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple items without parenthesis")
        fun noParenthesis_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple items without parenthesis; unexpected closing parenthesis")
        fun noParenthesis_Multiple_UnexpectedClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: single item; unexpected closing parenthesis")
        fun unexpectedClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (104) FTThesaurusID")
    internal inner class FTThesaurusID {
        @Test
        @DisplayName("thesaurus id")
        fun thesaurusID() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("thesaurus id; compact whitespace")
        fun thesaurusID_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URILiteral")
        fun missingUriLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_MissingUriLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_MissingUriLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("relationship")
        fun relationship() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("relationship; compact whitespace")
        fun relationship_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: relationship; missing StringLiteral")
        fun relationship_MissingStringLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("literal range")
        fun literalRange() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: literal range; missing 'levels' keyword")
        fun literalRange_MissingLevelsKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (105) FTLiteralRange")
    internal inner class FTLiteralRange {
        @Test
        @DisplayName("exactly")
        fun exactly() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("exactly; compact whitespace")
        fun exactly_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: exactly; missing IntegerLiteral")
        fun exactly_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at; missing qualifier")
        fun at_MissingQualifier() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_At_MissingQualifier.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_At_MissingQualifier.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at least")
        fun atLeast() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at least; compact whitespace")
        fun atLeast_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at least; missing IntegerLiteral")
        fun atLeast_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at most")
        fun atMost() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("at most; compact whitespace")
        fun atMost_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntegerLiteral")
        fun atMost_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("from/to")
        fun fromTo() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("from/to; compact whitespace")
        fun fromTo_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'from' IntegerLiteral")
        fun fromTo_MissingFromLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' keyword")
        fun fromTo_MissingToKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' IntegerLiteral")
        fun fromTo_MissingToLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (106) FTStopWordOption")
    internal inner class FTStopWordOption {
        @Test
        @DisplayName("default")
        fun default() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: stop words; missing 'words' keyword")
        fun missingWordsKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing stop words")
        fun missingStopWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_MissingStopWords.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_MissingStopWords.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no stop words")
        fun noStopWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stop words; missing 'words' keyword")
        fun noStopWords_MissingWordsKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stop words; missing 'using' keyword")
        fun noStopWords_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_NoStopWords_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stop words")
        fun stopWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude")
        fun default_FTStopWordsInclExcl() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; compact whitespace")
        fun default_FTStopWordsInclExcl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; multiple")
        fun default_FTStopWordsInclExcl_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; multiple; compact whitespace")
        fun default_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude")
        fun stopWords_FTStopWordsInclExcl() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; compact whitespace")
        fun stopWords_FTStopWordsInclExcl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; multiple")
        fun stopWords_FTStopWordsInclExcl_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; multiple; compact whitespace")
        fun stopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (107) FTStopWords")
    internal inner class FTStopWords {
        @Test
        @DisplayName("location")
        fun location() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("location; compact whitespace")
        fun location_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: location; missing URILiteral")
        fun location_MissingUriLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location_MissingUriLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_Location_MissingUriLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: list; missing stop word")
        fun list_MissingStopWord() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_MissingStopWord.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_MissingStopWord.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; single")
        fun list_Single() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; single; compact whitespace")
        fun list_Single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Single_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; two words")
        fun list_TwoWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; two words; compact whitespace")
        fun list_TwoWords_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: list; two words; missing stop word")
        fun list_TwoWords_MissingStopWord() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; multiple")
        fun list_Multiple() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("list; multiple; compact whitespace")
        fun list_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (108) FTStopWordsInclExcl")
    internal inner class FTStopWordsInclExcl {
        @Test
        @DisplayName("include")
        fun include() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("include; compact whitespace")
        fun include_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("exclude")
        fun exclude() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Exclude.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Exclude.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("exclude; compact whitespace")
        fun exclude_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing stop words")
        fun missingStopWords() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_MissingStopWords.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTStopWordsInclExcl_MissingStopWords.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (109) FTLanguageOption")
    internal inner class FTLanguageOption {
        @Test
        @DisplayName("language")
        fun language() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLanguageOption.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLanguageOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("language; compact whitespace")
        fun language_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing StringLiteral")
        fun missingStringLiteral() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_MissingStringLiteral.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_MissingStringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTLanguageOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (110) FTWildCardOption")
    internal inner class FTWildCardOption {
        @Test
        @DisplayName("wildcards")
        fun wildcards() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWildCardOption.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWildCardOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: wildcards; mising 'using' keyword")
        fun wildcards_missingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no wildcards")
        fun noWildCards() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_NoWildCards.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_NoWildCards.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no wildcards; mising 'using' keyword")
        fun noWildCards_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_NoWildCards_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/xpath-full-text-1.0/FTWildCardOption_NoWildCards_MissingUsingKeyword.xq")
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
