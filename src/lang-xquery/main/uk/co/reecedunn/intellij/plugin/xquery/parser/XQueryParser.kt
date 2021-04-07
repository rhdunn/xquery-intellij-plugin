/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.parser

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParser
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocTokenType

/**
 * A unified XQuery parser for different XQuery dialects.
 *
 * Supported core language specifications:
 * -  XQuery 1.0 Second Edition (W3C Recommendation 14 December 2010)
 * -  XQuery 3.0 (W3C Recommendation 08 April 2014)
 * -  XQuery 3.1 (W3C Recommendation 21 March 2017)
 *
 * Supported W3C XQuery extensions:
 * -  Full Text 1.0 (W3C Recommendation 17 March 2011)
 * -  Full Text 3.0 (W3C Recommendation 24 November 2015)
 * -  Update Facility 1.0 (W3C Recommendation 17 March 2011)
 * -  Update Facility 3.0 (W3C Working Group Note 24 January 2017)
 * -  Scripting Extension 1.0 (W3C Working Group Note 18 September 2014)
 *
 * Supported vendor extensions:
 * -  BaseX
 * -  MarkLogic 1.0-ml
 * -  Saxon
 *
 * See the *EBNF for XQuery 3.1* section of `docs/XQuery IntelliJ Plugin.md`
 * for details of the grammar implemented by this parser.
 */
@Suppress("FunctionName")
class XQueryParser : XPathParser() {
    // region XPath/XQuery Element Types
    //
    // These element types have different PSI implementations in XPath and XQuery.

    override val EXPR: IElementType = XQueryElementType.CONCAT_EXPR

    // endregion
    // region Grammar

    override fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean = parseTransactions(builder, isFirst)

    private enum class ParseStatus {
        MATCHED,
        MATCHED_WITH_ERRORS,
        NOT_MATCHED
    }

    // endregion
    // region Grammar :: Modules

    private enum class TransactionType {
        WITH_PROLOG,
        WITHOUT_PROLOG,
        NONE
    }

