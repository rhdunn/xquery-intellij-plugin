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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginUsingDecl

class PluginUsingDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginUsingDecl, XPathDefaultNamespaceDeclaration, VersionConformance {
    // region XPathNamespaceDeclaration

    override val namespacePrefix
        get(): XsNCNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri
        get(): XsAnyUriValue? = children().filterIsInstance<XPathUriLiteral>().firstOrNull()?.value as? XsAnyUriValue

    override fun accepts(namespaceType: XPathNamespaceType): Boolean {
        return namespaceType === XPathNamespaceType.DefaultFunctionRef // Usage only, not declaration.
    }

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(MarkLogic.VERSION_4_0, XQuerySpec.MARKLOGIC_0_9)

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
}
