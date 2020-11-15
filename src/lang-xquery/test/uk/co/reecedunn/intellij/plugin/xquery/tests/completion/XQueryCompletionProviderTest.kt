/*
 * Copyright (C) 2020 Reece H. Dunn
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

import com.intellij.util.ProcessingContext
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.completion.MockCompletionResultSet
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathStaticallyKnownElementOrTypeNamespaces
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.XPathAtomicOrUnionTypeProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Code Completion - Completion Providers")
private class XQueryCompletionProviderTest : ParserTestCase() {
    override fun registerExtensions() {
        registerNamespaceProvider(XQueryNamespaceProvider, "INSTANCE")
        registerInScopeVariableProvider(XQueryVariableProvider, "INSTANCE")
        registerStaticallyKnownFunctionProvider(XQueryFunctionProvider, "INSTANCE")
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
    internal inner class AtomicOrUnionType {
        @Test
        @DisplayName("XQuery 3.1 EBNF (182) SingleType ; XQuery 3.1 EBNF (187) AtomicOrUnionType")
        fun singleType() {
            val element = completion("2 cast as in", "in")
            val context = ProcessingContext()
            val results = MockCompletionResultSet()
            XPathStaticallyKnownElementOrTypeNamespaces.computeProperty(element, context)
            XPathAtomicOrUnionTypeProvider.apply(element, context, results)

            assertThat(results.elements.size, `is`(49))
        }
    }
}
