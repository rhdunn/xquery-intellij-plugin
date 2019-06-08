/*
 * Copyright (C) 2019 Reece H. Dunn
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
import com.intellij.openapi.util.Disposer
import org.picocontainer.MutablePicoContainer

// IntelliJ >= 2019.2 adds registerApplicationService.
abstract class PlatformLiteFixture : com.intellij.testFramework.PlatformLiteFixture() {
    protected fun <T> registerApplicationService(aClass: Class<T>, `object`: T) {
        getApplication().registerService(aClass, `object`)
        Disposer.register(myProject, com.intellij.openapi.Disposable {
            getApplication().picoContainer.unregisterComponent(aClass.name)
        })
    }

    companion object {
        fun getApplication(): MockApplicationEx = com.intellij.testFramework.PlatformLiteFixture.getApplication()

        fun <T> registerComponentInstance(container: MutablePicoContainer, key: Class<T>, implementation: T) {
            com.intellij.testFramework.PlatformLiteFixture.registerComponentInstance(container, key, implementation)
        }
    }
}
