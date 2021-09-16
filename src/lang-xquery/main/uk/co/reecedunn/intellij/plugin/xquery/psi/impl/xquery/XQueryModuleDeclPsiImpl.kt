/*
 * Copyright (C) 2016-2018, 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

class XQueryModuleDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryModuleDecl, XQueryPrologResolver {
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmNamespaceDeclaration

    // MarkLogic treats NCName FunctionDecls as being in the ModuleDecl namespace.
    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return (
            namespaceType === XdmNamespaceType.DefaultFunctionDecl ||
            namespaceType === XdmNamespaceType.DefaultFunctionRef
        )
    }

    // endregion
    // region XQueryPrologResolver

    override val prolog: Sequence<XQueryProlog>
        get() = siblings().filterIsInstance<XQueryProlog>()

    // endregion
}
