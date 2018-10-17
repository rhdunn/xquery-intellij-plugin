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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class XPathBracedURILiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathBracedURILiteral,
    XsAnyUriValue,
    XQueryConformance {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedContent.invalidate()
    }

    override val data: String get() = cachedContent.get()!!

    private val cachedContent = CacheableProperty {
        children().map { child ->
            when (child.node.elementType) {
                XQueryTokenType.BRACED_URI_LITERAL_START, XQueryTokenType.BRACED_URI_LITERAL_END ->
                    null
                XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ->
                    (child as XQueryPredefinedEntityRef).entityRef.value
                XQueryTokenType.CHARACTER_REFERENCE ->
                    (child as XQueryCharRef).codepoint.toString()
                else ->
                    child.text
            }
        }.filterNotNull().joinToString(separator = "") `is` Cacheable
    }

    override val element get(): PsiElement? = this

    override val requiresConformance get(): List<Version> = listOf(XQuery.REC_3_0_20140408, MarkLogic.VERSION_6_0)

    override val conformanceElement get(): PsiElement = firstChild
}
