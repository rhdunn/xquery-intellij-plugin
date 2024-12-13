/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocTokenType

@DisplayName("xqDoc - Lexer")
class XQDocLexerTest : LexerTestCase() {
    override val lexer: Lexer = XQDocLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, -1) }
            assertThat(e.message, `is`("Invalid state: -1"))
        }

        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() = tokenize("") {
        }
    }

    @Nested
    @DisplayName("xquery comment")
    internal inner class XQueryComment {
        @Test
        @DisplayName("single line")
        fun singleLine() = tokenize("Lorem ipsum dolor.") {
            state(11)
            token("Lorem ipsum dolor.", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() = tokenize("Lorem ipsum dolor\n : Alpha beta gamma\n : One two three") {
            state(11)
            token("Lorem ipsum dolor", XQDocTokenType.CONTENTS)
            state(12)
            token("\n :", XQDocTokenType.TRIM)
            token(" ", XQDocTokenType.WHITE_SPACE)
            state(11)
            token("Alpha beta gamma", XQDocTokenType.CONTENTS)
            state(12)
            token("\n :", XQDocTokenType.TRIM)
            token(" ", XQDocTokenType.WHITE_SPACE)
            state(11)
            token("One two three", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("tagged content after contents")
        fun taggedContentsAfterContents() = tokenize("Lorem\n@ipsum dolor.") {
            state(11)
            token("Lorem", XQDocTokenType.CONTENTS)
            state(12)
            token("\n", XQDocTokenType.TRIM)
            state(11)
            token("@ipsum dolor.", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("tagged content at the start of the comment")
        fun taggedContentsAtStart() = tokenize("@ipsum dolor.") {
            state(11)
            token("@ipsum dolor.", XQDocTokenType.CONTENTS)
        }
    }

    @Nested
    @DisplayName("xqdoc comment")
    internal inner class XQDocComment {
        @Test
        @DisplayName("Contents")
        fun contents() = tokenize("~Lorem ipsum dolor.") {
            token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
            state(1)
            token("Lorem ipsum dolor.", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("PredefinedEntityRef")
        fun predefinedEntityRef() {
            tokenize("~Lorem &amp; ipsum.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("Lorem ", XQDocTokenType.CONTENTS)
                token("&amp;", XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)
                token(" ipsum.", XQDocTokenType.CONTENTS)
            }

            tokenize("~&") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("&", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("~&abc") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("&abc", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("~&abc!") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("&abc", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                token("!", XQDocTokenType.CONTENTS)
            }

            tokenize("~&;") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("&;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
            }
        }

        @Nested
        @DisplayName("CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("decimal")
            fun decimal() {
                tokenize("~Lorem&#20;ipsum.") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("Lorem", XQDocTokenType.CONTENTS)
                    token("&#20;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("ipsum.", XQDocTokenType.CONTENTS)
                }

                tokenize("~Lorem&#9;ipsum.") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("Lorem", XQDocTokenType.CONTENTS)
                    token("&#9;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("ipsum.", XQDocTokenType.CONTENTS)
                }

                tokenize("~&#") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("~&#20") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#20", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("~&# ") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQDocTokenType.CONTENTS)
                }

                tokenize("~&#;") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("~One&#x20;&#xae;&#xDC;Two.") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("One", XQDocTokenType.CONTENTS)
                    token("&#x20;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("Two.", XQDocTokenType.CONTENTS)
                }

                tokenize("~&#x") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("~&#x20") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#x20", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("~&#x ") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQDocTokenType.CONTENTS)
                }

                tokenize("~&#x;&#x2G;&#x2g;&#xg2;") {
                    token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                    state(1)
                    token("&#x;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("G;", XQDocTokenType.CONTENTS)
                    token("&#x2", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g;", XQDocTokenType.CONTENTS)
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XQDocTokenType.CONTENTS)
                }
            }
        }
    }

    @Nested
    @DisplayName("DirAttributeList")
    internal inner class DirAttributeList {
        @Test
        @DisplayName("single quote")
        fun quot() {
            tokenize("~one <two three = \"four\" />") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("three", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("=", XQDocTokenType.XML_EQUAL)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("\"", XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
                state(6)
                token("four", XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("\"", XQDocTokenType.XML_ATTRIBUTE_VALUE_END)
                state(3)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("/>", XQDocTokenType.SELF_CLOSING_XML_TAG)
                state(1)
            }

            tokenize("~one <two three = \"four") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("three", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("=", XQDocTokenType.XML_EQUAL)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("\"", XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
                state(6)
                token("four", XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            }
        }

        @Test
        @DisplayName("double quote")
        fun apos() {
            tokenize("~one <two three = 'four' />") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("three", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("=", XQDocTokenType.XML_EQUAL)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("'", XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
                state(7)
                token("four", XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("'", XQDocTokenType.XML_ATTRIBUTE_VALUE_END)
                state(3)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("/>", XQDocTokenType.SELF_CLOSING_XML_TAG)
                state(1)
            }

            tokenize("~one <two three = 'four") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("three", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("=", XQDocTokenType.XML_EQUAL)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("'", XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
                state(7)
                token("four", XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            }
        }
    }

    @Nested
    @DisplayName("DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("element constructor")
        fun dirElemConstructor() {
            tokenize("~one <two >three</two > four") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("three", XQDocTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
                token(" four", XQDocTokenType.CONTENTS)
            }

            tokenize("~one <two >three") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("three", XQDocTokenType.XML_ELEMENT_CONTENTS)
            }

            tokenize("~one <two#>three</two#> four") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("one ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("two", XQDocTokenType.XML_TAG)
                token("#", XQDocTokenType.INVALID)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("three", XQDocTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("two", XQDocTokenType.XML_TAG)
                token("#", XQDocTokenType.INVALID)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
                token(" four", XQDocTokenType.CONTENTS)
            }
        }

        @Test
        @DisplayName("element constructor; self closing")
        fun selfClosing() {
            tokenize("~a <b /> c") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("b", XQDocTokenType.XML_TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("/>", XQDocTokenType.SELF_CLOSING_XML_TAG)
                state(1)
                token(" c", XQDocTokenType.CONTENTS)
            }

            tokenize("~a <b/") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("b", XQDocTokenType.XML_TAG)
                token("/", XQDocTokenType.INVALID)
            }

            tokenize("~a <b#/> c") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a ", XQDocTokenType.CONTENTS)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("b", XQDocTokenType.XML_TAG)
                token("#", XQDocTokenType.INVALID)
                token("/>", XQDocTokenType.SELF_CLOSING_XML_TAG)
                state(1)
                token(" c", XQDocTokenType.CONTENTS)
            }
        }

        @Test
        @DisplayName("element constructor; nested")
        fun nested() = tokenize("~a<b>c<d>e</d>f</b>g") {
            token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
            state(1)
            token("a", XQDocTokenType.CONTENTS)
            token("<", XQDocTokenType.OPEN_XML_TAG)
            state(3)
            token("b", XQDocTokenType.XML_TAG)
            token(">", XQDocTokenType.END_XML_TAG)
            state(4)
            token("c", XQDocTokenType.XML_ELEMENT_CONTENTS)
            token("<", XQDocTokenType.OPEN_XML_TAG)
            state(3)
            token("d", XQDocTokenType.XML_TAG)
            token(">", XQDocTokenType.END_XML_TAG)
            state(4)
            token("e", XQDocTokenType.XML_ELEMENT_CONTENTS)
            token("</", XQDocTokenType.CLOSE_XML_TAG)
            state(5)
            token("d", XQDocTokenType.XML_TAG)
            token(">", XQDocTokenType.END_XML_TAG)
            state(4)
            token("f", XQDocTokenType.XML_ELEMENT_CONTENTS)
            token("</", XQDocTokenType.CLOSE_XML_TAG)
            state(5)
            token("b", XQDocTokenType.XML_TAG)
            token(">", XQDocTokenType.END_XML_TAG)
            state(1)
            token("g", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("PredefinedEntityRef")
        fun predefinedEntityRef() {
            tokenize("~<p>Lorem &amp; ipsum.</p>") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("Lorem ", XQDocTokenType.XML_ELEMENT_CONTENTS)
                token("&amp;", XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)
                token(" ipsum.", XQDocTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
            }

            tokenize("~<p>&</p>") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("&", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
            }

            tokenize("~<p>&abc</p>") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("&abc", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
            }

            tokenize("~<p>&abc!</p>") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("&abc", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                token("!", XQDocTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
            }

            tokenize("~<p>&;</p>") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("<", XQDocTokenType.OPEN_XML_TAG)
                state(3)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(4)
                token("&;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
                token("</", XQDocTokenType.CLOSE_XML_TAG)
                state(5)
                token("p", XQDocTokenType.XML_TAG)
                token(">", XQDocTokenType.END_XML_TAG)
                state(1)
            }
        }

        @Nested
        @DisplayName("CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("decimal")
            fun decimal() {
                tokenize("Lorem&#20;ipsum.", 0, 16, 4) {
                    token("Lorem", XQDocTokenType.XML_ELEMENT_CONTENTS)
                    token("&#20;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("ipsum.", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }

                tokenize("Lorem&#9;ipsum.", 0, 15, 4) {
                    token("Lorem", XQDocTokenType.XML_ELEMENT_CONTENTS)
                    token("&#9;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("ipsum.", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }

                tokenize("&#", 0, 2, 4) {
                    token("&#", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("&#20", 0, 4, 4) {
                    token("&#20", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("&# ", 0, 3, 4) {
                    token("&#", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }

                tokenize("&#;", 0, 3, 4) {
                    token("&#;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("One&#x20;&#xae;&#xDC;Two.", 0, 25, 4) {
                    token("One", XQDocTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x20;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQDocTokenType.CHARACTER_REFERENCE)
                    token("Two.", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }

                tokenize("&#x", 0, 3, 4) {
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("&#x20", 0, 5, 4) {
                    token("&#x20", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("&#x ", 0, 4, 4) {
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }

                tokenize("&#x;&#x2G;&#x2g;&#xg2;", 0, 22, 4) {
                    token("&#x;", XQDocTokenType.EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("G;", XQDocTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x2", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g;", XQDocTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x", XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XQDocTokenType.XML_ELEMENT_CONTENTS)
                }
            }
        }
    }

    @Nested
    @DisplayName("TaggedContents")
    internal inner class TaggedContents {
        @Test
        @DisplayName("tagged content")
        fun taggedContents() {
            tokenize("~Lorem\n@ipsum dolor.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("Lorem", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("ipsum", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("dolor.", XQDocTokenType.CONTENTS)
            }

            tokenize("~Lorem\n@IPSUM dolor.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("Lorem", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("IPSUM", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("dolor.", XQDocTokenType.CONTENTS)
            }

            tokenize("~Lorem\n@12345 dolor.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("Lorem", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("12345", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("dolor.", XQDocTokenType.CONTENTS)
            }

            tokenize("~Lorem\n@# dolor.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("Lorem", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("# dolor.", XQDocTokenType.CONTENTS)
                state(1)
            }

            tokenize("~@lorem ipsum.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("lorem", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("ipsum.", XQDocTokenType.CONTENTS)
            }

            tokenize("~@ lorem ipsum.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("lorem ipsum.", XQDocTokenType.CONTENTS)
            }
        }

        @Test
        @DisplayName("at sign in contents")
        fun atSignInContents() = tokenize("~Lorem\n@ipsum ab@cd.") {
            token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
            state(1)
            token("Lorem", XQDocTokenType.CONTENTS)
            state(8)
            token("\n", XQDocTokenType.TRIM)
            token("@", XQDocTokenType.TAG_MARKER)
            state(2)
            token("ipsum", XQDocTokenType.TAG)
            token(" ", XQDocTokenType.WHITE_SPACE)
            state(1)
            token("ab@cd.", XQDocTokenType.CONTENTS)
        }
    }

    @Test
    @DisplayName("@author")
    fun author() = tokenize("~\n@author John Doe") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("author", XQDocTokenType.T_AUTHOR)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("John Doe", XQDocTokenType.CONTENTS)
    }

    @Test
    @DisplayName("@deprecated")
    fun deprecated() = tokenize("~\n@deprecated As of 1.1.") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("deprecated", XQDocTokenType.T_DEPRECATED)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("As of 1.1.", XQDocTokenType.CONTENTS)
    }

    @Test
    @DisplayName("@error")
    fun error() = tokenize("~\n@error The URI does not exist.") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("error", XQDocTokenType.T_ERROR)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("The URI does not exist.", XQDocTokenType.CONTENTS)
    }

    @Nested
    @DisplayName("@param")
    internal inner class AtParam {
        @Test
        @DisplayName("with VarRef")
        fun varRef() = tokenize("~\n@param \$arg An argument.") {
            token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
            state(8)
            token("\n", XQDocTokenType.TRIM)
            token("@", XQDocTokenType.TAG_MARKER)
            state(2)
            token("param", XQDocTokenType.T_PARAM)
            state(9)
            token(" ", XQDocTokenType.WHITE_SPACE)
            token("$", XQDocTokenType.VARIABLE_INDICATOR)
            state(10)
            token("arg", XQDocTokenType.NCNAME)
            token(" ", XQDocTokenType.WHITE_SPACE)
            state(1)
            token("An argument.", XQDocTokenType.CONTENTS)
        }

        @Test
        @DisplayName("missing VarRef")
        fun contentsOnly() = tokenize("~\n@param - \$arg An argument.") {
            token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
            state(8)
            token("\n", XQDocTokenType.TRIM)
            token("@", XQDocTokenType.TAG_MARKER)
            state(2)
            token("param", XQDocTokenType.T_PARAM)
            state(9)
            token(" ", XQDocTokenType.WHITE_SPACE)
            token("- \$arg An argument.", XQDocTokenType.CONTENTS)
            state(1)
        }
    }

    @Test
    @DisplayName("@return")
    fun `return`() = tokenize("~\n@return Some value.") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("return", XQDocTokenType.T_RETURN)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("Some value.", XQDocTokenType.CONTENTS)
    }

    @Test
    @DisplayName("@see")
    fun see() = tokenize("~\n@see http://www.example.com") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("see", XQDocTokenType.T_SEE)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("http://www.example.com", XQDocTokenType.CONTENTS)
    }

    @Test
    @DisplayName("@since")
    fun since() = tokenize("~\n@since 1.2") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("since", XQDocTokenType.T_SINCE)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("1.2", XQDocTokenType.CONTENTS)
    }

    @Test
    @DisplayName("@version")
    fun version() = tokenize("~\n@version 1.2") {
        token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(8)
        token("\n", XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(2)
        token("version", XQDocTokenType.T_VERSION)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(1)
        token("1.2", XQDocTokenType.CONTENTS)
    }

    @Nested
    @DisplayName("Trim")
    internal inner class Trim {
        @Test
        @DisplayName("line endings: linux")
        fun linux() {
            tokenize("~a\nb\nc") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~a\n \tb\n\t c") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\n \t", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\n\t ", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~\n\n") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\n", XQDocTokenType.TRIM)
                token("\n", XQDocTokenType.TRIM)
            }
        }

        @Test
        @DisplayName("line endings: mac")
        fun mac() {
            // The xqDoc grammar does not support Mac line endings ('\r'), but XQuery/XML
            // line ending normalisation rules do.

            tokenize("~a\rb\rc") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\r", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\r", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~a\r \tb\r\t c") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\r \t", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\r\t ", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~\r\r") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\r", XQDocTokenType.TRIM)
                token("\r", XQDocTokenType.TRIM)
            }
        }

        @Test
        @DisplayName("line endings: windows")
        fun windows() {
            // The xqDoc grammar does not support Windows line endings ('\r\n'), but XQuery/XML
            // line ending normalisation rules do.

            tokenize("~a\r\nb\r\nc") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\r\n", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\r\n", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~a\r\n \tb\r\n\t c") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(1)
                token("a", XQDocTokenType.CONTENTS)
                state(8)
                token("\r\n \t", XQDocTokenType.TRIM)
                state(1)
                token("b", XQDocTokenType.CONTENTS)
                state(8)
                token("\r\n\t ", XQDocTokenType.TRIM)
                state(1)
                token("c", XQDocTokenType.CONTENTS)
            }

            tokenize("~\r\n\r\n") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\r\n", XQDocTokenType.TRIM)
                token("\r\n", XQDocTokenType.TRIM)
            }
        }

        @Test
        @DisplayName("whitespace after trim")
        fun whitespaceAfterTrim() {
            // This is different to the xqDoc grammar, but is necessary to support treating
            // '@' characters within the line as part of the Contents token. The xqDoc
            // processor collates these in the parser phase, but the syntax highlighter
            // needs to highlight those as comment, not document tag, tokens.

            tokenize("~\n : \t@lorem ipsum.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\n :", XQDocTokenType.TRIM)
                token(" \t", XQDocTokenType.WHITE_SPACE)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("lorem", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("ipsum.", XQDocTokenType.CONTENTS)
            }

            tokenize("~\n :\t @lorem ipsum.") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\n :", XQDocTokenType.TRIM)
                token("\t ", XQDocTokenType.WHITE_SPACE)
                state(8)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("lorem", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("ipsum.", XQDocTokenType.CONTENTS)
            }

            tokenize("~\n : @lorem ipsum\n : @dolor sed emit") {
                token("~", XQDocTokenType.XQDOC_COMMENT_MARKER)
                state(8)
                token("\n :", XQDocTokenType.TRIM)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("lorem", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("ipsum", XQDocTokenType.CONTENTS)
                state(8)
                token("\n :", XQDocTokenType.TRIM)
                token(" ", XQDocTokenType.WHITE_SPACE)
                token("@", XQDocTokenType.TAG_MARKER)
                state(2)
                token("dolor", XQDocTokenType.TAG)
                token(" ", XQDocTokenType.WHITE_SPACE)
                state(1)
                token("sed emit", XQDocTokenType.CONTENTS)
            }
        }
    }
}
