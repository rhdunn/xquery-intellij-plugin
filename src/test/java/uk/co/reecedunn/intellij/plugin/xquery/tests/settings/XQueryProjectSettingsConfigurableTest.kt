/*
 * Copyright (C) 2016 Reece H. Dunn
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

import com.intellij.ide.ui.UISettings
import com.intellij.openapi.options.ConfigurationException
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.W3C
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettingsConfigurable
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

import javax.swing.*
import java.awt.*

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat

class XQueryProjectSettingsConfigurableTest : ParserTestCase() {
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        registerApplicationService(UISettings::class.java, UISettings())
    }

    private fun getComponentByName(component: JComponent, name: String): Component? {
        return component.components.firstOrNull { it.name != null && it.name == name }
    }

    private fun getSelectedItem(component: JComponent?, name: String): Any? {
        val comboBox = getComponentByName(component!!, name) as JComboBox<*>?
        return comboBox?.selectedItem
    }

    private fun setSelectedItem(component: JComponent?, name: String, item: Any) {
        val comboBox = getComponentByName(component!!, name) as JComboBox<*>?
        if (comboBox != null) {
            comboBox.selectedItem = item
        }
    }

    fun testDisplayName() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        assertThat(configurable.displayName, `is`("XQuery"))
    }

    fun testHelpTopic() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        assertThat(configurable.helpTopic, `is`(nullValue()))
    }

    @Throws(ConfigurationException::class)
    fun testCreateComponent() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        val component = configurable.createComponent()
        assertThat(component!!.javaClass.name, `is`(JPanel::class.java.name))

        assertThat(getComponentByName(component, "Implementation"), `is`(notNullValue()))
        assertThat(getComponentByName(component, "ImplementationVersion"), `is`(notNullValue()))
        assertThat(getComponentByName(component, "XQueryVersion"), `is`(notNullValue()))
        assertThat(getComponentByName(component, "DialectForXQuery1.0"), `is`(notNullValue()))
        assertThat(getComponentByName(component, "DialectForXQuery3.0"), `is`(notNullValue()))
        assertThat(getComponentByName(component, "DialectForXQuery3.1"), `is`(notNullValue()))

        assertThat(getSelectedItem(component, "Implementation"), `is`(W3C.SPECIFICATIONS as Any))
        assertThat(getSelectedItem(component, "ImplementationVersion"), `is`(W3C.FIRST_EDITION as Any))
        assertThat(getSelectedItem(component, "XQueryVersion"), `is`(XQuery.REC_1_0_20070123 as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), `is`(XQuery as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), `is`(XQuery as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), `is`(XQuery as Any))

        assertThat(configurable.isModified, `is`(false))
    }

    @Throws(ConfigurationException::class)
    fun testApply() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        val component = configurable.createComponent()

        setSelectedItem(component, "Implementation", MarkLogic.MARKLOGIC)

        assertThat(getSelectedItem(component, "Implementation"), `is`(MarkLogic.MARKLOGIC as Any))
        assertThat(getSelectedItem(component, "ImplementationVersion"), `is`(MarkLogic.VERSION_6_0 as Any))
        assertThat(getSelectedItem(component, "XQueryVersion"), `is`(XQuery.REC_1_0_20070123 as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), `is`(XQuery as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), `is`(nullValue()))
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), `is`(nullValue()))

        assertThat(configurable.isModified, `is`(true))

        configurable.apply()
        val settings = XQueryProjectSettings.getInstance(myProject)
        assertThat(settings.implementationVersion, `is`("marklogic/v6"))
        assertThat(settings.XQueryVersion, `is`("1.0"))
        assertThat(settings.XQuery10Dialect, `is`("xquery"))
        assertThat(settings.XQuery30Dialect, `is`(nullValue()))
        assertThat(settings.XQuery31Dialect, `is`(nullValue()))

        configurable.disposeUIResources()
    }

    @Throws(ConfigurationException::class)
    fun testReset() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        val component = configurable.createComponent()

        setSelectedItem(component, "Implementation", MarkLogic.MARKLOGIC)
        configurable.reset()

        assertThat(getSelectedItem(component, "Implementation"), `is`(W3C.SPECIFICATIONS as Any))
        assertThat(getSelectedItem(component, "ImplementationVersion"), `is`(W3C.FIRST_EDITION as Any))
        assertThat(getSelectedItem(component, "XQueryVersion"), `is`(XQuery.REC_1_0_20070123 as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), `is`(XQuery as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), `is`(XQuery as Any))
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), `is`(XQuery as Any))

        assertThat(configurable.isModified, `is`(false))

        configurable.disposeUIResources()
    }
}
