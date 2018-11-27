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

object XmlSchemaSpec : Versioned {
    val REC_1_0_20041028 = Specification(
        "1.0-20041028", 1.0, 20041028, "1.0", "https://www.w3.org/TR/2004/REC-xmlschema-2-20041028/", this
    )

    val REC_1_1_20120405 = Specification(
        "1.1-20120405", 1.1, 20120405, "1.1", "https://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/", this
    )

    override val id: String = "xmlschema"

    override val name: String = "XML Schema Definition"

    override val versions: List<Version> = listOf(
        REC_1_0_20041028,
        REC_1_1_20120405
    )
}

object XPathSpec : Versioned {
    val REC_1_0_19991116 = Specification(
        "1.0-19991116", 1.0, 19991116, "1.0", "https://www.w3.org/TR/1999/REC-xpath-19991116/", this
    )

    val REC_2_0_20070123 = Specification(
        "1.0-20070123", 2.0, 20070123, "2.0", "https://www.w3.org/TR/2007/REC-xpath20-20070123/", this
    )

    val REC_2_0_20101214 = Specification(
        "1.0-20101214", 2.0, 20101214, "2.0", "https://www.w3.org/TR/2010/REC-xpath20-20101214/", this
    )

    val REC_3_0_20140408 = Specification(
        "3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xpath-30-20140408/", this
    )

    val REC_3_1_20170321 = Specification(
        "3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xpath-31-20170321/", this
    )

    override val id: String = "xpath"

    override val name: String = "XPath"

    override val versions: List<Version> = listOf(
        REC_1_0_19991116,
        REC_2_0_20070123,
        REC_2_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321
    )
}

object XQuerySpec : Versioned {
    // region 1.0

    val WD_1_0_20030502 = DraftSpecification(
        "1.0-20030502", 1.0, 20030502, "1.0", "https://www.w3.org/TR/2003/WD-xquery-20030502/", this,
        "Working Draft 02 May 2003"
    )

    val REC_1_0_20070123 = Specification(
        "1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xquery-20070123/", this
    )

    val REC_1_0_20101214 = Specification(
        "1.0-20101214", 1.0, 20101214, "1.0", "https://www.w3.org/TR/2010/REC-xquery-20101214/", this
    )

    // endregion
    // region 3.0

    val REC_3_0_20140408 = Specification(
        "3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xquery-30-20140408/", this,
        "%annotation, count, group by, try/catch, switch, etc."
    )

    // endregion
    // region 3.1

    val CR_3_1_20151217 = Specification(
        "3.1-20151217", 3.1, 20151217, "3.1", "https://www.w3.org/TR/2015/CR-xquery-31-20151217/", this,
        "array, map, =>, ?key, {}, string interpolation"
    )

    val REC_3_1_20170321 = Specification(
        "3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xquery-31-20170321/", this,
        "array, map, =>, ?key, {}, string interpolation"
    )

    // endregion
    // region MarkLogic XQuery Versions

    val MARKLOGIC_0_9 = Specification(
        "0.9-ml", 0.9, 2007, "0.9-ml", "http://docs.marklogic.com/guide/xquery/dialects#id_65735", this
    ) // MarkLogic 3.2 (compatibility)

    val MARKLOGIC_1_0 = Specification(
        "1.0-ml", 1.0, 2008, "1.0-ml", "http://docs.marklogic.com/guide/xquery/dialects#id_63368", this
    ) // MarkLogic 4.0+

    // endregion

    override val id get(): String = "xquery"

    override val name get(): String = "XQuery"

    override val versions: List<Version> = listOf(
        MARKLOGIC_0_9,
        WD_1_0_20030502,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        CR_3_1_20151217,
        REC_3_1_20170321,
        MARKLOGIC_1_0
    )

    private val XQUERY10: List<Specification> = listOf(REC_1_0_20070123, REC_1_0_20101214, WD_1_0_20030502)
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
        versionsForXQuery(xquery).firstOrNull { spec -> product.conformsTo(version, spec) }
}

object FunctionsAndOperatorsSpec : Versioned {
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

    override val id: String = "xpath-functions"

    override val name: String = "XQuery and XPath Functions and Operators"

    override val versions: List<Version> = listOf(
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321
    )
}

object FullTextSpec : Versioned {
    // NOTE: The only changes from 1.0 to 3.0 are to support the changes in grammar from XQuery 1.0 to 3.0.
    val REC_1_0_20110317 = Specification(
        "1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xpath-full-text-10-20110317/", this
    )

    val REC_3_0_20151124 = Specification(
        "3.0-20151124", 3.0, 20151124, "3.0", "https://www.w3.org/TR/2015/REC-xpath-full-text-30-20151124/", this
    )

    override val id: String = "xpath-full-text"

    override val name: String = "XQuery and XPath Full Text"

    override val versions: List<Version> = listOf(
        REC_1_0_20110317,
        REC_3_0_20151124
    )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this || dialect === XQuerySpec
}

object UpdateFacilitySpec : Versioned {
    val REC_1_0_20110317 = Specification(
        "1.0-20110317", 1.0, 20110317, "1.0", "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/", this
    )

    val NOTE_3_0_20170124 = Specification(
        "3.0-20170124", 3.0, 20170124, "3.0", "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/", this
    )

    override val id: String = "xquery-update"

    override val name: String = "XQuery Update Facility"

    override val versions: List<Version> = listOf(
        REC_1_0_20110317,
        NOTE_3_0_20170124
    )

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === XQuerySpec
}

object ScriptingSpec : Versioned {
    val NOTE_1_0_20140918 = Specification(
        "1.0-20140918", 1.0, 20140918, "1.0", "https://www.w3.org/TR/2014/NOTE-xquery-sx-10-20140918/", this
    )

    override val id: String = "xquery-sx"

    override val name: String = "XQuery ScriptingSpec Extension"

    override val versions: List<Version> = listOf(
        NOTE_1_0_20140918
    )

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === UpdateFacilitySpec || dialect === XQuerySpec
}
