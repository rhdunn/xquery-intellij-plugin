// Copyright (C) 2021-2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.microservices.endpoints.presentation

interface EndpointMethodPresentation {
    val endpointMethodPresentation: String
    val endpointMethods: List<String>
    val endpointMethodOrder: Int
}
