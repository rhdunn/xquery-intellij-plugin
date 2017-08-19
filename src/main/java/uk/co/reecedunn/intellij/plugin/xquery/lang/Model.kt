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
    val name: String

    val versions: List<Version>

    fun versionsFor(product: Product, version: Version): List<Version> =
        versions.filter { spec -> product.conformsTo(version, spec) }
}

sealed class Version(val id: String, val value: Double, val kind: Versioned)

private class ProductVersion(id: String, kind: Versioned) : Version(id, id.toDouble(), kind)

class NamedVersion(id: String, value: Double, val name: String, kind: Versioned) : Version(id, value, kind)

class Specification(id: String, value: Double, date: Int, val label: String, val href: String, kind: Versioned) : Version(id, value, kind)

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

sealed class Product(val id: String, val name: String, val implementation: Implementation) {
    abstract fun supportsFeature(version: Version, feature: XQueryFeature): Boolean

    abstract fun conformsTo(productVersion: Version, ref: Version): Boolean
}

sealed class Implementation(val id: String, override val name: String, val vendorUri: String): Versioned {
    abstract val products: List<Product>
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

    override val name get(): String = displayName

    override val versions: List<Version> = listOf(
        MARKLOGIC_0_9,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        CR_3_1_20151217,
        REC_3_1_20170321,
        MARKLOGIC_1_0)
}

// endregion
// region Specification :: XQuery and XPath Full Text

object FullText : Versioned {
    // NOTE: The only changes from 1.0 to 3.0 are to support the changes in grammar from XQuery 1.0 to 3.0.
    val REC_1_0_20110317  = Specification("1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/", this)
    val REC_3_0_20151124  = Specification("3.0-20151124", 3.0, 20151124, "3.0", "https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/", this)

    override val name get(): String = "XQuery and XPath Full Text"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20110317,
        REC_3_0_20151124)
}

// endregion
// region Specification :: XQuery Update Facility

object UpdateFacility : Versioned {
    val REC_1_0_20110317  = Specification("1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/", this)
    val NOTE_3_0_20170124 = Specification("3.0-20170124", 3.0, 20170124, "3.0", "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/", this)

    override val name get(): String = "XQuery Update Facility"

    override val versions get(): List<Version> = listOf(
        REC_1_0_20110317,
        NOTE_3_0_20170124)
}

// endregion
// region Implementation :: BaseX

private class BaseXProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XQuery.REC_3_0_20140408 -> productVersion.value >= 7.7 // Full implementation.
        XQuery.CR_3_1_20151217  -> productVersion.value <= 8.5
        XQuery.REC_3_1_20170321 -> productVersion.value >= 8.6
        FullText.REC_1_0_20110317 -> true
        FullText.REC_3_0_20151124 -> true
        UpdateFacility.REC_1_0_20110317 -> true
        UpdateFacility.NOTE_3_0_20170124 -> productVersion.value >= 8.5
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }
}

object BaseX : Implementation("basex", "BaseX", "http://www.basex.org/") {
    val VERSION_8_4: Version = ProductVersion("8.4", this)
    val VERSION_8_5: Version = ProductVersion("8.5", this)
    val VERSION_8_6: Version = ProductVersion("8.6", this)

    override val versions: List<Version> = listOf(
        VERSION_8_4,
        VERSION_8_5,
        VERSION_8_6)

    val BASEX: Product = BaseXProduct("basex", "BaseX", this)

    override val products: List<Product> = listOf(BASEX)
}

// endregion
// region Implementation :: MarkLogic

private class MarkLogicProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XQuery.REC_1_0_20070123 -> true
        XQuery.MARKLOGIC_0_9 -> true
        XQuery.MARKLOGIC_1_0 -> true
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }
}

object MarkLogic : Implementation("marklogic", "MarkLogic", "http://www.marklogic.com/") {
    val VERSION_6_0: Version = ProductVersion("6.0", this)
    val VERSION_7_0: Version = ProductVersion("7.0", this)
    val VERSION_8_0: Version = ProductVersion("8.0", this)
    val VERSION_9_0: Version = ProductVersion("9.0", this)

