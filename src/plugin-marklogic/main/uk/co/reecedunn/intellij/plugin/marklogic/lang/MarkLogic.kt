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
package uk.co.reecedunn.intellij.plugin.marklogic.lang

import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmVendorType
import java.io.File

object MarkLogic : XpmVendorType, XpmProductType {
    // region XpmVendorType / XpmProductType

    override val id: String = "marklogic"

    override val name = "MarkLogic"

    // endregion
    // region XpmVendorType

    private val markLogicExecutable = listOf(
        "MarkLogic.exe", // Windows
        "bin/MarkLogic" // Linux / Mac OS
    )

    override fun isValidInstallDir(installDir: String): Boolean {
        return markLogicExecutable.find { File("$installDir/$it").exists() } != null
    }

    // endregion
    // region Language Versions

    val VERSION_6: XpmProductVersion = MarkLogicVersion(this, 6, "property::, namespace::, binary, transactions, etc.")
    val VERSION_7: XpmProductVersion = MarkLogicVersion(this, 7, "schema kind tests: schema-type, etc.")
    val VERSION_8: XpmProductVersion = MarkLogicVersion(this, 8, "json kind tests and constructors: object-node, etc.")
    val VERSION_9: XpmProductVersion = MarkLogicVersion(this, 9, "arrow operator '=>'")

    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_6,
        VERSION_7,
        VERSION_8,
        VERSION_9
    )

    // endregion
}
