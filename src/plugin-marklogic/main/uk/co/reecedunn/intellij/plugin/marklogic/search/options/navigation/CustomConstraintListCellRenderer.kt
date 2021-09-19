/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.search.options.navigation

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference.CustomConstraintFunctionReference
import javax.swing.Icon

class CustomConstraintListCellRenderer(private val facets: List<CustomConstraintFunctionReference>) :
    PsiElementListCellRenderer<PsiElement>() {
    override fun getContainerText(element: PsiElement, name: String?): String = element.containingFile.resourcePath()

    override fun getElementText(element: PsiElement): String {
        val ref = facets.find { it.element === element } ?: return element.text
        return ref.referenceType
    }

    override fun getIcon(element: PsiElement?): Icon {
        val ref = facets.find { it.element === element }
        return CustomConstraintFunctionReference.getIcon(ref?.referenceType)
    }

    override fun getIconFlags(): Int = 0
}
