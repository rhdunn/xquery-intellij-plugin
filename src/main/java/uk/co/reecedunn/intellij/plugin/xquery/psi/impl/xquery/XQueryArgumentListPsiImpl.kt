/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryArgumentList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPostfixExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

private val ARGUMENTS = TokenSet.create(
    XQueryElementType.ARGUMENT,
    XQueryElementType.ARGUMENT_PLACEHOLDER)

class XQueryArgumentListPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryArgumentList, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        if (parent !is XQueryPostfixExpr) {
            return true
        }

        val minimalConformance = implementation.getVersion(XQuery)
        val marklogic = implementation.getVersion(MarkLogic)
        return minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0) || marklogic.supportsVersion(XQueryVersion.VERSION_6_0)
    }

    override fun getConformanceElement(): PsiElement =
        firstChild

    override fun getConformanceErrorMessage(): String =
        XQueryBundle.message("requires.feature.marklogic-xquery.version")

    override val arity get(): Int =
        children().filter { e -> ARGUMENTS.contains(e.node.elementType) }.count()
}
