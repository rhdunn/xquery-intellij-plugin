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
import java.net.URI

interface Destination {
    fun setDestinationBaseURI(baseURI: URI?)

    fun getDestinationBaseURI(): URI?

    fun getReceiver(pipe: Any, params: Any): Any

    fun onClose(listener: Any)

    fun closeAndNotify()

    fun close()
}

fun Destination.proxy(vararg classes: Class<*>): Any {
    val classLoader = classes[0].classLoader
    return Proxy.newProxyInstance(classLoader, classes) { _, method, params ->
        when (method.name) {
            "setDestinationBaseURI" -> setDestinationBaseURI(params[0] as URI?)
            "getDestinationBaseURI" -> getDestinationBaseURI()
            "getReceiver" -> getReceiver(params[0], params[1])
            "onClose" -> onClose(params[0])
            "closeAndNotify" -> closeAndNotify()
            "close" -> close()
            else -> null
        }
    }
}
