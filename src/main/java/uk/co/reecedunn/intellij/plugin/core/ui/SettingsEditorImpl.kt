/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.ui

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor

import javax.swing.*

class SettingsEditorImpl<Configuration>(private val mSettingsFactory: SettingsUIFactory<Configuration>):
        SettingsEditor<Configuration>() {

    private var mSettings: SettingsUI<Configuration>? = null

    override fun resetEditorFrom(configuration: Configuration) {
        mSettings!!.reset(configuration)
    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(configuration: Configuration) {
        mSettings!!.apply(configuration)
    }

    override fun createEditor(): JComponent {
        mSettings = mSettingsFactory.createSettingsUI()
        return mSettings!!.panel
    }
}
