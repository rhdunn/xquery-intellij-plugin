// Copyright (C) 2020-2021, 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.search.options.navigation

import com.intellij.codeInsight.navigation.impl.PsiTargetPresentationRenderer
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference.CustomConstraintFunctionReference
import javax.swing.Icon

class CustomConstraintListCellRenderer(private val facets: List<CustomConstraintFunctionReference>) :
    PsiTargetPresentationRenderer<PsiElement>() {

    override fun getContainerText(element: PsiElement): String = element.containingFile.resourcePath()

    override fun getElementText(element: PsiElement): String {
        val ref = facets.find { it.element === element } ?: return element.text
        return ref.referenceType
    }

    override fun getIcon(element: PsiElement): Icon? {
        val ref = facets.find { it.element === element }
        return CustomConstraintFunctionReference.getIcon(ref?.referenceType)
    }
}
