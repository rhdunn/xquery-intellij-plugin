/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.saxon.resources.SaxonIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import javax.swing.Icon

object SaxonPE : ItemPresentation, XpmProductType {
    // region ItemPresentation

    override fun getPresentableText(): String = "Saxon Professional Edition"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = SaxonIcons.Product

    // endregion
    // region XpmProductType

    override val id: String = "saxon/PE"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Language Versions

    val VERSION_9_4: XpmProductVersion = SaxonVersion(this, 9, 4, "map")
    val VERSION_9_5: XpmProductVersion = SaxonVersion(this, 9, 5, "XQuery 3.0 REC") // 9.6 for HE
    val VERSION_9_7: XpmProductVersion = SaxonVersion(this, 9, 7, "XQuery 3.1 REC")
    val VERSION_9_8: XpmProductVersion = SaxonVersion(this, 9, 8, "tuple(), declare type, orElse, andAlso, fn{...}")
    val VERSION_9_9: XpmProductVersion = SaxonVersion(this, 9, 9, "calling java functions with '\$o?f()'")
    val VERSION_10_0: XpmProductVersion = SaxonVersion(
        this, 10, 0, "union(), .{...}, _{$1}, otherwise, type(c), element(*:test), for member"
    )

    @Suppress("unused")
    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_9_4,
        VERSION_9_5,
        VERSION_9_7,
        VERSION_9_8,
        VERSION_9_9,
        VERSION_10_0
    )

    // endregion
}
