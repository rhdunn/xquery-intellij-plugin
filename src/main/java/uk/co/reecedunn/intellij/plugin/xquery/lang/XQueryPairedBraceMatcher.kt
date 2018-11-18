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
package uk.co.reecedunn.intellij.plugin.xquery.lang

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

private val BRACE_PAIRS = arrayOf(
    BracePair(XQueryTokenType.BLOCK_OPEN, XQueryTokenType.BLOCK_CLOSE, true), // { ... }
    BracePair(XQueryTokenType.SQUARE_OPEN, XQueryTokenType.SQUARE_CLOSE, false), // [ ... ]
    BracePair(XPathTokenType.PARENTHESIS_OPEN, XPathTokenType.PARENTHESIS_CLOSE, false), // ( ... )
    BracePair(XPathTokenType.COMMENT_START_TAG, XPathTokenType.COMMENT_END_TAG, false), // (: ... :)
    BracePair(XQueryTokenType.OPEN_XML_TAG, XQueryTokenType.SELF_CLOSING_XML_TAG, false), // < ... />
    BracePair(XQueryTokenType.OPEN_XML_TAG, XQueryTokenType.END_XML_TAG, false), // < ... >
    BracePair(XQueryTokenType.CLOSE_XML_TAG, XQueryTokenType.END_XML_TAG, false), // </ ... >
    BracePair(XQueryTokenType.XML_COMMENT_START_TAG, XQueryTokenType.XML_COMMENT_END_TAG, false), // <!-- ... -->
    BracePair(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN, XQueryTokenType.PROCESSING_INSTRUCTION_END, false), // <? ... ?>
    BracePair(XQueryTokenType.CDATA_SECTION_START_TAG, XQueryTokenType.CDATA_SECTION_END_TAG, false), // <![CDATA[ ... ]]>
    BracePair(XQueryTokenType.PRAGMA_BEGIN, XQueryTokenType.PRAGMA_END, false), // (# ... #)
    BracePair(XQueryTokenType.STRING_INTERPOLATION_OPEN, XQueryTokenType.STRING_INTERPOLATION_CLOSE, true)) // `{ ... }`

class XQueryPairedBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> = BRACE_PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}
