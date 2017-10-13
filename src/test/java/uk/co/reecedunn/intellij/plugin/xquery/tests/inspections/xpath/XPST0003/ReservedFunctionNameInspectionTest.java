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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xpath.XPST0003;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0003.ReservedFunctionNameInspection;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReservedFunctionNameInspectionTest extends InspectionTestCase {
    // region Inspection Details

    public void testDisplayName() {
        ReservedFunctionNameInspection inspection = new ReservedFunctionNameInspection();
        assertThat(inspection.getDisplayName(), is(notNullValue()));
    }

    public void testDescription() {
        ReservedFunctionNameInspection inspection = new ReservedFunctionNameInspection();
        assertThat(inspection.loadDescription(), is(notNullValue()));
    }

    // endregion
    // region FunctionCall
    // region MarkLogic 8.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testFunctionCall_MarkLogic80ReservedFunctionName_XQuery10() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic70() {
        getSettings().setImplementationVersion("marklogic/v7");
        final XQueryFile file = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic80() {
        getSettings().setImplementationVersion("marklogic/v8");
        final XQueryFile file = parseResource("tests/parser/marklogic-8.0/NodeTest_ArrayTest_FunctionCallLike.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_ARRAY_NODE));
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testFunctionCall_Scripting10ReservedFunctionName_XQuery10() {
        getSettings().setImplementationVersion("saxon/HE/v9.5");
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionCall_Scripting10ReservedFunctionName_W3C() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // endregion
    // region FunctionDecl
    // region MarkLogic 8.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testFunctionDecl_MarkLogic80ReservedFunctionName_XQuery10() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic70() {
        getSettings().setImplementationVersion("marklogic/v7");
        final XQueryFile file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic80() {
        getSettings().setImplementationVersion("marklogic/v8");
        final XQueryFile file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_ARRAY_NODE));
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testFunctionDecl_Scripting10ReservedFunctionName_XQuery10() {
        getSettings().setImplementationVersion("saxon/HE/v9.5");
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testFunctionDecl_Scripting10ReservedFunctionName_W3C() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // endregion
    // region NamedFunctionRef
    // region XQuery 1.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testNamedFunctionRef_XQuery10ReservedFunctionName() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved XQuery 1.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_IF));
    }

    // endregion
    // region XQuery 3.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testNamedFunctionRef_XQuery30ReservedFunctionName() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved XQuery 3.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_FUNCTION));
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @SuppressWarnings("ConstantConditions")
    public void testNamedFunctionRef_Scripting10ReservedFunctionName_XQuery10() {
        getSettings().setImplementationVersion("saxon/HE/v9.5");
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(0));
    }

    @SuppressWarnings("ConstantConditions")
    public void testNamedFunctionRef_Scripting10ReservedFunctionName_W3C() {
        getSettings().setImplementationVersion("w3c/spec/v1ed");
        final XQueryFile file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq");

        final ProblemDescriptor[] problems = inspect(file, new ReservedFunctionNameInspection());
        assertThat(problems, is(notNullValue()));
        assertThat(problems.length, is(1));

        assertThat(problems[0].getHighlightType(), is(ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        assertThat(problems[0].getDescriptionTemplate(), is("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."));
        assertThat(problems[0].getPsiElement().getNode().getElementType(), is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // endregion
}
