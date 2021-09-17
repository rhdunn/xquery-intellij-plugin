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
package uk.co.reecedunn.intellij.plugin.xpath.lang.editor.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginContextItemFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginLambdaFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.enclosedExpressionBlocks

class XPathFoldingBuilder : FoldingBuilderEx() {
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

    override fun getPlaceholderText(node: ASTNode): String? = when (node.elementType) {
        XPathElementType.COMMENT -> "(:...:)"
        XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR -> ".{...}"
        XPathElementType.LAMBDA_FUNCTION_EXPR -> "_{...}"
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
        else -> null
    }

    companion object {
        private val ENCLOSED_EXPR_CONTAINER = TokenSet.create(
            XPathElementType.ARROW_INLINE_FUNCTION_CALL,
            XPathElementType.CURLY_ARRAY_CONSTRUCTOR,
            XPathElementType.FT_EXTENSION_SELECTION,
            XPathElementType.FT_WEIGHT,
            XPathElementType.FT_WORDS_VALUE,
            XPathElementType.INLINE_FUNCTION_EXPR,
            XPathElementType.MAP_CONSTRUCTOR,
            XPathElementType.WITH_EXPR
        )
    }
}
