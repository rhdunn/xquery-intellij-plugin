/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.psi

import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.XsltStylesheet
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XSLT 3.0 - IntelliJ Program Structure Interface (PSI)")
private class XsltPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XSLT 3.0 (3) Stylesheet Structure")
    internal inner class StylesheetStructure {
        @Nested
        @DisplayName("XSLT 3.0 (3.7) xsl:stylesheet")
        internal inner class Stylesheet {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                    </xsl:stylesheet>
                """
                val psi = parse<XsltStylesheet>(xml, XSLT.NAMESPACE, "stylesheet")[0]

                assertThat(psi.parent, `is`(nullValue()))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (3.7) xsl:transform")
        internal inner class Transform {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                    </xsl:transform>
                """
                val psi = parse<XsltStylesheet>(xml, XSLT.NAMESPACE, "transform")[0]

                assertThat(psi.parent, `is`(nullValue()))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))
            }
        }
    }
}
