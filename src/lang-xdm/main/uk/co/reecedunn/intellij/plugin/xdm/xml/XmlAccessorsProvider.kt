/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.xml

import com.intellij.compat.testFramework.registerExtension
import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface XmlAccessorsProvider {
    companion object {
        val EP_NAME: ExtensionPointName<XmlAccessorsProviderBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.xmlAccessors"
        )

        private val providers: Sequence<XmlAccessorsProvider>
            get() = EP_NAME.extensionList.asSequence().map { it.getInstance() }

        fun element(node: Any): Pair<Any, XmlAccessors>? = providers.mapNotNull { it.element(node) }.firstOrNull()

        fun attribute(node: Any): Pair<Any, XmlAccessors>? = providers.mapNotNull { it.attribute(node) }.firstOrNull()

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(plugin: PluginDescriptorProvider, provider: XmlAccessorsProvider, fieldName: String = "INSTANCE") {
            val bean = XmlAccessorsProviderBean()
            bean.implementationClass = provider.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)

            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XmlAccessorsProviderBean::class.java, plugin.pluginDisposable)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun element(node: Any): Pair<Any, XmlAccessors>?

    fun attribute(node: Any): Pair<Any, XmlAccessors>?
}
