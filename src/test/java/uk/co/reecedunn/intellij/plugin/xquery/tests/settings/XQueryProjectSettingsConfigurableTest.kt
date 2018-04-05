/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettingsConfigurable
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JComponent

class XQueryProjectSettingsConfigurableTest : ParserTestCase() {
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        registerApplicationService(UISettings::class.java, UISettings())
    }

    fun testDisplayName() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        assertThat(configurable.displayName, `is`("XQuery"))
    }

    fun testHelpTopic() {
        val configurable = XQueryProjectSettingsConfigurable(myProject)
        assertThat(configurable.helpTopic, `is`(nullValue()))
    }
}
