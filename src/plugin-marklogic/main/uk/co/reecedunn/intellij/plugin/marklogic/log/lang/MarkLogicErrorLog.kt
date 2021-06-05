/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer
import uk.co.reecedunn.intellij.plugin.marklogic.log.parser.MarkLogicErrorLogElementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.parser.MarkLogicErrorLogParser
import uk.co.reecedunn.intellij.plugin.marklogic.log.psi.impl.error.MarkLogicErrorLogPsiImpl
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import com.intellij.lang.ParserDefinition as LanguageParserDefinition

object MarkLogicErrorLog : Language("MLErrorLog") {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = MarkLogicBundle.message("language.error-log.display-name")

    // endregion
    // region Parser Definition

    class ParserDefinition : LanguageParserDefinition {
        override fun createLexer(project: Project): Lexer = MarkLogicErrorLogLexer(MarkLogicErrorLogFormat.MARKLOGIC_9)

        override fun createParser(project: Project): PsiParser = MarkLogicErrorLogParser()

        override fun getFileNodeType(): IFileElementType = MarkLogicErrorLogElementType.ERROR_LOG

        override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

        override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

        override fun createElement(node: ASTNode): PsiElement {
            val type = node.elementType
            if (type is ICompositeElementType) {
                return type.createPsiElement(node)
            }

            throw AssertionError("Alien element type [$type]. Can't create PsiElement for that.")
        }

        override fun createFile(viewProvider: FileViewProvider): PsiFile = MarkLogicErrorLogPsiImpl(viewProvider)
    }

    // endregion
}
