// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.highlighter.xml

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.TextRange
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml.XQueryElemTagRangesProvider.getElementAtOffset
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml.XQueryElemTagRangesProvider.getElementTagRanges
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - XQuery Element Tag Ranges Provider")
class XQueryElemTagRangesProviderTest : IdeaPlatformTestCase(), LanguageTestCase {
    override val pluginId: PluginId = PluginId.getId("XQueryElemTagRangesProviderTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

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
