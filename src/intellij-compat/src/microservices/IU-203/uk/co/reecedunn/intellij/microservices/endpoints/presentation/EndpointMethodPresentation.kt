// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints.presentation

@Suppress("UnstableApiUsage")
interface EndpointMethodPresentation : com.intellij.microservices.endpoints.presentation.EndpointMethodPresentation {
    val endpointMethodPresentation: String?

    val endpointMethods: List<String>

    override val endpointMethod: String?
        get() = endpointMethodPresentation
}
