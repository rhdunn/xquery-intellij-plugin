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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val EMPTY_NAMESPACE = XsAnyUri("", XdmUriContext.Namespace, null as PsiElement?)

private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery", XdmUriContext.Namespace, null as PsiElement?)

private val NAMESPACE_TYPE = mapOf(
    XQueryElementType.ANNOTATION to XPathNamespaceType.XQuery,
    XPathElementType.ARROW_FUNCTION_SPECIFIER to XPathNamespaceType.DefaultFunctionRef,
    XPathElementType.ATOMIC_OR_UNION_TYPE to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.ATTRIBUTE_TEST to XPathNamespaceType.None,
    XQueryElementType.COMP_ATTR_CONSTRUCTOR to XPathNamespaceType.None,
    XQueryElementType.COMP_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.CURRENT_ITEM to XPathNamespaceType.None,
    XQueryElementType.DECIMAL_FORMAT_DECL to XPathNamespaceType.None,
    XQueryElementType.DIR_ATTRIBUTE to XPathNamespaceType.None,
    XQueryElementType.DIR_ELEM_CONSTRUCTOR to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.FUNCTION_CALL to XPathNamespaceType.DefaultFunctionRef,
    XQueryElementType.FUNCTION_DECL to XPathNamespaceType.DefaultFunctionDecl,
    XPathElementType.NAMED_FUNCTION_REF to XPathNamespaceType.DefaultFunctionRef,
    XQueryElementType.NEXT_ITEM to XPathNamespaceType.None,
    XQueryElementType.OPTION_DECL to XPathNamespaceType.XQuery,
    XPathElementType.PARAM to XPathNamespaceType.None,
    XPathElementType.PRAGMA to XPathNamespaceType.None,
    XQueryElementType.PREVIOUS_ITEM to XPathNamespaceType.None,
    XPathElementType.SCHEMA_ATTRIBUTE_TEST to XPathNamespaceType.None,
    XPathElementType.SCHEMA_ELEMENT_TEST to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.SIMPLE_TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XQueryElementType.TYPE_DECL to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.TYPE_NAME to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.UNION_TYPE to XPathNamespaceType.DefaultElementOrType,
    XPathElementType.VAR_NAME to XPathNamespaceType.None
)

// region XPath 3.1 (2.1.1) Default element/type namespace ; XPath 3.1 (2.1.1) Default function namespace

fun PsiElement.defaultNamespace(
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
            is XQueryModuleDecl -> {
                sequenceOf(node as XPathDefaultNamespaceDeclaration)
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
    }.filter { ns -> ns.accepts(type) && ns.namespaceUri?.data != null }
}

// endregion

private fun Sequence<XPathDefaultNamespaceDeclaration>.expandNCName(ncname: XsQNameValue): Sequence<XsQNameValue> {
    var seenDefaultNamespace: XPathDefaultNamespaceDeclaration? = null
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
        isLexicalQName && prefix == null /* NCName */ -> {
            when (val type = getNamespaceType()) {
                XPathNamespaceType.DefaultElementOrType -> element!!.defaultNamespace(type).expandNCName(this)
                XPathNamespaceType.DefaultFunctionDecl -> element!!.defaultNamespace(type).expandNCName(this)
                XPathNamespaceType.DefaultFunctionRef -> element!!.defaultNamespace(type).expandNCName(this)
                XPathNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false, element))
                XPathNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false, element))
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
        else /* URIQualifiedName */ -> sequenceOf(this)
    }
}