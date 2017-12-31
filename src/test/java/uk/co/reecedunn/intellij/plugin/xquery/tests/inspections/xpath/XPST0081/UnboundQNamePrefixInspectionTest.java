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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xpath.XPST0081;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0081.UnboundQNamePrefixInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnboundQNamePrefixInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        UnboundQNamePrefixInspection inspection = new UnboundQNamePrefixInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        UnboundQNamePrefixInspection inspection = new UnboundQNamePrefixInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region Predefined Namespaces
    // region xmlns

    public void testXmlns() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/xmlns.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region XQuery

    public void testBuiltinXQuery() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testBuiltinXQuery31() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery-3.1.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region MarkLogic

    public void testBuiltinMarkLogic() {
        getSettings().setImplementationVersion("marklogic/v8");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testBuiltinMarkLogicNotTargettingMarkLogic() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("xdmp"));
    }

    // endregion
    // endregion
    // region Inspection Sources

    public void testQName() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName_UnboundPrefix.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("x"));
    }

    // endregion
    // region Namespace Providers
    // region ModuleDecl

    public void testModuleDecl_BoundPrefix() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testModuleDecl_UnboundPrefix() {
        final XQueryModule file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName_UnboundPrefix.xq");

        final ProblemDescriptor[] problems = inspect(file, new UnboundQNamePrefixInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0081: Cannot resolve namespace prefix."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.NCNAME));
        assertThat(problems[0].getPsiElement().getText(), is("x"));
    }

    // endregion
    // endregion
}
