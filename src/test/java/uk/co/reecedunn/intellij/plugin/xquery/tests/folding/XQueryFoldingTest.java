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
package uk.co.reecedunn.intellij.plugin.xquery.tests.folding;

import com.intellij.lang.folding.FoldingDescriptor;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.editor.XQueryFoldingBuilder;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryFoldingTest extends ParserTestCase {
    public void testNoFoldingDescriptors() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(0));

        assertThat(builder.getPlaceholderText(file.getNode()), is("..."));
        assertThat(builder.isCollapsedByDefault(file.getNode()), is(false));
    }
}
