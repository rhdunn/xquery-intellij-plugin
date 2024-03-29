/*
 * Copyright (C) 2016-2018, 2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.project.settings

import com.intellij.openapi.util.JDOMUtil
import com.intellij.util.xmlb.XmlSerializer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.W3C
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@DisplayName("IntelliJ - Settings - Languages and Frameworks - XQuery Project Settings")
class XQueryProjectSettingsTest {
    @Test
    @DisplayName("default values")
    fun testDefaultValues() {
        val settings = XQueryProjectSettings()
        assertThat(settings.implementationVersion, `is`("w3c/spec/v1ed"))
        assertThat(settings.XQueryVersion, `is`(XQuerySpec.REC_1_0_20070123.versionId))
        assertThat(settings.XQuery10Dialect, `is`(XQuerySpec.id))
        assertThat(settings.XQuery30Dialect, `is`(XQuerySpec.id))
        assertThat(settings.XQuery31Dialect, `is`(XQuerySpec.id))

        assertThat(settings.product, `is`(W3C.SPECIFICATIONS))
        assertThat(settings.productVersion, `is`(W3C.FIRST_EDITION))
    }

    @Test
    @DisplayName("state; get")
    fun testGetState() {
        val settings = XQueryProjectSettings()
        assertThat(settings.state, `is`(settings))
    }

    @Test
    @DisplayName("state; load")
    fun testLoadState() {
        val other = XQueryProjectSettings()
        other.implementationVersion = "marklogic/v6"
        other.XQueryVersion = XQuerySpec.MARKLOGIC_0_9.versionId
        other.XQuery10Dialect = MarkLogic.id
        other.XQuery30Dialect = MarkLogic.id
        other.XQuery31Dialect = MarkLogic.id

        val settings = XQueryProjectSettings()
        settings.loadState(other)
        assertThat(settings.implementationVersion, `is`("marklogic/v6"))
        assertThat(settings.XQueryVersion, `is`(XQuerySpec.MARKLOGIC_0_9.versionId))
        assertThat(settings.XQuery10Dialect, `is`(MarkLogic.id))
        assertThat(settings.XQuery30Dialect, `is`(MarkLogic.id))
        assertThat(settings.XQuery31Dialect, `is`(MarkLogic.id))

        assertThat(settings.product, `is`(MarkLogic.MARKLOGIC))
        assertThat(settings.productVersion, `is`(MarkLogic.VERSION_6_0))
    }

    @Test
    @DisplayName("transient properties")
    fun testTransientProperties() {
        val settings = XQueryProjectSettings()
        settings.implementationVersion = "marklogic/v6"

        assertThat(settings.product, `is`(MarkLogic.MARKLOGIC))
        assertThat(settings.productVersion, `is`(MarkLogic.VERSION_6_0))

        // Setting via the transient properties updates the bean properties.
        assertThat(settings.implementationVersion, `is`("marklogic/v6"))
        assertThat(settings.XQuery10Dialect, `is`("xquery"))
        assertThat(settings.XQuery30Dialect, `is`("xquery"))
        assertThat(settings.XQuery31Dialect, `is`("xquery"))
    }

    @Test
    @DisplayName("unsupported implementation version")
    fun testDefaultXQueryDialectForUnsupportedXQueryVersions() {
        val settings = XQueryProjectSettings()
        settings.implementationVersion = "marklogic/v7"
        settings.XQuery10Dialect = null
        settings.XQuery30Dialect = null
        settings.XQuery31Dialect = null

        settings.implementationVersion = "w3c/spec"
        settings.XQuery10Dialect = "w3c/1.0"
        settings.XQuery30Dialect = "w3c/3.0"
        settings.XQuery31Dialect = "w3c/3.1"
    }

    @Test
    @DisplayName("serialization to xml")
    fun testSerialization() {
        val settings = XQueryProjectSettings()

        val expected = """
            <XQueryProjectSettings>
              <option name="XQuery10Dialect" value="xquery" />
              <option name="XQuery30Dialect" value="xquery" />
              <option name="XQuery31Dialect" value="xquery" />
              <option name="XQueryVersion" value="1.0" />
              <option name="implementationVersion" value="w3c/spec/v1ed" />
            </XQueryProjectSettings>
        """.trimIndent()
        val element = XmlSerializer.serialize(settings)
        assertThat(JDOMUtil.write(element), `is`(expected))
    }
}
