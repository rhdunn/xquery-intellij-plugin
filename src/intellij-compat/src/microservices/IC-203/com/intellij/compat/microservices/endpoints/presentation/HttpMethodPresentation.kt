// Copyright (C) 2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.microservices.endpoints.presentation

object HttpMethodPresentation {
    @Suppress("UNUSED_PARAMETER")
    fun getHttpMethodOrder(method: String?): Int = 0

    fun getHttpMethodsPresentation(methods: List<String>) = when (methods.isEmpty()) {
        true -> ""
        else -> methods.joinToString("|", "[", "]")
    }
}
