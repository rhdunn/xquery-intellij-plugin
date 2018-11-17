/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.parser

import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathDecimalLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathDoubleLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathEscapeCharacterImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathIntegerLiteralImpl
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryDirWhiteSpaceImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryCharRefImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryPredefinedEntityRefImpl

class XQueryASTFactory : ASTFactory() {
    override fun createComposite(type: IElementType): CompositeElement? {
        return CompositeElement(type)
    }

    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? {
        if (type === XQueryTokenType.XML_WHITE_SPACE) {
            return XQueryDirWhiteSpaceImpl(text)
        } else if (type === XQueryTokenType.COMMENT || type === XQueryTokenType.XML_COMMENT) {
            return PsiCommentImpl(type, text)
        } else if (type === XQueryTokenType.INTEGER_LITERAL) {
            return XPathIntegerLiteralImpl(type, text)
        } else if (type === XQueryTokenType.DECIMAL_LITERAL) {
            return XPathDecimalLiteralImpl(type, text)
        } else if (type === XQueryTokenType.DOUBLE_LITERAL) {
            return XPathDoubleLiteralImpl(type, text)
        } else if (type === XQueryTokenType.PREDEFINED_ENTITY_REFERENCE || type === XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE) {
            return XQueryPredefinedEntityRefImpl(type, text)
        } else if (type === XQueryTokenType.CHARACTER_REFERENCE || type === XQueryTokenType.XML_CHARACTER_REFERENCE) {
            return XQueryCharRefImpl(type, text)
        } else if (type === XQueryTokenType.ESCAPED_CHARACTER || type === XQueryTokenType.XML_ESCAPED_CHARACTER) {
            return XPathEscapeCharacterImpl(type, text)
        } else if (type === XQueryTokenType.NCNAME ||
                type === XQueryTokenType.XML_TAG_NCNAME ||
                type === XQueryTokenType.XML_ATTRIBUTE_NCNAME ||
                type is IKeywordOrNCNameType
        ) {
            return XmlNCNameImpl(type, text)
        }

        return LeafPsiElement(type, text)
    }
}
