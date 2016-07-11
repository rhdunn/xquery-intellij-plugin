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
import com.intellij.openapi.util.Pair;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.PlatformLiteFixture;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryParserTest extends PlatformLiteFixture {
    // region IntelliJ Platform Infrastructure

    protected @Override boolean shouldContainTempFiles() {
        return false;
    }

    private <T> void registerApplicationService(final Class<T> aClass, T object) {
        getApplication().registerService(aClass, object);
        Disposer.register(myProject, () -> getApplication().getPicoContainer().unregisterComponent(aClass.getName()));
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
    }

    // endregion
    // region Parser Test Helpers

    private void serializeASTNode(@NotNull List<Pair<Integer, ASTNode>> nodes, ASTNode node, int depth) {
        nodes.add(Pair.create(depth, node));
        for (ASTNode child : node.getChildren(null)) {
            serializeASTNode(nodes, child, depth + 1);
        }
    }

    private @NotNull List<Pair<Integer, ASTNode>> parseText(@NotNull String text, int size) {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet whitespaces = parserDefinition.getWhitespaceTokens();
        TokenSet comments = parserDefinition.getCommentTokens();
        Lexer lexer = parserDefinition.createLexer(null);
        PsiBuilder builder = new PsiBuilderImpl(null, null, whitespaces, comments, lexer, null, text, null, null);

        ASTNode node = parserDefinition.createParser(null).parse(parserDefinition.getFileNodeType(), builder);
        List<Pair<Integer, ASTNode>> nodes = new ArrayList<>();
        serializeASTNode(nodes, node, 0);
        assertThat(nodes.size(), is(size));
        matchASTNode(nodes.get(0), 0, FileElement.class, XQueryElementType.FILE, 0, text.length(), text);
        return nodes;
    }

    private void matchASTNode(@NotNull final Pair<Integer, ASTNode> node, int depth, Class nodeClass, IElementType type, int start, int end, String text) {
        assertThat(node.getFirst(), is(depth));

        assertThat(node.getSecond().getText(), is(text));
        assertThat(node.getSecond().getElementType(), is(type));
        assertThat(node.getSecond().getClass().getName(), is(nodeClass.getName()));

        assertThat(node.getSecond().getTextRange().getStartOffset(), is(start));
        assertThat(node.getSecond().getTextRange().getEndOffset(), is(end));
    }

    // endregion

    public void testEmptyBuffer() {
        parseText("", 1);
    }

    public void testBadCharacters() {
        final List<Pair<Integer, ASTNode>> nodes = parseText("~\uFFFE\uFFFF", 4);
        matchASTNode(nodes.get(1), 1, LeafPsiElement.class, XQueryTokenType.BAD_CHARACTER, 0, 1, "~");
        matchASTNode(nodes.get(2), 1, LeafPsiElement.class, XQueryTokenType.BAD_CHARACTER, 1, 2, "\uFFFE");
        matchASTNode(nodes.get(3), 1, LeafPsiElement.class, XQueryTokenType.BAD_CHARACTER, 2, 3, "\uFFFF");
    }
}
