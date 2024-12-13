/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.lang.ParserDefinition
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition

@DisplayName("XPath 3.1 - (A.2.2) Terminal Delimitation")
class XPathTerminalDelimitationTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XPathTerminalDelimitationTest")

    fun required(left: IElementType, right: IElementType) {
        assertThat(
            XPathParserDefinition.spaceRequirements(left, right),
            `is`(ParserDefinition.SpaceRequirements.MUST)
        )
    }

    fun optional(left: IElementType, right: IElementType) {
        assertThat(
            XPathParserDefinition.spaceRequirements(left, right),
            `is`(ParserDefinition.SpaceRequirements.MAY)
        )
    }

    @Test
    @DisplayName("T=IntegerLiteral")
    fun integerLiteral() {
        // T and U are both non-delimiting terminal symbols
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.DOUBLE_LITERAL)
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.NCNAME)
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.K_CHILD) // keyword tokens
        optional(XPathTokenType.INTEGER_LITERAL, XPathTokenType.VARIABLE_INDICATOR) // ':='

        // T is a numeric literal and U is ".", or vice versa.
        required(XPathTokenType.INTEGER_LITERAL, XPathTokenType.DOT)
    }

    @Test
    @DisplayName("T=DecimalLiteral")
    fun decimalLiteral() {
        // T and U are both non-delimiting terminal symbols
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.DOUBLE_LITERAL)
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.NCNAME)
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.K_CHILD) // keyword tokens
        optional(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.VARIABLE_INDICATOR) // ':='

        // T is a numeric literal and U is ".", or vice versa.
        required(XPathTokenType.DECIMAL_LITERAL, XPathTokenType.DOT)
    }

    @Test
    @DisplayName("T=DoubleLiteral")
    fun doubleLiteral() {
        // T and U are both non-delimiting terminal symbols
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.DOUBLE_LITERAL)
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.NCNAME)
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.K_CHILD) // keyword tokens
        optional(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.VARIABLE_INDICATOR) // ':='

        // T is a numeric literal and U is ".", or vice versa.
        required(XPathTokenType.DOUBLE_LITERAL, XPathTokenType.DOT)
    }

    @Test
    @DisplayName("T=NCName")
    fun ncname() {
        // T and U are both non-delimiting terminal symbols
        required(XPathTokenType.NCNAME, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.NCNAME, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.NCNAME, XPathTokenType.DOUBLE_LITERAL)
        required(XPathTokenType.NCNAME, XPathTokenType.NCNAME)
        required(XPathTokenType.NCNAME, XPathTokenType.K_CHILD) // keyword tokens
        optional(XPathTokenType.NCNAME, XPathTokenType.VARIABLE_INDICATOR) // ':='

        // T is a QName or an NCName and U is "." or "-".
        required(XPathTokenType.NCNAME, XPathTokenType.DOT)
        required(XPathTokenType.NCNAME, XPathTokenType.MINUS)
    }

    @Test
    @DisplayName("T=keyword")
    fun keyword() {
        // T and U are both non-delimiting terminal symbols
        required(XPathTokenType.K_CHILD, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.K_CHILD, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.K_CHILD, XPathTokenType.DOUBLE_LITERAL)
        required(XPathTokenType.K_CHILD, XPathTokenType.NCNAME)
        required(XPathTokenType.K_CHILD, XPathTokenType.K_CHILD) // keyword tokens
        optional(XPathTokenType.K_CHILD, XPathTokenType.VARIABLE_INDICATOR) // ':='

        // T is a QName or an NCName and U is "." or "-".
        required(XPathTokenType.K_CHILD, XPathTokenType.DOT)
        required(XPathTokenType.K_CHILD, XPathTokenType.MINUS)
    }

    @Test
    @DisplayName("T=.")
    fun dot() {
        // T is a numeric literal and U is ".", or vice versa.
        required(XPathTokenType.DOT, XPathTokenType.INTEGER_LITERAL)
        required(XPathTokenType.DOT, XPathTokenType.DECIMAL_LITERAL)
        required(XPathTokenType.DOT, XPathTokenType.DOUBLE_LITERAL)
    }
}
