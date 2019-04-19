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

class XdmAtomicValue(saxonObject: Any, saxonClass: Class<*>) : XdmItem(saxonObject, saxonClass) {
    private constructor(qname: QName, saxonClass: Class<*>) :
            this(saxonClass.getConstructor(qname.saxonClass).newInstance(qname.`object`), saxonClass)

    constructor(qname: QName) : this(qname, qname.saxonClass.classLoader.loadClass("net.sf.saxon.s9api.XdmAtomicValue"))

    private constructor(value: String, itemtype: Any, itemtypeClass: Class<*>, saxonClass: Class<*>) :
            this(saxonClass.getConstructor(String::class.java, itemtypeClass).newInstance(value, itemtype), saxonClass)

    private constructor(value: String, itemtype: String, itemtypeClass: Class<*>, saxonClass: Class<*>) : this(
        value,
        ATOMIC_ITEM_TYPE_NAMES[itemtype]?.let { itemtypeClass.getField(it).get(itemtypeClass) }
            ?: throw UnsupportedOperationException(),
        itemtypeClass,
        saxonClass
    )

    constructor(value: String, itemtype: String, classLoader: ClassLoader) : this(
        value,
        itemtype,
        classLoader.loadClass("net.sf.saxon.s9api.ItemType"),
        classLoader.loadClass("net.sf.saxon.s9api.XdmAtomicValue")
    )
}
