/*
 * Copyright (C) 2018 Reece H. Dunn
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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.xdm.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginNamedKindTest
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class PluginNamedKindTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginNamedKindTest,
    XQueryConformance,
    XPathTypeDeclaration {
    // region XQueryConformance

    override val requiresConformance get(): List<Version> = listOf(MarkLogic.VERSION_8_0)

    override val conformanceElement get(): PsiElement = findChildByType(XQueryElementType.STRING_LITERAL)!!

    // endregion
    // region XPathTypeDeclaration

    override val cacheable get(): CachingBehaviour = CachingBehaviour.Cache

    override val declaredType get(): XdmSequenceType = XdmNode

    // endregion
}
