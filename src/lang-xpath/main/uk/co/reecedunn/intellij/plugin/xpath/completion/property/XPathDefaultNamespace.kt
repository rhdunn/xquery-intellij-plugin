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
package uk.co.reecedunn.intellij.plugin.xpath.completion.property

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.defaultNamespace
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import java.lang.UnsupportedOperationException

class XPathDefaultNamespace(private val property: Key<XpmNamespaceDeclaration?>) : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[property] == null) {
            val namespace = element.defaultNamespace(namespaceType).firstOrNull()
            context.put(property, namespace)
        }
    }

    private val namespaceType: XdmNamespaceType
        get() = when (property) {
            XPathCompletionProperty.DEFAULT_ELEMENT_NAMESPACE -> XdmNamespaceType.DefaultElement
            XPathCompletionProperty.DEFAULT_FUNCTION_DECL_NAMESPACE -> XdmNamespaceType.DefaultFunctionDecl
            XPathCompletionProperty.DEFAULT_FUNCTION_REF_NAMESPACE -> XdmNamespaceType.DefaultFunctionRef
            XPathCompletionProperty.DEFAULT_TYPE_NAMESPACE -> XdmNamespaceType.DefaultType
            else -> throw UnsupportedOperationException()
        }
}
