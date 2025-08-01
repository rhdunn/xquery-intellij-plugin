// Copyright (C) 2018, 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
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
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginAbbrevDescendantOrSelfStepPsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginWildcardIndicatorPsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.*

class XPathASTFactory : ASTFactory() {
    override fun createComposite(type: IElementType): CompositeElement = CompositeElement(type)

    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement = when (type) {
        XPathTokenType.ALL_DESCENDANTS_PATH -> PluginAbbrevDescendantOrSelfStepPsiImpl(type, text)
        XPathTokenType.COMMENT -> PsiCommentImpl(type, text)
        XPathTokenType.DECIMAL_LITERAL -> XPathDecimalLiteralImpl(type, text)
        XPathTokenType.DOT -> XPathContextItemExprPsiImpl(type, text)
        XPathTokenType.DOUBLE_LITERAL -> XPathDoubleLiteralImpl(type, text)
        XPathTokenType.ESCAPED_CHARACTER -> XPathEscapeCharacterImpl(type, text)
        XPathTokenType.INTEGER_LITERAL -> XPathIntegerLiteralImpl(type, text)
        XPathTokenType.PARENT_SELECTOR -> XPathAbbrevReverseStepPsiImpl(type, text)
        XPathTokenType.NCNAME -> XmlNCNameImpl(type, text)
        XPathTokenType.STAR -> PluginWildcardIndicatorPsiImpl(type, text)
        XPathTokenType.STRING_LITERAL_CONTENTS -> XPathStringLiteralContentsImpl(type, text)
        is IKeywordOrNCNameType -> XmlNCNameImpl(type, text)
        else -> LeafPsiElement(type, text)
    }
}
