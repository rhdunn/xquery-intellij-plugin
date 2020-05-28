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
package uk.co.reecedunn.intellij.plugin.xquery.tests.intellij.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.StringUtil
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryColorSettingsPage
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery Color Settings Page")
class XQueryColorSettingsPageTest {
    private val settings = XQueryColorSettingsPage()

    private fun getTextAttributeKeysForTokens(text: String): List<TextAttributesKey> {
        var withoutHighlightElements = text
        settings.additionalHighlightingTagToDescriptorMap!!.forEach { (name, _) ->
            withoutHighlightElements = "<$name>([^<]*)</$name>".toRegex().replace(withoutHighlightElements) {
                it.groups[1]!!.value
            }
        }

        val highlighter = settings.highlighter
        val lexer = highlighter.highlightingLexer
        lexer.start(withoutHighlightElements)

        val keys = ArrayList<TextAttributesKey>()
        while (lexer.tokenType != null) {
            for (key in highlighter.getTokenHighlights(lexer.tokenType)) {
                if (!keys.contains(key)) {
                    keys.add(key)
                }
            }

            lexer.advance()
        }
        return keys
    }

    private fun getTextAttributeKeysForAdditionalDescriptors(text: String): List<Pair<String, TextAttributesKey>> {
        return settings.additionalHighlightingTagToDescriptorMap!!.asSequence().flatMap { (name, attributesKey) ->
            val matches = "<$name>([^<]*)</$name>".toRegex().findAll(text)
            assertThat("additional highlight '$name' XML annotation is present", matches.any(), `is`(true))
            matches.map { match ->
                match.groups[1]?.value!! to attributesKey
            }
        }.toList()
    }

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        StringUtil.assertValidSeparators(settings.demoText)
    }

    @Test
    @DisplayName("demo text contains all syntax-based text attribute keys")
    fun syntaxHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForTokens(settings.demoText)
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ANNOTATION), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ATTRIBUTE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_MARKUP), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE), `is`(true))
        assertThat(keys.size, `is`(19)) // No other matching highlight colours
    }

    @Test
    @DisplayName("demo text contains all semantic-based text attribute keys")
    fun semanticHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForAdditionalDescriptors(settings.demoText)
        assertThat(keys.size, `is`(19))
        assertThat(keys[0], `is`("two" to XQuerySyntaxHighlighterColors.ATTRIBUTE))
        assertThat(keys[1], `is`("value" to XQuerySyntaxHighlighterColors.ATTRIBUTE))
        assertThat(keys[2], `is`("fmt" to XQuerySyntaxHighlighterColors.DECIMAL_FORMAT))
        assertThat(keys[3], `is`("one" to XQuerySyntaxHighlighterColors.ELEMENT))
        assertThat(keys[4], `is`("data" to XQuerySyntaxHighlighterColors.ELEMENT))
        assertThat(keys[5], `is`("true" to XQuerySyntaxHighlighterColors.FUNCTION_CALL))
        assertThat(keys[6], `is`("update" to XQuerySyntaxHighlighterColors.FUNCTION_DECL))
        assertThat(keys[7], `is`("json" to XQuerySyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[8], `is`("zip" to XQuerySyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[9], `is`("xs" to XQuerySyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[10], `is`("three" to XQuerySyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[11], `is`("fn" to XQuerySyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[12], `is`("opt" to XQuerySyntaxHighlighterColors.OPTION))
        assertThat(keys[13], `is`("a" to XQuerySyntaxHighlighterColors.PARAMETER))
        assertThat(keys[14], `is`("a" to XQuerySyntaxHighlighterColors.PARAMETER))
        assertThat(keys[15], `is`("ext" to XQuerySyntaxHighlighterColors.PRAGMA))
        assertThat(keys[16], `is`("integer" to XQuerySyntaxHighlighterColors.TYPE))
        assertThat(keys[17], `is`("test" to XQuerySyntaxHighlighterColors.VARIABLE))
        assertThat(keys[18], `is`("items" to XQuerySyntaxHighlighterColors.VARIABLE))
    }

    @Test
    @DisplayName("demo text contains all keys from the attribute descriptors")
    fun allDescriptorsPresentInDemoText() {
        val tokens = getTextAttributeKeysForTokens(settings.demoText)
        val additional = getTextAttributeKeysForAdditionalDescriptors(settings.demoText).map { (_, key) -> key }
        val keys = tokens.union(additional)
        val descriptorKeys = settings.attributeDescriptors.map { it.key }

        keys.forEach { key ->
            assertThat("$key from demo text in attributeDescriptors", descriptorKeys.contains(key), `is`(true))
        }

        descriptorKeys.forEach { key ->
            assertThat("$key from attributeDescriptors in keys from demo text", keys.contains(key), `is`(true))
        }
    }
}
