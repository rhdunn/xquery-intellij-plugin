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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SaxonParserTest extends ParserTestCase {
    // region Saxon 9.4 :: MapConstructorEntry + MapConstructor

    public void testMapConstructorEntry() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_CompactWhitespace() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_MissingSeparator() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_MissingValueExpr() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testMapConstructorEntry_Multiple_MissingEntry() {
        final String expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Saxon 9.8 :: UnionType

    public void testUnionType() {
        final String expected = loadResource("tests/parser/saxon-9.8/UnionType.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.8/UnionType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnionType_CompactWhitespace() {
        final String expected = loadResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnionType_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnionType_MissingFirstType() {
        final String expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testUnionType_MissingNextType() {
        final String expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingNextType.txt");
        final XQueryFile actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingNextType.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
