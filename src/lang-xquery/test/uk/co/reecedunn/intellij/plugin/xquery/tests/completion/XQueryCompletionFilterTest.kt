// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.completion

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.ProcessingContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.completion
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.completion.filters.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Code Completion - Completion Filters")
class XQueryCompletionFilterTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryCompletionFilterTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis ; XQuery 3.1 EBNF (44) ReverseAxis")
    internal inner class ForwardOrReverseAxis {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (111) AxisStep (invalid/unknown axis name)")
        internal inner class AxisStep {
            @Test
            @DisplayName("axis name")
            fun axisName() {
                val context = ProcessingContext()
                val element = completion("lorem::ipsum", "lorem")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("node name")
            fun nodeName() {
                val context = ProcessingContext()
                val element = completion("lorem::ipsum", "ipsum")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (113) ForwardAxis")
        internal inner class ForwardAxisStep {
            @Test
            @DisplayName("axis name")
            fun axisName() {
                val context = ProcessingContext()
                val element = completion("child::element", "child")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("node name")
            fun nodeName() {
                val context = ProcessingContext()
                val element = completion("child::element", "element")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("attribute selector")
            fun attributeSelector() {
                val context = ProcessingContext()
                val element = completion("@completion-point")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("element selector")
            fun elementSelector() {
                val context = ProcessingContext()
                val element = completion("completion-point")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (115) ReverseStep ; XQuery 3.1 EBNF (116) ReverseAxis")
        internal inner class ReverseAxisStep {
            @Test
            @DisplayName("axis name")
            fun axisName() {
                val context = ProcessingContext()
                val element = completion("parent::element", "parent")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("node name")
            fun nodeName() {
                val context = ProcessingContext()
                val element = completion("parent::element", "element")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("NCName function name")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point()")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName function name, from prefix")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("completion-point:test()")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName function name, from local-name")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("local:completion-point()")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (194) PITest")
        fun piTest() {
            val context = ProcessingContext()
            val element = completion("processing-instruction(\"completion-point\")")
            assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathVarRefFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (131) VarRef")
        fun varRef() {
            val context = ProcessingContext()
            val element = completion("\$completion-point")
            assertThat(XPathVarRefFilter.accepts(element, context), `is`(true))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
    internal inner class FunctionCall {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val context = ProcessingContext()
            val element = completion("@completion-point")
            assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
        fun namedTextTest() {
            val context = ProcessingContext()
            val element = completion("\$x/text(\"completion-point\")")
            assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("NCName function name")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point()")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName function name for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum()", "lorem")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("QName function name for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum()", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val context = ProcessingContext()
                val element = completion("Q{lorem}ipsum()", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("NCName function name")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("2 => completion-point()")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName function name for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("2 => lorem:ipsum()", "lorem")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("QName function name for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("2 => lorem:ipsum()", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val context = ProcessingContext()
                val element = completion("2 => Q{lorem}ipsum()", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (138) Argument")
        internal inner class Argument {
            @Test
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest")
            fun nodeTest() {
                val context = ProcessingContext()
                val element = completion("test(completion-point)")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = ProcessingContext()
                val element = completion("test(\$completion-point)")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
            fun stringLiteral() {
                val context = ProcessingContext()
                val element = completion("test(\"completion-point\")")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (118) NodeTest")
        internal inner class NodeTest {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "lorem")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("QName for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val context = ProcessingContext()
                val element = completion("Q{lorem}ipsum", "ipsum")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxisStep() {
            val context = ProcessingContext()
            val element = completion("child::completion-point")
            assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (186) ItemType")
        internal inner class ItemType {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("function (\$x as completion-point) {}")
                assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "lorem")
                assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "ipsum")
                assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (186) ItemType")
    internal inner class ItemType {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathItemTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxisStep() {
            val context = ProcessingContext()
            val element = completion("child::completion-point")
            assertThat(XPathItemTypeFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (186) ItemType")
        internal inner class ItemType {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("function (\$x as completion-point) {}")
                assertThat(XPathItemTypeFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "lorem")
                assertThat(XPathItemTypeFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "ipsum")
                assertThat(XPathItemTypeFilter.accepts(element, context), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
    internal inner class AtomicOrUnionType {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (182) SingleType ; XQuery 3.1 EBNF (187) AtomicOrUnionType")
        fun singleType() {
            val context = ProcessingContext()
            val element = completion("2 cast as completion-point")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(true))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (186) ItemType ; XQuery 3.1 EBNF (187) AtomicOrUnionType")
        fun itemType() {
            val context = ProcessingContext()
            val element = completion("function (\$x as completion-point) {}")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(true))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (188) KindTest")
    internal inner class KindTest {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (113) ForwardAxis")
        internal inner class ForwardAxisStep {
            @Test
            @DisplayName("compact whitespace")
            fun compactWhitespace() {
                val context = ProcessingContext()
                val element = completion("child::completion-point")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("whitespace and comments")
            fun whitespaceAndComments() {
                val context = ProcessingContext()
                val element = completion("child (: lorem :) :: (: ipsum:) completion-point (: dolor :)")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("attribute selector")
            fun attributeSelector() {
                val context = ProcessingContext()
                val element = completion("@completion-point")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector as NCName")
            fun elementSelector_ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector as QName for prefix part")
            fun elementSelector_qname_prefix() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "lorem")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector as QName for local-name part")
            fun elementSelector_qname_localName() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "ipsum")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("NCName function name")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point()")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName function name")
            fun qname() {
                val context = ProcessingContext()
                val element = completion("local:completion-point()")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (115) ReverseStep ; XQuery 3.1 EBNF (116) ReverseAxis")
        internal inner class ReverseAxisStep {
            @Test
            @DisplayName("compact whitespace")
            fun compactWhitespace() {
                val context = ProcessingContext()
                val element = completion("parent::completion-point")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("whitespace and comments")
            fun whitespaceAndComments() {
                val context = ProcessingContext()
                val element = completion("parent (: lorem :) :: (: ipsum:) completion-point (: dolor :)")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (186) ItemType")
        internal inner class ItemType {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = ProcessingContext()
                val element = completion("function (\$x as completion-point) {}")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for prefix part")
            fun qname_prefix() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "lorem")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("QName for local-name part")
            fun qname_localName() {
                val context = ProcessingContext()
                val element = completion("function (\$x as lorem:ipsum) {}", "ipsum")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
        internal inner class NamedMapNodeTest {
            @Test
            @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
            fun sequenceType() {
                val context = ProcessingContext()
                val element = completion("2 instance of object-node(\"completion-point\")")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest")
            fun nodeTest() {
                val context = ProcessingContext()
                val element = completion("()/object-node(\"completion-point\")")
                assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (234) QName")
    internal inner class QName {
        @Test
        @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (235) QName")
        fun ncname() {
            val context = ProcessingContext()
            val element = completion("completion-point")
            assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(true))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (234) QName")
        internal inner class QName {
            @Test
            @DisplayName("prefix part")
            fun prefix() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "lorem")
                assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("local-name part")
            fun localName() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "ipsum")
                assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(false))
            }
        }
    }
}
