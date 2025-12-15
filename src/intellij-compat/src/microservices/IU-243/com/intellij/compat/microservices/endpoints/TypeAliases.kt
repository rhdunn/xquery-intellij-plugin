// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.microservices.endpoints

import com.intellij.microservices.endpoints.EndpointsFilter
import com.intellij.microservices.endpoints.EndpointsProvider
import com.intellij.microservices.endpoints.FrameworkPresentation
import com.intellij.microservices.endpoints.HTTP_SERVER_TYPE

typealias EndpointsFilter = EndpointsFilter

typealias EndpointsProviderStatus = EndpointsProvider.Status

typealias EndpointType = com.intellij.microservices.endpoints.EndpointType

typealias FrameworkPresentation = FrameworkPresentation

val HTTP_SERVER_TYPE: EndpointType
    get() = HTTP_SERVER_TYPE

typealias EndpointsProvider<Group, Endpoint> = EndpointsProvider<Group, Endpoint>
