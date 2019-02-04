/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

internal object XPathSyntaxHighlighterKeys {
    val BAD_CHARACTER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XPathSyntaxHighlighterColors.BAD_CHARACTER
    )

    val COMMENT_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XPathSyntaxHighlighterColors.COMMENT
    )

    val NUMBER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XPathSyntaxHighlighterColors.NUMBER
    )

    val KEYS = mapOf(
        XPathTokenType.INTEGER_LITERAL to NUMBER_KEYS,
        XPathTokenType.DECIMAL_LITERAL to NUMBER_KEYS,
        XPathTokenType.DOUBLE_LITERAL to NUMBER_KEYS,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT to NUMBER_KEYS,
        XPathTokenType.COMMENT_START_TAG to XPathSyntaxHighlighterKeys.COMMENT_KEYS,
        XPathTokenType.COMMENT to XPathSyntaxHighlighterKeys.COMMENT_KEYS,
        XPathTokenType.COMMENT_END_TAG to XPathSyntaxHighlighterKeys.COMMENT_KEYS,
        XPathTokenType.BAD_CHARACTER to XPathSyntaxHighlighterKeys.BAD_CHARACTER_KEYS
    )
}
