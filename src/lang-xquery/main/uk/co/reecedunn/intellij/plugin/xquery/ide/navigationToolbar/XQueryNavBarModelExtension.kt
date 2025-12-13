// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.ide.navigationToolbar

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

class XQueryNavBarModelExtension : StructureAwareNavBarModelExtension() {
    override val language: Language = XQuery

    override fun getPresentableText(`object`: Any?): String? = getPresentableText(`object`, false)

    override fun getPresentableText(`object`: Any?, forPopup: Boolean): String? {
        val element = (`object` as? PsiElement)?.takeIf { isAcceptableLanguage(it) } ?: return null
        return when (element) {
            is ItemPresentationEx -> {
                if (forPopup)
                    element.getPresentableText(ItemPresentationEx.Type.NavBarPopup)
                else
                    element.getPresentableText(ItemPresentationEx.Type.NavBar)
            }
            is ItemPresentation -> element.presentableText
            else -> null
        }
    }

    override fun getParent(psiElement: PsiElement?): PsiElement? = (psiElement?.containingFile as? XQueryModule)

    override fun isAcceptableLanguage(psiElement: PsiElement?): Boolean = language.isKindOf(psiElement?.language)
}
