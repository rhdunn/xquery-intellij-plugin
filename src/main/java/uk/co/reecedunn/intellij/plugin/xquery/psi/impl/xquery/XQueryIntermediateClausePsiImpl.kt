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
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryInitialClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryIntermediateClause
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryIntermediateClausePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryIntermediateClause, XQueryConformanceCheck, XQueryVariableResolver {
    private val requiredXQueryVersion get(): XQueryVersion {
        val current = firstChild.node.elementType
        if (current === XQueryElementType.COUNT_CLAUSE || current === XQueryElementType.GROUP_BY_CLAUSE) {
            return XQueryVersion.VERSION_3_0
        }

        val prevElement = prevSibling
        val prev = if (prevElement is XQueryInitialClause) null else prevElement.firstChild.node.elementType
        if (prev === XQueryElementType.WHERE_CLAUSE) {
            return if (current === XQueryElementType.ORDER_BY_CLAUSE) XQueryVersion.VERSION_1_0 else XQueryVersion.VERSION_3_0
        } else if (prev === XQueryElementType.ORDER_BY_CLAUSE) {
            return XQueryVersion.VERSION_3_0
        }
        return XQueryVersion.VERSION_1_0
    }

    override fun conformsTo(implementation: ImplementationItem): Boolean =
        implementation.getVersion(XQuery).supportsVersion(requiredXQueryVersion)

    override val conformanceElement get(): PsiElement =
        firstChild.firstChild

    override val conformanceErrorMessage get(): String =
        XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_3_0)

    override fun resolveVariable(name: XQueryEQName?): XQueryVariable? {
        val element = firstChild
        if (element is XQueryVariableResolver) {
            return element.resolveVariable(name)
        }
        return null
    }
}
