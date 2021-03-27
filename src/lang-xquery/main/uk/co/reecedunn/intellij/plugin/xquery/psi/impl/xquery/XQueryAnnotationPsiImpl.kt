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
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNumericLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import java.util.*
import javax.swing.Icon

class XQueryAnnotationPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryAnnotation,
    XpmSyntaxValidationElement,
    ItemPresentation {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
    }

    // endregion
    // region XdmAnnotation

    override val name: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val values: Sequence<XsAnyAtomicType>
        get() = children().map {
            when (it) {
                is XPathNumericLiteral -> it as XsAnyAtomicType
                is XPathStringLiteral -> it
                else -> null
            }
        }.filterNotNull()

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XQueryTokenType.ANNOTATION_INDICATOR) ?: firstChild

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XQueryIcons.Nodes.Annotation

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        val values = values.joinToString { (it as PsiElement).text }
        name?.let {
            if (values.isEmpty())
                Optional.of("%${op_qname_presentation(it)}")
            else
                Optional.of("%${op_qname_presentation(it)}($values)")
        } ?: Optional.empty()
    }.orElse(null)

    // endregion
}
