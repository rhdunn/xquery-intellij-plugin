/*
 * Copyright (C) 2020-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.lexer

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xslt.lang.ValueTemplate
import xqt.platform.xml.lexer.LeftCurlyBracket
import xqt.platform.xml.lexer.RightCurlyBracket
import xqt.platform.xml.model.XmlCharReader

class XsltValueTemplateLexer : XPathLexer() {
    companion object {
        private const val STATE_VALUE_TEMPLATE_EXPRESSION = 32
    }

    private fun stateDefault(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LeftCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == LeftCurlyBracket) {
                characters.advance()
                ValueTemplate.ESCAPED_CHARACTER
            } else {
                pushState(STATE_VALUE_TEMPLATE_EXPRESSION)
                XPathTokenType.BLOCK_OPEN
            }
        }

        RightCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == RightCurlyBracket) {
                characters.advance()
                ValueTemplate.ESCAPED_CHARACTER
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
        }

        else -> run {
            while (true) {
                when (characters.currentChar) {
                    XmlCharReader.EndOfBuffer, LeftCurlyBracket, RightCurlyBracket -> {
                        return ValueTemplate.VALUE_CONTENTS
                    }

                    else -> characters.advance()
                }
            }
            @Suppress("UNREACHABLE_CODE") null
        }
    }

    override fun advance(state: Int): IElementType? = when (state) {
        STATE_DEFAULT -> stateDefault()
        STATE_VALUE_TEMPLATE_EXPRESSION -> stateDefault(state)
        else -> super.advance(state)
    }
}
