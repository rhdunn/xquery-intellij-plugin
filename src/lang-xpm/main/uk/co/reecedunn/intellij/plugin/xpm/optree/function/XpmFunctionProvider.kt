// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
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
        fun register(
            plugin: PluginDescriptorProvider,
            provider: XpmFunctionProvider? = null,
            fieldName: String = "INSTANCE"
        ) {
            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmFunctionProviderBean::class.java, plugin.pluginDisposable)
            if (provider == null) return

            val bean = XpmFunctionProviderBean()
            bean.implementationClass = provider.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun staticallyKnownFunctions(file: PsiFile): Sequence<XpmFunctionDeclaration>

    fun staticallyKnownFunctions(eqname: XsQNameValue): Sequence<XpmFunctionDeclaration>
}
