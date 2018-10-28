/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.util.IconLoader
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import javax.swing.Icon

private val FILETYPE_ICON = IconLoader.getIcon("/icons/xquery.png")
private val FILETYPE_ICON_163 = IconLoader.getIcon("/icons/xquery-163.png")

class XQueryConfigurationType : ConfigurationType {
    override fun getIcon(): Icon {
        return if (ApplicationInfo.getInstance().build.baselineVersion >= 163)
            FILETYPE_ICON_163
        else
            FILETYPE_ICON
    }

    override fun getConfigurationTypeDescription(): String = displayName

    override fun getId(): String = "XIJPXQueryProcessorConfiguration"

    override fun getDisplayName(): String = XQueryBundle.message("xquery.settings.execution.display-name")

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(QueryProcessorConfigurationFactory(this, MimeTypes.XQUERY))
    }
}
