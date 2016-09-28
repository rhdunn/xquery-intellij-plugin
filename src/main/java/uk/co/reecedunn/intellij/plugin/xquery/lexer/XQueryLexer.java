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
import java.util.Stack;

public class XQueryLexer extends LexerBase {
    private XQueryCodePointRange mTokenRange;
    private int mState;
    private final Stack<Integer> mStates = new Stack<>();
    private IElementType mType;

    public XQueryLexer() {
        mTokenRange = new XQueryCodePointRange();
    }

    // region States

    private void pushState(int state) {
        mStates.push(state);
    }

    private void popState() {
        if (!mStates.empty()) {
            mStates.pop();
        }
    }

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_STRING_LITERAL_QUOTE = 1;
    private static final int STATE_STRING_LITERAL_APOSTROPHE = 2;
    private static final int STATE_DOUBLE_EXPONENT = 3;
    private static final int STATE_XQUERY_COMMENT = 4;
    private static final int STATE_XML_COMMENT = 5;
    private static final int STATE_UNEXPECTED_END_OF_BLOCK = 6;
    private static final int STATE_CDATA_SECTION = 7;
    private static final int STATE_PRAGMA_PRE_QNAME = 8;
    private static final int STATE_PRAGMA_QNAME = 9;
    private static final int STATE_PRAGMA_CONTENTS = 10;
    private static final int STATE_DIR_ELEM_CONSTRUCTOR = 11;
    private static final int STATE_DIR_ELEM_CONSTRUCTOR_CLOSING = 12;
    private static final int STATE_DIR_ATTRIBUTE_VALUE_QUOTE = 13;
    private static final int STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE = 14;
    private static final int STATE_DEFAULT_ATTRIBUTE_QUOT = 15;
    private static final int STATE_DEFAULT_ATTRIBUTE_APOSTROPHE = 16;
    private static final int STATE_DIR_ELEM_CONTENT = 17;
    private static final int STATE_DEFAULT_ELEM_CONTENT = 18;
    private static final int STATE_XML_COMMENT_ELEM_CONTENT = 19;
    private static final int STATE_CDATA_SECTION_ELEM_CONTENT = 20;
    private static final int STATE_PROCESSING_INSTRUCTION = 21;
    private static final int STATE_PROCESSING_INSTRUCTION_CONTENTS = 22;
    private static final int STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT = 23;
    private static final int STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT = 24;
    private static final int STATE_DIR_ATTRIBUTE_LIST = 25;
    private static final int STATE_BRACED_URI_LITERAL = 26;

