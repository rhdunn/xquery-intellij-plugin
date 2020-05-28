/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.ide.navigationToolbar

import com.intellij.compat.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XQueryNavBarModelExtension : StructureAwareNavBarModelExtension() {
    override fun getPresentableText(`object`: Any?): String? = getPresentableText(`object`, false)

    override fun getPresentableText(`object`: Any?, forPopup: Boolean): String? {
        val element = (`object` as? PsiElement)?.takeIf { acceptElement(it) } ?: return null
        return when (element) {
            is ItemPresentationEx -> if (forPopup) element.structurePresentableText else element.presentableText
            is ItemPresentation -> element.presentableText
            else -> null
        }
    }

    override fun getParent(psiElement: PsiElement?): PsiElement? = (psiElement?.containingFile as? XQueryModule)

    override fun acceptElement(psiElement: PsiElement): Boolean = XQuery.isKindOf(psiElement.language)
}
