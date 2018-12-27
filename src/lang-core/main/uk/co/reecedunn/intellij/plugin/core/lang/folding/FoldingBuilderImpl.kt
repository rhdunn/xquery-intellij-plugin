/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.lang.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import java.util.ArrayList

open class FoldingBuilderImpl : FoldingBuilderEx() {
    private fun getRange(element: FoldablePsiElement): TextRange? {
        if (!element.textContains('\n')) {
            return null
        }
        return element.foldingRange
    }

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()
        root.walkTree().filterIsInstance<FoldablePsiElement>().forEach { child ->
            val range = getRange(child)
            if (range != null && range.length > 0) {
                descriptors.add(FoldingDescriptor(child, range))
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return (node.psi as? FoldablePsiElement)?.foldingPlaceholderText
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
