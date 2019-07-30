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
package uk.co.reecedunn.intellij.plugin.intellij.tests.documentation

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.documentation.XQueryDocumentationProvider
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Custom Language Support - Documentation Provider - XQuery")
private class XQueryDocumentationProviderTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
    internal inner class ModuleImport {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XPathFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[0].resolve()
            return element to ref
        }

        @Test
        @DisplayName("custom")
        fun custom() {
            val ref = parse(
                """
                import module namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace ex = \"http://www.example.com\""))
        }

        @Test
        @DisplayName("custom without namespace uri")
        fun customWithoutUri() {
            val ref = parse(
                """
                import module namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
    internal inner class NamespaceDecl {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XPathFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[0].resolve()
            return element to ref
        }

        @Test
        @DisplayName("builtin")
        fun builtin() {
            val ref = parse("fn:true()")

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace fn = \"http://www.w3.org/2005/xpath-functions\""))
        }

        @Test
        @DisplayName("custom")
        fun custom() {
            val ref = parse(
                """
                declare namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace ex = \"http://www.example.com\""))
        }

        @Test
        @DisplayName("custom without namespace uri")
        fun customWithoutUri() {
            val ref = parse(
                """
                declare namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }

        @Test
        @DisplayName("module")
        fun module() {
            val ref = parse(
                """
                module namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("module namespace ex = \"http://www.example.com\""))
        }

        @Test
        @DisplayName("module without namespace uri")
        fun moduleWithoutUri() {
            val ref = parse(
                """
                module namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XPathVariableReference
            val element = call.variableName?.element!!
            val ref = element.references[1].resolve()
            return element to ref
        }

        @Test
        @DisplayName("no type")
        fun noType() {
            val ref = parse("declare variable \$ local:test := 2; \$local:test")

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare variable \$local:test"))
        }

        @Test
        @DisplayName("type")
        fun type() {
            val ref = parse("declare variable \$ local:test  as  xs:float := 2; \$local:test")

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare variable \$local:test as xs:float"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
    internal inner class FunctionCall {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XPathFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[1].resolve()
            return element to ref
        }

        @Test
        @DisplayName("empty parameters ; no return type")
        fun emptyParams_noReturnType() {
            val ref = parse("declare function local:test((::)) {}; local:test()")

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test()"))
        }

        @Test
        @DisplayName("empty parameters ; return type")
        fun emptyParams_returnType() {
            val ref = parse("declare function local:test((::)) as (::) node((::)) {}; local:test()")

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test() as node()"))
        }

        @Test
        @DisplayName("non-empty parameters ; no return type")
        fun params_noReturnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n  as  xs:float *) {}; local:test(1,2)"
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*)"))
        }

        @Test
        @DisplayName("non-empty parameters ; return type")
        fun params_returnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n as xs:float *) as item((::)) + {}; local:test(1,2)"
            )

            val quickDoc = XQueryDocumentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*) as item()+"))
        }
    }
}
