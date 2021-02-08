/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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

data class W3CSpecificationsVersion(override val product: XpmProductType) : XpmProductVersion {
    // region XpmProductVersion

    override val id: String = "1.0"

    override val features: String = ""

    override fun compareTo(other: XpmProductVersion): Int = this.product.id.compareTo(other.product.id)

    // endregion
}
