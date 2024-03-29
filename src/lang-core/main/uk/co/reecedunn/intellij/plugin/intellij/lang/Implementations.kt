/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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

// region BaseX

private class BaseXProduct(id: String, name: String, implementation: Implementation) :
    Product(id, name, implementation) {

    override fun toString(): String = name

    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XmlSchemaSpec.REC_1_0_20041028, XmlSchemaSpec.REC_1_1_20120405 -> true
        XQuerySpec.REC_1_0_20070123 -> true // Recognises 1.0, processes as 3.0/3.1.
        XQuerySpec.REC_3_0_20140408 -> productVersion.value >= 7.7 // Full implementation.
        XQuerySpec.CR_3_1_20151217 -> productVersion.value in 8.2..8.5
        XQuerySpec.REC_3_1_20170321 -> productVersion.value >= 8.6
        FullTextSpec.REC_1_0_20110317, FullTextSpec.REC_3_0_20151124 -> true
        UpdateFacilitySpec.REC_1_0_20110317 -> true
        UpdateFacilitySpec.NOTE_3_0_20170124 -> productVersion.value >= 8.5
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY: List<Versioned> = listOf(BaseX, XQuerySpec, FullTextSpec, UpdateFacilitySpec)

    @Suppress("PropertyName")
    val FLAVOURS_UNSUPPORTED: List<Versioned> = listOf()

    override fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned> = when (version) {
        "3.0", "3.1" -> FLAVOURS_XQUERY
        else -> FLAVOURS_UNSUPPORTED
    }
}

@Suppress("MemberVisibilityCanBePrivate")
object BaseX : Implementation("basex", "BaseX", "http://www.basex.org/") {
    val VERSION_6_1: Version = ProductVersion("6.1", this, "Update Facility, Full Text, fuzzy")
    val VERSION_7_7: Version = ProductVersion("7.7", this, "XQuery 3.0 REC")
    val VERSION_7_8: Version = ProductVersion("7.8", this, "update")
    val VERSION_8_4: Version = ProductVersion("8.4", this, "non-deterministic")
    val VERSION_8_5: Version = ProductVersion("8.5", this, "update {}")
    val VERSION_8_6: Version = ProductVersion("8.6", this, "XQuery 3.1 REC")
    val VERSION_9_0: Version = ProductVersion("9.0", this)
    val VERSION_9_1: Version = ProductVersion("9.1", this, "ternary if, ?:, if without else")

    override val versions: List<Version> = listOf(
        VERSION_6_1,
        VERSION_7_7,
        VERSION_7_8,
        VERSION_8_4,
        VERSION_8_5,
        VERSION_8_6,
        VERSION_9_0,
        VERSION_9_1
    )

    val BASEX: Product = BaseXProduct("basex", "BaseX", this)

    override val products: List<Product> = listOf(BASEX)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === FullTextSpec || dialect === UpdateFacilitySpec || dialect === XQuerySpec

    override fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String? {
        return when (xqueryVersion) {
            XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
                "urn:static-context:basex"
            XQuerySpec.REC_3_0_20140408 ->
                "urn:static-context:basex"
            XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217 ->
                "urn:static-context:basex"
            else -> null
        }
    }
}

// endregion
// region eXist-db

private class EXistDBProduct(id: String, name: String, implementation: Implementation) :
    Product(id, name, implementation) {

    override fun toString(): String = name

    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = when (feature) {
        XQueryFeature.SCHEMA_IMPORT, XQueryFeature.SCHEMA_VALIDATION ->
            false
        else ->
            true
    }

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XmlSchemaSpec.REC_1_0_20041028 -> true
        XmlSchemaSpec.REC_1_1_20120405 -> productVersion.value >= 4.3
        XQuerySpec.REC_3_0_20140408 -> true
        XQuerySpec.CR_3_1_20151217 -> productVersion.value >= 3.0 && productVersion.value < 4.0
        XQuerySpec.REC_3_1_20170321 -> productVersion.value >= 4.0
        is UntilVersion -> !conformsTo(productVersion, ref.until)
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY: List<Versioned> = listOf(EXistDB, XQuerySpec)

    @Suppress("PropertyName")
    val FLAVOURS_UNSUPPORTED: List<Versioned> = listOf()

    override fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned> = when (version) {
        "3.0", "3.1" -> FLAVOURS_XQUERY
        else -> FLAVOURS_UNSUPPORTED
    }
}

