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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xquery.XQST0118;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0118.MismatchedDirElemTagNameInspection;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MismatchedDirElemTagNameInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        MismatchedDirElemTagNameInspection inspection = new MismatchedDirElemTagNameInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        MismatchedDirElemTagNameInspection inspection = new MismatchedDirElemTagNameInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region DirElemConstructor :: NCName

    public void testNCName_MatchedTags() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0118/NCName_MatchedTags.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testNCName_SelfClosing() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0118/NCName_SelfClosing.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testNCName_MismatchedTags() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0118/NCName_MismatchedTags.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0118: The closing tag 'b' does not match the open tag 'a'."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("b"));
    }

    // endregion
    // region DirElemConstructor :: QName

    public void testQName_MatchedPrefixAndLocalName() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testQName_SelfClosing() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testQName_MissingClosingTag() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testQName_MismatchedPrefix() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedPrefix.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0118: The closing tag 'c:b' does not match the open tag 'a:b'."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.QNAME));
        assertThat(problems[0].getPsiElement().getText(), is("c:b"));
    }

    public void testQName_MismatchedLocalName() {
        final XQueryFile file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedLocalName.xq");

        final ProblemDescriptor[] problems = inspect(file, new MismatchedDirElemTagNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0118: The closing tag 'a:c' does not match the open tag 'a:b'."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.QNAME));
        assertThat(problems[0].getPsiElement().getText(), is("a:c"));
    }

    // endregion
}
