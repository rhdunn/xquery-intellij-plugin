/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import com.intellij.lang.ASTNode;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuery10ParserTest extends ParserTestCase {
    // region Initialization

    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setXQueryVersion(XQueryVersion.XQUERY_1_0);
    }

    // endregion
    // region Basic Parser Tests

    public void testEmptyBuffer() {
        final String expected
                = "FileElement[FILE(0:0)]\n";

        assertThat(prettyPrintASTNode(parseText("")), is(expected));
    }

    public void testBadCharacters() {
        final String expected
                = "FileElement[FILE(0:3)]\n"
                + "   LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n"
                + "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n"
                + "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n";

        assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), is(expected));
    }

    public void testInvalidToken() {
        final String expected
                = "FileElement[FILE(0:2)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: Invalid XQuery symbol or operator.')\n"
                + "      LeafPsiElement[XQUERY_INVALID_TOKEN(0:2)]('--')\n";

        assertThat(prettyPrintASTNode(parseText("--")), is(expected));
    }

    // endregion
    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingVersionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingVersionString() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingEncodingString() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region VersionDecl (XQuery 3.0)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly10.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace10.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingEncodingString() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString10.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon10.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MainModule

    // NOTE: The QueryBody-only and missing QueryBody test cases are covered by
    // the IntegerLiteral and ModuleImport tests respectively.

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    public void testMainModule() {
        final String expected = loadResource("tests/parser/xquery-1.0/MainModule.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/MainModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Module")
    public void testMainModule_WithVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region LibraryModule

    // NOTE: The optional Prolog test cases are covered by the ModuleDecl tests.

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    public void testLibraryModule() {
        final String expected = loadResource("tests/parser/xquery-1.0/LibraryModule.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/LibraryModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Module")
    public void testLibraryModule_WithVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingEqualsAfterName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Prolog

    // NOTE: Test cases for the different items contained within the Prolog node
    // are handled by the test cases for the items that make up a Prolog node.

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testProlog_MultipleImports() {
        final String expected = loadResource("tests/parser/xquery-1.0/Prolog_MultipleImports.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Prolog_MultipleImports.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Import

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Import")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testImport_MissingSchemaOrModuleKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region NamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testNamespaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region BoundarySpaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_MissingPreserveOrStripKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_MissingBoundarySpaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testBoundarySpaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DefaultNamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_MissingElementOrFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testDefaultNamespaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region OptionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionValue() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionName() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region OrderingModeDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_MissingKeywordOrderedOrUnorderedKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_MissingOrderingKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testOrderingModeDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region EmptyOrderDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingGreatestOrLeastKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingEmptyKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingOrderKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testEmptyOrderDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region CopyNamespacesDecl + PreserveMode + InheritMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingInheritMode() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingPreserveMode() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingCopyNamespacesKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testCopyNamespacesDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DefaultCollationDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingCollationKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testDefaultCollationDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region BaseURIDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingBaseUriKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testBaseURIDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region SchemaImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_MissingSchemaUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testSchemaImport_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region SchemaPrefix

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_MissingElementKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ModuleImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_MissingModuleUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testModuleImport_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region VarDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingExprSingle() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingAssignment() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableMarker() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External_MissingVariableName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testVarDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ConstructionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl_MissingPreserveOrStripKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testConstructionDecl_MissingConstructionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testConstructionDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region FunctionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionName() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_EnclosedExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionBody() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_ReturnType() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_ReturnType_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testFunctionDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ParamList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParamList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList_MissingComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParamList_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Param

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_MissingParameterName() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_MissingParameterName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param_MissingParameterName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_MissingVariableMarker() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_TypeDeclaration_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Expr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_SpaceBeforeNextComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region VarRef + VarName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_NCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_MissingVarName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/VarRef_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ParenthesizedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_EmptyExpression() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_EmptyExpression_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ContextItemExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ContextItemExpr")
    public void testContextItemExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ContextItemExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ContextItemExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region OrderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingOpeningBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region UnorderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingOpeningBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region FunctionCall

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_SingleParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_SingleParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_SpaceBeforeNextComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DirCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnclosedComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnexpectedCommentEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DirElemConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_SelfClosing() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_SelfClosing_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_IncompleteOpenTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_IncompleteCloseTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region CDataSection

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CDataSection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_Unclosed() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection_Unclosed.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CDataSection_Unclosed.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_UnexpectedEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region TypeDeclaration + AtomicType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    public void testTypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    public void testTypeDeclaration_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    public void testTypeDeclaration_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region SequenceType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region OccurrenceIndicator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_Optional() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_Optional_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_ZeroOrMore() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_ZeroOrMore_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_OneOrMore() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_OneOrMore_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ItemType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region AnyKindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DocumentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_ElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_ElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_SchemaElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_SchemaElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_Empty() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_Empty_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region TextTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TextTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TextTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region CommentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CommentTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CommentTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region PITest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_NCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_NCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_StringLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_StringLiteral_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region AttributeTest + AttribNameOrWildcard + AttributeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_MissingAttribNameOrWildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    public void testAttributeTest_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    public void testAttributeTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region AttributeTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region SchemaAttributeTest + AttributeDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_MissingAttributeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ElementTest + ElementNameOrWildcard + ElementName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_MissingElementNameOrWildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region ElementTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region SchemaElementTest + ElementDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingOpeningParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_MissingElementDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
    public void testDecimalLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/DecimalLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DecimalLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral_IncompleteExponent() {
        final String expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_UnclosedString() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region StringLiteral + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_EmptyRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testPredefinedEntityRef_MisplacedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region StringLiteral + EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region StringLiteral + EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Comment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnclosedComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment_UnclosedComment.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Comment_UnclosedComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnexpectedCommentEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyNumericRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyHexadecimalRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeAndAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_NonNCNameLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingLocalPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixAndLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordLocalPart_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testNCName_UnexpectedQName() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testNCName_UnexpectedQName_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName_Keyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_Keyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
        final String expected = loadResource("tests/parser/xquery-1.0/S.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/S.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
