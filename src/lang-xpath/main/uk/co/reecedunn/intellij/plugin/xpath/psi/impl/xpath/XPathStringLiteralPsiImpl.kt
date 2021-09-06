/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextEscaperImpl
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextHost
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathStringLiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), LiteralTextHost, XPathStringLiteral {
    companion object {
        private val DATA = Key.create<String>("DATA")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(DATA)
    }

    // endregion
    // region PsiLanguageInjectionHost

    private fun encoded(text: String, quote: Char): String {
        val out = StringBuilder()
        text.forEach { c ->
            when (c) {
                quote -> {
                    out.append(quote)
                    out.append(quote)
                }
                '&' -> out.append("&amp;")
                else -> out.append(c)
            }
        }
        return out.toString()
    }

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val quote = text[0]
        val updated = createElement<XPathStringLiteral>("$quote${encoded(text, quote)}$quote") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    // endregion
    // region LiteralTextHost

    private val isClosed
        get() = children().find { it.elementType == XPathTokenType.STRING_LITERAL_END } != null

    override val isOneLine: Boolean = false

    override val relevantTextRange: TextRange
        get() = when {
            isClosed -> TextRange(1, textLength - 1)
            else -> TextRange(1, textLength)
        }

    override val decoded: String
        get() = data

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement? = null

    // endregion
    // region XsStringValue

    override val data: String
        get() = computeUserDataIfAbsent(DATA) {
            val decoded = StringBuilder()
            children().filterIsInstance<PsiElementTextDecoder>().forEach { decoder -> decoder.decode(decoded) }
            decoded.toString()
        }

    // endregion
}

