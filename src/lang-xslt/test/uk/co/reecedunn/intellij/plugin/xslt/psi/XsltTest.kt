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

import com.intellij.psi.xml.XmlFile
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec

@DisplayName("XSLT 3.0 - Platform Structure Interface")
private class XsltTest : ParserTestCase() {
    fun parseResource(resource: String): XmlFile {
        val file = ResourceVirtualFile(XsltTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
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
}
