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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.impl.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public abstract class ParserTestCase extends ParsingTestCase {
    public ParserTestCase() {
        super("", ".xqy", new XQueryParserDefinition());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerApplicationService(XQueryProjectSettings.class, new XQueryProjectSettings());
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery.INSTANCE, new XQueryASTFactory());
    }

    // region Parser Test Helpers

    public void initializeSettings(XQueryProjectSettings settings) {
    }

    public XQueryProjectSettings getSettings() {
        return XQueryProjectSettings.getInstance(myProject);
    }

    public String loadResource(String resource) {
        ClassLoader loader = ParserTestCase.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream(resource);
        StringWriter writer = new StringWriter();
        try {
            IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        } catch (Exception e) {
            //
        }
        return writer.toString();
    }

    public LightVirtualFile createVirtualFile(@NonNls String name, String text) {
        LightVirtualFile file = new LightVirtualFile(name, myLanguage, text);
        file.setCharset(CharsetToolkit.UTF8_CHARSET);
        return file;
    }

    public FileViewProvider getFileViewProvider(@NotNull Project project, LightVirtualFile file, boolean physical) {
        final PsiManager manager = PsiManager.getInstance(project);
        final FileViewProviderFactory factory = LanguageFileViewProviders.INSTANCE.forLanguage(XQuery.INSTANCE);
        FileViewProvider viewProvider = factory != null ? factory.createFileViewProvider(file, XQuery.INSTANCE, manager, physical) : null;
        if (viewProvider == null) viewProvider = new SingleRootFileViewProvider(manager, file, physical);
        return viewProvider;
    }

    private void buildPsi(@NotNull ParserDefinition parserDefinition, ASTNode node, String text) {
        if (node instanceof CompositeElement) {
            CompositeElement element = (CompositeElement)node;
            if (node instanceof FileElement) {
                element.setPsi(createFile("testcase.xqy", text));
            } else if (!(node instanceof PsiErrorElement)) {
                element.setPsi(parserDefinition.createElement(node));
            }
        }

        for (ASTNode child : node.getChildren(null)) {
            buildPsi(parserDefinition, child, text);
        }
    }

    private void prettyPrintASTNode(@NotNull StringBuilder prettyPrinted, ASTNode node, int depth) {
        for (int i = 0; i != depth; ++i) {
            prettyPrinted.append("   ");
        }

        String[] names = node.getClass().getName().split("\\.");
        prettyPrinted.append(names[names.length - 1]);
        prettyPrinted.append('[');
        prettyPrinted.append(node.getElementType());
        prettyPrinted.append('(');
        prettyPrinted.append(node.getTextRange().getStartOffset());
        prettyPrinted.append(':');
        prettyPrinted.append(node.getTextRange().getEndOffset());
        prettyPrinted.append(')');
        prettyPrinted.append(']');
        if ((node instanceof LeafElement) || (node instanceof PsiErrorElement)) {
            prettyPrinted.append('(');
            prettyPrinted.append('\'');
            if (node instanceof PsiErrorElement) {
                PsiErrorElement error = (PsiErrorElement)node;
                prettyPrinted.append(error.getErrorDescription());
            } else {
                prettyPrinted.append(node.getText());
            }
            prettyPrinted.append('\'');
            prettyPrinted.append(')');
        }
        prettyPrinted.append('\n');

        for (ASTNode child : node.getChildren(null)) {
            prettyPrintASTNode(prettyPrinted, child, depth + 1);
        }
    }

    public String prettyPrintASTNode(ASTNode node) {
        StringBuilder prettyPrinted = new StringBuilder();
        prettyPrintASTNode(prettyPrinted, node, 0);
        return prettyPrinted.toString();
    }

    public @NotNull ASTNode parseText(@NotNull String text) {
        initializeSettings(getSettings());

        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet whitespaces = parserDefinition.getWhitespaceTokens();
        TokenSet comments = parserDefinition.getCommentTokens();
        Lexer lexer = parserDefinition.createLexer(myProject);

        PsiBuilder builder = new PsiBuilderImpl(myProject, null, whitespaces, comments, lexer, null, text, null, null);
        ASTNode node = parserDefinition.createParser(myProject).parse(parserDefinition.getFileNodeType(), builder);
        buildPsi(parserDefinition, node, text);
        return node;
    }

    public @NotNull ASTNode parseResource(String resource) {
        return parseText(loadResource(resource));
    }

    // endregion
}
