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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryLanguageType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class XQueryPsiTest extends ParserTestCase {
    // region File

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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    // endregion
    // region XQuery 1.0 :: FunctionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-FunctionDecl")
    public void testFunctionDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionDecl.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)functionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    // endregion
    // region XQuery 1.0 :: ForwardAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Attribute() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Child() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Descendant() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_DescendantOrSelf() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Following() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_FollowingSibling() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ForwardAxis")
    public void testForwardAxis_Self() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_3_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY).getNode().getElementType(), is(XQueryTokenType.K_ENCODING));
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

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)versionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_3_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY).getNode().getElementType(), is(XQueryTokenType.K_ENCODING));
    }

    // endregion
}
