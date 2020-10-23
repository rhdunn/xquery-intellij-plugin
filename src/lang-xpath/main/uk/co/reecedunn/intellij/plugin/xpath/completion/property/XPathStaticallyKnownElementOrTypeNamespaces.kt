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

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpath.model.*

object XPathStaticallyKnownElementOrTypeNamespaces : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES] == null) {
            val namespaces = element.defaultNamespace(XdmNamespaceType.DefaultElementOrType).firstOrNull()?.let {
                val list = mutableListOf<XpmNamespaceDeclaration>(it)
                list.addAll(element.staticallyKnownNamespaces())
                list
            } ?: mutableListOf()
            context.put(XPathCompletionProperty.STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES, namespaces)
        }
    }
}
