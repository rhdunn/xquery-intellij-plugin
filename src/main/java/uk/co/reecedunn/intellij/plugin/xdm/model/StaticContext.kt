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
package uk.co.reecedunn.intellij.plugin.xdm.model

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

enum class QNameContext {
    Element,
    Function,
    Type
}

interface XdmStaticContext {
    fun defaultNamespace(context: QNameContext): Sequence<XdmLexicalValue>
}

fun PsiElement.inScopeNamespaces(): Sequence<XdmNamespaceDeclaration> {
    return walkTree().reversed().flatMap { node -> when (node) {
        is XdmNamespaceDeclaration ->
            sequenceOf(node as XdmNamespaceDeclaration)
        is XQueryDirElemConstructor ->
            node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                ?.children()?.filterIsInstance<XQueryDirAttribute>()?.map { attr -> attr as XdmNamespaceDeclaration }
                ?: emptySequence()
        is XQueryProlog ->
            node.children().reversed().filterIsInstance<XdmNamespaceDeclaration>()
        is XQueryModule ->
            node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XdmNamespaceDeclaration>() ?: emptySequence()
        else -> emptySequence()
    }}.filterNotNull().distinct().filter {
        node -> node.namespacePrefix != null && node.namespaceUri != null
    }
}
