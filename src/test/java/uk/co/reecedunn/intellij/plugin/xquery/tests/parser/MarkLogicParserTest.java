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

import com.intellij.lang.ASTNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MarkLogicParserTest extends ParserTestCase {
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_Namespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Namespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Namespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Namespace_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Namespace_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Namespace_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Property.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Property.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Property_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Property_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryKindTest

    public void testBinaryKindTest() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
