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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.XPST0081;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0081.UnboundQNamePrefixInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnboundQNamePrefixInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        UnboundQNamePrefixInspection inspection = new UnboundQNamePrefixInspection();
        assertThat(inspection.getDisplayName(), is("Unbound namespace prefix"));
    }

    public void testDescription() {
        UnboundQNamePrefixInspection inspection = new UnboundQNamePrefixInspection();
        assertThat(inspection.loadDescription(), is("This inspection checks for QName prefices that do not resolve to namespace declarations in the static context."));
    }

    // endregion
    // region Predefined Namespaces
    // region xmlns

    public void testXmlns() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/xmlns.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region XQuery

    public void testBuiltinXQuery() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/builtin-xquery.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testBuiltinXQuery31() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/builtin-xquery-3.1.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region MarkLogic

    public void testBuiltinMarkLogic() {
        getSettings().setImplementation("marklogic");
        getSettings().setImplementationVersion("marklogic/v8");
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0_MARKLOGIC);
        final XQueryFile file = parseResource("tests/inspections/XPST0081/builtin-marklogic.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testBuiltinMarkLogicNotTargettingMarkLogic() {
        getSettings().setImplementation("w3c");
        final XQueryFile file = parseResource("tests/inspections/XPST0081/builtin-marklogic.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("xdmp"));
    }

    // endregion
    // endregion
    // region Inspection Sources

    public void testQName() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/ModuleDecl_QName_UnboundPrefix.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("x"));
    }

    // endregion
    // region Namespace Providers
    // region ModuleDecl

    public void testModuleDecl_BoundPrefix() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/ModuleDecl_QName.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testModuleDecl_UnboundPrefix() {
        final XQueryFile file = parseResource("tests/inspections/XPST0081/ModuleDecl_QName_UnboundPrefix.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("x"));
    }

    // endregion
    // endregion
}
