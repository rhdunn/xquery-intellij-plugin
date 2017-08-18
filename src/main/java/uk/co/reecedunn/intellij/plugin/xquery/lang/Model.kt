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

// region Data Model

sealed class Version(val id: String, val value: Double)

class NamedVersion(id: String, value: Double, val name: String) : Version(id, value)

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
}

private class ProductVersion(id: String) : Version(id, id.toDouble())

sealed class Implementation(val id: String, val name: String, val vendorUri: String) {
    abstract val versions: List<Version>

    abstract val products: List<Product>
}

// endregion
// region Implementation :: BaseX

private class BaseXProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true
}

object BaseX : Implementation("basex", "BaseX", "http://www.basex.org/") {
    override val versions: List<Version> = listOf(
        ProductVersion("8.4"),
        ProductVersion("8.5"),
        ProductVersion("8.6"))

    override val products: List<Product> = listOf(
        BaseXProduct("basex", "BaseX", this))
}

// endregion
// region Implementation :: MarkLogic

private class MarkLogicProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true
}

object MarkLogic : Implementation("marklogic", "MarkLogic", "http://www.marklogic.com/") {
    override val versions: List<Version> = listOf(
        ProductVersion("6.0"),
        ProductVersion("7.0"),
        ProductVersion("8.0"),
        ProductVersion("9.0"))

    override val products: List<Product> = listOf(
        MarkLogicProduct("marklogic", "MarkLogic", this))
}

// endregion
// region Implementation :: Saxon (Saxonica)

private class SaxonProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = when (feature) {
        // http://www.saxonica.com/products/feature-matrix-9-8.xml:
        XQueryFeature.MINIMAL_CONFORMANCE, XQueryFeature.FULL_AXIS, XQueryFeature.MODULE, XQueryFeature.SERIALIZATION ->
            true
        XQueryFeature.HIGHER_ORDER_FUNCTION ->
            id != "HE"
        XQueryFeature.SCHEMA_IMPORT, XQueryFeature.SCHEMA_VALIDATION, XQueryFeature.TYPED_DATA ->
            id == "EE" || id == "EE-Q"
        XQueryFeature.STATIC_TYPING ->
            false
    }
}

object Saxon : Implementation("saxon", "Saxon", "http://www.saxonica.com") {
    override val versions: List<Version> = listOf(
        ProductVersion("9.5"),
        ProductVersion("9.6"),
        ProductVersion("9.7"),
        ProductVersion("9.8"))

    override val products: List<Product> = listOf(
        SaxonProduct("HE", "Home Edition", this),
        SaxonProduct("PE", "Professional Edition", this),
        SaxonProduct("EE", "Enterprise Edition", this),
        SaxonProduct("EE-T", "Enterprise Edition (Transformation package)", this),
        SaxonProduct("EE-Q", "Enterprise Edition (Query package)", this),
        SaxonProduct("EE-V", "Enterprise Edition (Validation package)", this))
}

// endregion
// region Implementation :: W3C Specifications

private class W3CProduct(id: String, name: String, implementation: Implementation) : Product(id, name, implementation) {
    override fun supportsFeature(version: Version, feature: XQueryFeature): Boolean = true
}

object W3C : Implementation("w3c", "W3C", "https://www.w3.org/XML/Query/") {
    override val versions: List<Version> = listOf(
        NamedVersion("1ed", 1.0, "First Edition"),
        NamedVersion("2ed", 2.0, "Second Edition"))

    override val products: List<Product> = listOf(
        W3CProduct("rec", "Recommendation", this))
}

// endregion