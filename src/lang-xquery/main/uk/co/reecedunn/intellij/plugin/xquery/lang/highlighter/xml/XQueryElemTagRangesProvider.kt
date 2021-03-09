/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml

import com.intellij.openapi.util.TextRange
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object XQueryElemTagRangesProvider {
    fun getElementTagRanges(element: XQueryDirElemConstructor): Pair<TextRange?, TextRange?> {
        val node = element as XdmElementNode
        val open = node.nodeName as? PsiElement
        val close = node.closingTag as? PsiElement
        return if (open === close)
            getTagRange(open) to null
        else
            getTagRange(open) to getTagRange(close)
    }

    private fun getTagRange(tag: PsiElement?): TextRange? {
        var start: PsiElement = tag?.prevSibling ?: return null
        if (start is PsiErrorElement) {
            start = start.prevSibling // Whitespace before NCName
        }

        var end: PsiElement = tag
        if (end.nextSibling.elementType in END_TAG_TOKENS) {
            end = end.nextSibling
        } else {
            end = end.lastChild
            if (end.elementType === XQueryTokenType.XML_WHITE_SPACE) {
                end = end.prevSibling
            }
        }

        return TextRange(start.textRange.startOffset, end.textRange.endOffset)
    }

    fun getElementAtOffset(viewProvider: FileViewProvider, offset: Int): XQueryDirElemConstructor? {
        return getElementAtOffset(viewProvider.findElementAt(offset))
    }

    fun getElementAtOffset(
        element: PsiElement?,
        skipWhitespace: Boolean = false
    ): XQueryDirElemConstructor? = when (element.elementType) {
        XQueryTokenType.OPEN_XML_TAG,
        XQueryTokenType.END_XML_TAG,
        XQueryTokenType.CLOSE_XML_TAG,
        XQueryTokenType.SELF_CLOSING_XML_TAG -> {
            element!!.ancestors().filterIsInstance<XQueryDirElemConstructor>().firstOrNull()
        }
        XQueryTokenType.XML_TAG_NCNAME, XQueryTokenType.XML_TAG_QNAME_SEPARATOR -> { // tag name
            getElementAtOffset(element!!.parent.prevSibling, true)
        }
        TokenType.ERROR_ELEMENT -> {
            if (skipWhitespace) getElementAtOffset(element?.prevSibling) else null
        }
        else -> null
    }

    private val END_TAG_TOKENS = TokenSet.create(XQueryTokenType.END_XML_TAG, XQueryTokenType.SELF_CLOSING_XML_TAG)
}
