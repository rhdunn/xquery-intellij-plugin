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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.highlighter.xml

import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.TextRange
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.parser.parse
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml.XQueryElemTagRangesProvider.getElementAtOffset
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml.XQueryElemTagRangesProvider.getElementTagRanges
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - XQuery Element Tag Ranges Provider")
class XQueryElemTagRangesProviderTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryElemTagRangesProviderTest")

    @Nested
    @DisplayName("getElementTagRanges")
    internal inner class GetElementTagRanges {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("open and close tag")
            fun openAndCloseTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:b>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 5) to TextRange(5, 11)))
            }

            @Test
            @DisplayName("self-closing")
            fun selfClosing() {
                val element = parse<XQueryDirElemConstructor>("<a:b/>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 6) to null))
            }

            @Test
            @DisplayName("missing closing tag")
            fun missingClosingTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 5) to null))
            }

            @Test
            @DisplayName("incomplete open tag")
            fun incompleteOpenTag() {
                val element = parse<XQueryDirElemConstructor>("<a:></a:b>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 4) to TextRange(4, 10)))
            }

            @Test
            @DisplayName("incomplete close tag")
            fun incompleteCloseTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 5) to TextRange(5, 10)))
            }

            @Test
            @DisplayName("with attributes")
            fun withAttributes() {
                val element = parse<XQueryDirElemConstructor>("<a:b test='abc'></a:b>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 4) to TextRange(16, 22)))
            }

            @Test
            @DisplayName("with space before")
            fun withSpaceBefore() {
                val element = parse<XQueryDirElemConstructor>("<  a:b></a:b>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 7) to TextRange(7, 13)))
            }

            @Test
            @DisplayName("with space after")
            fun withSpaceAfter() {
                val element = parse<XQueryDirElemConstructor>("<a:b  ></a:b  >")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 4) to TextRange(7, 12)))
            }

            @Test
            @DisplayName("close tag without NCName")
            fun closeTagWithoutNCName() {
                val element = parse<XQueryDirElemConstructor>("<a:b></>")[0]
                assertThat(getElementTagRanges(element), `is`(TextRange(0, 5) to null))
            }
        }
    }

    @Nested
    @DisplayName("getElementTagRanges")
    internal inner class GetElementWithinTagRanges {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("open and close tag")
            fun openAndCloseTag() {
                val elements = parse<XQueryDirElemConstructor>(" <a:b>cd</a:b> ")
                val provider = elements[0].containingFile.viewProvider

                assertThat(getElementAtOffset(provider, -1), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 0), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 1), `is`(sameInstance(elements[0]))) // '<'
                assertThat(getElementAtOffset(provider, 2), `is`(sameInstance(elements[0]))) // 'a'
                assertThat(getElementAtOffset(provider, 3), `is`(sameInstance(elements[0]))) // ':'
                assertThat(getElementAtOffset(provider, 4), `is`(sameInstance(elements[0]))) // 'b'
                assertThat(getElementAtOffset(provider, 5), `is`(sameInstance(elements[0]))) // '>'
                assertThat(getElementAtOffset(provider, 6), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 7), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 8), `is`(sameInstance(elements[0]))) // '<'
                assertThat(getElementAtOffset(provider, 9), `is`(sameInstance(elements[0]))) // '/'
                assertThat(getElementAtOffset(provider, 10), `is`(sameInstance(elements[0]))) // 'a'
                assertThat(getElementAtOffset(provider, 11), `is`(sameInstance(elements[0]))) // ':'
                assertThat(getElementAtOffset(provider, 12), `is`(sameInstance(elements[0]))) // 'b'
                assertThat(getElementAtOffset(provider, 13), `is`(sameInstance(elements[0]))) // '>'
                assertThat(getElementAtOffset(provider, 14), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 15), `is`(nullValue()))
            }

            @Test
            @DisplayName("self-closing tag")
            fun selfClosingTag() {
                val elements = parse<XQueryDirElemConstructor>(" <a:b/> ")
                val provider = elements[0].containingFile.viewProvider

                assertThat(getElementAtOffset(provider, -1), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 0), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 1), `is`(sameInstance(elements[0]))) // '<'
                assertThat(getElementAtOffset(provider, 2), `is`(sameInstance(elements[0]))) // 'a'
                assertThat(getElementAtOffset(provider, 3), `is`(sameInstance(elements[0]))) // ':'
                assertThat(getElementAtOffset(provider, 4), `is`(sameInstance(elements[0]))) // 'b'
                assertThat(getElementAtOffset(provider, 5), `is`(sameInstance(elements[0]))) // '/'
                assertThat(getElementAtOffset(provider, 6), `is`(sameInstance(elements[0]))) // '>'
                assertThat(getElementAtOffset(provider, 7), `is`(nullValue()))
                assertThat(getElementAtOffset(provider, 8), `is`(nullValue()))
            }

            @Test
            @DisplayName("with spaces")
            fun withSpaces() {
                val elements = parse<XQueryDirElemConstructor>("< a ></a >")
                val provider = elements[0].containingFile.viewProvider

                assertThat(getElementAtOffset(provider, -1), `is`(sameInstance(elements[0])))
                assertThat(getElementAtOffset(provider, 0), `is`(sameInstance(elements[0]))) // '<'
                assertThat(getElementAtOffset(provider, 1), `is`(nullValue())) // ' '
                assertThat(getElementAtOffset(provider, 2), `is`(sameInstance(elements[0]))) // 'a'
                assertThat(getElementAtOffset(provider, 3), `is`(nullValue())) // ' '
                assertThat(getElementAtOffset(provider, 4), `is`(sameInstance(elements[0]))) // '>'
                assertThat(getElementAtOffset(provider, 5), `is`(sameInstance(elements[0]))) // '<'
                assertThat(getElementAtOffset(provider, 6), `is`(sameInstance(elements[0]))) // '/'
                assertThat(getElementAtOffset(provider, 7), `is`(sameInstance(elements[0]))) // 'a'
                assertThat(getElementAtOffset(provider, 8), `is`(nullValue())) // ' '
                assertThat(getElementAtOffset(provider, 9), `is`(sameInstance(elements[0]))) // '>'
                assertThat(getElementAtOffset(provider, 10), `is`(nullValue()))
            }
        }
    }
}
