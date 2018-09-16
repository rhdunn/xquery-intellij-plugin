/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

class XQueryModuleImportPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryModuleImport,
    XQueryPrologResolver,
    XPathNamespaceDeclaration {
    // region XPathNamespaceDeclaration

    override val namespacePrefix
        get(): XsNCNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName

    override val namespaceUri
        get(): XsAnyUriValue? = children().filterIsInstance<XQueryUriLiteral>().firstOrNull()?.value as? XsAnyUriValue

    // endregion
    // region XQueryPrologResolver

    override val prolog
        get(): XQueryProlog? {
            var hasImportPath = false
            return children().filterIsInstance<XQueryUriLiteral>().drop(1).map { uri ->
                // 1. Try the "at ..." paths.
                hasImportPath = true
                val file = (uri.value as XsAnyUriValue).resolveUri<XQueryModule>()
                val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
                (library as? XQueryPrologResolver)?.prolog
            }.filterNotNull().firstOrNull() ?: if (!hasImportPath) {
                // 2. Try the import namespace if there are no "at ..." paths.
                val uri = children().filterIsInstance<XQueryUriLiteral>().firstOrNull()
                val file = (uri?.value as? XsAnyUriValue)?.resolveUri<XQueryModule>()
                val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
                (library as? XQueryPrologResolver)?.prolog
            } else {
                null
            }
        }

    // endregion
}
