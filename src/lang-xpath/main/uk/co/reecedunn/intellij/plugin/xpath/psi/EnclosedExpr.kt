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
package uk.co.reecedunn.intellij.plugin.xpath.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

data class EnclosedExprBlock(val open: PsiElement, val close: PsiElement) {
    val textRange: TextRange
        get() = TextRange.create(open.textOffset, close.textRange.endOffset)

    val isMultiLine: Boolean
        get() = open.siblings(end = close).any { it.textContains('\n') }

    val isEmpty: Boolean
        get() {
            val e = open.siblings(end = close).firstOrNull { it.elementType !in IGNORE_TOKENS && it !is XPathComment }
            return e == null || e.elementType === XPathTokenType.BLOCK_CLOSE
        }
}

val PsiElement.blockOpen: PsiElement?
    get() = children().filterIsElementType(XPathTokenType.BLOCK_OPEN).firstOrNull()

val PsiElement.enclosedExpressionBlocks: List<EnclosedExprBlock>
    get() {
        val enclosedExpressions = mutableListOf<EnclosedExprBlock>()
        var blockOpen: PsiElement? = null
        children().forEach { child ->
            when (child.elementType) {
                XPathTokenType.BLOCK_OPEN -> {
                    blockOpen = child
                }
                XPathTokenType.BLOCK_CLOSE -> {
                    blockOpen?.let { enclosedExpressions.add(EnclosedExprBlock(it, child)) }
                    blockOpen = null
                }
            }
        }
        blockOpen?.let { enclosedExpressions.add(EnclosedExprBlock(it, lastChild)) }
        return enclosedExpressions
    }

val PsiElement.isEmptyEnclosedExpr: Boolean
    get() {
        val e = siblings().filter { it.elementType !in IGNORE_TOKENS && it !is XPathComment }.firstOrNull()
        return e == null || e.elementType === XPathTokenType.BLOCK_CLOSE
    }

private val IGNORE_TOKENS = TokenSet.create(
    XPathTokenType.WHITE_SPACE,
    XPathTokenType.BLOCK_OPEN
)
