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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsGroup
import uk.co.reecedunn.intellij.plugin.core.xml.descendants
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.Rewriter
import javax.swing.Icon

class RewriterEndpointsGroup(private val rewriter: XmlTag) : EndpointsGroup, ItemPresentation, DataProvider {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = MarkLogicIcons.Rewriter.EndpointsGroup

    override fun getLocationString(): String = rewriter.containingFile.name

    override fun getPresentableText(): String = EndpointsGroup.ROOT_PATH

    // endregion
    // region EndpointsGroup

    override val presentation: ItemPresentation
        get() = this

    override val endpoints: Sequence<Endpoint>
        get() = rewriter.descendants(Rewriter.NAMESPACE, Rewriter.ENDPOINT_ELEMENTS).map { RewriterEndpoint(it) }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> rewriter
        else -> null
    }

    // endregion
}
