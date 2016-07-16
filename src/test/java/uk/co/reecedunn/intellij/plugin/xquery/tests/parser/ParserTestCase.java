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
import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.PlatformLiteFixture;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import uk.co.reecedunn.intellij.plugin.xquery.ast.impl.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.tests.mocks.MockFileViewProvider;

public abstract class ParserTestCase extends PlatformLiteFixture {
    // region IntelliJ Platform Infrastructure

    protected @Override boolean shouldContainTempFiles() {
        return false;
    }

    private <T> void registerApplicationService(final Class<T> aClass, T object) {
        getApplication().registerService(aClass, object);
        Disposer.register(myProject, () -> getApplication().getPicoContainer().unregisterComponent(aClass.getName()));
    }

    private <T> void addExplicitExtension(final LanguageExtension<T> instance, final Language language, final T object) {
        instance.addExplicitExtension(language, object);
        Disposer.register(myProject, () -> instance.removeExplicitExtension(language, object));
    }

    protected void setUp() {
        initApplication();
        ComponentAdapter component = getApplication().getPicoContainer().getComponentAdapter(ProgressManager.class.getName());
        if (component == null) {
            getApplication().getPicoContainer().registerComponent(new AbstractComponentAdapter(ProgressManager.class.getName(), Object.class) {
                @Override
                public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
                    return new ProgressManagerImpl();
                }

                @Override
                public void verify(PicoContainer container) throws PicoIntrospectionException {
                }
            });
        }

        Extensions.registerAreaClass("IDEA_PROJECT", null);
        myProject = new MockProjectEx(getTestRootDisposable());
        registerApplicationService(DefaultASTFactory.class, new DefaultASTFactoryImpl());
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery.INSTANCE, new XQueryASTFactory());
        addExplicitExtension(LanguageParserDefinitions.INSTANCE, XQuery.INSTANCE, new XQueryParserDefinition());
    }

    // endregion
    // region Parser Test Helpers

    private void buildPsi(@NotNull ParserDefinition parserDefinition, ASTNode node) {
        if (node instanceof CompositeElement) {
            CompositeElement element = (CompositeElement)node;
            if (node instanceof FileElement) {
                element.setPsi(parserDefinition.createFile(new MockFileViewProvider()));
            } else if (!(node instanceof PsiErrorElement)) {
                element.setPsi(parserDefinition.createElement(node));
            }
        }

        for (ASTNode child : node.getChildren(null)) {
            buildPsi(parserDefinition, child);
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
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet whitespaces = parserDefinition.getWhitespaceTokens();
        TokenSet comments = parserDefinition.getCommentTokens();
        Lexer lexer = parserDefinition.createLexer(null);

        PsiBuilder builder = new PsiBuilderImpl(null, null, whitespaces, comments, lexer, null, text, null, null);
        ASTNode node = parserDefinition.createParser(null).parse(parserDefinition.getFileNodeType(), builder);
        buildPsi(parserDefinition, node);
        return node;
    }

    // endregion
}
