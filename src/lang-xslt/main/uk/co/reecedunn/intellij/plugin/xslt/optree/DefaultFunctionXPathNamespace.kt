/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.optree

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration

internal object DefaultFunctionXPathNamespace : XpmNamespaceDeclaration {
    private const val FN_NAMESPACE_URI = "http://www.w3.org/2005/xpath-functions"

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri: XsAnyUriValue =
        XsAnyUri(FN_NAMESPACE_URI, XdmUriContext.Namespace, XdmModuleType.MODULE, null as PsiElement?)

    override val parentNode: XdmNode? = null

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return (
            namespaceType === XdmNamespaceType.DefaultFunctionDecl ||
            namespaceType === XdmNamespaceType.DefaultFunctionRef
        )
    }
}
