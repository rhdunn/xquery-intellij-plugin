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
package com.intellij.compat.testFramework

import com.intellij.mock.MockApplication
import com.intellij.mock.MockApplicationEx
import com.intellij.mock.MockProjectEx
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.*
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Getter
import com.intellij.testFramework.PlatformTestUtil
import org.picocontainer.MutablePicoContainer
import java.lang.reflect.Modifier

abstract class PlatformTestCase : com.intellij.testFramework.UsefulTestCase() {
    // region Application

    private var myApp: MockApplication? = null

    fun initApplication(): MockApplication {
        val app = MockApplicationEx(testRootDisposable)
        ApplicationManager.setApplication(app, Getter { FileTypeManager.getInstance() }, testRootDisposable)
        Extensions.registerAreaClass("IDEA_PROJECT", null) // Deprecated in IntelliJ 2019.3.
        return app
    }

    // endregion
    // region Project

    private var myProjectEx: MockProjectEx? = null
    protected var myProject: MockProjectEx
        get() = myProjectEx!!
        set(value) {
            myProjectEx = value
        }

    // endregion
    // region JUnit

    @Suppress("UnstableApiUsage")
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        Extensions.cleanRootArea(testRootDisposable)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        myProjectEx = null
        try {
            super.tearDown()
        } finally {
            clearFields(this)
        }
    }

    // endregion
    // region Registering Application Services

    fun <T> registerApplicationService(aClass: Class<T>, `object`: T) {
        val application = ApplicationManager.getApplication() as MockApplicationEx
        application.registerService(aClass, `object`)
        Disposer.register(testRootDisposable, Disposable {
            application.picoContainer.unregisterComponent(aClass.name)
        })
    }

    // endregion
    // region Registering Component Instances

    fun <T> registerComponentInstance(container: MutablePicoContainer, key: Class<T>, implementation: T): T {
        val old = container.getComponentInstance(key)
        container.unregisterComponent(key)
        container.registerComponentInstance(key, implementation)

        @Suppress("UNCHECKED_CAST")
        return old as T
    }

    fun <T> registerComponentInstance(container: ComponentManager, key: Class<T>, implementation: T): T {
        return registerComponentInstance(container.picoContainer as MutablePicoContainer, key, implementation)
    }

    // endregion
    // region Registering Extension Points

    open fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass)
    }

    @Suppress("UnstableApiUsage")
    open fun <T> registerExtensionPoint(
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
    open fun <T> registerExtensionPoint(
        area: AreaInstance,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        registerExtensionPoint(Extensions.getArea(area), extensionPointName, aClass)
    }

    // endregion
    // region Registering Extensions

    @Suppress("UnstableApiUsage")
    private fun <T : Any> registerExtension(area: ExtensionsArea, name: ExtensionPointName<T>, extension: T) {
        PlatformTestUtil.registerExtension(area, name, extension, testRootDisposable)
    }

    fun <T : Any> registerExtension(extensionPointName: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getRootArea(), extensionPointName, extension)
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    fun <T : Any> registerExtension(area: AreaInstance, name: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getArea(area), name, extension)
    }

    // endregion
}
