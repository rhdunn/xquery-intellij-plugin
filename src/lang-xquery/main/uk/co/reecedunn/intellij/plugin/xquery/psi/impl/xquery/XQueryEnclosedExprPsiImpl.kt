/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsNotElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathCurlyArrayConstructor
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

open class XQueryEnclosedExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathEnclosedExpr,
    VersionConformance {
    // region VersionConformance

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun previousVersionSupportsOptionalExpr(parent: PsiElement): Boolean {
        return (
            parent is XQueryCompPIConstructor ||
            parent is XQueryCompAttrConstructor ||
            parent is XQueryExtensionExpr ||
            parent is XPathCurlyArrayConstructor
        )
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun marklogicSupportsOptionalExpr(parent: PsiElement): Boolean {
        return (
            parent is XQueryCompTextConstructor ||
            parent is XQueryDirAttributeValue ||
            parent is XQueryDirElemConstructor ||
            parent is XQueryCatchClause
        )
    }

    override val requiresConformance: List<Version>
        get() {
            val parent = parent
            if (previousVersionSupportsOptionalExpr(parent) || conformanceElement !== firstChild) {
                return XQUERY10
            }
            if (marklogicSupportsOptionalExpr(parent)) {
                return MARKLOGIC60
            }
            return XQUERY31
        }

    override val conformanceElement: PsiElement
        get() = children().filterIsNotElementType(IGNORE_TOKENS).firstOrNull() ?: firstChild

    companion object {
        private val XQUERY10: List<Version> = listOf()
        private val XQUERY31: List<Version> = listOf(XQuerySpec.REC_3_1_20170321)
        private val MARKLOGIC60: List<Version> = listOf(XQuerySpec.REC_3_1_20170321, MarkLogic.VERSION_6_0)

        private val IGNORE_TOKENS = TokenSet.create(
            XPathTokenType.WHITE_SPACE,
            XPathElementType.COMMENT,
            XPathTokenType.BLOCK_OPEN,
            XPathTokenType.BLOCK_CLOSE
        )
    }

    // endregion
    // region FoldablePsiElement

    override val foldingRange: TextRange?
        get() = textRange

    override val foldingPlaceholderText: String? = "{...}"

    // endregion
}