    private void stateDefault(int mState) {
        int c = mTokenRange.getCodePoint();
        int cc = CharacterClass.getCharClass(c);
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
                        pushState(STATE_DOUBLE_EXPONENT);
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
                pushState((cc == CharacterClass.QUOTE) ? STATE_STRING_LITERAL_QUOTE : STATE_STRING_LITERAL_APOSTROPHE);
                break;
            case CharacterClass.NAME_START_CHAR:
                mTokenRange.match();
                cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
                if (c == 'Q' && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match();
                    mType = XQueryTokenType.BRACED_URI_LITERAL_START;
                    pushState(STATE_BRACED_URI_LITERAL);
                } else {
                    while (cc == CharacterClass.NAME_START_CHAR ||
                           cc == CharacterClass.DIGIT ||
                           cc == CharacterClass.DOT ||
                           cc == CharacterClass.HYPHEN_MINUS ||
                           cc == CharacterClass.NAME_CHAR) {
                        mTokenRange.match();
                        cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
                    }
                    mType = sKeywords.getOrDefault(getTokenText(), XQueryTokenType.NCNAME);
                }
                break;
            case CharacterClass.PARENTHESIS_OPEN:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == ':') {
                    mTokenRange.match();
                    mType = XQueryTokenType.COMMENT_START_TAG;
                    pushState(STATE_XQUERY_COMMENT);
                } else if (c == '#') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PRAGMA_BEGIN;
                    pushState(STATE_PRAGMA_PRE_QNAME);
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
                    mTokenRange.save();
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '>') {
                        mTokenRange.match();
                        mType = XQueryTokenType.XML_COMMENT_END_TAG;
                    } else {
                        mTokenRange.restore();
                        mType = XQueryTokenType.MINUS;
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
                    pushState(STATE_PROCESSING_INSTRUCTION);
                } else if (c == '!') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '-') {
                        mTokenRange.match();
                        if (mTokenRange.getCodePoint() == '-') {
                            mTokenRange.match();
                            mType = XQueryTokenType.XML_COMMENT_START_TAG;
                            pushState(STATE_XML_COMMENT);
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
                                                pushState(STATE_CDATA_SECTION);
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
                    if (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.NAME_START_CHAR) {
                        mType = XQueryTokenType.OPEN_XML_TAG;
                        pushState(STATE_DIR_ELEM_CONSTRUCTOR);
                    } else {
                        mType = XQueryTokenType.LESS_THAN;
                    }
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
                pushState(mState);
                break;
            case CharacterClass.CURLY_BRACE_CLOSE:
                mTokenRange.match();
                mType = XQueryTokenType.BLOCK_CLOSE;
                popState();
                break;
            case CharacterClass.VERTICAL_BAR:
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '|') {
                    mTokenRange.match();
                    mType = XQueryTokenType.CONCATENATION;
                } else {
                    mType = XQueryTokenType.UNION;
                }
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
            if (mTokenRange.getCodePoint() == type && type != '}') {
                mTokenRange.match();
                mType = XQueryTokenType.ESCAPED_CHARACTER;
            } else {
                mType = type == '}' ? XQueryTokenType.BRACED_URI_LITERAL_END : XQueryTokenType.STRING_LITERAL_END;
                popState();
            }
        } else if (c == '&') {
            matchEntityReference(type == '"' ? STATE_STRING_LITERAL_QUOTE : STATE_STRING_LITERAL_APOSTROPHE);
        } else if (c == '{' && type == '}') {
            mTokenRange.match();
            mType = XQueryTokenType.BAD_CHARACTER;
        } else if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
        } else {
            while (c != type && c != XQueryCodePointRange.END_OF_BUFFER && c != '&' && !(type == '}' && c == '{')) {
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
        popState();
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
                popState();
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
                popState();
                pushState(STATE_UNEXPECTED_END_OF_BLOCK);
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
                    popState();
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
                popState();
                pushState(STATE_UNEXPECTED_END_OF_BLOCK);
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

    private void stateUnexpectedEndOfBlock() {
        mType = XQueryTokenType.UNEXPECTED_END_OF_BLOCK;
        popState();
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
                    popState();
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
                popState();
                pushState(STATE_UNEXPECTED_END_OF_BLOCK);
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

    private void statePragmaPreQName() {
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        switch (cc) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                break;
            case CharacterClass.COLON:
                mTokenRange.match();
                mType = XQueryTokenType.QNAME_SEPARATOR;
                popState();
                pushState(STATE_PRAGMA_QNAME);
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
                mType = XQueryTokenType.NCNAME;
                popState();
                pushState(STATE_PRAGMA_QNAME);
                break;
            default:
                popState();
                pushState(STATE_PRAGMA_CONTENTS);
                statePragmaContents();
                break;
        }
    }

    private void statePragmaQName() {
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        switch (cc) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                popState();
                pushState(STATE_PRAGMA_CONTENTS);
                break;
            case CharacterClass.COLON:
                mTokenRange.match();
                mType = XQueryTokenType.QNAME_SEPARATOR;
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
                mType = XQueryTokenType.NCNAME;
                break;
            default:
                popState();
                pushState(STATE_PRAGMA_CONTENTS);
                statePragmaContents();
                break;
        }
    }

    private void statePragmaContents() {
        int c = mTokenRange.getCodePoint();
        if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
            return;
        } else if (c == '#') {
            mTokenRange.save();
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == ')') {
                mTokenRange.match();
                mType = XQueryTokenType.PRAGMA_END;
                popState();
                return;
            } else {
                mTokenRange.restore();
            }
        }

        while (true) {
            if (c == XQueryCodePointRange.END_OF_BUFFER) {
                mTokenRange.match();
                mType = XQueryTokenType.PRAGMA_CONTENTS;
                popState();
                pushState(STATE_UNEXPECTED_END_OF_BLOCK);
                return;
            } else if (c == '#') {
                mTokenRange.save();
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == ')') {
                    mTokenRange.restore();
                    mType = XQueryTokenType.PRAGMA_CONTENTS;
                    return;
                }
            } else {
                mTokenRange.match();
            }
            c = mTokenRange.getCodePoint();
        }
    }

    private void stateDirElemConstructor(int state) {
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        int c;
        switch (cc) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.XML_WHITE_SPACE;
                if (state == STATE_DIR_ELEM_CONSTRUCTOR) {
                    popState();
                    pushState(STATE_DIR_ATTRIBUTE_LIST);
                }
                break;
            case CharacterClass.COLON:
                mTokenRange.match();
                mType = (state == STATE_DIR_ATTRIBUTE_LIST) ? XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR : XQueryTokenType.XML_TAG_QNAME_SEPARATOR;
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
                mType = (state == STATE_DIR_ATTRIBUTE_LIST) ? XQueryTokenType.XML_ATTRIBUTE_NCNAME : XQueryTokenType.XML_TAG_NCNAME;
                break;
            case CharacterClass.GREATER_THAN:
                mTokenRange.match();
                mType = XQueryTokenType.END_XML_TAG;
                popState();
                if (state == STATE_DIR_ELEM_CONSTRUCTOR || state == STATE_DIR_ATTRIBUTE_LIST) {
                    pushState(STATE_DIR_ELEM_CONTENT);
                }
                break;
            case CharacterClass.FORWARD_SLASH:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                if (c == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.SELF_CLOSING_XML_TAG;
                    popState();
                } else {
                    mType = XQueryTokenType.INVALID;
                }
                break;
            case CharacterClass.EQUAL:
                mTokenRange.match();
                mType = XQueryTokenType.XML_EQUAL;
                break;
            case CharacterClass.QUOTE:
            case CharacterClass.APOSTROPHE:
                mTokenRange.match();
                mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_START;
                pushState((cc == CharacterClass.QUOTE) ? STATE_DIR_ATTRIBUTE_VALUE_QUOTE : STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE);
                break;
            case CharacterClass.END_OF_BUFFER:
                mType = null;
                break;
            default:
                mTokenRange.match();
                mType = XQueryTokenType.BAD_CHARACTER;
                break;
        }
    }

    private void stateDirAttributeValue(char type) {
        int c = mTokenRange.getCodePoint();
        if (c == type) {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == type) {
                mTokenRange.match();
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_END;
                popState();
            }
        } else if (c == '{') {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '{') {
                mTokenRange.match();
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.BLOCK_OPEN;
                pushState((type == '"') ? STATE_DEFAULT_ATTRIBUTE_QUOT : STATE_DEFAULT_ATTRIBUTE_APOSTROPHE);
            }
        } else if (c == '}') {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '}') {
                mTokenRange.match();
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.BLOCK_CLOSE;
            }
        } else if (c == '<') {
            mTokenRange.match();
            mType = XQueryTokenType.BAD_CHARACTER;
        } else if (c == '&') {
            matchEntityReference(type == '"' ? STATE_DIR_ATTRIBUTE_VALUE_QUOTE : STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE);
        } else if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
        } else {
            while (true) {
                switch (c) {
                    case XQueryCodePointRange.END_OF_BUFFER:
                    case '{':
                    case '}':
                    case '<':
                    case '&':
                        mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS;
                        return;
                    default:
                        if (c == type) {
                            mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS;
                            return;
                        } else {
                            mTokenRange.match();
                            c = mTokenRange.getCodePoint();
                        }
                }
            }
        }
    }

    private void stateDirElemContent() {
        int c = mTokenRange.getCodePoint();
        if (c == '{') {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '{') {
                mTokenRange.match();
                mType = XQueryTokenType.ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.BLOCK_OPEN;
                pushState(STATE_DEFAULT_ELEM_CONTENT);
            }
        } else if (c == '}') {
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '}') {
                mTokenRange.match();
                mType = XQueryTokenType.ESCAPED_CHARACTER;
            } else {
                mType = XQueryTokenType.BLOCK_CLOSE;
            }
        } else if (c == '<') {
            mTokenRange.match();
            c = mTokenRange.getCodePoint();
            if (c == '/') {
                mTokenRange.match();
                mType = XQueryTokenType.CLOSE_XML_TAG;
                popState();
                pushState(STATE_DIR_ELEM_CONSTRUCTOR_CLOSING);
            } else if (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.NAME_START_CHAR) {
                mType = XQueryTokenType.OPEN_XML_TAG;
                pushState(STATE_DIR_ELEM_CONSTRUCTOR);
            } else if (c == '?') {
                mTokenRange.match();
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN;
                pushState(STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT);
            } else if (c == '!') {
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '-') {
                    mTokenRange.match();
                    if (mTokenRange.getCodePoint() == '-') {
                        mTokenRange.match();
                        mType = XQueryTokenType.XML_COMMENT_START_TAG;
                        pushState(STATE_XML_COMMENT_ELEM_CONTENT);
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
                                            pushState(STATE_CDATA_SECTION_ELEM_CONTENT);
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
                mType = XQueryTokenType.BAD_CHARACTER;
            }
        } else if (c == '&') {
            matchEntityReference(STATE_DIR_ELEM_CONTENT);
        } else if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
        } else {
            while (true) {
                switch (c) {
                    case XQueryCodePointRange.END_OF_BUFFER:
                    case '{':
                    case '}':
                    case '<':
                    case '&':
                        mType = XQueryTokenType.XML_ELEMENT_CONTENTS;
                        return;
                    default:
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                }
            }
        }
    }

    private void stateProcessingInstruction(int state) {
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        switch (cc) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                popState();
                pushState((state == STATE_PROCESSING_INSTRUCTION) ? STATE_PROCESSING_INSTRUCTION_CONTENTS : STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT);
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
                mType = XQueryTokenType.NCNAME;
                break;
            case CharacterClass.QUESTION_MARK:
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '>') {
                    mTokenRange.match();
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_END;
                    popState();
                } else {
                    mType = XQueryTokenType.INVALID;
                }
                break;
            case CharacterClass.END_OF_BUFFER:
                mType = null;
                break;
            default:
                mTokenRange.match();
                mType = XQueryTokenType.BAD_CHARACTER;
                break;
        }
    }

    private void stateProcessingInstructionContents() {
        int c = mTokenRange.getCodePoint();
        if (c == XQueryCodePointRange.END_OF_BUFFER) {
            mType = null;
            return;
        } else if (c == '?') {
            mTokenRange.save();
            mTokenRange.match();
            if (mTokenRange.getCodePoint() == '>') {
                mTokenRange.match();
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_END;
                popState();
                return;
            } else {
                mTokenRange.restore();
            }
        }

        while (true) {
            if (c == XQueryCodePointRange.END_OF_BUFFER) {
                mTokenRange.match();
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS;
                popState();
                pushState(STATE_UNEXPECTED_END_OF_BLOCK);
                return;
            } else if (c == '?') {
                mTokenRange.save();
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '>') {
                    mTokenRange.restore();
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS;
                    return;
                }
            } else {
                mTokenRange.match();
            }
            c = mTokenRange.getCodePoint();
        }
    }

    // endregion
    // region Helper Functions

    private void matchEntityReference(int state) {
        boolean isAttributeValue = (state == STATE_DIR_ATTRIBUTE_VALUE_QUOTE) || (state == STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE);
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
                mType = isAttributeValue ? XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE : XQueryTokenType.PREDEFINED_ENTITY_REFERENCE;
            } else {
                mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
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
                        mType = isAttributeValue ? XQueryTokenType.XML_CHARACTER_REFERENCE : XQueryTokenType.CHARACTER_REFERENCE;
                    } else {
                        mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                    }
                } else if (c == ';') {
                    mTokenRange.match();
                    mType = isAttributeValue ? XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE : XQueryTokenType.EMPTY_ENTITY_REFERENCE;
                } else {
                    mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                }
            } else if ((c >= '0') && (c <= '9')) {
                mTokenRange.match();
                while ((c >= '0') && (c <= '9')) {
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }
                if (c == ';') {
                    mTokenRange.match();
                    mType = isAttributeValue ? XQueryTokenType.XML_CHARACTER_REFERENCE : XQueryTokenType.CHARACTER_REFERENCE;
                } else {
                    mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
                }
            } else if (c == ';') {
                mTokenRange.match();
                mType = isAttributeValue ? XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE : XQueryTokenType.EMPTY_ENTITY_REFERENCE;
            } else {
                mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
            }
        } else if (cc == CharacterClass.SEMICOLON) {
            mTokenRange.match();
            mType = isAttributeValue ? XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE : XQueryTokenType.EMPTY_ENTITY_REFERENCE;
        } else {
            mType = isAttributeValue ? XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE : XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
        }
    }

    // endregion
    // region Lexer

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mTokenRange.start(buffer, startOffset, endOffset);
        mStates.clear();

        if (initialState != STATE_DEFAULT) {
            pushState(STATE_DEFAULT);
        }
        pushState(initialState);
        advance();
    }

    @Override
    public final void advance() {
        mTokenRange.flush();
        if (mStates.isEmpty()) {
            mState = STATE_DEFAULT;
        } else {
            mState = mStates.peek();
        }
        switch (mState) {
            case STATE_DEFAULT:
            case STATE_DEFAULT_ATTRIBUTE_QUOT:
            case STATE_DEFAULT_ATTRIBUTE_APOSTROPHE:
            case STATE_DEFAULT_ELEM_CONTENT:
                stateDefault(mState);
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
            case STATE_XML_COMMENT_ELEM_CONTENT:
                stateXmlComment();
                break;
            case STATE_UNEXPECTED_END_OF_BLOCK:
                stateUnexpectedEndOfBlock();
                break;
            case STATE_CDATA_SECTION:
            case STATE_CDATA_SECTION_ELEM_CONTENT:
                stateCDataSection();
                break;
            case STATE_PRAGMA_PRE_QNAME:
                statePragmaPreQName();
                break;
            case STATE_PRAGMA_QNAME:
                statePragmaQName();
                break;
            case STATE_PRAGMA_CONTENTS:
                statePragmaContents();
                break;
            case STATE_DIR_ELEM_CONSTRUCTOR:
            case STATE_DIR_ELEM_CONSTRUCTOR_CLOSING:
            case STATE_DIR_ATTRIBUTE_LIST:
                stateDirElemConstructor(mState);
                break;
            case STATE_DIR_ATTRIBUTE_VALUE_QUOTE:
                stateDirAttributeValue('"');
                break;
            case STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE:
                stateDirAttributeValue('\'');
                break;
            case STATE_DIR_ELEM_CONTENT:
                stateDirElemContent();
                break;
            case STATE_PROCESSING_INSTRUCTION:
            case STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT:
                stateProcessingInstruction(mState);
                break;
            case STATE_PROCESSING_INSTRUCTION_CONTENTS:
            case STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT:
                stateProcessingInstructionContents();
                break;
            case STATE_BRACED_URI_LITERAL:
                stateStringLiteral('}');
                break;
            default:
                throw new AssertionError("Invalid state: " + mState);
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

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public CharSequence getBufferSequence() {
        return mTokenRange.getBufferSequence();
    }

    @Override
    public final int getBufferEnd() {
        return mTokenRange.getBufferEnd();
    }

    // endregion
    // region Keywords

    private static final Map<String, IElementType> sKeywords = new HashMap<>();

    static {
        sKeywords.put("after", XQueryTokenType.K_AFTER); // Update Facility 1.0
        sKeywords.put("allowing", XQueryTokenType.K_ALLOWING); // XQuery 3.0
        sKeywords.put("ancestor", XQueryTokenType.K_ANCESTOR);
        sKeywords.put("ancestor-or-self", XQueryTokenType.K_ANCESTOR_OR_SELF);
        sKeywords.put("and", XQueryTokenType.K_AND);
        sKeywords.put("array-node", XQueryTokenType.K_ARRAY_NODE); // MarkLogic 8.0
        sKeywords.put("as", XQueryTokenType.K_AS);
        sKeywords.put("ascending", XQueryTokenType.K_ASCENDING);
        sKeywords.put("at", XQueryTokenType.K_AT);
        sKeywords.put("attribute", XQueryTokenType.K_ATTRIBUTE);
        sKeywords.put("base-uri", XQueryTokenType.K_BASE_URI);
        sKeywords.put("before", XQueryTokenType.K_BEFORE); // Update Facility 1.0
        sKeywords.put("binary", XQueryTokenType.K_BINARY); // MarkLogic 6.0
        sKeywords.put("boolean-node", XQueryTokenType.K_BOOLEAN_NODE); // MarkLogic 8.0
        sKeywords.put("boundary-space", XQueryTokenType.K_BOUNDARY_SPACE);
        sKeywords.put("by", XQueryTokenType.K_BY);
        sKeywords.put("case", XQueryTokenType.K_CASE);
        sKeywords.put("cast", XQueryTokenType.K_CAST);
        sKeywords.put("castable", XQueryTokenType.K_CASTABLE);
        sKeywords.put("catch", XQueryTokenType.K_CATCH); // XQuery 3.0
        sKeywords.put("child", XQueryTokenType.K_CHILD);
        sKeywords.put("collation", XQueryTokenType.K_COLLATION);
        sKeywords.put("comment", XQueryTokenType.K_COMMENT);
        sKeywords.put("construction", XQueryTokenType.K_CONSTRUCTION);
        sKeywords.put("context", XQueryTokenType.K_CONTEXT); // XQuery 3.0
        sKeywords.put("copy", XQueryTokenType.K_COPY); // Update Facility 1.0
        sKeywords.put("copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES);
        sKeywords.put("count", XQueryTokenType.K_COUNT); // XQuery 3.0
        sKeywords.put("decimal-format", XQueryTokenType.K_DECIMAL_FORMAT); // XQuery 3.0
        sKeywords.put("decimal-separator", XQueryTokenType.K_DECIMAL_SEPARATOR); // XQuery 3.0
        sKeywords.put("declare", XQueryTokenType.K_DECLARE);
        sKeywords.put("default", XQueryTokenType.K_DEFAULT);
        sKeywords.put("delete", XQueryTokenType.K_DELETE); // Update Facility 1.0
        sKeywords.put("descendant", XQueryTokenType.K_DESCENDANT);
        sKeywords.put("descendant-or-self", XQueryTokenType.K_DESCENDANT_OR_SELF);
        sKeywords.put("descending", XQueryTokenType.K_DESCENDING);
        sKeywords.put("digit", XQueryTokenType.K_DIGIT); // XQuery 3.0
        sKeywords.put("div", XQueryTokenType.K_DIV);
        sKeywords.put("document", XQueryTokenType.K_DOCUMENT);
        sKeywords.put("document-node", XQueryTokenType.K_DOCUMENT_NODE);
        sKeywords.put("element", XQueryTokenType.K_ELEMENT);
        sKeywords.put("else", XQueryTokenType.K_ELSE);
        sKeywords.put("empty", XQueryTokenType.K_EMPTY);
        sKeywords.put("empty-sequence", XQueryTokenType.K_EMPTY_SEQUENCE);
        sKeywords.put("encoding", XQueryTokenType.K_ENCODING);
        sKeywords.put("end", XQueryTokenType.K_END); // XQuery 3.0
        sKeywords.put("eq", XQueryTokenType.K_EQ);
        sKeywords.put("every", XQueryTokenType.K_EVERY);
        sKeywords.put("except", XQueryTokenType.K_EXCEPT);
        sKeywords.put("external", XQueryTokenType.K_EXTERNAL);
        sKeywords.put("first", XQueryTokenType.K_FIRST); // Update Facility 1.0
        sKeywords.put("following", XQueryTokenType.K_FOLLOWING);
        sKeywords.put("following-sibling", XQueryTokenType.K_FOLLOWING_SIBLING);
        sKeywords.put("for", XQueryTokenType.K_FOR);
        sKeywords.put("function", XQueryTokenType.K_FUNCTION);
        sKeywords.put("ge", XQueryTokenType.K_GE);
        sKeywords.put("greatest", XQueryTokenType.K_GREATEST);
        sKeywords.put("group", XQueryTokenType.K_GROUP); // XQuery 3.0
        sKeywords.put("grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR); // XQuery 3.0
        sKeywords.put("gt", XQueryTokenType.K_GT);
        sKeywords.put("idiv", XQueryTokenType.K_IDIV);
        sKeywords.put("if", XQueryTokenType.K_IF);
        sKeywords.put("import", XQueryTokenType.K_IMPORT);
        sKeywords.put("in", XQueryTokenType.K_IN);
        sKeywords.put("infinity", XQueryTokenType.K_INFINITY); // XQuery 3.0
        sKeywords.put("inherit", XQueryTokenType.K_INHERIT);
        sKeywords.put("insert", XQueryTokenType.K_INSERT); // Update Facility 1.0
        sKeywords.put("instance", XQueryTokenType.K_INSTANCE);
        sKeywords.put("intersect", XQueryTokenType.K_INTERSECT);
        sKeywords.put("into", XQueryTokenType.K_INTO); // Update Facility 1.0
        sKeywords.put("is", XQueryTokenType.K_IS);
        sKeywords.put("item", XQueryTokenType.K_ITEM);
        sKeywords.put("last", XQueryTokenType.K_LAST); // Update Facility 1.0
        sKeywords.put("lax", XQueryTokenType.K_LAX);
        sKeywords.put("le", XQueryTokenType.K_LE);
        sKeywords.put("least", XQueryTokenType.K_LEAST);
        sKeywords.put("let", XQueryTokenType.K_LET);
        sKeywords.put("lt", XQueryTokenType.K_LT);
        sKeywords.put("minus-sign", XQueryTokenType.K_MINUS_SIGN); // XQuery 3.0
        sKeywords.put("mod", XQueryTokenType.K_MOD);
        sKeywords.put("modify", XQueryTokenType.K_MODIFY); // Update Facility 1.0
        sKeywords.put("module", XQueryTokenType.K_MODULE);
        sKeywords.put("namespace", XQueryTokenType.K_NAMESPACE);
        sKeywords.put("namespace-node", XQueryTokenType.K_NAMESPACE_NODE); // XQuery 3.0
        sKeywords.put("NaN", XQueryTokenType.K_NAN); // XQuery 3.0
        sKeywords.put("ne", XQueryTokenType.K_NE);
        sKeywords.put("next", XQueryTokenType.K_NEXT); // XQuery 3.0
        sKeywords.put("no-inherit", XQueryTokenType.K_NO_INHERIT);
        sKeywords.put("no-preserve", XQueryTokenType.K_NO_PRESERVE);
        sKeywords.put("node", XQueryTokenType.K_NODE);
        sKeywords.put("nodes", XQueryTokenType.K_NODES); // Update Facility 1.0
        sKeywords.put("null-node", XQueryTokenType.K_NULL_NODE); // MarkLogic 8.0
        sKeywords.put("number-node", XQueryTokenType.K_NUMBER_NODE); // MarkLogic 8.0
        sKeywords.put("object-node", XQueryTokenType.K_OBJECT_NODE); // MarkLogic 8.0
        sKeywords.put("of", XQueryTokenType.K_OF);
        sKeywords.put("only", XQueryTokenType.K_ONLY); // XQuery 3.0
        sKeywords.put("option", XQueryTokenType.K_OPTION);
        sKeywords.put("or", XQueryTokenType.K_OR);
        sKeywords.put("order", XQueryTokenType.K_ORDER);
        sKeywords.put("ordered", XQueryTokenType.K_ORDERED);
        sKeywords.put("ordering", XQueryTokenType.K_ORDERING);
        sKeywords.put("parent", XQueryTokenType.K_PARENT);
        sKeywords.put("pattern-separator", XQueryTokenType.K_PATTERN_SEPARATOR); // XQuery 3.0
        sKeywords.put("per-mille", XQueryTokenType.K_PER_MILLE); // XQuery 3.0
        sKeywords.put("percent", XQueryTokenType.K_PERCENT); // XQuery 3.0
        sKeywords.put("preceding", XQueryTokenType.K_PRECEDING);
        sKeywords.put("preceding-sibling", XQueryTokenType.K_PRECEDING_SIBLING);
        sKeywords.put("preserve", XQueryTokenType.K_PRESERVE);
        sKeywords.put("previous", XQueryTokenType.K_PREVIOUS); // XQuery 3.0
        sKeywords.put("private", XQueryTokenType.K_PRIVATE); // MarkLogic 6.0
        sKeywords.put("processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION);
        sKeywords.put("property", XQueryTokenType.K_PROPERTY); // MarkLogic 6.0
        sKeywords.put("rename", XQueryTokenType.K_RENAME); // Update Facility 1.0
        sKeywords.put("replace", XQueryTokenType.K_REPLACE); // Update Facility 1.0
        sKeywords.put("return", XQueryTokenType.K_RETURN);
        sKeywords.put("revalidation", XQueryTokenType.K_REVALIDATION); // Update Facility 1.0
        sKeywords.put("satisfies", XQueryTokenType.K_SATISFIES);
        sKeywords.put("schema", XQueryTokenType.K_SCHEMA);
        sKeywords.put("schema-attribute", XQueryTokenType.K_SCHEMA_ATTRIBUTE);
        sKeywords.put("schema-element", XQueryTokenType.K_SCHEMA_ELEMENT);
        sKeywords.put("self", XQueryTokenType.K_SELF);
        sKeywords.put("skip", XQueryTokenType.K_SKIP); // Update Facility 1.0
        sKeywords.put("sliding", XQueryTokenType.K_SLIDING); // XQuery 3.0
        sKeywords.put("some", XQueryTokenType.K_SOME);
        sKeywords.put("stable", XQueryTokenType.K_STABLE);
        sKeywords.put("start", XQueryTokenType.K_START); // XQuery 3.0
        sKeywords.put("strict", XQueryTokenType.K_STRICT);
        sKeywords.put("strip", XQueryTokenType.K_STRIP);
        sKeywords.put("stylesheet", XQueryTokenType.K_STYLESHEET); // MarkLogic 6.0
        sKeywords.put("switch", XQueryTokenType.K_SWITCH); // XQuery 3.0
        sKeywords.put("text", XQueryTokenType.K_TEXT);
        sKeywords.put("then", XQueryTokenType.K_THEN);
        sKeywords.put("to", XQueryTokenType.K_TO);
        sKeywords.put("treat", XQueryTokenType.K_TREAT);
        sKeywords.put("try", XQueryTokenType.K_TRY); // XQuery 3.0
        sKeywords.put("tumbling", XQueryTokenType.K_TUMBLING); // XQuery 3.0
        sKeywords.put("type", XQueryTokenType.K_TYPE); // XQuery 3.0
        sKeywords.put("typeswitch", XQueryTokenType.K_TYPESWITCH);
        sKeywords.put("union", XQueryTokenType.K_UNION);
        sKeywords.put("unordered", XQueryTokenType.K_UNORDERED);
        sKeywords.put("updating", XQueryTokenType.K_UPDATING); // Update Facility 1.0
        sKeywords.put("validate", XQueryTokenType.K_VALIDATE);
        sKeywords.put("value", XQueryTokenType.K_VALUE); // Update Facility 1.0
        sKeywords.put("variable", XQueryTokenType.K_VARIABLE);
        sKeywords.put("version", XQueryTokenType.K_VERSION);
        sKeywords.put("when", XQueryTokenType.K_WHEN); // XQuery 3.0
        sKeywords.put("where", XQueryTokenType.K_WHERE);
        sKeywords.put("window", XQueryTokenType.K_WINDOW); // XQuery 3.0
        sKeywords.put("with", XQueryTokenType.K_WITH); // Update Facility 1.0
        sKeywords.put("xquery", XQueryTokenType.K_XQUERY);
        sKeywords.put("zero-digit", XQueryTokenType.K_ZERO_DIGIT); // XQuery 3.0
    }

    // endregion
}