object EXistDB : Implementation("exist-db", "eXist-db", "http://www.exist-db.org/") {
    val VERSION_3_0: Version = ProductVersion("3.0", this, "XQuery 3.0 REC, array, map, json")
    val VERSION_3_1: Version = ProductVersion("3.1", this, "arrow operator '=>', string constructors")
    val VERSION_3_6: Version = ProductVersion("3.6", this, "declare context item")
    val VERSION_4_0: Version = ProductVersion("4.0", this, "XQuery 3.1 REC")
    val VERSION_4_3: Version = ProductVersion("4.3", this, "XMLSchema 1.1")
    val VERSION_5_0: Version = ProductVersion("5.0", this, "exist 5")
    val VERSION_5_1: Version = ProductVersion("5.1", this, "Lucene facets and fields")
    val VERSION_5_3: Version = ProductVersion("5.3", this, "Function types in context item")

    override val versions: List<Version> = listOf(
        VERSION_3_0,
        VERSION_3_1,
        VERSION_3_6,
        VERSION_4_0,
        VERSION_4_3,
        VERSION_5_0,
        VERSION_5_1,
        VERSION_5_3
    )

    val EXIST_DB: Product = EXistDBProduct("exist-db", "eXist-db", this)

    override val products: List<Product> = listOf(EXIST_DB)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuerySpec

    override fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String? {
        if (productVersion != null && productVersion.value >= 5.0) return "urn:static-context:exist-db-5"
        return when (xqueryVersion) {
            XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
                "urn:static-context:exist-db"
            XQuerySpec.REC_3_0_20140408 ->
                "urn:static-context:exist-db"
            XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217 ->
                "urn:static-context:exist-db"
            else -> null
        }
    }
}

// endregion
// region MarkLogic

private class MarkLogicProduct(id: String, name: String, implementation: Implementation) :
    Product(id, name, implementation) {

    override fun toString(): String = name

    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XmlSchemaSpec.REC_1_0_20041028 -> true
        XmlSchemaSpec.REC_1_1_20120405 -> productVersion.value >= 9.0
        XQuerySpec.REC_1_0_20070123 -> true
        XQuerySpec.MARKLOGIC_0_9 -> true
        XQuerySpec.MARKLOGIC_1_0 -> true
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY: List<Versioned> = listOf(XQuerySpec)

    @Suppress("PropertyName")
    val FLAVOURS_MARKLOGIC: List<Versioned> = listOf(MarkLogic)

    @Suppress("PropertyName")
    val FLAVOURS_UNSUPPORTED: List<Versioned> = listOf()

    override fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned> = when (version) {
        "0.9-ml" -> FLAVOURS_MARKLOGIC
        "1.0" -> FLAVOURS_XQUERY
        "1.0-ml" -> FLAVOURS_MARKLOGIC
        else -> FLAVOURS_UNSUPPORTED
    }
}

@Suppress("MemberVisibilityCanBePrivate")
object MarkLogic : Implementation("marklogic", "MarkLogic", "http://www.marklogic.com/") {
    val VERSION_4_0: Version = ProductVersion("4.0", this) // Introduction of the 1.0-ml syntax.
    val VERSION_5_0: Version = ProductVersion("5.0", this)
    val VERSION_6_0: Version = ProductVersion("6.0", this, "property::, namespace::, binary, transactions, etc.")
    val VERSION_7_0: Version = ProductVersion("7.0", this, "schema kind tests: schema-type, etc.")
    val VERSION_8_0: Version = ProductVersion("8.0", this, "json kind tests and constructors: object-node, etc.")
    val VERSION_9_0: Version = ProductVersion("9.0", this, "arrow operator '=>'")
    val VERSION_10_0: Version = ProductVersion("10.0", this)

    override val versions: List<Version> = listOf(
        VERSION_5_0,
        VERSION_6_0,
        VERSION_7_0,
        VERSION_8_0,
        VERSION_9_0,
        VERSION_10_0
    )

    val MARKLOGIC: Product = MarkLogicProduct("marklogic", "MarkLogic", this)

    override val products: List<Product> = listOf(MARKLOGIC)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuerySpec

    override fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String? {
        if (productVersion == null) return null
        return when (xqueryVersion) {
            XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
                "urn:static-context:marklogic:1.0"
            XQuerySpec.MARKLOGIC_0_9 ->
                "urn:static-context:marklogic:0.9-ml"
            XQuerySpec.MARKLOGIC_1_0 ->
                "urn:static-context:marklogic:1.0-ml"
            else -> null
        }
    }
}

// endregion
// region Saxon (Saxonica)

