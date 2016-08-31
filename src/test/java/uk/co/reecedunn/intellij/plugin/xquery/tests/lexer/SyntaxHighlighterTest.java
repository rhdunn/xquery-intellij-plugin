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
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighterFactory;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SyntaxHighlighterTest extends TestCase {
    public void testFactory() {
        SyntaxHighlighterFactory factory = new SyntaxHighlighterFactory();
        com.intellij.openapi.fileTypes.SyntaxHighlighter highlighter = factory.getSyntaxHighlighter(null, null);
        assertThat(highlighter.getClass().getName(), is(SyntaxHighlighter.class.getName()));
    }

    public void testHighlightingLexer() {
        Lexer lexer = new SyntaxHighlighter().getHighlightingLexer();
        assertThat(lexer.getClass().getName(), is(XQueryLexer.class.getName()));
    }

    public void testTokenHighlights() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(null).length, is(0));
    }

    public void testTokenHighlights_BadCharacter() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BAD_CHARACTER).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BAD_CHARACTER)[0], is(SyntaxHighlighter.BAD_CHARACTER));
    }

    public void testTokenHighlights_Comment() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT_START_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT_START_TAG)[0], is(SyntaxHighlighter.COMMENT));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT)[0], is(SyntaxHighlighter.COMMENT));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT_END_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMENT_END_TAG)[0], is(SyntaxHighlighter.COMMENT));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_START_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_START_TAG)[0], is(SyntaxHighlighter.COMMENT));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT)[0], is(SyntaxHighlighter.COMMENT));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_END_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_END_TAG)[0], is(SyntaxHighlighter.COMMENT));
    }

    public void testTokenHighlights_Number() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INTEGER_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INTEGER_LITERAL)[0], is(SyntaxHighlighter.NUMBER));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DECIMAL_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DECIMAL_LITERAL)[0], is(SyntaxHighlighter.NUMBER));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOUBLE_LITERAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOUBLE_LITERAL)[0], is(SyntaxHighlighter.NUMBER));

        // NOTE: This token is for the parser, so that a parser error will be emitted for incomplete double literals.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)[0], is(SyntaxHighlighter.NUMBER));
    }

    public void testTokenHighlights_String() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_START).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_START)[0], is(SyntaxHighlighter.STRING));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_CONTENTS).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_CONTENTS)[0], is(SyntaxHighlighter.STRING));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_END).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_LITERAL_END)[0], is(SyntaxHighlighter.STRING));
    }

    public void testTokenHighlights_EscapedCharacter() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ESCAPED_CHARACTER).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ESCAPED_CHARACTER)[0], is(SyntaxHighlighter.ESCAPED_CHARACTER));
    }

    public void testTokenHighlights_EntityReference() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)[0], is(SyntaxHighlighter.ENTITY_REFERENCE));

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CHARACTER_REFERENCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CHARACTER_REFERENCE)[0], is(SyntaxHighlighter.ENTITY_REFERENCE));

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_ENTITY_REFERENCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)[0], is(SyntaxHighlighter.STRING));

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.EMPTY_ENTITY_REFERENCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.EMPTY_ENTITY_REFERENCE)[0], is(SyntaxHighlighter.STRING));
    }

    public void testTokenHighlights_Identifier() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.NCNAME).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.NCNAME)[0], is(SyntaxHighlighter.IDENTIFIER));
    }

    public void testTokenHighlights_Keywords() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANCESTOR).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANCESTOR)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANCESTOR_OR_SELF).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANCESTOR_OR_SELF)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AND).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AND)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AS).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AS)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASCENDING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASCENDING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ATTRIBUTE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ATTRIBUTE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BASE_URI).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BASE_URI)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOUNDARY_SPACE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOUNDARY_SPACE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BY).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BY)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CAST).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CAST)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASTABLE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASTABLE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CHILD).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CHILD)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COLLATION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COLLATION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMMENT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONSTRUCTION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONSTRUCTION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY_NAMESPACES).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY_NAMESPACES)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECLARE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECLARE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DEFAULT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DEFAULT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDANT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDANT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDANT_OR_SELF).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDANT_OR_SELF)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIV).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIV)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT_NODE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT_NODE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY_SEQUENCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY_SEQUENCE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENCODING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENCODING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EQ).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EQ)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EVERY).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EVERY)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXCEPT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXCEPT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXTERNAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXTERNAL)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOLLOWING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOLLOWING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOLLOWING_SIBLING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOLLOWING_SIBLING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOR).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FOR)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUNCTION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUNCTION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GREATEST).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GREATEST)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IDIV).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IDIV)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IMPORT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IMPORT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IN).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IN)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INHERIT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INHERIT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSTANCE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSTANCE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INTERSECT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INTERSECT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IS).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IS)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ITEM).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ITEM)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAX).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAX)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEAST).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEAST)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LET).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LET)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MOD).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MOD)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODULE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODULE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAMESPACE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAMESPACE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_INHERIT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_INHERIT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_PRESERVE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_PRESERVE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OF).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OF)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OPTION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OPTION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OR).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OR)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDER).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDER)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERED).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERED)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARENT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRECEDING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRECEDING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRECEDING_SIBLING).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRECEDING_SIBLING)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRESERVE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRESERVE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROCESSING_INSTRUCTION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROCESSING_INSTRUCTION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RETURN).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RETURN)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SATISFIES).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SATISFIES)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ATTRIBUTE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ATTRIBUTE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ELEMENT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ELEMENT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SELF).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SELF)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SOME).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SOME)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STABLE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STABLE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRICT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRICT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRIP).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRIP)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TEXT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TEXT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TO).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TO)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TREAT).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TREAT)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPESWITCH).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPESWITCH)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNORDERED).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNORDERED)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALIDATE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALIDATE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VARIABLE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VARIABLE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VERSION).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VERSION)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHERE).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHERE)[0], is(SyntaxHighlighter.KEYWORD));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_XQUERY).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_XQUERY)[0], is(SyntaxHighlighter.KEYWORD));
    }

    public void testTokenHighlights_XmlOperator() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPEN_XML_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPEN_XML_TAG)[0], is(SyntaxHighlighter.XML_TAG));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.END_XML_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.END_XML_TAG)[0], is(SyntaxHighlighter.XML_TAG));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CLOSE_XML_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CLOSE_XML_TAG)[0], is(SyntaxHighlighter.XML_TAG));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SELF_CLOSING_XML_TAG).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SELF_CLOSING_XML_TAG)[0], is(SyntaxHighlighter.XML_TAG));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EQUAL).length, is(1));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EQUAL)[0], is(SyntaxHighlighter.XML_TAG));
    }

    public void testTokenHighlights_OtherToken() {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INVALID).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION_START_TAG).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION_END_TAG).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_BEGIN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_CONTENTS).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_END).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_END).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ELEMENT_CONTENTS).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.NOT_EQUAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.VARIABLE_INDICATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARENTHESIS_OPEN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARENTHESIS_CLOSE).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STAR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PLUS).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.COMMA).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.MINUS).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOT).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.EQUAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BLOCK_OPEN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BLOCK_CLOSE).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SEPARATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.LESS_THAN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.GREATER_THAN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.LESS_THAN_OR_EQUAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.GREATER_THAN_OR_EQUAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.UNION).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPTIONAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.AXIS_SEPARATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.QNAME_SEPARATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ASSIGN_EQUAL).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DIRECT_DESCENDANTS_PATH).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ALL_DESCENDANTS_PATH).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ATTRIBUTE_SELECTOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDICATE_BEGIN).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDICATE_END).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARENT_SELECTOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.NODE_BEFORE).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.NODE_AFTER).length, is(0));

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.MAP_OPERATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.FUNCTION_REF_OPERATOR).length, is(0));
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ANNOTATION_INDICATOR).length, is(0));
    }
}
