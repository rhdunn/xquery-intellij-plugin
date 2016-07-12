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
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.PlatformLiteFixture;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.psi.imp.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

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
    }

    // endregion
    // region Parser Test Helpers

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
        if (!node.getClass().equals(FileElement.class)) {
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

    private @NotNull String parseText(@NotNull String text) {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet whitespaces = parserDefinition.getWhitespaceTokens();
        TokenSet comments = parserDefinition.getCommentTokens();
        Lexer lexer = parserDefinition.createLexer(null);
        PsiBuilder builder = new PsiBuilderImpl(null, null, whitespaces, comments, lexer, null, text, null, null);

        ASTNode node = parserDefinition.createParser(null).parse(parserDefinition.getFileNodeType(), builder);
        StringBuilder prettyPrinted = new StringBuilder();
        prettyPrintASTNode(prettyPrinted, node, 0);
        return prettyPrinted.toString();
    }

    // endregion
    // region Basic Parser Tests

    public void testEmptyBuffer() {
        final String expected
                = "FileElement[FILE(0:0)]\n";

        assertThat(parseText(""), is(expected));
    }

    public void testBadCharacters() {
        final String expected
                = "FileElement[FILE(0:3)]\n"
                + "   LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n"
                + "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n"
                + "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n";

        assertThat(parseText("~\uFFFE\uFFFF"), is(expected));
    }

    // endregion
    // region A.2.1 Terminal Symbols

    // region IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   LeafPsiElement[XQUERY_INTEGER_LITERAL_TOKEN(0:4)]('1234')\n";

        assertThat(parseText("1234"), is(expected));
    }

    // endregion
    // region DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
    public void testDecimalLiteral() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   LeafPsiElement[XQUERY_DECIMAL_LITERAL_TOKEN(0:7)]('3.14159')\n";

        assertThat(parseText("3.14159"), is(expected));
    }

    // endregion
    // region DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral() {
        final String expected
                = "FileElement[FILE(0:12)]\n"
                + "   LeafPsiElement[XQUERY_DOUBLE_LITERAL_TOKEN(0:12)]('2.99792458e8')\n";

        assertThat(parseText("2.99792458e8"), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral_IncompleteExponent() {
        final String expected
                = "FileElement[FILE(0:11)]\n"
                + "   LeafPsiElement[XQUERY_DECIMAL_LITERAL_TOKEN(0:10)]('2.99792458')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(10:11)]('Incomplete double exponent.')\n"
                + "      LeafPsiElement[XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN(10:11)]('e')\n";

        assertThat(parseText("2.99792458e"), is(expected));
    }

    // endregion
    // region Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        final String expected
                = "FileElement[FILE(0:10)]\n"
                + "   PsiCommentImpl[XQUERY_COMMENT_TOKEN(0:10)]('(: Test :)')\n";

        assertThat(parseText("(: Test :)"), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnclosedComment() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   PsiCommentImpl[XQUERY_PARTIAL_COMMENT_TOKEN(0:7)]('(: Test')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(7:7)]('Unclosed XQuery comment.')\n";

        assertThat(parseText("(: Test"), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnexpectedCommentEndTag() {
        final String expected
                = "FileElement[FILE(0:2)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('End of XQuery comment marker found without a '(:' start of comment marker.')\n"
                + "      LeafPsiElement[XQUERY_COMMENT_END_TAG_TOKEN(0:2)](':)')\n";

        assertThat(parseText(":)"), is(expected));
    }

    // endregion
    // region S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   PsiWhiteSpaceImpl[WHITE_SPACE(0:4)](' \t\r\n')\n";

        assertThat(parseText(" \t\r\n"), is(expected));
    }

    // endregion

    // endregion
}
