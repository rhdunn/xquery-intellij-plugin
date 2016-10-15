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
    // region XQueryConformanceCheck
    // region AnyKindTest

    public void testAnyKindTest_KeyName() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
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
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
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
    // region ArrayTest

    public void testArrayTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/ArrayTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        MarkLogicArrayTest arrayTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicArrayTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)arrayTestPsi;

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
                is(XQueryTokenType.K_ARRAY_NODE));
    }

    // endregion
    // region BinaryTest

    public void testBinaryTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
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
    // region BooleanTest

    public void testBooleanTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/BooleanTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        MarkLogicBooleanTest booleanTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicBooleanTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)booleanTestPsi;

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
    // region CatchClause

    public void testCatchClause() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CatchClause.xq");

        XQueryTryCatchExpr tryCatchExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryTryCatchExpr.class);
        XQueryCatchClause catchClausePsi = PsiNavigation.findChildByClass(tryCatchExprPsi, XQueryCatchClause.class);
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
    // region CompatibilityAnnotation

    public void testCompatibilityAnnotation_FunctionDecl() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        MarkLogicCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, MarkLogicCompatibilityAnnotation.class);
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
        MarkLogicCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, MarkLogicCompatibilityAnnotation.class);
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
    // region CompArrayConstructor

    public void testCompArrayConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor.xq");

        MarkLogicCompArrayConstructor arrayConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompArrayConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)arrayConstructorPsi;

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
                is(XQueryTokenType.K_ARRAY_NODE));
    }

    // endregion
    // region CompBinaryConstructor

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
    // region CompBooleanConstructor

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
    // region CompNullConstructor

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
    // region CompNumberConstructor

    public void testCompNumberConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/CompNumberConstructor.xq");

        MarkLogicCompNumberConstructor numberConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompNumberConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)numberConstructorPsi;

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
                is(XQueryTokenType.K_NUMBER_NODE));
    }

    // endregion
    // region CompObjectConstructor

    public void testCompObjectConstructor() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/CompObjectConstructor.xq");

        MarkLogicCompObjectConstructor objectConstructorPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicCompObjectConstructor.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)objectConstructorPsi;

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
                is(XQueryTokenType.K_OBJECT_NODE));
    }

    // endregion
    // region ForwardAxis

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
    // region FunctionCall

    public void testFunctionCall_ArrayNode() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ARRAY_NODE));
    }

    public void testFunctionCall_BooleanNode() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NodeTest_BooleanTest_FunctionCallLike.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BOOLEAN_NODE));
    }

    public void testFunctionCall_NullNode() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NodeTest_NullTest_FunctionCallLike.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NULL_NODE));
    }

    public void testFunctionCall_NumberNode() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NodeTest_NumberTest_FunctionCallLike.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NUMBER_NODE));
    }

    public void testFunctionCall_ObjectNode() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NodeTest_ObjectTest_FunctionCallLike.xq");

        XQueryFunctionCall functionCallPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionCall.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionCallPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This function name conflicts with MarkLogic JSON KindTest keywords."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_OBJECT_NODE));
    }

    // endregion
    // region FunctionDecl

    public void testFunctionDecl_ReservedKeyword_ArrayNode() {
        final ASTNode node = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ARRAY_NODE));
    }

    public void testFunctionDecl_ReservedKeyword_BooleanNode() {
        final ASTNode node = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_BooleanNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BOOLEAN_NODE));
    }

    public void testFunctionDecl_ReservedKeyword_NullNode() {
        final ASTNode node = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_NullNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NULL_NODE));
    }

    public void testFunctionDecl_ReservedKeyword_NumberNode() {
        final ASTNode node = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_NumberNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_NUMBER_NODE));
    }

    public void testFunctionDecl_ReservedKeyword_ObjectNode() {
        final ASTNode node = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ObjectNode.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)functionDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Reserved keyword used as a function name."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_OBJECT_NODE));
    }

    // endregion
    // region NullTest

    public void testNullTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NullTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
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
    // region NumberTest

    public void testNumberTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/NumberTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        MarkLogicNumberTest numberTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicNumberTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)numberTestPsi;

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
                is(XQueryTokenType.K_NUMBER_NODE));
    }

    // endregion
    // region ObjectTest

    public void testObjectTest() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/ObjectTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotationDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
        MarkLogicObjectTest objectTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicObjectTest.class);

        XQueryConformanceCheck versioned = (XQueryConformanceCheck)objectTestPsi;

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
                is(XQueryTokenType.K_OBJECT_NODE));
    }

    // endregion
    // region StylesheetImport

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
    // region TextTest

    public void testTextTest_KeyName() {
        final ASTNode node = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryTypeDeclaration.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(typeDeclarationPsi, XQuerySequenceType.class);
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
    // region Transactions + TransactionSeparator

    public void testTransactions() {
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/Transactions.xq");

        MarkLogicTransactionSeparator transactionSeparatorPsi = PsiNavigation.findChildByClass(node.getPsi(), MarkLogicTransactionSeparator.class);
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
    // region ValidateExpr

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
    // endregion
}
