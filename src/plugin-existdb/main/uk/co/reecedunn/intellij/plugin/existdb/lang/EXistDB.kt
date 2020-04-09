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
package uk.co.reecedunn.intellij.plugin.existdb.lang

import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion

object EXistDB : XpmProductType {
    // region XpmProductType

    override val id: String = "exist-db"

    override val name = "eXist-db"

    // endregion
    // region Language Versions

    val VERSION_3_0: XpmProductVersion = EXistDBVersion(this, 3, 0, "XQuery 3.0 REC, array, map, json")
    val VERSION_3_1: XpmProductVersion = EXistDBVersion(this, 3, 1, "arrow operator '=>', string constructors")
    val VERSION_3_6: XpmProductVersion = EXistDBVersion(this, 3, 6, "declare context item")
    val VERSION_4_0: XpmProductVersion = EXistDBVersion(this, 4, 0, "XQuery 3.1 REC")
    val VERSION_4_3: XpmProductVersion = EXistDBVersion(this, 4, 0, "XMLSchema 1.1")

    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_3_0,
        VERSION_3_1,
        VERSION_3_6,
        VERSION_4_0,
        VERSION_4_3
    )

    // endregion
}
