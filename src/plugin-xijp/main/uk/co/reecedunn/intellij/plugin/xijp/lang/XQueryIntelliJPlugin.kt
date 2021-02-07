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
package uk.co.reecedunn.intellij.plugin.xijp.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import javax.swing.Icon

object XQueryIntelliJPlugin : ItemPresentation, XpmProductType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XQuery IntelliJ Plugin"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon? = null

    // endregion
    // region XpmProductType

    override val id: String = "xijp"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Language Versions

    val VERSION_1_3: XpmProductVersion = XQueryIntelliJPluginVersion(this, 1, 3)
    val VERSION_1_4: XpmProductVersion = XQueryIntelliJPluginVersion(this, 1, 4)

    @Suppress("unused")
    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_1_3,
        VERSION_1_4
    )

    // endregion
}
