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
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class UpdateFacilityPsiTest extends ParserTestCase {
    // region XQueryConformanceCheck
    // region CompatibilityAnnotation

    public void testCompatibilityAnnotation_FunctionDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    public void testCompatibilityAnnotation_VarDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 3.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    // endregion
    // region DeleteExpr

    public void testDeleteExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        UpdateFacilityDeleteExpr deleteExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityDeleteExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)deleteExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DELETE));
    }

    // endregion
    // region FunctionDecl

    public void testFunctionDecl_Updating() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)compatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    // endregion
    // region InsertExpr

    public void testInsertExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        UpdateFacilityInsertExpr insertExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityInsertExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)insertExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_INSERT));
    }

    // endregion
    // region RenameExpr

    public void testRenameExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq");

        UpdateFacilityRenameExpr renameExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityRenameExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)renameExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_RENAME));
    }

    // endregion
    // region ReplaceExpr

    public void testReplaceExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq");

        UpdateFacilityReplaceExpr replaceExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityReplaceExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)replaceExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_REPLACE));
    }

    // endregion
    // region RevalidationDecl

    public void testRevalidationDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");

        UpdateFacilityRevalidationDecl revalidationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityRevalidationDecl.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)revalidationDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_REVALIDATION));
    }

    // endregion
    // region TransformExpr

    public void testTransformExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq");

        UpdateFacilityTransformExpr transformExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityTransformExpr.class);
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)transformExprPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Update Facility 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_COPY));
    }

    // endregion
    // endregion
}
