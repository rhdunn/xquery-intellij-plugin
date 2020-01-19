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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.MockASTNode
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParser
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathImpl

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Implementing a Parser and PSI - XPath ParserDefinition")
private class XPathParserDefinitionTest : ParserTestCase() {
    @Test
    @DisplayName("createLexer")
    fun testLexer() {
        assertThat(XPathParserDefinition.createLexer(myProject).javaClass.name, `is`(XPathLexer::class.java.name))
    }

    @Test
    @DisplayName("createParser")
    fun testParser() {
        assertThat(XPathParserDefinition.createParser(myProject).javaClass.name, `is`(XPathParser::class.java.name))
    }

    @Test
    @DisplayName("fileNodeType")
    fun testFileNodeType() {
        assertThat(XPathParserDefinition.fileNodeType, `is`(XPathElementType.XPATH))
    }

    @Test
    @DisplayName("whitespaceTokens")
    fun testWhitespaceTokens() {
        val tokens = XPathParserDefinition.whitespaceTokens
        assertThat(tokens.types.size, `is`(0))
    }

    @Test
    @DisplayName("commentTokens")
    fun testCommentTokens() {
        val tokens = XPathParserDefinition.commentTokens
        assertThat(tokens.types.size, `is`(1))
        assertThat(tokens.contains(XPathTokenType.COMMENT), `is`(true))
    }

    @Test
    @DisplayName("stringLiteralElements")
    fun testStringLiteralElements() {
        val tokens = XPathParserDefinition.stringLiteralElements
        assertThat(tokens.types.size, `is`(1))
        assertThat(tokens.contains(XPathTokenType.STRING_LITERAL_CONTENTS), `is`(true))
    }

    @Test
    @DisplayName("createElement")
    fun testCreateElement() {
        // foreign ASTNode
        val e = Assertions.assertThrows(AssertionError::class.java) {
            XPathParserDefinition.createElement(MockASTNode(XPathTokenType.INTEGER_LITERAL))
        }
        assertThat(
            e.message,
            `is`("Alien element type [XPATH_INTEGER_LITERAL_TOKEN]. Can't create XPath PsiElement for that.")
        )
    }

    @Test
    @DisplayName("createFile")
    fun testCreateFile() {
        val file = createVirtualFile("test.xpath", "")
        val psiFile = XPathParserDefinition.createFile(getFileViewProvider(myProject, file, false))
        assertThat(psiFile.javaClass.name, `is`(XPathImpl::class.java.name))
        assertThat(psiFile.fileType, `is`(XPathFileType))
    }
}
