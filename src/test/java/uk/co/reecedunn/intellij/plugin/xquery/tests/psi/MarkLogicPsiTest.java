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

import com.intellij.lang.ASTNode;
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.*;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class MarkLogicPsiTest extends ParserTestCase {
    // region MarkLogic 6.0 :: Transactions + TransactionSeparator

    public void testTransactions() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/Transactions.xq");

        MarkLogicTransactionSeparator transactionSeparatorPsi = PsiNavigation.findChildrenByClass(node.getPsi(), MarkLogicTransactionSeparator.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)transactionSeparatorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.SEPARATOR));
    }

    // endregion
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    public void testCompatibilityAnnotation_FunctionDecl() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        MarkLogicCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, MarkLogicCompatibilityAnnotation.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_PRIVATE));
    }

    public void testCompatibilityAnnotation_VarDecl() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        MarkLogicCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, MarkLogicCompatibilityAnnotation.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_PRIVATE));
    }

    // endregion
    // region MarkLogic 6.0 :: StylesheetImport

    public void testStylesheetImport() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq");

        MarkLogicStylesheetImport stylesheetImportPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicStylesheetImport.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)stylesheetImportPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_IMPORT));
    }

    // endregion
    // region MarkLogic 6.0 :: CatchClause

    public void testCatchClause() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CatchClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTryCatchExpr.class);
        XQueryCatchClause catchClausePsi = PsiNavigation.findChildrenByClass(tryCatchExprPsi, XQueryCatchClause.class).get(0);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)catchClausePsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_CATCH));
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_Namespace() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NAMESPACE));
    }

    public void testForwardAxis_Property() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)forwardAxisPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_PROPERTY));
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    public void testValidateExpr_ValidateAs() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq");

        XQueryValidateExpr validateExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryValidateExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)validateExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_AS));
    }

    // endregion
    // region MarkLogic 6.0 :: CompBinaryConstructor

    public void testCompBinaryConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CompBinaryConstructor.xq");

        MarkLogicCompBinaryConstructor binaryKindTestPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompBinaryConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)binaryKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BINARY));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryTest

    public void testBinaryTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotationDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        MarkLogicBinaryTest binaryKindTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicBinaryTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)binaryKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BINARY));
    }

    // endregion
    // region MarkLogic 8.0 :: CompBooleanConstructor

    public void testCompBooleanConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/CompBooleanConstructor.xq");

        MarkLogicCompBooleanConstructor booleanConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompBooleanConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)booleanConstructorPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BOOLEAN_NODE));
    }

    // endregion
    // region MarkLogic 8.0 :: CompNullConstructor

    public void testCompNullConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/CompNullConstructor.xq");

        MarkLogicCompNullConstructor nullKindTestPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompNullConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)nullKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NULL_NODE));
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    public void testAnyKindTest_KeyName() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryAnyKindTest anyKindTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryAnyKindTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.STRING_LITERAL));
    }

    public void testAnyKindTest_Wildcard() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryAnyKindTest anyKindTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryAnyKindTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)anyKindTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.STAR));
    }

    // endregion
    // region MarkLogic 8.0 :: TextTest

    public void testTextTest_KeyName() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        XQueryTextTest textTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, XQueryTextTest.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)textTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryElementType.STRING_LITERAL));
    }

    // endregion
    // region MarkLogic 8.0 :: NullTest

    public void testNullTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NullTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotationDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        MarkLogicNullTest nullTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicNullTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)nullTestPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires MarkLogic 8.0 or later with XQuery version '1.0-ml'."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NULL_NODE));
    }

    // endregion
}
