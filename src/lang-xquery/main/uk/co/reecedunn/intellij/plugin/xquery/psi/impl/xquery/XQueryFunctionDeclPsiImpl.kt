/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import javax.swing.Icon

private val ARITY_ZERO = Range(0, 0)

class XQueryFunctionDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryFunctionDecl, ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
        cachedAlphaSortKey.invalidate()
    }

    // endregion
    // region XQueryFunctionDecl

    private val paramList get(): XPathParamList? = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName: XsQNameValue? = findChildByClass(XPathEQName::class.java) as? XsQNameValue

    override val arity get(): Range<Int> = paramList?.arity ?: ARITY_ZERO

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XQueryIcons.Nodes.FunctionDecl

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        val key = functionName?.let { "${op_qname_presentation(it)}#${arity.from}" }
        key `is` Cacheable
    }

    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
    // region SortableTreeElement

    private val cachedAlphaSortKey = CacheableProperty {
        val key = functionName?.let { "${op_qname_presentation(it)}#${arity.from}" } ?: ""
        key `is` Cacheable
    }

    override fun getAlphaSortKey(): String = cachedAlphaSortKey.get()!!

    // endregion
}
