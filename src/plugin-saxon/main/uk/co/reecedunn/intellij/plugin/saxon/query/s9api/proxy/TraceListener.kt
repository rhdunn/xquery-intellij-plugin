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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy

import java.lang.reflect.Proxy

interface TraceListener {
    fun setOutputDestination(logger: Any)

    fun open(controller: Any)

    fun close()

    fun enter(instruction: Any, context: Any)

    fun leave(instruction: Any)

    fun startCurrentItem(currentItem: Any)

    fun endCurrentItem(currentItem: Any)
}

fun TraceListener.proxy(listenerClass: Class<*>): Any {
    return Proxy.newProxyInstance(listenerClass.classLoader, arrayOf(listenerClass)) { _, method, params ->
        when (method.name) {
            "setOutputDestination" -> setOutputDestination(params[0])
            "open" -> open(params[0])
            "close" -> close()
            "enter" -> enter(params[0], params[1])
            "leave" -> leave(params[0])
            "startCurrentItem" -> startCurrentItem(params[0])
            "endCurrentItem" -> endCurrentItem(params[0])
        }
    }
}
