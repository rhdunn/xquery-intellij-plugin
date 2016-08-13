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

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class XQueryLexer extends LexerBase {
    private static final Map<String, IElementType> sKeywords = new HashMap<>();

    private XQueryCodePointRange mTokenRange;
    private int mState;
    private int mNextState;
    private IElementType mType;

    // States

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_STRING_LITERAL_QUOTE = 1;
    private static final int STATE_STRING_LITERAL_APOSTROPHE = 2;
    private static final int STATE_DOUBLE_EXPONENT = 3;
    private static final int STATE_XQUERY_COMMENT = 4;
    private static final int STATE_XML_COMMENT = 5;
    private static final int STATE_UNEXPECTED_END_OF_BLOCK = 6;
    private static final int STATE_CDATA_SECTION = 7;

    private void matchEntityReference() {
        mTokenRange.match();
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        if (cc == CharacterClass.NAME_START_CHAR) {
            mTokenRange.match();
            cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
            while (cc == CharacterClass.NAME_START_CHAR || cc == CharacterClass.DIGIT) {
                mTokenRange.match();
                cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
            }
            if (cc == CharacterClass.SEMICOLON) {
                mTokenRange.match();
                mType = XQueryTokenType.PREDEFINED_ENTITY_REFERENCE;
            } else {
                mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
            }
        } else if (cc == CharacterClass.HASH) {
            mTokenRange.match();
            int c = mTokenRange.getCodePoint();
            if (c == 'x') {
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'))) {
                    while (((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'))) {
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                    }
                    if (c == ';') {
                        mTokenRange.match();
                        mType = XQueryTokenType.CHARACTER_REFERENCE;
                    } else {
                        mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                    }
                } else if (c == ';') {
                    mTokenRange.match();
                    mType = XQueryTokenType.EMPTY_ENTITY_REFERENCE;
                } else {
                    mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                }
            } else if ((c >= '0') && (c <= '9')) {
                mTokenRange.match();
                while ((c >= '0') && (c <= '9')) {
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }
                if (c == ';') {
                    mTokenRange.match();
                    mType = XQueryTokenType.CHARACTER_REFERENCE;
                } else {
                    mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                }
            } else if (c == ';') {
                mTokenRange.match();
                mType = XQueryTokenType.EMPTY_ENTITY_REFERENCE;
            } else {
                mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
            }
        } else if (cc == CharacterClass.SEMICOLON) {
            mTokenRange.match();
            mType = XQueryTokenType.EMPTY_ENTITY_REFERENCE;
        } else {
            mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
        }
    }

    private void stateUnexpectedEndOfBlock() {
        mType = XQueryTokenType.UNEXPECTED_END_OF_BLOCK;
        mNextState = STATE_DEFAULT;
    }

    private void stateXQueryComment() {
        int c = mTokenRange.getCodePoint();
        if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
            return;
        } else if (c == ':') {
            mTokenRange.save();
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == ')') {
                mTokenRange.match();
                mType = XQueryTokenType.COMMENT_END_TAG;
                mNextState = STATE_DEFAULT;
                return;
            } else {
                mTokenRange.restore();
            }
        }

        int depth = 1;
        while (true) {
            if (c == XQueryCodePointRange.END_OF_BUFFER) {
                mTokenRange.match();
                mType = XQueryTokenType.COMMENT;
                mNextState = STATE_UNEXPECTED_END_OF_BLOCK;
                return;
            } else if (c == '(') {
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ':') {
                    mTokenRange.match();
                    ++depth;
                }
            } else if (c == ':') {
                mTokenRange.save();
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ')') {
                    mTokenRange.match();
                    if (--depth == 0) {
                        mTokenRange.restore();
                        mType = XQueryTokenType.COMMENT;
                        return;
                    }
                }
            } else {
                mTokenRange.match();
            }
            c = mTokenRange.getCodePoint();
        }
    }

    private void stateXmlComment() {
        int c = mTokenRange.getCodePoint();
        if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
            return;
        } else if (c == '-') {
            mTokenRange.save();
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '-') {
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.XML_COMMENT_END_TAG;
                    mNextState = STATE_DEFAULT;
                    return;
                } else {
                    mTokenRange.restore();
                }
            } else {
                mTokenRange.restore();
            }
        }

        while (true) {
            if (c == XQueryCodePointRange.END_OF_BUFFER) {
                mTokenRange.match();
                mType = XQueryTokenType.XML_COMMENT;
                mNextState = STATE_UNEXPECTED_END_OF_BLOCK;
                return;
            } else if (c == '-') {
                mTokenRange.save();
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '-') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '>') {
                        mTokenRange.restore();
                        mType = XQueryTokenType.XML_COMMENT;
                        return;
                    }
                }
            } else {
                mTokenRange.match();
            }
            c = mTokenRange.getCodePoint();
        }
    }

    private void stateCDataSection() {
        int c = mTokenRange.getCodePoint();
        if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
            return;
        } else if (c == ']') {
            mTokenRange.save();
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == ']') {
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.CDATA_SECTION_END_TAG;
                    mNextState = STATE_DEFAULT;
                    return;
                } else {
                    mTokenRange.restore();
                }
            } else {
                mTokenRange.restore();
            }
        }

        while (true) {
            if (c == XQueryCodePointRange.END_OF_BUFFER) {
                mTokenRange.match();
                mType = XQueryTokenType.CDATA_SECTION;
                mNextState = STATE_UNEXPECTED_END_OF_BLOCK;
                return;
            } else if (c == ']') {
                mTokenRange.save();
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ']') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '>') {
                        mTokenRange.restore();
                        mType = XQueryTokenType.CDATA_SECTION;
                        return;
                    }
                }
            } else {
                mTokenRange.match();
            }
            c = mTokenRange.getCodePoint();
        }
    }

    private void stateDefault() {
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        int c;
        switch (cc) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                break;
            case CharacterClass.DOT:
                mTokenRange.save();
                mTokenRange.match();
                cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
                if (cc == CharacterClass.DOT) {
                    mTokenRange.match();
                    mType = XQueryTokenType.PARENT_SELECTOR;
                    break;
                } else if (cc != CharacterClass.DIGIT) {
                    mType = XQueryTokenType.DOT;
                    break;
                } else {
                    mTokenRange.restore();
                    cc = CharacterClass.DOT;
                    // Fall Through
                }
            case CharacterClass.DIGIT:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DIGIT)
                    mTokenRange.match();
                if (cc != CharacterClass.DOT && CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DOT) {
                    mTokenRange.match();
                    while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DIGIT)
                        mTokenRange.match();
                    mType = XQueryTokenType.DECIMAL_LITERAL;
                } else {
                    mType = (cc == CharacterClass.DOT) ? XQueryTokenType.DECIMAL_LITERAL : XQueryTokenType.INTEGER_LITERAL;
                }
                c = mTokenRange.getCodePoint();
                if (c == 'e' || c == 'E') {
                    mTokenRange.save();
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                    if ((c == '+') || (c == '-')) {
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                    }
                    if (CharacterClass.getCharClass(c) == CharacterClass.DIGIT) {
                        mTokenRange.match();
                        while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DIGIT)
                            mTokenRange.match();
                        mType = XQueryTokenType.DOUBLE_LITERAL;
                    } else {
                        mNextState = STATE_DOUBLE_EXPONENT;
                        mTokenRange.restore();
                    }
                }
                break;
            case CharacterClass.END_OF_BUFFER:
                mType = null;
                break;
            case CharacterClass.QUOTE:
            case CharacterClass.APOSTROPHE:
                mTokenRange.match();
                mType = XQueryTokenType.STRING_LITERAL_START;
                mNextState = (cc == CharacterClass.QUOTE) ? STATE_STRING_LITERAL_QUOTE : STATE_STRING_LITERAL_APOSTROPHE;
                break;
            case CharacterClass.NAME_START_CHAR:
                mTokenRange.match();
                cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
                while (cc == CharacterClass.NAME_START_CHAR ||
                       cc == CharacterClass.DIGIT ||
                       cc == CharacterClass.DOT ||
                       cc == CharacterClass.HYPHEN_MINUS ||
                       cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match();
                    cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
                }
                mType = sKeywords.getOrDefault(getTokenText(), XQueryTokenType.NCNAME);
                break;
            case CharacterClass.PARENTHESIS_OPEN:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == ':') {
                    mTokenRange.match();
                    mType = XQueryTokenType.COMMENT_START_TAG;
                    mNextState = STATE_XQUERY_COMMENT;
                } else if (c == '#') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PRAGMA_BEGIN;
                } else {
                    mType = XQueryTokenType.PARENTHESIS_OPEN;
                }
                break;
            case CharacterClass.PARENTHESIS_CLOSE:
                mTokenRange.match();
                mType = XQueryTokenType.PARENTHESIS_CLOSE;
                break;
            case CharacterClass.COLON:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == ')') {
                    mTokenRange.match();
                    mType = XQueryTokenType.COMMENT_END_TAG;
                } else if (c == ':') {
                    mTokenRange.match();
                    mType = XQueryTokenType.AXIS_SEPARATOR;
                } else if (c == '=') {
                    mTokenRange.match();
                    mType = XQueryTokenType.ASSIGN_EQUAL;
                } else {
                    mType = XQueryTokenType.QNAME_SEPARATOR;
                }
                break;
            case CharacterClass.HASH:
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ')') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PRAGMA_END;
                } else {
                    mType = XQueryTokenType.FUNCTION_REF_OPERATOR;
                }
                break;
            case CharacterClass.EXCLAMATION_MARK:
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '=') {
                    mTokenRange.match();
                    mType = XQueryTokenType.NOT_EQUAL;
                } else {
                    mType = XQueryTokenType.MAP_OPERATOR; // XQuery 3.0
                }
                break;
            case CharacterClass.DOLLAR:
                mTokenRange.match();
                mType = XQueryTokenType.VARIABLE_INDICATOR;
                break;
            case CharacterClass.ASTERISK:
                mTokenRange.match();
                mType = XQueryTokenType.STAR;
                break;
            case CharacterClass.PLUS:
                mTokenRange.match();
                mType = XQueryTokenType.PLUS;
                break;
            case CharacterClass.COMMA:
                mTokenRange.match();
                mType = XQueryTokenType.COMMA;
                break;
            case CharacterClass.HYPHEN_MINUS:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '-') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '>') {
                        mTokenRange.match();
                        mType = XQueryTokenType.XML_COMMENT_END_TAG;
                    } else {
                        mTokenRange.match();
                        mType = XQueryTokenType.INVALID;
                    }
                } else {
                    mType = XQueryTokenType.MINUS;
                }
                break;
            case CharacterClass.SEMICOLON:
                mTokenRange.match();
                mType = XQueryTokenType.SEPARATOR;
                break;
            case CharacterClass.LESS_THAN:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '/') {
                    mTokenRange.match();
                    mType = XQueryTokenType.CLOSE_XML_TAG;
                } else if (c == '<') {
                    mTokenRange.match();
                    mType = XQueryTokenType.NODE_BEFORE;
                } else if (c == '=') {
                    mTokenRange.match();
                    mType = XQueryTokenType.LESS_THAN_OR_EQUAL;
                } else if (c == '?') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN;
                } else if (c == '!') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '-') {
                        mTokenRange.match();
                        if (mTokenRange.getCodePoint() == '-') {
                            mTokenRange.match();
                            mType = XQueryTokenType.XML_COMMENT_START_TAG;
                            mNextState = STATE_XML_COMMENT;
                        } else {
                            mType = XQueryTokenType.INVALID;
                        }
                    } else if (mTokenRange.getCodePoint() == '[') {
                        mTokenRange.match();
                        if (mTokenRange.getCodePoint() == 'C') {
                            mTokenRange.match();
                            if (mTokenRange.getCodePoint() == 'D') {
                                mTokenRange.match();
                                if (mTokenRange.getCodePoint() == 'A') {
                                    mTokenRange.match();
                                    if (mTokenRange.getCodePoint() == 'T') {
                                        mTokenRange.match();
                                        if (mTokenRange.getCodePoint() == 'A') {
                                            mTokenRange.match();
                                            if (mTokenRange.getCodePoint() == '[') {
                                                mTokenRange.match();
                                                mType = XQueryTokenType.CDATA_SECTION_START_TAG;
                                                mNextState = STATE_CDATA_SECTION;
                                            } else {
                                                mType = XQueryTokenType.INVALID;
                                            }
                                        } else {
                                            mType = XQueryTokenType.INVALID;
                                        }
                                    } else {
                                        mType = XQueryTokenType.INVALID;
                                    }
                                } else {
                                    mType = XQueryTokenType.INVALID;
                                }
                            } else {
                                mType = XQueryTokenType.INVALID;
                            }
                        } else {
                            mType = XQueryTokenType.INVALID;
                        }
                    } else {
                        mType = XQueryTokenType.INVALID;
                    }
                } else {
                    mType = XQueryTokenType.LESS_THAN;
                }
                break;
            case CharacterClass.GREATER_THAN:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.NODE_AFTER;
                } else if (c == '=') {
                    mTokenRange.match();
                    mType = XQueryTokenType.GREATER_THAN_OR_EQUAL;
                } else {
                    mType = XQueryTokenType.GREATER_THAN;
                }
                break;
            case CharacterClass.EQUAL:
                mTokenRange.match();
                mType = XQueryTokenType.EQUAL;
                break;
            case CharacterClass.CURLY_BRACE_OPEN:
                mTokenRange.match();
                mType = XQueryTokenType.BLOCK_OPEN;
                break;
            case CharacterClass.CURLY_BRACE_CLOSE:
                mTokenRange.match();
                mType = XQueryTokenType.BLOCK_CLOSE;
                break;
            case CharacterClass.VERTICAL_BAR:
                mTokenRange.match();
                mType = XQueryTokenType.UNION;
                break;
            case CharacterClass.FORWARD_SLASH:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '/') {
                    mTokenRange.match();
                    mType = XQueryTokenType.ALL_DESCENDANTS_PATH;
                } else if (c == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.SELF_CLOSING_XML_TAG;
                } else {
                    mType = XQueryTokenType.DIRECT_DESCENDANTS_PATH;
                }
                break;
            case CharacterClass.AT_SIGN:
                mTokenRange.match();
                mType = XQueryTokenType.ATTRIBUTE_SELECTOR;
                break;
            case CharacterClass.SQUARE_BRACE_OPEN:
                mTokenRange.match();
                mType = XQueryTokenType.PREDICATE_BEGIN;
                break;
            case CharacterClass.SQUARE_BRACE_CLOSE:
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ']') {
                    mTokenRange.save();
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '>') {
                        mTokenRange.match();
                        mType = XQueryTokenType.CDATA_SECTION_END_TAG;
                    } else {
                        mTokenRange.restore();
                        mType = XQueryTokenType.PREDICATE_END;
                    }
                } else {
                    mType = XQueryTokenType.PREDICATE_END;
                }
                break;
            case CharacterClass.QUESTION_MARK:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_END;
                } else {
                    mType = XQueryTokenType.OPTIONAL;
                }
                break;
            case CharacterClass.PERCENT:
                mTokenRange.match();
                mType = XQueryTokenType.ANNOTATION_INDICATOR;
                break;
            case CharacterClass.AMPERSAND:
                mTokenRange.match();
                mType = XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING;
                break;
            default:
                mTokenRange.match();
                mType = XQueryTokenType.BAD_CHARACTER;
                break;
        }
    }

    private void stateStringLiteral(char type) {
        int c = mTokenRange.getCodePoint();
        if (c == type) {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == type) {
                mTokenRange.match();
                mType = XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.STRING_LITERAL_END;
                mNextState = STATE_DEFAULT;
            }
        } else if (c == '&') {
            matchEntityReference();
        } else if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
        } else {
            while (c != type && c != XQueryCodePointRange.END_OF_BUFFER && c != '&') {
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
            }
            mType = XQueryTokenType.STRING_LITERAL_CONTENTS;
        }
    }

    private void stateDoubleExponent() {
        mTokenRange.match();
        int c = mTokenRange.getCodePoint();
        if ((c == '+') || (c == '-')) {
            mTokenRange.match();
        }
        mType = XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT;
        mNextState = STATE_DEFAULT;
    }

    // Lexer implementation

    public XQueryLexer() {
        mTokenRange = new XQueryCodePointRange();
    }

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mTokenRange.start(buffer, startOffset, endOffset);
        mNextState = initialState;
        advance();
    }

    @Override
    public final void advance() {
        mTokenRange.flush();
        mState = mNextState;
        switch (mState) {
            case STATE_DEFAULT:
                stateDefault();
                break;
            case STATE_STRING_LITERAL_QUOTE:
                stateStringLiteral('"');
                break;
            case STATE_STRING_LITERAL_APOSTROPHE:
                stateStringLiteral('\'');
                break;
            case STATE_DOUBLE_EXPONENT:
                stateDoubleExponent();
                break;
            case STATE_XQUERY_COMMENT:
                stateXQueryComment();
                break;
            case STATE_XML_COMMENT:
                stateXmlComment();
                break;
            case STATE_UNEXPECTED_END_OF_BLOCK:
                stateUnexpectedEndOfBlock();
                break;
            case STATE_CDATA_SECTION:
                stateCDataSection();
                break;
        }
    }

    @Override
    public final int getState() {
        return mState;
    }

    @Override
    public final IElementType getTokenType() {
        return mType;
    }

    @Override
    public final int getTokenStart() {
        return mTokenRange.getStart();
    }

    @Override
    public final int getTokenEnd() {
        return mTokenRange.getEnd();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return mTokenRange.getBufferSequence();
    }

    @Override
    public final int getBufferEnd() {
        return mTokenRange.getBufferEnd();
    }

    static {
        sKeywords.put("as", XQueryTokenType.K_AS);
        sKeywords.put("ascending", XQueryTokenType.K_ASCENDING);
        sKeywords.put("at", XQueryTokenType.K_AT);
        sKeywords.put("attribute", XQueryTokenType.K_ATTRIBUTE);
        sKeywords.put("base-uri", XQueryTokenType.K_BASE_URI);
        sKeywords.put("boundary-space", XQueryTokenType.K_BOUNDARY_SPACE);
        sKeywords.put("by", XQueryTokenType.K_BY);
        sKeywords.put("collation", XQueryTokenType.K_COLLATION);
        sKeywords.put("comment", XQueryTokenType.K_COMMENT);
        sKeywords.put("construction", XQueryTokenType.K_CONSTRUCTION);
        sKeywords.put("copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES);
        sKeywords.put("declare", XQueryTokenType.K_DECLARE);
        sKeywords.put("default", XQueryTokenType.K_DEFAULT);
        sKeywords.put("descending", XQueryTokenType.K_DESCENDING);
        sKeywords.put("document-node", XQueryTokenType.K_DOCUMENT_NODE);
        sKeywords.put("element", XQueryTokenType.K_ELEMENT);
        sKeywords.put("empty", XQueryTokenType.K_EMPTY);
        sKeywords.put("empty-sequence", XQueryTokenType.K_EMPTY_SEQUENCE);
        sKeywords.put("encoding", XQueryTokenType.K_ENCODING);
        sKeywords.put("every", XQueryTokenType.K_EVERY);
        sKeywords.put("external", XQueryTokenType.K_EXTERNAL);
        sKeywords.put("for", XQueryTokenType.K_FOR);
        sKeywords.put("function", XQueryTokenType.K_FUNCTION);
        sKeywords.put("greatest", XQueryTokenType.K_GREATEST);
        sKeywords.put("import", XQueryTokenType.K_IMPORT);
        sKeywords.put("in", XQueryTokenType.K_IN);
        sKeywords.put("inherit", XQueryTokenType.K_INHERIT);
        sKeywords.put("item", XQueryTokenType.K_ITEM);
        sKeywords.put("least", XQueryTokenType.K_LEAST);
        sKeywords.put("let", XQueryTokenType.K_LET);
        sKeywords.put("module", XQueryTokenType.K_MODULE);
        sKeywords.put("namespace", XQueryTokenType.K_NAMESPACE);
        sKeywords.put("no-inherit", XQueryTokenType.K_NO_INHERIT);
        sKeywords.put("no-preserve", XQueryTokenType.K_NO_PRESERVE);
        sKeywords.put("node", XQueryTokenType.K_NODE);
        sKeywords.put("option", XQueryTokenType.K_OPTION);
        sKeywords.put("order", XQueryTokenType.K_ORDER);
        sKeywords.put("ordered", XQueryTokenType.K_ORDERED);
        sKeywords.put("ordering", XQueryTokenType.K_ORDERING);
        sKeywords.put("preserve", XQueryTokenType.K_PRESERVE);
        sKeywords.put("processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION);
        sKeywords.put("return", XQueryTokenType.K_RETURN);
        sKeywords.put("satisfies", XQueryTokenType.K_SATISFIES);
        sKeywords.put("schema", XQueryTokenType.K_SCHEMA);
        sKeywords.put("schema-attribute", XQueryTokenType.K_SCHEMA_ATTRIBUTE);
        sKeywords.put("schema-element", XQueryTokenType.K_SCHEMA_ELEMENT);
        sKeywords.put("some", XQueryTokenType.K_SOME);
        sKeywords.put("stable", XQueryTokenType.K_STABLE);
        sKeywords.put("strip", XQueryTokenType.K_STRIP);
        sKeywords.put("text", XQueryTokenType.K_TEXT);
        sKeywords.put("unordered", XQueryTokenType.K_UNORDERED);
        sKeywords.put("variable", XQueryTokenType.K_VARIABLE);
        sKeywords.put("version", XQueryTokenType.K_VERSION);
        sKeywords.put("where", XQueryTokenType.K_WHERE);
        sKeywords.put("xquery", XQueryTokenType.K_XQUERY);
    }
}
