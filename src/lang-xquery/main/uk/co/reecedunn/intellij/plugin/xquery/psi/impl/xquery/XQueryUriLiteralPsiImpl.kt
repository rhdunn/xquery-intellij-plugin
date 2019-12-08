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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginLocationURIList
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginStylesheetImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.XQueryUriLiteralReference

class XQueryUriLiteralPsiImpl(node: ASTNode) : XQueryStringLiteralPsiImpl(node), XPathUriLiteral {
    override fun getReference(): PsiReference {
        val range = textRange
        return XQueryUriLiteralReference(this, TextRange(1, range.length - 1))
    }

    override val cachedValue: CacheableProperty<XsAnyAtomicType> = CacheableProperty {
        val context = when (parent) {
            is FTStopWords -> XdmUriContext.StopWords
            is PluginLocationURIList -> XdmUriContext.Location
            is PluginStylesheetImport -> XdmUriContext.Location
            is XQueryBaseURIDecl -> XdmUriContext.BaseUri
            is XQueryDefaultCollationDecl -> XdmUriContext.Collation
            is XQueryGroupingSpec -> XdmUriContext.Collation
            is XQueryModuleImport -> XdmUriContext.TargetNamespace
            is XQueryNamespaceDecl -> XdmUriContext.NamespaceDeclaration
            is XQueryOrderModifier -> XdmUriContext.Collation
            is XQuerySchemaImport -> XdmUriContext.TargetNamespace
            else -> XdmUriContext.Namespace
        }
        XsAnyUri(content, context, this)
    }
}
