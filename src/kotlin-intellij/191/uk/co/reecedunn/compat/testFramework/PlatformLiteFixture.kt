/*
 * Copyright (C) 2019 Reece H. Dunn
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

import com.intellij.mock.MockApplicationEx
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.*
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Getter
import com.intellij.testFramework.PlatformTestUtil
import org.picocontainer.MutablePicoContainer
import org.picocontainer.PicoContainer
import org.picocontainer.PicoInitializationException
import org.picocontainer.PicoIntrospectionException
import org.picocontainer.defaults.AbstractComponentAdapter
import java.lang.reflect.Modifier

abstract class PlatformLiteFixture : com.intellij.compat.testFramework.PlatformTestCase() {
    @Suppress("UnstableApiUsage")
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        Extensions.cleanRootArea(testRootDisposable)
    }

    fun getApplication(): MockApplicationEx {
        return ApplicationManager.getApplication() as MockApplicationEx
    }

    fun initApplication(): MockApplicationEx {
        val app = MockApplicationEx(testRootDisposable)
        ApplicationManager.setApplication(app, Getter { FileTypeManager.getInstance() }, testRootDisposable)
        Extensions.registerAreaClass("IDEA_PROJECT", null) // Deprecated in IntelliJ 2019.3.
        return app
    }

    protected fun registerFileBasedIndex() {
        // Not needed for using the XML DOM on IntelliJ <= 2019.2
    }

    protected fun registerProgressManager(appContainer: MutablePicoContainer) {
        val component = appContainer.getComponentAdapter(ProgressManager::class.java.name)
        if (component == null) {
            appContainer.registerComponent(object :
                AbstractComponentAdapter(ProgressManager::class.java.name, Any::class.java) {

                @Throws(PicoInitializationException::class, PicoIntrospectionException::class)
                override fun getComponentInstance(container: PicoContainer): Any {
                    return ProgressManagerImpl()
                }

                @Throws(PicoIntrospectionException::class)
                override fun verify(container: PicoContainer) {
                }
            })
        }
    }

    @Suppress("UnstableApiUsage")
    protected fun registerCodeStyleSettingsModifier() {
        try {
            val epClass = Class.forName("com.intellij.application.options.CodeStyleCachingUtil")
            val epname = epClass.getDeclaredField("CODE_STYLE_SETTINGS_MODIFIER_EP_NAME")
            epname.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            registerExtensionPoint(epname.get(null) as ExtensionPointName<Any>, epClass as Class<Any>)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
        }
    }

    protected fun <T: Any> registerExtension(extensionPointName: ExtensionPointName<T>, t: T) {
        registerExtension(Extensions.getRootArea(), extensionPointName, t)
    }

    @Suppress("UnstableApiUsage")
    fun <T: Any> registerExtension(area: ExtensionsArea, name: ExtensionPointName<T>, t: T) {
        registerExtensionPoint(area, name, t.javaClass)
        PlatformTestUtil.registerExtension(area, name, t, testRootDisposable)
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

    protected fun registerComponentImplementation(
        container: MutablePicoContainer,
        key: Class<*>,
        implementation: Class<*>
    ) {
        container.unregisterComponent(key)
        container.registerComponentImplementation(key, implementation)
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

    protected fun <T> registerApplicationService(aClass: Class<T>, `object`: T) {
        getApplication().registerService(aClass, `object`)
        Disposer.register(testRootDisposable, Disposable {
            getApplication().picoContainer.unregisterComponent(aClass.name)
        })
    }
}
