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
import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmProcessingInstruction
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPITest
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration

class XPathPITestPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathPITest,
        XPathTypeDeclaration {
    // region XPathTypeDeclaration

    override val cacheable get(): CachingBehaviour = CachingBehaviour.Cache

    override val declaredType get(): XdmSequenceType {
        return XdmProcessingInstruction(null)
    }

    // endregion
}
