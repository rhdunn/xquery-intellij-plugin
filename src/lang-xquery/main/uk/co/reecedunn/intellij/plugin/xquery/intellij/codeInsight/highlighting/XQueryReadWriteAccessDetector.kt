/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariable
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XQueryReadWriteAccessDetector : ReadWriteAccessDetector() {
    private fun isVariable(element: PsiElement): Boolean = when (element.parent) {
        is XpmVariable -> true
        else -> false
    }

    override fun isReadWriteAccessible(element: PsiElement): Boolean {
        if (element.containingFile !is XQueryModule) return false
        return element is XPathEQName && isVariable(element)
    }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean = true

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): Access {
        return getExpressionAccess(reference.element)
    }

    override fun getExpressionAccess(expression: PsiElement): Access {
        if (!isVariable(expression)) return Access.Read
        return when {
            expression.parent is XPathVarRef -> Access.Read
            expression.parent.parent is XPathVarRef -> Access.Read
            else -> Access.Write
        }
    }
}
