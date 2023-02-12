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
package uk.co.reecedunn.intellij.plugin.xquery.tests.codeInspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst.XPST0081
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

@Suppress("RedundantVisibilityModifier")
@DisplayName("XPath 3.1 - Error Conditions")
class XPathInspectionTest : InspectionTestCase() {
    override val pluginId: PluginId = PluginId.getId("XPathInspectionTest")

    @Nested
    @DisplayName("XPath (F) XPST - static errors")
    internal inner class XPSTTest {
        @Nested
        @DisplayName("XPST0081 - unbound qname prefix")
        internal inner class XPST0081Test {
            @Test
            @DisplayName("xmlns")
            fun testXmlns() {
                val file = parse<XQueryModule>(
                    """
                    <a:b xmlns:a="http://www.example.com/test"/>
                    """
                )[0]

                val problems = inspect(file, XPST0081())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 1.0/3.0 namespaces")
            fun testBuiltinXQuery() {
                val file = parse<XQueryModule>(
                    """
                    declare variable ${'$'}local:x as xs:boolean := fn:true();
                    declare variable ${'$'}local:y := <a xml:id="1" xsi:a="http://www.example.com/test"/>;
                    ()
                    """
                )[0]

                val problems = inspect(file, XPST0081())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 3.1 namespaces")
            fun testBuiltinXQuery31() {
                val file = parse<XQueryModule>(
                    """
                    xquery version "3.1";
                    declare variable ${'$'}local:x as xs:boolean := fn:true();
                    declare variable ${'$'}local:y := <a xml:id="1" xsi:a="http://www.example.com/test"/>;
                    declare variable ${'$'}local:z := math:sin(map:size(map {}) div array:size(array {}));
                    ()
                    """
                )[0]

                val problems = inspect(file, XPST0081())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in MarkLogic namespaces")
            fun testBuiltinMarkLogic() {
                val file = parse<XQueryModule>(
                    """
                    declare variable ${'$'}x := xdmp:version();
                    ()
                    """
                )[0]

                val problems = inspect(file, XPST0081()) {
                    implementationVersion = "marklogic/v8"
                    XQueryVersion = XQuerySpec.MARKLOGIC_1_0.versionId
                }
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 1.0 namespaces; different vendor")
            fun testBuiltinMarkLogicNotTargettingMarkLogic() {
                val file = parse<XQueryModule>(
                    """
                    declare variable ${'$'}x := xdmp:version();
                    ()
                    """
                )[0]

                val problems = inspect(file, XPST0081()) {
                    implementationVersion = "w3c/spec/v1ed"
                }
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL))
                assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
                assertThat(problems[0].psiElement.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("xdmp"))
            }

            @Test
            @DisplayName("unbound prefix namespace")
            fun testQName() {
                val file = parse<XQueryModule>(
                    """
                    module namespace test = "http://example.com/test";
                    declare function x:func() { () };
                    """
                )[0]

                val problems = inspect(file, XPST0081())
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL))
                assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
                assertThat(problems[0].psiElement.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("x"))
            }
        }
    }
}
