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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsString
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

open class XQueryStringLiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathStringLiteral {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedContent.invalidate()
    }

    override val value: XsAnyAtomicType get() = XsString(cachedContent.get()!!, this)

    protected val cachedContent = CacheableProperty {
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
        }.filterNotNull().joinToString(separator = "") `is` Cacheable
    }
}
