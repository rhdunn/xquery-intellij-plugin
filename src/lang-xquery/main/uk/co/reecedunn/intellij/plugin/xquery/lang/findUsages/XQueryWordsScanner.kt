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
package uk.co.reecedunn.intellij.plugin.xquery.lang.findUsages

import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.util.Processor
import uk.co.reecedunn.intellij.plugin.xpath.lang.findUsages.XPathWordsScanner
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryWordsScanner : XPathWordsScanner(XQueryLexer()) {
    override fun processWords(fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        mLexer.start(fileText)
        while (mLexer.tokenType != null) {
            characters.reset(fileText, mLexer.tokenStart, mLexer.tokenEnd)

            val type = mLexer.tokenType
            if (XQueryTokenType.COMMENT_TOKENS.contains(type)) {
                if (!processToken(processor, WordOccurrence.Kind.COMMENTS)) return
            } else if (XQueryTokenType.STRING_LITERAL_TOKENS.contains(type)) {
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
}
