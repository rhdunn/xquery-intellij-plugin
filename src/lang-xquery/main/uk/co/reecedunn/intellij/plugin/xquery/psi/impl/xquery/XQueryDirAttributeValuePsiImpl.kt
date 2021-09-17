/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.psi.enclosedExpressionBlocks
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDirAttributeValuePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDirAttributeValue,
    XpmSyntaxValidationElement {
    // region PsiLanguageInjectionHost

    private class LiteralTextEscaperImpl(host: XQueryDirAttributeValuePsiImpl) :
        LiteralTextEscaper<XQueryDirAttributeValuePsiImpl>(host) {

        private var decoded: Array<Int>? = null

        override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
            var currentOffset = 0
            val offsets = ArrayList<Int>()
            myHost.children().forEach { child ->
                if (child is PsiElementTextDecoder) {
                    child.decode(currentOffset, rangeInsideHost, outChars, offsets)
                }
                currentOffset += child.textLength
            }
            offsets.add(rangeInsideHost.endOffset)
            decoded = offsets.toTypedArray()
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

    private fun encoded(text: String, quote: Char): String {
        val out = StringBuilder()
        text.forEach { c ->
            when (c) {
                quote -> {
                    out.append(quote)
                    out.append(quote)
                }
                '{' -> out.append("{{")
                '}' -> out.append("}}")
                '&' -> out.append("&amp;")
                '<' -> out.append("&lt;")
                '>' -> out.append("&gt;")
                else -> out.append(c)
            }
        }
        return out.toString()
    }

    override fun isValidHost(): Boolean = children().none { it is XpmExpression }

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val quote = this.text[0]
        val updated = createElement<XQueryDirAttributeValue>(
            "<a updated=$quote${encoded(text, quote)}$quote/>"
        ) ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    private val isClosed
        get() = children().find { it.elementType == XQueryTokenType.XML_ATTRIBUTE_VALUE_END } != null

    // endregion
    // region XQueryDirAttributeValue

    override val relevantTextRange: TextRange
        get() = when {
            isClosed -> TextRange(1, textLength - 1)
            else -> TextRange(1, textLength)
        }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = when (val expr = enclosedExpressionBlocks.find { it.isEmpty }) {
            null -> firstChild
            else -> expr.open
        }

    // endregion
}
