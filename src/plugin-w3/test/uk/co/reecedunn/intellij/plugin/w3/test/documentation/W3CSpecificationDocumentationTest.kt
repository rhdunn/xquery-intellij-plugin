/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.w3.test.documentation

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.w3.documentation.FunctionsAndOperatorsDocumentation
import uk.co.reecedunn.intellij.plugin.w3.documentation.XsltDocumentation
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSourceProvider
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Documentation - W3C Specification Document")
private class W3CSpecificationDocumentationTest {
    @Nested
    @DisplayName("XQuery and XPath Functions and Operators")
    internal inner class FunctionsAndOperators {
        @Test
        @DisplayName("specification type")
        fun specificationType() {
            val type: XpmSpecificationType = FunctionsAndOperatorsDocumentation
            assertThat(type.id, `is`("xpath-functions"))
            assertThat(type.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(type.moduleTypes, `is`(arrayOf(XdmModuleType.XQuery, XdmModuleType.XPath)))
        }

        @Test
        @DisplayName("1.0 Working Draft 02 May 2003")
        fun spec20030502() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.WD_1_0_20030502
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (Working Draft 02 May 2003)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2003/WD-xpath-functions-20030502/"))
            assertThat(spec.path, `is`("w3/xpath-functions-1.0-20030502.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(0))
        }

        @Test
        @DisplayName("1.0 Recommendation 23 Jan 2007")
        fun spec20070123() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.REC_1_0_20070123
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (First Edition)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2007/REC-xpath-functions-20070123/"))
            assertThat(spec.path, `is`("w3/xpath-functions-1.0-20070123.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(1))
        }

        @Test
        @DisplayName("1.0 Recommendation 14 Dec 2010")
        fun spec20101214() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.REC_1_0_20101214
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (Second Edition)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2010/REC-xpath-functions-20101214/"))
            assertThat(spec.path, `is`("w3/xpath-functions-1.0-20101214.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(2))
        }

        @Test
        @DisplayName("3.0 Working Draft 13 Dec 2011")
        fun spec20111213() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.WD_3_0_20111213
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("3.0 (Working Draft 13 Dec 2011)"))
            assertThat(spec.href, `is`("http://www.w3.org/TR/2011/WD-xpath-functions-30-20111213/"))
            assertThat(spec.path, `is`("w3/xpath-functions-3.0-20111213.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(3))
        }

        @Test
        @DisplayName("3.0 Recommendation 08 Apr 2014")
        fun spec20140408() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.REC_3_0_20140408
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("3.0"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/"))
            assertThat(spec.path, `is`("w3/xpath-functions-3.0-20140408.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(4))
        }

        @Test
        @DisplayName("3.1 Recommendation 21 Apr 2017")
        fun spec20170321() {
            val spec: XQDocDocumentationSource = FunctionsAndOperatorsDocumentation.REC_3_1_20170321
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("3.1"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/"))
            assertThat(spec.path, `is`("w3/xpath-functions-3.1-20170321.html"))

            val provider: XQDocDocumentationSourceProvider = FunctionsAndOperatorsDocumentation
            assertThat(provider.sources.size, `is`(6))
            assertThat(provider.sources.indexOf(spec), `is`(5))
        }
    }

    @Nested
    @DisplayName("XSL Transformations (XSLT)")
    internal inner class Xslt {
        @Test
        @DisplayName("specification type")
        fun specificationType() {
            val type: XpmSpecificationType = XsltDocumentation
            assertThat(type.id, `is`("xslt"))
            assertThat(type.name, `is`("XSL Transformations (XSLT)"))
            assertThat(type.moduleTypes, `is`(arrayOf(XdmModuleType.XPath)))
        }

        @Test
        @DisplayName("1.0 Recommendation 16 Nov 1999")
        fun spec19991116() {
            val spec: XQDocDocumentationSource = XsltDocumentation.REC_1_0_19991116
            assertThat(spec.name, `is`("XSL Transformations (XSLT)"))
            assertThat(spec.version, `is`("1.0"))
            assertThat(spec.href, `is`("http://www.w3.org/TR/1999/REC-xslt-19991116/"))
            assertThat(spec.path, `is`("w3/xslt-1.0-19991116.html"))

            val provider: XQDocDocumentationSourceProvider = XsltDocumentation
            assertThat(provider.sources.size, `is`(3))
            assertThat(provider.sources.indexOf(spec), `is`(0))
        }

        @Test
        @DisplayName("2.0 Recommendation 23 Jan 2007")
        fun spec20070123() {
            val spec: XQDocDocumentationSource = XsltDocumentation.REC_2_0_20070123
            assertThat(spec.name, `is`("XSL Transformations (XSLT)"))
            assertThat(spec.version, `is`("2.0"))
            assertThat(spec.href, `is`("http://www.w3.org/TR/2007/REC-xslt20-20070123/"))
            assertThat(spec.path, `is`("w3/xslt-2.0-20070123.html"))

            val provider: XQDocDocumentationSourceProvider = XsltDocumentation
            assertThat(provider.sources.size, `is`(3))
            assertThat(provider.sources.indexOf(spec), `is`(1))
        }

        @Test
        @DisplayName("3.0 Recommendation 08 Jun 2017")
        fun spec20170608() {
            val spec: XQDocDocumentationSource = XsltDocumentation.REC_3_0_20170608
            assertThat(spec.name, `is`("XSL Transformations (XSLT)"))
            assertThat(spec.version, `is`("3.0"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2017/REC-xslt-30-20170608/"))
            assertThat(spec.path, `is`("w3/xslt-3.0-20170608.html"))

            val provider: XQDocDocumentationSourceProvider = XsltDocumentation
            assertThat(provider.sources.size, `is`(3))
            assertThat(provider.sources.indexOf(spec), `is`(2))
        }
    }
}
