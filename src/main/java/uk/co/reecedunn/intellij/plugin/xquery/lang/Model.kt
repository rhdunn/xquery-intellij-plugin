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

var DIALECTS: List<Versioned> = listOf(
    // W3C Standard Dialects
    XQuery,
    FullText,
    UpdateFacility,
    Scripting,
    // Vendor Dialects
    BaseX,
    MarkLogic,
    Saxon)

fun dialectById(id: CharSequence?): Versioned? = DIALECTS.firstOrNull { dialect -> dialect.id == id }

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

    constructor(product: Product?, productVersion: Version?) {
        this.vendor = product?.implementation
        this.product = product
        this.productVersion = productVersion
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
