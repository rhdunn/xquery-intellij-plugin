/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath

object XPathTokenType {
    // region Symbols

    val COMMA = IElementType("XQUERY_COMMA_TOKEN", XPath) // XPath 2.0
    val VARIABLE_INDICATOR = IElementType("XQUERY_VARIABLE_INDICATOR_TOKEN", XPath) // XPath 2.0
    val PARENTHESIS_OPEN = IElementType("XQUERY_PARENTHESIS_OPEN_TOKEN", XPath) // XPath 2.0
    val PARENTHESIS_CLOSE = IElementType("XQUERY_PARENTHESIS_CLOSE_TOKEN", XPath) // XPath 2.0
    val PLUS = IElementType("XQUERY_PLUS_TOKEN", XPath) // XPath 2.0
    val MINUS = IElementType("XQUERY_MINUS_TOKEN", XPath) // XPath 2.0
    val STAR = IElementType("XQUERY_STAR_TOKEN", XPath) // XPath 2.0
    val UNION = IElementType("XQUERY_UNION_TOKEN", XPath) // XPath 2.0

    // endregion
    // region Keywords

    val K_AND = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AND", XPath) // XPath 2.0
    val K_AS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AS", XPath) // XPath 2.0
    val K_DIV = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIV", XPath) // XPath 2.0
    val K_ELSE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELSE", XPath) // XPath 2.0
    val K_EVERY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EVERY", XPath) // XPath 2.0
    val K_EXCEPT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXCEPT", XPath) // XPath 2.0
    val K_FOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOR", XPath) // XPath 2.0
    val K_IDIV = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IDIV", XPath) // XPath 2.0
    val K_IF = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IF", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_IN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IN", XPath) // XPath 2.0
    val K_INSTANCE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSTANCE", XPath) // XPath 2.0
    val K_INTERSECT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTERSECT", XPath) // XPath 2.0
    val K_MOD = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MOD", XPath) // XPath 2.0
    val K_OF = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OF", XPath) // XPath 2.0
    val K_OR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OR", XPath) // XPath 2.0
    val K_RETURN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURN", XPath) // XPath 2.0
    val K_SATISFIES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SATISFIES", XPath) // XPath 2.0
    val K_SOME = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SOME", XPath) // XPath 2.0
    val K_THEN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_THEN", XPath) // XPath 2.0
    val K_TREAT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TREAT", XPath) // XPath 2.0
    val K_TO = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TO", XPath) // XPath 2.0
    val K_UNION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNION", XPath) // XPath 2.0

    // endregion
    // region Terminal Symbols

    val INTEGER_LITERAL = IElementType("XQUERY_INTEGER_LITERAL_TOKEN", XPath) // XPath 2.0
    val DECIMAL_LITERAL = IElementType("XQUERY_DECIMAL_LITERAL_TOKEN", XPath) // XPath 2.0
    val DOUBLE_LITERAL = IElementType("XQUERY_DOUBLE_LITERAL_TOKEN", XPath) // XPath 2.0

    val STRING_LITERAL_START = IElementType("XQUERY_STRING_LITERAL_START_TOKEN", XPath) // XPath 2.0
    val STRING_LITERAL_CONTENTS = IElementType("XQUERY_STRING_LITERAL_CONTENTS_TOKEN", XPath) // XPath 2.0
    val STRING_LITERAL_END = IElementType("XQUERY_STRING_LITERAL_END_TOKEN", XPath) // XPath 2.0
    val ESCAPED_CHARACTER = IElementType("XQUERY_ESCAPED_CHARACTER_TOKEN", XPath) // XPath 2.0 EscapeQuot ; XPath 2.0 EscapeApos ; XQuery 1.0 CommonContent

    val BRACED_URI_LITERAL_START = IElementType("XQUERY_BRACED_URI_LITERAL_START_TOKEN", XPath) // XPath 3.0
    val BRACED_URI_LITERAL_END = IElementType("XQUERY_BRACED_URI_LITERAL_END_TOKEN", XPath) // XPath 3.0

    val COMMENT = IElementType("XQUERY_COMMENT_TOKEN", XPath) // XPath 2.0
    val COMMENT_START_TAG = IElementType("XQUERY_COMMENT_START_TAG_TOKEN", XPath) // XPath 2.0
    val COMMENT_END_TAG = IElementType("XQUERY_COMMENT_END_TAG_TOKEN", XPath) // XPath 2.0

    val NCNAME: IElementType = INCNameType("XQUERY_NCNAME_TOKEN", XPath) // Namespaces in XML 1.0
    val QNAME_SEPARATOR = IElementType("XQUERY_QNAME_SEPARATOR_TOKEN", XPath) // Namespaces in XML 1.0

    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE // XML 1.0

    // endregion
    // region Error Reporting and Recovery

    val PARTIAL_DOUBLE_LITERAL_EXPONENT = IElementType("XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XPath)
    val UNEXPECTED_END_OF_BLOCK = IElementType("XQUERY_UNEXPECTED_END_OF_BLOCK_TOKEN", XPath)

    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

    // endregion
}
