// Copyright (C) 2020-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.tests.lang

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
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicSyntaxValidator
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicVersion
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
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - MarkLogic")
class MarkLogicSyntaxValidatorTest : IdeaPlatformTestCase(), LanguageTestCase, XpmDiagnostics {
    override val pluginId: PluginId = PluginId.getId("MarkLogicSyntaxValidatorTest")
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

        XpmSyntaxValidator.register(this, MarkLogicSyntaxValidator)
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
    private val VERSION_5 = XpmLanguageConfiguration(XQuery.VERSION_1_0, MarkLogicVersion(MarkLogic, 5, ""))

    @Suppress("PrivatePropertyName")
    private val VERSION_9 = XpmLanguageConfiguration(XQuery.VERSION_1_0, MarkLogic.VERSION_9)

    @Nested
    @DisplayName("XQuery 3.0 EBNF (27) Annotation")
    internal inner class Annotation {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(8:9): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (28) VarDecl")
    internal inner class VarDecl {
        @Test
        @DisplayName("no value")
        fun noValue() {
            val file = parse<XQueryModule>("declare variable \$x; \$x")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("value")
        fun value() {
            val file = parse<XQueryModule>("declare variable \$x := 2; \$x")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("external; no default value")
        fun externalWithoutDefaultValue() {
            val file = parse<XQueryModule>("declare variable \$x external; \$x")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("external; default value; MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("declare variable \$x external := 2; \$x")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("external; default value; MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("declare variable \$x external := 2; \$x")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(29:31): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (71) SwitchExpr")
    internal inner class SwitchExpr {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("switch (1) case 2 return 3 default return 4")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("switch (1) case 2 return 3 default return 4")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:6): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (76) SequenceTypeUnion")
    internal inner class SequenceTypeUnion {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("typeswitch (1) case xs:string|xs:integer return 2 default return 3")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("typeswitch (1) case xs:string|xs:integer return 2 default return 3")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(29:30): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
        fun sequenceType() {
            val file = parse<XQueryModule>("1 instance of (xs:string*|element(test))")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (78) TryCatchExpr ; XQuery 3.0 EBNF (79) TryClause")
    internal inner class TryCatchExpr {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("try { 1 } catch * { 2 }")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("try { 1 } catch * { 2 }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:3): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    internal inner class StringConcatExpr {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("1 || 2")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 || 2")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:4): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    internal inner class SimpleMapExpr {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("1!2")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1!2")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (120) PostfixExpr ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class PostfixExpr_ArgumentList {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:3): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
    internal inner class ArgumentPlaceholder {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(9:10): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (156) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:9): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    internal inner class NamedFunctionRef {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("true#0")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("true#0")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(4:5): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("function () { 2 } , %public function () { 2 }")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("function () { 2 } , %public function () { 2 }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(0:8): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    E XPST0003(28:36): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    E XPST0003(20:21): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
    internal inner class AnyFunctionTest {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
    internal inner class TypedFunctionTest {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of function(item()) as item()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of function(item()) as item()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:2): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (105) ArrowExpr ; XQuery 4.0 ED EBNF (108) FatArrowTarget")
    internal inner class ArrowExpr {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
        internal inner class ArrowStaticFunction {
            @Test
            @DisplayName("MarkLogic >= 9.0")
            fun supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 9.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): MarkLogic 5.0 does not support MarkLogic 9.0 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
        internal inner class ArrowDynamicFunction {
            @Test
            @DisplayName("MarkLogic >= 9.0")
            fun supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 9.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): MarkLogic 5.0 does not support MarkLogic 9.0 constructs.")
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    internal inner class ForwardAxis {
        @Test
        @DisplayName("child")
        fun child() {
            val file = parse<XQueryModule>("child::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("descendant")
        fun descendant() {
            val file = parse<XQueryModule>("descendant::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("attribute")
        fun attribute() {
            val file = parse<XQueryModule>("attribute::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("self")
        fun self() {
            val file = parse<XQueryModule>("self::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("descendant-or-self")
        fun descendantOrSelf() {
            val file = parse<XQueryModule>("descendant-or-self::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("following-sibling")
        fun followingSibling() {
            val file = parse<XQueryModule>("following-sibling::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("following")
        fun following() {
            val file = parse<XQueryModule>("following::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("namespace")
        internal inner class Namespace {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("namespace::para")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("namespace::para")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:9): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("property")
        internal inner class Property {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("property::para")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("property::para")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:8): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Nested
        @DisplayName("function declaration")
        internal inner class FunctionDeclaration {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("declare private function test() external;")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("declare private function test() external;")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(8:15): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("variable declaration")
        internal inner class VariableDeclaration {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("declare private variable \$x external;")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("declare private variable \$x external;")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(8:15): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (27) ValidateExpr")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("no type")
        fun noType() {
            val file = parse<XQueryModule>("validate { \"true\" }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("lax mode")
        fun laxMode() {
            val file = parse<XQueryModule>("validate lax { \"true\" }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("strict mode")
        fun strictMode() {
            val file = parse<XQueryModule>("validate strict { \"true\" }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("full mode")
        internal inner class FullMode {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("validate full { \"true\" }")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("validate full { \"true\" }")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(9:13): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("type TypeName")
        internal inner class TypeTypeName {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("validate type xs:boolean { \"true\" }")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("validate type xs:boolean { \"true\" }")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(9:13): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("as TypeName")
        internal inner class AsTypeName {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("validate as xs:boolean { \"true\" }")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("validate as xs:boolean { \"true\" }")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(9:11): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
    internal inner class BinaryTest {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of binary()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of binary()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:20): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (30) BinaryConstructor")
    internal inner class BinaryConstructor {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("binary { \"A0\" }")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("binary { \"A0\" }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:6): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
    internal inner class CatchClause {
        @Test
        @DisplayName("error code")
        fun errorCode() {
            val file = parse<XQueryModule>("try { 1 } catch * { 2 }")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:3): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.")
            )
        }

        @Nested
        @DisplayName("error variable")
        internal inner class ErrorVariable {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("try { 1 } catch (\$e) { 2 }")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("try { 1 } catch (\$e) { 2 }")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(0:3): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        E XPST0003(16:17): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
    internal inner class StylesheetImport {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("import stylesheet at \"test.xsl\"; 2")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("import stylesheet at \"test.xsl\"; 2")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(7:17): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (35) TransactionSeparator")
    internal inner class TransactionSeparator {
        @Test
        @DisplayName("single transaction; no semicolon")
        fun single_noSemicolon() {
            val file = parse<XQueryModule>("1234")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("single transaction; with semicolon")
        fun single_semicolon() {
            val file = parse<XQueryModule>("descendant::para")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Nested
        @DisplayName("multiple transactions; semicolon at end")
        internal inner class Multiple_SemicolonAtEnd {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("2; 3;")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("2; 3;")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(1:2): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("multiple transactions; no semicolon at end")
        internal inner class Multiple_NoSemicolonAtEnd {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("2; 3")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("2; 3")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(1:2): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }

        @Nested
        @DisplayName("multiple transactions; prolog in other transaction")
        internal inner class Multiple_PrologInOtherTransaction {
            @Test
            @DisplayName("MarkLogic >= 6.0")
            fun supported() {
                val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 2 ; xquery version \"1.0-ml\"; 3")[0]
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 6.0")
            fun notSupported() {
                val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 2; xquery version \"1.0-ml\"; 3")[0]
                validator.configuration = VERSION_5
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(
                    report.toString(), `is`(
                        """
                        E XPST0003(26:27): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
    internal inner class AttributeDeclTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of attribute-decl()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of attribute-decl()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:28): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
    internal inner class ComplexTypeTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of complex-type()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of complex-type()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:26): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
    internal inner class ElementDeclTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of element-decl()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of element-decl()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:26): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
    internal inner class SchemaComponentTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-component()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-component()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:30): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
    internal inner class SchemaParticleTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-particle()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-particle()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:29): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
    internal inner class SchemaRootTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-root()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-root()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
    internal inner class SchemaTypeTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-type()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-type()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
    internal inner class SimpleTypeTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of simple-type()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of simple-type()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
    internal inner class SchemaFacetTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-facet()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-facet()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:26): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (47) BooleanNodeTest ; XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    internal inner class AnyBooleanNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of boolean-node()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of boolean-node()")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (47) BooleanNodeTest ; XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    internal inner class NamedBooleanNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of boolean-node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of boolean-node(\"key\")")[0]
            validator.configuration = VERSION_5
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
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("boolean-node { \"true\" }")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (51) NumberNodeTest ; XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    internal inner class AnyNumberNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of number-node()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of number-node()")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (51) NumberNodeTest ; XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    internal inner class NamedNumberNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of number-node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of number-node(\"key\")")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (54) NumberConstructor")
    internal inner class NumberConstructor {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("number-node { 2 }")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("number-node { 2 }")[0]
            validator.configuration = VERSION_5
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

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (55) NullNodeTest ; XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    internal inner class AnyNullNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of null-node()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of null-node()")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (55) NullNodeTest ; XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    internal inner class NamedNullNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of null-node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of null-node(\"key\")")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (58) NullConstructor")
    internal inner class NullConstructor {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("null-node {}")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("null-node {}")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:9): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (59) ArrayNodeTest ; XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    internal inner class AnyArrayNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of array-node()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of array-node()")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (59) ArrayNodeTest ; XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    internal inner class NamedArrayNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of array-node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of array-node(\"key\")")[0]
            validator.configuration = VERSION_5
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
            validator.configuration = VERSION_5
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
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("array-node { 1 }")[0]
                validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (63) MapNodeTest ; XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    internal inner class AnyMapNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of object-node()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of object-node()")[0]
            validator.configuration = VERSION_5
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (63) MapNodeTest ; XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    internal inner class NamedMapNodeTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of object-node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of object-node(\"key\")")[0]
            validator.configuration = VERSION_5
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
            validator.configuration = VERSION_5
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
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("object-node { }")[0]
                validator.configuration = VERSION_5
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
            validator.configuration = VERSION_5
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
                validator.configuration = VERSION_9
                validator.validate(file, this@MarkLogicSyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("MarkLogic < 8.0")
            fun notSupported() {
                val file = parse<XQueryModule>("1 instance of node(*)")[0]
                validator.configuration = VERSION_5
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

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
    internal inner class NamedKindTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of node(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of node(\"key\")")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(19:24): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
    internal inner class NamedTextTest {
        @Test
        @DisplayName("MarkLogic >= 8.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of text(\"key\")")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 8.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of text(\"key\")")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(19:24): MarkLogic 5.0 does not support MarkLogic 8.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (103) SchemaWildcardTest")
    internal inner class SchemaWildcardTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of schema-wildcard()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of schema-wildcard()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:29): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (104) ModelGroupTest")
    internal inner class ModelGroupTest {
        @Test
        @DisplayName("MarkLogic >= 7.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of model-group()")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 7.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of model-group()")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(14:25): MarkLogic 5.0 does not support MarkLogic 7.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (105) UsingDecl")
    internal inner class UsingDecl {
        @Test
        @DisplayName("MarkLogic >= 6.0")
        fun supported() {
            val file = parse<XQueryModule>("using namespace \"http://www.example.com/test\"; 2")[0]
            validator.configuration = VERSION_9
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("MarkLogic < 6.0")
        fun notSupported() {
            val file = parse<XQueryModule>("using namespace \"http://www.example.com/test\"; 2")[0]
            validator.configuration = VERSION_5
            validator.validate(file, this@MarkLogicSyntaxValidatorTest)
            assertThat(
                report.toString(), `is`(
                    """
                    E XPST0003(0:5): MarkLogic 5.0 does not support MarkLogic 6.0 constructs.
                    """.trimIndent()
                )
            )
        }
    }
}
