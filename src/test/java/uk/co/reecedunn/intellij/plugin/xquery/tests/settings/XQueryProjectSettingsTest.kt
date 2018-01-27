/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.settings

import com.intellij.util.xmlb.XmlSerializer
import junit.framework.TestCase
import org.jdom.output.XMLOutputter
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.W3C
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version

class XQueryProjectSettingsTest : TestCase() {
    fun testDefaultValues() {
        val settings = XQueryProjectSettings()
        assertThat(settings.implementationVersion, `is`("w3c/spec/v1ed"))
        assertThat(settings.XQueryVersion, `is`(XQuery.REC_1_0_20070123.label))
        assertThat(settings.XQuery10Dialect, `is`(XQuery.id))
        assertThat(settings.XQuery30Dialect, `is`(XQuery.id))
        assertThat(settings.XQuery31Dialect, `is`(XQuery.id))

        assertThat(settings.product, `is`(W3C.SPECIFICATIONS))
        assertThat(settings.productVersion, `is`(W3C.FIRST_EDITION as Version))
    }

    fun testGetState() {
        val settings = XQueryProjectSettings()
        assertThat(settings.state, `is`(settings))
    }

    fun testLoadState() {
        val other = XQueryProjectSettings()
        other.implementationVersion = "marklogic/v6"
        other.XQueryVersion = XQuery.MARKLOGIC_0_9.label
        other.XQuery10Dialect = MarkLogic.id
        other.XQuery30Dialect = MarkLogic.id
        other.XQuery31Dialect = MarkLogic.id

        val settings = XQueryProjectSettings()
        settings.loadState(other)
        assertThat(settings.implementationVersion, `is`("marklogic/v6"))
        assertThat(settings.XQueryVersion, `is`(XQuery.MARKLOGIC_0_9.label))
        assertThat(settings.XQuery10Dialect, `is`(MarkLogic.id))
        assertThat(settings.XQuery30Dialect, `is`(MarkLogic.id))
        assertThat(settings.XQuery31Dialect, `is`(MarkLogic.id))

        assertThat(settings.product, `is`(MarkLogic.MARKLOGIC))
        assertThat(settings.productVersion, `is`(MarkLogic.VERSION_6_0))
    }

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

    fun testSerialization() {
        val settings = XQueryProjectSettings()
        val outputter = XMLOutputter()

        val expected =
                "<XQueryProjectSettings>" +
                "<option name=\"XQuery10Dialect\" value=\"xquery\" />" +
                "<option name=\"XQuery30Dialect\" value=\"xquery\" />" +
                "<option name=\"XQuery31Dialect\" value=\"xquery\" />" +
                "<option name=\"XQueryVersion\" value=\"1.0\" />" +
                "<option name=\"implementationVersion\" value=\"w3c/spec/v1ed\" />" +
                "</XQueryProjectSettings>"

        val element = XmlSerializer.serialize(settings)
        assertThat(outputter.outputString(element), `is`(expected))
    }
}
