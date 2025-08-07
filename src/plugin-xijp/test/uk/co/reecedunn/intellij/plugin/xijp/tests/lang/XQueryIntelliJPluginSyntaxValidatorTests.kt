// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xijp.tests.lang

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xijp.lang.XQueryIntelliJPlugin
import uk.co.reecedunn.intellij.plugin.xijp.lang.XQueryIntelliJPluginSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xijp.lang.XQueryIntelliJPluginVersion
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName")
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - XQuery IntelliJ Plugin")
class XQueryIntelliJPluginSyntaxValidatorTest : IdeaPlatformTestCase(), LanguageTestCase, XpmDiagnostics {
    override val pluginId: PluginId = PluginId.getId("XQueryIntelliJPluginSyntaxValidatorTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())

        XpmSyntaxValidator.register(this, XQueryIntelliJPluginSyntaxValidator)
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
    private val VERSION_1_0 = XpmLanguageConfiguration(
        XQuery.VERSION_1_0,
        XQueryIntelliJPluginVersion(XQueryIntelliJPlugin, 1, 0)
    )

    @Suppress("PrivatePropertyName")
    private val VERSION_1_3 = XpmLanguageConfiguration(XQuery.VERSION_1_0, XQueryIntelliJPlugin.VERSION_1_3)

    @Suppress("PrivatePropertyName")
    private val VERSION_1_4 = XpmLanguageConfiguration(XQuery.VERSION_1_0, XQueryIntelliJPlugin.VERSION_1_4)

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList; XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class ParamList_FunctionDecl {
        @Test
        @DisplayName("variadic; XQuery IntelliJ Plugin >= 1.3")
        fun supported() {
            val file = parse<XQueryModule>("declare function f(\$a as xs:string ...) external;")[0]
            validator.configuration = VERSION_1_4
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("variadic; XQuery IntelliJ Plugin < 1.3")
        fun notSupported() {
            val file = parse<XQueryModule>("declare function f(\$a as xs:string ...) external;")[0]
            validator.configuration = VERSION_1_3
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(35:38): XQuery IntelliJ Plugin 1.3 does not support XQuery IntelliJ Plugin 1.4 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("non-variadic")
        fun nonVariadic() {
            val file = parse<XQueryModule>("declare function f(\$a as xs:string) external;")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList; XQuery 3.1 EBNF (169) InlineFunctionExpr")
    internal inner class ParamList_InlineFunctionExpr {
        @Test
        @DisplayName("variadic; XQuery IntelliJ Plugin >= 1.3")
        fun supported() {
            val file = parse<XQueryModule>("function f(\$a as xs:string ...) { 2 }")[0]
            validator.configuration = VERSION_1_4
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("variadic; XQuery IntelliJ Plugin < 1.3")
        fun notSupported() {
            val file = parse<XQueryModule>("function (\$a as xs:string ...) { 2 }")[0]
            validator.configuration = VERSION_1_3
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(26:29): XQuery IntelliJ Plugin 1.3 does not support XQuery IntelliJ Plugin 1.4 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("non-variadic")
        fun nonVariadic() {
            val file = parse<XQueryModule>("function (\$a as xs:string) { 2 }")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    internal inner class SequenceTypeUnion {
        @Test
        @DisplayName("XQuery IntelliJ Plugin >= 1.3")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of (xs:string*|element(test))")[0]
            validator.configuration = VERSION_1_3
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin < 1.3")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of (xs:string*|element(test))")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(25:26): XQuery IntelliJ Plugin 1.0 does not support XQuery IntelliJ Plugin 1.3 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (43) TypeswitchExpr ; XQuery 1.0 EBNF (44) CaseClause")
        fun caseClause() {
            val file = parse<XQueryModule>("typeswitch (1) case xs:string*|element(test) return 1 default return 2")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    internal inner class SequenceTypeList {
        @Test
        @DisplayName("XQuery IntelliJ Plugin >= 1.3")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of (xs:string*, element(test))")[0]
            validator.configuration = VERSION_1_3
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin < 1.3")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of (xs:string*, element(test))")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(25:26): XQuery IntelliJ Plugin 1.0 does not support XQuery IntelliJ Plugin 1.3 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
        fun typedFunctionTest() {
            val file = parse<XQueryModule>("1 instance of function (xs:string*, element(test)) as item()")[0]
            validator.configuration = VERSION_1_0
            validator.validate(file, this@XQueryIntelliJPluginSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }
}
