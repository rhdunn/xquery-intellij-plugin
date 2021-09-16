/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.resolve
import uk.co.reecedunn.intellij.plugin.xpm.module.resolveUri
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

class XQueryNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryNamespaceDecl,
    XQueryPrologResolver,
    XpmNamespaceDeclaration {
    // region XQueryPrologResolver

    override val prolog: Sequence<XQueryProlog>
        get() {
            val file = namespaceUri?.let {
                it.resolve() ?: it.resolveUri<XQueryModule>()
            }
            val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
            return (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
        }

    // endregion
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return namespaceType === XdmNamespaceType.Prefixed
    }

    // endregion
}
