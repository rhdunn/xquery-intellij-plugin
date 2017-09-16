/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVersionDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

private val STRINGS = TokenSet.create(XQueryElementType.STRING_LITERAL)

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuery.REC_3_0_20140408)

class XQueryVersionDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryVersionDecl, XQueryConformance {
    override val version get(): XQueryStringLiteral? = getStringValueAfterKeyword(XQueryTokenType.K_VERSION)

    override val encoding get(): XQueryStringLiteral? = getStringValueAfterKeyword(XQueryTokenType.K_ENCODING)

    override val requiresConformance get(): List<Version> =
        if (conformanceElement === firstChild) XQUERY10 else XQUERY30

    override val conformanceElement get(): PsiElement {
        val encoding = node.findChildByType(XQueryTokenType.K_ENCODING) ?: return firstChild

        var previous = encoding.treePrev
        while (previous.elementType === XQueryTokenType.WHITE_SPACE || previous.elementType === XQueryElementType.COMMENT) {
            previous = previous.treePrev
        }

        return if (previous.elementType === XQueryTokenType.K_XQUERY) encoding.psi else firstChild
    }

    private fun getStringValueAfterKeyword(type: IXQueryKeywordOrNCNameType): XQueryStringLiteral? {
        for (child in node.getChildren(STRINGS)) {
            var previous = child.treePrev
            while (previous.elementType === XQueryTokenType.WHITE_SPACE || previous.elementType === XQueryElementType.COMMENT) {
                previous = previous.treePrev
            }

            if (previous.elementType === type) {
                return child.psi as XQueryStringLiteral
            } else if (previous is PsiErrorElementImpl) {
                if (previous.firstChildNode.elementType === type) {
                    return child.psi as XQueryStringLiteral
                }
            }
        }
        return null
    }
}
