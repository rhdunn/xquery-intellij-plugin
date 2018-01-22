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
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.psi.contains
import uk.co.reecedunn.intellij.plugin.xdm.XdmOptional
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSingleType
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XPathSingleTypePsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathSingleType,
        XPathTypeDeclaration {

    override val cacheable get(): CachingBehaviour = (firstChild as XPathTypeDeclaration).cacheable

    override val declaredType get(): XdmSequenceType {
        val type = (firstChild as XPathTypeDeclaration).declaredType
        return if (contains(XQueryTokenType.OPTIONAL)) XdmOptional(type) else type
    }
}
