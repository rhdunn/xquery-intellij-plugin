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
package uk.co.reecedunn.intellij.plugin.marklogic.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

object MarkLogicBuiltInVariableProvider : XpmVariableProvider {
    override fun inScopeVariables(context: PsiElement): Sequence<XpmVariableDefinition> {
        val file = inCtsFunction(context)?.toPsiFile(context.project) as? XQueryModule ?: return emptySequence()
        return file.walkTree().filterIsInstance<XpmVariableDefinition>()
    }

    private fun inCtsFunction(context: PsiElement): VirtualFile? {
        val name = context.ancestorsAndSelf().filterIsInstance<XpmFunctionReference>().find {
            val functionName = it.functionName
            when (functionName?.localName?.data) {
                !in FUNCTIONS -> false
                else -> functionName?.expand()?.any { name -> name.namespace?.data == CTS_NAMESPACE } == true
            }
        }?.functionName?.localName?.data

        return when (name) {
            ELEMENT_WALK -> MarkLogicQueries.CtsElementWalkVariables
            ENTITY_HIGHLIGHT -> MarkLogicQueries.CtsEntityHighlightVariables
            HIGHLIGHT -> MarkLogicQueries.CtsHighlightVariables
            WALK -> MarkLogicQueries.CtsHighlightVariables
            else -> null
        }
    }

    private const val CTS_NAMESPACE = "http://marklogic.com/cts"

    private const val ELEMENT_WALK = "element-walk"
    private const val ENTITY_HIGHLIGHT = "entity-highlight"
    private const val HIGHLIGHT = "highlight"
    private const val WALK = "walk"

    private val FUNCTIONS = setOf(ELEMENT_WALK, ENTITY_HIGHLIGHT, HIGHLIGHT, WALK)
}