private class SaxonProduct(id: String, name: String, implementation: Implementation) :
    Product(id, name, implementation) {

    // http://www.saxonica.com/products/feature-matrix-9-8.xml:
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = when (feature) {
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
        XmlSchemaSpec.REC_1_0_20041028, XmlSchemaSpec.REC_1_1_20120405 -> true
        XQuerySpec.REC_1_0_20070123 -> true
        XQuerySpec.REC_3_0_20140408 -> productVersion.value >= 9.6 || (productVersion.value >= 9.5 && this !== Saxon.HE)
        XQuerySpec.CR_3_1_20151217 -> productVersion === Saxon.VERSION_9_7
        XQuerySpec.REC_3_1_20170321 -> productVersion.value >= 9.8
        UpdateFacilitySpec.REC_1_0_20110317 -> this !== Saxon.HE && this !== Saxon.PE
        else -> ref.kind === implementation && ref.value <= productVersion.value
    }

    @Suppress("PropertyName")
    val FLAVOURS_EE: List<Versioned> = listOf(
        Saxon, // requires PE or EE -- http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions
        XQuerySpec,
        UpdateFacilitySpec // requires EE -- http://www.saxonica.com/products/feature-matrix-9-8.xml
    )

    @Suppress("PropertyName")
    val FLAVOURS_PE: List<Versioned> = listOf(Saxon, XQuerySpec)

    @Suppress("PropertyName")
    val FLAVOURS_HE: List<Versioned> = listOf(XQuerySpec)

    @Suppress("PropertyName")
    val FLAVOURS_UNSUPPORTED: List<Versioned> = listOf()

    override fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned> = when (version) {
        "1.0", "3.0", "3.1" -> when (this) {
            Saxon.EE, Saxon.EE_Q, Saxon.EE_V, Saxon.EE_T -> FLAVOURS_EE
            Saxon.PE -> FLAVOURS_PE
            Saxon.HE -> FLAVOURS_HE
            else -> FLAVOURS_UNSUPPORTED
        }
        else -> FLAVOURS_UNSUPPORTED
    }
}

object Saxon : Implementation("saxon", "Saxon", "http://www.saxonica.com") {
    val VERSION_9_4: Version = ProductVersion("9.4", this, "map")
    val VERSION_9_5: Version = ProductVersion("9.5", this, "XQuery 3.0 REC (not HE)")
    val VERSION_9_6: Version = ProductVersion("9.6", this, "XQuery 3.0 REC (HE)")
    val VERSION_9_7: Version = ProductVersion("9.7", this, "XQuery 3.1 REC")
    val VERSION_9_8: Version = ProductVersion("9.8", this, "tuple(), union(), declare type, orElse, andAlso, fn{...}")
    val VERSION_9_9: Version = ProductVersion("9.9", this, "calling java functions with '\$o?f()'")
    val VERSION_10_0: Version =
        ProductVersion("10.0", this, ".{...}, _{$1}, otherwise, type(c), element(*:test), for member")

    override val versions: List<Version> = listOf(
        VERSION_9_4,
        VERSION_9_5,
        VERSION_9_6,
        VERSION_9_7,
        VERSION_9_8,
        VERSION_9_9,
        VERSION_10_0
    )

    val HE: Product = SaxonProduct("HE", "Home Edition", this)
    val PE: Product = SaxonProduct("PE", "Professional Edition", this)
    val EE: Product = SaxonProduct("EE", "Enterprise Edition", this)
    val EE_T: Product = SaxonProduct("EE-T", "Enterprise Edition (Transformation package)", this)
    val EE_Q: Product = SaxonProduct("EE-Q", "Enterprise Edition (Query package)", this)
    val EE_V: Product = SaxonProduct("EE-V", "Enterprise Edition (Validation package)", this)

    override val products: List<Product> = listOf(HE, PE, EE, EE_T, EE_Q, EE_V)

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === UpdateFacilitySpec || dialect === XQuerySpec

    override fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String? {
        return when (xqueryVersion) {
            XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
                "urn:static-context:saxon"
            XQuerySpec.REC_3_0_20140408 ->
                "urn:static-context:saxon"
            XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217 ->
                "urn:static-context:saxon"
            else -> null
        }
    }
}

// endregion
// region W3C Specifications

