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

public class XQueryLexer extends LexerBase {
    private XQueryCodePointRange mTokenRange;
    private int mState;
    private int mNextState;
    private IElementType mType;

    // States

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_STRING_LITERAL_QUOTE = 1;
    private static final int STATE_STRING_LITERAL_APOSTROPHE = 2;
    private static final int STATE_DOUBLE_EXPONENT = 3;

    private void matchEntityReference() {
        mTokenRange.match();
        int cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        if (cc == CharacterClass.LETTER) {
            mTokenRange.match();
            cc = CharacterClass.getCharClass(mTokenRange.getCodePoint());
            while (cc == CharacterClass.LETTER || cc == CharacterClass.DIGIT) {
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
            } else {
                mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
            }
        } else {
            mType = XQueryTokenType.PARTIAL_ENTITY_REFERENCE;
        }
    }

    private void stateDefault() {
        int initialClass = CharacterClass.getCharClass(mTokenRange.getCodePoint());
        int c;
        switch (initialClass) {
            case CharacterClass.WHITESPACE:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                break;
            case CharacterClass.DOT:
            case CharacterClass.DIGIT:
                mTokenRange.match();
                while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DIGIT)
                    mTokenRange.match();
                if (initialClass != CharacterClass.DOT && CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DOT) {
                    mTokenRange.match();
                    while (CharacterClass.getCharClass(mTokenRange.getCodePoint()) == CharacterClass.DIGIT)
                        mTokenRange.match();
                    mType = XQueryTokenType.DECIMAL_LITERAL;
                } else {
                    mType = initialClass == CharacterClass.DOT ? XQueryTokenType.DECIMAL_LITERAL : XQueryTokenType.INTEGER_LITERAL;
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
                mNextState = (initialClass == CharacterClass.QUOTE) ? STATE_STRING_LITERAL_QUOTE : STATE_STRING_LITERAL_APOSTROPHE;
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
        } else if (c == '\0') {
            mType = null;
        } else {
            while (c != type && c != '\0' && c != '&') {
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
            c = mTokenRange.getCodePoint();
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
}
