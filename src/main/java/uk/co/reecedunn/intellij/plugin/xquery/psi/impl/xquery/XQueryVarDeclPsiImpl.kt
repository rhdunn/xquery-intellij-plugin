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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryVarDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryVarDecl, XQueryConformanceCheck, XQueryVariableResolver {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        if (conformanceElement === firstChild) {
            return true
        }
        return implementation.getVersion(XQuery).supportsVersion(XQueryVersion.VERSION_3_0) ||
               implementation.getVersion(MarkLogic).supportsVersion(XQueryVersion.VERSION_6_0)
    }

    override val conformanceElement get(): PsiElement {
        val element = findChildByType<PsiElement>(XQueryTokenType.ASSIGN_EQUAL)
        var previous: PsiElement? = element?.prevSibling
        while (previous != null && (previous.node.elementType === XQueryElementType.COMMENT || previous.node.elementType === XQueryTokenType.WHITE_SPACE)) {
            previous = previous.prevSibling
        }
        return if (previous == null || previous.node.elementType !== XQueryTokenType.K_EXTERNAL) firstChild else element!!
    }

    override val conformanceErrorMessage get(): String =
        XQueryBundle.message("requires.feature.marklogic-xquery.version")

    override fun resolveVariable(name: XQueryEQName?): XQueryVariable? {
        val varName = findChildByType<PsiElement>(XQueryElementType.VAR_NAME)
        if (varName != null && varName == name) {
            return XQueryVariable(varName, this)
        }
        return null
    }
}
