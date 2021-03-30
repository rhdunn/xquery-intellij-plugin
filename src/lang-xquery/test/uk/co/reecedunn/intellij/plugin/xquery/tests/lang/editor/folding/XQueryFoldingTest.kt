/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.folding

import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.folding.XQueryFoldingBuilder
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Code Folding - XQuery")
private class XQueryFoldingTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryFoldingTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    private val builder: FoldingBuilderEx = XQueryFoldingBuilder()

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FunctionDecl/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FunctionDecl/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.FUNCTION_DECL))
            assertThat(descriptors[0].range.startOffset, `is`(27))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 3.1 EBNF (169) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/InlineFunctionExpr/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/InlineFunctionExpr/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.INLINE_FUNCTION_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(12))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 4.0 ED EBNF (109) ThinArrowTarget")
    internal inner class ThinArrowTarget {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/ThinArrowTarget/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/ThinArrowTarget/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.ARROW_INLINE_FUNCTION_CALL))
            assertThat(descriptors[0].range.startOffset, `is`(5))
            assertThat(descriptors[0].range.endOffset, `is`(17))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirElemConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single line; self-closing")
        fun singleLine_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor/SelfClosing.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete")
        fun incomplete() {
            val file = parseResource("tests/folding/DirElemConstructor/Incomplete.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after")
        fun incompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor/IncompleteNamespace.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after inside a direct element")
        fun inner_IncompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor/Inner_IncompleteNamespace.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
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
        fun multipleLines() {
            val file = parseResource("tests/folding/DirElemConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
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
        fun multipleLines_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor/MultiLine_SelfClosing.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines with attributes")
        fun multipleLinesWithAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor/MultiLineWithAttributes.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
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

        @Test
        @DisplayName("multiple lines with attributes; space after attribute list")
        fun multipleLinesWithAttributesAndSpace() {
            val file = parseResource("tests/folding/DirElemConstructor/MultiLineWithAttributesAndSpace.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr only")
        fun enclosedExprOnly() {
            val file = parseResource("tests/folding/DirElemConstructor/EnclosedExprOnly.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
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
        @DisplayName("EnclosedExpr only, with attributes across multiple lines")
        fun enclosedExprOnly_multipleLineAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor/EnclosedExprOnly_MultiLineAttributes.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
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
    @DisplayName("XQuery 3.1 EBNF (149) DirCommentConstructor")
    internal inner class DirCommentConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirCommentConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/DirCommentConstructor/Empty.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(8))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--...-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/DirCommentConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--Lorem ipsum.-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/DirCommentConstructor/MultiLine_Incomplete.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(17))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--Lorem ipsum.-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (231) Comment")
    internal inner class Comment {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/Comment/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/Comment/Empty.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(5))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:...:)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/Comment/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; xqdoc")
        fun multipleLines_XQDoc() {
            val file = parseResource("tests/folding/Comment/MultiLine_XQDoc.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/Comment/MultiLine_Incomplete.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete; xqdoc")
        fun multipleLines_Incomplete_XQDoc() {
            val file = parseResource("tests/folding/Comment/MultiLine_Incomplete_XQDoc.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(16))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }
}
