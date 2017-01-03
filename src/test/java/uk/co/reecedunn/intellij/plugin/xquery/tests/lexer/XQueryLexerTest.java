/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XQueryLexerTest extends LexerTestCase {
    // region Lexer :: Invalid State

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testInvalidState() {
        Lexer lexer = new XQueryLexer();

        AssertionError e = assertThrows(AssertionError.class, () -> lexer.start("123", 0, 3, -1));
        assertThat(e.getMessage(), is("Invalid state: -1"));
    }

    // endregion
    // region Lexer :: Empty Stack In Advance

    public void testEmptyStackInAdvance() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"Hello World\"");
        lexer.advance();
        assertThat(lexer.getState(), is(1));

        lexer.start("} {\"");
        matchToken(lexer, "}",  0, 0, 1, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, " ",  0, 1, 2, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "{",  0, 2, 3, XQueryTokenType.BLOCK_OPEN);
        matchToken(lexer, "\"", 0, 3, 4, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "",   1, 4, 4, null);
    }

    // endregion
    // region Lexer :: Empty Stack In Pop State

    public void testEmptyStackInPopState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("} } ");
        matchToken(lexer, "}", 0, 0, 1, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, " ", 0, 1, 2, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "}", 0, 2, 3, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, " ", 0, 3, 4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",  0, 4, 4, null);
    }

    // endregion
    // region Lexer :: Empty Buffer

    public void testEmptyBuffer() {
        Lexer lexer = new XQueryLexer();

        lexer.start("");
        matchToken(lexer, "", 0, 0, 0, null);
    }

    // endregion
    // region Lexer :: Bad Characters

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
    // region XQuery 1.0 :: VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VersionDecl")
    public void testVersionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "xquery",   XQueryTokenType.K_XQUERY);
        matchSingleToken(lexer, "version",  XQueryTokenType.K_VERSION);
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING);
    }

    // endregion
    // region XQuery 1.0 :: ModuleDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ModuleDecl")
    public void testModuleDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "module",    XQueryTokenType.K_MODULE);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region XQuery 1.0 :: Separator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Separator")
    public void testSeparator() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR);
    }

    // endregion
    // region XQuery 1.0 :: NamespaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-NamespaceDecl")
    public void testNamespaceDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "=",         XQueryTokenType.EQUAL);
    }

    // endregion
    // region XQuery 1.0 :: BoundarySpaceDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-BoundarySpaceDecl")
    public void testBoundarySpaceDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",        XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "boundary-space", XQueryTokenType.K_BOUNDARY_SPACE);
        matchSingleToken(lexer, "preserve",       XQueryTokenType.K_PRESERVE);
        matchSingleToken(lexer, "strip",          XQueryTokenType.K_STRIP);
    }

    // endregion
    // region XQuery 1.0 :: DefaultNamespaceDecl

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
    // region XQuery 1.0 :: OptionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OptionDecl")
    public void testOptionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "option",  XQueryTokenType.K_OPTION);
    }

    // endregion
    // region XQuery 1.0 :: OrderingModeDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderingModeDecl")
    public void testOrderingModeDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "ordering",  XQueryTokenType.K_ORDERING);
        matchSingleToken(lexer, "ordered",   XQueryTokenType.K_ORDERED);
        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED);
    }

    // endregion
    // region XQuery 1.0 :: EmptyOrderDecl

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
    // region XQuery 1.0 :: CopyNamespacesDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CopyNamespacesDecl")
    public void testCopyNamespacesDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",         XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES);
        matchSingleToken(lexer, ",",               XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: PreserveMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PreserveMode")
    public void testPreserveMode() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "preserve",    XQueryTokenType.K_PRESERVE);
        matchSingleToken(lexer, "no-preserve", XQueryTokenType.K_NO_PRESERVE);
    }

    // endregion
    // region XQuery 1.0 :: InheritMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-InheritMode")
    public void testInheritMode() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "inherit",    XQueryTokenType.K_INHERIT);
        matchSingleToken(lexer, "no-inherit", XQueryTokenType.K_NO_INHERIT);
    }

    // endregion
    // region XQuery 1.0 :: DefaultCollationDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DefaultCollationDecl")
    public void testDefaultCollationDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",   XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "default",   XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION);
    }

    // endregion
    // region XQuery 1.0 :: BaseURIDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-BaseURIDecl")
    public void testBaseURIDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "base-uri", XQueryTokenType.K_BASE_URI);
    }

    // endregion
    // region XQuery 1.0 :: SchemaImport

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaImport")
    public void testSchemaImport() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT);
        matchSingleToken(lexer, "schema", XQueryTokenType.K_SCHEMA);
        matchSingleToken(lexer, "at",     XQueryTokenType.K_AT);
        matchSingleToken(lexer, ",",      XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: SchemaPrefix

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
    // region XQuery 1.0 :: ModuleImport

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
    // region XQuery 1.0 :: VarDecl

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
    // region XQuery 1.0 :: ConstructionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ConstructionDecl")
    public void testConstructionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",      XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "construction", XQueryTokenType.K_CONSTRUCTION);
        matchSingleToken(lexer, "strip",        XQueryTokenType.K_STRIP);
        matchSingleToken(lexer, "preserve",     XQueryTokenType.K_PRESERVE);
    }

    // endregion
    // region XQuery 1.0 :: FunctionDecl

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
    // region XQuery 1.0 :: ParamList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ParamList")
    public void testParamList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: Param

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Param")
    public void testParam() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region XQuery 1.0 :: EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-EnclosedExpr")
    public void testEnclosedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: Expr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Expr")
    public void testExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: FLWORExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-FLWORExpr")
    public void testFLWORExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region XQuery 1.0 :: ForClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ForClause")
    public void testForClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "for", XQueryTokenType.K_FOR);
        matchSingleToken(lexer, "$",   XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "in",  XQueryTokenType.K_IN);
        matchSingleToken(lexer, ",",   XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: PositionalVar

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PositionalVar")
    public void testPositionalVar() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT);
        matchSingleToken(lexer, "$",  XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region XQuery 1.0 :: LetClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-LetClause")
    public void testLetClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "let", XQueryTokenType.K_LET);
        matchSingleToken(lexer, "$",   XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",  XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, ",",   XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: WhereClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-WhereClause")
    public void testWhereClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "where", XQueryTokenType.K_WHERE);
    }

    // endregion
    // region XQuery 1.0 :: OrderByClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderByClause")
    public void testOrderByClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "stable", XQueryTokenType.K_STABLE);
        matchSingleToken(lexer, "order",  XQueryTokenType.K_ORDER);
        matchSingleToken(lexer, "by",     XQueryTokenType.K_BY);
    }

    // endregion
    // region XQuery 1.0 :: OrderSpecList

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderSpecList")
    public void testOrderSpecList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 1.0 :: OrderModifier

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
    // region XQuery 1.0 :: QuantifiedExpr

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
    // region XQuery 1.0 :: TypeswitchExpr

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
    // region XQuery 1.0 :: CaseClause

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CaseClause")
    public void testCaseClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "case",   XQueryTokenType.K_CASE);
        matchSingleToken(lexer, "$",      XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "as",     XQueryTokenType.K_AS);
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region XQuery 1.0 :: IfExpr

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
    // region XQuery 1.0 :: OrExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrExpr")
    public void testOrExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "or", XQueryTokenType.K_OR);
    }

    // endregion
    // region XQuery 1.0 :: AndExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AndExpr")
    public void testAndExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "and", XQueryTokenType.K_AND);
    }

    // endregion
    // region XQuery 1.0 :: RangeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-RangeExpr")
    public void testRangeExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "to", XQueryTokenType.K_TO);
    }

    // endregion
    // region XQuery 1.0 :: AdditiveExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AdditiveExpr")
    public void testAdditiveExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS);
    }

    // endregion
    // region XQuery 1.0 :: MultiplicativeExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-MultiplicativeExpr")
    public void testMultiplicativeExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*",    XQueryTokenType.STAR);
        matchSingleToken(lexer, "div",  XQueryTokenType.K_DIV);
        matchSingleToken(lexer, "idiv", XQueryTokenType.K_IDIV);
        matchSingleToken(lexer, "mod",  XQueryTokenType.K_MOD);
    }

    // endregion
    // region XQuery 1.0 :: UnionExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnionExpr")
    public void testUnionExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "union", XQueryTokenType.K_UNION);
        matchSingleToken(lexer, "|",     XQueryTokenType.UNION);
    }

    // endregion
    // region XQuery 1.0 :: IntersectExceptExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-IntersectExceptExpr")
    public void testIntersectExceptExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "intersect", XQueryTokenType.K_INTERSECT);
        matchSingleToken(lexer, "except",    XQueryTokenType.K_EXCEPT);
    }

    // endregion
    // region XQuery 1.0 :: InstanceofExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-InstanceofExpr")
    public void testInstanceofExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "instance", XQueryTokenType.K_INSTANCE);
        matchSingleToken(lexer, "of",       XQueryTokenType.K_OF);
    }

    // endregion
    // region XQuery 1.0 :: TreatExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TreatExpr")
    public void testTreatExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "treat", XQueryTokenType.K_TREAT);
        matchSingleToken(lexer, "as",    XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 1.0 :: CastableExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CastableExpr")
    public void testCastableExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "castable", XQueryTokenType.K_CASTABLE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 1.0 :: CastExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CastExpr")
    public void testCastExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "cast", XQueryTokenType.K_CAST);
        matchSingleToken(lexer, "as",   XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 1.0 :: UnaryExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnaryExpr")
    public void testUnaryExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS);

        lexer.start("++");
        matchToken(lexer, "+", 0, 0, 1, XQueryTokenType.PLUS);
        matchToken(lexer, "+", 0, 1, 2, XQueryTokenType.PLUS);
        matchToken(lexer, "",  0, 2, 2, null);

        lexer.start("--");
        matchToken(lexer, "-", 0, 0, 1, XQueryTokenType.MINUS);
        matchToken(lexer, "-", 0, 1, 2, XQueryTokenType.MINUS);
        matchToken(lexer, "",  0, 2, 2, null);
    }

    // endregion
    // region XQuery 1.0 :: GeneralComp

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
    // region XQuery 1.0 :: ValueComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ValueComp")
    public void testValueComp() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "eq", XQueryTokenType.K_EQ);
        matchSingleToken(lexer, "ne", XQueryTokenType.K_NE);
        matchSingleToken(lexer, "lt", XQueryTokenType.K_LT);
        matchSingleToken(lexer, "le", XQueryTokenType.K_LE);
        matchSingleToken(lexer, "gt", XQueryTokenType.K_GT);
        matchSingleToken(lexer, "ge", XQueryTokenType.K_GE);
    }

    // endregion
    // region XQuery 1.0 :: NodeComp

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-NodeComp")
    public void testNodeComp() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "is", XQueryTokenType.K_IS);
        matchSingleToken(lexer, "<<", XQueryTokenType.NODE_BEFORE);
        matchSingleToken(lexer, ">>", XQueryTokenType.NODE_AFTER);
    }

    // endregion
    // region XQuery 1.0 :: ValidateExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ValidateExpr")
    public void testValidateExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE);
        matchSingleToken(lexer, "{",        XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",        XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: ValidationMode

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ValidationMode")
    public void testValidationMode() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "lax",    XQueryTokenType.K_LAX);
        matchSingleToken(lexer, "strict", XQueryTokenType.K_STRICT);
    }

    // endregion
    // region XQuery 1.0 :: ExtensionExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ExtensionExpr")
    public void testExtensionExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: Pragma + PragmaContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Pragma")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PragmaContents")
    public void testPragma() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(#", 8, XQueryTokenType.PRAGMA_BEGIN);
        matchSingleToken(lexer, "#)", 0, XQueryTokenType.PRAGMA_END);

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR);

        lexer.start("(#  let:for  6^gkgw~*#g#)");
        matchToken(lexer, "(#",          0,  0,  2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "  ",          8,  2,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "let",         8,  4,  7, XQueryTokenType.NCNAME);
        matchToken(lexer, ":",           9,  7,  8, XQueryTokenType.QNAME_SEPARATOR);
        matchToken(lexer, "for",         9,  8, 11, XQueryTokenType.NCNAME);
        matchToken(lexer, "  ",          9, 11, 13, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "6^gkgw~*#g", 10, 13, 23, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "#)",         10, 23, 25, XQueryTokenType.PRAGMA_END);
        matchToken(lexer, "",            0, 25, 25, null);

        lexer.start("(#let ##)");
        matchToken(lexer, "(#",  0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "let", 8, 2, 5, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",   9, 5, 6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "#",  10, 6, 7, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "#)", 10, 7, 9, XQueryTokenType.PRAGMA_END);
        matchToken(lexer, "",    0, 9, 9, null);

        lexer.start("(#let 2");
        matchToken(lexer, "(#",  0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "let", 8, 2, 5, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",   9, 5, 6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2",  10, 6, 7, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "",    6, 7, 7, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",    0, 7, 7, null);

        lexer.start("(#let ");
        matchToken(lexer, "(#",  0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "let", 8, 2, 5, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",   9, 5, 6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   10, 6, 6, null);

        lexer.start("(#let~~~#)");
        matchToken(lexer, "(#",   0,  0,  2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "let",  8,  2,  5, XQueryTokenType.NCNAME);
        matchToken(lexer, "~~~",  9,  5,  8, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "#)",  10,  8, 10, XQueryTokenType.PRAGMA_END);
        matchToken(lexer, "",     0, 10, 10, null);

        lexer.start("(#let~~~");
        matchToken(lexer, "(#",  0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "let", 8, 2, 5, XQueryTokenType.NCNAME);
        matchToken(lexer, "~~~", 9, 5, 8, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "",    6, 8, 8, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",    0, 8, 8, null);

        lexer.start("(#:let 2#)");
        matchToken(lexer, "(#",   0,  0,  2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, ":",    8,  2,  3, XQueryTokenType.QNAME_SEPARATOR);
        matchToken(lexer, "let",  9,  3,  6, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",    9,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2",   10,  7,  8, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "#)",  10,  8, 10, XQueryTokenType.PRAGMA_END);
        matchToken(lexer, "",     0, 10, 10, null);

        lexer.start("(#~~~#)");
        matchToken(lexer, "(#",   0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "~~~",  8, 2, 5, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "#)",  10, 5, 7, XQueryTokenType.PRAGMA_END);
        matchToken(lexer, "",     0, 7, 7, null);

        lexer.start("(#~~~");
        matchToken(lexer, "(#",  0, 0, 2, XQueryTokenType.PRAGMA_BEGIN);
        matchToken(lexer, "~~~", 8, 2, 5, XQueryTokenType.PRAGMA_CONTENTS);
        matchToken(lexer, "",    6, 5, 5, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",    0, 5, 5, null);
    }

    // endregion
    // region XQuery 1.0 :: PathExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PathExpr")
    public void testPathExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "/",  XQueryTokenType.DIRECT_DESCENDANTS_PATH);
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH);
    }

    // endregion
    // region XQuery 1.0 :: RelativePathExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-RelativePathExpr")
    public void testRelativePathExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "/",  XQueryTokenType.DIRECT_DESCENDANTS_PATH);
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH);
    }

    // endregion
    // region XQuery 1.0 :: ForwardAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ForwardAxis")
    public void testForwardAxis() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "child",              XQueryTokenType.K_CHILD);
        matchSingleToken(lexer, "descendant",         XQueryTokenType.K_DESCENDANT);
        matchSingleToken(lexer, "attribute",          XQueryTokenType.K_ATTRIBUTE);
        matchSingleToken(lexer, "self",               XQueryTokenType.K_SELF);
        matchSingleToken(lexer, "descendant-or-self", XQueryTokenType.K_DESCENDANT_OR_SELF);
        matchSingleToken(lexer, "following-sibling",  XQueryTokenType.K_FOLLOWING_SIBLING);
        matchSingleToken(lexer, "following",          XQueryTokenType.K_FOLLOWING);
        matchSingleToken(lexer, "::",                 XQueryTokenType.AXIS_SEPARATOR);
    }

    // endregion
    // region XQuery 1.0 :: AbbrevForwardStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AbbrevForwardStep")
    public void testAbbrevForwardStep() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "@", XQueryTokenType.ATTRIBUTE_SELECTOR);
    }

    // endregion
    // region XQuery 1.0 :: ReverseAxis

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ReverseAxis")
    public void testReverseAxis() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "parent",            XQueryTokenType.K_PARENT);
        matchSingleToken(lexer, "ancestor",          XQueryTokenType.K_ANCESTOR);
        matchSingleToken(lexer, "preceding-sibling", XQueryTokenType.K_PRECEDING_SIBLING);
        matchSingleToken(lexer, "preceding",         XQueryTokenType.K_PRECEDING);
        matchSingleToken(lexer, "ancestor-or-self",  XQueryTokenType.K_ANCESTOR_OR_SELF);
        matchSingleToken(lexer, "::",                XQueryTokenType.AXIS_SEPARATOR);
    }

    // endregion
    // region XQuery 1.0 :: AbbrevReverseStep

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AbbrevReverseStep")
    public void testAbbrevReverseStep() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "..", XQueryTokenType.PARENT_SELECTOR);
    }

    // endregion
    // region XQuery 1.0 :: Wildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Wildcard")
    public void testWildcard() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, ":", XQueryTokenType.QNAME_SEPARATOR);
    }

    // endregion
    // region XQuery 1.0 :: Predicate

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-Predicate")
    public void testPredicate() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "[", XQueryTokenType.SQUARE_OPEN);
        matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: VarRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VarRef")
    public void testVarRef() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region XQuery 1.0 :: ParenthesizedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ParenthesizedExpr")
    public void testParenthesizedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: ContextItemExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ContextItemExpr")
    public void testContextItemExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ".", XQueryTokenType.DOT);
    }

    // endregion
    // region XQuery 1.0 :: OrderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OrderedExpr")
    public void testOrderedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "ordered", XQueryTokenType.K_ORDERED);
        matchSingleToken(lexer, "{",       XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",       XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: UnorderedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-UnorderedExpr")
    public void testUnorderedExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED);
        matchSingleToken(lexer, "{",         XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",         XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: FunctionCall

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-FunctionCall")
    public void testFunctionCall() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: DirElemConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemConstructor")
    public void testDirElemConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<", XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, ">", XQueryTokenType.GREATER_THAN);

        matchSingleToken(lexer, "</", XQueryTokenType.CLOSE_XML_TAG);
        matchSingleToken(lexer, "/>", XQueryTokenType.SELF_CLOSING_XML_TAG);

        lexer.start("<one:two/>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "one", 11,  1,  4, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   11,  4,  5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 11,  5,  8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, "/>",  11,  8, 10, XQueryTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, "",     0, 10, 10, null);

        lexer.start("<one:two></one:two  >");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "one", 11,  1,  4, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   11,  4,  5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 11,  5,  8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   11,  8,  9, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "</",  17,  9, 11, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "one", 12, 11, 14, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   12, 14, 15, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 12, 15, 18, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, "  ",  12, 18, 20, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, ">",   12, 20, 21, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",     0, 21, 21, null);

        lexer.start("<one:two  ></one:two>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "one", 11,  1,  4, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   11,  4,  5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 11,  5,  8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, "  ",  11,  8, 10, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, ">",   25, 10, 11, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "</",  17, 11, 13, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "one", 12, 13, 16, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   12, 16, 17, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 12, 17, 20, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   12, 20, 21, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",     0, 21, 21, null);

        lexer.start("<one:two//*/>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "one", 11,  1,  4, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   11,  4,  5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 11,  5,  8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, "/",   11,  8,  9, XQueryTokenType.INVALID);
        matchToken(lexer, "/",   11,  9, 10, XQueryTokenType.INVALID);
        matchToken(lexer, "*",   11, 10, 11, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "/>",  11, 11, 13, XQueryTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, "",     0, 13, 13, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeList + DirAttributeValue

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirAttributeList")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirAttributeValue")
    public void testDirAttributeList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL);

        lexer.start("<one:two  a:b  =  \"One\"  c:d  =  'Two'  />");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "one", 11,  1,  4, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ":",   11,  4,  5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR);
        matchToken(lexer, "two", 11,  5,  8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, "  ",  11,  8, 10, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "a",   25, 10, 11, XQueryTokenType.XML_ATTRIBUTE_NCNAME);
        matchToken(lexer, ":",   25, 11, 12, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR);
        matchToken(lexer, "b",   25, 12, 13, XQueryTokenType.XML_ATTRIBUTE_NCNAME);
        matchToken(lexer, "  ",  25, 13, 15, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "=",   25, 15, 16, XQueryTokenType.XML_EQUAL);
        matchToken(lexer, "  ",  25, 16, 18, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "\"",  25, 18, 19, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One", 13, 19, 22, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",  13, 22, 23, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "  ",  25, 23, 25, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "c",   25, 25, 26, XQueryTokenType.XML_ATTRIBUTE_NCNAME);
        matchToken(lexer, ":",   25, 26, 27, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR);
        matchToken(lexer, "d",   25, 27, 28, XQueryTokenType.XML_ATTRIBUTE_NCNAME);
        matchToken(lexer, "  ",  25, 28, 30, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "=",   25, 30, 31, XQueryTokenType.XML_EQUAL);
        matchToken(lexer, "  ",  25, 31, 33, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "'",   25, 33, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "Two", 14, 34, 37, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",   14, 37, 38, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "  ",  25, 38, 40, XQueryTokenType.XML_WHITE_SPACE);
        matchToken(lexer, "/>",  25, 40, 42, XQueryTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, "",     0, 42, 42, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + QuotAttrValueContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QuotAttrValueContent")
    public void testDirAttributeValue_QuotAttrValueContent() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One {2}<& \u3053\u3093\u3070\u3093\u306F.\"", 0, 18, 11);
        matchToken(lexer, "\"",                               11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One ",                             13,  1,  5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "{",                                13,  5,  6, XQueryTokenType.BLOCK_OPEN);
        matchToken(lexer, "2",                                15,  6,  7, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "}",                                15,  7,  8, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "<",                                13,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "&",                                13,  9, 10, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " \u3053\u3093\u3070\u3093\u306F.", 13, 10, 17, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",                               13, 17, 18, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",                                 11, 18, 18, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + AposAttrValueContent + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-AposAttrValueContent")
    public void testDirAttributeValue_AposAttrValueContent() {
        Lexer lexer = new XQueryLexer();

        lexer.start("'One {2}<& \u3053\u3093\u3070\u3093\u306F.}'", 0, 19, 11);
        matchToken(lexer, "'",                                11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One ",                             14,  1,  5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "{",                                14,  5,  6, XQueryTokenType.BLOCK_OPEN);
        matchToken(lexer, "2",                                16,  6,  7, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "}",                                16,  7,  8, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "<",                                14,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "&",                                14,  9, 10, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " \u3053\u3093\u3070\u3093\u306F.", 14, 10, 17, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "}",                                14, 17, 18, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "'",                                14, 18, 19, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",                                 11, 19, 19, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CommonContent

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    public void testDirAttributeValue_CommonContent() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"{{}}\"", 0, 6, 11);
        matchToken(lexer, "\"",           11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "{{",           13, 1, 3, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "}}",           13, 3, 5, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "\"",           13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",             11, 6, 6, null);

        lexer.start("'{{}}'", 0, 6, 11);
        matchToken(lexer, "'",            11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "{{",           14, 1, 3, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "}}",           14, 3, 5, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "'",            14, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",             11, 6, 6, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testDirAttributeValue_PredefinedEntityRef() {
        Lexer lexer = new XQueryLexer();

        // NOTE: The predefined entity reference names are not validated by the lexer, as some
        // XQuery processors support HTML predefined entities. Shifting the name validation to
        // the parser allows proper validation errors to be generated.

        lexer.start("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"", 0, 35, 11);
        matchToken(lexer, "\"",    11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",   13,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&abc;", 13,  4,  9, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 13,  9, 14, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 13, 14, 19, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 13, 19, 24, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  13, 24, 28, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   13, 28, 31, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   13, 31, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",    13, 34, 35, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",      11, 35, 35, null);

        lexer.start("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'", 0, 35, 11);
        matchToken(lexer, "'",     11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",   14,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&abc;", 14,  4,  9, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 14,  9, 14, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 14, 14, 19, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 14, 19, 24, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  14, 24, 28, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   14, 28, 31, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   14, 31, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",     14, 34, 35, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",      11, 35, 35, null);

        lexer.start("\"&\"", 0, 3, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&",  13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 13, 2, 3, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",   11, 3, 3, null);

        lexer.start("\"&abc!\"", 0, 7, 11);
        matchToken(lexer, "\"",   11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&abc", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "!",    13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",   13, 6, 7, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",     11, 7, 7, null);

        lexer.start("\"& \"", 0, 4, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&",  13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  13, 2, 3, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",   11, 4, 4, null);

        lexer.start("\"&", 0, 2, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&",  13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   13, 2, 2, null);

        lexer.start("\"&abc", 0, 5, 11);
        matchToken(lexer, "\"",   11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&abc", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     13, 5, 5, null);

        lexer.start("\"&;\"", 0, 4, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&;", 13, 1, 3, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",   11, 4, 4, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testDirAttributeValue_EscapeQuot() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One\"\"Two\"", 0, 10, 11);
        matchToken(lexer, "\"",   11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",  13,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"\"", 13,  4,  6, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  13,  6,  9, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",   13,  9, 10, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",     11, 10, 10, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testDirAttributeValue_EscapeApos() {
        Lexer lexer = new XQueryLexer();

        lexer.start("'One''Two'", 0, 10, 11);
        matchToken(lexer, "'",    11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",  14,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "''",   14,  4,  6, XQueryTokenType.XML_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  14,  6,  9, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",    14,  9, 10, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",     11, 10, 10, null);
    }

    // endregion
    // region XQuery 1.0 :: DirAttributeValue + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testDirAttributeValue_CharRef_Octal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#20;Two\"", 0, 13, 11);
        matchToken(lexer, "\"",    11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",   13,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#20;", 13,  4,  9, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "Two",   13,  9, 12, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",    13, 12, 13, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",      11, 13, 13, null);

        lexer.start("'One&#20;Two'", 0, 13, 11);
        matchToken(lexer, "'",     11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",   14,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#20;", 14,  4,  9, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "Two",   14,  9, 12, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",     14, 12, 13, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",      11, 13, 13, null);

        lexer.start("\"&#\"", 0, 4, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",   11, 4, 4, null);

        lexer.start("\"&# \"", 0, 5, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"", 13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",   11, 5, 5, null);

        lexer.start("\"&#", 0, 3, 11);
        matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   13, 3, 3, null);

        lexer.start("\"&#12", 0, 5, 11);
        matchToken(lexer, "\"",   11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#12", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     13, 5, 5, null);

        lexer.start("\"&#;\"", 0, 5, 11);
        matchToken(lexer, "\"",   11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#;",  13, 1, 4, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "\"",   13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",     11, 5, 5, null);
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirAttributeValue")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testDirAttributeValue_CharRef_Hexadecimal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#x20;&#xae;&#xDC;Two\"", 0, 26, 11);
        matchToken(lexer, "\"",     11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",    13,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#x20;", 13,  4, 10, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 13, 10, 16, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 13, 16, 22, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    13, 22, 25, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",     13, 25, 26, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",       11, 26, 26, null);

        lexer.start("'One&#x20;&#xae;&#xDC;Two'", 0, 26, 11);
        matchToken(lexer, "'",      11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "One",    14,  1,  4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#x20;", 14,  4, 10, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 14, 10, 16, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 14, 16, 22, XQueryTokenType.XML_CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    14, 22, 25, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",      14, 25, 26, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",       11, 26, 26, null);

        lexer.start("\"&#x\"", 0, 5, 11);
        matchToken(lexer, "\"",  11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"",  13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",    11, 5, 5, null);

        lexer.start("\"&#x \"", 0, 6, 11);
        matchToken(lexer, "\"",  11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",   13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",  13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",    11, 6, 6, null);

        lexer.start("\"&#x", 0, 4, 11);
        matchToken(lexer, "\"",  11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",    13, 4, 4, null);

        lexer.start("\"&#x12", 0, 6, 11);
        matchToken(lexer, "\"",    11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#x12", 13, 1, 6, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",      13, 6, 6, null);

        lexer.start("\"&#x;&#x2G;&#x2g;&#xg2;\"", 0, 24, 11);
        matchToken(lexer, "\"",   11,  0,  1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "&#x;", 13,  1,  5, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 13,  5,  9, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   13,  9, 11, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#x2", 13, 11, 15, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   13, 15, 17, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "&#x",  13, 17, 20, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g2;",  13, 20, 23, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",   13, 23, 24, XQueryTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, "",     11, 24, 24, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + ElementContentChar + EnclosedExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ElementContentChar")
    public void testDirElemContent_ElementContentChar() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a>One {2}<& \u3053\u3093\u3070\u3093\u306F.}</a>");
        matchToken(lexer, "<",                                 0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",                                11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",                                11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One ",                             17,  3,  7, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "{",                                17,  7,  8, XQueryTokenType.BLOCK_OPEN);
        matchToken(lexer, "2",                                18,  8,  9, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "}",                                18,  9, 10, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "<",                                17, 10, 11, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "&",                                17, 11, 12, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " \u3053\u3093\u3070\u3093\u306F.", 17, 12, 19, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "}",                                17, 19, 20, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "</",                               17, 20, 22, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",                                12, 22, 23, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",                                12, 23, 24, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",                                  0, 24, 24, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirElemConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemConstructor")
    public void testDirElemContent_DirElemConstructor() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a>One <b>Two</b> Three</a>");
        matchToken(lexer, "<",       0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",      11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One ",   17,  3,  7, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "<",      17,  7,  8, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "b",      11,  8,  9, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      11,  9, 10, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "Two",    17, 10, 13, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",     17, 13, 15, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "b",      12, 15, 16, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      12, 16, 17, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, " Three", 17, 17, 23, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",     17, 23, 25, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",      12, 25, 26, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      12, 26, 27, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",        0, 27, 27, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirCommentConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirCommentConstructor")
    public void testDirElemContent_DirCommentConstructor() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<!<!-", 0, 5, 17);
        matchToken(lexer, "<!",       17, 0, 2, XQueryTokenType.INVALID);
        matchToken(lexer, "<!-",      17, 2, 5, XQueryTokenType.INVALID);
        matchToken(lexer, "",         17, 5, 5, null);

        lexer.start("<a>One <!-- 2 --> Three</a>");
        matchToken(lexer, "<",       0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",      11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One ",   17,  3,  7, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "<!--",   17,  7, 11, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " 2 ",    19, 11, 14, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",    19, 14, 17, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, " Three", 17, 17, 23, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",     17, 23, 25, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",      12, 25, 26, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      12, 26, 27, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",        0, 27, 27, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CDataSection (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CDataSection")
    public void testDirElemContent_CDataSection() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<!<![<![C<![CD<![CDA<![CDAT<![CDATA", 0, 35, 17);
        matchToken(lexer, "<!",       17,  0,  2, XQueryTokenType.INVALID);
        matchToken(lexer, "<![",      17,  2,  5, XQueryTokenType.INVALID);
        matchToken(lexer, "<![C",     17,  5,  9, XQueryTokenType.INVALID);
        matchToken(lexer, "<![CD",    17,  9, 14, XQueryTokenType.INVALID);
        matchToken(lexer, "<![CDA",   17, 14, 20, XQueryTokenType.INVALID);
        matchToken(lexer, "<![CDAT",  17, 20, 27, XQueryTokenType.INVALID);
        matchToken(lexer, "<![CDATA", 17, 27, 35, XQueryTokenType.INVALID);
        matchToken(lexer, "",         17, 35, 35, null);

        lexer.start("<a>One <![CDATA[ 2 ]]> Three</a>");
        matchToken(lexer, "<",          0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",         11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",         11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One ",      17,  3,  7, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "<![CDATA[", 17,  7, 16, XQueryTokenType.CDATA_SECTION_START_TAG);
        matchToken(lexer, " 2 ",       20, 16, 19, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "]]>",       20, 19, 22, XQueryTokenType.CDATA_SECTION_END_TAG);
        matchToken(lexer, " Three",    17, 22, 28, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",        17, 28, 30, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",         12, 30, 31, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",         12, 31, 32, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",           0, 32, 32, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + DirPIConstructor (DirectConstructor)

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirPIConstructor")
    public void testDirElemContent_DirPIConstructor() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a><?for  6^gkgw~*?g?></a>");
        matchToken(lexer, "<",           0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",          11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "<?",         17,  3,  5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "for",        23,  5,  8, XQueryTokenType.NCNAME);
        matchToken(lexer, "  ",         23,  8, 10, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "6^gkgw~*?g", 24, 10, 20, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS);
        matchToken(lexer, "?>",         24, 20, 22, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "</",         17, 22, 24, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",          12, 24, 25, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          12, 25, 26, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",            0, 26, 26, null);

        lexer.start("<a><?for?></a>");
        matchToken(lexer, "<",           0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",          11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "<?",         17,  3,  5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "for",        23,  5,  8, XQueryTokenType.NCNAME);
        matchToken(lexer, "?>",         23,  8, 10, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "</",         17, 10, 12, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",          12, 12, 13, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          12, 13, 14, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",            0, 14, 14, null);

        lexer.start("<a><?*?$?></a>");
        matchToken(lexer, "<",           0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",          11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "<?",         17,  3,  5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "*",          23,  5,  6, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "?",          23,  6,  7, XQueryTokenType.INVALID);
        matchToken(lexer, "$",          23,  7,  8, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "?>",         23,  8, 10, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "</",         17, 10, 12, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",          12, 12, 13, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",          12, 13, 14, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",            0, 14, 14, null);

        lexer.start("<?a ?", 0, 5, 17);
        matchToken(lexer, "<?",         17, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "a",          23, 2, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",          23, 3, 4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "?",          24, 4, 5, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS);
        matchToken(lexer, "",            6, 5, 5, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",           17, 5, 5, null);

        lexer.start("<?a ", 0, 4, 17);
        matchToken(lexer, "<?",         17, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "a",          23, 2, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",          23, 3, 4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",           24, 4, 4, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CommonContent

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommonContent")
    public void testDirElemContent_CommonContent() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a>{{}}</a>");
        matchToken(lexer, "<",   0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "{{", 17,  3,  5, XQueryTokenType.ESCAPED_CHARACTER);
        matchToken(lexer, "}}", 17,  5,  7, XQueryTokenType.ESCAPED_CHARACTER);
        matchToken(lexer, "</", 17,  7,  9, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",  12,  9, 10, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  12, 10, 11, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",    0, 11, 11, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PredefinedEntityRef")
    public void testDirElemContent_PredefinedEntityRef() {
        Lexer lexer = new XQueryLexer();

        // NOTE: The predefined entity reference names are not validated by the lexer, as some
        // XQuery processors support HTML predefined entities. Shifting the name validation to
        // the parser allows proper validation errors to be generated.

        lexer.start("<a>One&abc;&aBc;&Abc;&ABC;&a4;&a;Two</a>");
        matchToken(lexer, "<",      0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",     11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",     11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One",   17,  3,  6, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "&abc;", 17,  6, 11, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 17, 11, 16, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 17, 16, 21, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 17, 21, 26, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  17, 26, 30, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   17, 30, 33, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   17, 33, 36, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",    17, 36, 38, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",     12, 38, 39, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",     12, 39, 40, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",       0, 40, 40, null);

        lexer.start("<a>&</a>");
        matchToken(lexer, "<",   0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&",  17, 3, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "</", 17, 4, 6, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",  12, 6, 7, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  12, 7, 8, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",    0, 8, 8, null);

        lexer.start("<a>&abc!</a>");
        matchToken(lexer, "<",     0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",    11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&abc", 17,  3,  7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "!",    17,  7,  8, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",   17,  8, 10, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",    12, 10, 11, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    12, 11, 12, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",      0, 12, 12, null);

        lexer.start("<a>&");
        matchToken(lexer, "<",   0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&",  17, 3, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   17, 4, 4, null);

        lexer.start("<a>&abc");
        matchToken(lexer, "<",     0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",    11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&abc", 17, 3, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     17, 7, 7, null);

        lexer.start("<a>&;</a>");
        matchToken(lexer, "<",   0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&;", 17, 3, 5, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "</", 17, 5, 7, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",  12, 7, 8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  12, 8, 9, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",    0, 9, 9, null);
    }

    // endregion
    // region XQuery 1.0 :: DirElemContent + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testDirElemContent_CharRef_Octal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a>One&#20;Two</a>");
        matchToken(lexer, "<",      0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",     11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",     11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One",   17,  3,  6, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "&#20;", 17,  6, 11, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",   17, 11, 14, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",    17, 14, 16, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",     12, 16, 17, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",     12, 17, 18, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",       0, 18, 18, null);

        lexer.start("<a>&#</a>");
        matchToken(lexer, "<",   0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#", 17, 3, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "</", 17, 5, 7, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",  12, 7, 8, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  12, 8, 9, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",    0, 9, 9, null);

        lexer.start("<a>&# </a>");
        matchToken(lexer, "<",   0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#", 17,  3,  5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  17,  5,  6, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</", 17,  6,  8, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",  12,  8,  9, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  12,  9, 10, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",    0, 10, 10, null);

        lexer.start("<a>&#");
        matchToken(lexer, "<",   0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",  11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",  11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#", 17, 3, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   17, 5, 5, null);

        lexer.start("<a>&#12");
        matchToken(lexer, "<",     0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",    11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#12", 17, 3, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     17, 7, 7, null);

        lexer.start("<a>&#;</a>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",   11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#;", 17,  3,  6, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "</",  17,  6,  8, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",   12,  8,  9, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   12,  9, 10, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirElemContent")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testDirElemContent_CharRef_Hexadecimal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<a>One&#x20;&#xae;&#xDC;Two</a>");
        matchToken(lexer, "<",       0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",      11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "One",    17,  3,  6, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "&#x20;", 17,  6, 12, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 17, 12, 18, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 17, 18, 24, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    17, 24, 27, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",     17, 27, 29, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",      12, 29, 30, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",      12, 30, 31, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",        0, 31, 31, null);

        lexer.start("<a>&#x</a>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",   11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#x", 17,  3,  6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "</",  17,  6,  8, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",   12,  8,  9, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   12,  9, 10, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",     0, 10, 10, null);

        lexer.start("<a>&#x </a>");
        matchToken(lexer, "<",    0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",   11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#x", 17,  3,  6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",   17,  6,  7, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",  17,  7,  9, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",   12,  9, 10, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   12, 10, 11, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",     0, 11, 11, null);

        lexer.start("<a>&#x");
        matchToken(lexer, "<",    0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",   11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",   11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#x", 17, 3, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",    17, 6, 6, null);

        lexer.start("<a>&#x12");
        matchToken(lexer, "<",      0, 0, 1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",     11, 1, 2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",     11, 2, 3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#x12", 17, 3, 8, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",      17, 8, 8, null);

        lexer.start("<a>&#x;&#x2G;&#x2g;&#xg2;</a>");
        matchToken(lexer, "<",     0,  0,  1, XQueryTokenType.OPEN_XML_TAG);
        matchToken(lexer, "a",    11,  1,  2, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    11,  2,  3, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "&#x;", 17,  3,  7, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 17,  7, 11, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   17, 11, 13, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "&#x2", 17, 13, 17, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   17, 17, 19, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "&#x",  17, 19, 22, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g2;",  17, 22, 25, XQueryTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",   17, 25, 27, XQueryTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "a",    12, 27, 28, XQueryTokenType.XML_TAG_NCNAME);
        matchToken(lexer, ">",    12, 28, 29, XQueryTokenType.END_XML_TAG);
        matchToken(lexer, "",      0, 29, 29, null);
    }

    // endregion
    // region XQuery 1.0 :: DirCommentConstructor + DirCommentContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentContents")
    public void testDirCommentConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<",    0, XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, "<!",   0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!-",  0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!--", 5, XQueryTokenType.XML_COMMENT_START_TAG);

        // Unary Minus
        lexer.start("--");
        matchToken(lexer, "-", 0, 0, 1, XQueryTokenType.MINUS);
        matchToken(lexer, "-", 0, 1, 2, XQueryTokenType.MINUS);
        matchToken(lexer, "",  0, 2, 2, null);

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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentContents")
    public void testDirCommentConstructor_InitialState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<!-- Test", 4, 9, 5);
        matchToken(lexer, " Test", 5, 4, 9, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 9, 9, null);

        lexer.start("<!-- Test -->", 4, 13, 5);
        matchToken(lexer, " Test ", 5,  4, 10, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",    5, 10, 13, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, "",       0, 13, 13, null);
    }

    // endregion
    // region XQuery 1.0 :: DirPIConstructor + DirPIContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirPIConstructor")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DirPIContents")
    public void testDirPIConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<?", 21, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchSingleToken(lexer, "?>",  0, XQueryTokenType.PROCESSING_INSTRUCTION_END);


        lexer.start("<?for  6^gkgw~*?g?>");
        matchToken(lexer, "<?",          0,  0,  2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "for",        21,  2,  5, XQueryTokenType.NCNAME);
        matchToken(lexer, "  ",         21,  5,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "6^gkgw~*?g", 22,  7, 17, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS);
        matchToken(lexer, "?>",         22, 17, 19, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "",            0, 19, 19, null);

        lexer.start("<?for?>");
        matchToken(lexer, "<?",          0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "for",        21, 2, 5, XQueryTokenType.NCNAME);
        matchToken(lexer, "?>",         21, 5, 7, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "",            0, 7, 7, null);

        lexer.start("<?*?$?>");
        matchToken(lexer, "<?",          0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "*",          21, 2, 3, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "?",          21, 3, 4, XQueryTokenType.INVALID);
        matchToken(lexer, "$",          21, 4, 5, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "?>",         21, 5, 7, XQueryTokenType.PROCESSING_INSTRUCTION_END);
        matchToken(lexer, "",            0, 7, 7, null);

        lexer.start("<?a ?");
        matchToken(lexer, "<?",          0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "a",          21, 2, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",          21, 3, 4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "?",          22, 4, 5, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS);
        matchToken(lexer, "",            6, 5, 5, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",            0, 5, 5, null);

        lexer.start("<?a ");
        matchToken(lexer, "<?",          0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchToken(lexer, "a",          21, 2, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",          21, 3, 4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",           22, 4, 4, null);
    }

    // endregion
    // region XQuery 1.0 :: CDataSection + CDataSectionContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSectionContents")
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

        matchSingleToken(lexer, "]",   XQueryTokenType.SQUARE_CLOSE);

        lexer.start("]]");
        matchToken(lexer, "]", 0, 0, 1, XQueryTokenType.SQUARE_CLOSE);
        matchToken(lexer, "]", 0, 1, 2, XQueryTokenType.SQUARE_CLOSE);
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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSection")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CDataSectionContents")
    public void testCDataSection_InitialState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("<![CDATA[ Test", 9, 14, 7);
        matchToken(lexer, " Test",     7,  9, 14, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "",          6, 14, 14, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",          0, 14, 14, null);

        lexer.start("<![CDATA[ Test ]]>", 9, 18, 7);
        matchToken(lexer, " Test ",    7,  9, 15, XQueryTokenType.CDATA_SECTION);
        matchToken(lexer, "]]>",       7, 15, 18, XQueryTokenType.CDATA_SECTION_END_TAG);
        matchToken(lexer, "",          0, 18, 18, null);
    }

    // endregion
    // region XQuery 1.0 :: CompDocConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompDocConstructor")
    public void testCompDocConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "document", XQueryTokenType.K_DOCUMENT);
        matchSingleToken(lexer, "{",        XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",        XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CompElemConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompElemConstructor")
    public void testCompElemConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT);
        matchSingleToken(lexer, "{",       XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",       XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CompAttrConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompAttrConstructor")
    public void testCompAttrConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE);
        matchSingleToken(lexer, "{",         XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",         XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CompTextConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompTextConstructor")
    public void testCompTextConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT);
        matchSingleToken(lexer, "{",    XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",    XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CompCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompCommentConstructor")
    public void testCompCommentConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "comment", XQueryTokenType.K_COMMENT);
        matchSingleToken(lexer, "{",       XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",       XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CompPIConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CompPIConstructor")
    public void testCompPIConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION);
        matchSingleToken(lexer, "{",                      XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",                      XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: TypeDeclaration

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TypeDeclaration")
    public void testTypeDeclaration() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "as", XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 1.0 :: SequenceType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SequenceType")
    public void testSequenceType() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "empty-sequence", XQueryTokenType.K_EMPTY_SEQUENCE);
        matchSingleToken(lexer, "(",              XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",              XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: OccurrenceIndicator

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-OccurrenceIndicator")
    public void testOccurrenceIndicator() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL);
        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
    }

    // endregion
    // region XQuery 1.0 :: ItemType

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ItemType")
    public void testItemType() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "item", XQueryTokenType.K_ITEM);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: AnyKindTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AnyKindTest")
    public void testAnyKindTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: DocumentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-DocumentTest")
    public void testDocumentTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "document-node", XQueryTokenType.K_DOCUMENT_NODE);
        matchSingleToken(lexer, "(",             XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",             XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: TextTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-TextTest")
    public void testTextTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: CommentTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-CommentTest")
    public void testCommentTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "comment", XQueryTokenType.K_COMMENT);
        matchSingleToken(lexer, "(",       XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",       XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: PITest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-PITest")
    public void testPITest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION);
        matchSingleToken(lexer, "(",                      XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",                      XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: AttributeTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AttributeTest")
    public void testAttributeTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE);
        matchSingleToken(lexer, "(",         XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",         XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: AttribNameOrWildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-AttribNameOrWildcard")
    public void testAttribNameOrWildcard() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
    }

    // endregion
    // region XQuery 1.0 :: SchemaAttributeTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaAttributeTest")
    public void testSchemaAttributeTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "schema-attribute", XQueryTokenType.K_SCHEMA_ATTRIBUTE);
        matchSingleToken(lexer, "(",                XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",                XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: ElementTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ElementTest")
    public void testElementTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT);
        matchSingleToken(lexer, "(",       XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",       XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "?",       XQueryTokenType.OPTIONAL);
    }

    // endregion
    // region XQuery 1.0 :: ElementNameOrWildcard

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ElementNameOrWildcard")
    public void testElementNameOrWildcard() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
    }

    // endregion
    // region XQuery 1.0 :: SchemaElementTest

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-SchemaElementTest")
    public void testSchemaElementTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "schema-element", XQueryTokenType.K_SCHEMA_ELEMENT);
        matchSingleToken(lexer, "(",              XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",              XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 1.0 :: IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("1234");
        matchToken(lexer, "1234", 0, 0, 4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "",     0, 4, 4, null);
    }

    // endregion
    // region XQuery 1.0 :: DecimalLiteral

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
    // region XQuery 1.0 :: DoubleLiteral

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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
    public void testDoubleLiteral_InitialState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("1e", 1, 2, 3);
        matchToken(lexer, "e",  3, 1, 2, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 2, 2, null);
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral

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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral_InitialState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"Hello World\"", 1, 13, 1);
        matchToken(lexer, "Hello World", 1,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",          1, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);

        lexer.start("'Hello World'", 1, 13, 2);
        matchToken(lexer, "Hello World", 2,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",           2, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + PredefinedEntityRef

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
    // region XQuery 1.0 :: StringLiteral + EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One\"\"Two\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"\"", 1,  4,  6, XQueryTokenType.ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  1,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        Lexer lexer = new XQueryLexer();

        lexer.start("'One''Two'");
        matchToken(lexer, "'",    0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "''",   2,  4,  6, XQueryTokenType.ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  2,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",    2,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region XQuery 1.0 :: StringLiteral + CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testStringLiteral_CharRef_Octal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#20;Two\"");
        matchToken(lexer, "\"",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  1,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    1,  9, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",     1, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 13, 13, null);

        lexer.start("'One&#20;Two'");
        matchToken(lexer, "'",      0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  2,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    2,  9, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",      2, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 13, 13, null);

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

        lexer.start("\"&#;\"");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#;",  1, 1, 4, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "\"",   1, 4, 5, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 5, 5, null);
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testStringLiteral_CharRef_Hexadecimal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#x20;&#xae;&#xDC;Two\"");
        matchToken(lexer, "\"",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x20;", 1,  4, 10, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 1, 10, 16, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 1, 16, 22, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    1, 22, 25, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",     1, 25, 26, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 26, 26, null);

        lexer.start("'One&#x20;&#xae;&#xDC;Two'");
        matchToken(lexer, "'",      0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x20;", 2,  4, 10, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 2, 10, 16, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 2, 16, 22, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    2, 22, 25, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",      2, 25, 26, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 26, 26, null);

        lexer.start("\"&#x\"");
        matchToken(lexer, "\"",  0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"",  1, 4, 5, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",    0, 5, 5, null);

        lexer.start("\"&#x \"");
        matchToken(lexer, "\"",  0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",   1, 4, 5, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",  1, 5, 6, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",    0, 6, 6, null);

        lexer.start("\"&#x");
        matchToken(lexer, "\"",  0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",    1, 4, 4, null);

        lexer.start("\"&#x12");
        matchToken(lexer, "\"",    0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#x12", 1, 1, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",      1, 6, 6, null);

        lexer.start("\"&#x;&#x2G;&#x2g;&#xg2;\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#x;", 1,  1,  5, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 1,  5,  9, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   1,  9, 11, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x2", 1, 11, 15, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   1, 15, 17, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x",  1, 17, 20, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g2;",  1, 20, 23, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1, 23, 24, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 24, 24, null);
    }

    // endregion
    // region XQuery 1.0 :: Comment + CommentContents

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentContents")
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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CommentContents")
    public void testComment_InitialState() {
        Lexer lexer = new XQueryLexer();

        lexer.start("(: Test :", 2, 9, 4);
        matchToken(lexer, " Test :", 4, 2, 9, XQueryTokenType.COMMENT);
        matchToken(lexer, "",        6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",        0, 9, 9, null);

        lexer.start("(: Test :)", 2, 10, 4);
        matchToken(lexer, " Test ", 4,  2,  8, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",     4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0, 10, 10, null);
    }

    // endregion
    // region XQuery 1.0 :: QName

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
    // region XQuery 1.0 :: NCName

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
    // region XQuery 1.0 :: S

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
    // region XQuery 3.0 :: DecimalFormatDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-DecimalFormatDecl")
    public void testDecimalFormatDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",        XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "decimal-format", XQueryTokenType.K_DECIMAL_FORMAT);
        matchSingleToken(lexer, "default",        XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "=",              XQueryTokenType.EQUAL);
    }

    // endregion
    // region XQuery 3.0 :: DFPropertyName

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-DFPropertyName")
    public void testDFPropertyName() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "decimal-separator",  XQueryTokenType.K_DECIMAL_SEPARATOR);
        matchSingleToken(lexer, "grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR);
        matchSingleToken(lexer, "infinity",           XQueryTokenType.K_INFINITY);
        matchSingleToken(lexer, "minus-sign",         XQueryTokenType.K_MINUS_SIGN);
        matchSingleToken(lexer, "NaN",                XQueryTokenType.K_NAN);
        matchSingleToken(lexer, "percent",            XQueryTokenType.K_PERCENT);
        matchSingleToken(lexer, "per-mille",          XQueryTokenType.K_PER_MILLE);
        matchSingleToken(lexer, "zero-digit",         XQueryTokenType.K_ZERO_DIGIT);
        matchSingleToken(lexer, "digit",              XQueryTokenType.K_DIGIT);
        matchSingleToken(lexer, "pattern-separator",  XQueryTokenType.K_PATTERN_SEPARATOR);
    }

    // endregion
    // region XQuery 3.0 :: AnnotationDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-AnnotationDecl")
    public void testAnnotationDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE);
    }

    // endregion
    // region XQuery 3.0 :: Annotation

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-Annotation")
    public void testAnnotation() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "%", XQueryTokenType.ANNOTATION_INDICATOR);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);

        matchSingleToken(lexer, "public",  XQueryTokenType.K_PUBLIC);
        matchSingleToken(lexer, "private", XQueryTokenType.K_PRIVATE);
    }

    // endregion
    // region XQuery 3.0 :: ContextItemDecl

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-ContextItemDecl")
    public void testContextItemDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "context",  XQueryTokenType.K_CONTEXT);
        matchSingleToken(lexer, "item",     XQueryTokenType.K_ITEM);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL);
        matchSingleToken(lexer, ":=",       XQueryTokenType.ASSIGN_EQUAL);
    }

    // endregion
    // region XQuery 3.0 :: AllowingEmpty

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-AllowingEmpty")
    public void testAllowingEmpty() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "allowing", XQueryTokenType.K_ALLOWING);
        matchSingleToken(lexer, "empty",    XQueryTokenType.K_EMPTY);
    }

    // endregion
    // region XQuery 3.0 :: WindowClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-WindowClause")
    public void testWindowClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "for", XQueryTokenType.K_FOR);
    }

    // endregion
    // region XQuery 3.0 :: TumblingWindowClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-TumblingWindowClause")
    public void testTumblingWindowClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "tumbling", XQueryTokenType.K_TUMBLING);
        matchSingleToken(lexer, "window",   XQueryTokenType.K_WINDOW);
        matchSingleToken(lexer, "$",        XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "in",       XQueryTokenType.K_IN);
    }

    // endregion
    // region XQuery 3.0 :: SlidingWindowClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-SlidingWindowClause")
    public void testSlidingWindowClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "sliding", XQueryTokenType.K_SLIDING);
        matchSingleToken(lexer, "window",  XQueryTokenType.K_WINDOW);
        matchSingleToken(lexer, "$",       XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "in",      XQueryTokenType.K_IN);
    }

    // endregion
    // region XQuery 3.0 :: WindowStartCondition

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-WindowStartCondition")
    public void testWindowStartCondition() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "start", XQueryTokenType.K_START);
        matchSingleToken(lexer, "when",  XQueryTokenType.K_WHEN);
    }

    // endregion
    // region XQuery 3.0 :: WindowEndCondition

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-WindowEndCondition")
    public void testWindowEndCondition() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "only", XQueryTokenType.K_ONLY);
        matchSingleToken(lexer, "end",  XQueryTokenType.K_END);
        matchSingleToken(lexer, "when", XQueryTokenType.K_WHEN);
    }

    // endregion
    // region XQuery 3.0 :: WindowVars

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-WindowVars")
    public void testWindowVars() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$",        XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "previous", XQueryTokenType.K_PREVIOUS);
        matchSingleToken(lexer, "next",     XQueryTokenType.K_NEXT);
    }

    // endregion
    // region XQuery 3.0 :: CountClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-CountClause")
    public void testCountClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "count", XQueryTokenType.K_COUNT);
        matchSingleToken(lexer, "$",     XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region XQuery 3.0 :: GroupByClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-GroupByClause")
    public void testGroupByClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "group", XQueryTokenType.K_GROUP);
        matchSingleToken(lexer, "by",    XQueryTokenType.K_BY);
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpecList

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-GroupingSpecList")
    public void testGroupingSpecList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region XQuery 3.0 :: GroupingSpec

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-GroupingSpec")
    public void testGroupingSpec() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ":=",        XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION);
    }

    // endregion
    // region XQuery 3.0 :: GroupingVariable

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-GroupingVariable")
    public void testGroupingVariable() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
    }

    // endregion
    // region XQuery 3.0 :: ReturnClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-ReturnClause")
    public void testReturnClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region XQuery 3.0 :: SwitchExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-SwitchExpr")
    public void testSwitchExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "switch",  XQueryTokenType.K_SWITCH);
        matchSingleToken(lexer, "(",       XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",       XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT);
        matchSingleToken(lexer, "return",  XQueryTokenType.K_RETURN);
    }

    // endregion
    // region XQuery 3.0 :: SwitchCaseClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-SwitchCaseClause")
    public void testSwitchCaseClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "case",   XQueryTokenType.K_CASE);
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region XQuery 3.0 :: SequenceTypeUnion

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-SequenceTypeUnion")
    public void testSequenceTypeUnion() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "|", XQueryTokenType.UNION);
    }

    // endregion
    // region XQuery 3.0 :: TryClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-TryClause")
    public void testTryClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "try", XQueryTokenType.K_TRY);
        matchSingleToken(lexer, "{",   XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",   XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: CatchClause

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-CatchClause")
    public void testCatchClause() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "catch", XQueryTokenType.K_CATCH);
        matchSingleToken(lexer, "{",     XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",     XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: CatchErrorList

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-CatchErrorList")
    public void testCatchErrorList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "|", XQueryTokenType.UNION);
    }

    // endregion
    // region XQuery 3.0 :: StringConcatExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-StringConcatExpr")
    public void testStringConcatExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "||", XQueryTokenType.CONCATENATION);
    }

    // endregion
    // region XQuery 3.0 :: ValidateExpr

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-ValidateExpr")
    public void testValidateExpr_Type() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE);
        matchSingleToken(lexer, "type",     XQueryTokenType.K_TYPE);
        matchSingleToken(lexer, "{",        XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",        XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: SimpleMapExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-SimpleMapExpr")
    public void testSimpleMapExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "!", XQueryTokenType.MAP_OPERATOR);
    }

    // endregion
    // region XQuery 3.0 :: ArgumentList

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-ArgumentList")
    public void testArgumentList() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: ArgumentPlaceholder

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-ArgumentPlaceholder")
    public void testArgumentPlaceholder() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL);
    }

    // endregion
    // region XQuery 3.0 :: CompNamespaceConstructor

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-CompNamespaceConstructor")
    public void testCompNamespaceConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "{",         XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",         XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: NamedFunctionRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-NamedFunctionRef")
    public void testNamedFunctionRef() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR);
    }

    // endregion
    // region XQuery 3.0 :: InlineFunctionExpr

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-InlineFunctionExpr")
    public void testInlineFunctionExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 3.0 :: NamespaceNodeTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-NamespaceNodeTest")
    public void testNamespaceNodeTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "namespace-node", XQueryTokenType.K_NAMESPACE_NODE);
        matchSingleToken(lexer, "(",              XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",              XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: AnyFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-AnyFunctionTest")
    public void testAnyFunctionTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, "*",        XQueryTokenType.STAR);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: TypedFunctionTest

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-TypedFunctionTest")
    public void testTypedFunctionTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",",        XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
    }

    // endregion
    // region XQuery 3.0 :: ParenthesizedItemType

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-ParenthesizedItemType")
    public void testParenthesizedItemType() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.0 :: BracedURILiteral

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-BracedURILiteral")
    public void testBracedURILiteral() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "Q", XQueryTokenType.NCNAME);

        lexer.start("Q{");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "",   26, 2, 2, null);

        lexer.start("Q{Hello World}");
        matchToken(lexer, "Q{",           0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "Hello World", 26,  2, 13, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",           26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",             0, 14, 14, null);

        // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
        lexer.start("Q{A\"\"B''C{{D}}E}");
        matchToken(lexer, "Q{",         0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "A\"\"B''C", 26,  2,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "{",         26,  9, 10, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "{",         26, 10, 11, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "D",         26, 11, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",         26, 12, 13, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "}",          0, 13, 14, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "E",          0, 14, 15, XQueryTokenType.NCNAME);
        matchToken(lexer, "}",          0, 15, 16, XQueryTokenType.BLOCK_CLOSE);
        matchToken(lexer, "",           0, 16, 16, null);
    }

    // endregion
    // region XQuery 3.0 :: BracedURILiteral + PredefinedEntityRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testBracedURILiteral_PredefinedEntityRef() {
        Lexer lexer = new XQueryLexer();

        // NOTE: The predefined entity reference names are not validated by the lexer, as some
        // XQuery processors support HTML predefined entities. Shifting the name validation to
        // the parser allows proper validation errors to be generated.

        lexer.start("Q{One&abc;&aBc;&Abc;&ABC;&a4;&a;Two}");
        matchToken(lexer, "Q{",     0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "One",   26,  2,  5, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&abc;", 26,  5, 10, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 26, 10, 15, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 26, 15, 20, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 26, 20, 25, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  26, 25, 29, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   26, 29, 32, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   26, 32, 35, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",     26, 35, 36, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",       0, 36, 36, null);

        lexer.start("Q{&}");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&",  26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "}",  26, 3, 4, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",    0, 4, 4, null);

        lexer.start("Q{&abc!}");
        matchToken(lexer, "Q{",    0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&abc", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "!",    26, 6, 7, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",    26, 7, 8, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",      0, 8, 8, null);

        lexer.start("Q{& }");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&",  26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  26, 3, 4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",  26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",    0, 5, 5, null);

        lexer.start("Q{&");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&",  26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   26, 3, 3, null);

        lexer.start("Q{&abc");
        matchToken(lexer, "Q{",    0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&abc", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     26, 6, 6, null);

        lexer.start("Q{&;}");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&;", 26, 2, 4, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "}",  26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",    0, 5, 5, null);
    }

    // endregion
    // region XQuery 3.0 :: BracedURILiteral + CharRef

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testBracedURILiteral_CharRef_Octal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("Q{One&#20;Two}");
        matchToken(lexer, "Q{",     0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "One",   26,  2,  5, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;", 26,  5, 10, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",   26, 10, 13, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",     26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",       0, 14, 14, null);

        lexer.start("Q{&#}");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "}",  26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",    0, 5, 5, null);

        lexer.start("Q{&# }");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  26, 4, 5, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",  26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",    0, 6, 6, null);

        lexer.start("Q{&#");
        matchToken(lexer, "Q{",  0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   26, 4, 4, null);

        lexer.start("Q{&#12");
        matchToken(lexer, "Q{",    0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#12", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     26, 6, 6, null);

        lexer.start("Q{&#;}");
        matchToken(lexer, "Q{",   0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#;", 26, 2, 5, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "}",   26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",     0, 6, 6, null);
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-BracedURILiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testBracedURILiteral_CharRef_Hexadecimal() {
        Lexer lexer = new XQueryLexer();

        lexer.start("Q{One&#x20;&#xae;&#xDC;Two}");
        matchToken(lexer, "Q{",      0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "One",    26,  2,  5, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x20;", 26,  5, 11, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xae;", 26, 11, 17, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xDC;", 26, 17, 23, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    26, 23, 26, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",      26, 26, 27, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",        0, 27, 27, null);

        lexer.start("Q{&#x}");
        matchToken(lexer, "Q{",   0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "}",   26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",     0, 6, 6, null);

        lexer.start("Q{&#x }");
        matchToken(lexer, "Q{",   0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",   26, 5, 6, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",   26, 6, 7, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",     0, 7, 7, null);

        lexer.start("Q{&#x");
        matchToken(lexer, "Q{",   0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",    26, 5, 5, null);

        lexer.start("Q{&#x12");
        matchToken(lexer, "Q{",     0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#x12", 26, 2, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",      26, 7, 7, null);

        lexer.start("Q{&#x;&#x2G;&#x2g;&#xg2;}");
        matchToken(lexer, "Q{",    0,  0,  2, XQueryTokenType.BRACED_URI_LITERAL_START);
        matchToken(lexer, "&#x;", 26,  2,  6, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 26,  6, 10, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   26, 10, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x2", 26, 12, 16, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   26, 16, 18, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x",  26, 18, 21, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g2;",  26, 21, 24, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",    26, 24, 25, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",      0, 25, 25, null);
    }

    // endregion
    // region XQuery 3.1 :: DFPropertyName

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/xquery-30/#doc-xquery30-DFPropertyName")
    public void testDFPropertyName_XQuery31() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "decimal-separator",  XQueryTokenType.K_DECIMAL_SEPARATOR);
        matchSingleToken(lexer, "grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR);
        matchSingleToken(lexer, "infinity",           XQueryTokenType.K_INFINITY);
        matchSingleToken(lexer, "minus-sign",         XQueryTokenType.K_MINUS_SIGN);
        matchSingleToken(lexer, "NaN",                XQueryTokenType.K_NAN);
        matchSingleToken(lexer, "percent",            XQueryTokenType.K_PERCENT);
        matchSingleToken(lexer, "per-mille",          XQueryTokenType.K_PER_MILLE);
        matchSingleToken(lexer, "zero-digit",         XQueryTokenType.K_ZERO_DIGIT);
        matchSingleToken(lexer, "digit",              XQueryTokenType.K_DIGIT);
        matchSingleToken(lexer, "pattern-separator",  XQueryTokenType.K_PATTERN_SEPARATOR);
        matchSingleToken(lexer, "exponent-separator", XQueryTokenType.K_EXPONENT_SEPARATOR); // New in XQuery 3.1
    }

    // endregion
    // region XQuery 3.1 :: ArrowExpr

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-ArrowExpr")
    public void testArrowExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "=>", XQueryTokenType.ARROW);
    }

    // endregion
    // region XQuery 3.1 :: Lookup

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-Lookup")
    public void testLookup() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL);
    }

    // endregion
    // region XQuery 3.1 :: KeySpecifier

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-KeySpecifier")
    public void testKeySpecifier() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
    }

    // endregion
    // region XQuery 3.1 :: MapConstructor

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-MapConstructor")
    public void testMapConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP);
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: MapConstructorEntry

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-MapConstructorEntry")
    public void testMapConstructorEntry() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ":", XQueryTokenType.QNAME_SEPARATOR);
    }

    // endregion
    // region XQuery 3.1 :: SquareArrayConstructor

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-SquareArrayConstructor")
    public void testSquareArrayConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "[", XQueryTokenType.SQUARE_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: CurlyArrayConstructor

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-CurlyArrayConstructor")
    public void testCurlyArrayConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY);
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: StringConstructor + StringConstructorContent

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-StringConstructor")
    public void testStringConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "`",   XQueryTokenType.INVALID);
        matchSingleToken(lexer, "``",  XQueryTokenType.INVALID);

        matchSingleToken(lexer, "]",   XQueryTokenType.SQUARE_CLOSE);
        matchSingleToken(lexer, "]`",  XQueryTokenType.INVALID);
        matchSingleToken(lexer, "]``", XQueryTokenType.STRING_CONSTRUCTOR_END);

        lexer.start("``[");
        matchToken(lexer, "``[",  0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START);
        matchToken(lexer, "",    27, 3, 3, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS);
        matchToken(lexer, "",     6, 3, 3, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",     0, 3, 3, null);

        lexer.start("``[One]Two]`");
        matchToken(lexer, "``[",        0,  0,  3, XQueryTokenType.STRING_CONSTRUCTOR_START);
        matchToken(lexer, "One]Two]`", 27,  3, 12, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS);
        matchToken(lexer, "",           6, 12, 12, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",           0, 12, 12, null);

        lexer.start("``[One]Two]``");
        matchToken(lexer, "``[",      0,  0,  3, XQueryTokenType.STRING_CONSTRUCTOR_START);
        matchToken(lexer, "One]Two", 27,  3, 10, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS);
        matchToken(lexer, "]``",      0, 10, 13, XQueryTokenType.STRING_CONSTRUCTOR_END);
        matchToken(lexer, "",         0, 13, 13, null);
    }

    // endregion
    // region XQuery 3.1 :: StringConstructorInterpolation

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-StringConstructorInterpolation")
    public void testStringConstructorInterpolation() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "`{", XQueryTokenType.STRING_INTERPOLATION_OPEN);
        matchSingleToken(lexer, "}`", XQueryTokenType.STRING_INTERPOLATION_CLOSE);

        lexer.start("``[One`{2}`Three]``");
        matchToken(lexer, "``[",    0,  0,  3, XQueryTokenType.STRING_CONSTRUCTOR_START);
        matchToken(lexer, "One",   27,  3,  6, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS);
        matchToken(lexer, "`{",    27,  6,  8, XQueryTokenType.STRING_INTERPOLATION_OPEN);
        matchToken(lexer, "2",     28,  8,  9, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "}`",    28,  9, 11, XQueryTokenType.STRING_INTERPOLATION_CLOSE);
        matchToken(lexer, "Three", 27, 11, 16, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS);
        matchToken(lexer, "]``",    0, 16, 19, XQueryTokenType.STRING_CONSTRUCTOR_END);
        matchToken(lexer, "",       0, 19, 19, null);
    }

    // endregion
    // region XQuery 3.1 :: UnaryLookup

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-UnaryLookup")
    public void testUnaryLookup() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL);
    }

    // endregion
    // region XQuery 3.1 :: AnyMapTest

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-AnyMapTest")
    public void testAnyMapTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: TypedMapTest

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-TypedMapTest")
    public void testTypedMapTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: AnyArrayTest

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-AnyArrayTest")
    public void testAnyArrayTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region XQuery 3.1 :: TypedArrayTest

    @Specification(name="XQuery 3.1 CR", reference="https://www.w3.org/TR/2015/CR-xquery-31-20151217/#prod-xquery31-TypedArrayTest")
    public void testTypedArrayTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_UpdateFacility10() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING);
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL);
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",      XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "revalidation", XQueryTokenType.K_REVALIDATION);
        matchSingleToken(lexer, "strict",       XQueryTokenType.K_STRICT);
        matchSingleToken(lexer, "lax",          XQueryTokenType.K_LAX);
        matchSingleToken(lexer, "skip",         XQueryTokenType.K_SKIP);
    }

    // endregion
    // region Update Facility 1.0 :: InsertExprTargetChoice

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "as",     XQueryTokenType.K_AS);
        matchSingleToken(lexer, "first",  XQueryTokenType.K_FIRST);
        matchSingleToken(lexer, "last",   XQueryTokenType.K_LAST);
        matchSingleToken(lexer, "into",   XQueryTokenType.K_INTO);
        matchSingleToken(lexer, "after",  XQueryTokenType.K_AFTER);
        matchSingleToken(lexer, "before", XQueryTokenType.K_BEFORE);
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "insert", XQueryTokenType.K_INSERT);
        matchSingleToken(lexer, "node",   XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "nodes",  XQueryTokenType.K_NODES);
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "delete", XQueryTokenType.K_DELETE);
        matchSingleToken(lexer, "node",   XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "nodes",  XQueryTokenType.K_NODES);
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "replace", XQueryTokenType.K_REPLACE);
        matchSingleToken(lexer, "value",   XQueryTokenType.K_VALUE);
        matchSingleToken(lexer, "of",      XQueryTokenType.K_OF);
        matchSingleToken(lexer, "node",    XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "with",    XQueryTokenType.K_WITH);
    }

    // endregion
    // region Update Facility 1.0 :: RenameExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "rename", XQueryTokenType.K_RENAME);
        matchSingleToken(lexer, "node",   XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "as",     XQueryTokenType.K_AS);
    }

    // endregion
    // region Update Facility 1.0 :: TransformExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "copy",   XQueryTokenType.K_COPY);
        matchSingleToken(lexer, "$",      XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",     XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, ",",      XQueryTokenType.COMMA);
        matchSingleToken(lexer, "modify", XQueryTokenType.K_MODIFY);
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN);
    }

    // endregion
    // region Update Facility 3.0 :: CompatibilityAnnotation

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING);
    }

    // endregion
    // region Update Facility 3.0 :: TransformWithExpr

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "transform", XQueryTokenType.K_TRANSFORM);
        matchSingleToken(lexer, "with", XQueryTokenType.K_WITH);
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region Update Facility 3.0 :: UpdatingFunctionCall

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "invoke", XQueryTokenType.K_INVOKE);
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region Scripting Extension 1.0 :: VarDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    public void testVarDecl_Scripting10() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",      XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "unassignable", XQueryTokenType.K_UNASSIGNABLE);
        matchSingleToken(lexer, "assignable",   XQueryTokenType.K_ASSIGNABLE);
        matchSingleToken(lexer, "variable",     XQueryTokenType.K_VARIABLE);
        matchSingleToken(lexer, "$",            XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",           XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, "external",     XQueryTokenType.K_EXTERNAL);
    }

    // endregion
    // region Scripting Extension 1.0 :: FunctionDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Scripting10() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare",  XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION);
        matchSingleToken(lexer, "(",        XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",        XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL);

        matchSingleToken(lexer, "sequential", XQueryTokenType.K_SEQUENTIAL);
        matchSingleToken(lexer, "simple",     XQueryTokenType.K_SIMPLE);
        matchSingleToken(lexer, "updating",   XQueryTokenType.K_UPDATING);
    }

    // endregion
    // region Scripting Extension 1.0 :: ApplyExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ApplyExpr")
    public void testApplyExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR);
    }

    // endregion
    // region Scripting Extension 1.0 :: ConcatExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ConcatExpr")
    public void testConcatExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
    }

    // endregion
    // region Scripting Extension 1.0 :: BlockExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockExpr")
    public void testBlockExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "block", XQueryTokenType.K_BLOCK);
    }

    // endregion
    // region Scripting Extension 1.0 :: Block

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-Block")
    public void testBlock() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region Scripting Extension 1.0 :: BlockDecls

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockDecls")
    public void testBlockDecls() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR);
    }

    // endregion
    // region Scripting Extension 1.0 :: BlockVarDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-BlockVarDecl")
    public void testBlockVarDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE);
        matchSingleToken(lexer, "$",       XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=",      XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, ",",       XQueryTokenType.COMMA);
    }

    // endregion
    // region Scripting Extension 1.0 :: AssignmentExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-AssignmentExpr")
    public void testAssignmentExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$",  XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL);
    }

    // endregion
    // region Scripting Extension 1.0 :: ExitExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-ExitExpr")
    public void testExitExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "exit",      XQueryTokenType.K_EXIT);
        matchSingleToken(lexer, "returning", XQueryTokenType.K_RETURNING);
    }

    // endregion
    // region Scripting Extension 1.0 :: WhileExpr

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-WhileExpr")
    public void testWhileExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "while", XQueryTokenType.K_WHILE);
        matchSingleToken(lexer, "(",     XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",     XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 6.0 :: TransactionSeparator

    public void testTransactionSeparator() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR);
    }

    // endregion
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    public void testCompatibilityAnnotation_MarkLogic() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "private", XQueryTokenType.K_PRIVATE);
    }

    // endregion
    // region MarkLogic 6.0 :: StylesheetImport

    public void testStylesheetImport() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "import",     XQueryTokenType.K_IMPORT);
        matchSingleToken(lexer, "stylesheet", XQueryTokenType.K_STYLESHEET);
        matchSingleToken(lexer, "at",         XQueryTokenType.K_AT);
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    public void testValidateExpr_ValidateAs() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE);
        matchSingleToken(lexer, "as",       XQueryTokenType.K_AS);
        matchSingleToken(lexer, "{",        XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",        XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_MarkLogic() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE);
        matchSingleToken(lexer, "property",  XQueryTokenType.K_PROPERTY);
        matchSingleToken(lexer, "::",        XQueryTokenType.AXIS_SEPARATOR);
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryConstructor

    public void testBinaryConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "binary", XQueryTokenType.K_BINARY);
        matchSingleToken(lexer, "{",      XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",      XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryTest

    public void testBinaryTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "binary", XQueryTokenType.K_BINARY);
        matchSingleToken(lexer, "(",      XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",      XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayConstructor

    public void testArrayConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "array-node", XQueryTokenType.K_ARRAY_NODE);
        matchSingleToken(lexer, "{",          XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, ",",          XQueryTokenType.COMMA);
        matchSingleToken(lexer, "}",          XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanConstructor

    public void testBooleanConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "boolean-node", XQueryTokenType.K_BOOLEAN_NODE);
        matchSingleToken(lexer, "{",            XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",            XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: NullConstructor

    public void testNullConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE);
        matchSingleToken(lexer, "{",         XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",         XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: NumberConstructor

    public void testNumberConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE);
        matchSingleToken(lexer, "{",           XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}",           XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: MapConstructor

    public void testMapConstructor_MarkLogic() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE);
        matchSingleToken(lexer, "{",           XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, ":",           XQueryTokenType.QNAME_SEPARATOR);
        matchSingleToken(lexer, ",",           XQueryTokenType.COMMA);
        matchSingleToken(lexer, "}",           XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    public void testAnyKindTest_MarkLogic() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE);
        matchSingleToken(lexer, "(",    XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, "*",    XQueryTokenType.STAR);
        matchSingleToken(lexer, ")",    XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayTest

    public void testArrayTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "array-node", XQueryTokenType.K_ARRAY_NODE);
        matchSingleToken(lexer, "(",          XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",          XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanTest

    public void testBooleanTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "boolean-node", XQueryTokenType.K_BOOLEAN_NODE);
        matchSingleToken(lexer, "(",            XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",            XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: NullTest

    public void testNullTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE);
        matchSingleToken(lexer, "(",         XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",         XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: NumberTest

    public void testNumberTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE);
        matchSingleToken(lexer, "(",           XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",           XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region MarkLogic 8.0 :: MapTest

    public void testMapTest() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE);
        matchSingleToken(lexer, "(",           XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")",           XQueryTokenType.PARENTHESIS_CLOSE);
    }

    // endregion
    // region BaseX 7.8 :: UpdateExpr

    public void testUpdateExpr() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "update", XQueryTokenType.K_UPDATE);
    }

    // endregion
    // region BaseX 8.5 :: UpdateExpr

    public void testUpdateExpr_BaseX85() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "update", XQueryTokenType.K_UPDATE);
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
    }

    // endregion
}
