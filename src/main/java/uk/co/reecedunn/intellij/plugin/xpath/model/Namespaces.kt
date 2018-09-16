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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

private val EMPTY_NAMESPACE = XsAnyUri("")
private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery")

private val NAMESPACE_TYPE = mapOf(
    XQueryElementType.ANNOTATION to XPathNamespaceType.XQuery,
    XQueryElementType.ARROW_FUNCTION_SPECIFIER to XPathNamespaceType.DefaultFunction,
    XQueryElementType.ATOMIC_OR_UNION_TYPE to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.ATTRIBUTE_TEST to XPathNamespaceType.None,
    XQueryElementType.COMP_ATTR_CONSTRUCTOR to XPathNamespaceType.None,
    XQueryElementType.COMP_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.CURRENT_ITEM to XPathNamespaceType.None,
    XQueryElementType.DECIMAL_FORMAT_DECL to XPathNamespaceType.None,
    XQueryElementType.DIR_ATTRIBUTE to XPathNamespaceType.None,
    XQueryElementType.DIR_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.FUNCTION_CALL to XPathNamespaceType.DefaultFunction,
    XQueryElementType.FUNCTION_DECL to XPathNamespaceType.DefaultFunction,
    XQueryElementType.NAMED_FUNCTION_REF to XPathNamespaceType.DefaultFunction,
    XQueryElementType.NEXT_ITEM to XPathNamespaceType.None,
    XQueryElementType.OPTION_DECL to XPathNamespaceType.XQuery,
    XQueryElementType.PARAM to XPathNamespaceType.None,
    XQueryElementType.PRAGMA to XPathNamespaceType.None,
    XQueryElementType.PREVIOUS_ITEM to XPathNamespaceType.None,
    XQueryElementType.SCHEMA_ATTRIBUTE_TEST to XPathNamespaceType.None,
    XQueryElementType.SCHEMA_ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.SIMPLE_TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.TYPE_DECL to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.VAR_NAME to XPathNamespaceType.None
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
    var visitedProlog = false
    return walkTree().reversed().flatMap { node ->
        when (node) {
            is XQueryProlog -> {
                visitedProlog = true
                node.children().reversed().filterIsInstance<XPathDefaultNamespaceDeclaration>()
            }
            is XQueryModule -> {
                if (resolveProlog)
                    node.predefinedStaticContext?.children()?.reversed()
                        ?.filterIsInstance<XPathDefaultNamespaceDeclaration>() ?: emptySequence()
                else
                    emptySequence()
            }
            is XQueryMainModule ->
                if (resolveProlog && !visitedProlog)
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
    return if (parentType === XQueryElementType.NAME_TEST)
        when (((this as? PsiElement)?.parent?.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
            XPathPrincipalNodeKind.Element -> XPathNamespaceType.DefaultElementOrType
            else -> XPathNamespaceType.None
        }
    else
        NAMESPACE_TYPE.getOrDefault(parentType, XPathNamespaceType.Undefined)
}

fun XsQNameValue.expand(): Sequence<XsQNameValue> {
    return when {
        isLexicalQName && prefix == null -> { // NCName
            when (getNamespaceType()) {
                XPathNamespaceType.DefaultElementOrType -> {
                    (this as PsiElement).defaultElementOrTypeNamespace().map { decl ->
                        XsQName(decl.namespaceUri, null, localName, false)
                    }
                }
                XPathNamespaceType.DefaultFunction -> {
                    (this as PsiElement).defaultFunctionNamespace().map { decl ->
                        XsQName(decl.namespaceUri, null, localName, false)
                    }
                }
                XPathNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false))
                XPathNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false))
                else -> sequenceOf(this)
            }
        }
        isLexicalQName -> { // QName
            (this as PsiElement).staticallyKnownNamespaces().filter { ns ->
                ns.namespacePrefix?.data == prefix!!.data
            }.map { ns ->
                XsQName(ns.namespaceUri, prefix, localName, false)
            }
        }
        else -> { // URIQualifiedName
            sequenceOf(
                (this as PsiElement).staticallyKnownNamespaces().filter { ns ->
                    ns.namespaceUri?.data == namespace!!.data
                }.map { ns ->
                    XsQName(ns.namespaceUri, null, localName, false)
                },
                sequenceOf<XsQNameValue>(this)
            ).flatten()
        }
    }
}