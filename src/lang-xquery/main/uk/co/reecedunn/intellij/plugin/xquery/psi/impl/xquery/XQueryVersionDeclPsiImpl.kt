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
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xdm.types.XsStringValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVersionDecl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryVersionDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryVersionDecl,
    XpmSyntaxValidationElement {
    // region XQueryVersionDecl

    override val version: XsStringValue?
        get() = getStringValueAfterKeyword(XQueryTokenType.K_VERSION)

    override val encoding: XsStringValue?
        get() = getStringValueAfterKeyword(XQueryTokenType.K_ENCODING)

    private fun getStringValueAfterKeyword(type: IKeywordOrNCNameType): XsStringValue? {
        for (child in node.getChildren(STRINGS)) {
            var previous = child.treePrev
            while (previous.elementType === XPathTokenType.WHITE_SPACE || previous.psi is XPathComment) {
                previous = previous.treePrev
            }

            if (previous.elementType === type) {
                return child.psi as XsStringValue
            } else if (previous is PsiErrorElementImpl) {
                if (previous.firstChildNode.elementType === type) {
                    return child.psi as XsStringValue
                }
            }
        }
        return null
    }

    companion object {
        private val STRINGS = TokenSet.create(XPathElementType.STRING_LITERAL)
    }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() {
            val encoding = node.findChildByType(XQueryTokenType.K_ENCODING) ?: return firstChild

            var previous = encoding.treePrev
            while (previous.elementType === XPathTokenType.WHITE_SPACE || previous.psi is XPathComment) {
                previous = previous.treePrev
            }

            return if (previous.elementType === XQueryTokenType.K_XQUERY) encoding.psi else firstChild
        }

    // endregion
}
