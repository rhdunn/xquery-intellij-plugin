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

import com.intellij.util.xml.impl.DomApplicationComponent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.ast.XsltPackage
import uk.co.reecedunn.intellij.plugin.xslt.ast.XsltStylesheet
import uk.co.reecedunn.intellij.plugin.xslt.dom.XslPackageDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XsltStylesheetDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XslTransformDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

@DisplayName("XSLT 3.0 - Document Object Model - DomFileDescription")
private class XsltDomFileDescriptionTest : ParserTestCase() {
    @Test
    @DisplayName("XSLT 3.0 (3.5) xsl:package")
    fun xslPackage() {
        val ss = DomApplicationComponent.getInstance().getFileDescriptions("package").toList()
        assertThat(ss.size, `is`(1))

        assertThat(ss[0], `is`(sameInstance(XslPackageDomFileDescription)))
        assertThat(ss[0].allPossibleRootTagNamespaces, `is`(arrayOf(XSLT.NAMESPACE)))
        assertThat(ss[0].rootElementClass, `is`(sameInstance(XsltPackage::class.java)))
        assertThat(ss[0].rootTagName, `is`("package"))
    }

    @Test
    @DisplayName("XSLT 3.0 (3.7) xsl:stylesheet")
    fun xslStylesheet() {
        val ss = DomApplicationComponent.getInstance().getFileDescriptions("stylesheet").toList()
        assertThat(ss.size, `is`(1))

        assertThat(ss[0], `is`(sameInstance(XsltStylesheetDomFileDescription)))
        assertThat(ss[0].allPossibleRootTagNamespaces, `is`(arrayOf(XSLT.NAMESPACE)))
        assertThat(ss[0].rootElementClass, `is`(sameInstance(XsltStylesheet::class.java)))
        assertThat(ss[0].rootTagName, `is`("stylesheet"))
    }

    @Test
    @DisplayName("XSLT 3.0 (3.7) xsl:transform")
    fun xslTransform() {
        val ss = DomApplicationComponent.getInstance().getFileDescriptions("transform").toList()
        assertThat(ss.size, `is`(1))

        assertThat(ss[0], `is`(sameInstance(XslTransformDomFileDescription)))
        assertThat(ss[0].allPossibleRootTagNamespaces, `is`(arrayOf(XSLT.NAMESPACE)))
        assertThat(ss[0].rootElementClass, `is`(sameInstance(XsltStylesheet::class.java)))
        assertThat(ss[0].rootTagName, `is`("transform"))
    }
}
