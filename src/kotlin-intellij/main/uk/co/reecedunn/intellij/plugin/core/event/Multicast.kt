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
package uk.co.reecedunn.intellij.plugin.core.event

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.Disposer
import com.intellij.util.containers.ContainerUtil
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.*

class Multicaster<T : EventListener>(listenerClass: Class<T>) {
    private val LOG = Logger.getInstance("#uk.co.reecedunn.intellij.plugin.core.event.Multicast")
    private val listeners = ContainerUtil.createLockFreeCopyOnWriteList<T>()

    val eventMulticaster = createEventMulticaster(listenerClass)

    fun addListener(listener: T) {
        listeners.add(listener)
    }

    fun addListener(listener: T, parentDisposable: Disposable) {
        listeners.add(listener)
        Disposer.register(parentDisposable, Disposable { listeners.remove(listener) })
    }

    fun removeListener(listener: T) {
        listeners.remove(listener)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createEventMulticaster(listenerClass: Class<T>): T {
        return Proxy.newProxyInstance(listenerClass.classLoader, arrayOf(listenerClass)) { `object`, method, params ->
            listeners.forEach {
                try {
                    method.invoke(it, *params)
                } catch (e: Throwable) {
                    if (e is InvocationTargetException && e.cause is ProcessCanceledException) {
                        LOG.info(e)
                    } else {
                        LOG.error(e)
                    }
                }
            }
            null
        } as T
    }
}
