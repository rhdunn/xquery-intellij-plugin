/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginTupleField
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

private val SAXON98: List<Version> = listOf()
private val SAXON99: List<Version> = listOf(Saxon.VERSION_9_9)

private val OPTIONAL_TOKENS = TokenSet.create(
    XQueryTokenType.OPTIONAL,
    XQueryTokenType.ELVIS // ?: for compact whitespace
)

class PluginTupleFieldImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    PluginTupleField, XQueryConformance {

    override val requiresConformance
        get(): List<Version> = if (conformanceElement === firstChild) SAXON98 else SAXON99

    override val conformanceElement get(): PsiElement = findChildByType(OPTIONAL_TOKENS) ?: firstChild
}