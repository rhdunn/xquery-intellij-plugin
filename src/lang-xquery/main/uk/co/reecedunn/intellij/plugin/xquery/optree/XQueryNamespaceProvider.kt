/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.optree

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.defaultNamespace

object XQueryNamespaceProvider : XpmNamespaceProvider {
    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XQueryModule) return emptySequence()
        return context.walkTree().reversed().flatMap { node ->
            when (node) {
                is XpmNamespaceDeclaration -> sequenceOf(node as XpmNamespaceDeclaration)
                is XQueryDirElemConstructor -> node.attributes.filterIsInstance<XpmNamespaceDeclaration>()
                is XQueryProlog -> node.children().reversed().filterIsInstance<XpmNamespaceDeclaration>()
                is XQueryModule ->
                    node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XpmNamespaceDeclaration>()
                        ?: emptySequence()
                else -> emptySequence()
            }
        }.filterNotNull().distinct().filter { node -> node.namespacePrefix != null && node.namespaceUri != null }
    }

    override fun defaultNamespace(context: PsiElement, type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XQueryModule) return emptySequence()
        return context.defaultNamespace(type, true)
    }
}
