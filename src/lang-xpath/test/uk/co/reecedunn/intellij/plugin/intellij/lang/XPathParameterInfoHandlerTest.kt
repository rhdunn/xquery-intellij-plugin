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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
private class XPathParameterInfoHandlerTest : ParserTestCase() {
    @Nested
    @DisplayName("find element for parameter info")
    internal inner class FindElementForParameterInfo {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val context = createParameterInfoContext("abs(2)", 4)
            val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(0))

            val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
            assertThat(items.size, `is`(0))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val context = createParameterInfoContext("fn:abs(2)", 7)
            val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(0))

            val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
            assertThat(items.size, `is`(0))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val context = createParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
            val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(0))

            val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
            assertThat(items.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("find element for updating parameter info")
    internal inner class FindElementForUpdatingParameterInfo {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val context = updateParameterInfoContext("abs(2)", 4)
            val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.parameterOwner, `is`(nullValue()))
            assertThat(context.highlightedParameter, `is`(nullValue()))
            assertThat(context.objectsToView.size, `is`(0))

            assertThat(context.parameterListStart, `is`(0))
            assertThat(context.isPreservedOnHintHidden, `is`(false))
            assertThat(context.isInnermostContext, `is`(false))
            assertThat(context.isSingleParameterInfo, `is`(false))

            assertThat(context.isUIComponentEnabled(0), `is`(false))
            assertThat(context.isUIComponentEnabled(1), `is`(false))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val context = updateParameterInfoContext("fn:abs(2)", 7)
            val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.parameterOwner, `is`(nullValue()))
            assertThat(context.highlightedParameter, `is`(nullValue()))
            assertThat(context.objectsToView.size, `is`(0))

            assertThat(context.parameterListStart, `is`(0))
            assertThat(context.isPreservedOnHintHidden, `is`(false))
            assertThat(context.isInnermostContext, `is`(false))
            assertThat(context.isSingleParameterInfo, `is`(false))

            assertThat(context.isUIComponentEnabled(0), `is`(false))
            assertThat(context.isUIComponentEnabled(1), `is`(false))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val context = updateParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
            val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
            assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

            assertThat(context.parameterOwner, `is`(nullValue()))
            assertThat(context.highlightedParameter, `is`(nullValue()))
            assertThat(context.objectsToView.size, `is`(0))

            assertThat(context.parameterListStart, `is`(0))
            assertThat(context.isPreservedOnHintHidden, `is`(false))
            assertThat(context.isInnermostContext, `is`(false))
            assertThat(context.isSingleParameterInfo, `is`(false))

            assertThat(context.isUIComponentEnabled(0), `is`(false))
            assertThat(context.isUIComponentEnabled(1), `is`(false))
        }
    }
}
