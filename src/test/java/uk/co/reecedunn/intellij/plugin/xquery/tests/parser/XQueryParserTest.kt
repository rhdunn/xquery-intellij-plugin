/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Parser")
private class XQueryParserTest : ParserTestCase() {
    // region Parser :: Empty Buffer

    @Test
    fun testEmptyBuffer() {
        val expected = "XQueryModuleImpl[FILE(0:0)]\n"

        assertThat(prettyPrintASTNode(parseText("")), `is`(expected))
    }

    // endregion
    // region Parser :: Bad Characters

    @Test
    fun testBadCharacters() {
        val expected =
                "XQueryModuleImpl[FILE(0:3)]\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n" +
                "      LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n" +
                "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n" +
                "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n"

        assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), `is`(expected))
    }

    // endregion
    // region Parser :: Invalid Token

    @Test
    fun testInvalidToken() {
        val expected =
                "XQueryModuleImpl[FILE(0:2)]\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: Unexpected token.')\n" +
                "      LeafPsiElement[XQUERY_INVALID_TOKEN(0:2)]('<!')\n"

        assertThat(prettyPrintASTNode(parseText("<!")), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: Expr

    @Test
    fun testExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExpr_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExpr_Multiple_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testExpr_Multiple_SpaceBeforeNextComma() {
        val expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: FLWORExpr

    @Test
    fun testFLWORExpr_ReturnOnly() {
        val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFLWORExpr_ClauseOrdering() {
        val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFLWORExpr_Nested() {
        val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_Nested.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_Nested.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ForClause

    @Test
    fun testForClause() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingInKeyword_PositionalVar() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingInExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_TypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_TypeDeclaration_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_TypeDeclaration_MissingInKeyword_PositionalVar() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MultipleVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MultipleVarName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_MultipleVarName_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForClause_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/ForClause_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: PositionalVar + ForClause

    @Test
    fun testPositionalVar() {
        val expected = loadResource("tests/parser/xquery-1.0/PositionalVar.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PositionalVar.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPositionalVar_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPositionalVar_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPositionalVar_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: LetClause

    @Test
    fun testLetClause() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_Equal() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_Equal.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MissingVarAssignOperator() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MissingVarAssignExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_TypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_TypeDeclaration_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MultipleVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MultipleVarName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_MultipleVarName_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testLetClause_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/LetClause_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: QuantifiedExpr

    @Test
    fun testQuantifiedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MissingInKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MissingInExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MissingSatisfiesKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MissingSatisfiesExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_TypeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MultipleVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MultipleVarName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQuantifiedExpr_MultipleVarName_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: IfExpr

    @Test
    fun testIfExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingCondExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingThenKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingThenExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingElseKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIfExpr_MissingElseExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OrExpr

    @Test
    fun testOrExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/OrExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrExpr_MissingAndExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOrExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/OrExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OrExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AndExpr

    @Test
    fun testAndExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/AndExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AndExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAndExpr_MissingComparisonExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAndExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/AndExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AndExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + GeneralComp

    @Test
    fun testComparisonExpr_GeneralComp() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComparisonExpr_GeneralComp_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComparisonExpr_GeneralComp_MissingRangeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRangeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRangeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + ValueComp

    @Test
    fun testComparisonExpr_ValueComp() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComparisonExpr_ValueComp_MissingRangeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRangeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRangeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + NodeComp

    @Test
    fun testComparisonExpr_NodeComp() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComparisonExpr_NodeComp_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComparisonExpr_NodeComp_MissingRangeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRangeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRangeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: RangeExpr

    @Test
    fun testRangeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/RangeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RangeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRangeExpr_MissingAdditiveExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AdditiveExpr

    @Test
    fun testAdditiveExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAdditiveExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAdditiveExpr_MissingMultiplicativeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAdditiveExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: MultiplicativeExpr

    @Test
    fun testMultiplicativeExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMultiplicativeExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMultiplicativeExpr_MissingUnionExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMultiplicativeExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: UnionExpr

    @Test
    fun testUnionExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/UnionExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionExpr_MissingIntersectExceptExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: IntersectExceptExpr

    @Test
    fun testIntersectExceptExpr_Intersect() {
        val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntersectExceptExpr_Intersect_MissingInstanceofExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntersectExceptExpr_Except() {
        val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntersectExceptExpr_Except_MissingInstanceofExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testIntersectExceptExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: InstanceofExpr

    @Test
    fun testInstanceofExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInstanceofExpr_MissingInstanceKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInstanceofExpr_MissingOfKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInstanceofExpr_MissingSingleType() {
        val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSingleType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSingleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: TreatExpr

    @Test
    fun testTreatExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/TreatExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TreatExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTreatExpr_MissingTreatKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTreatExpr_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTreatExpr_MissingSingleType() {
        val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingSingleType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingSingleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CastableExpr

    @Test
    fun testCastableExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CastableExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastableExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastableExpr_MissingCastableKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastableExpr_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastableExpr_MissingSingleType() {
        val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CastExpr

    @Test
    fun testCastExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastExpr_MissingCastKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastExpr_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCastExpr_MissingSingleType() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: UnaryExpr

    @Test
    fun testUnaryExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_Mixed() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_Mixed_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnaryExpr_MissingValueExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: PathExpr

    @Test
    fun testPathExpr_LeadingForwardSlash() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPathExpr_LeadingForwardSlash_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPathExpr_LeadingDoubleForwardSlash() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPathExpr_LeadingDoubleForwardSlash_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPathExpr_LoneForwardSlash() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPathExpr_LoneDoubleForwardSlash() {
        val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: RelativePathExpr

    @Test
    fun testRelativePathExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRelativePathExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRelativePathExpr_AllDescendants() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRelativePathExpr_AllDescendants_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRelativePathExpr_MissingStepExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testRelativePathExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AxisStep

    @Test
    fun testAxisStep_PredicateList() {
        val expected = loadResource("tests/parser/xquery-1.0/AxisStep_PredicateList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AxisStep_PredicateList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAxisStep_PredicateList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AxisStep_PredicateList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AxisStep_PredicateList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep

    @Test
    fun testForwardStep_KindTest() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_KindTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_KindTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_KindTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_KindTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_KindTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_QName() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_QName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_QName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_QName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_QName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_QName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardStep_MissingNodeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardStep_MissingNodeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardStep_MissingNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + ForwardAxis

    @Test
    fun testForwardAxis_Child() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Child.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Child_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Child_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Descendant() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Descendant_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Descendant_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Attribute() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Attribute_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Attribute_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Self() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Self.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Self_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Self_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_DescendantOrSelf() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_DescendantOrSelf_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_FollowingSibling() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_FollowingSibling_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Following() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Following.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Following_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Following_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + AbbrevForwardStep

    @Test
    fun testAbbrevForwardStep() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_MissingNodeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + AbbrevForwardStep (Keywords)

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_XQuery10() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_KeywordNCNames_XQuery10.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_KeywordNCNames_XQuery10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_XQuery30() {
        val expected = loadResource("tests/parser/xquery-3.0/AbbrevForwardStep_KeywordNCNames_XQuery30.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AbbrevForwardStep_KeywordNCNames_XQuery30.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_XQuery31() {
        val expected = loadResource("tests/parser/xquery-3.1/AbbrevForwardStep_KeywordNCNames_XQuery31.txt")
        val actual = parseResource("tests/parser/xquery-3.1/AbbrevForwardStep_KeywordNCNames_XQuery31.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_UpdateFacility10() {
        val expected = loadResource("tests/parser/xquery-update-1.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility10.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_UpdateFacility30() {
        val expected = loadResource("tests/parser/xquery-update-3.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility30.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility30.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_Scripting10() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/AbbrevForwardStep_KeywordNCNames_Scripting10.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/AbbrevForwardStep_KeywordNCNames_Scripting10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_MarkLogic60() {
        val expected = loadResource("tests/parser/marklogic-6.0/AbbrevForwardStep_KeywordNCNames_MarkLogic60.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/AbbrevForwardStep_KeywordNCNames_MarkLogic60.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_MarkLogic70() {
        val expected = loadResource("tests/parser/marklogic-7.0/AbbrevForwardStep_KeywordNCNames_MarkLogic70.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/AbbrevForwardStep_KeywordNCNames_MarkLogic70.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_MarkLogic80() {
        val expected = loadResource("tests/parser/marklogic-8.0/AbbrevForwardStep_KeywordNCNames_MarkLogic80.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AbbrevForwardStep_KeywordNCNames_MarkLogic80.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_BaseX78() {
        val expected = loadResource("tests/parser/basex-7.8/AbbrevForwardStep_KeywordNCNames_BaseX78.txt")
        val actual = parseResource("tests/parser/basex-7.8/AbbrevForwardStep_KeywordNCNames_BaseX78.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAbbrevForwardStep_KeywordNCNames_BaseX84() {
        val expected = loadResource("tests/parser/basex-8.4/AbbrevForwardStep_KeywordNCNames_BaseX84.txt")
        val actual = parseResource("tests/parser/basex-8.4/AbbrevForwardStep_KeywordNCNames_BaseX84.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep

    @Test
    fun testReverseStep_KindTest() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_KindTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_KindTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_KindTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_KindTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_KindTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_QName() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_QName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_QName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_QName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_QName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_QName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseStep_MissingNodeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseStep_MissingNodeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseStep_MissingNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep + ReverseAxis

    @Test
    fun testReverseAxis_Parent() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Parent.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Parent.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_Parent_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Parent_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Parent_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_Ancestor() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_Ancestor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_PrecedingSibling() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_PrecedingSibling_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_Preceding() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Preceding.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Preceding.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_Preceding_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Preceding_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Preceding_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_AncestorOrSelf() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testReverseAxis_AncestorOrSelf_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep + AbbrevReverseStep

    @Test
    fun testAbbrevReverseStep() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevReverseStep.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevReverseStep.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: NodeTest + KindTest

    @Test
    fun testKindTest_DocumentTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_ElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_AttributeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_SchemaElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_SchemaAttributeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_PITest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_CommentTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_TextTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testKindTest_AnyKindTest() {
        val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: NodeTest + NameTest + Wildcard

    @Test
    fun testWildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_SpaceBeforeColon() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_SpaceAfterColon() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceAfterColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_SpaceBeforeAndAfterColon() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeAndAfterColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeAndAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_MissingPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_MissingPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_MissingPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_NCNamePrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_NCNamePrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_NCNamePrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_NCNameLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_NCNameLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_NCNameLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_KeywordPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_KeywordPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_KeywordPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_KeywordLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_KeywordLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_KeywordLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testWildcard_BothPrefixAndLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/Wildcard_BothPrefixAndLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Wildcard_BothPrefixAndLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: FilterExpr + PredicateList + Predicate

    @Test
    fun testFilterExpr_PredicateList() {
        val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFilterExpr_PredicateList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFilterExpr_PredicateList_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFilterExpr_PredicateList_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFilterExpr_PredicateList_Multiple() {
        val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: VarRef + VarName

    @Test
    fun testVarRef() {
        val expected = loadResource("tests/parser/xquery-1.0/VarRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarRef_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarRef_NCName() {
        val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarRef_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testVarRef_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-1.0/VarRef_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/VarRef_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ParenthesizedExpr

    @Test
    fun testParenthesizedExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedExpr_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedExpr_EmptyExpression() {
        val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedExpr_EmptyExpression_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ContextItemExpr

    @Test
    fun testContextItemExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ContextItemExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ContextItemExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: FunctionCall

    @Test
    fun testFunctionCall() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MissingOpeningParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_SingleParam() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_SingleParam_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MultipleParam() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MultipleParam_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MultipleParam_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_MultipleParam_SpaceBeforeNextComma() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: FunctionCall (Keywords)

    @Test
    fun testFunctionCall_KeywordNCNames_XQuery10() {
        val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.txt")
        val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_XQuery30() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionCall_KeywordNCNames_XQuery30.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionCall_KeywordNCNames_XQuery30.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_XQuery31() {
        val expected = loadResource("tests/parser/xquery-3.1/FunctionCall_KeywordNCNames_XQuery31.txt")
        val actual = parseResource("tests/parser/xquery-3.1/FunctionCall_KeywordNCNames_XQuery31.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_UpdateFacility10() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionCall_KeywordNCNames_UpdateFacility10.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionCall_KeywordNCNames_UpdateFacility10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_UpdateFacility30() {
        val expected = loadResource("tests/parser/xquery-update-3.0/FunctionCall_KeywordNCNames_UpdateFacility30.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/FunctionCall_KeywordNCNames_UpdateFacility30.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_Scripting10() {
        val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_KeywordNCNames_Scripting10.txt")
        val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_KeywordNCNames_Scripting10.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_MarkLogic60() {
        val expected = loadResource("tests/parser/marklogic-6.0/FunctionCall_KeywordNCNames_MarkLogic60.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/FunctionCall_KeywordNCNames_MarkLogic60.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_BaseX78() {
        val expected = loadResource("tests/parser/basex-7.8/FunctionCall_KeywordNCNames_BaseX78.txt")
        val actual = parseResource("tests/parser/basex-7.8/FunctionCall_KeywordNCNames_BaseX78.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionCall_KeywordNCNames_BaseX84() {
        val expected = loadResource("tests/parser/basex-8.4/FunctionCall_KeywordNCNames_BaseX84.txt")
        val actual = parseResource("tests/parser/basex-8.4/FunctionCall_KeywordNCNames_BaseX84.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: CompElemConstructor + ContentExpr

    @Test
    fun testCompElemConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_NoExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_ExprTagName() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_ExprTagName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_ExprTagName_MissingTagNameExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_ExprTagName_MissingClosingTagNameBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompElemConstructor_MissingValueExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CompAttrConstructor

    @Test
    fun testCompAttrConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_NoExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_ExprTagName() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_ExprTagName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_ExprTagName_MissingTagNameExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_ExprTagName_MissingClosingTagNameBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompAttrConstructor_MissingValueExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: CompPIConstructor

    @Test
    fun testCompPIConstructor() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_NoExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_ExprTagName() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_ExprTagName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_ExprTagName_MissingTagNameExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_ExprTagName_MissingClosingTagNameBrace() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_MissingValueExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompPIConstructor_QNameTagName() {
        val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: SingleType

    @Test
    fun testSingleType() {
        val expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSingleType_Optional() {
        val expected = loadResource("tests/parser/xquery-1.0/SingleType_Optional.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SingleType_Optional.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSingleType_Optional_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SingleType_Optional_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SingleType_Optional_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 1.0 :: SequenceType

    @Test
    fun testSequenceType_Empty() {
        val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSequenceType_Empty_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSequenceType_Empty_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: OccurrenceIndicator

    @Test
    fun testOccurrenceIndicator_Optional() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOccurrenceIndicator_Optional_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOccurrenceIndicator_ZeroOrMore() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOccurrenceIndicator_ZeroOrMore_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOccurrenceIndicator_OneOrMore() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testOccurrenceIndicator_OneOrMore_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ItemType

    @Test
    fun testItemType() {
        val expected = loadResource("tests/parser/xquery-1.0/ItemType.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testItemType_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testItemType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AnyKindTest

    @Test
    fun testAnyKindTest() {
        val expected = loadResource("tests/parser/xquery-1.0/AnyKindTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DocumentTest

    @Test
    fun testDocumentTest_ElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_ElementTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_SchemaElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_SchemaElementTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_Empty() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_Empty_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: TextTest

    @Test
    fun testTextTest() {
        val expected = loadResource("tests/parser/xquery-1.0/TextTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TextTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: CommentTest

    @Test
    fun testCommentTest() {
        val expected = loadResource("tests/parser/xquery-1.0/CommentTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CommentTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCommentTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCommentTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: PITest

    @Test
    fun testPITest() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_NCName() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_NCName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_StringLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPITest_StringLiteral_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AttributeTest + AttribNameOrWildcard + AttributeName

    @Test
    fun testAttributeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_MissingAttribNameOrWildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: AttributeTest + TypeName

    @Test
    fun testAttributeTest_TypeName() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_TypeName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_TypeName_MissingTypeName() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeTest_TypeName_MissingComma() {
        val expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: SchemaAttributeTest + AttributeDeclaration

    @Test
    fun testSchemaAttributeTest() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaAttributeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaAttributeTest_MissingAttributeDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaAttributeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ElementTest + ElementNameOrWildcard + ElementName

    @Test
    fun testElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_MissingElementNameOrWildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: ElementTest + TypeName

    @Test
    fun testElementTest_TypeName() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_MissingTypeName() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_MissingComma() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_Optional() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_Optional_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementTest_TypeName_Optional_MissingTypeName() {
        val expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: SchemaElementTest + ElementDeclaration

    @Test
    fun testSchemaElementTest() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaElementTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaElementTest_MissingElementDeclaration() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaElementTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: IntegerLiteral

    @Test
    fun testIntegerLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DecimalLiteral

    @Test
    fun testDecimalLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/DecimalLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DecimalLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: DoubleLiteral

    @Test
    fun testDoubleLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDoubleLiteral_IncompleteExponent() {
        val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral

    @Test
    fun testStringLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_UnclosedString() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + PredefinedEntityRef

    @Test
    fun testStringLiteral_PredefinedEntityRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_PredefinedEntityRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_PredefinedEntityRef_EmptyRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPredefinedEntityRef_MisplacedEntityRef() {
        val expected = loadResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + CharRef

    @Test
    fun testStringLiteral_CharRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_CharRef_IncompleteRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_CharRef_EmptyNumericRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringLiteral_CharRef_EmptyHexadecimalRef() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + EscapeQuot

    @Test
    fun testStringLiteral_EscapeQuot() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + EscapeApos

    @Test
    fun testStringLiteral_EscapeApos() {
        val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.txt")
        val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: Comment

    @Test
    fun testComment() {
        val expected = loadResource("tests/parser/xquery-1.0/Comment.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Comment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComment_UnclosedComment() {
        val expected = loadResource("tests/parser/xquery-1.0/Comment_UnclosedComment.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Comment_UnclosedComment.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComment_UnexpectedCommentEndTag() {
        val expected = loadResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.txt")
        val actual = parseResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: QName

    @Test
    fun testQName() {
        val expected = loadResource("tests/parser/xquery-1.0/QName.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_SpaceBeforeColon() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_SpaceAfterColon() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_SpaceBeforeAndAfterColon() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_NonNCNameLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_MissingLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_MissingLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_MissingPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_MissingPrefixAndLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_KeywordPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_KeywordLocalPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testQName_KeywordLocalPart_MissingPrefixPart() {
        val expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.txt")
        val actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

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
    fun testNCName() {
        val expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt")
        val actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

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
    fun testNCName_Keyword() {
        val expected = loadResource("tests/parser/xquery-1.0/NCName_Keyword.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNCName_Wildcard() {
        val expected = loadResource("tests/parser/xquery-1.0/NCName_Wildcard.txt")
        val actual = parseResource("tests/parser/xquery-1.0/NCName_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 1.0 :: S

    @Test
    fun testS() {
        val expected = loadResource("tests/parser/xquery-1.0/S.txt")
        val actual = parseResource("tests/parser/xquery-1.0/S.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 3.0 :: StringConcatExpr

    @Test
    fun testStringConcatExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConcatExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConcatExpr_MissingRangeExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConcatExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStringConcatExpr_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 3.0 :: SimpleMapExpr

    @Test
    fun testSimpleMapExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSimpleMapExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSimpleMapExpr_MissingPathExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSimpleMapExpr_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ForwardStep

    @Test
    fun testForwardStep_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/ForwardStep_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ForwardStep_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: NodeTest + NameTest + Wildcard

    @Test
    fun testWildcard_BracedURILiteral() {
        val expected = loadResource("tests/parser/xquery-3.0/Wildcard_BracedURILiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/Wildcard_BracedURILiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: PostfixExpr

    @Test
    fun testPostfixExpr_ArgumentList() {
        val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPostfixExpr_ArgumentList_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPostfixExpr_Mixed() {
        val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.txt")
        val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testPostfixExpr_Mixed_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 3.0 :: ArgumentPlaceholder

    @Test
    fun testArgumentPlaceholder() {
        val expected = loadResource("tests/parser/xquery-3.0/ArgumentPlaceholder.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CompElemConstructor

    @Test
    fun testCompElemConstructor_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CompAttrConstructor

    @Test
    fun testCompAttrConstructor_EQName() {
        val expected = loadResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: CompNamespaceConstructor

    @Test
    fun testCompNamespaceConstructor() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_PrefixExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_PrefixExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompNamespaceConstructor_PrefixExpr_MissingURIExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: NamedFunctionRef

    @Test
    fun testNamedFunctionRef() {
        val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedFunctionRef_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedFunctionRef_MissingArity() {
        val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: InlineFunctionExpr

    @Test
    fun testInlineFunctionExpr() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_MissingFunctionBody() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_Annotation() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_Annotation_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_Annotation_MissingFunctionKeyword() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_Annotation_MissingOpeningParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_ParamList() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_ReturnType() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testInlineFunctionExpr_ReturnType_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 3.0 :: NamespaceNodeTest

    @Test
    fun testNamespaceNodeTest() {
        val expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamespaceNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
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
    // region XQuery 3.0 :: FunctionTest

    @Test
    fun testFunctionTest() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionTest.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionTest_MultipleAnnotations() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionTest_MultipleAnnotations_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFunctionTest_TypedFunctionWithAnnotations() {
        val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.txt")
        val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: AnyFunctionTest

    @Test
    fun testAnyFunctionTest() {
        val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyFunctionTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyFunctionTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyFunctionTest_UnexpectedReturnType() {
        val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: TypedFunctionTest

    @Test
    fun testTypedFunctionTest() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_Multiple() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_Multiple_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_MultipleWithOccurenceIndicator() {
        // This is testing handling of whitespace before parsing the next comma.
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_ReturnType() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_ReturnType_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_ReturnType_MissingSequenceType() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypedFunctionTest_EmptyTypeList() {
        val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.txt")
        val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: ParenthesizedItemType

    @Test
    fun testParenthesizedItemType() {
        val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedItemType_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedItemType_MissingItemType() {
        val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testParenthesizedItemType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral

    @Test
    fun testURIQualifiedName() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testURIQualifiedName_KeywordLocalName() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testURIQualifiedName_MissingLocalName() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_MissingLocalName.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_MissingLocalName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testURIQualifiedName_IncompleteLiteral() {
        val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_IncompleteLiteral.txt")
        val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_IncompleteLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

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
