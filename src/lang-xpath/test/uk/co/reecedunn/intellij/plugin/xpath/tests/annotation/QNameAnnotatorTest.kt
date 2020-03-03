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
package uk.co.reecedunn.intellij.plugin.xpath.tests.annotation

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.prettyPrint
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath QNameAnnotator")
private class QNameAnnotatorTest : AnnotatorTestCase() {
    @Nested
    @DisplayName("XPath 3.1 EBNF (48) Wildcard")
    internal inner class Wildcard {
        @Test
        @DisplayName("any")
        fun any() {
            val file = parse<XPath>("*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:1) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: identifier")
        fun wildcard() {
            val file = parse<XPath>("lorem:*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (6:7) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: keyword")
        fun keywordPrefixPart() {
            val file = parse<XPath>("cast:*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (5:6) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: missing")
        fun missingPrefixPart() {
            val file = parse<XPath>(":*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("local name: keyword")
        fun keywordLocalPart() {
            val file = parse<XPath>("*:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (2:6) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: missing")
        fun missingLocalPart() {
            val file = parse<XPath>("*:")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:1) ERASED/DEFAULT + XPATH_ELEMENT
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
                val file = parse<XPath>("lorem :*")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a wildcard."
                        INFORMATION (7:8) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XPath>("lorem: *")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (6:7) "XPST0003: Whitespace is not allowed after ':' in a wildcard."
                        INFORMATION (7:8) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XPath>("lorem : *")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a wildcard."
                        ERROR (7:8) "XPST0003: Whitespace is not allowed after ':' in a wildcard."
                        INFORMATION (8:9) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }
        }

        @Test
        @DisplayName("URIQualifiedName wildcard")
        fun uriQualifiedName() {
            val file = parse<XPath>("Q{http://www.example.com/test#}*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:32) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (123) NCName")
    internal inner class NCName {
        @Test
        @DisplayName("identifier")
        fun testNCName() {
            val file = parse<XPath>("lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:11) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("keyword")
        fun testNCName_Keyword() {
            val file = parse<XPath>("cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    internal inner class QName {
        @Test
        @DisplayName("prefix: identifier; local name: identifier")
        fun testQName() {
            val file = parse<XPath>("lorem:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (6:11) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: keyword")
        fun testQName_KeywordPrefixPart() {
            val file = parse<XPath>("cast:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (5:10) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("prefix: missing")
        fun testQName_MissingPrefixPart() {
            val file = parse<XPath>(":ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testQName_KeywordLocalPart() {
            val file = parse<XPath>("lorem:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (6:10) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: missing")
        fun testQName_MissingLocalPart() {
            val file = parse<XPath>("lorem:")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
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
                val file = parse<XPath>("lorem :ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a qualified name."
                        INFORMATION (7:12) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XPath>("lorem: ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (6:7) "XPST0003: Whitespace is not allowed after ':' in a qualified name."
                        INFORMATION (7:12) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XPath>("lorem : ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        INFORMATION (0:5) ERASED/DEFAULT + XPATH_NS_PREFIX
                        ERROR (5:6) "XPST0003: Whitespace is not allowed before ':' in a qualified name."
                        ERROR (7:8) "XPST0003: Whitespace is not allowed after ':' in a qualified name."
                        INFORMATION (8:13) ERASED/DEFAULT + XPATH_ELEMENT
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
    internal inner class URIQualifiedName {
        @Test
        @DisplayName("local name: identifier")
        fun testURIQualifiedName() {
            val file = parse<XPath>("Q{http://www.example.com/test#}lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:42) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("local name: keyword")
        fun testURIQualifiedName_Keyword() {
            val file = parse<XPath>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (31:34) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Attribute")
    internal inner class UsageType_Attribute {
        @Test
        @DisplayName("XPath 3.1 EBNF (41) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XPath>("attribute::test, attribute::ns:test, attribute::Q{}test, attribute::*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (11:15) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (28:30) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (31:35) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (51:55) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (68:69) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (42) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val file = parse<XPath>("@test, @ns:test, @Q{}test, @*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (1:5) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (8:10) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (11:15) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (21:25) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (28:29) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (93) AttributeDeclaration")
        fun attributeDeclaration() {
            val file = parse<XPath>(
                """
                |() instance of schema-attribute(test),
                |() instance of schema-attribute(ns:test),
                |() instance of schema-attribute(Q{}test)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (32:36) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (71:73) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (74:78) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (116:120) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (98) AttributeName")
        fun attributeName() {
            val file = parse<XPath>(
                """
                |() instance of attribute(test),
                |() instance of attribute(ns:test),
                |() instance of attribute(Q{}test),
                |() instance of attribute(*)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (25:29) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (57:59) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (60:64) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    INFORMATION (95:99) ERASED/DEFAULT + XPATH_ATTRIBUTE
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("Usage Type: Element")
    internal inner class UsageType_Element {
        @Test
        @DisplayName("XPath 3.1 EBNF (41) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XPath>("child::test, child::ns:test, child::Q{}test, child::*")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (7:11) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (20:22) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (23:27) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (39:43) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (52:53) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (42) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val file = parse<XPath>("test, ns:test, Q{}test, *")[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (6:8) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (9:13) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (18:22) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (24:25) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (97) ElementDeclaration")
        fun elementDeclaration() {
            val file = parse<XPath>(
                """
                |() instance of schema-element(test),
                |() instance of schema-element(ns:test),
                |() instance of schema-element(Q{}test)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (30:34) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (67:69) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (70:74) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (110:114) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (99) ElementName")
        fun elementName() {
            val file = parse<XPath>(
                """
                |() instance of element(test),
                |() instance of element(ns:test),
                |() instance of element(Q{}test),
                |() instance of element(*)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator()).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (23:27) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (53:55) ERASED/DEFAULT + XPATH_NS_PREFIX
                    INFORMATION (56:60) ERASED/DEFAULT + XPATH_ELEMENT
                    INFORMATION (89:93) ERASED/DEFAULT + XPATH_ELEMENT
                    """.trimIndent()
                )
            )
        }
    }
}

