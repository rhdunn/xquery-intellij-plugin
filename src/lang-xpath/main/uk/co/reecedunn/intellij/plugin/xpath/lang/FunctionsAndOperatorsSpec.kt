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
package uk.co.reecedunn.intellij.plugin.xpath.lang

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpm.intellij.resources.XpmIcons
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType
import javax.swing.Icon

object FunctionsAndOperatorsSpec : ItemPresentation, XpmSpecificationType {
    // region ItemPresentation

    override fun getPresentableText(): String = "XQuery and XPath Functions and Operators"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = XpmIcons.W3.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xpath-functions"

    override val presentation: ItemPresentation
        get() = this

    // endregion
}
