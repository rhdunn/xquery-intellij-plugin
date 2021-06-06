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

@Suppress("MemberVisibilityCanBePrivate", "unused")
object FormalSemanticsSpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XQuery: An XML Query Language"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xquery-semantics"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Versions

    val REC_1_0_20070123: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20070123",
        "1.0 (First Edition)",
        "http://www.w3.org/TR/2007/REC-xquery-semantics-20070123/"
    )

    val REC_1_0_20101214: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20101214",
        "1.0 (Second Edition)",
        "http://www.w3.org/TR/2010/REC-xquery-semantics-20101214/"
    )

    val versions: List<XpmSpecificationVersion> = listOf(
        REC_1_0_20070123,
        REC_1_0_20101214
    )

    // endregion
}
