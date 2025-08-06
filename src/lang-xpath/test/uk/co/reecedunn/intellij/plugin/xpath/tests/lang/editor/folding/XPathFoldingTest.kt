// Copyright (C) 2017-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.editor.folding

import com.intellij.lang.Language
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetDocument
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.editor.folding.XPathFoldingBuilder
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Code Folding - XPath")
class XPathFoldingTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XPathFoldingTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()
        requiresPsiFileGetDocument()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XPath = res.toPsiFile(resource, project)

    private val builder: FoldingBuilderEx = XPathFoldingBuilder()

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
    @DisplayName("XPath 3.1 EBNF (4) FunctionBody ; XPath 3.1 EBNF (68) InlineFunctionExpr")
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
    @DisplayName("XPath 3.1 EBNF (4) FunctionBody ; XPath 4.0 ED EBNF (39) ThinArrowTarget")
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
    @DisplayName("XPath 3.1 EBNF (5) EnclosedExpr ; XPath 4.0 ED EBNF (9) WithExpr")
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
    @DisplayName("XPath 3.1 EBNF (5) EnclosedExpr ; XQuery 3.1 EBNF (75) CurlyArrayConstructor")
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
    @DisplayName("XPath 3.1 EBNF (70) MapConstructor")
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
    @DisplayName("XPath 3.1 EBNF (121) Comment")
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
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
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
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(:...:)"))
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
            assertThat(descriptors[0].element.elementType, `is`(XPathElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(getPlaceholderText(descriptors[0]), `is`("(:...:)"))
            assertThat(isCollapsedByDefault(descriptors[0]), `is`(false))
        }
    }

    @Nested
    @DisplayName("XPath Full Text 1.0 EBNF (77) FTWeight")
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
    @DisplayName("XPath Full Text 1.0 EBNF (85) FTWordsValue")
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
    @DisplayName("XPath Full Text 1.0 EBNF (86) FTExtensionSelection")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
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

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (35) LambdaFunctionExpr")
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
}
