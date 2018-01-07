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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

interface XPathStaticContext {
    val defaultElementOrTypeNamespace: Sequence<XdmStaticValue>

    val defaultFunctionNamespace: Sequence<XdmStaticValue>
}

fun PsiElement.staticallyKnownNamespaces(): Sequence<XPathNamespaceDeclaration> {
    return walkTree().reversed().flatMap { node -> when (node) {
        is XPathNamespaceDeclaration ->
            sequenceOf(node as XPathNamespaceDeclaration)
        is XQueryDirElemConstructor ->
            node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                ?.children()?.filterIsInstance<XQueryDirAttribute>()?.map { attr -> attr as XPathNamespaceDeclaration }
                ?: emptySequence()
        is XQueryProlog ->
            node.children().reversed().filterIsInstance<XPathNamespaceDeclaration>()
        is XQueryModule ->
            node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XPathNamespaceDeclaration>() ?: emptySequence()
        else -> emptySequence()
    }}.filterNotNull().distinct().filter {
        node -> node.namespacePrefix != null && node.namespaceUri != null
    }
}

fun PsiElement.inScopeVariables(): Sequence<XPathVariableDeclaration> {
    var visited = false
    return walkTree().reversed().map { node -> when (node) {
        is XQueryCaseClause, is XQueryDefaultCaseClause -> {
            // Only the `case`/`default` clause variable of the return expression is in scope.
            if (!visited) {
                visited = true
                node as XPathVariableDeclaration
            } else
                null
        }
        is XQueryTypeswitchExpr -> {
            visited = false // Reset the visited logic now the `typeswitch` has been resolved.
            null
        }
        else -> null
    }}.filterNotNull().filter {
        variable -> variable.variableName != null
    }
}
