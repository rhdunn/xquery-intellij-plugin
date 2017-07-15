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
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
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

    public void testEnclosedExpr() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(0));
    }

    public void testEnclosedExpr_MultiLine() {
        final XQueryFile file = parseResource("tests/folding/EnclosedExpr_MultiLine.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(1));

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), is(false));
        assertThat(descriptors[0].getDependencies(), is(notNullValue()));
        assertThat(descriptors[0].getDependencies().size(), is(0));
        assertThat(descriptors[0].getGroup(), is(nullValue()));
        assertThat(descriptors[0].getElement().getElementType(), is(XQueryElementType.FUNCTION_BODY));
        assertThat(descriptors[0].getRange().getStartOffset(), is(27));
        assertThat(descriptors[0].getRange().getEndOffset(), is(39));

        assertThat(builder.getPlaceholderText(descriptors[0].getElement()), is("{...}"));
        assertThat(builder.isCollapsedByDefault(descriptors[0].getElement()), is(false));
    }

    public void testEnclosedExpr_OnlyContentForDirElem() {
        final XQueryFile file = parseResource("tests/folding/EnclosedExpr_OnlyContentForDirElem.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(1));

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), is(false));
        assertThat(descriptors[0].getDependencies(), is(notNullValue()));
        assertThat(descriptors[0].getDependencies().size(), is(0));
        assertThat(descriptors[0].getGroup(), is(nullValue()));
        assertThat(descriptors[0].getElement().getElementType(), is(XQueryElementType.ENCLOSED_EXPR));
        assertThat(descriptors[0].getRange().getStartOffset(), is(3));
        assertThat(descriptors[0].getRange().getEndOffset(), is(10));

        assertThat(builder.getPlaceholderText(descriptors[0].getElement()), is("{...}"));
        assertThat(builder.isCollapsedByDefault(descriptors[0].getElement()), is(false));
    }

    public void testDirElemConstructor() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(0));
    }

    public void testDirElemConstructor_MultiLine() {
        final XQueryFile file = parseResource("tests/folding/DirElemConstructor_MultiLine.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(1));

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), is(false));
        assertThat(descriptors[0].getDependencies(), is(notNullValue()));
        assertThat(descriptors[0].getDependencies().size(), is(0));
        assertThat(descriptors[0].getGroup(), is(nullValue()));
        assertThat(descriptors[0].getElement().getElementType(), is(XQueryElementType.DIR_ELEM_CONSTRUCTOR));
        assertThat(descriptors[0].getRange().getStartOffset(), is(4));
        assertThat(descriptors[0].getRange().getEndOffset(), is(21));

        assertThat(builder.getPlaceholderText(descriptors[0].getElement()), is("..."));
        assertThat(builder.isCollapsedByDefault(descriptors[0].getElement()), is(false));
    }

    public void testDirElemConstructor_MultiLineWithAttributes() {
        final XQueryFile file = parseResource("tests/folding/DirElemConstructor_MultiLineWithAttributes.xq");
        final XQueryFoldingBuilder builder = new XQueryFoldingBuilder();

        final FoldingDescriptor[] descriptors = builder.buildFoldRegions(file, getDocument(file), false);
        assertThat(descriptors, is(notNullValue()));
        assertThat(descriptors.length, is(1));

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), is(false));
        assertThat(descriptors[0].getDependencies(), is(notNullValue()));
        assertThat(descriptors[0].getDependencies().size(), is(0));
        assertThat(descriptors[0].getGroup(), is(nullValue()));
        assertThat(descriptors[0].getElement().getElementType(), is(XQueryElementType.DIR_ELEM_CONSTRUCTOR));
        assertThat(descriptors[0].getRange().getStartOffset(), is(20));
        assertThat(descriptors[0].getRange().getEndOffset(), is(37));

        assertThat(builder.getPlaceholderText(descriptors[0].getElement()), is("..."));
        assertThat(builder.isCollapsedByDefault(descriptors[0].getElement()), is(false));
    }
}
