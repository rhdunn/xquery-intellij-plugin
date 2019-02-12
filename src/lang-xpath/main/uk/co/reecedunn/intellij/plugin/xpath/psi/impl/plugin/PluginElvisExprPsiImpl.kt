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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.intellij.lang.BaseX
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginElvisExpr
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class PluginElvisExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginElvisExpr,
    VersionConformance {

    override val requiresConformance get(): List<Version> = listOf(BaseX.VERSION_9_1)

    override val conformanceElement get(): PsiElement = findChildByType(XPathTokenType.ELVIS)!!
}