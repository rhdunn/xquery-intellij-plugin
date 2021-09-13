/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.optree.function

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

interface XpmFunctionProvider {
    companion object {
        val EP_NAME: ExtensionPointName<XpmFunctionProviderBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.functionProvider"
        )

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(plugin: PluginDescriptorProvider, provider: XpmFunctionProvider, fieldName: String = "INSTANCE") {
            val bean = XpmFunctionProviderBean()
            bean.implementationClass = provider.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)

            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmFunctionProviderBean::class.java, plugin.pluginDisposable)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun staticallyKnownFunctions(file: PsiFile): Sequence<XpmFunctionDeclaration>

    fun staticallyKnownFunctions(eqname: XsQNameValue): Sequence<XpmFunctionDeclaration>
}
