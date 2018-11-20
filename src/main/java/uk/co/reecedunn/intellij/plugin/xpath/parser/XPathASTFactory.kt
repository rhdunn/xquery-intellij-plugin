/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathDecimalLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathDoubleLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathIntegerLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathEscapeCharacterImpl

class XPathASTFactory : ASTFactory() {
    override fun createComposite(type: IElementType): CompositeElement? = CompositeElement(type)

    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? {
        return when (type) {
            XPathTokenType.INTEGER_LITERAL -> XPathIntegerLiteralImpl(type, text)
            XPathTokenType.DECIMAL_LITERAL -> XPathDecimalLiteralImpl(type, text)
            XPathTokenType.DOUBLE_LITERAL -> XPathDoubleLiteralImpl(type, text)
            XPathTokenType.ESCAPED_CHARACTER -> XPathEscapeCharacterImpl(type, text)
            XPathTokenType.COMMENT -> PsiCommentImpl(type, text)
            XPathTokenType.NCNAME -> XmlNCNameImpl(type, text)
            is IKeywordOrNCNameType -> XmlNCNameImpl(type, text)
            else -> LeafPsiElement(type, text)
        }
    }
}
