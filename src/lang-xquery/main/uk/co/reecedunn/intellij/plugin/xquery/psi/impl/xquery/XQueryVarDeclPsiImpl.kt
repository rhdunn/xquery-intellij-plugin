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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.filterNotWhitespace
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import javax.swing.Icon

class XQueryVarDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryVarDecl,
    XpmSyntaxValidationElement,
    ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
        cachedAlphaSortKey.invalidate()
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
    // region XPathVariableDeclaration

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XPathIcons.Nodes.VarDecl

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        variableName?.let { name ->
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
        variableName?.let { op_qname_presentation(it) } ?: ""
    }

    override fun getAlphaSortKey(): String = cachedAlphaSortKey.get()!!

    // endregion
}
