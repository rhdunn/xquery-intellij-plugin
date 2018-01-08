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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExprSingle
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

// region In-scope variables

private class InScopeVariableContext {
    var visitedTypeswitch = false
    var visitedForLetBinding = false
    var visitedForLetClause = false
}

private fun PsiElement.forLetBindingVariables(node: PsiElement, context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    if (node is XQueryForBinding || node is XQueryLetBinding) {
        context.visitedForLetClause = true
        if (context.visitedForLetBinding) {
            context.visitedForLetBinding = false
            return emptySequence()
        }
    }

    val pos = children().filterIsInstance<XPathVariableDeclaration>().firstOrNull()
    return if (pos != null)
        sequenceOf(this as XPathVariableDeclaration, pos)
    else
        sequenceOf(this as XPathVariableDeclaration)
}

fun PsiElement.inScopeVariables(): Sequence<XPathVariableDeclaration> {
    val context = InScopeVariableContext()
    return walkTree().reversed().flatMap { node -> when (node) {
        // region ForClause/LetClause
        is XQueryForClause, is XQueryLetClause -> {
            if (context.visitedForLetClause)
                emptySequence()
            else
                node.children().flatMap { binding -> when (binding) {
                    is XQueryForBinding, is XQueryLetBinding -> binding.forLetBindingVariables(node, context)
                    else -> emptySequence()
                }}
        }
        is XQueryForBinding, is XQueryLetBinding -> node.forLetBindingVariables(node, context)
        is XPathExprSingle -> {
            if (node.parent is XQueryForBinding || node.parent is XQueryLetBinding) {
                context.visitedForLetBinding = true
            }
            emptySequence()
        }
        // endregion
        // region IntermediateClause (CountClause)
        is XQueryIntermediateClause ->
            node.children().filterIsInstance<XPathVariableDeclaration>()
        // endregion
        // region TypeswitchExpr
        is XQueryCaseClause, is XQueryDefaultCaseClause -> {
            // Only the `case`/`default` clause variable of the return expression is in scope.
            if (!context.visitedTypeswitch) {
                context.visitedTypeswitch = true
                sequenceOf(node as XPathVariableDeclaration)
            } else
                emptySequence()
        }
        is XQueryTypeswitchExpr -> {
            context.visitedTypeswitch = false // Reset the visited logic now the `typeswitch` has been resolved.
            emptySequence()
        }
        // endregion
        else -> emptySequence()
    }}.filterNotNull().filter {
        variable -> variable.variableName != null
    }
}

// endregion
