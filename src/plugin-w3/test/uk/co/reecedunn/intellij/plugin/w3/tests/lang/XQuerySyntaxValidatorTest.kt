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
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicVersion
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
import uk.co.reecedunn.intellij.plugin.w3.lang.XQuerySyntaxValidator

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
    private val XQUERY_3_0 = XpmLanguageConfiguration(XQuery.VERSION_3_0, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val XQUERY_3_1 = XpmLanguageConfiguration(XQuery.VERSION_3_1, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val XQUERY_4_0 = XpmLanguageConfiguration(XQuery.VERSION_4_0, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val XQUERY_1_0_ML_WITH_MARKLOGIC_5 = XpmLanguageConfiguration(
        XQuery.VERSION_1_0_ML,
        MarkLogicVersion(MarkLogic, 5, "")
    )

    @Suppress("PrivatePropertyName")
    private val XQUERY_1_0_ML_WITH_MARKLOGIC_9 = XpmLanguageConfiguration(XQuery.VERSION_1_0_ML, MarkLogic.VERSION_9)

    @Nested
    @DisplayName("XQuery 3.0 EBNF (2) VersionDecl")
    internal inner class VersionDecl {
        @Test
        @DisplayName("version only")
        fun version() {
            val file = parse<XQueryModule>("xquery version \"1.0\"; 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("version and encoding")
        fun versionAndEncoding() {
            val file = parse<XQueryModule>("xquery version \"1.0\" encoding \"latin1\"; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("encoding only; XQuery < 3.0")
        fun encoding_notSupported() {
            val file = parse<XQueryModule>("xquery encoding \"latin1\"; 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(7:15): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("encoding only; XQuery >= 3.0")
        fun encoding_supported() {
            val file = parse<XQueryModule>("xquery encoding \"latin1\"; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (27) Annotation")
    internal inner class Annotation {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(8:9): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(8:9): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (31) ContextItemDecl")
    internal inner class ContextItemDecl {
        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("declare context item := 2;")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("declare context item := 2;")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(8:15): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (46) AllowingEmpty")
    internal inner class AllowingEmpty {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("for \$x allowing empty in () return \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(7:15): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("for \$x allowing empty in () return \$x")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (120) PostfixExpr ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class PostfixExpr_ArgumentList {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:3): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:3): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
    internal inner class ArgumentPlaceholder {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(9:10): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(9:10): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
    internal inner class AnyFunctionTest {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:2): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:2): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (105) ArrowExpr ; XQuery 4.0 ED EBNF (108) FatArrowTarget")
    internal inner class FatArrowTarget {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
        internal inner class ArrowStaticFunction {
            @Test
            @DisplayName("XQuery >= 3.1")
            fun xquery_supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 3.1")
            fun xquery_notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 3.1, or MarkLogic 9.0 constructs.")
                )
            }

            @Test
            @DisplayName("MarkLogic >= 9.0")
            fun marklogic_supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 9.0")
            fun marklogic_notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): MarkLogic 5.0 does not support XQuery 3.1, or MarkLogic 9.0 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
        internal inner class ArrowDynamicFunction {
            @Test
            @DisplayName("XQuery >= 3.1")
            fun xquery_supported() {
                val file = parse<XQueryModule>("1 => \$f()")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 3.1")
            fun xquery_notSupported() {
                val file = parse<XQueryModule>("1 => \$f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 3.1, or MarkLogic 9.0 constructs.")
                )
            }

            @Test
            @DisplayName("MarkLogic >= 9.0")
            fun marklogic_supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 9.0")
            fun marklogic_notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): MarkLogic 5.0 does not support XQuery 3.1, or MarkLogic 9.0 constructs.")
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (175) SquareArrayConstructor")
    internal inner class SquareArrayConstructor {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("[1, 2, 3]")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("[1, 2, 3]")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:9): XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun marklogic_supported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_9
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun marklogic_notSupported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_1_0_ML_WITH_MARKLOGIC_5
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:9): MarkLogic 5.0 does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
    internal inner class AnyArrayTest {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("1 instance of array(*)")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("1 instance of array(*)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:19): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (25) DefaultNamespaceDecl")
    internal inner class DefaultNamespaceDecl {
        @Test
        @DisplayName("default function namespace")
        fun function() {
            val file = parse<XQueryModule>("declare default function namespace \"http://www.example.com\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("default element namespace")
        fun element() {
            val file = parse<XQueryModule>("declare default element namespace \"http://www.example.com\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("default type namespace ; XQuery < 4.0")
        fun type_notSupported() {
            val file = parse<XQueryModule>("declare default type namespace \"http://www.example.com\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(16:20): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }

        @Test
        @DisplayName("default type namespace ; XQuery >= 4.0")
        fun type_supported() {
            val file = parse<XQueryModule>("declare default type namespace \"http://www.example.com\"")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

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
                report.toString(),
                `is`("E XPST0003(0:4): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (105) ArrowExpr ; XQuery 4.0 ED EBNF (109) ThinArrowTarget")
    internal inner class ThinArrowTarget {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
        internal inner class ArrowStaticFunction {
            @Test
            @DisplayName("XQuery >= 4.0")
            fun supported() {
                val file = parse<XQueryModule>("1 -> f()")[0]
                validator.configuration = XQUERY_4_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 4.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 -> f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
        internal inner class ArrowDynamicFunction {
            @Test
            @DisplayName("XQuery >= 4.0")
            fun supported() {
                val file = parse<XQueryModule>("1 -> \$f()")[0]
                validator.configuration = XQUERY_4_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 4.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 -> \$f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (37) EnclosedExpr")
        internal inner class EnclosedExpr {
            @Test
            @DisplayName("XQuery >= 4.0")
            fun supported() {
                val file = parse<XQueryModule>("1 -> {}")[0]
                validator.configuration = XQUERY_4_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 4.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 -> {}")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
                )
            }
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
                report.toString(),
                `is`("E XPST0003(2:5): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (140) Lookup ; XQuery 4.0 ED EBNF (141) KeySpecifier")
    internal inner class Lookup {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val file = parse<XQueryModule>(".?test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("IntegerLiteral")
        fun integerLiteral() {
            val file = parse<XQueryModule>(".?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("StringLiteral ; XQuery < 4.0")
        fun stringLiteral_notSupported() {
            val file = parse<XQueryModule>(".?\"test\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:8): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }

        @Test
        @DisplayName("StringLiteral ; XQuery >= 4.0")
        fun stringLiteral_supported() {
            val file = parse<XQueryModule>(".?\"test\"")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("VarRef ; XQuery < 4.0")
        fun varRef_notSupported() {
            val file = parse<XQueryModule>(".?\$test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:7): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }

        @Test
        @DisplayName("VarRef ; XQuery >= 4.0")
        fun varRef_supported() {
            val file = parse<XQueryModule>(".?\$test")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("ParenthesizedExpr")
        fun parenthesizedExpr() {
            val file = parse<XQueryModule>(".?(2)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Wildcard")
        fun wildcard() {
            val file = parse<XQueryModule>(".?*")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (199) UnaryLookup ; XQuery 4.0 ED EBNF (141) KeySpecifier")
    internal inner class UnaryLookup {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val file = parse<XQueryModule>("?test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("IntegerLiteral")
        fun integerLiteral() {
            val file = parse<XQueryModule>("?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("StringLiteral ; XQuery < 4.0")
        fun stringLiteral_notSupported() {
            val file = parse<XQueryModule>("?\"test\"")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:7): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }

        @Test
        @DisplayName("StringLiteral ; XQuery >= 4.0")
        fun stringLiteral_supported() {
            val file = parse<XQueryModule>("?\"test\"")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("VarRef ; XQuery < 4.0")
        fun varRef_notSupported() {
            val file = parse<XQueryModule>("?\$test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:6): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }

        @Test
        @DisplayName("VarRef ; XQuery >= 4.0")
        fun varRef_supported() {
            val file = parse<XQueryModule>("?\$test")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("ParenthesizedExpr")
        fun parenthesizedExpr() {
            val file = parse<XQueryModule>("?(2)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Wildcard")
        fun wildcard() {
            val file = parse<XQueryModule>("?*")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
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
                report.toString(),
                `is`("E XPST0003(14:18): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }
}
