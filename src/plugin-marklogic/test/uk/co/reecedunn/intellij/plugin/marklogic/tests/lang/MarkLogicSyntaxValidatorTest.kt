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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.lang

import com.intellij.lang.LanguageASTFactory
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - MarkLogic")
class MarkLogicSyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition, XPathParserDefinition),
    XpmDiagnostics {
    // region ParsingTestCase

    @BeforeAll
    override fun setUp() {
        super.setUp()
        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        registerExtensionPoint(XpmSyntaxValidator.EP_NAME, XpmSyntaxValidator::class.java)
        registerExtension(XpmSyntaxValidator.EP_NAME, MarkLogicSyntaxValidator)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    // endregion
    // region XpmDiagnostics

    val report = StringBuffer()

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

    val validator = XpmSyntaxValidation()

    // endregion

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    internal inner class AnyBooleanNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of boolean-node()")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of boolean-node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:26): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (50) BooleanConstructor")
    internal inner class BooleanConstructor {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("boolean-node { \"true\" }")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("boolean-node { \"true\" }")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:12): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    internal inner class AnyNumberNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of number-node()")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of number-node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    internal inner class AnyNullNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of null-node()")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of null-node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:23): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    internal inner class AnyArrayNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of array-node()")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of array-node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:24): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (62) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("array")
        fun array() {
            val file = parse<XQueryModule>("array { 1 }")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("array-node")
        internal inner class ArrayNode {
            @Test
            @DisplayName("MarkLogic >= 8.0")
            fun supported() {
                val file = parse<XQueryModule>("array-node { 1 }")[0]
                validator.product = MarkLogic.VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("array-node { 1 }")[0]
                validator.product = MarkLogic.VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:10): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    internal inner class AnyMapNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of object-node()")[0]
            validator.product = MarkLogic.VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of object-node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (66) MapConstructor")
    internal inner class MapConstructor {
        @Test
        @DisplayName("map")
        fun map() {
            val file = parse<XQueryModule>("map { }")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("object-node")
        internal inner class ObjectNode {
            @Test
            @DisplayName("MarkLogic >= 8.0")
            fun supported() {
                val file = parse<XQueryModule>("object-node { }")[0]
                validator.product = MarkLogic.VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("object-node { }")[0]
                validator.product = MarkLogic.VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:11): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (67) AnyKindTest")
    internal inner class AnyKindTest {
        @Test
        @DisplayName("any kind test")
        fun anyKindTest() {
            val file = parse<XQueryModule>("1 instance of node()")[0]
            validator.product = MarkLogic.VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("MarkLogic >= 8.0")
            fun supported() {
                val file = parse<XQueryModule>("1 instance of node(*)")[0]
                validator.product = MarkLogic.VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 instance of node(*)")[0]
                validator.product = MarkLogic.VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(19:20): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }
}
