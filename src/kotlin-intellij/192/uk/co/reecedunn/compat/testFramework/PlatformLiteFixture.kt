/*
 * Copyright (C) 2019-2020 Reece H. Dunn
 * Copyright 2000-2019 JetBrains s.r.o.
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
package uk.co.reecedunn.compat.testFramework

import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.*
import com.intellij.psi.codeStyle.modifier.CodeStyleSettingsModifier
import com.intellij.testFramework.PlatformTestUtil
import org.picocontainer.MutablePicoContainer
import java.lang.reflect.Modifier

abstract class PlatformLiteFixture : com.intellij.compat.testFramework.PlatformTestCase() {
    protected fun registerFileBasedIndex() {
        // Not needed for using the XML DOM on IntelliJ <= 2019.2
    }

    @Suppress("UnstableApiUsage")
    protected fun registerCodeStyleSettingsModifier() {
        registerExtensionPoint(CodeStyleSettingsModifier.EP_NAME, CodeStyleSettingsModifier::class.java)
    }

    protected fun <T: Any> registerExtension(extensionPointName: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getRootArea(), extensionPointName, extension)
    }

    @Suppress("UnstableApiUsage")
    fun <T: Any> registerExtension(area: ExtensionsArea, name: ExtensionPointName<T>, extension: T) {
        registerExtensionPoint(area, name, extension.javaClass)
        PlatformTestUtil.registerExtension(area, name, extension, testRootDisposable)
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    fun <T: Any> registerExtension(area: AreaInstance, name: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getArea(area), name, extension)
    }

    protected open fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass)
    }

    @Suppress("UnstableApiUsage")
    protected open fun <T> registerExtensionPoint(
        area: ExtensionsArea,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        if (!area.hasExtensionPoint(extensionPointName)) {
            val kind =
                if (aClass.isInterface || aClass.modifiers and Modifier.ABSTRACT != 0) ExtensionPoint.Kind.INTERFACE else ExtensionPoint.Kind.BEAN_CLASS
            area.registerExtensionPoint(extensionPointName, aClass.name, kind, testRootDisposable)
        }
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    @Suppress("SameParameterValue")
    protected open fun <T> registerExtensionPoint(
        area: AreaInstance,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        registerExtensionPoint(Extensions.getArea(area), extensionPointName, aClass)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> registerComponentInstance(container: MutablePicoContainer, key: Class<T>, implementation: T): T {
        val old = container.getComponentInstance(key)
        container.unregisterComponent(key)
        container.registerComponentInstance(key, implementation)

        return old as T
    }

    fun <T> registerComponentInstance(container: ComponentManager, key: Class<T>, implementation: T): T {
        return registerComponentInstance(container.picoContainer as MutablePicoContainer, key, implementation)
    }
}
