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
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsID
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsUntypedAtomic
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import java.util.*

class PluginDirAttributePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginDirAttribute {
    companion object {
        private val STRING_VALUE = Key.create<Optional<String>>("STRING_VALUE")
        private val TYPED_VALUE = Key.create<Optional<XsAnyAtomicType>>("TYPED_VALUE")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(STRING_VALUE)
        clearUserData(TYPED_VALUE)
    }

    // endregion
    // region XdmAttributeNode

    override val nodeName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    override val parentNode: XdmNode?
        get() = parent as? XdmElementNode

    override val stringValue: String?
        get() = computeUserDataIfAbsent(STRING_VALUE) {
            val attrValue = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
            when {
                attrValue == null -> Optional.empty()
                !attrValue.isValidHost -> Optional.empty() // Cannot evaluate enclosed content expressions statically.
                else -> {
                    val value = StringBuilder()
                    attrValue.children().filterIsInstance<PsiElementTextDecoder>().forEach { it.decode(value) }
                    Optional.of(value.toString())
                }
            }
        }.orElse(null)

    override val typedValue: XsAnyAtomicType?
        get() = computeUserDataIfAbsent(TYPED_VALUE) {
            val contents = stringValue ?: return@computeUserDataIfAbsent Optional.empty()
            val qname = nodeName ?: return@computeUserDataIfAbsent Optional.empty()
            val ret = when {
                qname.prefix?.data == "xml" && qname.localName?.data == "id" -> {
                    XsID(contents, this)
                }
                else -> XsUntypedAtomic(contents, this)
            }
            Optional.of(ret)
        }.orElse(null)

    // endregion
}
