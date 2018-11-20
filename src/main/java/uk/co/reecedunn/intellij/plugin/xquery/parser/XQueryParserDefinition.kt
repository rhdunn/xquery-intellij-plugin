/*
 * Copyright (C) 2016; 2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryModuleImpl

class XQueryParserDefinition : ParserDefinition() {
    override fun createLexer(project: Project): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    override fun createParser(project: Project): PsiParser = XQueryParser()

    override fun getFileNodeType(): IFileElementType = XQueryElementType.MODULE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = COMMENT_TOKENS

    override fun getStringLiteralElements(): TokenSet = STRING_LITERAL_TOKENS

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType
        if (type is ICompositeElementType) {
            return type.createPsiElement(node)
        }

        throw AssertionError("Alien element type [$type]. Can't create XQuery PsiElement for that.")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XQueryModuleImpl(viewProvider)
}
