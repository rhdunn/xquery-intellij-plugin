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

import com.intellij.util.ProcessingContext
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.completion.filters.XPathNodeTestFilter
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Code Completion - Completion Filters")
private class XPathCompletionFilterTest : ParserTestCase() {
    @Nested
    @DisplayName("XPath 3.1 EBNF (46) NodeTest")
    internal inner class NodeTest {
        @Test
        @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
        fun instanceofExpr() {
            val context = ProcessingContext()
            val element = completion("2 instance of empty-sequence()", "instance")
            assertThat(XPathNodeTestFilter.accepts(element, context), `is`(false))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (40) ForwardStep ; XPath 3.1 EBNF (41) ForwardAxis")
        internal inner class ForwardAxisStep {
            @Test
            @DisplayName("compact whitespace")
            fun compactWhitespace() {
                val context = ProcessingContext()
                val element = completion("child::completion-point")
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("whitespace and comments")
            fun whitespaceAndComments() {
                val context = ProcessingContext()
                val element = completion("child (: lorem :) :: (: ipsum:) completion-point (: dolor :)")
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
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
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("element selector")
            fun elementSelector() {
                val context = ProcessingContext()
                val element = completion("completion-point")
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
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
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
            }

            @Test
            @DisplayName("whitespace and comments")
            fun whitespaceAndComments() {
                val context = ProcessingContext()
                val element = completion("parent (: lorem :) :: (: ipsum:) completion-point (: dolor :)")
                assertThat(XPathNodeTestFilter.accepts(element, context), `is`(true))
            }
        }
    }
}
