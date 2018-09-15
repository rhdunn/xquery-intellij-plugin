/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

private val EMPTY_NAMESPACE = XsAnyUri("")
private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery")

private val EMPTY_NS_PARENTS = TokenSet.create(
    XQueryElementType.ATTRIBUTE_TEST,
    XQueryElementType.COMP_ATTR_CONSTRUCTOR,
    XQueryElementType.CURRENT_ITEM,
    XQueryElementType.DECIMAL_FORMAT_DECL,
    XQueryElementType.DIR_ATTRIBUTE,
    XQueryElementType.NEXT_ITEM,
    XQueryElementType.PARAM,
    XQueryElementType.PRAGMA,
    XQueryElementType.PREVIOUS_ITEM,
    XQueryElementType.SCHEMA_ATTRIBUTE_TEST,
    XQueryElementType.VAR_NAME
)

private val XQUERY_NS_PARENTS = TokenSet.create(
    XQueryElementType.ANNOTATION,
    XQueryElementType.OPTION_DECL
)

enum class XPathNamespaceType {
    DefaultElementOrType,
    DefaultFunction,
    None,
    Prefixed,
    Undefined,
    XQuery,
}

interface XPathNamespaceDeclaration {
    val namespacePrefix: XsNCNameValue?

    val namespaceUri: XsAnyUriValue?
}

interface XPathDefaultNamespaceDeclaration : XPathNamespaceDeclaration {
    val namespaceType: XPathNamespaceType
}

private fun PsiElement.defaultNamespace(
    type: XPathNamespaceType,
    resolveProlog: Boolean
): Sequence<XPathDefaultNamespaceDeclaration> {
    return walkTree().reversed().flatMap { node ->
        when (node) {
            is XQueryProlog ->
                node.children().reversed().filterIsInstance<XPathDefaultNamespaceDeclaration>()
            is XQueryModule -> {
                if (resolveProlog)
                    node.predefinedStaticContext?.children()?.reversed()
                        ?.filterIsInstance<XPathDefaultNamespaceDeclaration>() ?: emptySequence()
                else
                    emptySequence()
            }
            is XQueryMainModule ->
                if (resolveProlog)
                    (node as XQueryPrologResolver).prolog?.defaultNamespace(type, false) ?: emptySequence()
                else
                    emptySequence()
            is XQueryDirElemConstructor ->
                node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                    ?.children()?.filterIsInstance<XPathDefaultNamespaceDeclaration>() ?: emptySequence()
            else -> emptySequence()
        }
    }.filter { ns -> ns.namespaceType === type && ns.namespaceUri?.data != null }
}

fun PsiElement.defaultElementOrTypeNamespace(): Sequence<XPathDefaultNamespaceDeclaration> {
    return defaultNamespace(XPathNamespaceType.DefaultElementOrType, true)
}

fun PsiElement.defaultFunctionNamespace(): Sequence<XPathDefaultNamespaceDeclaration> {
    return defaultNamespace(XPathNamespaceType.DefaultFunction, true)
}

fun XsQNameValue.getNamespaceType(): XPathNamespaceType {
    val parentType = (this as? PsiElement)?.parent?.node?.elementType
    return when {
        XQUERY_NS_PARENTS.contains(parentType) -> XPathNamespaceType.XQuery
        EMPTY_NS_PARENTS.contains(parentType) -> XPathNamespaceType.None
        else -> XPathNamespaceType.Undefined
    }
}

fun XsQNameValue.expand(): Sequence<XsQNameValue> {
    return when {
        isLexicalQName && prefix == null -> { // NCName
            when (this.getNamespaceType()) {
                XPathNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false))
                XPathNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false))
                else -> sequenceOf(this)
            }
        }
        else -> sequenceOf(this)
    }
}