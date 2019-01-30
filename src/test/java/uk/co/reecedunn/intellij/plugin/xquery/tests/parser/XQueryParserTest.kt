/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Parser")
private class XQueryParserTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(XQueryParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile(XQueryParserTest::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("Parser")
    internal inner class Parser {
        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val expected = "XQueryModuleImpl[FILE(0:0)]\n"

            assertThat(prettyPrintASTNode(parseText("")), `is`(expected))
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val expected =
                    "XQueryModuleImpl[FILE(0:3)]\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n" +
                    "      LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n" +
                    "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n" +
                    "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n"

            assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), `is`(expected))
        }

        @Test
        @DisplayName("invalid token")
        fun testInvalidToken() {
            val expected =
                    "XQueryModuleImpl[FILE(0:2)]\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: Unexpected token.')\n" +
                    "      LeafPsiElement[XQUERY_INVALID_TOKEN(0:2)]('<!')\n"

            assertThat(prettyPrintASTNode(parseText("<!")), `is`(expected))
        }
    }

    // region XQuery 1.0 :: VersionDecl

    @Test
    fun testVersionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_WithEncoding() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_WithEncoding_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_MissingVersionKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_MissingVersionString() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_MissingEncodingString() {
        val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: MainModule

    @Test
    fun testMainModule() {
        val expected = loadResource("tests/parser/xquery-1.0/MainModule.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MainModule.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMainModule_WithVersionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMainModule_TokensAfterExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: LibraryModule

    @Test
    fun testLibraryModule() {
        val expected = loadResource("tests/parser/xquery-1.0/LibraryModule.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LibraryModule.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLibraryModule_WithVersionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLibraryModule_WithInvalidConstructRecovery() {
        val expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ModuleDecl

    @Test
    fun testModuleDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_MissingNamespaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_MissingNamespaceName() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_MissingEqualsAfterName() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_MissingNamespaceUri() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: Prolog

    @Test
    fun testProlog_MultipleImports() {
        val expected = loadResource("tests/parser/xquery-1.0/Prolog_MultipleImports.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Prolog_MultipleImports.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: Import

    @Test
    fun testImport_MissingSchemaOrModuleKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: NamespaceDecl

    @Test
    fun testNamespaceDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_MissingEquals() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_MissingNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_MissingNamespaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: BoundarySpaceDecl

    @Test
    fun testBoundarySpaceDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBoundarySpaceDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBoundarySpaceDecl_MissingPreserveOrStripKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBoundarySpaceDecl_MissingBoundarySpaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBoundarySpaceDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBoundarySpaceDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DefaultNamespaceDecl

    @Test
    fun testDefaultNamespaceDecl_Element() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_MissingElementOrFunctionKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Element_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Element_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Element_MissingNamespaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function_MissingNamespaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Element_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Element_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_Function_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultNamespaceDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OptionDecl

    @Test
    fun testOptionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_MissingOptionValue() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_MissingOptionName() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_MissingOptionKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOptionDecl_PrologHeaderStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OrderingModeDecl

    @Test
    fun testOrderingModeDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderingModeDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderingModeDecl_MissingKeywordOrderedOrUnorderedKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderingModeDecl_MissingOrderingKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderingModeDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderingModeDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: EmptyOrderDecl

    @Test
    fun testEmptyOrderDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_MissingGreatestOrLeastKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_MissingEmptyKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_MissingOrderKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEmptyOrderDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CopyNamespacesDecl + PreserveMode + InheritMode

    @Test
    fun testCopyNamespacesDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_MissingInheritMode() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_MissingComma() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_MissingPreserveMode() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_MissingCopyNamespacesKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCopyNamespacesDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DefaultCollationDecl

    @Test
    fun testDefaultCollationDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_MissingCollationKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDefaultCollationDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: BaseURIDecl

    @Test
    fun testBaseURIDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBaseURIDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBaseURIDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBaseURIDecl_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBaseURIDecl_MissingBaseUriKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBaseURIDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: SchemaImport

    @Test
    fun testSchemaImport() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_MissingSchemaUri() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaImport_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: SchemaPrefix

    @Test
    fun testSchemaPrefix() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_MissingNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_MissingEquals() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_Default() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_Default_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_Default_MissingNamespaceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaPrefix_Default_MissingElementKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ModuleImport

    @Test
    fun testModuleImport() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_MissingModuleUri() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithNamespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithNamespace_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithNamespace_MissingNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithNamespace_MissingEquals() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence_MissingUri() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testModuleImport_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: VarDecl

    @Test
    fun testVarDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_Equal() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_Equal.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingExprSingle() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingAssignment() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingVariableName() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingVariableMarker() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingVariableKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_External() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_External_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_External_MissingVariableName() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_PrologHeaderStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (24) VarDecl")
    internal inner class VarDecl {
        @Nested
        @DisplayName("error recovery: 'external' as attribute/element test TypeName")
        internal inner class AttributeOrElementDecl {
            @Test
            @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
            fun attributeTest_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/VarDecl_AttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/VarDecl_AttributeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
            fun elementTest_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/VarDecl_ElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/VarDecl_ElementTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    // region XQuery 1.0 :: ConstructionDecl

    @Test
    fun testConstructionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testConstructionDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testConstructionDecl_MissingPreserveOrStripKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testConstructionDecl_MissingConstructionKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testConstructionDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testConstructionDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: FunctionDecl

    @Test
    fun testFunctionDecl() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingFunctionKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingFunctionName() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingOpeningParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_EnclosedExpr_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingFunctionBody() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_ReturnType() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_ReturnType_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionDecl_PrologHeaderStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ParamList

    @Test
    fun testParamList() {
        val expected = loadResource("tests/parser/xquery-1.0/ParamList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParamList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParamList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParamList_MissingComma() {
        val expected = loadResource("tests/parser/xquery-1.0/ParamList_MissingComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParamList_MissingComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: Param

    @Test
    fun testParam() {
        val expected = loadResource("tests/parser/xquery-1.0/Param.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParam_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/Param_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParam_MissingParameterName() {
        val expected = loadResource("tests/parser/xquery-1.0/Param_MissingParameterName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param_MissingParameterName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParam_MissingVariableMarker() {
        val expected = loadResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParam_TypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParam_TypeDeclaration_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: EnclosedExpr

    @Test
    fun testEnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEnclosedExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testEnclosedExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (30) QueryBody ; XQuery 1.0 EBNF (31) Expr")
    internal inner class QueryBody {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple; missing Expr")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; space before next comma")
        fun multiple_SpaceBeforeNextComma() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_SpaceBeforeNextComma.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_SpaceBeforeNextComma.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr")
    internal inner class FLWORExpr {
        @Test
        @DisplayName("error recovery: ReturnClause only")
        fun returnClauseOnly() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("clause ordering")
        fun clauseOrdering() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("nested")
        fun nested() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_Nested.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_Nested.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple ForClause")
        fun forClause_Multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/ForClause_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple LetClause")
        fun letClause_multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/LetClause_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (34) ForClause")
    internal inner class ForClause {
        @Nested
        @DisplayName("single ForBinding")
        internal inner class SingleForBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' ExprSingle")
            fun missingInExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple ForBindings")
        internal inner class MultipleForBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (35) PositionalVar")
        internal inner class PositionalVar {
            @Test
            @DisplayName("positional variable")
            fun positionalVar() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("positional variable; compact whitespace")
            fun positionalVar_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
        internal inner class TypeDeclaration {
            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword after PositionalVar")
            fun missingInKeyword_PositionalVar() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (36) LetClause")
    internal inner class LetClause {
        @Nested
        @DisplayName("single LetBinding")
        internal inner class SingleLetBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: '=' instead of ':='")
            fun equal() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_Equal.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_Equal.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment operator")
            fun missingVarAssignOperator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment ExprSingle")
            fun missingVarAssignExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple LetBindings")
        internal inner class MultipleLetBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
        internal inner class TypeDeclaration {
            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    // region XQuery 1.0 :: WhereClause

    @Test
    fun testWhereClause_ForClause() {
        val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWhereClause_ForClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWhereClause_ForClause_MissingWhereExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWhereClause_LetClause() {
        val expected = loadResource("tests/parser/xquery-1.0/WhereClause_LetClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/WhereClause_LetClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OrderByClause

    @Test
    fun testOrderByClause_ForClause() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_LetClause() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_LetClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_LetClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_MissingByKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_MissingOrderSpecList() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_Stable_ForClause() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_Stable_LetClause() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_Stable_MissingOrderKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_Stable_MissingByKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderByClause_Stable_MissingOrderSpecList() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OrderSpecList + OrderSpec

    @Test
    fun testOrderSpecList() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderSpecList_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderSpecList_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderSpecList_Multiple_MissingOrderSpec() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OrderModifier

    @Test
    fun testOrderModifier_DirectionOnly() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderModifier_EmptyOnly() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderModifier_EmptyOnly_MissingSpecifier() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderModifier_CollationOnly() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderModifier_CollationOnly_MissingUriString() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (46) QuantifiedExpr")
    internal inner class QuantifiedExpr {
        @Test
        @DisplayName("some, every")
        fun quantifiedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("some, every; compact whitespace")
        fun quantifiedExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' ExprSingle")
        fun testQuantifiedExpr_MissingInExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' keyword")
        fun missingSatisfiesKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' ExprSingle")
        fun missingSatisfiesExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("type declaration")
        fun typeDeclaration() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName")
        fun multipleVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; compact whitespace")
        fun multipleVarName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; missing variable indicator")
        fun multipleVarName_MissingVarIndicator() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 1.0 :: TypeswitchExpr

    @Test
    fun testTypeswitchExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingTypeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingCaseClause() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_MultipleCaseClause() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_Variable() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_Variable_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeswitchExpr_Variable_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CaseClause + TypeswitchExpr

    @Test
    fun testCaseClauseExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_Variable() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_Variable_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCaseClauseExpr_Variable_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (45) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if expression")
        fun ifExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if expression; compact whitespace")
        fun ifExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing condition Expr")
        fun missingCondExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' keyword")
        fun missingThenKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' ExprSingle")
        fun missingThenExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' keyword")
        fun missingElseKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' ExprSingle")
        fun missingElseExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (46) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (47) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ComparisonExpr")
        fun missingComparisonExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (60) GeneralComp")
    internal inner class GeneralComp {
        @Test
        @DisplayName("general comparison")
        fun generalComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("general comparison; compact whitespace")
        fun generalComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (61) ValueComp")
    internal inner class ValueComp {
        @Test
        @DisplayName("value comparison")
        fun valueComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (62) NodeComp")
    internal inner class NodeComp {
        @Test
        @DisplayName("node comparison")
        fun nodeComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("node comparison; compact whitespace")
        fun nodeComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (49) RangeExpr")
    internal inner class RangeExpr {
        @Test
        @DisplayName("range expression")
        fun rangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AdditiveExpr")
        fun missingAdditiveExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (50) AdditiveExpr")
    internal inner class AdditiveExpr {
        @Test
        @DisplayName("additive expression")
        fun additiveExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("additive expression; compact whitespace")
        fun additiveExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing MultiplicativeExpr")
        fun missingMultiplicativeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (51) MultiplicativeExpr")
    internal inner class MultiplicativeExpr {
        @Test
        @DisplayName("multiplicative expression")
        fun multiplicativeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiplicative expression; compact whitespace")
        fun multiplicativeExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (52) UnionExpr")
    internal inner class UnionExpr {
        @Test
        @DisplayName("union expression")
        fun unionExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("union expression; compact whitespace")
        fun unionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntersectExceptExpr")
        fun missingIntersectExceptExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (53) IntersectExceptExpr")
    internal inner class IntersectExceptExpr {
        @Test
        @DisplayName("intersect")
        fun intersect() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: intersect; missing InstanceofExpr")
        fun intersect_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("except")
        fun except() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: except; missing InstanceofExpr")
        fun except_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (54) InstanceofExpr")
    internal inner class InstanceofExpr {
        @Test
        @DisplayName("instance of expression")
        fun instanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'instance' keyword")
        fun missingInstanceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'of' keyword")
        fun missingOfKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (55) TreatExpr")
    internal inner class TreatExpr {
        @Test
        @DisplayName("treat expression")
        fun treatExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'treat' keyword")
        fun missingTreatKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (56) CastableExpr ; XQuery 1.0 EBNF (117) SingleType")
    internal inner class CastableExpr {
        @Test
        @DisplayName("castable expression")
        fun castableExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'castable' keyword")
        fun missingCastableKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (57) CastExpr ; XQuery 1.0 EBNF (117) SingleType")
    internal inner class CastExpr {
        @Test
        @DisplayName("cast expression")
        fun castExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'cast' keyword")
        fun missingCastKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (58) UnaryExpr")
    internal inner class UnaryExpr {
        @Test
        @DisplayName("plus; single")
        fun plusSingle() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; single; compact whitespace")
        fun plusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple")
        fun plusMultiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple; compact whitespace")
        fun plusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: plus; missing ValueExpr")
        fun plus_missingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; single")
        fun minusSingle() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; single; compact whitespace")
        fun minusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple")
        fun minusMultiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple; compact whitespace")
        fun minusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: minus; missing ValueExpr")
        fun minus_missingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 1.0 :: ValidateExpr + ValidationMode

    @Test
    fun testValidateExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidationMode() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidationMode_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidationMode_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ExtensionExpr

    @Test
    fun testExtensionExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_EmptyExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_MultiplePragmas() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExtensionExpr_MultiplePragmas_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: Pragma

    @Test
    fun testPragma() {
        val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPragma_MissingPragmaName() {
        val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPragma_MissingPragmaContents() {
        // This is invalid according to the XQuery grammar, but is supported by
        // XQuery implementations.
        val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPragma_MissingPragmaContents_CompactWhitespace() {
        // This is valid according to the XQuery grammar.
        val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPragma_UnclosedPragma() {
        val expected = loadResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (68) PathExpr")
    internal inner class PathExpr {
        @Test
        @DisplayName("leading forward slash")
        fun leadingForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading forward slash; compact whitespace")
        fun leadingForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash")
        fun leadingDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash; compact whitespace")
        fun leadingDoubleForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("lone forward slash")
        fun loneForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("lone double forward slash")
        fun loneDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (69) RelativePathExpr")
    internal inner class RelativePathExpr {
        @Test
        @DisplayName("direct descendants")
        fun directDescendants() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("direct descendants; compact whitespace")
        fun directDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all descendants")
        fun allDescendants() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all descendants; compact whitespace")
        fun allDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing StepExpr")
        fun missingStepExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (72) ForwardStep ; XQuery 1.0 EBNF (73) ForwardAxis")
    internal inner class ForwardAxis {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("child")
            fun child() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("child; compact whitespace")
            fun child_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant")
            fun descendant() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant; compact whitespace")
            fun descendant_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute")
            fun attribute() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute; compact whitespace")
            fun attribute_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self")
            fun descendantOrSelf() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self; compact whitespace")
            fun descendantOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling")
            fun followingSibling() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling; compact whitespace")
            fun followingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following")
            fun following() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following; compact whitespace")
            fun following_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (72) ForwardStep ; XQuery 1.0 EBNF (74) AbbrevForwardStep")
    internal inner class AbbrevForwardStep {
        @Test
        @DisplayName("node test only")
        fun nodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute")
        fun attribute() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute; compact whitespace")
        fun attribute_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: abbreviated attribute; missing NodeTest")
        fun attribute_MissingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (75) ReverseStep ; XQuery 1.0 EBNF (76) ReverseAxis")
    internal inner class ReverseAxis {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor")
            fun ancestor() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor; compact whitespace")
            fun ancestor_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling")
            fun precedingSibling() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling; compact whitespace")
            fun precedingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding")
            fun preceding() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding; compact whitespace")
            fun preceding_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self")
            fun ancestorOrSelf() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self; compact whitespace")
            fun ancestorOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (75) ReverseStep ; XQuery 1.0 EBNF (77) AbbrevReverseStep")
    fun abbrevReverseStep() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevReverseStep.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevReverseStep.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
    internal inner class KindTest {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
        internal inner class AnyKindTest {
            @Test
            @DisplayName("any kind test")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any kind test; compact whitespace")
            fun anyKindTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("document test")
            fun documentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test; compact whitespace")
            fun documentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (126) TextTest")
        internal inner class TestTest {
            @Test
            @DisplayName("text test")
            fun textTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("text test; compact whitespace")
            fun textTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (127) CommentTest")
        internal inner class CommentTest {
            @Test
            @DisplayName("comment test")
            fun commentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("comment test; compact whitespace")
            fun commentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (128) PITest")
        internal inner class PITest {
            @Test
            @DisplayName("processing instruction test")
            fun piTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("processing instruction test; compact whitespace")
            fun piTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("NCName; compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Nested
            @DisplayName("StringLiteral")
            internal inner class StringLiteral {
                @Test
                @DisplayName("string literal")
                fun stringLiteral() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("string literal; compact whitespace")
                fun stringLiteral_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("attribute test")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute test; compact whitespace")
            fun attributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (129) AttribNameOrWildcard")
            internal inner class AttribNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (139) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingComma.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
        internal inner class SchemaAttributeTest {
            @Test
            @DisplayName("schema attribute test")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema attribute test; compact whitespace")
            fun schemaAttributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing attribute declaration")
            fun missingAttributeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
        internal inner class ElementTest {
            @Test
            @DisplayName("element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (134) ElementNameOrWildcard")
            internal inner class ElementNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (139) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingComma.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("optional type name")
                fun optionalTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("optional type name: compact whitespace")
                fun optionalTypeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name on optional type name")
                fun optionalTypeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
        internal inner class SchemaElementTest {
            @Test
            @DisplayName("schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing element declaration")
            fun missingAttributeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingElementDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingElementDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
    internal inner class NameTest {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (154) QName")
        internal inner class QName {
            @Test
            @DisplayName("qname")
            fun qname() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword prefix part")
            fun keywordPrefixPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_KeywordPrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword local part")
            fun keywordLocalPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeAndAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Test
            @DisplayName("error recovery: integer literal local name")
            fun integerLiteralLocalPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_IntegerLiteralLocalName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_IntegerLiteralLocalName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: missing part")
            internal inner class MissingPart {
                @Test
                @DisplayName("missing local name")
                fun missingLocalName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingLocalPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingLocalPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix")
                fun missingPrefix() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix and local name")
                fun missingPrefixAndLocalName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixAndLocalPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixAndLocalPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (155) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("identifier")
            fun identifier() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_NCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_NCName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword")
            fun keyword() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_NCName_Keyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_NCName_Keyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (80) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("ncname")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: ncname prefix")
            fun ncnamePrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNamePrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNamePrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword prefix")
            fun keywordPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordPrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: ncname local name")
            fun ncnameLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNameLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword local name")
            fun keywordLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prefix and local name wildcard")
            fun prefixAndLocalNameWildcard() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_PrefixAndLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_PrefixAndLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing prefix")
            fun missingPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingPrefix.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingPrefix.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingLocalName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingLocalName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (81) FilterExpr ; XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
    internal inner class FilterExpr {
        @Test
        @DisplayName("predicate list")
        fun predicateList() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("predicate list; compact whitespace")
        fun predicateList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (87) VarRef ; XQuery 1.0 EBNF (88) VarName")
    internal inner class VarRef {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compat whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName; compat whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (89) ParenthesizedExpr ; XQuery 1.0 EBNF (31) Expr")
    internal inner class ParenthesizedExpr {
        @Test
        @DisplayName("empty expression")
        fun emptyExpression() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty expression; compact whitespace")
        fun emptyExpression_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun emptyExpression_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (89) Expr")
        internal inner class Expr {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple; missing Expr")
            fun multiple_MissingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; space before next comma")
            fun multiple_SpaceBeforeNextComma() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (90) ContextItemExpr")
    fun contextItemExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ContextItemExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ContextItemExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // region XQuery 1.0 :: OrderedExpr

    @Test
    fun testOrderedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderedExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrderedExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: UnorderedExpr

    @Test
    fun testUnorderedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnorderedExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnorderedExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (93) FunctionCall ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class FunctionCall {
        @Test
        @DisplayName("keyword NCName")
        fun keywordNCName() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_NCName_Keyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_NCName_Keyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list")
        fun argumentList_Empty() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list; compact whitespace")
        fun argumentList_Empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening parenthesis")
        fun missingOpeningParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (134) Argument")
        internal inner class Argument {
            @Test
            @DisplayName("single argument")
            fun singleParam() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single argument; compact whitespace")
            fun singleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument")
            fun multipleParam() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; compact whitespace")
            fun multipleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple argument; missing ExprSingle")
            fun multipleParam_MissingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; space before next comma")
            fun multipleParam_SpaceBeforeNextComma() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    // region XQuery 1.0 :: DirElemConstructor

    @Test
    fun testDirElemConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_SelfClosing() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_SelfClosing_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_SpaceBeforeNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_IncompleteOpenTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_IncompleteCloseTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_MissingClosingTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_ClosingTagWithoutOpenTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_IncompleteOpenTagQName() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemConstructor_IncompleteCloseTagQName() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeList + DirAttributeValue + (Quot|Apos)AttrValueContent

    @Test
    fun testDirAttributeList() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_NCName() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_MissingAttributeValue() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeList_MissingEquals() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeQuot

    @Test
    fun testDirAttributeValue_EscapeQuot() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeApos

    @Test
    fun testDirAttributeValue_EscapeApos() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + (Quot|Apos)AttrContentChar

    @Test
    fun testDirAttributeValue_AttrContentChar() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent

    @Test
    fun testDirAttributeValue_CommonContent_EscapeCharacters() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + PredefinedEntityRef

    @Test
    fun testDirAttributeValue_CommonContent_PredefinedEntityRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + CharRef

    @Test
    fun testDirAttributeValue_CommonContent_CharRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeValue_CommonContent_CharRef_EmptyNumericRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirAttributeValue_CommonContent_CharRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + EnclosedExpr

    @Test
    fun testDirAttributeValue_CommonContent_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + ElementContentChar

    @Test
    fun testDirElemContent_ElementContentChar() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent

    @Test
    fun testDirElemContent_CommonContent() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + PredefinedEntityRef

    @Test
    fun testDirElemContent_CommonContent_PredefinedEntityRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CommonContent_PredefinedEntityRef_EmptyRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + CharRef

    @Test
    fun testDirElemContent_CommonContent_CharRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CommonContent_CharRef_EmptyHexadecimalRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CommonContent_CharRef_EmptyNumericRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CommonContent_CharRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + EnclosedExpr

    @Test
    fun testDirElemContent_CommonContent_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CDataSection

    @Test
    fun testDirElemContent_CDataSection() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CDataSection_Unclosed() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirElemContent_CDataSection_UnexpectedEndTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirElemConstructor (DirectConstructor)

    @Test
    fun testDirElemContent_DirElemConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirCommentConstructor (DirectConstructor)

    @Test
    fun testDirElemContent_DirCommentConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirPIConstructor (DirectConstructor)

    @Test
    fun testDirElemContent_DirPIConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirCommentConstructor

    @Test
    fun testDirCommentConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirCommentConstructor_UnclosedComment() {
        val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirCommentConstructor_UnexpectedCommentEndTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DirPIConstructor + DirPIContents

    @Test
    fun testDirPIConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirPIConstructor_UnexpectedWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirPIConstructor_MissingNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirPIConstructor_MissingContents() {
        val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDirPIConstructor_MissingEndTag() {
        val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CDataSection

    @Test
    fun testCDataSection() {
        val expected = loadResource("tests/parser/xquery-1.0/CDataSection.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CDataSection.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCDataSection_Unclosed() {
        val expected = loadResource("tests/parser/xquery-1.0/CDataSection_Unclosed.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CDataSection_Unclosed.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCDataSection_UnexpectedEndTag() {
        val expected = loadResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CompDocConstructor

    @Test
    fun testCompDocConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompDocConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompDocConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Test
        @DisplayName("tag name: QName")
        fun testCompElemConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName; compact whitespace")
        fun testCompElemConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompElemConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompElemConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompElemConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompElemConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompElemConstructor_ExprTagName_MissingTagNameExpr() {
            val expected =
                loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompElemConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected =
                loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual =
                parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing expression")
        fun testCompElemConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: URIQualifiedName [XQuery 3.0]")
        fun testCompElemConstructor_EQName() {
            val expected = loadResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompElemConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
    internal inner class CompAttrConstructor {
        @Test
        @DisplayName("tag name: QName")
        fun testCompAttrConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName; compact whitespace")
        fun testCompAttrConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompAttrConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompAttrConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompAttrConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompAttrConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompAttrConstructor_ExprTagName_MissingTagNameExpr() {
            val expected =
                loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompAttrConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected =
                loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual =
                parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing expression")
        fun testCompAttrConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: URIQualifiedName [XQuery 3.0]")
        fun testCompAttrConstructor_EQName() {
            val expected = loadResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompAttrConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 1.0 :: CompTextConstructor

    @Test
    fun testCompTextConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompTextConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompTextConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CompCommentConstructor

    @Test
    fun testCompCommentConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompCommentConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompCommentConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.1 EBNF (166) CompPIConstructor")
    internal inner class CompPIConstructor {
        @Test
        @DisplayName("tag name: NCName")
        fun testCompPIConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: NCName; compact whitespace")
        fun testCompPIConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompPIConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompPIConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompPIConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompPIConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompPIConstructor_ExprTagName_MissingTagNameExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompPIConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected =
                loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual =
                parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing expression")
        fun testCompPIConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName (error recovery)")
        fun testCompPIConstructor_QNameTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompPIConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 1.0 :: TypeDeclaration + AtomicType

    @Test
    fun testTypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDeclaration_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDeclaration_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 1.0 EBNF (119) SequenceType")
    internal inner class SequenceType {
        @Nested
        @DisplayName("empty-sequence()")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun empty() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty sequence; compact whitespace")
            fun empty_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun empty_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (121) ItemType")
        fun itemType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (121) ItemType ; XQuery 1.0 EBNF (120) OccurrenceIndicator")
        internal inner class OccurrenceIndicator {
            @Test
            @DisplayName("zero or one")
            fun zeroOrOne() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or one; compact whitespace")
            fun zeroOrOne_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or more")
            fun zeroOrMore() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or more; compact whitespace")
            fun zeroOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("one or more")
            fun oneOrMore() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_OneOrMore.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_OneOrMore.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("one or more; compact whitespace")
            fun oneOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_OneOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_OneOrMore_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (121) ItemType")
    internal inner class ItemType {
        @Nested
        @DisplayName("item()")
        internal inner class Item {
            @Test
            @DisplayName("item")
            fun itemType() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("item; compact whitespace")
            fun itemType_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (123) AtomicType")
        fun atomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_AnyKindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
            fun documentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_DocumentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_DocumentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (126) TextTest")
            fun textTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_TextTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_TextTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (127) CommentTest")
            fun commentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_CommentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_CommentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (128) PITest")
            fun piTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_PITest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_PITest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_AttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_AttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_SchemaAttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (141) IntegerLiteral")
    fun integerLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (142) DecimalLiteral")
    fun decimalLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/DecimalLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DecimalLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (143) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("double literal with incomplete exponent")
        fun incompleteExponent() {
            val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (144) StringLiteral")
    internal inner class StringLiteral {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed string literal")
        fun unclosedString() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        internal inner class PredefinedEntityRef {
            @Test
            @DisplayName("predefined entity reference")
            fun predefinedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("incomplete entity reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty entity reference")
            fun emptyRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("entity reference outside a string literal")
            fun misplacedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("character reference")
            fun charRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("incomplete character reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty numeric reference")
            fun emptyNumericRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty hexadecimal reference")
            fun emptyHexadecimalRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
        fun escapeQuot() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
        fun escapeApos() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (151) Comment ; XQuery 1.0 EBNF (159) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment_UnclosedComment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 1.0 :: QName

    @Test
    fun testQName_WildcardPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_WildcardLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_WildcardLocalPart_MissingPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: NCName

    @Test
    fun testNCName_UnexpectedQName() {
        val expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNCName_UnexpectedQName_MissingPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNCName_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/NCName_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NCName_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Test
    @DisplayName("XML 1.0 EBNF (3) S")
    fun s() {
        val expected = loadResource("tests/parser/xquery-1.0/S.txt")
        val actual = parseResource("tests/parser/xquery-1.0/S.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // region XQuery 3.0 :: VersionDecl

    @Test
    fun testVersionDecl_EncodingOnly() {
        val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_EncodingOnly_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_EncodingOnly_MissingEncodingString() {
        val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVersionDecl_EncodingOnly_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: DecimalFormatDecl

    @Test
    fun testDecimalFormatDecl() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_MissingEQName() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_Default() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_Property_AllProperties() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_Property_MissingEquals() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_Property_MissingStringLiteral() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDecimalFormatDecl_MissingDecimalFormatKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ContextItemDecl

    @Test
    fun testContextItemDecl() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_Equal() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_MissingVarValue() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_MissingAssignment() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_MissingItemKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_AsItemType() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_AsItemType_MissingItemType() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_External() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_External_DefaultValue() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_External_DefaultValue_Equal() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_External_DefaultValue_MissingValue() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_MissingContextKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testContextItemDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: AnnotatedDecl + Annotation

    @Test
    fun testAnnotation() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_MissingQName() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_MissingQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_MissingQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_ParameterList() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_ParameterList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_ParameterList_MissingLiteral() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_ParameterList_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnnotation_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/Annotation_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Annotation_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: VarDecl

    @Test
    fun testVarDecl_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/VarDecl_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VarDecl_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarDecl_External_DefaultValue() {
        val expected = loadResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: FunctionDecl

    @Test
    fun testFunctionDecl_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionDecl_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionDecl_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: Param

    @Test
    fun testParam_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/Param_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Param_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: OptionDecl

    @Test
    fun testOptionDecl_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/OptionDecl_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/OptionDecl_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: FLWORExpr

    @Test
    fun testFLWORExpr_RelaxedOrdering() {
        val expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFLWORExpr_NestedWithoutReturnClause() {
        val expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: IntermediateClause

    @Test
    fun testIntermediateClause_WhereFor() {
        val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.txt")
        val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntermediateClause_ForWhereLet() {
        val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.txt")
        val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntermediateClause_ForOrderByLet() {
        val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.txt")
        val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ForClause

    @Test
    fun testForClause_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ForClause_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ForClause_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: AllowingEmpty + ForClause

    @Test
    fun testAllowingEmpty() {
        val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAllowingEmpty_MissingEmptyKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAllowingEmpty_ForBinding_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: PositionalVar + ForClause

    @Test
    fun testPositionalVar_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/PositionalVar_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/PositionalVar_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: LetClause

    @Test
    fun testLetClause_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/LetClause_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/LetClause_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: TumblingWindowClause

    @Test
    fun testTumblingWindowClause() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingWindowKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_MissingStartCondition() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_TypeDecl() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTumblingWindowClause_TypeDecl_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: SlidingWindowClause

    @Test
    fun testSlidingWindowClause() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingWindowKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingStartCondition() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_MissingEndCondition() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_TypeDecl() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSlidingWindowClause_TypeDecl_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: WindowStartCondition

    @Test
    fun testWindowStartCondition() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowStartCondition_MissingWhenKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowStartCondition_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: WindowEndCondition

    @Test
    fun testWindowEndCondition() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowEndCondition_MissingWhenKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowEndCondition_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowEndCondition_Only() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowEndCondition_Only_MissingEndKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: WindowVars

    @Test
    fun testWindowVars_Empty() {
        val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Current() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Current_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Position() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Position.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Position.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Previous() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Previous_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Previous_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Next() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Next_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_Next_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWindowVars_AllVars() {
        val expected = loadResource("tests/parser/xquery-3.0/WindowVars_AllVars.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CountClause

    @Test
    fun testCountClause() {
        val expected = loadResource("tests/parser/xquery-3.0/CountClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CountClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCountClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCountClause_MissingVariableIndicator() {
        val expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCountClause_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: WhereClause

    @Test
    fun testWhereClause_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/WhereClause_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: GroupByClause

    @Test
    fun testGroupByClause() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupByClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupByClause_MissingByKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupByClause_MissingGroupingSpecList() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpecList

    @Test
    fun testGroupingSpecList() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpecList_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpecList_Multiple_MissingGroupingSpec() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpec

    @Test
    fun testGroupingSpec() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Value() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Value_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Value_Equal() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Value_MissingValue() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_TypedValue() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_TypedValue_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_TypedValue_Equal() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_TypedValue_MissingAssignment() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_TypedValue_MissingValue() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Collation() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_Collation_MissingUriLiteral() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testGroupingSpec_ValueAndCollation() {
        val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.txt")
        val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: OrderByClause

    @Test
    fun testOrderByClause_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/OrderByClause_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: QuantifiedExpr

    @Test
    fun testQuantifiedExpr_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: SwitchExpr

    @Test
    fun testSwitchExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingCaseClause() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingDefaultKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchExpr_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: SwitchCaseClause

    @Test
    fun testSwitchCaseClause() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchCaseClause_MissingCaseOperand() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchCaseClause_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchCaseClause_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSwitchCaseClause_MultipleCases() {
        val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: TypeswitchExpr

    @Test
    fun testTypeswitchExpr_Variable_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CaseClause

    @Test
    fun testCaseClause_Variable_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CaseClause + SequenceTypeUnion

    @Test
    fun testSequenceTypeUnion() {
        val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSequenceTypeUnion_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSequenceTypeUnion_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: TryClause + TryTargetExpr + TryCatchExpr

    @Test
    fun testTryClause() {
        val expected = loadResource("tests/parser/xquery-3.0/TryClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TryClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTryClause_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CatchClause + TryCatchExpr

    @Test
    fun testCatchClause() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingCatchErrorList() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CatchErrorList + CatchClause + TryCatchExpr

    @Test
    fun testCatchErrorList() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchErrorList_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchErrorList_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchErrorList_Multiple_MissingNameTest() {
        val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    internal inner class StringConcatExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing RangeExpr")
        fun missingRangeExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 3.0 :: ValidateExpr

    @Test
    fun testValidateExpr_Type() {
        val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_Type_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_Type_MissingTypeName() {
        val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_Type_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_Type_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: Pragma

    @Test
    fun testPragma_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/Pragma_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Pragma_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    internal inner class SimpleMapExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing PathExpr")
        fun missingPathExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 3.0 :: ForwardStep

    @Test
    fun testForwardStep_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ForwardStep_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ForwardStep_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (117) NodeTest ; XQuery 3.0 EBNF (172) KindTest")
    internal inner class NodeTest_XQuery30 {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
        internal inner class NamespaceNodeTest {
            @Test
            @DisplayName("namespace node test")
            fun namespaceNodeTest() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("namespace node test; compact whitespace")
            fun namespaceNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (117) NodeTest ; XQuery 3.0 EBNF (118) NameTest")
    internal inner class NameTest_XQuery30 {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (201) URIQualifiedName ; XQuery 3.0 EBNF (202) BracedURILiteral")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("NCName local name")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_NCNameLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword local name")
            fun keyword() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_MissingLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_MissingLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete braced URI literal")
            fun incompleteBracedURILiteral() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (119) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("BracedURILiteral prefix")
            fun bracedURILiteralPrefix() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_Wildcard_BracedURILiteral.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_Wildcard_BracedURILiteral.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (120) PostfixExpr; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class PostfixExpr_ArgumentList {
        @Test
        @DisplayName("argument list")
        fun argumentList() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("argument list; compact whitespace")
        fun argumentList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 3.0 :: VarRef + VarName

    @Test
    fun testVarRef_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/VarRef_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/VarRef_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: FunctionCall

    @Test
    fun testFunctionCall_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionCall_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionCall_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (133) FunctionCall ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class FunctionCall_XQuery30 {
        @Test
        @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
        fun argumentPlaceholder() {
            val expected = loadResource("tests/parser/xquery-3.0/ArgumentPlaceholder.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (160) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("tag name: NCName")
        fun testCompNamespaceConstructor() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: NCName; compact whitespace")
        fun testCompNamespaceConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompNamespaceConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompNamespaceConstructor_PrefixExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompNamespaceConstructor_PrefixExpr_CompactWhitespace() {
            val expected =
                loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.txt")
            val actual =
                parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace() {
            val expected =
                loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.txt")
            val actual =
                parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing expression")
        fun testCompNamespaceConstructor_PrefixExpr_MissingURIExpr() {
            val expected =
                loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompNamespaceConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    internal inner class NamedFunctionRef {
        @Test
        @DisplayName("named function reference")
        fun namedFunctionRef() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("named function reference; compact whitespace")
        fun namedFunctionRef_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing arity")
        fun missingArity() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("inline function expression")
        fun inlineFunctionExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("inline function expression; compact whitespace")
        fun inlineFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function body")
        fun missingFunctionBody() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parameter list")
        fun paramList() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("return type")
        fun returnType() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("return type; missing SequenceType")
        fun returnType_MissingSequenceType() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'function' keyword")
            fun missingFunctionKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening parenthesis")
            fun missingOpeningParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    // region XQuery 3.0 :: SingleType

    @Test
    fun testSingleType_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/SingleType_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SingleType_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: TypeDeclaration + AtomicOrUnionType

    @Test
    fun testTypeDeclaration_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (170) ItemType ; XQuery 3.0 EBNF (172) KindTest")
    internal inner class ItemType_XQuery30 {
        @Test
        @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
        fun namespaceNodeTest() {
            val expected = loadResource("tests/parser/xquery-3.0/ItemType_NamespaceNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ItemType_NamespaceNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    // region XQuery 3.0 :: AttributeTest + AttribNameOrWildcard + AttributeName

    @Test
    fun testAttributeTest_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/AttributeTest_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AttributeTest_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: AttributeTest + TypeName

    @Test
    fun testAttributeTest_TypeName_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ElementTest + ElementNameOrWildcard + ElementName

    @Test
    fun testElementTest_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ElementTest_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ElementTest_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ElementTest + TypeName

    @Test
    fun testElementTest_TypeName_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.0 EBNF (191) FunctionTest")
    internal inner class FunctionTest {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("single annotation")
            fun singleAnnotation() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single annotation; compact whitespace")
            fun singleAnnotation_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple annotations")
            fun multipleAnnotations() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple annotations; compact whitespace")
            fun multipleAnnotations_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("typed function with annotations")
            fun typedFunctionWithAnnotations() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'function' keyword")
            fun missingFunctionKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MissingFunctionKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MissingFunctionKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
        internal inner class AnyFunctionTest {
            @Test
            @DisplayName("any function test")
            fun anyFunctionTest() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any function test; compact whitespace")
            fun anyFunctionTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unexpected return type")
            fun unexpectedReturnType() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
        internal inner class TypedFunctionTest {
            @Test
            @DisplayName("single parameter")
            fun singleParameter() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single parameter; compact whitespace")
            fun singleParameter_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters")
            fun multipleParameters() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters; compact whitespace")
            fun multipleParameters_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple parameters; missing SequenceType")
            fun multipleParameters_MissingSequenceType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters with OccurrenceIndicator")
            fun multipleParametersWithOccurenceIndicator() {
                // This is testing handling of whitespace before parsing the next comma.
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return type")
            fun missingReturnType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing SequnceType from the return type")
            fun returnType_MissingSequenceType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty parameter list")
            fun emptyParameterList() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (216) ParenthesizedItemType")
    internal inner class ParenthesizedItemType {
        @Test
        @DisplayName("parenthesized item type")
        fun parenthesizedItemType() {
            val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized item type; compact whitespace")
        fun parenthesizedItemType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("error recovery; item type as sequence type")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral

    @Test
    fun testURIQualifiedName_Wildcard() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral + PredefinedEntityRef

    @Test
    fun testBracedURILiteral_PredefinedEntityRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBracedURILiteral_PredefinedEntityRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBracedURILiteral_PredefinedEntityRef_EmptyRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral + CharRef

    @Test
    fun testBracedURILiteral_CharRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBracedURILiteral_CharRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBracedURILiteral_CharRef_EmptyNumericRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBracedURILiteral_CharRef_EmptyHexadecimalRef() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: DFPropertyName + DecimalFormatDecl

    @Test
    fun testDecimalFormatDecl_Property_XQuery31() {
        val expected = loadResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.txt")
        val actual = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: EnclosedExpr

    @Test
    fun testEnclosedExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: TryClause + TryTargetExpr + TryCatchExpr

    @Test
    fun testTryClause_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/TryClause_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CatchClause + TryCatchExpr

    @Test
    fun testCatchClause_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: ArrowExpr

    @Test
    fun testArrowExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowExpr_MissingArgumentList() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowExpr_MissingFunctionSpecifier() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowExpr_MultipleArrows() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: ValidateExpr + ValidationMode

    @Test
    fun testValidateExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: Lookup

    @Test
    fun testLookup() {
        val expected = loadResource("tests/parser/xquery-3.1/Lookup.txt")
        val actual = parseResource("tests/parser/xquery-3.1/Lookup.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLookup_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLookup_MissingKeySpecifier() {
        val expected = loadResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.txt")
        val actual = parseResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: KeySpecifier

    @Test
    fun testKeySpecifier_NCName() {
        val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup.txt")
        val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKeySpecifier_IntegerLiteral() {
        val expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKeySpecifier_ParenthesizedExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_ParenthesizedExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_ParenthesizedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKeySpecifier_Wildcard() {
        val expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: ArrowFunctionSpecifier

    @Test
    fun testArrowFunctionSpecifier_EQName() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowFunctionSpecifier_VarRef() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_VarRef.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_VarRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrowFunctionSpecifier_ParenthesizedExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_ParenthesizedExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_ParenthesizedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: OrderedExpr

    @Test
    fun testOrderedExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: UnorderedExpr

    @Test
    fun testUnorderedExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: DirAttributeValue + CommonContent + EnclosedExpr

    @Test
    fun testDirAttributeValue_CommonContent_EnclosedExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: DirElemContent + CommonContent + EnclosedExpr

    @Test
    fun testDirElemContent_CommonContent_EnclosedExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CompDocConstructor

    @Test
    fun testCompDocConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CompNamespaceConstructor

    @Test
    fun testCompNamespaceConstructor_PrefixExpr_MissingPrefixExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_MissingURIExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CompTextConstructor

    @Test
    fun testCompTextConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CompCommentConstructor

    @Test
    fun testCompCommentConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: MapConstructor

    @Test
    fun testMapConstructor() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructor.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: MapConstructorEntry + MapConstructor

    @Test
    fun testMapConstructorEntry() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingSeparator() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingValueExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_MissingEntry() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName_WhitespaceAfterColon() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_KeyExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_ValueExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_QName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_VarRef_NCName() {
        val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.txt")
        val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: SquareArrayConstructor

    @Test
    fun testSquareArrayConstructor() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSquareArrayConstructor_Multiple_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: CurlyArrayConstructor

    @Test
    fun testCurlyArrayConstructor() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCurlyArrayConstructor_Multiple_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: StringConstructor + StringConstructorContent

    @Test
    fun testStringConstructor() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructor.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructor_MissingEndOfString() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructor_Empty() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructor_Empty.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructor_Empty.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: StringConstructorInterpolation

    @Test
    fun testStringConstructorInterpolation() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructorInterpolation_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructorInterpolation_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructorInterpolation_MultipleExpr() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConstructorInterpolation_AtStart() {
        val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_AtStart.txt")
        val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_AtStart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: UnaryLookup

    @Test
    fun testUnaryLookup() {
        val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup.txt")
        val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryLookup_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryLookup_MissingKeySpecifier() {
        val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.txt")
        val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: AnyMapTest

    @Test
    fun testAnyMapTest() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapTest_MissingWildcard() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: TypedMapTest

    @Test
    fun testTypedMapTest() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedMapTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedMapTest_MissingAtomicOrUnionType() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedMapTest_MissingComma() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedMapTest_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedMapTest_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: AnyArrayTest

    @Test
    fun testAnyArrayTest() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayTest_MissingWildcard() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.1 :: TypedArrayTest

    @Test
    fun testTypedArrayTest() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedArrayTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedArrayTest_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedArrayTest_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
