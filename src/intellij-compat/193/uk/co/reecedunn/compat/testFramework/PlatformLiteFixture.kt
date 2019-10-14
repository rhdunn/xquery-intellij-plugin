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

import com.intellij.mock.MockApplication
import com.intellij.mock.MockProjectEx
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.*
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.impl.DirectoryIndex
import com.intellij.openapi.roots.impl.DirectoryIndexImpl
import com.intellij.openapi.roots.impl.ProjectFileIndexImpl
import com.intellij.openapi.util.Getter
import com.intellij.testFramework.UsefulTestCase
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexImpl
import org.picocontainer.MutablePicoContainer
import java.lang.reflect.Modifier

abstract class PlatformLiteFixture : com.intellij.testFramework.UsefulTestCase() {
    protected var myProjectEx: MockProjectEx? = null
    protected val myProject: MockProjectEx get() = myProjectEx!!

    private var myApp: MockApplication? = null

    fun getApplication(): MockApplication = myApp!!

    fun initApplication(): MockApplication {
        val app = MockApplication.setUp(testRootDisposable)
        myApp = app
        return app
    }

    @Throws(Exception::class)
    override fun tearDown() {
        myProjectEx = null
        try {
            super.tearDown()
        } finally {
            UsefulTestCase.clearFields(this)
        }
    }

    protected fun registerFileBasedIndex() {
        myProject.registerService(DirectoryIndex::class.java, DirectoryIndexImpl(myProject))
        myProject.registerService(ProjectFileIndex::class.java, ProjectFileIndexImpl(myProject))
        registerApplicationService(FileBasedIndex::class.java, FileBasedIndexImpl())
    }

    protected fun registerProgressManager(appContainer: MutablePicoContainer) {
        val component = appContainer.getComponentAdapter(ProgressManager::class.java.name)
        if (component == null) {
            appContainer.registerComponentInstance(ProgressManager::class.java.name, ProgressManagerImpl())
        }
    }

    protected fun <T: Any> registerExtension(extensionPointName: ExtensionPointName<T>, extension: T) {
        registerExtension(Extensions.getRootArea(), extensionPointName, extension)
    }

    fun <T: Any> registerExtension(area: ExtensionsArea, name: ExtensionPointName<T>, extension: T) {
        registerExtensionPoint(area, name, extension.javaClass)
        area.getExtensionPoint<T>(name.name).registerExtension(extension, testRootDisposable)
    }

    // IntelliJ >= 2019.3 deprecates Extensions#getArea
    fun <T: Any> registerExtension(area: AreaInstance, name: ExtensionPointName<T>, extension: T) {
        registerExtension(area.extensionArea, name, extension)
    }

    protected open fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass)
    }

    protected open fun <T> registerExtensionPoint(
        area: ExtensionsArea,
        extensionPointName: ExtensionPointName<T>,
        aClass: Class<out T>
    ) {
        if (!area.hasExtensionPoint(extensionPointName)) {
            val kind =
                if (aClass.isInterface || aClass.modifiers and Modifier.ABSTRACT != 0) ExtensionPoint.Kind.INTERFACE else ExtensionPoint.Kind.BEAN_CLASS
            (area as ExtensionsAreaImpl).registerExtensionPoint(
                extensionPointName,
                aClass.name,
                kind,
                testRootDisposable
            )
        }
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
        getApplication().registerService(aClass, `object`, testRootDisposable)
    }
}
