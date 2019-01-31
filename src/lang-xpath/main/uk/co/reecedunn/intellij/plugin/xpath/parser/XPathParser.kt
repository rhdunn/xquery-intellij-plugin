/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

/**
 * A unified XPath parser for different XPath versions and dialects.
 */
@Suppress("PropertyName", "PrivatePropertyName")
open class XPathParser : PsiParser {
    // region XPath/XQuery Element Types
    //
    // These element types have different PSI implementations in XPath and XQuery.

    open val ENCLOSED_EXPR: IElementType = XPathElementType.ENCLOSED_EXPR
    open val EXPR: IElementType = XPathElementType.EXPR
    open val FUNCTION_BODY: IElementType = XPathElementType.FUNCTION_BODY
    open val FUNCTION_TEST: IElementType = XPathElementType.FUNCTION_TEST

    // endregion
    // region PsiParser

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        parse(builder)
        rootMarker.done(root)
        return builder.treeBuilt
    }

    // endregion
    // region Grammar

    fun parse(builder: PsiBuilder) {
        var matched = false
        var haveError = false
        while (builder.tokenType != null) {
            if (matched && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-eof"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parse(builder, !matched && !haveError)) {
                matched = true
                continue
            }

            if (haveError) {
                builder.advanceLexer()
            } else if (builder.tokenType != null) {
                val errorMarker = builder.mark()
                builder.advanceLexer()
                errorMarker.error(XPathBundle.message("parser.error.unexpected-token"))
                haveError = true
            }
        }
    }

    open fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean {
        if (parseExpr(builder, null)) {
            return true
        }
        if (isFirst) {
            builder.error(XPathBundle.message("parser.error.expected-expression"))
        }
        return false
    }

    // endregion
    // region Grammar :: ParamList

    fun parseParamList(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        while (parseParam(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.VARIABLE_INDICATOR) {
                builder.error(XPathBundle.message("parser.error.expected", ","))
            } else if (!builder.matchTokenType(XPathTokenType.COMMA)) {
                builder.matchTokenType(XPathTokenType.ELLIPSIS)

                marker.done(XPathElementType.PARAM_LIST)
                return true
            }

            parseWhiteSpaceAndCommentTokens(builder)
        }

        marker.drop()
        return false
    }

    private fun parseParam(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, QNAME, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            marker.done(XPathElementType.PARAM)
            return true
        } else if (builder.tokenType === XPathTokenType.NCNAME || builder.tokenType is IKeywordOrNCNameType || builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            parseEQNameOrWildcard(builder, QNAME, false)

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            marker.done(XPathElementType.PARAM)
            return true
        }

        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: EnclosedExpr|Block

    enum class BlockOpen {
        REQUIRED,
        OPTIONAL
    }

    enum class BlockExpr {
        REQUIRED,
        OPTIONAL
    }

    open fun parseEnclosedExprOrBlock(
        builder: PsiBuilder,
        type: IElementType?,
        blockOpen: BlockOpen,
        blockExpr: BlockExpr
    ): Boolean {
        var haveErrors = false
        val marker = if (type == null) null else builder.mark()
        if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
            if (blockOpen == BlockOpen.OPTIONAL) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                haveErrors = true
            } else {
                marker?.drop()
                return false
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        var haveExpr = parseExpr(builder, EXPR)
        if (!haveExpr && blockExpr == BlockExpr.REQUIRED) {
            builder.error(XPathBundle.message("parser.error.expected-expression"))
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)) {
            haveExpr = true
        } else if (!haveErrors) {
            builder.error(XPathBundle.message("parser.error.expected", "}"))
        }

        if (marker != null) {
            if (haveExpr) {
                marker.done(type!!)
                return true
            }
            marker.drop()
        }
        return haveExpr
    }

    // endregion
    // region Grammar :: Expr

    open fun parseExpr(builder: PsiBuilder, type: IElementType?, functionDeclRecovery: Boolean = false): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (type == null)
                marker.drop()
            else
                marker.done(type)
            return true
        }
        marker.drop()
        return false
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseExprSingle(builder: PsiBuilder): Boolean {
        return (
            parseForExpr(builder) ||
            parseLetExpr(builder) ||
            parseQuantifiedExpr(builder) ||
            parseIfExpr(builder) ||
            parseOrExpr(builder, null)
        )
    }

    // endregion
    // region Grammar :: Expr :: ForExpr

    fun parseReturnClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K_RETURN)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.RETURN_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseForExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseForOrWindowClause(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseReturnClause(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                parseWhiteSpaceAndCommentTokens(builder)
                parseExprSingle(builder)
            }

            marker.done(XPathElementType.FOR_EXPR)
            return true
        } else if (
            builder.errorOnTokenType(XPathTokenType.K_RETURN, XPathBundle.message("parser.error.return-without-flwor"))
        ) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            return if (builder.tokenType !== XPathTokenType.PARENTHESIS_OPEN && parseExprSingle(builder)) {
                marker.drop()
                true
            } else {
                marker.rollbackTo()
                false
            }
        }
        marker.drop()
        return false
    }

    open fun parseForOrWindowClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (parseForClause(builder)) {
                marker.done(XPathElementType.SIMPLE_FOR_CLAUSE)
                true
            } else {
                marker.rollbackTo()
                false
            }
        }
        return false
    }

    fun parseForClause(builder: PsiBuilder): Boolean {
        if (parseForBinding(builder, true)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                parseForBinding(builder, false)
                parseWhiteSpaceAndCommentTokens(builder)
            }
            return true
        }
        return false
    }

    open fun parseForBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, XPathElementType.VAR_NAME, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "in"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.SIMPLE_FOR_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: LetExpr

    open val LET_CLAUSE: IElementType = XPathElementType.SIMPLE_LET_CLAUSE

    private fun parseLetExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseLetClause(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseReturnClause(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                parseWhiteSpaceAndCommentTokens(builder)
                parseExprSingle(builder)
            }

            marker.done(XPathElementType.LET_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    fun parseLetClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K_LET)) {
            var isFirst = true
            do {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseLetBinding(builder, isFirst) && isFirst) {
                    marker.rollbackTo()
                    return false
                }

                isFirst = false
                parseWhiteSpaceAndCommentTokens(builder)
            } while (builder.matchTokenType(XPathTokenType.COMMA))

            marker.done(LET_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    open fun parseLetBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, XPathElementType.VAR_NAME, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.errorOnTokenType(XPathTokenType.EQUAL, XPathBundle.message("parser.error.expected", ":="))) {
                haveErrors = true
            } else if (!builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ":="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.SIMPLE_LET_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: QuantifiedExpr

    fun parseQuantifiedExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.QUANTIFIED_EXPR_QUALIFIER_TOKENS)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            val hasBinding = parseQuantifiedExprBinding(builder, true)
            if (hasBinding) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    parseQuantifiedExprBinding(builder, false)
                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            var haveErrors = false
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_SATISFIES)) {
                if (hasBinding) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "satisfies"))
                    haveErrors = true
                } else { // NCName
                    marker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.QUANTIFIED_EXPR)
            return true
        }
        return false
    }

    open fun parseQuantifiedExprBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, XPathElementType.VAR_NAME, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "in"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.QUANTIFIED_EXPR_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    fun parseIfExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_IF)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExpr(builder, EXPR)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_THEN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "then"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_ELSE)) { // else branch is optional in BaseX 9.1
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                }
            }

            marker.done(XPathElementType.IF_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    fun parseOrExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseAndExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.OR_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseAndExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "AndExpr"))
                }
            }

            marker.done(XPathElementType.OR_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    open fun parseAndExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseComparisonExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveAndExpr = false
            while (builder.matchTokenType(XPathTokenType.AND_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseComparisonExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "ComparisonExpr"))
                }
                haveAndExpr = true
            }

            if (haveAndExpr)
                marker.done(XPathElementType.AND_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    open fun parseComparisonExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseStringConcatExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseGeneralComp(builder) || parseValueComp(builder) || parseNodeComp(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringConcatExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "StringConcatExpr"))
                }
                marker.done(XPathElementType.COMPARISON_EXPR)
            } else {
                marker.drop()
            }
            return true
        } else if (
            builder.errorOnTokenType(
                XPathTokenType.COMP_SYMBOL_TOKENS, XPathBundle.message("parser.error.comparison-no-lhs")
            )
        ) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringConcatExpr(builder, type)) {
                builder.error(XPathBundle.message("parser.error.expected", "StringConcatExpr"))
            }

            marker.done(XPathElementType.COMPARISON_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    fun parseStringConcatExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseRangeExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveStringConcatExpr = false
            while (builder.matchTokenType(XPathTokenType.CONCATENATION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseRangeExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "RangeExpr"))
                }
                parseWhiteSpaceAndCommentTokens(builder)
                haveStringConcatExpr = true
            }

            if (haveStringConcatExpr)
                marker.done(XPathElementType.STRING_CONCAT_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseRangeExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseAdditiveExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_TO)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseAdditiveExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                }
                marker.done(XPathElementType.RANGE_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    fun parseAdditiveExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseMultiplicativeExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveAdditativeExpr = false
            while (builder.matchTokenType(XPathTokenType.ADDITIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseMultiplicativeExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "MultiplicativeExpr"))
                }
                haveAdditativeExpr = true
            }

            if (haveAdditativeExpr)
                marker.done(XPathElementType.ADDITIVE_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseMultiplicativeExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseUnionExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveMultiplicativeExpr = false
            while (builder.matchTokenType(XPathTokenType.MULTIPLICATIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseUnionExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "UnionExpr"))
                }
                haveMultiplicativeExpr = true
            }

            if (haveMultiplicativeExpr)
                marker.done(XPathElementType.MULTIPLICATIVE_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    fun parseUnionExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseIntersectExceptExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveUnionExpr = false
            while (builder.matchTokenType(XPathTokenType.UNION_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseIntersectExceptExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "IntersectExceptExpr"))
                }
                haveUnionExpr = true
            }

            if (haveUnionExpr)
                marker.done(XPathElementType.UNION_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseIntersectExceptExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseInstanceofExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveIntersectExceptExpr = false
            while (builder.matchTokenType(XPathTokenType.INTERSECT_EXCEPT_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseInstanceofExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "InstanceofExpr"))
                }
                parseWhiteSpaceAndCommentTokens(builder)
                haveIntersectExceptExpr = true
            }

            if (haveIntersectExceptExpr)
                marker.done(XPathElementType.INTERSECT_EXCEPT_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseInstanceofExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseTreatExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.K_INSTANCE) -> {
                    var haveErrors = false

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.K_OF)) {
                        haveErrors = true
                        builder.error(XPathBundle.message("parser.error.expected-keyword", "of"))
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                    }
                    marker.done(XPathElementType.INSTANCEOF_EXPR)
                }
                builder.tokenType === XPathTokenType.K_OF -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "instance"))
                    builder.advanceLexer()

                    parseWhiteSpaceAndCommentTokens(builder)
                    parseSequenceType(builder)
                    marker.done(XPathElementType.INSTANCEOF_EXPR)
                }
                else -> marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    open fun parseTreatExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseCastableExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.K_TREAT) -> {
                    var haveErrors = false

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                        haveErrors = true
                        builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                    }
                    marker.done(XPathElementType.TREAT_EXPR)
                }
                builder.tokenType === XPathTokenType.K_AS -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "cast, castable, treat"))
                    builder.advanceLexer()

                    parseWhiteSpaceAndCommentTokens(builder)
                    parseSequenceType(builder)
                    marker.done(XPathElementType.TREAT_EXPR)
                }
                else -> marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    fun parseCastableExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseCastExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_CASTABLE)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSingleType(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "SingleType"))
                }
                marker.done(XPathElementType.CASTABLE_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    open fun parseCastExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseUnaryExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_CAST)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSingleType(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "SingleType"))
                }
                marker.done(XPathElementType.CAST_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    fun parseUnaryExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        var matched = false
        while (builder.matchTokenType(XPathTokenType.UNARY_EXPR_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            matched = true
        }
        if (matched) {
            if (parseValueExpr(builder, null)) {
                marker.done(XPathElementType.UNARY_EXPR)
                return true
            } else if (matched) {
                builder.error(XPathBundle.message("parser.error.expected", "ValueExpr"))
                marker.done(XPathElementType.UNARY_EXPR)
                return true
            }
        } else if (parseValueExpr(builder, type)) {
            marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    fun parseGeneralComp(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.GENERAL_COMP_TOKENS)
    }

    fun parseValueComp(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.VALUE_COMP_TOKENS)
    }

    fun parseNodeComp(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.NODE_COMP_TOKENS)
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: ValueExpr

    open fun parseValueExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return parseSimpleMapExpr(builder, type)
    }

    fun parseSimpleMapExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parsePathExpr(builder, type)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var haveSimpleMapExpr = false
            while (builder.matchTokenType(XPathTokenType.MAP_OPERATOR)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parsePathExpr(builder, null) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "PathExpr"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
                haveSimpleMapExpr = true
            }

            if (haveSimpleMapExpr)
                marker.done(XPathElementType.SIMPLE_MAP_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parsePathExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        when {
            builder.matchTokenType(XPathTokenType.DIRECT_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                parseRelativePathExpr(builder, null)

                marker.done(XPathElementType.PATH_EXPR)
                return true
            }
            builder.matchTokenType(XPathTokenType.ALL_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseRelativePathExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "RelativePathExpr"))
                }

                marker.done(XPathElementType.PATH_EXPR)
                return true
            }
            parseRelativePathExpr(builder, type) -> {
                marker.drop()
                return true
            }
            else -> {
                marker.drop()
                return false
            }
        }
    }

    private fun parseRelativePathExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseStepExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveRelativePathExpr = false
            while (builder.matchTokenType(XPathTokenType.RELATIVE_PATH_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStepExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "StepExpr"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                haveRelativePathExpr = true
            }

            if (haveRelativePathExpr)
                marker.done(XPathElementType.RELATIVE_PATH_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: StepExpr

    open fun parseStepExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return parseAxisStep(builder, type) || parsePostfixExpr(builder, type)
    }

    fun parseAxisStep(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseReverseStep(builder) || parseForwardStep(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parsePredicateList(builder))
                marker.done(XPathElementType.AXIS_STEP)
            else
                marker.drop()
            return true
        }

        marker.drop()
        return false
    }

    private fun parseForwardStep(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseForwardAxis(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseNodeTest(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected", "NodeTest"))
            }

            marker.done(XPathElementType.FORWARD_STEP)
            return true
        } else if (parseAbbrevForwardStep(builder, type)) {
            marker.drop()
            return true
        }

        marker.drop()
        return false
    }

    private fun parseForwardAxis(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.FORWARD_AXIS_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.AXIS_SEPARATOR)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XPathElementType.FORWARD_AXIS)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseAbbrevForwardStep(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        val matched = builder.matchTokenType(XPathTokenType.ATTRIBUTE_SELECTOR)

        parseWhiteSpaceAndCommentTokens(builder)
        if (parseNodeTest(builder, type)) {
            if (matched)
                marker.done(XPathElementType.ABBREV_FORWARD_STEP)
            else
                marker.drop()
            return true
        } else if (matched) {
            builder.error(XPathBundle.message("parser.error.expected", "NodeTest"))

            marker.done(XPathElementType.ABBREV_FORWARD_STEP)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseReverseStep(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseReverseAxis(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseNodeTest(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected", "NodeTest"))
            }

            marker.done(XPathElementType.REVERSE_STEP)
            return true
        } else if (parseAbbrevReverseStep(builder)) {
            marker.drop()
            return true
        }

        marker.drop()
        return false
    }

    private fun parseReverseAxis(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.REVERSE_AXIS_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.AXIS_SEPARATOR)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XPathElementType.REVERSE_AXIS)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseAbbrevReverseStep(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENT_SELECTOR)
        if (marker != null) {
            marker.done(XPathElementType.ABBREV_REVERSE_STEP)
            return true
        }
        return false
    }

    private fun parseNodeTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseKindTest(builder) || parseNameTest(builder, type)) {
            marker.done(XPathElementType.NODE_TEST)
            return true
        }
        marker.drop()
        return false
    }

    fun parseNameTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (
            parseEQNameOrWildcard(builder, XPathElementType.WILDCARD, type === XPathElementType.MAP_CONSTRUCTOR_ENTRY)
        ) {
            val nonNameMarker = builder.mark()
            parseWhiteSpaceAndCommentTokens(builder)
            val nextTokenType = builder.tokenType
            nonNameMarker.rollbackTo()

            if (
                nextTokenType === XPathTokenType.PARENTHESIS_OPEN ||
                nextTokenType === XPathTokenType.FUNCTION_REF_OPERATOR
            ) {
                marker.rollbackTo()
                return false
            }
            marker.done(XPathElementType.NAME_TEST)
            return true
        }
        marker.drop()
        return false
    }

    open fun parsePostfixExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parsePrimaryExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var havePostfixExpr = false
            while (parsePredicate(builder) || parseArgumentList(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                havePostfixExpr = true
            }

            if (havePostfixExpr)
                marker.done(XPathElementType.POSTFIX_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parsePredicateList(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var havePredicate = false
        while (parsePredicate(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            havePredicate = true
        }
        if (havePredicate)
            marker.done(XPathElementType.PREDICATE_LIST)
        else
            marker.drop()
        return havePredicate
    }

    fun parsePredicate(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.SQUARE_OPEN)
        if (marker != null) {
            var haveErrors = false
            parseWhiteSpaceAndCommentTokens(builder)

            if (!parseExpr(builder, EXPR)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.SQUARE_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "]"))
            }

            marker.done(XPathElementType.PREDICATE)
            return true
        }

        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parsePrimaryExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return (
            parseLiteral(builder) ||
            parseVarRef(builder, type) ||
            parseParenthesizedExpr(builder) ||
            parseContextItemExpr(builder) ||
            parseFunctionItemExpr(builder) ||
            parseFunctionCall(builder)
        )
    }

    fun parseLiteral(builder: PsiBuilder): Boolean {
        return parseNumericLiteral(builder) || parseStringLiteral(builder)
    }

    private fun parseNumericLiteral(builder: PsiBuilder): Boolean {
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

    fun parseVarRef(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (
                !parseEQNameOrWildcard(
                    builder, XPathElementType.VAR_NAME,
                    type === XPathElementType.MAP_CONSTRUCTOR_ENTRY
                )
            ) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            marker.done(XPathElementType.VAR_REF)
            return true
        }
        return false
    }

    fun parseParenthesizedExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseExpr(builder, EXPR)) {
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.PARENTHESIZED_EXPR)
            return true
        }
        return false
    }

    private fun parseContextItemExpr(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.DOT)
    }

    open fun parseFunctionCall(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseEQNameOrWildcard(builder, QNAME, false)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseArgumentList(builder)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XPathElementType.FUNCTION_CALL)
            return true
        }
        marker.drop()
        return false
    }

    fun parseArgumentList(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseArgument(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseArgument(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-either", "ExprSingle", "?"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ARGUMENT_LIST)
            return true
        }
        return false
    }

    private fun parseArgument(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            marker.done(XPathElementType.ARGUMENT)
            return true
        }
        marker.drop()
        return parseArgumentPlaceholder(builder)
    }

    private fun parseArgumentPlaceholder(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.OPTIONAL)
        if (marker != null) {
            marker.done(XPathElementType.ARGUMENT_PLACEHOLDER)
            return true
        }
        return false
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseFunctionItemExpr(builder: PsiBuilder): Boolean {
        return (
            parseNamedFunctionRef(builder) ||
            parseInlineFunctionExpr(builder)
        )
    }

    fun parseNamedFunctionRef(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseEQNameOrWildcard(builder, QNAME, false)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.FUNCTION_REF_OPERATOR)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
            }

            marker.done(XPathElementType.NAMED_FUNCTION_REF)
            return true
        }

        marker.drop()
        return false
    }

    open fun parseInlineFunctionExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K_FUNCTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseParamList(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSequenceType(builder)) {
                    builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, FUNCTION_BODY, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                parseExpr(builder, EXPR)

                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)
            }

            marker.done(XPathElementType.INLINE_FUNCTION_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration

    fun parseTypeDeclaration(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_AS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceType(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: SequenceType

    open fun parseSequenceType(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.SEQUENCE_TYPE_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.SEQUENCE_TYPE)
            return true
        } else if (parseItemType(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseOccurrenceIndicator(builder))
                marker.done(XPathElementType.SEQUENCE_TYPE)
            else
                marker.drop()
            return true
        }

        marker.drop()
        return false
    }

    fun parseOccurrenceIndicator(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.OCCURRENCE_INDICATOR_TOKENS)
    }

    fun parseAtomicOrUnionType(builder: PsiBuilder): Boolean {
        return parseEQNameOrWildcard(builder, XPathElementType.ATOMIC_OR_UNION_TYPE, false)
    }

    fun parseSingleType(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseEQNameOrWildcard(builder, XPathElementType.SIMPLE_TYPE_NAME, false)) {
            parseWhiteSpaceAndCommentTokens(builder)
            builder.matchTokenType(XPathTokenType.OPTIONAL)

            marker.done(XPathElementType.SINGLE_TYPE)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: ItemType

    enum class KindTest {
        ANY_TEST,
        TYPED_TEST,
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseItemType(builder: PsiBuilder): Boolean {
        return (
            parseKindTest(builder) ||
            parseAnyItemType(builder) ||
            parseAnnotatedFunctionOrSequence(builder) ||
            parseAtomicOrUnionType(builder) ||
            parseParenthesizedItemType(builder)
        )
    }

    fun parseAnyItemType(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ITEM)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ANY_ITEM_TYPE)
            return true
        }
        return false
    }

    open fun parseAnnotatedFunctionOrSequence(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseAnyOrTypedFunctionTest(builder)) {
            marker.done(FUNCTION_TEST)
            return true
        }
        marker.drop()
        return false
    }

    fun parseAnyOrTypedFunctionTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FUNCTION)
        if (marker != null) {
            var type: KindTest = KindTest.ANY_TEST
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.STAR)) {
                //
            } else if (parseSequenceType(builder)) {
                type = KindTest.TYPED_TEST

                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }
            } else {
                type = KindTest.TYPED_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.K_AS) {
                if (type === KindTest.ANY_TEST && !haveErrors) {
                    val errorMarker = builder.mark()
                    builder.advanceLexer()
                    errorMarker.error(XPathBundle.message("parser.error.as-not-supported-in-test", "AnyFunctionTest"))
                    haveErrors = true
                } else {
                    builder.advanceLexer()
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSequenceType(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                }
            } else if (type === KindTest.TYPED_TEST) {
                builder.error(XPathBundle.message("parser.error.expected", "as"))
            }

            marker.drop()
            return true
        }
        return false
    }

    fun parseParenthesizedItemType(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceType(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                if (
                    builder.tokenType === XPathTokenType.UNION ||
                    builder.tokenType === XPathTokenType.COMMA
                ) {
                    marker.rollbackTo() // parenthesized sequence type (XQuery Formal Semantics)
                    return false
                }
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.PARENTHESIZED_ITEM_TYPE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    private val ATTRIBUTE_DECLARATION: IElementType get() = QNAME
    private val ATTRIBUTE_NAME: IElementType get() = QNAME

    private val ELEMENT_DECLARATION: IElementType get() = QNAME
    private val ELEMENT_NAME: IElementType get() = QNAME

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseKindTest(builder: PsiBuilder): Boolean {
        return (
            parseDocumentTest(builder) ||
            parseElementTest(builder) ||
            parseAttributeTest(builder) ||
            parseSchemaElementTest(builder) ||
            parseSchemaAttributeTest(builder) ||
            parsePITest(builder) ||
            parseCommentTest(builder) ||
            parseTextTest(builder) ||
            parseNamespaceNodeTest(builder) ||
            parseAnyKindTest(builder)
        )
    }

    open fun parseAnyKindTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ANY_KIND_TEST)
            return true
        }
        return false
    }

    open fun parseDocumentTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DOCUMENT_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                parseElementTest(builder) ||
                parseSchemaElementTest(builder)
            ) {
                //
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.DOCUMENT_TEST)
            return true
        }
        return false
    }

    open fun parseTextTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TEXT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ANY_TEXT_TEST)
            return true
        }
        return false
    }

    private fun parseCommentTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_COMMENT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.COMMENT_TEST)
            return true
        }
        return false
    }

    private fun parseNamespaceNodeTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NAMESPACE_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.NAMESPACE_NODE_TEST)
            return true
        }
        return false
    }

    private fun parsePITest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_PROCESSING_INSTRUCTION)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameOrWildcard(builder, NCNAME) || parseStringLiteral(builder)) {
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.PI_TEST)
            return true
        }
        return false
    }

    open fun parseAttributeTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ATTRIBUTE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseAttribNameOrWildcard(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)) {
                        builder.error(XPathBundle.message("parser.error.expected-eqname"))
                        haveErrors = true
                    }
                } else if (builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE) {
                    builder.error(XPathBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ATTRIBUTE_TEST)
            return true
        }
        return false
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    fun parseAttribNameOrWildcard(builder: PsiBuilder): Boolean {
        return (
            builder.matchTokenType(XPathTokenType.STAR) ||
            parseEQNameOrWildcard(builder, ATTRIBUTE_NAME, false)
        )
    }

    private fun parseSchemaAttributeTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_SCHEMA_ATTRIBUTE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, ATTRIBUTE_DECLARATION, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.SCHEMA_ATTRIBUTE_TEST)
            return true
        }
        return false
    }

    open fun parseElementTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ELEMENT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseElementNameOrWildcard(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)) {
                        builder.error(XPathBundle.message("parser.error.expected-eqname"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    builder.matchTokenType(XPathTokenType.OPTIONAL)
                } else if (builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE) {
                    builder.error(XPathBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ELEMENT_TEST)
            return true
        }
        return false
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    fun parseElementNameOrWildcard(builder: PsiBuilder): Boolean {
        return (
            builder.matchTokenType(XPathTokenType.STAR) ||
            parseEQNameOrWildcard(builder, ELEMENT_NAME, false)
        )
    }

    fun parseSchemaElementTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_SCHEMA_ELEMENT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEQNameOrWildcard(builder, ELEMENT_DECLARATION, false)) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.SCHEMA_ELEMENT_TEST)
            return true
        }
        return false
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols

    open val STRING_LITERAL: IElementType = XPathElementType.STRING_LITERAL

    fun parseStringLiteral(builder: PsiBuilder): Boolean {
        return parseStringLiteral(builder, STRING_LITERAL)
    }

    open fun parseStringLiteral(builder: PsiBuilder, type: IElementType): Boolean {
        val stringMarker = builder.matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        while (stringMarker != null) {
            if (
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER)
            ) {
                //
            } else if (builder.matchTokenType(XPathTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type)
                return true
            } else {
                stringMarker.done(type)
                builder.error(XPathBundle.message("parser.error.incomplete-string"))
                return true
            }
        }
        return false
    }

    fun parseComment(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.COMMENT_START_TAG) {
            val commentMarker = builder.mark()
            builder.advanceLexer()
            // NOTE: XQueryTokenType.COMMENT is omitted by the PsiBuilder.
            if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
                builder.advanceLexer()
                commentMarker.done(XPathElementType.COMMENT)
            } else {
                builder.advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(XPathElementType.COMMENT)
                builder.error(XPathBundle.message("parser.error.incomplete-comment"))
            }
            return true
        } else if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
            val errorMarker = builder.mark()
            builder.advanceLexer()
            errorMarker.error(XPathBundle.message("parser.error.end-of-comment-without-start", "(:"))
            return true
        }
        return false
    }

    open fun parseWhiteSpaceAndCommentTokens(builder: PsiBuilder): Boolean {
        var skipped = false
        while (true) {
            when {
                builder.tokenType === XPathTokenType.WHITE_SPACE -> {
                    skipped = true
                    builder.advanceLexer()
                }
                parseComment(builder) -> skipped = true
                else -> return skipped
            }
        }
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: EQName

    open val URI_QUALIFIED_NAME: IElementType = XPathElementType.URI_QUALIFIED_NAME
    open val BRACED_URI_LITERAL: IElementType = XPathElementType.BRACED_URI_LITERAL

    fun parseEQNameOrWildcard(builder: PsiBuilder, type: IElementType, endQNameOnSpace: Boolean): Boolean {
        val marker = builder.mark()
        if (
            parseQNameOrWildcard(builder, type, endQNameOnSpace) ||
            parseURIQualifiedNameOrWildcard(builder, type)
        ) {
            if (type === QNAME || type === NCNAME || type === XPathElementType.WILDCARD) {
                marker.drop()
            } else {
                marker.done(type)
            }
            return true
        }
        marker.drop()
        return false
    }

    private fun parseURIQualifiedNameOrWildcard(builder: PsiBuilder, type: IElementType): Boolean {
        val marker = builder.mark()
        if (parseBracedURILiteral(builder)) {
            val localName = parseQNameNCName(builder, QNamePart.URIQualifiedLiteralLocalName, type, false)
            if (type === XPathElementType.WILDCARD && localName === XPathTokenType.STAR) {
                marker.done(XPathElementType.WILDCARD)
            } else {
                marker.done(URI_QUALIFIED_NAME)
            }
            return true
        }
        marker.drop()
        return false
    }

    open fun parseBracedURILiteral(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.BRACED_URI_LITERAL_START)
        while (marker != null) {
            when {
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) -> {
                    //
                }
                builder.matchTokenType(XPathTokenType.BRACED_URI_LITERAL_END) -> {
                    marker.done(BRACED_URI_LITERAL)
                    return true
                }
                else -> {
                    marker.done(BRACED_URI_LITERAL)
                    builder.error(XPathBundle.message("parser.error.incomplete-braced-uri-literal"))
                    return true
                }
            }
        }
        return false
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: QName

    open val NCNAME: IElementType = XPathElementType.NCNAME
    open val QNAME: IElementType = XPathElementType.QNAME

    open fun parseQNameOrWildcard(
        builder: PsiBuilder,
        type: IElementType,
        endQNameOnSpace: Boolean = false
    ): Boolean {
        val marker = builder.mark()
        val prefix = parseQNameNCName(builder, QNamePart.Prefix, type, false)
        if (prefix != null) {
            if (parseQNameWhitespace(builder, QNamePart.Prefix, endQNameOnSpace, prefix === XPathTokenType.STAR)) {
                if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                    marker.done(XPathElementType.WILDCARD)
                } else {
                    marker.done(NCNAME)
                }
                return true
            }

            val nameMarker = builder.mark()
            if (parseQNameSeparator(builder, type)) {
                if (
                    parseQNameWhitespace(builder, QNamePart.LocalName, endQNameOnSpace, prefix === XPathTokenType.STAR)
                ) {
                    nameMarker.rollbackTo()
                    if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                        marker.done(XPathElementType.WILDCARD)
                    } else {
                        marker.done(NCNAME)
                    }
                    return true
                }
                nameMarker.drop()

                val localName = parseQNameNCName(builder, QNamePart.LocalName, type, prefix == XPathTokenType.STAR)
                if (
                    type === XPathElementType.WILDCARD &&
                    (prefix === XPathTokenType.STAR || localName === XPathTokenType.STAR)
                ) {
                    marker.done(XPathElementType.WILDCARD)
                } else {
                    marker.done(QNAME)
                }
            } else if (type === XPathElementType.WILDCARD && prefix == XPathTokenType.STAR) {
                nameMarker.drop()
                marker.done(XPathElementType.WILDCARD)
            } else {
                nameMarker.drop()
                marker.done(NCNAME)
            }
            return true
        }

        if (parseQNameSeparator(builder, null)) { // Missing prefix
            builder.advanceLexer()
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType is INCNameType || builder.tokenType == XPathTokenType.STAR) {
                builder.advanceLexer()
            }
            if (type === NCNAME) {
                marker.error(XPathBundle.message("parser.error.expected-ncname-not-qname"))
            } else {
                marker.error(XPathBundle.message("parser.error.qname.missing-prefix"))
            }
            return true
        }

        marker.drop()
        return false
    }

    enum class QNamePart {
        Prefix,
        LocalName,
        URIQualifiedLiteralLocalName
    }

    private fun parseQNameNCName(
        builder: PsiBuilder,
        partType: QNamePart,
        elementType: IElementType,
        isWildcard: Boolean
    ): IElementType? {
        val tokenType = builder.tokenType
        if (tokenType is INCNameType) {
            builder.advanceLexer()
            return tokenType
        } else if (tokenType == XPathTokenType.STAR) {
            if (elementType === XPathElementType.WILDCARD) {
                if (isWildcard) {
                    builder.error(XPathBundle.message("parser.error.wildcard.both-prefix-and-local-wildcard"))
                }
            } else if (partType === QNamePart.Prefix) {
                builder.error(XPathBundle.message("parser.error.unexpected-wildcard"))
            } else if (partType === QNamePart.URIQualifiedLiteralLocalName) {
                builder.error(XPathBundle.message("parser.error.eqname.wildcard-local-name"))
            } else {
                builder.error(XPathBundle.message("parser.error.qname.wildcard-local-name"))
            }
            builder.advanceLexer()
            return tokenType
        } else if (tokenType == XPathTokenType.INTEGER_LITERAL && partType == QNamePart.LocalName) {
            // The user has started the local name with a number, so treat it as part of the QName.
            val errorMarker = builder.mark()
            builder.advanceLexer()
            errorMarker.error(XPathBundle.message("parser.error.qname.missing-local-name"))
        } else if (partType == QNamePart.LocalName) {
            // Don't consume the next token with an error, as it may be a valid part of the next construct
            // (e.g. the start of a string literal, or the '>' of a direct element constructor).
            builder.error(XPathBundle.message("parser.error.qname.missing-local-name"))
        } else if (partType === QNamePart.URIQualifiedLiteralLocalName) {
            // Don't consume the next token with an error, as it may be a valid part of the next construct
            // (e.g. the start of a string literal, or the '>' of a direct element constructor).
            builder.error(XPathBundle.message("parser.error.eqname.missing-local-name"))
        }
        return null
    }

    private fun parseQNameWhitespace(
        builder: PsiBuilder,
        type: QNamePart,
        endQNameOnWhitespace: Boolean,
        isWildcard: Boolean
    ): Boolean {
        val marker = builder.mark()
        if (parseWhiteSpaceAndCommentTokens(builder)) {
            if (endQNameOnWhitespace) {
                marker.drop()
                return true
            } else if (type == QNamePart.Prefix && parseQNameSeparator(builder, null)) {
                if (isWildcard)
                    marker.error(XPathBundle.message("parser.error.wildcard.whitespace-before-local-part"))
                else
                    marker.error(XPathBundle.message("parser.error.qname.whitespace-before-local-part"))
                return false
            } else if (type == QNamePart.LocalName) {
                if (isWildcard)
                    marker.error(XPathBundle.message("parser.error.wildcard.whitespace-after-local-part"))
                else
                    marker.error(XPathBundle.message("parser.error.qname.whitespace-after-local-part"))
                return false
            }
        }
        marker.drop()
        return false
    }

    open fun parseQNameSeparator(builder: PsiBuilder, type: IElementType?): Boolean {
        if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
            if (type != null) {
                builder.advanceLexer()
            }
            return true
        }
        return false
    }

    // endregion
}
