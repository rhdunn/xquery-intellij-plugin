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
package uk.co.reecedunn.intellij.plugin.xpath.completion.property

import com.intellij.openapi.util.Key
import uk.co.reecedunn.intellij.plugin.intellij.lang.Product
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

object XPathCompletionProperty {
    val XPATH_VERSION: Key<Version> = Key.create("uk.co.reecedunn.intellij.plugin.xpath.XPathVersion")

    val XSLT_VERSION: Key<Version> = Key.create("uk.co.reecedunn.intellij.plugin.xpath.XsltVersion")

    val XPATH_PRODUCT: Key<Product> = Key.create("uk.co.reecedunn.intellij.plugin.xpath.XPathProduct")

    val XPATH_PRODUCT_VERSION: Key<Version> = Key.create("uk.co.reecedunn.intellij.plugin.xpath.XPathProductVersion")

    val STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES: Key<List<XdmNamespaceDeclaration>> =
        Key.create("uk.co.reecedunn.intellij.plugin.xpath.StaticallyKnownElementOrTypeNamespaces")

    val STATICALLY_KNOWN_FUNCTION_NAMESPACES: Key<List<XdmNamespaceDeclaration>> =
        Key.create("uk.co.reecedunn.intellij.plugin.xpath.StaticallyKnownFunctionNamespaces")
}
