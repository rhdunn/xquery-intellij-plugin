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
import uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003.FinalStatementSemicolonInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FinalStatementSemicolonInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        FinalStatementSemicolonInspection inspection = new FinalStatementSemicolonInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        FinalStatementSemicolonInspection inspection = new FinalStatementSemicolonInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region MarkLogic 6.0

    public void testMarkLogic_Single_NoSemicolon() {
        getSettings().setImplementationVersion("marklogic/v6.0");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testMarkLogic_Single_Semicolon() {
        getSettings().setImplementationVersion("marklogic/v6.0");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 6.0 does not allow ';' at the end of the last statement."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.TRANSACTION_SEPARATOR));
    }

    public void testMarkLogic_Multiple_SemicolonAtEnd() {
        getSettings().setImplementationVersion("marklogic/v6.0");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: MarkLogic 6.0 does not allow ';' at the end of the last statement."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryElementType.TRANSACTION_SEPARATOR));
    }

    public void testMarkLogic_Multiple_NoSemicolonAtEnd() {
        getSettings().setImplementationVersion("marklogic/v6.0");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testMarkLogic_WithProlog() {
        getSettings().setImplementationVersion("marklogic/v6.0");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_1_0().getLabel());
        final XQueryFile file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
    // region Scripting Extension 1.0

    public void testScripting_Single_NoSemicolon() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_1_0_20070123().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testScripting_Single_Semicolon() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_1_0_20070123().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testScripting_Multiple_SemicolonAtEnd() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_1_0_20070123().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    public void testScripting_Multiple_NoSemicolonAtEnd() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_1_0_20070123().getLabel());
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: XQuery Scripting Extension 1.0 requires ';' at the end of each statement."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.INTEGER_LITERAL));
    }

    public void testScripting_WithProlog() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        getSettings().setXQueryVersion(XQuery.INSTANCE.getREC_1_0_20070123().getLabel());
        final XQueryFile file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq");

        final ProblemDescriptor[] problems = inspect(file, new FinalStatementSemicolonInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    // endregion
}
