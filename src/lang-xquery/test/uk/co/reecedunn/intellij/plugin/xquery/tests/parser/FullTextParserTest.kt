// Copyright (C) 2017-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName", "Reformat", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 with Full Text 3.0 - Parser")
class FullTextParserTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("FullTextParserTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (24) FTOptionDecl ; XQuery Full Text 1.0 EBNF (167) FTMatchOptions")
    internal inner class FTOptionDecl {
        @Test
        @DisplayName("option declaration")
        fun ftOptionDecl() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTMatchOptions")
        fun missingFTMatchOptions() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTMatchOption")
        fun missingFTMatchOption() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl_FTMatchOptions_MissingFTMatchOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl_FTMatchOptions_MissingFTMatchOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: 'no' keyword without option qualifier")
        fun noKeywordOnly() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl_FTMatchOptions_NoKeywordOnly.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl_FTMatchOptions_NoKeywordOnly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (35) ForClause ; XQuery Full Text 1.0 EBNF (37) FTScoreVar")
    internal inner class ForClause_FTScoreVar {
        @Test
        @DisplayName("score")
        fun ftScoreVar() {
            val expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.txt")
            val actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("score; compact whitespace")
        fun ftScoreVar_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable indicator")
        fun missingVarIndicator() {
            val expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingVarIndicator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingVarName.txt")
            val actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword from ForClause")
        fun forClause_MissingInKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/ForBinding_FTScoreVar_MissingInKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (38) LetClause ; XQuery Full Text 1.0 EBNF (37) FTScoreVar")
    internal inner class LetClause_FTScoreVar {
        @Test
        @DisplayName("score")
        fun ftScoreVar() {
            val expected = loadResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar.txt")
            val actual = parseResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("score; compact whitespace")
        fun ftScoreVar_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/LetBinding_FTScoreVar_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (51) FTContainsExpr")
    internal inner class FTContainsExpr {
        @Test
        @DisplayName("contains text expression")
        fun ftContainsExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with FTIgnoreOption")
        fun withFTIgnoreOption() {
            val expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'text' keyword")
        fun missingTextKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContainsExpr_MissingTextKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContainsExpr_MissingTextKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTSelection")
        fun missingFTSelection() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContainsExpr_MissingFTSelection.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContainsExpr_MissingFTSelection.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (144) FTSelection")
    internal inner class FTSelection {
        @Test
        @DisplayName("selection")
        fun ftSelection() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with single position filter")
        fun ftPosFilter_Single() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOrder.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOrder.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with multiple position filters")
        fun ftPosFilter_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTSelection_FTPosFilter_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTSelection_FTPosFilter_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (146) FTOr")
    internal inner class FTOr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOr_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOr_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTAnd")
        fun missingFTAnd() {
            val expected = loadResource("tests/parser/full-text-1.0/FTOr_MissingFTAnd.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTOr_MissingFTAnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (147) FTAnd")
    internal inner class FTAnd {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnd.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnd_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnd_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnd_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnd_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTMildNot")
        fun missingFTMildNot() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnd_MissingFTMildNot.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnd_MissingFTMildNot.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (148) FTMildNot")
    internal inner class FTMildNot {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot_MissingInKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnaryNot")
        fun missingFTUnaryNot() {
            val expected = loadResource("tests/parser/full-text-1.0/FTMildNot_MissingFTUnaryNot.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTMildNot_MissingFTUnaryNot.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Test
    @DisplayName("XQuery Full Text 1.0 EBNF (149) FTUnaryNot")
    fun ftUnaryNot() {
        val expected = loadResource("tests/parser/full-text-1.0/FTUnaryNot.txt")
        val actual = parseResource("tests/parser/full-text-1.0/FTUnaryNot.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (150) FTPrimaryWithOptions ; XQuery Full Text 1.0 EBNF (145) FTWeight")
    internal inner class FTPrimaryWithOptions {
        @Nested
        @DisplayName("XQuery Full Text 1.0 EBNF (145) FTWeight")
        internal inner class FTWeight {
            @Test
            @DisplayName("weight")
            fun weight() {
                val expected = loadResource("tests/parser/full-text-1.0/FTWeight.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTWeight.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("weight; compact whitespace")
            fun weight_CompactWhitespace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTWeight_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTWeight_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/full-text-1.0/FTWeight_MissingExpr.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTWeight_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTWeight_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTWeight_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("match options and weight")
        fun matchOptionsAndWeight() {
            val expected = loadResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTPrimaryWithOptions_MatchOptionsAndWeight.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (151) FTPrimary")
    internal inner class FTPrimary {
        @Nested
        @DisplayName("XQuery Full Text 1.0 EBNF (152) FTWords ; XQuery Full Text 1.0 EBNF (156) FTTimes")
        internal inner class FTWords_FTTimes {
            @Test
            @DisplayName("words")
            fun words() {
                val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("occurs")
            fun times() {
                val expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTRange")
            fun missingFTRange() {
                val expected = loadResource("tests/parser/full-text-1.0/FTTimes_MissingFTRange.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTTimes_MissingFTRange.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'times' keyword")
            fun missingTimesKeyword() {
                val expected = loadResource("tests/parser/full-text-1.0/FTTimes_MissingTimesKeyword.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTTimes_MissingTimesKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery Full Text 1.0 EBNF (144) FTSelection")
        internal inner class FTSelection {
            @Test
            @DisplayName("parenthesis")
            fun parenthesis() {
                val expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("parenthesis; compact whitespace")
            fun parenthesis_CompactWhitespace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTSelection")
            fun missingFTSelection() {
                val expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingFTSelection.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTPrimary_Parenthesis_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery Full Text 1.0 EBNF (154) FTExtensionSelection")
        internal inner class FTExtensionSelection {
            @Test
            @DisplayName("single pragma")
            fun singlePragma() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single pragma; compact whitespace")
            fun singlePragma_CompactWhitespace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple pragmas")
            fun multiplePragmas() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MultiplePragmas.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MultiplePragmas.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening brace")
            fun missingOpenBrace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingOpenBrace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingOpenBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing FTSelection")
            fun missingFTSelection() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingFTSelection.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingFTSelection.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingCloseBrace() {
                val expected = loadResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingCloseBrace.txt")
                val actual = parseResource("tests/parser/full-text-1.0/FTExtensionSelection_MissingCloseBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (152) FTWords ; XQuery Full Text 1.0 EBNF (155) FTAnyallOption")
    internal inner class FTWords_FTAnyallOption {
        @Test
        @DisplayName("any")
        fun any() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_Any.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_Any.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("any word")
        fun anyWord() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_AnyWord.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_AnyWord.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("all")
        fun all() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_All.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_All.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("all words")
        fun allWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_AllWords.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_AllWords.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("phrase")
        fun phrase() {
            val expected = loadResource("tests/parser/full-text-1.0/FTAnyallOption_Phrase.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTAnyallOption_Phrase.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (153) FTWordsValue")
    internal inner class FTWordsValue {
        @Test
        @DisplayName("word")
        fun ftWordsValue() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("word; compact whitespace")
        fun ftWordsValue_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("expression")
        fun expr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("expression; compact whitespace")
        fun expr_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr from expression")
        fun expr_MissingExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace from expression")
        fun expr_MissingClosingBrace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWordsValue_Expr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (157) FTRange")
    internal inner class FTRange {
        @Test
        @DisplayName("exactly")
        fun exactly() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_Exactly.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_Exactly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: exactly; missing AdditiveExpr")
        fun exactly_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_Exactly_MissingAdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: 'at' keyword without least/most qualifier")
        fun at_MissingQualifier() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_At_MissingQualifier.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_At_MissingQualifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at least")
        fun atLeast() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at least; missing AdditiveExpr")
        fun atLeast_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_AtLeast_MissingAdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at most")
        fun atMost() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_AtMost.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_AtMost.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at most; missing AdditiveExpr")
        fun atMost_MissingAdditiveExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_AtMost_MissingAdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("from/to")
        fun fromTo() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing from AdditiveExpr")
        fun fromTo_MissingFromExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingFromExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingFromExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' keyword")
        fun fromTo_MissingToKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing to AdditiveExpr")
        fun fromTo_MissingToExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTRange_FromTo_MissingToExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Test
    @DisplayName("XQuery Full Text 1.0 EBNF (159) FTOrder")
    fun ftOrder() {
        val expected = loadResource("tests/parser/full-text-1.0/FTOrder.txt")
        val actual = parseResource("tests/parser/full-text-1.0/FTOrder.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (160) FTWindow ; XQuery Full Text 1.0 EBNF (162) FTUnit")
    internal inner class FTWindow {
        @Test
        @DisplayName("words")
        fun words() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Words.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Words.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("sentences")
        fun sentences() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Sentences.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Sentences.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("paragraphs")
        fun paragraphs() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Paragraphs.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWindow_FTUnit_Paragraphs.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AdditiveExpr")
        fun missingAdditiveExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWindow_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWindow_MissingAdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnit")
        fun missingFTUnit() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWindow_MissingFTUnit.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWindow_MissingFTUnit.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (161) FTDistance ; XQuery Full Text 1.0 EBNF (162) FTUnit")
    internal inner class FTDistance {
        @Test
        @DisplayName("distance")
        fun distance() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDistance.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDistance.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTRange")
        fun missingFTRange() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDistance_MissingFTRange.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDistance_MissingFTRange.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTUnit")
        fun missingFTUnit() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDistance_MissingFTUnit.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDistance_MissingFTUnit.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (163) FTScope ; XQuery Full Text 1.0 EBNF (164) FTBigUnit")
    internal inner class FTScope {
        @Test
        @DisplayName("same sentence")
        fun sameSentence() {
            val expected = loadResource("tests/parser/full-text-1.0/FTScope_SameSentence.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTScope_SameSentence.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("same paragraph")
        fun sameParagraph() {
            val expected = loadResource("tests/parser/full-text-1.0/FTScope_SameParagraph.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTScope_SameParagraph.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: same; missing FTBigUnit")
        fun same_MissingFTBigUnit() {
            val expected = loadResource("tests/parser/full-text-1.0/FTScope_Same_MissingFTBigUnit.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTScope_Same_MissingFTBigUnit.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("different sentence")
        fun differentSentence() {
            val expected = loadResource("tests/parser/full-text-1.0/FTScope_DifferentSentence.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTScope_DifferentSentence.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: different; missing FTBigUnit")
        fun different_MissingFTBigUnit() {
            val expected = loadResource("tests/parser/full-text-1.0/FTScope_Different_MissingFTBigUnit.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTScope_Different_MissingFTBigUnit.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (165) FTContent")
    internal inner class FTContent {
        @Test
        @DisplayName("at start")
        fun atStart() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContent_AtStart.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContent_AtStart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at end")
        fun atEnd() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContent_AtEnd.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContent_AtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at; missing 'start'/'end' keyword")
        fun at_MissingStartEndKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContent_At_MissingStartEndKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContent_At_MissingStartEndKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("entire content")
        fun entireContent() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContent_EntireContent.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContent_EntireContent.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: entire; missing 'content' keyword")
        fun entireContent_MissingContentKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTContent_EntireContent_MissingContentKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTContent_EntireContent_MissingContentKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (168) FTCaseOption")
    internal inner class FTCaseOption {
        @Test
        @DisplayName("lower case")
        fun lowerCase() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: lower case; missing 'using' keyword")
        fun lowerCase_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_LowerCase_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("upper case")
        fun upperCase() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: upper case; missing 'using' keyword")
        fun upperCase_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_UpperCase_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("case sensitive")
        fun caseSensitive() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_Sensitive.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_Sensitive.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("case insensitive")
        fun caseInsensitive() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_Insensitive.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_Insensitive.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: case; missing 'sensitivity' keyword")
        fun case_MissingSensitivityKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingSensitivityKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: case; missing 'using' keyword")
        fun case_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTCaseOption_Case_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (169) FTDiacriticsOption")
    internal inner class FTDiacriticsOption {
        @Test
        @DisplayName("sensitive")
        fun sensitive() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_Sensitive.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_Sensitive.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("insensitive")
        fun insensitive() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_Insensitive.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_Insensitive.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'sensitivity' keyword")
        fun missingSensitivityKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingSensitivityKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTDiacriticsOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (170) FTStemOption")
    internal inner class FTStemOption {
        @Test
        @DisplayName("stemming")
        fun stemming() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStemOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStemOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: stemming; missing 'using' keyword")
        fun stemming_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStemOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStemOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no stemming")
        fun noStemming() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStemOption_NoStemming.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStemOption_NoStemming.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stemming; missing 'using' keyword")
        fun noStemming_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStemOption_NoStemming_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStemOption_NoStemming_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (171) FTThesaurusOption")
    internal inner class FTThesaurusOption {
        @Test
        @DisplayName("default")
        fun default() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Default.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Default.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: thesaurus; missing 'using' keyword")
        fun default_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no thesaurus")
        fun noThesaurus() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no thesaurus; missing 'using' keyword")
        fun noThesaurus_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoThesaurus_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("thesaurus id")
        fun thesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing FTThesaurusID")
        fun missingThesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_MissingThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: single; default")
        fun parenthesized_Default() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Default.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Default.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: single; thesaurus id")
        fun parenthesized_ThesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_ThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: compact whitespace")
        fun parenthesized_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: missing FTThesaurusID")
        fun parenthesized_MissingThesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: missing closing parenthesis")
        fun parenthesized_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: two items")
        fun parenthesized_TwoItems() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: two items; compact whitespace")
        fun parenthesized_TwoItems_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: two items; missing FTThesaurusID")
        fun parenthesized_TwoItems_MissingThesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_TwoItems_MissingThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: multiple")
        fun parenthesized_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized: multiple; compact whitespace")
        fun parenthesized_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parenthesized: multiple; default after first item")
        fun parenthesized_Multiple_DefaultAfterFirstItem() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_Parenthesized_Multiple_DefaultAfterFirstItem.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple items without parenthesis")
        fun noParenthesis_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple items without parenthesis; unexpected closing parenthesis")
        fun noParenthesis_Multiple_UnexpectedClosingParenthesis() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_NoParenthesis_Multiple_UnexpectedClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: single item; unexpected closing parenthesis")
        fun unexpectedClosingParenthesis() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusOption_UnexpectedClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (172) FTThesaurusID")
    internal inner class FTThesaurusID {
        @Test
        @DisplayName("thesaurus id")
        fun thesaurusID() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("thesaurus id; compact whitespace")
        fun thesaurusID_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URILiteral")
        fun missingUriLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_MissingUriLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_MissingUriLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("relationship")
        fun relationship() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("relationship; compact whitespace")
        fun relationship_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: relationship; missing StringLiteral")
        fun relationship_MissingStringLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_Relationship_MissingStringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("literal range")
        fun literalRange() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: literal range; missing 'levels' keyword")
        fun literalRange_MissingLevelsKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTThesaurusID_LiteralRange_MissingLevelsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (173) FTLiteralRange")
    internal inner class FTLiteralRange {
        @Test
        @DisplayName("exactly")
        fun exactly() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("exactly; compact whitespace")
        fun exactly_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: exactly; missing IntegerLiteral")
        fun exactly_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_Exactly_MissingIntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at; missing qualifier")
        fun at_MissingQualifier() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_At_MissingQualifier.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_At_MissingQualifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at least")
        fun atLeast() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at least; compact whitespace")
        fun atLeast_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: at least; missing IntegerLiteral")
        fun atLeast_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtLeast_MissingIntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at most")
        fun atMost() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("at most; compact whitespace")
        fun atMost_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntegerLiteral")
        fun atMost_MissingIntegerLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_AtMost_MissingIntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("from/to")
        fun fromTo() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("from/to; compact whitespace")
        fun fromTo_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'from' IntegerLiteral")
        fun fromTo_MissingFromLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingFromLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' keyword")
        fun fromTo_MissingToKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: from/to; missing 'to' IntegerLiteral")
        fun fromTo_MissingToLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLiteralRange_FromTo_MissingToLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (174) FTStopWordOption")
    internal inner class FTStopWordOption {
        @Test
        @DisplayName("default")
        fun default() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: stop words; missing 'words' keyword")
        fun missingWordsKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_MissingWordsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing stop words")
        fun missingStopWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_MissingStopWords.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_MissingStopWords.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no stop words")
        fun noStopWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stop words; missing 'words' keyword")
        fun noStopWords_MissingWordsKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingWordsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no stop words; missing 'using' keyword")
        fun noStopWords_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_NoStopWords_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("stop words")
        fun stopWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude")
        fun default_FTStopWordsInclExcl() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; compact whitespace")
        fun default_FTStopWordsInclExcl_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; multiple")
        fun default_FTStopWordsInclExcl_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default; include/exclude; multiple; compact whitespace")
        fun default_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_Default_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude")
        fun stopWords_FTStopWordsInclExcl() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; compact whitespace")
        fun stopWords_FTStopWordsInclExcl_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; multiple")
        fun stopWords_FTStopWordsInclExcl_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("stop words; include/exclude; multiple; compact whitespace")
        fun stopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordOption_FTStopWords_FTStopWordsInclExcl_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (175) FTStopWords")
    internal inner class FTStopWords {
        @Test
        @DisplayName("location")
        fun location() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("location; compact whitespace")
        fun location_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: location; missing URILiteral")
        fun location_MissingUriLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_Location_MissingUriLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_Location_MissingUriLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: list; missing stop word")
        fun list_MissingStopWord() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_MissingStopWord.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_MissingStopWord.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; single")
        fun list_Single() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; single; compact whitespace")
        fun list_Single_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Single_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Single_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; two words")
        fun list_TwoWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; two words; compact whitespace")
        fun list_TwoWords_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: list; two words; missing stop word")
        fun list_TwoWords_MissingStopWord() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_TwoWords_MissingStopWord.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; multiple")
        fun list_Multiple() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("list; multiple; compact whitespace")
        fun list_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWords_List_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (176) FTStopWordsInclExcl")
    internal inner class FTStopWordsInclExcl {
        @Test
        @DisplayName("include")
        fun include() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("include; compact whitespace")
        fun include_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Include_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("exclude")
        fun exclude() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("exclude; compact whitespace")
        fun exclude_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_Exclude_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing stop words")
        fun missingStopWords() {
            val expected = loadResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_MissingStopWords.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTStopWordsInclExcl_MissingStopWords.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (177) FTLanguageOption")
    internal inner class FTLanguageOption {
        @Test
        @DisplayName("language")
        fun language() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("language; compact whitespace")
        fun language_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing StringLiteral")
        fun missingStringLiteral() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption_MissingStringLiteral.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption_MissingStringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTLanguageOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTLanguageOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (178) FTWildCardOption")
    internal inner class FTWildCardOption {
        @Test
        @DisplayName("wildcards")
        fun wildcards() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: wildcards; mising 'using' keyword")
        fun wildcards_missingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no wildcards")
        fun noWildCards() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: no wildcards; mising 'using' keyword")
        fun noWildCards_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTWildCardOption_NoWildCards_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (179) FTExtensionOption")
    internal inner class FTExtensionOption {
        @Test
        @DisplayName("extension")
        fun extension() {
            val expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("extension; compact whitespace")
        fun extension_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing option name")
        fun missingOptionName() {
            val expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionName.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing option value")
        fun missingOptionValue() {
            val expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionValue.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_MissingOptionValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun missingUsingKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTExtensionOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTExtensionOption_MissingUsingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (180) FTIgnoreOption")
    internal inner class FTIgnoreOption {
        @Test
        @DisplayName("ignore option")
        fun ftIgnoreOption() {
            val expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("ignore option; compact whitespace")
        fun ftIgnoreOption_CompactWhitespace() {
            val expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'content' keyword")
        fun missingContentKeyword() {
            val expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingContentKeyword.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingContentKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/full-text-1.0/FTIgnoreOption_MissingUnionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
