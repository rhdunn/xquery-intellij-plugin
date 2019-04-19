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

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmEmptySequence
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans.toXPathException
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_parse
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader

private val SAXON_NAMESPACES = mapOf(
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

private val ATOMIC_ITEM_TYPE_NAMES = mapOf(
    "xs:anyAtomicType" to "ANY_ATOMIC_VALUE",
    "xs:anyURI" to "ANY_URI",
    "xs:base64Binary" to "BASE64_BINARY",
    "xs:boolean" to "BOOLEAN",
    "xs:byte" to "BYTE",
    "xs:date" to "DATE",
    "xs:dateTime" to "DATE_TIME",
    "xs:dateTimeStamp" to "DATE_TIME_STAMP", // XML Schema 1.1 Part 2 (fallback = xs:dateTime)
    "xs:dayTimeDuration" to "DAY_TIME_DURATION",
    "xs:decimal" to "DECIMAL",
    "xs:double" to "DOUBLE",
    "xs:duration" to "DURATION",
    "xs:ENTITY" to "ENTITY",
    "xs:float" to "FLOAT",
    "xs:gDay" to "G_DAY",
    "xs:gMonth" to "G_MONTH",
    "xs:gMonthDay" to "G_MONTH_DAY",
    "xs:gYear" to "G_YEAR",
    "xs:gYearMonth" to "G_YEAR_MONTH",
    "xs:hexBinary" to "HEX_BINARY",
    "xs:ID" to "ID",
    "xs:IDREF" to "IDREF",
    "xs:int" to "INT",
    "xs:integer" to "INTEGER",
    "xs:language" to "LANGUAGE",
    "xs:long" to "LONG",
    "xs:Name" to "NAME",
    "xs:NCName" to "NCNAME",
    "xs:negativeInteger" to "NEGATIVE_INTEGER",
    "xs:NMTOKEN" to "NMTOKEN",
    "xs:nonNegativeInteger" to "NON_NEGATIVE_INTEGER",
    "xs:nonPositiveInteger" to "NON_POSITIVE_INTEGER",
    "xs:normalizedString" to "NORMALIZED_STRING",
    "xs:NOTATION" to "NOTATION",
    "xs:numeric" to "NUMERIC", // XQuery and XPath Data Model 3.1
    "xs:positiveInteger" to "POSITIVE_INTEGER",
    "xs:QName" to "QNAME",
    "xs:short" to "SHORT",
    "xs:string" to "STRING",
    "xs:time" to "TIME",
    "xs:token" to "TOKEN",
    "xs:unsignedByte" to "UNSIGNED_BYTE",
    "xs:unsignedInt" to "UNSIGNED_INT",
    "xs:unsignedLong" to "UNSIGNED_LONG",
    "xs:unsignedShort" to "UNSIGNED_SHORT",
    "xs:untypedAtomic" to "UNTYPED_ATOMIC",
    "xs:yearMonthDuration" to "YEAR_MONTH_DURATION"
)

internal class SaxonClasses(path: File) {
    val loader: ClassLoader

    val itemClass: Class<*>
    val itemTypeClass: Class<*>
    val qnameClass: Class<*>
    val typeClass: Class<*>
    val typeHierarchyClass: Class<*>
    val xdmAtomicValueClass: Class<*>
    val xdmItemClass: Class<*>
    val xdmSequenceIteratorClass: Class<*>

    init {
        loader = URLClassLoader(arrayOf(path.toURI().toURL()))

        itemClass = loader.loadClass("net.sf.saxon.om.Item")
        itemTypeClass = loader.loadClass("net.sf.saxon.s9api.ItemType")
        qnameClass = loader.loadClass("net.sf.saxon.s9api.QName")
        typeClass = loader.loadClass("net.sf.saxon.type.Type")
        typeHierarchyClass = loader.loadClass("net.sf.saxon.type.TypeHierarchy")
        xdmAtomicValueClass = loader.loadClass("net.sf.saxon.s9api.XdmAtomicValue")
        xdmItemClass = loader.loadClass("net.sf.saxon.s9api.XdmItem")
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
                    xdmAtomicValueClass.getConstructor(qnameClass).newInstance(toQName(value as String))
                }
                "xs:numeric" -> {
                    tryXdmValue(value, "xs:double") ?: tryXdmValue(value, "xs:integer") ?: toXdmValue(value, "xs:decimal")
                }
                else -> {
                    ATOMIC_ITEM_TYPE_NAMES[type ?: "xs:string"]?.let {
                        val itemtype = itemTypeClass.getField(it).get(itemTypeClass)
                        xdmAtomicValueClass.getConstructor(String::class.java, itemTypeClass).newInstance(value, itemtype)
                    } ?: throw UnsupportedOperationException()
                }
            }
        } ?: XdmEmptySequence.getInstance(loader).saxonObject
    }

    fun toQName(qname: String): Any {
        val value = op_qname_parse(qname, SAXON_NAMESPACES)
        return when {
            value.namespace == null -> {
                qnameClass.getConstructor(String::class.java).newInstance(value.localName!!.data)
            }
            value.prefix == null -> {
                qnameClass
                    .getConstructor(String::class.java, String::class.java)
                    .newInstance(value.namespace!!.data, value.localName!!.data)
            }
            else -> {
                qnameClass
                    .getConstructor(String::class.java, String::class.java, String::class.java)
                    .newInstance(value.prefix!!.data, value.namespace!!.data, value.localName!!.data)
            }
        }
    }
}
