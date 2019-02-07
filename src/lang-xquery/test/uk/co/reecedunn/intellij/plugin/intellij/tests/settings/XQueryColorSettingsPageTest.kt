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
package uk.co.reecedunn.intellij.plugin.intellij.tests.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.StringUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.StringUtils
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryColorSettingsPage
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery Color Settings Page")
class XQueryColorSettingsPageTest {
    @Test
    @DisplayName("icon")
    fun testIcon() {
        val settings = XQueryColorSettingsPage()
        assertThat(settings.icon, `is`(nullValue()))
    }

    @Test
    @DisplayName("highlighter")
    fun testHighlighter() {
        val settings = XQueryColorSettingsPage()
        val highlighter = settings.highlighter
        assertThat(highlighter.javaClass.name, `is`(XQuerySyntaxHighlighter::class.java.name))
    }

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        val settings = XQueryColorSettingsPage()
        StringUtil.assertValidSeparators(settings.demoText)
    }

    @Test
    @DisplayName("demo text contains all syntax highlighter highlight types")
    fun testDemoText() {
        val settings = XQueryColorSettingsPage()
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

        assertThat(keys.size, `is`(18))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ANNOTATION), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_MARKUP), `is`(true))
    }

    @Test
    @DisplayName("additional highlighting tag to descriptor map")
    fun testAdditionalHighlightingTagToDescriptorMap() {
        val settings = XQueryColorSettingsPage()
        assertThat(settings.additionalHighlightingTagToDescriptorMap, `is`(nullValue()))
    }

    @Test
    @DisplayName("attribute descriptors")
    fun testAttributeDescriptors() {
        val settings = XQueryColorSettingsPage()
        assertThat(settings.attributeDescriptors.size, `is`(18))
        assertThat(settings.attributeDescriptors[0].displayName, `is`("Invalid Character"))
        assertThat(settings.attributeDescriptors[0].key, `is`(XQuerySyntaxHighlighterColors.BAD_CHARACTER))
        assertThat(settings.attributeDescriptors[1].displayName, `is`("Comment"))
        assertThat(settings.attributeDescriptors[1].key, `is`(XQuerySyntaxHighlighterColors.COMMENT))
        assertThat(settings.attributeDescriptors[2].displayName, `is`("Entity Reference"))
        assertThat(settings.attributeDescriptors[2].key, `is`(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE))
        assertThat(settings.attributeDescriptors[3].displayName, `is`("Escaped Character"))
        assertThat(settings.attributeDescriptors[3].key, `is`(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER))
        assertThat(settings.attributeDescriptors[4].displayName, `is`("Identifier"))
        assertThat(settings.attributeDescriptors[4].key, `is`(XQuerySyntaxHighlighterColors.IDENTIFIER))
        assertThat(settings.attributeDescriptors[5].displayName, `is`("Keyword"))
        assertThat(settings.attributeDescriptors[5].key, `is`(XQuerySyntaxHighlighterColors.KEYWORD))
        assertThat(settings.attributeDescriptors[6].displayName, `is`("Annotation"))
        assertThat(settings.attributeDescriptors[6].key, `is`(XQuerySyntaxHighlighterColors.ANNOTATION))
        assertThat(settings.attributeDescriptors[7].displayName, `is`("Number"))
        assertThat(settings.attributeDescriptors[7].key, `is`(XQuerySyntaxHighlighterColors.NUMBER))
        assertThat(settings.attributeDescriptors[8].displayName, `is`("String"))
        assertThat(settings.attributeDescriptors[8].key, `is`(XQuerySyntaxHighlighterColors.STRING))
        assertThat(settings.attributeDescriptors[9].displayName, `is`("Direct XML Construction//Tag"))
        assertThat(settings.attributeDescriptors[9].key, `is`(XQuerySyntaxHighlighterColors.XML_TAG))
        assertThat(settings.attributeDescriptors[10].displayName, `is`("Direct XML Construction//Tag Name"))
        assertThat(settings.attributeDescriptors[10].key, `is`(XQuerySyntaxHighlighterColors.XML_TAG_NAME))
        assertThat(settings.attributeDescriptors[11].displayName, `is`("Direct XML Construction//Attribute Name"))
        assertThat(settings.attributeDescriptors[11].key, `is`(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME))
        assertThat(settings.attributeDescriptors[12].displayName, `is`("Direct XML Construction//Attribute Value"))
        assertThat(settings.attributeDescriptors[12].key, `is`(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE))
        assertThat(settings.attributeDescriptors[13].displayName, `is`("Direct XML Construction//Entity Reference"))
        assertThat(settings.attributeDescriptors[13].key, `is`(XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE))
        assertThat(settings.attributeDescriptors[14].displayName, `is`("Direct XML Construction//Escaped Character"))
        assertThat(settings.attributeDescriptors[14].key, `is`(XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER))
        assertThat(settings.attributeDescriptors[15].displayName, `is`("xqDoc//Tag"))
        assertThat(settings.attributeDescriptors[15].key, `is`(XQuerySyntaxHighlighterColors.XQDOC_TAG))
        assertThat(settings.attributeDescriptors[16].displayName, `is`("xqDoc//Tag Value"))
        assertThat(settings.attributeDescriptors[16].key, `is`(XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE))
        assertThat(settings.attributeDescriptors[17].displayName, `is`("xqDoc//Markup"))
        assertThat(settings.attributeDescriptors[17].key, `is`(XQuerySyntaxHighlighterColors.XQDOC_MARKUP))
    }

    @Test
    @DisplayName("color descriptors")
    fun testColorDescriptors() {
        val settings = XQueryColorSettingsPage()
        assertThat(settings.colorDescriptors.size, `is`(0))
    }

    @Test
    @DisplayName("display name")
    fun testDisplayName() {
        val settings = XQueryColorSettingsPage()
        assertThat(settings.displayName, `is`("XQuery"))
    }
}
