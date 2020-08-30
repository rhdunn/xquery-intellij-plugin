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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiLanguageInjectionHost
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Language Injection Host - XPath")
private class LanguageInjectionPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("relevant text range")
    internal inner class RelevantTextRange {
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
    @DisplayName("decode")
    internal inner class Decode {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteral() {
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
                assertThat(escaper.getOffsetInHost(4, range), `is`(-1))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val host = parse<XPathStringLiteral>("'a''\"\"b'")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

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
                assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val host = parse<XPathStringLiteral>("\"a''\"\"b\"")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

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
                assertThat(escaper.getOffsetInHost(5, range), `is`(-1))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: BMP, UTF-16 surrogate pair, multi-character entity
                val host = parse<XPathStringLiteral>("\"a&lt;&;&Afr;&gt&NotLessLess;b\"")[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

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
                assertThat(escaper.getOffsetInHost(7, range), `is`(-1))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val host = parse<XPathStringLiteral>(
                    "\"a&#xA0;&;&#160;&#x&#x1D520;&#x;b\""
                )[0] as PsiLanguageInjectionHost
                val escaper = host.createLiteralTextEscaper()

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
                assertThat(escaper.getOffsetInHost(6, range), `is`(-1))
            }
        }
    }
}
