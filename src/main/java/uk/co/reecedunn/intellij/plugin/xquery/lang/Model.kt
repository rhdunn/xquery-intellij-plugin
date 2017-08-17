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

data class Version(val id: String, val name: String, val value: Double) {
    constructor(id: String) : this(id, id, id.toDouble())
}

data class Product(val id: String, val name: String, val implementation: Implementation)

sealed class Implementation(val id: String, val name: String, val vendorUri: String) {
    abstract val versions: List<Version>

    abstract val products: List<Product>
}

object BaseX : Implementation("basex", "BaseX", "http://www.basex.org/") {
    override val versions: List<Version> = listOf(
        Version("8.4"),
        Version("8.5"),
        Version("8.6"))

    override val products: List<Product> = listOf()
}

object MarkLogic : Implementation("marklogic", "MarkLogic", "http://www.marklogic.com/") {
    override val versions: List<Version> = listOf(
        Version("6.0"),
        Version("7.0"),
        Version("8.0"),
        Version("9.0"))

    override val products: List<Product> = listOf()
}

object Saxon : Implementation("saxon", "Saxon", "http://www.saxonica.com") {
    override val versions: List<Version> = listOf(
        Version("9.5"),
        Version("9.6"),
        Version("9.7"),
        Version("9.8"))

    override val products: List<Product> = listOf(
        Product("HE", "Home Edition", this),
        Product("PE", "Professional Edition", this),
        Product("EE", "Enterprise Edition", this),
        Product("EE-T", "Enterprise Edition (Transformation package)", this),
        Product("EE-Q", "Enterprise Edition (Query package)", this),
        Product("EE-V", "Enterprise Edition (Validation package)", this))
}

object W3C : Implementation("w3c", "W3C", "https://www.w3.org/XML/Query/") {
    override val versions: List<Version> = listOf(
        Version("1ed", "First Edition", 1.0),
        Version("2ed", "Second Edition", 2.0))

    override val products: List<Product> = listOf(
        Product("rec", "Recommendation", this))
}
