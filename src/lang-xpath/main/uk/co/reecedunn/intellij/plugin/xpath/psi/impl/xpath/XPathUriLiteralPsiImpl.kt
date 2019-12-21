/*
 * Copyright (C) 2019 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathUriLiteralPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathUriLiteral, XsAnyUriValue {
    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedData.invalidate()
    }

    override val element: PsiElement? get() = this

    override val context: XdmUriContext
        get() = when (parent) {
            is FTStopWords -> XdmUriContext.StopWords
            is FTThesaurusID -> XdmUriContext.Thesaurus
            else -> XdmUriContext.Namespace
        }

    override val data: String get() = cachedData.get()!!

    private val cachedData: CacheableProperty<String> = CacheableProperty {
        children().map { child ->
            when (child.node.elementType) {
                XPathTokenType.STRING_LITERAL_START, XPathTokenType.STRING_LITERAL_END ->
                    null
                XPathTokenType.ESCAPED_CHARACTER ->
                    (child as XPathEscapeCharacter).unescapedValue
                else ->
                    child.text
            }
        }.filterNotNull().joinToString(separator = "")
    }
}
