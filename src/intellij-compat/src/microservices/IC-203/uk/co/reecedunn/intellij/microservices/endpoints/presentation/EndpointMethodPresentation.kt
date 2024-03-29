// Copyright (C) 2021-2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints.presentation

interface EndpointMethodPresentation {
    val endpointMethodPresentation: String
    val endpointMethods: List<String>
    val endpointMethodOrder: Int
}
