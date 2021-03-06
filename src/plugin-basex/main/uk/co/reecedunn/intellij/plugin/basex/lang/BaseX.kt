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
package uk.co.reecedunn.intellij.plugin.basex.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.basex.resources.BaseXIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSchemaFile
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmVendorType
import java.io.File
import javax.swing.Icon

object BaseX : ItemPresentation, XpmVendorType, XpmProductType {
    // region ItemPresentation

    override fun getPresentableText(): String = "BaseX"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = BaseXIcons.Product

    // endregion
    // region XpmVendorType / XpmProductType

    override val id: String = "basex"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region XpmVendorType

    private val basexJar = listOf(
        "BaseX.jar",
        "basex.jar",
        "BaseX6.jar"
    )

    override fun isValidInstallDir(installDir: String): Boolean {
        return basexJar.find { File("$installDir/$it").exists() } != null
    }

    override val modulePath: String? = null

    override fun schemaFiles(path: String): Sequence<XpmSchemaFile> = sequenceOf()

    // endregion
    // region Language Versions

    val VERSION_6_1: XpmProductVersion = BaseXVersion(this, 6, 1, "Update Facility, Full Text, fuzzy")
    val VERSION_7_7: XpmProductVersion = BaseXVersion(this, 7, 7, "XQuery 3.0 REC")
    val VERSION_7_8: XpmProductVersion = BaseXVersion(this, 7, 8, "update")
    val VERSION_8_4: XpmProductVersion = BaseXVersion(this, 8, 4, "non-deterministic")
    val VERSION_8_5: XpmProductVersion = BaseXVersion(this, 8, 5, "update {}, transform with")
    val VERSION_8_6: XpmProductVersion = BaseXVersion(this, 8, 6, "XQuery 3.1 REC")
    val VERSION_9_1: XpmProductVersion = BaseXVersion(this, 9, 1, "ternary if, ?:, if without else")

    @Suppress("unused")
    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_6_1,
        VERSION_7_7,
        VERSION_7_8,
        VERSION_8_4,
        VERSION_8_5,
        VERSION_8_6,
        VERSION_9_1
    )

    // endregion
}
