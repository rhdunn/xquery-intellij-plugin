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

/**
 * XQuery and XPath Full Text
 */
object FullText : Versioned {
    // NOTE: The only changes from 1.0 to 3.0 are to support the changes in grammar from XQuery 1.0 to 3.0.
    val REC_1_0_20110317 = Specification(
        "1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/", this
    )

    val REC_3_0_20151124 = Specification(
        "3.0-20151124", 3.0, 20151124, "3.0", "https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/", this
    )

    override val id get(): String = "xpath-full-text"

    override val name get(): String = "XQuery and XPath Full Text"

    override val versions
        get(): List<Version> = listOf(
            REC_1_0_20110317,
            REC_3_0_20151124
        )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this || dialect === XQuerySpec
}

/**
 * XQuery and XPath Functions and Operators
 */
object FunctionsAndOperators : Versioned {
    val REC_1_0_20070123 = Specification(
        "1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xpath-functions-20070123/", this
    )

    val REC_1_0_20101214 = Specification(
        "1.0-20101214", 1.0, 20101214, "1.0", "https://www.w3.org/TR/2010/REC-xpath-functions-20101214/", this
    )

    val REC_3_0_20140408 = Specification(
        "3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/", this
    )

    val REC_3_1_20170321 = Specification(
        "3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/", this
    )

    override val id get(): String = "xpath-functions"

    override val name get(): String = "XQuery and XPath Functions and Operators"

    override val versions
        get(): List<Version> = listOf(
            REC_1_0_20070123,
            REC_1_0_20101214,
            REC_3_0_20140408,
            REC_3_1_20170321
        )
}

/**
 * XQuery Scripting Extension
 */
object Scripting : Versioned {
    val NOTE_1_0_20140918 = Specification(
        "1.0-20140918", 1.0, 20140918, "1.0", "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/", this
    )

    override val id get(): String = "xquery-sx"

    override val name get(): String = "XQuery Scripting Extension"

    override val versions
        get(): List<Version> = listOf(
            NOTE_1_0_20140918
        )

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === UpdateFacility || dialect === XQuerySpec
}

/**
 * XQuery Update Facility
 */
object UpdateFacility : Versioned {
    val REC_1_0_20110317 = Specification(
        "1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/", this
    )

    val NOTE_3_0_20170124 = Specification(
        "3.0-20170124", 3.0, 20170124, "3.0", "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/", this
    )

    override val id get(): String = "xquery-update"

    override val name get(): String = "XQuery Update Facility"

    override val versions
        get(): List<Version> = listOf(
            REC_1_0_20110317,
            NOTE_3_0_20170124
        )

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuerySpec
}

/**
 * XQuery Formal Semantics
 */
object FormalSemantics : Versioned {
    val REC_1_0_20070123 = Specification(
        "1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xquery-semantics-20070123/", this
    )

    val REC_1_0_20101214 = Specification(
        "1.0-20101214", 1.0, 20101214, "1.0", "http://www.w3.org/TR/2010/REC-xquery-semantics-20101214/", this
    )

    override val id get(): String = "xquery-semantics"

    override val name get(): String = "XQuery Formal Semantics"

    override val versions
        get(): List<Version> = listOf(
            REC_1_0_20070123,
            REC_1_0_20101214
        )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this
}
