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
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;
import uk.co.reecedunn.intellij.plugin.xquery.settings.ColorSettingsPage;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ColorSettingsPageTest extends TestCase {
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

        final List<TextAttributesKey> keys = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            for (TextAttributesKey key : highlighter.getTokenHighlights(lexer.getTokenType())) {
                if (!keys.contains(key)) {
                    keys.add(key);
                }
            }

            lexer.advance();
        }

        assertThat(keys.size(), is(17));
        assertThat(keys.contains(SyntaxHighlighter.COMMENT), is(true));
        assertThat(keys.contains(SyntaxHighlighter.IDENTIFIER), is(true));
        assertThat(keys.contains(SyntaxHighlighter.KEYWORD), is(true));
        assertThat(keys.contains(SyntaxHighlighter.ANNOTATION), is(true));
        assertThat(keys.contains(SyntaxHighlighter.NUMBER), is(true));
        assertThat(keys.contains(SyntaxHighlighter.STRING), is(true));
        assertThat(keys.contains(SyntaxHighlighter.ESCAPED_CHARACTER), is(true));
        assertThat(keys.contains(SyntaxHighlighter.ENTITY_REFERENCE), is(true));
        assertThat(keys.contains(SyntaxHighlighter.BAD_CHARACTER), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XQDOC_TAG), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XQDOC_MARKUP), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_TAG), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_TAG_NAME), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_ATTRIBUTE_NAME), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_ATTRIBUTE_VALUE), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_ENTITY_REFERENCE), is(true));
        assertThat(keys.contains(SyntaxHighlighter.XML_ESCAPED_CHARACTER), is(true));
    }

    public void testAdditionalHighlightingTagToDescriptorMap() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getAdditionalHighlightingTagToDescriptorMap(), is(nullValue()));
    }

    public void testAttributeDescriptors() {
        ColorSettingsPage settings = new ColorSettingsPage();
        assertThat(settings.getAttributeDescriptors().length, is(17));
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
        assertThat(settings.getAttributeDescriptors()[6].getDisplayName(), is("Annotation"));
        assertThat(settings.getAttributeDescriptors()[6].getKey(), is(SyntaxHighlighter.ANNOTATION));
        assertThat(settings.getAttributeDescriptors()[7].getDisplayName(), is("Number"));
        assertThat(settings.getAttributeDescriptors()[7].getKey(), is(SyntaxHighlighter.NUMBER));
        assertThat(settings.getAttributeDescriptors()[8].getDisplayName(), is("String"));
        assertThat(settings.getAttributeDescriptors()[8].getKey(), is(SyntaxHighlighter.STRING));
        assertThat(settings.getAttributeDescriptors()[9].getDisplayName(), is("Direct XML Construction//Tag"));
        assertThat(settings.getAttributeDescriptors()[9].getKey(), is(SyntaxHighlighter.XML_TAG));
        assertThat(settings.getAttributeDescriptors()[10].getDisplayName(), is("Direct XML Construction//Tag Name"));
        assertThat(settings.getAttributeDescriptors()[10].getKey(), is(SyntaxHighlighter.XML_TAG_NAME));
        assertThat(settings.getAttributeDescriptors()[11].getDisplayName(), is("Direct XML Construction//Attribute Name"));
        assertThat(settings.getAttributeDescriptors()[11].getKey(), is(SyntaxHighlighter.XML_ATTRIBUTE_NAME));
        assertThat(settings.getAttributeDescriptors()[12].getDisplayName(), is("Direct XML Construction//Attribute Value"));
        assertThat(settings.getAttributeDescriptors()[12].getKey(), is(SyntaxHighlighter.XML_ATTRIBUTE_VALUE));
        assertThat(settings.getAttributeDescriptors()[13].getDisplayName(), is("Direct XML Construction//Entity Reference"));
        assertThat(settings.getAttributeDescriptors()[13].getKey(), is(SyntaxHighlighter.XML_ENTITY_REFERENCE));
        assertThat(settings.getAttributeDescriptors()[14].getDisplayName(), is("Direct XML Construction//Escaped Character"));
        assertThat(settings.getAttributeDescriptors()[14].getKey(), is(SyntaxHighlighter.XML_ESCAPED_CHARACTER));
        assertThat(settings.getAttributeDescriptors()[15].getDisplayName(), is("xqDoc//Tag"));
        assertThat(settings.getAttributeDescriptors()[15].getKey(), is(SyntaxHighlighter.XQDOC_TAG));
        assertThat(settings.getAttributeDescriptors()[16].getDisplayName(), is("xqDoc//Markup"));
        assertThat(settings.getAttributeDescriptors()[16].getKey(), is(SyntaxHighlighter.XQDOC_MARKUP));
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
