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
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryIntegerLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNamedFunctionRef
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryNamedFunctionRefPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryNamedFunctionRef, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        val minimalConformance = implementation.getVersion(XQuery)
        val marklogic = implementation.getVersion(MarkLogic)
        return minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0) || marklogic.supportsVersion(XQueryVersion.VERSION_6_0)
    }

    override val conformanceElement get(): PsiElement =
        findChildByType(XQueryTokenType.FUNCTION_REF_OPERATOR) ?: this

    override val conformanceErrorMessage get(): String =
        XQueryBundle.message("requires.feature.marklogic-xquery.version")

    override val functionName: XQueryEQName? =
        findChildByClass(XQueryEQName::class.java)

    override val arity get(): Int =
        children().filterIsInstance<XQueryIntegerLiteral>().firstOrNull()?.atomicValue ?: 0
}
