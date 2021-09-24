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
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginContextItemFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginLambdaFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.enclosedExpressionBlocks
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirCommentConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringConstructorInterpolation
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQDocCommentLineExtractor
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class XQueryFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()
        root.walkTree().forEach { child ->
            val range = getSingleFoldingRange(child)
            if (range?.isEmpty == false && child.textContains('\n')) {
                descriptors.add(FoldingDescriptor(child, range))
            }
            getEnclosedExprContainer(child)?.enclosedExpressionBlocks?.forEach { block ->
                if (block.isMultiLine) {
                    descriptors.add(FoldingDescriptor(child, block.textRange))
                }
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? = null

    override fun getPlaceholderText(node: ASTNode, textRange: TextRange): String? = when (node.elementType) {
        XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR -> ".{...}"
        XPathElementType.LAMBDA_FUNCTION_EXPR -> "_{...}"
        XQueryElementType.COMMENT -> getCommentPlaceholderText(node.text)
        XQueryElementType.DIR_COMMENT_CONSTRUCTOR -> getDirCommentConstructorPlaceholderTest(node.psi)
        XQueryElementType.DIR_ELEM_CONSTRUCTOR -> {
            val child = node.findLeafElementAt(textRange.startOffset - node.startOffset)
            if (child?.elementType === XPathTokenType.BLOCK_OPEN)
                "{...}"
            else
                "..."
        }
        XQueryElementType.STRING_CONSTRUCTOR_INTERPOLATION -> "`{...}`"
        in ENCLOSED_EXPR_CONTAINER -> "{...}"
        else -> null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    private fun getEnclosedExprContainer(element: PsiElement): PsiElement? = when (element.elementType) {
        in ENCLOSED_EXPR_CONTAINER -> element
        else -> null
    }

    private fun getSingleFoldingRange(element: PsiElement): TextRange? = when (element) {
        is PluginContextItemFunctionExpr -> element.textRange
        is PluginLambdaFunctionExpr -> element.textRange
        is XPathComment -> element.textRange
        is XQueryDirCommentConstructor -> element.textRange
        is XQueryDirElemConstructor -> getDirElemConstructorFoldingRange(element)
        is XQueryStringConstructorInterpolation -> element.textRange
        else -> null
    }

    private fun getCommentPlaceholderText(text: String): String {
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

    private fun getDirCommentConstructorPlaceholderTest(element: PsiElement): String {
        var length = element.textRange.length
        if (element.lastChild.elementType === XQueryTokenType.XML_COMMENT_END_TAG)
            length -= 3

        val firstLine = element.text.substring(4, length).split("\n").firstOrNull { line ->
            line.trim().isNotEmpty()
        }
        return firstLine?.let { "<!--$it-->" } ?: "<!--...-->"
    }

    private fun getDirElemConstructorFoldingRange(element: XQueryDirElemConstructor): TextRange? {
        var start: PsiElement = element.dirAttributeListStartElement
        val dirAttributeList = parseDirAttributeList(start)
        val hasMultiLineAttributes = dirAttributeList.first
        if (!hasMultiLineAttributes) {
            start = dirAttributeList.second ?: return null
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

        return when {
            !hasMultiLineText(element) && !hasMultiLineAttributes -> null
            else -> TextRange(start.textRange.startOffset, endOffset)
        }
    }

    companion object {
        private fun hasMultiLineText(element: PsiElement): Boolean {
            return element.children().find { it is PluginDirTextConstructor && it.textContains('\n') } != null
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

        private val ENCLOSED_EXPR_CONTAINER = TokenSet.create(
            XPathElementType.ARROW_INLINE_FUNCTION_CALL,
            XPathElementType.CURLY_ARRAY_CONSTRUCTOR,
            XPathElementType.FT_EXTENSION_SELECTION,
            XPathElementType.FT_WEIGHT,
            XPathElementType.FT_WORDS_VALUE,
            XPathElementType.INLINE_FUNCTION_EXPR,
            XPathElementType.MAP_CONSTRUCTOR,
            XPathElementType.WITH_EXPR,
            XQueryElementType.BINARY_CONSTRUCTOR,
            XQueryElementType.BLOCK,
            XQueryElementType.BOOLEAN_CONSTRUCTOR,
            XQueryElementType.CATCH_CLAUSE,
            XQueryElementType.COMP_ATTR_CONSTRUCTOR,
            XQueryElementType.COMP_COMMENT_CONSTRUCTOR,
            XQueryElementType.COMP_DOC_CONSTRUCTOR,
            XQueryElementType.COMP_ELEM_CONSTRUCTOR,
            XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR,
            XQueryElementType.COMP_PI_CONSTRUCTOR,
            XQueryElementType.COMP_TEXT_CONSTRUCTOR,
            XQueryElementType.DIR_ATTRIBUTE_VALUE,
            XQueryElementType.DIR_ELEM_CONSTRUCTOR,
            XQueryElementType.EXTENSION_EXPR,
            XQueryElementType.FUNCTION_DECL,
            XQueryElementType.NULL_CONSTRUCTOR,
            XQueryElementType.NUMBER_CONSTRUCTOR,
            XQueryElementType.ORDERED_EXPR,
            XQueryElementType.TRANSFORM_WITH_EXPR,
            XQueryElementType.TRY_CATCH_EXPR,
            XQueryElementType.UNORDERED_EXPR,
            XQueryElementType.UPDATE_EXPR,
            XQueryElementType.VALIDATE_EXPR,
            XQueryElementType.WHILE_BODY
        )
    }
}
