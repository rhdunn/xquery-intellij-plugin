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
import uk.co.reecedunn.compat.testFramework.MockUpdateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
private class XPathParameterInfoHandlerTest : ParserTestCase() {
    @Nested
    @DisplayName("find element for parameter info")
    internal inner class FindElementForParameterInfo {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = createParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(4))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val context = createParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(45))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("find element for updating parameter info")
    internal inner class FindElementForUpdatingParameterInfo {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val context = updateParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(4))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val context = updateParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val context = updateParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(45))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }
        }
    }

    @Nested
    @DisplayName("show parameter info")
    internal inner class ShowParameterInfo {
        @Test
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        fun functionCall() {
            val context = createParameterInfoContext("abs(2)", 4)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            XPathParameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(4))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(3))
            assertThat(hint.showHintHandler, `is`(sameInstance(XPathParameterInfoHandler)))
        }
    }

    @Nested
    @DisplayName("update parameter info")
    internal inner class UpdateParameterInfo {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("first parameter")
            fun firstParameter() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 7)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("second parameter")
            fun secondParameter() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 10)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(10))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("last parameter")
            fun lastParameter() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 19)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(19))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(4))
            }
        }
    }
}
