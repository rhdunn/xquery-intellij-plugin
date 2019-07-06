/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.tests.lang.findUsages

import com.intellij.lang.HelpID
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.findUsages.XQueryFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Find Usages - FindUsagesProvider")
private class XQueryFindUsagesProviderTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        fun parse(text: String): Pair<PsiElement, PsiElement> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XPathVariableReference
            val element = call.variableName?.element!!
            val references = element.references
            return when (references.size) {
                1 -> element to references[0].resolve()!! // NCName
                2 -> element to references[1].resolve()!! // QName
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        @Test
        @DisplayName("NCName")
        fun ncname() {
            val provider = XQueryFindUsagesProvider
            val ref = parse("declare variable \$ x := 2; \$x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("Identifier"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val provider = XQueryFindUsagesProvider
            val ref = parse("declare variable \$ local:x := 2; \$local:x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("Identifier"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }

        @Test
        @DisplayName("with type")
        fun withType() {
            val provider = XQueryFindUsagesProvider
            val ref = parse("declare variable \$ x as xs:long := 2; \$x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("Identifier"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }
    }
}
