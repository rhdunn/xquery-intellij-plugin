/*
 * Copyright (C) 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.model.defaultElementOrTypeXPathNamespace
import uk.co.reecedunn.intellij.plugin.xpath.model.defaultFunctionXPathNamespace
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownXPathNamespaces
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider

object XsltNamespaceProvider : XpmNamespaceProvider {
    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XPath) return emptySequence()
        return context.staticallyKnownXPathNamespaces()
    }

    override fun defaultNamespace(context: PsiElement, type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XPath) return emptySequence()
        return when (type) {
            XdmNamespaceType.DefaultElementOrType -> context.defaultElementOrTypeXPathNamespace()
            XdmNamespaceType.DefaultFunctionDecl -> context.defaultFunctionXPathNamespace()
            XdmNamespaceType.DefaultFunctionRef -> context.defaultFunctionXPathNamespace()
            else -> emptySequence()
        }
    }
}
