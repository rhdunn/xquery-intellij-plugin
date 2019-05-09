/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.ide.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.AbstractPsiBasedNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.intellij.ide.structureView.XQueryStructureViewElement

class XQueryLeafNode(node: XQueryStructureViewElement, viewSettings: ViewSettings) :
    AbstractPsiBasedNode<XQueryStructureViewElement>(node.project, node, viewSettings) {

    override fun updateImpl(data: PresentationData) {
        val presentation = value.presentation!!
        data.presentableText = presentation.presentableText
        data.setIcon(presentation.getIcon(false))
    }

    override fun getChildrenImpl(): MutableCollection<AbstractTreeNode<Any>> {
        return mutableListOf()
    }

    override fun extractPsiFromValue(): PsiElement? = value
}
