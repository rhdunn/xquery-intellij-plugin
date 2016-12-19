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
package uk.co.reecedunn.intellij.plugin.xqdoc.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class XQDocLexer extends LexerBase {
    private CodePointRange mTokenRange;
    private int mState;
    private final Stack<Integer> mStates = new Stack<>();
    private IElementType mType;

    public XQDocLexer() {
        mTokenRange = new CodePointRange();
    }

    // region States

    private void pushState(int state) {
        mStates.push(state);
    }

    private void popState() {
        try {
            mStates.pop();
        } catch (EmptyStackException e) {
            //
        }
    }

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_CONTENTS = 1;
    private static final int STATE_TAGGED_CONTENTS = 2;
    private static final int STATE_ELEM_CONSTRUCTOR = 3;
    private static final int STATE_ELEM_CONTENTS = 4;
    private static final int STATE_ELEM_CONSTRUCTOR_CLOSING = 5;
    private static final int STATE_ATTRIBUTE_VALUE_QUOTE = 6;
    private static final int STATE_ATTRIBUTE_VALUE_APOS = 7;
    private static final int STATE_TRIM = 8;

    private void stateDefault() {
        int c = mTokenRange.getCodePoint();
        switch (c) {
            case CodePointRange.END_OF_BUFFER:
                mType = null;
                break;
            case '~':
                mTokenRange.match();
                mType = XQDocTokenType.XQDOC_COMMENT_MARKER;
                pushState(STATE_CONTENTS);
                pushState(STATE_TRIM);
                break;
            default:
                mTokenRange.seek(mTokenRange.getBufferEnd());
                mType = XQDocTokenType.COMMENT_CONTENTS;
                break;
        }
    }

    private void stateContents() {
        int c = mTokenRange.getCodePoint();
        switch (c) {
            case CodePointRange.END_OF_BUFFER:
                mType = null;
                break;
            case '<':
                mTokenRange.match();
                mType = XQDocTokenType.OPEN_XML_TAG;
                pushState(STATE_ELEM_CONSTRUCTOR);
                break;
            case '\n': // U+000A
            case '\r': // U+000D
                pushState(STATE_TRIM);
                stateTrim();
                break;
            default:
                while (true) switch (c) {
                    case '\n': // U+000A
                    case '\r': // U+000D
                        pushState(STATE_TRIM);
                        // fallthrough
                    case CodePointRange.END_OF_BUFFER:
                    case '<':
                        mType = XQDocTokenType.CONTENTS;
                        return;
                    default:
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                        break;
                }
        }
    }

    private void stateTaggedContents() {
        int c = mTokenRange.getCodePoint();
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
            }
            mType = sTagNames.getOrDefault(getTokenText(), XQDocTokenType.TAG);
        } else {
            popState();
            stateContents();
        }
    }

    private void stateElemConstructor(int state) {
        int c = mTokenRange.getCodePoint();
        switch (c) {
            case CodePointRange.END_OF_BUFFER:
                mType = null;
                break;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g':
            case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n':
            case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
            case 'v': case 'w': case 'x': case 'y': case 'z':
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G':
            case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N':
            case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U':
            case 'V': case 'W': case 'X': case 'Y': case 'Z':
                while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }
                mType = XQDocTokenType.XML_TAG;
                break;
            case ' ': case '\t': case '\r': case '\n':
                while (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }
                mType = XQDocTokenType.WHITE_SPACE;
                break;
            case '=':
                mTokenRange.match();
                mType = XQDocTokenType.XML_EQUAL;
                break;
            case '"':
                mTokenRange.match();
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START;
                pushState(STATE_ATTRIBUTE_VALUE_QUOTE);
                break;
            case '\'':
                mTokenRange.match();
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START;
                pushState(STATE_ATTRIBUTE_VALUE_APOS);
                break;
            case '/':
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '>') {
                    mTokenRange.match();
                    mType = XQDocTokenType.SELF_CLOSING_XML_TAG;
                    popState();
                } else {
                    mType = XQDocTokenType.INVALID;
                }
                break;
            case '>':
                mTokenRange.match();
                mType = XQDocTokenType.END_XML_TAG;
                popState();
                if (state == STATE_ELEM_CONSTRUCTOR) {
                    pushState(STATE_ELEM_CONTENTS);
                }
                break;
            default:
                mTokenRange.match();
                mType = XQDocTokenType.INVALID;
                break;
        }
    }

    private void stateElemContents() {
        int c = mTokenRange.getCodePoint();
        switch (c) {
            case CodePointRange.END_OF_BUFFER:
                mType = null;
                break;
            case '<':
                mTokenRange.match();
                if (mTokenRange.getCodePoint() == '/') {
                    mTokenRange.match();
                    mType = XQDocTokenType.CLOSE_XML_TAG;
                    popState();
                    pushState(STATE_ELEM_CONSTRUCTOR_CLOSING);
                } else {
                    mType = XQDocTokenType.OPEN_XML_TAG;
                    pushState(STATE_ELEM_CONSTRUCTOR);
                }
                break;
            default:
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
                while (true) switch (c) {
                    case CodePointRange.END_OF_BUFFER:
                    case '<':
                        mType = XQDocTokenType.XML_ELEMENT_CONTENTS;
                        return;
                    default:
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                        break;
                }
        }
    }

    private void stateAttributeValue(int endChar) {
        int c = mTokenRange.getCodePoint();
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null;
        } else if (c == endChar) {
            mTokenRange.match();
            mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_END;
            popState();
        } else {
            while (c != CodePointRange.END_OF_BUFFER && c != endChar) {
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
            }
            mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS;
        }
    }

    private void stateTrim() {
        int c = mTokenRange.getCodePoint();
        switch (c) {
            case CodePointRange.END_OF_BUFFER:
                mType = null;
                break;
            case ' ':
            case '\t':
                while (c == ' ' || c == '\t') {
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }
                mType = XQDocTokenType.WHITE_SPACE;
                break;
            case '\r': // U+000D
            case '\n': // U+000A
                mTokenRange.match();
                if (c == '\r' && mTokenRange.getCodePoint() == '\n') {
                    mTokenRange.match();
                }

                c = mTokenRange.getCodePoint();
                while (c == ' ' || c == '\t') { // U+0020 || U+0009
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                }

                if (c == ':') {
                    mTokenRange.match();
                }

                mType = XQDocTokenType.TRIM;
                break;
            case '@':
                mTokenRange.match();
                mType = XQDocTokenType.TAG_MARKER;
                popState();
                pushState(STATE_TAGGED_CONTENTS);
                break;
            default:
                popState();
                advance();
                break;
        }
    }

    // endregion
    // region Lexer

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mTokenRange.start(buffer, startOffset, endOffset);
        pushState(initialState);
        advance();
    }

    @Override
    public final void advance() {
        mTokenRange.flush();
        try {
            mState = mStates.peek();
        } catch (EmptyStackException e) {
            mState = STATE_DEFAULT;
        }
        switch (mState) {
            case STATE_DEFAULT:
                stateDefault();
                break;
            case STATE_CONTENTS:
                stateContents();
                break;
            case STATE_TAGGED_CONTENTS:
                stateTaggedContents();
                break;
            case STATE_ELEM_CONSTRUCTOR:
            case STATE_ELEM_CONSTRUCTOR_CLOSING:
                stateElemConstructor(mState);
                break;
            case STATE_ELEM_CONTENTS:
                stateElemContents();
                break;
            case STATE_ATTRIBUTE_VALUE_QUOTE:
                stateAttributeValue('"');
                break;
            case STATE_ATTRIBUTE_VALUE_APOS:
                stateAttributeValue('\'');
                break;
            case STATE_TRIM:
                stateTrim();
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
    // region Special Tag Names

    private static final Map<String, IElementType> sTagNames = new HashMap<>();

    static {
        sTagNames.put("author", XQDocTokenType.T_AUTHOR);
        sTagNames.put("deprecated", XQDocTokenType.T_DEPRECATED);
        sTagNames.put("error", XQDocTokenType.T_ERROR);
        sTagNames.put("param", XQDocTokenType.T_PARAM);
        sTagNames.put("return", XQDocTokenType.T_RETURN);
        sTagNames.put("see", XQDocTokenType.T_SEE);
        sTagNames.put("since", XQDocTokenType.T_SINCE);
        sTagNames.put("version", XQDocTokenType.T_VERSION);
    }

    // endregion
}
