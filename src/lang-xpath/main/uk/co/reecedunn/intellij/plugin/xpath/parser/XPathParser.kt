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
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

/**
 * A unified XPath parser for different XPath versions and dialects.
 */
open class XPathParser : PsiParser {
    // region PsiParser

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        parse(builder)
        rootMarker.done(root)
        return builder.treeBuilt
    }

    // endregion
    // region Main Interface

    fun parse(builder: PsiBuilder) {
        var matched = false
        var haveError = false
        while (builder.tokenType != null) {
            if (matched && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-eof"))
                haveError = true
            }

            if (parse(builder, !matched && !haveError)) {
                matched = true
                continue
            }

            if (haveError) {
                builder.advanceLexer()
            } else {
                val errorMarker = builder.mark()
                builder.advanceLexer()
                errorMarker.error(XPathBundle.message("parser.error.unexpected-token"))
                haveError = true
            }
        }
    }

    open fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean {
        return parseOrExpr(builder)
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private fun parseOrExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseNumericLiteral(builder)) {
            marker.done(XPathElementType.OR_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr

    fun parseNumericLiteral(builder: PsiBuilder): Boolean {
        if (
            builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) ||
            builder.matchTokenType(XPathTokenType.DOUBLE_LITERAL)
        ) {
            return true
        } else if (builder.matchTokenType(XPathTokenType.DECIMAL_LITERAL)) {
            builder.errorOnTokenType(
                XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                XPathBundle.message("parser.error.incomplete-double-exponent")
            )
            return true
        }
        return false
    }

    // endregion
}
