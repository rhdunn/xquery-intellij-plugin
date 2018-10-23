/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import java.io.File
import java.net.URLClassLoader

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
    val itemClass: Class<*>
    val itemTypeClass: Class<*>
    val processorClass: Class<*>
    val qnameClass: Class<*>
    val typeClass: Class<*>
    val typeHierarchyClass: Class<*>
    val xdmAtomicValueClass: Class<*>
    val xdmItemClass: Class<*>
    val xdmSequenceIteratorClass: Class<*>
    val xdmValueClass: Class<*>
    val xqueryCompilerClass: Class<*>
    val xqueryEvaluatorClass: Class<*>
    val xqueryExecutableClass: Class<*>

    init {
        val loader = URLClassLoader(arrayOf(path.toURI().toURL()))
        itemClass = loader.loadClass("net.sf.saxon.om.Item")
        itemTypeClass = loader.loadClass("net.sf.saxon.s9api.ItemType")
        processorClass = loader.loadClass("net.sf.saxon.s9api.Processor")
        qnameClass = loader.loadClass("net.sf.saxon.s9api.QName")
        typeClass = loader.loadClass("net.sf.saxon.type.Type")
        typeHierarchyClass = loader.loadClass("net.sf.saxon.type.TypeHierarchy")
        xdmAtomicValueClass = loader.loadClass("net.sf.saxon.s9api.XdmAtomicValue")
        xdmItemClass = loader.loadClass("net.sf.saxon.s9api.XdmItem")
        xdmSequenceIteratorClass = loader.loadClass("net.sf.saxon.s9api.XdmSequenceIterator")
        xdmValueClass = loader.loadClass("net.sf.saxon.s9api.XdmValue")
        xqueryCompilerClass = loader.loadClass("net.sf.saxon.s9api.XQueryCompiler")
        xqueryEvaluatorClass = loader.loadClass("net.sf.saxon.s9api.XQueryEvaluator")
        xqueryExecutableClass = loader.loadClass("net.sf.saxon.s9api.XQueryExecutable")
    }

    fun toXdmValue(value: Any?, type: String?): Any? {
        val itemtype = itemTypeClass.getField(ATOMIC_ITEM_TYPE_NAMES[type]).get(itemTypeClass)
        return xdmAtomicValueClass.getConstructor(String::class.java, itemTypeClass).newInstance(value, itemtype)
    }

    fun toQName(value: XsQNameValue): Any {
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
