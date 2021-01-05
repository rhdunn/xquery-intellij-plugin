/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathRecordTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmMap
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFieldDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class XPathRecordTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathRecordTest, XpmSyntaxValidationElement {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region XPathRecordTest

    override val fields: Sequence<XPathFieldDeclaration>
        get() = children().filterIsInstance<XPathFieldDeclaration>()

    override val isExtensible: Boolean
        get() = conformanceElement !== firstChild

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val fields = fields.map {
            val fieldName = it.fieldName.data.let { name ->
                if (name.contains("\\s".toRegex()))
                    "\"$name\""
                else
                    name
            }
            val name = if (it.isOptional) "$fieldName?" else fieldName
            val type = it.fieldType?.let { type -> ": ${type.typeName}" } ?: ""
            "$name$type"
        }.filterNotNull().joinToString()

        if (isExtensible)
            "tuple($fields, *)"
        else
            "tuple($fields)"
    }

    override val typeName: String
        get() = cachedTypeName.get()!!

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int = 1

    override val upperBound: Int = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmMap::class.java

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.STAR) ?: firstChild

    // endregion
}
