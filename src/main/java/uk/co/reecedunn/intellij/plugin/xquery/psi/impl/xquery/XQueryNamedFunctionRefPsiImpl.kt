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
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryNamedFunctionRefPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryNamedFunctionRef, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        val type = conformanceElement?.node?.elementType
        if (type is IXQueryKeywordOrNCNameType) {
            when (type.keywordType) {
                IXQueryKeywordOrNCNameType.KeywordType.KEYWORD -> {}
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

        val minimalConformance = implementation.getVersion(XQuery)
        val marklogic = implementation.getVersion(MarkLogic)
        return minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0) || marklogic.supportsVersion(XQueryVersion.VERSION_6_0)
    }

    override fun getConformanceElement(): PsiElement? {
        val name = findChildByClass(XQueryEQName::class.java)
        if (name?.node?.elementType === XQueryElementType.NCNAME) {
            val ncname = name?.firstChild
            val type = ncname?.node?.elementType
            if (type is IXQueryKeywordOrNCNameType) {
                when (type.keywordType) {
                    IXQueryKeywordOrNCNameType.KeywordType.KEYWORD -> {}
                    else -> return ncname
                }
            }
        }
        return findChildByType(XQueryTokenType.FUNCTION_REF_OPERATOR)
    }

    override fun getConformanceErrorMessage(): String {
        val type = conformanceElement?.node?.elementType
        if (type is IXQueryKeywordOrNCNameType) {
            when (type.keywordType) {
                IXQueryKeywordOrNCNameType.KeywordType.KEYWORD -> {}
                else -> return XQueryBundle.message("requires.error.reserved-keyword-as-function-name")
            }
        }
        return XQueryBundle.message("requires.feature.marklogic-xquery.version")
    }

    override val arity get(): Int =
        children().filterIsInstance<XQueryIntegerLiteral>().firstOrNull()?.atomicValue ?: 0
}
