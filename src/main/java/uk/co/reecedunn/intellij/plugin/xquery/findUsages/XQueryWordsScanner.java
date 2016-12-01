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
package uk.co.reecedunn.intellij.plugin.xquery.findUsages;

import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Processor;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.CharacterClass;
import uk.co.reecedunn.intellij.plugin.core.lexer.XQueryCodePointRange;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

public class XQueryWordsScanner implements WordsScanner {
    private final Lexer mLexer = new XQueryLexer();
    private final WordOccurrence mOccurrence = new WordOccurrence(null, 0, 0, null);
    private final XQueryCodePointRange mRange = new XQueryCodePointRange();

    @Override
    public void processWords(CharSequence fileText, Processor<WordOccurrence> processor) {
        mLexer.start(fileText);
        while (mLexer.getTokenType() != null) {
            mRange.start(fileText, mLexer.getTokenStart(), mLexer.getTokenEnd());

            IElementType type = mLexer.getTokenType();
            if (XQueryTokenType.COMMENT_TOKENS.contains(type)) {
                if (!processToken(processor, WordOccurrence.Kind.COMMENTS)) return;
            } else if (XQueryTokenType.STRING_LITERAL_TOKENS.contains(type)) {
                if (!processToken(processor, WordOccurrence.Kind.LITERALS)) return;
            } else if (XQueryTokenType.LITERAL_TOKENS.contains(type)) {
                mOccurrence.init(fileText, mLexer.getTokenStart(), mLexer.getTokenEnd(), WordOccurrence.Kind.CODE);
                if (!processor.process(mOccurrence)) return;
            } else {
                if (!processToken(processor, WordOccurrence.Kind.CODE)) return;
            }

            mLexer.advance();
        }
    }

    private boolean processToken(final Processor<WordOccurrence> processor, WordOccurrence.Kind kind) {
        boolean inWord = false;
        while (true) switch (CharacterClass.getCharClass(mRange.getCodePoint())) {
            case CharacterClass.NAME_START_CHAR:
                if (!inWord) {
                    inWord = true;
                    mRange.flush();
                }
                mRange.match();
                break;
            case CharacterClass.END_OF_BUFFER:
                if (inWord) {
                    mOccurrence.init(mRange.getBufferSequence(), mRange.getStart(), mRange.getEnd(), kind);
                    return processor.process(mOccurrence);
                }
                return true;
            case CharacterClass.DIGIT:
            case CharacterClass.DOT:
            case CharacterClass.HYPHEN_MINUS:
            case CharacterClass.NAME_CHAR:
                mRange.match();
                break;
            default:
                if (inWord) {
                    mOccurrence.init(mRange.getBufferSequence(), mRange.getStart(), mRange.getEnd(), kind);
                    if (!processor.process(mOccurrence)) return false;
                    inWord = false;
                }
                mRange.match();
                break;
        }
    }
}
