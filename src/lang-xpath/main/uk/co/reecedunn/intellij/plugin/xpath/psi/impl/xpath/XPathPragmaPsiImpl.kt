/*
 * Copyright (C) 2016, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPragma
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathPragmaPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathPragma {
    // region PsiLanguageInjectionHost

    private class LiteralTextEscaperImpl(host: XPathPragmaPsiImpl) :
        LiteralTextEscaper<XPathPragmaPsiImpl>(host) {

        private var decoded: Array<Int>? = null

        override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
            outChars.append(rangeInsideHost.subSequence(myHost.text))
            decoded = Array(rangeInsideHost.length + 1) { it + rangeInsideHost.startOffset }
            return true
        }

        override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int = when {
            offsetInDecoded < 0 -> -1
            offsetInDecoded >= decoded!!.size -> -1
            else -> decoded!![offsetInDecoded]
        }

        override fun getRelevantTextRange(): TextRange = myHost.relevantTextRange

        override fun isOneLine(): Boolean = false
    }

    override fun isValidHost(): Boolean = children().any { it.elementType === XPathTokenType.PRAGMA_CONTENTS }

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val before = this.text.substring(0, relevantTextRange.startOffset)
        val updated = createElement<XPathPragma>("() contains text $before$text#)") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    private val relevantTextRange: TextRange
        get() {
            val end = lastChild
            val startOffset = when {
                end.elementType === XPathTokenType.PRAGMA_END -> when (end.prevSibling.elementType) {
                    XPathTokenType.PRAGMA_CONTENTS -> end.prevSibling.textOffset
                    else -> end.textOffset
                }
                else -> end.textOffset
            }
            return TextRange(startOffset, end.textOffset).shiftLeft(textOffset)
        }

    // endregion
}
