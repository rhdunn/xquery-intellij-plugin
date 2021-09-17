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
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.blockOpen
import uk.co.reecedunn.intellij.plugin.xpath.psi.isEmptyEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import java.util.*
import javax.swing.Icon

class XQueryFunctionDeclPsiImpl(node: ASTNode) :
    XQueryAnnotatedDeclPsiImpl(node),
    XQueryFunctionDecl,
    ItemPresentationEx,
    XpmSyntaxValidationElement {
    companion object {
        private val PARAMETERS = Key.create<List<XpmParameter>>("PARAMETERS")
        private val VARIADIC_TYPE = Key.create<XpmVariadic>("VARIADIC_TYPE")
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
        private val STRUCTURE_PRESENTABLE_TEXT = Key.create<Optional<String>>("STRUCTURE_PRESENTABLE_TEXT")
        private val PARAM_LIST_PRESENTABLE_TEXT = Key.create<String>("PARAM_LIST_PRESENTABLE_TEXT")
        private val FUNCTION_REF_PRESENTABLE_TEXT = Key.create<String>("FUNCTION_REF_PRESENTABLE_TEXT")

        private val PARAM_OR_VARIADIC = TokenSet.create(
            XPathElementType.PARAM,
            XPathTokenType.ELLIPSIS
        )
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PARAMETERS)
        clearUserData(VARIADIC_TYPE)
        clearUserData(PRESENTABLE_TEXT)
        clearUserData(STRUCTURE_PRESENTABLE_TEXT)
        clearUserData(PARAM_LIST_PRESENTABLE_TEXT)
        clearUserData(FUNCTION_REF_PRESENTABLE_TEXT)
    }

    // endregion
    // region XdmFunctionDeclaration (Data Model)

    override val functionName: XsQNameValue?
        get() = children().filter {
            it is XsQNameValue && it !is PluginCompatibilityAnnotation
        }.firstOrNull() as? XsQNameValue?

    override val parameters: List<XpmParameter>
        get() = computeUserDataIfAbsent(PARAMETERS) {
            children().filterIsInstance<XPathParam>().toList()
        }

    override val returnType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val functionBody: XpmExpression?
        get() = when (blockOpen) {
            null -> null // external function declaration
            else -> children().filterIsInstance<XpmExpression>().firstOrNull() ?: XpmEmptyExpression
        }

    // endregion
    // region XdmFunctionDeclaration (Variadic Type and Arity)

    private val variadicParameter: PsiElement?
        get() = reverse(children()).firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.elementType) }

    override val variadicType: XpmVariadic
        get() = computeUserDataIfAbsent(VARIADIC_TYPE) {
            val variadicParameter = variadicParameter
            when (variadicParameter.elementType) {
                XPathTokenType.ELLIPSIS -> XpmVariadic.Ellipsis
                else -> XpmVariadic.No
            }
        }

    override val declaredArity: Int
        get() = parameters.size

    private val defaultArgumentCount: Int
        get() = when (variadicType) {
            XpmVariadic.Ellipsis -> 1
            else -> 0
        }

    override val requiredArity: Int
        get() = declaredArity - defaultArgumentCount

    // endregion
    // region XdmFunctionDeclaration (Presentation)

    override val paramListPresentableText: String
        get() = computeUserDataIfAbsent(PARAM_LIST_PRESENTABLE_TEXT) {
            val params = parameters.mapNotNull { param ->
                (param as NavigationItem).presentation?.presentableText
            }.joinToString()
            if (variadicType === XpmVariadic.Ellipsis) "($params ...)" else "($params)"
        }

    override val functionRefPresentableText: String?
        get() = computeUserDataIfAbsent(FUNCTION_REF_PRESENTABLE_TEXT) {
            functionName?.let { "${qname_presentation(it)}#$declaredArity" } ?: ""
        }

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
        Optional.ofNullable(qname_presentation(name))
    }.orElse(null)

    // endregion
    // region ItemPresentationEx

    private val structurePresentableText: String?
        get() = computeUserDataIfAbsent(STRUCTURE_PRESENTABLE_TEXT) {
            val name = functionName
            name?.localName ?: return@computeUserDataIfAbsent Optional.empty()

            val returnType = returnType
            if (returnType == null)
                Optional.of("${qname_presentation(name)}$paramListPresentableText")
            else
                Optional.of("${qname_presentation(name)}$paramListPresentableText as ${returnType.typeName}")
        }.orElse(null)

    override fun getPresentableText(type: ItemPresentationEx.Type): String? = when (type) {
        ItemPresentationEx.Type.StructureView -> structurePresentableText
        ItemPresentationEx.Type.NavBarPopup -> structurePresentableText
        else -> presentableText
    }

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = functionRefPresentableText ?: ""

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() {
            val blockOpen = blockOpen
            return when (blockOpen?.isEmptyEnclosedExpr) {
                true -> blockOpen
                else -> variadicParameter ?: firstChild
            }
        }

    // endregion
}
