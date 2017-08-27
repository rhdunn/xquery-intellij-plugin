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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.XPST0003;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003.UnsupportedConstructInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnsupportedConstructInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        UnsupportedConstructInspection inspection = new UnsupportedConstructInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        UnsupportedConstructInspection inspection = new UnsupportedConstructInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region XQuery Conformance

    @SuppressWarnings("ConstantConditions")
    public void testXQuery30VersionDeclInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: This expression requires XQuery 3.0 or later."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_ENCODING));
    }

    @SuppressWarnings("ConstantConditions")
    public void testXQuery30VersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region Update Facility Conformance

    public void testUpdateFacility10_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("w3c/spec/v1ed");

        final XQueryFile file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUpdateFacility10_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_DELETE));
    }

    public void testUpdateFacility30_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        getSettings().setImplementationVersion("w3c/spec/v1ed");

        final XQueryFile file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUpdateFacility30_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        getSettings().setImplementationVersion("saxon/EE/v9.5"); // Supports Update Facility 1.0, not 3.0

        final XQueryFile file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_INVOKE));
    }

    public void testUpdateFacilityBaseX_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        getSettings().setImplementationVersion("basex/v8.6");

        final XQueryFile file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUpdateFacilityBaseX_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        getSettings().setImplementationVersion("saxon/EE/v9.5"); // Supports Update Facility 1.0, not 3.0

        final XQueryFile file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0, or BaseX 8.5 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_TRANSFORM));
    }

    // endregion
    // region Scripting Conformance

    public void testScripting10_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("w3c/spec/v1ed");

        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testScripting10_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 7.0 does not support XQuery Scripting Extension 1.0 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_BLOCK));
    }

    // endregion
    // region BaseX Conformance

    public void testBaseX_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        getSettings().setImplementationVersion("basex/v8.5");

        final XQueryFile file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testBaseX_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(2));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 7.0 does not support BaseX 7.8 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_UPDATE));

        assertThat(problems[1].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[1].getDescriptionTemplate(), is("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs."));
        assertThat(problems[1].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_DELETE));
    }

    // endregion
    // region MarkLogic Conformance

    public void testMarkLogic09ml_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_0_9_MARKLOGIC);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testMarkLogic09ml_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_0_9_MARKLOGIC);
        getSettings().setImplementationVersion("saxon/EE/v9.5");

        final XQueryFile file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Saxon 9.5 does not support MarkLogic 4.0, or XQuery 0.9-ml constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_BINARY));
    }

    public void testMarkLogic10ml_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0_MARKLOGIC);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testMarkLogic10ml_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0_MARKLOGIC);
        getSettings().setImplementationVersion("saxon/EE/v9.5");

        final XQueryFile file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Saxon 9.5 does not support MarkLogic 7.0 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_SCHEMA_ROOT));
    }

    // endregion
}
