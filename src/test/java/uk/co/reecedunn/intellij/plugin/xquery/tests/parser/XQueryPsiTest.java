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
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryVersionDecl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryPsiTest extends ParserTestCase {
    // region A.2.1 Terminal Symbols

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        ASTNode node = parseText("\"One Two\"");

        ASTNode stringLiteral = node.getFirstChildNode();
        assertThat(stringLiteral.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteral stringLiteralPsi = (XQueryStringLiteral) stringLiteral.getPsi();
        assertThat(stringLiteralPsi.getSimpleContents(), is("One Two"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_Empty() {
        ASTNode node = parseText("\"\"");

        ASTNode stringLiteral = node.getFirstChildNode();
        assertThat(stringLiteral.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteral stringLiteralPsi = (XQueryStringLiteral) stringLiteral.getPsi();
        assertThat(stringLiteralPsi.getSimpleContents(), is(nullValue()));
    }

    // endregion
    // region A.1 EBNF

    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl() {
        ASTNode node = parseText("xquery version \"1.0\";");

        ASTNode versionDecl = node.getFirstChildNode();
        assertThat(versionDecl.getElementType(), is(XQueryElementType.VERSION_DECL));

        XQueryVersionDecl versionDeclPsi = (XQueryVersionDecl)versionDecl.getPsi();
        assertThat(versionDeclPsi.getVersion(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_EmptyVersion() {
        ASTNode node = parseText("xquery version \"\";");

        ASTNode versionDecl = node.getFirstChildNode();
        assertThat(versionDecl.getElementType(), is(XQueryElementType.VERSION_DECL));

        XQueryVersionDecl versionDeclPsi = (XQueryVersionDecl)versionDecl.getPsi();
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        ASTNode node = parseText("xquery version \"1.0\" encoding \"latin1\";");

        ASTNode versionDecl = node.getFirstChildNode();
        assertThat(versionDecl.getElementType(), is(XQueryElementType.VERSION_DECL));

        XQueryVersionDecl versionDeclPsi = (XQueryVersionDecl)versionDecl.getPsi();
        assertThat(versionDeclPsi.getVersion(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is("latin1"));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEmptyEncoding() {
        ASTNode node = parseText("xquery version \"1.0\" encoding \"\";");

        ASTNode versionDecl = node.getFirstChildNode();
        assertThat(versionDecl.getElementType(), is(XQueryElementType.VERSION_DECL));

        XQueryVersionDecl versionDeclPsi = (XQueryVersionDecl)versionDecl.getPsi();
        assertThat(versionDeclPsi.getVersion(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_NoVersion() {
        ASTNode node = parseText("xquery;");

        ASTNode versionDecl = node.getFirstChildNode();
        assertThat(versionDecl.getElementType(), is(XQueryElementType.VERSION_DECL));

        XQueryVersionDecl versionDeclPsi = (XQueryVersionDecl)versionDecl.getPsi();
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));
    }

    // endregion

    // endregion
}
