/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.microservices.endpoints.presentation

import com.intellij.microservices.endpoints.presentation.EndpointMethodPresentation
import com.intellij.microservices.endpoints.presentation.HttpMethodPresentation
import com.intellij.navigation.ItemPresentation
import javax.swing.Icon

data class EndpointMethodPresentation(
    private val presentation: ItemPresentation,
    override val endpointMethod: String?,
    override val endpointMethodOrder: Int
) : ItemPresentation, EndpointMethodPresentation {
    companion object {
        fun getHttpMethodOrder(method: String?): Int = HttpMethodPresentation.getHttpMethodOrder(method)
    }
    // region ItemPresentation

    override fun getPresentableText(): String? = presentation.presentableText

    override fun getLocationString(): String? = presentation.locationString

    override fun getIcon(unused: Boolean): Icon? = presentation.getIcon(unused)

    // endregion
}
