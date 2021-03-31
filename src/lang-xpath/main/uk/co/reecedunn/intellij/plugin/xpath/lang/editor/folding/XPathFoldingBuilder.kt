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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowInlineFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWithExpr
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.blockFoldingRange
import java.util.ArrayList

class XPathFoldingBuilder : FoldingBuilderEx() {
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
        XPathElementType.COMMENT -> "(:...:)"
        XPathElementType.INLINE_FUNCTION_EXPR -> "{...}"
        XPathElementType.WITH_EXPR -> "{...}"
        else -> null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    private fun getSingleFoldingRange(element: PsiElement): TextRange? = when (element) {
        is PluginArrowInlineFunctionCall -> element.blockFoldingRange
        is XPathComment -> element.textRange
        is XPathInlineFunctionExpr -> element.blockFoldingRange
        is XPathWithExpr -> element.blockFoldingRange
        else -> null
    }
}
