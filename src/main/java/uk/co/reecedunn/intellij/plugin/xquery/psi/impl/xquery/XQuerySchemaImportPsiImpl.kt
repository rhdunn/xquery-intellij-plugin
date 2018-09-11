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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaPrefix
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQuerySchemaImportPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQuerySchemaImport,
    XPathDefaultNamespaceDeclaration {
    // region XPathDefaultNamespaceDeclaration

    private val schemaPrefix get() = children().filterIsInstance<XQuerySchemaPrefix>().firstOrNull()

    override val namespaceType
        get(): XPathNamespaceType {
            return when (schemaPrefix?.firstChild?.node?.elementType) {
                XQueryTokenType.K_NAMESPACE -> XPathNamespaceType.Prefixed
                else -> XPathNamespaceType.DefaultElementOrType
            }
        }

    override val namespacePrefix
        get(): XsNCNameValue? {
            return schemaPrefix?.let { it.children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName }
        }

    override val namespaceUri
        get(): XsAnyUriValue? = children().filterIsInstance<XQueryUriLiteral>().firstOrNull()?.value as? XsAnyUriValue

    // endregion
}
