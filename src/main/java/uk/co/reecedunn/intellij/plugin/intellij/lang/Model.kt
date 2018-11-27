/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

fun until(until: Version): Version = UntilVersion(until)

var DIALECTS: List<Versioned> = listOf(
    // W3C Standard Dialects
    XQuerySpec,
    FullText,
    UpdateFacility,
    Scripting,
    // Vendor Dialects
    BaseX,
    MarkLogic,
    Saxon
)

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
            BaseX, EXistDB ->
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

            vendor = if (parts.isNotEmpty()) {
                when (parts[0]) {
                    "basex" -> BaseX
                    "exist-db" -> EXistDB
                    "marklogic" -> MarkLogic
                    "saxon" -> Saxon
                    "w3c" -> W3C
                    else -> null
                }
            } else {
                null
            }

            var version: Version? = null
            if (parts.size >= 2 && vendor != null) {
                if (parts[1].startsWith("v")) {
                    val versionId = parts[1].substring(1)
                    product = vendor!!.products[0]
                    version =
                            vendor!!.versions.find { v -> v.id == versionId } ?:
                            vendor!!.versions.find { v -> v.id == "$versionId.0" } // MarkLogic compatibility IDs (e.g. `v9`).
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
