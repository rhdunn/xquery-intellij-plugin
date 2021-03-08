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
package uk.co.reecedunn.intellij.plugin.xslt.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.impl.W3CSpecification
import javax.swing.Icon

@Suppress("MemberVisibilityCanBePrivate")
object XsltSpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XSL Transformations (XSLT)"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xslt"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Versions

    val REC_1_0_19991116: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-19991116",
        "1.0",
        "http://www.w3.org/TR/1999/REC-xslt-19991116/"
    )

    val REC_2_0_20070123: XpmSpecificationVersion = W3CSpecification(
        this,
        "2.0-20070123",
        "2.0",
        "http://www.w3.org/TR/2007/REC-xslt20-20070123/"
    )

    val REC_3_0_20170608: XpmSpecificationVersion = W3CSpecification(
        this,
        "3.0-20170608",
        "3.0",
        "https://www.w3.org/TR/2017/REC-xslt-30-20170608/"
    )

    val ED_4_0_20210113: XpmSpecificationVersion = W3CSpecification(
        this,
        "4.0-20210113",
        "4.0 (Editor's Draft 13 January 2021)",
        "https://qt4cg.org/branch/master/xslt-40/Overview.html"
    )

    val versions: List<XpmSpecificationVersion> = listOf(
        REC_1_0_19991116,
        REC_2_0_20070123,
        REC_3_0_20170608,
        ED_4_0_20210113
    )

    // endregion
}
