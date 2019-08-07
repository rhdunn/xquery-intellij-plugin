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
package uk.co.reecedunn.intellij.plugin.xslt.completion

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathSyntaxSubset
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathVersion
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XsltVersion
import uk.co.reecedunn.intellij.plugin.xslt.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xslt.psi.isXslStylesheet

@DisplayName("XSLT 3.0 - Code Completion - Properties")
private class XsltCompletionPropertyTest : ParserTestCase() {
    @Nested
    @DisplayName("generic-element-type")
    internal inner class GenericElementType {
        @Test
        @DisplayName("@use-when = expression")
        fun useWhen() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "comment", "use-when")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "comment", "use-when")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator")
    internal inner class Accumulator {
        @Test
        @DisplayName("@initial-value = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator", "initial-value")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator", "initial-value")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator-rule")
    internal inner class AccumulatorRule {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator-rule", "match")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator-rule", "match")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "accumulator-rule", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "accumulator-rule", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:analyze-string")
    internal inner class AnalyzeString {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "analyze-string", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "analyze-string", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:apply-templates")
    internal inner class ApplyTemplates {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "apply-templates", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "apply-templates", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:assert")
    internal inner class Assert {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "assert", "test")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "assert", "test")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "assert", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "assert", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:attribute")
    internal inner class Attribute {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "attribute", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "attribute", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:break")
    internal inner class Break {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "break", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "break", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:catch")
    internal inner class Catch {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "catch", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "catch", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:comment")
    internal inner class Comment {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "comment", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "comment", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:copy")
    internal inner class Copy {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "copy", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "copy", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:copy-of")
    internal inner class CopyOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "copy-of", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "copy-of", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:evaluate")
    internal inner class Evaluate {
        @Test
        @DisplayName("@xpath = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "xpath")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "xpath")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@with-params = expression")
        fun withParams() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "with-params")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "with-params")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@context-item = expression")
        fun contextItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "context-item")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "context-item")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@namespace-context = expression")
        fun namespaceContext() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "evaluate", "namespace-context")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "evaluate", "namespace-context")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:for-each")
    internal inner class ForEach {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "for-each", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "for-each", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:for-each-group")
    internal inner class ForEachGroup {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@group-by = expression")
        fun groupBy() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-by")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-by")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@group-adjacent = expression")
        fun groupAdjacent() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl", "for-each-group", "group-adjacent"
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl", "for-each-group", "group-adjacent"
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@group-ending-with = pattern")
        fun groupEndingWith() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                "for-each-group", "group-ending-with"
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                "for-each-group", "group-ending-with"
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@group-starting-with = pattern")
        fun groupStartingWith() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                "for-each-group", "group-starting-with"
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                "for-each-group", "group-starting-with"
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }
    }

    @Nested
    @DisplayName("xsl:if")
    internal inner class If {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "if", "test")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "if", "test")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:iterate")
    internal inner class Iterate {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "iterate", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "iterate", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:key")
    internal inner class Key {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "key", "match")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "key", "match")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@use = expression")
        fun use() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "key", "use")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "key", "use")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:map-entry")
    internal inner class MapEntry {
        @Test
        @DisplayName("@key = expression")
        fun key() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "map-entry", "key")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "map-entry", "key")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "map-entry", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "map-entry", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:merge-key")
    internal inner class MergeKey {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-key", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-key", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:merge-source")
    internal inner class MergeSource {
        @Test
        @DisplayName("@for-each-item = expression")
        fun forEachItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "for-each-item")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-source", "for-each-item")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@for-each-source = expression")
        fun forEachSource() {
            val ss = attribute(
                "tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "for-each-source"
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute(
                "tests/xslt/xslt-3.0-transform.xsl", "merge-source", "for-each-source"
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "merge-source", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "merge-source", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:message")
    internal inner class Message {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "message", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "message", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:namespace")
    internal inner class Namespace {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "namespace", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "namespace", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:number")
    internal inner class Number {
        @Test
        @DisplayName("@value = expression")
        fun value() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "value")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "value")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }

        @Test
        @DisplayName("@count = pattern")
        fun count() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "count")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "count")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@from = pattern")
        fun from() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "number", "from")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "number", "from")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "number", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "number", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:on-completion")
    internal inner class OnCompletion {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-completion", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-completion", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:on-empty")
    internal inner class OnEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-empty", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-empty", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:on-non-empty")
    internal inner class OnNonEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "on-non-empty", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "on-non-empty", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:package")
    internal inner class Package {
        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-package.xsl", "package", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }
    }

    @Nested
    @DisplayName("xsl:param")
    internal inner class Param {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "param", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "param", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:perform-sort")
    internal inner class PerformSort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "perform-sort", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "perform-sort", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:processing-instruction")
    internal inner class ProcessingInstruction {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl", "processing-instruction", "select"
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl", "processing-instruction", "select"
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:sequence")
    internal inner class Sequence {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "sequence", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", "sequence", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:sort")
    internal inner class Sort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "sort", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "sort", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:stylesheet")
    internal inner class Stylesheet {
        @Test
        @DisplayName("@version = number [1.0]")
        fun version10() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "stylesheet", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }

        @Test
        @DisplayName("@version = number [2.0]")
        fun version20() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", "stylesheet", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }

        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "stylesheet", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }
    }

    @Nested
    @DisplayName("xsl:template")
    internal inner class Template {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "template", "match")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XsltPattern))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "template", "match")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XsltPattern))
        }

        @Test
        @DisplayName("@name = qname")
        fun name() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "template", "name")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "template", "name")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.Unknown))
        }
    }

    @Nested
    @DisplayName("xsl:transform")
    internal inner class Transform {
        @Test
        @DisplayName("@version = number [1.0]")
        fun version10() {
            val ss = attribute("tests/xslt/xslt-1.0-transform.xsl", "transform", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }

        @Test
        @DisplayName("@version = number [2.0]")
        fun version20() {
            val ss = attribute("tests/xslt/xslt-2.0-transform.xsl", "transform", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_2_0_20070123))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }

        @Test
        @DisplayName("@version = number [3.0]")
        fun version30() {
            val ss = attribute("tests/xslt/xslt-3.0-transform.xsl", "transform", "version")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathVersion.get(ss), `is`(XPathSpec.REC_3_1_20170321))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.Unknown))
        }
    }

    @Nested
    @DisplayName("xsl:try")
    internal inner class Try {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", "try", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", "try", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:value-of")
    internal inner class ValueOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "value-of", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "value-of", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:variable")
    internal inner class Variable {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "variable", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "variable", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:when")
    internal inner class When {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "when", "test")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "when", "test")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }

    @Nested
    @DisplayName("xsl:with-param")
    internal inner class WithParam {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", "with-param", "select")
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(ss), `is`(XPathSubset.XPath))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", "with-param", "select")
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(XsltVersion.get(tf), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(XPathSyntaxSubset.get(tf), `is`(XPathSubset.XPath))
        }
    }
}
