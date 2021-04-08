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
import uk.co.reecedunn.intellij.plugin.xquery.lang.editor.folding.XQueryFoldingBuilder
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
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (79) TryClause ; XQuery 3.1 EBNF (81) CatchClause")
    internal inner class TryCatchExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/TryCatchExpr/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/TryCatchExpr/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.TRY_CATCH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(11))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.CATCH_CLAUSE))
            assertThat(descriptors[1].range.startOffset, `is`(20))
            assertThat(descriptors[1].range.endOffset, `is`(27))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (135) OrderedExpr")
    internal inner class OrderedExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/OrderedExpr/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/OrderedExpr/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.ORDERED_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (136) UnorderedExpr")
    internal inner class UnorderedExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/UnorderedExpr/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/UnorderedExpr/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.UNORDERED_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(23))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (144) DirAttributeValue")
    internal inner class DirAttributeValue {
        @Test
        @DisplayName("single expressions ; single line")
        fun single_singleLine() {
            val file = parseResource("tests/folding/DirAttributeValue/Single_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single expressions ; multiple lines")
        fun single_multipleLines() {
            val file = parseResource("tests/folding/DirAttributeValue/Single_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(29))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; single line")
        fun multiple_singleLine() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (first)")
        fun multiple_multipleLinesFirst() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(43))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (last)")
        fun multiple_multipleLinesLast() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(43))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(24))
            assertThat(descriptors[1].range.endOffset, `is`(37))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (both)")
        fun multiple_multipleLinesBoth() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(3))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(47))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))

            assertThat(descriptors[2].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[2].dependencies, `is`(notNullValue()))
            assertThat(descriptors[2].dependencies.size, `is`(0))
            assertThat(descriptors[2].group, `is`(nullValue()))
            assertThat(descriptors[2].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[2].range.startOffset, `is`(28))
            assertThat(descriptors[2].range.endOffset, `is`(41))

            assertThat(builder.getPlaceholderText(descriptors[2].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[2].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (147) DirElemContent")
    internal inner class DirElemContent {
        @Test
        @DisplayName("single expressions ; single line")
        fun single_singleLine() {
            val file = parseResource("tests/folding/DirElemContent/Single_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single expressions ; multiple lines")
        fun single_multipleLines() {
            val file = parseResource("tests/folding/DirElemContent/Single_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; single line")
        fun multiple_singleLine() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (first)")
        fun multiple_multipleLinesFirst() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (last)")
        fun multiple_multipleLinesLast() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(18))
            assertThat(descriptors[0].range.endOffset, `is`(31))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (both)")
        fun multiple_multipleLinesBoth() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(22))
            assertThat(descriptors[1].range.endOffset, `is`(35))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (165) CompCommentConstructor")
    internal inner class CompCommentConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompCommentConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompCommentConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (156) CompDocConstructor")
    internal inner class CompDocConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompDocConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompDocConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_DOC_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(9))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompElemConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompElemConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(13))
            assertThat(descriptors[0].range.endOffset, `is`(26))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(19))
            assertThat(descriptors[0].range.endOffset, `is`(32))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(20))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(21))
            assertThat(descriptors[1].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (159) CompAttrConstructor")
    internal inner class CompAttrConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompAttrConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompAttrConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(15))
            assertThat(descriptors[0].range.endOffset, `is`(28))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(21))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(23))
            assertThat(descriptors[1].range.endOffset, `is`(36))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (160) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("NCName node name ; single line")
        fun ncname_singleLine() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/NCNameNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("NCName node name ; multiple lines")
        fun ncname_multipleLines() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/NCNameNodeName_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(15))
            assertThat(descriptors[0].range.endOffset, `is`(28))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(21))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(23))
            assertThat(descriptors[1].range.endOffset, `is`(36))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (164) CompTextConstructor")
    internal inner class CompTextConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompTextConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompTextConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_TEXT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(5))
            assertThat(descriptors[0].range.endOffset, `is`(18))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (166) CompPIConstructor")
    internal inner class CompPIConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompPIConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompPIConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(28))
            assertThat(descriptors[0].range.endOffset, `is`(41))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(23))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(34))
            assertThat(descriptors[0].range.endOffset, `is`(47))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(23))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(36))
            assertThat(descriptors[1].range.endOffset, `is`(49))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CurlyArrayConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CurlyArrayConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (37) EnclosedExpr ; XQuery 4.0 ED EBNF (43) WithExpr")
    internal inner class WithExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/WithExpr/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/WithExpr/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.WITH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(14))
            assertThat(descriptors[0].range.endOffset, `is`(27))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple line namespaces")
        fun multipleLineNamespaces() {
            val file = parseResource("tests/folding/WithExpr/MultiLineNamespaces.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.WITH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(55))
            assertThat(descriptors[0].range.endOffset, `is`(68))

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
        @DisplayName("text; multiple lines")
        fun text_multipleLines() {
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLine.xq")

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
        @DisplayName("attributes; multiple lines; self closing")
        fun attributes_multipleLines_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor/Attributes_MultiLine_SelfClosing.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("text; multiple lines with attributes")
        fun text_multipleLinesWithAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLineWithAttributes.xq")

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
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLineWithAttributesAndSpace.xq")

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
    @DisplayName("XQuery 3.1 EBNF (170) MapConstructor")
    internal inner class MapConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/MapConstructor/SingleLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/MapConstructor/MultiLine.xq")

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(30))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
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
