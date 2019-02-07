/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.StringUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery Color Settings Page")
class XPathColorSettingsPageTest {
    @Test
    @DisplayName("icon")
    fun testIcon() {
        val settings = XPathColorSettingsPage()
        assertThat(settings.icon, `is`(nullValue()))
    }

    @Test
    @DisplayName("highlighter")
    fun testHighlighter() {
        val settings = XPathColorSettingsPage()
        val highlighter = settings.highlighter
        assertThat(highlighter.javaClass.name, `is`(XPathSyntaxHighlighter::class.java.name))
    }

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        val settings = XPathColorSettingsPage()
        StringUtil.assertValidSeparators(settings.demoText)
    }

    @Test
    @DisplayName("demo text contains all syntax highlighter highlight types")
    fun testDemoText() {
        val settings = XPathColorSettingsPage()
        val highlighter = settings.highlighter
        val lexer = highlighter.highlightingLexer
        lexer.start(settings.demoText)

        val keys = ArrayList<TextAttributesKey>()
        while (lexer.tokenType != null) {
            for (key in highlighter.getTokenHighlights(lexer.tokenType)) {
                if (!keys.contains(key)) {
                    keys.add(key)
                }
            }

            lexer.advance()
        }

        assertThat(keys.size, `is`(7))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
    }

    @Test
    @DisplayName("additional highlighting tag to descriptor map")
    fun testAdditionalHighlightingTagToDescriptorMap() {
        val settings = XPathColorSettingsPage()
        assertThat(settings.additionalHighlightingTagToDescriptorMap?.size, `is`(1))
        assertThat(settings.additionalHighlightingTagToDescriptorMap?.get("nsprefix"), `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
    }

    @Test
    @DisplayName("attribute descriptors")
    fun testAttributeDescriptors() {
        val settings = XPathColorSettingsPage()
        assertThat(settings.attributeDescriptors.size, `is`(8))
        assertThat(settings.attributeDescriptors[0].displayName, `is`("Invalid Character"))
        assertThat(settings.attributeDescriptors[0].key, `is`(XPathSyntaxHighlighterColors.BAD_CHARACTER))
        assertThat(settings.attributeDescriptors[1].displayName, `is`("Comment"))
        assertThat(settings.attributeDescriptors[1].key, `is`(XPathSyntaxHighlighterColors.COMMENT))
        assertThat(settings.attributeDescriptors[2].displayName, `is`("Escaped Character"))
        assertThat(settings.attributeDescriptors[2].key, `is`(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER))
        assertThat(settings.attributeDescriptors[3].displayName, `is`("Identifier"))
        assertThat(settings.attributeDescriptors[3].key, `is`(XPathSyntaxHighlighterColors.IDENTIFIER))
        assertThat(settings.attributeDescriptors[4].displayName, `is`("Keyword"))
        assertThat(settings.attributeDescriptors[4].key, `is`(XPathSyntaxHighlighterColors.KEYWORD))
        assertThat(settings.attributeDescriptors[5].displayName, `is`("Namespace Prefix"))
        assertThat(settings.attributeDescriptors[5].key, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        assertThat(settings.attributeDescriptors[6].displayName, `is`("Number"))
        assertThat(settings.attributeDescriptors[6].key, `is`(XPathSyntaxHighlighterColors.NUMBER))
        assertThat(settings.attributeDescriptors[7].displayName, `is`("String"))
        assertThat(settings.attributeDescriptors[7].key, `is`(XPathSyntaxHighlighterColors.STRING))
    }

    @Test
    @DisplayName("color descriptors")
    fun testColorDescriptors() {
        val settings = XPathColorSettingsPage()
        assertThat(settings.colorDescriptors.size, `is`(0))
    }

    @Test
    @DisplayName("display name")
    fun testDisplayName() {
        val settings = XPathColorSettingsPage()
        assertThat(settings.displayName, `is`("XPath"))
    }
}
