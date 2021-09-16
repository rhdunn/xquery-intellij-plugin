/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsID
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsUntypedAtomic
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import java.util.*

class PluginDirAttributePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginDirAttribute {
    companion object {
        private val NODE_VALUE = Key.create<Optional<XsAnyAtomicType>>("NODE_VALUE")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(NODE_VALUE)
    }

    // endregion
    // region XdmAttributeNode

    override val nodeName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    override val typedValue: XsAnyAtomicType?
        get() = computeUserDataIfAbsent(NODE_VALUE) {
            val qname = nodeName ?: return@computeUserDataIfAbsent Optional.empty()
            val attrValue = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
                ?: return@computeUserDataIfAbsent Optional.empty()
            val contents =
                if (!attrValue.isValidHost)
                    null // Cannot evaluate enclosed content expressions statically.
                else
                    attrValue.children().map { child ->
                        when (child.elementType) {
                            XQueryTokenType.XML_ATTRIBUTE_VALUE_START, XQueryTokenType.XML_ATTRIBUTE_VALUE_END ->
                                null
                            XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE ->
                                (child as XQueryPredefinedEntityRef).entityRef.value
                            XQueryTokenType.XML_CHARACTER_REFERENCE ->
                                (child as XQueryCharRef).codepoint.toString()
                            XQueryTokenType.XML_ESCAPED_CHARACTER ->
                                (child as XPathEscapeCharacter).unescapedCharacter.toString()
                            else ->
                                child.text
                        }
                    }.filterNotNull().joinToString(separator = "")
            val ret = when {
                contents == null -> null
                qname.prefix?.data == "xml" && qname.localName?.data == "id" -> {
                    XsID(contents, this)
                }
                else -> XsUntypedAtomic(contents, this)
            }
            Optional.ofNullable(ret)
        }.orElse(null)

    // endregion
}
