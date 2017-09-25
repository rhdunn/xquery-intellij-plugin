/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lang

import com.intellij.lang.Language

// region Data Model

interface Versioned {
    val id: String

    val name: String

    val versions: List<Version>

    fun versionsFor(product: Product, version: Version): List<Version> =
        versions.filter { spec -> product.conformsTo(version, spec) }

    fun supportsDialect(dialect: Versioned): Boolean = dialect === this
}

sealed class Version(val id: String, val value: Double, val kind: Versioned)

internal class ProductVersion(id: String, kind: Versioned) : Version(id, id.toDouble(), kind) {
    override fun toString(): String = kind.name + " " + id
}

class NamedVersion(id: String, value: Double, val name: String, kind: Versioned) : Version(id, value, kind) {
    override fun toString(): String = kind.name + " " + name
}

class Specification(id: String, value: Double, date: Int, val label: String, val href: String, kind: Versioned) : Version(id, value, kind) {
    override fun toString(): String = kind.name + " " + label
}

enum class XQueryFeature {
    MINIMAL_CONFORMANCE, // XQuery 1.0 - 3.1
    FULL_AXIS, // XQuery 1.0
    HIGHER_ORDER_FUNCTION, // XQuery 3.0 - 3.1
    MODULE, // XQuery 1.0 - 3.1
    SCHEMA_IMPORT, // XQuery 1.0; XQuery 3.0 - 3.1 ("Schema Aware")
    SCHEMA_VALIDATION, // XQuery 1.0; XQuery 3.0 - 3.1 ("Schema Aware")
    SERIALIZATION, // XQuery 1.0 - 3.1
    STATIC_TYPING, // XQuery 1.0 - 3.1
    TYPED_DATA, // XQuery 3.0 - 3.1
}

abstract class Product(val id: String, val name: String, val implementation: Implementation) {
    override fun toString(): String = implementation.name + " " + name

    abstract fun supportsFeature(version: Version, feature: XQueryFeature): Boolean

    abstract fun conformsTo(productVersion: Version, ref: Version): Boolean

    abstract fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned>
}

abstract class Implementation(override val id: String, override val name: String, val vendorUri: String): Versioned {
    abstract val products: List<Product>

    abstract fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String?
}

// endregion
// region Specification :: XML Schema Definition Language (XSD)

object XmlSchema : Versioned {
    val REC_1_0_20041028  = Specification("1.0-20041028", 1.0, 20041028, "1.0", "https://www.w3.org/TR/2004/REC-xmlschema-2-20041028/", this)
    val REC_1_1_20120405  = Specification("1.1-20120405", 1.1, 20120405, "1.1", "https://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/", this)

    override val id get(): String = "xmlschema"

    override val name get(): String = "XML Schema Definition"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20041028,
        REC_1_1_20120405)
}

// endregion
// region Specification :: XQuery

object XQuery : Language("XQuery", "application/xquery"), Versioned {
    // region 1.0

    val REC_1_0_20070123 = Specification("1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xquery-20070123/", this)
    val REC_1_0_20101214 = Specification("1.0-20101214", 1.0, 20101214, "1.0", "https://www.w3.org/TR/2010/REC-xquery-20101214/", this)

    // endregion
    // region 3.0

    val REC_3_0_20140408 = Specification("3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xquery-30-20140408/", this)

    // endregion
    // region 3.1

    val CR_3_1_20151217  = Specification("3.1-20151217", 3.1, 20151217, "3.1", "https://www.w3.org/TR/2015/CR-xquery-31-20151217/", this)
    val REC_3_1_20170321 = Specification("3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xquery-31-20170321/", this)

    // endregion
    // region MarkLogic XQuery Versions

    val MARKLOGIC_0_9 = Specification("0.9-ml", 0.9, 2007, "0.9-ml", "http://docs.marklogic.com/guide/xquery/dialects#id_65735", this) // MarkLogic 3.2 (compatibility)
    val MARKLOGIC_1_0 = Specification("1.0-ml", 1.0, 2008, "1.0-ml", "http://docs.marklogic.com/guide/xquery/dialects#id_63368", this) // MarkLogic 4.0+

    // endregion

    override fun isCaseSensitive(): Boolean = true

    override val id get(): String = "xquery"

    override val name get(): String = displayName

    override val versions: List<Version> = listOf(
        MARKLOGIC_0_9,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        CR_3_1_20151217,
        REC_3_1_20170321,
        MARKLOGIC_1_0)

    private val XQUERY10: List<Specification> = listOf(REC_1_0_20070123, REC_1_0_20101214)
    private val XQUERY30: List<Specification> = listOf(REC_3_0_20140408)
    private val XQUERY31: List<Specification> = listOf(REC_3_1_20170321, CR_3_1_20151217)
    private val XQUERY09_MARKLOGIC: List<Specification> = listOf(MARKLOGIC_0_9)
    private val XQUERY10_MARKLOGIC: List<Specification> = listOf(MARKLOGIC_1_0)
    private val XQUERY_UNKNOWN: List<Specification> = listOf()

