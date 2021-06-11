/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.filterNotWhitespace
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import java.util.*
import javax.swing.Icon

class XQueryVarDeclPsiImpl(node: ASTNode) :
    XQueryAnnotatedDeclPsiImpl(node),
    XQueryVarDecl,
    XpmSyntaxValidationElement,
    ItemPresentation {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
        private val ALPHA_SORT_KEY = Key.create<String>("ALPHA_SORT_KEY")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
        clearUserData(ALPHA_SORT_KEY)
    }

    // endregion
    // region VersionConformance

    override val conformanceElement: PsiElement
        get() {
            val element = findChildByType<PsiElement>(XPathTokenType.ASSIGN_EQUAL) ?: return firstChild
            val previous = reverse(element.siblings()).filterNotWhitespace().firstOrNull()
            return if (previous == null || previous.elementType !== XQueryTokenType.K_EXTERNAL) firstChild else element
        }

    // endregion
    // region XpmVariableDefinition

    override val variableName: XsQNameValue?
        get() = children().filter {
            it is XsQNameValue && it !is PluginCompatibilityAnnotation
        }.firstOrNull() as? XsQNameValue?

    // endregion
    // region XpmVariableDeclaration

    override val isExternal: Boolean
        get() = findChildByType<PsiElement>(XQueryTokenType.K_EXTERNAL) != null

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val variableExpression: XpmExpression?
        get() = children().filterIsInstance<XpmExpression>().firstOrNull()

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XPathIcons.Nodes.VarDecl

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        variableName?.let { name ->
            val type = variableType
            if (type == null)
                Optional.ofNullable(op_qname_presentation(name))
            else
                Optional.of("${op_qname_presentation(name)} as ${type.typeName}")
        } ?: Optional.empty()
    }.orElse(null)

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = computeUserDataIfAbsent(ALPHA_SORT_KEY) {
        variableName?.let { op_qname_presentation(it) } ?: ""
    }

    // endregion
}
