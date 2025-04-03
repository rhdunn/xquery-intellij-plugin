// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2020 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.ide.navigationToolbar

import com.intellij.compat.lang.LanguageStructureViewBuilder
import com.intellij.ide.navigationToolbar.AbstractNavBarModelExtension
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.ui.UISettings
import com.intellij.ide.util.treeView.smartTree.NodeProvider
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.Processor
import java.lang.ref.SoftReference

@Suppress("unused")
private fun <T> Array<T>.toListOrSelf(): List<T> = toList() // For IntelliJ 2020.1.

@Suppress("unused")
private fun <T> List<T>.toListOrSelf(): List<T> = this // For IntelliJ 2020.2+.

// The StructureAwareNavBarModelExtension class is copied and modified here to
// fix IDEA-232100. Specifically, this version replaces the language property
// with an acceptElement method.
abstract class StructureAwareNavBarModelExtension : AbstractNavBarModelExtension() {
    private var currentFile: SoftReference<PsiFile>? = null
    private var currentFileStructure: SoftReference<StructureViewModel>? = null
    private var currentFileModCount = -1L

    abstract fun acceptElement(psiElement: PsiElement): Boolean

    override fun getLeafElement(dataContext: DataContext): PsiElement? {
        if (UISettings.getInstance().showMembersInNavigationBar) {
            val psiFile = CommonDataKeys.PSI_FILE.getData(dataContext)
            val editor = CommonDataKeys.EDITOR.getData(dataContext)
            if (psiFile == null || editor == null) return null
            val psiElement = psiFile.findElementAt(editor.caretModel.offset) ?: return null
            if (acceptElement(psiElement)) {
                buildStructureViewModel(psiFile, editor)?.let { model ->
                    return (model.currentEditorElement as? PsiElement)?.originalElement
                }
            }
        }
        return null
    }

    override fun processChildren(
        `object`: Any,
        rootElement: Any?,
        processor: Processor<Any>
    ): Boolean {
        if (UISettings.getInstance().showMembersInNavigationBar) {
            (`object` as? PsiElement)?.let { psiElement ->
                if (acceptElement(psiElement)) {
                    buildStructureViewModel(psiElement.containingFile)?.let { model ->
                        return processStructureViewChildren(model.root, `object`, processor)
                    }
                }
            }
        }
        return super.processChildren(`object`, rootElement, processor)
    }

    override fun getParent(psiElement: PsiElement?): PsiElement? {
        if (psiElement != null && acceptElement(psiElement)) {
            val file = psiElement.containingFile ?: return null
            val model = buildStructureViewModel(file)
            if (model != null) {
                val parentInModel = findParentInModel(model.root, psiElement)
                if (acceptParentFromModel(parentInModel)) {
                    return parentInModel
                }
            }
        }
        return super.getParent(psiElement)
    }

    protected open fun acceptParentFromModel(psiElement: PsiElement?): Boolean = true

    private fun findParentInModel(root: StructureViewTreeElement, psiElement: PsiElement): PsiElement? {
        for (child in childrenFromNodeAndProviders(root)) {
            if ((child as StructureViewTreeElement).value == psiElement) {
                return root.value as? PsiElement
            }
            findParentInModel(child, psiElement)?.let { return it }
        }
        return null
    }

    private fun buildStructureViewModel(file: PsiFile, editor: Editor? = null): StructureViewModel? {
        if (currentFile?.get() == file && currentFileModCount == file.modificationStamp) {
            currentFileStructure?.get()?.let { return it }
        }

        val builder = LanguageStructureViewBuilder.getInstance().getStructureViewBuilder(file)
        val model = (builder as? TreeBasedStructureViewBuilder)?.createStructureViewModel(editor)
        if (model != null) {
            currentFile = SoftReference(file)
            currentFileStructure = SoftReference(model)
            currentFileModCount = file.modificationStamp
        }
        return model
    }

    private fun processStructureViewChildren(
        parent: StructureViewTreeElement,
        `object`: Any,
        processor: Processor<Any>
    ): Boolean {
        if (parent.value == `object`) {
            return childrenFromNodeAndProviders(parent)
                .filterIsInstance<StructureViewTreeElement>()
                .all { processor.process(it.value) }
        }

        return childrenFromNodeAndProviders(parent)
            .filterIsInstance<StructureViewTreeElement>()
            .all { processStructureViewChildren(it, `object`, processor) }
    }

    private fun childrenFromNodeAndProviders(parent: StructureViewTreeElement): List<TreeElement> {
        val children =
            if (parent is PsiTreeElementBase<*>)
                parent.childrenWithoutCustomRegions.toListOrSelf()
            else
                parent.children.toList()
        return children + applicableNodeProviders.flatMap { it.provideNodes(parent) }
    }

    override fun normalizeChildren(): Boolean = false

    protected open val applicableNodeProviders: List<NodeProvider<*>> = emptyList()
}
