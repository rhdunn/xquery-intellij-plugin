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
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.impl.W3CSpecification
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmIcons
import javax.swing.Icon

@Suppress("MemberVisibilityCanBePrivate")
object UpdateFacilitySpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XQuery Update Facility"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xquery-update"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Versions

    val REC_1_0_20110317: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20110317",
        "1.0",
        "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/"
    )

    val NOTE_3_0_20170124: XpmSpecificationVersion = W3CSpecification(
        this,
        "3.0-20170124",
        "3.0 (Working Group Note 24 January 2017)",
        "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/"
    )

    val versions: List<XpmSpecificationVersion> = listOf(
        REC_1_0_20110317,
        NOTE_3_0_20170124
    )

    // endregion
}
