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
import uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003.UnsupportedConstructInspection2;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnsupportedConstructInspection2Test extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        UnsupportedConstructInspection2 inspection = new UnsupportedConstructInspection2();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        UnsupportedConstructInspection2 inspection = new UnsupportedConstructInspection2();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region Update Facility Conformance

    public void testUpdateFacility10_ProductConformsToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("w3c/spec/v1ed");

        final XQueryFile file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection2());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUpdateFacility10_ProductDoesNotConformToSpecification() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementationVersion("marklogic/v7.0");

        final XQueryFile file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedConstructInspection2());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_DELETE));
    }

    // endregion
}
