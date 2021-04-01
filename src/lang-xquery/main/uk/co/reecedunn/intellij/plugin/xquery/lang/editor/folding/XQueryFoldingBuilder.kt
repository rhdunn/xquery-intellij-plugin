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
package uk.co.reecedunn.intellij.plugin.xquery.lang.editor.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowInlineFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.blockFoldingRange
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirCommentConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryTryClause
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQDocCommentLineExtractor
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import java.util.ArrayList

class XQueryFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()
        root.walkTree().forEach { child ->
            val range = getSingleFoldingRange(child)
            if (range?.isEmpty == false && child.textContains('\n')) {
                descriptors.add(FoldingDescriptor(child, range))
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? = when (node.elementType) {
        XPathElementType.ARROW_INLINE_FUNCTION_CALL -> "{...}"
        XPathElementType.CURLY_ARRAY_CONSTRUCTOR -> "{...}"
        XPathElementType.INLINE_FUNCTION_EXPR -> "{...}"
        XPathElementType.MAP_CONSTRUCTOR -> "{...}"
        XPathElementType.WITH_EXPR -> "{...}"
        XQueryElementType.COMMENT -> getCommentPlaceholderText(node.text)
        XQueryElementType.DIR_COMMENT_CONSTRUCTOR -> getDirCommentConstructorPlaceholderTest(node.psi)
        XQueryElementType.DIR_ELEM_CONSTRUCTOR -> "..."
        XQueryElementType.ENCLOSED_EXPR -> "{...}"
        XQueryElementType.TRY_CLAUSE -> "{...}"
        XQueryElementType.FUNCTION_DECL -> "{...}"
        else -> null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    private fun getSingleFoldingRange(element: PsiElement): TextRange? = when (element) {
        is PluginArrowInlineFunctionCall -> element.blockFoldingRange
        is XPathComment -> element.textRange
        is XPathCurlyArrayConstructor -> element.blockFoldingRange
        is XPathEnclosedExpr -> element.textRange
        is XPathInlineFunctionExpr -> element.blockFoldingRange
        is XPathMapConstructor -> element.blockFoldingRange
        is XPathWithExpr -> element.blockFoldingRange
        is XQueryDirCommentConstructor -> element.textRange
        is XQueryDirElemConstructor -> getDirElemConstructorFoldingRange(element)
        is XQueryFunctionDecl -> element.blockFoldingRange
        is XQueryTryClause -> element.blockFoldingRange
        else -> null
    }

    private fun getCommentPlaceholderText(text: String): String? {
        val parser =
            if (text.endsWith(":)"))
                XQDocCommentLineExtractor(text.subSequence(2, text.length - 2))
            else
                XQDocCommentLineExtractor(text.subSequence(2, text.length))
        return if (parser.next()) {
            if (parser.isXQDoc)
                "(:~ ${parser.text} :)"
            else
                "(: ${parser.text} :)"
        } else
            "(:...:)"
    }

    private fun getDirCommentConstructorPlaceholderTest(element: PsiElement): String? {
        var length = element.textRange.length
        if (element.lastChild.elementType === XQueryTokenType.XML_COMMENT_END_TAG)
            length -= 3

        val firstLine = element.text.substring(4, length).split("\n").firstOrNull { line ->
            line.trim().isNotEmpty()
        }
        return firstLine?.let { "<!--$it-->" } ?: "<!--...-->"
    }

    private fun getDirElemConstructorFoldingRange(element: PsiElement): TextRange? {
        var start: PsiElement? = element.firstChild
        if (start!!.elementType === XQueryTokenType.OPEN_XML_TAG)
            start = start!!.nextSibling
        if (start!!.elementType === XQueryTokenType.XML_WHITE_SPACE)
            start = start!!.nextSibling
        if (
            start!!.elementType === XPathElementType.NCNAME ||
            start!!.elementType === XPathElementType.QNAME
        )
            start = start!!.nextSibling
        if (start?.elementType === XQueryTokenType.XML_WHITE_SPACE)
            start = start.nextSibling

        val dirAttributeList = parseDirAttributeList(start)
        val hasMultiLineAttributes = dirAttributeList.first
        if (!hasMultiLineAttributes) {
            start = dirAttributeList.second
        }

        val end = element.lastChild
        val endOffset =
            if (
                end.elementType === XQueryTokenType.CLOSE_XML_TAG ||
                end.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG
            ) {
                end.prevSibling.textRange.endOffset
            } else {
                end.textRange.startOffset
            }

        if (hasEnclosedExprOnlyContent(element) && !hasMultiLineAttributes || start == null) {
            return null
        }
        return TextRange(start.textRange.startOffset, endOffset)
    }

    companion object {
        private fun hasEnclosedExprOnlyContent(element: PsiElement): Boolean {
            var n = 0
            element.children().forEach { child ->
                n += when (child.elementType) {
                    in ELEMENT_CONSTRUCTOR_TOKENS -> 0
                    XQueryElementType.ENCLOSED_EXPR -> 1
                    else -> return false
                }
            }
            return n == 1
        }

        private fun parseDirAttributeList(first: PsiElement?): Pair<Boolean, PsiElement?> {
            var start = first
            var hasMultiLineAttributes = false
            while (start?.elementType === XQueryElementType.DIR_ATTRIBUTE) {
                if (start.textContains('\n')) {
                    hasMultiLineAttributes = true
                }
                start = start.nextSibling

                if (start?.elementType === XQueryTokenType.XML_WHITE_SPACE) {
                    if (start.textContains('\n')) {
                        hasMultiLineAttributes = true
                    }
                    if (start.nextSibling?.elementType in CLOSE_TAG) {
                        return hasMultiLineAttributes to start
                    }
                    start = start.nextSibling
                }
            }
            return hasMultiLineAttributes to start
        }

        private val CLOSE_TAG = TokenSet.create(XQueryTokenType.END_XML_TAG, XQueryTokenType.SELF_CLOSING_XML_TAG)

        private val ELEMENT_CONSTRUCTOR_TOKENS = TokenSet.create(
            XQueryTokenType.OPEN_XML_TAG,
            XQueryTokenType.XML_WHITE_SPACE,
            XPathElementType.NCNAME,
            XPathElementType.QNAME,
            XQueryElementType.DIR_ATTRIBUTE,
            XQueryTokenType.END_XML_TAG,
            XQueryTokenType.CLOSE_XML_TAG,
            XQueryTokenType.SELF_CLOSING_XML_TAG
        )
    }
}
