/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableName
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.filterNotWhitespace
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import javax.swing.Icon

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XQueryVarDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryVarDecl,
    VersionConformance,
    XpmVariableType,
    ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
        cachedAlphaSortKey.invalidate()
    }

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() {
            if (conformanceElement === firstChild) {
                return XQUERY10
            }
            return XQUERY30
        }

    override val conformanceElement: PsiElement
        get() {
            val element = findChildByType<PsiElement>(XPathTokenType.ASSIGN_EQUAL)
            val previous = element?.siblings()?.reversed()?.filterNotWhitespace()?.firstOrNull()
            return if (previous == null || previous.elementType !== XQueryTokenType.K_EXTERNAL) firstChild else element
        }

    // endregion
    // region XPathVariableDeclaration

    private val varName: XpmVariableName?
        get() = children().filterIsInstance<XPathVarName>().firstOrNull() as? XpmVariableName

    override val variableName: XsQNameValue?
        get() = varName?.variableName

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XPathIcons.Nodes.VarDecl

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        varName?.variableName?.let { name ->
            val type = variableType
            if (type == null)
                op_qname_presentation(name)
            else
                "${op_qname_presentation(name)} as ${type.typeName}"
        }
    }

    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
    // region SortableTreeElement

    private val cachedAlphaSortKey = CacheableProperty {
        varName?.variableName?.let { op_qname_presentation(it) } ?: ""
    }

    override fun getAlphaSortKey(): String = cachedAlphaSortKey.get()!!

    // endregion
}
