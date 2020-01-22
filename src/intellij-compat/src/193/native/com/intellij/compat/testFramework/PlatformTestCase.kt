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
import com.intellij.openapi.extensions.AreaInstance
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.ExtensionsArea
import org.picocontainer.MutablePicoContainer

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
