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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryEQNamePrefixReference
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryVariableNameReference

abstract class XPathEQNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathEQName {
    override fun equals(other: Any?): Boolean {
        if (other !is XPathEQName) {
            return false
        }

        if (localName?.text?.equals(other.localName?.text) == true) {
            val lhsPrefix = ((this as XdmStaticValue).staticValue as? QName)?.prefix as? PsiElement
            val rhsPrefix = ((other as XdmStaticValue).staticValue as? QName)?.prefix as? PsiElement
            return lhsPrefix == null && rhsPrefix == null || lhsPrefix?.text?.equals(rhsPrefix?.text) == true
        }
        return false
    }

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        val eqnameStart = node.startOffset
        val localName = localName
        val localNameRef: PsiReference? =
            if (localName != null) when (type) {
                XPathEQName.Type.Function -> XQueryFunctionNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                XPathEQName.Type.Variable -> XQueryVariableNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                else -> null
            } else {
                null
            }

        val prefix = ((this as XdmStaticValue).staticValue as? QName)?.prefix as? PsiElement
        if (prefix == null || prefix is XPathBracedURILiteral) { // local name only
            if (localNameRef != null) {
                return arrayOf(localNameRef)
            }
            return PsiReference.EMPTY_ARRAY
        } else {
            if (localNameRef != null) {
                return arrayOf(XQueryEQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)), localNameRef)
            }
            return arrayOf(XQueryEQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)))
        }
    }

    override val localName get(): PsiElement? = ((this as XdmStaticValue).staticValue as? QName)?.localName as? PsiElement

    override fun resolvePrefixNamespace(): Sequence<XPathNamespaceDeclaration> {
        return when (this) {
            is XPathQName -> {
                val text = (((this as XdmStaticValue).staticValue as? QName)?.prefix as? PsiElement)!!.text
                return staticallyKnownNamespaces().filter { ns -> ns.namespacePrefix?.staticValue == text }
            }
            else -> emptySequence()
        }
    }
}
