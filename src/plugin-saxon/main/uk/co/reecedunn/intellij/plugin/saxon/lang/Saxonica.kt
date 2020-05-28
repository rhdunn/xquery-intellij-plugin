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
import uk.co.reecedunn.intellij.plugin.saxon.intellij.resources.SaxonIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSchemaFile
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmVendorType
import javax.swing.Icon

object Saxonica : ItemPresentation, XpmVendorType {
    // region ItemPresentation

    override fun getPresentableText(): String? = "Saxonica"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon? = SaxonIcons.Product

    // endregion
    // region XpmVendorType

    override val id: String = "saxon"

    override val presentation: ItemPresentation get() = this

    // endregion
    // region XpmVendorType

    override fun isValidInstallDir(installDir: String): Boolean = false

    override val modulePath: String? = null

    override fun schemaFiles(path: String): Sequence<XpmSchemaFile> = sequenceOf()

    // endregion
}
