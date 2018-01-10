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
    var visitedFlworBinding = false
    var visitedFlworClause = false
    var visitedFlworClauseAsIntermediateClause = false
}

// ForBinding, LetBinding, GroupingSpec
private fun PsiElement.flworBindingVariables(node: PsiElement, context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    if (node is XQueryForBinding || node is XQueryLetBinding || node is XQueryGroupingSpec) {
        context.visitedFlworClause = true
        if (context.visitedFlworBinding) {
            context.visitedFlworBinding = false
            return emptySequence()
        }
    }

    val pos = children().filterIsInstance<XPathVariableDeclaration>().firstOrNull()
    return if (pos != null)
        sequenceOf(this as XPathVariableDeclaration, pos)
    else
        sequenceOf(this as XPathVariableDeclaration)
}

// ForClause, LetClause, GroupingSpecList
private fun PsiElement.flworClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    if (context.visitedFlworClause) {
        context.visitedFlworClause = false
        return emptySequence()
    } else {
        return children().flatMap { binding -> when (binding) {
            is XQueryForBinding, is XQueryLetBinding, is XQueryGroupingSpec -> binding.flworBindingVariables(this, context)
            else -> emptySequence()
        }}
    }
}

// WindowClause + SlidingWindowClause
private fun PsiElement.windowClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    if (context.visitedFlworBinding) {
        return emptySequence()
    }

    val node = children().map { e -> when (e) {
        is XQuerySlidingWindowClause -> e as PsiElement
        else -> null
    }}.filterNotNull().firstOrNull() ?: return emptySequence()

    return sequenceOf(node as XPathVariableDeclaration)
}

private fun PsiElement.groupByClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    return children().filterIsInstance<XQueryGroupingSpecList>().firstOrNull()?.flworClauseVariables(context)
        ?: emptySequence()
}

private fun PsiElement.intermediateClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    return children().flatMap { node -> when (node) {
        is XQueryForClause, is XQueryLetClause ->
            if (context.visitedFlworClauseAsIntermediateClause) {
                context.visitedFlworClauseAsIntermediateClause = false
                emptySequence()
            } else
                node.flworClauseVariables(context)
        is XQueryGroupByClause ->
            if (context.visitedFlworClauseAsIntermediateClause) {
                context.visitedFlworClauseAsIntermediateClause = false
                emptySequence()
            } else
                node.groupByClauseVariables(context)
        is XQueryWindowClause -> node.windowClauseVariables(context)
        is XPathVariableDeclaration -> sequenceOf(node as XPathVariableDeclaration)
        else -> emptySequence()
    }}
}

fun PsiElement.inScopeVariables(): Sequence<XPathVariableDeclaration> {
    val context = InScopeVariableContext()
    return walkTree().reversed().flatMap { node -> when (node) {
        // NOTE: GroupingSpecList is handled by the IntermediateClause logic.
        is XQueryForClause, is XQueryLetClause -> node.flworClauseVariables(context)
        is XQueryForBinding, is XQueryLetBinding, is XQueryGroupingSpec -> node.flworBindingVariables(node, context)
        is XQueryWindowClause -> node.windowClauseVariables(context)
        is XPathExprSingle -> {
            when (node.parent) {
                is XQueryForBinding, is XQueryLetBinding,
                is XQueryGroupingSpec,
                is XQuerySlidingWindowClause -> {
                    context.visitedFlworBinding = true
                    if (node.parent.parent.parent is XQueryIntermediateClause) { // The parent of the ForClause/LetClause.
                        context.visitedFlworClauseAsIntermediateClause = true
                    }
                }
                else -> {}
            }
            emptySequence()
        }
        is XQueryIntermediateClause -> node.intermediateClauseVariables(context)
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
        else -> emptySequence()
    }}.filterNotNull().filter {
        variable -> variable.variableName != null
    }
}

// endregion
