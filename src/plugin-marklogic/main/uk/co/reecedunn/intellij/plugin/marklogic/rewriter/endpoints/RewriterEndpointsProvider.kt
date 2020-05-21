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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsProvider
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicIcons
import javax.swing.Icon

object RewriterEndpointsProvider : EndpointsProvider(), ItemPresentation {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = MarkLogicIcons.Product

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = MarkLogicBundle.message("endpoints.rewriter.label")

    // endregion
    // region EndpointsFramework

    override val id: String = "xijp.marklogic-rewriter"

    override val presentation: ItemPresentation get() = this

    // endregion
}
