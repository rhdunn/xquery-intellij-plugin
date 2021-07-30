/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lang.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathPairedBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> = BRACE_PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        private val BRACE_PAIRS = arrayOf(
            // { ... }
            BracePair(XPathTokenType.BLOCK_OPEN, XPathTokenType.BLOCK_CLOSE, true),
            // [ ... ]
            BracePair(XPathTokenType.SQUARE_OPEN, XPathTokenType.SQUARE_CLOSE, false),
            // ( ... )
            BracePair(XPathTokenType.PARENTHESIS_OPEN, XPathTokenType.PARENTHESIS_CLOSE, false),
            // (: ... :)
            BracePair(XPathTokenType.COMMENT_START_TAG, XPathTokenType.COMMENT_END_TAG, false),
            // (# ... #)
            BracePair(XPathTokenType.PRAGMA_BEGIN, XPathTokenType.PRAGMA_END, false),
            // .{ ... }
            BracePair(XPathTokenType.CONTEXT_FUNCTION, XPathTokenType.BLOCK_CLOSE, true),
            // _{ ... }
            BracePair(XPathTokenType.LAMBDA_FUNCTION, XPathTokenType.BLOCK_CLOSE, true)
        )
    }
}
