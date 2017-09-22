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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.XPST0003;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003.MapConstructorEntryInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapConstructorEntryInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        MapConstructorEntryInspection inspection = new MapConstructorEntryInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        MapConstructorEntryInspection inspection = new MapConstructorEntryInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region XQuery 3.1

    @SuppressWarnings("ConstantConditions")
    public void testXQuery31_Map_XQuerySeparator() {
        getSettings().setImplementation("w3c/spec");
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_1_20170321().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testXQuery31_Map_SaxonSeparator() {
        getSettings().setImplementation("w3c/spec");
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_1_20170321().getLabel());
        final XQueryFile file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.ASSIGN_EQUAL));
    }

    @SuppressWarnings("ConstantConditions")
    public void testXQuery31_Map_NoValueAssignmentOperator() {
        getSettings().setImplementation("w3c/spec");
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_1_20170321().getLabel());
        final XQueryFile file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region Saxon 9.4

    @SuppressWarnings("ConstantConditions")
    public void testSaxon94_Map_SaxonSeparator() {
        getSettings().setImplementation("saxon/EE");
        getSettings().setImplementationVersion("saxon/EE/v9.5");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_0_20140408().getLabel());
        final XQueryFile file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testSaxon94_Map_XQuerySeparator() {
        getSettings().setImplementation("saxon/EE");
        getSettings().setImplementationVersion("saxon/EE/v9.5");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_0_20140408().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.QNAME_SEPARATOR));
    }

    @SuppressWarnings("ConstantConditions")
    public void testSaxon94_Map_NoValueAssignmentOperator() {
        getSettings().setImplementation("saxon/EE");
        getSettings().setImplementationVersion("saxon/EE/v9.5");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_3_0_20140408().getLabel());
        final XQueryFile file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region MarkLogic 8.0

    @SuppressWarnings("ConstantConditions")
    public void testMarkLogic80_ObjectNode_MarkLogicSeparator() {
        getSettings().setImplementation("marklogic");
        getSettings().setImplementationVersion("marklogic/v8");
        final XQueryFile file = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testMarkLogic80_ObjectNode_SaxonSeparator() {
        getSettings().setImplementation("marklogic");
        getSettings().setImplementationVersion("marklogic/v8");
        final XQueryFile file = parseResource("tests/psi/marklogic-8.0/MapConstructorEntry_SaxonSeparator.xq");

        final ProblemDescriptor[] problems = inspect(file, new MapConstructorEntryInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.ASSIGN_EQUAL));
    }

    // endregion
}
