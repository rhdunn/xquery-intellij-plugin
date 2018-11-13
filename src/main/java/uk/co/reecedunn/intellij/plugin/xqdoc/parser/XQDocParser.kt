/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xqdoc.parser

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType

private val DESCRIPTION_LINE_TOKENS = TokenSet.create(
    XQDocTokenType.CONTENTS,
    XQDocTokenType.CHARACTER_REFERENCE,
    XQDocTokenType.PREDEFINED_ENTITY_REFERENCE,
    XQDocTokenType.PARTIAL_ENTITY_REFERENCE,
    XQDocTokenType.EMPTY_ENTITY_REFERENCE
)

class XQDocParser(private val comment: CharSequence) {
    private val lexer = XQDocLexer()
    private var startOfComment = true

    val isXQDoc: Boolean

    var elementType: IElementType? = null
        private set

    var text: CharSequence? = null
        private set

    var textRange: Range<Int>? = null
        private set

    fun next(): Boolean {
        do {
            if (lexer.tokenType == XQDocTokenType.TRIM)
                lexer.advance()
            if (lexer.tokenType == XQDocTokenType.WHITE_SPACE)
                lexer.advance()
        } while (lexer.tokenType == XQDocTokenType.TRIM && startOfComment)
        startOfComment = false

        when (lexer.tokenType) {
            XQDocTokenType.CONTENTS -> parseDescriptionLine()
            XQDocTokenType.TRIM -> parseEmptyDescriptionLine()
            else -> {
                elementType = null
                text = null
                textRange = null
                return false
            }
        }
        return true
    }

    init {
        lexer.start(comment)
        if (lexer.tokenType === XQDocTokenType.XQDOC_COMMENT_MARKER) {
            isXQDoc = true
            lexer.advance()
        } else {
            isXQDoc = false
        }
    }

    private fun parseEmptyDescriptionLine() {
        elementType = XQDocElementType.DESCRIPTION_LINE
        text = ""
        textRange = Range(lexer.tokenStart, lexer.tokenStart)
        lexer.advance()
    }

    private fun parseDescriptionLine() {
        val start = lexer.tokenStart
        var end = lexer.tokenEnd
        while (DESCRIPTION_LINE_TOKENS.contains(lexer.tokenType)) {
            end = lexer.tokenEnd
            lexer.advance()
        }

        elementType = XQDocElementType.DESCRIPTION_LINE
        text = comment.subSequence(start, end)
        textRange = Range(start, end)
    }
}
