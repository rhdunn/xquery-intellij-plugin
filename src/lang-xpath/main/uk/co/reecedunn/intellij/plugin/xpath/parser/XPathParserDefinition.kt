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

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.compat.lang.ParserDefinition
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathImpl

class XPathParserDefinition : ParserDefinition() {
    override fun createLexer(project: Project): Lexer = XPathLexer(CodePointRangeImpl())

    override fun createParser(project: Project): PsiParser = XPathParser()

    override fun getFileNodeType(): IFileElementType = XPathElementType.XPATH

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = COMMENT_TOKENS

    override fun getStringLiteralElements(): TokenSet = STRING_LITERAL_TOKENS

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType
        if (type is ICompositeElementType) {
            return type.createPsiElement(node)
        }

        throw AssertionError("Alien element type [$type]. Can't create XPath PsiElement for that.")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XPathImpl(viewProvider)

    companion object {
        val STRING_LITERAL_TOKENS = TokenSet.create(
            XPathTokenType.STRING_LITERAL_CONTENTS
        )

        val COMMENT_TOKENS = TokenSet.create(
            XPathTokenType.COMMENT
        )
    }
}
