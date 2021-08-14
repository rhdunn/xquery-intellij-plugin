/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.parser.IASTWrapperElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathImpl

open class XPathParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = XPathLexer()

    override fun createParser(project: Project): PsiParser = XPathParser()

    override fun getFileNodeType(): IFileElementType = XPathElementType.XPATH

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = XPathTokenType.COMMENT_TOKENS

    override fun getStringLiteralElements(): TokenSet = XPathTokenType.STRING_LITERAL_TOKENS

    override fun createElement(node: ASTNode): PsiElement = when (val type = node.elementType) {
        is IASTWrapperElementType -> type.createPsiElement(node)
        else -> throw AssertionError("Alien element type [$type]. Can't create PsiElement for that.")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XPathImpl(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        val leftType = left?.elementType ?: return ParserDefinition.SpaceRequirements.MAY
        val rightType = right?.elementType ?: return ParserDefinition.SpaceRequirements.MAY
        return spaceRequirements(leftType, rightType)
    }

    companion object {
        fun spaceRequirements(left: IElementType, right: IElementType): ParserDefinition.SpaceRequirements = when {
            isNonDelimiting(left) && isNonDelimiting(right) -> ParserDefinition.SpaceRequirements.MUST
            left is INCNameType && isNCNamePart(right) -> ParserDefinition.SpaceRequirements.MUST
            isNumericLiteral(left) && right === XPathTokenType.DOT -> ParserDefinition.SpaceRequirements.MUST
            left === XPathTokenType.DOT && isNumericLiteral(right) -> ParserDefinition.SpaceRequirements.MUST
            else -> ParserDefinition.SpaceRequirements.MAY
        }

        private fun isNonDelimiting(symbol: IElementType): Boolean = when (symbol) {
            is INCNameType -> true
            XPathTokenType.INTEGER_LITERAL -> true
            XPathTokenType.DECIMAL_LITERAL -> true
            XPathTokenType.DOUBLE_LITERAL -> true
            else -> false
        }

        private fun isNCNamePart(symbol: IElementType): Boolean = when (symbol) {
            XPathTokenType.DOT -> true
            XPathTokenType.MINUS -> true
            else -> false
        }

        private fun isNumericLiteral(symbol: IElementType): Boolean = when (symbol) {
            XPathTokenType.INTEGER_LITERAL -> true
            XPathTokenType.DECIMAL_LITERAL -> true
            XPathTokenType.DOUBLE_LITERAL -> true
            else -> false
        }
    }
}
