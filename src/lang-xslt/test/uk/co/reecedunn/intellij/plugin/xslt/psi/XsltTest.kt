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
package uk.co.reecedunn.intellij.plugin.xslt.psi

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec
import javax.xml.namespace.QName

@DisplayName("XSLT 3.0 - Platform Structure Interface")
private class XsltTest : ParserTestCase() {
    fun parseResource(resource: String): XmlFile {
        val file = ResourceVirtualFile(XsltTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun attribute(resource: String, element: QName, attribute: QName): XmlAttribute {
        return parseResource(resource).walkTree().filterIsInstance<XmlTag>().filter { e ->
            e.namespace == element.namespaceURI && e.localName == element.localPart
        }.map { e ->
            e.getAttribute(attribute.localPart, attribute.namespaceURI)
        }.filterNotNull().first()
    }

    @Nested
    @DisplayName("generic-element-type")
    internal inner class GenericElementType {
        @Test
        @DisplayName("@use-when = expression")
        fun useWhen() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:comment"), qname("use-when"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:comment"), qname("use-when"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator")
    internal inner class Accumulator {
        @Test
        @DisplayName("@initial-value = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:accumulator"), qname("initial-value"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:accumulator"), qname("initial-value"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:accumulator-rule")
    internal inner class AccumulatorRule {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:accumulator-rule"), qname("match"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:accumulator-rule"), qname("match"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:accumulator-rule"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:accumulator-rule"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:analyze-string")
    internal inner class AnalyzeString {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:analyze-string"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:analyze-string"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:apply-templates")
    internal inner class ApplyTemplates {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:apply-templates"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:apply-templates"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:assert")
    internal inner class Assert {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:assert"), qname("test"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:assert"), qname("test"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:attribute")
    internal inner class Attribute {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:attribute"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:attribute"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:break")
    internal inner class Break {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:break"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:break"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:catch")
    internal inner class Catch {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:catch"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:catch"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:comment")
    internal inner class Comment {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:comment"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:comment"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:copy")
    internal inner class Copy {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:copy"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:copy"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:copy-of")
    internal inner class CopyOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:copy-of"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:copy-of"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:evaluate")
    internal inner class Evaluate {
        @Test
        @DisplayName("@xpath = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:evaluate"), qname("xpath"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:evaluate"), qname("xpath"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@with-params = expression")
        fun withParams() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:evaluate"), qname("with-params"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:evaluate"), qname("with-params"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@context-item = expression")
        fun contextItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:evaluate"), qname("context-item"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:evaluate"), qname("context-item"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@namespace-context = expression")
        fun namespaceContext() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:evaluate"), qname("namespace-context"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:evaluate"), qname("namespace-context"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:for-each")
    internal inner class ForEach {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:for-each"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:for-each"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:for-each-group")
    internal inner class ForEachGroup {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:for-each-group"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:for-each-group"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@group-by = expression")
        fun groupBy() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:for-each-group"), qname("group-by"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:for-each-group"), qname("group-by"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@group-adjacent = expression")
        fun groupAdjacent() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:for-each-group"), qname("group-adjacent")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl", qname("xsl:for-each-group"), qname("group-adjacent")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@group-ending-with = pattern")
        fun groupEndingWith() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                qname("xsl:for-each-group"), qname("group-ending-with")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                qname("xsl:for-each-group"), qname("group-ending-with")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@group-starting-with = pattern")
        fun groupStartingWith() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                qname("xsl:for-each-group"), qname("group-starting-with")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                qname("xsl:for-each-group"), qname("group-starting-with")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:if")
    internal inner class If {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:if"), qname("test"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:if"), qname("test"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:iterate")
    internal inner class Iterate {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:iterate"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:iterate"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:key")
    internal inner class Key {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:key"), qname("match"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:key"), qname("match"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@use = expression")
        fun use() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:key"), qname("use"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:key"), qname("use"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:map-entry")
    internal inner class MapEntry {
        @Test
        @DisplayName("@key = expression")
        fun key() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:map-entry"), qname("key"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:map-entry"), qname("key"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:map-entry"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:map-entry"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:merge-key")
    internal inner class MergeKey {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:merge-key"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:merge-key"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:merge-source")
    internal inner class MergeSource {
        @Test
        @DisplayName("@for-each-item = expression")
        fun forEachItem() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:merge-source"), qname("for-each-item"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:merge-source"), qname("for-each-item"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@for-each-source = expression")
        fun forEachSource() {
            val ss = attribute(
                "tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:merge-source"), qname("for-each-source")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute(
                "tests/xslt/xslt-3.0-transform.xsl", qname("xsl:merge-source"), qname("for-each-source")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:merge-source"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:merge-source"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:message")
    internal inner class Message {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:message"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:message"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:namespace")
    internal inner class Namespace {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:namespace"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:namespace"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:number")
    internal inner class Number {
        @Test
        @DisplayName("@value = expression")
        fun value() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("value"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("value"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }

        @Test
        @DisplayName("@count = pattern")
        fun count() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("count"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("count"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@from = pattern")
        fun from() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("from"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("from"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:number"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:number"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:on-completion")
    internal inner class OnCompletion {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:on-completion"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:on-completion"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:on-empty")
    internal inner class OnEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:on-empty"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:on-empty"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:on-non-empty")
    internal inner class OnNonEmpty {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:on-non-empty"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:on-non-empty"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:package")
    internal inner class Package {
        @Test
        @DisplayName("3.0")
        fun package30() {
            val xsl = parseResource("tests/xslt/xslt-3.0-package.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@version = number")
        fun version() {
            val ss = attribute("tests/xslt/xslt-3.0-package.xsl", qname("xsl:package"), qname("version"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:param")
    internal inner class Param {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:param"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:param"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:perform-sort")
    internal inner class PerformSort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:perform-sort"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:perform-sort"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:processing-instruction")
    internal inner class ProcessingInstruction {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:processing-instruction"), qname("select")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl", qname("xsl:processing-instruction"), qname("select")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:sequence")
    internal inner class Sequence {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:sequence"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-2.0-transform.xsl", qname("xsl:sequence"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:sort")
    internal inner class Sort {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:sort"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:sort"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:stylesheet")
    internal inner class Stylesheet {
        @Test
        @DisplayName("1.0")
        fun stylesheet10() {
            val xsl = parseResource("tests/xslt/xslt-1.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("2.0")
        fun stylesheet20() {
            val xsl = parseResource("tests/xslt/xslt-2.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("3.0")
        fun stylesheet30() {
            val xsl = parseResource("tests/xslt/xslt-3.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@version = number")
        fun version() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:stylesheet"), qname("version"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:template")
    internal inner class Template {
        @Test
        @DisplayName("@match = pattern")
        fun match() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:template"), qname("match"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:template"), qname("match"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
            assertThat(tf.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@name = qname")
        fun name() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:template"), qname("name"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:template"), qname("name"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:transform")
    internal inner class Transform {
        @Test
        @DisplayName("1.0")
        fun transform10() {
            val xsl = parseResource("tests/xslt/xslt-1.0-transform.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("2.0")
        fun transform20() {
            val xsl = parseResource("tests/xslt/xslt-2.0-transform.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("3.0")
        fun transform30() {
            val xsl = parseResource("tests/xslt/xslt-3.0-transform.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(xsl.isXslPattern(), `is`(false))
            assertThat(xsl.isXslExpression(), `is`(false))
        }

        @Test
        @DisplayName("@version = number")
        fun version() {
            val ss = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:transform"), qname("version"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:try")
    internal inner class Try {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:try"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-3.0-transform.xsl", qname("xsl:try"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:value-of")
    internal inner class ValueOf {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:value-of"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:value-of"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:variable")
    internal inner class Variable {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:variable"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:variable"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:when")
    internal inner class When {
        @Test
        @DisplayName("@test = expression")
        fun test() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:when"), qname("test"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:when"), qname("test"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }

    @Nested
    @DisplayName("xsl:with-param")
    internal inner class WithParam {
        @Test
        @DisplayName("@select = expression")
        fun select() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:with-param"), qname("select"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))
            assertThat(ss.isXslExpression(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:with-param"), qname("select"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
            assertThat(tf.isXslExpression(), `is`(true))
        }
    }
}
