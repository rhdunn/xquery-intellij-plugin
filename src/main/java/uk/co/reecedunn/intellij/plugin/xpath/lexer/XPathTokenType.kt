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
    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE

    val INTEGER_LITERAL = IElementType("XQUERY_INTEGER_LITERAL_TOKEN", XPath) // XPath 2.0
    val DECIMAL_LITERAL = IElementType("XQUERY_DECIMAL_LITERAL_TOKEN", XPath) // XPath 2.0
    val DOUBLE_LITERAL = IElementType("XQUERY_DOUBLE_LITERAL_TOKEN", XPath) // XPath 2.0
    val PARTIAL_DOUBLE_LITERAL_EXPONENT = IElementType("XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XPath) // XPath 2.0 (error reporting & recovery)

    val NCNAME: IElementType = INCNameType("XQUERY_NCNAME_TOKEN", XPath) // Namespaces in XML 1.0
}
