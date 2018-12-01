/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
@file:Suppress("PackageName")

package uk.co.reecedunn.intellij.plugin.xquery.tests.codeInspection

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0031
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0033
import uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0118

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Error Conditions")
private class XQueryInspectionTest : InspectionTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(XQueryInspectionTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XQuery (F) XQST - static errors")
    internal inner class XQSTTest {
        @Nested
        @DisplayName("XQST0031 - unsupported xquery version")
        internal inner class XQST0031Test {
            @Test
            @DisplayName("no VersionDecl")
            fun testNoVersionDecl() {
                val file = parseResource("tests/inspections/xquery/XQST0031/no-versiondecl.xq")

                val problems = inspect(
                    file,
                    XQST0031()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("empty version string")
            fun testEmptyVersionDecl() {
                val file = parseResource("tests/inspections/xquery/XQST0031/empty-version.xq")

                val problems = inspect(
                    file,
                    XQST0031()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(
                    problems[0].descriptionTemplate,
                    `is`("XQST0031: The implementation does not support this XQuery version.")
                )
                assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                assertThat(problems[0].psiElement.text, `is`("\"\""))
            }

            @Test
            @DisplayName("version does not match any known xquery version")
            fun testUnsupportedXQueryVersion() {
                val file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.99.xq")

                val problems = inspect(
                    file,
                    XQST0031()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(
                    problems[0].descriptionTemplate,
                    `is`("XQST0031: The implementation does not support this XQuery version.")
                )
                assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                assertThat(problems[0].psiElement.text, `is`("\"3.99\""))
            }

            @Test
            @DisplayName("vendor supports the xquery version (W3C supports '3.0'?)")
            fun testSupportedVersion_W3C() {
                settings.implementationVersion = "w3c/spec"

                val file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.0.xq")

                val problems = inspect(
                    file,
                    XQST0031()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("vendor does not support the xquery version (W3C supports '1.0-ml'?)")
            fun testUnsupportedVersion_W3C() {
                settings.implementationVersion = "w3c/spec"

                val file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq")

                val problems = inspect(
                    file,
                    XQST0031()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(
                    problems[0].descriptionTemplate,
                    `is`("XQST0031: The implementation does not support this XQuery version.")
                )
                assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                assertThat(problems[0].psiElement.text, `is`("\"1.0-ml\""))
            }

            @Nested
            @DisplayName("MarkLogic Transactions")
            internal inner class MarkLogicTransactions {
                @Test
                @DisplayName("same version; MarkLogic")
                fun testTransactions_SameVersion_MarkLogic() {
                    settings.implementationVersion = "marklogic/v8"

                    val file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq")

                    val problems = inspect(
                        file,
                        XQST0031()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("same version; other implementation")
                fun testTransactions_SameVersion_W3C() {
                    settings.implementationVersion = "w3c/spec"

                    val file = parseResource("tests/inspections/xquery/XQST0031/transaction-same-version.xq")

                    val problems = inspect(
                        file,
                        XQST0031()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(2))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0031: The implementation does not support this XQuery version.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                    assertThat(problems[0].psiElement.text, `is`("\"1.0-ml\""))

                    assertThat(problems[1].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[1].descriptionTemplate,
                        `is`("XQST0031: The implementation does not support this XQuery version.")
                    )
                    assertThat(problems[1].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                    assertThat(problems[1].psiElement.text, `is`("\"1.0-ml\""))
                }

                @Test
                @DisplayName("unsupported version after the first transaction")
                fun testTransactions_UnsupportedOtherVersion() {
                    settings.implementationVersion = "marklogic/v8"

                    val file =
                        parseResource("tests/inspections/xquery/XQST0031/transaction-unsupported-other-version.xq")

                    val problems = inspect(
                        file,
                        XQST0031()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0031: The implementation does not support this XQuery version.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                    assertThat(problems[0].psiElement.text, `is`("\"0.2\""))
                }

                @Test
                @DisplayName("different versions in different transactions")
                fun testTransactions_DifferentVersions() {
                    settings.implementationVersion = "marklogic/v8"

                    val file = parseResource("tests/inspections/xquery/XQST0031/transaction-different-version.xq")

                    val problems = inspect(
                        file,
                        XQST0031()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0031: MarkLogic requires that XQuery versions are the same across different transactions.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
                    assertThat(problems[0].psiElement.text, `is`("\"0.9-ml\""))
                }
            }
        }

        @Nested
        @DisplayName("XQST0033 - duplicate namespace prefix")
        internal inner class XQST0033Test {
            @Test
            @DisplayName("no duplicates")
            fun testNoDuplicates() {
                val file = parseResource("tests/inspections/xquery/XQST0033/no-duplicates.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("ModuleImport duplicate of ModuleDecl")
            fun testModuleDecl_ModuleImport() {
                val file = parseResource("tests/inspections/xquery/XQST0033/ModuleDecl-ModuleImport.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'test' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("test"))
            }

            @Test
            @DisplayName("ModuleDecl missing URILiteral")
            fun testModuleDecl_NoUri() {
                val file = parseResource("tests/inspections/xquery/XQST0033/ModuleDecl-no-uri.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("NamespaceDecl duplicate of ModuleImport")
            fun testModuleImport_NamespaceDecl() {
                val file = parseResource("tests/inspections/xquery/XQST0033/ModuleImport-NamespaceDecl.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("SchemaImport duplicate of NamespaceDecl")
            fun testNamespaceDecl_SchemaImport() {
                val file = parseResource("tests/inspections/xquery/XQST0033/NamespaceDecl-SchemaImport.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("ModuleImport duplicate of SchemaImport")
            fun testSchemaImport_ModuleImport() {
                val file = parseResource("tests/inspections/xquery/XQST0033/SchemaImport-ModuleImport.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("MarkLogic transactions; no duplicates")
            fun testOtherTransaction_NoDuplicates() {
                val file = parseResource("tests/inspections/xquery/XQST0033/other-transaction-no-duplicates.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("MarkLogic transactions; duplicates")
            fun testOtherTransaction_Duplicates() {
                val file = parseResource("tests/inspections/xquery/XQST0033/other-transaction-duplicates.xq")

                val problems = inspect(file,
                    XQST0033()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }
        }

        @Nested
        @DisplayName("XQST0118 - mismatched direct xml element name")
        internal inner class XQST0118Test {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("matching element names")
                fun testNCName_MatchedTags() {
                    val file = parseResource("tests/inspections/xquery/XQST0118/NCName_MatchedTags.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("self-closing element")
                fun testNCName_SelfClosing() {
                    val file = parseResource("tests/inspections/xquery/XQST0118/NCName_SelfClosing.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("mismatching element names")
                fun testNCName_MismatchedTags() {
                    val file = parseResource("tests/inspections/xquery/XQST0118/NCName_MismatchedTags.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'b' does not match the open tag 'a'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.NCNAME))
                    assertThat(problems[0].psiElement.text, `is`("b"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("matching element names (prefix and local name)")
                fun testQName_MatchedPrefixAndLocalName() {
                    val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("self-closing")
                fun testQName_SelfClosing() {
                    val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("missing closing tag")
                fun testQName_MissingClosingTag() {
                    val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("invalid opening tag")
                fun testQName_InvalidOpeningTag() {
                    val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("invalid closing tag")
                fun testQName_InvalidClosingTag() {
                    val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("mismatched prefix")
                fun testQName_MismatchedPrefix() {
                    val file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedPrefix.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'c:b' does not match the open tag 'a:b'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.QNAME))
                    assertThat(problems[0].psiElement.text, `is`("c:b"))
                }

                @Test
                @DisplayName("mismatched local name")
                fun testQName_MismatchedLocalName() {
                    val file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedLocalName.xq")

                    val problems = inspect(
                        file,
                        XQST0118()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'a:c' does not match the open tag 'a:b'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.QNAME))
                    assertThat(problems[0].psiElement.text, `is`("a:c"))
                }
            }
        }
    }
}