private class W3CProduct(id: String, name: String, implementation: Implementation) :
    Product(id, name, implementation) {

    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true

    override fun conformsTo(productVersion: Version, ref: Version): Boolean = when (ref) {
        XmlSchemaSpec.REC_1_0_20041028, XmlSchemaSpec.REC_1_1_20120405 ->
            true
        XQuerySpec.WD_1_0_20030502, XQuerySpec.ED_4_0_20210113 ->
            productVersion === W3C.WORKING_DRAFT
        XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_3_0_20140408, XQuerySpec.REC_3_1_20170321 ->
            productVersion === W3C.FIRST_EDITION
        XQuerySpec.REC_1_0_20101214 ->
            productVersion === W3C.SECOND_EDITION
        FullTextSpec.REC_1_0_20110317, FullTextSpec.REC_3_0_20151124 ->
            productVersion === W3C.FIRST_EDITION
        UpdateFacilitySpec.REC_1_0_20110317, UpdateFacilitySpec.NOTE_3_0_20170124 ->
            productVersion === W3C.FIRST_EDITION
        ScriptingSpec.NOTE_1_0_20140918 ->
            productVersion === W3C.FIRST_EDITION
        else -> false // NOTE: 1ed/2ed conformance is done at the Specification level.
    }

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY_1_0: List<Versioned> = listOf(XQuerySpec, FullTextSpec, UpdateFacilitySpec, ScriptingSpec)

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY_3_0: List<Versioned> = listOf(XQuerySpec, FullTextSpec, UpdateFacilitySpec)

    @Suppress("PropertyName")
    val FLAVOURS_XQUERY: List<Versioned> = listOf(XQuerySpec)

    @Suppress("PropertyName")
    val FLAVOURS_UNSUPPORTED: List<Versioned> = listOf()

    override fun flavoursForXQueryVersion(productVersion: Version, version: String): List<Versioned> = when (version) {
        "1.0" -> if (productVersion === W3C.FIRST_EDITION) FLAVOURS_XQUERY_1_0 else FLAVOURS_XQUERY
        "3.0" -> if (productVersion === W3C.FIRST_EDITION) FLAVOURS_XQUERY_3_0 else FLAVOURS_XQUERY
        "3.1" -> FLAVOURS_XQUERY
        else -> FLAVOURS_UNSUPPORTED
    }
}

object W3C : Implementation("w3c", "W3C", "https://www.w3.org/XML/Query/") {
    val WORKING_DRAFT: NamedVersion = NamedVersion("wd", 0.5, "Working Draft", this)
    val FIRST_EDITION: NamedVersion = NamedVersion("1ed", 1.0, "Recommendation (First Edition)", this)
    val SECOND_EDITION: NamedVersion = NamedVersion("2ed", 2.0, "Recommendation (Second Edition)", this)

    override val versions: List<Version> = listOf(WORKING_DRAFT, FIRST_EDITION, SECOND_EDITION)

    val SPECIFICATIONS: Product = W3CProduct("spec", "Specifications", this)

    override val products: List<Product> = listOf(SPECIFICATIONS)

    override fun staticContext(product: Product?, productVersion: Version?, xqueryVersion: Specification?): String? {
        return when (xqueryVersion) {
            XQuerySpec.WD_1_0_20030502, XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
                "urn:static-context:w3"
            XQuerySpec.REC_3_0_20140408 ->
                "urn:static-context:w3"
            XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217 ->
                "urn:static-context:w3"
            XQuerySpec.ED_4_0_20210113 ->
                "urn:static-context:w3"
            else -> null
        }
    }
}

// endregion

val PRODUCTS: List<Product> = listOf(
    BaseX.BASEX,
    EXistDB.EXIST_DB,
    MarkLogic.MARKLOGIC,
    Saxon.HE,
    Saxon.PE,
    Saxon.EE,
    Saxon.EE_Q,
    Saxon.EE_T,
    Saxon.EE_V,
    W3C.SPECIFICATIONS
)

fun defaultStaticContext(xquery: Specification?): String? = when (xquery) {
    XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214 ->
        W3C.staticContext(W3C.SPECIFICATIONS, W3C.FIRST_EDITION, xquery)
    XQuerySpec.REC_3_0_20140408 ->
        W3C.staticContext(W3C.SPECIFICATIONS, W3C.FIRST_EDITION, xquery)
    XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217 ->
        W3C.staticContext(W3C.SPECIFICATIONS, W3C.FIRST_EDITION, xquery)
    XQuerySpec.ED_4_0_20210113 ->
        W3C.staticContext(W3C.SPECIFICATIONS, W3C.WORKING_DRAFT, xquery)
    XQuerySpec.MARKLOGIC_1_0, XQuerySpec.MARKLOGIC_0_9 ->
        MarkLogic.staticContext(MarkLogic.MARKLOGIC, MarkLogic.VERSION_9_0, xquery)
    else -> null
}

fun defaultProductVersion(product: Product): Version = when (product) {
    BaseX.BASEX -> BaseX.VERSION_9_1
    MarkLogic.MARKLOGIC -> MarkLogic.VERSION_9_0
    Saxon.HE, Saxon.PE, Saxon.EE, Saxon.EE_Q, Saxon.EE_T, Saxon.EE_V -> Saxon.VERSION_9_9
    W3C.SPECIFICATIONS -> W3C.FIRST_EDITION
    else -> throw RuntimeException("Unknown product: $product")
}
