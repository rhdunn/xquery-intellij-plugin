// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.saxon.resources

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider

class SaxonResourceProvider : StandardResourceProvider {
    companion object {
        private const val SAXON_NAMESPACE = "http://saxon.sf.net/"
        private const val SAXON6_NAMESPACE = "http://icl.com/saxon"
    }

    override fun registerResources(registrar: ResourceRegistrar?) {
        registrar!!.addStdResource(SAXON_NAMESPACE, "1.0", "/schemas/saxon-xslt.xsd", SaxonResourceProvider::class.java)
        registrar.addStdResource(SAXON6_NAMESPACE, "1.0", "/schemas/saxon6-xslt.xsd", SaxonResourceProvider::class.java)
    }
}
