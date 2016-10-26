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

import com.intellij.lang.ParserDefinition;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.LightVirtualFile;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryPsiParser;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryFileImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.mocks.MockASTNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

public class XQueryParserDefinitionTest extends ParserTestCase {
    public void testLexer() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        assertThat(parserDefinition.createLexer(myProject).getClass().getName(), is(XQueryLexer.class.getName()));
    }

    public void testParser() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        assertThat(parserDefinition.createParser(myProject).getClass().getName(), is(XQueryPsiParser.class.getName()));
    }

    public void testFileNodeType() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        assertThat(parserDefinition.getFileNodeType(), is(XQueryElementType.FILE));
    }

    public void testWhitespaceTokens() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet tokens = parserDefinition.getWhitespaceTokens();
        assertThat(tokens.getTypes().length, is(0));
    }

    public void testCommentTokens() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet tokens = parserDefinition.getCommentTokens();
        assertThat(tokens.getTypes().length, is(2));
        assertThat(tokens.contains(XQueryTokenType.COMMENT), is(true));
        assertThat(tokens.contains(XQueryTokenType.XML_COMMENT), is(true));
    }

    public void testStringLiteralElements() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        TokenSet tokens = parserDefinition.getStringLiteralElements();
        assertThat(tokens.getTypes().length, is(3));
        assertThat(tokens.contains(XQueryTokenType.STRING_LITERAL_CONTENTS), is(true));
        assertThat(tokens.contains(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS), is(true));
        assertThat(tokens.contains(XQueryTokenType.XML_ELEMENT_CONTENTS), is(true));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testCreateElement() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();

        // foreign ASTNode
        AssertionError e = expectThrows(AssertionError.class, () -> parserDefinition.createElement(new MockASTNode(XQueryTokenType.INTEGER_LITERAL)));
        assertThat(e.getMessage(), is("Alien element type [XQUERY_INTEGER_LITERAL_TOKEN]. Can't create XQuery PsiElement for that."));
    }

    public void testCreateFile() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        LightVirtualFile file = createVirtualFile("test.xqy", "");
        PsiFile psiFile = parserDefinition.createFile(getFileViewProvider(myProject, file, false));
        assertThat(psiFile.getClass().getName(), is(XQueryFileImpl.class.getName()));
        assertThat(psiFile.getFileType(), is(XQueryFileType.INSTANCE));
    }

    public void testSpaceExistanceTypeBetweenTokens() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        assertThat(parserDefinition.spaceExistanceTypeBetweenTokens(null, null), is(ParserDefinition.SpaceRequirements.MAY));
    }
}
