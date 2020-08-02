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
package uk.co.reecedunn.intellij.plugin.xpm.psi.shadow

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.light.LightElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import javax.swing.Icon

open class XpmShadowPsiElement(val shadowed: PsiElement, language: Language) :
    LightElement(shadowed.manager, language) {
    // region LightElement

    override fun getParent(): PsiElement? {
        return shadowed.ancestors().map { XpmShadowPsiElementFactory.create(it) }.firstOrNull()
    }

    override fun getChildren(): Array<PsiElement> {
        return shadowed.children.mapNotNull { XpmShadowPsiElementFactory.create(it) }.toTypedArray()
    }

    override fun getContainingFile(): PsiFile? = shadowed.containingFile

    override fun getTextRange(): TextRange? = shadowed.textRange

    override fun getStartOffsetInParent(): Int = shadowed.startOffsetInParent

    override fun textToCharArray(): CharArray = shadowed.textToCharArray()

    override fun textMatches(text: CharSequence): Boolean = shadowed.textMatches(text)

    override fun textMatches(element: PsiElement): Boolean = shadowed.textMatches(element)

    override fun findElementAt(offset: Int): PsiElement? = shadowed.findElementAt(offset)

    override fun getTextOffset(): Int = shadowed.textOffset

    override fun getNode(): ASTNode? = shadowed.node

    override fun getText(): String? = shadowed.text

    override fun accept(visitor: PsiElementVisitor): Unit = shadowed.accept(visitor)

    override fun copy(): PsiElement? {
        return XpmShadowPsiElementFactory.create(shadowed.copy())
    }

    override fun getNavigationElement(): PsiElement = shadowed.navigationElement

    override fun getPrevSibling(): PsiElement? {
        return shadowed.siblings().reversed().mapNotNull { XpmShadowPsiElementFactory.create(it) }.firstOrNull()
    }

    override fun getNextSibling(): PsiElement? {
        return shadowed.siblings().mapNotNull { XpmShadowPsiElementFactory.create(it) }.firstOrNull()
    }

    override fun getIcon(flags: Int): Icon? = shadowed.getIcon(flags)

    override fun toString(): String = "${javaClass.simpleName}($elementType)"

    // endregion
}
