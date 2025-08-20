// Copyright (C) 2019, 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.resources

import com.intellij.compat.javaee.addInternalResource
import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT

class XsltResourceProvider : StandardResourceProvider {
    companion object {
        private const val XSLT_20_URI = "https://www.w3.org/2007/schema-for-xslt20.xsd"
        private const val XSLT_30_URI = "https://www.w3.org/TR/xslt-30/schema-for-xslt30.xsd"

        private const val EXSL_COMMON_NAMESPACE = "http://exslt.org/common"
        private const val ELEMENT_SYNTAX_NAMESPACE = "http://www.w3.org/1999/XSL/Spec/ElementSyntax"
    }

    override fun registerResources(registrar: ResourceRegistrar) {
        registrar.addInternalResource(XSLT_20_URI, "xslt-2_0.xsd", XsltResourceProvider::class.java)
        registrar.addStdResource(XSLT_30_URI, "3.0", "/schemas/xslt-3_0.xsd", XsltResourceProvider::class.java)

        registrar.addStdResource(XSLT.NAMESPACE, "3.0", "/schemas/xslt-3_0.xsd", XsltResourceProvider::class.java)
        registrar.addStdResource(XSLT.NAMESPACE, "4.0", "/schemas/xslt-4_0.xsd", XsltResourceProvider::class.java)

        registrar.addStdResource(EXSL_COMMON_NAMESPACE, "1.0", "/schemas/exsl-common.xsd", XsltResourceProvider::class.java)

        registrar.addIgnoredResource(ELEMENT_SYNTAX_NAMESPACE)
    }
}
