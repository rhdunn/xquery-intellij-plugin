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
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.*
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Getter
import java.lang.reflect.Modifier

abstract class PlatformLiteFixture : com.intellij.testFramework.UsefulTestCase() {
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

    val project: Project
        get() = myProject

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
    // region Registering Extension Points

    fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass)
    }

    fun registerExtensionPoint(epClassName: String, epField: String, aClass: Class<*>? = null) {
        registerExtensionPoint(Extensions.getRootArea(), epClassName, epField, aClass)
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
            area.registerExtensionPoint(extensionPointName, aClass.name, kind, testRootDisposable)
            Disposer.register(myProject, com.intellij.openapi.Disposable {
                area.unregisterExtensionPoint(extensionPointName.name)
            })
        }
    }

    fun registerExtensionPoint(area: ExtensionsArea, epClassName: String, epField: String, aClass: Class<*>? = null) {
        try {
            val epClass = Class.forName(epClassName)
            val epname = epClass.getDeclaredField(epField)
            val register = PlatformLiteFixture::class.java.getDeclaredMethod(
                "registerExtensionPoint", ExtensionsArea::class.java, ExtensionPointName::class.java, Class::class.java
            )
            epname.isAccessible = true
            register.invoke(this, area, epname.get(null), aClass ?: epClass)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
        }
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    @Suppress("SameParameterValue")
    fun <T> registerExtensionPoint(
        area: AreaInstance,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        registerExtensionPoint(Extensions.getArea(area), extensionPointName, aClass)
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    fun registerExtensionPoint(area: AreaInstance, epClassName: String, epField: String, aClass: Class<*>? = null) {
        registerExtensionPoint(Extensions.getArea(area), epClassName, epField, aClass)
    }

    // endregion
}
