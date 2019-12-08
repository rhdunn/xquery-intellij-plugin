/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginLocationURIList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

class XQueryModuleImportPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryModuleImport,
    XQueryPrologResolver,
    XPathNamespaceDeclaration {
    // region XPathNamespaceDeclaration

    override val namespacePrefix
        get(): XsNCNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri
        get(): XsAnyUriValue? = children().filterIsInstance<XPathUriLiteral>().firstOrNull()?.value as? XsAnyUriValue

    // endregion
    // region XQueryModuleImport

    override val locationUris
        get(): Sequence<XsAnyUriValue> {
            val uris = children().filterIsInstance<PluginLocationURIList>().firstOrNull()
            return uris?.children()?.filterIsInstance<XPathUriLiteral>()?.map { uri ->
                uri.value as XsAnyUriValue
            }?.filterNotNull() ?: emptySequence()
        }

    // endregion
    // region XQueryPrologResolver

    override val prolog
        get(): Sequence<XQueryProlog> {
            val locations = locationUris
            return if (locations.any())
                locations.flatMap { uri ->
                    val file = uri.resolveUri<XQueryModule>()
                    val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
                    (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
                }.filterNotNull()
            else
                namespaceUri?.let { uri ->
                    val file = uri.resolveUri<XQueryModule>()
                    val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
                    (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
                } ?: emptySequence()
        }

    // endregion
}
