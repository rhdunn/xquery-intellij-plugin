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
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:4)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:4)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:4)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:4)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:4)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:4)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:4)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:4)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:4)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:4)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:4)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:4)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "                                          XQueryEscapeCharacterImpl[XQUERY_STRING_LITERAL_ESCAPED_CHARACTER_TOKEN(1:3)]('\"\"')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"\"\"\"")), is(expected));
    }

    // endregion
    // region EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:4)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:4)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:4)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:4)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:4)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:4)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:4)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:4)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:4)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:4)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:4)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:4)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)](''')\n"
                + "                                          XQueryEscapeCharacterImpl[XQUERY_STRING_LITERAL_ESCAPED_CHARACTER_TOKEN(1:3)]('''')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)](''')\n";

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
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:8)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:8)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:8)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:8)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:8)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:8)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:8)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:8)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:8)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:8)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:8)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:8)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:8)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "                                          XQueryCharRefImpl[XQUERY_CHARACTER_REFERENCE_TOKEN(1:7)]('&#xA0;')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(7:8)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#xA0;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    public void testStringLiteral_CharRef_IncompleteRef() {
        final String expected
                = "FileElement[FILE(0:4)]\n"
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:4)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:4)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:4)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:4)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:4)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:4)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:4)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:4)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:4)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:4)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:4)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:4)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:4)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "                                          LeafPsiElement[XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN(1:3)]('&#')\n"
                + "                                          PsiErrorElementImpl[ERROR_ELEMENT(3:3)]('XPST0003: Invalid entity reference character.')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(3:4)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyNumericRef() {
        final String expected
                = "FileElement[FILE(0:5)]\n"
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:5)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:5)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:5)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:5)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:5)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:5)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:5)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:5)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:5)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:5)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:5)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:5)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:5)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "                                          PsiErrorElementImpl[ERROR_ELEMENT(1:4)]('XPST0003: Entity references must not be empty.')\n"
                + "                                             LeafPsiElement[XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN(1:4)]('&#;')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(4:5)]('\"')\n";

        assertThat(prettyPrintASTNode(parseText("\"&#;\"")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_CharRef_EmptyHexidecimalRef() {
        final String expected
                = "FileElement[FILE(0:6)]\n"
                + "   XQueryMultiplicativeExprImpl[XQUERY_MULTIPLICATIVE_EXPR(0:6)]\n"
                + "      XQueryUnionExprImpl[XQUERY_UNION_EXPR(0:6)]\n"
                + "         XQueryIntersectExceptExprImpl[XQUERY_INTERSECT_EXCEPT_EXPR(0:6)]\n"
                + "            XQueryInstanceofExprImpl[XQUERY_INSTANCEOF_EXPR(0:6)]\n"
                + "               XQueryTreatExprImpl[XQUERY_TREAT_EXPR(0:6)]\n"
                + "                  XQueryCastableExprImpl[XQUERY_CASTABLE_EXPR(0:6)]\n"
                + "                     XQueryCastExprImpl[XQUERY_CAST_EXPR(0:6)]\n"
                + "                        XQueryUnaryExprImpl[XQUERY_UNARY_EXPR(0:6)]\n"
                + "                           XQueryPathExprImpl[XQUERY_PATH_EXPR(0:6)]\n"
                + "                              XQueryRelativePathExprImpl[XQUERY_RELATIVE_PATH_EXPR(0:6)]\n"
                + "                                 XQueryFilterExprImpl[XQUERY_FILTER_EXPR(0:6)]\n"
                + "                                    XQueryLiteralImpl[XQUERY_LITERAL(0:6)]\n"
                + "                                       XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(0:6)]\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(0:1)]('\"')\n"
                + "                                          PsiErrorElementImpl[ERROR_ELEMENT(1:5)]('XPST0003: Entity references must not be empty.')\n"
                + "                                             LeafPsiElement[XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN(1:5)]('&#x;')\n"
                + "                                          LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(5:6)]('\"')\n";

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
                + "      XQueryIntegerLiteralImpl[XQUERY_INTEGER_LITERAL_TOKEN(4:7)]('234')\n";

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
    // region A.1 EBNF (XQuery 1.0)

    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl() {
        final String expected
                = "FileElement[FILE(0:22)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:22)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(14:15)](' ')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(15:20)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(15:16)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(16:19)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(19:20)]('\"')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(20:21)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(21:22)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:20)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:20)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(14:19)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:18)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(18:19)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(19:20)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version\"1.0\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding() {
        final String expected
                = "FileElement[FILE(0:40)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:40)]\n"
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
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(38:39)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(39:40)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\" encoding \"latin1\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_WithEncoding_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:36)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:36)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(14:19)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(14:15)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(15:18)]('1.0')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(18:19)]('\"')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(19:27)]('encoding')\n"
                + "      XQueryStringLiteralImpl[XQUERY_STRING_LITERAL(27:35)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(27:28)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(28:34)]('latin1')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(34:35)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(35:36)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version\"1.0\"encoding\"latin1\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_NoVersionKeyword() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:7)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Missing keyword 'version'.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(6:7)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_NoVersionString() {
        final String expected
                = "FileElement[FILE(0:15)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:15)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_VERSION(7:14)]('version')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(14:14)]('XPST0003: Missing version string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(14:15)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-VersionDecl")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
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
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_MissingEncodingString() {
        final String expected
                = "FileElement[FILE(0:30)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:30)]\n"
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
                + "      PsiErrorElementImpl[ERROR_ELEMENT(29:29)]('XPST0003: Missing encoding string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(29:30)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery version \"1.0\" encoding;")), is(expected));
    }

    // endregion
    // region ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl() {
        final String expected
                = "FileElement[FILE(0:55)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:55)]\n"
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
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(53:54)](' ')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(54:55)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace test = \"http://www.example.com/test\" ;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_CompactWhitespace() {
        final String expected
                = "FileElement[FILE(0:52)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:52)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:21)]('test')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(21:22)]('=')\n"
                + "      XQueryUriLiteralImpl[XQUERY_URI_LITERAL(22:51)]\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_START_TOKEN(22:23)]('\"')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_CONTENTS_TOKEN(23:50)]('http://www.example.com/test')\n"
                + "         LeafPsiElement[XQUERY_STRING_LITERAL_END_TOKEN(50:51)]('\"')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(51:52)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace test=\"http://www.example.com/test\";")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceKeyword() {
        final String expected
                = "FileElement[FILE(0:7)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:7)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(6:6)]('XPST0003: Missing keyword 'namespace'.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(6:7)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceName() {
        final String expected
                = "FileElement[FILE(0:17)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:17)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(16:16)]('XPST0003: Missing identifier.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(16:17)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoEqualsAfterName() {
        final String expected
                = "FileElement[FILE(0:21)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:21)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:20)]('one')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(20:20)]('XPST0003: Expected '='.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(20:21)](';')\n";

        assertThat(prettyPrintASTNode(parseText("module namespace one;")), is(expected));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleDecl")
    public void testModuleDecl_NoNamespaceUri() {
        final String expected
                = "FileElement[FILE(0:23)]\n"
                + "   XQueryModuleDeclImpl[XQUERY_MODULE_DECL(0:23)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_MODULE(0:6)]('module')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_NAMESPACE(7:16)]('namespace')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(16:17)](' ')\n"
                + "      XQueryNCNameImpl[XQUERY_NCNAME_TOKEN(17:20)]('one')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(20:21)](' ')\n"
                + "      LeafPsiElement[XQUERY_EQUAL_TOKEN(21:22)]('=')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(22:22)]('XPST0003: Missing URI string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(22:23)](';')\n";

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
    // region A.1 EBNF (XQuery 3.0)

    // region VersionDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        final String expected
                = "FileElement[FILE(0:26)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:26)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(7:15)]('XPST0003: Encoding-only xquery declarations require XQuery 3.0 or later.')\n"
                + "         LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
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
                + "      PsiErrorElementImpl[ERROR_ELEMENT(7:15)]('XPST0003: Encoding-only xquery declarations require XQuery 3.0 or later.')\n"
                + "         LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
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
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:16)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(7:15)]('XPST0003: Encoding-only xquery declarations require XQuery 3.0 or later.')\n"
                + "         LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(15:15)]('XPST0003: Missing encoding string.')\n"
                + "      LeafPsiElement[XQUERY_SEPARATOR_TOKEN(15:16)](';')\n";

        assertThat(prettyPrintASTNode(parseText("xquery encoding;")), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingSemicolon() {
        final String expected
                = "FileElement[FILE(0:25)]\n"
                + "   XQueryVersionDeclImpl[XQUERY_VERSION_DECL(0:24)]\n"
                + "      LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_XQUERY(0:6)]('xquery')\n"
                + "      PsiWhiteSpaceImpl[WHITE_SPACE(6:7)](' ')\n"
                + "      PsiErrorElementImpl[ERROR_ELEMENT(7:15)]('XPST0003: Encoding-only xquery declarations require XQuery 3.0 or later.')\n"
                + "         LeafPsiElement[XQUERY_KEYWORD_OR_NCNAME_ENCODING(7:15)]('encoding')\n"
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
