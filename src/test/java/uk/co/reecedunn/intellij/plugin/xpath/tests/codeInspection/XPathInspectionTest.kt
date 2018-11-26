/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.codeInspection

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst.XPST0081
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Error Conditions")
private class XPathInspectionTest : InspectionTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(XPathInspectionTest::class.java, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XPath (F) XPST - static errors")
    internal inner class XPSTTest {
        @Nested
        @DisplayName("XPST0081 - unbound qname prefix")
        internal inner class XPST0081Test {
            @Test
            @DisplayName("xmlns")
            fun testXmlns() {
                val file = parseResource("tests/inspections/xpath/XPST0081/xmlns.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 1.0/3.0 namespaces")
            fun testBuiltinXQuery() {
                val file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 3.1 namespaces")
            fun testBuiltinXQuery31() {
                val file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery-3.1.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in MarkLogic namespaces")
            fun testBuiltinMarkLogic() {
                settings.implementationVersion = "marklogic/v8"
                settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                val file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            @Test
            @DisplayName("built-in XQuery 1.0 namespaces; different vendor")
            fun testBuiltinMarkLogicNotTargettingMarkLogic() {
                settings.implementationVersion = "w3c/spec/v1ed"
                val file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("xdmp"))
            }

            @Test
            @DisplayName("unbound prefix namespace")
            fun testQName() {
                val file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName_UnboundPrefix.xq")

                val problems = inspect(file,
                    XPST0081()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(1))

                assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
                assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
                assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.NCNAME))
                assertThat(problems[0].psiElement.text, `is`("x"))
            }
        }
    }
}
