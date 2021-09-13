/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module.loader

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.xmlb.annotations.Tag
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface XpmModuleLoaderFactory {
    companion object {
        val EP_NAME: ExtensionPointName<XpmModuleLoaderFactoryBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.moduleLoaderFactory"
        )

        fun create(name: String, context: String?): XpmModuleLoader? {
            return EP_NAME.extensionList.find { it.name == name }?.getInstance()?.loader(context)
        }

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(
            plugin: PluginDescriptorProvider,
            name: String,
            factory: XpmModuleLoaderFactory,
            fieldName: String = "INSTANCE"
        ) {
            val bean = XpmModuleLoaderFactoryBean()
            bean.name = name
            bean.implementationClass = factory::class.java.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)

            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmModuleLoaderFactoryBean::class.java, plugin.pluginDisposable)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun loader(context: String?): XpmModuleLoader?
}

@Tag("moduleLoader")
data class XpmModuleLoaderBean(var name: String = "", var context: String? = null) {
    val loader: XpmModuleLoader?
        get() = XpmModuleLoaderFactory.create(name, context)
}
