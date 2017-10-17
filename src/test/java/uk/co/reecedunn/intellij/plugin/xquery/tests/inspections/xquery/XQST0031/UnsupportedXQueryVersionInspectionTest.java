/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xquery.XQST0031;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0031.UnsupportedXQueryVersionInspection;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnsupportedXQueryVersionInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        UnsupportedXQueryVersionInspection inspection = new UnsupportedXQueryVersionInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        UnsupportedXQueryVersionInspection inspection = new UnsupportedXQueryVersionInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region Invalid XQuery Versions

    public void testNoVersionDecl() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/no-versiondecl.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testEmptyVersionDecl() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/empty-version.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"\""));
    }

    public void testXQueryVersion_UNSUPPORTED() {
        // XQueryVersion.parse("3.99") returns XQueryVersion.UNSUPPORTED
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.99.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"3.99\""));
    }

    public void testUnsupportedVersion() {
        // XQueryVersion.parse("9.7") returns XQueryVersion.VERSION_9_7, but that is not a valid XQuery version.
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-9.7.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"9.7\""));
    }

    // endregion
    // region MarkLogic

    public void testSupportedVersion_MarkLogic() {
        getSettings().setImplementationVersion("marklogic/v8");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUnsupportedVersion_MarkLogic() {
        getSettings().setImplementationVersion("marklogic/v8");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.0.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"3.0\""));
    }

    // endregion
    // region W3C

    public void testSupportedVersion_W3C() {
        getSettings().setImplementationVersion("w3c/spec");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.0.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testUnsupportedVersion_W3C() {
        getSettings().setImplementationVersion("w3c/spec");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"1.0-ml\""));
    }

    // endregion
    // region MarkLogic Transactions

    public void testTransactions_SameVersion_MarkLogic() {
        getSettings().setImplementationVersion("marklogic/v8");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testTransactions_SameVersion_W3C() {
        getSettings().setImplementationVersion("w3c/spec");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/transaction-same-version.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(2));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"1.0-ml\""));

        assertThat(problems[1].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[1].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[1].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[1].getPsiElement().getText(), is("\"1.0-ml\""));
    }

    public void testTransactions_UnsupportedOtherVersion() {
        getSettings().setImplementationVersion("marklogic/v8");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/transaction-unsupported-other-version.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: The implementation does not support this XQuery version."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"0.2\""));
    }

    public void testTransactions_DifferentVersions() {
        getSettings().setImplementationVersion("marklogic/v8");

        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0031/transaction-different-version.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnsupportedXQueryVersionInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0031: MarkLogic requires that XQuery versions are the same across different transactions."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.STRING_LITERAL));
        assertThat(problems[0].getPsiElement().getText(), is("\"0.9-ml\""));
    }

    // endregion
}
