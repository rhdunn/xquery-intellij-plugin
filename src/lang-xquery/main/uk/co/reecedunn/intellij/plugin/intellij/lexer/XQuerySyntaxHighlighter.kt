/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.STATE_XQUERY_COMMENT
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.*

class XQuerySyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer.addState(
            XQDocLexer(), 0x70000000, STATE_XQUERY_COMMENT, XPathTokenType.COMMENT
        )
        return lexer
    }

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        val default =
            if (type is IKeywordOrNCNameType)
                XQuerySyntaxHighlighterKeys.KEYWORD_KEYS
            else
                SyntaxHighlighterBase.EMPTY
        return XQuerySyntaxHighlighterKeys.KEYS.getOrDefault(type, default)
    }
}
