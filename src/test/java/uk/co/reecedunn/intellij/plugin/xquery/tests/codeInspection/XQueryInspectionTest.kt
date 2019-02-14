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
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0033
import uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0118
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType2

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Error Conditions")
private class XQueryInspectionTest : InspectionTestCase() {
    @Nested
    @DisplayName("XQuery (F) XQST - static errors")
    internal inner class XQSTTest {
        @Nested
        @DisplayName("XQST0031 - unsupported xquery version")
        internal inner class XQST0031Test {
            @Test
            @DisplayName("no VersionDecl")
            fun noVersionDecl() {
                val file = parse<XQueryModule>("2")[0]

                val problems = inspect(file, XQST0031())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("empty version string")
            fun emptyVersionDecl() {
                val file = parse<XQueryModule>("xquery version \"\"; 2")[0]

                val problems = inspect(file, XQST0031())
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
            fun unsupportedXQueryVersion() {
                val file = parse<XQueryModule>("xquery version \"3.99\"; 2")[0]

                val problems = inspect(file, XQST0031())
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
            fun supportedVersion_W3C() {
                settings.implementationVersion = "w3c/spec"

                val file = parse<XQueryModule>("xquery version \"3.0\"; 2")[0]

                val problems = inspect(file, XQST0031())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("vendor does not support the xquery version (W3C supports '1.0-ml'?)")
            fun unsupportedVersion_W3C() {
                settings.implementationVersion = "w3c/spec"

                val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 2")[0]

                val problems = inspect(file, XQST0031())
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
                fun sameVersion_MarkLogic() {
                    settings.implementationVersion = "marklogic/v8"

                    val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 2")[0]

                    val problems = inspect(file, XQST0031())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("same version; other implementation")
                fun sameVersion_W3C() {
                    settings.implementationVersion = "w3c/spec"

                    val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 1 ; xquery version \"1.0-ml\"; 2")[0]

                    val problems = inspect(file, XQST0031())
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
                fun unsupportedOtherVersion() {
                    settings.implementationVersion = "marklogic/v8"

                    val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 1 ; xquery version \"0.2\"; 2")[0]

                    val problems = inspect(file, XQST0031())
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
                fun differentVersions() {
                    settings.implementationVersion = "marklogic/v8"

                    val file = parse<XQueryModule>("xquery version \"1.0-ml\"; 1 ; xquery version \"0.9-ml\"; 2")[0]

                    val problems = inspect(file, XQST0031())
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
            fun noDuplicates() {
                val file = parse<XQueryModule>(
                    """
                    module namespace one = "http://example.com/one";
                    import module namespace two = "http://example.com/two";
                    import schema namespace three = "http://example.com/three";
                    declare namespace four = "http://example.com/four";
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("ModuleImport duplicate of ModuleDecl")
            fun moduleDecl_ModuleImport() {
                val file = parse<XQueryModule>(
                    """
                        module namespace test = "http://example.com/test";
                        import module namespace test = "http://example.com/test2";
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'test' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("test"))
            }

            @Test
            @DisplayName("ModuleDecl missing URILiteral")
            fun moduleDecl_NoUri() {
                val file = parse<XQueryModule>(
                    """
                        module namespace one
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("NamespaceDecl duplicate of ModuleImport")
            fun moduleImport_NamespaceDecl() {
                val file = parse<XQueryModule>(
                    """
                        module namespace test = "http://example.com/test";
                        import module namespace one = "http://example.com/one";
                        declare namespace one = "http://example.com/1";
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("SchemaImport duplicate of NamespaceDecl")
            fun namespaceDecl_SchemaImport() {
                val file = parse<XQueryModule>(
                    """
                        module namespace test = "http://example.com/test";
                        declare namespace one = "http://example.com/one";
                        import schema namespace one = "http://example.com/1";
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("ModuleImport duplicate of SchemaImport")
            fun schemaImport_ModuleImport() {
                val file = parse<XQueryModule>(
                    """
                        module namespace test = "http://example.com/test";
                        import schema namespace one = "http://example.com/one";
                        import module namespace one = "http://example.com/1";
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XQST0033: The namespace prefix 'one' has already been defined."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("one"))
            }

            @Test
            @DisplayName("MarkLogic transactions; no duplicates")
            fun otherTransaction_NoDuplicates() {
                val file = parse<XQueryModule>(
                    """
                        import module namespace one = "http://example.com/one";
                        import schema namespace two = "http://example.com/two";
                        declare namespace three = "http://example.com/three";
                        ()
                        ;
                        import module namespace one = "http://example.com/one";
                        import schema namespace two = "http://example.com/two";
                        declare namespace three = "http://example.com/three";
                        ()
                    """
                )[0]

                val problems = inspect(file, XQST0033())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("MarkLogic transactions; duplicates")
            fun otherTransaction_Duplicates() {
                val file = parse<XQueryModule>(
                    """
                        import module namespace one = "http://example.com/one";
                        import schema namespace two = "http://example.com/two";
                        declare namespace three = "http://example.com/three";
                        ()
                        ;
                        import module namespace one = "http://example.com/one";
                        import schema namespace two = "http://example.com/two";
                        declare namespace one = "http://example.com/three";
                        ()
                    """
                )[0]

                val problems = inspect(file, XQST0033())
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
                fun matchedTags() {
                    val file = parse<XQueryModule>("<a></a>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("self-closing element")
                fun selfClosing() {
                    val file = parse<XQueryModule>("<a/>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("mismatching element names")
                fun mismatchedTags() {
                    val file = parse<XQueryModule>("<a></b>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'b' does not match the open tag 'a'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType2.NCNAME))
                    assertThat(problems[0].psiElement.text, `is`("b"))
                }

                @Test
                @DisplayName("closing tag only")
                fun closingTagOnly() {
                    val file = parse<XQueryModule>("</a>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("partial closing tag only")
                fun partialClosingTagOnly() {
                    val file = parse<XQueryModule>("</<test>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("matching element names (prefix and local name)")
                fun matchedPrefixAndLocalName() {
                    val file = parse<XQueryModule>("<a:b></a:b>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("self-closing")
                fun selfClosing() {
                    val file = parse<XQueryModule>("<h:br/>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("error recovery: missing closing tag")
                fun missingClosingTag() {
                    val file = parse<XQueryModule>("<a:b>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("error recovery: invalid opening tag")
                fun invalidOpeningTag() {
                    val file = parse<XQueryModule>("<a:></a:b>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("error recovery: invalid closing tag")
                fun invalidClosingTag() {
                    val file = parse<XQueryModule>("<a:b></a:>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("mismatched prefix")
                fun mismatchedPrefix() {
                    val file = parse<XQueryModule>("<a:b></c:b>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'c:b' does not match the open tag 'a:b'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType2.QNAME))
                    assertThat(problems[0].psiElement.text, `is`("c:b"))
                }

                @Test
                @DisplayName("mismatched local name")
                fun mismatchedLocalName() {
                    val file = parse<XQueryModule>("<a:b></a:c>")[0]

                    val problems = inspect(file, XQST0118())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XQST0118: The closing tag 'a:c' does not match the open tag 'a:b'.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType2.QNAME))
                    assertThat(problems[0].psiElement.text, `is`("a:c"))
                }
            }
        }
    }
}
