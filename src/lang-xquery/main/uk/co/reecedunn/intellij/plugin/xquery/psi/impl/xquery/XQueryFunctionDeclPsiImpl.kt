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
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import javax.swing.Icon

class XQueryFunctionDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQueryFunctionDecl, XdmFunctionDeclaration, ItemPresentationEx {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
        cachedStructurePresentableText.invalidate()
        cachedFunctionRefPresentableText.invalidate()
    }

    // endregion
    // region XdmFunctionDeclaration

    private val paramList get(): XPathParamList? = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName get(): XsQNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val arity get(): Range<Int> = paramList?.arity ?: XdmFunctionDeclaration.ARITY_ZERO

    override val returnType get(): XdmSequenceType? = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val params get(): List<XdmVariableBinding> = paramList?.params ?: emptyList()

    override val paramListPresentation get(): ItemPresentation? = paramList?.presentation

    override val isVariadic get(): Boolean = paramList?.isVariadic == true

    override val functionRefPresentableText: String? get() = cachedFunctionRefPresentableText.get()

    override val annotations: Sequence<XdmAnnotation> get() = parent.children().filterIsInstance<XdmAnnotation>()

    private val cachedFunctionRefPresentableText = CacheableProperty {
        functionName?.let { "${op_qname_presentation(it)}#${arity.from}" } ?: ""
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? {
        return XpmFunctionDecorator.getIcon(this) ?: XPathIcons.Nodes.FunctionDecl
    }

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        val name = functionName
        name?.localName ?: return@CacheableProperty null
        op_qname_presentation(name)
    }

    // e.g. the documentation tool window title.
    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
    // region ItemPresentationEx

    private val cachedStructurePresentableText = CacheableProperty {
        val name = functionName
        name?.localName ?: return@CacheableProperty null

        val returnType = returnType
        if (returnType == null)
            "${op_qname_presentation(name)}${paramList?.presentation?.presentableText ?: "()"}"
        else
            "${op_qname_presentation(name)}${paramList?.presentation?.presentableText
                ?: "()"} as ${returnType.typeName}"
    }

    override val structurePresentableText: String? get() = cachedStructurePresentableText.get()

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = functionRefPresentableText ?: ""

    // endregion
}
