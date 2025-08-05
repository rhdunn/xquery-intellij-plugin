// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.optree.function

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import javax.swing.Icon

interface XpmFunctionDecorator {
    companion object {
        val EP_NAME: ExtensionPointName<XpmFunctionDecoratorBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.functionDecorator"
        )

        fun getIcon(function: XpmFunctionDeclaration): Icon? {
            return EP_NAME.extensionList.asSequence().mapNotNull { it.getInstance().getIcon(function) }.firstOrNull()
        }


        @TestOnly
        fun register(plugin: PluginDescriptorProvider) {
            val app = ApplicationManager.getApplication()
            app.registerExtensionPointBean(
                EP_NAME,
                XpmFunctionDecoratorBean::class.java,
                plugin.pluginDisposable
            )
        }
    }

    fun getIcon(function: XpmFunctionDeclaration): Icon?
}
