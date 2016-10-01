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
import com.intellij.lang.ASTNode;
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
        assertThat(inspection.getDisplayName(), is("Unsupported XQuery construct"));
    }

    public void testDescription() {
        UnsupportedConstructInspection inspection = new UnsupportedConstructInspection();
        assertThat(inspection.loadDescription(), is("The unsupported construct inspection reports warnings for XQuery constructs that are not supported by the specified XQuery dialect. These constructs would raise XPST0003 errors on that XQuery processor."));
    }

    // endregion
    // region XQuery Conformance

    @SuppressWarnings("ConstantConditions")
    public void testXQuery30VersionDeclInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: This expression requires XQuery 3.0 or later."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_ENCODING));
    }

    @SuppressWarnings("ConstantConditions")
    public void testXQuery30VersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region Update Facility Conformance

    public void testUpdateFacility10InsertExprInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: This expression requires Update Facility 1.0 or later."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_INSERT));
    }

    public void testUpdateFacility10InsertExpr() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementation("w3c");
        getSettings().setXQuery10Dialect("w3c/1.0-update");
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region MarkLogic Conformance

    public void testMarkLogicForwardAxisInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_NAMESPACE));
    }

    public void testMarkLogicForwardAxis() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0_MARKLOGIC);
        getSettings().setImplementation("marklogic");
        getSettings().setImplementationVersion("marklogic/v6");
        final ASTNode node = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq");

        final ProblemDescriptor[] problems = inspect(node, new UnsupportedConstructInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
}
