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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang

import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.w3.lang.W3CSpecifications
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuerySyntaxValidator

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - XQuery")
class XQuerySyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    PluginDescriptorProvider,
    XpmDiagnostics {
    // region ParsingTestCase

    @BeforeAll
    override fun setUp() {
        super.setUp()
        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        XpmSyntaxValidator.register(this, XQuerySyntaxValidator)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    // endregion
    // region PluginDescriptorProvider

    override val pluginId: PluginId = PluginId.getId("XQuerySyntaxValidatorTest")

    override val pluginDescriptor: PluginDescriptor
        get() = DefaultPluginDescriptor(pluginId, this::class.java.classLoader)

    override val pluginDisposable: Disposable
        get() = testRootDisposable

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
    private val XQUERY_4_0 = XpmLanguageConfiguration(XQuery.VERSION_4_0, W3CSpecifications.REC)

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
    internal inner class WithExpr {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("with xmlns='http://www.example.com' { test }")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("with xmlns='http://www.example.com' { test }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:4): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (137) KeywordArgument")
    internal inner class KeywordArgument {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("f(arg: 2)")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("f(arg: 2)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(2:5): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (236) EnumerationType")
    internal inner class EnumerationType {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of enum('lorem', 'ipsum')")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of enum('lorem', 'ipsum')")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:18): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }
}
