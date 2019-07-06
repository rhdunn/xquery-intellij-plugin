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
import com.intellij.ui.JBColor
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.compat.codeInsight.lookup.LookupElementPresentation
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathAtomicOrUnionTypeLookup
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Code Completion - Lookup Element")
private class XPathLookupElementTest : ParserTestCase() {
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

            assertThat(presentation.isReal, `is`(false))
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

            assertThat(presentation.isReal, `is`(false))
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
            val context = insertionContext("xsd:integer", 'i', lookup, 11)
            lookup.handleInsert(context)

            assertThat(context.document.text, `is`("xsd:integer"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }
    }
}