    private fun parseTransactions(builder: PsiBuilder, isFirst: Boolean): Boolean {
        if (parseModule(builder, isFirst)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (parseTransactionSeparator(builder) != TransactionType.NONE) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseModule(builder, false)) {
                    // NOTE: Handles error cases for VersionDecl-only and library modules.
                    builder.error(XPathBundle.message("parser.error.expected", "MainModule"))
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }
            return true
        }
        return false
    }

    private fun parseTransactionSeparator(builder: PsiBuilder): TransactionType {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.SEPARATOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var haveProlog = false
            if (
                builder.tokenType === XQueryTokenType.K_XQUERY ||
                builder.tokenType === XQueryTokenType.K_DECLARE ||
                builder.tokenType === XQueryTokenType.K_IMPORT ||
                builder.tokenType === XQueryTokenType.K_MODULE
            ) {
                val prologMarker = builder.mark()
                builder.advanceLexer()
                parseWhiteSpaceAndCommentTokens(builder)
                haveProlog = builder.tokenType is IKeywordOrNCNameType
                prologMarker.rollbackTo()
            }

            marker.done(XQueryElementType.TRANSACTION_SEPARATOR)
            return if (haveProlog) TransactionType.WITH_PROLOG else TransactionType.WITHOUT_PROLOG
        }
        return TransactionType.NONE
    }

    private fun parseModule(builder: PsiBuilder, isFirst: Boolean): Boolean {
        var hasVersionDeclOrWhitespace: Boolean = parseVersionDecl(builder)
        hasVersionDeclOrWhitespace = hasVersionDeclOrWhitespace or parseWhiteSpaceAndCommentTokens(builder)

        val marker = builder.mark()
        if (parseLibraryModule(builder)) {
            marker.done(XQueryElementType.LIBRARY_MODULE)
            return true
        } else if (parseMainModule(builder)) {
            marker.done(XQueryElementType.MAIN_MODULE)
            return true
        }

        if (isFirst) {
            builder.error(XQueryBundle.message("parser.error.expected-module-type"))
        }
        marker.drop()
        return hasVersionDeclOrWhitespace
    }

    private fun parseVersionDecl(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_XQUERY)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XQueryTokenType.K_ENCODING)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder)) {
                    builder.error(XQueryBundle.message("parser.error.expected-encoding-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            } else {
                if (!builder.matchTokenType(XQueryTokenType.K_VERSION)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "version"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder) && !haveErrors) {
                    builder.error(XQueryBundle.message("parser.error.expected-version-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XQueryTokenType.K_ENCODING)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseStringLiteral(builder) && !haveErrors) {
                        builder.error(XQueryBundle.message("parser.error.expected-encoding-string"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            if (!builder.matchTokenType(XQueryTokenType.SEPARATOR)) {
                marker.done(XQueryElementType.VERSION_DECL)
                if (!haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", ";"))
                }
                if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
                    builder.advanceLexer()
                }
                return true
            }

            marker.done(XQueryElementType.VERSION_DECL)
            return true
        }
        return false
    }

    private fun parseMainModule(builder: PsiBuilder): Boolean {
        if (parseProlog(builder, false)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExpr(builder, XQueryElementType.QUERY_BODY)) {
                builder.error(XQueryBundle.message("parser.error.expected-query-body"))
            }
            if (builder.tokenType != null && builder.tokenType != XQueryTokenType.SEPARATOR) {
                builder.error(XPathBundle.message("parser.error.expected-eof"))
                // Keep any unknown expressions in the MainModule to keep the Prolog context.
                while (
                    builder.tokenType != XQueryTokenType.SEPARATOR &&
                    parseExpr(builder, XQueryElementType.QUERY_BODY)
                ) {
                    //
                }
            }
            return true
        }
        return parseExpr(builder, XQueryElementType.QUERY_BODY)
    }

    private fun parseLibraryModule(builder: PsiBuilder): Boolean {
        if (parseModuleDecl(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseProlog(builder, true)
            return true
        }
        return false
    }

    private fun parseModuleDecl(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_MODULE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NAMESPACE)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameOrWildcard(builder, XPathElementType.NCNAME, false) == null && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.SEPARATOR)) {
                marker.done(XQueryElementType.MODULE_DECL)
                if (!haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", ";"))
                }
                if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
                    builder.advanceLexer()
                }
                return true
            }

            marker.done(XQueryElementType.MODULE_DECL)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Prolog

    private enum class PrologDeclState {
        HEADER_STATEMENT,
        BODY_STATEMENT,
        UNKNOWN_STATEMENT,
        NOT_MATCHED
    }

    private fun parseProlog(builder: PsiBuilder, parseInvalidConstructs: Boolean): Boolean {
        val prologMarker = builder.mark()

        var state = PrologDeclState.NOT_MATCHED
        while (true) {
            val parseState = if (state == PrologDeclState.NOT_MATCHED) PrologDeclState.HEADER_STATEMENT else state
            var nextState = parseDecl(builder, parseState)
            if (nextState == PrologDeclState.NOT_MATCHED) {
                nextState = parseImport(builder, parseState)
            }
            if (nextState == PrologDeclState.NOT_MATCHED) {
                nextState = parseUsingDecl(builder, parseState)
            }

            when (nextState) {
                PrologDeclState.NOT_MATCHED -> if (parseInvalidConstructs && builder.tokenType != null) {
                    if (!parseWhiteSpaceAndCommentTokens(builder)) {
                        builder.error(XPathBundle.message("parser.error.unexpected-token"))
                        if (!parseExprSingle(builder)) builder.advanceLexer()
                    }
                } else {
                    if (state == PrologDeclState.NOT_MATCHED) {
                        prologMarker.drop()
                        return false
                    }
                    prologMarker.done(XQueryElementType.PROLOG)
                    return true
                }
                PrologDeclState.HEADER_STATEMENT, PrologDeclState.UNKNOWN_STATEMENT ->
                    if (state == PrologDeclState.NOT_MATCHED) {
                        state = PrologDeclState.HEADER_STATEMENT
                    }
                PrologDeclState.BODY_STATEMENT ->
                    if (state != PrologDeclState.BODY_STATEMENT) {
                        state = PrologDeclState.BODY_STATEMENT
                    }
            }

            if (nextState != PrologDeclState.NOT_MATCHED) {
                if (!builder.matchTokenType(XQueryTokenType.SEPARATOR)) {
                    builder.error(XPathBundle.message("parser.error.expected", ";"))
                    if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
                        builder.advanceLexer()
                    }
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }
        }
    }

    private fun parseDecl(builder: PsiBuilder, state: PrologDeclState): PrologDeclState {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseBaseURIDecl(builder, state) -> marker.done(XQueryElementType.BASE_URI_DECL)
                parseBoundarySpaceDecl(builder, state) -> marker.done(XQueryElementType.BOUNDARY_SPACE_DECL)
                parseConstructionDecl(builder, state) -> marker.done(XQueryElementType.CONSTRUCTION_DECL)
                parseCopyNamespacesDecl(builder, state) -> marker.done(XQueryElementType.COPY_NAMESPACES_DECL)
                parseDecimalFormatDecl(builder, state, false) -> marker.done(XQueryElementType.DECIMAL_FORMAT_DECL)
                parseDefaultDecl(builder, marker, state) -> {
                    //
                }
                parseNamespaceDecl(builder, state) -> marker.done(XQueryElementType.NAMESPACE_DECL)
                parseOptionDecl(builder) -> {
                    marker.done(XQueryElementType.OPTION_DECL)
                    return PrologDeclState.BODY_STATEMENT
                }
                parseOrderingModeDecl(builder, state) -> marker.done(XQueryElementType.ORDERING_MODE_DECL)
                parseRevalidationDecl(builder, state) -> marker.done(XQueryElementType.REVALIDATION_DECL)
                parseAnnotatedDecl(builder, marker) -> return PrologDeclState.BODY_STATEMENT
                parseContextItemDecl(builder) -> {
                    marker.done(XQueryElementType.CONTEXT_ITEM_DECL)
                    return PrologDeclState.BODY_STATEMENT
                }
                parseFTOptionDecl(builder) -> marker.done(XQueryElementType.FT_OPTION_DECL)
                else -> {
                    builder.error(
                        XPathBundle.message(
                            "parser.error.expected-keyword",
                            "base-uri, boundary-space, construction, context, copy-namespaces, decimal-format, default, ft-option, function, namespace, option, ordering, revalidation, type, variable"
                        )
                    )
                    parseUnknownDecl(builder)
                    marker.done(XQueryElementType.UNKNOWN_DECL)
                    return PrologDeclState.UNKNOWN_STATEMENT
                }
            }
            return PrologDeclState.HEADER_STATEMENT
        }
        return PrologDeclState.NOT_MATCHED
    }

    private fun parseDefaultDecl(builder: PsiBuilder, marker: PsiBuilder.Marker, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DEFAULT)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseDefaultNamespaceDecl(builder) -> marker.done(XQueryElementType.DEFAULT_NAMESPACE_DECL)
                parseEmptyOrderDecl(builder) -> marker.done(XQueryElementType.EMPTY_ORDER_DECL)
                parseDefaultCollationDecl(builder) -> marker.done(XQueryElementType.DEFAULT_COLLATION_DECL)
                parseDecimalFormatDecl(builder, state, true) -> marker.done(XQueryElementType.DECIMAL_FORMAT_DECL)
                else -> {
                    builder.error(
                        XPathBundle.message("parser.error.expected-keyword", "collation, element, function, type, order")
                    )
                    parseUnknownDecl(builder)
                    marker.done(XQueryElementType.UNKNOWN_DECL)
                }
            }
            return true
        }
        return false
    }

    private fun parseUnknownDecl(builder: PsiBuilder): Boolean {
        while (true) {
            if (parseWhiteSpaceAndCommentTokens(builder)) continue
            if (builder.matchTokenType(XPathTokenType.NCNAME)) continue
            if (parseStringLiteral(builder)) continue

            if (builder.matchTokenType(XPathTokenType.EQUAL)) continue
            if (builder.matchTokenType(XPathTokenType.COMMA)) continue
            if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) continue
            if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL)) continue
            if (builder.matchTokenType(XPathTokenType.QNAME_SEPARATOR)) continue
            if (builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) continue
            if (builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) continue

            if (builder.matchTokenType(XPathTokenType.K_AS)) continue
            if (builder.matchTokenType(XQueryTokenType.K_COLLATION)) continue
            if (builder.matchTokenType(XPathTokenType.K_ELEMENT)) continue
            if (builder.matchTokenType(XPathTokenType.K_EMPTY)) continue
            if (builder.matchTokenType(XQueryTokenType.K_EXTERNAL)) continue
            if (builder.matchTokenType(XPathTokenType.K_FUNCTION)) continue
            if (builder.matchTokenType(XQueryTokenType.K_GREATEST)) continue
            if (builder.matchTokenType(XQueryTokenType.K_INHERIT)) continue
            if (builder.matchTokenType(XPathTokenType.K_ITEM)) continue
            if (builder.matchTokenType(XQueryTokenType.K_LAX)) continue
            if (builder.matchTokenType(XPathTokenType.K_LEAST)) continue
            if (builder.matchTokenType(XPathTokenType.K_NAMESPACE)) continue
            if (builder.matchTokenType(XQueryTokenType.K_NO_INHERIT)) continue
            if (builder.matchTokenType(XQueryTokenType.K_NO_PRESERVE)) continue
            if (builder.matchTokenType(XQueryTokenType.K_ORDER)) continue
            if (builder.matchTokenType(XPathTokenType.K_ORDERED)) continue
            if (builder.matchTokenType(XQueryTokenType.K_PRESERVE)) continue
            if (builder.matchTokenType(XQueryTokenType.K_SKIP)) continue
            if (builder.matchTokenType(XQueryTokenType.K_STRICT)) continue
            if (builder.matchTokenType(XQueryTokenType.K_STRIP)) continue
            if (builder.matchTokenType(XPathTokenType.K_TYPE)) continue
            if (builder.matchTokenType(XQueryTokenType.K_UNORDERED)) continue

            if (parseDFPropertyName(builder)) continue
            if (parseExprSingle(builder)) continue
            return true
        }
    }

    private fun parseDefaultNamespaceDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.DEFAULT_NAMESPACE_TOKENS)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NAMESPACE)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseNamespaceDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NAMESPACE)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameOrWildcard(builder, XPathElementType.NCNAME, false) == null) {
                builder.error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseFTOptionDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_FT_OPTION)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTMatchOptions(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "using"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseUsingDecl(builder: PsiBuilder, state: PrologDeclState): PrologDeclState {
        val marker = builder.mark()
        val errorMarker = builder.mark()
        if (builder.matchTokenType(XPathTokenType.K_USING)) {
            var haveErrors = false
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NAMESPACE)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            marker.done(XQueryElementType.USING_DECL)
            return PrologDeclState.HEADER_STATEMENT
        }

        errorMarker.drop()
        marker.drop()
        return PrologDeclState.NOT_MATCHED
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Setter

    private fun parseBoundarySpaceDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_BOUNDARY_SPACE)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.BOUNDARY_SPACE_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "preserve, strip"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseDefaultCollationDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL)) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseBaseURIDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_BASE_URI)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL)) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseConstructionDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_CONSTRUCTION)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.CONSTRUCTION_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "preserve, strip"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseOrderingModeDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_ORDERING)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.ORDERING_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "ordered, unordered"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseEmptyOrderDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_ORDER)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_EMPTY)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "empty"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.EMPTY_ORDERING_MODE_TOKENS) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "greatest, least"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseRevalidationDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_REVALIDATION)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.REVALIDATION_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "lax, skip, strict"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseCopyNamespacesDecl(builder: PsiBuilder, state: PrologDeclState): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_COPY_NAMESPACES)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.PRESERVE_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "preserve, no-preserve"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.COMMA) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ","))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.INHERIT_MODE_TOKENS) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "inherit, no-inherit"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseDecimalFormatDecl(builder: PsiBuilder, state: PrologDeclState, isDefault: Boolean): Boolean {
        val errorMarker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_DECIMAL_FORMAT)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false
            if (!isDefault) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }
            }

            while (parseDFPropertyName(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder) && !haveErrors) {
                    builder.error(XQueryBundle.message("parser.error.expected-property-value-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseDFPropertyName(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XQueryTokenType.DF_PROPERTY_NAME)) {
            marker.done(XQueryElementType.DF_PROPERTY_NAME)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Import

    private fun parseImport(builder: PsiBuilder, state: PrologDeclState): PrologDeclState {
        val marker = builder.mark()
        val errorMarker = builder.mark()
        if (builder.matchTokenType(XQueryTokenType.K_IMPORT)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseSchemaImport(builder) -> marker.done(XQueryElementType.SCHEMA_IMPORT)
                parseStylesheetImport(builder) -> marker.done(XQueryElementType.STYLESHEET_IMPORT)
                parseModuleImport(builder) -> marker.done(XQueryElementType.MODULE_IMPORT)
                else -> {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "schema, stylesheet, module"))
                    marker.done(XQueryElementType.IMPORT)
                    return PrologDeclState.UNKNOWN_STATEMENT
                }
            }
            return PrologDeclState.HEADER_STATEMENT
        }

        errorMarker.drop()
        marker.drop()
        return PrologDeclState.NOT_MATCHED
    }

    private fun parseSchemaImport(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XQueryTokenType.K_SCHEMA) {
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            val haveErrors = parseSchemaPrefix(builder)

            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseLocationURIList(builder)
            return true
        }
        return false
    }

    private fun parseSchemaPrefix(builder: PsiBuilder): Boolean {
        var haveErrors = false
        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NAMESPACE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameOrWildcard(builder, XPathElementType.NCNAME, false) == null) {
                builder.error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            marker.done(XQueryElementType.SCHEMA_PREFIX)
            return haveErrors
        }

        marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DEFAULT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_ELEMENT)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "element"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NAMESPACE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            marker.done(XQueryElementType.SCHEMA_PREFIX)
        }
        return haveErrors
    }

    private fun parseStylesheetImport(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XQueryTokenType.K_STYLESHEET) {
            var haveErrors = false
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_AT)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "at"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseModuleImport(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XQueryTokenType.K_MODULE) {
            var haveErrors = false
            builder.advanceLexer()

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_NAMESPACE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseQNameOrWildcard(builder, XPathElementType.NCNAME, false) == null) {
                    builder.error(XQueryBundle.message("parser.error.expected-ncname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseLocationURIList(builder)
            return true
        }
        return false
    }

    private fun parseLocationURIList(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_AT)
        if (marker != null) {
            var haveErrors = false
            do {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                    builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            } while (builder.matchTokenType(XPathTokenType.COMMA))
            marker.done(XQueryElementType.LOCATION_URI_LIST)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Prolog :: Body

    private fun parseContextItemDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_CONTEXT)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_ITEM)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "item"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseItemType(builder)) {
                    builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) || run {
                    haveErrors = builder.errorOnTokenType(
                        XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")
                    )
                    haveErrors
                }) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                }
            } else if (builder.matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) || run {
                        haveErrors = builder.errorOnTokenType(
                            XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")
                        )
                        haveErrors
                    }) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseExprSingle(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-expression"))
                    }
                }
            } else {
                builder.error(XQueryBundle.message("parser.error.expected-variable-value"))
                parseExprSingle(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseAnnotatedDecl(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        var haveAnnotations = false
        var firstAnnotation: IElementType? = null
        var annotation: IElementType?
        do {
            annotation = if (parseAnnotation(builder)) {
                XQueryElementType.ANNOTATION
            } else {
                parseCompatibilityAnnotation(builder)
            }

            if (firstAnnotation == null) {
                firstAnnotation = annotation
            }

            if (annotation != null) {
                parseWhiteSpaceAndCommentTokens(builder)
                haveAnnotations = true
            }
        } while (annotation != null)

        return when {
            parseVarDecl(builder) -> {
                marker.done(XQueryElementType.VAR_DECL)
                true
            }
            parseFunctionDecl(builder, marker, firstAnnotation) -> true
            parseItemTypeDecl(builder, haveAnnotations) -> {
                marker.done(XQueryElementType.ITEM_TYPE_DECL)
                true
            }
            haveAnnotations -> {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "function, item-type, variable"))
                parseUnknownDecl(builder)
                marker.done(XQueryElementType.UNKNOWN_DECL)
                true
            }
            else -> false
        }
    }

    private fun parseAnnotation(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.ANNOTATION_INDICATOR)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                do {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseLiteral(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "Literal"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                } while (builder.matchTokenType(XPathTokenType.COMMA))

                if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", ")"))
                }
            }

            marker.done(XQueryElementType.ANNOTATION)
            return true
        }
        return false
    }

    private fun parseCompatibilityAnnotation(builder: PsiBuilder): IElementType? {
        val type = builder.tokenType
        return builder.matchTokenTypeWithMarker(XQueryTokenType.COMPATIBILITY_ANNOTATION_TOKENS)?.let {
            it.done(XQueryElementType.COMPATIBILITY_ANNOTATION)
            type
        }
    }

    private fun parseVarDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_VARIABLE)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeDeclaration(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) || run {
                    haveErrors = haveErrors or builder.errorOnTokenType(
                        XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")
                    )
                    haveErrors
                }) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                }
            } else if (builder.matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!parseExprSingle(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-expression"))
                    }
                }
            } else {
                builder.error(XQueryBundle.message("parser.error.expected-variable-value"))
                parseExprSingle(builder)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseFunctionDecl(
        builder: PsiBuilder,
        marker: PsiBuilder.Marker,
        firstAnnotation: IElementType?
    ): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_FUNCTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.STRING_LITERAL_START) {
                // DefaultNamespaceDecl with missing 'default' keyword.
                builder.error(XPathBundle.message("parser.error.expected", "("))
                parseStringLiteral(builder)
                parseWhiteSpaceAndCommentTokens(builder)
                marker.done(XQueryElementType.UNKNOWN_DECL)
                return true
            } else if (!parseFunctionSignature(builder)) {
                haveErrors = true
            }

            val bodyType = if (firstAnnotation === XQueryTokenType.K_SEQUENTIAL)
                XQueryElementType.BLOCK
            else
                null

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                !builder.matchTokenType(XQueryTokenType.K_EXTERNAL) &&
                !parseEnclosedExprOrBlock(builder, bodyType, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) &&
                !haveErrors
            ) {
                builder.error(XQueryBundle.message("parser.error.expected-enclosed-expression-or-keyword", "external"))
                parseExpr(builder, EXPR, true)

                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)
            }

            parseWhiteSpaceAndCommentTokens(builder)
            marker.done(XQueryElementType.FUNCTION_DECL)
            return true
        }

        return false
    }

    private fun parseOptionDecl(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XPathTokenType.K_OPTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-option-string"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    private fun parseItemTypeDecl(builder: PsiBuilder, haveAnnotation: Boolean): Boolean {
        val itemTypeToken = when {
            haveAnnotation && builder.errorOnTokenType(
                XPathTokenType.K_TYPE, XPathBundle.message("parser.error.expected-keyword", "function, item-type, variable")
            ) -> XPathTokenType.K_TYPE
            builder.matchTokenType(XPathTokenType.K_TYPE) -> XPathTokenType.K_TYPE
            builder.matchTokenType(XQueryTokenType.K_ITEM_TYPE) -> XQueryTokenType.K_ITEM_TYPE
            else -> null
        }

        if (itemTypeToken != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            when (itemTypeToken) {
                XPathTokenType.K_TYPE /* Saxon 9.8 */ -> {
                    if (!builder.matchTokenType(XPathTokenType.EQUAL) && run {
                            haveErrors = builder.errorOnTokenType(
                                XQueryTokenType.TYPE_DECL_ASSIGN_ERROR_TOKENS,
                                XPathBundle.message("parser.error.expected", "=")
                            )
                            !haveErrors
                        }) {
                        builder.error(XPathBundle.message("parser.error.expected", "="))
                        haveErrors = true
                    }
                }
                else /* XQuery 4.0 ED */ -> {
                    if (!builder.matchTokenType(XPathTokenType.K_AS) && run {
                            haveErrors = builder.errorOnTokenType(
                                XQueryTokenType.ITEM_TYPE_DECL_ASSIGN_ERROR_TOKENS,
                                XPathBundle.message("parser.error.expected", "as")
                            )
                            !haveErrors
                        }) {
                        builder.error(XPathBundle.message("parser.error.expected", "as"))
                        haveErrors = true
                    }
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseItemType(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "ItemType"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: EnclosedExpr|Block

    override fun parseEnclosedExprOrBlockExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        var exprType = EXPR
        if (type === XQueryElementType.BLOCK || type === XQueryElementType.WHILE_BODY) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseBlockDecls(builder)
            exprType = XQueryElementType.BLOCK_BODY
        }

        parseWhiteSpaceAndCommentTokens(builder)
        return parseExpr(builder, exprType)
    }

    private fun parseBlockDecls(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        parseWhiteSpaceAndCommentTokens(builder)
        while (true) {
            when (parseBlockVarDecl(builder)) {
                ParseStatus.MATCHED -> {
                    if (!builder.matchTokenType(XQueryTokenType.SEPARATOR)) {
                        builder.error(XPathBundle.message("parser.error.expected", ";"))
                        if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
                            builder.advanceLexer()
                        }
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }
                ParseStatus.MATCHED_WITH_ERRORS -> {
                    builder.matchTokenType(XQueryTokenType.SEPARATOR)
                    parseWhiteSpaceAndCommentTokens(builder)
                }
                ParseStatus.NOT_MATCHED -> {
                    marker.done(XQueryElementType.BLOCK_DECLS)
                    return true
                }
            }
        }
    }

    private fun parseBlockVarDecl(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType === XPathTokenType.PARENTHESIS_OPEN || builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
                // 'declare' used as a function name.
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            var status: ParseStatus
            do {
                parseWhiteSpaceAndCommentTokens(builder)
                status = parseBlockVarDeclEntry(builder)
                if (status == ParseStatus.NOT_MATCHED) {
                    status = ParseStatus.MATCHED_WITH_ERRORS
                }
            } while (builder.matchTokenType(XPathTokenType.COMMA))

            marker.done(XQueryElementType.BLOCK_VAR_DECL)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseBlockVarDeclEntry(builder: PsiBuilder): ParseStatus {
        val marker = builder.mark()
        var haveErrors = false
        if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            if (builder.tokenType === XQueryTokenType.SEPARATOR) {
                marker.drop()
                return ParseStatus.NOT_MATCHED
            }
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
            builder.error(XPathBundle.message("parser.error.expected-eqname"))
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens(builder)
        val errorMessage = if (parseTypeDeclaration(builder))
            "parser.error.expected-variable-assign-scripting"
        else
            "parser.error.expected-variable-assign-scripting-no-type-decl"

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) || run {
                haveErrors = builder.errorOnTokenType(
                    XPathTokenType.EQUAL, XQueryBundle.message(errorMessage)
                )
                haveErrors
            }) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }
            parseWhiteSpaceAndCommentTokens(builder)
        } else if (builder.tokenType !== XPathTokenType.COMMA && builder.tokenType !== XQueryTokenType.SEPARATOR) {
            builder.error(XQueryBundle.message(errorMessage))
            parseExprSingle(builder)
            parseWhiteSpaceAndCommentTokens(builder)
            haveErrors = true
        }
        marker.done(XQueryElementType.BLOCK_VAR_DECL_ENTRY)
        return if (haveErrors) ParseStatus.MATCHED_WITH_ERRORS else ParseStatus.MATCHED
    }

    // endregion
    // region Grammar :: Expr

    enum class HaveConcatExpr {
        None,
        Single,
        Multiple
    }

    override fun parseExpr(builder: PsiBuilder, type: IElementType?, functionDeclRecovery: Boolean): Boolean {
        val marker = builder.mark()
        return when (parseApplyExpr(builder, type!!, functionDeclRecovery)) {
            HaveConcatExpr.Multiple, HaveConcatExpr.Single -> {
                if (type !== EXPR)
                    marker.done(type)
                else
                    marker.drop()
                true
            }
            HaveConcatExpr.None -> {
                marker.drop()
                false
            }
        }
    }

    private fun parseApplyExpr(builder: PsiBuilder, type: IElementType, functionDeclRecovery: Boolean): HaveConcatExpr {
        val marker = builder.mark()
        var haveConcatExpr: HaveConcatExpr = HaveConcatExpr.None
        while (true) {
            if (!parseConcatExpr(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (
                    functionDeclRecovery || !builder.errorOnTokenType(
                        XQueryTokenType.SEPARATOR, XQueryBundle.message("parser.error.expected-query-statement", ";")
                    )
                ) {
                    if (haveConcatExpr != HaveConcatExpr.None)
                        marker.done(XQueryElementType.APPLY_EXPR)
                    else
                        marker.drop()
                    return haveConcatExpr
                } else {
                    // Semicolon without a query body -- continue parsing.
                    parseWhiteSpaceAndCommentTokens(builder)
                    continue
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)

            val transactionMarker = builder.mark()
            when (parseTransactionSeparator(builder)) {
                TransactionType.WITH_PROLOG -> {
                    // MarkLogic transaction containing a Prolog/Module statement.
                    transactionMarker.rollbackTo()
                    if (haveConcatExpr === HaveConcatExpr.Multiple)
                        marker.done(XQueryElementType.APPLY_EXPR)
                    else
                        marker.drop()
                    return HaveConcatExpr.Multiple
                }
                TransactionType.WITHOUT_PROLOG -> {
                    if (type !== XQueryElementType.QUERY_BODY) {
                        // Scripting Extension: Use a Separator as part of the ApplyExpr.
                        transactionMarker.rollbackTo()
                        builder.matchTokenType(XQueryTokenType.SEPARATOR)
                    } else {
                        // Scripting Extension, or MarkLogic Transaction: Keep the MarkLogic TransactionSeparator.
                        transactionMarker.drop()
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }
                TransactionType.NONE -> {
                    transactionMarker.rollbackTo()
                    if (haveConcatExpr !== HaveConcatExpr.None) {
                        if (type !== XQueryElementType.QUERY_BODY) {
                            // Scripting Extension: The semicolon is required to end a ConcatExpr.
                            builder.error(XPathBundle.message("parser.error.expected", ";"))
                        } else {
                            // Scripting Extension: The semicolon is required to end a ConcatExpr.
                            // MarkLogic Transactions: The last expression must not end with a semicolon.
                            val marker2 = builder.mark()
                            marker2.done(XQueryElementType.TRANSACTION_SEPARATOR)
                        }
                    }
                    return when (haveConcatExpr) {
                        HaveConcatExpr.None -> {
                            marker.drop()
                            HaveConcatExpr.Single
                        }
                        else -> {
                            marker.done(XQueryElementType.APPLY_EXPR)
                            HaveConcatExpr.Multiple
                        }
                    }
                }
            }

            haveConcatExpr = when (haveConcatExpr) {
                HaveConcatExpr.None -> HaveConcatExpr.Single
                else -> HaveConcatExpr.Multiple
            }
        }
    }

    private fun parseConcatExpr(builder: PsiBuilder): Boolean {
        return super.parseExpr(builder, XQueryElementType.CONCAT_EXPR, false)
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun parseExprSingleImpl(builder: PsiBuilder, parentType: IElementType?): Boolean {
        return (
            parseWithExpr(builder) ||
            parseFLWORExpr(builder) ||
            parseQuantifiedExpr(builder) ||
            parseSwitchExpr(builder) ||
            parseTypeswitchExpr(builder) ||
            parseIfExpr(builder) ||
            parseTryCatchExpr(builder) ||
            parseInsertExpr(builder) ||
            parseDeleteExpr(builder) ||
            parseRenameExpr(builder) ||
            parseReplaceExpr(builder) ||
            parseCopyModifyExpr(builder) ||
            parseUpdatingFunctionCall(builder) ||
            parseBlockExpr(builder) ||
            parseAssignmentExpr(builder) ||
            parseExitExpr(builder) ||
            parseWhileExpr(builder) ||
            parseTernaryConditionalExpr(builder, parentType)
        )
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr

    private fun parseFLWORExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseInitialClause(builder)) {
            while (parseIntermediateClause(builder)) {
                //
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseReturnClause(builder)) {
                builder.error(
                    XPathBundle.message(
                        "parser.error.expected-keyword",
                        "count, for, group, let, order, return, sliding, stable, tumbling, where"
                    )
                )
                parseWhiteSpaceAndCommentTokens(builder)
                parseExprSingle(builder)
            }

            marker.done(XQueryElementType.FLWOR_EXPR)
            return true
        } else if (
            builder.errorOnTokenType(
                XPathTokenType.K_RETURN, XQueryBundle.message("parser.error.return-without-flwor")
            )
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

    private fun parseInitialClause(builder: PsiBuilder): Boolean {
        return parseForOrWindowClause(builder) != null || parseLetClause(builder)
    }

    private fun parseIntermediateClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (
            parseInitialClause(builder) ||
            parseWhereClause(builder) ||
            parseOrderByClause(builder) ||
            parseCountClause(builder) ||
            parseGroupByClause(builder)
        ) {
            marker.done(XQueryElementType.INTERMEDIATE_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: ForClause|ForMemberClause

    override fun parseForOrWindowClause(builder: PsiBuilder): IElementType? {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_FOR)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            val forClauseType = parseForClause(builder)
            return when {
                forClauseType != null -> {
                    marker.done(forClauseType)
                    forClauseType
                }
                parseTumblingWindowClause(builder) || parseSlidingWindowClause(builder) -> {
                    marker.done(XQueryElementType.WINDOW_CLAUSE)
                    XQueryElementType.WINDOW_CLAUSE
                }
                else -> {
                    marker.rollbackTo()
                    null
                }
            }
        }
        return null
    }

    private fun parseForClause(builder: PsiBuilder): IElementType? {
        val type = when {
            builder.matchTokenType(XPathTokenType.K_MEMBER) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseForBinding(builder, true, XQueryElementType.FOR_MEMBER_BINDING)) {
                    builder.error(XPathBundle.message("parser.error.expected", "$"))
                }

                XQueryElementType.FOR_MEMBER_CLAUSE
            }
            parseForBinding(builder, true, XQueryElementType.FOR_BINDING) -> XQueryElementType.FOR_CLAUSE
            else -> null
        }
        if (type != null) {
            val bindingType = when (type) {
                XQueryElementType.FOR_MEMBER_CLAUSE -> XQueryElementType.FOR_MEMBER_BINDING
                else -> XQueryElementType.FOR_BINDING
            }

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                parseForBinding(builder, false, bindingType)
                parseWhiteSpaceAndCommentTokens(builder)
            }
        }
        return type
    }

    private fun parseForBinding(builder: PsiBuilder, isFirst: Boolean, type: IElementType): Boolean {
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
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val haveTypeDeclaration = parseTypeDeclaration(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            val haveAllowingEmpty = parseAllowingEmpty(builder, type)

            parseWhiteSpaceAndCommentTokens(builder)
            val havePositionalVar = parsePositionalVar(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            val haveScoreVar = parseFTScoreVar(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                val keywords = when {
                    haveScoreVar -> "in"
                    havePositionalVar -> "in, score"
                    haveAllowingEmpty -> "at, in, score"
                    haveTypeDeclaration -> "allowing, at, in, score"
                    else -> "allowing, as, at, in, score"
                }
                builder.error(XPathBundle.message("parser.error.expected-keyword", keywords))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(type)
            return true
        }
        marker.drop()
        return haveMember
    }

    private fun parseAllowingEmpty(builder: PsiBuilder, type: IElementType): Boolean {
        if (type == XQueryElementType.FOR_MEMBER_BINDING) {
            if (
                builder.errorOnTokenType(
                    XQueryTokenType.K_ALLOWING, XPathBundle.message("parser.error.unexpected-allowing-empty")
                )
            ) {
                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.K_EMPTY)
            }
            return false
        }

        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_ALLOWING)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_EMPTY)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "empty"))
            }

            marker.done(XQueryElementType.ALLOWING_EMPTY)
            return true
        }
        return false
    }

    private fun parsePositionalVar(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_AT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }

            marker.done(XQueryElementType.POSITIONAL_VAR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: LetClause

    private fun parseLetClause(builder: PsiBuilder): Boolean {
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

            marker.done(XQueryElementType.LET_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseLetBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val haveVariableIndicator = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        val matched = haveVariableIndicator || parseFTScoreVar(builder)
        if (!matched) {
            builder.error(XPathBundle.message("parser.error.expected-keyword-or-token", "$", "score"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            val errorMessage: String
            if (haveVariableIndicator) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                errorMessage = if (parseTypeDeclaration(builder))
                    XPathBundle.message("parser.error.expected", ":=")
                else
                    XQueryBundle.message("parser.error.expected-variable-assign-or-keyword", "as")
            } else {
                errorMessage = XPathBundle.message("parser.error.expected", ":=")
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.errorOnTokenType(XPathTokenType.EQUAL, errorMessage)) {
                haveErrors = true
            } else if (!builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) && !haveErrors) {
                builder.error(errorMessage)
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.LET_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: WindowClause

    private fun parseTumblingWindowClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_TUMBLING)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_WINDOW)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "window"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val haveTypeDeclaration = parseTypeDeclaration(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                builder.error(
                    XPathBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in")
                )
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseWindowStartCondition(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "WindowStartCondition"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseWindowEndCondition(builder)

            marker.done(XQueryElementType.TUMBLING_WINDOW_CLAUSE)
            return true
        }
        return false
    }

    private fun parseSlidingWindowClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SLIDING)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_WINDOW)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "window"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val haveTypeDeclaration = parseTypeDeclaration(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                builder.error(
                    XPathBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in")
                )
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseWindowStartCondition(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "WindowStartCondition"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseWindowEndCondition(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "WindowEndCondition"))
            }

            marker.done(XQueryElementType.SLIDING_WINDOW_CLAUSE)
            return true
        }
        return false
    }

    private fun parseWindowStartCondition(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_START)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            parseWindowVars(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_WHEN)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "when"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.WINDOW_START_CONDITION)
            return true
        }
        return false
    }

    private fun parseWindowEndCondition(builder: PsiBuilder): Boolean {
        var haveErrors = false

        var marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_END)
        if (marker == null) {
            marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_ONLY)
            if (marker != null) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_END)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "end"))
                    haveErrors = true
                }
            }
        }

        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseWindowVars(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_WHEN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "when"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.WINDOW_END_CONDITION)
            return true
        }
        return false
    }

    private fun parseWindowVars(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var haveErrors = false

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XQueryElementType.CURRENT_ITEM, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        parsePositionalVar(builder)

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XQueryTokenType.K_PREVIOUS)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XQueryElementType.PREVIOUS_ITEM, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XQueryTokenType.K_NEXT)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XQueryElementType.NEXT_ITEM, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }
        }

        marker.done(XQueryElementType.WINDOW_VARS)
        return true
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: CountClause

    private fun parseCountClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_COUNT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                builder.error(XPathBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-qname"))
            }

            marker.done(XQueryElementType.COUNT_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: WhereClause

    private fun parseWhereClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_WHERE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.WHERE_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: GroupByClause

    private fun parseGroupByClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_GROUP)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_BY)) {
                builder.error(XPathBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseGroupingSpecList(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "GroupingSpecList"))
            }

            marker.done(XQueryElementType.GROUP_BY_CLAUSE)
            return true
        }
        return false
    }

    private fun parseGroupingSpecList(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseGroupingSpec(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseGroupingSpec(builder)) {
                    builder.error(XPathBundle.message("parser.error.expected", "GroupingSpec"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            marker.done(XQueryElementType.GROUPING_SPEC_LIST)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseGroupingSpec(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseGroupingVariable(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseTypeDeclaration(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (
                    builder.errorOnTokenType(
                        XPathTokenType.EQUAL, XPathBundle.message("parser.error.expected", ":=")
                    )
                ) {
                    haveErrors = true
                } else if (!builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL)) {
                    builder.error(XPathBundle.message("parser.error.expected", ":="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
            } else if (builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) || run {
                    haveErrors = builder.errorOnTokenType(
                        XPathTokenType.EQUAL, XPathBundle.message("parser.error.expected", ":=")
                    )
                    haveErrors
                }) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
            }

            if (builder.matchTokenType(XQueryTokenType.K_COLLATION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                }
            }

            marker.done(XQueryElementType.GROUPING_SPEC)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseGroupingVariable(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: OrderByClause

    private fun parseOrderByClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XQueryTokenType.K_ORDER)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_BY)) {
                builder.error(XPathBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseOrderSpecList(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "OrderSpecList"))
            }

            marker.done(XQueryElementType.ORDER_BY_CLAUSE)
            return true
        } else if (builder.matchTokenType(XQueryTokenType.K_STABLE)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_ORDER)) {
                builder.error(XPathBundle.message("parser.error.expected", "order"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_BY) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseOrderSpecList(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "OrderSpecList"))
            }

            marker.done(XQueryElementType.ORDER_BY_CLAUSE)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseOrderSpecList(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseOrderSpec(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseOrderSpec(builder)) {
                    builder.error(XPathBundle.message("parser.error.expected", "OrderSpec"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }

            marker.done(XQueryElementType.ORDER_SPEC_LIST)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseOrderSpec(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseOrderModifier(builder)

            marker.done(XQueryElementType.ORDER_SPEC)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseOrderModifier(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        builder.matchTokenType(XQueryTokenType.ORDER_MODIFIER_TOKENS)

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XPathTokenType.K_EMPTY)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.EMPTY_ORDERING_MODE_TOKENS)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "greatest, least"))
            }
        }

        parseWhiteSpaceAndCommentTokens(builder)
        if (builder.matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseStringLiteral(builder, XQueryElementType.URI_LITERAL)) {
                builder.error(XQueryBundle.message("parser.error.expected-uri-string"))
            }
        }

        marker.done(XQueryElementType.ORDER_MODIFIER)
        return false
    }

    // endregion
    // region Grammar :: Expr :: QuantifiedExpr

    override fun parseQuantifierBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val haveTypeDeclaration = parseTypeDeclaration(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                builder.error(
                    XPathBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in")
                )
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
    // region Grammar :: Expr :: SwitchExpr

    private fun parseSwitchExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SWITCH)
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
            var matched = false
            while (parseSwitchCaseClause(builder)) {
                matched = true
                parseWhiteSpaceAndCommentTokens(builder)
            }
            if (!matched) {
                builder.error(XPathBundle.message("parser.error.expected", "SwitchCaseClause"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_DEFAULT) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "case, default"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.SWITCH_EXPR)
            return true
        }
        return false
    }

    private fun parseSwitchCaseClause(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        var haveCase = false
        while (builder.matchTokenType(XPathTokenType.K_CASE)) {
            haveCase = true
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSwitchCaseOperand(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }
            parseWhiteSpaceAndCommentTokens(builder)
        }

        if (haveCase) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.SWITCH_CASE_CLAUSE)
            return true
        }

        marker.drop()
        return false
    }

    private fun parseSwitchCaseOperand(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            marker.done(XQueryElementType.SWITCH_CASE_OPERAND)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TypeswitchExpr

    private fun parseTypeswitchExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_TYPESWITCH)
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
            }

            parseWhiteSpaceAndCommentTokens(builder)
            var matched = false
            while (parseCaseClause(builder)) {
                matched = true
                parseWhiteSpaceAndCommentTokens(builder)
            }
            if (!matched) {
                builder.error(XPathBundle.message("parser.error.expected", "CaseClause"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseDefaultCaseClause(builder)

            marker.done(XQueryElementType.TYPESWITCH_EXPR)
            return true
        }
        return false
    }

    private fun parseCaseClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_CASE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_AS) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceTypeUnion(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.CASE_CLAUSE)
            return true
        }
        return false
    }

    private fun parseDefaultCaseClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DEFAULT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.DEFAULT_CASE_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    override fun parseIfExpr(builder: PsiBuilder): Boolean {
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
    // region Grammar :: Expr :: TryCatchExpr

    private enum class CatchClauseType {
        NONE,
        XQUERY_30,
        MARK_LOGIC
    }

    private fun parseTryCatchExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseTryClause(builder)) {
            var type = CatchClauseType.NONE

            parseWhiteSpaceAndCommentTokens(builder)
            while (true) {
                val nextType = parseCatchClause(builder, type)
                if (nextType == CatchClauseType.NONE) {
                    if (type == CatchClauseType.NONE) {
                        builder.error(XPathBundle.message("parser.error.expected", "CatchClause"))
                    }

                    marker.done(XQueryElementType.TRY_CATCH_EXPR)
                    return true
                } else if (type != CatchClauseType.MARK_LOGIC) {
                    type = nextType
                }

                parseWhiteSpaceAndCommentTokens(builder)
            }
        }
        marker.drop()
        return false
    }

    private fun parseTryClause(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_TRY)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }
            marker.drop()
            return true
        }
        return false
    }

    private fun parseCatchClause(builder: PsiBuilder, type: CatchClauseType): CatchClauseType {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_CATCH)
        if (marker != null) {
            var haveErrors = false
            var nextType = CatchClauseType.XQUERY_30

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseCatchErrorList(builder)) {
                if (builder.tokenType === XPathTokenType.PARENTHESIS_OPEN) {
                    if (type == CatchClauseType.MARK_LOGIC) {
                        builder.error(XQueryBundle.message("parser.error.multiple-marklogic-catch-clause"))
                    }
                    builder.advanceLexer()

                    nextType = CatchClauseType.MARK_LOGIC

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                        builder.error(XPathBundle.message("parser.error.expected", "$"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", "VarName"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens(builder)
                    if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected", ")"))
                    }
                } else {
                    builder.error(XPathBundle.message("parser.error.expected", "CatchErrorList"))
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseEnclosedExprOrBlock(builder, null, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)

            marker.done(XQueryElementType.CATCH_CLAUSE)
            return nextType
        }
        return CatchClauseType.NONE
    }

    private fun parseCatchErrorList(builder: PsiBuilder): Boolean {
        if (parseNameTest(builder, XPathElementType.NAME_TEST, allowAxisStep = false) != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseNameTest(builder, XPathElementType.NAME_TEST, allowAxisStep = false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected", "NameTest"))
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: InsertExpr

    private fun parseInsertExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_INSERT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.INSERT_DELETE_NODE_TOKENS)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSourceExpr(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseInsertExprTargetChoice(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "after, as, before, into"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseTargetExpr(builder, null) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.INSERT_EXPR)
            return true
        }
        return false
    }

    private fun parseSourceExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder, null, XQueryElementType.SOURCE_EXPR)) {
            marker.done(XQueryElementType.SOURCE_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseInsertExprTargetChoice(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        when {
            builder.matchTokenType(XPathTokenType.K_AS) -> {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XQueryTokenType.INSERT_POSITION_TOKENS)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "first, last"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XQueryTokenType.K_INTO) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "into"))
                }

                marker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
                return true
            }
            builder.matchTokenType(XQueryTokenType.INSERT_LOCATION_TOKENS) -> {
                marker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
                return true
            }
            XQueryTokenType.INSERT_POSITION_TOKENS.contains(builder.tokenType) -> {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                builder.advanceLexer()

                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XQueryTokenType.K_INTO)

                marker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
                return true
            }
            else -> {
                marker.drop()
                return false
            }
        }

    }

    private fun parseTargetExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder, null, type)) {
            marker.done(XQueryElementType.TARGET_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: DeleteExpr

    private fun parseDeleteExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_DELETE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.INSERT_DELETE_NODE_TOKENS)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseTargetExpr(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.DELETE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: ReplaceExpr

    private fun parseReplaceExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_REPLACE)
        if (marker != null) {
            var haveErrors = false
            var haveValueOf = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XQueryTokenType.K_VALUE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_OF)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "of"))
                    haveErrors = true
                }
                haveValueOf = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NODE)) {
                if (!haveValueOf) {
                    marker.rollbackTo()
                    return false
                }
                if (!haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "node"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseTargetExpr(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_WITH) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "with"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.REPLACE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: RenameExpr

    private fun parseRenameExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_RENAME)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_NODE)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseTargetExpr(builder, XQueryElementType.TARGET_EXPR)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_AS) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "as"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseNewNameExpr(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.RENAME_EXPR)
            return true
        }
        return false
    }

    private fun parseNewNameExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            marker.done(XQueryElementType.NEW_NAME_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: CopyModifyExpr (TransformExpr)

    private fun parseCopyModifyExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_COPY)
        if (marker != null) {
            var haveErrors = false
            var isFirst = true
            do {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseCopyModifyExprBinding(builder, isFirst) && isFirst) {
                    marker.rollbackTo()
                    return false
                }

                isFirst = false
                parseWhiteSpaceAndCommentTokens(builder)
            } while (builder.matchTokenType(XPathTokenType.COMMA))

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_MODIFY)) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "modify"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.COPY_MODIFY_EXPR)
            return true
        }
        return false
    }

    private fun parseCopyModifyExprBinding(builder: PsiBuilder, isFirst: Boolean): Boolean {
        val marker = builder.mark()

        var haveErrors = false
        val matched = builder.matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            builder.error(XPathBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                builder.errorOnTokenType(XPathTokenType.EQUAL, XPathBundle.message("parser.error.expected", ":="))
            ) {
                haveErrors = true
            } else if (!builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ":="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.COPY_MODIFY_EXPR_BINDING)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: BlockExpr

    private fun parseBlockExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_BLOCK)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, XQueryElementType.BLOCK, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XQueryElementType.BLOCK_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: AssignmentExpr

    private fun parseAssignmentExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseEQNameOrWildcard(builder, XPathElementType.VAR_REF, false) == null) {
                builder.error(XPathBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.ASSIGN_EQUAL)) {
                // VarRef construct -- handle in the OrExpr parser for the correct AST.
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.ASSIGNMENT_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: ExitExpr

    private fun parseExitExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_EXIT)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            if (!builder.matchTokenType(XQueryTokenType.K_RETURNING)) {
                if (builder.tokenType === XPathTokenType.PARENTHESIS_OPEN) {
                    // FunctionCall construct
                    marker.rollbackTo()
                    return false
                }
                builder.error(XPathBundle.message("parser.error.expected-keyword", "returning"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                if (haveErrors) {
                    // AbbrevForwardStep construct
                    marker.rollbackTo()
                    return false
                }
                builder.error(XPathBundle.message("parser.error.expected-expression"))
            }

            marker.done(XQueryElementType.EXIT_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: WhileExpr

    private fun parseWhileExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_WHILE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseExprSingle(builder)) {
                builder.error(XPathBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                !parseEnclosedExprOrBlock(
                    builder, XQueryElementType.WHILE_BODY, BlockOpen.REQUIRED, BlockExpr.REQUIRED
                )
            ) {
                // FunctionCall construct. Check for reserved function name in the FunctionCall PSI class.
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.WHILE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr

    override fun parseTernaryConditionalExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseElvisExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.TERNARY_IF)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseTernaryConditionalExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected-either", "TernaryConditionalExpr", "ElvisExpr"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.TERNARY_ELSE)) {
                    builder.error(XPathBundle.message("parser.error.expected", "!!"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseTernaryConditionalExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected-either", "TernaryConditionalExpr", "ElvisExpr"))
                }

                marker.done(XPathElementType.TERNARY_CONDITIONAL_EXPR)
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
                    marker.done(XQueryElementType.ELVIS_EXPR)
                }
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    override fun parseAndExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseUpdateExpr(builder, type)) {
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

    private fun parseUpdateExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseComparisonExpr(builder, type)) {
            var haveUpdateExpr = false
            while (builder.matchTokenType(XQueryTokenType.K_UPDATE)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.tokenType === XPathTokenType.BLOCK_OPEN) {
                    haveUpdateExpr = true
                    parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)
                } else if (!parseExpr(builder, EXPR)) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                } else {
                    haveUpdateExpr = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (haveUpdateExpr)
                marker.done(XQueryElementType.UPDATE_EXPR)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    override fun parseComparisonExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseFTContainsExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseGeneralComp(builder) || parseValueComp(builder) || parseNodeComp(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseFTContainsExpr(builder, type)) {
                    builder.error(XPathBundle.message("parser.error.expected", "FTContainsExpr"))
                }
                marker.done(XPathElementType.COMPARISON_EXPR)
            } else {
                marker.drop()
            }
            return true
        } else if (
            builder.errorOnTokenType(
                XPathTokenType.LESS_THAN, XQueryBundle.message("parser.error.comparison-no-lhs-or-direlem")
            ) || builder.errorOnTokenType(
                XPathTokenType.COMP_SYMBOL_TOKENS, XPathBundle.message("parser.error.comparison-no-lhs")
            )
        ) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFTContainsExpr(builder, type)) {
                builder.error(XPathBundle.message("parser.error.expected", "FTContainsExpr"))
            }

            marker.done(XPathElementType.COMPARISON_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    override fun parseTreatExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseCastableExpr(builder, type)) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.matchTokenType(XPathTokenType.K_TREAT)) {
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
            } else if (
                builder.tokenType === XPathTokenType.K_AS &&
                type !== XQueryElementType.SOURCE_EXPR &&
                type !== XQueryElementType.TARGET_EXPR
            ) {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "cast, castable, treat"))
                builder.advanceLexer()

                parseWhiteSpaceAndCommentTokens(builder)
                parseSequenceType(builder)
                marker.drop()
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    override fun parseCastExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseTransformWithExpr(builder, type)) {
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

    private fun parseTransformWithExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseArrowExpr(builder, type)) {
            if (builder.matchTokenType(XQueryTokenType.K_TRANSFORM)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!builder.matchTokenType(XPathTokenType.K_WITH)) {
                    builder.error(XPathBundle.message("parser.error.expected-keyword", "with"))
                }

                parseWhiteSpaceAndCommentTokens(builder)
                parseEnclosedExprOrBlock(builder, null, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)

                marker.done(XQueryElementType.TRANSFORM_WITH_EXPR)
            } else {
                marker.drop()
            }
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: ValueExpr

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun parseValueExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return (
            parseExtensionExpr(builder) ||
            parseValidateExpr(builder) ||
            parseSimpleMapExpr(builder, type)
        )
    }

    private fun parseValidateExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_VALIDATE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            var blockOpen = BlockOpen.REQUIRED
            if (builder.matchTokenType(XQueryTokenType.VALIDATION_MODE_TOKENS)) {
                blockOpen = BlockOpen.OPTIONAL
            } else if (builder.matchTokenType(XQueryTokenType.VALIDATE_EXPR_TOKENS)) {
                blockOpen = BlockOpen.OPTIONAL

                parseWhiteSpaceAndCommentTokens(builder)
                if (parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false) == null) {
                    builder.error(XPathBundle.message("parser.error.expected", "TypeName"))
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, blockOpen, BlockExpr.REQUIRED)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.VALIDATE_EXPR)
            return true
        }
        return false
    }

    private fun parseExtensionExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var matched = false
        while (parsePragma(builder)) {
            matched = true
            parseWhiteSpaceAndCommentTokens(builder)
        }
        if (matched) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseEnclosedExprOrBlock(builder, null, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)
            marker.done(XQueryElementType.EXTENSION_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: StepExpr

    override fun parseStepExpr(builder: PsiBuilder, type: IElementType?): ParsedStepExpr = when {
        parsePostfixExpr(builder, type) -> ParsedStepExpr.Expression
        parseAxisStep(builder, type) -> ParsedStepExpr.Step
        else -> ParsedStepExpr.None
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun parsePrimaryExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        return (
            super.parsePrimaryExpr(builder, type) ||
            parseNonDeterministicFunctionCall(builder) ||
            parseOrderedExpr(builder) ||
            parseUnorderedExpr(builder) ||
            parseBinaryConstructor(builder) ||
            parseBooleanConstructor(builder) ||
            parseNodeConstructor(builder) ||
            parseNullConstructor(builder) ||
            parseNumberConstructor(builder) ||
            parseStringConstructor(builder)
        )
    }

    private fun parseOrderedExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ORDERED)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.ORDERED_EXPR)
            return true
        }
        return false
    }

    private fun parseUnorderedExpr(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_UNORDERED)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.UNORDERED_EXPR)
            return true
        }
        return false
    }

    private fun parseNonDeterministicFunctionCall(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_NON_DETERMINISTIC)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType != XPathTokenType.VARIABLE_INDICATOR) {
                marker.rollbackTo()
                return false
            }

            if (!parseVarOrParamRef(builder, null)) {
                builder.error(XPathBundle.message("parser.error.expected", "VarDecl"))
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseArgumentList(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "ArgumentList"))
                marker.drop()
            } else {
                marker.done(XQueryElementType.NON_DETERMINISTIC_FUNCTION_CALL)
            }
            return true
        }
        return false
    }

    private fun parseStringConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.STRING_CONSTRUCTOR_START)
        if (marker != null) {
            parseStringConstructorContent(builder)

            if (!builder.matchTokenType(XQueryTokenType.STRING_CONSTRUCTOR_END)) {
                builder.error(XQueryBundle.message("parser.error.incomplete-string-constructor"))
            }

            marker.done(XQueryElementType.STRING_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseStringConstructorContent(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        while (
            builder.matchTokenType(XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS) ||
            parseStringConstructorInterpolation(builder)
        ) {
            //
        }
        marker.done(XQueryElementType.STRING_CONSTRUCTOR_CONTENT)
        return true
    }

    private fun parseStringConstructorInterpolation(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.STRING_INTERPOLATION_OPEN)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            parseExpr(builder, EXPR)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.STRING_INTERPOLATION_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", "}`"))
            }

            marker.done(XQueryElementType.STRING_CONSTRUCTOR_INTERPOLATION)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: FunctionCall

    override fun parseFunctionCall(builder: PsiBuilder): Boolean {
        if (builder.tokenType is IKeywordOrNCNameType) {
            val type = builder.tokenType as IKeywordOrNCNameType?
            when (type!!.keywordType) {
                IKeywordOrNCNameType.KeywordType.KEYWORD,
                IKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                }
                IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME,
                IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME -> return false
                IKeywordOrNCNameType.KeywordType.MARKLOGIC60_RESERVED_FUNCTION_NAME,
                IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME,
                IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    // Don't keep the MarkLogic Schema/JSON parseTree here as KindTest is not anchored to the correct parent
                    // at this point.
                    val testMarker = builder.mark()
                    var status = parseSchemaKindTest(builder)
                    if (status == ParseStatus.NOT_MATCHED) {
                        status = parseJsonKindTest(builder)
                    }
                    if (status == ParseStatus.NOT_MATCHED) {
                        status = parseBinaryTest(builder)
                    }
                    testMarker.rollbackTo()

                    // If this is a valid MarkLogic Schema/JSON KindTest, return false here to parse it as a KindTest.
                    if (status == ParseStatus.MATCHED) {
                        return false
                    }
                }
            }
            // Otherwise, fall through to the FunctionCall parser to parse it as a FunctionCall to allow
            // standard XQuery to use these keywords as function names.
        }

        val marker = builder.mark()
        if (parseEQNameOrWildcard(builder, XPathElementType.QNAME, false) != null) {
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

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: FunctionItemExpr

    override fun parseInlineFunctionExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        var haveAnnotations = false
        while (parseAnnotation(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            haveAnnotations = true
        }

        val token = builder.tokenType
        if (builder.matchTokenType(XPathTokenType.INLINE_FUNCTION_TOKENS)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseFunctionSignature(builder, required = token === XPathTokenType.K_FUNCTION)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (builder.tokenType === XPathTokenType.BLOCK_OPEN) {
                    //
                } else if (!haveAnnotations) {
                    marker.rollbackTo()
                    return false
                } else {
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "{"))
                parseExpr(builder, EXPR)

                parseWhiteSpaceAndCommentTokens(builder)
                builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)
            }

            marker.done(XPathElementType.INLINE_FUNCTION_EXPR)
            return true
        } else if (haveAnnotations) {
            builder.error(XPathBundle.message("parser.error.expected-either", "function", "->"))

            marker.drop()
            return true
        }

        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: Constructors

    private fun parseBinaryConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_BINARY)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XQueryElementType.BINARY_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseBooleanConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_BOOLEAN_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XQueryElementType.BOOLEAN_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseNodeConstructor(builder: PsiBuilder): Boolean {
        return parseDirectConstructor(builder, 0) || parseComputedConstructor(builder)
    }

    private fun parseNullConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NULL_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.BLOCK_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", "}"))
            }

            marker.done(XQueryElementType.NULL_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseNumberConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NUMBER_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                marker.rollbackTo()
                return false
            }
            marker.done(XQueryElementType.NUMBER_CONSTRUCTOR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: NodeConstructor :: DirectConstructor

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun parseDirectConstructor(builder: PsiBuilder, depth: Int): Boolean {
        return (
            parseDirElemConstructor(builder, depth) ||
            parseDirCommentConstructor(builder) ||
            parseDirPIConstructor(builder) ||
            parseCDataSection(builder, null)
        )
    }

    private fun parseDirElemConstructor(builder: PsiBuilder, depth: Int): Boolean {
        val marker = builder.mark()
        if (builder.matchTokenType(XQueryTokenType.OPEN_XML_TAG)) {
            builder.errorOnTokenType(
                XQueryTokenType.XML_WHITE_SPACE, XQueryBundle.message("parser.error.unexpected-whitespace")
            )
            parseQNameOrWildcard(builder, XPathElementType.QNAME, false)

            parseDirAttributeList(builder)

            if (!builder.matchTokenType(XQueryTokenType.SELF_CLOSING_XML_TAG)) {
                if (builder.matchTokenType(XQueryTokenType.END_XML_TAG)) {
                    parseDirElemContent(builder, depth + 1)

                    if (builder.matchTokenType(XQueryTokenType.CLOSE_XML_TAG)) {
                        var haveError = false
                        if (parseQNameOrWildcard(builder, XPathElementType.QNAME, false) == null) {
                            builder.error(XQueryBundle.message("parser.error.expected-qname"))
                            haveError = true
                        }

                        builder.matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
                        if (!builder.matchTokenType(XQueryTokenType.END_XML_TAG) && !haveError) {
                            builder.error(XPathBundle.message("parser.error.expected", ">"))
                        }
                    } else {
                        builder.error(XQueryBundle.message("parser.error.expected-closing-tag"))
                    }
                } else {
                    builder.error(XQueryBundle.message("parser.error.incomplete-open-tag"))
                }
            }

            marker.done(XQueryElementType.DIR_ELEM_CONSTRUCTOR)
            return true
        } else if (depth == 0 && builder.tokenType === XQueryTokenType.CLOSE_XML_TAG) {
            builder.error(XQueryBundle.message("parser.error.unexpected-closing-tag"))
            builder.matchTokenType(XQueryTokenType.CLOSE_XML_TAG)
            parseQNameOrWildcard(builder, XPathElementType.QNAME, false)
            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
            builder.matchTokenType(XPathTokenType.GREATER_THAN)

            marker.done(XQueryElementType.DIR_ELEM_CONSTRUCTOR)
            return true
        }

        marker.drop()
        return false
    }

    private fun parseDirAttributeList(builder: PsiBuilder): Boolean {
        // NOTE: The XQuery grammar uses whitespace as the token to start the next iteration of the matching loop.
        // Because the parseQName function can consume that whitespace during error handling, the QName tokens are
        // used as the next iteration marker in this implementation.
        var parsed = builder.matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
        while (parseDirAttribute(builder)) {
            parsed = true
            builder.matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
        }
        return parsed
    }

    private fun parseDirAttribute(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseQNameOrWildcard(builder, XPathElementType.QNAME, false) != null) {
            var haveErrors = false

            builder.matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
            if (!builder.matchTokenType(XQueryTokenType.XML_EQUAL)) {
                builder.error(XPathBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            builder.matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
            if (!parseDirAttributeValue(builder) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-attribute-string"))
            }

            marker.done(XQueryElementType.DIR_ATTRIBUTE)
            return true
        }
        marker.drop()
        return false
    }

    private fun parseDirAttributeValue(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
        while (marker != null) {
            if (
                builder.matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS) ||
                builder.matchTokenType(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE) ||
                builder.matchTokenType(XQueryTokenType.XML_CHARACTER_REFERENCE) ||
                builder.matchTokenType(XQueryTokenType.XML_ESCAPED_CHARACTER)
            ) {
                continue
            } else if (builder.matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_END)) {
                marker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE)
                return true
            } else if (builder.matchTokenType(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)) {
                builder.error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (
                builder.errorOnTokenType(
                    XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")
                ) || builder.matchTokenType(XPathTokenType.BAD_CHARACTER)
            ) {
                continue
            } else if (
                parseEnclosedExprOrBlock(
                    builder, XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL
                ) || builder.errorOnTokenType(
                    XPathTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr")
                )
            ) {
                continue
            } else {
                marker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE)
                builder.error(XQueryBundle.message("parser.error.incomplete-attribute-value"))
                return true
            }
        }
        return false
    }

    private fun parseDirCommentConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.XML_COMMENT_START_TAG)
        if (marker != null) {
            // NOTE: XQueryTokenType.XML_COMMENT is omitted by the PsiBuilder.
            if (builder.matchTokenType(XQueryTokenType.XML_COMMENT_END_TAG)) {
                marker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR)
            } else {
                builder.advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                marker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR)
                builder.error(XQueryBundle.message("parser.error.incomplete-xml-comment"))
            }
            return true
        }

        return builder.errorOnTokenType(
            XQueryTokenType.XML_COMMENT_END_TAG,
            XPathBundle.message("parser.error.end-of-comment-without-start", "<!--")
        )
    }

    private fun parseDirPIConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        if (marker != null) {
            var haveErrors = false

            if (builder.matchTokenType(XPathTokenType.WHITE_SPACE)) {
                builder.error(XQueryBundle.message("parser.error.unexpected-whitespace"))
                haveErrors = true
            }

            if (parseQNameOrWildcard(builder, XPathElementType.NCNAME, false) == null && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
            if (!builder.matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS) && !haveErrors) {
                builder.error(XQueryBundle.message("parser.error.expected-pi-contents"))
                haveErrors = true
            }

            while (
                builder.matchTokenType(XPathTokenType.BAD_CHARACTER) ||
                builder.matchTokenType(XPathTokenType.NCNAME) ||
                builder.matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            ) {
                //
            }

            if (!builder.matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_END) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "?>"))
            }

            marker.done(XQueryElementType.DIR_PI_CONSTRUCTOR)
            return true
        }

        return false
    }

    private fun parseDirElemContent(builder: PsiBuilder, depth: Int): Boolean {
        var matched = false
        while (true) {
            if (
                parseDirTextConstructor(builder) ||
                parseEnclosedExprOrBlock(
                    builder, XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL
                ) ||
                parseCDataSection(builder, XQueryElementType.DIR_ELEM_CONSTRUCTOR) ||
                parseDirectConstructor(builder, depth)
            ) {
                matched = true
            } else {
                return matched
            }
        }
    }

    private fun parseDirTextConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var matched = false
        while (true) {
            if (
                builder.matchTokenType(XQueryTokenType.XML_ELEMENT_CONTENTS) ||
                builder.matchTokenType(XPathTokenType.BAD_CHARACTER) ||
                builder.matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                builder.matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER) ||
                builder.errorOnTokenType(
                    XPathTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr")
                ) ||
                builder.errorOnTokenType(
                    XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")
                )
            ) {
                matched = true
            } else if (builder.matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                builder.error(XQueryBundle.message("parser.error.incomplete-entity"))
                matched = true
            } else {
                if (matched) {
                    marker.done(XQueryElementType.DIR_TEXT_CONSTRUCTOR)
                } else {
                    marker.drop()
                }
                return matched
            }
        }
    }

    private fun parseCDataSection(builder: PsiBuilder, context: IElementType?): Boolean {
        val marker = builder.mark()
        val errorMarker = builder.mark()
        if (builder.matchTokenType(XQueryTokenType.CDATA_SECTION_START_TAG)) {
            if (context == null) {
                errorMarker.error(XQueryBundle.message("parser.error.cdata-section-not-in-element-content"))
            } else {
                errorMarker.drop()
            }

            builder.matchTokenType(XQueryTokenType.CDATA_SECTION)
            if (builder.matchTokenType(XQueryTokenType.CDATA_SECTION_END_TAG)) {
                marker.done(XQueryElementType.CDATA_SECTION)
            } else {
                builder.advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                marker.done(XQueryElementType.CDATA_SECTION)
                builder.error(XQueryBundle.message("parser.error.incomplete-cdata-section"))
            }
            return true
        }

        errorMarker.drop()
        marker.drop()
        return builder.errorOnTokenType(
            XQueryTokenType.CDATA_SECTION_END_TAG,
            XQueryBundle.message("parser.error.end-of-cdata-section-without-start")
        )
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: PrimaryExpr :: NodeConstructor :: ComputedConstructor

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    private fun parseComputedConstructor(builder: PsiBuilder): Boolean {
        return (
            parseCompDocConstructor(builder) ||
            parseCompElemConstructor(builder) ||
            parseCompAttrConstructor(builder) ||
            parseCompNamespaceConstructor(builder) ||
            parseCompTextConstructor(builder) ||
            parseCompCommentConstructor(builder) ||
            parseCompPIConstructor(builder)
        )
    }

    private fun parseCompDocConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.COMP_DOC_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompElemConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ELEMENT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            val name = parseEQNameOrWildcard(builder, XPathElementType.QNAME, false)
            if (name == null && !parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (builder.tokenType === XPathTokenType.STRING_LITERAL_START) {
                    val errorMarker = builder.mark()
                    parseStringLiteral(builder)
                    errorMarker.error(XQueryBundle.message("parser.error.expected-qname-or-braced-expression"))
                } else {
                    marker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                if (name is IKeywordOrNCNameType) {
                    // This may be a continuation keyword from another expression (e.g. 'return' from a FLWORExpr).
                    marker.rollbackTo()
                    return false
                } else {
                    builder.error(XQueryBundle.message("parser.error.expected-enclosed-expression"))
                }
            }

            marker.done(XQueryElementType.COMP_ELEM_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompAttrConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ATTRIBUTE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            val name = parseEQNameOrWildcard(builder, XPathElementType.QNAME, false)
            if (name == null && !parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (builder.tokenType === XPathTokenType.STRING_LITERAL_START) {
                    val errorMarker = builder.mark()
                    parseStringLiteral(builder)
                    errorMarker.error(XQueryBundle.message("parser.error.expected-qname-or-braced-expression"))
                } else {
                    marker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                if (name is IKeywordOrNCNameType) {
                    // This may be a continuation keyword from another expression (e.g. 'return' from a FLWORExpr).
                    marker.rollbackTo()
                    return false
                } else {
                    builder.error(XQueryBundle.message("parser.error.expected-enclosed-expression"))
                }
            }

            marker.done(XQueryElementType.COMP_ATTR_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompNamespaceConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NAMESPACE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            val name = parseEQNameOrWildcard(builder, XPathElementType.NCNAME, false)
            if (
                name == null &&
                !parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)
            ) {
                if (builder.tokenType === XPathTokenType.STRING_LITERAL_START) {
                    val errorMarker = builder.mark()
                    parseStringLiteral(builder)
                    errorMarker.error(XQueryBundle.message("parser.error.expected-identifier-or-braced-expression"))
                } else {
                    marker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (
                !parseEnclosedExprOrBlock(
                    builder, XQueryElementType.ENCLOSED_URI_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL
                )
            ) {
                if (name is IKeywordOrNCNameType) {
                    // This may be a continuation keyword from another expression (e.g. 'return' from a FLWORExpr).
                    marker.rollbackTo()
                    return false
                } else {
                    builder.error(XQueryBundle.message("parser.error.expected-enclosed-expression"))
                }
            }

            marker.done(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompTextConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TEXT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.COMP_TEXT_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompCommentConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_COMMENT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                marker.rollbackTo()
                return false
            }

            marker.done(XQueryElementType.COMP_COMMENT_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompPIConstructor(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_PROCESSING_INSTRUCTION)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseQNameSeparator(builder, null)) { // QName
                marker.rollbackTo()
                return false
            }

            val name = parseQNameOrWildcard(builder, XPathElementType.NCNAME, false)
            if (name == null && !parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (builder.tokenType === XPathTokenType.STRING_LITERAL_START) {
                    val errorMarker = builder.mark()
                    parseStringLiteral(builder)
                    errorMarker.error(XQueryBundle.message("parser.error.expected-identifier-or-braced-expression"))
                } else {
                    marker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                if (name is IKeywordOrNCNameType) {
                    // This may be a continuation keyword from another expression (e.g. 'return' from a FLWORExpr).
                    marker.rollbackTo()
                    return false
                } else {
                    builder.error(XQueryBundle.message("parser.error.expected-enclosed-expression"))
                }
            }

            marker.done(XQueryElementType.COMP_PI_CONSTRUCTOR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryConditionalExpr :: FTMatchOptions

    override val FTMATCH_OPTION_START_TOKENS: TokenSet = XQueryTokenType.FTMATCH_OPTION_START_TOKENS
    override val URI_LITERAL: IElementType = XQueryElementType.URI_LITERAL

    override fun parseFTMatchOption(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (
            parseFTCaseOption(builder, marker) ||
            parseFTDiacriticsOption(builder, marker) ||
            parseFTExtensionOption(builder, marker) ||
            parseFTFuzzyOption(builder, marker) ||
            parseFTLanguageOption(builder, marker) ||
            parseFTStemOption(builder, marker) ||
            parseFTStopWordOption(builder, marker) ||
            parseFTThesaurusOption(builder, marker) ||
            parseFTWildCardOption(builder, marker)
        ) {
            return true
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
            // NOTE: `fuzzy` is the BaseX FTMatchOption extension.
            builder.error(XPathBundle.message("parser.error.expected-keyword-or-token", "FTMatchOption", "fuzzy"))
            marker.drop()
            return false
        }
        return true
    }

    private fun parseFTFuzzyOption(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        if (builder.matchTokenType(XQueryTokenType.K_FUZZY)) {
            marker.done(XQueryElementType.FT_FUZZY_OPTION)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: UpdatingFunctionCall

    private fun parseUpdatingFunctionCall(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_INVOKE)
        if (marker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XQueryTokenType.K_UPDATING)) {
                if (builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) { // FunctionCall
                    marker.rollbackTo()
                    return false
                }

                builder.error(XPathBundle.message("parser.error.expected-keyword", "updating"))
                haveErrors = true

                parseWhiteSpaceAndCommentTokens(builder)
                if (!parsePrimaryExpr(builder, null)) { // AbbrevForwardStep
                    marker.rollbackTo()
                    return false
                }
            } else {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parsePrimaryExpr(builder, null)) {
                    builder.error(XPathBundle.message("parser.error.expected", "PrimaryExpr"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", "("))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parseExprSingle(builder)) {
                parseWhiteSpaceAndCommentTokens(builder)
                while (builder.matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens(builder)
                    if (parseExprSingle(builder) && !haveErrors) {
                        builder.error(XPathBundle.message("parser.error.expected-expression"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens(builder)
                }
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XQueryElementType.UPDATING_FUNCTION_CALL)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: SequenceType

    private fun parseSequenceTypeUnion(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseSequenceTypeList(builder)) {
            var haveErrors = false
            var haveSequenceTypeUnion = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.UNION)) {
                haveSequenceTypeUnion = true
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseSequenceTypeList(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (haveSequenceTypeUnion)
                marker.done(XQueryElementType.SEQUENCE_TYPE_UNION)
            else
                marker.drop()
            return true
        }
        marker.drop()
        return false
    }

    override fun parseSequenceType(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        when {
            builder.matchTokenType(XPathTokenType.SEQUENCE_TYPE_TOKENS) -> {
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
            }
            parseItemType(builder) -> {
                parseWhiteSpaceAndCommentTokens(builder)
                if (parseOccurrenceIndicator(builder))
                    marker.done(XPathElementType.SEQUENCE_TYPE)
                else
                    marker.drop()
                return true
            }
            parseParenthesizedSequenceType(builder) -> {
                marker.drop()
                return true
            }
            else -> {
                marker.drop()
                return false
            }
        }

    }

    private fun parseParenthesizedSequenceType(builder: PsiBuilder): Boolean {
        if (builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            if (!parseSequenceTypeUnion(builder)) {
                builder.error(XPathBundle.message("parser.error.expected", "SequenceType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: ItemType

    override fun parseFunctionTest(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        var haveAnnotations = false
        while (parseAnnotation(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            haveAnnotations = true
        }

        return when {
            !haveAnnotations -> super.parseFunctionTest(builder, marker)
            super.parseFunctionTest(builder, marker) -> true
            else -> {
                builder.error(XPathBundle.message("parser.error.expected-keyword", "function"))
                true
            }
        }
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    override val EXTERNAL_KEYWORD: IElementType = XQueryTokenType.K_EXTERNAL

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun parseKindTest(builder: PsiBuilder): Boolean {
        return (
            super.parseKindTest(builder) ||
            parseBinaryTest(builder) != ParseStatus.NOT_MATCHED ||
            parseSchemaKindTest(builder) != ParseStatus.NOT_MATCHED ||
            parseJsonKindTest(builder) != ParseStatus.NOT_MATCHED
        )
    }

    override fun parseAnyKindTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            val type = if (parseStringLiteral(builder)) { // MarkLogic 8.0
                XQueryElementType.NAMED_KIND_TEST
            } else {
                builder.matchTokenType(XPathTokenType.STAR) // MarkLogic 8.0
                XPathElementType.ANY_KIND_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(type)
            return true
        }
        return false
    }

    override fun parseDocumentTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_DOCUMENT_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseElementTest(builder) ||
                    parseSchemaElementTest(builder) ||
                    parseAnyArrayNodeTest(builder) != ParseStatus.NOT_MATCHED ||
                    parseAnyMapNodeTest(builder) != ParseStatus.NOT_MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.DOCUMENT_TEST)
            return true
        }
        return false
    }

    override fun parseTextTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TEXT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            if (parseStringLiteral(builder)) {
                type = XQueryElementType.NAMED_TEXT_TEST
            } else {
                type = XPathElementType.ANY_TEXT_TEST
                builder.errorOnTokenType(
                    XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                ) // MarkLogic 8.0
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(type)
            return true
        }
        return false
    }

    private fun parseBinaryTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_BINARY)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XQueryElementType.BINARY_TEST)
            return ParseStatus.MATCHED
        }
        return ParseStatus.NOT_MATCHED
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest :: SchemaKindTest

    private fun parseAttribNameOrWildcard(builder: PsiBuilder): Boolean {
        return this.parseEQNameOrWildcard(builder, XPathElementType.WILDCARD, isElementOrAttributeName = true) != null
    }

    private fun parseElementNameOrWildcard(builder: PsiBuilder): Boolean {
        return this.parseEQNameOrWildcard(builder, XPathElementType.WILDCARD, isElementOrAttributeName = true) != null
    }

    private fun parseSchemaKindTest(builder: PsiBuilder): ParseStatus {
        var status = parseAttributeDeclTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseComplexTypeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseElementDeclTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseModelGroupTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaComponentTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaFacetTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaParticleTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaRootTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaTypeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaWildcardTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseSimpleTypeTest(builder)
        return status
    }

    private fun parseAttributeDeclTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE_DECL)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseAttribNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.ATTRIBUTE_DECL_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseComplexTypeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_COMPLEX_TYPE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.COMPLEX_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseElementDeclTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT_DECL)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseElementNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.ELEMENT_DECL_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseModelGroupTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_MODEL_GROUP)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseElementNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.MODEL_GROUP_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaComponentTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_COMPONENT)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_COMPONENT_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaFacetTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_FACET)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseElementNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_FACET_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaParticleTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_PARTICLE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseElementNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_PARTICLE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaRootTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ROOT)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_ROOT_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaTypeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_TYPE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaWildcardTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_WILDCARD)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SCHEMA_WILDCARD_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSimpleTypeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XQueryTokenType.K_SIMPLE_TYPE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens(builder)
            parseTypeNameOrWildcard(builder)

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(XQueryElementType.SIMPLE_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    fun parseTypeNameOrWildcard(builder: PsiBuilder): Boolean {
        return (
            builder.matchTokenType(XPathTokenType.STAR) ||
            this.parseEQNameOrWildcard(builder, XPathElementType.TYPE_NAME, false) != null
        )
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest :: JsonKindTest

    private fun parseJsonKindTest(builder: PsiBuilder): ParseStatus {
        var status = parseArrayNodeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseBooleanNodeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseNullNodeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseNumberNodeTest(builder)
        if (status == ParseStatus.NOT_MATCHED) status = parseMapNodeTest(builder)
        return status
    }

    private fun parseAnyArrayNodeTest(builder: PsiBuilder): ParseStatus = parseArrayNodeTest(builder, true)

    private fun parseArrayNodeTest(builder: PsiBuilder, isAnyOnly: Boolean = false): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_ARRAY_NODE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            if (isAnyOnly && builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS

                // array-node() tests in a document-node test do not allow `StringLiteral` or `*`
                // tokens, but accept them here to recover when used incorrectly.
                parseStringLiteral(builder)
                builder.matchTokenType(XPathTokenType.STAR)
            } else if (parseStringLiteral(builder)) {
                type = XQueryElementType.NAMED_ARRAY_NODE_TEST
            } else if (
                builder.errorOnTokenType(
                    XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                )
            ) {
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseBooleanNodeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_BOOLEAN_NODE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseStringLiteral(builder) -> type = XQueryElementType.NAMED_BOOLEAN_NODE_TEST
                builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE -> {
                    builder.errorOnTokenType(
                        XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                    )
                    type = XQueryElementType.ANY_BOOLEAN_NODE_TEST
                    status = ParseStatus.MATCHED_WITH_ERRORS
                }
                else -> type = XQueryElementType.ANY_BOOLEAN_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseAnyMapNodeTest(builder: PsiBuilder): ParseStatus = parseMapNodeTest(builder, true)

    private fun parseMapNodeTest(builder: PsiBuilder, isAnyOnly: Boolean = false): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_OBJECT_NODE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            if (isAnyOnly && builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                type = XQueryElementType.ANY_MAP_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS

                // object-node() tests in a document-node test do not allow `StringLiteral` or `*`
                // tokens, but accept them here to recover when used incorrectly.
                parseStringLiteral(builder)
                builder.matchTokenType(XPathTokenType.STAR)
            } else if (parseStringLiteral(builder)) {
                type = XQueryElementType.NAMED_MAP_NODE_TEST
            } else if (
                builder.errorOnTokenType(
                    XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                )
            ) {
                type = XQueryElementType.ANY_MAP_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_MAP_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseNullNodeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NULL_NODE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseStringLiteral(builder) -> type = XQueryElementType.NAMED_NULL_NODE_TEST
                builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE -> {
                    builder.errorOnTokenType(
                        XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                    )
                    type = XQueryElementType.ANY_NULL_NODE_TEST
                    status = ParseStatus.MATCHED_WITH_ERRORS
                }
                else -> type = XQueryElementType.ANY_NULL_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseNumberNodeTest(builder: PsiBuilder): ParseStatus {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NUMBER_NODE)
        if (marker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens(builder)
            when {
                parseStringLiteral(builder) -> type = XQueryElementType.NAMED_NUMBER_NODE_TEST
                builder.tokenType !== XPathTokenType.PARENTHESIS_CLOSE -> {
                    builder.errorOnTokenType(
                        XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")
                    )
                    type = XQueryElementType.ANY_NUMBER_NODE_TEST
                    status = ParseStatus.MATCHED_WITH_ERRORS
                }
                else -> type = XQueryElementType.ANY_NUMBER_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            marker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols

    override val COMMENT: IElementType = XQueryElementType.COMMENT

    override fun parseStringLiteral(builder: PsiBuilder, type: IElementType): Boolean {
        val stringMarker = builder.matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        while (stringMarker != null) {
            if (
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                builder.matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                builder.matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER)
            ) {
                continue
            } else if (builder.matchTokenType(XPathTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type)
                return true
            } else if (builder.matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                builder.error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (
                builder.errorOnTokenType(
                    XQueryTokenType.EMPTY_ENTITY_REFERENCE,
                    XQueryBundle.message("parser.error.empty-entity")
                ) ||
                builder.matchTokenType(XPathTokenType.BAD_CHARACTER)
            ) {
                continue
            } else {
                stringMarker.done(type)
                builder.error(XPathBundle.message("parser.error.incomplete-string"))
                return true
            }
        }
        return false
    }

    override fun parseCommentContents(builder: PsiBuilder): Boolean {
        while (builder.matchTokenType(XQDocTokenType.XQDOC_TOKENS)) {
            // Skip unparsed XQDoc tokens so the calling parser can parse its content correctly.
        }
        return false
    }

    override fun parseWhiteSpaceAndCommentTokens(builder: PsiBuilder): Boolean {
        var skipped = false
        while (true) {
            if (
                builder.tokenType === XPathTokenType.WHITE_SPACE ||
                builder.tokenType === XQueryTokenType.XML_WHITE_SPACE
            ) {
                skipped = true
                builder.advanceLexer()
            } else if (
                parseComment(builder) ||
                builder.errorOnTokenType(
                    XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING,
                    XQueryBundle.message("parser.error.misplaced-entity")
                )
            ) {
                skipped = true
            } else {
                return skipped
            }
        }
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: EQName

    override val URI_QUALIFIED_NAME: IElementType = XQueryElementType.URI_QUALIFIED_NAME
    override val BRACED_URI_LITERAL: IElementType = XQueryElementType.BRACED_URI_LITERAL

    override fun parseBracedURILiteral(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.BRACED_URI_LITERAL_START)
        while (marker != null) {
            if (
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                builder.matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                builder.matchTokenType(XQueryTokenType.CHARACTER_REFERENCE)
            ) {
                continue
            } else if (builder.matchTokenType(XPathTokenType.BRACED_URI_LITERAL_END)) {
                marker.done(XQueryElementType.BRACED_URI_LITERAL)
                return true
            } else if (builder.matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                builder.error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (
                builder.errorOnTokenType(
                    XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")
                ) ||
                builder.matchTokenType(XPathTokenType.BAD_CHARACTER)
            ) {
                continue
            } else {
                marker.done(XQueryElementType.BRACED_URI_LITERAL)
                builder.error(XPathBundle.message("parser.error.incomplete-braced-uri-literal"))
                return true
            }
        }
        return false
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: QName

    override fun parseQNameSeparator(builder: PsiBuilder, type: IElementType?): Boolean {
        if (
            builder.tokenType === XPathTokenType.QNAME_SEPARATOR ||
            builder.tokenType === XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR ||
            builder.tokenType === XQueryTokenType.XML_TAG_QNAME_SEPARATOR
        ) {
            if (type === XPathElementType.NCNAME) {
                val errorMarker = builder.mark()
                builder.advanceLexer()
                errorMarker.error(XPathBundle.message("parser.error.expected-ncname-not-qname"))
            } else if (type != null) {
                builder.advanceLexer()
            }
            return true
        }
        return false
    }

    // endregion
}
