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
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQDocParserTest extends ParserTestCase {
    // region xqDoc :: XQDocComment

    @Specification(name="xqDoc", reference="https://raw.githubusercontent.com/xquery/xquerydoc/master/ebnf/XQDocComments.ebnf")
    public void testXQDocComment() {
        final String expected = loadResource("tests/parser/xqdoc/XQDocComment.txt");
        final XQueryFile actual = parseResource("tests/parser/xqdoc/XQDocComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="xqDoc", reference="https://raw.githubusercontent.com/xquery/xquerydoc/master/ebnf/XQDocComments.ebnf")
    public void testXQDocComment_Multiline() {
        final String expected = loadResource("tests/parser/xqdoc/XQDocComment_Multiline.txt");
        final XQueryFile actual = parseResource("tests/parser/xqdoc/XQDocComment_Multiline.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="xqDoc", reference="https://raw.githubusercontent.com/xquery/xquerydoc/master/ebnf/XQDocComments.ebnf")
    public void testXQDocComment_UnclosedComment() {
        final String expected = loadResource("tests/parser/xqdoc/XQDocComment_UnclosedComment.txt");
        final XQueryFile actual = parseResource("tests/parser/xqdoc/XQDocComment_UnclosedComment.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
