/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaPrefix
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver

class XQuerySchemaImportPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQuerySchemaImport, XQueryNamespaceResolver {
    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val schema = findChildByType<XQuerySchemaPrefix>(XQueryElementType.SCHEMA_PREFIX) ?: return null
        return schema.children().filterIsInstance<XQueryNCName>().map { name ->
            val localName = name.localName
            if (localName?.text == prefix) {
                val element = findChildByType<PsiElement>(XQueryElementType.URI_LITERAL)
                XQueryNamespace(localName, element, this)
            } else {
                null
            }
        }.filterNotNull().firstOrNull()
    }
}
