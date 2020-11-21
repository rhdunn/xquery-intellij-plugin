/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.testFramework

import com.intellij.compat.testFramework.createMockApplication
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

    private var mainProject: Project? = null
    val project: Project
        get() = mainProject!!

    @BeforeAll
    fun setupFixture() {
        createMockApplication(pluginDisposable)
        mainProject = MockProjectEx(pluginDisposable)
    }

    @AfterAll
    fun tearDownFixture() {
        mainProject = null
        Disposer.dispose(pluginDisposable)
    }

    private class PluginDisposable : Disposable {
        @Volatile
        var isDisposed: Boolean = false
            private set

        override fun dispose() {
            isDisposed = true
        }
    }
}
