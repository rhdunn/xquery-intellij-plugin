// Copyright (C) 2017-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider

@Suppress("ClassName", "Reformat", "RedundantVisibilityModifier")
@DisplayName("XQuery IntelliJ Plugin - XPath Parser")
class PluginParserTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("PluginParserTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XPath = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("single")
        fun singleAndAlso() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_SingleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_SingleAndAlso.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing ComparisonExpr")
        fun missingUpdateExpr() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_MissingComparisonExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_MissingComparisonExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleAndAlso() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_MultipleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_MultipleAndAlso.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is first")
        fun mixedAndAlsoFirst() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoFirst.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is last")
        fun mixedAndAlsoLast() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoLast.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (25) RecordTest")
    internal inner class RecordTest {
        @Test
        @DisplayName("tuple type")
        fun tupleType() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tuple type; compact whitespace")
        fun tupleType_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 4.0 ED EBNF (126) ExtensibleFlag")
        internal inner class ExtensibleFlag {
            @Test
            @DisplayName("extensible flag")
            fun extensibleFlag() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("extensible flag; compact whitespace")
            fun extensibleFlag_compactWhitespace() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: field declaration after extensible flag")
            fun fieldDeclarationAfter() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_NotLast.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_NotLast.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: extensible flag at start")
            fun atStart() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_AtStart.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_AtStart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (26) FieldDeclaration")
        internal inner class FieldDeclaration {
            @Test
            @DisplayName("no SequenceType")
            fun noSequenceType() {
                val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_NoSequenceType.txt")
                val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_NoSequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: with SequenceType (XQuery 4.0 ED syntax)")
            internal inner class WithSelfReference {
                @Test
                @DisplayName("self reference")
                fun selfReference() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_SelfReference.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_SelfReference.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("self reference with occurrence indicator")
                fun occurrenceIndicator() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_SelfReference_OccurrenceIndicator.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_SelfReference_OccurrenceIndicator.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("with SequenceType (Saxon 9.8 syntax)")
            internal inner class WithSequenceType_Saxon9 {
                @Test
                @DisplayName("single")
                fun single() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("single; compact whitespace")
                fun single_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("multiple")
                fun multiple() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("multiple; compact whitespace")
                fun multiple_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("occurrence indicator in SequenceType")
                fun multiple_withOccurrenceIndicator() {
                    // This is testing handling of whitespace before parsing the next comma.
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MultipleWithOccurrenceIndicator.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MultipleWithOccurrenceIndicator.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("missing colon")
                fun missingColon() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MissingColon.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MissingColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("missing SequenceType")
                fun missingSequenceType() {
                    val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MissingSequenceType.txt")
                    val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MissingSequenceType.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("with SequenceType (Saxon 10.0 syntax)")
            internal inner class WithSequenceType_Saxon10 {
                @Test
                @DisplayName("single")
                fun single() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("single; compact whitespace")
                fun single_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("multiple")
                fun multiple() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_Multiple.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_Multiple.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("multiple; compact whitespace")
                fun multiple_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_Multiple_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_Multiple_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("missing SequenceType")
                fun missingSequenceType() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_MissingSequenceType.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_MissingSequenceType.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("optional field name (Saxon 9.9)")
            internal inner class OptionalFieldName {
                @Test
                @DisplayName("optional field name")
                fun optionalFieldName() {
                    val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName.txt")
                    val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("optional field name; compact whitespace")
                fun optionalFieldName_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("optional field name; no sequence type")
                fun optionalFieldName_noSequenceType() {
                    val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_NoSequenceType.txt")
                    val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_NoSequenceType.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("StringLiteral field name (Saxon 10.0)")
            internal inner class StringLiteralFieldName {
                @Test
                @DisplayName("field name")
                fun fieldName() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_StringLiteralFieldName.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_StringLiteralFieldName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("field name; compact whitespace")
                fun fieldName_compactWhitespace() {
                    val expected = loadResource("tests/parser/saxon-10.0-xpath/TupleField_StringLiteralFieldName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/saxon-10.0-xpath/TupleField_StringLiteralFieldName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun singleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_SingleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_SingleOrElse.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_MissingAndExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_MultipleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_MultipleOrElse.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is first")
        fun mixedOrElseFirst() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseFirst.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is last")
        fun mixedOrElseLast() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseLast.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
    internal inner class ContextItemFunctionExpr {
        @Nested
        @DisplayName("simple inline function")
        internal inner class SimpleInlineFunction {
            @Test
            @DisplayName("simple inline function expression")
            fun simpleInlineFunctionExpr() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("simple inline function expression; compact whitespace")
            fun simpleInlineFunctionExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/saxon-9.9-xpath/SimpleInlineFunctionExpr_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("context inline function")
        internal inner class ContextInlineFunction {
            @Test
            @DisplayName("context item function expression")
            fun contextItemFunctionExpr() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("context item function expression; compact whitespace")
            fun contextItemFunctionExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("space between dot and brace")
            fun spaceBetweenDotAndBrace() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_SpaceBetweenDotAndBrace.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/ContextItemFunctionExpr_SpaceBetweenDotAndBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (22) ParamList")
    internal inner class ParamList {
        @Test
        @DisplayName("untyped")
        fun untyped() {
            val expected = loadResource("tests/parser/intellij-plugin/ParamList_Variadic_Untyped.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ParamList_Variadic_Untyped.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("untyped; compact whitespace")
        fun untyped_compactWhitespace() {
            val expected = loadResource("tests/parser/intellij-plugin/ParamList_Variadic_Untyped_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ParamList_Variadic_Untyped_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple Params; on the last parameter")
        fun multipleParams() {
            val expected = loadResource("tests/parser/intellij-plugin/ParamList_Variadic_Multiple_LastParam.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ParamList_Variadic_Multiple_LastParam.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (34) TypeAlias")
    internal inner class TypeAlias {
        @Nested
        @DisplayName("Saxon 9.8 syntax")
        internal inner class Saxon9 {
            @Test
            @DisplayName("single")
            fun typeAlias() {
                val expected = loadResource("tests/parser/saxon-9.8-xpath/TypeAlias.txt")
                val actual = parseResource("tests/parser/saxon-9.8-xpath/TypeAlias.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing UnionExpr")
            fun typeAlias_compactWhitespace() {
                val expected = loadResource("tests/parser/saxon-9.8-xpath/TypeAlias_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/saxon-9.8-xpath/TypeAlias_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing EQName")
            fun missingEQName() {
                val expected = loadResource("tests/parser/saxon-9.8-xpath/TypeAlias_MissingEQName.txt")
                val actual = parseResource("tests/parser/saxon-9.8-xpath/TypeAlias_MissingEQName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("Saxon 10.0 syntax")
        internal inner class Saxon10 {
            @Test
            @DisplayName("single")
            fun typeAlias() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/TypeAlias.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/TypeAlias.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing UnionExpr")
            fun typeAlias_compactWhitespace() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/TypeAlias_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/TypeAlias_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing EQName")
            fun missingEQName() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/TypeAlias_MissingEQName.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/TypeAlias_MissingEQName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/saxon-10.0-xpath/TypeAlias_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/saxon-10.0-xpath/TypeAlias_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (35) LambdaFunctionExpr")
    internal inner class LambdaFunctionExpr {
        @Test
        @DisplayName("lambda function expression")
        fun lambdaFunctionExpr() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("lambda function expression; compact whitespace")
        fun lambdaFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("space between underscore and brace")
        fun spaceBetweenUnderscoreAndBrace() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_SpaceBetweenUnderscoreAndBrace.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_SpaceBetweenUnderscoreAndBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/LambdaFunctionExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (56) PrimaryExpr ; XQuery IntelliJ Plugin XPath EBNF (37) ParamRef")
    internal inner class PrimaryExpr_ParamRef {
        @Test
        @DisplayName("parameter reference")
        fun paramRef() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/PrimaryExpr_ParamRef.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/PrimaryExpr_ParamRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter reference; compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/PrimaryExpr_ParamRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/PrimaryExpr_ParamRef_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (55) ArrowFunctionSpecifier ; XQuery IntelliJ Plugin XPath EBNF (37) ParamRef")
    internal inner class ArrowFunctionSpecifier_ParamRef {
        @Test
        @DisplayName("parameter reference")
        fun paramRef() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/ArrowFunctionSpecifier_ParamRef.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/ArrowFunctionSpecifier_ParamRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter reference; compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/saxon-10.0-xpath/ArrowFunctionSpecifier_ParamRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-10.0-xpath/ArrowFunctionSpecifier_ParamRef_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
