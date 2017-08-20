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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryParamList
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryFunctionDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryFunctionDecl, XQueryConformanceCheck, XQueryVariableResolver {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        val element = conformanceElement
        if (element === firstChild) {
            return true
        }

        val type = element.node.elementType
        if (type is IXQueryKeywordOrNCNameType) {
            when (type.keywordType) {
                IXQueryKeywordOrNCNameType.KeywordType.KEYWORD -> return true
                IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME -> return false
                IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                    val scripting = implementation.getVersion(Scripting)
                    return !scripting.supportsVersion(XQueryVersion.VERSION_1_0)
                }
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME -> {
                    val marklogicVersion = implementation.getVersion(MarkLogic)
                    return !marklogicVersion.supportsVersion(XQueryVersion.VERSION_7_0)
                }
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    val marklogicVersion = implementation.getVersion(MarkLogic)
                    return !marklogicVersion.supportsVersion(XQueryVersion.VERSION_8_0)
                }
                IXQueryKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME -> {
                    val marklogic = implementation.getVersion(MarkLogic)
                    val xquery = implementation.getVersion(XQuery)
                    return !xquery.supportsVersion(XQueryVersion.VERSION_3_0) && !marklogic.supportsVersion(XQueryVersion.VERSION_6_0)
                }
            }
        }
        return true
    }

    override fun getConformanceElement(): PsiElement {
        val name = findChildByClass(XQueryEQName::class.java)
        if (name == null) {
            return firstChild
        } else if (name.node.elementType === XQueryElementType.NCNAME) {
            return name.firstChild
        }
        return name
    }

    override fun getConformanceErrorMessage(): String =
        XQueryBundle.message("requires.error.reserved-keyword-as-function-name")

    override val arity get(): Int =
        children().filterIsInstance<XQueryParamList>().firstOrNull()?.arity ?: 0

    override fun resolveVariable(name: XQueryEQName?): XQueryVariable? {
        val element = findChildByType<PsiElement>(XQueryElementType.PARAM_LIST)
        return if (element is XQueryVariableResolver) element.resolveVariable(name) else null
    }
}
