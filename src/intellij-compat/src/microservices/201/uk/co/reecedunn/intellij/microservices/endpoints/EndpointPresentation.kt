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
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.microservices.presentation.EndpointMethodPresentation
import com.intellij.microservices.presentation.HttpMethodPresentation
import com.intellij.navigation.ItemPresentation
import javax.swing.Icon

@Suppress("UnstableApiUsage")
class EndpointPresentation(private val endpoint: Endpoint) : ItemPresentation, EndpointMethodPresentation {
    // region ItemPresentation

    override fun getLocationString(): String? = endpoint.presentation.locationString

    override fun getPresentableText(): String? = endpoint.presentation.presentableText

    override fun getIcon(unused: Boolean): Icon? = endpoint.presentation.getIcon(unused)

    // endregion
    // region EndpointMethodPresentation

    override val endpointMethod: String?
        get() = endpoint.method

    override val endpointMethodOrder: Int
        get() = HttpMethodPresentation.getHttpMethodOrder(endpointMethod?.split("\\s+")?.get(0))

    // endregion
}
