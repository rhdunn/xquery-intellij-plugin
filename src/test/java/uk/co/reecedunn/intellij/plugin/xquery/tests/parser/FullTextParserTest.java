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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FullTextParserTest extends ParserTestCase {
    // region Full Text 1.0 :: FTOptionDecl

    public void testFTOptionDecl_MissingFTMatchOptions() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTOptionDecl_MissingFTMatchOptions.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Full Text 1.0 :: FTMatchOptions

    public void testFTMatchOptions_MissingFTOption() {
        final String expected = loadResource("tests/parser/full-text-1.0/FTMatchOptions_MissingFTMatchOption.txt");
        final XQueryFile actual = parseResource("tests/parser/full-text-1.0/FTMatchOptions_MissingFTMatchOption.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
