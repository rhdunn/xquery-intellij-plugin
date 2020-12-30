/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathBundle
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
            if (this.parseEQNameOrWildcard(builder, QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            marker.done(XPathElementType.PARAM)
            return true
        } else if (builder.tokenType === XPathTokenType.NCNAME || builder.tokenType is IKeywordOrNCNameType || builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            this.parseEQNameOrWildcard(builder, QNAME, false)

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
        OPTIONAL,
        CONTEXT_FUNCTION,
        LAMBDA_FUNCTION
    }

    enum class BlockExpr {
        REQUIRED,
        OPTIONAL
    }

    fun parseEnclosedExprOrBlock(
        builder: PsiBuilder,
        type: IElementType?,
        blockOpen: BlockOpen,
        blockExpr: BlockExpr
    ): Boolean {
        var haveErrors = false
        val marker = if (type == null) null else builder.mark()
        val openToken = when (blockOpen) {
            BlockOpen.CONTEXT_FUNCTION -> XPathTokenType.CONTEXT_FUNCTION
            BlockOpen.LAMBDA_FUNCTION -> XPathTokenType.LAMBDA_FUNCTION
            else -> XPathTokenType.BLOCK_OPEN
        }
        if (!builder.matchTokenType(openToken)) {
            if (blockOpen == BlockOpen.OPTIONAL) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                haveErrors = true
            } else {
                marker?.drop()
                return false
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        var haveExpr = parseEnclosedExprOrBlockExpr(builder, type)
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

    open fun parseEnclosedExprOrBlockExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return parseExpr(builder, EXPR)
    }

    // endregion
    // region Grammar :: Expr

    open fun parseExpr(builder: PsiBuilder, type: IElementType?, functionDeclRecovery: Boolean = false): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var multipleExprSingles = false
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder)) {
                    if (!haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-expression"))
                        haveErrors = true
                    }
                } else {
                    multipleExprSingles = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (type == null || !multipleExprSingles)
                marker.drop()
            else
                marker.done(type)
            return true
        }
        marker.drop()
        return false
    }

    fun parseExprSingle(builder: PsiBuilder): Boolean = parseExprSingleImpl(builder, null)

    fun parseExprSingle(builder: PsiBuilder, type: IElementType?, parentType: IElementType? = null): Boolean {
        if (type == null) {
            return parseExprSingleImpl(builder, parentType)
        }

        val marker = builder.mark()
        if (parseExprSingleImpl(builder, parentType)) {
            marker.done(type)
            return true
        }

        marker.drop()
        return false
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseExprSingleImpl(builder: PsiBuilder, parentType: IElementType?): Boolean {
        return (
            parseForExpr(builder) ||
            parseLetExpr(builder) ||
            parseQuantifiedExpr(builder) ||
            parseIfExpr(builder) ||
            parseTernaryIfExpr(builder, parentType)
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
        val match = parseForOrWindowClause(builder)
        if (match != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseReturnClause(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                parseWhiteSpaceAndCommentTokens(builder)
                parseExprSingle(builder)
            }

            if (match === FOR_MEMBER_CLAUSE) {
                marker.done(XPathElementType.FOR_MEMBER_EXPR)
            } else {
                marker.done(XPathElementType.FOR_EXPR)
            }
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

    open fun parseForOrWindowClause(builder: PsiBuilder): IElementType? {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return when {
                parseForClause(builder) -> {
                    marker.done(XPathElementType.SIMPLE_FOR_CLAUSE)
                    XPathElementType.SIMPLE_FOR_CLAUSE
                }
                parseForMemberClause(builder, marker) -> FOR_MEMBER_CLAUSE
                else -> {
                    marker.rollbackTo()
                    null
                }
            }
        }
        return null
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
            if (this.parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val haveScoreVar = parseFTScoreVar(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                if (haveScoreVar) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "in"))
                } else {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "in, score"))
                }
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

    fun parseFTScoreVar(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_SCORE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            marker.done(XPathElementType.FT_SCORE_VAR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: ForMemberExpr

    open val FOR_MEMBER_CLAUSE: IElementType = XPathElementType.SIMPLE_FOR_MEMBER_CLAUSE

    fun parseForMemberClause(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_MEMBER)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseForClause(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "ForBinding"))
                marker.drop()
                return true
            }
            marker.done(FOR_MEMBER_CLAUSE)
            return true
        }
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
            if (this.parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
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
            if (this.parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
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
    // region Grammar :: Expr :: TernaryIfExpr

    fun parseTernaryIfExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseElvisExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.TERNARY_IF)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseElvisExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "ElvisExpr"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.TERNARY_ELSE)) {
                    builder.error(XPathBundle.message("parser.error.expected", "!!"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseElvisExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "ElvisExpr"))
                }

                marker.done(XPathElementType.TERNARY_IF_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    private fun parseElvisExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseOrExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.ELVIS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseOrExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "OrExpr"))
                    marker.drop()
                } else {
                    marker.done(XPathElementType.ELVIS_EXPR)
                }
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private fun parseOrExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseAndExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveOrExpr = false
            while (builder.matchTokenType(XPathTokenType.OR_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseAndExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "AndExpr"))
                } else {
                    haveOrExpr = true
                }
            }

            if (haveOrExpr)
                marker.done(XPathElementType.OR_EXPR)
            else
                marker.drop()
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
                } else {
                    haveAndExpr = true
                }
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
        if (parseFTContainsExpr(builder, type)) {
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
            if (!parseFTContainsExpr(builder, type)) {
                builder.error(XPathBundle.message("parser.error.expected", "StringConcatExpr"))
            }

            marker.done(XPathElementType.COMPARISON_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    fun parseFTContainsExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseStringConcatExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)

            if (builder.matchTokenType(XPathTokenType.K_CONTAINS)) {
                var haveError = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_TEXT)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "text"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTSelection(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTSelection"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                parseFTIgnoreOption(builder)
                marker.done(XPathElementType.FT_CONTAINS_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTIgnoreOption(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_WITHOUT)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_CONTENT)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "content"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseUnionExpr(builder, XPathElementType.FT_IGNORE_OPTION) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", "UnionExpr"))
            }

            marker.done(XPathElementType.FT_IGNORE_OPTION)
            return true
        }
        return false
    }

    private fun parseStringConcatExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseRangeExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveStringConcatExpr = false
            while (builder.matchTokenType(XPathTokenType.CONCATENATION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseRangeExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "RangeExpr"))
                } else {
                    haveStringConcatExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
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
                    marker.drop()
                } else {
                    marker.done(XPathElementType.RANGE_EXPR)
                }
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    private fun parseAdditiveExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseMultiplicativeExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveAdditativeExpr = false
            while (builder.matchTokenType(XPathTokenType.ADDITIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseMultiplicativeExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "MultiplicativeExpr"))
                } else {
                    haveAdditativeExpr = true
                }
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
        if (parseOtherwiseExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveMultiplicativeExpr = false
            while (builder.matchTokenType(XPathTokenType.MULTIPLICATIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseOtherwiseExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "OtherwiseExpr"))
                } else {
                    haveMultiplicativeExpr = true
                }
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

    private fun parseOtherwiseExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseUnionExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveOtherwiseExpr = false
            while (builder.matchTokenType(XPathTokenType.K_OTHERWISE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseUnionExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "OtherwiseExpr"))
                } else {
                    haveOtherwiseExpr = true
                }
            }

            if (haveOtherwiseExpr)
                marker.done(XPathElementType.OTHERWISE_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseUnionExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseIntersectExceptExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveUnionExpr = false
            while (builder.matchTokenType(XPathTokenType.UNION_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseIntersectExceptExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "IntersectExceptExpr"))
                } else {
                    haveUnionExpr = true
                }
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
                } else {
                    haveIntersectExceptExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
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
                    marker.drop()
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
                    marker.drop()
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
        if (parseArrowExpr(builder, type)) {
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

    private fun parseUnaryExpr(builder: PsiBuilder, type: IElementType?): Boolean {
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

    fun parseGeneralComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.GENERAL_COMP_TOKENS)

    fun parseValueComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.VALUE_COMP_TOKENS)

    fun parseNodeComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.NODE_COMP_TOKENS)

    fun parseArrowExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseUnaryExpr(builder, type)) {
            var haveErrors = false
            var haveArrowExpr = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.ARROW)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseArrowFunctionCall(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "ArrowFunctionSpecifier"))
                    haveErrors = true
                } else {
                    haveArrowExpr = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (haveArrowExpr)
                marker.done(XPathElementType.ARROW_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseArrowFunctionCall(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        val elementType = when {
            this.parseEQNameOrWildcard(builder, QNAME, false) != null -> XPathElementType.ARROW_FUNCTION_CALL
            parseVarOrParamRef(builder, null) -> XPathElementType.ARROW_DYNAMIC_FUNCTION_CALL
            parseParenthesizedExpr(builder) -> XPathElementType.ARROW_DYNAMIC_FUNCTION_CALL
            else -> {
                marker.drop()
                return false
            }
        }
        parseWhiteSpaceAndCommentTokens(builder)
        if (!parseArgumentList(builder)) {
            builder.error(XPathBundle.message("parser.error.expected", "ArgumentList"))
            marker.drop()
        } else {
            marker.done(elementType)
        }
        return true
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: ValueExpr

    open fun parseValueExpr(builder: PsiBuilder, type: IElementType?): Boolean = parseSimpleMapExpr(builder, type)

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
                } else {
                    haveSimpleMapExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
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
        return when {
            builder.matchTokenType(XPathTokenType.DIRECT_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                parseRelativePathExpr(builder, null, marker, XPathTokenType.DIRECT_DESCENDANTS_PATH)
                true
            }
            builder.matchTokenType(XPathTokenType.ALL_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseRelativePathExpr(builder, null, marker, XPathTokenType.ALL_DESCENDANTS_PATH)) {
                    builder.error(XPathBundle.message("parser.error.expected", "RelativePathExpr"))
                }
                true
            }
            else -> parseRelativePathExpr(builder, type, marker, null)
        }
    }

    private fun parseRelativePathExpr(
        builder: PsiBuilder,
        type: IElementType?,
        marker: PsiBuilder.Marker,
        pathExprStartToken: IElementType?
    ): Boolean {
        val step = parseStepExpr(builder, type)
        return when {
            step !== ParsedStepExpr.None -> {
                parseWhiteSpaceAndCommentTokens(builder)
                var haveRelativePathExpr = step === ParsedStepExpr.Step
                while (builder.matchTokenType(XPathTokenType.RELATIVE_PATH_EXPR_TOKENS)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (parseStepExpr(builder, XPathElementType.PATH_EXPR) === ParsedStepExpr.None) {
                        builder.error(XPathBundle.message("parser.error.expected", "StepExpr"))
                    } else {
                        haveRelativePathExpr = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }

                if (haveRelativePathExpr || pathExprStartToken != null)
                    marker.done(XPathElementType.PATH_EXPR)
                else
                    marker.drop()
                true
            }
            pathExprStartToken === XPathTokenType.DIRECT_DESCENDANTS_PATH -> {
                marker.done(XPathElementType.PATH_EXPR)
                true
            }
            else -> {
                marker.drop()
                false
            }
        }
    }

    fun parsePragma(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PRAGMA_BEGIN)
        if (marker != null) {
            var haveErrors = false

            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
            if (this.parseEQNameOrWildcard(builder, QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            // NOTE: The XQuery grammar requires pragma contents if the EQName
            // is followed by a space token, but implementations make it optional.
            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
            builder.matchTokenType(XPathTokenType.PRAGMA_CONTENTS)

            if (!builder.matchTokenType(XPathTokenType.PRAGMA_END) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "#)"))
            }

            marker.done(XPathElementType.PRAGMA)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: StepExpr

    enum class ParsedStepExpr {
        Step,
        Expression,
        None
    }

    open fun parseStepExpr(builder: PsiBuilder, type: IElementType?): ParsedStepExpr = when {
        parseAxisStep(builder, type) -> ParsedStepExpr.Step
        parsePostfixExpr(builder, type) -> ParsedStepExpr.Expression
        else -> ParsedStepExpr.None
    }

    fun parseAxisStep(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseReverseStep(builder) || parseForwardStep(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            parsePredicateList(builder, marker, recover = false)
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
                marker.drop()
            } else {
                marker.done(XPathElementType.FORWARD_STEP)
            }
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
            marker.drop()
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
        }
        marker.drop()
        return matched
    }

    private fun parseReverseStep(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseReverseAxis(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseNodeTest(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected", "NodeTest"))
                marker.drop()
            } else {
                marker.done(XPathElementType.REVERSE_STEP)
            }
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
            marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseAbbrevReverseStep(builder: PsiBuilder): Boolean {
        return builder.matchTokenType(XPathTokenType.PARENT_SELECTOR)
    }

    private fun parseNodeTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseKindTest(builder)) {
            marker.done(XPathElementType.NODE_TEST)
            return true
        }

        marker.drop()
        return parseNameTest(builder, type, allowAxisStep = true) != null
    }

    fun parseNameTest(builder: PsiBuilder, type: IElementType?, allowAxisStep: Boolean): IElementType? {
        val isElementOrAttributeTest =
            type === XPathElementType.ELEMENT_TEST || type === XPathElementType.ATTRIBUTE_TEST
        val marker = builder.mark()
        if (
            this.parseEQNameOrWildcard(
                builder, XPathElementType.WILDCARD, type === XPathElementType.MAP_CONSTRUCTOR_ENTRY,
                isElementOrAttributeName = isElementOrAttributeTest
            ) != null
        ) {
            val nonNameMarker = builder.mark()
            parseWhiteSpaceAndCommentTokens(builder)
            val nextTokenType = builder.tokenType
            nonNameMarker.rollbackTo()

            if (
                (type !== XPathElementType.NAME_TEST && nextTokenType === XPathTokenType.BLOCK_OPEN) ||
                nextTokenType === XPathTokenType.PARENTHESIS_OPEN ||
                nextTokenType === XPathTokenType.FUNCTION_REF_OPERATOR
            ) {
                marker.rollbackTo()
                return null
            } else if (
                builder.errorOnTokenType(
                    XPathTokenType.AXIS_SEPARATOR,
                    if (allowAxisStep)
                        XPathBundle.message("parser.error.invalid-axis")
                    else
                        XPathBundle.message("parser.error.invalid-axis-in-nametest")
                )
            ) {
                parseWhiteSpaceAndCommentTokens(builder)
                return if (allowAxisStep) {
                    parseNodeTest(builder, null)

                    parseWhiteSpaceAndCommentTokens(builder)
                    parsePredicateList(builder, marker, recover = true)
                    XPathElementType.AXIS_STEP
                } else {
                    parseNCName(builder) // QName written with '::' instead of ':'.

                    marker.done(XPathElementType.NAME_TEST)
                    XPathElementType.NAME_TEST
                }
            } else if (isElementOrAttributeTest) {
                marker.drop()
                return XPathElementType.NAME_TEST
            } else {
                marker.done(XPathElementType.NAME_TEST)
                return XPathElementType.NAME_TEST
            }
        }
        marker.drop()
        return null
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PostfixExpr

    fun parsePostfixExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        var marker = builder.mark()
        if (parsePrimaryExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)

            var havePostfixExpr = false
            while (true) {
                when {
                    parsePredicate(builder) -> {
                        parseWhiteSpaceAndCommentTokens(builder)

                        marker.done(XPathElementType.FILTER_EXPR)
                        marker = marker.precede()

                        // Keep PostfixExpr if there is a filter expression.
                        havePostfixExpr = true
                    }
                    parseArgumentList(builder) -> {
                        parseWhiteSpaceAndCommentTokens(builder)

                        marker.done(XPathElementType.DYNAMIC_FUNCTION_CALL)
                        marker = marker.precede()

                        // Keep PostfixExpr if there is a dynamic function call.
                        havePostfixExpr = true
                    }
                    parseLookup(builder, XPathElementType.LOOKUP) -> {
                        parseWhiteSpaceAndCommentTokens(builder)

                        marker.done(XPathElementType.POSTFIX_LOOKUP)
                        marker = marker.precede()

                        // Keep PostfixExpr if there is a postfix lookup.
                        havePostfixExpr = true
                    }
                    havePostfixExpr -> {
                        marker.drop()
                        return true
                    }
                    type === XPathElementType.PATH_EXPR -> {
                        // Keep PostfixExpr if the PrimaryExpr is in a non-initial StepExpr.
                        marker.done(XPathElementType.POSTFIX_EXPR)
                        return true
                    }
                    XPathTokenType.RELATIVE_PATH_EXPR_TOKENS.contains(builder.tokenType) -> {
                        // Keep PostfixExpr if a StepExpr follows the PrimaryExpr (i.e. this is the first step).
                        marker.done(XPathElementType.POSTFIX_EXPR)
                        return true
                    }
                    else -> {
                        marker.drop()
                        return true
                    }
                }
            }
        }
        marker.drop()
        return false
    }

    private fun parsePredicateList(builder: PsiBuilder, marker: PsiBuilder.Marker, recover: Boolean): Boolean {
        var inner = marker
        var havePredicate = false
        while (parsePredicate(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            inner.done(XPathElementType.FILTER_STEP)
            inner = inner.precede()
            havePredicate = true
        }

        if (recover && !havePredicate)
            inner.done(XPathElementType.AXIS_STEP)
        else
            inner.drop()
        return havePredicate
    }

    private fun parsePredicate(builder: PsiBuilder): Boolean {
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

            if (haveErrors)
                marker.drop()
            else
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
            parseVarOrParamRef(builder, type) ||
            parseParenthesizedExpr(builder) ||
            parseFunctionItemExpr(builder) ||
            parseFunctionCall(builder) ||
            parseMapConstructor(builder) ||
            parseArrayConstructor(builder) ||
            parseContextItemExpr(builder) ||
            parseLookup(builder, XPathElementType.UNARY_LOOKUP)
        )
    }

    fun parseLiteral(builder: PsiBuilder): Boolean {
        if (parseNumericLiteral(builder)) {
            if (
                builder.tokenType is IKeywordOrNCNameType ||
                builder.tokenType === XPathTokenType.BRACED_URI_LITERAL_START
            ) {
                builder.error(XPathBundle.message("parser.error.consecutive-non-delimiting-terminals"))
            }
            return true
        }
        return parseStringLiteral(builder)
    }

    private fun parseNumericLiteral(builder: PsiBuilder): Boolean {
        when {
            builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) -> {
                builder.errorOnTokenType(
                    XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                    XPathBundle.message("parser.error.incomplete-double-exponent")
                )
            }
            builder.matchTokenType(XPathTokenType.DOUBLE_LITERAL) -> {
            }
            builder.matchTokenType(XPathTokenType.DECIMAL_LITERAL) -> {
                builder.errorOnTokenType(
                    XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                    XPathBundle.message("parser.error.incomplete-double-exponent")
                )
            }
            else -> return false
        }
        builder.errorOnTokenType(
            XPathTokenType.NCNAME,
            XPathBundle.message("parser.error.ncname-following-number")
        )
        return true
    }

    fun parseVarOrParamRef(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            val eqnameType = this.parseEQNameOrWildcard(
                builder, XPathElementType.VAR_REF,
                type === XPathElementType.MAP_CONSTRUCTOR_ENTRY
            )
            when (eqnameType) {
                null -> {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    marker.drop()
                }
                XPathElementType.PARAM_REF -> marker.done(XPathElementType.PARAM_REF)
                else -> marker.done(XPathElementType.VAR_REF)
            }
            return true
        }
        return false
    }

    private fun parseParenthesizedExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            val haveExpr = parseExpr(builder, EXPR)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            if (haveExpr)
                marker.drop()
            else
                marker.done(XPathElementType.EMPTY_EXPR)
            return true
        }
        return false
    }

    private fun parseContextItemExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.DOT)
        if (marker != null) {
            val whitespace = builder.mark()
            val haveWhitespace = parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.BLOCK_OPEN && haveWhitespace) {
                whitespace.error(XPathBundle.message("parser.error.context-function.whitespace-between-dot-and-brace"))
            } else {
                whitespace.drop()
            }

            if (parseContextItemFunctionExpr(builder, marker)) {
                // NOTE: marker is consumed by parseContextItemFunctionExpr.
            } else {
                marker.drop()
            }
            return true
        }
        return false
    }

    private fun parseLookup(builder: PsiBuilder, type: IElementType): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.OPTIONAL)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseKeySpecifier(builder)) {
                if (type === XPathElementType.UNARY_LOOKUP) {
                    // NOTE: This conflicts with '?' used as an ArgumentPlaceholder, so don't match '?' only as UnaryLookup.
                    marker.rollbackTo()
                    return false
                } else {
                    builder.error(XPathBundle.message("parser.error.expected", "KeySpecifier"))
                }
            }

            marker.done(type)
            return true
        }
        return false
    }

    private fun parseKeySpecifier(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (
            builder.matchTokenType(XPathTokenType.STAR) ||
            builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) ||
            this.parseEQNameOrWildcard(builder, NCNAME, false) != null ||
            parseParenthesizedExpr(builder)
        ) {
            marker.done(XPathElementType.KEY_SPECIFIER)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: FunctionCall

    open fun parseFunctionCall(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (this.parseEQNameOrWildcard(builder, QNAME, false) != null) {
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
        return parseExprSingle(builder) || parseArgumentPlaceholder(builder)
    }

    private fun parseArgumentPlaceholder(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.OPTIONAL)
        if (marker != null) {
            marker.done(XPathElementType.ARGUMENT_PLACEHOLDER)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: FunctionItemExpr

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun parseFunctionItemExpr(builder: PsiBuilder): Boolean {
        return (
            parseNamedFunctionRef(builder) ||
            parseInlineFunctionExpr(builder) ||
            parseContextItemFunctionExpr(builder, null) ||
            parseLambdaFunctionExpr(builder)
        )
    }

    private fun parseNamedFunctionRef(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (this.parseEQNameOrWildcard(builder, QNAME, false) != null) {
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
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFunctionSignature(builder)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, FUNCTION_BODY, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
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

    fun parseFunctionSignature(builder: PsiBuilder): Boolean {
        val matched = builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)
        if (!matched) {
            builder.error(XPathBundle.message("parser.error.expected", "("))
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parseParamList(builder)

        parseWhiteSpaceAndCommentTokens(builder)
        if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
            builder.error(XPathBundle.message("parser.error.expected", ")"))
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parseTypeDeclaration(builder)

        return matched
    }

    private fun parseContextItemFunctionExpr(builder: PsiBuilder, contextItem: PsiBuilder.Marker?): Boolean {
        val marker = contextItem ?: builder.mark()
        if (contextItem != null || builder.matchTokenType(XPathTokenType.K_FN)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (marker !== contextItem) {
                    marker.rollbackTo()
                }
                return false
            }

            marker.done(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR)
            return true
        } else if (parseEnclosedExprOrBlock(builder, null, BlockOpen.CONTEXT_FUNCTION, BlockExpr.REQUIRED)) {
            marker.done(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseLambdaFunctionExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K__)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XPathElementType.LAMBDA_FUNCTION_EXPR)
            return true
        } else if (parseEnclosedExprOrBlock(builder, null, BlockOpen.LAMBDA_FUNCTION, BlockExpr.REQUIRED)) {
            marker.done(XPathElementType.LAMBDA_FUNCTION_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: MapConstructor

    private fun parseMapConstructor(builder: PsiBuilder): Boolean {
        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_MAP)
        if (marker == null) {
            marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_OBJECT_NODE)
        }

        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseMapConstructorEntry(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseMapConstructorEntry(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "MapConstructor"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "}"))
            }

            marker.done(XPathElementType.MAP_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseMapConstructorEntry(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder, XPathElementType.MAP_KEY_EXPR, XPathElementType.MAP_CONSTRUCTOR_ENTRY)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.MAP_ENTRY_SEPARATOR_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-map-entry-assign"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder, XPathElementType.MAP_VALUE_EXPR) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.MAP_CONSTRUCTOR_ENTRY)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: ArrayConstructor

    private fun parseArrayConstructor(builder: PsiBuilder): Boolean {
        return parseSquareArrayConstructor(builder) || parseCurlyArrayConstructor(builder)
    }

    private fun parseSquareArrayConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.SQUARE_OPEN)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseExprSingle(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseExprSingle(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-expression"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.SQUARE_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "]"))
            }

            marker.done(XPathElementType.SQUARE_ARRAY_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCurlyArrayConstructor(builder: PsiBuilder): Boolean {
        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ARRAY)
        if (marker == null) {
            marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ARRAY_NODE)
        }

        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XPathElementType.CURLY_ARRAY_CONSTRUCTOR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTSelection

    private fun parseFTSelection(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTOr(builder)) {
            do {
                parseWhiteSpaceAndCommentTokens(builder)
            } while (parseFTPosFilter(builder))

            marker.done(XPathElementType.FT_SELECTION)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTOr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTAnd(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var haveFTOr = false
            while (builder.matchTokenType(XPathTokenType.K_FTOR)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTAnd(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTAnd"))
                    haveErrors = true
                }
                haveFTOr = true
            }

            if (haveFTOr)
                marker.done(XPathElementType.FT_OR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTAnd(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTMildNot(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var haveFTMildNot = false
            while (builder.matchTokenType(XPathTokenType.K_FTAND)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTMildNot(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTMildNot"))
                    haveErrors = true
                }
                haveFTMildNot = true
            }

            if (haveFTMildNot)
                marker.done(XPathElementType.FT_AND)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTMildNot(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTUnaryNot(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var haveFTUnaryNot = false
            while (builder.matchTokenType(XPathTokenType.K_NOT)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "in"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTUnaryNot(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTUnaryNot"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                haveFTUnaryNot = true
            }

            if (haveFTUnaryNot)
                marker.done(XPathElementType.FT_MILD_NOT)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTUnaryNot(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        val haveFTNot = builder.matchTokenType(XPathTokenType.K_FTNOT)

        parseWhiteSpaceAndCommentTokens(builder)
        if (parseFTPrimaryWithOptions(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)

            if (haveFTNot)
                marker.done(XPathElementType.FT_UNARY_NOT)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTPrimaryWithOptions(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTPrimary(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveOptions = parseFTMatchOptions(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseFTWeight(builder)) {
                haveOptions = true
            }

            if (haveOptions)
                marker.done(XPathElementType.FT_PRIMARY_WITH_OPTIONS)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTPrimary(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        when {
            parseFTWords(builder) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseFTTimes(builder))
                    marker.done(XPathElementType.FT_PRIMARY)
                else
                    marker.drop()
                return true
            }
            builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN) -> {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTSelection(builder)) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTSelection"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", ")"))
                }

                marker.done(XPathElementType.FT_PRIMARY)
                return true
            }
            parseFTExtensionSelection(builder) -> {
                marker.drop()
                return true
            }
            else -> {
                marker.drop()
                return false
            }
        }
    }

    private fun parseFTExtensionSelection(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var haveError = false

        var havePragma = false
        while (parsePragma(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            havePragma = true
        }

        if (!havePragma) {
            marker.drop()
            return false
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
            builder.error(XPathBundle.message("parser.error.expected", "{"))
            haveError = true
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parseFTSelection(builder)

        parseWhiteSpaceAndCommentTokens(builder)
        if (!builder.matchTokenType(XPathTokenType.BLOCK_CLOSE) && !haveError) {
            builder.error(XPathBundle.message("parser.error.expected", "}"))
        }

        marker.done(XPathElementType.FT_EXTENSION_SELECTION)
        return true
    }

    private fun parseFTWords(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFTWordsValue(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseFTAnyallOption(builder))
                marker.done(XPathElementType.FT_WORDS)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTWordsValue(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseStringLiteral(builder)) {
            marker.done(XPathElementType.FT_WORDS_VALUE)
            return true
        } else if (builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExpr(builder, EXPR)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "}"))
            }

            marker.done(XPathElementType.FT_WORDS_VALUE)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFTAnyallOption(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        when {
            builder.matchTokenType(XPathTokenType.K_ANY) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.K_WORD)

                marker.done(XPathElementType.FT_ANYALL_OPTION)
                return true
            }
            builder.matchTokenType(XPathTokenType.K_ALL) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.K_WORDS)

                marker.done(XPathElementType.FT_ANYALL_OPTION)
                return true
            }
            builder.matchTokenType(XPathTokenType.K_PHRASE) -> {
                marker.done(XPathElementType.FT_ANYALL_OPTION)
                return true
            }
            else -> {
                marker.drop()
                return false
            }
        }
    }

    private fun parseFTTimes(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_OCCURS)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTRange(builder, XPathElementType.FT_RANGE)) {
                builder.error(XPathBundle.message("parser.error.expected", "FTRange"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_TIMES) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "times"))
            }

            marker.done(XPathElementType.FT_TIMES)
            return true
        }
        return false
    }

    private fun parseFTRange(builder: PsiBuilder, type: IElementType): Boolean {
        when {
            builder.tokenType === XPathTokenType.K_EXACTLY -> {
                val marker = builder.mark()
                builder.advanceLexer()

                parseWhiteSpaceAndCommentTokens(builder)
                if (type === XPathElementType.FT_LITERAL_RANGE) {
                    if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                        builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
                    }
                } else {
                    if (!parseAdditiveExpr(builder, type)) {
                        builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                    }
                }

                marker.done(type)
                return true
            }
            builder.tokenType === XPathTokenType.K_AT -> {
                val marker = builder.mark()
                builder.advanceLexer()

                var haveError = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.FTRANGE_AT_QUALIFIER_TOKENS)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "least, most"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (type === XPathElementType.FT_LITERAL_RANGE) {
                    if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
                    }
                } else {
                    if (!parseAdditiveExpr(builder, type) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                    }
                }

                marker.done(type)
                return true
            }
            builder.tokenType === XPathTokenType.K_FROM -> {
                val marker = builder.mark()
                builder.advanceLexer()

                var haveError = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (type === XPathElementType.FT_LITERAL_RANGE) {
                    if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                        builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
                        haveError = true
                    }
                } else {
                    if (!parseAdditiveExpr(builder, type)) {
                        builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                        haveError = true
                    }
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_TO) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "to"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (type === XPathElementType.FT_LITERAL_RANGE) {
                    if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
                    }
                } else {
                    if (!parseAdditiveExpr(builder, type) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                    }
                }

                marker.done(type)
                return true
            }
            else -> return false
        }
    }

    private fun parseFTWeight(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_WEIGHT)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExpr(builder, EXPR) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", "}"))
            }

            marker.done(XPathElementType.FT_WEIGHT)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTPosFilter

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun parseFTPosFilter(builder: PsiBuilder): Boolean {
        return (
            parseFTOrder(builder) ||
            parseFTWindow(builder) ||
            parseFTDistance(builder) ||
            parseFTScope(builder) ||
            parseFTContent(builder)
        )
    }

    private fun parseFTOrder(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ORDERED)
        if (marker != null) {
            marker.done(XPathElementType.FT_ORDER)
            return true
        }
        return false
    }

    private fun parseFTWindow(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_WINDOW)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseAdditiveExpr(builder, XPathElementType.FT_WINDOW)) {
                builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTUnit(builder) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "paragraphs, sentences, words"))
            }

            marker.done(XPathElementType.FT_WINDOW)
            return true
        }
        return false
    }

    private fun parseFTDistance(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DISTANCE)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTRange(builder, XPathElementType.FT_RANGE)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "at, exactly, from"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTUnit(builder) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "paragraphs, sentences, words"))
            }

            marker.done(XPathElementType.FT_DISTANCE)
            return true
        }
        return false
    }

    private fun parseFTUnit(builder: PsiBuilder): Boolean {
        if (
            builder.tokenType === XPathTokenType.K_WORDS ||
            builder.tokenType === XPathTokenType.K_SENTENCES ||
            builder.tokenType === XPathTokenType.K_PARAGRAPHS
        ) {
            val marker = builder.mark()
            builder.advanceLexer()
            marker.done(XPathElementType.FT_UNIT)
            return true
        }
        return false
    }

    private fun parseFTScope(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.FTSCOPE_QUALIFIER_TOKENS)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTBigUnit(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "paragraph, sentence"))
            }

            marker.done(XPathElementType.FT_SCOPE)
            return true
        }
        return false
    }

    private fun parseFTBigUnit(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.K_SENTENCE || builder.tokenType === XPathTokenType.K_PARAGRAPH) {
            val marker = builder.mark()
            builder.advanceLexer()
            marker.done(XPathElementType.FT_BIG_UNIT)
            return true
        }
        return false
    }

    private fun parseFTContent(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.K_AT) {
            val marker = builder.mark()
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.FTCONTENT_AT_QUALIFIER_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "end, start"))
            }

            marker.done(XPathElementType.FT_CONTENT)
            return true
        } else if (builder.tokenType === XPathTokenType.K_ENTIRE) {
            val marker = builder.mark()
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_CONTENT)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "content"))
            }

            marker.done(XPathElementType.FT_CONTENT)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTMatchOptions

    open val FTMATCH_OPTION_START_TOKENS: TokenSet = XPathTokenType.FTMATCH_OPTION_START_TOKENS
    open val URI_LITERAL: IElementType = XPathElementType.URI_LITERAL

    fun parseFTMatchOptions(builder: PsiBuilder): Boolean {
        var haveFTMatchOptions = false
        var haveFTMatchOption: Boolean
        do {
            haveFTMatchOption = when {
                builder.matchTokenType(XPathTokenType.K_USING) -> {
                    parseWhiteSpaceAndCommentTokens(builder)
                    parseFTMatchOption(builder)
                    true
                }
                FTMATCH_OPTION_START_TOKENS.contains(builder.tokenType) -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "using"))
                    parseFTMatchOption(builder)
                    true
                }
                else -> false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            haveFTMatchOptions = haveFTMatchOptions or haveFTMatchOption
        } while (haveFTMatchOption)

        return haveFTMatchOptions
    }

    open fun parseFTMatchOption(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (
            parseFTCaseOption(builder, marker) ||
            parseFTDiacriticsOption(builder, marker) ||
            parseFTExtensionOption(builder, marker) ||
            parseFTLanguageOption(builder, marker) ||
            parseFTStemOption(builder, marker) ||
            parseFTStopWordOption(builder, marker) ||
            parseFTThesaurusOption(builder, marker) ||
            parseFTWildCardOption(builder, marker)
        ) {
            return false
        } else if (builder.matchTokenType(XPathTokenType.K_NO)) {
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.K_STEMMING) -> marker.done(XPathElementType.FT_STEM_OPTION)
                builder.matchTokenType(XPathTokenType.K_STOP) -> {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.K_WORDS)) {
                        builder.error(XPathBundle.message("parser.error.expected-keyword", "words"))
                    }
                    marker.done(XPathElementType.FT_STOP_WORD_OPTION)
                }
                builder.matchTokenType(XPathTokenType.K_THESAURUS) -> marker.done(XPathElementType.FT_THESAURUS_OPTION)
                builder.matchTokenType(XPathTokenType.K_WILDCARDS) -> marker.done(XPathElementType.FT_WILDCARD_OPTION)
                else -> {
                    builder.error(
                        XPathBundle.message("parser.error.expected-keyword", "stemming, stop, thesaurus, wildcards")
                    )
                    marker.drop()
                    return false
                }
            }
        } else {
            builder.error(XPathBundle.message("parser.error.expected", "FTMatchOption"))
            marker.drop()
            return false
        }
        return false
    }

    fun parseFTCaseOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_LOWERCASE) || builder.matchTokenType(XPathTokenType.K_UPPERCASE)) {
            marker.done(XPathElementType.FT_CASE_OPTION)
            return true
        } else if (builder.matchTokenType(XPathTokenType.K_CASE)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.FTCASE_SENSITIVITY_QUALIFIER_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "sensitive, insensitive"))
            }

            marker.done(XPathElementType.FT_CASE_OPTION)
            return true
        }
        return false
    }

    fun parseFTDiacriticsOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_DIACRITICS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.FTDIACRITICS_SENSITIVITY_QUALIFIER_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "sensitive, insensitive"))
            }

            marker.done(XPathElementType.FT_DIACRITICS_OPTION)
            return true
        }
        return false
    }

    fun parseFTStemOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_STEMMING)) {
            marker.done(XPathElementType.FT_STEM_OPTION)
            return true
        }
        return false
    }

    fun parseFTThesaurusOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_THESAURUS)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            val hasParenthesis = builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_DEFAULT) && !parseFTThesaurusID(builder)) {
                if (hasParenthesis) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "at, default"))
                } else {
                    builder.error(XPathBundle.message("parser.error.expected-keyword-or-token", "(", "at, default"))
                }
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            var haveComma: Boolean
            if (hasParenthesis) {
                haveComma = builder.matchTokenType(XPathTokenType.COMMA)
            } else {
                haveComma = builder.errorOnTokenType(
                    XPathTokenType.COMMA,
                    XPathBundle.message("parser.error.full-text.multientry-thesaurus-requires-parenthesis")
                )
                haveError = haveError or haveComma
            }

            while (haveComma) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTThesaurusID(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "at"))

                    builder.matchTokenType(XPathTokenType.K_DEFAULT)
                    parseWhiteSpaceAndCommentTokens(builder)

                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                haveComma = builder.matchTokenType(XPathTokenType.COMMA)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (hasParenthesis) {
                if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-either", ",", ")"))
                }
            } else if (!haveError) {
                builder.errorOnTokenType(
                    XPathTokenType.PARENTHESIS_CLOSE,
                    XPathBundle.message("parser.error.expected-keyword-or-token", ";", "using")
                )
            } else {
                builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)
            }

            marker.done(XPathElementType.FT_THESAURUS_OPTION)
            return true
        }
        return false
    }

    private fun parseFTThesaurusID(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_AT)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, URI_LITERAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "URILiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_RELATIONSHIP)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                    haveError = true
                }
            }

            if (parseFTRange(builder, XPathElementType.FT_LITERAL_RANGE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_LEVELS) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "levels"))
                }
            }

            marker.done(XPathElementType.FT_THESAURUS_ID)
            return true
        }
        return false
    }

    fun parseFTStopWordOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_STOP)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_WORDS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "words"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_DEFAULT) && !parseFTStopWords(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword-or-token", "(", "at, default"))
            }

            do {
                parseWhiteSpaceAndCommentTokens(builder)
            } while (parseFTStopWordsInclExcl(builder))

            marker.done(XPathElementType.FT_STOP_WORD_OPTION)
            return true
        }
        return false
    }

    private fun parseFTStopWords(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.K_AT) {
            val marker = builder.mark()
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, URI_LITERAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "URILiteral"))
            }

            marker.done(XPathElementType.FT_STOP_WORDS)
            return true
        } else if (builder.tokenType === XPathTokenType.PARENTHESIS_OPEN) {
            val marker = builder.mark()
            builder.advanceLexer()

            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-either", ",", ")"))
            }

            marker.done(XPathElementType.FT_STOP_WORDS)
            return true
        }
        return false
    }

    private fun parseFTStopWordsInclExcl(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.FTSTOP_WORDS_INCL_EXCL_QUALIFIER_TOKENS)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTStopWords(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword-or-token", "(", "at"))
            }

            marker.done(XPathElementType.FT_STOP_WORDS_INCL_EXCL)
            return true
        }
        return false
    }

    fun parseFTLanguageOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_LANGUAGE)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
            }

            marker.done(XPathElementType.FT_LANGUAGE_OPTION)
            return true
        }
        return false
    }

    fun parseFTWildCardOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_WILDCARDS)) {
            marker.done(XPathElementType.FT_WILDCARD_OPTION)
            return true
        }
        return false
    }

    fun parseFTExtensionOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_OPTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
            }

            marker.done(XPathElementType.FT_EXTENSION_OPTION)
            return true
        }
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

    fun parseSequenceTypeList(builder: PsiBuilder, alwaysIncludeNode: Boolean = false): Boolean {
        val marker = builder.mark()
        if (parseSequenceType(builder)) {
            var haveErrors = false
            var haveSequenceTypeList = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                haveSequenceTypeList = true
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSequenceType(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (haveSequenceTypeList || alwaysIncludeNode)
                marker.done(XPathElementType.SEQUENCE_TYPE_LIST)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

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

            marker.done(XPathElementType.EMPTY_SEQUENCE_TYPE)
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

    private fun parseAtomicOrUnionType(builder: PsiBuilder): Boolean {
        return this.parseEQNameOrWildcard(builder, XPathElementType.ATOMIC_OR_UNION_TYPE, false) != null
    }

    fun parseSingleType(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (
            parseLocalUnionType(builder) ||
            this.parseEQNameOrWildcard(builder, XPathElementType.SIMPLE_TYPE_NAME, false) != null
        ) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.OPTIONAL)) {
                marker.done(XPathElementType.SINGLE_TYPE)
            } else {
                marker.drop()
            }
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
    fun parseItemType(builder: PsiBuilder): Boolean {
        return (
            parseKindTest(builder) ||
            parseAnyItemTest(builder) ||
            parseAnnotatedFunction(builder) ||
            parseMapTest(builder) ||
            parseArrayTest(builder) ||
            parseTupleType(builder) ||
            parseLocalUnionType(builder) ||
            parseTypeAlias(builder) ||
            parseAtomicOrUnionType(builder) ||
            parseParenthesizedItemType(builder)
        )
    }

    private fun parseAnyItemTest(builder: PsiBuilder): Boolean {
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

            marker.done(XPathElementType.ANY_ITEM_TEST)
            return true
        }
        return false
    }

    open fun parseAnnotatedFunction(builder: PsiBuilder): Boolean = parseAnyOrTypedFunctionTest(builder)

    fun parseAnyOrTypedFunctionTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FUNCTION)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val type = when {
                builder.matchTokenType(XPathTokenType.STAR) -> KindTest.ANY_TEST
                parseSequenceTypeList(builder, alwaysIncludeNode = true) -> KindTest.TYPED_TEST
                else -> KindTest.TYPED_TEST
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

            if (type === KindTest.ANY_TEST)
                marker.done(XPathElementType.ANY_FUNCTION_TEST)
            else
                marker.done(XPathElementType.TYPED_FUNCTION_TEST)
            return true
        }
        return false
    }

    private fun parseParenthesizedItemType(builder: PsiBuilder): Boolean {
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

    private fun parseMapTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_MAP)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.STAR) -> type = XPathElementType.ANY_MAP_TEST
                parseLocalUnionType(builder) || parseAtomicOrUnionType(builder) -> {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.COMMA)) {
                        builder.error(XPathBundle.message("parser.error.expected", ","))
                        haveError = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                        haveError = true
                    }

                    type = XPathElementType.TYPED_MAP_TEST
                }
                builder.tokenType === XPathTokenType.COMMA -> {
                    builder.error(XPathBundle.message("parser.error.expected-either", "LocalUnionType", "AtomicOrUnionType"))
                    haveError = true

                    builder.matchTokenType(XPathTokenType.COMMA)

                    parseWhiteSpaceAndCommentTokens(builder)
                    parseSequenceType(builder)

                    type = XPathElementType.TYPED_MAP_TEST
                }
                else -> {
                    builder.error(XPathBundle.message("parser.error.expected-eqname-or-token", "*"))
                    type = XPathElementType.ANY_MAP_TEST
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(type)
            return true
        }
        return false
    }

    private fun parseArrayTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ARRAY)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.STAR) -> type = XPathElementType.ANY_ARRAY_TEST
                parseSequenceType(builder) -> type = XPathElementType.TYPED_ARRAY_TEST
                else -> {
                    builder.error(XPathBundle.message("parser.error.expected-either", "*", "SequenceType"))
                    type = XPathElementType.ANY_ARRAY_TEST
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(type)
            return true
        }
        return false
    }

    private fun parseTupleType(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TUPLE)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseTupleField(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "NCName"))
                haveError = true
            }

            var isExtensible = false
            var haveNext = true
            while (haveNext) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (isExtensible) {
                    val extMarker = builder.mark()
                    if (!builder.matchTokenType(XPathTokenType.COMMA)) {
                        haveNext = false
                        extMarker.drop()
                        continue
                    } else {
                        extMarker.error(XPathBundle.message("parser.error.tuple-wildcard-with-names-after"))
                    }
                } else if (!builder.matchTokenType(XPathTokenType.COMMA)) {
                    haveNext = false
                    continue
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.STAR)) {
                    isExtensible = true
                } else if (!parseTupleField(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-either", "NCName", "*"))
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.TUPLE_TYPE)
            return true
        }
        return false
    }

    private fun parseTupleField(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseNCName(builder) || parseStringLiteral(builder)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            val haveSeparator =
                if (builder.matchTokenType(XPathTokenType.ELVIS)) // ?: without whitespace
                    true
                else {
                    builder.matchTokenType(XPathTokenType.OPTIONAL)
                    parseWhiteSpaceAndCommentTokens(builder)
                    builder.matchTokenType(XPathTokenType.TUPLE_FIELD_SEQUENCE_INDICATOR)
                }

            if (!haveSeparator) {
                if (builder.tokenType === XPathTokenType.COMMA || builder.tokenType === XPathTokenType.PARENTHESIS_CLOSE) {
                    marker.done(XPathElementType.TUPLE_FIELD)
                    return true
                }
                builder.error(XPathBundle.message("parser.error.expected-either", ":", "as"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceType(builder) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
            }

            marker.done(XPathElementType.TUPLE_FIELD)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseLocalUnionType(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_UNION)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseItemType(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseItemType(builder) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
                    haveError = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.LOCAL_UNION_TYPE)
            return true
        }
        return false
    }

    private fun parseTypeAlias(builder: PsiBuilder): Boolean {
        // Saxon 9.8 syntax
        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.TYPE_ALIAS)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "EQName"))
                marker.drop()
            } else {
                marker.done(XPathElementType.TYPE_ALIAS)
            }
            return true
        }

        // Saxon 10.0 syntax
        marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TYPE)
        if (marker != null) {
            var haveTypeAlias = true

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "EQName"))
                haveTypeAlias = false
                marker.drop()
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && haveTypeAlias) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            if (haveTypeAlias) {
                marker.done(XPathElementType.TYPE_ALIAS)
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    private val ATTRIBUTE_DECLARATION: IElementType
        get() = QNAME

    private val ELEMENT_DECLARATION: IElementType
        get() = QNAME

    open val EXTERNAL_KEYWORD: IElementType? = null

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
            parseElementTest(builder) || parseSchemaElementTest(builder)

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
            parseQNameOrWildcard(builder, NCNAME) != null || parseStringLiteral(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.PI_TEST)
            return true
        }
        return false
    }

    private fun parseAttributeTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ATTRIBUTE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseNameTest(builder, XPathElementType.ATTRIBUTE_TEST, allowAxisStep = false) != null) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (this.parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false) == null) {
                        builder.error(XPathBundle.message("parser.error.expected-eqname"))
                        haveErrors = true
                    }
                } else if (
                    builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE &&
                    builder.tokenType !== EXTERNAL_KEYWORD // XQuery VarDecl
                ) {
                    builder.error(XPathBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    this.parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)
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
            if (this.parseEQNameOrWildcard(builder, ATTRIBUTE_DECLARATION, false) == null) {
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

    private fun parseNillableOrNonNillableTypeName(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var haveErrors = false
        if (this.parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false) == null) {
            builder.error(XPathBundle.message("parser.error.expected-eqname"))
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XPathTokenType.OPTIONAL) && !haveErrors)
            marker.done(XPathElementType.NILLABLE_TYPE_NAME)
        else
            marker.drop()
        return haveErrors
    }

    fun parseElementTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ELEMENT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseNameTest(builder, XPathElementType.ELEMENT_TEST, allowAxisStep = false) != null) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    haveErrors = parseNillableOrNonNillableTypeName(builder)
                } else if (
                    builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE &&
                    builder.tokenType !== EXTERNAL_KEYWORD // XQuery VarDecl
                ) {
                    builder.error(XPathBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    this.parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false)
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
            if (this.parseEQNameOrWildcard(builder, ELEMENT_DECLARATION, false) == null) {
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

    open val COMMENT: IElementType = XPathElementType.COMMENT

    fun parseStringLiteral(builder: PsiBuilder): Boolean = parseStringLiteral(builder, XPathElementType.STRING_LITERAL)

    open fun parseStringLiteral(builder: PsiBuilder, type: IElementType): Boolean {
        val stringMarker = builder.matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        while (stringMarker != null) {
            return if (
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER)
            ) {
                continue
            } else if (builder.matchTokenType(XPathTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type)
                true
            } else {
                stringMarker.done(type)
                builder.error(XPathBundle.message("parser.error.incomplete-string"))
                true
            }
        }
        return false
    }

    open fun parseComment(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.COMMENT_START_TAG) {
            val commentMarker = builder.mark()
            builder.advanceLexer()
            parseCommentContents(builder)
            if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
                builder.advanceLexer()
                commentMarker.done(COMMENT)
            } else {
                builder.advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(COMMENT)
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

    open fun parseCommentContents(builder: PsiBuilder): Boolean {
        // NOTE: XQueryTokenType.COMMENT is omitted by the PsiBuilder.
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

    fun parseEQNameOrWildcard(
        builder: PsiBuilder,
        type: IElementType,
        endQNameOnSpace: Boolean = false,
        isElementOrAttributeName: Boolean = false
    ): IElementType? {
        val marker = builder.mark()
        val eqnameType = parseQNameOrWildcard(builder, type, endQNameOnSpace, isElementOrAttributeName)
            ?: parseURIQualifiedNameOrWildcard(builder, type)
        if (eqnameType != null) {
            when {
                type === QNAME -> marker.drop()
                type === NCNAME -> marker.drop()
                type === XPathElementType.WILDCARD -> marker.drop()
                type === XPathElementType.VAR_REF -> marker.drop()
                else -> marker.done(type)
            }
            return eqnameType
        }
        marker.drop()
        return null
    }

    private fun parseURIQualifiedNameOrWildcard(builder: PsiBuilder, type: IElementType): IElementType? {
        val marker = builder.mark()
        if (parseBracedURILiteral(builder)) {
            val localName = parseQNameNCName(builder, QNamePart.URIQualifiedLiteralLocalName, type, false)
            return if (type === XPathElementType.WILDCARD && localName === XPathTokenType.STAR) {
                marker.done(XPathElementType.WILDCARD)
                XPathElementType.WILDCARD
            } else {
                marker.done(URI_QUALIFIED_NAME)
                URI_QUALIFIED_NAME
            }
        }
        marker.drop()
        return null
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

    private fun parseNCName(builder: PsiBuilder): Boolean {
        if (builder.tokenType is INCNameType) {
            val marker = builder.mark()
            builder.advanceLexer()
            marker.done(NCNAME)
            return true
        }
        return false
    }

    fun parseQNameOrWildcard(
        builder: PsiBuilder,
        type: IElementType,
        endQNameOnSpace: Boolean = false,
        isElementOrAttributeName: Boolean = false
    ): IElementType? {
        val marker = builder.mark()
        val prefix = parseQNameNCName(builder, QNamePart.Prefix, type, false)
        if (prefix != null) {
            // NOTE: Whitespace in QNames is reported by QNameAnnotator so the prefix can be correctly styled.
            if (parseWhiteSpaceAndCommentTokens(builder) && endQNameOnSpace) {
                return if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                    marker.done(XPathElementType.WILDCARD)
                    XPathElementType.WILDCARD
                } else {
                    marker.done(NCNAME)
                    prefix
                }
            }

            val nameMarker = builder.mark()
            if (parseQNameSeparator(builder, type)) {
                // NOTE: Whitespace in QNames is reported by QNameAnnotator so the prefix can be correctly styled.
                if (parseWhiteSpaceAndCommentTokens(builder) && endQNameOnSpace) {
                    nameMarker.rollbackTo()
                    return if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                        marker.done(XPathElementType.WILDCARD)
                        XPathElementType.WILDCARD
                    } else {
                        marker.done(NCNAME)
                        prefix
                    }
                }
                nameMarker.drop()

                // MarkLogic's roxy project uses 'c:#function-name' in several template files.
                builder.errorOnTokenType(
                    XPathTokenType.FUNCTION_REF_OPERATOR,
                    XPathBundle.message("parser.error.qname.missing-local-name")
                )

                val localName = parseQNameNCName(builder, QNamePart.LocalName, type, prefix == XPathTokenType.STAR)
                return if (
                    type === XPathElementType.WILDCARD &&
                    (prefix === XPathTokenType.STAR || localName === XPathTokenType.STAR)
                ) {
                    marker.done(XPathElementType.WILDCARD)
                    XPathElementType.WILDCARD
                } else {
                    marker.done(QNAME)
                    QNAME
                }
            } else if (
                (type === XPathElementType.WILDCARD || isElementOrAttributeName) && prefix == XPathTokenType.STAR
            ) {
                nameMarker.drop()
                if (isElementOrAttributeName) {
                    // Don't create a PSI element for `*` only wildcards for ElementNameOrWildcard and
                    // AttribNameOrWildcard symbols. This is so that `element()` has the same PSI tree
                    // as `element(*)`, and `attribute()` has the same PSI tree as `attribute(*)`.
                    marker.drop()
                } else {
                    marker.done(XPathElementType.WILDCARD)
                }
                return XPathElementType.WILDCARD
            } else {
                nameMarker.drop()
                marker.done(NCNAME)
                return prefix
            }
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
            return TokenType.ERROR_ELEMENT
        }

        marker.drop()
        return null
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
        } else if (tokenType == XPathTokenType.INTEGER_LITERAL) {
            if (partType == QNamePart.Prefix && elementType === XPathElementType.VAR_REF) {
                builder.advanceLexer()
                return XPathElementType.PARAM_REF
            } else if (partType == QNamePart.LocalName) {
                // The user has started the local name with a number, so treat it as part of the QName.
                val errorMarker = builder.mark()
                builder.advanceLexer()
                errorMarker.error(XPathBundle.message("parser.error.qname.missing-local-name"))
            }
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
