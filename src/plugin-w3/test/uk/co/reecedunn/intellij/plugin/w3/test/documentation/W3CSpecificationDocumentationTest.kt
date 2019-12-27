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
package uk.co.reecedunn.intellij.plugin.w3.test.documentation

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.w3.documentation.FunctionsAndOperatorsDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Documentation - W3C Specification Document")
private class W3CSpecificationDocumentationTest {
    @Nested
    @DisplayName("XQuery and XPath Functions and Operators")
    internal inner class FunctionsAndOperators {
        @Test
        @DisplayName("1.0 Working Draft 2003 May 02")
        fun spec20030502() {
            val spec: XdmDocumentationSource = FunctionsAndOperatorsDocumentation.WD_1_0_20030502
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (Working Draft 2003 May 02)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2003/WD-xpath-functions-20030502/"))
            assertThat(spec.path, `is`("w3/xpath-functions/1.0-20030502.html"))
        }

        @Test
        @DisplayName("1.0 Recommendation 2007 Jan 23")
        fun spec20070123() {
            val spec: XdmDocumentationSource = FunctionsAndOperatorsDocumentation.REC_1_0_20070123
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (First Edition)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2007/REC-xpath-functions-20070123/"))
            assertThat(spec.path, `is`("w3/xpath-functions/1.0-20070123.html"))
        }

        @Test
        @DisplayName("1.0 Recommendation 2010 Dec 14")
        fun spec20101214() {
            val spec: XdmDocumentationSource = FunctionsAndOperatorsDocumentation.REC_1_0_20101214
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("1.0 (Second Edition)"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2010/REC-xpath-functions-20101214/"))
            assertThat(spec.path, `is`("w3/xpath-functions/1.0-20101214.html"))
        }

        @Test
        @DisplayName("3.0 Recommendation 2014 Apr 08")
        fun spec20140408() {
            val spec: XdmDocumentationSource = FunctionsAndOperatorsDocumentation.REC_3_0_20140408
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("3.0"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/"))
            assertThat(spec.path, `is`("w3/xpath-functions/3.0-20140408.html"))
        }

        @Test
        @DisplayName("3.1 Recommendation 2014 Apr 08")
        fun spec20170321() {
            val spec: XdmDocumentationSource = FunctionsAndOperatorsDocumentation.REC_3_1_20170321
            assertThat(spec.name, `is`("XQuery and XPath Functions and Operators"))
            assertThat(spec.version, `is`("3.1"))
            assertThat(spec.href, `is`("https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/"))
            assertThat(spec.path, `is`("w3/xpath-functions/3.1-20170321.html"))
        }
    }
}
