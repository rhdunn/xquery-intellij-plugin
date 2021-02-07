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

object XmlSchemaSpec : Versioned {
    val REC_1_0_20041028: Specification = Specification("1.0-20041028", 1.0, "1.0", this)
    val REC_1_1_20120405: Specification = Specification("1.1-20120405", 1.1, "1.1", this)

    override val id: String = "xmlschema"

    override val name: String = "XML Schema Definition"

    override val versions: List<Version> = listOf(
        REC_1_0_20041028,
        REC_1_1_20120405
    )
}

@Suppress("MemberVisibilityCanBePrivate")
object XPathSpec : Versioned {
    val REC_1_0_19991116: Specification =
        Specification("1.0-19991116", 1.0, "1.0", this)

    val WD_2_0_20030502: Specification =
        DraftSpecification("2.0-20030502", 2.0, "2.0", this, "Working Draft 02 May 2003")

    val REC_2_0_20070123: Specification =
        Specification("1.0-20070123", 2.0, "2.0", this)

    val REC_2_0_20101214: Specification =
        Specification("1.0-20101214", 2.0, "2.0", this)

    val REC_3_0_20140408: Specification =
        Specification("3.0-20140408", 3.0, "3.0", this)

    val CR_3_1_20151217: Specification =
        DraftSpecification("3.1-20151217", 3.1, "3.1", this, "Candidate Recommendation 17 December 2015")

    val REC_3_1_20170321: Specification =
        Specification("3.1-20170321", 3.1, "3.1", this)

    val ED_4_0_20210113: Specification =
        DraftSpecification("4.0-20210113", 4.0, "4.0", this, "Editor's Draft 13 January 2021")

    override val id: String = "xpath"

    override val name: String = "XPath"

    override val versions: List<Version> = listOf(
        REC_1_0_19991116,
        WD_2_0_20030502,
        REC_2_0_20070123,
        REC_2_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321,
        ED_4_0_20210113
    )
}

@Suppress("MemberVisibilityCanBePrivate")
object XQuerySpec : Versioned {
    val WD_1_0_20030502: Specification =
        DraftSpecification("1.0-20030502", 1.0, "1.0", this, "Working Draft 02 May 2003")

    val REC_1_0_20070123: Specification =
        Specification("1.0-20070123", 1.0, "1.0", this)

    val REC_1_0_20101214: Specification =
        Specification("1.0-20101214", 1.0, "1.0", this)

    val REC_3_0_20140408: Specification = Specification(
        "3.0-20140408", 3.0, "3.0", this,
        "%annotation, count, group by, try/catch, switch, etc."
    )

    val CR_3_1_20151217: Specification = DraftSpecification(
        "3.1-20151217", 3.1, "3.1", this,
        "Candidate Recommendation 17 December 2015",
        "array, map, =>, ?key, {}, string interpolation"
    )

    val REC_3_1_20170321: Specification = Specification(
        "3.1-20170321", 3.1, "3.1", this,
        "array, map, =>, ?key, {}, string interpolation"
    )

    val ED_4_0_20210113: Specification = DraftSpecification(
        "4.0-20210113", 4.0, "4.0", this,
        "Editor's Draft 13 January 2021",
        "?? !!, ->{}, -> f(), union(), record(), enum(), etc."
    )

    val MARKLOGIC_0_9: Specification =
        Specification("0.9-ml", 0.9, "0.9-ml", this) // MarkLogic 3.2 (compatibility) [2007]

    val MARKLOGIC_1_0: Specification =
        Specification("1.0-ml", 1.0, "1.0-ml", this) // MarkLogic 4.0+ [2008]

    override val id: String = "xquery"

    override val name: String = "XQuery"

    override val versions: List<Version> = listOf(
        MARKLOGIC_0_9,
        WD_1_0_20030502,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        CR_3_1_20151217,
        REC_3_1_20170321,
        ED_4_0_20210113,
        MARKLOGIC_1_0
    )

