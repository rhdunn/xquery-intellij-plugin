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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi;

import com.intellij.lang.ASTNode;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class XQueryPsiTest extends ParserTestCase {
    // region File

    public void testFile_Empty() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseText("");

        assertThat(node.getElementType(), is(XQueryElementType.FILE));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_1);
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_1));
    }

    public void testFile() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        assertThat(node.getElementType(), is(XQueryElementType.FILE));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_1);
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_1));
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/StringLiteral.xq");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getStringValue(), is("One Two"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_Empty() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/StringLiteral_Empty.xq");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getStringValue(), is(nullValue()));
    }

    // endregion
    // region XQuery 1.0 :: VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-1.0/VersionDecl.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_CommentBeforeDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-1.0/VersionDecl_CommentBeforeDecl.xq");

        XQueryModule modulePsi = PsiNavigation.findChildrenByClass(node.getPsi(), XQueryModule.class).get(0);
        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(modulePsi, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_EmptyVersion() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-1.0/VersionDecl_EmptyVersion.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getStringValue(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEncoding_CommentsAsWhitespace() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_CommentsAsWhitespace.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getStringValue(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_EmptyEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getStringValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getStringValue(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_NoVersion() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-1.0/VersionDecl_NoVersion.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_XQUERY));
    }

    // endregion
    // region XQuery 1.0 :: FunctionDecl

    public void testFunctionDecl_QName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.QNAME));
    }

    public void testFunctionDecl_NCName() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/FunctionDecl_NCName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.NCNAME));
    }

    public void testFunctionDecl_Keyword() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/FunctionDecl_Keyword.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    public void testFunctionDecl_ReservedKeyword() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/FunctionDecl_ReservedKeyword.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_IF));
    }

    public void testFunctionDecl_MissingFunctionName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    // endregion
    // region XQuery 1.0 :: ValidateExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ValidateExpr")
    public void testValidateExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq");

        XQueryValidateExpr validateExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryValidateExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_VALIDATE));
    }

    // endregion
    // region XQuery 1.0 :: ForwardAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Attribute() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ATTRIBUTE));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Child() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_CHILD));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Descendant() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_DescendantOrSelf() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT_OR_SELF));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Following() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_FollowingSibling() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING_SIBLING));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Self() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_SELF));
    }

    // endregion
    // region XQuery 1.0 :: FunctionCall

    public void testFunctionCall_NCName() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/FunctionCall_NCName.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.NCNAME));
    }

    public void testFunctionCall_KeywordNCName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.NCNAME));
    }

    // endregion
    // region XQuery 1.0 :: AnyKindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AnyKindTest")
    public void testAnyKindTest() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryAnyKindTest anyKindTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryAnyKindTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NODE));
    }

    // endregion
    // region XQuery 1.0 :: TextTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-TextTest")
    public void testTextTest() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/TextTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryTextTest textTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryTextTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)textTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_TEXT));
    }

    // endregion
    // region XQuery 1.0 :: QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getPrefix().getText(), is("one"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getLocalName().getText(), is("two"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName_KeywordLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getPrefix().getText(), is("sort"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.K_LEAST));
        assertThat(qnamePsi.getLocalName().getText(), is("least"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName_MissingLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getPrefix().getText(), is("one"));

        assertThat(qnamePsi.getLocalName(), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName_KeywordPrefixPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.K_ORDER));
        assertThat(qnamePsi.getPrefix().getText(), is("order"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getLocalName().getText(), is("two"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName_DirElemConstructor() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryDirElemConstructor.class);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(dirElemConstructorPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.XML_TAG_NCNAME));
        assertThat(qnamePsi.getPrefix().getText(), is("a"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.XML_TAG_NCNAME));
        assertThat(qnamePsi.getLocalName().getText(), is("b"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName_DirAttributeList() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildrenByClass(dirElemConstructorPsi, XQueryDirAttributeList.class).get(0);
        XQueryQName qnamePsi = PsiNavigation.findChildrenByClass(dirAttributeListPsi, XQueryQName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.XML_ATTRIBUTE_NCNAME));
        assertThat(qnamePsi.getPrefix().getText(), is("xml"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.XML_ATTRIBUTE_NCNAME));
        assertThat(qnamePsi.getLocalName().getText(), is("id"));
    }

    // endregion
    // region XQuery 1.0 :: NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    public void testNCName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryNCName ncnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryNCName.class).get(0);

        assertThat(ncnamePsi.getPrefix(), is(nullValue()));

        assertThat(ncnamePsi.getLocalName(), is(notNullValue()));
        assertThat(ncnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.K_COLLATION));
        assertThat(ncnamePsi.getLocalName().getText(), is("collation"));
    }

    // endregion
    // region XQuery 3.0 :: VersionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getStringValue(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ENCODING));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_EmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/psi/xquery-3.0/VersionDecl_EncodingOnly_EmptyEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getStringValue(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ENCODING));
    }

    // endregion
    // region XQuery 3.0 :: ForClause (IntermediateClause)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_FirstIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                   instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOR));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_AfterForIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOR));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_AfterLetIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOR));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_AfterWhereIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOR));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ForClause")
    public void testForClause_AfterOrderByIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOR));
    }

    // endregion
    // region XQuery 3.0 :: LetClause (IntermediateClause)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_FirstIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_LET));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_AfterForIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_LET));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_AfterLetIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_LET));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_AfterWhereIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_LET));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-LetClause")
    public void testLetClause_AfterOrderByIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_LET));
    }

    // endregion
    // region XQuery 3.0 :: WhereClause (IntermediateClause)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_FirstIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_AfterForIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_AfterLetIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_AfterWhereIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-WhereClause")
    public void testWhereClause_AfterOrderByIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHERE));
    }

    // endregion
    // region XQuery 3.0 :: OrderByClause (IntermediateClause)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_FirstIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ORDER));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_AfterForIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ORDER));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_AfterLetIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ORDER));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_AfterWhereIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(4).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(4);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ORDER));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-OrderByClause")
    public void testOrderByClause_AfterOrderByIntermediateClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ORDER));
    }

    // endregion
    // region XQuery 3.0 :: AllowingEmpty

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AllowingEmpty")
    public void testAllowingEmpty() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq");

        XQueryForClause forClausePsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildrenByClass(forClausePsi, XQueryForBinding.class).get(0);
        XQueryAllowingEmpty allowingEmptyPsi = PsiNavigation.findChildrenByClass(forBindingPsi, XQueryAllowingEmpty.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)allowingEmptyPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ALLOWING));
    }

    // endregion
    // region XQuery 3.0 :: SequenceTypeUnion

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SequenceTypeUnion")
    public void testSequenceTypeUnion() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTypeswitchExpr.class);
        XQueryCaseClause caseClausePsi = PsiNavigation.findChildrenByClass(typeswitchExprPsi, XQueryCaseClause.class).get(0);
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = PsiNavigation.findChildrenByClass(caseClausePsi, XQuerySequenceTypeUnion.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.UNION));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testSequenceTypeUnion_NoUnion() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTypeswitchExpr.class);
        XQueryCaseClause caseClausePsi = PsiNavigation.findChildrenByClass(typeswitchExprPsi, XQueryCaseClause.class).get(0);
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = PsiNavigation.findChildrenByClass(caseClausePsi, XQuerySequenceTypeUnion.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.SEQUENCE_TYPE));
    }

    // endregion
    // region XQuery 3.0 :: StringConcatExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq");

        XQueryStringConcatExpr stringConcatExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringConcatExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.CONCATENATION));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-StringConcatExpr")
    public void testStringConcatExpr_NoConcatenation() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");

        XQueryStringConcatExpr stringConcatExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringConcatExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.RANGE_EXPR));
    }

    // endregion
    // region XQuery 3.0 :: SimpleMapExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq");

        XQuerySimpleMapExpr simpleMapExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQuerySimpleMapExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.MAP_OPERATOR));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr_NoMap() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");

        XQuerySimpleMapExpr simpleMapExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQuerySimpleMapExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.PATH_EXPR));
    }

    // endregion
    // region XQuery 3.0 :: ArgumentList

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ArgumentList")
    public void testArgumentList_FunctionCall() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildrenByClass(functionCallPsi, XQueryArgumentList.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.PARENTHESIS_OPEN));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ArgumentList")
    public void testArgumentList_PostfixExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq");

        XQueryPostfixExpr postfixExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryPostfixExpr.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildrenByClass(postfixExprPsi, XQueryArgumentList.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.PARENTHESIS_OPEN));
    }

    // endregion
    // region XQuery 3.0 :: ArgumentPlaceholder

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ArgumentPlaceholder")
    public void testArgumentPlaceholder() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildrenByClass(functionCallPsi, XQueryArgumentList.class).get(0);
        XQueryArgument argumentPsi = PsiNavigation.findChildrenByClass(argumentListPsi, XQueryArgument.class).get(0);
        XQueryArgumentPlaceholder argumentPlaceholderPsi = PsiNavigation.findFirstChildByClass(argumentPsi, XQueryArgumentPlaceholder.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentPlaceholderPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.OPTIONAL));
    }

    // endregion
    // region XQuery 3.0 :: FunctionDecl

    public void testFunctionDecl_ReservedKeyword_Function() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_Function.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    public void testFunctionDecl_ReservedKeyword_Switch() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_Switch.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_SWITCH));
    }

    public void testFunctionDecl_ReservedKeyword_NamespaceNode() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_NamespaceNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryFunctionDecl.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NAMESPACE_NODE));
    }

    // endregion
    // region XQuery 3.0 :: ValidateExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testValidateExpr_Type() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq");

        XQueryValidateExpr validateExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryValidateExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_TYPE));
    }

    // endregion
    // region XQuery 3.0 :: Annotation

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ValidateExpr")
    public void testAnnotation() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/Annotation.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryAnnotation annotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryAnnotation.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)annotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.ANNOTATION_INDICATOR));
    }

    // endregion
    // region XQuery 3.0 :: TryClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryClause")
    public void testTryClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/CatchClause.xq");

        XQueryTryClause tryClausePsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTryClause.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)tryClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_TRY));
    }

    // endregion
    // region XQuery 3.0 :: CatchClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TryClause")
    public void testCatchClause() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/CatchClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTryCatchExpr.class);
        XQueryCatchClause catchClausePsi = PsiNavigation.findChildrenByClass(tryCatchExprPsi, XQueryCatchClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)catchClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_CATCH));
    }

    // endregion
    // region XQuery 3.0 :: NamedFunctionRef

    public void testNamedFunctionRef_QName() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_QName.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.FUNCTION_REF_OPERATOR));
    }

    public void testNamedFunctionRef_NCName() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.FUNCTION_REF_OPERATOR));
    }

    public void testNamedFunctionRef_Keyword() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_Keyword.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.FUNCTION_REF_OPERATOR));
    }

    public void testNamedFunctionRef_ReservedKeyword() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_IF));
    }

    public void testNamedFunctionRef_ReservedKeyword_Function() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    public void testNamedFunctionRef_ReservedKeyword_NamespaceNode() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_NamespaceNode.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NAMESPACE_NODE));
    }

    public void testNamedFunctionRef_ReservedKeyword_Switch() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Switch.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_SWITCH));
    }

    // endregion
    // region XQuery 3.0 :: InlineFunctionExpr

    public void testInlineFunctionExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq");

        XQueryInlineFunctionExpr inlineFunctionExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryInlineFunctionExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    public void testInlineFunctionExpr_AnnotationOnly() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq");

        XQueryInlineFunctionExpr inlineFunctionExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryInlineFunctionExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.ANNOTATION));
    }

    // endregion
    // region XQuery 3.0 :: AnyFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryAnyFunctionTest anyFunctionTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryAnyFunctionTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    // endregion
    // region XQuery 3.0 :: TypedFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryTypedFunctionTest typedFunctionTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryTypedFunctionTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)typedFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FUNCTION));
    }

    // endregion
    // region XQuery 3.0 :: ParenthesizedItemType

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryParenthesizedItemType parenthesizedItemTypePsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryParenthesizedItemType.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)parenthesizedItemTypePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.PARENTHESIS_OPEN));
    }

    // endregion
    // region XQuery 1.0 :: EQName

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_QName() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notNullValue()));
        assertThat(eqnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getPrefix().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalName(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getLocalName().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_KeywordLocalPart() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordLocalPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notNullValue()));
        assertThat(eqnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getPrefix().getText(), is("sort"));

        assertThat(eqnamePsi.getLocalName(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.K_LEAST));
        assertThat(eqnamePsi.getLocalName().getText(), is("least"));
    }

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_MissingLocalPart() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_MissingLocalPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notNullValue()));
        assertThat(eqnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getPrefix().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalName(), is(nullValue()));
    }

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_KeywordPrefixPart() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordPrefixPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notNullValue()));
        assertThat(eqnamePsi.getPrefix().getNode().getElementType(), is(XQueryTokenType.K_ORDER));
        assertThat(eqnamePsi.getPrefix().getText(), is("order"));

        assertThat(eqnamePsi.getLocalName(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getLocalName().getText(), is("column"));
    }

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_NCName() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_NCName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(nullValue()));

        assertThat(eqnamePsi.getLocalName(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getLocalName().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EQName")
    public void testEQName_URIQualifiedName() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/SimpleTypeName_URIQualifiedName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildrenByClass(castExprPsi, XQuerySingleType.class).get(0);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findFirstChildByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notNullValue()));
        assertThat(eqnamePsi.getPrefix().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(eqnamePsi.getPrefix().getText(), is("Q{http://www.w3.org/2001/XMLSchema}"));

        assertThat(eqnamePsi.getLocalName(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(eqnamePsi.getLocalName().getText(), is("double"));
    }

    // endregion
    // region XQuery 3.0 :: URIQualifiedName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-URIQualifiedName")
    public void testURIQualifiedName() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryURIQualifiedName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryURIQualifiedName.class).get(0);

        assertThat(qnamePsi.getPrefix(), is(notNullValue()));
        assertThat(qnamePsi.getPrefix().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(qnamePsi.getPrefix().getText(), is("Q{one{two}"));

        assertThat(qnamePsi.getLocalName(), is(notNullValue()));
        assertThat(qnamePsi.getLocalName().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(qnamePsi.getLocalName().getText(), is("three"));
    }

    // endregion
    // region XQuery 3.0 :: BracedURILiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-BracedURILiteral")
    public void testBracedURILiteral() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryOptionDecl.class);
        XQueryURIQualifiedName qnamePsi = PsiNavigation.findChildrenByClass(optionDeclPsi, XQueryURIQualifiedName.class).get(0);
        XQueryBracedURILiteral bracedURILiteralPsi = PsiNavigation.findFirstChildByClass(qnamePsi, XQueryBracedURILiteral.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)bracedURILiteralPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later, or MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BRACED_URI_LITERAL_START));
    }

    // endregion
}
