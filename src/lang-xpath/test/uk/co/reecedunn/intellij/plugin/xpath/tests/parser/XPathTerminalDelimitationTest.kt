// Copyright (C) 2018, 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider

@DisplayName("XPath 3.1 - (A.2.2) Terminal Delimitation")
class XPathTerminalDelimitationTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XPathTerminalDelimitationTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
    }

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
