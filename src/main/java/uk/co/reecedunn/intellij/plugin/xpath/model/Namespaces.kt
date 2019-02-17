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
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType2
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

private val EMPTY_NAMESPACE = XsAnyUri("", null as PsiElement?)
private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery", null as PsiElement?)

private val NAMESPACE_TYPE = mapOf(
    XQueryElementType.ANNOTATION to XPathNamespaceType.XQuery,
    XPathElementType.ARROW_FUNCTION_SPECIFIER to XPathNamespaceType.DefaultFunction,
    XPathElementType.ATOMIC_OR_UNION_TYPE to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.ATTRIBUTE_TEST to XPathNamespaceType.None,
    XQueryElementType.COMP_ATTR_CONSTRUCTOR to XPathNamespaceType.None,
    XQueryElementType.COMP_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.CURRENT_ITEM to XPathNamespaceType.None,
    XQueryElementType.DECIMAL_FORMAT_DECL to XPathNamespaceType.None,
    XQueryElementType.DIR_ATTRIBUTE to XPathNamespaceType.None,
    XQueryElementType2.DIR_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.FUNCTION_CALL to XPathNamespaceType.DefaultFunction,
    XQueryElementType.FUNCTION_DECL to XPathNamespaceType.DefaultFunction,
    XPathElementType.NAMED_FUNCTION_REF to XPathNamespaceType.DefaultFunction,
    XQueryElementType.NEXT_ITEM to XPathNamespaceType.None,
    XQueryElementType.OPTION_DECL to XPathNamespaceType.XQuery,
    XPathElementType.PARAM to XPathNamespaceType.None,
    XQueryElementType.PRAGMA to XPathNamespaceType.None,
    XQueryElementType.PREVIOUS_ITEM to XPathNamespaceType.None,
    XPathElementType.SCHEMA_ATTRIBUTE_TEST to XPathNamespaceType.None,
    XPathElementType.SCHEMA_ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.SIMPLE_TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.TYPE_DECL to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.UNION_TYPE to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.VAR_NAME to XPathNamespaceType.None
)

// region XPath 3.1 (2.1.1) Statically known namespaces

fun PsiElement.staticallyKnownNamespaces(): Sequence<XPathNamespaceDeclaration> {
    return walkTree().reversed().flatMap { node ->
        when (node) {
            is XPathNamespaceDeclaration ->
                sequenceOf(node as XPathNamespaceDeclaration)
            is XQueryDirElemConstructor ->
                node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                    ?.children()?.filterIsInstance<PluginDirAttribute>()?.map { attr -> attr as XPathNamespaceDeclaration }
                    ?: emptySequence()
            is XQueryProlog ->
                node.children().reversed().filterIsInstance<XPathNamespaceDeclaration>()
            is XQueryModule ->
                node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XPathNamespaceDeclaration>() ?: emptySequence()
            else -> emptySequence()
        }
    }.filterNotNull().distinct().filter { node -> node.namespacePrefix != null && node.namespaceUri != null }
}

// endregion
// region XPath 3.1 (2.1.1) Default element/type namespace ; XPath 3.1 (2.1.1) Default function namespace

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
                    (node as XQueryPrologResolver).prolog.flatMap { prolog -> prolog.defaultNamespace(type, false) }
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

// endregion

private fun Sequence<XPathDefaultNamespaceDeclaration>.expandNCName(ncname: XsQNameValue): Sequence<XsQNameValue> {
    return firstOrNull()?.let { decl ->
        val expanded =
            ncname.element!!.staticallyKnownNamespaces().filter { ns ->
                ns.namespaceUri?.data == decl.namespaceUri?.data
            }.map { ns ->
                XsQName(ns.namespaceUri, null, ncname.localName, false, ncname.element)
            }
        if (expanded.any())
            expanded
        else
            sequenceOf(XsQName(decl.namespaceUri, null, ncname.localName, false, ncname.element))
    } ?: emptySequence()
}

fun XsQNameValue.getNamespaceType(): XPathNamespaceType {
    val parentType = (this as? PsiElement)?.parent?.node?.elementType ?: return XPathNamespaceType.Undefined
    return if (parentType === XPathElementType.NAME_TEST)
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
                XPathNamespaceType.DefaultElementOrType -> element!!.defaultElementOrTypeNamespace().expandNCName(this)
                XPathNamespaceType.DefaultFunction -> element!!.defaultFunctionNamespace().expandNCName(this)
                XPathNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false, element))
                XPathNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false, element))
                else -> sequenceOf(this)
            }
        }
        isLexicalQName -> { // QName
            element!!.staticallyKnownNamespaces().filter { ns ->
                ns.namespacePrefix?.data == prefix!!.data
            }.map { ns ->
                XsQName(ns.namespaceUri, prefix, localName, false, element)
            }
        }
        else -> { // URIQualifiedName
            sequenceOf(
                element!!.staticallyKnownNamespaces().filter { ns ->
                    ns.namespaceUri?.data == namespace!!.data
                }.map { ns ->
                    XsQName(ns.namespaceUri, null, localName, false, element)
                },
                sequenceOf(this)
            ).flatten()
        }
    }
}