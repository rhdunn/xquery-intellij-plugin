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

import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuery10ParserTest extends ParserTestCase {
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
    // region A.2.1 Terminal Symbols

    // region IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryNumericLiteralImpl[XQUERY_INTEGER_LITERAL_TOKEN(0:4)]('1234')\n";

        assertThat(prettyPrintASTNode(parseText("1234")), is(expected));
    }

    // endregion
    // region DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
    public void testDecimalLiteral() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryNumericLiteralImpl[XQUERY_DECIMAL_LITERAL_TOKEN(0:7)]('3.14159')\n";

        assertThat(prettyPrintASTNode(parseText("3.14159")), is(expected));
    }

    // endregion
    // region DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral() {
        final String expected
                = "FileElement[FILE(0:12)]\n"
                + "   XQueryNumericLiteralImpl[XQUERY_DOUBLE_LITERAL_TOKEN(0:12)]('2.99792458e8')\n";

        assertThat(prettyPrintASTNode(parseText("2.99792458e8")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral_IncompleteExponent() {
        final String expected
                = "FileElement[FILE(0:11)]\n"
                + "   XQueryNumericLiteralImpl[XQUERY_DECIMAL_LITERAL_TOKEN(0:10)]('2.99792458')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(10:11)]('XPST0003: Incomplete double exponent.')\n"
                + "      LeafPsiElement[XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN(10:11)]('e')\n";

        assertThat(prettyPrintASTNode(parseText("2.99792458e")), is(expected));
    }

    // endregion
    // region StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        final String expected
                = "FileElement[FILE(0:9)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:9)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(1:8)]('One Two')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(8:9)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"One Two\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_UnclosedString() {
        final String expected
                = "FileElement[FILE(0:8)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:8)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(1:8)]('One Two')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(8:8)]('XPST0003: Unclosed string literal.')\n";

        assertThat(prettyPrintASTNode(parseText("\"One Two")), is(expected));
    }

    // endregion
    // region PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:7)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      XQueryPredefinedEntityRefImpl[XQUERY_PREDEFINED_ENTITY_REFERENCE_TOKEN(1:6)]('&amp;')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(6:7)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&amp;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_IncompleteRef() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:7)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      LeafPsiElement[XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN(1:6)]('&quot')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Invalid entity reference character.')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(6:7)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&quot\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef_EmptyRef() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(1:3)]('XPST0003: Entity references must not be empty.')\n"
                + "         LeafPsiElement[XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN(1:3)]('&;')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testPredefinedEntityRef_MisplacedEntityRef() {
        final String expected
                = "FileElement[FILE(0:1)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Entity references are not allowed here.')\n"
                + "      LeafPsiElement[XQUERY_ENTITY_REFERENCE_NOT_IN_STRING_TOKEN(0:1)]('&')\n";

        assertThat(prettyPrintASTNode(parseText("&")), is(expected));
    }

    // endregion
    // region EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      XQueryEscapeCharacterImpl[XQUERY_STRING_LITERAL_ESCAPED_CHARACTER_TOKEN(1:3)]('\"\"')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"\"\"\"")), is(expected));
    }

    // endregion
    // region EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)](''')\n"
                + "      XQueryEscapeCharacterImpl[XQUERY_STRING_LITERAL_ESCAPED_CHARACTER_TOKEN(1:3)]('''')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)](''')\n";

        assertThat(prettyPrintASTNode(parseText("''''")), is(expected));
    }

    // endregion
    // region Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        final String expected
                = "FileElement[FILE(0:10)]\n"
                + "   XQueryCommentImpl[XQUERY_COMMENT(0:10)]\n"
                + "      LeafPsiElement[XQUERY_COMMENT_START_TAG_TOKEN(0:2)]('(:')\n"
                + "      PsiCommentImpl[XQUERY_COMMENT_TOKEN(2:8)](' Test ')\n"
                + "      LeafPsiElement[XQUERY_COMMENT_END_TAG_TOKEN(8:10)](':)')\n";

        assertThat(prettyPrintASTNode(parseText("(: Test :)")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnclosedComment() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryCommentImpl[XQUERY_COMMENT(0:7)]\n"
                + "      LeafPsiElement[XQUERY_COMMENT_START_TAG_TOKEN(0:2)]('(:')\n"
                + "      PsiCommentImpl[XQUERY_COMMENT_TOKEN(2:7)](' Test')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(7:7)]('XPST0003: Unclosed XQuery comment.')\n";

        assertThat(prettyPrintASTNode(parseText("(: Test")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment_UnexpectedCommentEndTag() {
        final String expected
                = "FileElement[FILE(0:2)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: End of comment marker found without a '(:' start of comment marker.')\n"
                + "      LeafPsiElement[XQUERY_COMMENT_END_TAG_TOKEN(0:2)](':)')\n";

        assertThat(prettyPrintASTNode(parseText(":)")), is(expected));
    }

    // endregion
    // region CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef() {
        final String expected
                = "FileElement[FILE(0:8)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:8)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      XQueryCharRefImpl[XQUERY_CHARACTER_REFERENCE_TOKEN(1:7)]('&#xA0;')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(7:8)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#xA0;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef_IncompleteRef() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      LeafPsiElement[XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN(1:3)]('&#')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(3:3)]('XPST0003: Invalid entity reference character.')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyNumericRef() {
        final String expected
                = "FileElement[FILE(0:5)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:5)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(1:4)]('XPST0003: Entity references must not be empty.')\n"
                + "         LeafPsiElement[XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN(1:4)]('&#;')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(4:5)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyHexidecimalRef() {
        final String expected
                = "FileElement[FILE(0:6)]\n"
                + "   XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:6)]\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(1:5)]('XPST0003: Entity references must not be empty.')\n"
                + "         LeafPsiElement[XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN(1:5)]('&#x;')\n"
                + "      LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(5:6)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#x;\"")), is(expected));
    }

    // endregion
    // region QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryQNameImpl[XQUERY_QNAME(0:7)]\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(3:4)](':')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(4:7)]('two')\n";

        assertThat(prettyPrintASTNode(parseText("one:two")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeColon() {
        final String expected
                = "FileElement[FILE(0:8)]\n"
                + "   XQueryQNameImpl[XQUERY_QNAME(0:8)]\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(3:4)]('XPST0003: Whitespace is not allowed before ':' in a qualified name.')\n"
                + "         PsiWhiteSpaceImpl[WHITE_SPACE(3:4)](' ')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(4:5)](':')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(5:8)]('two')\n";

        assertThat(prettyPrintASTNode(parseText("one :two")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceAfterColon() {
        final String expected
                = "FileElement[FILE(0:8)]\n"
                + "   XQueryQNameImpl[XQUERY_QNAME(0:8)]\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(3:4)](':')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(4:5)]('XPST0003: Whitespace is not allowed after ':' in a qualified name.')\n"
                + "         PsiWhiteSpaceImpl[WHITE_SPACE(4:5)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(5:8)]('two')\n";

        assertThat(prettyPrintASTNode(parseText("one: two")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_SpaceBeforeAndAfterColon() {
        final String expected
                = "FileElement[FILE(0:9)]\n"
                + "   XQueryQNameImpl[XQUERY_QNAME(0:9)]\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(3:4)]('XPST0003: Whitespace is not allowed before ':' in a qualified name.')\n"
                + "         PsiWhiteSpaceImpl[WHITE_SPACE(3:4)](' ')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(4:5)](':')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(5:6)]('XPST0003: Whitespace is not allowed after ':' in a qualified name.')\n"
                + "         PsiWhiteSpaceImpl[WHITE_SPACE(5:6)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(6:9)]('two')\n";

        assertThat(prettyPrintASTNode(parseText("one : two")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_NonNCNameLocalPart() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(3:4)](':')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(4:7)]('XPST0003: Missing local name after ':' in qualified name.')\n"
                + "      XQueryNumericLiteralImpl[XQUERY_INTEGER_LITERAL_TOKEN(4:7)]('234')\n";

        assertThat(prettyPrintASTNode(parseText("one:234")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingLocalPart() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:3)]('one')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(3:4)](':')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(4:4)]('XPST0003: Missing local name after ':' in qualified name.')\n";

        assertThat(prettyPrintASTNode(parseText("one:")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixPart() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:4)]('XPST0003: Missing prefix before ':' in qualified name.')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(0:1)](':')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(1:4)]('two')\n";

        assertThat(prettyPrintASTNode(parseText(":two")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName_MissingPrefixAndLocalPart() {
        final String expected
                = "FileElement[FILE(0:1)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Missing prefix before ':' in qualified name.')\n"
                + "      LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(0:1)](':')\n";

        assertThat(prettyPrintASTNode(parseText(":")), is(expected));
    }

    // endregion
    // region NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(0:4)]('test')\n";

        assertThat(prettyPrintASTNode(parseText("test")), is(expected));
    }

    // endregion
    // region S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   PsiWhiteSpaceImpl[WHITE_SPACE(0:4)](' \t\r\n')\n";

        assertThat(prettyPrintASTNode(parseText(" \t\r\n")), is(expected));
    }

    // endregion

    // endregion
    // region A.1 EBNF

    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl() {
        final String expected
                = "FileElement[FILE(0:21)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:21)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(14:15)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:20)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:19)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(19:20)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(20:21)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        final String expected
                = "FileElement[FILE(0:39)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:39)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(14:15)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:20)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:19)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(19:20)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(20:21)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(21:29)]('encoding')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(29:30)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(30:38)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(30:31)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(31:37)]('latin1')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(37:38)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(38:39)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\" encoding \"latin1\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_NoVersionKeyword() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:6)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Missing keyword 'version'.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(6:7)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_NoVersionString() {
        final String expected
                = "FileElement[FILE(0:15)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:14)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(14:14)]('XPST0003: Missing version string.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(14:15)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_MissingSemicolon() {
        final String expected
                = "FileElement[FILE(0:21)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:20)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(14:15)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:20)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:19)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(19:20)]('\"')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(20:20)]('XPST0003: Missing semicolon.')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(20:21)](':')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\":")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    public void testVersionDecl_MissingEncodingString() {
        final String expected
                = "FileElement[FILE(0:30)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:29)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(14:15)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:20)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:19)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(19:20)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(20:21)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(21:29)]('encoding')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(29:29)]('XPST0003: Missing encoding string.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(29:30)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\" encoding;")), is(expected));
    }

    // endregion
    // region ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl() {
        final String expected
                = "FileElement[FILE(0:54)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:54)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:21)]('test')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(21:22)](' ')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(22:23)]('=')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(24:53)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(24:25)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(25:52)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(52:53)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(53:54)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace test = \"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceKeyword() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:6)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Missing keyword 'namespace'.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(6:7)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceName() {
        final String expected
                = "FileElement[FILE(0:17)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:16)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(16:16)]('XPST0003: Missing identifier.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(16:17)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoEqualsAfterName() {
        final String expected
                = "FileElement[FILE(0:21)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:20)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:20)]('one')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(20:20)]('XPST0003: Expected '='.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(20:21)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace one;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceUri() {
        final String expected
                = "FileElement[FILE(0:23)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:22)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:20)]('one')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(20:21)](' ')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(21:22)]('=')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(22:22)]('XPST0003: Missing URI string.')\n"
                + "   LeafPsiElement[XQUERY_SEPARATOR_TOKEN(22:23)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace one =;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_MissingSemicolon() {
        final String expected
                = "FileElement[FILE(0:54)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:53)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:21)]('test')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(21:22)](' ')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(22:23)]('=')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(23:24)](' ')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(24:53)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(24:25)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(25:52)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(52:53)]('\"')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(53:53)]('XPST0003: Missing semicolon.')\n"
                + "   LeafPsiElement[XQUERY_QNAME_SEPARATOR_TOKEN(53:54)](':')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace test = \"http://www.example.com/test\":")), is(expected));
    }

    // endregion
    // region DirCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor() {
        final String expected
                = "FileElement[FILE(0:13)]\n"
                + "   XQueryDirCommentConstructorImpl[XQUERY_DIR_COMMENT_CONSTRUCTOR(0:13)]\n"
                + "      LeafPsiElement[XQUERY_XML_COMMENT_START_TAG_TOKEN(0:4)]('<!--')\n"
                + "      PsiCommentImpl[XQUERY_XML_COMMENT_TOKEN(4:10)](' Test ')\n"
                + "      LeafPsiElement[XQUERY_XML_COMMENT_END_TAG_TOKEN(10:13)]('-->')\n";

        assertThat(prettyPrintASTNode(parseText("<!-- Test -->")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnclosedComment() {
        final String expected
                = "FileElement[FILE(0:10)]\n"
                + "   XQueryDirCommentConstructorImpl[XQUERY_DIR_COMMENT_CONSTRUCTOR(0:10)]\n"
                + "      LeafPsiElement[XQUERY_XML_COMMENT_START_TAG_TOKEN(0:4)]('<!--')\n"
                + "      PsiCommentImpl[XQUERY_XML_COMMENT_TOKEN(4:10)](' Test ')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(10:10)]('XPST0003: Unclosed XML comment.')\n";

        assertThat(prettyPrintASTNode(parseText("<!-- Test ")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor_UnexpectedCommentEndTag() {
        final String expected
                = "FileElement[FILE(0:3)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:3)]('XPST0003: End of comment marker found without a '<!--' start of comment marker.')\n"
                + "      LeafPsiElement[XQUERY_XML_COMMENT_END_TAG_TOKEN(0:3)]('-->')\n";

        assertThat(prettyPrintASTNode(parseText("-->")), is(expected));
    }

    // endregion
    // region CDataSection

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection() {
        final String expected
                = "FileElement[FILE(0:18)]\n"
                + "   XQueryCDataSectionImpl[XQUERY_CDATA_SECTION(0:18)]\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_START_TAG_TOKEN(0:9)]('<![CDATA[')\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_TOKEN(9:15)](' Test ')\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_END_TAG_TOKEN(15:18)](']]>')\n";

        assertThat(prettyPrintASTNode(parseText("<![CDATA[ Test ]]>")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_Unclosed() {
        final String expected
                = "FileElement[FILE(0:15)]\n"
                + "   XQueryCDataSectionImpl[XQUERY_CDATA_SECTION(0:15)]\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_START_TAG_TOKEN(0:9)]('<![CDATA[')\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_TOKEN(9:15)](' Test ')\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(15:15)]('XPST0003: Unclosed CDATA section.')\n";

        assertThat(prettyPrintASTNode(parseText("<![CDATA[ Test ")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection_UnexpectedEndTag() {
        final String expected
                = "FileElement[FILE(0:3)]\n"
                + "   PsiErrorElementImpl[ERROR_ELEMENT(0:3)]('XPST0003: End of CDATA section marker found without a '<![CDATA[' start of CDATA section marker.')\n"
                + "      LeafPsiElement[XQUERY_CDATA_SECTION_END_TAG_TOKEN(0:3)](']]>')\n";

        assertThat(prettyPrintASTNode(parseText("]]>")), is(expected));
    }

    // endregion

    // endregion
}
