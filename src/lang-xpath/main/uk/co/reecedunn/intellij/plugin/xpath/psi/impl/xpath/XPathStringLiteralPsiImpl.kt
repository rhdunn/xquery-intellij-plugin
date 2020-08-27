/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xdm.types.XsStringValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathStringLiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathStringLiteral, XsStringValue {
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedData.invalidate()
    }

    // endregion
    // region XsStringValue

    override val data: String get() = cachedData.get()!!

    private val cachedData: CacheableProperty<String> = CacheableProperty {
        children().map { child ->
            when (child.elementType) {
                XPathTokenType.STRING_LITERAL_START, XPathTokenType.STRING_LITERAL_END ->
                    null
                XPathTokenType.ESCAPED_CHARACTER ->
                    (child as XPathEscapeCharacter).unescapedCharacter.toString()
                else ->
                    child.text
            }
        }.filterNotNull().joinToString(separator = "")
    }

    // endregion
}
