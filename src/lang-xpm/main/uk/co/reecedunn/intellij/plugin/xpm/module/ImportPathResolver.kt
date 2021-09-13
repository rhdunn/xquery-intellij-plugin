/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface ImportPathResolver {
    companion object {
        val EP_NAME: ExtensionPointName<ImportPathResolverBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.importPathResolver"
        )

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(plugin: PluginDescriptorProvider, resolver: ImportPathResolver, fieldName: String = "INSTANCE") {
            val bean = ImportPathResolverBean()
            bean.implementationClass = resolver.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)

            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, ImportPathResolverBean::class.java, plugin.pluginDisposable)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun match(path: String): Boolean

    fun resolve(path: String): VirtualFile?
}
