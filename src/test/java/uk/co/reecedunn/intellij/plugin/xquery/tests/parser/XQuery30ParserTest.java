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

import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuery30ParserTest extends XQuery10ParserTest {
    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setXQueryVersion(XQueryVersion.XQUERY_3_0);
    }

    // region A.1 EBNF (XQuery 3.0)

    // region VersionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        final String expected
                = "FileElement[FILE(0:26)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:26)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(15:16)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(16:24)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(16:17)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(17:23)]('latin1')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(23:24)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(24:25)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(25:26)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery encoding \"latin1\" ;")), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:24)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:24)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:23)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:22)]('latin1')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(22:23)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(23:24)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery encoding\"latin1\";")), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_NoEncodingString() {
        final String expected
                = "FileElement[FILE(0:16)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:15)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(15:15)]('XPST0003: Missing encoding string.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(15:16)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery encoding;")), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingSemicolon() {
        final String expected
                = "FileElement[FILE(0:25)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:24)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(15:16)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(16:24)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(16:17)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(17:23)]('latin1')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(23:24)]('\"')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(24:24)]('XPST0003: Missing semicolon.')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(24:25)](':')\n";

        assertThat(prettyPrintASTNode(parseText("xquery encoding \"latin1\":")), is(expected));
    }

    // endregion

    // endregion
}
