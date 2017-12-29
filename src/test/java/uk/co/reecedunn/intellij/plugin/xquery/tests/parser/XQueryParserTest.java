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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryParserTest extends ParserTestCase {
    // region Parser :: Empty Buffer

    public void testEmptyBuffer() {
        final String expected
                = "FileElement[FILE(0:0)]\n";

        assertThat(prettyPrintASTNode(parseText("")), is(expected));
    }

    // endregion
    // region Parser :: Bad Characters

    public void testBadCharacters() {
        final String expected
                = "FileElement[FILE(0:3)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n"
                + "      LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n"
                + "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n"
                + "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n";

        assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), is(expected));
    }

    // endregion
    // region Parser :: Invalid Token

    public void testInvalidToken() {
        final String expected
                = "FileElement[FILE(0:2)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: Unexpected token.')\n"
                + "      LeafPsiElement[XQUERY_INVALID_TOKEN(0:2)]('<!')\n";

        assertThat(prettyPrintASTNode(parseText("<!")), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingVersionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingVersionString() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingEncodingString() {
        final String expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: MainModule

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    public void testMainModule() {
        final String expected = loadResource("tests/parser/xquery-1.0/MainModule.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MainModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Module")
    public void testMainModule_WithVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MainModule")
    public void testMainModule_TokensAfterExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: LibraryModule

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LibraryModule")
    public void testLibraryModule() {
        final String expected = loadResource("tests/parser/xquery-1.0/LibraryModule.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LibraryModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LibraryModule")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Module")
    public void testLibraryModule_WithVersionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LibraryModule")
    public void testLibraryModule_WithInvalidConstructRecovery() {
        final String expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingEqualsAfterName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingNamespaceUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Prolog

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testProlog_MultipleImports() {
        final String expected = loadResource("tests/parser/xquery-1.0/Prolog_MultipleImports.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Prolog_MultipleImports.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Import

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Import")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testImport_MissingSchemaOrModuleKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: NamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    public void testNamespaceDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NamespaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testNamespaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: BoundarySpaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_MissingPreserveOrStripKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_MissingBoundarySpaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testBoundarySpaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DefaultNamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_MissingElementOrFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Element_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl_Function_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultNamespaceDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testDefaultNamespaceDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OptionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionValue() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionName() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingOptionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OptionDecl")
    public void testOptionDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrderingModeDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_MissingKeywordOrderedOrUnorderedKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_MissingOrderingKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    public void testOrderingModeDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderingModeDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testOrderingModeDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: EmptyOrderDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingGreatestOrLeastKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingEmptyKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingOrderKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EmptyOrderDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testEmptyOrderDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CopyNamespacesDecl + PreserveMode + InheritMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingInheritMode() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingPreserveMode() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingCopyNamespacesKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    public void testCopyNamespacesDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CopyNamespacesDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PreserveMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InheritMode")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testCopyNamespacesDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DefaultCollationDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingCollationKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DefaultCollationDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testDefaultCollationDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: BaseURIDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    public void testBaseURIDecl_MissingBaseUriKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BaseURIDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testBaseURIDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SchemaImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_MissingSchemaUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    public void testSchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testSchemaImport_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SchemaPrefix

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_MissingNamespaceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaPrefix")
    public void testSchemaPrefix_Default_MissingElementKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ModuleImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_MissingModuleUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_MissingUri() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testModuleImport_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: VarDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_Equal() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingExprSingle() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingAssignment() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableMarker() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingVariableKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_External_MissingVariableName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    public void testVarDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testVarDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ConstructionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl_MissingPreserveOrStripKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BoundarySpaceDecl")
    public void testConstructionDecl_MissingConstructionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    public void testConstructionDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ConstructionDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testConstructionDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: FunctionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionName() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_EnclosedExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingFunctionBody() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_ReturnType() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_ReturnType_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Prolog")
    public void testFunctionDecl_PrologHeaderStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ParamList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParamList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParamList")
    public void testParamList_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParamList_MissingComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParamList_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Param

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_MissingParameterName() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_MissingParameterName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param_MissingParameterName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_MissingVariableMarker() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Param")
    public void testParam_TypeDeclaration_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testEnclosedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Expr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Expr")
    public void testExpr_Multiple_SpaceBeforeNextComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Expr_Multiple_SpaceBeforeNextComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: FLWORExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    public void testFLWORExpr_ReturnOnly() {
        final String expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    public void testFLWORExpr_ClauseOrdering() {
        final String expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    public void testFLWORExpr_Nested() {
        final String expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_Nested.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_Nested.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testForClause_MissingInKeyword_PositionalVar() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MissingInExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_TypeDeclaration_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testForClause_TypeDeclaration_MissingInKeyword_PositionalVar() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MultipleVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MultipleVarName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_MultipleVarName_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testForClause_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForClause_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: PositionalVar + ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testPositionalVar() {
        final String expected = loadResource("tests/parser/xquery-1.0/PositionalVar.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PositionalVar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testPositionalVar_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testPositionalVar_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PositionalVar")
    public void testPositionalVar_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: LetClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_Equal() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MissingVarAssignOperator() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MissingVarAssignExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_TypeDeclaration_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MultipleVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MultipleVarName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-LetClause")
    public void testLetClause_MultipleVarName_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    public void testLetClause_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/LetClause_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: WhereClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-WhereClause")
    public void testWhereClause_ForClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-WhereClause")
    public void testWhereClause_ForClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-WhereClause")
    public void testWhereClause_ForClause_MissingWhereExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-WhereClause")
    public void testWhereClause_LetClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/WhereClause_LetClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/WhereClause_LetClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrderByClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_ForClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_LetClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_LetClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_LetClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_MissingByKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_MissingOrderSpecList() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_Stable_ForClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_Stable_LetClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_Stable_MissingOrderKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_Stable_MissingByKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    public void testOrderByClause_Stable_MissingOrderSpecList() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrderSpecList + OrderSpec

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderSpecList")
    public void testOrderSpecList() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderSpecList")
    public void testOrderSpecList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderSpecList")
    public void testOrderSpecList_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderSpecList")
    public void testOrderSpecList_Multiple_MissingOrderSpec() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrderModifier

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderModifier")
    public void testOrderModifier_DirectionOnly() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderModifier")
    public void testOrderModifier_EmptyOnly() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderModifier")
    public void testOrderModifier_EmptyOnly_MissingSpecifier() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderModifier")
    public void testOrderModifier_CollationOnly() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderByClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderModifier")
    public void testOrderModifier_CollationOnly_MissingUriString() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: QuantifiedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MissingInExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MissingSatisfiesKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MissingSatisfiesExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_TypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MultipleVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MultipleVarName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuantifiedExpr")
    public void testQuantifiedExpr_MultipleVarName_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: TypeswitchExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingTypeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingCaseClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_MultipleCaseClause() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_Variable() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_Variable_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    public void testTypeswitchExpr_Variable_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CaseClause + TypeswitchExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_Variable() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_Variable_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeswitchExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CaseClause")
    public void testCaseClauseExpr_Variable_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: IfExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingCondExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingThenKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingThenExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingElseKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IfExpr")
    public void testIfExpr_MissingElseExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrExpr")
    public void testOrExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrExpr")
    public void testOrExpr_MissingAndExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrExpr")
    public void testOrExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AndExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AndExpr")
    public void testAndExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/AndExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AndExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AndExpr")
    public void testAndExpr_MissingComparisonExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AndExpr")
    public void testAndExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/AndExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AndExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + GeneralComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-GeneralComp")
    public void testComparisonExpr_GeneralComp() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-GeneralComp")
    public void testComparisonExpr_GeneralComp_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-GeneralComp")
    public void testComparisonExpr_GeneralComp_MissingRangeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRangeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRangeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + ValueComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValueComp")
    public void testComparisonExpr_ValueComp() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValueComp")
    public void testComparisonExpr_ValueComp_MissingRangeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRangeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRangeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ComparisonExpr + NodeComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeComp")
    public void testComparisonExpr_NodeComp() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeComp")
    public void testComparisonExpr_NodeComp_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.xq");
        System.out.println(prettyPrintASTNode(actual));
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ComparisonExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeComp")
    public void testComparisonExpr_NodeComp_MissingRangeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRangeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRangeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: RangeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RangeExpr")
    public void testRangeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/RangeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RangeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RangeExpr")
    public void testRangeExpr_MissingAdditiveExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AdditiveExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AdditiveExpr")
    public void testAdditiveExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AdditiveExpr")
    public void testAdditiveExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AdditiveExpr")
    public void testAdditiveExpr_MissingMultiplicativeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AdditiveExpr")
    public void testAdditiveExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: MultiplicativeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr_MissingUnionExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: UnionExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnionExpr")
    public void testUnionExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnionExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnionExpr")
    public void testUnionExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnionExpr")
    public void testUnionExpr_MissingIntersectExceptExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnionExpr")
    public void testUnionExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnionExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnionExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: IntersectExceptExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr_Intersect() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr_Intersect_MissingInstanceofExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr_Except() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr_Except_MissingInstanceofExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: InstanceofExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InstanceofExpr")
    public void testInstanceofExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InstanceofExpr")
    public void testInstanceofExpr_MissingInstanceKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InstanceofExpr")
    public void testInstanceofExpr_MissingOfKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-InstanceofExpr")
    public void testInstanceofExpr_MissingSingleType() {
        final String expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSingleType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSingleType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: TreatExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TreatExpr")
    public void testTreatExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/TreatExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TreatExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TreatExpr")
    public void testTreatExpr_MissingTreatKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TreatExpr")
    public void testTreatExpr_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TreatExpr")
    public void testTreatExpr_MissingSingleType() {
        final String expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingSingleType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingSingleType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CastableExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastableExpr")
    public void testCastableExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastableExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastableExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastableExpr")
    public void testCastableExpr_MissingCastableKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastableExpr")
    public void testCastableExpr_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastableExpr")
    public void testCastableExpr_MissingSingleType() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CastExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    public void testCastExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    public void testCastExpr_MissingCastKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    public void testCastExpr_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    public void testCastExpr_MissingSingleType() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: UnaryExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_Mixed() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_Mixed_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnaryExpr")
    public void testUnaryExpr_MissingValueExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_MissingValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ValidateExpr + ValidationMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr_ValidationMode() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr_ValidationMode_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr_ValidationMode_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ExtensionExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_EmptyExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_MultiplePragmas() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testExtensionExpr_MultiplePragmas_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Pragma

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testPragma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testPragma_MissingPragmaName() {
        final String expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testPragma_MissingPragmaContents() {
        // This is invalid according to the XQuery grammar, but is supported by
        // XQuery implementations.
        final String expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testPragma_MissingPragmaContents_CompactWhitespace() {
        // This is valid according to the XQuery grammar.
        final String expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ExtensionExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Pragma")
    public void testPragma_UnclosedPragma() {
        final String expected = loadResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: PathExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LeadingForwardSlash() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LeadingForwardSlash_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LeadingDoubleForwardSlash() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LeadingDoubleForwardSlash_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LoneForwardSlash() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PathExpr")
    public void testPathExpr_LoneDoubleForwardSlash() {
        final String expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: RelativePathExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr_AllDescendants() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr_AllDescendants_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr_MissingStepExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-RelativePathExpr")
    public void testRelativePathExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AxisStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AxisStep")
    public void testAxisStep_PredicateList() {
        final String expected = loadResource("tests/parser/xquery-1.0/AxisStep_PredicateList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AxisStep_PredicateList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AxisStep")
    public void testAxisStep_PredicateList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AxisStep_PredicateList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AxisStep_PredicateList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_KindTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_KindTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_KindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_KindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_KindTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_KindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_QName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_QName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_QName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_QName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_QName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_QName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_Wildcard_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    public void testForwardStep_MissingNodeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardStep_MissingNodeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardStep_MissingNodeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + ForwardAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Child() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Child.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Child_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Child_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Descendant() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Descendant_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Descendant_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Attribute() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Attribute_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Attribute_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Self() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Self.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Self_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Self_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_DescendantOrSelf() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_DescendantOrSelf_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_FollowingSibling() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_FollowingSibling_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Following() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Following.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Following_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_Following_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + AbbrevForwardStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep() {
        final String expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_MissingNodeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ForwardStep + AbbrevForwardStep (Keywords)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_XQuery10() {
        final String expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_KeywordNCNames_XQuery10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_KeywordNCNames_XQuery10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_XQuery30() {
        final String expected = loadResource("tests/parser/xquery-3.0/AbbrevForwardStep_KeywordNCNames_XQuery30.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AbbrevForwardStep_KeywordNCNames_XQuery30.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_XQuery31() {
        final String expected = loadResource("tests/parser/xquery-3.1/AbbrevForwardStep_KeywordNCNames_XQuery31.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AbbrevForwardStep_KeywordNCNames_XQuery31.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_UpdateFacility10() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-update-1.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_UpdateFacility30() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility30.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-update-3.0/AbbrevForwardStep_KeywordNCNames_UpdateFacility30.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_Scripting10() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/AbbrevForwardStep_KeywordNCNames_Scripting10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-sx-1.0/AbbrevForwardStep_KeywordNCNames_Scripting10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_MarkLogic60() {
        final String expected = loadResource("tests/parser/marklogic-6.0/AbbrevForwardStep_KeywordNCNames_MarkLogic60.txt");
        final XQueryModule actual = parseResource("tests/parser/marklogic-6.0/AbbrevForwardStep_KeywordNCNames_MarkLogic60.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_MarkLogic70() {
        final String expected = loadResource("tests/parser/marklogic-7.0/AbbrevForwardStep_KeywordNCNames_MarkLogic70.txt");
        final XQueryModule actual = parseResource("tests/parser/marklogic-7.0/AbbrevForwardStep_KeywordNCNames_MarkLogic70.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_MarkLogic80() {
        final String expected = loadResource("tests/parser/marklogic-8.0/AbbrevForwardStep_KeywordNCNames_MarkLogic80.txt");
        final XQueryModule actual = parseResource("tests/parser/marklogic-8.0/AbbrevForwardStep_KeywordNCNames_MarkLogic80.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep_KeywordNCNames_BaseX78() {
        final String expected = loadResource("tests/parser/basex-7.8/AbbrevForwardStep_KeywordNCNames_BaseX78.txt");
        final XQueryModule actual = parseResource("tests/parser/basex-7.8/AbbrevForwardStep_KeywordNCNames_BaseX78.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_KindTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_KindTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_KindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_KindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_KindTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_KindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_QName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_QName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_QName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_QName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_QName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_QName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_Wildcard_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    public void testReverseStep_MissingNodeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseStep_MissingNodeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseStep_MissingNodeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep + ReverseAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Parent() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Parent.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Parent.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Parent_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Parent_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Parent_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Ancestor() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Ancestor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Ancestor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_PrecedingSibling() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_PrecedingSibling_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PrecedingSibling_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Preceding() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Preceding.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Preceding.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_Preceding_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_Preceding_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_Preceding_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_AncestorOrSelf() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseStep")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ReverseAxis")
    public void testReverseAxis_AncestorOrSelf_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_AncestorOrSelf_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ReverseStep + AbbrevReverseStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AbbrevReverseStep")
    public void testAbbrevReverseStep() {
        final String expected = loadResource("tests/parser/xquery-1.0/AbbrevReverseStep.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AbbrevReverseStep.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: NodeTest + KindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_DocumentTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_ElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_AttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_SchemaElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_SchemaAttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_PITest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_CommentTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_TextTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NodeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-KindTest")
    public void testKindTest_AnyKindTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: NodeTest + NameTest + Wildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_SpaceBeforeColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_SpaceAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceAfterColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_SpaceBeforeAndAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeAndAfterColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_SpaceBeforeAndAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_MissingPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_NCNamePrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_NCNamePrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_NCNamePrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_NCNameLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_NCNameLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_NCNameLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_KeywordPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_KeywordPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_KeywordPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_KeywordLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_KeywordLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_KeywordLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Wildcard")
    public void testWildcard_BothPrefixAndLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/Wildcard_BothPrefixAndLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Wildcard_BothPrefixAndLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: FilterExpr + PredicateList + Predicate

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FilterExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredicateList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Predicate")
    public void testFilterExpr_PredicateList() {
        final String expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FilterExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredicateList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Predicate")
    public void testFilterExpr_PredicateList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FilterExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredicateList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Predicate")
    public void testFilterExpr_PredicateList_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FilterExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredicateList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Predicate")
    public void testFilterExpr_PredicateList_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FilterExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredicateList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Predicate")
    public void testFilterExpr_PredicateList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: VarRef + VarName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_NCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-1.0/VarRef_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/VarRef_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ParenthesizedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_EmptyExpression() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr_EmptyExpression_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ContextItemExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ContextItemExpr")
    public void testContextItemExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/ContextItemExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ContextItemExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OrderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OrderedExpr")
    public void testOrderedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: UnorderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-UnorderedExpr")
    public void testUnorderedExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: FunctionCall

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_SingleParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_SingleParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_MultipleParam_SpaceBeforeNextComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: FunctionCall (Keywords)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_XQuery10() {
        final String expected = loadResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_XQuery30() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionCall_KeywordNCNames_XQuery30.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionCall_KeywordNCNames_XQuery30.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_XQuery31() {
        final String expected = loadResource("tests/parser/xquery-3.1/FunctionCall_KeywordNCNames_XQuery31.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/FunctionCall_KeywordNCNames_XQuery31.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_UpdateFacility10() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionCall_KeywordNCNames_UpdateFacility10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-update-1.0/FunctionCall_KeywordNCNames_UpdateFacility10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_UpdateFacility30() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/FunctionCall_KeywordNCNames_UpdateFacility30.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-update-3.0/FunctionCall_KeywordNCNames_UpdateFacility30.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_Scripting10() {
        final String expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_KeywordNCNames_Scripting10.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_KeywordNCNames_Scripting10.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_MarkLogic60() {
        final String expected = loadResource("tests/parser/marklogic-6.0/FunctionCall_KeywordNCNames_MarkLogic60.txt");
        final XQueryModule actual = parseResource("tests/parser/marklogic-6.0/FunctionCall_KeywordNCNames_MarkLogic60.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionCall")
    public void testFunctionCall_KeywordNCNames_BaseX78() {
        final String expected = loadResource("tests/parser/basex-7.8/FunctionCall_KeywordNCNames_BaseX78.txt");
        final XQueryModule actual = parseResource("tests/parser/basex-7.8/FunctionCall_KeywordNCNames_BaseX78.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_SelfClosing() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_SelfClosing_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_SpaceBeforeNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_IncompleteOpenTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_IncompleteCloseTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_MissingClosingTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemConstructor_ClosingTagWithoutOpenTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeList + DirAttributeValue + (Quot|Apos)AttrValueContent

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_NCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    public void testDirAttributeList_MissingAttributeValue() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    public void testDirAttributeList_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testDirAttributeValue_EscapeQuot() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testDirAttributeValue_EscapeApos() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + (Quot|Apos)AttrContentChar

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuotAttrContentChar")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AposAttrContentChar")
    public void testDirAttributeValue_AttrContentChar() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    public void testDirAttributeValue_CommonContent_EscapeCharacters() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirAttributeValue_CommonContent_PredefinedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirAttributeValue_CommonContent_CharRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirAttributeValue_CommonContent_CharRef_EmptyNumericRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirAttributeValue_CommonContent_CharRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testDirAttributeValue_CommonContent_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + ElementContentChar

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementContentChar")
    public void testDirElemContent_ElementContentChar() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    public void testDirElemContent_CommonContent() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirElemContent_CommonContent_PredefinedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirElemContent_CommonContent_PredefinedEntityRef_EmptyRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirElemContent_CommonContent_CharRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirElemContent_CommonContent_CharRef_EmptyHexadecimalRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirElemContent_CommonContent_CharRef_EmptyNumericRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testDirElemContent_CommonContent_CharRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EnclosedExpr")
    public void testDirElemContent_CommonContent_EnclosedExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CDataSection

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testDirElemContent_CDataSection() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testDirElemContent_CDataSection_Unclosed() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testDirElemContent_CDataSection_UnexpectedEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirElemConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemConstructor")
    public void testDirElemContent_DirElemConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirCommentConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirElemContent_DirCommentConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirPIConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    public void testDirElemContent_DirPIConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnclosedComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnexpectedCommentEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DirPIConstructor + DirPIContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIContents")
    public void testDirPIConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIContents")
    public void testDirPIConstructor_UnexpectedWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIContents")
    public void testDirPIConstructor_MissingNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIContents")
    public void testDirPIConstructor_MissingContents() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirPIContents")
    public void testDirPIConstructor_MissingEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CDataSection

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CDataSection.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_Unclosed() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection_Unclosed.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CDataSection_Unclosed.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_UnexpectedEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompDocConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompDocConstructor")
    public void testCompDocConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompDocConstructor")
    public void testCompDocConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompDocConstructor")
    public void testCompDocConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompElemConstructor + ContentExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_NoExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_ExprTagName() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_ExprTagName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_ExprTagName_MissingTagNameExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_ExprTagName_MissingClosingTagNameBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompElemConstructor")
    public void testCompElemConstructor_MissingValueExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompAttrConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_NoExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_ExprTagName() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_ExprTagName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_ExprTagName_MissingTagNameExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_ExprTagName_MissingClosingTagNameBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompAttrConstructor")
    public void testCompAttrConstructor_MissingValueExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompTextConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompTextConstructor")
    public void testCompTextConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompTextConstructor")
    public void testCompTextConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompTextConstructor")
    public void testCompTextConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompCommentConstructor")
    public void testCompCommentConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompCommentConstructor")
    public void testCompCommentConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompCommentConstructor")
    public void testCompCommentConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CompPIConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_NoExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_ExprTagName() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_ExprTagName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_ExprTagName_MissingTagNameExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_ExprTagName_MissingClosingTagNameBrace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_MissingValueExpr() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CompPIConstructor")
    public void testCompPIConstructor_QNameTagName() {
        final String expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SingleType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SingleType")
    public void testSingleType() {
        final String expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SingleType")
    public void testSingleType_Optional() {
        final String expected = loadResource("tests/parser/xquery-1.0/SingleType_Optional.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SingleType_Optional.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CastExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SingleType")
    public void testSingleType_Optional_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SingleType_Optional_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SingleType_Optional_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: TypeDeclaration + AtomicType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AtomicType")
    public void testTypeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AtomicType")
    public void testTypeDeclaration_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AtomicType")
    public void testTypeDeclaration_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SequenceType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SequenceType")
    public void testSequenceType_Empty_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: OccurrenceIndicator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_Optional() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_Optional_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_Optional_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_ZeroOrMore() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_ZeroOrMore_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_ZeroOrMore_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_OneOrMore() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator_OneOrMore_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OccurrenceIndicator_OneOrMore_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ItemType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ItemType")
    public void testItemType_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AnyKindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AnyKindTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DocumentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_ElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_ElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_ElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_SchemaElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_SchemaElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_SchemaElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_Empty() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_Empty_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_Empty_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DocumentTest")
    public void testDocumentTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DocumentTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: TextTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TextTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TextTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/TextTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: CommentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CommentTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CommentTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentTest")
    public void testCommentTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/CommentTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: PITest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_NCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_NCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_StringLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PITest")
    public void testPITest_StringLiteral_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PITest_StringLiteral_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AttributeTest + AttribNameOrWildcard + AttributeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_MissingAttribNameOrWildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingAttribNameOrWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeName")
    public void testAttributeTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    public void testAttributeTest_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    public void testAttributeTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: AttributeTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testAttributeTest_TypeName_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/AttributeTest_TypeName_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SchemaAttributeTest + AttributeDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_MissingAttributeDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingAttributeDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaAttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeDeclaration")
    public void testSchemaAttributeTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaAttributeTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ElementTest + ElementNameOrWildcard + ElementName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_MissingElementNameOrWildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingElementNameOrWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementName")
    public void testElementTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: ElementTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeName")
    public void testElementTest_TypeName_Optional_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/ElementTest_TypeName_Optional_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: SchemaElementTest + ElementDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_MissingElementDeclaration() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingElementDeclaration.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-SchemaElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementDeclaration")
    public void testSchemaElementTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/SchemaElementTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
    public void testDecimalLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/DecimalLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DecimalLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral_IncompleteExponent() {
        final String expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_UnclosedString() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_EmptyRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testPredefinedEntityRef_MisplacedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyNumericRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyHexadecimalRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Comment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnclosedComment() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment_UnclosedComment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Comment_UnclosedComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnexpectedCommentEndTag() {
        final String expected = loadResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_SpaceAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeAndAfterColon() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_SpaceBeforeAndAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_NonNCNameLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_NonNCNameLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixAndLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixAndLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_KeywordLocalPart_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_WildcardPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_WildcardLocalPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_WildcardLocalPart_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        final String expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testNCName_UnexpectedQName() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testNCName_UnexpectedQName_MissingPrefixPart() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName_Keyword() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_Keyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-1.0/NCName_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/NCName_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 1.0 :: S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
        final String expected = loadResource("tests/parser/xquery-1.0/S.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-1.0/S.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: VersionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingEncodingString() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: DecimalFormatDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_MissingEQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_Default() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_Property_AllProperties() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_Property_MissingEquals() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_Property_MissingStringLiteral() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl_MissingDecimalFormatKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ContextItemDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_Equal() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_MissingVarValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_MissingAssignment() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_MissingItemKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_AsItemType() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_AsItemType_MissingItemType() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_External() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_External_DefaultValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_External_DefaultValue_Equal() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_External_DefaultValue_MissingValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_MissingContextKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ContextItemDecl")
    public void testContextItemDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: AnnotatedDecl + Annotation

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_MissingQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_MissingQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_MissingQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_ParameterList() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_ParameterList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_ParameterList_MissingLiteral() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_ParameterList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnnotatedDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Annotation")
    public void testAnnotation_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/Annotation_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Annotation_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: VarDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VarDecl")
    public void testVarDecl_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/VarDecl_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VarDecl_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VarDecl")
    public void testVarDecl_External_DefaultValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: FunctionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionDecl")
    public void testFunctionDecl_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionDecl_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionDecl_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: Param

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Param")
    public void testParam_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/Param_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Param_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: OptionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OptionDecl")
    public void testOptionDecl_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/OptionDecl_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/OptionDecl_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: FLWORExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FLWORExpr")
    public void testFLWORExpr_RelaxedOrdering() {
        final String expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FLWORExpr")
    public void testFLWORExpr_NestedWithoutReturnClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: IntermediateClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-IntermediateClause")
    public void testIntermediateClause_WhereFor() {
        final String expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-IntermediateClause")
    public void testIntermediateClause_ForWhereLet() {
        final String expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-IntermediateClause")
    public void testIntermediateClause_ForOrderByLet() {
        final String expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ForClause_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ForClause_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: AllowingEmpty + ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AllowingEmpty")
    public void testAllowingEmpty() {
        final String expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AllowingEmpty")
    public void testAllowingEmpty_MissingEmptyKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AllowingEmpty")
    public void testAllowingEmpty_ForBinding_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: PositionalVar + ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForClause")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-PositionalVar")
    public void testPositionalVar_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/PositionalVar_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/PositionalVar_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: LetClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FLWORExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/LetClause_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/LetClause_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: TumblingWindowClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingWindowKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_MissingStartCondition() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_TypeDecl() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause_TypeDecl_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: SlidingWindowClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingWindowKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingStartCondition() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_MissingEndCondition() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_TypeDecl() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause_TypeDecl_MissingInKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: WindowStartCondition

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowStartCondition")
    public void testWindowStartCondition() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowStartCondition")
    public void testWindowStartCondition_MissingWhenKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowStartCondition")
    public void testWindowStartCondition_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: WindowEndCondition

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowEndCondition")
    public void testWindowEndCondition() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowEndCondition")
    public void testWindowEndCondition_MissingWhenKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowEndCondition")
    public void testWindowEndCondition_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowEndCondition")
    public void testWindowEndCondition_Only() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowEndCondition")
    public void testWindowEndCondition_Only_MissingEndKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: WindowVars

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Empty() {
        final String expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Current() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Current_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Position() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Position.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Position.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Previous() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Previous_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Previous_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Next() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Next_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_Next_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WindowVars")
    public void testWindowVars_AllVars() {
        final String expected = loadResource("tests/parser/xquery-3.0/WindowVars_AllVars.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CountClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CountClause")
    public void testCountClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/CountClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CountClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CountClause")
    public void testCountClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CountClause")
    public void testCountClause_MissingVariableIndicator() {
        final String expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CountClause")
    public void testCountClause_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVarName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: WhereClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/WhereClause_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: GroupByClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupByClause")
    public void testGroupByClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupByClause")
    public void testGroupByClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupByClause")
    public void testGroupByClause_MissingByKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupByClause")
    public void testGroupByClause_MissingGroupingSpecList() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpecList

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpecList")
    public void testGroupingSpecList() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpecList")
    public void testGroupingSpecList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpecList")
    public void testGroupingSpecList_Multiple_MissingGroupingSpec() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpec

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Value() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Value_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Value_Equal() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Value_MissingValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_TypedValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_TypedValue_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_TypedValue_Equal() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_TypedValue_MissingAssignment() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_TypedValue_MissingValue() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Collation() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_Collation_MissingUriLiteral() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-GroupingSpec")
    public void testGroupingSpec_ValueAndCollation() {
        final String expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: OrderByClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/OrderByClause_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: QuantifiedExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testQuantifiedExpr_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: SwitchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchExpr")
    public void testSwitchExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchExpr")
    public void testSwitchExpr_MissingCaseClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_MissingDefaultKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-QuantifiedExpr")
    public void testSwitchExpr_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: SwitchCaseClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause_MissingCaseOperand() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause_MultipleCases() {
        final String expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: TypeswitchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypeswitchExpr")
    public void testTypeswitchExpr_Variable_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CaseClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CaseClause")
    public void testCaseClause_Variable_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CaseClause + SequenceTypeUnion

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SequenceTypeUnion")
    public void testSequenceTypeUnion() {
        final String expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SequenceTypeUnion")
    public void testSequenceTypeUnion_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SequenceTypeUnion")
    public void testSequenceTypeUnion_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: TryClause + TryTargetExpr + TryCatchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryClause")
    public void testTryClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/TryClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TryClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryClause")
    public void testTryClause_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CatchClause + TryCatchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause_MissingCatchErrorList() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    public void testCatchClause_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CatchErrorList + CatchClause + TryCatchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchErrorList")
    public void testCatchErrorList() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchErrorList")
    public void testCatchErrorList_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchErrorList")
    public void testCatchErrorList_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchClause")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CatchErrorList")
    public void testCatchErrorList_Multiple_MissingNameTest() {
        final String expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: StringConcatExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr_MissingRangeExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ValidateExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type() {
        final String expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type_MissingTypeName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: Pragma

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Pragma")
    public void testPragma_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/Pragma_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Pragma_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: SimpleMapExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr_MissingPathExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ForwardStep

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForwardStep")
    public void testForwardStep_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ForwardStep_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ForwardStep_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: NodeTest + NameTest + Wildcard

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-Wildcard")
    public void testWildcard_BracedURILiteral() {
        final String expected = loadResource("tests/parser/xquery-3.0/Wildcard_BracedURILiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/Wildcard_BracedURILiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: PostfixExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-PostfixExpr")
    public void testPostfixExpr_ArgumentList() {
        final String expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-PostfixExpr")
    public void testPostfixExpr_ArgumentList_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-PostfixExpr")
    public void testPostfixExpr_Mixed() {
        final String expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-PostfixExpr")
    public void testPostfixExpr_Mixed_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: VarRef + VarName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarRef")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VarName")
    public void testVarRef_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/VarRef_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/VarRef_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: FunctionCall

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionCall")
    public void testFunctionCall_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionCall_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionCall_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ArgumentPlaceholder

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ArgumentPlaceholder")
    public void testArgumentPlaceholder() {
        final String expected = loadResource("tests/parser/xquery-3.0/ArgumentPlaceholder.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CompElemConstructor

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompElemConstructor")
    public void testCompElemConstructor_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CompAttrConstructor

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompAttrConstructor")
    public void testCompAttrConstructor_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: CompNamespaceConstructor

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_PrefixExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_PrefixExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_PrefixExpr_MissingURIExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: NamedFunctionRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamedFunctionRef")
    public void testNamedFunctionRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamedFunctionRef")
    public void testNamedFunctionRef_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamedFunctionRef")
    public void testNamedFunctionRef_MissingArity() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: InlineFunctionExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_MissingFunctionBody() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_Annotation() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_Annotation_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_Annotation_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_Annotation_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_ParamList() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_ReturnType() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr_ReturnType_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: SingleType

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SingleType")
    public void testSingleType_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/SingleType_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/SingleType_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: TypeDeclaration + AtomicOrUnionType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TypeDeclaration")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AtomicOrUnionType")
    public void testTypeDeclaration_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: NamespaceNodeTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamespaceNodeTest")
    public void testNamespaceNodeTest() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamespaceNodeTest")
    public void testNamespaceNodeTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-NamespaceNodeTest")
    public void testNamespaceNodeTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/NamespaceNodeTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: AttributeTest + AttribNameOrWildcard + AttributeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttribNameOrWildcard")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AttributeName")
    public void testAttributeTest_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/AttributeTest_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AttributeTest_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: AttributeTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AttributeTest")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypeName")
    public void testAttributeTest_TypeName_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ElementTest + ElementNameOrWildcard + ElementName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementNameOrWildcard")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ElementName")
    public void testElementTest_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ElementTest_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ElementTest_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ElementTest + TypeName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ElementTest")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypeName")
    public void testElementTest_TypeName_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: FunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionTest")
    public void testFunctionTest() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionTest")
    public void testFunctionTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionTest")
    public void testFunctionTest_MultipleAnnotations() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-FunctionTest")
    public void testFunctionTest_MultipleAnnotations_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: AnyFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest() {
        final String expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest_UnexpectedReturnType() {
        final String expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: TypedFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_Multiple_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_MultipleWithOccurenceIndicator() {
        // This is testing handling of whitespace before parsing the next comma.
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_ReturnType() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_ReturnType_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_ReturnType_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_ReturnType_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest_EmptyTypeList() {
        final String expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: ParenthesizedItemType

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType() {
        final String expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType_MissingItemType() {
        final String expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    public void testURIQualifiedName() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    public void testURIQualifiedName_KeywordLocalName() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    public void testURIQualifiedName_MissingLocalName() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_MissingLocalName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_MissingLocalName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    public void testURIQualifiedName_IncompleteLiteral() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_IncompleteLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_IncompleteLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    public void testURIQualifiedName_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral + PredefinedEntityRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testBracedURILiteral_PredefinedEntityRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testBracedURILiteral_PredefinedEntityRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testBracedURILiteral_PredefinedEntityRef_EmptyRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName + BracedURILiteral + CharRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testBracedURILiteral_CharRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testBracedURILiteral_CharRef_IncompleteRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testBracedURILiteral_CharRef_EmptyNumericRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testBracedURILiteral_CharRef_EmptyHexadecimalRef() {
        final String expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: DFPropertyName + DecimalFormatDecl

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-DFPropertyName")
    public void testDecimalFormatDecl_Property_XQuery31() {
        final String expected = loadResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: EnclosedExpr

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-EnclosedExpr")
    public void testEnclosedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: TryClause + TryTargetExpr + TryCatchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-TryClause")
    public void testTryClause_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/TryClause_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CatchClause + TryCatchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryCatchExpr")
    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-CatchClause")
    public void testCatchClause_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: ArrowExpr

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowExpr")
    public void testArrowExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowExpr")
    public void testArrowExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowExpr")
    public void testArrowExpr_MissingArgumentList() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowExpr")
    public void testArrowExpr_MissingFunctionSpecifier() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowExpr")
    public void testArrowExpr_MultipleArrows() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: ValidateExpr + ValidationMode

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ValidateExpr")
    public void testValidateExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: Lookup

    public void testLookup() {
        final String expected = loadResource("tests/parser/xquery-3.1/Lookup.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/Lookup.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testLookup_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testLookup_MissingKeySpecifier() {
        final String expected = loadResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: KeySpecifier

    public void testKeySpecifier_NCName() {
        final String expected = loadResource("tests/parser/xquery-3.1/UnaryLookup.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testKeySpecifier_IntegerLiteral() {
        final String expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_IntegerLiteral.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_IntegerLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testKeySpecifier_ParenthesizedExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_ParenthesizedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_ParenthesizedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testKeySpecifier_Wildcard() {
        final String expected = loadResource("tests/parser/xquery-3.1/KeySpecifier_Wildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/KeySpecifier_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: ArrowFunctionSpecifier

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowFunctionSpecifier")
    public void testArrowFunctionSpecifier_EQName() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowFunctionSpecifier")
    public void testArrowFunctionSpecifier_VarRef() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_VarRef.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_VarRef.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-ArrowFunctionSpecifier")
    public void testArrowFunctionSpecifier_ParenthesizedExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_ParenthesizedExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/ArrowFunctionSpecifier_ParenthesizedExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: OrderedExpr

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-OrderedExpr")
    public void testOrderedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: UnorderedExpr

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-UnorderedExpr")
    public void testUnorderedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: DirAttributeValue + CommonContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-EnclosedExpr")
    public void testDirAttributeValue_CommonContent_EnclosedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: DirElemContent + CommonContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-EnclosedExpr")
    public void testDirElemContent_CommonContent_EnclosedExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CompDocConstructor

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-CompDocConstructor")
    public void testCompDocConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CompNamespaceConstructor

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_PrefixExpr_MissingPrefixExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-CompNamespaceConstructor")
    public void testCompNamespaceConstructor_MissingURIExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CompTextConstructor

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-CompTextConstructor")
    public void testCompTextConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CompCommentConstructor

    @Specification(name="XQuery 3.1", reference="https://www.w3.org/TR/2017/REC-xquery-31-20170321/#prod-xquery31-CompCommentConstructor")
    public void testCompCommentConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: MapConstructor

    public void testMapConstructor() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: MapConstructorEntry + MapConstructor

    public void testMapConstructorEntry() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_MissingSeparator() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_MissingValueExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple_MissingEntry() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_NCName() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_NCName_WhitespaceAfterColon() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_NCName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_QName_KeyExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_QName_ValueExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_QName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_VarRef_NCName() {
        final String expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: SquareArrayConstructor

    public void testSquareArrayConstructor() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testSquareArrayConstructor_Multiple_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: CurlyArrayConstructor

    public void testCurlyArrayConstructor() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_Multiple() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCurlyArrayConstructor_Multiple_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: StringConstructor + StringConstructorContent

    public void testStringConstructor() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructor.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStringConstructor_MissingEndOfString() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStringConstructor_Empty() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructor_Empty.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructor_Empty.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: StringConstructorInterpolation

    public void testStringConstructorInterpolation() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStringConstructorInterpolation_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStringConstructorInterpolation_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStringConstructorInterpolation_MultipleExpr() {
        final String expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: UnaryLookup

    public void testUnaryLookup() {
        final String expected = loadResource("tests/parser/xquery-3.1/UnaryLookup.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnaryLookup_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnaryLookup_MissingKeySpecifier() {
        final String expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: AnyMapTest

    public void testAnyMapTest() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyMapTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyMapTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyMapTest_MissingWildcard() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyMapTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: TypedMapTest

    public void testTypedMapTest() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedMapTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedMapTest_MissingAtomicOrUnionType() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedMapTest_MissingComma() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedMapTest_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedMapTest_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: AnyArrayTest

    public void testAnyArrayTest() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyArrayTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyArrayTest_MissingWildcard() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyArrayTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region XQuery 3.1 :: TypedArrayTest

    public void testTypedArrayTest() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedArrayTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedArrayTest_MissingSequenceType() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTypedArrayTest_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.txt");
        final XQueryModule actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
