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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class UpdateFacilityPsiTest extends ParserTestCase {
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class).get(0);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)compatibilityAnnotationPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");

        UpdateFacilityRevalidationDecl revalidationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityRevalidationDecl.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)revalidationDeclPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_REVALIDATION));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        UpdateFacilityInsertExpr insertExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityInsertExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)insertExprPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_INSERT));
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        UpdateFacilityDeleteExpr deleteExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityDeleteExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)deleteExprPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_DELETE));
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq");

        UpdateFacilityReplaceExpr replaceExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityReplaceExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)replaceExprPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_REPLACE));
    }

    // endregion
    // region Update Facility 1.0 :: RenameExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq");

        UpdateFacilityRenameExpr renameExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityRenameExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)renameExprPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_RENAME));
    }

    // endregion
    // region Update Facility 1.0 :: TransformExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq");

        UpdateFacilityTransformExpr transformExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityTransformExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)transformExprPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_COPY));
    }

    // endregion
    // region Update Facility 3.0 :: CompatibilityAnnotation

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_FunctionDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class).get(0);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)compatibilityAnnotationPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_VarDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        UpdateFacilityCompatibilityAnnotation compatibilityAnnotationPsi = PsiNavigation.findChildrenByClass(annotatedDeclPsi, UpdateFacilityCompatibilityAnnotation.class).get(0);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)compatibilityAnnotationPsi;

        assertThat(versioned.getConformanceVersion(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_3_0));
        assertThat(versioned.getConformanceVersion(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceVersion(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.XQUERY), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getConformanceElement(XQueryConformance.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getConformanceElement(XQueryConformance.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    // endregion
}
