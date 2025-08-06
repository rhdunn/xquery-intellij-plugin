// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.completion

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.completion.filters.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider

@Suppress("RedundantVisibilityModifier")
@DisplayName("XPath 3.1 - Code Completion - Completion Filters")
class XPathCompletionFilterTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XPathCompletionFilterTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (41) ForwardAxis ; XPath 3.1 EBNF (44) ReverseAxis")
    internal inner class ForwardOrReverseAxis {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (39) AxisStep (invalid/unknown axis name)")
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
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (41) ForwardAxis")
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
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (42) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("attribute selector")
            fun attributeSelector() {
                val context = ProcessingContext()
                val element = completion("@completion-point")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("element selector as NCName")
            fun elementSelector_ncname() {
                val context = ProcessingContext()
                val element = completion("completion-point")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector as QName; prefix part")
            fun elementSelector_qname_prefix() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "lorem")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector as QName; local-name part")
            fun elementSelector_qname_localName() {
                val context = ProcessingContext()
                val element = completion("lorem:ipsum", "ipsum")
                assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (43) ReverseStep ; XPath 3.1 EBNF (44) ReverseAxis")
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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
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
        @DisplayName("XPath 3.1 EBNF (89) PITest")
        fun piTest() {
            val context = ProcessingContext()
            val element = completion("processing-instruction(\"completion-point\")")
            assertThat(XPathForwardOrReverseAxisFilter.accepts(element, context), `is`(false))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (59) VarRef")
    internal inner class VarRef {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathVarRefFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (59) VarRef")
        fun varRef() {
            val context = ProcessingContext()
            val element = completion("\$completion-point")
            assertThat(XPathVarRefFilter.accepts(element, context), `is`(true))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
    internal inner class FunctionCall {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (42) AbbrevForwardStep")
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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
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
        @DisplayName("XPath 3.1 EBNF (29) ArrowExpr")
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

            @Test
            @DisplayName("XPath 3.1 EBNF (116) StringLiteral")
            fun stringLiteral() {
                val context = ProcessingContext()
                val element = completion("1 => test(\"completion-point\")")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (64) Argument")
        internal inner class Argument {
            @Test
            @DisplayName("XPath 3.1 EBNF (46) NodeTest")
            fun nodeTest() {
                val context = ProcessingContext()
                val element = completion("test(completion-point)")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (59) VarRef")
            fun varRef() {
                val context = ProcessingContext()
                val element = completion("test(\$completion-point)")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (116) StringLiteral")
            fun stringLiteral() {
                val context = ProcessingContext()
                val element = completion("test(\"completion-point\")")
                assertThat(XPathFunctionCallFilter.accepts(element, context), `is`(false))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (46) NodeTest")
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
    @DisplayName("XPath 3.1 EBNF (79) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (41) ForwardAxis")
        fun forwardAxisStep() {
            val context = ProcessingContext()
            val element = completion("child::completion-point")
            assertThat(XPathSequenceTypeFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
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
    @DisplayName("XPath 3.1 EBNF (81) ItemType")
    internal inner class ItemType {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathItemTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (41) ForwardAxis")
        fun forwardAxisStep() {
            val context = ProcessingContext()
            val element = completion("child::completion-point")
            assertThat(XPathItemTypeFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
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
    @DisplayName("XPath 3.1 EBNF (82) AtomicOrUnionType")
    internal inner class AtomicOrUnionType {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (182) SingleType ; XPath 3.1 EBNF (187) AtomicOrUnionType")
        fun singleType() {
            val context = ProcessingContext()
            val element = completion("2 cast as completion-point")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(true))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (186) ItemType ; XPath 3.1 EBNF (187) AtomicOrUnionType")
        fun itemType() {
            val context = ProcessingContext()
            val element = completion("function (\$x as completion-point) {}")
            assertThat(XPathAtomicOrUnionTypeFilter.accepts(element, context), `is`(true))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (83) KindTest")
    internal inner class KindTest {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathKindTestFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (41) ForwardAxis")
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
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (42) AbbrevForwardStep")
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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
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
        @DisplayName("XPath 3.1 EBNF (43) ReverseStep ; XPath 3.1 EBNF (44) ReverseAxis")
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
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
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
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    internal inner class QName {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(false))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val context = ProcessingContext()
            val element = completion("completion-point")
            assertThat(XPathQNamePrefixFilter.accepts(element, context), `is`(true))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (122) QName")
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
