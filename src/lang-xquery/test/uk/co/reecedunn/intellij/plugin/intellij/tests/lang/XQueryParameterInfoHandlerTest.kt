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
package uk.co.reecedunn.intellij.plugin.intellij.tests.lang

import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.compat.testFramework.MockUpdateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathParameterInfoHandler
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
private class XQueryParameterInfoHandlerTest : ParserTestCase() {
    @Nested
    @DisplayName("find element for parameter info")
    internal inner class FindElementForParameterInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = createParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(4))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = createParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
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

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val context = createParameterInfoContext("string(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(2))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:string"))
                assertThat(items[0].arity, `is`(Range(0, 0)))

                assertThat(op_qname_presentation(items[1].functionName!!), `is`("fn:string"))
                assertThat(items[1].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("no duplicates")
            fun noDuplicates() {
                val context = createParameterInfoContext(
                    """
                    module namespace t = "http://www.example.co.uk";
                    declare function f(${'$'}x) {};
                    declare function g() { f(2) };
                    """, 161
                )
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(161))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("f"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = createParameterInfoContext("let \$a := abs#1 return \$a(2)", 26)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(26))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
            fun namedFunctionRef() {
                val context = createParameterInfoContext("abs#1(2)", 6)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(6))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = createParameterInfoContext("let \$a := abs#1 return 2 => \$a()", 31)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(31))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = createParameterInfoContext("2 => abs()", 9)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(9))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = createParameterInfoContext("2 => fn:abs()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(12))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("2 => Q{http://www.w3.org/2005/xpath-functions}abs()", 50)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(50))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val context = createParameterInfoContext("2 => (fn:abs#1)()", 16)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(16))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val context = createParameterInfoContext("2 => adjust-date-to-timezone()", 29)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(29))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(2))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:adjust-date-to-timezone"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                assertThat(op_qname_presentation(items[1].functionName!!), `is`("fn:adjust-date-to-timezone"))
                assertThat(items[1].arity, `is`(Range(2, 2)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("no duplicates")
            fun noDuplicates() {
                val context = createParameterInfoContext(
                    """
                    module namespace t = "http://www.example.co.uk";
                    declare function f(${'$'}x) {};
                    declare function g() { 2 => f() };
                    """, 166
                )
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(166))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("f"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("exclude empty parameter function")
            fun excludeEmptyParameterFunction() {
                val context = createParameterInfoContext("2 => string()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(12))

                val items = context.itemsToShow!!.map { it as XPathFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(op_qname_presentation(items[0].functionName!!), `is`("fn:string"))
                assertThat(items[0].arity, `is`(Range(1, 1)))

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
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
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
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
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
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
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

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = updateParameterInfoContext("let \$a := abs#1 return \$a(2)", 26)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(26))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
            fun namedFunctionRef() {
                val context = updateParameterInfoContext("abs#1(2)", 6)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(6))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = updateParameterInfoContext("let \$a := abs#1 return 2 => \$a()", 31)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(31))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = updateParameterInfoContext("2 => abs()", 9)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = updateParameterInfoContext("2 => fn:abs()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = updateParameterInfoContext("2 => Q{http://www.w3.org/2005/xpath-functions}abs()", 50)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(50))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val context = updateParameterInfoContext("2 => (fn:abs#1)()", 16)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = XPathParameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(16))
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
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
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

        @Test
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        fun postfixExpr() {
            val context = createParameterInfoContext("abs#1(2)", 6)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            XPathParameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(6))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(5))
            assertThat(hint.showHintHandler, `is`(sameInstance(XPathParameterInfoHandler)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        fun arrowExpr() {
            val context = createParameterInfoContext("2 => abs()", 9)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            XPathParameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(9))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(8))
            assertThat(hint.showHintHandler, `is`(sameInstance(XPathParameterInfoHandler)))
        }
    }

    @Nested
    @DisplayName("update parameter info")
    internal inner class UpdateParameterInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("concat()", 7)
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
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("concat(1)", 7)
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
            @DisplayName("first argument")
            fun first() {
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
            @DisplayName("second argument")
            fun second() {
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
            @DisplayName("last argument")
            fun last() {
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

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("concat#1()", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("concat#1(1)", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("first argument")
            fun first() {
                val context = updateParameterInfoContext("concat#1(1, 2, 3, 4, 5)", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("second argument")
            fun second() {
                val context = updateParameterInfoContext("concat#1(1, 2, 3, 4, 5)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("last argument")
            fun last() {
                val context = updateParameterInfoContext("concat#2(1, 2, 3, 4, 5)", 21)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(21))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(4))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("1 => concat()", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("1 => concat(1)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("first argument")
            fun first() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("second argument")
            fun second() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 15)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(15))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(2))
            }

            @Test
            @DisplayName("last argument")
            fun last() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 24)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                XPathParameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(24))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(5))
            }
        }
    }

    @Nested
    @DisplayName("update UI")
    internal inner class UpdateUI {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("no parameters")
            fun empty() {
                val context = createParameterInfoContext("true()", 5)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = -1

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(-1))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("<no parameters>"))
                assertThat(ui.highlightStart, `is`(-1))
                assertThat(ui.highlightEnd, `is`(-1))
            }

            @Test
            @DisplayName("parameters")
            fun parameters() {
                val context = createParameterInfoContext("replace(1, 2, 3)", 8)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = -1

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(-1))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(-1))
                assertThat(ui.highlightEnd, `is`(-1))
            }

            @Test
            @DisplayName("parameters; first parameter highlighted")
            fun parameters_first() {
                val context = createParameterInfoContext("replace(1, 2, 3)", 8)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 0

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(0))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(0))
                assertThat(ui.highlightEnd, `is`(20))
            }

            @Test
            @DisplayName("parameters; last parameter highlighted")
            fun parameters_last() {
                val context = createParameterInfoContext("replace(1, 2, 3)", 14)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 2

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(2))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(45))
                assertThat(ui.highlightEnd, `is`(70))
            }

            @Test
            @DisplayName("parameters; out of range")
            fun parameters_outOfRange() {
                val context = createParameterInfoContext("replace(1, 2, 3, 4)", 17)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 3

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(3))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(-1))
                assertThat(ui.highlightEnd, `is`(-1))
            }

            @Test
            @DisplayName("parameters; variadic parameter highlighted")
            fun parameters_variadic() {
                val context = createParameterInfoContext("concat(1, 2, 3, 4, 5)", 19)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 4

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.last() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(4))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$arg1 as xs:anyAtomicType?, \$arg2 as xs:anyAtomicType?, \$args as xs:anyAtomicType? ..."))
                assertThat(ui.highlightStart, `is`(56))
                assertThat(ui.highlightEnd, `is`(86))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("parameters")
            fun parameters() {
                val context = createParameterInfoContext("1 => replace(2, 3)", 13)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = -1

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(-1))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(-1))
                assertThat(ui.highlightEnd, `is`(-1))
            }

            @Test
            @DisplayName("parameters; first parameter highlighted")
            fun parameters_first() {
                val context = createParameterInfoContext("1 => replace(2, 3)", 13)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 0

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(0))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(0))
                assertThat(ui.highlightEnd, `is`(20))
            }

            @Test
            @DisplayName("parameters; last parameter highlighted")
            fun parameters_last() {
                val context = createParameterInfoContext("1 => replace(2, 3)", 16)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 2

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(2))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(45))
                assertThat(ui.highlightEnd, `is`(70))
            }

            @Test
            @DisplayName("parameters; out of range")
            fun parameters_outOfRange() {
                val context = createParameterInfoContext("1 => replace(2, 3, 4)", 19)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 3

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.first() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(3))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$input as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                assertThat(ui.highlightStart, `is`(-1))
                assertThat(ui.highlightEnd, `is`(-1))
            }

            @Test
            @DisplayName("parameters; variadic parameter highlighted")
            fun parameters_variadic() {
                val context = createParameterInfoContext("1 => concat(2, 3, 4, 5)", 21)
                val function = XPathParameterInfoHandler.findElementForParameterInfo(context)

                val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
                ui.currentParameterIndex = 4

                XPathParameterInfoHandler.updateUI(context.itemsToShow?.last() as XPathFunctionDeclaration, ui)
                assertThat(ui.currentParameterIndex, `is`(4))
                assertThat(ui.parameterOwner, `is`(sameInstance(function)))
                assertThat(ui.isSingleOverload, `is`(false))
                assertThat(ui.isSingleParameterInfo, `is`(false))
                assertThat(ui.isUIComponentEnabled, `is`(false))

                assertThat(ui.text, `is`("\$arg1 as xs:anyAtomicType?, \$arg2 as xs:anyAtomicType?, \$args as xs:anyAtomicType? ..."))
                assertThat(ui.highlightStart, `is`(56))
                assertThat(ui.highlightEnd, `is`(86))
            }
        }
    }
}
