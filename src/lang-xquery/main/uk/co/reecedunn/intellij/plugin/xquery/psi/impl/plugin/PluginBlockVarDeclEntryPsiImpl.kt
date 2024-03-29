/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginBlockVarDeclEntry

class PluginBlockVarDeclEntryPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginBlockVarDeclEntry {
    // region PsiElement

    override fun getUseScope(): SearchScope = LocalSearchScope(parent.parent.parent)

    // endregion
    // region XpmAnnotatedDeclaration

    override val annotations: Sequence<XdmAnnotation> = emptySequence()

    // endregion
    // region XpmVariableDefinition

    override val isExternal: Boolean = false

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val accessLevel: XpmAccessLevel = XpmAccessLevel.Public

    // endregion
    // region XpmVariableDeclaration

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val variableExpression: XpmExpression?
        get() = children().filterIsInstance<XpmExpression>().firstOrNull()

    // endregion
}
