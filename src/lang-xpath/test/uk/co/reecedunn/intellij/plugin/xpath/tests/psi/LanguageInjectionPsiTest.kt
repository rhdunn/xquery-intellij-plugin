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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiLanguageInjectionHost
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Language Injection Host - XPath")
private class LanguageInjectionPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("relevant text range")
    internal inner class RelevantTextRange {
        @Nested
        @DisplayName("XPath 3.1 EBNF (116) StringLiteral")
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
    @DisplayName("decode")
    internal inner class Decode {
        @Nested
        @DisplayName("XPath 3.1 EBNF (116) StringLiteral")
        internal inner class StringLiteral {
            @Nested
            @DisplayName("string literal content")
            internal inner class StringLiteral {
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
        }
    }
}
