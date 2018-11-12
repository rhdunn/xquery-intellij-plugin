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
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType

class XQDocParser(comment: CharSequence) {
    private val lexer = XQDocLexer()

    val isXQDoc: Boolean

    var elementType: IElementType? = null
        private set

    var text: CharSequence? = null
        private set

    var textRange: Range<Int>? = null
        private set

    fun next(): Boolean {
        if (lexer.tokenType == XQDocTokenType.TRIM)
            lexer.advance()

        when (lexer.tokenType) {
            XQDocTokenType.CONTENTS -> parseDescription()
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

    private fun parseDescription() {
        elementType = XQDocElementType.DESCRIPTION_LINE
        text = lexer.tokenText
        textRange = Range(lexer.tokenStart, lexer.tokenEnd)
        lexer.advance()
    }
}
