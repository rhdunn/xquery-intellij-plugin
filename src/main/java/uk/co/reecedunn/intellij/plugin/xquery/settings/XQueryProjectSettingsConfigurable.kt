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
package uk.co.reecedunn.intellij.plugin.xquery.settings

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import uk.co.reecedunn.intellij.plugin.core.ui.ConfigurableImpl
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI

class XQueryProjectSettingsConfigurable(project: Project):
        ConfigurableImpl<XQueryProjectSettings>(XQueryProjectSettings.getInstance(project)) {

    @Nls
    override fun getDisplayName(): String = "XQuery"

    override fun getHelpTopic(): String? = null

    override fun createSettingsUI(): SettingsUI<XQueryProjectSettings> = XQueryPropertiesUI()
}
