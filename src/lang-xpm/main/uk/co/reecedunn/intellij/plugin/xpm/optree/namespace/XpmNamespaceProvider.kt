// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.optree.namespace

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface XpmNamespaceProvider {
    companion object {
        val EP_NAME: ExtensionPointName<XpmNamespaceProviderBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.namespaceProvider"
        )

        @TestOnly
        @Suppress("UsePropertyAccessSyntax")
        fun register(
            plugin: PluginDescriptorProvider,
            provider: XpmNamespaceProvider? = null,
            fieldName: String = "INSTANCE"
        ) {
            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(EP_NAME, XpmNamespaceProviderBean::class.java, plugin.pluginDisposable)
            if (provider == null) return

            val bean = XpmNamespaceProviderBean()
            bean.implementationClass = provider.javaClass.name
            bean.fieldName = fieldName
            bean.setPluginDescriptor(plugin.pluginDescriptor)
            app.registerExtension(EP_NAME, bean, plugin.pluginDisposable)
        }
    }

    fun staticallyKnownNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration>

    fun defaultNamespace(context: PsiElement, type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration>
}
