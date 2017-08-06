/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.core.extensions.siblings
import uk.co.reecedunn.intellij.plugin.core.extensions.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName
import uk.co.reecedunn.intellij.plugin.core.functional.Option
import uk.co.reecedunn.intellij.plugin.xquery.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryEQNamePrefixReference
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryVariableNameReference

private val QNAME_SEPARATORS = TokenSet.create(
    XQueryTokenType.QNAME_SEPARATOR,
    XQueryTokenType.XML_TAG_QNAME_SEPARATOR,
    XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)

open class XQueryEQNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryEQName {
    override fun equals(other: Any?): Boolean {
        if (other !is XQueryEQName) {
            return false
        }

        if (localName?.text?.equals(other.localName?.text) == true) {
            val lhsPrefix = prefix
            val rhsPrefix = other.prefix
            return lhsPrefix == null && rhsPrefix == null || lhsPrefix?.text?.equals(rhsPrefix?.text) == true
        }
        return false
    }

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        val eqnameStart = textOffset
        val localName = localName
        val localNameRef: PsiReference? =
            if (localName != null) when (type) {
                XQueryEQName.Type.Function -> XQueryFunctionNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                XQueryEQName.Type.Variable -> XQueryVariableNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                else -> null
            } else {
                null
            }

        val prefix = prefix
        if (prefix == null || prefix is XQueryBracedURILiteral) { // local name only
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

    override val prefix get(): PsiElement? {
        var element: PsiElement? = firstChild
        if (element?.node?.elementType === XQueryElementType.URI_QUALIFIED_NAME) {
            return (element as XQueryEQName).prefix
        }

        var match: PsiElement? = null
        while (element != null) {
            if (element.node.elementType === XQueryElementType.NCNAME) {
                match = element
            } else if (QNAME_SEPARATORS.contains(element.node.elementType)) {
                return match
            }
            element = element.nextSibling
        }
        return null
    }

    override val localName get(): PsiElement? {
        var element = findChildByType<PsiElement>(QNAME_SEPARATORS)
        if (element == null) { // NCName | URIQualifiedName
            element = firstChild
            if (element?.node?.elementType === XQueryElementType.URI_QUALIFIED_NAME) {
                return (element as XQueryEQName).localName
            }

            return children().firstOrNull { e ->
                e.node.elementType is INCNameType ||
                e.node.elementType === XQueryElementType.NCNAME
            }
        }

        // QName
        return element.siblings().firstOrNull { e -> e.node.elementType === XQueryElementType.NCNAME }
    }

    override fun resolvePrefixNamespace(): Sequence<XQueryNamespace> {
        val prefix = prefix
        if (prefix == null || prefix is XQueryBracedURILiteral) {
            return emptySequence()
        }

        val text = prefix.text
        return prefix.walkTree().map { e ->
            if (e is XQueryNamespaceResolver) {
                val resolved = e.resolveNamespace(text)
                if (resolved.isDefined) resolved.get() else null
            } else {
                null
            }
        }.filterNotNull()
    }
}
