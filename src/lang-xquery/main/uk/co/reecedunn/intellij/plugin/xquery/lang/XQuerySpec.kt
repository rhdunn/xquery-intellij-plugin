/*
 * Copyright (C) 2021 Reece H. Dunn
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

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.impl.W3CSpecification
import javax.swing.Icon

@Suppress("MemberVisibilityCanBePrivate")
object XQuerySpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XQuery: An XML Query Language"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xquery"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Versions

    val WD_1_0_20030502: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20030502",
        "1.0 (Working Draft 02 May 2003)",
        "https://www.w3.org/TR/2003/WD-xquery-20030502/"
    )

    val REC_1_0_20070123: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20070123",
        "1.0 (First Edition)",
        "https://www.w3.org/TR/2007/REC-xquery-20070123/"
    )

    val REC_1_0_20101214: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20101214",
        "1.0 (Second Edition)",
        "https://www.w3.org/TR/2010/REC-xquery-20101214/"
    )

    val REC_3_0_20140408: XpmSpecificationVersion = W3CSpecification(
        this,
        "3.0-20140408",
        "3.0",
        "https://www.w3.org/TR/2014/REC-xquery-30-20140408/"
    )

    val CR_3_1_20151217: XpmSpecificationVersion = W3CSpecification(
        this,
        "3.1-20151217",
        "3.1 (Candidate Recommendation 17 December 2015)",
        "https://www.w3.org/TR/2015/CR-xquery-31-20151217/"
    )

    val REC_3_1_20170321: XpmSpecificationVersion = W3CSpecification(
        this,
        "3.1-20170321",
        "3.1",
        "https://www.w3.org/TR/2017/REC-xquery-31-20170321/"
    )

    val ED_4_0_20210113: XpmSpecificationVersion = W3CSpecification(
        this,
        "4.0-20210113",
        "4.0 (Editor's Draft 13 January 2021)",
        "https://qt4cg.org/branch/master/xquery-40/xquery-40.html"
    )

    val versions: List<XpmSpecificationVersion> = listOf(
        WD_1_0_20030502,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        CR_3_1_20151217,
        REC_3_1_20170321,
        ED_4_0_20210113
    )

    // endregion
}
