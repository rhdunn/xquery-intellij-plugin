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
package uk.co.reecedunn.intellij.plugin.xquery.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

public class SyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("XQUERY_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey ENTITY_REFERENCE = TextAttributesKey.createTextAttributesKey("XQUERY_ENTITY_REFERENCE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public static final TextAttributesKey ESCAPED_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("XQUERY_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("XQUERY_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ANNOTATION = TextAttributesKey.createTextAttributesKey("XQUERY_ANNOTATION", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("XQUERY_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("XQUERY_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NS_PREFIX = TextAttributesKey.createTextAttributesKey("XQUERY_NS_PREFIX", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey XQDOC_TAG = TextAttributesKey.createTextAttributesKey("XQUERY_XQDOC_TAG", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
    public static final TextAttributesKey XQDOC_MARKUP = TextAttributesKey.createTextAttributesKey("XQUERY_XQDOC_MARKUP", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);

    public static final TextAttributesKey XML_TAG = TextAttributesKey.createTextAttributesKey("XQUERY_XML_TAG", XmlHighlighterColors.XML_TAG);
    public static final TextAttributesKey XML_TAG_NAME = TextAttributesKey.createTextAttributesKey("XQUERY_XML_TAG_NAME", XmlHighlighterColors.XML_TAG_NAME);
    public static final TextAttributesKey XML_ATTRIBUTE_NAME = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ATTRIBUTE_NAME", XmlHighlighterColors.XML_ATTRIBUTE_NAME);
    public static final TextAttributesKey XML_ATTRIBUTE_VALUE = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ATTRIBUTE_VALUE", XmlHighlighterColors.XML_ATTRIBUTE_VALUE);
    public static final TextAttributesKey XML_ENTITY_REFERENCE = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ENTITY_REFERENCE", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey XML_ESCAPED_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);

    private static final TextAttributesKey[] BAD_CHARACTER_KEYS = pack(BAD_CHARACTER);
    private static final TextAttributesKey[] ENTITY_REFERENCE_KEYS = pack(ENTITY_REFERENCE);
    private static final TextAttributesKey[] ESCAPED_CHARACTER_KEYS = pack(ESCAPED_CHARACTER);
    private static final TextAttributesKey[] IDENTIFIER_KEYS = pack(IDENTIFIER);
    private static final TextAttributesKey[] KEYWORD_KEYS = pack(KEYWORD);
    private static final TextAttributesKey[] ANNOTATION_KEYS = pack(ANNOTATION);
    private static final TextAttributesKey[] NUMBER_KEYS = pack(NUMBER);
    private static final TextAttributesKey[] STRING_KEYS = pack(STRING);

    private static final TextAttributesKey[] COMMENT_KEYS = pack(COMMENT);
    private static final TextAttributesKey[] XQDOC_TAG_KEYS = pack(COMMENT, XQDOC_TAG);
    private static final TextAttributesKey[] XQDOC_MARKUP_KEYS = pack(COMMENT, XQDOC_MARKUP);

    private static final TextAttributesKey[] XML_TAG_KEYS = pack(XML_TAG);
    private static final TextAttributesKey[] XML_TAG_NAME_KEYS = pack(XML_TAG, XML_TAG_NAME);
    private static final TextAttributesKey[] XML_ATTRIBUTE_NAME_KEYS = pack(XML_TAG, XML_ATTRIBUTE_NAME);
    private static final TextAttributesKey[] XML_ATTRIBUTE_VALUE_KEYS = pack(XML_TAG, XML_ATTRIBUTE_VALUE);
    private static final TextAttributesKey[] XML_ENTITY_REFERENCE_KEYS = pack(XML_TAG, XML_ENTITY_REFERENCE);
    private static final TextAttributesKey[] XML_ESCAPED_CHARACTER_KEYS = pack(XML_TAG, XML_ESCAPED_CHARACTER);

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public Lexer getHighlightingLexer() {
        return new XQueryLexer();
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public TextAttributesKey[] getTokenHighlights(IElementType type) {
        if (type == XQueryTokenType.INTEGER_LITERAL ||
            type == XQueryTokenType.DECIMAL_LITERAL ||
            type == XQueryTokenType.DOUBLE_LITERAL ||
            type == XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT) {
            return NUMBER_KEYS;
        } else if (type == XQueryTokenType.STRING_LITERAL_START ||
                   type == XQueryTokenType.STRING_LITERAL_CONTENTS ||
                   type == XQueryTokenType.STRING_LITERAL_END ||
                   type == XQueryTokenType.STRING_CONSTRUCTOR_START ||
                   type == XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS ||
                   type == XQueryTokenType.STRING_CONSTRUCTOR_END ||
                   type == XQueryTokenType.BRACED_URI_LITERAL_START ||
                   type == XQueryTokenType.BRACED_URI_LITERAL_END) {
            return STRING_KEYS;
        } else if (type == XQueryTokenType.ESCAPED_CHARACTER) {
            return ESCAPED_CHARACTER_KEYS;
        } else if (type == XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ||
                   type == XQueryTokenType.PARTIAL_ENTITY_REFERENCE ||
                   type == XQueryTokenType.EMPTY_ENTITY_REFERENCE ||
                   type == XQueryTokenType.CHARACTER_REFERENCE) {
            return ENTITY_REFERENCE_KEYS;
        } else if (type == XQueryTokenType.BAD_CHARACTER) {
            return BAD_CHARACTER_KEYS;
        } else if (type == XQueryTokenType.NCNAME) {
            return IDENTIFIER_KEYS;
        } else if (type == XQueryTokenType.COMMENT_START_TAG ||
                   type == XQueryTokenType.COMMENT ||
                   type == XQueryTokenType.COMMENT_END_TAG ||
                   type == XQueryTokenType.XQDOC_START_TAG ||
                   type == XQueryTokenType.XQDOC_XML_ELEM_CONTENTS ||
                   type == XQueryTokenType.XML_COMMENT_END_TAG ||
                   type == XQueryTokenType.XML_COMMENT ||
                   type == XQueryTokenType.XML_COMMENT_START_TAG) {
            return COMMENT_KEYS;
        } else if (type == XQueryTokenType.K_UPDATING ||
                   type == XQueryTokenType.K_PRIVATE ||
                   type == XQueryTokenType.K_PUBLIC ||
                   type == XQueryTokenType.ANNOTATION_INDICATOR) {
            return ANNOTATION_KEYS;
        } else if (type instanceof IXQueryKeywordOrNCNameType) {
            return KEYWORD_KEYS;
        } else if (type == XQueryTokenType.OPEN_XML_TAG ||
                   type == XQueryTokenType.END_XML_TAG ||
                   type == XQueryTokenType.CLOSE_XML_TAG ||
                   type == XQueryTokenType.SELF_CLOSING_XML_TAG ||
                   type == XQueryTokenType.XML_WHITE_SPACE) {
            return XML_TAG_KEYS;
        } else if (type == XQueryTokenType.XML_TAG_NCNAME ||
                   type == XQueryTokenType.XML_TAG_QNAME_SEPARATOR) {
            return XML_TAG_NAME_KEYS;
        } else if (type == XQueryTokenType.XML_EQUAL ||
                   type == XQueryTokenType.XML_ATTRIBUTE_NCNAME ||
                   type == XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR) {
            return XML_ATTRIBUTE_NAME_KEYS;
        } else if (type == XQueryTokenType.XML_ATTRIBUTE_VALUE_START ||
                   type == XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS ||
                   type == XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE ||
                   type == XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE ||
                   type == XQueryTokenType.XML_ATTRIBUTE_VALUE_END) {
            return XML_ATTRIBUTE_VALUE_KEYS;
        } else if (type == XQueryTokenType.XML_ESCAPED_CHARACTER) {
            return XML_ESCAPED_CHARACTER_KEYS;
        } else if (type == XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE ||
                   type == XQueryTokenType.XML_CHARACTER_REFERENCE) {
            return XML_ENTITY_REFERENCE_KEYS;
        } else if (type == XQueryTokenType.XQDOC_TAG_INDICATOR ||
                   type == XQueryTokenType.XQDOC_TAG_NAME) {
            return XQDOC_TAG_KEYS;
        } else if (type == XQueryTokenType.XQDOC_OPEN_XML_TAG ||
                   type == XQueryTokenType.XQDOC_END_XML_TAG ||
                   type == XQueryTokenType.XQDOC_CLOSE_XML_TAG ||
                   type == XQueryTokenType.XQDOC_SELF_CLOSING_XML_TAG ||
                   type == XQueryTokenType.XQDOC_XML_TAG_NAME ||
                   type == XQueryTokenType.XQDOC_XML_EQUAL ||
                   type == XQueryTokenType.XQDOC_XML_ATTRIBUTE_VALUE_START ||
                   type == XQueryTokenType.XQDOC_XML_ATTRIBUTE_VALUE_CONTENTS ||
                   type == XQueryTokenType.XQDOC_XML_ATTRIBUTE_VALUE_END) {
            return XQDOC_MARKUP_KEYS;
        }
        return EMPTY;
    }
}
