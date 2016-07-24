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
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryPsiParser;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryFileImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.mocks.MockASTNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(tokens.getTypes().length, is(7));
        assertThat(tokens.contains(XQueryTokenType.STRING_LITERAL_START), is(true));
        assertThat(tokens.contains(XQueryTokenType.STRING_LITERAL_CONTENTS), is(true));
        assertThat(tokens.contains(XQueryTokenType.STRING_LITERAL_END), is(true));
        assertThat(tokens.contains(XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER), is(true));
        assertThat(tokens.contains(XQueryTokenType.CHARACTER_REFERENCE), is(true));
        assertThat(tokens.contains(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE), is(true));
        assertThat(tokens.contains(XQueryTokenType.PARTIAL_ENTITY_REFERENCE), is(true));
    }

    public void testCreateElement() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();

        // foreign ASTNode

        boolean thrown = false;
        try {
            parserDefinition.createElement(new MockASTNode(XQueryTokenType.INTEGER_LITERAL));
        } catch (AssertionError e) {
            thrown = true;
            assertThat(e.getMessage(), is("Alien element type [XQUERY_INTEGER_LITERAL_TOKEN]. Can't create XQuery PsiElement for that."));
        } catch (Exception e) {
            // Unexpected exception.
        }
        assertTrue("createElement(null) should throw AssertionError.", thrown);
    }

    public void testCreateFile() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        PsiFile file = parserDefinition.createFile(getFileViewProvider(myProject));
        assertThat(file.getClass().getName(), is(XQueryFileImpl.class.getName()));
        assertThat(file.getFileType(), is(XQueryFileType.INSTANCE));
    }

    public void testSpaceExistanceTypeBetweenTokens() {
        ParserDefinition parserDefinition = new XQueryParserDefinition();
        assertThat(parserDefinition.spaceExistanceTypeBetweenTokens(null, null), is(ParserDefinition.SpaceRequirements.MAY));
    }
}
