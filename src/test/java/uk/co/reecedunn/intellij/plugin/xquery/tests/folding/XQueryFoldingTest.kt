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
package uk.co.reecedunn.intellij.plugin.xquery.tests.folding

import uk.co.reecedunn.intellij.plugin.xquery.editor.XQueryFoldingBuilder
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat

class XQueryFoldingTest : ParserTestCase() {
    // region Unsupported Element

    fun testNoFoldingDescriptors() {
        val file = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))

        assertThat<String>(builder.getPlaceholderText(file.node), `is`("..."))
        assertThat(builder.isCollapsedByDefault(file.node), `is`(false))
    }

    // endregion
    // region EnclosedExpr

    fun testEnclosedExpr() {
        val file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))
    }

    fun testEnclosedExpr_MultiLine() {
        val file = parseResource("tests/folding/EnclosedExpr_MultiLine.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(1))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.FUNCTION_BODY))
        assertThat(descriptors[0].range.startOffset, `is`(27))
        assertThat(descriptors[0].range.endOffset, `is`(39))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
    }

    fun testEnclosedExpr_OnlyContentForDirElem() {
        val file = parseResource("tests/folding/EnclosedExpr_OnlyContentForDirElem.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(1))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.ENCLOSED_EXPR))
        assertThat(descriptors[0].range.startOffset, `is`(3))
        assertThat(descriptors[0].range.endOffset, `is`(10))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
    }

    fun testEnclosedExpr_OnlyContentForDirElem_MultiLineAttributes() {
        val file = parseResource("tests/folding/EnclosedExpr_OnlyContentForDirElem_MultiLineAttributes.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(2))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
        assertThat(descriptors[0].range.startOffset, `is`(3))
        assertThat(descriptors[0].range.endOffset, `is`(32))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

        assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[1].dependencies, `is`(notNullValue()))
        assertThat(descriptors[1].dependencies.size, `is`(0))
        assertThat(descriptors[1].group, `is`(nullValue()))
        assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.ENCLOSED_EXPR))
        assertThat(descriptors[1].range.startOffset, `is`(22))
        assertThat(descriptors[1].range.endOffset, `is`(29))

        assertThat<String>(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
        assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
    }

    // endregion
    // region DirElemConstructor

    fun testDirElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))
    }

    fun testDirElemConstructor_Incomplete() {
        val file = parseResource("tests/folding/DirElemConstructor_Incomplete.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))
    }

    fun testDirElemConstructor_IncompleteNamespace() {
        val file = parseResource("tests/folding/DirElemConstructor_IncompleteNamespace.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))
    }

    fun testDirElemConstructor_MultiLine() {
        val file = parseResource("tests/folding/DirElemConstructor_MultiLine.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(1))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
        assertThat(descriptors[0].range.startOffset, `is`(4))
        assertThat(descriptors[0].range.endOffset, `is`(21))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
    }

    fun testDirElemConstructor_MultiLineWithAttributes() {
        val file = parseResource("tests/folding/DirElemConstructor_MultiLineWithAttributes.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(1))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
        assertThat(descriptors[0].range.startOffset, `is`(20))
        assertThat(descriptors[0].range.endOffset, `is`(37))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
    }

    // endregion
    // region Comment

    fun testComment() {
        val file = parseResource("tests/parser/xquery-1.0/Comment.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))
    }

    fun testComment_MultiLine() {
        val file = parseResource("tests/folding/Comment_MultiLine.xq")
        val builder = XQueryFoldingBuilder()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(1))

        assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
        assertThat(descriptors[0].dependencies, `is`(notNullValue()))
        assertThat(descriptors[0].dependencies.size, `is`(0))
        assertThat(descriptors[0].group, `is`(nullValue()))
        assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
        assertThat(descriptors[0].range.startOffset, `is`(0))
        assertThat(descriptors[0].range.endOffset, `is`(18))

        assertThat<String>(builder.getPlaceholderText(descriptors[0].element), `is`("(...)"))
        assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
    }

    // endregion
}
