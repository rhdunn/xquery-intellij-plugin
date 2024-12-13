// Copyright (C) 2020, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.testFramework

import com.intellij.mock.MockApplication
import com.intellij.mock.MockProjectEx
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IdeaPlatformTestCase : PluginDescriptorProvider {
    // region PluginDescriptorProvider

    override val pluginDescriptor: PluginDescriptor
        get() = DefaultPluginDescriptor(pluginId, this::class.java.classLoader)

    override val pluginDisposable: Disposable = PluginDisposable()

    // endregion

    private var mainProject: MockProjectEx? = null

    val mockProject: MockProjectEx get() = mainProject!!
    val project: Project get() = mainProject!!

    @BeforeAll
    fun setupFixture() {
        MockApplication.setUp(pluginDisposable)
        mainProject = MockProjectEx(pluginDisposable)
        registerServicesAndExtensions()
    }

    @AfterAll
    fun tearDownFixture() {
        mainProject = null
        Disposer.dispose(pluginDisposable)
    }

    open fun registerServicesAndExtensions() {}

    private class PluginDisposable : Disposable {
        @Volatile
        var isDisposed: Boolean = false
            private set

        override fun dispose() {
            isDisposed = true
        }
    }
}
