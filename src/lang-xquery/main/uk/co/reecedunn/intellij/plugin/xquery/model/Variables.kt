/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQuantifierBinding
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_equal
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.inScopeVariables
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

// ForBinding, ForMemberBinding, LetBinding, GroupingSpec
private fun PsiElement.flworBindingVariables(
    node: PsiElement,
    context: InScopeVariableContext
): Sequence<XpmVariableBinding> {
    if (
        node is XQueryForBinding ||
        node is XQueryForMemberBinding ||
        node is XQueryLetBinding ||
        node is XQueryGroupingSpec
    ) {
        context.visitedFlworClause = true
        if (context.visitedFlworBinding) {
            context.visitedFlworBinding = false
            return emptySequence()
        }
    }

    val pos = children().filterIsInstance<XpmVariableBinding>().firstOrNull()
    return if (pos != null)
        sequenceOf(this as XpmVariableBinding, pos)
    else
        sequenceOf(this as XpmVariableBinding)
}

// ForClause, ForMemberClause, LetClause, GroupingSpecList
private fun PsiElement.flworClauseVariables(context: InScopeVariableContext): Sequence<XpmVariableBinding> {
    return if (context.visitedFlworClause) {
        context.visitedFlworClause = false
        emptySequence()
    } else {
        children().flatMap { binding ->
            when (binding) {
                is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                    binding.flworBindingVariables(this, context)
                }
                else -> emptySequence()
            }
        }
    }
}

private fun PsiElement.windowConditionVariables(context: InScopeVariableContext): Sequence<XpmVariableBinding> {
    if (context.visitedFlworWindowConditions) {
        return emptySequence()
    }
    return children().filterIsInstance<XQueryWindowVars>().firstOrNull()
        ?.children()?.filterIsInstance<XpmVariableBinding>() ?: emptySequence()
}

// WindowClause + (SlidingWindowClause | TumblingWindowClause)
private fun PsiElement.windowClauseVariables(context: InScopeVariableContext): Sequence<XpmVariableBinding> {
    val node = children().map { e ->
        when (e) {
            is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> e
            else -> null
        }
    }.filterNotNull().firstOrNull() ?: return emptySequence()

    return sequenceOf(
        if (context.visitedFlworBinding) emptySequence() else sequenceOf(node as XpmVariableBinding),
        node.children().filterIsInstance<XQueryWindowStartCondition>().flatMap { e ->
            e.windowConditionVariables(context)
        },
        node.children().filterIsInstance<XQueryWindowEndCondition>().flatMap { e ->
            e.windowConditionVariables(context)
        }
    ).filterNotNull().flatten()
}

private fun PsiElement.groupByClauseVariables(context: InScopeVariableContext): Sequence<XpmVariableBinding> {
    return children().filterIsInstance<XQueryGroupingSpecList>().firstOrNull()?.flworClauseVariables(context)
        ?: emptySequence()
}

private fun PsiElement.intermediateClauseVariables(context: InScopeVariableContext): Sequence<XpmVariableBinding> {
    return children().flatMap { node ->
        when (node) {
            is XQueryForClause, is XQueryForMemberClause, is XQueryLetClause ->
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
            is XpmVariableBinding -> sequenceOf(node as XpmVariableBinding)
            else -> emptySequence()
        }
    }
}

private fun PsiElement.blockVarDeclEntry(context: InScopeVariableContext): Sequence<XpmVariableDeclaration> {
    return if (context.visitedBlockVarDeclEntry) {
        context.visitedBlockVarDeclEntry = false
        emptySequence()
    } else
        sequenceOf(this as XpmVariableDeclaration)
}

private fun PsiElement.blockVarDecl(context: InScopeVariableContext): Sequence<XpmVariableDeclaration> {
    return if (context.visitedBlockVarDecl) {
        context.visitedBlockVarDecl = false
        emptySequence()
    } else {
        return children().filterIsInstance<PluginBlockVarDeclEntry>().flatMap { entry ->
            entry.blockVarDeclEntry(context)
        }
    }
}

private fun PsiElement.blockDecls(context: InScopeVariableContext): Sequence<XpmVariableDeclaration> {
    return if (context.visitedBlockDecls) {
        context.visitedBlockDecls = false
        emptySequence()
    } else {
        return children().filterIsInstance<ScriptingBlockVarDecl>().flatMap { entry ->
            entry.blockVarDecl(context)
        }
    }
}

private fun XQueryProlog.varDecls(): Sequence<XpmVariableDeclaration?> {
    return importedPrologs().flatMap { prolog ->
        prolog.annotatedDeclarations<XpmVariableDeclaration>()
    }.filter { variable -> variable.variableName != null }
}

// endregion
// region XPath 3.1 (2.1.1) In-scope variables

fun PsiElement.xqueryInScopeVariables(): Sequence<XpmVariableDefinition> {
    val context = InScopeVariableContext()
    return reverse(walkTree())
        .flatMap { node ->
            when (node) {
                is XQueryProlog -> node.varDecls().filterNotNull()
                is XQueryForClause, is XQueryForMemberClause, is XQueryLetClause -> node.flworClauseVariables(context)
                is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                    node.flworBindingVariables(node, context)
                }
                is XQueryWindowClause -> node.windowClauseVariables(context)
                is XQueryWindowStartCondition, is XQueryWindowEndCondition -> node.windowConditionVariables(context)
                is XPathQuantifierBinding -> {
                    if (context.visitedQuantifiedBinding) {
                        context.visitedQuantifiedBinding = false
                        emptySequence()
                    } else
                        sequenceOf(node as XpmVariableBinding)
                }
                is XQueryIntermediateClause -> node.intermediateClauseVariables(context)
                is XQueryCaseClause, is PluginDefaultCaseClause -> {
                    // Only the `case`/`default` clause variable of the return expression is in scope.
                    if (!context.visitedTypeswitch) {
                        context.visitedTypeswitch = true
                        sequenceOf(node as XpmVariableBinding)
                    } else
                        emptySequence()
                }
                is XQueryTypeswitchExpr -> {
                    context.visitedTypeswitch = false // Reset the visited logic now the `typeswitch` has been resolved.
                    emptySequence()
                }
                is XPathParamList -> node.children().filterIsInstance<XpmVariableBinding>()
                is PluginBlockVarDeclEntry -> node.blockVarDeclEntry(context)
                is ScriptingBlockVarDecl -> node.blockVarDecl(context)
                is ScriptingBlockDecls -> node.blockDecls(context)
                else -> {
                    when (node.parent) {
                        is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                            context.visitedFlworBinding = true
                            if (node.parent.parent.parent is XQueryIntermediateClause) {
                                // The parent of the ForClause/ForMemberClause/LetClause.
                                context.visitedFlworClauseAsIntermediateClause = true
                            }
                        }
                        is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> {
                            // 'in' expression: don't include window conditions
                            context.visitedFlworWindowConditions = true
                            context.visitedFlworBinding = true
                            if (node.parent.parent.parent is XQueryIntermediateClause) {
                                // The parent of the ForClause/ForMemberClause/LetClause.
                                context.visitedFlworClauseAsIntermediateClause = true
                            }
                        }
                        is XPathQuantifierBinding -> {
                            context.visitedQuantifiedBinding = true
                        }
                        is PluginBlockVarDeclEntry -> {
                            context.visitedBlockVarDeclEntry = true
                            context.visitedBlockVarDecl = true
                            context.visitedBlockDecls = true
                        }
                        else -> {
                        }
                    }
                    emptySequence()
                }
            }
        }
        .filter { variable -> variable.variableName != null }
}

// endregion
