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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi;

import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.*;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.descendants;

@SuppressWarnings("ConstantConditions")
public class ScriptingPsiTest extends ParserTestCase {
    // region XQueryConformanceCheck
    // region AssignmentExpr

    public void testAssignmentExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq");

        ScriptingAssignmentExpr assignmentExpr = descendants(file).findFirst(ScriptingAssignmentExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)assignmentExpr;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.ASSIGN_EQUAL));
    }

    // endregion
    // region BlockExpr

    public void testBlockExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq");

        ScriptingBlockExpr blockExpr = descendants(file).findFirst(ScriptingBlockExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)blockExpr;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_BLOCK));
    }

    // endregion
    // region BlockVarDecl

    public void testBlockVarDecl() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        ScriptingBlock blockPsi = children(functionDeclPsi).findFirst(ScriptingBlock.class).get();
        ScriptingBlockDecls blockDeclsPsi = children(blockPsi).findFirst(ScriptingBlockDecls.class).get();
        ScriptingBlockVarDecl blockVarDeclPsi = children(blockDeclsPsi).findFirst(ScriptingBlockVarDecl.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)blockVarDeclPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_DECLARE));
    }

    // endregion
    // region ExitExpr

    public void testExitExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq");

        ScriptingExitExpr exitExpr = descendants(file).findFirst(ScriptingExitExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)exitExpr;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_EXIT));
    }

    // endregion
    // region FunctionDecl

    public void testFunctionDecl_Simple() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        ScriptingCompatibilityAnnotation scriptingCompatibilityAnnotationPsi = children(annotatedDeclPsi).findFirst(ScriptingCompatibilityAnnotation.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) scriptingCompatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_SIMPLE));
    }

    public void testFunctionDecl_Sequential() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        ScriptingCompatibilityAnnotation scriptingCompatibilityAnnotationPsi = children(annotatedDeclPsi).findFirst(ScriptingCompatibilityAnnotation.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) scriptingCompatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_SEQUENTIAL));
    }

    // endregion
    // region VarDecl

    public void testVarDecl_Assignable() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        ScriptingCompatibilityAnnotation scriptingCompatibilityAnnotationPsi = children(annotatedDeclPsi).findFirst(ScriptingCompatibilityAnnotation.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) scriptingCompatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_ASSIGNABLE));
    }

    public void testVarDecl_Unassignable() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        ScriptingCompatibilityAnnotation scriptingCompatibilityAnnotationPsi = children(annotatedDeclPsi).findFirst(ScriptingCompatibilityAnnotation.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck) scriptingCompatibilityAnnotationPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_UNASSIGNABLE));
    }

    // endregion
    // region WhileExpr

    public void testWhileExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq");

        ScriptingWhileExpr whileExpr = descendants(file).findFirst(ScriptingWhileExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)whileExpr;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires Scripting Extension 1.0 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_WHILE));
    }

    // endregion
    // endregion
}
