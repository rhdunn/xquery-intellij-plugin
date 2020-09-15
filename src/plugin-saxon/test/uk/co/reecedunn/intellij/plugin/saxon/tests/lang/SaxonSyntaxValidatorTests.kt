/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.tests.lang

import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.saxon.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidatorBean

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - Saxon")
class SaxonSyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    XpmDiagnostics {
    // region ParsingTestCase

    @BeforeAll
    override fun setUp() {
        super.setUp()
        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        registerExtensionPoint(XpmSyntaxValidator.EP_NAME, XpmSyntaxValidatorBean::class.java)
        registerSyntaxValidator(SaxonSyntaxValidator, "INSTANCE")
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerSyntaxValidator(factory: XpmSyntaxValidator, fieldName: String) {
        val classLoader = SaxonSyntaxValidatorTest::class.java.classLoader
        val bean = XpmSyntaxValidatorBean()
        bean.implementationClass = factory.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerSyntaxValidator"), classLoader))
        registerExtension(XpmSyntaxValidator.EP_NAME, bean)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
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
    private val SAXON_HE_9_9: XpmProductVersion = SaxonVersion(SaxonHE, 9, 9, "")

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
                validator.product = SaxonHE.VERSION_10_0
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
                validator.product = SaxonPE.VERSION_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 10.0")
            fun notSupportedPE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.product = SaxonPE.VERSION_9_9
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
                validator.product = SaxonEE.VERSION_10_0
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 10.0")
            fun notSupportedEE() {
                val file = parse<XQueryModule>(".{1} , . {2}")[0]
                validator.product = SaxonEE.VERSION_9_9
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
                validator.product = SAXON_HE_9_9
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
                validator.product = SaxonPE.VERSION_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon PE < 9.9")
            fun ltSupportedPE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.product = SaxonPE.VERSION_9_8
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
                validator.product = SaxonPE.VERSION_10_0
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
                validator.product = SaxonEE.VERSION_9_9
                validator.validate(file, this@SaxonSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("Saxon EE < 9.9")
            fun ltSupportedEE() {
                val file = parse<XQueryModule>("fn{1}")[0]
                validator.product = SaxonEE.VERSION_9_8
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
                validator.product = SaxonEE.VERSION_10_0
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (114) OtherwiseExpr")
    internal inner class OtherwiseExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.product = SaxonHE.VERSION_10_0
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
            validator.product = SaxonPE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.product = SaxonPE.VERSION_9_9
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
            validator.product = SaxonEE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.product = SaxonEE.VERSION_9_9
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (117) LambdaFunctionExpr")
    internal inner class LambdaFunctionExpr {
        @Test
        @DisplayName("Saxon HE")
        fun notSupportedHE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.product = SaxonHE.VERSION_10_0
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
            validator.product = SaxonPE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.product = SaxonPE.VERSION_9_9
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
            validator.product = SaxonEE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("_{1} , _ {2}")[0]
            validator.product = SaxonEE.VERSION_9_9
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
            validator.product = SaxonHE.VERSION_10_0
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
            validator.product = SaxonPE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon PE < 10.0")
        fun notSupportedPE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.product = SaxonPE.VERSION_9_9
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
            validator.product = SaxonEE.VERSION_10_0
            validator.validate(file, this@SaxonSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("Saxon EE < 10.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>("\$1234")[0]
            validator.product = SaxonEE.VERSION_9_9
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
