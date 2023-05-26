/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.w3.lang.W3CSpecifications
import uk.co.reecedunn.intellij.plugin.w3.lang.XQuerySyntaxValidator
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
@DisplayName("XQuery IntelliJ Plugin - Syntax Validation - XQuery")
class XQuerySyntaxValidatorTest :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    XpmDiagnostics {

    override val pluginId: PluginId = PluginId.getId("XQuerySyntaxValidatorTest")

    // region ParsingTestCase

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        XpmSyntaxValidator.register(this, XQuerySyntaxValidator)
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
    private val XQUERY_3_0 = XpmLanguageConfiguration(XQuery.VERSION_3_0, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val XQUERY_3_1 = XpmLanguageConfiguration(XQuery.VERSION_3_1, W3CSpecifications.REC)

    @Suppress("PrivatePropertyName")
    private val XQUERY_4_0 = XpmLanguageConfiguration(XQuery.VERSION_4_0, W3CSpecifications.REC)

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
    @DisplayName("XQuery 3.0 EBNF (18) DecimalFormatDecl")
    internal inner class DecimalFormatDecl {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("declare decimal-format test; 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(8:22): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("declare decimal-format test; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("all properties")
        fun allProperties() {
            val file = parse<XQueryModule>(
                """
                declare decimal-format test decimal-separator = "."
                                            grouping-separator = "#"
                                            infinity = "INF"
                                            minus-sign = "-"
                                            NaN = "NaN"
                                            percent = "%"
                                            per-mille = ","
                                            zero-digit = "0"
                                            digit = "1"
                                            pattern-separator = "|";
                2
                """.trimIndent()
            )[0]
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
        fun notSupported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(30:31): XQuery version string '1.0' does not support XQuery 3.1 constructs.
                    E XPST0003(8:9): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("declare %private function f() {}; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(30:31): XQuery version string '3.0' does not support XQuery 3.1 constructs.")
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
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("value")
        fun value() {
            val file = parse<XQueryModule>("declare variable \$x := 2; \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("external; no default value")
        fun externalWithoutDefaultValue() {
            val file = parse<XQueryModule>("declare variable \$x external; \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("external; default value; XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("declare variable \$x external := 2; \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(29:31): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("external; default value; XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("declare variable \$x external := 2; \$x")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
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
    @DisplayName("XQuery 3.0 EBNF (44) ForClause")
    internal inner class ForClause {
        @Test
        @DisplayName("after 'for'")
        fun afterFor() {
            val file = parse<XQueryModule>("for \$x in () for \$y in () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'let'")
        fun afterLet() {
            val file = parse<XQueryModule>("for \$x in () let \$y := () for \$z in () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'where'; XQuery < 3.0")
        fun afterWhere_notSupported() {
            val file = parse<XQueryModule>("for \$x in () where true() for \$y in () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(26:29): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'where'; XQuery >= 3.0")
        fun afterWhere_supported() {
            val file = parse<XQueryModule>("for \$x in () where true() for \$y in () return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'order by'; XQuery < 3.0")
        fun afterOrderBy_notSupported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x for \$y in () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(25:28): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'order by'; XQuery >= 3.0")
        fun afterOrderBy_supported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x for \$y in () return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
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
    @DisplayName("XQuery 3.0 EBNF (48) LetClause")
    internal inner class LetClause {
        @Test
        @DisplayName("after 'for'")
        fun afterFor() {
            val file = parse<XQueryModule>("for \$x in () let \$y := () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'let'")
        fun afterLet() {
            val file = parse<XQueryModule>("let \$x := () let \$y := () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'where'; XQuery < 3.0")
        fun afterWhere_notSupported() {
            val file = parse<XQueryModule>("let \$x := () where true() let \$y := () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(26:29): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'where'; XQuery >= 3.0")
        fun afterWhere_supported() {
            val file = parse<XQueryModule>("let \$x := () where true() let \$y := () return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'order by'; XQuery < 3.0")
        fun afterOrderBy_notSupported() {
            val file = parse<XQueryModule>("let \$x := () order by \$x let \$y := () return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(25:28): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'order by'; XQuery >= 3.0")
        fun afterOrderBy_supported() {
            val file = parse<XQueryModule>("let \$x := () order by \$x let \$y := () return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (51) TumblingWindowClause")
    internal inner class TumblingWindowClause {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("for tumbling window \$x in \$y start when true() return \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(4:12): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("for tumbling window \$x in \$y start when true() return \$x")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (52) SlidingWindowClause")
    internal inner class SlidingWindowClause {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>(
                "for sliding window \$x in \$y start when true() end when false() return \$x"
            )[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(4:11): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("for sliding window \$x in \$y start when true() end when false() return \$x")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (59) CountClause")
    internal inner class CountClause {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("for \$x in () count \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(13:18): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("for \$x in () count \$x return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (60) WhereClause")
    internal inner class WhereClause {
        @Test
        @DisplayName("after 'for'")
        fun afterFor() {
            val file = parse<XQueryModule>("for \$x in () for \$y in () where true() return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'let'")
        fun afterLet() {
            val file = parse<XQueryModule>("let \$x := () let \$y := () where true() return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'where'; XQuery < 3.0")
        fun afterWhere_notSupported() {
            val file = parse<XQueryModule>("for \$x in () where true() where true() return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(26:31): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'where'; XQuery >= 3.0")
        fun afterWhere_supported() {
            val file = parse<XQueryModule>("for \$x in () where true() where true() return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'order by'; XQuery < 3.0")
        fun afterOrderBy_notSupported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x where true() return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(25:30): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'order by'; XQuery >= 3.0")
        fun afterOrderBy_supported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x where true() return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (61) GroupByClause")
    internal inner class GroupByClause {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("for \$x in () group by \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(13:18): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("for \$x in () group by \$x return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (65) OrderByClause")
    internal inner class OrderByClause {
        @Test
        @DisplayName("after 'for'")
        fun afterFor() {
            val file = parse<XQueryModule>("for \$x in () for \$y in () order by \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'let'")
        fun afterLet() {
            val file = parse<XQueryModule>("let \$x := () let \$y := () order by \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'where'")
        fun afterWhere() {
            val file = parse<XQueryModule>("for \$x in () where true() order by \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("after 'order by'; XQuery < 3.0")
        fun afterOrderBy_notSupported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x order by \$x return ()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    "E XPST0003(25:30): XQuery version string '1.0' does not support XQuery 3.0 relaxed FLWOR ordering constructs."
                )
            )
        }

        @Test
        @DisplayName("after 'order by'; XQuery >= 3.0")
        fun afterOrderBy_supported() {
            val file = parse<XQueryModule>("for \$x in () order by \$x order by \$x return ()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (71) SwitchExpr")
    internal inner class SwitchExpr {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("switch (1) case 2 return 3 default return 4")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:6): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("switch (1) case 2 return 3 default return 4")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (76) SequenceTypeUnion")
    internal inner class SequenceTypeUnion {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("typeswitch (1) case xs:string|xs:integer return 2 default return 3")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(29:30): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("typeswitch (1) case xs:string|xs:integer return 2 default return 3")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
        fun sequenceType() {
            val file = parse<XQueryModule>("1 instance of (xs:string*|element(test))")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (78) TryCatchExpr ; XQuery 3.0 EBNF (79) TryClause")
    internal inner class TryCatchExpr {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("try { 1 } catch * { 2 }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:3): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("try { 1 } catch * { 2 }")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    internal inner class StringConcatExpr {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 || 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("1 || 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (101) ValidateExpr")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("no type")
        fun noType() {
            val file = parse<XQueryModule>("validate { \"true\" }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("lax")
        fun lax() {
            val file = parse<XQueryModule>("validate lax { \"true\" }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("strict")
        fun strict() {
            val file = parse<XQueryModule>("validate strict { \"true\" }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("validate type xs:boolean { \"true\" }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(9:13): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("validate type xs:boolean { \"true\" }")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    internal inner class SimpleMapExpr {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1!2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("1!2")[0]
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
        fun notSupported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:3): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("\$f(1, 2, 3)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
    internal inner class ArgumentPlaceholder {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(9:10): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("math:pow(?, 2)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (156) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:9): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("namespace test {\"http://www.example.com\"}")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    internal inner class NamedFunctionRef {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("true#0")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(4:5): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("true#0")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    internal inner class InlineFunctionExpr_XQuery30 {
        @Test
        @DisplayName("with parameters")
        fun withParameters() {
            val file = parse<XQueryModule>("function (\$x) { 2 } , %public function (\$x) { 2 }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(0:8): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    E XPST0003(30:38): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    E XPST0003(22:23): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery < 3.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("function () { 2 } , %public function () { 2 }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(0:8): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    E XPST0003(28:36): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    E XPST0003(20:21): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("function () { 2 } , %public function () { 2 }")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
    internal inner class NamespaceNodeTest {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("namespace-node()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:14): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("namespace-node()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
    internal inner class AnyFunctionTest {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of function(*)")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
    internal inner class TypedFunctionTest {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of function(item()) as item()")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:22): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of function(item()) as item()")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("XQuery < 3.0")
        fun notSupported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:2): XQuery version string '1.0' does not support XQuery 3.0 constructs.")
            )
        }

        @Test
        @DisplayName("XQuery >= 3.0")
        fun supported() {
            val file = parse<XQueryModule>("Q{http://www.example.com}test")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
    internal inner class DecimalFormatDecl_XQuery31 {
        @Test
        @DisplayName("XQuery < 3.1")
        fun notSupported() {
            val file = parse<XQueryModule>("declare decimal-format test exponent-separator = \"e\"; 2")[0]
            validator.configuration = XQUERY_3_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(28:46): XQuery version string '3.0' does not support XQuery 3.1 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery >= 3.1")
        fun supported() {
            val file = parse<XQueryModule>("declare decimal-format test exponent-separator = \"e\"; 2")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr")
    internal inner class EnclosedExpr {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl; XQuery 3.1 EBNF (35) FunctionBody")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("XQuery < 3.1")
            fun xquery_notSupported() {
                val file = parse<XQueryModule>("declare function local:test() {};")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(30:31): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun xquery_supported() {
                val file = parse<XQueryModule>("function () {} , %public function () {}")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr; XQuery 3.1 EBNF (35) FunctionBody")
        internal inner class InlineFunctionExpr {
            @Test
            @DisplayName("XQuery < 3.0 (InlineFunctionExpr not supported)")
            fun inlineFunctionExpr_notSupported() {
                val file = parse<XQueryModule>("function () {} , %public function () {}")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`(
                        """
                        E XPST0003(12:13): XQuery version string '1.0' does not support XQuery 3.1 constructs.
                        E XPST0003(37:38): XQuery version string '1.0' does not support XQuery 3.1 constructs.
                        E XPST0003(17:18): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("XQuery == 3.0")
            fun xquery_notSupported() {
                val file = parse<XQueryModule>("function () {} , %public function () {}")[0]
                validator.configuration = XQUERY_3_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`(
                        """
                        E XPST0003(12:13): XQuery version string '3.0' does not support XQuery 3.1 constructs.
                        E XPST0003(37:38): XQuery version string '3.0' does not support XQuery 3.1 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun xquery_supported() {
                val file = parse<XQueryModule>("function () {} , %public function () {}")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (78) TryCatchExpr ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class TryCatchExpr {
            @Test
            @DisplayName("XQuery < 3.0 (TryCatchExpr not supported)")
            fun tryCatchExpr_notSupported() {
                val file = parse<XQueryModule>("try { } catch * { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`(
                        """
                        E XPST0003(4:5): XQuery version string '1.0' does not support XQuery 3.1 constructs.
                        E XPST0003(16:17): XQuery version string '1.0' does not support XQuery 3.1 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("XQuery == 3.0")
            fun xquery_notSupported() {
                val file = parse<XQueryModule>("try { } catch * { }")[0]
                validator.configuration = XQUERY_3_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`(
                        """
                        E XPST0003(4:5): XQuery version string '3.0' does not support XQuery 3.1 constructs.
                        E XPST0003(16:17): XQuery version string '3.0' does not support XQuery 3.1 constructs.
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun xquery_supported() {
                val file = parse<XQueryModule>("try { } catch * { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (135) OrderedExpr ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class OrderedExpr {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("ordered { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(8:9): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("ordered { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (136) UnorderedExpr ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class UnorderedExpr {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("unordered { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(10:11): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("unordered { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("<a test=\"one{ }two\"/>")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(12:13): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("<a test=\"one{ }two\"/>")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class DirElemContent {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("<a>one{ }two</a>")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(6:7): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("<a>one{ }two</a>")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class CompDocConstructor {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("document { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(9:10): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("document { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor ; XQuery 3.1 EBNF (162) EnclosedPrefixExpr")
        internal inner class EnclosedPrefixExpr {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("namespace { } { 2 }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(10:11): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("namespace { } { 2 }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor ; XQuery 3.1 EBNF (163) EnclosedURIExpr")
        internal inner class EnclosedURIExpr {
            @Test
            @DisplayName("NCName prefix ; XQuery < 3.1")
            fun ncnamePrefix_notSupported() {
                val file = parse<XQueryModule>("namespace test { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(15:16): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("NCName prefix ; XQuery >= 3.1")
            fun ncnamePrefix_supported() {
                val file = parse<XQueryModule>("namespace test { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("enclosed prefix ; XQuery < 3.1")
            fun enclosedExprPrefix_notSupported() {
                val file = parse<XQueryModule>("namespace { 1 } { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(16:17): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("enclosed prefix ; XQuery >= 3.1")
            fun enclosedExprPrefix_supported() {
                val file = parse<XQueryModule>("namespace { 1 } { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class CompTextConstructor {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("text { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(5:6): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("text { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (165) CompCommentConstructor ; XQuery 3.1 EBNF (36) EnclosedExpr")
        internal inner class CompCommentConstructor {
            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("comment { }")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(8:9): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }

            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("comment { }")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (105) ArrowExpr ; XQuery 4.0 ED EBNF (108) FatArrowTarget")
    internal inner class ArrowExpr {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
        internal inner class ArrowStaticFunction {
            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("1 => f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
        internal inner class ArrowDynamicFunction {
            @Test
            @DisplayName("XQuery >= 3.1")
            fun supported() {
                val file = parse<XQueryModule>("1 => \$f()")[0]
                validator.configuration = XQUERY_3_1
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(report.toString(), `is`(""))
            }

            @Test
            @DisplayName("XQuery < 3.1")
            fun notSupported() {
                val file = parse<XQueryModule>("1 => \$f()")[0]
                validator.configuration = XQUERY_1_0
                validator.validate(file, this@XQuerySyntaxValidatorTest)
                assertThat(
                    report.toString(),
                    `is`("E XPST0003(2:4): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
                )
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (125) Lookup")
    internal inner class Lookup_XQuery31 {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>(".?2")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>(".?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
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
    @DisplayName("XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("array { 1, 2, 3 }")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("array { 1, 2, 3 }")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:5): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (177) StringConstructor")
    internal inner class StringConstructor {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("``[One `{ 2 }` Three]``")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("``[One `{ 2 }` Three]``")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:3): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (181) UnaryLookup")
    internal inner class UnaryLookup_XQuery31 {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("?2")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
    internal inner class AnyMapTest {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of map(*)")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of map(*)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:17): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
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
    @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
    internal inner class TypedArrayTest {
        @Test
        @DisplayName("XQuery >= 3.1")
        fun xquery_supported() {
            val file = parse<XQueryModule>("1 instance of array(xs:string)")[0]
            validator.configuration = XQUERY_3_1
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 3.1")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("1 instance of array(xs:string)")[0]
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
    @DisplayName("XQuery 4.0 ED EBNF (38) ItemTypeDecl")
    internal inner class ItemTypeDecl {
        @Test
        @DisplayName("Saxon 10 syntax")
        fun saxon10() {
            val file = parse<XQueryModule>("declare type a:test = xs:string;")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("declare item-type a:test = xs:string;")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("declare item-type a:test = xs:string;")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(8:17): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
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
    @DisplayName("XQuery 4.0 ED EBNF (45) TernaryConditionalExpr")
    internal inner class TernaryConditionalExpr {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("1 eq 2 ?? 3 !! 4")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 eq 2 ?? 3 !! 4")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(7:9): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (53) ForMemberBinding")
    internal inner class ForMemberBinding {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("for member \$x in \$y return \$x")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(4:10): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (96) OtherwiseExpr")
    internal inner class OtherwiseExpr {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 otherwise 2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(2:11): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
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
    internal inner class Lookup_XQuery40 {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val file = parse<XQueryModule>(".?test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }

        @Test
        @DisplayName("IntegerLiteral")
        fun integerLiteral() {
            val file = parse<XQueryModule>(".?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
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
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }

        @Test
        @DisplayName("Wildcard")
        fun wildcard() {
            val file = parse<XQueryModule>(".?*")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(1:2): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (187) InlineFunctionExpr")
    internal inner class InlineFunctionExpr_XQuery40 {
        @Test
        @DisplayName("XQuery < 4.0")
        fun xquery_notSupported() {
            val file = parse<XQueryModule>("-> () { 2 } , -> { 2 } , %public -> { 2 } , -> {}")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`(
                    """
                    E XPST0003(0:2): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    E XPST0003(14:16): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    E XPST0003(33:35): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    E XPST0003(25:26): XQuery version string '1.0' does not support XQuery 3.0 constructs.
                    E XPST0003(44:46): XQuery version string '1.0' does not support XQuery 4.0 constructs.
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery >= 4.0")
        fun xquery_supported() {
            val file = parse<XQueryModule>("-> () { 2 } , -> { 2 } , %public -> { 2 } , -> {}")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (199) UnaryLookup ; XQuery 4.0 ED EBNF (141) KeySpecifier")
    internal inner class UnaryLookup_XQuery40 {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val file = parse<XQueryModule>("?test")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }

        @Test
        @DisplayName("IntegerLiteral")
        fun integerLiteral() {
            val file = parse<XQueryModule>("?2")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
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
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }

        @Test
        @DisplayName("Wildcard")
        fun wildcard() {
            val file = parse<XQueryModule>("?*")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(0:1): XQuery version string '1.0' does not support XQuery 3.1 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (212) AttributeTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class AttributeTest {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: XQuery 4.0 ED :)
                """.trimIndent()
            )[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupportedEE() {
            val file = parse<XQueryModule>(
                """
                1 instance of attribute(), (: XPath/XQuery :)
                2 instance of attribute(ns:test), (: XPath/XQuery :)
                3 instance of attribute(test:*) (: XQuery 4.0 ED :)
                """.trimIndent()
            )[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(123:129): XQuery version string '1.0' does not support XQuery 4.0 wildcard local or prefix part in 'AttributeTest' constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (215) ElementTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class ElementTest {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: XQuery 4.0 ED :)
                """.trimIndent()
            )[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>(
                """
                1 instance of element(), (: XPath/XQuery :)
                2 instance of element(ns:test), (: XPath/XQuery :)
                3 instance of element(test:*) (: XQuery 4.0 ED :)
                """.trimIndent()
            )[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(117:123): XQuery version string '1.0' does not support XQuery 4.0 wildcard local or prefix part in 'ElementTest' constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (230) RecordTest")
    internal inner class RecordTest {
        @Test
        @DisplayName("Saxon 10 tuple test")
        fun saxon10() {
            val file = parse<XQueryModule>("1 instance of tuple(a as xs:string, b as .., \"c\"? as xs:int, *)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of record(a as xs:string, b as .., \"c\"? as xs:int, *)")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of record(a as xs:string, b as .., \"c\"? as xs:int, *)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:20): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (233) LocalUnionType")
    internal inner class LocalUnionType {
        @Test
        @DisplayName("XQuery >= 4.0")
        fun supported() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = XQUERY_4_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(report.toString(), `is`(""))
        }

        @Test
        @DisplayName("XQuery < 4.0")
        fun notSupported() {
            val file = parse<XQueryModule>("1 instance of union(xs:integer, xs:double)")[0]
            validator.configuration = XQUERY_1_0
            validator.validate(file, this@XQuerySyntaxValidatorTest)
            assertThat(
                report.toString(),
                `is`("E XPST0003(14:19): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
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
                report.toString(),
                `is`("E XPST0003(14:18): XQuery version string '1.0' does not support XQuery 4.0 constructs.")
            )
        }
    }
}
