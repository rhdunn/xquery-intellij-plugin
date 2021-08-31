/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle

/**
 * A unified XPath parser for different XPath versions and dialects.
 */
@Suppress("PropertyName", "PrivatePropertyName")
open class XPathParser : PsiParser {
    companion object {
        private val KEY_SPECIFIER = IElementType("XPATH_KEY_SPECIFIER", XPath)
    }
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

    open val EXPR: IElementType = XPathElementType.EXPR

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
            parseWithExpr(builder) ||
            parseForExpr(builder) ||
            parseLetExpr(builder) ||
            parseQuantifiedExpr(builder) != null ||
            parseIfExpr(builder) != null ||
            parseTernaryConditionalExpr(builder, parentType) != null
        )
    }

    // endregion
    // region Grammar :: Expr :: WithExpr

    fun parseWithExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_WITH)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            val haveNamespaceDeclaration = parseNamespaceDeclaration(builder)
            if (haveNamespaceDeclaration) {
                var haveError = false

                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseNamespaceDeclaration(builder) && !haveError) {
                        builder.error(XPathBundle.message("parser.error.expected", "NamespaceDeclaration"))
                        haveError = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                }
            } else {
                builder.error(XPathBundle.message("parser.error.expected", "NamespaceDeclaration"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.tokenType === XPathTokenType.BLOCK_OPEN -> {
                    parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)

                    if (!haveNamespaceDeclaration)
                        marker.drop()
                    else
                        marker.done(XPathElementType.WITH_EXPR)
                    return true
                }
                haveNamespaceDeclaration -> {
                    builder.error(XPathBundle.message("parser.error.expected-either", ",", "EnclosedExpr"))

                    marker.done(XPathElementType.WITH_EXPR)
                    return true
                }
                else -> {
                    marker.rollbackTo()
                }
            }
        }
        return false
    }

    private fun parseNamespaceDeclaration(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseQNameOrWildcard(builder, XPathElementType.QNAME) != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.EQUAL)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseStringLiteral(builder, URI_LITERAL) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "URILiteral"))
                marker.drop()
            } else {
                marker.done(XPathElementType.NAMESPACE_DECLARATION)
            }
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: ForExpr

    fun parseReturnClause(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_RETURN)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }
            return true
        }
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

    open fun parseForOrWindowClause(builder: PsiBuilder): IElementType? {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return when {
                parseSimpleForClause(builder) -> marker.doneAndReturn(XPathElementType.SIMPLE_FOR_CLAUSE)
                else -> marker.rollbackToAndReturn()
            }
        }
        return null
    }

    private fun parseSimpleForClause(builder: PsiBuilder): Boolean {
        val haveMember = builder.matchTokenType(XPathTokenType.K_MEMBER)
        parseWhiteSpaceAndCommentTokens(builder)

        if (parseSimpleForBinding(builder, true)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (
                builder.matchTokenType(XPathTokenType.COMMA) ||
                builder.errorOnTokenType(XPathTokenType.K_FOR, XPathBundle.message("parser.error.expected", ","))
            ) {
                parseWhiteSpaceAndCommentTokens(builder)
                parseSimpleForBinding(builder, false)
                parseWhiteSpaceAndCommentTokens(builder)
            }
            return true
        }
        if (haveMember) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
        }
        return haveMember
    }

    private fun parseSimpleForBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        val haveMember = builder.errorOnTokenType(XPathTokenType.K_MEMBER, XPathBundle.message("parser.error.expected", "$"))
        parseWhiteSpaceAndCommentTokens(builder)

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && (!isFirst || haveMember)) {
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
        return haveMember
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
    // region Grammar :: Expr :: LetExpr

    private fun parseLetExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseSimpleLetClause(builder)) {
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

    private fun parseSimpleLetClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K_LET)) {
            var isFirst = true
            do {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSimpleLetBinding(builder, isFirst) && isFirst) {
                    marker.rollbackTo()
                    return false
                }

                isFirst = false
                parseWhiteSpaceAndCommentTokens(builder)
            } while (
                builder.matchTokenType(XPathTokenType.COMMA) ||
                builder.errorOnTokenType(XPathTokenType.K_LET, XPathBundle.message("parser.error.expected", ""))
            )

            marker.done(XPathElementType.SIMPLE_LET_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    open fun parseSimpleLetBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
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

    fun parseQuantifiedExpr(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.QUANTIFIED_EXPR_QUALIFIER_TOKENS) { marker ->
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                return@matchTokenTypeWithMarker marker.rollbackToAndReturn()
            }

            val hasBinding = parseQuantifierBinding(builder, true)
            if (hasBinding) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    parseQuantifierBinding(builder, false)
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
                    return@matchTokenTypeWithMarker marker.rollbackToAndReturn()
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.doneAndReturn(XPathElementType.QUANTIFIED_EXPR)
        }
    }

    open fun parseQuantifierBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
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

            marker.done(XPathElementType.QUANTIFIER_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    open fun parseIfExpr(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.K_IF) { marker ->
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                return@matchTokenTypeWithMarker marker.rollbackToAndReturn()
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
            if (!builder.matchTokenType(XPathTokenType.K_ELSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "else"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.doneAndReturn(XPathElementType.IF_EXPR)
        }
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr

    open fun parseTernaryConditionalExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveOrExpr = parseOrExpr(builder, type)
        if (haveOrExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (builder.matchTokenType(XPathTokenType.TERNARY_IF)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseTernaryConditionalExpr(builder, null) == null) {
                    builder.error(XPathBundle.message("parser.error.expected", "TernaryConditionalExpr"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.TERNARY_ELSE)) {
                    builder.error(XPathBundle.message("parser.error.expected", "!!"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (parseTernaryConditionalExpr(builder, null) == null) {
                    builder.error(XPathBundle.message("parser.error.expected", "TernaryConditionalExpr"))
                }

                marker.doneAndReturn(XPathElementType.TERNARY_CONDITIONAL_EXPR)
            } else {
                marker.dropAndReturn(haveOrExpr)
            }
        }
        return marker.dropAndReturn()
    }

    fun parseOrExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveAndExpr = parseAndExpr(builder, type)
        if (haveAndExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveOrExpr = false
            while (builder.matchTokenType(XPathTokenType.OR_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseAndExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "AndExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveOrExpr = true
                }
            }

            return when (haveOrExpr) {
                true -> marker.doneAndReturn(XPathElementType.OR_EXPR)
                else -> marker.dropAndReturn(haveAndExpr)
            }
        }
        return marker.dropAndReturn()
    }

    open fun parseAndExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveComparisonExpr = parseComparisonExpr(builder, type)
        if (haveComparisonExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveAndExpr = false
            while (builder.matchTokenType(XPathTokenType.AND_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseComparisonExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "ComparisonExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveAndExpr = true
                }
            }

            return when (haveAndExpr) {
                true -> marker.doneAndReturn(XPathElementType.AND_EXPR)
                else -> marker.dropAndReturn(haveComparisonExpr)
            }
        }
        return marker.dropAndReturn()
    }

    open fun parseComparisonExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        var haveFTContainsExpr = parseFTContainsExpr(builder, type)
        if (haveFTContainsExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (parseGeneralComp(builder) || parseValueComp(builder) || parseNodeComp(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseFTContainsExpr(builder, type)) {
                    null -> {
                        builder.error(XPathBundle.message("parser.error.expected", "FTContainsExpr"))
                        marker.dropAndReturn(haveFTContainsExpr)
                    }
                    TokenType.ERROR_ELEMENT -> marker.dropAndReturn(haveFTContainsExpr)
                    else -> marker.doneAndReturn(XPathElementType.COMPARISON_EXPR)
                }
            } else {
                marker.dropAndReturn(haveFTContainsExpr)
            }
        } else if (
            builder.errorOnTokenType(
                XPathTokenType.COMP_SYMBOL_TOKENS, XPathBundle.message("parser.error.comparison-no-lhs")
            )
        ) {
            parseWhiteSpaceAndCommentTokens(builder)
            haveFTContainsExpr = parseFTContainsExpr(builder, type)
            if (haveFTContainsExpr == null) {
                builder.error(XPathBundle.message("parser.error.expected", "StringConcatExpr"))
            }
            return marker.dropAndReturn(haveFTContainsExpr)
        }
        return marker.dropAndReturn()
    }

    fun parseFTContainsExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveStringConcatExpr = parseStringConcatExpr(builder, type)
        if (haveStringConcatExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)

            return if (builder.matchTokenType(XPathTokenType.K_CONTAINS)) {
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
                marker.doneAndReturn(XPathElementType.FT_CONTAINS_EXPR)
            } else {
                return marker.dropAndReturn(haveStringConcatExpr)
            }
        }
        return marker.dropAndReturn()
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
            if (parseUnionExpr(builder, XPathElementType.FT_IGNORE_OPTION) == null && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", "UnionExpr"))
            }

            marker.done(XPathElementType.FT_IGNORE_OPTION)
            return true
        }
        return false
    }

    private fun parseStringConcatExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveRangeExpr = parseRangeExpr(builder, type)
        if (haveRangeExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveStringConcatExpr = false
            while (builder.matchTokenType(XPathTokenType.CONCATENATION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseRangeExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "RangeExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else  -> haveStringConcatExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            return when (haveStringConcatExpr) {
                true -> marker.doneAndReturn(XPathElementType.STRING_CONCAT_EXPR)
                else -> marker.dropAndReturn(haveRangeExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseRangeExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveAdditiveExpr = parseAdditiveExpr(builder, type)
        if (haveAdditiveExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (builder.matchTokenType(XPathTokenType.K_TO)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseAdditiveExpr(builder, type)) {
                    null -> {
                        builder.error(XPathBundle.message("parser.error.expected", "AdditiveExpr"))
                        marker.dropAndReturn(haveAdditiveExpr)
                    }
                    TokenType.ERROR_ELEMENT -> marker.dropAndReturn(haveAdditiveExpr)
                    else -> marker.doneAndReturn(XPathElementType.RANGE_EXPR)
                }
            } else {
                marker.dropAndReturn(haveAdditiveExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseAdditiveExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveMultiplicativeExpr = parseMultiplicativeExpr(builder, type)
        if (haveMultiplicativeExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveAdditiveExpr = false
            while (builder.matchTokenType(XPathTokenType.ADDITIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseMultiplicativeExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "MultiplicativeExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveAdditiveExpr = true
                }
            }

            return when (haveAdditiveExpr) {
                true -> marker.doneAndReturn(XPathElementType.ADDITIVE_EXPR)
                else -> marker.dropAndReturn(haveMultiplicativeExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseMultiplicativeExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveOtherwiseExpr = parseOtherwiseExpr(builder, type)
        if (haveOtherwiseExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveMultiplicativeExpr = false
            while (builder.matchTokenType(XPathTokenType.MULTIPLICATIVE_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseOtherwiseExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "OtherwiseExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveMultiplicativeExpr = true
                }
            }

            return when (haveMultiplicativeExpr) {
                true -> marker.doneAndReturn(XPathElementType.MULTIPLICATIVE_EXPR)
                else -> marker.dropAndReturn(haveOtherwiseExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseOtherwiseExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveUnionExpr = parseUnionExpr(builder, type)
        if (haveUnionExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveOtherwiseExpr = false
            while (builder.matchTokenType(XPathTokenType.K_OTHERWISE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseUnionExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "OtherwiseExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveOtherwiseExpr = true
                }
            }

            return when (haveOtherwiseExpr) {
                true -> marker.doneAndReturn(XPathElementType.OTHERWISE_EXPR)
                else -> marker.dropAndReturn(haveUnionExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseUnionExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveIntersectExceptExpr = parseIntersectExceptExpr(builder, type)
        if (haveIntersectExceptExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveUnionExpr = false
            while (builder.matchTokenType(XPathTokenType.UNION_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseIntersectExceptExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "IntersectExceptExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveUnionExpr = true
                }
            }

            return when (haveUnionExpr) {
                true -> marker.doneAndReturn(XPathElementType.UNION_EXPR)
                else -> marker.dropAndReturn(haveIntersectExceptExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseIntersectExceptExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveInstanceofExpr = parseInstanceofExpr(builder, type)
        if (haveInstanceofExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveIntersectExceptExpr = false
            while (builder.matchTokenType(XPathTokenType.INTERSECT_EXCEPT_EXPR_TOKENS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parseInstanceofExpr(builder, type)) {
                    null -> builder.error(XPathBundle.message("parser.error.expected", "InstanceofExpr"))
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveIntersectExceptExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            return when (haveIntersectExceptExpr) {
                true -> marker.doneAndReturn(XPathElementType.INTERSECT_EXCEPT_EXPR)
                else -> marker.dropAndReturn(haveInstanceofExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseInstanceofExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveTreatExpr = parseTreatExpr(builder, type)
        if (haveTreatExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return when {
                builder.matchTokenType(XPathTokenType.K_INSTANCE) -> {
                    var haveErrors = false

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.K_OF)) {
                        haveErrors = true
                        builder.error(XPathBundle.message("parser.error.expected-keyword", "of"))
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder)) {
                        if (!haveErrors) {
                            builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                        }
                        marker.dropAndReturn(haveTreatExpr)
                    } else {
                        marker.doneAndReturn(XPathElementType.INSTANCEOF_EXPR)
                    }
                }
                builder.tokenType === XPathTokenType.K_OF -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "instance"))
                    builder.advanceLexer()

                    parseWhiteSpaceAndCommentTokens(builder)
                    parseSequenceType(builder)
                    marker.dropAndReturn(haveTreatExpr)
                }
                else -> marker.dropAndReturn(haveTreatExpr)
            }
        }
        return marker.dropAndReturn(haveTreatExpr)
    }

    open fun parseTreatExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveCastableExpr = parseCastableExpr(builder, type)
        if (haveCastableExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return when {
                builder.matchTokenType(XPathTokenType.K_TREAT) -> {
                    var haveErrors = false

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                        haveErrors = true
                        builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseSequenceType(builder)) {
                        if (!haveErrors) {
                            builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                        }
                        marker.dropAndReturn(haveCastableExpr)
                    } else {
                        marker.doneAndReturn(XPathElementType.TREAT_EXPR)
                    }
                }
                builder.tokenType === XPathTokenType.K_AS -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "cast, castable, treat"))
                    builder.advanceLexer()

                    parseWhiteSpaceAndCommentTokens(builder)
                    parseSequenceType(builder)
                    marker.dropAndReturn(haveCastableExpr)
                }
                else -> marker.dropAndReturn(haveCastableExpr)
            }
        }
        return marker.dropAndReturn()
    }

    fun parseCastableExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveCastExpr = parseCastExpr(builder, type)
        if (haveCastExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (builder.matchTokenType(XPathTokenType.K_CASTABLE)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSingleType(builder)) {
                    if (!haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "SingleType"))
                    }
                    marker.dropAndReturn(haveCastExpr)
                } else {
                    marker.doneAndReturn(XPathElementType.CASTABLE_EXPR)
                }
            } else {
                marker.dropAndReturn(haveCastExpr)
            }
        }
        return marker.dropAndReturn()
    }

    open fun parseCastExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveArrowExpr = parseArrowExpr(builder, type)
        if (haveArrowExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            return if (builder.matchTokenType(XPathTokenType.K_CAST)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSingleType(builder)) {
                    if (!haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "SingleType"))
                    }
                    marker.dropAndReturn(haveArrowExpr)
                } else {
                    marker.doneAndReturn(XPathElementType.CAST_EXPR)
                }
            } else {
                marker.dropAndReturn(haveArrowExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseUnaryExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        var matched = false
        while (builder.matchTokenType(XPathTokenType.UNARY_EXPR_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            matched = true
        }
        return when (matched) {
            true -> {
                val haveValueExpr = parseValueExpr(builder, null)
                when {
                    haveValueExpr === TokenType.ERROR_ELEMENT -> marker.dropAndReturn(haveValueExpr)
                    haveValueExpr != null -> marker.doneAndReturn(XPathElementType.UNARY_EXPR)
                    else -> {
                        builder.error(XPathBundle.message("parser.error.expected", "ValueExpr"))
                        marker.doneAndReturn(XPathElementType.UNARY_EXPR)
                    }
                }
            }
            else -> marker.dropAndReturn(parseValueExpr(builder, type))
        }
    }

    fun parseGeneralComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.GENERAL_COMP_TOKENS)

    fun parseValueComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.VALUE_COMP_TOKENS)

    fun parseNodeComp(builder: PsiBuilder): Boolean = builder.matchTokenType(XPathTokenType.NODE_COMP_TOKENS)

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: ArrowExpr

    fun parseArrowExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val haveUnaryExpr = parseUnaryExpr(builder, type)
        if (haveUnaryExpr != null) {
            var haveErrors = false
            var haveArrowExpr = false

            parseWhiteSpaceAndCommentTokens(builder)
            var targetType = builder.tokenType
            while (XPathTokenType.ARROW_TARGET_TOKENS.contains(targetType)) {
                builder.advanceLexer()

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseArrowFunctionCall(builder, targetType) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "ArrowFunctionSpecifier"))
                    haveErrors = true
                } else {
                    haveArrowExpr = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                targetType = builder.tokenType
            }

            return when (haveArrowExpr) {
                true -> marker.doneAndReturn(XPathElementType.ARROW_EXPR)
                else -> marker.dropAndReturn(haveUnaryExpr)
            }
        }
        return marker.dropAndReturn()
    }

    private fun parseArrowFunctionCall(builder: PsiBuilder, targetType: IElementType?): Boolean {
        val marker = builder.mark()
        val elementType = when {
            this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) != null ->
                XPathElementType.ARROW_FUNCTION_CALL
            parseVarOrParamRef(builder, null) != null -> XPathElementType.ARROW_DYNAMIC_FUNCTION_CALL
            parseParenthesizedExpr(builder) != null -> XPathElementType.ARROW_DYNAMIC_FUNCTION_CALL
            builder.tokenType === XPathTokenType.BLOCK_OPEN -> {
                if (targetType === XPathTokenType.ARROW) {
                    builder.error(XPathBundle.message("parser.error.enclosed-expr-on-fat-arrow-target"))
                }
                parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)
                XPathElementType.ARROW_INLINE_FUNCTION_CALL
            }
            else -> {
                marker.drop()
                return false
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (!parseArgumentList(builder) && elementType !== XPathElementType.ARROW_INLINE_FUNCTION_CALL) {
            builder.error(XPathBundle.message("parser.error.expected", "ArgumentList"))
            marker.drop()
        } else {
            marker.done(elementType)
        }

        return true
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: ValueExpr

    open fun parseValueExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        return parseSimpleMapExpr(builder, type)
    }

    fun parseSimpleMapExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        val havePathExpr = parsePathExpr(builder, type)
        if (havePathExpr != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            var haveSimpleMapExpr = false
            while (builder.matchTokenType(XPathTokenType.MAP_OPERATOR)) {
                parseWhiteSpaceAndCommentTokens(builder)
                when (parsePathExpr(builder, null)) {
                    null -> if (!haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "PathExpr"))
                        haveErrors = true
                    }
                    TokenType.ERROR_ELEMENT -> {
                    }
                    else -> haveSimpleMapExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            return if (haveSimpleMapExpr)
                marker.doneAndReturn(XPathElementType.SIMPLE_MAP_EXPR)
            else
                marker.dropAndReturn(havePathExpr)
        }
        return marker.dropAndReturn()
    }

    private fun parsePathExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        return when {
            builder.matchTokenType(XPathTokenType.DIRECT_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                parseRelativePathExpr(builder, null, marker, XPathTokenType.DIRECT_DESCENDANTS_PATH)
                XPathElementType.PATH_EXPR
            }
            builder.matchTokenType(XPathTokenType.ALL_DESCENDANTS_PATH) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseRelativePathExpr(builder, null, marker, XPathTokenType.ALL_DESCENDANTS_PATH) == null) {
                    builder.error(XPathBundle.message("parser.error.expected", "RelativePathExpr"))
                }
                XPathElementType.PATH_EXPR
            }
            else -> parseRelativePathExpr(builder, type, marker, null)
        }
    }

    private fun parseRelativePathExpr(
        builder: PsiBuilder,
        type: IElementType?,
        marker: PsiBuilder.Marker,
        pathExprStartToken: IElementType?
    ): IElementType? {
        val step = parseStepExpr(builder, type)
        return when {
            step != null -> {
                parseWhiteSpaceAndCommentTokens(builder)
                var haveRelativePathExpr = step === XPathElementType.AXIS_STEP
                while (builder.matchTokenType(XPathTokenType.RELATIVE_PATH_EXPR_TOKENS)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (parseStepExpr(builder, XPathElementType.PATH_EXPR) == null) {
                        builder.error(XPathBundle.message("parser.error.expected", "StepExpr"))
                    } else {
                        haveRelativePathExpr = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }

                if (haveRelativePathExpr || pathExprStartToken != null)
                    marker.doneAndReturn(XPathElementType.PATH_EXPR)
                else
                    marker.dropAndReturn(step)
            }
            pathExprStartToken === XPathTokenType.DIRECT_DESCENDANTS_PATH -> {
                marker.doneAndReturn(XPathElementType.PATH_EXPR)
            }
            else -> marker.dropAndReturn()
        }
    }

    fun parsePragma(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PRAGMA_BEGIN)
        if (marker != null) {
            var haveErrors = false

            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
            if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
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
    // region Grammar :: Expr :: TernaryConditionalExpr :: StepExpr

    open fun parseStepExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        return parseAxisStep(builder, type) ?: parsePostfixExpr(builder, type)
    }

    fun parseAxisStep(builder: PsiBuilder, type: IElementType?): IElementType? {
        val marker = builder.mark()
        if (parseReverseStep(builder) || parseForwardStep(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            parsePredicateList(builder, marker, recover = false)
            return XPathElementType.AXIS_STEP
        }
        return marker.dropAndReturn()
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
                return marker.rollbackToAndReturn()
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
                    marker.doneAndReturn(XPathElementType.NAME_TEST)
                }
            } else if (isElementOrAttributeTest) {
                marker.drop()
                return XPathElementType.NAME_TEST
            } else {
                return marker.doneAndReturn(XPathElementType.NAME_TEST)
            }
        }
        return marker.dropAndReturn()
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PostfixExpr

    fun parsePostfixExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        var marker = builder.mark()
        val havePrimaryExpr = parsePrimaryExpr(builder, type)
        if (havePrimaryExpr != null) {
            parseWhiteSpaceAndCommentTokens(builder)

            var havePostfixExpr = false
            postfix@while (true) {
                if (parsePredicate(builder)) {
                    parseWhiteSpaceAndCommentTokens(builder)

                    marker.done(XPathElementType.FILTER_EXPR)
                    marker = marker.precede()

                    // Keep PostfixExpr if there is a filter expression.
                    havePostfixExpr = true
                    continue@postfix
                }
                if (parsePositionalArgumentList(builder)) {
                    parseWhiteSpaceAndCommentTokens(builder)

                    marker.done(XPathElementType.DYNAMIC_FUNCTION_CALL)
                    marker = marker.precede()

                    // Keep PostfixExpr if there is a dynamic function call.
                    havePostfixExpr = true
                    continue@postfix
                }
                when (parseLookup(builder, null)) {
                    null -> {
                    }
                    TokenType.ERROR_ELEMENT -> {
                        parseWhiteSpaceAndCommentTokens(builder)
                        continue@postfix
                    }
                    else -> {
                        parseWhiteSpaceAndCommentTokens(builder)
                        marker.done(XPathElementType.POSTFIX_LOOKUP)
                        marker = marker.precede()

                        // Keep PostfixExpr if there is a postfix lookup.
                        havePostfixExpr = true
                        continue@postfix
                    }
                }
                if (havePostfixExpr) {
                    marker.drop()
                    return XPathElementType.POSTFIX_EXPR
                }
                if (type === XPathElementType.PATH_EXPR) {
                    // Keep PostfixExpr if the PrimaryExpr is in a non-initial StepExpr.
                    return marker.doneAndReturn(XPathElementType.POSTFIX_EXPR)
                }
                if (XPathTokenType.RELATIVE_PATH_EXPR_TOKENS.contains(builder.tokenType)) {
                    // Keep PostfixExpr if a StepExpr follows the PrimaryExpr (i.e. this is the first step).
                    return marker.doneAndReturn(XPathElementType.POSTFIX_EXPR)
                }
                return marker.dropAndReturn(havePrimaryExpr)
            }
        }
        return marker.dropAndReturn()
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
        if (builder.matchTokenType(XPathTokenType.SQUARE_OPEN)) {
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
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr

    open fun parsePrimaryExpr(builder: PsiBuilder, type: IElementType?): IElementType? {
        var ret: IElementType? = null
        ret = ret ?: parseLiteral(builder)
        ret = ret ?: parseVarOrParamRef(builder, type)
        ret = ret ?: parseParenthesizedExpr(builder)
        ret = ret ?: parseFunctionItemExpr(builder)
        ret = ret ?: parseFunctionCall(builder)
        ret = ret ?: parseMapConstructor(builder)
        ret = ret ?: parseArrayConstructor(builder)
        ret = ret ?: parseContextItemExpr(builder)
        ret = ret ?: parseLookup(builder, XPathElementType.UNARY_LOOKUP)
        return ret
    }

    fun parseLiteral(builder: PsiBuilder): IElementType? {
        val numericLiteral = parseNumericLiteral(builder)
        if (numericLiteral != null) {
            if (
                builder.tokenType is IKeywordOrNCNameType ||
                builder.tokenType === XPathTokenType.BRACED_URI_LITERAL_START
            ) {
                builder.error(XPathBundle.message("parser.error.consecutive-non-delimiting-terminals"))
            }
            return numericLiteral
        }
        return parseStringLiteral(builder)
    }

    private fun parseNumericLiteral(builder: PsiBuilder): IElementType? {
        val ret = when {
            builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) -> {
                builder.errorOnTokenType(
                    XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                    XPathBundle.message("parser.error.incomplete-double-exponent")
                )
                XPathTokenType.INTEGER_LITERAL
            }
            builder.matchTokenType(XPathTokenType.DOUBLE_LITERAL) -> XPathTokenType.DOUBLE_LITERAL
            builder.matchTokenType(XPathTokenType.DECIMAL_LITERAL) -> {
                builder.errorOnTokenType(
                    XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                    XPathBundle.message("parser.error.incomplete-double-exponent")
                )
                XPathTokenType.DECIMAL_LITERAL
            }
            else -> return null
        }
        builder.errorOnTokenType(
            XPathTokenType.NCNAME,
            XPathBundle.message("parser.error.ncname-following-number")
        )
        return ret
    }

    fun parseVarOrParamRef(builder: PsiBuilder, type: IElementType?): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR) { marker ->
            parseWhiteSpaceAndCommentTokens(builder)
            val eqnameType = this.parseEQNameOrWildcard(
                builder, XPathElementType.VAR_REF,
                type === XPathElementType.MAP_CONSTRUCTOR_ENTRY
            )
            when (eqnameType) {
                null -> {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    marker.dropAndReturn(TokenType.ERROR_ELEMENT)
                }
                XPathElementType.PARAM_REF -> marker.doneAndReturn(XPathElementType.PARAM_REF)
                else -> marker.doneAndReturn(XPathElementType.VAR_REF)
            }
        }
    }

    private fun parseParenthesizedExpr(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN) { marker ->
            parseWhiteSpaceAndCommentTokens(builder)
            val haveExpr = parseExpr(builder, EXPR)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            when {
                haveExpr -> marker.dropAndReturn(XPathElementType.EXPR)
                else -> marker.doneAndReturn(XPathElementType.EMPTY_EXPR)
            }
        }
    }

    private fun parseContextItemExpr(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.DOT) { marker ->
            val whitespace = builder.mark()
            val haveWhitespace = parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.BLOCK_OPEN && haveWhitespace) {
                whitespace.error(XPathBundle.message("parser.error.context-function.whitespace-between-dot-and-brace"))
            } else {
                whitespace.drop()
            }

            when (val ret = parseContextItemFunctionExpr(builder, marker)) {
                null -> marker.dropAndReturn(XPathTokenType.DOT)
                else -> ret // NOTE: marker is consumed by parseContextItemFunctionExpr.
            }
        }
    }

    private fun parseLookup(builder: PsiBuilder, type: IElementType?): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.OPTIONAL) { marker ->
            parseWhiteSpaceAndCommentTokens(builder)
            val keySpecifier = parseKeySpecifier(builder)
            when {
                keySpecifier == null -> when (type) {
                    // NOTE: This conflicts with '?' used as an ArgumentPlaceholder, so don't match '?' only as UnaryLookup.
                    XPathElementType.UNARY_LOOKUP -> marker.rollbackToAndReturn()
                    else -> {
                        builder.error(XPathBundle.message("parser.error.expected", "KeySpecifier"))
                        marker.dropAndReturn()
                    }
                }
                keySpecifier === TokenType.ERROR_ELEMENT -> marker.dropAndReturn(keySpecifier)
                type != null -> marker.doneAndReturn(type)
                else -> marker.dropAndReturn(XPathElementType.POSTFIX_LOOKUP)
            }
        }
    }

    private fun parseKeySpecifier(builder: PsiBuilder): IElementType? = when {
        builder.matchTokenType(XPathTokenType.STAR) -> KEY_SPECIFIER
        builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) -> KEY_SPECIFIER
        this.parseEQNameOrWildcard(builder, XPathElementType.NCNAME, false) != null -> KEY_SPECIFIER
        parseStringLiteral(builder) != null -> KEY_SPECIFIER
        parseParenthesizedExpr(builder) != null -> KEY_SPECIFIER
        else -> when (parseVarOrParamRef(builder, null)) {
            null -> null
            TokenType.ERROR_ELEMENT -> TokenType.ERROR_ELEMENT
            else -> KEY_SPECIFIER
        }
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: FunctionCall

    open fun parseFunctionCall(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseArgumentList(builder)) {
                return marker.rollbackToAndReturn()
            }
            return marker.doneAndReturn(XPathElementType.FUNCTION_CALL)
        }
        return marker.dropAndReturn()
    }

    fun parseArgumentList(builder: PsiBuilder, type: IElementType = XPathElementType.ARGUMENT_LIST): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseArguments(builder, type)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(type)
            return true
        }
        return false
    }

    private fun parsePositionalArgumentList(builder: PsiBuilder): Boolean {
        return parseArgumentList(builder, XPathElementType.POSITIONAL_ARGUMENT_LIST)
    }

    private fun parseArguments(builder: PsiBuilder, listType: IElementType): Boolean {
        var prevArgumentType: IElementType? = parseArgument(builder, null, listType)
        if (prevArgumentType != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)

                prevArgumentType = parseArgument(builder, prevArgumentType, listType)
                if (prevArgumentType == null && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-either", "ExprSingle", "?"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }
            return true
        }
        return false
    }

    private fun parseArgument(
        builder: PsiBuilder,
        prevArgumentType: IElementType?,
        listType: IElementType
    ): IElementType? {
        var keywordArgument: PsiBuilder.Marker? = null
        if (builder.tokenType is INCNameType) {
            keywordArgument = builder.mark()
            parseNCName(builder)

            val spaceBeforeSeparator = parseWhiteSpaceAndCommentTokens(builder)
            when {
                builder.matchTokenType(XPathTokenType.QNAME_SEPARATOR) -> {
                    if (builder.tokenType is INCNameType && !spaceBeforeSeparator) { // QName
                        keywordArgument.rollbackTo()
                        keywordArgument = null
                    } else if (listType === XPathElementType.POSITIONAL_ARGUMENT_LIST) {
                        keywordArgument.error(XPathBundle.message("parser.error.keyword-argument-in-positional-argument-list"))
                        keywordArgument = null
                        parseWhiteSpaceAndCommentTokens(builder)
                    } else { // KeywordArgument
                        parseWhiteSpaceAndCommentTokens(builder)
                    }
                }
                builder.errorOnTokenType(
                    XPathTokenType.ASSIGN_EQUAL,
                    XPathBundle.message("parser.error.expected", ":")
                ) -> {
                    parseWhiteSpaceAndCommentTokens(builder)
                }
                else -> /* NCName */ {
                    keywordArgument.rollbackTo()
                    keywordArgument = null
                }
            }
        } else if (prevArgumentType === XPathElementType.KEYWORD_ARGUMENT) {
            builder.error(XPathBundle.message("parser.error.expected", "KeywordArgument"))
        }

        val argumentType = when {
            parseExprSingle(builder) -> XPathElementType.ARGUMENT
            parseArgumentPlaceholder(builder, keywordArgument != null) -> XPathElementType.ARGUMENT_PLACEHOLDER
            keywordArgument != null -> {
                builder.error(XPathBundle.message("parser.error.keyword-argument-expr-single-or-qname"))
                null
            }
            else -> null
        }

        return when {
            keywordArgument != null -> keywordArgument.doneAndReturn(XPathElementType.KEYWORD_ARGUMENT)
            prevArgumentType === XPathElementType.KEYWORD_ARGUMENT -> XPathElementType.KEYWORD_ARGUMENT
            else -> argumentType
        }
    }

    private fun parseArgumentPlaceholder(builder: PsiBuilder, isKeywordArgument: Boolean): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.OPTIONAL)
        if (marker != null) {
            if (isKeywordArgument) {
                marker.error(XPathBundle.message("parser.error.expected", "ExprSingle"))
            } else {
                marker.done(XPathElementType.ARGUMENT_PLACEHOLDER)
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: FunctionItemExpr

    private fun parseFunctionItemExpr(builder: PsiBuilder): IElementType? {
        var ret: IElementType? = null
        ret = ret ?: parseNamedFunctionRef(builder)
        ret = ret ?: parseInlineFunctionExpr(builder)
        ret = ret ?: parseContextItemFunctionExpr(builder, null)
        ret = ret ?: parseLambdaFunctionExpr(builder)
        return ret
    }

    private fun parseNamedFunctionRef(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.FUNCTION_REF_OPERATOR)) {
                return marker.rollbackToAndReturn()
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "IntegerLiteral"))
            }

            return marker.doneAndReturn(XPathElementType.NAMED_FUNCTION_REF)
        }
        return marker.dropAndReturn()
    }

    open fun parseInlineFunctionExpr(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        val token = builder.tokenType
        if (builder.matchTokenType(XPathTokenType.INLINE_FUNCTION_TOKENS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFunctionSignature(builder, required = token === XPathTokenType.K_FUNCTION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.tokenType === XPathTokenType.BLOCK_OPEN) {
                    //
                } else {
                    return marker.rollbackToAndReturn()
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                parseExpr(builder, EXPR)

                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)
            }

            return marker.doneAndReturn(XPathElementType.INLINE_FUNCTION_EXPR)
        }
        return marker.dropAndReturn()
    }

    private fun parseContextItemFunctionExpr(builder: PsiBuilder, contextItem: PsiBuilder.Marker?): IElementType? {
        val marker = contextItem ?: builder.mark()
        if (contextItem != null || builder.matchTokenType(XPathTokenType.K_FN)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (marker !== contextItem) {
                    marker.rollbackTo()
                }
                return null
            }
            return marker.doneAndReturn(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR)
        } else if (parseEnclosedExprOrBlock(builder, null, BlockOpen.CONTEXT_FUNCTION, BlockExpr.REQUIRED)) {
            return marker.doneAndReturn(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR)
        }
        return marker.dropAndReturn()
    }

    private fun parseLambdaFunctionExpr(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K__)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                return marker.rollbackToAndReturn()
            }
            return marker.doneAndReturn(XPathElementType.LAMBDA_FUNCTION_EXPR)
        } else if (parseEnclosedExprOrBlock(builder, null, BlockOpen.LAMBDA_FUNCTION, BlockExpr.REQUIRED)) {
            return marker.doneAndReturn(XPathElementType.LAMBDA_FUNCTION_EXPR)
        }
        return marker.dropAndReturn()
    }

    fun parseFunctionSignature(builder: PsiBuilder, required: Boolean = true): Boolean {
        val matched = builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)
        if (!matched && required) {
            builder.error(XPathBundle.message("parser.error.expected", "("))
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parseParamList(builder)

        parseWhiteSpaceAndCommentTokens(builder)
        if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && matched) {
            builder.error(XPathBundle.message("parser.error.expected", ")"))
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parseTypeDeclaration(builder)

        return matched
    }

    private fun parseParamList(builder: PsiBuilder): Boolean {
        while (parseParam(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.VARIABLE_INDICATOR) {
                builder.error(XPathBundle.message("parser.error.expected", ","))
            } else if (!builder.matchTokenType(XPathTokenType.COMMA)) {
                builder.matchTokenType(XPathTokenType.ELLIPSIS)
                return true
            }

            parseWhiteSpaceAndCommentTokens(builder)
        }
        return false
    }

    private fun parseParam(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            marker.done(XPathElementType.PARAM)
            return true
        } else if (builder.tokenType === XPathTokenType.NCNAME || builder.tokenType is IKeywordOrNCNameType || builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false)

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            marker.done(XPathElementType.PARAM)
            return true
        }

        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: MapConstructor

    private fun parseMapConstructor(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.MAP_CONSTRUCTOR_TOKENS) { marker ->
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
                return@matchTokenTypeWithMarker marker.rollbackToAndReturn()
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

            marker.doneAndReturn(XPathElementType.MAP_CONSTRUCTOR)
        }
    }

    private fun parseMapConstructorEntry(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder, null, XPathElementType.MAP_CONSTRUCTOR_ENTRY)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.MAP_ENTRY_SEPARATOR_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-map-entry-assign"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XPathElementType.MAP_CONSTRUCTOR_ENTRY)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: ArrayConstructor

    private fun parseArrayConstructor(builder: PsiBuilder): IElementType? {
        return parseSquareArrayConstructor(builder) ?: parseCurlyArrayConstructor(builder)
    }

    private fun parseSquareArrayConstructor(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.SQUARE_OPEN) { marker ->
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

            marker.doneAndReturn(XPathElementType.SQUARE_ARRAY_CONSTRUCTOR)
        }
    }

    private fun parseCurlyArrayConstructor(builder: PsiBuilder): IElementType? {
        return builder.matchTokenTypeWithMarker(XPathTokenType.CURLY_ARRAY_CONSTRUCTOR_TOKENS) { marker ->
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackToAndReturn()
            } else {
                marker.doneAndReturn(XPathElementType.CURLY_ARRAY_CONSTRUCTOR)
            }
        }
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: FTSelection

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
        if (parseStringLiteral(builder) != null) {
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
                    if (parseAdditiveExpr(builder, type) == null) {
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
                    if (parseAdditiveExpr(builder, type) == null && !haveError) {
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
                    if (parseAdditiveExpr(builder, type) == null) {
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
                    if (parseAdditiveExpr(builder, type) == null && !haveError) {
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
    // region Grammar :: Expr :: TernaryConditionalExpr :: FTPosFilter

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
            if (parseAdditiveExpr(builder, XPathElementType.FT_WINDOW) == null) {
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
    // region Grammar :: Expr :: TernaryConditionalExpr :: FTMatchOptions

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
            if (parseStringLiteral(builder, URI_LITERAL) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "URILiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_RELATIONSHIP)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseStringLiteral(builder) == null && !haveError) {
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
            if (parseStringLiteral(builder, URI_LITERAL) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "URILiteral"))
            }

            marker.done(XPathElementType.FT_STOP_WORDS)
            return true
        } else if (builder.tokenType === XPathTokenType.PARENTHESIS_OPEN) {
            val marker = builder.mark()
            builder.advanceLexer()

            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseStringLiteral(builder) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseStringLiteral(builder) == null && !haveError) {
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
            if (parseStringLiteral(builder) == null) {
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
            if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseStringLiteral(builder) == null && !haveErrors) {
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
            parseFunctionTest(builder) ||
            parseMapTest(builder) ||
            parseArrayTest(builder) ||
            parseRecordTest(builder) ||
            parseLocalUnionType(builder) ||
            parseEnumerationType(builder) ||
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

    open fun parseFunctionTest(builder: PsiBuilder, marker: PsiBuilder.Marker = builder.mark()): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_FUNCTION)) {
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
        marker.drop()
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
                parseItemType(builder) -> {
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
                    builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
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

    private fun parseEnumerationType(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ENUM)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseStringLiteral(builder) == null) {
                builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseStringLiteral(builder) == null && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected", "StringLiteral"))
                    haveError = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ENUMERATION_TYPE)
            return true
        }
        return false
    }

    private fun parseTypeAlias(builder: PsiBuilder): Boolean {
        // Saxon 9.8 syntax
        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.TYPE_ALIAS)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
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
            if (this.parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
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
    // region Grammar :: TypeDeclaration :: ItemType :: RecordTest

    private fun parseRecordTest(builder: PsiBuilder): Boolean {
        val recordType = builder.tokenType
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.RECORD_TEST_TOKENS)
        if (marker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                !parseFieldDeclaration(builder, recordType) &&
                !builder.errorOnTokenType(XPathTokenType.STAR, XPathBundle.message("parser.error.expected", "FieldDeclaration"))
            ) {
                builder.error(XPathBundle.message("parser.error.expected", "FieldDeclaration"))
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
                } else if (!parseFieldDeclaration(builder, recordType) && !haveError) {
                    builder.error(XPathBundle.message("parser.error.expected-either", "FieldDeclaration", "*"))
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.RECORD_TEST)
            return true
        }
        return false
    }

    private fun parseFieldDeclaration(builder: PsiBuilder, recordType: IElementType?): Boolean = when (recordType) {
        XPathTokenType.K_TUPLE -> parseTupleFieldDeclaration(builder) // Saxon 9.8
        else -> parseRecordFieldDeclaration(builder) // XPath 4.0 ED
    }

    private fun parseRecordFieldDeclaration(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFieldName(builder)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens(builder)
            val haveSeparator = when {
                builder.errorOnTokenType(
                    XPathTokenType.ELVIS,
                    XPathBundle.message("parser.error.expected", "? as")
                ) -> true // ?: without whitespace
                else -> {
                    builder.matchTokenType(XPathTokenType.OPTIONAL)
                    parseWhiteSpaceAndCommentTokens(builder)
                    when {
                        builder.matchTokenType(XPathTokenType.K_AS) -> true
                        builder.errorOnTokenType(
                            XPathTokenType.QNAME_SEPARATOR,
                            XPathBundle.message("parser.error.expected", "as")
                        ) -> true
                        else -> false
                    }
                }
            }

            if (!haveSeparator) {
                if (builder.tokenType === XPathTokenType.COMMA || builder.tokenType === XPathTokenType.PARENTHESIS_CLOSE) {
                    marker.done(XPathElementType.FIELD_DECLARATION)
                    return true
                }
                builder.error(XPathBundle.message("parser.error.expected-either", ":", "as"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!(parseSequenceType(builder) || parseSelfReference(builder)) && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-either", "SequenceType", "SelfReference"))
            }

            marker.done(XPathElementType.FIELD_DECLARATION)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseTupleFieldDeclaration(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseFieldName(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            val haveSeparator = when {
                builder.matchTokenType(XPathTokenType.ELVIS) -> true // ?: without whitespace
                else -> {
                    builder.matchTokenType(XPathTokenType.OPTIONAL)
                    parseWhiteSpaceAndCommentTokens(builder)
                    builder.matchTokenType(XPathTokenType.TUPLE_FIELD_SEQUENCE_INDICATOR)
                }
            }

            if (!haveSeparator) {
                if (builder.tokenType === XPathTokenType.COMMA || builder.tokenType === XPathTokenType.PARENTHESIS_CLOSE) {
                    marker.done(XPathElementType.FIELD_DECLARATION)
                    return true
                }
                builder.error(XPathBundle.message("parser.error.expected-either", ":", "as"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceType(builder)) {
                if (
                    builder.errorOnTokenType(
                        XPathTokenType.PARENT_SELECTOR,
                        XPathBundle.message("parser.error.expected", "SequenceType")
                    )
                ) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    parseOccurrenceIndicator(builder)
                } else {
                    builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                }
            }

            marker.done(XPathElementType.FIELD_DECLARATION)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseFieldName(builder: PsiBuilder): Boolean {
        return parseNCName(builder) || parseStringLiteral(builder) != null
    }

    private fun parseSelfReference(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.PARENT_SELECTOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseOccurrenceIndicator(builder)
            marker.done(XPathElementType.SELF_REFERENCE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    private val ATTRIBUTE_DECLARATION: IElementType
        get() = XPathElementType.QNAME

    private val ELEMENT_DECLARATION: IElementType
        get() = XPathElementType.QNAME

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
            parseQNameOrWildcard(builder, XPathElementType.NCNAME) != null || parseStringLiteral(builder) != null

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

    fun parseStringLiteral(builder: PsiBuilder): IElementType? {
        return parseStringLiteral(builder, XPathElementType.STRING_LITERAL)
    }

    open fun parseStringLiteral(builder: PsiBuilder, type: IElementType): IElementType? {
        val stringMarker = builder.matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        tokens@while (stringMarker != null) {
            return when {
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) -> continue@tokens
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER) -> continue@tokens
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_END) -> stringMarker.doneAndReturn(type)
                else -> {
                    stringMarker.done(type)
                    builder.error(XPathBundle.message("parser.error.incomplete-string"))
                    type
                }
            }
        }
        return null
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
                type === XPathElementType.QNAME -> marker.drop()
                type === XPathElementType.NCNAME -> marker.drop()
                type === XPathElementType.WILDCARD -> marker.drop()
                type === XPathElementType.VAR_REF -> marker.drop()
                else -> marker.done(type)
            }
            return eqnameType
        }
        return marker.dropAndReturn()
    }

    private fun parseURIQualifiedNameOrWildcard(builder: PsiBuilder, type: IElementType): IElementType? {
        val marker = builder.mark()
        if (parseBracedURILiteral(builder)) {
            val localName = parseQNameNCName(builder, QNamePart.URIQualifiedLiteralLocalName, type, false)
            return if (type === XPathElementType.WILDCARD && localName === XPathTokenType.STAR) {
                marker.doneAndReturn(XPathElementType.WILDCARD)
            } else {
                marker.doneAndReturn(XPathElementType.URI_QUALIFIED_NAME)
            }
        }
        return marker.dropAndReturn()
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

    private fun parseNCName(builder: PsiBuilder): Boolean {
        if (builder.tokenType is INCNameType) {
            val marker = builder.mark()
            builder.advanceLexer()
            marker.done(XPathElementType.NCNAME)
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
            val nameMarker = builder.mark()
            if (parseWhiteSpaceAndCommentTokens(builder) && endQNameOnSpace) {
                nameMarker.drop()
                return if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                    marker.done(XPathElementType.WILDCARD)
                    XPathElementType.WILDCARD
                } else {
                    marker.done(XPathElementType.NCNAME)
                    prefix
                }
            }

            if (parseQNameSeparator(builder, type)) {
                // NOTE: Whitespace in QNames is reported by QNameAnnotator so the prefix can be correctly styled.
                if (parseWhiteSpaceAndCommentTokens(builder) && endQNameOnSpace) {
                    nameMarker.rollbackTo()
                    return if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                        marker.doneAndReturn(XPathElementType.WILDCARD)
                    } else {
                        marker.done(XPathElementType.NCNAME)
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
                    marker.done(XPathElementType.QNAME)
                    XPathElementType.QNAME
                }
            } else if (
                (type === XPathElementType.WILDCARD || isElementOrAttributeName) && prefix == XPathTokenType.STAR
            ) {
                nameMarker.rollbackTo()
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
                nameMarker.rollbackTo()
                marker.done(XPathElementType.NCNAME)
                return prefix
            }
        }

        if (parseQNameSeparator(builder, null)) { // Missing prefix
            builder.advanceLexer()
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType is INCNameType || builder.tokenType == XPathTokenType.STAR) {
                builder.advanceLexer()
            }
            if (type === XPathElementType.NCNAME) {
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
