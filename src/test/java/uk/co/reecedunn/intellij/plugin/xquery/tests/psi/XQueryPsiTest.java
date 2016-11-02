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

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryNCNamePsiImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.xquery.tests.functional.IsDefined.defined;
import static uk.co.reecedunn.intellij.plugin.xquery.tests.functional.IsDefined.notDefined;

@SuppressWarnings("ConstantConditions")
public class XQueryPsiTest extends ParserTestCase {
    // region XQueryArgumentList

    public void testArgumentList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(functionCallPsi, XQueryArgumentList.class);
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(2));
    }

    public void testArgumentList_Empty() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(functionCallPsi, XQueryArgumentList.class);
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(0));
    }

    public void testArgumentList_ArgumentPlaceholder() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(functionCallPsi, XQueryArgumentList.class);
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(1));
    }

    // endregion
    // region XQueryConformanceCheck
    // region AllowingEmpty

    public void testAllowingEmpty() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq");

        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryAllowingEmpty allowingEmptyPsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryAllowingEmpty.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)allowingEmptyPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region Annotation

    public void testAnnotation() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/Annotation.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryAnnotation annotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryAnnotation.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)annotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region AnyFunctionTest

    public void testAnyFunctionTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryAnyFunctionTest anyFunctionTestPsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryAnyFunctionTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region AnyKindTest

    public void testAnyKindTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryAnyKindTest anyKindTestPsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryAnyKindTest.class);
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
    // region ArgumentList

    public void testArgumentList_FunctionCall() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(functionCallPsi, XQueryArgumentList.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testArgumentList_PostfixExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq");

        XQueryPostfixExpr postfixExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryPostfixExpr.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(postfixExprPsi, XQueryArgumentList.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region ArgumentPlaceholder

    public void testArgumentPlaceholder() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        XQueryArgumentList argumentListPsi = PsiNavigation.findChildByClass(functionCallPsi, XQueryArgumentList.class);
        XQueryArgument argumentPsi = PsiNavigation.findChildByClass(argumentListPsi, XQueryArgument.class);
        XQueryArgumentPlaceholder argumentPlaceholderPsi = PsiNavigation.findDirectDescendantByClass(argumentPsi, XQueryArgumentPlaceholder.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentPlaceholderPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region BracedURILiteral

    public void testBracedURILiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryURIQualifiedName qnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryURIQualifiedName.class);
        XQueryBracedURILiteral bracedURILiteralPsi = PsiNavigation.findDirectDescendantByClass(qnamePsi, XQueryBracedURILiteral.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)bracedURILiteralPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region CatchClause

    public void testCatchClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CatchClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTryCatchExpr.class);
        XQueryCatchClause catchClausePsi = PsiNavigation.findChildByClass(tryCatchExprPsi, XQueryCatchClause.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)catchClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region CompNamespaceConstructor

    public void testCompNamespaceConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq");

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCompNamespaceConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compNamespaceConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_NAMESPACE));
    }

    // endregion
    // region ContextItemDecl

    public void testContextItemDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq");

        XQueryContextItemDecl contextItemDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryContextItemDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)contextItemDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_CONTEXT));
    }

    // endregion
    // region DecimalFormatDecl

    public void testDecimalFormatDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq");

        XQueryDecimalFormatDecl decimalFormatDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDecimalFormatDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)decimalFormatDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_DECIMAL_FORMAT));
    }

    // endregion
    // region ForwardAxis

    public void testForwardAxis_Attribute() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ATTRIBUTE));
    }

    public void testForwardAxis_Child() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_CHILD));
    }

    public void testForwardAxis_Descendant() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT));
    }

    public void testForwardAxis_DescendantOrSelf() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT_OR_SELF));
    }

    public void testForwardAxis_Following() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING));
    }

    public void testForwardAxis_FollowingSibling() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING_SIBLING));
    }

    public void testForwardAxis_Self() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForwardAxis.class);
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
    // region FunctionCall

    public void testFunctionCall_NCName() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/FunctionCall_NCName.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
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
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall_KeywordNCNames_XQuery10.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
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
    // region FunctionDecl

    public void testFunctionDecl_QName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
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
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/FunctionDecl_NCName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
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
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/FunctionDecl_Keyword.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
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
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/FunctionDecl_ReservedKeyword.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
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
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
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

    public void testFunctionDecl_ReservedKeyword_Function() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_Function.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_Switch.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/FunctionDecl_ReservedKeyword_NamespaceNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
    // region InlineFunctionExpr

    public void testInlineFunctionExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq");

        XQueryInlineFunctionExpr inlineFunctionExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryInlineFunctionExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq");

        XQueryInlineFunctionExpr inlineFunctionExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryInlineFunctionExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region TumblingWindowClause

    public void testTumblingWindowClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildrenByClass(windowClausePsi, XQueryTumblingWindowClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)tumblingWindowClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_TUMBLING));
    }

    // endregion
    // region SlidingWindowClause

    public void testSlidingWindowClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildrenByClass(windowClausePsi, XQuerySlidingWindowClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)slidingWindowClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_SLIDING));
    }

    // endregion
    // region IntermediateClause (ForClause)

    public void testForClause_FirstIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testForClause_AfterForIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testForClause_AfterLetIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testForClause_AfterWhereIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testForClause_AfterOrderByIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region IntermediateClause (LetClause)

    public void testLetClause_FirstIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testLetClause_AfterForIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testLetClause_AfterLetIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testLetClause_AfterWhereIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testLetClause_AfterOrderByIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region IntermediateClause (OrderByClause)

    public void testOrderByClause_FirstIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testOrderByClause_AfterForIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testOrderByClause_AfterLetIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testOrderByClause_AfterWhereIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(4).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(4);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testOrderByClause_AfterOrderByIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region IntermediateClause (WhereClause)

    public void testWhereClause_FirstIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testWhereClause_AfterForIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testWhereClause_AfterLetIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testWhereClause_AfterWhereIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testWhereClause_AfterOrderByIntermediateClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region IntermediateClause (CountClause)

    public void testCountClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CountClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryCountClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_COUNT));
    }

    // endregion
    // region IntermediateClause (GroupByClause)

    public void testGroupByClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        // prev == null
        assertThat(PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryGroupByClause.class));

        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildrenByClass(flworExprPsi, XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_GROUP));
    }

    // endregion
    // region NamedFunctionRef

    public void testNamedFunctionRef_QName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_QName.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_Keyword.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_NamespaceNode.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Switch.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));

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
    // region NamespaceNodeTest

    public void testNamespaceNodeTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryNamespaceNodeTest namespaceNodeTestPsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryNamespaceNodeTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namespaceNodeTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_NAMESPACE_NODE));
    }

    // endregion
    // region ParenthesizedItemType

    public void testParenthesizedItemType() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryParenthesizedItemType parenthesizedItemTypePsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryParenthesizedItemType.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)parenthesizedItemTypePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region SequenceTypeUnion

    public void testSequenceTypeUnion() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTypeswitchExpr.class);
        XQueryCaseClause caseClausePsi = PsiNavigation.findChildByClass(typeswitchExprPsi, XQueryCaseClause.class);
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = PsiNavigation.findChildByClass(caseClausePsi, XQuerySequenceTypeUnion.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testSequenceTypeUnion_NoUnion() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTypeswitchExpr.class);
        XQueryCaseClause caseClausePsi = PsiNavigation.findChildByClass(typeswitchExprPsi, XQueryCaseClause.class);
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = PsiNavigation.findChildByClass(caseClausePsi, XQuerySequenceTypeUnion.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region SimpleMapExpr

    public void testSimpleMapExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq");

        XQuerySimpleMapExpr simpleMapExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySimpleMapExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testSimpleMapExpr_NoMap() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");

        XQuerySimpleMapExpr simpleMapExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySimpleMapExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region StringConcatExpr

    public void testStringConcatExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq");

        XQueryStringConcatExpr stringConcatExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryStringConcatExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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

    public void testStringConcatExpr_NoConcatenation() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");

        XQueryStringConcatExpr stringConcatExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryStringConcatExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region SwitchExpr

    public void testSwitchExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq");

        XQuerySwitchExpr switchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySwitchExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)switchExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_SWITCH));
    }

    // endregion
    // region TextTest

    public void testTextTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/TextTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryTextTest textTestPsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryTextTest.class);
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
    // region TryClause

    public void testTryClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CatchClause.xq");

        XQueryTryClause tryClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTryClause.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)tryClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region TypedFunctionTest

    public void testTypedFunctionTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        XQueryTypedFunctionTest typedFunctionTestPsi = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryTypedFunctionTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)typedFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region ValidateExpr

    public void testValidateExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq");

        XQueryValidateExpr validateExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryValidateExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_VALIDATE));
    }

    public void testValidateExpr_Type() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq");

        XQueryValidateExpr validateExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryValidateExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
    // region VarDecl

    public void testVarDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_VARIABLE));
    }

    public void testVarDecl_External() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.K_VARIABLE));
    }

    public void testVarDecl_External_DefaultValue() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

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
                is(XQueryTokenType.ASSIGN_EQUAL));
    }

    // endregion
    // endregion
    // region XQueryEQName
    // region EQName

    @SuppressWarnings("RedundantCast")
    public void testEQName_QName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_KeywordLocalPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordLocalPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("sort"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("least"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_MissingLocalPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_MissingLocalPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalNameElement(), is(nullValue()));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_KeywordPrefixPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordPrefixPart.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("order"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("column"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_NCName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_NCName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notDefined()));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_URIQualifiedName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_URIQualifiedName.xq");

        XQueryCastExpr castExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryCastExpr.class);
        XQuerySingleType singleTypePsi = PsiNavigation.findChildByClass(castExprPsi, XQuerySingleType.class);
        XQuerySimpleTypeName simpleTypeNamePsi = PsiNavigation.findDirectDescendantByClass(singleTypePsi, XQuerySimpleTypeName.class);
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("Q{http://www.w3.org/2001/XMLSchema}"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("double"));
    }

    // endregion
    // region NCName

    public void testNCName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(notDefined()));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryTokenType.K_COLLATION));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("collation"));
    }

    // endregion
    // region QName

    public void testQName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("one"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("two"));
    }

    public void testQName_KeywordLocalPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("sort"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("least"));
    }

    public void testQName_MissingLocalPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("one"));

        assertThat(eqnamePsi.getLocalNameElement(), is(nullValue()));
    }

    public void testQName_KeywordPrefixPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("order"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("two"));
    }

    public void testQName_DirElemConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("a"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("b"));
    }

    public void testQName_DirAttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryEQName eqnamePsi = PsiNavigation.findChildByClass(dirAttributeListPsi, XQueryEQName.class);

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xml"));

        assertThat(eqnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(eqnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalNameElement().getText(), is("id"));
    }

    // endregion
    // endregion
    // region XQueryFile

    public void testFile_Empty() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseText("");

        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_1);
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_1));
    }

    public void testFile() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_1);
        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_1));
    }

    // endregion
    // region XQueryFunctionCall

    public void testFunctionCall() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(2));
    }

    public void testFunctionCall_Empty() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(0));
    }

    public void testFunctionCall_ArgumentPlaceholder() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFunctionCall.class);
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(1));
    }

    // endregion
    // region XQueryFunctionDecl

    public void testFunctionDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        assertThat(functionDeclPsi, is(notNullValue()));
        assertThat(functionDeclPsi.getArity(), is(0));
    }

    public void testFunctionDecl_ParamList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ParamList.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        assertThat(functionDeclPsi, is(notNullValue()));
        assertThat(functionDeclPsi.getArity(), is(2));
    }

    // endregion
    // region XQueryIntegerLiteral

    public void testIntegerLiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        XQueryIntegerLiteral integerLiteralPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryIntegerLiteral.class);
        assertThat(integerLiteralPsi, is(notNullValue()));
        assertThat(integerLiteralPsi.getAtomicValue(), is(1234));
    }

    // endregion
    // region XQueryPrologResolver
    // region Module

    public void testModule_ModuleProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModule modulePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModule.class);

        XQueryPrologResolver provider = (XQueryPrologResolver)modulePsi;
        assertThat(provider.resolveProlog(), is(notDefined()));
    }

    // endregion
    // region ModuleDecl

    public void testModuleDecl_ModuleProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModuleDecl moduleDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleDecl.class);

        XQueryPrologResolver provider = (XQueryPrologResolver)moduleDeclPsi;
        assertThat(provider.resolveProlog(), is(notDefined()));
    }

    // endregion
    // endregion
    // region XQueryNamedFunctionRef

    public void testNamedFunctionRef() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        assertThat(namedFunctionRefPsi, is(notNullValue()));
        assertThat(namedFunctionRefPsi.getArity(), is(3));
    }

    public void testNamedFunctionRef_MissingArity() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamedFunctionRef.class);
        assertThat(namedFunctionRefPsi, is(notNullValue()));
        assertThat(namedFunctionRefPsi.getArity(), is(0));
    }

    // endregion
    // region XQueryNamespaceResolver
    // region DirAttributeList

    public void testDirAttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirAttributeListPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirAttributeList_XmlNamespace() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirAttributeListPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryDirAttributeValue.class)));
        assertThat(ns.get().getUri().getText(), is("\"http://www.example.com/a\""));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    public void testDirAttributeList_XmlNamespace_MissingValue() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingValue.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirAttributeListPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    public void testDirAttributeList_XmlNamespace_MissingMiddleValue() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingMiddleValue.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirAttributeListPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    // endregion
    // region DirElemConstructor

    public void testDirElemConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirElemConstructor_AttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirElemConstructor_XmlNamespace() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryDirAttributeValue.class)));
        assertThat(ns.get().getUri().getText(), is("\"http://www.example.com/a\""));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    public void testDirElemConstructor_XmlNamespace_MissingValue() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingValue.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    public void testDirElemConstructor_XmlNamespace_MissingMiddleValue() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingMiddleValue.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryDirElemConstructor.class);
        XQueryDirAttributeList dirAttributeListPsi = PsiNavigation.findChildByClass(dirElemConstructorPsi, XQueryDirAttributeList.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("a");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(XQueryNCNamePsiImpl.class)));
        assertThat(ns.get().getPrefix().getText(), is("a"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryDirAttributeList.class)));
        assertThat(ns.get().getDeclaration(), is(dirAttributeListPsi));
    }

    // endregion
    // region Module

    public void testModule() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModule modulePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModule.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)modulePsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("local");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(nullValue()));
        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryModule.class)));
        assertThat(ns.get().getDeclaration(), is(modulePsi));
    }

    // endregion
    // region ModuleDecl

    public void testModuleDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModuleDecl moduleDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryUriLiteral.class)));
        assertThat(((XQueryUriLiteral)ns.get().getUri()).getAtomicValue(), is("http://www.example.com/test"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryModuleDecl.class)));
        assertThat(ns.get().getDeclaration(), is(moduleDeclPsi));
    }

    public void testModuleDecl_MissingNamespaceName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.xq");

        XQueryModuleDecl moduleDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testModulesDecl_MissingNamespaceUri() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq");

        XQueryModuleDecl moduleDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("one");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("one"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryModuleDecl.class)));
        assertThat(ns.get().getDeclaration(), is(moduleDeclPsi));
    }

    // endregion
    // region ModuleImport

    public void testModuleImport() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleImport.xq");

        XQueryModuleImport moduleImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testModuleImport_WithNamespace() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq");

        XQueryModuleImport moduleImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryModuleImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryUriLiteral.class)));
        assertThat(((XQueryUriLiteral)ns.get().getUri()).getAtomicValue(), is("http://www.example.com/test"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryModuleImport.class)));
        assertThat(ns.get().getDeclaration(), is(moduleImportPsi));
    }

    // endregion
    // region NamespaceDecl

    public void testNamespaceDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq");

        XQueryNamespaceDecl namespaceDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamespaceDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)namespaceDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryUriLiteral.class)));
        assertThat(((XQueryUriLiteral)ns.get().getUri()).getAtomicValue(), is("http://www.example.org/test"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryNamespaceDecl.class)));
        assertThat(ns.get().getDeclaration(), is(namespaceDeclPsi));
    }

    public void testNamespaceDecl_MissingNCName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.xq");

        XQueryNamespaceDecl namespaceDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamespaceDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)namespaceDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testNamespaceDecl_MissingUri() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq");

        XQueryNamespaceDecl namespaceDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamespaceDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)namespaceDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(nullValue()));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryNamespaceDecl.class)));
        assertThat(ns.get().getDeclaration(), is(namespaceDeclPsi));
    }

    // endregion
    // region Prolog

    public void testProlog_NoNamespaceProviders() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryProlog prologPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryProlog.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)prologPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testProlog_NamespaceDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq");

        XQueryProlog prologPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryProlog.class);
        XQueryNamespaceDecl namespaceDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryNamespaceDecl.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)prologPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryUriLiteral.class)));
        assertThat(((XQueryUriLiteral)ns.get().getUri()).getAtomicValue(), is("http://www.example.org/test"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryNamespaceDecl.class)));
        assertThat(ns.get().getDeclaration(), is(namespaceDeclPsi));
    }

    // endregion
    // region SchemaImport

    public void testSchemaImport() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaImport.xq");

        XQuerySchemaImport schemaImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySchemaImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testSchemaImport_WithSchemaPrefix() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq");

        XQuerySchemaImport schemaImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySchemaImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("test");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(instanceOf(LeafPsiElement.class)));
        assertThat(ns.get().getPrefix().getText(), is("test"));

        assertThat(ns.get().getUri(), is(instanceOf(XQueryUriLiteral.class)));
        assertThat(((XQueryUriLiteral)ns.get().getUri()).getAtomicValue(), is("http://www.example.com/test"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQuerySchemaImport.class)));
        assertThat(ns.get().getDeclaration(), is(schemaImportPsi));
    }

    public void testSchemaImport_WithSchemaPrefix_MissingNCName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.xq");

        XQuerySchemaImport schemaImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySchemaImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testSchemaImport_WithSchemaPrefix_Default() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq");

        XQuerySchemaImport schemaImportPsi = PsiNavigation.findDirectDescendantByClass(file, XQuerySchemaImport.class);
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    // endregion
    // endregion
    // region XQueryParamList

    public void testParamList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ParamList.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryParamList.class);
        assertThat(paramListPsi, is(notNullValue()));
        assertThat(paramListPsi.getArity(), is(2));
    }

    // endregion
    // region XQueryStringLiteral

    public void testStringLiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/StringLiteral.xq");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getAtomicValue(), is("One Two"));
    }

    public void testStringLiteral_Empty() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/StringLiteral_Empty.xq");

        XQueryStringLiteral stringLiteralPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryStringLiteral.class);
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getAtomicValue(), is(nullValue()));
    }

    // endregion
    // region XQueryURIQualifiedName

    public void testURIQualifiedName() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryOptionDecl.class);
        XQueryURIQualifiedName qnamePsi = PsiNavigation.findChildByClass(optionDeclPsi, XQueryURIQualifiedName.class);

        assertThat(qnamePsi.getPrefix(), is(defined()));
        assertThat(qnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(qnamePsi.getPrefix().get().getText(), is("Q{one{two}"));

        assertThat(qnamePsi.getLocalNameElement(), is(notNullValue()));
        assertThat(qnamePsi.getLocalNameElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(qnamePsi.getLocalNameElement().getText(), is("three"));
    }

    // endregion
    // region XQueryVariableResolver
    // region AnnotatedDecl

    public void testAnnotatedDecl_VarDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryEQName varNamePsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)annotatedDeclPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryVarDecl.class)));
        assertThat(variable.get().getDeclaration(), is(varDeclPsi));
    }

    public void testAnnotatedDecl_FunctionDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryEQName functionNamePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)annotatedDeclPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));
        assertThat(provider.resolveVariable(functionNamePsi), is(notDefined()));
    }

    // endregion
    // region CaseClause

    public void testCaseClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTypeswitchExpr.class);
        XQueryCaseClause caseClausePsi = PsiNavigation.findChildByClass(typeswitchExprPsi, XQueryCaseClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(caseClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)caseClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryCaseClause.class)));
        assertThat(variable.get().getDeclaration(), is(caseClausePsi));
    }

    // endregion
    // region CountClause

    public void testCountClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CountClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryCountClause countClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryCountClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(countClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)intermediateClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryCountClause.class)));
        assertThat(variable.get().getDeclaration(), is(countClausePsi));
    }

    // endregion
    // region ForBinding

    public void testForBinding_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForClause.xq");

        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)forBindingPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryForBinding.class)));
        assertThat(variable.get().getDeclaration(), is(forBindingPsi));
    }

    public void testForBinding_PositionalVar_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/PositionalVar.xq");

        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);

        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);

        XQueryVariableResolver provider = (XQueryVariableResolver)forBindingPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // bound variable

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryForBinding.class)));
        assertThat(variable.get().getDeclaration(), is(forBindingPsi));

        // positional variable

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));
    }

    // endregion
    // region ForClause

    public void testForClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForClause.xq");

        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)forClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryForBinding.class)));
        assertThat(variable.get().getDeclaration(), is(forBindingPsi));
    }

    // endregion
    // region FunctionDecl

    public void testFunctionDecl_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/Param.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryParamList.class);
        XQueryParam paramPsi = PsiNavigation.findChildByClass(paramListPsi, XQueryParam.class);
        XQueryEQName paramNamePsi = PsiNavigation.findChildByClass(paramPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)functionDeclPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(paramNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(paramNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryParam.class)));
        assertThat(variable.get().getDeclaration(), is(paramPsi));
    }

    // endregion
    // region GroupByClause

    public void testGroupByClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryGroupByClause groupByClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryGroupByClause.class);
        XQueryGroupingSpecList groupingSpecListPsi = PsiNavigation.findChildByClass(groupByClausePsi, XQueryGroupingSpecList.class);
        XQueryGroupingSpec groupingSpecPsi = PsiNavigation.findChildByClass(groupingSpecListPsi, XQueryGroupingSpec.class);
        XQueryGroupingVariable groupingVariablePsi = PsiNavigation.findChildByClass(groupingSpecPsi, XQueryGroupingVariable.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(groupingVariablePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)groupByClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryGroupingVariable.class)));
        assertThat(variable.get().getDeclaration(), is(groupingVariablePsi));
    }

    // endregion
    // region GroupingSpec

    public void testGroupingSpec_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryGroupByClause groupByClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryGroupByClause.class);
        XQueryGroupingSpecList groupingSpecListPsi = PsiNavigation.findChildByClass(groupByClausePsi, XQueryGroupingSpecList.class);
        XQueryGroupingSpec groupingSpecPsi = PsiNavigation.findChildByClass(groupingSpecListPsi, XQueryGroupingSpec.class);
        XQueryGroupingVariable groupingVariablePsi = PsiNavigation.findChildByClass(groupingSpecPsi, XQueryGroupingVariable.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(groupingVariablePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)groupingSpecPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryGroupingVariable.class)));
        assertThat(variable.get().getDeclaration(), is(groupingVariablePsi));
    }

    // endregion
    // region GroupingSpecList

    public void testGroupingSpecList_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryGroupByClause groupByClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryGroupByClause.class);
        XQueryGroupingSpecList groupingSpecListPsi = PsiNavigation.findChildByClass(groupByClausePsi, XQueryGroupingSpecList.class);
        XQueryGroupingSpec groupingSpecPsi = PsiNavigation.findChildByClass(groupingSpecListPsi, XQueryGroupingSpec.class);
        XQueryGroupingVariable groupingVariablePsi = PsiNavigation.findChildByClass(groupingSpecPsi, XQueryGroupingVariable.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(groupingVariablePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)groupingSpecListPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryGroupingVariable.class)));
        assertThat(variable.get().getDeclaration(), is(groupingVariablePsi));
    }

    // endregion
    // region GroupingVariable

    public void testGroupingVariable_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryGroupByClause groupByClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryGroupByClause.class);
        XQueryGroupingSpecList groupingSpecListPsi = PsiNavigation.findChildByClass(groupByClausePsi, XQueryGroupingSpecList.class);
        XQueryGroupingSpec groupingSpecPsi = PsiNavigation.findChildByClass(groupingSpecListPsi, XQueryGroupingSpec.class);
        XQueryGroupingVariable groupingVariablePsi = PsiNavigation.findChildByClass(groupingSpecPsi, XQueryGroupingVariable.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(groupingVariablePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)groupingVariablePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryGroupingVariable.class)));
        assertThat(variable.get().getDeclaration(), is(groupingVariablePsi));
    }

    // endregion
    // region IntermediateClause

    public void testIntermediateClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq");

        XQueryFLWORExpr flworExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(intermediateClausePsi, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)intermediateClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryForBinding.class)));
        assertThat(variable.get().getDeclaration(), is(forBindingPsi));
    }

    // endregion
    // region LetBinding

    public void testLetBinding_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/LetClause.xq");

        XQueryLetClause letClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryLetClause.class);
        XQueryLetBinding letBindingPsi = PsiNavigation.findChildByClass(letClausePsi, XQueryLetBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(letBindingPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)letBindingPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryLetBinding.class)));
        assertThat(variable.get().getDeclaration(), is(letBindingPsi));
    }

    // endregion
    // region LetClause

    public void testLetClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/LetClause.xq");

        XQueryLetClause letClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryLetClause.class);
        XQueryLetBinding letBindingPsi = PsiNavigation.findChildByClass(letClausePsi, XQueryLetBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(letBindingPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)letClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryLetBinding.class)));
        assertThat(variable.get().getDeclaration(), is(letBindingPsi));
    }

    // endregion
    // region Param

    public void testParam_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/Param.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryParamList.class);
        XQueryParam paramPsi = PsiNavigation.findChildByClass(paramListPsi, XQueryParam.class);
        XQueryEQName paramNamePsi = PsiNavigation.findChildByClass(paramPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)paramPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(paramNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(paramNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryParam.class)));
        assertThat(variable.get().getDeclaration(), is(paramPsi));
    }

    // endregion
    // region ParamList

    public void testParamList_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/Param.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryParamList.class);
        XQueryParam paramPsi = PsiNavigation.findChildByClass(paramListPsi, XQueryParam.class);
        XQueryEQName paramNamePsi = PsiNavigation.findChildByClass(paramPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)paramListPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(paramNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(paramNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryParam.class)));
        assertThat(variable.get().getDeclaration(), is(paramPsi));
    }

    // endregion
    // region PositionalVar

    public void testPositionalVar_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/PositionalVar.xq");

        XQueryForClause forClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryForClause.class);
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryPositionalVar.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)positionalVarPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));
    }

    // endregion
    // region Prolog

    public void testProlog_VarDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryEQName varNamePsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryEQName.class);

        XQueryProlog prologPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryProlog.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)prologPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryVarDecl.class)));
        assertThat(variable.get().getDeclaration(), is(varDeclPsi));
    }

    public void testProlog_FunctionDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryEQName functionNamePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryEQName.class);

        XQueryProlog prologPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryProlog.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)prologPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));
        assertThat(provider.resolveVariable(functionNamePsi), is(notDefined()));
    }

    // endregion
    // region QuantifiedExpr

    public void testQuantifiedExpr_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq");

        XQueryQuantifiedExpr quantifiedExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryQuantifiedExpr.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(quantifiedExprPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)quantifiedExprPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryQuantifiedExpr.class)));
        assertThat(variable.get().getDeclaration(), is(quantifiedExprPsi));
    }

    // endregion
    // region SlidingWindowClause

    public void testSlidingWindowClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQuerySlidingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(slidingWindowClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)slidingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQuerySlidingWindowClause.class)));
        assertThat(variable.get().getDeclaration(), is(slidingWindowClausePsi));
    }

    public void testSlidingWindowClause_WindowEndCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SlidingWindowClause_EndCondition_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQuerySlidingWindowClause.class);
        XQueryWindowEndCondition windowEndConditionPsi = PsiNavigation.findChildByClass(slidingWindowClausePsi, XQueryWindowEndCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowEndConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)slidingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    public void testSlidingWindowClause_WindowStartCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SlidingWindowClause_StartCondition_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQuerySlidingWindowClause.class);
        XQueryWindowStartCondition windowStartConditionPsi = PsiNavigation.findChildByClass(slidingWindowClausePsi, XQueryWindowStartCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowStartConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)slidingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    // endregion
    // region TumblingWindowClause

    public void testTumblingWindowClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)tumblingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryTumblingWindowClause.class)));
        assertThat(variable.get().getDeclaration(), is(tumblingWindowClausePsi));
    }

    public void testTumblingWindowClause_WindowEndCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/TumblingWindowClause_EndCondition_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryWindowEndCondition windowEndConditionPsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryWindowEndCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowEndConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)tumblingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    public void testTumblingWindowClause_WindowStartCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryWindowStartCondition windowStartConditionPsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryWindowStartCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowStartConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)tumblingWindowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    // endregion
    // region TypeswitchExpr

    public void testTypeswitchExpr_Default_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryTypeswitchExpr.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(typeswitchExprPsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)typeswitchExprPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryTypeswitchExpr.class)));
        assertThat(variable.get().getDeclaration(), is(typeswitchExprPsi));
    }

    // endregion
    // region VarDecl

    public void testVarDecl_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryEQName varNamePsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryEQName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)varDeclPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryEQName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryVarDecl.class)));
        assertThat(variable.get().getDeclaration(), is(varDeclPsi));
    }

    // endregion
    // region WindowClause

    public void testWindowClause_SlidingWindowClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQuerySlidingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(slidingWindowClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)windowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQuerySlidingWindowClause.class)));
        assertThat(variable.get().getDeclaration(), is(slidingWindowClausePsi));
    }

    public void testWindowClause_TumblingWindowClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryVarName.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)windowClausePsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        Option<XQueryVariable> variable = provider.resolveVariable(varNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(varNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryTumblingWindowClause.class)));
        assertThat(variable.get().getDeclaration(), is(tumblingWindowClausePsi));
    }

    // endregion
    // region WindowEndCondition

    public void testWindowEndCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/TumblingWindowClause_EndCondition_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryWindowEndCondition windowEndConditionPsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryWindowEndCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowEndConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)windowEndConditionPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    // endregion
    // region WindowStartCondition

    public void testWindowStartCondition_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryWindowStartCondition windowStartConditionPsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryWindowStartCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowStartConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)windowStartConditionPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    // endregion
    // region WindowVars

    public void testWindowVars_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq");

        XQueryWindowClause windowClausePsi = PsiNavigation.findDirectDescendantByClass(file, XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryWindowStartCondition windowStartConditionPsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryWindowStartCondition.class);
        XQueryWindowVars windowVarsPsi = PsiNavigation.findChildByClass(windowStartConditionPsi, XQueryWindowVars.class);
        XQueryCurrentItem currentItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryCurrentItem.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPositionalVar.class);
        XQueryVarName posVarNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);
        XQueryPreviousItem previousItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryPreviousItem.class);
        XQueryNextItem nextItemPsi = PsiNavigation.findChildByClass(windowVarsPsi, XQueryNextItem.class);
        XQueryVariableResolver provider = (XQueryVariableResolver)windowVarsPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));

        // current

        Option<XQueryVariable> variable = provider.resolveVariable(currentItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryCurrentItem.class)));
        assertThat(variable.get().getVariable(), is(currentItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // positional

        variable = provider.resolveVariable(posVarNamePsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryVarName.class)));
        assertThat(variable.get().getVariable(), is(posVarNamePsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryPositionalVar.class)));
        assertThat(variable.get().getDeclaration(), is(positionalVarPsi));

        // previous

        variable = provider.resolveVariable(previousItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryPreviousItem.class)));
        assertThat(variable.get().getVariable(), is(previousItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));

        // next

        variable = provider.resolveVariable(nextItemPsi);
        assertThat(variable, is(defined()));

        assertThat(variable.get().getVariable(), is(instanceOf(XQueryNextItem.class)));
        assertThat(variable.get().getVariable(), is(nextItemPsi));

        assertThat(variable.get().getDeclaration(), is(instanceOf(XQueryWindowVars.class)));
        assertThat(variable.get().getDeclaration(), is(windowVarsPsi));
    }

    // endregion
    // endregion
    // region XQueryVersionDecl

    public void testVersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VersionDecl.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

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

    public void testVersionDecl_CommentBeforeDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_CommentBeforeDecl.xq");

        XQueryModule modulePsi = PsiNavigation.findChildByClass(file, XQueryModule.class);
        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(modulePsi, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

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

    public void testVersionDecl_EmptyVersion() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_EmptyVersion.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

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

    public void testVersionDecl_WithEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getAtomicValue(), is("latin1"));

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

    public void testVersionDecl_WithEncoding_CommentsAsWhitespace() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_CommentsAsWhitespace.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getAtomicValue(), is("latin1"));

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

    public void testVersionDecl_WithEmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_EmptyEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("1.0"));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getAtomicValue(), is(nullValue()));

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

    public void testVersionDecl_NoVersion() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_NoVersion.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

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

    public void testVersionDecl_EncodingOnly() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getAtomicValue(), is("latin1"));

        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ENCODING));
    }

    public void testVersionDecl_EncodingOnly_EmptyEncoding() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/VersionDecl_EncodingOnly_EmptyEncoding.xq");

        XQueryVersionDecl versionDeclPsi = PsiNavigation.findDirectDescendantByClass(file, XQueryVersionDecl.class);
        assertThat(versionDeclPsi.getVersion(), is(nullValue()));
        assertThat(versionDeclPsi.getEncoding(), is(notNullValue()));
        assertThat(versionDeclPsi.getEncoding().getAtomicValue(), is(nullValue()));

        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_3_0));

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)versionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ENCODING));
    }

    // endregion
}
