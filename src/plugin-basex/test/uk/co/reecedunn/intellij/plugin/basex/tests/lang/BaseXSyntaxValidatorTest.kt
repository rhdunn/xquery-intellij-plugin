/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.tests.lang

import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseX
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseXSyntaxValidator
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseXVersion
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
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

@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - BaseX")
class BaseXSyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    XpmDiagnostics {

    override val pluginId: PluginId = PluginId.getId("BaseXSyntaxValidatorTest")

    // region ParsingTestCase

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        XpmSyntaxValidator.register(this, BaseXSyntaxValidator)
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
    private val VERSION_6_0 = XpmLanguageConfiguration(XQuery.VERSION_1_0, BaseXVersion(BaseX, 6, 0, ""))

    @Suppress("PrivatePropertyName")
    private val VERSION_9_1 = XpmLanguageConfiguration(XQuery.VERSION_1_0, BaseX.VERSION_9_1)

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (12) UpdateExpr")
    internal inner class UpdateExpr {
        @Nested
        @DisplayName("inline")
        internal inner class Inline {
            @Test
            @DisplayName("BaseX >= 7.8")
            fun supported() {
                val file = parse<XQueryModule>("//item update delete node .")[0]
                validator.configuration = VERSION_9_1
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("BaseX < 7.8")
            fun notSupported() {
                val file = parse<XQueryModule>("//item update delete node .")[0]
                validator.configuration = VERSION_6_0
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(7:13): BaseX 6.0 does not support BaseX 7.8 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("enclosed expression")
        internal inner class EnclosedExpr {
            @Test
            @DisplayName("BaseX >= 8.5")
            fun supported() {
                val file = parse<XQueryModule>("//item update { delete node . }")[0]
                validator.configuration = VERSION_9_1
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("BaseX < 8.5")
            fun notSupported() {
                val file = parse<XQueryModule>("//item update { delete node . }")[0]
                validator.configuration = VERSION_6_0
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(14:15): BaseX 6.0 does not support BaseX 8.5 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (14) FTFuzzyOption")
    internal inner class FTFuzzyOption {
        @Test
        @DisplayName("BaseX >= 6.1")
        fun supported() {
            val file = parse<XQueryModule>("title contains text \"lorem\" using fuzzy")[0]
            validator.configuration = VERSION_9_1
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("BaseX < 6.1")
        fun notSupported() {
            val file = parse<XQueryModule>("title contains text \"lorem\" using fuzzy")[0]
            validator.configuration = VERSION_6_0
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(34:39): BaseX 6.0 does not support BaseX 6.1 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (16) NonDeterministicFunctionCall")
    internal inner class NonDeterministicFunctionCall {
        @Test
        @DisplayName("BaseX >= 8.4")
        fun supported() {
            val file = parse<XQueryModule>("let \$x := fn:true#0 return non-deterministic \$x()")[0]
            validator.configuration = VERSION_9_1
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("BaseX < 8.4")
        fun notSupported() {
            val file = parse<XQueryModule>("let \$x := fn:true#0 return non-deterministic \$x()")[0]
            validator.configuration = VERSION_6_0
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(27:44): BaseX 6.0 does not support BaseX 8.4 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (45) TernaryConditionalExpr")
    internal inner class TernaryConditionalExpr {
        @Test
        @DisplayName("BaseX >= 9.1")
        fun supported() {
            val file = parse<XQueryModule>("1 eq 2 ?? 3 !! 4")[0]
            validator.configuration = VERSION_9_1
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("BaseX < 9.1")
        fun notSupported() {
            val file = parse<XQueryModule>("1 eq 2 ?? 3 !! 4")[0]
            validator.configuration = VERSION_6_0
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(7:9): BaseX 6.0 does not support BaseX 9.1 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (93) ElvisExpr")
    internal inner class ElvisExpr {
        @Test
        @DisplayName("BaseX >= 9.1")
        fun supported() {
            val file = parse<XQueryModule>("1 ?: 2")[0]
            validator.configuration = VERSION_9_1
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("BaseX < 9.1")
        fun notSupported() {
            val file = parse<XQueryModule>("1 ?: 2")[0]
            validator.configuration = VERSION_6_0
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(2:4): BaseX 6.0 does not support BaseX 9.1 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (94) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if with else")
        fun ifWithElse() {
            val file = parse<XQueryModule>("if (1) then 2 else 3")[0]
            validator.configuration = VERSION_6_0
            validator.validate(file, this@BaseXSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("if without else")
        internal inner class IfWithoutElse {
            @Test
            @DisplayName("BaseX >= 9.1")
            fun supported() {
                val file = parse<XQueryModule>("if (1) then 2")[0]
                validator.configuration = VERSION_9_1
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("BaseX < 9.1")
            fun notSupported() {
                val file = parse<XQueryModule>("if (1) then 2")[0]
                validator.configuration = VERSION_6_0
                validator.validate(file, this@BaseXSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:2): BaseX 6.0 does not support BaseX 9.1 'if' without 'else' constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }
}
