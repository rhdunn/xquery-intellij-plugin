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
import com.intellij.psi.PsiElement;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryVersionDecl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class XQueryPsiTest extends ParserTestCase {
    // region File

    public void testFile() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("123");
        assertThat(node.getElementType(), is(XQueryElementType.FILE));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));

        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_1);
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_1));
    }

    // endregion
    // region A.2.1 Terminal Symbols

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        ASTNode node = parseText("\"One Two\"");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getSimpleContents(), is("One Two"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_Empty() {
        ASTNode node = parseText("\"\"");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getSimpleContents(), is(nullValue()));
    }

    // endregion
    // region A.1 EBNF

    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery version \"1.0\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getSimpleContents(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_1_0));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_EmptyVersion() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery version \"\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getSimpleContents(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery version \"1.0\" encoding \"latin1\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getSimpleContents(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getSimpleContents(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_1_0));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery version \"1.0\" encoding \"\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getSimpleContents(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getSimpleContents(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_1_0));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_NoVersion() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery;");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery encoding \"latin1\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getSimpleContents(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_EmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_3_0);

        ASTNode node = parseText("xquery encoding \"\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getSimpleContents(), is(nullValue()));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_AsXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.XQUERY_1_0);

        ASTNode node = parseText("xquery encoding \"latin1\";");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getSimpleContents(), is("latin1"));

        XQueryFile file = (XQueryFile)node.getPsi();
        assertThat(file.getXQueryVersion(), is(XQueryVersion.XQUERY_1_0));
    }

    // endregion

    // endregion
}
