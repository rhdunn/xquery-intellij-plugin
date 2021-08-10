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
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDefinition
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
    var visitingFuntionDeclaration = false

    var variables: ArrayList<XpmVariableDefinition> = ArrayList()

    fun add(variable: XpmVariableDefinition) {
        if (variable.variableName != null) {
            variables.add(variable)
        }
    }
}

// ForBinding, ForMemberBinding, LetBinding, GroupingSpec
private fun PsiElement.flworBindingVariables(
    node: PsiElement,
    context: InScopeVariableContext
) {
    if (
        node is XQueryForBinding ||
        node is XQueryForMemberBinding ||
        node is XQueryLetBinding ||
        node is XQueryGroupingSpec
    ) {
        context.visitedFlworClause = true
        if (context.visitedFlworBinding) {
            context.visitedFlworBinding = false
            return
        }
    }

    context.add(this as XpmVariableBinding)
    children().filterIsInstance<XpmVariableBinding>().firstOrNull()?.let { pos -> context.add(pos) }
}

// ForClause, ForMemberClause, LetClause, GroupingSpecList
private fun PsiElement.flworClauseVariables(context: InScopeVariableContext) {
    if (context.visitedFlworClause) {
        context.visitedFlworClause = false
    } else {
        children().forEach { binding ->
            when (binding) {
                is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                    binding.flworBindingVariables(this, context)
                }
                else -> {
                }
            }
        }
    }
}

private fun PsiElement.windowConditionVariables(context: InScopeVariableContext) {
    if (context.visitedFlworWindowConditions) {
        return
    }
    children().filterIsInstance<XQueryWindowVars>().firstOrNull()
        ?.children()?.filterIsInstance<XpmVariableBinding>()
        ?.forEach { binding -> context.add(binding) }
}

// WindowClause + (SlidingWindowClause | TumblingWindowClause)
private fun PsiElement.windowClauseVariables(context: InScopeVariableContext) {
    val node = children().map { e ->
        when (e) {
            is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> e
            else -> null
        }
    }.filterNotNull().firstOrNull() ?: return

    if (!context.visitedFlworBinding) {
        context.add(node as XpmVariableBinding)
    }
    node.children().filterIsInstance<XQueryWindowStartCondition>().forEach { e ->
        e.windowConditionVariables(context)
    }
    node.children().filterIsInstance<XQueryWindowEndCondition>().forEach { e ->
        e.windowConditionVariables(context)
    }
}

private fun PsiElement.groupByClauseVariables(context: InScopeVariableContext) {
    if (context.visitedFlworClauseAsIntermediateClause) {
        context.visitedFlworClauseAsIntermediateClause = false
    } else {
        flworClauseVariables(context)
    }
}

private fun PsiElement.blockVarDeclEntry(context: InScopeVariableContext) {
    return if (context.visitedBlockVarDeclEntry) {
        context.visitedBlockVarDeclEntry = false
    } else {
        context.add(this as XpmVariableDefinition)
    }
}

private fun PsiElement.parameters(context: InScopeVariableContext) {
    if (context.visitingFuntionDeclaration) {
        context.visitingFuntionDeclaration = false
        (this as XpmFunctionDeclaration).parameters.forEach { parameter -> context.add(parameter) }
    }
}

private fun PsiElement.blockVarDecl(context: InScopeVariableContext) {
    if (context.visitedBlockVarDecl) {
        context.visitedBlockVarDecl = false
    } else {
        children().filterIsInstance<PluginBlockVarDeclEntry>().forEach { entry ->
            entry.blockVarDeclEntry(context)
        }
    }
}

private fun PsiElement.blockDecls(context: InScopeVariableContext) {
    if (context.visitedBlockDecls) {
        context.visitedBlockDecls = false
    } else {
        children().filterIsInstance<ScriptingBlockVarDecl>().forEach { entry ->
            entry.blockVarDecl(context)
        }
    }
}

private fun XQueryProlog.varDecls(context: InScopeVariableContext) {
    importedPrologs().forEach { prolog ->
        prolog.annotatedDeclarations<XpmVariableDeclaration>().forEach { declaration -> context.add(declaration) }
    }
}

// endregion
// region XPath 3.1 (2.1.1) In-scope variables

fun PsiElement.xqueryInScopeVariables(): Sequence<XpmVariableDefinition> {
    val context = InScopeVariableContext()
    reverse(walkTree()).forEach { node ->
        when (node) {
            is XQueryProlog -> node.varDecls(context)
            is XQueryForClause, is XQueryForMemberClause, is XQueryLetClause -> node.flworClauseVariables(context)
            is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                node.flworBindingVariables(node, context)
            }
            is XQueryWindowClause -> node.windowClauseVariables(context)
            is XQueryWindowStartCondition, is XQueryWindowEndCondition -> node.windowConditionVariables(context)
            is XPathQuantifierBinding -> {
                if (context.visitedQuantifiedBinding) {
                    context.visitedQuantifiedBinding = false
                } else {
                    context.add(node as XpmVariableBinding)
                }
            }
            is XQueryCountClause -> context.add(node as XpmVariableBinding)
            is XQueryGroupByClause -> node.groupByClauseVariables(context)
            is XQueryCaseClause, is PluginDefaultCaseClause -> {
                // Only the `case`/`default` clause variable of the return expression is in scope.
                if (!context.visitedTypeswitch) {
                    context.visitedTypeswitch = true
                    context.add(node as XpmVariableBinding)
                }
            }
            is XQueryTypeswitchExpr -> {
                context.visitedTypeswitch = false // Reset the visited logic now the `typeswitch` has been resolved.
            }
            is XpmFunctionDeclaration -> node.parameters(context)
            is PluginBlockVarDeclEntry -> node.blockVarDeclEntry(context)
            is ScriptingBlockVarDecl -> node.blockVarDecl(context)
            is ScriptingBlockDecls -> node.blockDecls(context)
            else -> when (node.parent) {
                is XQueryForBinding, is XQueryForMemberBinding, is XQueryLetBinding, is XQueryGroupingSpec -> {
                    context.visitedFlworBinding = true
                }
                is XQuerySlidingWindowClause, is XQueryTumblingWindowClause -> {
                    // 'in' expression: don't include window conditions
                    context.visitedFlworWindowConditions = true
                    context.visitedFlworBinding = true
                }
                is XPathQuantifierBinding -> {
                    context.visitedQuantifiedBinding = true
                }
                is PluginBlockVarDeclEntry -> {
                    context.visitedBlockVarDeclEntry = true
                    context.visitedBlockVarDecl = true
                    context.visitedBlockDecls = true
                }
                is XpmFunctionDeclaration -> {
                    context.visitingFuntionDeclaration = true
                }
                else -> {
                }
            }
        }
    }
    return context.variables.asSequence()
}

// endregion
