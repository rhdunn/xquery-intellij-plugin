/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lang.refactoring

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathNamesValidator : NamesValidator {
    private val lexer = XPathLexer()

    override fun isKeyword(name: String, project: Project?): Boolean = false

    override fun isIdentifier(name: String, project: Project?): Boolean {
        lexer.start(name)
        if (lexer.tokenType === XPathTokenType.WHITE_SPACE) lexer.advance()

        val isNCName = lexer.tokenType is INCNameType
        lexer.advance()

        if (lexer.tokenType === XPathTokenType.WHITE_SPACE) lexer.advance()
        return lexer.tokenType == null && isNCName
    }
}
