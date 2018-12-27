/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.folding

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.foldable.FoldingBuilderImpl
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Code Folding - XQuery FoldingBuilder")
private class XQueryFoldingTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(XQueryFoldingTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Test
    @DisplayName("no foldable elements")
    fun testNoFoldingDescriptors() {
        val file = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq")
        val builder = FoldingBuilderImpl()

        val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))

        assertThat(builder.getPlaceholderText(file.node), `is`(nullValue()))
        assertThat(builder.isCollapsedByDefault(file.node), `is`(false))
    }

    @Nested
    @DisplayName("EnclosedExpr")
    internal inner class EnclosedExpr {
        @Test
        @DisplayName("single line")
        fun testEnclosedExpr() {
            val file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun testEnclosedExpr_MultiLine() {
            val file = parseResource("tests/folding/EnclosedExpr_MultiLine.xq")
            val builder = FoldingBuilderImpl()

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

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("only content of a DirElemConstructor")
        fun testEnclosedExpr_OnlyContentForDirElem() {
            val file = parseResource("tests/folding/EnclosedExpr_OnlyContentForDirElem.xq")
            val builder = FoldingBuilderImpl()

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

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("only content of a DirElemConstructor with attributes across multiple lines")
        fun testEnclosedExpr_OnlyContentForDirElem_MultiLineAttributes() {
            val file = parseResource("tests/folding/EnclosedExpr_OnlyContentForDirElem_MultiLineAttributes.xq")
            val builder = FoldingBuilderImpl()

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

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.ENCLOSED_EXPR))
            assertThat(descriptors[1].range.startOffset, `is`(22))
            assertThat(descriptors[1].range.endOffset, `is`(29))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("single line")
        fun testDirElemConstructor() {
            val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single line; self-closing")
        fun testDirElemConstructor_SelfClosing() {
            val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete")
        fun testDirElemConstructor_Incomplete() {
            val file = parseResource("tests/folding/DirElemConstructor_Incomplete.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after")
        fun testDirElemConstructor_IncompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor_IncompleteNamespace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after inside a direct element")
        fun testDirElemConstructor_Inner_IncompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor_Inner_IncompleteNamespace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(2))
            assertThat(descriptors[0].range.endOffset, `is`(9))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun testDirElemConstructor_MultiLine() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLine.xq")
            val builder = FoldingBuilderImpl()

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

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; self closing")
        fun testDirElemConstructor_MultiLine_() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLine_SelfClosing.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines with attributes")
        fun testDirElemConstructor_MultiLineWithAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLineWithAttributes.xq")
            val builder = FoldingBuilderImpl()

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

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("Comment")
    internal inner class Comment {
        @Test
        @DisplayName("single line")
        fun testComment() {
            val file = parseResource("tests/parser/xquery-1.0/Comment.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun testComment_MultiLine_EmptyText() {
            val file = parseResource("tests/folding/Comment_Empty.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(5))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:...:)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun testComment_MultiLine() {
            val file = parseResource("tests/folding/Comment_MultiLine.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; xqdoc")
        fun testComment_MultiLine_XQDoc() {
            val file = parseResource("tests/folding/Comment_MultiLine_XQDoc.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun testComment_MultiLine_Incomplete() {
            val file = parseResource("tests/folding/Comment_MultiLine_Incomplete.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete; xqdoc")
        fun testComment_MultiLine_Incomplete_XQDoc() {
            val file = parseResource("tests/folding/Comment_MultiLine_Incomplete_XQDoc.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, getDocument(file), false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(16))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }
}
