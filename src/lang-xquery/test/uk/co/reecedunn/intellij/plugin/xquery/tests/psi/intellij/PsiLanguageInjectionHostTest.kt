// Copyright (C) 2020-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi.intellij

import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.DebugUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPragma
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ Program Structure Interface (PSI) - PsiLanguageInjectionHost - XQuery")
class PsiLanguageInjectionHostTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("PsiLanguageInjectionHostTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        registerPsiModification()
    }

    @Nested
    @DisplayName("relevant text range")
    internal inner class RelevantTextRange {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("pragma without contents")
            fun pragmaWithoutContents() {
                val host = parse<XPathPragma>("  (# test #)")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(8, 8)))
            }

            @Test
            @DisplayName("unclosed pragma without contents")
            fun unclosedPragmaWithoutContents() {
                val host = parse<XPathPragma>("  (# test ")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(8, 8)))
            }

            @Test
            @DisplayName("pragma contents")
            fun pragmaContents() {
                val host = parse<XPathPragma>("  (# test Lorem ipsum. #)")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(8, 21)))
            }

            @Test
            @DisplayName("unclosed pragma contents")
            fun unclosedPragmaContents() {
                val host = parse<XPathPragma>("  (# test Lorem ipsum. #)")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(8, 21)))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValueContent() {
                val host = parse<XQueryDirAttributeValue>("<a test=\"Lorem ipsum.\"/>")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(1, 13)))
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValueContent() {
                val host = parse<XQueryDirAttributeValue>("<a test=\"Lorem ipsum.")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(1, 13)))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin XQuery EBNF (123) DirTextConstructor")
        internal inner class DirTextConstructor {
            @Test
            @DisplayName("element")
            fun element() {
                val host = parse<PluginDirTextConstructor>("<a>Lorem ipsum.</a>")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(0, 12)))
            }

            @Test
            @DisplayName("unclosed element")
            fun unclosedElement() {
                val host = parse<PluginDirTextConstructor>("<a>Lorem ipsum.")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(0, 12)))
            }

            @Test
            @DisplayName("enclosed expression")
            fun enclosedExpr() {
                val host = parse<PluginDirTextConstructor>("<a>Lorem{1}ipsum.</a>")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(0, 5)))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteral() {
                val host = parse<XPathStringLiteral>("\"Lorem ipsum.\"")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(1, 13)))
            }

            @Test
            @DisplayName("unclosed string literal content")
            fun unclosedStringLiteral() {
                val host = parse<XPathStringLiteral>("\"Lorem ipsum.")[0] as PsiLanguageInjectionHost
                assertThat(host.createLiteralTextEscaper().relevantTextRange, `is`(TextRange(1, 13)))
            }
        }
    }

    @Nested
    @DisplayName("is valid host")
    internal inner class IsValidHost {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("pragma without contents")
            fun pragmaWithoutContents() {
                val host = parse<XPathPragma>("(# test #)")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(false))
            }

            @Test
            @DisplayName("pragma contents")
            fun pragmaContents() {
                val host = parse<XPathPragma>("(# test Lorem ipsum. #)")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("unclosed pragma contents")
            fun unclosedPragmaContents() {
                val host = parse<XPathPragma>("(# test Lorem ipsum. #)")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("attribute content")
            fun attributeContent() {
                val host = parse<XQueryDirAttributeValue>("<a test=\"test\"/>")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val host = parse<XQueryDirAttributeValue>("<a test='a''\"\"b'/>")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val host = parse<XQueryDirAttributeValue>("<a test=\"a''\"\"b\"/>")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                val host = parse<XQueryDirAttributeValue>(
                    "<a test=\"a&lt;&;&Afr;&gt&NotLessLess;b\"/>"
                )[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val host = parse<XQueryDirAttributeValue>(
                    "<a test=\"a&#xA0;&;&#160;&#x&#x1D520;&#x;b\"/>"
                )[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("enclosed expression")
            fun enclosedExpr() {
                val host = parse<XQueryDirAttributeValue>("<a test=\"a{1}b\"/>")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin XQuery EBNF (123) DirTextConstructor")
        internal inner class DirTextConstructor {
            @Test
            @DisplayName("element content")
            fun elementContent() {
                val host = parse<PluginDirTextConstructor>("<a>Lorem ipsum.</a>")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteralContent() {
                val host = parse<XPathStringLiteral>("\"test\"")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val host = parse<XPathStringLiteral>("'a''\"\"b'")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val host = parse<XPathStringLiteral>("\"a''\"\"b\"")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                val host = parse<XPathStringLiteral>("\"a&lt;&;&Afr;&gt&NotLessLess;b\"")[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val host = parse<XPathStringLiteral>(
                    "\"a&#xA0;&;&#160;&#x&#x1D520;&#x;b\""
                )[0] as PsiLanguageInjectionHost
                assertThat(host.isValidHost, `is`(true))
            }
        }
    }

    @Nested
    @DisplayName("decode")
    internal inner class Decode {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("relevant text range")
            fun relevantTextRange() {
                val host = parse<XPathPragma>("(# pragma test#)")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                val range = escaper.relevantTextRange
                val builder = StringBuilder()
                assertThat(escaper.decode(range, builder), `is`(true))
                assertThat(builder.toString(), `is`("test"))

                assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                assertThat(escaper.getOffsetInHost(0, range), `is`(10)) // t
                assertThat(escaper.getOffsetInHost(1, range), `is`(11)) // e
                assertThat(escaper.getOffsetInHost(2, range), `is`(12)) // s
                assertThat(escaper.getOffsetInHost(3, range), `is`(13)) // t
                assertThat(escaper.getOffsetInHost(4, range), `is`(14)) // -- (end offset)
                assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
            }

            @Test
            @DisplayName("subrange")
            fun subrange() {
                val host = parse<XPathPragma>("(# pragma Lorem ipsum dolor.#)")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                val range = TextRange(16, 21)
                val builder = StringBuilder()
                assertThat(escaper.decode(range, builder), `is`(true))
                assertThat(builder.toString(), `is`("ipsum"))

                assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                assertThat(escaper.getOffsetInHost(0, range), `is`(16)) // i
                assertThat(escaper.getOffsetInHost(1, range), `is`(17)) // p
                assertThat(escaper.getOffsetInHost(2, range), `is`(18)) // s
                assertThat(escaper.getOffsetInHost(3, range), `is`(19)) // u
                assertThat(escaper.getOffsetInHost(4, range), `is`(20)) // m
                assertThat(escaper.getOffsetInHost(5, range), `is`(21)) // -- (end offset)
                assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Nested
            @DisplayName("attribute content")
            internal inner class AttributeContent {
                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val host = parse<XQueryDirAttributeValue>("<a test=\"test\"/>")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("test"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // t
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // e
                    assertThat(escaper.getOffsetInHost(2, range), `is`(3)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(4)) // t
                    assertThat(escaper.getOffsetInHost(4, range), `is`(5)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange")
                fun subrange() {
                    val host = parse<XPathStringLiteral>("\"Lorem ipsum dolor.\"")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = TextRange(7, 12)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("ipsum"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(7)) // i
                    assertThat(escaper.getOffsetInHost(1, range), `is`(8)) // p
                    assertThat(escaper.getOffsetInHost(2, range), `is`(9)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(10)) // u
                    assertThat(escaper.getOffsetInHost(4, range), `is`(11)) // m
                    assertThat(escaper.getOffsetInHost(5, range), `is`(12)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("EscapeApos tokens")
            internal inner class EscapeApos {
                val host = parse<XQueryDirAttributeValue>("<a test='a''\"\"b'/>")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a'\"\"b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(2, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(3, range), `is`(5)) // "
                    assertThat(escaper.getOffsetInHost(4, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(5, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(2, 4)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("'"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(1, range), `is`(4)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 3)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(3)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("EscapeQuot tokens")
            internal inner class EscapeQuot {
                val host = parse<XQueryDirAttributeValue>("<a test=\"a''\"\"b\"/>")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a''\"b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(2, range), `is`(3)) // '
                    assertThat(escaper.getOffsetInHost(3, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(4, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(5, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(4, 6)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("\""))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(1, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(5, 7)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(1, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("PredefinedEntityRef tokens")
            internal inner class PredefinedEntityRef {
                // entity reference types: BMP, UTF-16 surrogate pair, multi-character entity
                val host = parse<XQueryDirAttributeValue>(
                    "<a test=\"a&lt;&;&Afr;&gt&NotLessLess;b\"/>"
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<\uD835\uDD04≪\u0338b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(8)) // &Afr;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(8)) // &Afr;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(16)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(16)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(6, range), `is`(29)) // b
                    assertThat(escaper.getOffsetInHost(7, range), `is`(30)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(8, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(1, 6)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 5)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(5)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("CharRef tokens")
            internal inner class CharRef {
                val host = parse<XQueryDirAttributeValue>(
                    "<a test=\"a&#xA0;&;&#160;&#x&#x1D520;&#x;b\"/>"
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0\u00A0\uD835\uDD20b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(10)) // &#160;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(19)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(19)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(32)) // b
                    assertThat(escaper.getOffsetInHost(6, range), `is`(33)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(7, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(1, 8)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(8)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 7)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin XQuery EBNF (123) DirTextConstructor")
        internal inner class DirTextConstructor {
            @Nested
            @DisplayName("element content")
            internal inner class ElementContent {
                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val host = parse<PluginDirTextConstructor>("<a>test</a>")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("test"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // t
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // e
                    assertThat(escaper.getOffsetInHost(2, range), `is`(2)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(3)) // t
                    assertThat(escaper.getOffsetInHost(4, range), `is`(4)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange")
                fun subrange() {
                    val host = parse<PluginDirTextConstructor>(
                        "<a>Lorem ipsum dolor.</a>"
                    )[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = TextRange(6, 11)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("ipsum"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(6)) // i
                    assertThat(escaper.getOffsetInHost(1, range), `is`(7)) // p
                    assertThat(escaper.getOffsetInHost(2, range), `is`(8)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(9)) // u
                    assertThat(escaper.getOffsetInHost(4, range), `is`(10)) // m
                    assertThat(escaper.getOffsetInHost(5, range), `is`(11)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("escape '{' and '}'")
            internal inner class EscapeCharacters {
                val host = parse<PluginDirTextConstructor>("<a>a{{}}b</a>")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a{}b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // {
                    assertThat(escaper.getOffsetInHost(2, range), `is`(3)) // }
                    assertThat(escaper.getOffsetInHost(3, range), `is`(5)) // b
                    assertThat(escaper.getOffsetInHost(4, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(1, 3)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("{"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // '
                    assertThat(escaper.getOffsetInHost(1, range), `is`(3)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(0, 2)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("PredefinedEntityRef tokens")
            internal inner class PredefinedEntityRef {
                // entity reference types: BMP, UTF-16 surrogate pair, multi-character entity
                val host = parse<PluginDirTextConstructor>(
                    "<a>a&lt;&;&Afr;&gt&NotLessLess;b</a>"
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<\uD835\uDD04≪\u0338b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(7)) // &Afr;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(7)) // &Afr;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(15)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(15)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(6, range), `is`(28)) // b
                    assertThat(escaper.getOffsetInHost(7, range), `is`(29)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(8, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(0, 5)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(5)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(0, 4)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(4)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("CharRef tokens")
            internal inner class CharRef {
                val host = parse<PluginDirTextConstructor>(
                    "<a>a&#xA0;&;&#160;&#x&#x1D520;&#x;b</a>"
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0\u00A0\uD835\uDD20b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(9)) // &#160;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(18)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(18)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(31)) // b
                    assertThat(escaper.getOffsetInHost(6, range), `is`(32)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(7, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(0, 7)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(1)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(0, 6)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(0)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (107) CDataSection")
            internal inner class CDataSection {
                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val host = parse<PluginDirTextConstructor>("<a><![CDATA[test]]></a>")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("test"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(9)) // t
                    assertThat(escaper.getOffsetInHost(1, range), `is`(10)) // e
                    assertThat(escaper.getOffsetInHost(2, range), `is`(11)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(12)) // t
                    assertThat(escaper.getOffsetInHost(4, range), `is`(16)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange")
                fun subrange() {
                    val host = parse<PluginDirTextConstructor>(
                        "<a><![CDATA[Lorem ipsum dolor.]]></a>"
                    )[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = TextRange(15, 20)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("ipsum"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(15)) // i
                    assertThat(escaper.getOffsetInHost(1, range), `is`(16)) // p
                    assertThat(escaper.getOffsetInHost(2, range), `is`(17)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(18)) // u
                    assertThat(escaper.getOffsetInHost(4, range), `is`(19)) // m
                    assertThat(escaper.getOffsetInHost(5, range), `is`(20)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Nested
            @DisplayName("string literal content")
            internal inner class StringLiteralContent {
                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val host = parse<XPathStringLiteral>("\"test\"")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("test"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // t
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // e
                    assertThat(escaper.getOffsetInHost(2, range), `is`(3)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(4)) // t
                    assertThat(escaper.getOffsetInHost(4, range), `is`(5)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange")
                fun subrange() {
                    val host = parse<XPathStringLiteral>("\"Lorem ipsum dolor.\"")[0] as PsiLanguageInjectionHost
                    val escaper = host.createLiteralTextEscaper()

                    val range = TextRange(7, 12)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("ipsum"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(7)) // i
                    assertThat(escaper.getOffsetInHost(1, range), `is`(8)) // p
                    assertThat(escaper.getOffsetInHost(2, range), `is`(9)) // s
                    assertThat(escaper.getOffsetInHost(3, range), `is`(10)) // u
                    assertThat(escaper.getOffsetInHost(4, range), `is`(11)) // m
                    assertThat(escaper.getOffsetInHost(5, range), `is`(12)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("EscapeApos tokens")
            internal inner class EscapeApos {
                val host = parse<XPathStringLiteral>("'a''\"\"b'")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a'\"\"b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(2, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(3, range), `is`(5)) // "
                    assertThat(escaper.getOffsetInHost(4, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(5, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(2, 4)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("'"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(1, range), `is`(4)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 3)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(3)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("EscapeQuot tokens")
            internal inner class EscapeQuot {
                val host = parse<XPathStringLiteral>("\"a''\"\"b\"")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a''\"b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // '
                    assertThat(escaper.getOffsetInHost(2, range), `is`(3)) // '
                    assertThat(escaper.getOffsetInHost(3, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(4, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(5, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(4, 6)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("\""))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(4)) // "
                    assertThat(escaper.getOffsetInHost(1, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(5, 7)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(6)) // b
                    assertThat(escaper.getOffsetInHost(1, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("PredefinedEntityRef tokens")
            internal inner class PredefinedEntityRef {
                // entity reference types: BMP, UTF-16 surrogate pair, multi-character entity
                val host = parse<XPathStringLiteral>("\"a&lt;&;&Afr;&gt&NotLessLess;b\"")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<\uD835\uDD04≪\u0338b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(8)) // &Afr;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(8)) // &Afr;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(16)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(16)) // &NotLessLess;
                    assertThat(escaper.getOffsetInHost(6, range), `is`(29)) // b
                    assertThat(escaper.getOffsetInHost(7, range), `is`(30)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(8, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(1, 6)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a<"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &lt;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(6)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 5)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(5)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }

            @Nested
            @DisplayName("CharRef tokens")
            internal inner class CharRef {
                val host = parse<XPathStringLiteral>(
                    "\"a&#xA0;&;&#160;&#x&#x1D520;&#x;b\""
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

                @Test
                @DisplayName("relevant text range")
                fun relevantTextRange() {
                    val range = escaper.relevantTextRange
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0\u00A0\uD835\uDD20b"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(10)) // &#160;
                    assertThat(escaper.getOffsetInHost(3, range), `is`(19)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(4, range), `is`(19)) // &#x1D520;
                    assertThat(escaper.getOffsetInHost(5, range), `is`(32)) // b
                    assertThat(escaper.getOffsetInHost(6, range), `is`(33)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(7, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange inside")
                fun subrangeInside() {
                    val range = TextRange(1, 8)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a\u00A0"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(2)) // &#xA0;
                    assertThat(escaper.getOffsetInHost(2, range), `is`(8)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(3, range), `is`(-1))
                }

                @Test
                @DisplayName("subrange incomplete")
                fun subrangeIncomplete() {
                    val range = TextRange(1, 7)
                    val builder = StringBuilder()
                    assertThat(escaper.decode(range, builder), `is`(true))
                    assertThat(builder.toString(), `is`("a"))

                    assertThat(escaper.getOffsetInHost(-1, range), `is`(-1))
                    assertThat(escaper.getOffsetInHost(0, range), `is`(1)) // a
                    assertThat(escaper.getOffsetInHost(1, range), `is`(7)) // -- (end offset)
                    assertThat(escaper.getOffsetInHost(2, range), `is`(-1))
                }
            }
        }
    }

    @Nested
    @DisplayName("update text")
    internal inner class UpdateText {
        @Test
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        fun pragma() {
            val host = parse<XPathPragma>("2 contains text (# pragma test#)")[0] as PsiLanguageInjectionHost
            val file = host.containingFile

            DebugUtil.performPsiModification<Throwable>("update text") {
                val updated = host.updateText("lorem ipsum")
                assertThat(updated.text, `is`("(# pragma lorem ipsum#)"))
            }

            assertThat(file.text, `is`("2 contains text (# pragma lorem ipsum#)"))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("string literal content")
            fun stringLiteralContent() {
                val host = parse<XQueryDirAttributeValue>("<a b=\"test\"/>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("lorem ipsum")
                    assertThat(updated.text, `is`("\"lorem ipsum\""))
                }

                assertThat(file.text, `is`("<a b=\"lorem ipsum\"/>"))
            }

            @Test
            @DisplayName("apos string escaping")
            fun aposStringEscaping() {
                val host = parse<XQueryDirAttributeValue>("<a b='test'/>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("a'b\"c&d <e> {f} g")
                    assertThat(updated.text, `is`("'a''b\"c&amp;d &lt;e&gt; {{f}} g'"))
                }

                assertThat(file.text, `is`("<a b='a''b\"c&amp;d &lt;e&gt; {{f}} g'/>"))
            }

            @Test
            @DisplayName("apos string escaping")
            fun quotStringEscaping() {
                val host = parse<XQueryDirAttributeValue>("<a b=\"test\"/>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("a'b\"c&d <e> {f} g")
                    assertThat(updated.text, `is`("\"a'b\"\"c&amp;d &lt;e&gt; {{f}} g\""))
                }

                assertThat(file.text, `is`("<a b=\"a'b\"\"c&amp;d &lt;e&gt; {{f}} g\"/>"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin XQuery EBNF (123) DirTextConstructor")
        internal inner class DirTextConstructor {
            @Test
            @DisplayName("string literal content")
            fun stringLiteralContent() {
                val host = parse<PluginDirTextConstructor>("<a>test</a>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("lorem ipsum")
                    assertThat(updated.text, `is`("lorem ipsum"))
                }

                assertThat(file.text, `is`("<a>lorem ipsum</a>"))
            }

            @Test
            @DisplayName("string escaping")
            fun stringEscaping() {
                val host = parse<PluginDirTextConstructor>("<a>test</a>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("a'b\"c&d <e> {f} g")
                    assertThat(updated.text, `is`("a'b\"c&amp;d &lt;e&gt; {{f}} g"))
                }

                assertThat(file.text, `is`("<a>a'b\"c&amp;d &lt;e&gt; {{f}} g</a>"))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (107) CDataSection")
            fun cdataSection() {
                val host = parse<PluginDirTextConstructor>("<a><![CDATA[test]]></a>")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("lorem ipsum")
                    assertThat(updated.text, `is`("lorem ipsum"))
                }

                assertThat(file.text, `is`("<a>lorem ipsum</a>"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteralContent() {
                val host = parse<XPathStringLiteral>("\"test\"")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("lorem ipsum")
                    assertThat(updated.text, `is`("\"lorem ipsum\""))
                }

                assertThat(file.text, `is`("\"lorem ipsum\""))
            }

            @Test
            @DisplayName("apos string escaping")
            fun aposStringEscaping() {
                val host = parse<XPathStringLiteral>("'test'")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("a'b\"c&d <e> f")
                    assertThat(updated.text, `is`("'a''b\"c&amp;d <e> f'"))
                }

                assertThat(file.text, `is`("'a''b\"c&amp;d <e> f'"))
            }

            @Test
            @DisplayName("apos string escaping")
            fun quotStringEscaping() {
                val host = parse<XPathStringLiteral>("\"test\"")[0] as PsiLanguageInjectionHost
                val file = host.containingFile

                DebugUtil.performPsiModification<Throwable>("update text") {
                    val updated = host.updateText("a'b\"c&d <e> f")
                    assertThat(updated.text, `is`("\"a'b\"\"c&amp;d <e> f\""))
                }

                assertThat(file.text, `is`("\"a'b\"\"c&amp;d <e> f\""))
            }
        }
    }
}
