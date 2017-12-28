/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import java.util.*

class XQueryFoldingBuilder : FoldingBuilderEx() {
    private fun getDirElemConstructorRange(element: PsiElement): TextRange? {
        var hasEnclosedExprOnlyContent = false
        var hasMultiLineAttributes = false

        val contents = element.node.findChildByType(XQueryElementType.DIR_ELEM_CONTENT)
        if (contents != null) {
            val first = contents.firstChildNode
            val last = contents.lastChildNode
            if (first === last && first.elementType === XQueryElementType.ENCLOSED_EXPR) {
                hasEnclosedExprOnlyContent = true
            }
        }

        var start: PsiElement? = element.firstChild
        if (start!!.node.elementType === XQueryTokenType.OPEN_XML_TAG)
            start = start!!.nextSibling
        if (start!!.node.elementType === XQueryTokenType.XML_WHITE_SPACE)
            start = start!!.nextSibling
        if (start!!.node.elementType === XQueryElementType.NCNAME || start!!.node.elementType === XQueryElementType.QNAME)
            start = start!!.nextSibling
        if (start!!.node.elementType === XQueryTokenType.XML_WHITE_SPACE)
            start = start!!.nextSibling
        if (start!!.node.elementType === XQueryElementType.DIR_ATTRIBUTE_LIST) {
            hasMultiLineAttributes = start!!.textContains('\n')
            if (!hasMultiLineAttributes) {
                start = start.nextSibling
            }
        }

        var end = element.lastChild
        if (end.node.elementType === XQueryTokenType.CLOSE_XML_TAG || end.node.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG) {
            end = end.prevSibling
        }

        if (hasEnclosedExprOnlyContent && !hasMultiLineAttributes || start == null) {
            return null
        }
        return TextRange(start.textRange.startOffset, end.textRange.startOffset)
    }

    private fun getRange(element: PsiElement): TextRange? {
        if (!element.textContains('\n')) {
            return null
        }

        when (element) {
            is XQueryEnclosedExpr, is XPathComment ->
                return element.textRange
            is XQueryDirElemConstructor ->
                return getDirElemConstructorRange(element)
            else ->
                return null
        }
    }

    private fun createFoldRegions(element: PsiElement, descriptors: MutableList<FoldingDescriptor>) {
        element.children().forEach { child ->
            val range = getRange(child)
            if (range != null && range.length > 0) {
                descriptors.add(FoldingDescriptor(child, range))
            }
            createFoldRegions(child, descriptors)
        }
    }

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()
        createFoldRegions(root, descriptors)
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        when (node.psi) {
            is XQueryEnclosedExpr ->
                return "{...}"
            is XPathComment ->
                return "(...)"
            else ->
                return "..."
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean =
        false
}
