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
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XsltVersion
import uk.co.reecedunn.intellij.plugin.xslt.psi.*
import javax.xml.namespace.QName

@DisplayName("XSLT 3.0 - Platform Structure Interface")
private class CompletionPropertyTest : ParserTestCase() {
    fun parseResource(resource: String): XmlFile {
        val file = ResourceVirtualFile(CompletionPropertyTest::class.java.classLoader, resource)
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
    @DisplayName("XSLT 1.0")
    internal inner class Xslt10 {
        @Test
        @DisplayName("expression")
        fun expression() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:apply-templates"), qname("select"))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
        }

        @Test
        @DisplayName("pattern")
        fun pattern() {
            val ss = attribute("tests/xslt/xslt-1.0-stylesheet.xsl", qname("xsl:template"), qname("match"))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_1_0_19991116))
        }
    }

    @Nested
    @DisplayName("XSLT 2.0")
    internal inner class Xslt20 {
        @Test
        @DisplayName("expression")
        fun expression() {
            val ss = attribute("tests/xslt/xslt-2.0-stylesheet.xsl", qname("xsl:attribute"), qname("select"))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
        }

        @Test
        @DisplayName("pattern")
        fun pattern() {
            val ss = attribute(
                "tests/xslt/xslt-2.0-stylesheet.xsl",
                qname("xsl:for-each-group"), qname("group-ending-with")
            )
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_2_0_20070123))
        }
    }

    @Nested
    @DisplayName("XSLT 3.0")
    internal inner class Xslt30 {
        @Test
        @DisplayName("expression")
        fun expression() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:assert"), qname("select"))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
        }

        @Test
        @DisplayName("pattern")
        fun pattern() {
            val ss = attribute("tests/xslt/xslt-3.0-stylesheet.xsl", qname("xsl:accumulator-rule"), qname("match"))
            assertThat(XsltVersion.get(ss), `is`(XsltSpec.REC_3_0_20170608))
        }
    }
}
