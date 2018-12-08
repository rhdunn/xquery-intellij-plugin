/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.XQueryQNamePrefixReference
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.XQueryFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.XQueryVariableNameReference

abstract class XQueryEQNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathEQName {
    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        val eqnameStart = node.startOffset
        val localName = (this as XsQNameValue).localName as? PsiElement
        val localNameRef: PsiReference? =
            if (localName != null) when {
                parent is XPathFunctionReference ->
                    XQueryFunctionNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                parent is XPathVariableName ->
                    XQueryVariableNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                else -> null
            } else {
                null
            }

        val prefix = (this as XsQNameValue).prefix as? PsiElement
        if (prefix == null || prefix is XPathBracedURILiteral) { // local name only
            if (localNameRef != null) {
                return arrayOf(localNameRef)
            }
            return PsiReference.EMPTY_ARRAY
        } else {
            if (localNameRef != null) {
                return arrayOf(XQueryQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)), localNameRef)
            }
            return arrayOf(XQueryQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)))
        }
    }
}
