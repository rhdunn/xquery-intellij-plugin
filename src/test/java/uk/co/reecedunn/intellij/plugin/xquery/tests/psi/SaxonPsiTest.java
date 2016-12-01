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

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
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
public class SaxonPsiTest extends ParserTestCase {
    // region XQueryConformanceCheck
    // region MapConstructorEntry

    public void testMapConstructorEntry() {
        final XQueryFile file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq");

        XQueryMapConstructor mapConstructorPsi = descendants(file).findFirst(XQueryMapConstructor.class).get();
        XQueryMapConstructorEntry mapConstructorEntryPsi = children(mapConstructorPsi).findFirst(XQueryMapConstructorEntry.class).get();
        XQueryConformanceCheck versioned = (XQueryConformanceCheck)mapConstructorEntryPsi;

        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/1.0-update")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("w3c/3.1")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.4/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.5/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.6/3.0")), is(true));
        assertThat(versioned.conformsTo(Implementations.getItemById("saxon/EE/v9.7/3.0")), is(false));

        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v6/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v7/1.0-ml")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0")), is(false));
        assertThat(versioned.conformsTo(Implementations.getItemById("marklogic/v8/1.0-ml")), is(false));

        assertThat(versioned.getConformanceErrorMessage(),
                is("XPST0003: Use ':=' for Saxon 9.4 to 9.6, and ':' for XQuery 3.1 and MarkLogic."));

        assertThat(versioned.getConformanceElement(), is(notNullValue()));
        assertThat(versioned.getConformanceElement().getNode().getElementType(),
                is(XQueryTokenType.ASSIGN_EQUAL));
    }

    // endregion
    // endregion
}
