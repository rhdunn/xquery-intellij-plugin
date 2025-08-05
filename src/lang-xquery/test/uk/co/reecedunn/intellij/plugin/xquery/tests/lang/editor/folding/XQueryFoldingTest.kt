// Copyright (C) 2017-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.folding

import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.editor.folding.XQueryFoldingBuilder
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier", "ClassName")
@DisplayName("IntelliJ - Custom Language Support - Code Folding - XQuery")
class XQueryFoldingTest : ParsingTestCase<XQueryModule>(XQuery) {
    override val pluginId: PluginId = PluginId.getId("XQueryFoldingTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    private val builder: FoldingBuilderEx = XQueryFoldingBuilder()

    fun buildFoldRegions(root: PsiFile, quick: Boolean = false): Array<FoldingDescriptor> {
        return builder.buildFoldRegions(root, root.document!!, quick)
    }

    fun getPlaceholderText(descriptor: FoldingDescriptor): String? {
        return builder.getPlaceholderText(descriptor.element, descriptor.range)
    }

    fun isCollapsedByDefault(descriptor: FoldingDescriptor): Boolean {
        return builder.isCollapsedByDefault(descriptor.element)
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FunctionDecl/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FunctionDecl/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.FUNCTION_DECL))
            assertThat(descriptors[0].range.startOffset, `is`(27))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 3.1 EBNF (169) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/InlineFunctionExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/InlineFunctionExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.INLINE_FUNCTION_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(12))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody ; XQuery 4.0 ED EBNF (109) ThinArrowTarget")
    internal inner class ThinArrowTarget {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/ThinArrowTarget/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/ThinArrowTarget/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.ARROW_INLINE_FUNCTION_CALL))
            assertThat(descriptors[0].range.startOffset, `is`(5))
            assertThat(descriptors[0].range.endOffset, `is`(17))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (79) TryClause ; XQuery 3.1 EBNF (81) CatchClause")
    internal inner class TryCatchExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/TryCatchExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/TryCatchExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.TRY_CATCH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(11))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.CATCH_CLAUSE))
            assertThat(descriptors[1].range.startOffset, `is`(20))
            assertThat(descriptors[1].range.endOffset, `is`(27))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (135) OrderedExpr")
    internal inner class OrderedExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/OrderedExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/OrderedExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.ORDERED_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (136) UnorderedExpr")
    internal inner class UnorderedExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/UnorderedExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/UnorderedExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.UNORDERED_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(23))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (144) DirAttributeValue")
    internal inner class DirAttributeValue {
        @Test
        @DisplayName("single expressions ; single line")
        fun single_singleLine() {
            val file = parseResource("tests/folding/DirAttributeValue/Single_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single expressions ; multiple lines")
        fun single_multipleLines() {
            val file = parseResource("tests/folding/DirAttributeValue/Single_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(29))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; single line")
        fun multiple_singleLine() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (first)")
        fun multiple_multipleLinesFirst() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(43))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (last)")
        fun multiple_multipleLinesLast() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(43))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(24))
            assertThat(descriptors[1].range.endOffset, `is`(37))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (both)")
        fun multiple_multipleLinesBoth() {
            val file = parseResource("tests/folding/DirAttributeValue/Multiple_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(3))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(47))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[1].range.startOffset, `is`(12))
            assertThat(descriptors[1].range.endOffset, `is`(25))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))

            assertThat(descriptors[2].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[2].dependencies, `is`(notNullValue()))
            assertThat(descriptors[2].dependencies.size, `is`(0))
            assertThat(descriptors[2].group, `is`(nullValue()))
            assertThat(descriptors[2].element.elementType, `is`(XQueryElementType.DIR_ATTRIBUTE_VALUE))
            assertThat(descriptors[2].range.startOffset, `is`(28))
            assertThat(descriptors[2].range.endOffset, `is`(41))

            assertThat(getPlaceholderText(descriptors[2]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[2]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (147) DirElemContent")
    internal inner class DirElemContent {
        @Test
        @DisplayName("single expressions ; single line")
        fun single_singleLine() {
            val file = parseResource("tests/folding/DirElemContent/Single_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single expressions ; multiple lines")
        fun single_multipleLines() {
            val file = parseResource("tests/folding/DirElemContent/Single_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("single expressions ; multiple lines ; whitespace in element")
        fun single_multipleLines_whitespaceInElement() {
            val file = parseResource("tests/folding/DirElemContent/Single_MultiLine_WhitespaceInElement.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; single line")
        fun multiple_singleLine() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (first)")
        fun multiple_multipleLinesFirst() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (last)")
        fun multiple_multipleLinesLast() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(18))
            assertThat(descriptors[0].range.endOffset, `is`(31))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple expressions ; multiple lines (both)")
        fun multiple_multipleLinesBoth() {
            val file = parseResource("tests/folding/DirElemContent/Multiple_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(22))
            assertThat(descriptors[1].range.endOffset, `is`(35))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (165) CompCommentConstructor")
    internal inner class CompCommentConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompCommentConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompCommentConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (156) CompDocConstructor")
    internal inner class CompDocConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompDocConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompDocConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_DOC_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(9))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompElemConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompElemConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(13))
            assertThat(descriptors[0].range.endOffset, `is`(26))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(19))
            assertThat(descriptors[0].range.endOffset, `is`(32))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompElemConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(8))
            assertThat(descriptors[0].range.endOffset, `is`(20))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(21))
            assertThat(descriptors[1].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (159) CompAttrConstructor")
    internal inner class CompAttrConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompAttrConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompAttrConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(15))
            assertThat(descriptors[0].range.endOffset, `is`(28))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(21))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompAttrConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_ATTR_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(23))
            assertThat(descriptors[1].range.endOffset, `is`(36))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (160) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("NCName node name ; single line")
        fun ncname_singleLine() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/NCNameNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("NCName node name ; multiple lines")
        fun ncname_multipleLines() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/NCNameNodeName_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(15))
            assertThat(descriptors[0].range.endOffset, `is`(28))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(21))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompNamespaceConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(23))
            assertThat(descriptors[1].range.endOffset, `is`(36))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (164) CompTextConstructor")
    internal inner class CompTextConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CompTextConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CompTextConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_TEXT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(5))
            assertThat(descriptors[0].range.endOffset, `is`(18))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (166) CompPIConstructor")
    internal inner class CompPIConstructor {
        @Test
        @DisplayName("EQName node name ; single line")
        fun eqname_singleLine() {
            val file = parseResource("tests/folding/CompPIConstructor/EQNameNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EQName node name ; multiple lines")
        fun eqname_multipleLines() {
            val file = parseResource("tests/folding/CompPIConstructor/EQNameNodeName_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(28))
            assertThat(descriptors[0].range.endOffset, `is`(41))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; single line")
        fun enclosedExpr_singleLine() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (first)")
        fun enclosedExpr_multipleLinesFirst() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineFirst.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(23))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (last)")
        fun enclosedExpr_multipleLinesLast() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineLast.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(34))
            assertThat(descriptors[0].range.endOffset, `is`(47))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr node name ; multiple lines (both)")
        fun enclosedExpr_multipleLinesBoth() {
            val file = parseResource("tests/folding/CompPIConstructor/EnclosedExprNodeName_MultiLineBoth.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(23))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
            assertThat(descriptors[1].range.startOffset, `is`(36))
            assertThat(descriptors[1].range.endOffset, `is`(49))

            assertThat(getPlaceholderText(descriptors[1]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[1]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/CurlyArrayConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/CurlyArrayConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr ; XQuery 4.0 ED EBNF (43) WithExpr")
    internal inner class WithExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/WithExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/WithExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.WITH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(14))
            assertThat(descriptors[0].range.endOffset, `is`(27))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple line namespaces")
        fun multipleLineNamespaces() {
            val file = parseResource("tests/folding/WithExpr/MultiLineNamespaces.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.WITH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(55))
            assertThat(descriptors[0].range.endOffset, `is`(68))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (102) ValidateExpr")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/ValidateExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/ValidateExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.VALIDATE_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(25))
            assertThat(descriptors[0].range.endOffset, `is`(38))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (104) ExtensionExpr")
    internal inner class ExtensionExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/ExtensionExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/ExtensionExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.EXTENSION_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(11))
            assertThat(descriptors[0].range.endOffset, `is`(24))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirElemConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single line; self-closing")
        fun singleLine_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor/SelfClosing.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete")
        fun incomplete() {
            val file = parseResource("tests/folding/DirElemConstructor/Incomplete.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after")
        fun incompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor/IncompleteNamespace.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after inside a direct element")
        fun inner_IncompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor/Inner_IncompleteNamespace.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(2))
            assertThat(descriptors[0].range.endOffset, `is`(9))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("text; multiple lines")
        fun text_multipleLines() {
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("attributes; multiple lines; self closing")
        fun attributes_multipleLines_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor/Attributes_MultiLine_SelfClosing.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("text; multiple lines with attributes")
        fun text_multipleLinesWithAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLineWithAttributes.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines with attributes; space after attribute list")
        fun multipleLinesWithAttributesAndSpace() {
            val file = parseResource("tests/folding/DirElemConstructor/Text_MultiLineWithAttributesAndSpace.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(getPlaceholderText(descriptors[0]), `is`("..."))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (149) DirCommentConstructor")
    internal inner class DirCommentConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirCommentConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/DirCommentConstructor/Empty.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(8))

            assertThat(getPlaceholderText(descriptors[0]), `is`("<!--...-->"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/DirCommentConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(getPlaceholderText(descriptors[0]), `is`("<!--Lorem ipsum.-->"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/DirCommentConstructor/MultiLine_Incomplete.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(17))

            assertThat(getPlaceholderText(descriptors[0]), `is`("<!--Lorem ipsum.-->"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (170) MapConstructor")
    internal inner class MapConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/MapConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/MapConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(30))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (177) StringConstructor ; XQuery 3.1 EBNF (180) StringConstructorInterpolation")
    internal inner class StringConstructorInterpolation {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/StringConstructorInterpolation/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/StringConstructorInterpolation/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.STRING_CONSTRUCTOR_INTERPOLATION))
            assertThat(descriptors[0].range.startOffset, `is`(7))
            assertThat(descriptors[0].range.endOffset, `is`(18))

            assertThat(getPlaceholderText(descriptors[0]), `is`("`{...}`"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (231) Comment")
    internal inner class Comment {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/Comment/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/Comment/Empty.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(5))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(:...:)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/Comment/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(: Lorem ipsum. :)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; xqdoc")
        fun multipleLines_XQDoc() {
            val file = parseResource("tests/folding/Comment/MultiLine_XQDoc.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(:~ Lorem ipsum. :)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/Comment/MultiLine_Incomplete.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(: Lorem ipsum. :)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete; xqdoc")
        fun multipleLines_Incomplete_XQDoc() {
            val file = parseResource("tests/folding/Comment/MultiLine_Incomplete_XQDoc.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(16))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(:~ Lorem ipsum. :)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 with Full Text EBNF (145) FTWeight")
    internal inner class FTWeight {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FTWeight/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FTWeight/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.FT_WEIGHT))
            assertThat(descriptors[0].range.startOffset, `is`(35))
            assertThat(descriptors[0].range.endOffset, `is`(44))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 with Full Text EBNF (153) FTWordsValue")
    internal inner class FTWordsValue {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FTWordsValue/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FTWordsValue/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.FT_WORDS_VALUE))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery Full Text 1.0 EBNF (154) FTExtensionSelection")
    internal inner class FTExtensionSelection {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FTExtensionSelection/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FTExtensionSelection/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.FT_EXTENSION_SELECTION))
            assertThat(descriptors[0].range.startOffset, `is`(36))
            assertThat(descriptors[0].range.endOffset, `is`(51))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
    internal inner class TransformWithExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/TransformWithExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/TransformWithExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.TRANSFORM_WITH_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(18))
            assertThat(descriptors[0].range.endOffset, `is`(49))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 EBNF (154) Block ; XQuery Scripting Extension 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl_Scripting {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FunctionDecl/SingleLine_Block.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FunctionDecl/MultiLine_Block.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.BLOCK))
            assertThat(descriptors[0].range.startOffset, `is`(38))
            assertThat(descriptors[0].range.endOffset, `is`(50))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 EBNF (154) Block ; XQuery Scripting Extension 1.0 EBNF (153) BlockExpr")
    internal inner class BlockExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/BlockExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/BlockExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.BLOCK))
            assertThat(descriptors[0].range.startOffset, `is`(6))
            assertThat(descriptors[0].range.endOffset, `is`(18))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 EBNF (161) WhileBody ; XQuery Scripting Extension 1.0 EBNF (160) WhileExpr")
    internal inner class WhileExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/WhileExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/WhileExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.WHILE_BODY))
            assertThat(descriptors[0].range.startOffset, `is`(16))
            assertThat(descriptors[0].range.endOffset, `is`(36))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (12) UpdateExpr")
    internal inner class UpdateExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/UpdateExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/UpdateExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.UPDATE_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(14))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (30) BinaryConstructor")
    internal inner class BinaryConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/BinaryConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/BinaryConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.BINARY_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(7))
            assertThat(descriptors[0].range.endOffset, `is`(19))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (35) LambdaFunctionExpr")
    internal inner class LambdaFunctionExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/LambdaFunctionExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/LambdaFunctionExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.LAMBDA_FUNCTION_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(getPlaceholderText(descriptors[0]), `is`("_{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (50) BooleanConstructor")
    internal inner class BooleanConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/BooleanConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/BooleanConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.BOOLEAN_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(13))
            assertThat(descriptors[0].range.endOffset, `is`(27))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (54) NumberConstructor")
    internal inner class NumberConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/NumberConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/NumberConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.NUMBER_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(12))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (58) NullConstructor")
    internal inner class NullConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/NullConstructor/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/NullConstructor/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.NULL_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(10))
            assertThat(descriptors[0].range.endOffset, `is`(22))

            assertThat(getPlaceholderText(descriptors[0]), `is`("{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (81) ContextItemFunctionExpr")
    internal inner class ContextItemFunctionExpr {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/ContextItemFunctionExpr/SingleLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/ContextItemFunctionExpr/MultiLine.xq")

            val descriptors = buildFoldRegions(file)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(14))

            assertThat(getPlaceholderText(descriptors[0]), `is`(".{...}"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }
}
