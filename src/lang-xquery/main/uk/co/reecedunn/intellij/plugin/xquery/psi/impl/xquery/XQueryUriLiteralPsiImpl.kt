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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginLocationURIList
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginStylesheetImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathUriLiteralReference
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryUriLiteralPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathUriLiteral, XsAnyUriValue {
    // region PsiElement

    override fun getReference(): PsiReference {
        val range = textRange
        return XPathUriLiteralReference(this, TextRange(1, range.length - 1))
    }

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedData.invalidate()
    }

    // endregion

    override val element: PsiElement? get() = this

    override val context: XdmUriContext
        get() = when (parent) {
            is FTStopWords -> XdmUriContext.StopWords
            is FTThesaurusID -> XdmUriContext.Thesaurus
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

    override val data: String get() = cachedData.get()!!

    private val cachedData: CacheableProperty<String> = CacheableProperty {
        children().map { child ->
            when (child.node.elementType) {
                XPathTokenType.STRING_LITERAL_START, XPathTokenType.STRING_LITERAL_END ->
                    null
                XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ->
                    (child as XQueryPredefinedEntityRef).entityRef.value
                XQueryTokenType.CHARACTER_REFERENCE ->
                    (child as XQueryCharRef).codepoint.toString()
                XPathTokenType.ESCAPED_CHARACTER ->
                    (child as XPathEscapeCharacter).unescapedValue
                else ->
                    child.text
            }
        }.filterNotNull().joinToString(separator = "")
    }
}
