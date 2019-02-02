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
    @DisplayName("xsl:stylesheet")
    internal inner class Stylesheet {
        @Test
        @DisplayName("1.0")
        fun stylesheet10() {
            val xsl = parseResource("tests/xslt/xslt-1.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("2.0")
        fun stylesheet20() {
            val xsl = parseResource("tests/xslt/xslt-2.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("3.0")
        fun stylesheet30() {
            val xsl = parseResource("tests/xslt/xslt-3.0-stylesheet.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
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
        }

        @Test
        @DisplayName("2.0")
        fun transform20() {
            val xsl = parseResource("tests/xslt/xslt-2.0-transform.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("3.0")
        fun transform30() {
            val xsl = parseResource("tests/xslt/xslt-3.0-transform.xsl")
            assertThat(xsl.isXslStylesheet(), `is`(true))
            assertThat(xsl.getXslVersion(), `is`(XsltSpec.REC_3_0_20170608))
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

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:template"), qname("match"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
        }

        @Test
        @DisplayName("@name = qname")
        fun name() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:template"), qname("name"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:template"), qname("name"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:number")
    internal inner class Number {
        @Test
        @DisplayName("@count = pattern")
        fun count() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("count"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("count"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
        }

        @Test
        @DisplayName("@from = pattern")
        fun from() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("from"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(true))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("from"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
        }

        @Test
        @DisplayName("@format = { string }")
        fun format() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:number"), qname("format"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:number"), qname("format"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
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

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:key"), qname("match"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(true))
        }

        @Test
        @DisplayName("@name = qname")
        fun name() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:key"), qname("name"))
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(ss.isXslPattern(), `is`(false))

            val tf = attribute("tests/xslt/xslt-1.0-transform.xsl", qname("xsl:key"), qname("name"))
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_1_0_19991116))
            assertThat(tf.isXslPattern(), `is`(false))
        }
    }

    @Nested
    @DisplayName("xsl:for-each-group")
    internal inner class ForEachGroup {
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

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                qname("xsl:for-each-group"), qname("group-ending-with")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(true))
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

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                qname("xsl:for-each-group"), qname("group-starting-with")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(true))
        }

        @Test
        @DisplayName("@collation = { uri }")
        fun collation() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                qname("xsl:for-each-group"), qname("collation")
            )
            assertThat(ss.isXslStylesheet(), `is`(true))
            assertThat(ss.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(ss.isXslPattern(), `is`(false))

            val tf = attribute(
                "tests/xslt/xslt-2.0-transform.xsl",
                qname("xsl:for-each-group"), qname("collation")
            )
            assertThat(tf.isXslStylesheet(), `is`(true))
            assertThat(tf.getXslVersion(), `is`(XsltSpec.REC_2_0_20070123))
            assertThat(tf.isXslPattern(), `is`(false))
        }
    }
}