    override val versions: List<Version> = listOf(
        VERSION_6_0,
        VERSION_7_0,
        VERSION_8_0,
        VERSION_9_0)

    val MARKLOGIC: Product = MarkLogicProduct("marklogic", "MarkLogic", this)

    override val products: List<Product> = listOf(MARKLOGIC)
}

// endregion
// region Implementation :: Saxon (Saxonica)

private class SaxonProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = when (feature) {
        // http://www.saxonica.com/products/feature-matrix-9-8.xml:
        XQueryFeature.MINIMAL_CONFORMANCE, XQueryFeature.FULL_AXIS, XQueryFeature.MODULE, XQueryFeature.SERIALIZATION ->
            true
        XQueryFeature.HIGHER_ORDER_FUNCTION ->
            this !== Saxon.HE
        XQueryFeature.SCHEMA_IMPORT, XQueryFeature.SCHEMA_VALIDATION, XQueryFeature.TYPED_DATA ->
            this === Saxon.EE || this == Saxon.EE_Q
        XQueryFeature.STATIC_TYPING ->
            false
    }

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XQuery.REC_1_0_20070123 -> true
        XQuery.REC_3_0_20140408 -> productVersion.value >= 9.6 || (productVersion.value >= 9.5 && this !== Saxon.HE)
        XQuery.CR_3_1_20151217  -> productVersion === Saxon.VERSION_9_7
        XQuery.REC_3_1_20170321 -> productVersion.value >= 9.8
        UpdateFacility.REC_1_0_20110317 -> this !== Saxon.HE && this !== Saxon.PE
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }
}

object Saxon : Implementation("saxon", "Saxon", "http://www.saxonica.com") {
    val VERSION_9_5: Version = ProductVersion("9.5", this)
    val VERSION_9_6: Version = ProductVersion("9.6", this)
    val VERSION_9_7: Version = ProductVersion("9.7", this)
    val VERSION_9_8: Version = ProductVersion("9.8", this)

    override val versions: List<Version> = listOf(
        VERSION_9_5,
        VERSION_9_6,
        VERSION_9_7,
        VERSION_9_8)

    val HE: Product = SaxonProduct("HE", "Home Edition", this)
    val PE: Product = SaxonProduct("PE", "Professional Edition", this)
    val EE: Product = SaxonProduct("EE", "Enterprise Edition", this)
    val EE_T: Product = SaxonProduct("EE-T", "Enterprise Edition (Transformation package)", this)
    val EE_Q: Product = SaxonProduct("EE-Q", "Enterprise Edition (Query package)", this)
    val EE_V: Product = SaxonProduct("EE-V", "Enterprise Edition (Validation package)", this)

    override val products: List<Product> = listOf(HE, PE, EE, EE_T, EE_Q, EE_V)
}

// endregion
// region Implementation :: W3C Specifications

private class W3CProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XQuery.REC_1_0_20070123 -> productVersion === W3C.FIRST_EDITION
        XQuery.REC_1_0_20101214 -> productVersion === W3C.SECOND_EDITION
        XQuery.REC_3_0_20140408 -> productVersion === W3C.FIRST_EDITION
        XQuery.REC_3_1_20170321 -> productVersion === W3C.FIRST_EDITION
        FullText.REC_1_0_20110317 -> productVersion === W3C.FIRST_EDITION
        FullText.REC_3_0_20151124 -> productVersion === W3C.FIRST_EDITION
        UpdateFacility.REC_1_0_20110317 -> productVersion === W3C.FIRST_EDITION
        else -> false // NOTE: 1ed/2ed conformance is done at the Specification level.
    }
}

object W3C : Implementation("w3c", "W3C", "https://www.w3.org/XML/Query/") {
    val FIRST_EDITION: NamedVersion = NamedVersion("1ed", 1.0, "First Edition", this)
    val SECOND_EDITION: NamedVersion = NamedVersion("2ed", 2.0, "Second Edition", this)

    override val versions: List<Version> = listOf(FIRST_EDITION, SECOND_EDITION)

    val REC: Product = W3CProduct("rec", "Recommendation", this)

    override val products: List<Product> = listOf(REC)
}

// endregion