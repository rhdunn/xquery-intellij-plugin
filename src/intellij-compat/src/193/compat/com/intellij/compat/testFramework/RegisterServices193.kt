/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import org.picocontainer.MutablePicoContainer
import org.picocontainer.PicoContainer
import org.picocontainer.PicoInitializationException
import org.picocontainer.PicoIntrospectionException
import org.picocontainer.defaults.AbstractComponentAdapter

fun MutablePicoContainer.registerProgressManager() {
    val component = getComponentAdapter(ProgressManager::class.java.name)
    if (component == null) {
        registerComponent(object : AbstractComponentAdapter(ProgressManager::class.java.name, Any::class.java) {
            @Throws(PicoInitializationException::class, PicoIntrospectionException::class)
            override fun getComponentInstance(container: PicoContainer): Any = ProgressManagerImpl()

            @Throws(PicoIntrospectionException::class)
            override fun verify(container: PicoContainer) {
            }
        })
    }
}
