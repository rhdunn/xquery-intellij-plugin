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
import com.intellij.util.Range
import com.intellij.util.containers.orNull
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import java.util.*
import javax.swing.Icon

class XQueryFunctionDeclPsiImpl(node: ASTNode) :
    XQueryAnnotatedDeclPsiImpl(node),
    XQueryFunctionDecl,
    ItemPresentationEx {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
        cachedStructurePresentableText.invalidate()
        cachedFunctionRefPresentableText.invalidate()
    }

    // endregion
    // region XdmFunctionDeclaration

    private val paramList: XPathParamList?
        get() = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val arity: Range<Int>
        get() = paramList?.arity ?: XpmFunctionDeclaration.ARITY_ZERO

    override val returnType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val parameters: List<XpmParameter>
        get() = paramList?.params ?: emptyList()

    override val paramListPresentation: ItemPresentation?
        get() = paramList?.presentation

    override val isVariadic: Boolean
        get() = paramList?.isVariadic == true

    override val functionRefPresentableText: String?
        get() = cachedFunctionRefPresentableText.get()

    private val cachedFunctionRefPresentableText = CacheableProperty {
        functionName?.let { "${op_qname_presentation(it)}#${arity.from}" } ?: ""
    }

    override val functionBody: XpmExpression?
        get() = children().filterIsInstance<XpmExpression>().firstOrNull()

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XpmFunctionDecorator.getIcon(this) ?: XPathIcons.Nodes.FunctionDecl

    override fun getLocationString(): String? = null

    // e.g. the documentation tool window title.
    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        val name = functionName
        name?.localName ?: return@computeUserDataIfAbsent Optional.empty()
        Optional.ofNullable(op_qname_presentation(name))
    }.orNull()

    // endregion
    // region ItemPresentationEx

    private val cachedStructurePresentableText = CacheableProperty {
        val name = functionName
        name?.localName ?: return@CacheableProperty null

        val returnType = returnType
        if (returnType == null)
            "${op_qname_presentation(name)}${paramList?.presentation?.presentableText ?: "()"}"
        else
            "${op_qname_presentation(name)}${
                paramList?.presentation?.presentableText ?: "()"
            } as ${returnType.typeName}"
    }

    override fun getPresentableText(type: ItemPresentationEx.Type): String? = when (type) {
        ItemPresentationEx.Type.StructureView -> cachedStructurePresentableText.get()
        ItemPresentationEx.Type.NavBarPopup -> cachedStructurePresentableText.get()
        else -> presentableText
    }

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = functionRefPresentableText ?: ""

    // endregion
}
