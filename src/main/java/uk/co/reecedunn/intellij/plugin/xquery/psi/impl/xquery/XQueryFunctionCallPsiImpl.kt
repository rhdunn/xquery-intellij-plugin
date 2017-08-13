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
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryArgumentList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionCall
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryFunctionCallPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryFunctionCall, XQueryConformanceCheck {
    private val localName get(): Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType>? {
        val name = firstChild
        if (name.node.elementType === XQueryElementType.NCNAME) {
            val localname = name.firstChild
            val type = localname.node.elementType
            if (type is IXQueryKeywordOrNCNameType) {
                return Pair(localname, type.keywordType)
            }
        }
        return null
    }

    override fun conformsTo(implementation: ImplementationItem): Boolean {
        val localname = localName
        if (localname != null) {
            when (localname.second) {
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME -> {
                    val marklogicVersion = implementation.getVersion(XQueryConformance.MARKLOGIC)
                    return !marklogicVersion.supportsVersion(XQueryVersion.VERSION_7_0)
                }
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    val marklogic = implementation.getVersion(XQueryConformance.MARKLOGIC)
                    return !marklogic.supportsVersion(XQueryVersion.VERSION_8_0)
                }
                IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                    val scripting = implementation.getVersion(XQueryConformance.SCRIPTING)
                    return !scripting.supportsVersion(XQueryVersion.VERSION_1_0)
                }
                else -> {}
            }
        }
        return true
    }

    override fun getConformanceElement(): PsiElement {
        val localname = localName
        if (localname != null) {
            when (localname.second) {
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME,
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME,
                IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME ->
                    return localname.first
                else -> {}
            }
        }
        return firstChild
    }

    override fun getConformanceErrorMessage(): String {
        val localname = localName
        if (localname != null) {
            when (localname.second) {
                IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME ->
                    return XQueryBundle.message("requires.error.scripting-keyword-as-function-name", XQueryVersion.VERSION_1_0)
                else -> {}
            }
        }
        return XQueryBundle.message("requires.error.marklogic-json-keyword-as-function-name", XQueryVersion.VERSION_8_0)
    }

    override val arity get(): Int =
        children().filterIsInstance<XQueryArgumentList>().first().arity
}
