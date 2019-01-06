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
    val destinationClass: Class<*>
    val rawDestinationClass: Class<*>

    val itemClass: Class<*>
    val itemTypeClass: Class<*>
    val processorClass: Class<*>
    val qnameClass: Class<*>
    val saxonApiExceptionClass: Class<*>
    val structuredQNameClass: Class<*>
    val typeClass: Class<*>
    val typeHierarchyClass: Class<*>
    val xdmAtomicValueClass: Class<*>
    val xdmEmptySequenceClass: Class<*>
    val xdmItemClass: Class<*>
    val xdmSequenceIteratorClass: Class<*>
    val xdmValueClass: Class<*>
    val xpathExceptionClass: Class<*>

    val xpathCompilerClass: Class<*>
    val xpathExecutableClass: Class<*>
    val xpathSelectorClass: Class<*>

    val xqueryCompilerClass: Class<*>
    val xqueryEvaluatorClass: Class<*>
    val xqueryExecutableClass: Class<*>

    val xsltCompilerClass: Class<*>
    val xsltExecutableClass: Class<*>
    val xsltTransformerClass: Class<*>

    init {
        val loader = URLClassLoader(arrayOf(path.toURI().toURL()))

        destinationClass = loader.loadClass("net.sf.saxon.s9api.Destination")
        rawDestinationClass = loader.loadClass("net.sf.saxon.s9api.RawDestination")

        itemClass = loader.loadClass("net.sf.saxon.om.Item")
        itemTypeClass = loader.loadClass("net.sf.saxon.s9api.ItemType")
        processorClass = loader.loadClass("net.sf.saxon.s9api.Processor")
        qnameClass = loader.loadClass("net.sf.saxon.s9api.QName")
        saxonApiExceptionClass = loader.loadClass("net.sf.saxon.s9api.SaxonApiException")
        structuredQNameClass = loader.loadClass("net.sf.saxon.om.StructuredQName")
        typeClass = loader.loadClass("net.sf.saxon.type.Type")
        typeHierarchyClass = loader.loadClass("net.sf.saxon.type.TypeHierarchy")
        xdmAtomicValueClass = loader.loadClass("net.sf.saxon.s9api.XdmAtomicValue")
        xdmEmptySequenceClass = loader.loadClass("net.sf.saxon.s9api.XdmEmptySequence")
        xdmItemClass = loader.loadClass("net.sf.saxon.s9api.XdmItem")
        xdmSequenceIteratorClass = loader.loadClass("net.sf.saxon.s9api.XdmSequenceIterator")
        xdmValueClass = loader.loadClass("net.sf.saxon.s9api.XdmValue")
        xpathExceptionClass = loader.loadClass("net.sf.saxon.trans.XPathException")

        xpathCompilerClass = loader.loadClass("net.sf.saxon.s9api.XPathCompiler")
        xpathExecutableClass = loader.loadClass("net.sf.saxon.s9api.XPathExecutable")
        xpathSelectorClass = loader.loadClass("net.sf.saxon.s9api.XPathSelector")

        xqueryCompilerClass = loader.loadClass("net.sf.saxon.s9api.XQueryCompiler")
        xqueryEvaluatorClass = loader.loadClass("net.sf.saxon.s9api.XQueryEvaluator")
        xqueryExecutableClass = loader.loadClass("net.sf.saxon.s9api.XQueryExecutable")

        xsltCompilerClass = loader.loadClass("net.sf.saxon.s9api.XsltCompiler")
        xsltExecutableClass = loader.loadClass("net.sf.saxon.s9api.XsltExecutable")
        xsltTransformerClass = loader.loadClass("net.sf.saxon.s9api.XsltTransformer")
    }

    fun tryXdmValue(value: Any?, type: String?): Any? {
        return try {
            toXdmValue(value, type)
        } catch (e: Exception) {
            null
        }
    }

    fun toXdmValue(value: Any?, type: String?): Any? {
        return when (type) {
            null, "empty-sequence()" -> xdmEmptySequenceClass.getMethod("getInstance").invoke(null)
            "xs:QName" -> {
                // The string constructor throws "Requested type is namespace-sensitive"
                xdmAtomicValueClass.getConstructor(qnameClass).newInstance(toQName(value as String))
            }
            "xs:numeric" -> {
                tryXdmValue(value, "xs:double") ?: tryXdmValue(value, "xs:integer") ?: toXdmValue(value, "xs:decimal")
            }
            else -> {
                ATOMIC_ITEM_TYPE_NAMES[type]?.let {
                    val itemtype = itemTypeClass.getField(it).get(itemTypeClass)
                    xdmAtomicValueClass.getConstructor(String::class.java, itemTypeClass).newInstance(value, itemtype)
                } ?: throw UnsupportedOperationException()
            }
        }
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

    fun <T> check(f: () -> T): T {
        return try {
            f()
        } catch (e: InvocationTargetException) {
            if (saxonApiExceptionClass.isInstance(e.targetException)) {
                throw SaxonQueryError(e.targetException, this)
            } else if (e.targetException is SaxonTransformerQueryError) {
                throw e.targetException
            } else {
                throw e
            }
        }
    }
}
