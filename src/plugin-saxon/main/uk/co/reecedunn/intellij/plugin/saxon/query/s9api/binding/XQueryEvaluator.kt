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

class XQueryEvaluator(private val `object`: Any, private val `class`: Class<*>) {
    fun setContextItem(item: Any) {
        val xdmItemClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmItem")
        `class`.getMethod("setContextItem", xdmItemClass).invoke(`object`, item)
    }

    fun setExternalVariable(qname: Any, value: Any?) {
        val qnameClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.QName")
        val xdmValueClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmValue")
        `class`.getMethod("setExternalVariable", qnameClass, xdmValueClass).invoke(`object`, qname, value)
    }

    fun iterator(): Any {
        return `class`.getMethod("iterator").invoke(`object`)
    }
}
