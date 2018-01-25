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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmProcessingInstruction
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPITest
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration

class XPathPITestPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathPITest,
        XPathTypeDeclaration {
    // region XPathTypeDeclaration

    override val cacheable get(): CachingBehaviour = targetName?.cacheable ?: CachingBehaviour.Cache

    override val declaredType get(): XdmSequenceType {
        return XdmProcessingInstruction(targetName?.staticValue as? QName)
    }

    private val targetName get(): XdmStaticValue? {
        return children().map { e -> when (e) {
            is XPathNCName -> e as XdmStaticValue
            else -> null
        }}.filterNotNull().firstOrNull()
    }

    // endregion
}
