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
package uk.co.reecedunn.intellij.plugin.xquery.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.*;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

public class XQueryParserDefinition implements ParserDefinition {
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new XQueryLexer();
    }

    @Override
    public PsiParser createParser(@NotNull Project project) {
        final XQueryProjectSettings settings = XQueryProjectSettings.getInstance(project);
        return new XQueryPsiParser(settings);
    }

    @Override
    public IFileElementType getFileNodeType() {
        return XQueryElementType.FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return XQueryTokenType.COMMENT_TOKENS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return XQueryTokenType.STRING_LITERAL_TOKENS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        final IElementType type = node.getElementType();
        if (type == XQueryElementType.STRING_LITERAL) {
            return new XQueryStringLiteralPsiImpl(node);
        } else if (type == XQueryElementType.URI_LITERAL) {
            return new XQueryUriLiteralPsiImpl(node);
        } else if (type == XQueryElementType.QNAME) {
            return new XQueryQNamePsiImpl(node);
        } else if (type == XQueryElementType.COMMENT) {
            return new XQueryCommentPsiImpl(node);
        } else if (type == XQueryElementType.DIR_COMMENT_CONSTRUCTOR) {
            return new XQueryDirCommentConstructorPsiImpl(node);
        } else if (type == XQueryElementType.CDATA_SECTION) {
            return new XQueryCDataSectionPsiImpl(node);
        } else if (type == XQueryElementType.VERSION_DECL) {
            return new XQueryVersionDeclPsiImpl(node);
        } else if (type == XQueryElementType.MODULE_DECL) {
            return new XQueryModuleDeclPsiImpl(node);
        } else if (type == XQueryElementType.MODULE_IMPORT) {
            return new XQueryModuleImportPsiImpl(node);
        }
        throw new AssertionError("Alien element type [" + type + "]. Can't create XQuery PsiElement for that.");
    }

    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new XQueryFileImpl(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
