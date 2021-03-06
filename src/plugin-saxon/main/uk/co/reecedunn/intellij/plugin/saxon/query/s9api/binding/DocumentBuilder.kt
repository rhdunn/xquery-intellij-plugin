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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import javax.xml.transform.Source

class DocumentBuilder(private val `object`: Any, private val `class`: Class<*>) {
    fun build(source: Source): XdmNode {
        val node = `class`.getMethod("build", Source::class.java).invoke(`object`, source)
        return XdmNode(node, `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmNode"))
    }

    fun wrap(node: Any): XdmNode {
        val wrapped = `class`.getMethod("wrap", Any::class.java).invoke(`object`, node)
        return XdmNode(wrapped, `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmNode"))
    }
}
