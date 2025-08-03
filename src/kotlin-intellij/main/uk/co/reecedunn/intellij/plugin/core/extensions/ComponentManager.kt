// Copyright (C) 2016-2022, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2020 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.extensions

import com.intellij.mock.MockComponentManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.Disposer
import org.jetbrains.annotations.TestOnly

@TestOnly
fun <T : Any> ComponentManager.registerExtensionPointBean(
    name: ExtensionPointName<*>,
    aClass: Class<T>,
    parentDisposable: Disposable
) {
    if (!extensionArea.hasExtensionPoint(name)) {
        @Suppress("DEPRECATION")
        extensionArea.registerExtensionPoint(name.name, aClass.name, ExtensionPoint.Kind.BEAN_CLASS)
        Disposer.register(parentDisposable, Disposable {
            extensionArea.unregisterExtensionPoint(name.name)
        })
    }
}

@TestOnly
fun ComponentManager.registerExtensionPointBean(
    name: String,
    className: String,
    parentDisposable: Disposable
) {
    if (!extensionArea.hasExtensionPoint(name)) {
        @Suppress("DEPRECATION")
        extensionArea.registerExtensionPoint(name, className, ExtensionPoint.Kind.BEAN_CLASS)
        Disposer.register(parentDisposable, Disposable {
            extensionArea.unregisterExtensionPoint(name)
        })
    }
}

@TestOnly
fun <T : Any> ComponentManager.registerExtension(
    name: BaseExtensionPointName<*>,
    instance: T,
    parentDisposable: Disposable
) {
    extensionArea.getExtensionPoint<T>(name.name).registerExtension(instance, parentDisposable)
}

@TestOnly
fun <T : Any> ComponentManager.registerService(serviceInterface: Class<T>, implementation: T) {
    (this as MockComponentManager).registerService(serviceInterface, implementation)
}

@TestOnly
inline fun <reified T : Any> ComponentManager.registerService(implementation: T) {
    registerService(T::class.java, implementation)
}
