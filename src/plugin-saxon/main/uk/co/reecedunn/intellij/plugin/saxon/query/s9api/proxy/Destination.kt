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

import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Action
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.event.Receiver
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Destination as SaxonDestination

import java.lang.reflect.Proxy
import java.net.URI

interface Destination : SaxonDestination {
    fun setDestinationBaseURI(baseURI: URI?)

    fun getDestinationBaseURI(): URI?

    fun getReceiver(pipe: Any, params: Any): Receiver

    fun onClose(listener: Action)

    fun closeAndNotify()

    fun close()
}

fun Destination.proxy(vararg classes: Class<*>): Any {
    val classLoader = classes[0].classLoader
    val actionClass = classLoader.loadClassOrNull("net.sf.saxon.s9api.Action")
    return Proxy.newProxyInstance(classLoader, classes) { _, method, params ->
        when (method.name) {
            "setDestinationBaseURI" -> setDestinationBaseURI(params[0] as URI?)
            "getDestinationBaseURI" -> getDestinationBaseURI()
            "getReceiver" -> getReceiver(params[0], params[1]).saxonObject
            "onClose" -> onClose(Action(params[0], actionClass!!))
            "closeAndNotify" -> closeAndNotify()
            "close" -> close()
            else -> null
        }
    }
}