    fun versionsForXQuery(xquery: CharSequence?): List<Specification> = when (xquery) {
        "0.9-ml" -> XQUERY09_MARKLOGIC
        "1.0-ml" -> XQUERY10_MARKLOGIC
        "1.0" -> XQUERY10
        "3.0" -> XQUERY30
        "3.1" -> XQUERY31
        else -> XQUERY_UNKNOWN
    }

    fun versionForXQuery(product: Product, version: Version, xquery: String): Specification? =
        versionsForXQuery(xquery).filter { spec -> product.conformsTo(version, spec) }.firstOrNull()
}

// endregion
// region Specification :: XQuery and XPath Full Text

object FullText : Versioned {
    // NOTE: The only changes from 1.0 to 3.0 are to support the changes in grammar from XQuery 1.0 to 3.0.
    val REC_1_0_20110317  = Specification("1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/", this)
    val REC_3_0_20151124  = Specification("3.0-20151124", 3.0, 20151124, "3.0", "https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/", this)

    override val id get(): String = "xpath-full-text"

    override val name get(): String = "XQuery and XPath Full Text"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20110317,
        REC_3_0_20151124)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuery
}

// endregion
// region Specification :: XQuery and XPath Functions and Operators

object FunctionsAndOperators : Versioned {
    val REC_1_0_20070123 = Specification("1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xpath-functions-20070123/", this)
    val REC_1_0_20101214 = Specification("1.0-20101214", 1.0, 20101214, "1.0", "https://www.w3.org/TR/2010/REC-xpath-functions-20101214/", this)
    val REC_3_0_20140408 = Specification("3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/", this)
    val REC_3_1_20170321 = Specification("3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/", this)

    override val id get(): String = "xpath-functions"

    override val name get(): String = "XQuery and XPath Functions and Operators"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321)
}

// endregion
// region Specification :: XQuery Scripting Extension

object Scripting : Versioned {
    val NOTE_1_0_20140918  = Specification("1.0-20140918", 1.0, 20140918, "1.0", "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/", this)

    override val id get(): String = "xquery-sx"

    override val name get(): String = "XQuery Scripting Extension"

    override val versions get(): List<Version> = listOf(
        NOTE_1_0_20140918)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === UpdateFacility || dialect === XQuery
}

// endregion
// region Specification :: XQuery Update Facility

object UpdateFacility : Versioned {
    val REC_1_0_20110317  = Specification("1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/", this)
    val NOTE_3_0_20170124 = Specification("3.0-20170124", 3.0, 20170124, "3.0", "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/", this)

    override val id get(): String = "xquery-update"

    override val name get(): String = "XQuery Update Facility"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20110317,
        NOTE_3_0_20170124)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuery
}

// endregion

/**
 * Supports IDs used in XQueryProjectSettings to refer to XQuery implementations.
 *
 * This is designed to preserve the interoperability with the versioning scheme
 * established in previous versions of the plugin.
 */
class VersionedProductId {
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(id: String?) {
        this.id = id
    }

    var vendor: Implementation? = null
    var product: Product? = null
    var productVersion: Version? = null

    var id: String?
        get() = when (vendor) {
            BaseX ->
                if (productVersion == null) {
                    vendor?.id
                } else {
                    "${vendor?.id}/v${productVersion?.id}"
                }
            MarkLogic ->
                if (productVersion == null) {
                    vendor?.id
                } else {
                    "${vendor?.id}/v${productVersion?.id?.replace(".0", "")}"
                }
            else ->
                if (productVersion == null) {
                    if (product == null) {
                        vendor?.id
                    } else {
                        "${vendor?.id}/${product?.id}"
                    }
                } else {
                    "${vendor?.id}/${product?.id}/v${productVersion?.id}"
                }
        }
        set(value) {
            val parts = value?.split("/") ?: listOf()

            if (parts.size >= 1) {
                when (parts[0]) {
                    "basex" -> vendor = BaseX
                    "marklogic" -> vendor = MarkLogic
                    "saxon" -> vendor = Saxon
                    "w3c" -> vendor = W3C
                    else -> vendor = null
                }
            } else {
                vendor = null
            }

            var version: Version? = null
            if (parts.size >= 2 && vendor != null) {
                if (parts[1].startsWith("v")) {
                    val versionId = parts[1].substring(1)
                    product = vendor!!.products.get(0)
                    version =
                        vendor!!.versions.find { v -> v.id == versionId } ?:
                        vendor!!.versions.find { v -> v.id == versionId + ".0" } // MarkLogic compatibility IDs (e.g. `v9`).
                } else {
                    product = vendor!!.products.find { p -> p.id == parts[1] }
                }
            } else {
                product = null
            }
            if (parts.size >= 3 && vendor != null && product != null) {
                if (parts[2].startsWith("v")) {
                    val versionId = parts[2].substring(1)
                    version = vendor!!.versions.find { v -> v.id == versionId }
                }
            }

            if (version == null && product === W3C.SPECIFICATIONS) {
                this.productVersion = W3C.FIRST_EDITION
            } else {
                this.productVersion = version
            }
        }
}
