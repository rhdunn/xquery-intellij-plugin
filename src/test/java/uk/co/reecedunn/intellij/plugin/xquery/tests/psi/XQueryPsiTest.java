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
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryNCNamePsiImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.descendants;
import static uk.co.reecedunn.intellij.plugin.core.tests.functional.IsDefined.defined;
import static uk.co.reecedunn.intellij.plugin.core.tests.functional.IsDefined.notDefined;

@SuppressWarnings("ConstantConditions")
public class XQueryPsiTest extends ParserTestCase {
    // region XQueryArgumentList

    public void testArgumentList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryArgumentList argumentListPsi = children(functionCallPsi).findFirst(XQueryArgumentList.class).get();
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(2));
    }

    public void testArgumentList_Empty() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryArgumentList argumentListPsi = children(functionCallPsi).findFirst(XQueryArgumentList.class).get();
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(0));
    }

    public void testArgumentList_ArgumentPlaceholder() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryArgumentList argumentListPsi = children(functionCallPsi).findFirst(XQueryArgumentList.class).get();
        assertThat(argumentListPsi, is(notNullValue()));
        assertThat(argumentListPsi.getArity(), is(1));
    }

    // endregion
    // region XQueryArrowFunctionSpecifier

    public void testArrowFunctionSpecifier() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.1/ArrowExpr_MultipleArguments.xq");

        XQueryArrowExpr arrowExprPsi = descendants(file).findFirst(XQueryArrowExpr.class).get();
        XQueryArrowFunctionSpecifier arrowFunctionSpecifierPsi = children(arrowExprPsi).findFirst(XQueryArrowFunctionSpecifier.class).get();
        assertThat(arrowFunctionSpecifierPsi.getArity(), is(4));
    }

    public void testArrowFunctionSpecifier_Empty() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq");

        XQueryArrowExpr arrowExprPsi = descendants(file).findFirst(XQueryArrowExpr.class).get();
        XQueryArrowFunctionSpecifier arrowFunctionSpecifierPsi = children(arrowExprPsi).findFirst(XQueryArrowFunctionSpecifier.class).get();
        assertThat(arrowFunctionSpecifierPsi.getArity(), is(1));
    }

    public void testArrowFunctionSpecifier_MissingArgumentList() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq");

        XQueryArrowExpr arrowExprPsi = descendants(file).findFirst(XQueryArrowExpr.class).get();
        XQueryArrowFunctionSpecifier arrowFunctionSpecifierPsi = children(arrowExprPsi).findFirst(XQueryArrowFunctionSpecifier.class).get();
        assertThat(arrowFunctionSpecifierPsi.getArity(), is(1));
    }

    // endregion
    // region XQueryConformanceCheck
    // region AllowingEmpty

    public void testAllowingEmpty() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq");

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryAllowingEmpty allowingEmptyPsi = children(forBindingPsi).findFirst(XQueryAllowingEmpty.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)allowingEmptyPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryAnnotation annotationPsi = children(annotatedDeclPsi).findFirst(XQueryAnnotation.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)annotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region AnyArrayTest

    public void testAnyArrayTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryAnyArrayTest anyArrayTestPsi = descendants(sequenceTypePsi).findFirst(XQueryAnyArrayTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyArrayTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ARRAY));
    }

    // endregion
    // region AnyFunctionTest

    public void testAnyFunctionTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryAnyFunctionTest anyFunctionTestPsi = descendants(sequenceTypePsi).findFirst(XQueryAnyFunctionTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryAnyKindTest anyKindTestPsi = descendants(sequenceTypePsi).findFirst(XQueryAnyKindTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region AnyMapTest

    public void testAnyMapTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryAnyMapTest anyMapTestPsi = descendants(sequenceTypePsi).findFirst(XQueryAnyMapTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyMapTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later, or Saxon 9.4 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_MAP));
    }

    // endregion
    // region ArgumentList

    public void testArgumentList_FunctionCall() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryArgumentList argumentListPsi = children(functionCallPsi).findFirst(XQueryArgumentList.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryPostfixExpr postfixExprPsi = descendants(file).findFirst(XQueryPostfixExpr.class).get();
        XQueryArgumentList argumentListPsi = children(postfixExprPsi).findFirst(XQueryArgumentList.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentListPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryArgumentList argumentListPsi = children(functionCallPsi).findFirst(XQueryArgumentList.class).get();
        XQueryArgument argumentPsi = children(argumentListPsi).findFirst(XQueryArgument.class).get();
        XQueryArgumentPlaceholder argumentPlaceholderPsi = descendants(argumentPsi).findFirst(XQueryArgumentPlaceholder.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)argumentPlaceholderPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region ArrowExpr

    public void testArrowExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq");

        XQueryArrowExpr arrowExprPsi = descendants(file).findFirst(XQueryArrowExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)arrowExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v9/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v9/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later, or MarkLogic 9.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.ARROW));
    }

    public void testArrowExpr_NoMap() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq");

        XQueryArrowExpr arrowExprPsi = descendants(file).findFirst(XQueryArrowExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)arrowExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v9/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v9/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later, or MarkLogic 9.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.UNARY_EXPR));
    }

    // endregion
    // region BracedURILiteral

    public void testBracedURILiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryURIQualifiedName qnamePsi = children(optionDeclPsi).findFirst(XQueryURIQualifiedName.class).get();
        XQueryBracedURILiteral bracedURILiteralPsi = descendants(qnamePsi).findFirst(XQueryBracedURILiteral.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)bracedURILiteralPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryTryCatchExpr tryCatchExprPsi = descendants(file).findFirst(XQueryTryCatchExpr.class).get();
        XQueryCatchClause catchClausePsi = children(tryCatchExprPsi).findFirst(XQueryCatchClause.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)catchClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = descendants(file).findFirst(XQueryCompNamespaceConstructor.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compNamespaceConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryContextItemDecl contextItemDeclPsi = descendants(file).findFirst(XQueryContextItemDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)contextItemDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region SquareArrayConstructor

    public void testSquareArrayConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq");

        XQuerySquareArrayConstructor squareArrayConstructorPsi = descendants(file).findFirst(XQuerySquareArrayConstructor.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)squareArrayConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.SQUARE_OPEN));
    }

    // endregion
    // region CurlyArrayConstructor

    public void testCurlyArrayConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq");

        XQueryCurlyArrayConstructor curlyArrayConstructorPsi = descendants(file).findFirst(XQueryCurlyArrayConstructor.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)curlyArrayConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ARRAY));
    }

    // endregion
    // region DecimalFormatDecl

    public void testDecimalFormatDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq");

        XQueryDecimalFormatDecl decimalFormatDeclPsi = descendants(file).findFirst(XQueryDecimalFormatDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)decimalFormatDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

    public void testDecimalFormatDecl_XQuery30Properties() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq");

        XQueryDecimalFormatDecl decimalFormatDeclPsi = descendants(file).findFirst(XQueryDecimalFormatDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)decimalFormatDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

    public void testDecimalFormatDecl_XQuery31Properties() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq");

        XQueryDecimalFormatDecl decimalFormatDeclPsi = descendants(file).findFirst(XQueryDecimalFormatDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)decimalFormatDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.DF_PROPERTY_NAME));
        assertThat(versioned.getConformanceElement().getFirstChild().getNode().getElementType(),
                is(XQueryTokenType.K_EXPONENT_SEPARATOR));
    }

    // endregion
    // region EnclosedExpr (CatchClause)

    public void testEnclosedExpr_CatchClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CatchClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = descendants(file).findFirst(XQueryTryCatchExpr.class).get();
        XQueryCatchClause catchClausePsi = children(tryCatchExprPsi).findFirst(XQueryCatchClause.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(catchClausePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CatchClause_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq");

        XQueryTryCatchExpr tryCatchExprPsi = descendants(file).findFirst(XQueryTryCatchExpr.class).get();
        XQueryCatchClause catchClausePsi = children(tryCatchExprPsi).findFirst(XQueryCatchClause.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(catchClausePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompAttrConstructor)

    public void testEnclosedExpr_CompAttrConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq");

        XQueryCompAttrConstructor compAttrConstructorPsi = descendants(file).findFirst(XQueryCompAttrConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compAttrConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompAttrConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq");

        XQueryCompAttrConstructor compAttrConstructorPsi = descendants(file).findFirst(XQueryCompAttrConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compAttrConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompCommentConstructor)

    public void testEnclosedExpr_CompCommentConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq");

        XQueryCompCommentConstructor compCommentConstructorPsi = descendants(file).findFirst(XQueryCompCommentConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compCommentConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompCommentConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq");

        XQueryCompCommentConstructor compCommentConstructorPsi = descendants(file).findFirst(XQueryCompCommentConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compCommentConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompDocConstructor)

    public void testEnclosedExpr_CompDocConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq");

        XQueryCompDocConstructor compDocConstructorPsi = descendants(file).findFirst(XQueryCompDocConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compDocConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompDocConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq");

        XQueryCompDocConstructor compDocConstructorPsi = descendants(file).findFirst(XQueryCompDocConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compDocConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompElemConstructor)

    public void testEnclosedExpr_CompElemConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq");

        XQueryCompElemConstructor compElemConstructorPsi = descendants(file).findFirst(XQueryCompElemConstructor.class).get();
        XQueryEnclosedContentExpr enclosedContentExprPsi = children(compElemConstructorPsi).findFirst(XQueryEnclosedContentExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedContentExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompElemConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq");

        XQueryCompElemConstructor compElemConstructorPsi = descendants(file).findFirst(XQueryCompElemConstructor.class).get();
        XQueryEnclosedContentExpr enclosedContentExprPsi = children(compElemConstructorPsi).findFirst(XQueryEnclosedContentExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedContentExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedPrefixExpr)

    public void testEnclosedExpr_CompNamespaceConstructor_PrefixExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq");

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = descendants(file).findFirst(XQueryCompNamespaceConstructor.class).get();
        XQueryEnclosedPrefixExpr enclosedExprPsi = children(compNamespaceConstructorPsi).findFirst(XQueryEnclosedPrefixExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompNamespaceConstructor_NoPrefixExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq");

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = descendants(file).findFirst(XQueryCompNamespaceConstructor.class).get();
        XQueryEnclosedPrefixExpr enclosedExprPsi = children(compNamespaceConstructorPsi).findFirst(XQueryEnclosedPrefixExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedURIExpr)

    public void testEnclosedExpr_CompNamespaceConstructor_UriExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq");

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = descendants(file).findFirst(XQueryCompNamespaceConstructor.class).get();
        XQueryEnclosedUriExpr enclosedExprPsi = children(compNamespaceConstructorPsi).findFirst(XQueryEnclosedUriExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompNamespaceConstructor_NoUriExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq");

        XQueryCompNamespaceConstructor compNamespaceConstructorPsi = descendants(file).findFirst(XQueryCompNamespaceConstructor.class).get();
        XQueryEnclosedUriExpr enclosedExprPsi = children(compNamespaceConstructorPsi).findFirst(XQueryEnclosedUriExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompPIConstructor)

    public void testEnclosedExpr_CompPIConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq");

        XQueryCompPIConstructor compPIConstructorPsi = descendants(file).findFirst(XQueryCompPIConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compPIConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompPIConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq");

        XQueryCompPIConstructor compPIConstructorPsi = descendants(file).findFirst(XQueryCompPIConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compPIConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CompTextConstructor)

    public void testEnclosedExpr_CompTextConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq");

        XQueryCompTextConstructor compTextConstructorPsi = descendants(file).findFirst(XQueryCompTextConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compTextConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CompTextConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq");

        XQueryCompTextConstructor compTextConstructorPsi = descendants(file).findFirst(XQueryCompTextConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(compTextConstructorPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (CurlyArrayConstructor)

    public void testEnclosedExpr_CurlyArrayConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq");

        XQueryCurlyArrayConstructor curlyArrayConstructor = descendants(file).findFirst(XQueryCurlyArrayConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(curlyArrayConstructor).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_CurlyArrayConstructor_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq");

        XQueryCurlyArrayConstructor curlyArrayConstructor = descendants(file).findFirst(XQueryCurlyArrayConstructor.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(curlyArrayConstructor).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (DirAttributeValue)

    public void testEnclosedExpr_DirAttributeValue() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
        XQueryDirAttributeValue dirAttributeValuePsi = children(dirAttributeListPsi).findFirst(XQueryDirAttributeValue.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(dirAttributeValuePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_DirAttributeValue_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
        XQueryDirAttributeValue dirAttributeValuePsi = children(dirAttributeListPsi).findFirst(XQueryDirAttributeValue.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(dirAttributeValuePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (DirElemContent)

    public void testEnclosedExpr_DirElemContent() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirElemContent dirElemContentPsi = children(dirElemConstructorPsi).findFirst(XQueryDirElemContent.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(dirElemContentPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_DirElemContent_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirElemContent dirElemContentPsi = children(dirElemConstructorPsi).findFirst(XQueryDirElemContent.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(dirElemContentPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (FunctionDecl)

    public void testEnclosedExpr_FunctionDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(functionDeclPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_FunctionDecl_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(functionDeclPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (OrderedExpr)

    public void testEnclosedExpr_OrderedExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq");

        XQueryOrderedExpr orderedExprPsi = descendants(file).findFirst(XQueryOrderedExpr.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(orderedExprPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_OrderedExpr_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq");

        XQueryOrderedExpr orderedExprPsi = descendants(file).findFirst(XQueryOrderedExpr.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(orderedExprPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (TryClause)

    public void testEnclosedExpr_TryClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TryClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = descendants(file).findFirst(XQueryTryCatchExpr.class).get();
        XQueryTryClause tryClausePsi = children(tryCatchExprPsi).findFirst(XQueryTryClause.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(tryClausePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_TryClause_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq");

        XQueryTryCatchExpr tryCatchExprPsi = descendants(file).findFirst(XQueryTryCatchExpr.class).get();
        XQueryTryClause tryClausePsi = children(tryCatchExprPsi).findFirst(XQueryTryClause.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(tryClausePsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region EnclosedExpr (UnorderedExpr)

    public void testEnclosedExpr_UnorderedExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq");

        XQueryUnorderedExpr unorderedExprPsi = descendants(file).findFirst(XQueryUnorderedExpr.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(unorderedExprPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.EXPR));
    }

    public void testEnclosedExpr_UnorderedExpr_NoExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq");

        XQueryUnorderedExpr unorderedExprPsi = descendants(file).findFirst(XQueryUnorderedExpr.class).get();
        XQueryEnclosedExpr enclosedExprPsi = children(unorderedExprPsi).findFirst(XQueryEnclosedExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)enclosedExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Empty enclosed expressions requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.BLOCK_OPEN));
    }

    // endregion
    // region ForwardAxis

    public void testForwardAxis_Attribute() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ATTRIBUTE));
    }

    public void testForwardAxis_Child() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_CHILD));
    }

    public void testForwardAxis_Descendant() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT));
    }

    public void testForwardAxis_DescendantOrSelf() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DESCENDANT_OR_SELF));
    }

    public void testForwardAxis_Following() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING));
    }

    public void testForwardAxis_FollowingSibling() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_FOLLOWING_SIBLING));
    }

    public void testForwardAxis_Self() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq");

        XQueryForwardAxis forwardAxisPsi = descendants(file).findFirst(XQueryForwardAxis.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

    public void testFunctionCall_ReservedKeyword_While() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq");

        XQueryFunctionCall functionCall = descendants(file).findFirst(XQueryFunctionCall.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCall;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved Scripting Extension 1.0 keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // region FunctionDecl

    public void testFunctionDecl_QName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

    public void testFunctionDecl_ReservedKeyword_While() {
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
                is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // region InlineFunctionExpr

    public void testInlineFunctionExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq");

        XQueryInlineFunctionExpr inlineFunctionExprPsi = descendants(file).findFirst(XQueryInlineFunctionExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryInlineFunctionExpr inlineFunctionExprPsi = descendants(file).findFirst(XQueryInlineFunctionExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)inlineFunctionExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)tumblingWindowClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)slidingWindowClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryForClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(4).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(4);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryForClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryLetClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(3).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(3);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryWhereClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(1).getFirstChild(),
                instanceOf(XQueryOrderByClause.class));
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2).getFirstChild(),
                instanceOf(XQueryWhereClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(2);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryCountClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        // prev == null
        assertThat(children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0).getFirstChild(),
                instanceOf(XQueryGroupByClause.class));

        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).toListOf(XQueryIntermediateClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)intermediateClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region UnaryLookup

    public void testLookup() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/Lookup.xq");

        XQueryPostfixExpr postfixExprPsi = descendants(file).findFirst(XQueryPostfixExpr.class).get();
        XQueryLookup lookupPsi = children(postfixExprPsi).findFirst(XQueryLookup.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)lookupPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.OPTIONAL));
    }

    // endregion
    // region MapConstructor

    public void testMapConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/MapConstructor.xq");

        XQueryMapConstructor objectConstructorPsi = descendants(file).findFirst(XQueryMapConstructor.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)objectConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later, or Saxon 9.4 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_MAP));
    }

    // endregion
    // region MapConstructorEntry

    public void testMapConstructorEntry() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq");

        XQueryMapConstructor mapConstructorPsi = descendants(file).findFirst(XQueryMapConstructor.class).get();
        XQueryMapConstructorEntry mapConstructorEntryPsi = children(mapConstructorPsi).findFirst(XQueryMapConstructorEntry.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)mapConstructorEntryPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Use ':=' for Saxon 9.4 to 9.6, and ':' for XQuery 3.1 and MarkLogic."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.QNAME_SEPARATOR));
    }

    public void testMapConstructorEntry_NoValueAssignmentOperator() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq");

        XQueryMapConstructor mapConstructorPsi = descendants(file).findFirst(XQueryMapConstructor.class).get();
        XQueryMapConstructorEntry mapConstructorEntryPsi = children(mapConstructorPsi).findFirst(XQueryMapConstructorEntry.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)mapConstructorEntryPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Use ':=' for Saxon 9.4 to 9.6, and ':' for XQuery 3.1 and MarkLogic."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.MAP_KEY_EXPR));
    }

    // endregion
    // region NamedFunctionRef

    public void testNamedFunctionRef_QName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_QName.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

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

    public void testNamedFunctionRef_ReservedKeyword_While() {
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namedFunctionRefPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
                is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // region NamespaceNodeTest

    public void testNamespaceNodeTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryNamespaceNodeTest namespaceNodeTestPsi = descendants(sequenceTypePsi).findFirst(XQueryNamespaceNodeTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)namespaceNodeTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryParenthesizedItemType parenthesizedItemTypePsi = descendants(sequenceTypePsi).findFirst(XQueryParenthesizedItemType.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)parenthesizedItemTypePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryTypeswitchExpr typeswitchExprPsi = descendants(file).findFirst(XQueryTypeswitchExpr.class).get();
        XQueryCaseClause caseClausePsi = children(typeswitchExprPsi).findFirst(XQueryCaseClause.class).get();
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = children(caseClausePsi).findFirst(XQuerySequenceTypeUnion.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryTypeswitchExpr typeswitchExprPsi = descendants(file).findFirst(XQueryTypeswitchExpr.class).get();
        XQueryCaseClause caseClausePsi = children(typeswitchExprPsi).findFirst(XQueryCaseClause.class).get();
        XQuerySequenceTypeUnion sequenceTypeUnionPsi = children(caseClausePsi).findFirst(XQuerySequenceTypeUnion.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)sequenceTypeUnionPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQuerySimpleMapExpr simpleMapExprPsi = descendants(file).findFirst(XQuerySimpleMapExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQuerySimpleMapExpr simpleMapExprPsi = descendants(file).findFirst(XQuerySimpleMapExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)simpleMapExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryStringConcatExpr stringConcatExprPsi = descendants(file).findFirst(XQueryStringConcatExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryStringConcatExpr stringConcatExprPsi = descendants(file).findFirst(XQueryStringConcatExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConcatExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region StringConstructor

    public void testStringConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/StringConstructor.xq");

        XQueryStringConstructor stringConstructorPsi = descendants(file).findFirst(XQueryStringConstructor.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stringConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.STRING_CONSTRUCTOR_START));
    }

    // endregion
    // region SwitchExpr

    public void testSwitchExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq");

        XQuerySwitchExpr switchExprPsi = descendants(file).findFirst(XQuerySwitchExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)switchExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryTextTest textTestPsi = descendants(sequenceTypePsi).findFirst(XQueryTextTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)textTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryTryClause tryClausePsi = descendants(file).findFirst(XQueryTryClause.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)tryClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region TypedArrayTest

    public void testTypedArrayTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryTypedArrayTest typedArrayTestPsi = descendants(sequenceTypePsi).findFirst(XQueryTypedArrayTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)typedArrayTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ARRAY));
    }

    // endregion
    // region TypedFunctionTest

    public void testTypedFunctionTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryTypedFunctionTest typedFunctionTestPsi = descendants(sequenceTypePsi).findFirst(XQueryTypedFunctionTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)typedFunctionTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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
    // region TypedMapTest

    public void testTypedMapTest() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryTypeDeclaration typeDeclarationPsi = children(varDeclPsi).findFirst(XQueryTypeDeclaration.class).get();
        XQuerySequenceType sequenceTypePsi = children(typeDeclarationPsi).findFirst(XQuerySequenceType.class).get();
        XQueryTypedMapTest typedMapTestPsi = descendants(sequenceTypePsi).findFirst(XQueryTypedMapTest.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)typedMapTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later, or Saxon 9.4 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_MAP));
    }

    // endregion
    // region UnaryLookup

    public void testUnaryLookup() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq");

        XQuerySimpleMapExpr simpleMapExprPsi = descendants(file).findFirst(XQuerySimpleMapExpr.class).get();
        XQueryPathExpr pathExprPsi = children(simpleMapExprPsi).toListOf(XQueryPathExpr.class).get(1);
        XQueryUnaryLookup unaryLookupPsi = descendants(pathExprPsi).findFirst(XQueryUnaryLookup.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)unaryLookupPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 3.1 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.OPTIONAL));
    }

    // endregion
    // region ValidateExpr

    public void testValidateExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq");

        XQueryValidateExpr validateExprPsi = descendants(file).findFirst(XQueryValidateExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires XQuery 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_VALIDATE));
    }

    public void testValidateExpr_Type() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq");

        XQueryValidateExpr validateExprPsi = descendants(file).findFirst(XQueryValidateExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)varDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));

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

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_KeywordLocalPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordLocalPart.xq");

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("sort"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("least"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_MissingLocalPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_MissingLocalPart.xq");

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xs"));

        assertThat(eqnamePsi.getLocalName(), is(notDefined()));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_KeywordPrefixPart() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordPrefixPart.xq");

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("order"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("column"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_NCName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_NCName.xq");

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(notDefined()));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("double"));
    }

    @SuppressWarnings("RedundantCast")
    public void testEQName_URIQualifiedName() {
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_URIQualifiedName.xq");

        XQueryCastExpr castExprPsi = descendants(file).findFirst(XQueryCastExpr.class).get();
        XQuerySingleType singleTypePsi = children(castExprPsi).findFirst(XQuerySingleType.class).get();
        XQuerySimpleTypeName simpleTypeNamePsi = descendants(singleTypePsi).findFirst(XQuerySimpleTypeName.class).get();
        XQueryEQName eqnamePsi = (XQueryEQName)simpleTypeNamePsi;

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("Q{http://www.w3.org/2001/XMLSchema}"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("double"));
    }

    // endregion
    // region NCName

    public void testNCName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryEQName eqnamePsi = children(optionDeclPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(notDefined()));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryTokenType.K_COLLATION));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("collation"));
    }

    // endregion
    // region QName

    public void testQName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryEQName eqnamePsi = children(optionDeclPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("one"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("two"));
    }

    public void testQName_KeywordLocalPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryEQName eqnamePsi = children(optionDeclPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("sort"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("least"));
    }

    public void testQName_MissingLocalPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryEQName eqnamePsi = children(optionDeclPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("one"));

        assertThat(eqnamePsi.getLocalName(), is(notDefined()));
    }

    public void testQName_KeywordPrefixPart() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryEQName eqnamePsi = children(optionDeclPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("order"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("two"));
    }

    public void testQName_DirElemConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryEQName eqnamePsi = children(dirElemConstructorPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("a"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("b"));
    }

    public void testQName_DirAttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
        XQueryEQName eqnamePsi = children(dirAttributeListPsi).findFirst(XQueryEQName.class).get();

        assertThat(eqnamePsi.getPrefix(), is(defined()));
        assertThat(eqnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getPrefix().get().getText(), is("xml"));

        assertThat(eqnamePsi.getLocalName(), is(defined()));
        assertThat(eqnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(eqnamePsi.getLocalName().get().getText(), is("id"));
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

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(2));
    }

    public void testFunctionCall_Empty() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(0));
    }

    public void testFunctionCall_ArgumentPlaceholder() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq");

        XQueryFunctionCall functionCallPsi = descendants(file).findFirst(XQueryFunctionCall.class).get();
        assertThat(functionCallPsi, is(notNullValue()));
        assertThat(functionCallPsi.getArity(), is(1));
    }

    // endregion
    // region XQueryFunctionDecl

    public void testFunctionDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        assertThat(functionDeclPsi, is(notNullValue()));
        assertThat(functionDeclPsi.getArity(), is(0));
    }

    public void testFunctionDecl_ParamList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ParamList.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        assertThat(functionDeclPsi, is(notNullValue()));
        assertThat(functionDeclPsi.getArity(), is(2));
    }

    // endregion
    // region XQueryIntegerLiteral

    public void testIntegerLiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        XQueryIntegerLiteral integerLiteralPsi = descendants(file).findFirst(XQueryIntegerLiteral.class).get();
        assertThat(integerLiteralPsi, is(notNullValue()));
        assertThat(integerLiteralPsi.getAtomicValue(), is(1234));
    }

    // endregion
    // region XQueryPrologResolver
    // region Module

    public void testModule_ModuleProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModule modulePsi = descendants(file).findFirst(XQueryModule.class).get();

        XQueryPrologResolver provider = (XQueryPrologResolver)modulePsi;
        assertThat(provider.resolveProlog(), is(notDefined()));
    }

    // endregion
    // region ModuleDecl

    public void testModuleDecl_ModuleProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModuleDecl moduleDeclPsi = descendants(file).findFirst(XQueryModuleDecl.class).get();

        XQueryPrologResolver provider = (XQueryPrologResolver)moduleDeclPsi;
        assertThat(provider.resolveProlog(), is(notDefined()));
    }

    // endregion
    // endregion
    // region XQueryNamedFunctionRef

    public void testNamedFunctionRef() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        assertThat(namedFunctionRefPsi, is(notNullValue()));
        assertThat(namedFunctionRefPsi.getArity(), is(3));
    }

    public void testNamedFunctionRef_MissingArity() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq");

        XQueryNamedFunctionRef namedFunctionRefPsi = descendants(file).findFirst(XQueryNamedFunctionRef.class).get();
        assertThat(namedFunctionRefPsi, is(notNullValue()));
        assertThat(namedFunctionRefPsi.getArity(), is(0));
    }

    // endregion
    // region XQueryNamespaceResolver
    // region DirAttributeList

    public void testDirAttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirAttributeListPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirAttributeList_XmlNamespace() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirElemConstructor_AttributeList() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)dirElemConstructorPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("a"), is(notDefined()));
    }

    public void testDirElemConstructor_XmlNamespace() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryDirElemConstructor dirElemConstructorPsi = descendants(file).findFirst(XQueryDirElemConstructor.class).get();
        XQueryDirAttributeList dirAttributeListPsi = children(dirElemConstructorPsi).findFirst(XQueryDirAttributeList.class).get();
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

        XQueryModule modulePsi = descendants(file).findFirst(XQueryModule.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)modulePsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));

        Option<XQueryNamespace> ns = provider.resolveNamespace("local");
        assertThat(ns, is(defined()));

        assertThat(ns.get().getPrefix(), is(notNullValue()));
        assertThat(ns.get().getPrefix().getText(), is("local"));

        assertThat(ns.get().getUri(), is(notNullValue()));
        assertThat(ns.get().getUri().getText(), is("http://www.w3.org/2005/xquery-local-functions"));

        assertThat(ns.get().getDeclaration(), is(instanceOf(XQueryModule.class)));
        assertThat(ns.get().getDeclaration(), is(modulePsi));
    }

    // endregion
    // region ModuleDecl

    public void testModuleDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq");

        XQueryModuleDecl moduleDeclPsi = descendants(file).findFirst(XQueryModuleDecl.class).get();
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

        XQueryModuleDecl moduleDeclPsi = descendants(file).findFirst(XQueryModuleDecl.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testModulesDecl_MissingNamespaceUri() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq");

        XQueryModuleDecl moduleDeclPsi = descendants(file).findFirst(XQueryModuleDecl.class).get();
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

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)moduleImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testModuleImport_WithNamespace() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class).get();
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

        XQueryNamespaceDecl namespaceDeclPsi = descendants(file).findFirst(XQueryNamespaceDecl.class).get();
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

        XQueryNamespaceDecl namespaceDeclPsi = descendants(file).findFirst(XQueryNamespaceDecl.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)namespaceDeclPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testNamespaceDecl_MissingUri() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq");

        XQueryNamespaceDecl namespaceDeclPsi = descendants(file).findFirst(XQueryNamespaceDecl.class).get();
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

        XQueryProlog prologPsi = descendants(file).findFirst(XQueryProlog.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)prologPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testProlog_NamespaceDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq");

        XQueryProlog prologPsi = descendants(file).findFirst(XQueryProlog.class).get();
        XQueryNamespaceDecl namespaceDeclPsi = descendants(file).findFirst(XQueryNamespaceDecl.class).get();
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

        XQuerySchemaImport schemaImportPsi = descendants(file).findFirst(XQuerySchemaImport.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testSchemaImport_WithSchemaPrefix() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq");

        XQuerySchemaImport schemaImportPsi = descendants(file).findFirst(XQuerySchemaImport.class).get();
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

        XQuerySchemaImport schemaImportPsi = descendants(file).findFirst(XQuerySchemaImport.class).get();
        XQueryNamespaceResolver provider = (XQueryNamespaceResolver)schemaImportPsi;

        assertThat(provider.resolveNamespace(null), is(notDefined()));
        assertThat(provider.resolveNamespace("abc"), is(notDefined()));
        assertThat(provider.resolveNamespace("testing"), is(notDefined()));
        assertThat(provider.resolveNamespace("test"), is(notDefined()));
    }

    public void testSchemaImport_WithSchemaPrefix_Default() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq");

        XQuerySchemaImport schemaImportPsi = descendants(file).findFirst(XQuerySchemaImport.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class).get();
        assertThat(paramListPsi, is(notNullValue()));
        assertThat(paramListPsi.getArity(), is(2));
    }

    // endregion
    // region XQueryStringLiteral

    public void testStringLiteral() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/StringLiteral.xq");

        XQueryStringLiteral stringLiteralPsi = descendants(file).findFirst(XQueryStringLiteral.class).get();
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getAtomicValue(), is("One Two"));
    }

    public void testStringLiteral_Empty() {
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/StringLiteral_Empty.xq");

        XQueryStringLiteral stringLiteralPsi = descendants(file).findFirst(XQueryStringLiteral.class).get();
        assertThat(stringLiteralPsi, is(notNullValue()));
        assertThat(stringLiteralPsi.getAtomicValue(), is(nullValue()));
    }

    // endregion
    // region XQueryURIQualifiedName

    public void testURIQualifiedName() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");

        XQueryOptionDecl optionDeclPsi = descendants(file).findFirst(XQueryOptionDecl.class).get();
        XQueryURIQualifiedName qnamePsi = children(optionDeclPsi).findFirst(XQueryURIQualifiedName.class).get();

        assertThat(qnamePsi.getPrefix(), is(defined()));
        assertThat(qnamePsi.getPrefix().get().getNode().getElementType(), is(XQueryElementType.BRACED_URI_LITERAL));
        assertThat(qnamePsi.getPrefix().get().getText(), is("Q{one{two}"));

        assertThat(qnamePsi.getLocalName(), is(defined()));
        assertThat(qnamePsi.getLocalName().get().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(qnamePsi.getLocalName().get().getText(), is("three"));
    }

    // endregion
    // region XQueryVariableResolver
    // region AnnotatedDecl

    public void testAnnotatedDecl_VarDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryEQName varNamePsi = children(varDeclPsi).findFirst(XQueryEQName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryEQName functionNamePsi = children(functionDeclPsi).findFirst(XQueryEQName.class).get();
        XQueryVariableResolver provider = (XQueryVariableResolver)annotatedDeclPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));
        assertThat(provider.resolveVariable(functionNamePsi), is(notDefined()));
    }

    // endregion
    // region CaseClause

    public void testCaseClause_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq");

        XQueryTypeswitchExpr typeswitchExprPsi = descendants(file).findFirst(XQueryTypeswitchExpr.class).get();
        XQueryCaseClause caseClausePsi = children(typeswitchExprPsi).findFirst(XQueryCaseClause.class).get();
        XQueryVarName varNamePsi = children(caseClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryCountClause countClausePsi = descendants(intermediateClausePsi).findFirst(XQueryCountClause.class).get();
        XQueryVarName varNamePsi = children(countClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();
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

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();

        XQueryPositionalVar positionalVarPsi = children(forBindingPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();

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

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class).get();
        XQueryParam paramPsi = children(paramListPsi).findFirst(XQueryParam.class).get();
        XQueryEQName paramNamePsi = children(paramPsi).findFirst(XQueryEQName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryGroupByClause groupByClausePsi = descendants(intermediateClausePsi).findFirst(XQueryGroupByClause.class).get();
        XQueryGroupingSpecList groupingSpecListPsi = children(groupByClausePsi).findFirst(XQueryGroupingSpecList.class).get();
        XQueryGroupingSpec groupingSpecPsi = children(groupingSpecListPsi).findFirst(XQueryGroupingSpec.class).get();
        XQueryGroupingVariable groupingVariablePsi = children(groupingSpecPsi).findFirst(XQueryGroupingVariable.class).get();
        XQueryVarName varNamePsi = children(groupingVariablePsi).findFirst(XQueryVarName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryGroupByClause groupByClausePsi = descendants(intermediateClausePsi).findFirst(XQueryGroupByClause.class).get();
        XQueryGroupingSpecList groupingSpecListPsi = children(groupByClausePsi).findFirst(XQueryGroupingSpecList.class).get();
        XQueryGroupingSpec groupingSpecPsi = children(groupingSpecListPsi).findFirst(XQueryGroupingSpec.class).get();
        XQueryGroupingVariable groupingVariablePsi = children(groupingSpecPsi).findFirst(XQueryGroupingVariable.class).get();
        XQueryVarName varNamePsi = children(groupingVariablePsi).findFirst(XQueryVarName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryGroupByClause groupByClausePsi = descendants(intermediateClausePsi).findFirst(XQueryGroupByClause.class).get();
        XQueryGroupingSpecList groupingSpecListPsi = children(groupByClausePsi).findFirst(XQueryGroupingSpecList.class).get();
        XQueryGroupingSpec groupingSpecPsi = children(groupingSpecListPsi).findFirst(XQueryGroupingSpec.class).get();
        XQueryGroupingVariable groupingVariablePsi = children(groupingSpecPsi).findFirst(XQueryGroupingVariable.class).get();
        XQueryVarName varNamePsi = children(groupingVariablePsi).findFirst(XQueryVarName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryGroupByClause groupByClausePsi = descendants(intermediateClausePsi).findFirst(XQueryGroupByClause.class).get();
        XQueryGroupingSpecList groupingSpecListPsi = children(groupByClausePsi).findFirst(XQueryGroupingSpecList.class).get();
        XQueryGroupingSpec groupingSpecPsi = children(groupingSpecListPsi).findFirst(XQueryGroupingSpec.class).get();
        XQueryGroupingVariable groupingVariablePsi = children(groupingSpecPsi).findFirst(XQueryGroupingVariable.class).get();
        XQueryVarName varNamePsi = children(groupingVariablePsi).findFirst(XQueryVarName.class).get();
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

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryForClause forClausePsi = descendants(intermediateClausePsi).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();
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

        XQueryLetClause letClausePsi = descendants(file).findFirst(XQueryLetClause.class).get();
        XQueryLetBinding letBindingPsi = children(letClausePsi).findFirst(XQueryLetBinding.class).get();
        XQueryVarName varNamePsi = children(letBindingPsi).findFirst(XQueryVarName.class).get();
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

        XQueryLetClause letClausePsi = descendants(file).findFirst(XQueryLetClause.class).get();
        XQueryLetBinding letBindingPsi = children(letClausePsi).findFirst(XQueryLetBinding.class).get();
        XQueryVarName varNamePsi = children(letBindingPsi).findFirst(XQueryVarName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class).get();
        XQueryParam paramPsi = children(paramListPsi).findFirst(XQueryParam.class).get();
        XQueryEQName paramNamePsi = children(paramPsi).findFirst(XQueryEQName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class).get();
        XQueryParam paramPsi = children(paramListPsi).findFirst(XQueryParam.class).get();
        XQueryEQName paramNamePsi = children(paramPsi).findFirst(XQueryEQName.class).get();
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

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryPositionalVar positionalVarPsi = children(forBindingPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName varNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryEQName varNamePsi = children(varDeclPsi).findFirst(XQueryEQName.class).get();

        XQueryProlog prologPsi = descendants(file).findFirst(XQueryProlog.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryEQName functionNamePsi = children(functionDeclPsi).findFirst(XQueryEQName.class).get();

        XQueryProlog prologPsi = descendants(file).findFirst(XQueryProlog.class).get();
        XQueryVariableResolver provider = (XQueryVariableResolver)prologPsi;

        assertThat(provider.resolveVariable(null), is(notDefined()));
        assertThat(provider.resolveVariable(functionNamePsi), is(notDefined()));
    }

    // endregion
    // region QuantifiedExpr

    public void testQuantifiedExpr_VariableProvider() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq");

        XQueryQuantifiedExpr quantifiedExprPsi = descendants(file).findFirst(XQueryQuantifiedExpr.class).get();
        XQueryVarName varNamePsi = children(quantifiedExprPsi).findFirst(XQueryVarName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryVarName varNamePsi = children(slidingWindowClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryWindowEndCondition windowEndConditionPsi = children(slidingWindowClausePsi).findFirst(XQueryWindowEndCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowEndConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryWindowStartCondition windowStartConditionPsi = children(slidingWindowClausePsi).findFirst(XQueryWindowStartCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowStartConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryVarName varNamePsi = children(tumblingWindowClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryWindowEndCondition windowEndConditionPsi = children(tumblingWindowClausePsi).findFirst(XQueryWindowEndCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowEndConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryWindowStartCondition windowStartConditionPsi = children(tumblingWindowClausePsi).findFirst(XQueryWindowStartCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowStartConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryTypeswitchExpr typeswitchExprPsi = descendants(file).findFirst(XQueryTypeswitchExpr.class).get();
        XQueryVarName varNamePsi = children(typeswitchExprPsi).findFirst(XQueryVarName.class).get();
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

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryEQName varNamePsi = children(varDeclPsi).findFirst(XQueryEQName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryVarName varNamePsi = children(slidingWindowClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryVarName varNamePsi = children(tumblingWindowClausePsi).findFirst(XQueryVarName.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryWindowEndCondition windowEndConditionPsi = children(tumblingWindowClausePsi).findFirst(XQueryWindowEndCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowEndConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryWindowStartCondition windowStartConditionPsi = children(tumblingWindowClausePsi).findFirst(XQueryWindowStartCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowStartConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryWindowStartCondition windowStartConditionPsi = children(tumblingWindowClausePsi).findFirst(XQueryWindowStartCondition.class).get();
        XQueryWindowVars windowVarsPsi = children(windowStartConditionPsi).findFirst(XQueryWindowVars.class).get();
        XQueryCurrentItem currentItemPsi = children(windowVarsPsi).findFirst(XQueryCurrentItem.class).get();
        XQueryPositionalVar positionalVarPsi = children(windowVarsPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName posVarNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();
        XQueryPreviousItem previousItemPsi = children(windowVarsPsi).findFirst(XQueryPreviousItem.class).get();
        XQueryNextItem nextItemPsi = children(windowVarsPsi).findFirst(XQueryNextItem.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryModule modulePsi = children(file).findFirst(XQueryModule.class).get();
        XQueryVersionDecl versionDeclPsi = descendants(modulePsi).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

    public void testVersionDecl_UnsupportedVersion() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/psi/xquery-1.0/VersionDecl_UnsupportedVersion.xq");

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
        assertThat(versionDeclPsi.getVersion(), is(notNullValue()));
        assertThat(versionDeclPsi.getVersion().getAtomicValue(), is("9.4"));
        assertThat(versionDeclPsi.getEncoding(), is(nullValue()));

        assertThat(file.getXQueryVersion(), is(XQueryVersion.VERSION_9_4));

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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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

        XQueryVersionDecl versionDeclPsi = descendants(file).findFirst(XQueryVersionDecl.class).get();
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
