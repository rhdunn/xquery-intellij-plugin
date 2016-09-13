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
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityDeleteExpr;
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityInsertExpr;
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityReplaceExpr;
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityRevalidationDecl;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryLanguageType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class UpdateFacilityPsiTest extends ParserTestCase {
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");

        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryFunctionDecl.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)functionDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_UPDATING));
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");

        UpdateFacilityRevalidationDecl revalidationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityRevalidationDecl.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)revalidationDeclPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_REVALIDATION));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        UpdateFacilityInsertExpr insertExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityInsertExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)insertExprPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_INSERT));
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        UpdateFacilityDeleteExpr deleteExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityDeleteExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)deleteExprPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_DELETE));
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr() {
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq");

        UpdateFacilityReplaceExpr replaceExprPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), UpdateFacilityReplaceExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)replaceExprPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(XQueryVersion.VERSION_1_0));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(notNullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(nullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION).getNode().getElementType(),
                is(XQueryTokenType.K_REPLACE));
    }

    // endregion
}
