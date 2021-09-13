/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.completion

import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.ui.JBColor
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathAtomicOrUnionTypeLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("XPath 3.1 - Code Completion - Lookup Element")
class XPathLookupElementTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XPathLookupElementTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            "com.intellij.documentWriteAccessGuard",
            "com.intellij.openapi.editor.impl.DocumentWriteAccessGuard",
            pluginDisposable
        )
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (41) ForwardAxis ; XPath 3.1 EBNF (44) ReverseAxis")
    internal inner class ForwardOrReverseAxis {
        @Test
        @DisplayName("lookup element")
        fun lookupElement() {
            val lookup: LookupElement = XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER)

            assertThat(lookup.toString(), `is`("descendant"))
            assertThat(lookup.lookupString, `is`("descendant"))
            assertThat(lookup.allLookupStrings, `is`(setOf("descendant")))
            assertThat(lookup.`object`, `is`(sameInstance(lookup)))
            assertThat(lookup.psiElement, `is`(nullValue()))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            // NOTE: IntelliJ 2020.2 deprecates and changes the behaviour of presentation.isReal.
            assertThat(presentation.icon, `is`(nullValue()))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("descendant"))
            assertThat(presentation.tailText, `is`("::"))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(true))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`("::"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val lookup: LookupElement = XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER)
            val context = handleInsert("descendant", 'd', lookup, 10)

            assertThat(context.document.text, `is`("descendant::"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }

        @Test
        @DisplayName("handle insert with ':' after the inserted text")
        fun handleInsert_colonAfter() {
            val lookup: LookupElement = XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER)
            val context = handleInsert("descendant:", 'd', lookup, 10)

            assertThat(context.document.text, `is`("descendant::"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }

        @Test
        @DisplayName("handle insert with '::' after the inserted text")
        fun handleInsert_axisSpecifierAfter() {
            val lookup: LookupElement = XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER)
            val context = handleInsert("descendant::", 'd', lookup, 10)

            assertThat(context.document.text, `is`("descendant::"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (82) AtomicOrUnionType")
    internal inner class AtomicOrUnionType {
        @Test
        @DisplayName("lookup element for NCName")
        fun lookupElement_ncname() {
            val lookup: LookupElement = XPathAtomicOrUnionTypeLookup("string")

            assertThat(lookup.toString(), `is`("string"))
            assertThat(lookup.lookupString, `is`("string"))
            assertThat(lookup.allLookupStrings, `is`(setOf("string")))
            assertThat(lookup.`object`, `is`(sameInstance(lookup)))
            assertThat(lookup.psiElement, `is`(nullValue()))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(false))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            // NOTE: IntelliJ 2020.2 deprecates and changes the behaviour of presentation.isReal.
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("string"))
            assertThat(presentation.tailFragments, `is`(listOf()))
            assertThat(presentation.tailText, `is`(nullValue()))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))
        }

        @Test
        @DisplayName("lookup element for QName")
        fun lookupElement_qname() {
            val lookup: LookupElement = XPathAtomicOrUnionTypeLookup("integer", "xsd")

            assertThat(lookup.toString(), `is`("xsd:integer"))
            assertThat(lookup.lookupString, `is`("xsd:integer"))
            assertThat(lookup.allLookupStrings, `is`(setOf("xsd:integer")))
            assertThat(lookup.`object`, `is`(sameInstance(lookup)))
            assertThat(lookup.psiElement, `is`(nullValue()))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(false))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            // NOTE: IntelliJ 2020.2 deprecates and changes the behaviour of presentation.isReal.
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("xsd:integer"))
            assertThat(presentation.tailFragments, `is`(listOf()))
            assertThat(presentation.tailText, `is`(nullValue()))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val lookup: LookupElement = XPathAtomicOrUnionTypeLookup("integer", "xsd")
            val context = handleInsert("xsd:integer", 'i', lookup, 11)

            assertThat(context.document.text, `is`("xsd:integer"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    internal inner class QName {
        @Test
        @DisplayName("lookup element")
        fun lookupElement() {
            val lookup: LookupElement = XPathKeywordLookup("math", XPathInsertText.QNAME_PREFIX)

            assertThat(lookup.toString(), `is`("math"))
            assertThat(lookup.lookupString, `is`("math"))
            assertThat(lookup.allLookupStrings, `is`(setOf("math")))
            assertThat(lookup.`object`, `is`(sameInstance(lookup)))
            assertThat(lookup.psiElement, `is`(nullValue()))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            // NOTE: IntelliJ 2020.2 deprecates and changes the behaviour of presentation.isReal.
            assertThat(presentation.icon, `is`(nullValue()))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("math"))
            assertThat(presentation.tailText, `is`(":"))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(true))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`(":"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val lookup: LookupElement = XPathKeywordLookup("math", XPathInsertText.QNAME_PREFIX)
            val context = handleInsert("math", 'm', lookup, 4)

            assertThat(context.document.text, `is`("math:"))
            assertThat(context.editor.caretModel.offset, `is`(5))
        }

        @Test
        @DisplayName("handle insert with ':' after the inserted text")
        fun handleInsert_colonAfter() {
            val lookup: LookupElement = XPathKeywordLookup("math", XPathInsertText.QNAME_PREFIX)
            val context = handleInsert("math:", 'd', lookup, 4)

            assertThat(context.document.text, `is`("math:"))
            assertThat(context.editor.caretModel.offset, `is`(5))
        }
    }
}
