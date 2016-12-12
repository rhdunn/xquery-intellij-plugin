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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScriptingParserTest extends ParserTestCase {
    // region Scripting Extension 1.0 :: VarDecl

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    public void testVarDecl_Assignable() {
        final String expected = loadResource("tests/parser/scripting-1.0/VarDecl_Assignable.txt");
        final XQueryFile actual = parseResource("tests/parser/scripting-1.0/VarDecl_Assignable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Scripting Extension 1.0", reference="https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/#prod-xquery-VarDecl")
    public void testVarDecl_Unassignable() {
        final String expected = loadResource("tests/parser/scripting-1.0/VarDecl_Unassignable.txt");
        final XQueryFile actual = parseResource("tests/parser/scripting-1.0/VarDecl_Unassignable.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
