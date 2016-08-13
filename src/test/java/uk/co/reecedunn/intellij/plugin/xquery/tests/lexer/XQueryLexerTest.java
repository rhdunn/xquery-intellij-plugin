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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XQueryLexerTest extends TestCase {
    // region Lexer Test Helpers

    private void matchToken(Lexer lexer, String text, int state, int start, int end, IElementType type) {
        assertThat(lexer.getTokenText(), is(text));
        assertThat(lexer.getState(), is(state));
        assertThat(lexer.getTokenStart(), is(start));
        assertThat(lexer.getTokenEnd(), is(end));
        assertThat(lexer.getTokenType(), is(type));

        if (lexer.getTokenType() == null) {
            assertThat(lexer.getBufferEnd(), is(start));
            assertThat(lexer.getBufferEnd(), is(end));
        }

        lexer.advance();
    }

    private void matchSingleToken(Lexer lexer, String text, int state, IElementType type) {
        final int length = text.length();
        lexer.start(text);
        matchToken(lexer, text, 0,     0,      length, type);
        matchToken(lexer, "",   state, length, length, null);
    }

    private void matchSingleToken(Lexer lexer, String text, IElementType type) {
        matchSingleToken(lexer, text, 0, type);
    }

    // endregion
    // region Basic Lexer Tests

    public void testEmptyBuffer() {
        Lexer lexer = new XQueryLexer();

        lexer.start("");
        matchToken(lexer, "", 0, 0, 0, null);
    }

    public void testBadCharacters() {
        Lexer lexer = new XQueryLexer();

        lexer.start("~\uFFFE\u0000\uFFFF");
        matchToken(lexer, "~",      0, 0, 1, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\uFFFE", 0, 1, 2, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\u0000", 0, 2, 3, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\uFFFF", 0, 3, 4, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",       0, 4, 4, null);
    }

    // endregion
    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VersionDecl")
    public void testVersionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "xquery",   XQueryTokenType.K_XQUERY);
        matchSingleToken(lexer, "version",  XQueryTokenType.K_VERSION);
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING);
    }

    // endregion
    // region ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ModuleDecl")
    public void testModuleDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "module",    XQueryTokenType.K_MODULE);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region Separator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Separator")
    public void testSeparator() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR);
    }

    // endregion
    // region NamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-NamespaceDecl")
    public void testNamespaceDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region BoundarySpaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",        XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "boundary-space", XQueryTokenType.K_BOUNDARY_SPACE);
        matchSingleToken(lexer, "preserve",       XQueryTokenType.K_PRESERVE);
        matchSingleToken(lexer, "strip",          XQueryTokenType.K_STRIP);
    }

    // endregion
    // region DefaultNamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DefaultNamespaceDecl")
    public void testDefaultNamespaceDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "default",   XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "element",   XQueryTokenType.K_ELEMENT);
        matchSingleToken(lexer, "function",  XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region OptionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OptionDecl")
    public void testOptionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "option",  XQueryTokenType.K_OPTION);
    }

    // endregion
    // region OrderingModeDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderingModeDecl")
    public void testOrderingModeDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "ordering",  XQueryTokenType.K_ORDERING);
        matchSingleToken(lexer, "ordered",   XQueryTokenType.K_ORDERED);
        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED);
    }

    // endregion
    // region EmptyOrderDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-EmptyOrderDecl")
    public void testEmptyOrderDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "default",  XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "order",    XQueryTokenType.K_ORDER);
        matchSingleToken(lexer, "empty",    XQueryTokenType.K_EMPTY);
        matchSingleToken(lexer, "greatest", XQueryTokenType.K_GREATEST);
        matchSingleToken(lexer, "least",    XQueryTokenType.K_LEAST);
    }

    // endregion
    // region CopyNamespacesDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CopyNamespacesDecl")
    public void testCopyNamespacesDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",         XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES);
        matchSingleToken(lexer, ",",               XQueryTokenType.COMMA);
    }

    // endregion
    // region PreserveMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PreserveMode")
    public void testPreserveMode() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "preserve",    XQueryTokenType.K_PRESERVE);
        matchSingleToken(lexer, "no-preserve", XQueryTokenType.K_NO_PRESERVE);
    }

    // endregion
    // region InheritMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-InheritMode")
    public void testInheritMode() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "inherit",    XQueryTokenType.K_INHERIT);
        matchSingleToken(lexer, "no-inherit", XQueryTokenType.K_NO_INHERIT);
    }

    // endregion
    // region DefaultCollationDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "default",   XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION);
    }

    // endregion
    // region BaseURIDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-BaseURIDecl")
    public void testBaseURIDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "base-uri", XQueryTokenType.K_BASE_URI);
    }

    // endregion
    // region SchemaImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaImport")
    public void testSchemaImport() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT);
        matchSingleToken(lexer, "schema", XQueryTokenType.K_SCHEMA);
        matchSingleToken(lexer, "at",     XQueryTokenType.K_AT);
        matchSingleToken(lexer, ",",      XQueryTokenType.COMMA);
    }

    // endregion
    // region SchemaPrefix

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaPrefix")
    public void testSchemaPrefix() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);

        matchSingleToken(lexer, "default",   XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "element",   XQueryTokenType.K_ELEMENT);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
    }

    // endregion
    // region ModuleImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ModuleImport")
    public void testModuleImport() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "import",    XQueryTokenType.K_IMPORT);
        matchSingleToken(lexer, "module",    XQueryTokenType.K_MODULE);
        matchSingleToken(lexer, "at",        XQueryTokenType.K_AT);
        matchSingleToken(lexer, ",",         XQueryTokenType.COMMA);

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region VarDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VarDecl")
    public void testVarDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "variable", XQueryTokenType.K_VARIABLE);
        matchSingleToken(lexer, "$",        XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",       XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL);
    }

    // endregion
    // region ConstructionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ConstructionDecl")
    public void testConstructionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",      XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "construction", XQueryTokenType.K_CONSTRUCTION);
        matchSingleToken(lexer, "strip",        XQueryTokenType.K_STRIP);
        matchSingleToken(lexer, "preserve",     XQueryTokenType.K_PRESERVE);
    }

    // endregion
    // region FunctionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-FunctionDecl")
    public void testFunctionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL);
    }

    // endregion
    // region ParamList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ParamList")
    public void testParamList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region Param

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Param")
    public void testParam() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-EnclosedExpr")
    public void testEnclosedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region Expr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Expr")
    public void testExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region FLWORExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-FLWORExpr")
    public void testFLWORExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ForClause")
    public void testForClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "for", XQueryTokenType.K_FOR);
        matchSingleToken(lexer, "$",   XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "in",  XQueryTokenType.K_IN);
        matchSingleToken(lexer, ",",   XQueryTokenType.COMMA);
    }

    // endregion
    // region PositionalVar

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PositionalVar")
    public void testPositionalVar() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT);
        matchSingleToken(lexer, "$",  XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region LetClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-LetClause")
    public void testLetClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "let", XQueryTokenType.K_LET);
        matchSingleToken(lexer, "$",   XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",  XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, ",",   XQueryTokenType.COMMA);
    }

    // endregion
    // region WhereClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-WhereClause")
    public void testWhereClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "where", XQueryTokenType.K_WHERE);
    }

    // endregion
    // region OrderByClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderByClause")
    public void testOrderByClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "stable", XQueryTokenType.K_STABLE);
        matchSingleToken(lexer, "order",  XQueryTokenType.K_ORDER);
        matchSingleToken(lexer, "by",     XQueryTokenType.K_BY);
    }

    // endregion
    // region OrderSpecList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderSpecList")
    public void testOrderSpecList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region OrderModifier

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderModifier")
    public void testOrderModifier() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "ascending",  XQueryTokenType.K_ASCENDING);
        matchSingleToken(lexer, "descending", XQueryTokenType.K_DESCENDING);

        matchSingleToken(lexer, "empty",    XQueryTokenType.K_EMPTY);
        matchSingleToken(lexer, "greatest", XQueryTokenType.K_GREATEST);
        matchSingleToken(lexer, "least",    XQueryTokenType.K_LEAST);

        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION);
    }

    // endregion
    // region QuantifiedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-QuantifiedExpr")
    public void testQuantifiedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "some",      XQueryTokenType.K_SOME);
        matchSingleToken(lexer, "every",     XQueryTokenType.K_EVERY);
        matchSingleToken(lexer, "$",         XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "in",        XQueryTokenType.K_IN);
        matchSingleToken(lexer, ",",         XQueryTokenType.COMMA);
        matchSingleToken(lexer, "satisfies", XQueryTokenType.K_SATISFIES);
    }

    // endregion
    // region TypeswitchExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TypeswitchExpr")
    public void testTypeswitchExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "typeswitch", XQueryTokenType.K_TYPESWITCH);
        matchSingleToken(lexer, "(",          XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",          XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "default",    XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "$",          XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "return",     XQueryTokenType.K_RETURN);
    }

    // endregion
    // region CaseClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CaseClause")
    public void testCaseClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "case",   XQueryTokenType.K_CASE);
        matchSingleToken(lexer, "$",      XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "as",     XQueryTokenType.K_AS);
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region IfExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-IfExpr")
    public void testIfExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "if",   XQueryTokenType.K_IF);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "then", XQueryTokenType.K_THEN);
        matchSingleToken(lexer, "else", XQueryTokenType.K_ELSE);
    }

    // endregion
    // region OrExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrExpr")
    public void testOrExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "or", XQueryTokenType.K_OR);
    }

    // endregion
    // region AndExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AndExpr")
    public void testAndExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "and", XQueryTokenType.K_AND);
    }

    // endregion
    // region RangeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-RangeExpr")
    public void testRangeExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "to", XQueryTokenType.K_TO);
    }

    // endregion
    // region AdditiveExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AdditiveExpr")
    public void testAdditiveExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS);
    }

    // endregion
    // region MultiplicativeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*",    XQueryTokenType.STAR);
        matchSingleToken(lexer, "div",  XQueryTokenType.K_DIV);
        matchSingleToken(lexer, "idiv", XQueryTokenType.K_IDIV);
        matchSingleToken(lexer, "mod",  XQueryTokenType.K_MOD);
    }

    // endregion
    // region UnionExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnionExpr")
    public void testUnionExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "union", XQueryTokenType.K_UNION);
        matchSingleToken(lexer, "|",     XQueryTokenType.UNION);
    }

    // endregion
    // region IntersectExceptExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "intersect", XQueryTokenType.K_INTERSECT);
        matchSingleToken(lexer, "except",    XQueryTokenType.K_EXCEPT);
    }

    // endregion
    // region InstanceofExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-InstanceofExpr")
    public void testInstanceofExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "instance", XQueryTokenType.K_INSTANCE);
        matchSingleToken(lexer, "of",       XQueryTokenType.K_OF);
    }

    // endregion
    // region TreatExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TreatExpr")
    public void testTreatExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "treat", XQueryTokenType.K_TREAT);
        matchSingleToken(lexer, "as",    XQueryTokenType.K_AS);
    }

    // endregion
    // region CastableExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CastableExpr")
    public void testCastableExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "castable", XQueryTokenType.K_CASTABLE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
    }

    // endregion
    // region CastExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CastExpr")
    public void testCastExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "cast", XQueryTokenType.K_CAST);
        matchSingleToken(lexer, "as",   XQueryTokenType.K_AS);
    }

    // endregion
    // region UnaryExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnaryExpr")
    public void testUnaryExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS);
    }

    // endregion
    // region GeneralComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-GeneralComp")
    public void testGeneralComp() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "=",  XQueryTokenType.EQUAL);
        matchSingleToken(lexer, "!=", XQueryTokenType.NOT_EQUAL);
        matchSingleToken(lexer, "<",  XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, "<=", XQueryTokenType.LESS_THAN_OR_EQUAL);
        matchSingleToken(lexer, ">",  XQueryTokenType.GREATER_THAN);
        matchSingleToken(lexer, ">=", XQueryTokenType.GREATER_THAN_OR_EQUAL);
    }

    // endregion
    // region VarRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VarRef")
    public void testVarRef() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region ParenthesizedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region ContextItemExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ContextItemExpr")
    public void testContextItemExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ".", XQueryTokenType.DOT);
    }

    // endregion
    // region OrderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderedExpr")
    public void testOrderedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "ordered", XQueryTokenType.K_ORDERED);
        matchSingleToken(lexer, "{",       XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",       XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region UnorderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnorderedExpr")
    public void testUnorderedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED);
        matchSingleToken(lexer, "{",         XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",         XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region FunctionCall

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-FunctionCall")
    public void testFunctionCall() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region DirCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<",    0, XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, "<!",   0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!-",  0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!--", 5, XQueryTokenType.XML_COMMENT_START_TAG);

        matchSingleToken(lexer, "--",  XQueryTokenType.INVALID);
        matchSingleToken(lexer, "-->", XQueryTokenType.XML_COMMENT_END_TAG);

        lexer.start("<!-- Test");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test", 5, 4, 9, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 9, 9, null);

        lexer.start("<!-- Test --");
        matchToken(lexer, "<!--",     0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test --", 5,  4, 12, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",         6, 12, 12, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",         0, 12, 12, null);

        lexer.start("<!-- Test -->");
        matchToken(lexer, "<!--",   0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test ", 5,  4, 10, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",    5, 10, 13, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, "",       0, 13, 13, null);

        lexer.start("<!--\nMultiline\nComment\n-->");
        matchToken(lexer, "<!--",                   0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "\nMultiline\nComment\n", 5,  4, 23, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",                    5, 23, 26, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, "",                       0, 26, 26, null);

        lexer.start("<!---");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "-",     5, 4, 5, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 5, 5, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 5, 5, null);

        lexer.start("<!----");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "--",    5, 4, 6, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 6, 6, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 6, 6, null);
    }

    // endregion
    // region CDataSection

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    public void testCDataSection() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<",         0, XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, "<!",        0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![",       0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![C",      0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![CD",     0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![CDA",    0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![CDAT",   0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![CDATA",  0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<![CDATA[", 7, XQueryTokenType.CDATA_SECTION_START_TAG);

        matchSingleToken(lexer, "]",   XQueryTokenType.PREDICATE_END);

        lexer.start("]]");
        matchToken(lexer, "]", 0, 0, 1, XQueryTokenType.PREDICATE_END);
        matchToken(lexer, "]", 0, 1, 2, XQueryTokenType.PREDICATE_END);
        matchToken(lexer, "",  0, 2, 2, null);

        matchSingleToken(lexer, "]]>", XQueryTokenType.CDATA_SECTION_END_TAG);

        lexer.start("<![CDATA[ Test");
        matchToken(lexer, "<![CDATA[", 0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, " Test",     7,  9, 14, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "",          6, 14, 14, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",          0, 14, 14, null);

        lexer.start("<![CDATA[ Test ]]");
        matchToken(lexer, "<![CDATA[", 0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, " Test ]]",  7,  9, 17, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "",          6, 17, 17, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",          0, 17, 17, null);

        lexer.start("<![CDATA[ Test ]]>");
        matchToken(lexer, "<![CDATA[", 0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, " Test ",    7,  9, 15, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "]]>",       7, 15, 18, XQueryTokenType.CDATA_SECTION_END_TAG);
        matchToken(lexer, "",          0, 18, 18, null);

        lexer.start("<![CDATA[\nMultiline\nComment\n]]>");
        matchToken(lexer, "<![CDATA[",              0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, "\nMultiline\nComment\n", 7,  9, 28, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "]]>",                    7, 28, 31, XQueryTokenType.CDATA_SECTION_END_TAG);
        matchToken(lexer, "",                       0, 31, 31, null);

        lexer.start("<![CDATA[]");
        matchToken(lexer, "<![CDATA[", 0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, "]",         7,  9, 10, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "",          6, 10, 10, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",          0, 10, 10, null);

        lexer.start("<![CDATA[]]");
        matchToken(lexer, "<![CDATA[", 0,  0,  9, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, "]]",        7,  9, 11, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "",          6, 11, 11, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",          0, 11, 11, null);
    }

    // endregion
    // region TypeDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TypeDeclaration")
    public void testTypeDeclaration() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "as", XQueryTokenType.K_AS);
    }

    // endregion
    // region SequenceType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SequenceType")
    public void testSequenceType() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "empty-sequence", XQueryTokenType.K_EMPTY_SEQUENCE);
        matchSingleToken(lexer, "(",              XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",              XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region OccurrenceIndicator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL);
        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
    }

    // endregion
    // region ItemType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ItemType")
    public void testItemType() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "item", XQueryTokenType.K_ITEM);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region AnyKindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AnyKindTest")
    public void testAnyKindTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region DocumentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DocumentTest")
    public void testDocumentTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "document-node", XQueryTokenType.K_DOCUMENT_NODE);
        matchSingleToken(lexer, "(",             XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",             XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region TextTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TextTest")
    public void testTextTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region CommentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CommentTest")
    public void testCommentTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "comment", XQueryTokenType.K_COMMENT);
        matchSingleToken(lexer, "(",       XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",       XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region PITest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PITest")
    public void testPITest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION);
        matchSingleToken(lexer, "(",                      XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",                      XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region AttributeTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AttributeTest")
    public void testAttributeTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE);
        matchSingleToken(lexer, "(",         XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",         XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region AttribNameOrWildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AttribNameOrWildcard")
    public void testAttribNameOrWildcard() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
    }

    // endregion
    // region SchemaAttributeTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaAttributeTest")
    public void testSchemaAttributeTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "schema-attribute", XQueryTokenType.K_SCHEMA_ATTRIBUTE);
        matchSingleToken(lexer, "(",                XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",                XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region ElementTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ElementTest")
    public void testElementTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT);
        matchSingleToken(lexer, "(",       XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",       XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "?",       XQueryTokenType.OPTIONAL);
    }

    // endregion
    // region ElementNameOrWildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ElementNameOrWildcard")
    public void testElementNameOrWildcard() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
    }

    // endregion
    // region SchemaElementTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaElementTest")
    public void testSchemaElementTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "schema-element", XQueryTokenType.K_SCHEMA_ELEMENT);
        matchSingleToken(lexer, "(",              XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",              XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region (A.2.2) Terminal Delimitation

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#id-terminal-delimitation")
    public void testDelimitingTerminalSymbols() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "/", XQueryTokenType.DIRECT_DESCENDANTS_PATH);
        matchSingleToken(lexer, "@", XQueryTokenType.ATTRIBUTE_SELECTOR);
        matchSingleToken(lexer, "[", XQueryTokenType.PREDICATE_BEGIN);
        matchSingleToken(lexer, "]", XQueryTokenType.PREDICATE_END);

        matchSingleToken(lexer, "(#", XQueryTokenType.PRAGMA_BEGIN);
        matchSingleToken(lexer, "#)", XQueryTokenType.PRAGMA_END);
        matchSingleToken(lexer, "::", XQueryTokenType.AXIS_SEPARATOR);
        matchSingleToken(lexer, "..", XQueryTokenType.PARENT_SELECTOR);
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH);
        matchSingleToken(lexer, "</", XQueryTokenType.CLOSE_XML_TAG);
        matchSingleToken(lexer, "/>", XQueryTokenType.SELF_CLOSING_XML_TAG);
        matchSingleToken(lexer, "<<", XQueryTokenType.NODE_BEFORE);
        matchSingleToken(lexer, ">>", XQueryTokenType.NODE_AFTER);
        matchSingleToken(lexer, "<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchSingleToken(lexer, "?>", XQueryTokenType.PROCESSING_INSTRUCTION_END);
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#id-terminal-delimitation")
    public void testDelimitingTerminalSymbols_XQuery30() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "!", XQueryTokenType.MAP_OPERATOR);
        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR);
        matchSingleToken(lexer, "%", XQueryTokenType.ANNOTATION_INDICATOR);
    }

    // endregion
    // region IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("1234");
        matchToken(lexer, "1234", 0, 0, 4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "",     0, 4, 4, null);
    }

    // endregion
    // region DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
    public void testDecimalLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("47.");
        matchToken(lexer, "47.", 0, 0, 3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",    0, 3, 3, null);

        lexer.start("1.234");
        matchToken(lexer, "1.234", 0, 0, 5, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",      0, 5, 5, null);

        lexer.start(".25");
        matchToken(lexer, ".25", 0, 0, 3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",    0, 3, 3, null);

        lexer.start(".1.2");
        matchToken(lexer, ".1", 0, 0, 2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, ".2", 0, 2, 4, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",   0, 4, 4, null);
    }

    // endregion
    // region DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("3e7 3e+7 3e-7");
        matchToken(lexer, "3e7",  0,  0,  3, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",    0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "3e+7", 0,  4,  8, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",    0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "3e-7", 0,  9, 13, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",     0, 13, 13, null);

        lexer.start("43E22 43E+22 43E-22");
        matchToken(lexer, "43E22",  0,  0,  5, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0,  5,  6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "43E+22", 0,  6, 12, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0, 12, 13, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "43E-22", 0, 13, 19, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("2.1e3 2.1e+3 2.1e-3");
        matchToken(lexer, "2.1e3",  0,  0,  5, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0,  5,  6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2.1e+3", 0,  6, 12, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0, 12, 13, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2.1e-3", 0, 13, 19, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("1.7E99 1.7E+99 1.7E-99");
        matchToken(lexer, "1.7E99",  0,  0,  6, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1.7E+99", 0,  7, 14, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1.7E-99", 0, 15, 22, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",        0, 22, 22, null);

        lexer.start(".22e42 .22e+42 .22e-42");
        matchToken(lexer, ".22e42",  0,  0,  6, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".22e+42", 0,  7, 14, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".22e-42", 0, 15, 22, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",        0, 22, 22, null);

        lexer.start(".8E2 .8E+2 .8E-2");
        matchToken(lexer, ".8E2",  0,  0,  4, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",     0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".8E+2", 0,  5, 10, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",     0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".8E-2", 0, 11, 16, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",      0, 16, 16, null);

        lexer.start("1e 1e+ 1e-");
        matchToken(lexer, "1",  0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e",  3,  1,  2, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e+", 3,  4,  6, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e-", 3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 10, 10, null);

        lexer.start("1E 1E+ 1E-");
        matchToken(lexer, "1",  0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E",  3,  1,  2, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E+", 3,  4,  6, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E-", 3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 10, 10, null);

        lexer.start("8.9e 8.9e+ 8.9e-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",   3,  3,  4, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e+",  3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e-",  3, 14, 16, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start("8.9E 8.9E+ 8.9E-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",   3,  3,  4, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E+",  3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E-",  3, 14, 16, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start(".4e .4e+ .4e-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",  3,  2,  3, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e+", 3,  6,  8, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e-", 3, 11, 13, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 13, 13, null);

        lexer.start(".4E .4E+ .4E-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",  3,  2,  3, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E+", 3,  6,  8, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E-", 3, 11, 13, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 13, 13, null);
    }

    // endregion
    // region StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "",   1, 1, 1, null);

        lexer.start("\"Hello World\"");
        matchToken(lexer, "\"",          0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "Hello World", 1,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",          1, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);

        lexer.start("'");
        matchToken(lexer, "'", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "",  2, 1, 1, null);

        lexer.start("'Hello World'");
        matchToken(lexer, "'",           0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "Hello World", 2,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",           2, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);
    }

    // endregion
    // region PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef() {
        Lexer lexer = new XQueryLexer();

        // NOTE: The predefined entity reference names are not validated by the lexer, as some
        // XQuery processors support HTML predefined entities. Shifting the name validation to
        // the parser allows proper validation errors to be generated.

        lexer.start("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"");
        matchToken(lexer, "\"",    0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",   1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&abc;", 1,  4,  9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 1,  9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 1, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 1, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  1, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   1, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   1, 31, 34, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",    1, 34, 35, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",      0, 35, 35, null);

        lexer.start("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'");
        matchToken(lexer, "'",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",   2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&abc;", 2,  4,  9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 2,  9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 2, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 2, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  2, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   2, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   2, 31, 34, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",     2, 34, 35, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",      0, 35, 35, null);

        lexer.start("\"&\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 2, 3, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 3, 3, null);

        lexer.start("\"&abc!\"");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "!",    1, 5, 6, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1, 6, 7, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 7, 7, null);

        lexer.start("\"& \"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  1, 2, 3, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("\"&");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   1, 2, 2, null);

        lexer.start("\"&abc");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     1, 5, 5, null);

        lexer.start("\"&;\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&;", 1, 1, 3, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("&");
        matchToken(lexer, "&",  0, 0, 1, XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING);
        matchToken(lexer, "",   0, 1, 1, null);
    }

    // endregion
    // region EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One\"\"Two\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"\"", 1,  4,  6, XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  1,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        Lexer lexer = new XQueryLexer();

        lexer.start("'One''Two'");
        matchToken(lexer, "'",    0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "''",   2,  4,  6, XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  2,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",    2,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(:", 4, XQueryTokenType.COMMENT_START_TAG);
        matchSingleToken(lexer, ":)", 0, XQueryTokenType.COMMENT_END_TAG);

        lexer.start("(: Test :");
        matchToken(lexer, "(:",      0, 0, 2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Test :", 4, 2, 9, XQueryTokenType.COMMENT);
        matchToken(lexer, "",        6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",        0, 9, 9, null);

        lexer.start("(: Test :)");
        matchToken(lexer, "(:",     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Test ", 4,  2,  8, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",     4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0, 10, 10, null);

        lexer.start("(::Test::)");
        matchToken(lexer, "(:",     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, ":Test:", 4,  2,  8, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",     4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0, 10, 10, null);

        lexer.start("(:\nMultiline\nComment\n:)");
        matchToken(lexer, "(:",                     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, "\nMultiline\nComment\n", 4,  2, 21, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                     4, 21, 23, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                       0, 23, 23, null);

        lexer.start("(: Outer (: Inner :) Outer :)");
        matchToken(lexer, "(:",                        0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Outer (: Inner :) Outer ", 4,  2, 27, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                        4, 27, 29, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                          0, 29, 29, null);

        lexer.start("(: Outer ( : Inner :) Outer :)");
        matchToken(lexer, "(:",                0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Outer ( : Inner ", 4,  2, 19, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                4, 19, 21, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, " ",                 0, 21, 22, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "Outer",             0, 22, 27, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",                 0, 27, 28, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ":)",                0, 28, 30, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                  0, 30, 30, null);
    }

    // endregion
    // region CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testStringLiteral_CharRef() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#20;&#xA0;Two\"");
        matchToken(lexer, "\"",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  1,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xA0;", 1,  9, 15, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    1, 15, 18, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",     1, 18, 19, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("'One&#20;&#xA0;Two'");
        matchToken(lexer, "'",      0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  2,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xA0;", 2,  9, 15, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    2, 15, 18, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",      2, 18, 19, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("\"&#\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("\"&# \"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  1, 3, 4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"", 1, 4, 5, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 5, 5, null);

        lexer.start("\"&#");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   1, 3, 3, null);

        lexer.start("\"&#12");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#12", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     1, 5, 5, null);

        lexer.start("\"&#;&#x;&#x2G;&#x2g;&#xg2;\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#;",  1,  1,  4, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x;", 1,  4,  8, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 1,  8, 12, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   1, 12, 14, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x2", 1, 14, 18, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   1, 18, 20, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x",  1, 20, 23, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g2;",  1, 23, 26, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1, 26, 27, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 27, 27, null);
    }

    // endregion
    // region QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName() {
        Lexer lexer = new XQueryLexer();

        lexer.start("one:two");
        matchToken(lexer, "one", 0, 0, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, ":",   0, 3, 4, XQueryTokenType.QNAME_SEPARATOR);
        matchToken(lexer, "two", 0, 4, 7, XQueryTokenType.NCNAME);
        matchToken(lexer, "",    0, 7, 7, null);
    }

    // endregion
    // region NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        Lexer lexer = new XQueryLexer();

        lexer.start("test x b2b F.G a-b g\u0330d");
        matchToken(lexer, "test",     0,  0,  4, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "x",        0,  5,  6, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "b2b",      0,  7, 10, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "F.G",      0, 11, 14, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "a-b",      0, 15, 18, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 18, 19, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "g\u0330d", 0, 19, 22, XQueryTokenType.NCNAME);
        matchToken(lexer, "",         0, 22, 22, null);
    }

    // endregion
    // region S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
        Lexer lexer = new XQueryLexer();

        lexer.start(" ");
        matchToken(lexer, " ", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",  0, 1, 1, null);

        lexer.start("\t");
        matchToken(lexer, "\t", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("\r");
        matchToken(lexer, "\r", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("\n");
        matchToken(lexer, "\n", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("   \t  \r\n ");
        matchToken(lexer, "   \t  \r\n ", 0, 0, 9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",             0, 9, 9, null);
    }

    // endregion
}
