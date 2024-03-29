/*
 * Copyright (C) 2016-2017, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginTransactionSeparator
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val MARKLOGIC60 = listOf(MarkLogic.VERSION_4_0, XQuerySpec.MARKLOGIC_0_9)
private val MARKLOGIC60_SCRIPTING = listOf(
    MarkLogic.VERSION_4_0,
    XQuerySpec.MARKLOGIC_0_9,
    ScriptingSpec.NOTE_1_0_20140918
)
private val XQUERY = listOf<Version>()

class PluginTransactionSeparatorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginTransactionSeparator, XpmSyntaxValidationElement, VersionConformance {

    override val requiresConformance: List<Version>
        get() = when {
            parent.elementType === XQueryElementType.MODULE ->
                // File-level TransactionSeparators are created when the following QueryBody has a Prolog.
                MARKLOGIC60
            nextSibling === null ->
                // The last TransactionSeparator in a QueryBody.
                // NOTE: The behaviour differs from MarkLogic and Scripting Extension, so is checked in an inspection.
                XQUERY
            else -> MARKLOGIC60_SCRIPTING
        }

    override val conformanceElement: PsiElement
        get() = firstChild ?: this
}
