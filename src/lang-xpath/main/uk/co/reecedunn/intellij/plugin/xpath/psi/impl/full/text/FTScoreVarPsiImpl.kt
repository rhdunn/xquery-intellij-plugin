/*
 * Copyright (C) 2017, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.full.text

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTScoreVar
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class FTScoreVarPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), FTScoreVar, XpmSyntaxValidationElement {
    // region PsiElement

    override fun getUseScope(): SearchScope = LocalSearchScope(parent.parent.parent)

    // endregion
    // region XpmVariableBinding

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
