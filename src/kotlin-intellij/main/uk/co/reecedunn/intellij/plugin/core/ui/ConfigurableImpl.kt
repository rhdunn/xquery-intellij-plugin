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

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import javax.swing.JComponent

abstract class ConfigurableImpl<in Configuration>(private val mConfiguration: Configuration):
        Configurable, SettingsUIFactory<Configuration> {

    private var mSettings: SettingsUI<Configuration>? = null

    override fun createComponent(): JComponent? {
        mSettings = createSettingsUI()
        mSettings!!.reset(mConfiguration)
        return mSettings!!.panel
    }

    override fun isModified(): Boolean {
        return mSettings!!.isModified(mConfiguration)
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        mSettings!!.apply(mConfiguration)
    }

    override fun reset() {
        mSettings!!.reset(mConfiguration)
    }

    // NOTE: IntelliJ 2016.1 does not provide a default disposeUIResources
    // implementation, so provide one here.
    override fun disposeUIResources() {
        mSettings = null
    }
}
