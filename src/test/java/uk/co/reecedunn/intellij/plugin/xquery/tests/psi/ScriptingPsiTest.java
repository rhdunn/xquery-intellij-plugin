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

import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingCompatibilityAnnotation;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
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
    // endregion
}
