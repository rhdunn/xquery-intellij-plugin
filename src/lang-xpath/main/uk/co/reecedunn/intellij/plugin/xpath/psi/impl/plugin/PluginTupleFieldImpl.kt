/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTupleField
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XsQNameValue

private val SAXON98: List<Version> = listOf()
private val SAXON99: List<Version> = listOf(Saxon.VERSION_9_9)

private val OPTIONAL_TOKENS = TokenSet.create(
    XPathTokenType.OPTIONAL,
    XPathTokenType.ELVIS // ?: for compact whitespace
)

class PluginTupleFieldImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginTupleField, VersionConformance {
    // region PluginTupleField

    override val fieldName: XsNCNameValue get() = (firstChild as XsQNameValue).localName!!

    override val fieldType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val isOptional: Boolean get() = conformanceElement !== firstChild

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = if (isOptional) SAXON99 else SAXON98

    override val conformanceElement get(): PsiElement = findChildByType(OPTIONAL_TOKENS) ?: firstChild

    // endregion
}
