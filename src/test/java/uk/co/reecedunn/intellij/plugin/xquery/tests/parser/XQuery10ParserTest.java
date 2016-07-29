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
    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setXQueryVersion(XQueryVersion.XQUERY_1_0);
    }

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
    // region A.2.1 Terminal Symbols (XQuery 1.0)

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
        final String expected = loadResource("tests/parser/xquery-1.0/Comment.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/Comment.xq");
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
    public void testStringLiteral_CharRef_EmptyHexidecimalRef() {
        final String expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexidecimalRef.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexidecimalRef.xq");
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

    // endregion
    // region A.1 EBNF (XQuery 1.0)

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
    // region ModuleImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport() {
        final String expected
                = "FileElement[FILE(0:45)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:45)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(43:44)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(44:45)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:43)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:43)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(13:42)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(13:14)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(14:41)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(41:42)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(42:43)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module\"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_NoModuleKeyword() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:7)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Missing keyword 'module'.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(6:7)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_NoModuleUri() {
        final String expected
                = "FileElement[FILE(0:14)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:14)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(13:13)]('XPST0003: Missing URI string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(13:14)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_MissingSemicolon() {
        final String expected
                = "FileElement[FILE(0:44)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:43)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(43:43)]('XPST0003: Missing semicolon.')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(43:44)](':')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\":")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace() {
        final String expected
                = "FileElement[FILE(0:62)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:62)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(14:23)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(24:28)]('test')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(28:29)](' ')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(29:30)]('=')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(30:31)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(31:60)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(31:32)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(32:59)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(59:60)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(60:61)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(61:62)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module namespace test = \"http://www.example.com/test\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:59)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:59)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(14:23)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(24:28)]('test')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(28:29)]('=')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(29:58)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(29:30)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(30:57)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(57:58)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(58:59)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module namespace test=\"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_NoNCName() {
        final String expected
                = "FileElement[FILE(0:56)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:56)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(14:23)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(24:24)]('XPST0003: Missing identifier.')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(24:25)]('=')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(25:26)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(26:55)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(26:27)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(27:54)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(54:55)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(55:56)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module namespace = \"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithNamespace_NoEquals() {
        final String expected
                = "FileElement[FILE(0:59)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:59)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(14:23)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(24:28)]('test')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(28:29)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(29:29)]('XPST0003: Expected '='.')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(29:58)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(29:30)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(30:57)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(57:58)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(58:59)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module namespace test \"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence() {
        final String expected
                = "FileElement[FILE(0:60)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:60)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(43:44)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(44:46)]('at')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(46:47)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(47:58)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(47:48)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(48:57)]('/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(57:58)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(58:59)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(59:60)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\" at \"/test.xqy\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:56)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:56)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(13:42)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(13:14)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(14:41)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(41:42)]('\"')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(42:44)]('at')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(44:55)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(44:45)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(45:54)]('/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(54:55)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(55:56)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module\"http://www.example.com/test\"at\"/test.xqy\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_MissingUri() {
        final String expected
                = "FileElement[FILE(0:47)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:47)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(43:44)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(44:46)]('at')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(46:46)]('XPST0003: Missing URI string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(46:47)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\" at;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple() {
        final String expected
                = "FileElement[FILE(0:78)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:78)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(43:44)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(44:46)]('at')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(46:47)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(47:58)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(47:48)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(48:57)]('/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(57:58)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(58:59)](' ')\n"
                + "      LeafPsiElement[XQUERY_COMMA_TOKEN(59:60)](',')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(60:61)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(61:76)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(61:62)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(62:75)]('/app/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(75:76)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(76:77)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(77:78)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\" at \"/test.xqy\" , \"/app/test.xqy\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_Multiple_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:72)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:72)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(13:42)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(13:14)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(14:41)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(41:42)]('\"')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(42:44)]('at')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(44:55)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(44:45)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(45:54)]('/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(54:55)]('\"')\n"
                + "      LeafPsiElement[XQUERY_COMMA_TOKEN(55:56)](',')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(56:71)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(56:57)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(57:70)]('/app/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(70:71)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(71:72)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module\"http://www.example.com/test\"at\"/test.xqy\",\"/app/test.xqy\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    public void testModuleImport_WithAtSequence_NoNCNameAfterComma() {
        final String expected
                = "FileElement[FILE(0:61)]\n"
                + "   XQueryModuleImportImpl[XQUERY_MODULE_IMPORT(0:61)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_IMPORT(0:6)]('import')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(7:13)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(13:14)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(14:43)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:42)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(42:43)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(43:44)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_AT(44:46)]('at')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(46:47)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(47:58)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(47:48)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(48:57)]('/test.xqy')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(57:58)]('\"')\n"
                + "      LeafPsiElement[XQUERY_COMMA_TOKEN(58:59)](',')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(59:60)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(60:60)]('XPST0003: Missing URI string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(60:61)](';')\n";

        assertThat(prettyPrintASTNode(parseText("import module \"http://www.example.com/test\" at \"/test.xqy\", ;")), is(expected));
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

    // endregion
    // region A.1 EBNF (XQuery 3.0)

    // region VersionDecl

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

    // endregion
}
