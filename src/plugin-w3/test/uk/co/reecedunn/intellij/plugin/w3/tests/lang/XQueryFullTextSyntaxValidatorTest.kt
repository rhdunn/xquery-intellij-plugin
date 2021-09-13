/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.w3.tests.lang

import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.w3.lang.FullTextSyntaxValidator
import uk.co.reecedunn.intellij.plugin.w3.lang.W3CSpecifications
import uk.co.reecedunn.intellij.plugin.xpath.lang.FullTextSpec
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
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - XQuery Full Text")
class XQueryFullTextSyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    XpmDiagnostics {

    override val pluginId: PluginId = PluginId.getId("XQueryFullTextSyntaxValidatorTest")

    // region ParsingTestCase

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        XpmSyntaxValidator.register(this, FullTextSyntaxValidator)
    }

    // endregion
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
    private val XQUERY_1_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val FULL_TEXT_1_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, W3CSpecifications.REC)
        .withImplementations(FullTextSpec.REC_1_0_20110317)

    @Nested
    @DisplayName("XQuery 3.1 with Full Text EBNF (24) FTOptionDecl")
    internal inner class FTOptionDecl {
        @Test
        @DisplayName("without Full Text")
        fun notSupported() {
            val file = parse<XQueryModule>("declare ft-option using language \"en-IE\";")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(8:17): W3C Specifications REC does not support XQuery and XPath Full Text 1.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("with Full Text")
        fun supported() {
            val file = parse<XQueryModule>("declare ft-option using language \"en-IE\";")[0]
            validator.configuration = FULL_TEXT_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 with Full Text EBNF (37) FTScoreVar")
    internal inner class FTScoreVar {
        @Test
        @DisplayName("without Full Text")
        fun notSupported() {
            val file = parse<XQueryModule>("for \$x score \$y in \$z return \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(7:12): W3C Specifications REC does not support XQuery and XPath Full Text 1.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("with Full Text")
        fun supported() {
            val file = parse<XQueryModule>("for \$x score \$y in \$z return \$x")[0]
            validator.configuration = FULL_TEXT_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 with Full Text EBNF (51) FTContainsExpr")
    internal inner class FTContainsExpr {
        @Test
        @DisplayName("without Full Text")
        fun notSupported() {
            val file = parse<XQueryModule>("title contains text \"lorem\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(6:14): W3C Specifications REC does not support XQuery and XPath Full Text 1.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("with Full Text")
        fun supported() {
            val file = parse<XQueryModule>("title contains text \"lorem\"")[0]
            validator.configuration = FULL_TEXT_1_0
            validator.validate(file, this@XQueryFullTextSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }
}
