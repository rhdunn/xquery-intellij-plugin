/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0001
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0002

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Error and Warning Conditions")
private class PluginInspectionTest : InspectionTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin (D.1) Vendor-Specific Behaviour")
    internal inner class IJVS0001Test {
        @Nested
        @DisplayName("IJVS0001 - unsupported construct")
        internal inner class IJVS0001Test {
            @Nested
            @DisplayName("XQuery")
            internal inner class XQueryTest {
                @Test
                @DisplayName("XQuery 3.0 VersionDecl in XQuery 1.0")
                fun testXQuery30VersionDeclInXQuery10() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("XQuery 3.0 VersionDecl in XQuery 3.0")
                fun testXQuery30VersionDecl() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery Update Facility")
            internal inner class UpdateFacilityTest {
                @Test
                @DisplayName("Update Facility 1.0: product conforms to the specification")
                fun testUpdateFacility10_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Update Facility 1.0: product does not conform to the specification")
                fun testUpdateFacility10_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
                }

                @Test
                @DisplayName("Update Facility 3.0: product conforms to the specification")
                fun testUpdateFacility30_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Update Facility 3.0: product does not conform to the specification")
                fun testUpdateFacility30_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

                    val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_INVOKE))
                }

                @Test
                @DisplayName("BaseX: product conforms to the specification")
                fun testUpdateFacilityBaseX_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    settings.implementationVersion = "basex/v8.6"

                    val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("BaseX: product does not conform to the specification")
                fun testUpdateFacilityBaseX_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

                    val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0, or BaseX 8.5 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_TRANSFORM))
                }
            }

            @Nested
            @DisplayName("XQuery Scripting Extension")
            internal inner class ScriptingTest {
                @Test
                @DisplayName("Scripting Extension 1.0: product conforms to the specification")
                fun testScripting10_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Scripting Extension 1.0: product does not conform to the specification")
                fun testScripting10_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Scripting Extension 1.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BLOCK))
                }
            }

            @Nested
            @DisplayName("BaseX")
            internal inner class BaseXTest {
                @Test
                @DisplayName("product conforms to the specification")
                fun testBaseX_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.label
                    settings.implementationVersion = "basex/v8.5"

                    val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("product does not conform to the specification")
                fun testBaseX_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.label
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(2))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support BaseX 7.8 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_UPDATE))

                    assertThat(problems[1].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[1].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs.")
                    )
                    assertThat(problems[1].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
                }
            }

            @Nested
            @DisplayName("MarkLogic")
            internal inner class MarkLogicTest {
                @Test
                @DisplayName("0.9-ml: product conforms to the specification")
                fun testMarkLogic09ml_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.label
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("0.9-ml: product does not conform to the specification")
                fun testMarkLogic09ml_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.label
                    settings.implementationVersion = "saxon/EE/v9.5"

                    val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support MarkLogic 4.0, or XQuery 0.9-ml constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BINARY))
                }

                @Test
                @DisplayName("1.0-ml: product conforms to the specification")
                fun testMarkLogic10ml_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("1.0-ml: product does not conform to the specification")
                fun testMarkLogic10ml_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
                    settings.implementationVersion = "saxon/EE/v9.5"

                    val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support MarkLogic 7.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_SCHEMA_ROOT))
                }
            }
        }

        @Nested
        @DisplayName("IJVS0002 - reserved function name")
        internal inner class IJVS0002Test {
            @Nested
            @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
            internal inner class FunctionCall {
                @Nested
                @DisplayName("MarkLogic 8.0 reserved function names")
                internal inner class MarkLogic80 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 7.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic70() {
                        settings.implementationVersion = "marklogic/v7"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 8.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic80() {
                        settings.implementationVersion = "marklogic/v8"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
                    }
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionCall_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testFunctionCall_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
            internal inner class FunctionDecl {
                @Nested
                @DisplayName("MarkLogic 8.0 reserved function names")
                internal inner class MarkLogic80 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 7.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic70() {
                        settings.implementationVersion = "marklogic/v7"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 8.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic80() {
                        settings.implementationVersion = "marklogic/v8"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
                    }
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionDecl_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testFunctionDecl_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
            internal inner class NamedFunctionRef {
                @Test
                @DisplayName("XQuery 1.0 reserved function names")
                fun testNamedFunctionRef_XQuery10ReservedFunctionName() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq")

                    val problems = inspect(
                        file,
                        IJVS0002()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Reserved XQuery 1.0 keyword used as a function name.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_IF))
                }

                @Test
                @DisplayName("XQuery 3.0 reserved function names")
                fun testNamedFunctionRef_XQuery30ReservedFunctionName() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq")

                    val problems = inspect(
                        file,
                        IJVS0002()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Reserved XQuery 3.0 keyword used as a function name.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_FUNCTION))
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testNamedFunctionRef_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testNamedFunctionRef_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }
        }
    }
}
