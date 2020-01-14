/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.model

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val EMPTY_NAMESPACE = XsAnyUri("", XdmUriContext.Namespace, XdmModuleType.NONE)

private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery", XdmUriContext.Namespace, XdmModuleType.NONE)

private val NAMESPACE_TYPE = mapOf(
    XQueryElementType.ANNOTATION to XdmNamespaceType.XQuery,
    XPathElementType.ARROW_FUNCTION_SPECIFIER to XdmNamespaceType.DefaultFunctionRef,
    XPathElementType.ATOMIC_OR_UNION_TYPE to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.ATTRIBUTE_TEST to XdmNamespaceType.None,
    XQueryElementType.COMP_ATTR_CONSTRUCTOR to XdmNamespaceType.None,
    XQueryElementType.COMP_ELEM_CONSTRUCTOR to XdmNamespaceType.DefaultElementOrType,
    XQueryElementType.CURRENT_ITEM to XdmNamespaceType.None,
    XQueryElementType.DECIMAL_FORMAT_DECL to XdmNamespaceType.None,
    XQueryElementType.DIR_ATTRIBUTE to XdmNamespaceType.None,
    XQueryElementType.DIR_ELEM_CONSTRUCTOR to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.ELEMENT_TEST to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.FUNCTION_CALL to XdmNamespaceType.DefaultFunctionRef,
    XQueryElementType.FUNCTION_DECL to XdmNamespaceType.DefaultFunctionDecl,
    XPathElementType.NAMED_FUNCTION_REF to XdmNamespaceType.DefaultFunctionRef,
    XQueryElementType.NEXT_ITEM to XdmNamespaceType.None,
    XQueryElementType.OPTION_DECL to XdmNamespaceType.XQuery,
    XPathElementType.PARAM to XdmNamespaceType.None,
    XPathElementType.PRAGMA to XdmNamespaceType.None,
    XQueryElementType.PREVIOUS_ITEM to XdmNamespaceType.None,
    XPathElementType.SCHEMA_ATTRIBUTE_TEST to XdmNamespaceType.None,
    XPathElementType.SCHEMA_ELEMENT_TEST to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.SIMPLE_TYPE_NAME to XdmNamespaceType.DefaultElementOrType,
    XQueryElementType.TYPE_DECL to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.TYPE_NAME to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.UNION_TYPE to XdmNamespaceType.DefaultElementOrType,
    XPathElementType.VAR_NAME to XdmNamespaceType.None
)

// region XPath 3.1 (2.1.1) Default element/type namespace ; XPath 3.1 (2.1.1) Default function namespace

fun PsiElement.defaultNamespace(
    type: XdmNamespaceType,
    resolveProlog: Boolean
): Sequence<XdmDefaultNamespaceDeclaration> {
    var visitedProlog = false
    return walkTree().reversed().flatMap { node ->
        when (node) {
            is XQueryProlog -> {
                visitedProlog = true
                node.children().reversed().filterIsInstance<XdmDefaultNamespaceDeclaration>()
            }
            is XQueryModule -> {
                if (resolveProlog)
                    node.predefinedStaticContext?.children()?.reversed()
                        ?.filterIsInstance<XdmDefaultNamespaceDeclaration>() ?: emptySequence()
                else
                    emptySequence()
            }
            is XQueryModuleDecl -> {
                sequenceOf(node as XdmDefaultNamespaceDeclaration)
            }
            is XQueryMainModule ->
                if (resolveProlog && !visitedProlog)
                    (node as XQueryPrologResolver).prolog.flatMap { prolog -> prolog.defaultNamespace(type, false) }
                else
                    emptySequence()
            is XQueryDirElemConstructor ->
                node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                    ?.children()?.filterIsInstance<XdmDefaultNamespaceDeclaration>() ?: emptySequence()
            else -> emptySequence()
        }
    }.filter { ns -> ns.accepts(type) && ns.namespaceUri?.data != null }
}

// endregion

private fun Sequence<XdmDefaultNamespaceDeclaration>.expandNCName(ncname: XsQNameValue): Sequence<XsQNameValue> {
    var seenDefaultNamespace: XdmDefaultNamespaceDeclaration? = null
    val expanded = flatMap { decl ->
        val isDefaultNamespace = decl is XQueryDefaultNamespaceDecl
        if ((isDefaultNamespace && seenDefaultNamespace == null) || !isDefaultNamespace) {
            if (isDefaultNamespace) {
                seenDefaultNamespace = decl
            }

            ncname.element!!.staticallyKnownNamespaces().filter { ns ->
                ns.namespaceUri?.data == decl.namespaceUri?.data
            }.map { ns ->
                XsQName(ns.namespaceUri, null, ncname.localName, false, ncname.element)
            }
        } else {
            sequenceOf()
        }
    }.toList()

    return when {
        expanded.isNotEmpty() -> expanded.asSequence()
        seenDefaultNamespace != null -> {
            sequenceOf(XsQName(seenDefaultNamespace!!.namespaceUri, null, ncname.localName, false, ncname.element))
        }
        else -> sequenceOf()
    }
}

fun XsQNameValue.getNamespaceType(): XdmNamespaceType {
    val parentType = (this as? PsiElement)?.parent?.node?.elementType ?: return XdmNamespaceType.Undefined
    return when {
        parentType === XPathElementType.NAME_TEST -> {
            when (((this as? PsiElement)?.parent?.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
                XPathPrincipalNodeKind.Element -> XdmNamespaceType.DefaultElementOrType
                else -> XdmNamespaceType.None
            }
        }
        node.elementType === XQueryElementType.COMPATIBILITY_ANNOTATION -> XdmNamespaceType.XQuery
        else -> NAMESPACE_TYPE.getOrDefault(parentType, XdmNamespaceType.Undefined)
    }
}

fun XsQNameValue.expand(): Sequence<XsQNameValue> {
    return when {
        isLexicalQName && prefix == null /* NCName */ -> {
            when (val type = getNamespaceType()) {
                XdmNamespaceType.DefaultElementOrType -> element!!.defaultNamespace(type).expandNCName(this)
                XdmNamespaceType.DefaultFunctionDecl -> element!!.defaultNamespace(type).expandNCName(this)
                XdmNamespaceType.DefaultFunctionRef -> element!!.defaultNamespace(type).expandNCName(this)
                XdmNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false, element))
                XdmNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false, element))
                else -> sequenceOf(this)
            }
        }
        isLexicalQName /* QName */ -> {
            element!!.staticallyKnownNamespaces().filter { ns ->
                ns.namespacePrefix?.data == prefix!!.data
            }.map { ns ->
                XsQName(ns.namespaceUri, prefix, localName, false, element)
            }
        }
        else /* URIQualifiedName */ -> {
            val expanded = element!!.staticallyKnownNamespaces().filter { ns ->
                ns.namespaceUri?.data == namespace!!.data
            }.map { ns ->
                XsQName(ns.namespaceUri, prefix, localName, false, element)
            }
            if (expanded.any())
                expanded
            else
                sequenceOf(this)
        }
    }
}