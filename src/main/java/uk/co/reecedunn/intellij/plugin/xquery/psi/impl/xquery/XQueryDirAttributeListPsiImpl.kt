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
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.model.toNamespace
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver

class XQueryDirAttributeListPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryDirAttributeList, XQueryNamespaceResolver {
    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        return children().filterIsInstance<XQueryDirAttribute>().map { attr ->
            val e = attr as XdmNamespaceDeclaration
            if (prefix != null && e.namespacePrefix?.lexicalRepresentation == prefix)
                e.toNamespace()
            else
                null
        }.filterNotNull().firstOrNull()
    }
}
