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
package uk.co.reecedunn.intellij.plugin.xslt.tests.dom

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xslt.ast.XsltDomElement
import uk.co.reecedunn.intellij.plugin.xslt.ast.XsltPackage
import uk.co.reecedunn.intellij.plugin.xslt.ast.XsltStylesheet
import uk.co.reecedunn.intellij.plugin.xslt.dom.xslt
import uk.co.reecedunn.intellij.plugin.xslt.dom.xsltFile
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

@DisplayName("XSLT 3.0 - Document Object Model")
private class XsltDomTest : ParserTestCase() {
    fun dom(contents: String, localName: String): XsltDomElement? {
        return element(contents, localName)[0].xslt()
    }

    @Nested
    @DisplayName("XSLT 3.0 (3.5) xsl:package")
    internal inner class XslPackageElement {
        @Test
        @DisplayName("XSLT 3.0")
        fun version30() {
            val dom = dom("<package xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"3.0\"/>", "package")
            assertThat(dom, `is`(instanceOf(XsltPackage::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val pkg = dom as XsltPackage
            assertThat(pkg.version.value, `is`("3.0"))
        }
    }

    @Nested
    @DisplayName("XSLT 3.0 (3.7) xsl:stylesheet")
    internal inner class XslStylesheetElement {
        @Test
        @DisplayName("XSLT 1.0")
        fun version10() {
            val dom = dom("<stylesheet xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"/>", "stylesheet")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("1.0"))
        }

        @Test
        @DisplayName("XSLT 2.0")
        fun version20() {
            val dom = dom("<stylesheet xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\"/>", "stylesheet")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("2.0"))
        }

        @Test
        @DisplayName("XSLT 3.0")
        fun version30() {
            val dom = dom("<stylesheet xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"3.0\"/>", "stylesheet")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("3.0"))
        }
    }

    @Nested
    @DisplayName("XSLT 3.0 (3.7) xsl:transform")
    internal inner class XslTransformElement {
        @Test
        @DisplayName("XSLT 1.0")
        fun version10() {
            val dom = dom("<transform xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"/>", "transform")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("1.0"))
        }

        @Test
        @DisplayName("XSLT 2.0")
        fun version20() {
            val dom = dom("<transform xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\"/>", "transform")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("2.0"))
        }

        @Test
        @DisplayName("XSLT 3.0")
        fun version30() {
            val dom = dom("<transform xmlns=\"http://www.w3.org/1999/XSL/Transform\" version=\"3.0\"/>", "transform")
            assertThat(dom, `is`(instanceOf(XsltStylesheet::class.java)))
            assertThat(dom?.xmlElement?.xsltFile(), `is`(sameInstance(dom)))

            val ss = dom as XsltStylesheet
            assertThat(ss.version.value, `is`("3.0"))
        }
    }
}
