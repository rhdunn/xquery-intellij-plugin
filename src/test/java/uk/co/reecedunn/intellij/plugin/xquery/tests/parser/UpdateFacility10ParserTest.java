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

public class UpdateFacility10ParserTest extends XQuery10ParserTest {
    // region Initialization

    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setImplementation("w3c");
        settings.setXQueryVersion(XQueryVersion.XQUERY_1_0);
        settings.setXQuery10Dialect("w3c/1.0-update");
    }

    // endregion
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingRevalidationMode() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsBefore() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
