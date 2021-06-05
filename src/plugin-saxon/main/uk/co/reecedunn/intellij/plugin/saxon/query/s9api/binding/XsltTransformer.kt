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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.proxy
import javax.xml.transform.Source
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.Destination as ProxyDestination

class XsltTransformer(private val `object`: Any, private val `class`: Class<*>) {
    fun setSource(source: Source) {
        `class`.getMethod("setSource", Source::class.java).invoke(`object`, source)
    }

    @Suppress("DuplicatedCode")
    fun setDestination(destination: Destination) {
        val destinationClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.Destination")
        if (destination is ProxyDestination) {
            val proxy = destination.proxy(destinationClass)
            `class`.getMethod("setDestination", destinationClass).invoke(`object`, proxy)
        } else {
            `class`.getMethod("setDestination", destinationClass).invoke(`object`, destination.saxonObject)
        }
    }

    fun setParameter(qname: QName, value: XdmValue) {
        val xdmValueClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmValue")
        `class`.getMethod("setParameter", qname.saxonClass, xdmValueClass).invoke(
            `object`, qname.`object`, value.saxonObject
        )
    }

    fun transform() {
        `class`.getMethod("transform").invoke(`object`)
    }
}
