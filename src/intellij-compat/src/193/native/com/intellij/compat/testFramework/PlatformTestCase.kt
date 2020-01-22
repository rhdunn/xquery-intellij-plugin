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
import com.intellij.mock.MockProjectEx
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.*
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import org.picocontainer.MutablePicoContainer
import java.lang.reflect.Modifier

abstract class PlatformTestCase : com.intellij.testFramework.UsefulTestCase() {
    // region Application

    private var myApp: MockApplication? = null

    fun initApplication(): MockApplication {
        val app = MockApplication.setUp(testRootDisposable)
        myApp = app
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

    val project: Project get() = myProject

    // endregion
    // region JUnit

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
        myApp!!.registerService(aClass, `object`, testRootDisposable)
    }

    // endregion
    // region Registering Project Services

    fun <T> registerProjectService(aClass: Class<T>, `object`: T) {
        myProject.registerService(aClass, `object`)
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

    fun <T> registerExtensionPoint(
        area: ExtensionsArea,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        if (!area.hasExtensionPoint(extensionPointName)) {
            val kind =
                if (aClass.isInterface || aClass.modifiers and Modifier.ABSTRACT != 0)
                    ExtensionPoint.Kind.INTERFACE
                else
                    ExtensionPoint.Kind.BEAN_CLASS
            val impl = area as ExtensionsAreaImpl
            impl.registerExtensionPoint(extensionPointName, aClass.name, kind, testRootDisposable)
            Disposer.register(myProject, com.intellij.openapi.Disposable {
                area.unregisterExtensionPoint(extensionPointName.name)
            })
        }
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    @Suppress("UnstableApiUsage", "SameParameterValue")
    fun <T> registerExtensionPoint(
        area: AreaInstance,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        registerExtensionPoint(area.extensionArea, extensionPointName, aClass)
    }

    // endregion
    // region Registering Extensions

    private fun <T : Any> registerExtension(area: ExtensionsArea, name: ExtensionPointName<T>, extension: T) {
        area.getExtensionPoint<T>(name.name).registerExtension(extension, testRootDisposable)
    }

    fun <T : Any> registerExtension(extensionPointName: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getRootArea(), extensionPointName, extension)
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    @Suppress("UnstableApiUsage")
    fun <T : Any> registerExtension(area: AreaInstance, name: ExtensionPointName<T>, extension: T) {
        registerExtension(area.extensionArea, name, extension)
    }

    // endregion
}
