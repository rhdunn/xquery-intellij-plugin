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
    // region PredefinedEntityRef

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
    // region EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region EscapeApos

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

    // endregion
    // region NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/NCName.xq");
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
