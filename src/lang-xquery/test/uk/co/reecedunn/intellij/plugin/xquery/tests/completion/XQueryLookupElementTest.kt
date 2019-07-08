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
package uk.co.reecedunn.intellij.plugin.xquery.tests.completion

import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.ui.JBColor
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.compat.codeInsight.lookup.LookupElementPresentation
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathFunctionCallLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathVarNameLookup
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XQuery 3.1 - Code Completion - Lookup Element")
private class XPathLookupElementTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        fun parse(text: String): Pair<XQueryModule, XPathVariableDeclaration> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XPathVariableReference
            val ref = call.variableName?.element?.references?.get(1)?.resolve()?.parent?.parent!!
            return module to ref as XPathVariableDeclaration
        }

        @Test
        @DisplayName("lookup element")
        fun lookupElement() {
            val ref = parse("declare variable \$local:test := 2; \$local:test")
            val lookup: LookupElement = XPathVarNameLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.variableName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(false))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`(nullValue()))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(0))
        }

        @Test
        @DisplayName("lookup element with type")
        fun lookupElement_withType() {
            val ref = parse("declare variable \$local:test as (::) xs:string := 2; \$local:test")
            val lookup: LookupElement = XPathVarNameLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.variableName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(false))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`(nullValue()))
            assertThat(presentation.typeText, `is`("xs:string"))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(0))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val ref = parse("declare variable \$local:test := 2; \$local:test")
            val lookup: LookupElement = XPathVarNameLookup("test", "local", ref.second)
            val context = handleInsert("\$local:test", 't', lookup, 11)

            assertThat(context.document.text, `is`("\$local:test"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall (empty parameters)")
    internal inner class FunctionCall_EmptyParams {
        fun parse(text: String): Pair<XQueryModule, XPathFunctionDeclaration> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XPathFunctionReference
            val ref = call.functionName?.element?.references?.get(1)?.resolve()?.parent!!
            return module to ref as XPathFunctionDeclaration
        }

        @Test
        @DisplayName("lookup element")
        fun lookupElement() {
            val ref = parse("declare function local:test() {}; local:test()")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.functionName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`("()"))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`("()"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("lookup element with return type")
        fun lookupElement_withReturnType() {
            val ref = parse("declare function local:test() as xs:string {}; local:test()")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.functionName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`("()"))
            assertThat(presentation.typeText, `is`("xs:string"))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`("()"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val ref = parse("declare function local:test() {}; local:test()")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }

        @Test
        @DisplayName("handle insert with '(' after the inserted text")
        fun handleInsert_openParenAfter() {
            val ref = parse("declare function local:test() {}; local:test()")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test(", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }

        @Test
        @DisplayName("handle insert with '()' after the inserted text")
        fun handleInsert_emptyParensAfter() {
            val ref = parse("declare function local:test() {}; local:test()")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test()", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(12))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall (with parameters)")
    internal inner class FunctionCall_WithParams {
        fun parse(text: String): Pair<XQueryModule, XPathFunctionDeclaration> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XPathFunctionReference
            val ref = call.functionName?.element?.references?.get(1)?.resolve()?.parent!!
            return module to ref as XPathFunctionDeclaration
        }

        @Test
        @DisplayName("lookup element")
        fun lookupElement() {
            val ref = parse("declare function local:test(\$x as (::) xs:float, \$y as item((::))) {}; local:test(1,2)")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.functionName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`("(\$x as xs:float, \$y as item())"))
            assertThat(presentation.typeText, `is`(nullValue()))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`("(\$x as xs:float, \$y as item())"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("lookup element with return type")
        fun lookupElement_withReturnType() {
            val ref = parse(
                "declare function local:test(\$x as (::) xs:float, \$y as item((::))) as xs:string {}; local:test(1,2)"
            )
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)

            assertThat(lookup.toString(), `is`("local:test"))
            assertThat(lookup.lookupString, `is`("local:test"))
            assertThat(lookup.allLookupStrings, `is`(setOf("local:test")))
            assertThat(lookup.`object`, `is`(sameInstance(ref.second)))
            assertThat(lookup.psiElement, `is`(sameInstance(ref.second.functionName?.element)))
            assertThat(lookup.isValid, `is`(true))
            assertThat(lookup.requiresCommittedDocuments(), `is`(true))
            assertThat(lookup.autoCompletionPolicy, `is`(AutoCompletionPolicy.SETTINGS_DEPENDENT))
            assertThat(lookup.isCaseSensitive, `is`(true))
            assertThat(lookup.isWorthShowingInAutoPopup, `is`(true))

            val presentation = LookupElementPresentation()
            lookup.renderElement(presentation)

            assertThat(presentation.isReal, `is`(false))
            assertThat(presentation.icon, `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
            assertThat(presentation.typeIcon, `is`(nullValue()))
            assertThat(presentation.itemText, `is`("local:test"))
            assertThat(presentation.tailText, `is`("(\$x as xs:float, \$y as item())"))
            assertThat(presentation.typeText, `is`("xs:string"))
            assertThat(presentation.isStrikeout, `is`(false))
            assertThat(presentation.isItemTextBold, `is`(false))
            assertThat(presentation.isItemTextItalic, `is`(false))
            assertThat(presentation.isItemTextUnderlined, `is`(false))
            assertThat(presentation.itemTextForeground, `is`(JBColor.foreground()))
            assertThat(presentation.isTypeGrayed, `is`(false))
            assertThat(presentation.isTypeIconRightAligned, `is`(false))

            val tailFragments = presentation.tailFragments
            assertThat(tailFragments.size, `is`(1))

            assertThat(tailFragments[0].text, `is`("(\$x as xs:float, \$y as item())"))
            assertThat(tailFragments[0].isGrayed, `is`(false))
            assertThat(tailFragments[0].isItalic, `is`(false))
            assertThat(tailFragments[0].foregroundColor, `is`(nullValue()))
        }

        @Test
        @DisplayName("handle insert")
        fun handleInsert() {
            val ref = parse("declare function local:test(\$x as (::) xs:float, \$y as item((::))) {}; local:test(1,2)")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }

        @Test
        @DisplayName("handle insert with '(' after the inserted text")
        fun handleInsert_openParenAfter() {
            val ref = parse("declare function local:test(\$x as (::) xs:float, \$y as item((::))) {}; local:test(1,2)")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test(", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }

        @Test
        @DisplayName("handle insert with '()' after the inserted text")
        fun handleInsert_emptyParensAfter() {
            val ref = parse("declare function local:test(\$x as (::) xs:float, \$y as item((::))) {}; local:test(1,2)")
            val lookup: LookupElement = XPathFunctionCallLookup("test", "local", ref.second)
            val context = handleInsert("local:test()", 't', lookup, 10)

            assertThat(context.document.text, `is`("local:test()"))
            assertThat(context.editor.caretModel.offset, `is`(11))
        }
    }
}
