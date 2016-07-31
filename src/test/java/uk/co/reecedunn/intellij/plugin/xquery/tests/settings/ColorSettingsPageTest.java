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
package uk.co.reecedunn.intellij.plugin.xquery.tests.settings;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.settings.ColorSettingsPage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ColorSettingsPageTest extends TestCase {
    private void matchToken(Lexer lexer, com.intellij.openapi.fileTypes.SyntaxHighlighter highlighter, IElementType type, TextAttributesKey key) {
        assertThat(lexer.getTokenType(), is(type));
        if (key == null) {
            assertThat(highlighter.getTokenHighlights(type).length, is(0));
        } else {
            assertThat(highlighter.getTokenHighlights(type).length, is(1));
            assertThat(highlighter.getTokenHighlights(type)[0], is(key));
        }
        lexer.advance();
    }

    public void testIcon() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getIcon(), is(nullValue()));
    }

    public void testHighlighter() {
        ColorSettingsPage settings = new ColorSettingsPage();
        com.intellij.openapi.fileTypes.SyntaxHighlighter highlighter = settings.getHighlighter();
        assertThat(highlighter.getClass().getName(), is(SyntaxHighlighter.class.getName()));
    }

    public void testDemoText() {
        ColorSettingsPage settings = new ColorSettingsPage();
        com.intellij.openapi.fileTypes.SyntaxHighlighter highlighter = settings.getHighlighter();
        Lexer lexer = highlighter.getHighlightingLexer();
        lexer.start(settings.getDemoText());

        // This is documenting the tokens that are generated from the demo text
        // with their corresponding syntax highlighting group. This is to
        // show that there is at least one matching token type in this text for
        // each attribute descriptor.
        matchToken(lexer, highlighter, XQueryTokenType.COMMENT_START_TAG, SyntaxHighlighter.COMMENT);
        matchToken(lexer, highlighter, XQueryTokenType.COMMENT, SyntaxHighlighter.COMMENT);
        matchToken(lexer, highlighter, XQueryTokenType.COMMENT_END_TAG, SyntaxHighlighter.COMMENT);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.K_XQUERY, SyntaxHighlighter.KEYWORD);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.K_VERSION, SyntaxHighlighter.KEYWORD);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_START, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_CONTENTS, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_END, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.SEPARATOR, null);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.PARENTHESIS_OPEN, null);
        matchToken(lexer, highlighter, XQueryTokenType.INTEGER_LITERAL, SyntaxHighlighter.NUMBER);
        matchToken(lexer, highlighter, XQueryTokenType.COMMA, null);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_START, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_CONTENTS, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER, SyntaxHighlighter.ESCAPED_CHARACTER);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_CONTENTS, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE, SyntaxHighlighter.ENTITY_REFERENCE);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_CONTENTS, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.STRING_LITERAL_END, SyntaxHighlighter.STRING);
        matchToken(lexer, highlighter, XQueryTokenType.COMMA, null);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.NCNAME, SyntaxHighlighter.IDENTIFIER);
        matchToken(lexer, highlighter, XQueryTokenType.PARENTHESIS_CLOSE, null);
        matchToken(lexer, highlighter, XQueryTokenType.WHITE_SPACE, null);
        matchToken(lexer, highlighter, XQueryTokenType.BAD_CHARACTER, SyntaxHighlighter.BAD_CHARACTER);
        matchToken(lexer, highlighter, XQueryTokenType.BAD_CHARACTER, SyntaxHighlighter.BAD_CHARACTER);
        matchToken(lexer, highlighter, XQueryTokenType.BAD_CHARACTER, SyntaxHighlighter.BAD_CHARACTER);
        matchToken(lexer, highlighter, null, null);
    }

    public void testAdditionalHighlightingTagToDescriptorMap() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getAdditionalHighlightingTagToDescriptorMap(), is(nullValue()));
    }

    public void testAttributeDescriptors() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getAttributeDescriptors().length, is(8));
        assertThat(settings.getAttributeDescriptors()[0].getDisplayName(), is("Invalid Character"));
        assertThat(settings.getAttributeDescriptors()[0].getKey(), is(SyntaxHighlighter.BAD_CHARACTER));
        assertThat(settings.getAttributeDescriptors()[1].getDisplayName(), is("Comment"));
        assertThat(settings.getAttributeDescriptors()[1].getKey(), is(SyntaxHighlighter.COMMENT));
        assertThat(settings.getAttributeDescriptors()[2].getDisplayName(), is("Entity Reference"));
        assertThat(settings.getAttributeDescriptors()[2].getKey(), is(SyntaxHighlighter.ENTITY_REFERENCE));
        assertThat(settings.getAttributeDescriptors()[3].getDisplayName(), is("Escaped Character"));
        assertThat(settings.getAttributeDescriptors()[3].getKey(), is(SyntaxHighlighter.ESCAPED_CHARACTER));
        assertThat(settings.getAttributeDescriptors()[4].getDisplayName(), is("Identifier"));
        assertThat(settings.getAttributeDescriptors()[4].getKey(), is(SyntaxHighlighter.IDENTIFIER));
        assertThat(settings.getAttributeDescriptors()[5].getDisplayName(), is("Keyword"));
        assertThat(settings.getAttributeDescriptors()[5].getKey(), is(SyntaxHighlighter.KEYWORD));
        assertThat(settings.getAttributeDescriptors()[6].getDisplayName(), is("Number"));
        assertThat(settings.getAttributeDescriptors()[6].getKey(), is(SyntaxHighlighter.NUMBER));
        assertThat(settings.getAttributeDescriptors()[7].getDisplayName(), is("String"));
        assertThat(settings.getAttributeDescriptors()[7].getKey(), is(SyntaxHighlighter.STRING));
    }

    public void testColorDescriptors() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getColorDescriptors().length, is(0));
    }

    public void testDisplayName() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getDisplayName(), is("XQuery"));
    }
}
