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
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExprSingle
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginBlockVarDeclEntry
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDefaultCaseClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingBlockDecls
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingBlockVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

// region In-scope variables implementation details

private class InScopeVariableContext {
    var visitedFlworBinding = false
    var visitedFlworClause = false
    var visitedFlworClauseAsIntermediateClause = false
    var visitedFlworWindowConditions = false
    var visitedQuantifiedBinding = false
    var visitedTypeswitch = false
    var visitedBlockVarDeclEntry = false
    var visitedBlockVarDecl = false
    var visitedBlockDecls = false
}

// ForBinding, LetBinding, GroupingSpec
private fun PsiElement.flworBindingVariables(node: PsiElement, context: InScopeVariableContext): Sequence<XPathVariableBinding> {
    if (node is XQueryForBinding || node is XQueryLetBinding || node is XQueryGroupingSpec) {
        context.visitedFlworClause = true
        if (context.visitedFlworBinding) {
            context.visitedFlworBinding = false
            return emptySequence()
        }
    }

    val pos = children().filterIsInstance<XPathVariableBinding>().firstOrNull()
    return if (pos != null)
        sequenceOf(this as XPathVariableBinding, pos)
    else
        sequenceOf(this as XPathVariableBinding)
}

// ForClause, LetClause, GroupingSpecList
private fun PsiElement.flworClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableBinding> {
    return if (context.visitedFlworClause) {
        context.visitedFlworClause = false
        emptySequence()
    } else {
        children().flatMap { binding -> when (binding) {
            is XQueryForBinding, is XQueryLetBinding, is XQueryGroupingSpec -> binding.flworBindingVariables(this, context)
            else -> emptySequence()
        }}
    }
}

private fun PsiElement.windowConditionVariables(context: InScopeVariableContext): Sequence<XPathVariableBinding> {
    if (context.visitedFlworWindowConditions) {
        return emptySequence()
    }
    return children().filterIsInstance<XQueryWindowVars>().firstOrNull()
        ?.children()?.filterIsInstance<XPathVariableBinding>() ?: emptySequence()
}

// WindowClause + (SlidingWindowClause | TumblingWindowClause)
private fun PsiElement.windowClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableBinding> {
    val node = children().map { e -> when (e) {
        is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> e
        else -> null
    }}.filterNotNull().firstOrNull() ?: return emptySequence()

    return sequenceOf(
        if (context.visitedFlworBinding) emptySequence() else sequenceOf(node as XPathVariableBinding),
        node.children().filterIsInstance<XQueryWindowStartCondition>().flatMap { e -> e.windowConditionVariables(context) },
        node.children().filterIsInstance<XQueryWindowEndCondition>().flatMap   { e -> e.windowConditionVariables(context) }
    ).filterNotNull().flatten()
}

private fun PsiElement.groupByClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableBinding> {
    return children().filterIsInstance<XQueryGroupingSpecList>().firstOrNull()?.flworClauseVariables(context)
        ?: emptySequence()
}

private fun PsiElement.intermediateClauseVariables(context: InScopeVariableContext): Sequence<XPathVariableBinding> {
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
        is XPathVariableBinding -> sequenceOf(node as XPathVariableBinding)
        else -> emptySequence()
    }}
}

private fun PsiElement.blockVarDeclEntry(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    return if (context.visitedBlockVarDeclEntry) {
        context.visitedBlockVarDeclEntry = false
        emptySequence()
    } else
        sequenceOf(this as XPathVariableDeclaration)
}

private fun PsiElement.blockVarDecl(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    return if (context.visitedBlockVarDecl) {
        context.visitedBlockVarDecl = false
        emptySequence()
    } else {
        return children().filterIsInstance<PluginBlockVarDeclEntry>().flatMap { entry ->
            entry.blockVarDeclEntry(context)
        }
    }
}

private fun PsiElement.blockDecls(context: InScopeVariableContext): Sequence<XPathVariableDeclaration> {
    return if (context.visitedBlockDecls) {
        context.visitedBlockDecls = false
        emptySequence()
    } else {
        return children().filterIsInstance<ScriptingBlockVarDecl>().flatMap { entry ->
            entry.blockVarDecl(context)
        }
    }
}

// endregion
// region XPath 3.1 (2.1.1) In-scope variables

fun PsiElement.inScopeVariables(): Sequence<XPathVariableName> {
    val context = InScopeVariableContext()
    return walkTree().reversed().flatMap { node -> when (node) {
        is XQueryProlog -> (node as XPathVariableDeclarations).variables
        is XQueryForClause, is XQueryLetClause -> node.flworClauseVariables(context)
        is XQueryForBinding, is XQueryLetBinding, is XQueryGroupingSpec -> node.flworBindingVariables(node, context)
        is XQueryWindowClause -> node.windowClauseVariables(context)
        is XQueryWindowStartCondition, is XQueryWindowEndCondition -> node.windowConditionVariables(context)
        is PluginQuantifiedExprBinding -> {
            if (context.visitedQuantifiedBinding) {
                context.visitedQuantifiedBinding = false
                emptySequence()
            } else
                sequenceOf(node as XPathVariableBinding)
        }
        is XPathExprSingle -> {
            when (node.parent) {
                is XQueryForBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                    context.visitedFlworBinding = true
                    if (node.parent.parent.parent is XQueryIntermediateClause) { // The parent of the ForClause/LetClause.
                        context.visitedFlworClauseAsIntermediateClause = true
                    }
                }
                is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> {
                    context.visitedFlworWindowConditions = true // 'in' expression: don't include window conditions
                    context.visitedFlworBinding = true
                    if (node.parent.parent.parent is XQueryIntermediateClause) { // The parent of the ForClause/LetClause.
                        context.visitedFlworClauseAsIntermediateClause = true
                    }
                }
                is PluginQuantifiedExprBinding -> {
                    context.visitedQuantifiedBinding = true
                }
                is PluginBlockVarDeclEntry -> {
                    context.visitedBlockVarDeclEntry = true
                    context.visitedBlockVarDecl = true
                    context.visitedBlockDecls = true
                }
                else -> {}
            }
            emptySequence()
        }
        is XQueryIntermediateClause -> node.intermediateClauseVariables(context)
        is XQueryCaseClause, is PluginDefaultCaseClause -> {
            // Only the `case`/`default` clause variable of the return expression is in scope.
            if (!context.visitedTypeswitch) {
                context.visitedTypeswitch = true
                sequenceOf(node as XPathVariableBinding)
            } else
                emptySequence()
        }
        is XQueryTypeswitchExpr -> {
            context.visitedTypeswitch = false // Reset the visited logic now the `typeswitch` has been resolved.
            emptySequence()
        }
        is XPathParamList -> node.children().filterIsInstance<XPathVariableBinding>()
        is PluginBlockVarDeclEntry -> node.blockVarDeclEntry(context)
        is ScriptingBlockVarDecl -> node.blockVarDecl(context)
        is ScriptingBlockDecls -> node.blockDecls(context)
        else -> emptySequence()
    }}.filterNotNull().filter { variable -> variable.variableName != null }
}

// endregion
