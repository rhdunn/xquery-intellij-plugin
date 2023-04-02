// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints

interface EndpointsProvider<Group : Any, Endpoint : Any> :
    com.intellij.microservices.endpoints.EndpointsProvider<Group, Endpoint> {
}
