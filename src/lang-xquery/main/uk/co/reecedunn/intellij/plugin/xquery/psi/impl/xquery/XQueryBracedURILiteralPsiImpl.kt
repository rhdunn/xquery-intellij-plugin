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
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryBracedURILiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathBracedURILiteral,
    XpmModulePath,
    XpmSyntaxValidationElement {
    companion object {
        private val DATA = Key.create<String>("DATA")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(DATA)
    }

    // endregion
    // region XsAnyUriValue

    override val data: String
        get() = computeUserDataIfAbsent(DATA) {
            children().map { child ->
                when (child.elementType) {
                    XPathTokenType.BRACED_URI_LITERAL_START, XPathTokenType.BRACED_URI_LITERAL_END ->
                        null
                    XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ->
                        (child as XQueryPredefinedEntityRef).entityRef.value
                    XQueryTokenType.CHARACTER_REFERENCE ->
                        (child as XQueryCharRef).codepoint.toString()
                    else ->
                        child.text
                }
            }.filterNotNull().joinToString(separator = "")
        }

    override val context: XdmUriContext = XdmUriContext.Namespace

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.MODULE_OR_SCHEMA

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
