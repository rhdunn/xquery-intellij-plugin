// Copyright (C) 2020-2022, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.saxon.tests.lang

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.saxon.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName")
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - Saxon")
class SaxonSyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    XpmDiagnostics {

    override val pluginId: PluginId = PluginId.getId("SaxonSyntaxValidatorTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPath)
        XQueryASTFactory().registerExtension(project, XQuery)

        XQueryProjectSettings.register(project)

        XpmSyntaxValidator.register(this, SaxonSyntaxValidator)
    }

    // region XpmDiagnostics

    val report: StringBuffer = StringBuffer()

    @BeforeEach
    fun reset() {
        report.delete(0, report.length)
    }

    override fun error(element: PsiElement, code: String, description: String) {
        if (report.isNotEmpty()) {
            report.append('\n')
        }
        report.append("E $code(${element.textOffset}:${element.textOffset + element.textLength}): $description")
    }

    override fun warning(element: PsiElement, code: String, description: String) {
        if (report.isNotEmpty()) {
            report.append('\n')
        }
        report.append("W $code(${element.textOffset}:${element.textOffset + element.textLength}): $description")
    }

    val validator: XpmSyntaxValidation = XpmSyntaxValidation()

    // endregion

    @Suppress("PrivatePropertyName")
    private val SAXON_HE_9_8 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonVersion(SaxonHE, 9, 8, ""))

    @Suppress("PrivatePropertyName")
    private val SAXON_HE_9_9 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonVersion(SaxonHE, 9, 9, ""))

    @Suppress("PrivatePropertyName")
    private val SAXON_HE_10_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonVersion(SaxonHE, 10, 0, ""))

    @Suppress("PrivatePropertyName")
    private val SAXON_PE_9_7 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonPE.VERSION_9_7)

    @Suppress("PrivatePropertyName")
    private val SAXON_PE_9_8 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonPE.VERSION_9_8)

    @Suppress("PrivatePropertyName")
    private val SAXON_PE_9_9 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonPE.VERSION_9_9)

    @Suppress("PrivatePropertyName")
    private val SAXON_PE_10_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonPE.VERSION_10_0)

    @Suppress("PrivatePropertyName")
    private val SAXON_EE_9_7 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonEE.VERSION_9_7)

    @Suppress("PrivatePropertyName")
    private val SAXON_EE_9_8 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonEE.VERSION_9_8)

    @Suppress("PrivatePropertyName")
    private val SAXON_EE_9_9 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonEE.VERSION_9_9)

    @Suppress("PrivatePropertyName")
    private val SAXON_EE_10_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, SaxonEE.VERSION_10_0)

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) OrExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("1 and 2, 3 andAlso 4")[0]
            validator.configuration = SAXON_HE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(11:18): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 9.9")
        fun supportedPE() {
            val file = parse<XQueryModule>("1 and 2, 3 andAlso 4")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 9.9")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("1 and 2, 3 andAlso 4")[0]
            validator.configuration = SAXON_PE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(11:18): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 9.9")
        fun supportedEE() {
            val file = parse<XQueryModule>("1 and 2, 3 andAlso 4")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 9.9")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("1 and 2, 3 andAlso 4")[0]
            validator.configuration = SAXON_EE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(11:18): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) ItemTypeDecl")
    internal inner class ItemTypeDecl {
        @Test
        @DisplayName("XQuery 4.0 ED syntax")
        fun xquery40() {
            val file = parse<XQueryModule>("declare item-type a:test = xs:string;")[0]
            validator.configuration = SAXON_HE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = SAXON_HE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(8:12): Saxon Home Edition 9.8 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 9.8")
        fun supportedPE() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = SAXON_PE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 9.8")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = SAXON_PE_9_7
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(8:12): Saxon Professional Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 9.8")
        fun supportedEE() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = SAXON_EE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 9.8")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = SAXON_EE_9_7
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(8:12): Saxon Enterprise Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (233) LocalUnionType")
    internal inner class LocalUnionType {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = SAXON_HE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:19): Saxon Home Edition 9.8 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 9.8")
        fun supportedPE() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = SAXON_PE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 9.8")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = SAXON_PE_9_7
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:19): Saxon Professional Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 9.8")
        fun supportedEE() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = SAXON_EE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 9.8")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = SAXON_EE_9_7
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:19): Saxon Enterprise Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) RecordTest")
    internal inner class RecordTest {
        @Test
        @DisplayName("XQuery 4.0 ED record test")
        fun xquery40() {
            val file = parse<XQueryModule>("1 instance of record(a as xs:string, b as .., \"c\"? as xs:int, *)")[0]
            validator.configuration = SAXON_HE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("tuple type")
        internal inner class TupleType {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of tuple(a: xs:string, b: xs:string)")[0]
                validator.configuration = SAXON_HE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:19): Saxon Home Edition 9.8 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 9.8")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a: xs:string, b: xs:string)")[0]
                validator.configuration = SAXON_PE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.8")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a: xs:string, b: xs:string)")[0]
                validator.configuration = SAXON_PE_9_7
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:19): Saxon Professional Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 9.8")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a: xs:string, b: xs:string)")[0]
                validator.configuration = SAXON_EE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.8")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a: xs:string, b: xs:string)")[0]
                validator.configuration = SAXON_EE_9_7
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:19): Saxon Enterprise Edition 9.7 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("extensible tuple type")
        internal inner class ExtensibleTupleType {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of tuple(a, *)")[0]
                validator.configuration = SAXON_HE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(23:24): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 9.9")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a, *)")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.9")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a, *)")[0]
                validator.configuration = SAXON_PE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(23:24): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 9.9")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a, *)")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.9")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a, *)")[0]
                validator.configuration = SAXON_EE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(23:24): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (24) FieldDeclaration")
    internal inner class FieldDeclaration {
        @Nested
        @DisplayName("optional tuple field")
        internal inner class Optional {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of tuple(a?: xs:string, b? : xs:string)")[0]
                validator.configuration = SAXON_HE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:19): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                        E XPST0003(21:23): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        E XPST0003(36:37): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 9.9")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a?: xs:string, b? : xs:string)")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.9")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a?: xs:string, b? : xs:string)")[0]
                validator.configuration = SAXON_PE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(21:23): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        E XPST0003(36:37): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 9.9")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a?: xs:string, b? : xs:string)")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.9")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a?: xs:string, b? : xs:string)")[0]
                validator.configuration = SAXON_EE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(21:23): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        E XPST0003(36:37): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("as SequenceType")
        internal inner class AsSequenceType {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b? as xs:string)")[0]
                validator.configuration = SAXON_HE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:19): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.8, or Saxon Enterprise Edition 9.8 constructs.
                        E XPST0003(22:24): Saxon Home Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        E XPST0003(37:38): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 10.0")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b? as xs:string)")[0]
                validator.configuration = SAXON_PE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 10.0")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b? as xs:string)")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(22:24): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 10.0")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b? as xs:string)")[0]
                validator.configuration = SAXON_EE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 10.0")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b? as xs:string)")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(22:24): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("1 or 2, 3 orElse 4")[0]
            validator.configuration = SAXON_HE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(10:16): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 9.9")
        fun supportedPE() {
            val file = parse<XQueryModule>("1 or 2, 3 orElse 4")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 9.9")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("1 or 2, 3 orElse 4")[0]
            validator.configuration = SAXON_PE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(10:16): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 9.9")
        fun supportedEE() {
            val file = parse<XQueryModule>("1 or 2, 3 orElse 4")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 9.9")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("1 or 2, 3 orElse 4")[0]
            validator.configuration = SAXON_EE_9_8
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(10:16): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9, or Saxon Enterprise Edition 9.9 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (81) ContextItemFunctionExpr")
    internal inner class ContextItemFunctionExpr {
        @Nested
        @DisplayName("dot syntax")
        internal inner class DotSyntax {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.configuration = SAXON_HE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        E XPST0003(7:8): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 10.0")
            fun supportedPE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.configuration = SAXON_PE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 10.0")
            fun notSupportedPE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        E XPST0003(7:8): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 10.0")
            fun supportedEE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.configuration = SAXON_EE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 10.0")
            fun notSupportedEE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        E XPST0003(7:8): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("fn syntax")
        internal inner class FnSyntax {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_HE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Home Edition 9.9 does not support Saxon Professional Edition 9.9-9.9, or Saxon Enterprise Edition 9.9-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE == 9.9")
            fun supportedPE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.9")
            fun ltSupportedPE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_PE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Professional Edition 9.8 does not support Saxon Professional Edition 9.9-9.9, or Saxon Enterprise Edition 9.9-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE > 9.9")
            fun gtSupportedPE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_PE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Professional Edition 10.0 does not support Saxon Professional Edition 9.9-9.9, or Saxon Enterprise Edition 9.9-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE == 9.9")
            fun supportedEE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.9")
            fun ltSupportedEE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_EE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Enterprise Edition 9.8 does not support Saxon Professional Edition 9.9-9.9, or Saxon Enterprise Edition 9.9-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE > 9.9")
            fun gtSupportedEE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.configuration = SAXON_EE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): Saxon Enterprise Edition 10.0 does not support Saxon Professional Edition 9.9-9.9, or Saxon Enterprise Edition 9.9-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (215) ElementTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class ElementTest {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(117:123): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'ElementTest' constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(117:123): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'ElementTest' constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(117:123): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'ElementTest' constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (212) AttributeTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class AttributeTest {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(123:129): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'AttributeTest' constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(123:129): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'AttributeTest' constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: Saxon :)
                """.trimIndent()
            )[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(123:129): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 wildcard local or prefix part in 'AttributeTest' constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (53) ForMemberBinding")
    internal inner class ForMemberBinding {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(4:10): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(4:10): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(4:10): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (96) OtherwiseExpr")
    internal inner class OtherwiseExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(2:11): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(2:11): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(2:11): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
    internal inner class TypeAlias {
        @Nested
        @DisplayName("tilde syntax")
        internal inner class TildeSyntax {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of ~a:type-name")[0]
                validator.configuration = SAXON_HE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:15): Saxon Home Edition 9.8 does not support Saxon Professional Edition 9.8-9.9, or Saxon Enterprise Edition 9.8-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 9.8")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of ~a:type-name")[0]
                validator.configuration = SAXON_PE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.8")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of ~a:type-name")[0]
                validator.configuration = SAXON_PE_9_7
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:15): Saxon Professional Edition 9.7 does not support Saxon Professional Edition 9.8-9.9, or Saxon Enterprise Edition 9.8-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 9.8")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of ~a:type-name")[0]
                validator.configuration = SAXON_EE_9_8
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.8")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of ~a:type-name")[0]
                validator.configuration = SAXON_EE_9_7
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:15): Saxon Enterprise Edition 9.7 does not support Saxon Professional Edition 9.8-9.9, or Saxon Enterprise Edition 9.8-9.9 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("type alias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("Saxon HE")
            fun notSupportedHE() {
                val file = parse<XQueryModule>("1 instance of type(a:type-name)")[0]
                validator.configuration = SAXON_HE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:18): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon PE >= 10.0")
            fun supportedPE() {
                val file = parse<XQueryModule>("1 instance of type(a:type-name)")[0]
                validator.configuration = SAXON_PE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 10.0")
            fun notSupportedPE() {
                val file = parse<XQueryModule>("1 instance of type(a:type-name)")[0]
                validator.configuration = SAXON_PE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:18): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("Saxon EE >= 10.0")
            fun supportedEE() {
                val file = parse<XQueryModule>("1 instance of type(a:type-name)")[0]
                validator.configuration = SAXON_EE_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 10.0")
            fun notSupportedEE() {
                val file = parse<XQueryModule>("1 instance of type(a:type-name)")[0]
                validator.configuration = SAXON_EE_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:18): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (117) LambdaFunctionExpr")
    internal inner class LambdaFunctionExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:2): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    E XPST0003(7:8): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:2): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    E XPST0003(7:8): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:2): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    E XPST0003(7:8): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (118) ParamRef")
    internal inner class ParamRef {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.configuration = SAXON_HE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(1:5): Saxon Home Edition 10.0 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon PE >= 10.0")
        fun supportedPE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.configuration = SAXON_PE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.configuration = SAXON_PE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(1:5): Saxon Professional Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("Saxon EE >= 10.0")
        fun supportedEE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.configuration = SAXON_EE_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.configuration = SAXON_EE_9_9
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(1:5): Saxon Enterprise Edition 9.9 does not support Saxon Professional Edition 10.0, or Saxon Enterprise Edition 10.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }
}
