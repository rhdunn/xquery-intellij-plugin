/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmEmptySequence
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.toQName
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_parse
import java.io.File
import java.net.URLClassLoader

val SAXON_NAMESPACES = mapOf(
    // XQuery 1.0
    "xml" to "http://www.w3.org/XML/1998/namespace",
    "xs" to "http://www.w3.org/2001/XMLSchema",
    "xsi" to "http://www.w3.org/2001/XMLSchema-instance",
    "fn" to "http://www.w3.org/2005/xpath-functions",
    "local" to "http://www.w3.org/2005/xquery-local-functions",
    // XQuery 3.0
    "math" to "http://www.w3.org/2005/xpath-functions/math",
    // XQuery 3.1
    "map" to "http://www.w3.org/2005/xpath-functions/map",
    "array" to "http://www.w3.org/2005/xpath-functions/array"
)

internal class SaxonClasses(path: File) {
    val loader: ClassLoader

    val itemClass: Class<*>
    val typeClass: Class<*>
    val typeHierarchyClass: Class<*>
    val xdmSequenceIteratorClass: Class<*>

    init {
        loader = URLClassLoader(arrayOf(path.toURI().toURL()))

        itemClass = loader.loadClass("net.sf.saxon.om.Item")
        typeClass = loader.loadClass("net.sf.saxon.type.Type")
        typeHierarchyClass = loader.loadClass("net.sf.saxon.type.TypeHierarchy")
        xdmSequenceIteratorClass = loader.loadClass("net.sf.saxon.s9api.XdmSequenceIterator")
    }

    fun tryXdmValue(value: Any?, type: String?): Any? {
        return try {
            toXdmValue(value, type)
        } catch (e: Exception) {
            null
        }
    }

    fun toXdmValue(value: Any?, type: String?): Any? {
        return value?.let {
            when (type) {
                "empty-sequence()" -> XdmEmptySequence.getInstance(loader).saxonObject
                "xs:QName" -> {
                    // The string constructor throws "Requested type is namespace-sensitive"
                    XdmAtomicValue(op_qname_parse(value as String, SAXON_NAMESPACES).toQName(loader)).saxonObject
                }
                "xs:numeric" -> {
                    tryXdmValue(value, "xs:double") ?: tryXdmValue(value, "xs:integer") ?: toXdmValue(value, "xs:decimal")
                }
                else -> XdmAtomicValue(value as String, type ?: "xs:string", loader).saxonObject
            }
        } ?: XdmEmptySequence.getInstance(loader).saxonObject
    }
}
