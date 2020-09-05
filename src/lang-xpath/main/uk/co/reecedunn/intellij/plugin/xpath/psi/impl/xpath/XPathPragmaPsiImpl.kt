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
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextEscaperImpl
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextHost
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPragma
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import java.lang.StringBuilder

class XPathPragmaPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathPragma, LiteralTextHost {
    // region PsiLanguageInjectionHost

    override fun isValidHost(): Boolean = children().any { it.elementType === XPathTokenType.PRAGMA_CONTENTS }

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val before = this.text.substring(0, relevantTextRange.startOffset + 1)
        val updated = createElement<XPathPragma>("$before$text#)") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return object : LiteralTextEscaperImpl<XPathPragmaPsiImpl>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.subSequence(text))
                decoded = Array(rangeInsideHost.length + 1) { it + rangeInsideHost.startOffset }
                return true
            }
        }
    }

    // endregion
    // region LiteralTextHost

    override val isOneLine: Boolean = false

    override val relevantTextRange: TextRange
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

    override val decoded: String get() = relevantTextRange.substring(text)

    // endregion
}
