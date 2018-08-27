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
package uk.co.reecedunn.intellij.plugin.xquery.tests.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xquery.settings.ColorSettingsPage
import java.util.*

class ColorSettingsPageTest {
    @Test
    fun testIcon() {
        val settings = ColorSettingsPage()
        assertThat(settings.icon, `is`(nullValue()))
    }

    @Test
    fun testHighlighter() {
        val settings = ColorSettingsPage()
        val highlighter = settings.highlighter
        assertThat(highlighter.javaClass.name, `is`(SyntaxHighlighter::class.java.name))
    }

    @Test
    fun testDemoText() {
        val settings = ColorSettingsPage()
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
        assertThat(keys.contains(SyntaxHighlighter.COMMENT), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.IDENTIFIER), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.KEYWORD), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.ANNOTATION), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.NUMBER), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.STRING), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.BAD_CHARACTER), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_TAG), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_TAG_NAME), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_ATTRIBUTE_NAME), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_ATTRIBUTE_VALUE), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XML_ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XQDOC_TAG), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XQDOC_TAG_VALUE), `is`(true))
        assertThat(keys.contains(SyntaxHighlighter.XQDOC_MARKUP), `is`(true))
    }

    @Test
    fun testAdditionalHighlightingTagToDescriptorMap() {
        val settings = ColorSettingsPage()
        assertThat(settings.additionalHighlightingTagToDescriptorMap, `is`(nullValue()))
    }

    @Test
    fun testAttributeDescriptors() {
        val settings = ColorSettingsPage()
        assertThat(settings.attributeDescriptors.size, `is`(18))
        assertThat(settings.attributeDescriptors[0].displayName, `is`("Invalid Character"))
        assertThat(settings.attributeDescriptors[0].key, `is`(SyntaxHighlighter.BAD_CHARACTER))
        assertThat(settings.attributeDescriptors[1].displayName, `is`("Comment"))
        assertThat(settings.attributeDescriptors[1].key, `is`(SyntaxHighlighter.COMMENT))
        assertThat(settings.attributeDescriptors[2].displayName, `is`("Entity Reference"))
        assertThat(settings.attributeDescriptors[2].key, `is`(SyntaxHighlighter.ENTITY_REFERENCE))
        assertThat(settings.attributeDescriptors[3].displayName, `is`("Escaped Character"))
        assertThat(settings.attributeDescriptors[3].key, `is`(SyntaxHighlighter.ESCAPED_CHARACTER))
        assertThat(settings.attributeDescriptors[4].displayName, `is`("Identifier"))
        assertThat(settings.attributeDescriptors[4].key, `is`(SyntaxHighlighter.IDENTIFIER))
        assertThat(settings.attributeDescriptors[5].displayName, `is`("Keyword"))
        assertThat(settings.attributeDescriptors[5].key, `is`(SyntaxHighlighter.KEYWORD))
        assertThat(settings.attributeDescriptors[6].displayName, `is`("Annotation"))
        assertThat(settings.attributeDescriptors[6].key, `is`(SyntaxHighlighter.ANNOTATION))
        assertThat(settings.attributeDescriptors[7].displayName, `is`("Number"))
        assertThat(settings.attributeDescriptors[7].key, `is`(SyntaxHighlighter.NUMBER))
        assertThat(settings.attributeDescriptors[8].displayName, `is`("String"))
        assertThat(settings.attributeDescriptors[8].key, `is`(SyntaxHighlighter.STRING))
        assertThat(settings.attributeDescriptors[9].displayName, `is`("Direct XML Construction//Tag"))
        assertThat(settings.attributeDescriptors[9].key, `is`(SyntaxHighlighter.XML_TAG))
        assertThat(settings.attributeDescriptors[10].displayName, `is`("Direct XML Construction//Tag Name"))
        assertThat(settings.attributeDescriptors[10].key, `is`(SyntaxHighlighter.XML_TAG_NAME))
        assertThat(settings.attributeDescriptors[11].displayName, `is`("Direct XML Construction//Attribute Name"))
        assertThat(settings.attributeDescriptors[11].key, `is`(SyntaxHighlighter.XML_ATTRIBUTE_NAME))
        assertThat(settings.attributeDescriptors[12].displayName, `is`("Direct XML Construction//Attribute Value"))
        assertThat(settings.attributeDescriptors[12].key, `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))
        assertThat(settings.attributeDescriptors[13].displayName, `is`("Direct XML Construction//Entity Reference"))
        assertThat(settings.attributeDescriptors[13].key, `is`(SyntaxHighlighter.XML_ENTITY_REFERENCE))
        assertThat(settings.attributeDescriptors[14].displayName, `is`("Direct XML Construction//Escaped Character"))
        assertThat(settings.attributeDescriptors[14].key, `is`(SyntaxHighlighter.XML_ESCAPED_CHARACTER))
        assertThat(settings.attributeDescriptors[15].displayName, `is`("xqDoc//Tag"))
        assertThat(settings.attributeDescriptors[15].key, `is`(SyntaxHighlighter.XQDOC_TAG))
        assertThat(settings.attributeDescriptors[16].displayName, `is`("xqDoc//Tag Value"))
        assertThat(settings.attributeDescriptors[16].key, `is`(SyntaxHighlighter.XQDOC_TAG_VALUE))
        assertThat(settings.attributeDescriptors[17].displayName, `is`("xqDoc//Markup"))
        assertThat(settings.attributeDescriptors[17].key, `is`(SyntaxHighlighter.XQDOC_MARKUP))
    }

    @Test
    fun testColorDescriptors() {
        val settings = ColorSettingsPage()
        assertThat(settings.colorDescriptors.size, `is`(0))
    }

    @Test
    fun testDisplayName() {
        val settings = ColorSettingsPage()
        assertThat(settings.displayName, `is`("XQuery"))
    }
}
