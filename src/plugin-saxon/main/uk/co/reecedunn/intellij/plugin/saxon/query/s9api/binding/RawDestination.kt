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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.event.Receiver
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.event.SequenceOutputter
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.Destination as ProxyDestination

import java.net.URI
import java.util.ArrayList

// Saxon 9.8 and earlier do not implement the RawDestination class.
class RawDestination(val classLoader: ClassLoader) : ProxyDestination {
    private var baseURI: URI? = null
    private val listeners = ArrayList<Action>()
    private var outputter: SequenceOutputter? = null

    override val saxonObject: Any get() = this

    fun getXdmValue(): XdmValue {
        return XdmValue.wrap(outputter!!.getSequence(), classLoader)
    }

    override fun setDestinationBaseURI(baseURI: URI?) {
        this.baseURI = baseURI
    }

    override fun getDestinationBaseURI(): URI? {
        return baseURI
    }

    override fun getReceiver(pipe: Any, params: Any?): Receiver {
        outputter = SequenceOutputter(pipe, classLoader)
        return outputter!!
    }

    override fun onClose(listener: Action) {
        listeners.add(listener)
    }

    override fun closeAndNotify() {
        close()
        listeners.forEach { listener -> listener.act() }
    }

    override fun close() {
        outputter?.close()
    }
}
