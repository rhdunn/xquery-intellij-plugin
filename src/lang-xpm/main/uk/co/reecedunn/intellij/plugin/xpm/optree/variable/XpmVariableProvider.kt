// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.optree.variable

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface XpmVariableProvider {
    companion object {
        val EP_NAME: ExtensionPointName<XpmVariableProviderBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.variableProvider"
        )

        @Suppress("UsePropertyAccessSyntax")
        fun register(
            plugin: PluginDescriptorProvider,
            provider: XpmVariableProvider? = null,
            fieldName: String = "INSTANCE"
        ) {
            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmVariableProviderBean::class.java, plugin.pluginDisposable)
            if (provider == null) return

            val bean = XpmVariableProviderBean()
            bean.implementationClass = provider.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun inScopeVariables(context: PsiElement): Sequence<XpmVariableDefinition>
}
