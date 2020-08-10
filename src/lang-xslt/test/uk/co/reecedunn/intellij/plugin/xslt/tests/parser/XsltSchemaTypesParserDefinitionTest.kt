/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.lang.ParserDefinition
import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.MockASTNode
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypesParser
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypesParserDefinition

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Implementing a Parser and PSI - XSLT Schema Types ParserDefinition")
private class XsltValueTemplateParserDefinitionTest : ParserTestCase() {
    val definition = XsltSchemaTypesParserDefinition()

    @Test
    @DisplayName("createLexer")
    fun testLexer() {
        assertThat(definition.createLexer(myProject).javaClass.name, `is`(XPathLexer::class.java.name))
    }

    @Test
    @DisplayName("createParser")
    fun testParser() {
        assertThat(definition.createParser(myProject).javaClass.name, `is`(XsltSchemaTypesParser::class.java.name))
    }

    @Test
    @DisplayName("fileNodeType")
    fun testFileNodeType() {
        assertThat(definition.fileNodeType, `is`(XPathElementType.XPATH))
    }

    @Test
    @DisplayName("whitespaceTokens")
    fun testWhitespaceTokens() {
        val tokens = definition.whitespaceTokens
        assertThat(tokens.types.size, `is`(0))
    }

    @Test
    @DisplayName("commentTokens")
    fun testCommentTokens() {
        val tokens = definition.commentTokens
        assertThat(tokens.types.size, `is`(1))
        assertThat(tokens.contains(XPathTokenType.COMMENT), `is`(true))
    }

    @Test
    @DisplayName("stringLiteralElements")
    fun testStringLiteralElements() {
        val tokens = definition.stringLiteralElements
        assertThat(tokens.types.size, `is`(1))
        assertThat(tokens.contains(XPathTokenType.STRING_LITERAL_CONTENTS), `is`(true))
    }

    @Test
    @DisplayName("createElement")
    fun testCreateElement() {
        // foreign ASTNode
        val e = Assertions.assertThrows(AssertionError::class.java) {
            definition.createElement(MockASTNode(XPathTokenType.INTEGER_LITERAL))
        }
        assertThat(
            e.message,
            `is`("Alien element type [XPATH_INTEGER_LITERAL_TOKEN]. Can't create XsltSchemaTypes PsiElement for that.")
        )
    }

    @Test
    @DisplayName("createFile")
    fun testCreateFile() {
        //val file = createVirtualFile("test.avt", "")
        //val psiFile = definition.createFile(getFileViewProvider(myProject, file, false))
        //assertThat(psiFile.javaClass.name, `is`(XPathImpl::class.java.name))
        //assertThat(psiFile.fileType, `is`(XPathFileType))
    }

    @Nested
    @DisplayName("XPath (A.2.2) Terminal Delimitation")
    internal inner class TerminalDelimitation {
        fun required(left: IElementType, right: IElementType) {
            assertThat(
                definition.spaceRequirements(left, right),
                `is`(ParserDefinition.SpaceRequirements.MUST)
            )
        }

        fun optional(left: IElementType, right: IElementType) {
            assertThat(
                definition.spaceRequirements(left, right),
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
}
