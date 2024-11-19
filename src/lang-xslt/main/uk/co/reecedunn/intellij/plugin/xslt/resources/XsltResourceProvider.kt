/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.resources

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.ResourceRegistrarImpl
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
        registrar as ResourceRegistrarImpl

        registrar.addInternalResource(XSLT_20_URI, "xslt-2_0.xsd")
        registrar.addStdResource(XSLT_30_URI, "/schemas/xslt-3_0.xsd", XsltResourceProvider::class.java)

        registrar.addStdResource(XSLT.NAMESPACE, "3.0", "/schemas/xslt-3_0.xsd", XsltResourceProvider::class.java)
        registrar.addStdResource(XSLT.NAMESPACE, "4.0", "/schemas/xslt-4_0.xsd", XsltResourceProvider::class.java)

        registrar.addStdResource(EXSL_COMMON_NAMESPACE, "/schemas/exsl-common.xsd", XsltResourceProvider::class.java)

        registrar.addIgnoredResource(ELEMENT_SYNTAX_NAMESPACE)
    }
}
