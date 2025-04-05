// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.resources

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider

class MarkLogicResourceProvider : StandardResourceProvider {
    companion object {
        private const val XDMP_NAMESPACE = "http://marklogic.com/xdmp"
    }

    override fun registerResources(registrar: ResourceRegistrar?) {
        registrar!!.addStdResource(XDMP_NAMESPACE, "1.0", "/schemas/xdmp.xsd", MarkLogicResourceProvider::class.java)
    }
}
