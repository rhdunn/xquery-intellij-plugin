/*
 * Copyright (C) 2016, 2019-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lang.findUsages

import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.lexer.Lexer
import com.intellij.util.Processor
import uk.co.reecedunn.intellij.plugin.core.lexer.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

open class XPathWordsScanner(protected val mLexer: Lexer = XPathLexer()) : WordsScanner {
    protected val mOccurrence: WordOccurrence = WordOccurrence(null, 0, 0, null)
    protected val characters: XmlCharReader = XmlCharReader()

    override fun processWords(fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        mLexer.start(fileText)
        while (mLexer.tokenType != null) {
            characters.reset(fileText, mLexer.tokenStart, mLexer.tokenEnd)

            val type = mLexer.tokenType
            if (XPathTokenType.COMMENT_TOKENS.contains(type)) {
                if (!processToken(processor, WordOccurrence.Kind.COMMENTS)) return
            } else if (XPathTokenType.STRING_LITERAL_TOKENS.contains(type)) {
                if (!processToken(processor, WordOccurrence.Kind.LITERALS)) return
            } else if (XPathTokenType.LITERAL_TOKENS.contains(type)) {
                mOccurrence.init(fileText, mLexer.tokenStart, mLexer.tokenEnd, WordOccurrence.Kind.CODE)
                if (!processor.process(mOccurrence)) return
            } else {
                if (!processToken(processor, WordOccurrence.Kind.CODE)) return
            }

            mLexer.advance()
        }
    }

    protected fun processToken(processor: Processor<in WordOccurrence>, kind: WordOccurrence.Kind): Boolean {
        var inWord = false
        var startOffset = characters.currentOffset
        while (true)
            when (characters.currentChar) {
                in NameStartChar -> {
                    if (!inWord && characters.currentChar != Colon) {
                        inWord = true
                        startOffset = characters.currentOffset
                    }
                    characters.advance()
                }

                XmlCharReader.EndOfBuffer -> {
                    if (inWord) {
                        mOccurrence.init(characters.buffer, startOffset, characters.currentOffset, kind)
                        return processor.process(mOccurrence)
                    }
                    return true
                }

                in NameChar -> characters.advance()
                else -> {
                    if (inWord) {
                        mOccurrence.init(characters.buffer, startOffset, characters.currentOffset, kind)
                        if (!processor.process(mOccurrence)) return false
                        inWord = false
                    }
                    characters.advance()
                }
            }
    }
}
