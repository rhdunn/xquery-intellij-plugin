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
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDirElemConstructorPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryDirElemConstructor {
    override val openTag get(): QName? =
        (findChildByClass(XPathEQName::class.java) as? XdmStaticValue)?.constantValue as? QName

    override val closeTag get(): QName? {
        val tag = findChildrenByClass(XPathEQName::class.java)
        return if (tag.size == 2) (tag[1] as XdmStaticValue).constantValue as QName else null
    }

    override val isSelfClosing get(): Boolean =
        lastChild.node.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG
}
