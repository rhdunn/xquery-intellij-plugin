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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi;

import uk.co.reecedunn.intellij.plugin.xquery.ast.basex.BaseXUpdateExpr;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.descendants;

@SuppressWarnings("ConstantConditions")
public class BaseXPsiTest extends ParserTestCase {
    // region XQueryConformanceCheck
    // region UpdateExpr

    public void testUpdateExpr() {
        final XQueryFile file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq");

        BaseXUpdateExpr updateExpr = descendants(file).findFirst(BaseXUpdateExpr.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)updateExpr;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-scripting")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1-update")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.4/3.1-update")), is(true));

        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/1.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.0-update")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("basex/v8.5/3.1-update")), is(true));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: This expression requires BaseX 8.4 or later."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.K_UPDATE));
    }

    // endregion
    // endregion
}
