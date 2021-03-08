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
package uk.co.reecedunn.intellij.plugin.xpm.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.impl.W3CSpecification
import javax.swing.Icon

@Suppress("MemberVisibilityCanBePrivate")
object XmlSchemaDatatypesSpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XML Schema Definition Language (XSD) Part 2: Datatypes"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xmlschema"

    override val presentation: ItemPresentation
        get() = this

    // endregion
    // region Versions

    val REC_1_0_20041028: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.0-20041028",
        "1.0",
        "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/"
    )

    val REC_1_1_20120405: XpmSpecificationVersion = W3CSpecification(
        this,
        "1.1-20120405",
        "1.1",
        "http://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/"
    )

    val versions: List<XpmSpecificationVersion> = listOf(
        REC_1_0_20041028,
        REC_1_1_20120405
    )

    // endregion
}
