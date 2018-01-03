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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.model.toNamespace
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaPrefix
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver

class XQuerySchemaImportPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQuerySchemaImport,
        XQueryNamespaceResolver,
        XdmNamespaceDeclaration {
    // region XdmNamespaceDeclaration

    override val namespacePrefix get(): XdmLexicalValue? {
        val schema = children().filterIsInstance<XQuerySchemaPrefix>().firstOrNull() ?: return null
        return schema.children().filterIsInstance<XPathNCName>().firstOrNull()?.localName as? XdmLexicalValue
    }

    override val namespaceUri get(): XdmLexicalValue? =
        children().filterIsInstance<XQueryUriLiteral>().firstOrNull() as? XdmLexicalValue

    // endregion
    // region XQueryNamespaceResolver

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        return prefix?.let {
            if (namespacePrefix?.lexicalRepresentation == prefix)
                this.toNamespace()
            else
                null
        }
    }

    // endregion
}
