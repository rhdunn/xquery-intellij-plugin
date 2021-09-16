/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginUsingDecl

class PluginUsingDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginUsingDecl, XpmSyntaxValidationElement {
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return namespaceType === XdmNamespaceType.DefaultFunctionRef // Usage only, not declaration.
    }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
