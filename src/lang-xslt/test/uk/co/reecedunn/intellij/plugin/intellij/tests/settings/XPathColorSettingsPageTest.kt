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
package uk.co.reecedunn.intellij.plugin.intellij.tests.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.StringUtil
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.settings.XPathColorSettingsPage
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath Color Settings Page")
class XPathColorSettingsPageTest {
    private val settings = XPathColorSettingsPage()

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        StringUtil.assertValidSeparators(settings.demoText)
    }

    private fun getTextAttributeKeysForTokens(text: String): List<TextAttributesKey> {
        val highlighter = settings.highlighter
        val lexer = highlighter.highlightingLexer
        lexer.start(text)

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

    @Test
    @DisplayName("demo text contains all syntax-based text attribute keys")
    fun syntaxHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForTokens(settings.demoText)
        assertThat(keys.contains(XPathSyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
        assertThat(keys.size, `is`(7)) // No other matching highlight colours
    }
}
