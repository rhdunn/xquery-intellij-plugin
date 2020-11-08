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
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

private val EMPTY_NAMESPACE = XsAnyUri("", XdmUriContext.Namespace, XdmModuleType.NONE)

private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery", XdmUriContext.Namespace, XdmModuleType.NONE)

// region XPath 3.1 (2.1.1) Default element/type namespace ; XPath 3.1 (2.1.1) Default function namespace

fun PsiElement.defaultNamespace(
    type: XdmNamespaceType,
    resolveProlog: Boolean
): Sequence<XpmNamespaceDeclaration> {
    var visitedProlog = false
    return walkTree().reversed().flatMap { node ->
        when (node) {
            is XQueryProlog -> {
                visitedProlog = true
                node.children().reversed().filterIsInstance<XpmNamespaceDeclaration>()
            }
            is XQueryModule -> {
                if (resolveProlog)
                    node.predefinedStaticContext?.children()?.reversed()
                        ?.filterIsInstance<XpmNamespaceDeclaration>() ?: emptySequence()
                else
                    emptySequence()
            }
            is XQueryModuleDecl -> {
                sequenceOf(node as XpmNamespaceDeclaration)
            }
            is XQueryMainModule ->
                if (resolveProlog && !visitedProlog)
                    (node as XQueryPrologResolver).prolog.flatMap { prolog -> prolog.defaultNamespace(type, false) }
                else
                    emptySequence()
            is XQueryDirElemConstructor ->
                node.children().filterIsInstance<XpmNamespaceDeclaration>()
            else -> emptySequence()
        }
    }.filter { ns -> ns.accepts(type) && ns.namespaceUri?.data != null }
}

// endregion

private fun Sequence<XpmNamespaceDeclaration>.expandNCName(ncname: XsQNameValue): Sequence<XsQNameValue> {
    var seenDefaultNamespace: XpmNamespaceDeclaration? = null
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
    return (this as? PsiElement)?.getUsageType()?.namespaceType ?: return XdmNamespaceType.Undefined
}

fun XsQNameValue.expandQName(): Sequence<XsQNameValue> = when {
    isLexicalQName && prefix == null /* NCName */ -> when (val type = getNamespaceType()) {
        XdmNamespaceType.DefaultElementOrType -> element!!.defaultNamespace(type).expandNCName(this)
        XdmNamespaceType.DefaultFunctionDecl -> element!!.defaultNamespace(type).expandNCName(this)
        XdmNamespaceType.DefaultFunctionRef -> element!!.defaultNamespace(type).expandNCName(this)
        XdmNamespaceType.None -> sequenceOf(XsQName(EMPTY_NAMESPACE, null, localName, false, element))
        XdmNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false, element))
        else -> sequenceOf(this)
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
