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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.breadcrumbs

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.editor.breadcrumbs.XQueryBreadcrumbProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Custom Language Support - Breadcrumb Provider - XQuery")
class XQueryBreadcrumbProviderTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryFindUsagesProviderTest")

    private val breadcrumbProvider = XQueryBreadcrumbProvider()

    fun breadcrumbs(text: String, name: String = "breadcrumbs"): List<PsiElement> {
        var crumb: PsiElement? = parse<XPathEQName>(text).first { (it as XsQNameValue).localName?.data == name }

        val crumbs = mutableListOf<PsiElement>()
        while (crumb != null) {
            if (breadcrumbProvider.acceptElement(crumb)) {
                crumbs.add(crumb)
            }
            crumb = breadcrumbProvider.getParent(crumb)
        }
        return crumbs
    }

    @Test
    @DisplayName("languages")
    fun languages() {
        assertThat(breadcrumbProvider.languages.size, `is`(1))
        assertThat(breadcrumbProvider.languages[0], `is`(sameInstance(XQuery)))
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("empty parameters ; no return type")
        fun emptyParams_noReturnType() {
            val crumbs = breadcrumbs("declare function local:test((::)) { breadcrumbs };")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("local:test"))
            assertThat(tooltip, `is`("declare function local:test()"))
        }

        @Test
        @DisplayName("empty parameters ; return type")
        fun emptyParams_returnType() {
            val crumbs = breadcrumbs("declare function local:test((::)) as (::) node((::)) { breadcrumbs };")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("local:test"))
            assertThat(tooltip, `is`("declare function local:test() as node()"))
        }

        @Test
        @DisplayName("non-empty parameters ; no return type")
        fun params_noReturnType() {
            val crumbs = breadcrumbs(
                "declare function local:test( \$x as (::) xs:int , \$n  as  xs:float *) { breadcrumbs };"
            )
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("local:test"))
            assertThat(tooltip, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*)"))
        }

        @Test
        @DisplayName("non-empty parameters ; return type")
        fun params_returnType() {
            val crumbs = breadcrumbs(
                "declare function local:test( \$x as (::) xs:int , \$n as xs:float *) as item((::)) + { breadcrumbs };"
            )
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("local:test"))
            assertThat(tooltip, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*) as item()+"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("ncname")
        fun ncname() {
            val crumbs = breadcrumbs("<lorem-ipsum>{ breadcrumbs }</lorem-ipsum>")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("lorem-ipsum"))
            assertThat(tooltip, `is`("element lorem-ipsum {...}"))
        }

        @Test
        @DisplayName("qname")
        fun qname() {
            val crumbs = breadcrumbs("<lorem:ipsum>{ breadcrumbs }</lorem:ipsum>")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("lorem:ipsum"))
            assertThat(tooltip, `is`("element lorem:ipsum {...}"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Test
        @DisplayName("ncname")
        fun ncname() {
            val crumbs = breadcrumbs("element lorem-ipsum { breadcrumbs }")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("lorem-ipsum"))
            assertThat(tooltip, `is`("element lorem-ipsum {...}"))
        }

        @Test
        @DisplayName("qname")
        fun qname() {
            val crumbs = breadcrumbs("element lorem:ipsum { breadcrumbs }")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("lorem:ipsum"))
            assertThat(tooltip, `is`("element lorem:ipsum {...}"))
        }

        @Test
        @DisplayName("expression")
        fun expr() {
            val crumbs = breadcrumbs("element { \"lorem:\" || \"ipsum\" } { breadcrumbs }")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("element"))
            assertThat(tooltip, `is`("element <dynamic> {...}"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("empty parameters ; no return type")
        fun emptyParams_noReturnType() {
            val crumbs = breadcrumbs("function ((::)) { breadcrumbs };")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("function"))
            assertThat(tooltip, `is`("function ()"))
        }

        @Test
        @DisplayName("empty parameters ; return type")
        fun emptyParams_returnType() {
            val crumbs = breadcrumbs("function ((::)) as (::) node((::)) { breadcrumbs };")
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("function"))
            assertThat(tooltip, `is`("function () as node()"))
        }

        @Test
        @DisplayName("non-empty parameters ; no return type")
        fun params_noReturnType() {
            val crumbs = breadcrumbs(
                "function ( \$x as (::) xs:int , \$n  as  xs:float *) { breadcrumbs };"
            )
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("function"))
            assertThat(tooltip, `is`("function (\$x as xs:int, \$n as xs:float*)"))
        }

        @Test
        @DisplayName("non-empty parameters ; return type")
        fun params_returnType() {
            val crumbs = breadcrumbs(
                "function ( \$x as (::) xs:int , \$n as xs:float *) as item((::)) + { breadcrumbs };"
            )
            assertThat(crumbs.size, `is`(1))

            val info = breadcrumbProvider.getElementInfo(crumbs[0])
            val tooltip = breadcrumbProvider.getElementTooltip(crumbs[0])

            assertThat(info, `is`("function"))
            assertThat(tooltip, `is`("function (\$x as xs:int, \$n as xs:float*) as item()+"))
        }
    }
}
