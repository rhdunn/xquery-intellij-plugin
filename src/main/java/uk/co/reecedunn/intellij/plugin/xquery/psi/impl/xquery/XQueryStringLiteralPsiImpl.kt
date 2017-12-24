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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.data.CachedProperty
import uk.co.reecedunn.intellij.plugin.xdm.XsString
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

open class XQueryStringLiteralPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryStringLiteral,
        XdmAtomicValue {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedLexicalRepresentation.invalidate()
    }

    override val lexicalRepresentation get(): String = cachedLexicalRepresentation.get()!!
    private val cachedLexicalRepresentation = CachedProperty {
        children().map { child -> when (child.node.elementType) {
            XQueryTokenType.STRING_LITERAL_START, XQueryTokenType.STRING_LITERAL_END ->
                null
            XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ->
                (child as XQueryPredefinedEntityRef).entityRef.value
            XQueryTokenType.CHARACTER_REFERENCE ->
                (child as XQueryCharRef).codepoint.toString()
            XQueryTokenType.ESCAPED_CHARACTER ->
                (child as XQueryEscapeCharacter).unescapedValue
            else ->
                child.text
        }}.filterNotNull().joinToString(separator = "")
    }

    override val staticType get(): XdmSequenceType = XsString
}
