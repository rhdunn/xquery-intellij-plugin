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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xquery.XQST0033;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0033.DuplicateNamespacePrefixInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DuplicateNamespacePrefixInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        DuplicateNamespacePrefixInspection inspection = new DuplicateNamespacePrefixInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        DuplicateNamespacePrefixInspection inspection = new DuplicateNamespacePrefixInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region XQuery 1.0

    public void testNoDuplicates() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/no-duplicates.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testModuleDecl_ModuleImport() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/ModuleDecl-ModuleImport.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0033: The namespace prefix 'test' has already been defined."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("test"));
    }

    public void testModuleImport_NamespaceDecl() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/ModuleImport-NamespaceDecl.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0033: The namespace prefix 'one' has already been defined."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("one"));
    }

    public void testNamespaceDecl_SchemaImport() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/NamespaceDecl-SchemaImport.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0033: The namespace prefix 'one' has already been defined."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("one"));
    }

    public void testSchemaImport_ModuleImport() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/SchemaImport-ModuleImport.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0033: The namespace prefix 'one' has already been defined."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("one"));
    }

    // endregion
    // region MarkLogic 6.0

    public void testOtherTransaction_NoDuplicates() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/other-transaction-no-duplicates.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testOtherTransaction_Duplicates() {
        final XQueryModule file = parseResource("tests/inspections/xquery/XQST0033/other-transaction-duplicates.xq");

        final ProblemDescriptor[] problems = inspect(file, new DuplicateNamespacePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XQST0033: The namespace prefix 'one' has already been defined."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("one"));
    }

    // endregion
}
