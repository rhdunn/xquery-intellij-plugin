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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer;

import com.intellij.lexer.Lexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SyntaxHighlighterTest extends XQueryLexerTest {
    public void testHighlightingLexer() {
        Lexer lexer = new SyntaxHighlighter().getHighlightingLexer();
        assertThat(lexer.getClass().getName(), is(XQueryLexer.class.getName()));
    }

    public void testTokenHighlights() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(null).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INTEGER_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INTEGER_LITERAL)[0], is(SyntaxHighlighter.NUMBER));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DECIMAL_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DECIMAL_LITERAL)[0], is(SyntaxHighlighter.NUMBER));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOUBLE_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOUBLE_LITERAL)[0], is(SyntaxHighlighter.NUMBER));
    }
}