    private val XQUERY10: List<Specification> = listOf(REC_1_0_20070123, REC_1_0_20101214, WD_1_0_20030502)
    private val XQUERY30: List<Specification> = listOf(REC_3_0_20140408)
    private val XQUERY31: List<Specification> = listOf(REC_3_1_20170321, CR_3_1_20151217)
    private val XQUERY40: List<Specification> = listOf(ED_4_0_20210113)
    private val XQUERY09_MARKLOGIC: List<Specification> = listOf(MARKLOGIC_0_9)
    private val XQUERY10_MARKLOGIC: List<Specification> = listOf(MARKLOGIC_1_0)
    private val XQUERY_UNKNOWN: List<Specification> = listOf()

    fun versionsForXQuery(xquery: CharSequence?): List<Specification> = when (xquery) {
        "0.9-ml" -> XQUERY09_MARKLOGIC
        "1.0-ml" -> XQUERY10_MARKLOGIC
        "1.0" -> XQUERY10
        "3.0" -> XQUERY30
        "3.1" -> XQUERY31
        "4.0" -> XQUERY40
        else -> XQUERY_UNKNOWN
    }

    fun versionForXQuery(product: Product, version: Version, xquery: String): Specification? =
        versionsForXQuery(xquery).firstOrNull { spec -> product.conformsTo(version, spec) }
}

@Suppress("MemberVisibilityCanBePrivate")
object XsltSpec : Versioned {
    val REC_1_0_19991116: Specification = Specification("1.0-19991116", 1.0, "1.0", this)

    val REC_2_0_20070123: Specification = Specification("2.0-20070123", 2.0, "2.0", this)

    val REC_3_0_20170608: Specification = Specification("3.0-20170608", 3.0, "3.0", this)

    val ED_4_0_20210113: Specification =
        DraftSpecification("4.0-20210113", 4.0, "4.0", this, "Editor's Draft 13 January 2021")

    override val id: String = "xslt"

    override val name: String = "XSL Transformations (XSLT)"

    override val versions: List<Version> = listOf(
        REC_1_0_19991116,
        REC_2_0_20070123,
        REC_3_0_20170608,
        ED_4_0_20210113
    )
}

@Suppress("MemberVisibilityCanBePrivate")
object FormalSemanticsSpec : Versioned {
    val REC_1_0_20070123: Specification = Specification("1.0-20070123", 1.0, "1.0", this)

    val REC_1_0_20101214: Specification = Specification("1.0-20101214", 1.0, "1.0", this)

    override val id: String = "xquery-semantics"

    override val name: String = "XQuery Formal Semantics"

    override val versions: List<Version> = listOf(
        REC_1_0_20070123,
        REC_1_0_20101214
    )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this
}

// NOTE: The only changes from 1.0 to 3.0 are to support the changes in grammar from XQuery 1.0 to 3.0.
object FullTextSpec : Versioned {
    val REC_1_0_20110317: Specification = Specification("1.0-20110317", 1.0, "1.0", this)

    val REC_3_0_20151124: Specification = Specification("3.0-20151124", 3.0, "3.0", this)

    override val id: String = "xpath-full-text"

    override val name: String = "XQuery and XPath Full Text"

    override val versions: List<Version> = listOf(
        REC_1_0_20110317,
        REC_3_0_20151124
    )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this || dialect === XQuerySpec
}

object UpdateFacilitySpec : Versioned {
    val REC_1_0_20110317: Specification = Specification("1.0-20110317", 1.0, "1.0", this)

    val NOTE_3_0_20170124: Specification = Specification("3.0-20170124", 3.0, "3.0", this)

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
    val NOTE_1_0_20140918: Specification = Specification("1.0-20140918", 1.0, "1.0", this)

    override val id: String = "xquery-sx"

    override val name: String = "XQuery Scripting Extension"

    override val versions: List<Version> = listOf(
        NOTE_1_0_20140918
    )

    override fun supportsDialect(dialect: Versioned): Boolean =
        dialect === this || dialect === UpdateFacilitySpec || dialect === XQuerySpec
}
