/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotation

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.prettyPrint
import uk.co.reecedunn.intellij.plugin.xslt.annotation.ValueTemplateAnnotator as XPathQNameAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery QNameAnnotator")
private class QNameAnnotatorTest : AnnotatorTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 EBNF (120) Wildcard")
    internal inner class Wildcard {
        @Test
        @DisplayName("any")
        fun any() {
            val file = parse<XQueryModule>("*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:1) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: identifier")
        fun wildcard() {
            val file = parse<XQueryModule>("lorem:*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (6:7) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: keyword")
        fun keywordPrefixPart() {
            val file = parse<XQueryModule>("cast:*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (5:6) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: missing")
        fun missingPrefixPart() {
            val file = parse<XQueryModule>(":*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("local name: keyword")
        fun keywordLocalPart() {
            val file = parse<XQueryModule>("*:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (2:6) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: missing")
        fun missingLocalPart() {
            val file = parse<XQueryModule>("*:")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:1) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Nested
        @DisplayName("whitespace in QName")
        internal inner class WhitespaceInQName {
            @Test
            @DisplayName("before ':'")
            fun beforeColon() {
                val file = parse<XQueryModule>("lorem :*")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a wildcard."
                        INFORMATION (7:8) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XQueryModule>("lorem: *")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (6:7) "XPST0003: Whitespace is not allowed after ':' in a wildcard."
                        INFORMATION (7:8) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XQueryModule>("lorem : *")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a wildcard."
                        ERROR (7:8) "XPST0003: Whitespace is not allowed after ':' in a wildcard."
                        INFORMATION (8:9) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }
        }

        @Test
        @DisplayName("URIQualifiedName wildcard")
        fun uriQualifiedName() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:32) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (235) NCName")
    internal inner class NCName {
        @Test
        @DisplayName("identifier")
        fun testNCName() {
            val file = parse<XQueryModule>("lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:11) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("keyword")
        fun testNCName_Keyword() {
            val file = parse<XQueryModule>("cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("cast")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (234) QName")
    internal inner class QName {
        @Test
        @DisplayName("prefix: identifier; local name: identifier")
        fun testQName() {
            val file = parse<XQueryModule>("lorem:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (6:11) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: keyword")
        fun testQName_KeywordPrefixPart() {
            val file = parse<XQueryModule>("cast:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (5:10) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: missing")
        fun testQName_MissingPrefixPart() {
            val file = parse<XQueryModule>(":ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testQName_KeywordLocalPart() {
            val file = parse<XQueryModule>("lorem:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (6:10) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: missing")
        fun testQName_MissingLocalPart() {
            val file = parse<XQueryModule>("lorem:")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Nested
        @DisplayName("whitespace in QName")
        internal inner class WhitespaceInQName {
            @Test
            @DisplayName("before ':'")
            fun beforeColon() {
                val file = parse<XQueryModule>("lorem :ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a qualified name."
                        INFORMATION (7:12) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XQueryModule>("lorem: ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (6:7) "XPST0003: Whitespace is not allowed after ':' in a qualified name."
                        INFORMATION (7:12) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XQueryModule>("lorem : ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XQUERY_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a qualified name."
                        ERROR (7:8) "XPST0003: Whitespace is not allowed after ':' in a qualified name."
                        INFORMATION (8:13) ERASED/DEFAULT + XQUERY_ELEMENT
                        """.trimIndent()
                    )
                )
            }
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("cast:as")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
    internal inner class URIQualifiedName {
        @Test
        @DisplayName("local name: identifier")
        fun testURIQualifiedName() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:42) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: keyword")
        fun testURIQualifiedName_Keyword() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:34) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }
    }

    @Nested
    @DisplayName("Usage Type: Annotation")
    internal inner class UsageType_Annotation {
        @Test
        @DisplayName("XQuery 3.1 EBNF (27) Annotation")
        fun annotation() {
            val file = parse<XQueryModule>(
                """
                declare %private function test() external; (: 'private' is an annotation keyword :)
                declare %test function test() external;
                declare %xs:string function test() external;
                """.trimIndent()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (26:30) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    INFORMATION (93:97) ERASED/DEFAULT + XQUERY_ANNOTATION
                    INFORMATION (107:111) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    INFORMATION (133:135) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (136:142) ERASED/DEFAULT + XQUERY_ANNOTATION
                    INFORMATION (152:156) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Attribute")
    internal inner class UsageType_Attribute {
        @Test
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XQueryModule>("attribute::test, attribute::ns:test, attribute::Q{}test, attribute::*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (28:30) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (31:35) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (51:55) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (68:69) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val file = parse<XQueryModule>("@test, @ns:test, @Q{}test, @*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (1:5) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (8:10) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (21:25) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (28:29) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        fun dirAttributeList() {
            val file = parse<XQueryModule>("""<a test="one" ns:test="two"/>""")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (14:16) ERASED/DEFAULT + XQUERY_XML_TAG + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        fun compAttrConstructor() {
            val file = parse<XQueryModule>("attribute test {}, attribute ns:test {}, attribute Q{}test {}")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (10:14) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (29:31) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (32:36) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (54:58) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (198) AttributeDeclaration")
        fun attributeDeclaration() {
            val file = parse<XQueryModule>(
                """
                |() instance of schema-attribute(test),
                |() instance of schema-attribute(ns:test),
                |() instance of schema-attribute(Q{}test)
                """.trimMargin()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (32:36) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (71:73) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (74:78) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (116:120) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (203) AttributeName")
        fun attributeName() {
            val file = parse<XQueryModule>(
                """
                |() instance of attribute(test),
                |() instance of attribute(ns:test),
                |() instance of attribute(Q{}test),
                |() instance of attribute(*)
                """.trimMargin()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (25:29) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (57:59) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (60:64) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    INFORMATION (95:99) ERASED/DEFAULT + XQUERY_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Decimal Format")
    internal inner class UsageType_DecimalFormat {
        @Test
        @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
        fun decimalFormatDecl() {
            val file = parse<XQueryModule>(
                """
                declare decimal-format test;
                declare decimal-format ns:test;
                """.trimIndent()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (23:27) ERASED/DEFAULT + XQUERY_DECIMAL_FORMAT
                    INFORMATION (52:54) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (55:59) ERASED/DEFAULT + XQUERY_DECIMAL_FORMAT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Element")
    internal inner class UsageType_Element {
        @Test
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XQueryModule>("child::test, child::ns:test, child::Q{}test, child::*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (7:11) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (20:22) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (23:27) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (39:43) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (52:53) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val file = parse<XQueryModule>("test, ns:test, Q{}test, *")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (6:8) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (9:13) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (18:22) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (24:25) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        fun dirElemConstructor() {
            val file = parse<XQueryModule>("""<test/>, <ns:test/>""")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (10:12) ERASED/DEFAULT + XQUERY_XML_TAG + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
        fun compElemConstructor() {
            val file = parse<XQueryModule>("element test {}, element ns:test {}, element Q{}test {}")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (8:12) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (25:27) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (28:32) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (48:52) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (202) ElementDeclaration")
        fun elementDeclaration() {
            val file = parse<XQueryModule>(
                """
                |() instance of schema-element(test),
                |() instance of schema-element(ns:test),
                |() instance of schema-element(Q{}test)
                """.trimMargin()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (30:34) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (67:69) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (70:74) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (110:114) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (204) ElementName")
        fun elementName() {
            val file = parse<XQueryModule>(
                """
                |() instance of element(test),
                |() instance of element(ns:test),
                |() instance of element(Q{}test),
                |() instance of element(*)
                """.trimMargin()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (23:27) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (53:55) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (56:60) ERASED/DEFAULT + XQUERY_ELEMENT
                    INFORMATION (89:93) ERASED/DEFAULT + XQUERY_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Function Call")
    internal inner class UsageType_FunctionCall {
        @Test
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        fun functionCall() {
            val file = parse<XQueryModule>("test(), ns:test(), Q{}test()")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (8:10) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (22:26) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
        fun namedFunctionRef() {
            val file = parse<XQueryModule>("test#0, ns:test#0, Q{}test#0")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (8:10) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (22:26) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        fun arrowFunctionSpecifier() {
            val file = parse<XQueryModule>("() => test(), () => ns:test(), () => Q{}test()")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (6:10) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (20:22) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (23:27) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    INFORMATION (40:44) ERASED/DEFAULT + XQUERY_FUNCTION_CALL
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Function Declaration")
    internal inner class UsageType_FunctionDecl {
        @Test
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
        fun functionDecl() {
            val file = parse<XQueryModule>(
                """
                declare function test() external;
                declare function ns:test() external;
                declare function Q{}test() external;
                """.trimMargin()
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (33:37) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    INFORMATION (83:85) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (86:90) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    INFORMATION (139:143) ERASED/DEFAULT + XQUERY_FUNCTION_DECL
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Namespace Prefix")
    internal inner class UsageType_NamespacePrefix {
        @Test
        @DisplayName("XQuery 3.1 EBNF (5) ModuleDecl")
        fun moduleDecl() {
            val file = parse<XQueryModule>("module namespace test = \"http://www.example.com\";")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (17:21) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        fun schemaImport() {
            val file = parse<XQueryModule>("import schema namespace test = 'http://www.example.com';")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (24:28) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        fun moduleImport() {
            val file = parse<XQueryModule>("import module namespace test = 'http://www.example.com';")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (24:28) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        fun namespaceDecl() {
            val file = parse<XQueryModule>("declare namespace test = 'http://www.example.com';")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (18:22) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XQueryModule>("namespace::test, namespace::ns:test, namespace::Q{}test, namespace::*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (28:30) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (31:35) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (51:55) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (68:69) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        fun dirAttributeList() {
            val file = parse<XQueryModule>("<a:b xmlns:a=\"http://www.example.com/a\"/>")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (1:2) ERASED/DEFAULT + XQUERY_XML_TAG + XQUERY_NS_PREFIX
                    INFORMATION (11:12) ERASED/DEFAULT + XQUERY_XML_TAG + XQUERY_NS_PREFIX
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Option")
    internal inner class UsageType_Option {
        @Test
        @DisplayName("XQuery 3.1 EBNF (37) OptionDecl")
        fun optionDecl() {
            val file = parse<XQueryModule>("declare option test \"lorem ipsum\";")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (15:19) ERASED/DEFAULT + XQUERY_OPTION
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Parameter")
    internal inner class UsageType_Parameter {
        @Test
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        fun param() {
            val file = parse<XQueryModule>("function (\$test) { \$test }")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_PARAMETER
                    INFORMATION (20:24) ERASED/DEFAULT + XQUERY_PARAMETER
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Pragma")
    internal inner class UsageType_Pragma {
        @Test
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        fun pragma() {
            val file = parse<XQueryModule>("(# test #) {}")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (3:7) ERASED/DEFAULT + XQUERY_PRAGMA
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Type")
    internal inner class UsageType_Type {
        @Test
        @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
        fun atomicOrUnionType() {
            val file = parse<XQueryModule>("() instance of test")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (15:19) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (205) SimpleTypeName")
        fun simpleTypeName() {
            val file = parse<XQueryModule>("() cast as test")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (11:15) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (206) TypeName")
        fun typeName() {
            val file = parse<XQueryModule>("() instance of element(*, test)")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (26:30) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
        fun typeDecl() {
            val file = parse<XQueryModule>("declare type test := xs:string;")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (13:17) ERASED/DEFAULT + XQUERY_TYPE
                    INFORMATION (21:23) ERASED/DEFAULT + XQUERY_NS_PREFIX
                    INFORMATION (24:30) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
        fun unionType() {
            val file = parse<XQueryModule>("() instance of union(test)")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (21:25) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
        fun typeAlias() {
            val file = parse<XQueryModule>("() instance of ~test")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (16:20) ERASED/DEFAULT + XQUERY_TYPE
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Variable")
    internal inner class UsageType_Variable {
        @Test
        @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
        fun currentItem() {
            val file = parse<XQueryModule>(
                "for sliding window \$x in () start \$test when () end when () return ()"
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (20:21) ERASED/DEFAULT + XQUERY_VARIABLE
                    INFORMATION (35:39) ERASED/DEFAULT + XQUERY_VARIABLE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
        fun previousItem() {
            val file = parse<XQueryModule>(
                "for sliding window \$x in () start previous \$test when () end when () return ()"
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (20:21) ERASED/DEFAULT + XQUERY_VARIABLE
                    INFORMATION (44:48) ERASED/DEFAULT + XQUERY_VARIABLE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (58) NextItem")
        fun nextItem() {
            val file = parse<XQueryModule>(
                "for sliding window \$x in () start next \$test when () end when () return ()"
            )[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (20:21) ERASED/DEFAULT + XQUERY_VARIABLE
                    INFORMATION (40:44) ERASED/DEFAULT + XQUERY_VARIABLE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (132) VarName")
        fun varName() {
            val file = parse<XQueryModule>("let \$test := 2 return \$test")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (5:9) ERASED/DEFAULT + XQUERY_VARIABLE
                    INFORMATION (23:27) ERASED/DEFAULT + XQUERY_VARIABLE
                    """.trimIndent()
                )
            )
        }
    }
}
