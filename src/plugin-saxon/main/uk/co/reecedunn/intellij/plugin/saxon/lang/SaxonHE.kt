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

import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion

object SaxonHE : XpmProductType {
    // region XpmProductType

    override val id: String = "saxon/HE"

    override val name = "Saxon Home Edition"

    // endregion
    // region Language Versions

    val VERSION_9_6: XpmProductVersion = SaxonVersion(this, 9, 6, "XQuery 3.0 REC") // 9.5 for PE and EE
    val VERSION_9_7: XpmProductVersion = SaxonVersion(this, 9, 7, "XQuery 3.1 REC")
    val VERSION_10_0: XpmProductVersion = SaxonVersion(this, 10, 0, "XQuery 3.0 higher-order functions")

    val languageVersions: List<XpmProductVersion> = listOf(
        VERSION_9_6,
        VERSION_9_7,
        VERSION_10_0
    )

    // endregion
}
