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
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.AreaInstance
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import java.lang.reflect.Modifier

abstract class PlatformLiteFixture : com.intellij.testFramework.UsefulTestCase() {
    // region Project

    private var mainProject: Project? = null
    val project: Project
        get() = mainProject!!

    // endregion
    // region JUnit

    override fun setUp() {
        MockApplication.setUp(testRootDisposable)
        mainProject = MockProjectEx(testRootDisposable)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        mainProject = null
        try {
            super.tearDown()
        } catch (e: Throwable) {
            // IntelliJ 2020.1 can throw an error in CodeStyleSettingsManager.getCurrentSettings
            // when trying to clean up the registered endpoints. Log that error, but don't make
            // it fail the test.
            LOG.warn(e)
        } finally {
            clearFields(this)
        }
    }

    // endregion
    // region Registering Extension Points

    fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        registerExtensionPoint(ApplicationManager.getApplication().extensionArea, extensionPointName, aClass)
    }

    fun registerExtensionPoint(epClassName: String, epField: String, aClass: Class<*>? = null) {
        registerExtensionPoint(ApplicationManager.getApplication().extensionArea, epClassName, epField, aClass)
    }

    @Suppress("UnstableApiUsage")
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
            Disposer.register(project, com.intellij.openapi.Disposable {
                area.unregisterExtensionPoint(extensionPointName.name)
            })
        }
    }

    fun registerExtensionPoint(area: ExtensionsArea, epClassName: String, epField: String, aClass: Class<*>? = null) {
        try {
            val epClass = Class.forName(epClassName)
            val epName = epClass.getDeclaredField(epField)
            val register = PlatformLiteFixture::class.java.getDeclaredMethod(
                "registerExtensionPoint", ExtensionsArea::class.java, ExtensionPointName::class.java, Class::class.java
            )
            epName.isAccessible = true
            register.invoke(this, area, epName.get(null), aClass ?: epClass)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
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

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    @Suppress("UnstableApiUsage")
    fun registerExtensionPoint(area: AreaInstance, epClassName: String, epField: String, aClass: Class<*>? = null) {
        registerExtensionPoint(area.extensionArea, epClassName, epField, aClass)
    }

    // endregion
}
