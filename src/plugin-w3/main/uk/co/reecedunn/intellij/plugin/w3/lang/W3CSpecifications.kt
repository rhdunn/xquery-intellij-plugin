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
package uk.co.reecedunn.intellij.plugin.w3.lang

import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion

object W3CSpecifications : XpmProductType {
    // region XpmProductType

    override val id: String = "w3c"

    override val name = "W3C Specifications"

    // endregion
    // region Language Versions

    val REC: XpmProductVersion = W3CSpecificationsVersion(this)

    val languageVersions: List<XpmProductVersion> = listOf(REC)

    // endregion
}
