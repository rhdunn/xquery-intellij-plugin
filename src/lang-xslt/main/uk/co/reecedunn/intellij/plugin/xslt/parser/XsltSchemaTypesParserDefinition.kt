/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.parser

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
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XsltSchemaTypes
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.schema.XsltSchemaTypePsiImpl

class XsltSchemaTypesParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = XPathLexer(XmlCodePointRangeImpl())

    override fun createParser(project: Project): PsiParser = createParser(XsltSchemaTypes.Expression)

    fun createParser(schemaType: ISchemaType): PsiParser = XsltSchemaTypesParser(schemaType)

    override fun getFileNodeType(): IFileElementType = XPathElementType.XPATH

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = XPathTokenType.COMMENT_TOKENS

    override fun getStringLiteralElements(): TokenSet = XPathTokenType.STRING_LITERAL_TOKENS

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType
        if (type is ICompositeElementType) {
            return type.createPsiElement(node)
        }

        throw AssertionError("Alien element type [$type]. Can't create XsltSchemaTypes PsiElement for that.")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XsltSchemaTypePsiImpl(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        val leftType = left?.elementType ?: return ParserDefinition.SpaceRequirements.MAY
        val rightType = right?.elementType ?: return ParserDefinition.SpaceRequirements.MAY
        return spaceRequirements(leftType, rightType)
    }

    fun spaceRequirements(left: IElementType, right: IElementType): ParserDefinition.SpaceRequirements {
        return XPathParserDefinition.spaceRequirements(left, right)
    }
}
