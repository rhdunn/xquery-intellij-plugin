/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.parser.PsiTreeParser
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XQueryParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        XQueryParserImpl(builder).parse()
        rootMarker.done(root)
        return builder.treeBuilt
    }
}

private enum class KindTest {
    ANY_TEST,
    TYPED_TEST,
}

private val COMPATIBILITY_ANNOTATION_TOKENS = TokenSet.create(
    XQueryTokenType.K_ASSIGNABLE,
    XQueryTokenType.K_PRIVATE,
    XQueryTokenType.K_SEQUENTIAL,
    XQueryTokenType.K_SIMPLE,
    XQueryTokenType.K_UNASSIGNABLE,
    XQueryTokenType.K_UPDATING
)

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
private class XQueryParserImpl(builder: PsiBuilder) : PsiTreeParser(builder) {
    // region Main Interface

    fun parse() {
        var matched = false
        var haveError = false
        while (getTokenType() != null) {
            if (parseWhiteSpaceAndCommentTokens()) continue
            if (matched && !haveError) {
                error(XQueryBundle.message("parser.error.expected-eof"))
                haveError = true
            }

            if (parseTransactions(!matched && !haveError)) {
                matched = true
                continue
            }

            if (haveError) {
                advanceLexer()
            } else {
                val errorMarker = mark()
                advanceLexer()
                errorMarker.error(XQueryBundle.message("parser.error.unexpected-token"))
                haveError = true
            }
        }
    }

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

    private fun parseTransactions(isFirst: Boolean): Boolean {
        if (parseModule(isFirst)) {
            parseWhiteSpaceAndCommentTokens()
            while (parseTransactionSeparator() != TransactionType.NONE) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseModule(false)) { // NOTE: Handles error cases for VersionDecl-only and library modules.
                    error(XQueryBundle.message("parser.error.expected", "MainModule"))
                }
                parseWhiteSpaceAndCommentTokens()
            }
            return true
        }
        return false
    }

    private fun parseTransactionSeparator(): TransactionType {
        val transactionSeparatorMarker = matchTokenTypeWithMarker(XQueryTokenType.SEPARATOR)
        if (transactionSeparatorMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            var haveProlog = false
            if (getTokenType() === XQueryTokenType.K_XQUERY ||
                    getTokenType() === XQueryTokenType.K_DECLARE ||
                    getTokenType() === XQueryTokenType.K_IMPORT ||
                    getTokenType() === XQueryTokenType.K_MODULE) {

                val marker = mark()
                advanceLexer()
                parseWhiteSpaceAndCommentTokens()
                haveProlog = getTokenType() is IKeywordOrNCNameType
                marker.rollbackTo()
            }

            transactionSeparatorMarker.done(XQueryElementType.TRANSACTION_SEPARATOR)
            return if (haveProlog) TransactionType.WITH_PROLOG else TransactionType.WITHOUT_PROLOG
        }
        return TransactionType.NONE
    }

    private fun parseModule(isFirst: Boolean): Boolean {
        var hasVersionDeclOrWhitespace: Boolean = parseVersionDecl()
        hasVersionDeclOrWhitespace = hasVersionDeclOrWhitespace or parseWhiteSpaceAndCommentTokens()

        val moduleMarker = mark()
        if (parseLibraryModule()) {
            moduleMarker.done(XQueryElementType.LIBRARY_MODULE)
            return true
        } else if (parseMainModule()) {
            moduleMarker.done(XQueryElementType.MAIN_MODULE)
            return true
        }

        if (isFirst) {
            error(XQueryBundle.message("parser.error.expected-module-type"))
        }
        moduleMarker.drop()
        return hasVersionDeclOrWhitespace
    }

    private fun parseVersionDecl(): Boolean {
        val versionDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_XQUERY)
        if (versionDeclMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected-encoding-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            } else {
                if (!matchTokenType(XQueryTokenType.K_VERSION)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "version"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-version-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-encoding-string"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens()
                }
            }

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                versionDeclMarker.done(XQueryElementType.VERSION_DECL)
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ";"))
                }
                if (getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
                    advanceLexer()
                }
                return true
            }

            versionDeclMarker.done(XQueryElementType.VERSION_DECL)
            return true
        }
        return false
    }

    private fun parseMainModule(): Boolean {
        if (parseProlog(false)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.QUERY_BODY)) {
                error(XQueryBundle.message("parser.error.expected-query-body"))
            }
            return true
        }
        return parseExpr(XQueryElementType.QUERY_BODY)
    }

    private fun parseLibraryModule(): Boolean {
        if (parseModuleDecl()) {
            parseWhiteSpaceAndCommentTokens()
            parseProlog(true)
            return true
        }
        return false
    }

    private fun parseModuleDecl(): Boolean {
        val moduleDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_MODULE)
        if (moduleDeclMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseQName(XQueryElementType.NCNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                moduleDeclMarker.done(XQueryElementType.MODULE_DECL)
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ";"))
                }
                if (getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
                    advanceLexer()
                }
                return true
            }

            moduleDeclMarker.done(XQueryElementType.MODULE_DECL)
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

    private fun parseProlog(parseInvalidConstructs: Boolean): Boolean {
        val prologMarker = mark()

        var state = PrologDeclState.NOT_MATCHED
        while (true) {
            var nextState = parseDecl(if (state == PrologDeclState.NOT_MATCHED) PrologDeclState.HEADER_STATEMENT else state)
            if (nextState == PrologDeclState.NOT_MATCHED) {
                nextState = parseImport(if (state == PrologDeclState.NOT_MATCHED) PrologDeclState.HEADER_STATEMENT else state)
            }

            when (nextState) {
                XQueryParserImpl.PrologDeclState.NOT_MATCHED -> if (parseInvalidConstructs && getTokenType() != null) {
                    if (!parseWhiteSpaceAndCommentTokens()) {
                        error(XQueryBundle.message("parser.error.unexpected-token"))
                        if (!parseExprSingle()) advanceLexer()
                    }
                } else {
                    if (state == PrologDeclState.NOT_MATCHED) {
                        prologMarker.drop()
                        return false
                    }
                    prologMarker.done(XQueryElementType.PROLOG)
                    return true
                }
                XQueryParserImpl.PrologDeclState.HEADER_STATEMENT, XQueryParserImpl.PrologDeclState.UNKNOWN_STATEMENT -> if (state == PrologDeclState.NOT_MATCHED) {
                    state = PrologDeclState.HEADER_STATEMENT
                }
                XQueryParserImpl.PrologDeclState.BODY_STATEMENT -> if (state != PrologDeclState.BODY_STATEMENT) {
                    state = PrologDeclState.BODY_STATEMENT
                }
            }

            if (nextState != PrologDeclState.NOT_MATCHED) {
                if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                    error(XQueryBundle.message("parser.error.expected", ";"))
                    if (getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
                        advanceLexer()
                    }
                }
                parseWhiteSpaceAndCommentTokens()
            }
        }
    }

    private fun parseDecl(state: PrologDeclState): PrologDeclState {
        val declMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE)
        if (declMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (parseBaseURIDecl(state)) {
                declMarker.done(XQueryElementType.BASE_URI_DECL)
            } else if (parseBoundarySpaceDecl(state)) {
                declMarker.done(XQueryElementType.BOUNDARY_SPACE_DECL)
            } else if (parseConstructionDecl(state)) {
                declMarker.done(XQueryElementType.CONSTRUCTION_DECL)
            } else if (parseCopyNamespacesDecl(state)) {
                declMarker.done(XQueryElementType.COPY_NAMESPACES_DECL)
            } else if (parseDecimalFormatDecl(state, false)) {
                declMarker.done(XQueryElementType.DECIMAL_FORMAT_DECL)
            } else if (parseDefaultDecl(declMarker, state)) {
            } else if (parseNamespaceDecl(state)) {
                declMarker.done(XQueryElementType.NAMESPACE_DECL)
            } else if (parseOptionDecl()) {
                declMarker.done(XQueryElementType.OPTION_DECL)
                return PrologDeclState.BODY_STATEMENT
            } else if (parseOrderingModeDecl(state)) {
                declMarker.done(XQueryElementType.ORDERING_MODE_DECL)
            } else if (parseRevalidationDecl(state)) {
                declMarker.done(XQueryElementType.REVALIDATION_DECL)
            } else if (parseAnnotatedDecl()) {
                declMarker.done(XQueryElementType.ANNOTATED_DECL)
                return PrologDeclState.BODY_STATEMENT
            } else if (parseContextItemDecl()) {
                declMarker.done(XQueryElementType.CONTEXT_ITEM_DECL)
                return PrologDeclState.BODY_STATEMENT
            } else if (parseTypeDecl()) {
                declMarker.done(XQueryElementType.TYPE_DECL)
            } else if (parseFTOptionDecl()) {
                declMarker.done(XQueryElementType.FT_OPTION_DECL)
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "base-uri, boundary-space, construction, context, copy-namespaces, decimal-format, default, ft-option, function, namespace, option, ordering, revalidation, type, variable"))
                parseUnknownDecl()
                declMarker.done(XQueryElementType.UNKNOWN_DECL)
                return PrologDeclState.UNKNOWN_STATEMENT
            }
            return PrologDeclState.HEADER_STATEMENT
        }
        return PrologDeclState.NOT_MATCHED
    }

    private fun parseDefaultDecl(defaultDeclMarker: PsiBuilder.Marker, state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseDefaultNamespaceDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_NAMESPACE_DECL)
            } else if (parseEmptyOrderDecl()) {
                defaultDeclMarker.done(XQueryElementType.EMPTY_ORDER_DECL)
            } else if (parseDefaultCollationDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_COLLATION_DECL)
            } else if (parseDecimalFormatDecl(state, true)) {
                defaultDeclMarker.done(XQueryElementType.DECIMAL_FORMAT_DECL)
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "collation, element, function, order"))
                parseUnknownDecl()
                defaultDeclMarker.done(XQueryElementType.UNKNOWN_DECL)
            }
            return true
        }
        return false
    }

    private fun parseUnknownDecl(): Boolean {
        while (true) {
            if (parseWhiteSpaceAndCommentTokens()) continue
            if (matchTokenType(XPathTokenType.NCNAME)) continue
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) continue

            if (matchTokenType(XPathTokenType.EQUAL)) continue
            if (matchTokenType(XPathTokenType.COMMA)) continue
            if (matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) continue
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) continue
            if (matchTokenType(XPathTokenType.QNAME_SEPARATOR)) continue
            if (matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) continue
            if (matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) continue

            if (matchTokenType(XQueryTokenType.K_COLLATION)) continue
            if (matchTokenType(XQueryTokenType.K_ELEMENT)) continue
            if (matchTokenType(XQueryTokenType.K_EMPTY)) continue
            if (matchTokenType(XQueryTokenType.K_EXTERNAL)) continue
            if (matchTokenType(XQueryTokenType.K_FUNCTION)) continue
            if (matchTokenType(XQueryTokenType.K_GREATEST)) continue
            if (matchTokenType(XQueryTokenType.K_INHERIT)) continue
            if (matchTokenType(XQueryTokenType.K_ITEM)) continue
            if (matchTokenType(XQueryTokenType.K_LAX)) continue
            if (matchTokenType(XQueryTokenType.K_LEAST)) continue
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) continue
            if (matchTokenType(XQueryTokenType.K_NO_INHERIT)) continue
            if (matchTokenType(XQueryTokenType.K_NO_PRESERVE)) continue
            if (matchTokenType(XQueryTokenType.K_ORDER)) continue
            if (matchTokenType(XQueryTokenType.K_ORDERED)) continue
            if (matchTokenType(XQueryTokenType.K_PRESERVE)) continue
            if (matchTokenType(XQueryTokenType.K_SKIP)) continue
            if (matchTokenType(XQueryTokenType.K_STRICT)) continue
            if (matchTokenType(XQueryTokenType.K_STRIP)) continue
            if (matchTokenType(XQueryTokenType.K_UNORDERED)) continue

            if (parseDFPropertyName()) continue
            if (parseExprSingle()) continue
            return true
        }
    }

    private fun parseDefaultNamespaceDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_ELEMENT) || matchTokenType(XQueryTokenType.K_FUNCTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseNamespaceDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseFTOptionDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_FT_OPTION)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseFTMatchOptions()) {
                error(XQueryBundle.message("parser.error.expected-keyword", "using"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Setter

    private fun parseBoundarySpaceDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BOUNDARY_SPACE)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseDefaultCollationDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseBaseURIDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BASE_URI)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseConstructionDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CONSTRUCTION)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseOrderingModeDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ORDERING)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_ORDERED) && !matchTokenType(XQueryTokenType.K_UNORDERED)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "ordered, unordered"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseEmptyOrderDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_EMPTY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "empty"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseRevalidationDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_REVALIDATION)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_STRICT) && !matchTokenType(XQueryTokenType.K_LAX) && !matchTokenType(XQueryTokenType.K_SKIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "lax, skip, strict"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseCopyNamespacesDecl(state: PrologDeclState): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COPY_NAMESPACES)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_NO_PRESERVE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, no-preserve"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.COMMA) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ","))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_INHERIT) && !matchTokenType(XQueryTokenType.K_NO_INHERIT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "inherit, no-inherit"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseDecimalFormatDecl(state: PrologDeclState, isDefault: Boolean): Boolean {
        val errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECIMAL_FORMAT)
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            var haveErrors = false
            if (!isDefault) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.QNAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }
            }

            while (parseDFPropertyName()) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-property-value-string"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseDFPropertyName(): Boolean {
        val dfPropertyNameMarker = mark()
        if (matchTokenType(XQueryTokenType.K_DECIMAL_SEPARATOR) ||
                matchTokenType(XQueryTokenType.K_GROUPING_SEPARATOR) ||
                matchTokenType(XQueryTokenType.K_INFINITY) ||
                matchTokenType(XQueryTokenType.K_MINUS_SIGN) ||
                matchTokenType(XQueryTokenType.K_NAN) ||
                matchTokenType(XQueryTokenType.K_PERCENT) ||
                matchTokenType(XQueryTokenType.K_PER_MILLE) ||
                matchTokenType(XQueryTokenType.K_ZERO_DIGIT) ||
                matchTokenType(XQueryTokenType.K_DIGIT) ||
                matchTokenType(XQueryTokenType.K_PATTERN_SEPARATOR) ||
                matchTokenType(XQueryTokenType.K_EXPONENT_SEPARATOR)) { // XQuery 3.1

            dfPropertyNameMarker.done(XQueryElementType.DF_PROPERTY_NAME)
            return true
        }
        dfPropertyNameMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Import

    private fun parseImport(state: PrologDeclState): PrologDeclState {
        val importMarker = mark()
        val errorMarker = mark()
        if (matchTokenType(XQueryTokenType.K_IMPORT)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop()
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseSchemaImport()) {
                importMarker.done(XQueryElementType.SCHEMA_IMPORT)
            } else if (parseStylesheetImport()) {
                importMarker.done(XQueryElementType.STYLESHEET_IMPORT)
            } else if (parseModuleImport()) {
                importMarker.done(XQueryElementType.MODULE_IMPORT)
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "schema, stylesheet, module"))
                importMarker.done(XQueryElementType.IMPORT)
                return PrologDeclState.UNKNOWN_STATEMENT
            }
            return PrologDeclState.HEADER_STATEMENT
        }

        errorMarker.drop()
        importMarker.drop()
        return PrologDeclState.NOT_MATCHED
    }

    private fun parseSchemaImport(): Boolean {
        if (getTokenType() === XQueryTokenType.K_SCHEMA) {
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            var haveErrors = parseSchemaPrefix()

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                } while (matchTokenType(XPathTokenType.COMMA))
            }
            return true
        }
        return false
    }

    private fun parseSchemaPrefix(): Boolean {
        var haveErrors = false
        val schemaPrefixMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE)
        if (schemaPrefixMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            schemaPrefixMarker.done(XQueryElementType.SCHEMA_PREFIX)
            return haveErrors
        }

        val schemaPrefixDefaultMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT)
        if (schemaPrefixDefaultMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_ELEMENT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "element"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            schemaPrefixDefaultMarker.done(XQueryElementType.SCHEMA_PREFIX)
        }
        return haveErrors
    }

    private fun parseStylesheetImport(): Boolean {
        if (getTokenType() === XQueryTokenType.K_STYLESHEET) {
            var haveErrors = false
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_AT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "at"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseModuleImport(): Boolean {
        if (getTokenType() === XQueryTokenType.K_MODULE) {
            var haveErrors = false
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseQName(XQueryElementType.NCNAME)) {
                    error(XQueryBundle.message("parser.error.expected-ncname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            }

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                } while (matchTokenType(XPathTokenType.COMMA))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Prolog :: Body

    private fun parseContextItemDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_CONTEXT)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_ITEM)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "item"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseItemType()) {
                    error(XQueryBundle.message("parser.error.expected", "ItemType"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL) || { haveErrors = errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")); haveErrors }()) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle(XQueryElementType.VAR_VALUE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }
            } else if (matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL) || { haveErrors = errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")); haveErrors }()) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseExprSingle(XQueryElementType.VAR_DEFAULT_VALUE) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"))
                    }
                }
            } else {
                error(XQueryBundle.message("parser.error.expected-variable-value"))
                parseExprSingle(XQueryElementType.VAR_VALUE)
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseAnnotatedDecl(): Boolean {
        var haveAnnotations = false
        var firstAnnotation: IElementType? = null
        var annotation: IElementType?
        do {
            annotation = if (parseAnnotation()) {
                XQueryElementType.ANNOTATION
            } else {
                parseCompatibilityAnnotationDecl()
            }

            if (firstAnnotation == null) {
                firstAnnotation = annotation
            }

            if (annotation != null) {
                parseWhiteSpaceAndCommentTokens()
                haveAnnotations = true
            }
        } while (annotation != null)

        val declMarker = mark()
        if (parseVarDecl()) {
            declMarker.done(XQueryElementType.VAR_DECL)
            return true
        } else if (parseFunctionDecl(declMarker, firstAnnotation)) {
            return true
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected-keyword", "function, variable"))
            parseUnknownDecl()
            declMarker.done(XQueryElementType.UNKNOWN_DECL)
            return true
        }
        declMarker.drop()
        return false
    }

    private fun parseAnnotation(): Boolean {
        val annotationMarker = matchTokenTypeWithMarker(XQueryTokenType.ANNOTATION_INDICATOR)
        if (annotationMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                do {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseLiteral() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "Literal"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                } while (matchTokenType(XPathTokenType.COMMA))

                if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ")"))
                }
            }

            annotationMarker.done(XQueryElementType.ANNOTATION)
            return true
        }
        return false
    }

    private fun parseCompatibilityAnnotationDecl(): IElementType? {
        val compatibilityAnnotationMarker = mark()
        val type = getTokenType()
        if (COMPATIBILITY_ANNOTATION_TOKENS.contains(type)) {
            advanceLexer()
            compatibilityAnnotationMarker.done(XQueryElementType.COMPATIBILITY_ANNOTATION)
            return type
        }
        compatibilityAnnotationMarker.drop()
        return null
    }

    private fun parseVarDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_VARIABLE)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            parseTypeDeclaration()

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL) || { haveErrors = haveErrors or errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected-variable-value")); haveErrors }()) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle(XQueryElementType.VAR_VALUE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }
            } else if (matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseExprSingle(XQueryElementType.VAR_DEFAULT_VALUE) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"))
                    }
                }
            } else {
                error(XQueryBundle.message("parser.error.expected-variable-value"))
                parseExprSingle(XQueryElementType.VAR_VALUE)
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseFunctionDecl(functionDeclMarker: PsiBuilder.Marker, firstAnnotation: IElementType?): Boolean {
        if (matchTokenType(XQueryTokenType.K_FUNCTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() === XPathTokenType.STRING_LITERAL_START) {
                // DefaultNamespaceDecl with missing 'default' keyword.
                error(XQueryBundle.message("parser.error.expected", "("))
                parseStringLiteral(XQueryElementType.STRING_LITERAL)
                parseWhiteSpaceAndCommentTokens()
                functionDeclMarker.done(XQueryElementType.UNKNOWN_DECL)
                return true
            } else if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "("))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            parseParamList()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType()) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
            }

            val bodyType = if (firstAnnotation === XQueryTokenType.K_SEQUENTIAL)
                XQueryElementType.BLOCK
            else
                XQueryElementType.FUNCTION_BODY

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_EXTERNAL) &&
                    !parseEnclosedExprOrBlock(bodyType, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) &&
                    !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-enclosed-expression-or-keyword", "external"))
                parseExpr(XQueryElementType.EXPR, true)

                parseWhiteSpaceAndCommentTokens()
                matchTokenType(XQueryTokenType.BLOCK_CLOSE)
            }

            parseWhiteSpaceAndCommentTokens()
            functionDeclMarker.done(XQueryElementType.FUNCTION_DECL)
            return true
        }

        return false
    }

    private fun parseParamList(): Boolean {
        val paramListMarker = mark()

        while (parseParam()) {
            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() === XPathTokenType.VARIABLE_INDICATOR) {
                error(XQueryBundle.message("parser.error.expected", ","))
            } else if (!matchTokenType(XPathTokenType.COMMA)) {
                matchTokenType(XQueryTokenType.ELLIPSIS)

                paramListMarker.done(XQueryElementType.PARAM_LIST)
                return true
            }

            parseWhiteSpaceAndCommentTokens()
        }

        paramListMarker.drop()
        return false
    }

    private fun parseParam(): Boolean {
        val paramMarker = mark()
        if (matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }

            parseWhiteSpaceAndCommentTokens()
            parseTypeDeclaration()

            paramMarker.done(XQueryElementType.PARAM)
            return true
        } else if (getTokenType() === XPathTokenType.NCNAME || getTokenType() is IKeywordOrNCNameType || getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
            error(XQueryBundle.message("parser.error.expected", "$"))
            parseEQName(XQueryElementType.QNAME)

            parseWhiteSpaceAndCommentTokens()
            parseTypeDeclaration()

            paramMarker.done(XQueryElementType.PARAM)
            return true
        }

        paramMarker.drop()
        return false
    }

    private fun parseOptionDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_OPTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-option-string"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    private fun parseTypeDecl(): Boolean {
        if (matchTokenType(XQueryTokenType.K_TYPE)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-qname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.EQUAL) && !{ haveErrors = errorOnTokenType(XQueryTokenType.ASSIGN_EQUAL, XQueryBundle.message("parser.error.expected", "=")); haveErrors }()) {
                error(XQueryBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseItemType() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "ItemType"))
            }

            parseWhiteSpaceAndCommentTokens()
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: EnclosedExpr|Block

    private enum class BlockOpen {
        REQUIRED,
        OPTIONAL
    }

    private enum class BlockExpr {
        REQUIRED,
        OPTIONAL
    }

    private fun parseEnclosedExprOrBlock(type: IElementType?, blockOpen: BlockOpen, blockExpr: BlockExpr): Boolean {
        var haveErrors = false
        val enclosedExprMarker = if (type == null) null else mark()
        if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
            if (blockOpen == BlockOpen.OPTIONAL) {
                error(XQueryBundle.message("parser.error.expected", "{"))
                haveErrors = true
            } else {
                enclosedExprMarker?.drop()
                return false
            }
        }

        var exprType = XQueryElementType.EXPR
        if (type === XQueryElementType.BLOCK || type === XQueryElementType.WHILE_BODY) {
            parseWhiteSpaceAndCommentTokens()
            parseBlockDecls()
            exprType = XQueryElementType.BLOCK_BODY
        }

        parseWhiteSpaceAndCommentTokens()
        var haveExpr = parseExpr(exprType)
        if (!haveExpr && blockExpr == BlockExpr.REQUIRED) {
            error(XQueryBundle.message("parser.error.expected-expression"))
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.BLOCK_CLOSE)) {
            haveExpr = true
        } else if (!haveErrors) {
            error(XQueryBundle.message("parser.error.expected", "}"))
        }

        if (enclosedExprMarker != null) {
            if (haveExpr) {
                enclosedExprMarker.done(type!!)
                return true
            }
            enclosedExprMarker.drop()
        }
        return haveExpr
    }

    private fun parseBlockDecls(): Boolean {
        val blockDeclsMarker = mark()
        parseWhiteSpaceAndCommentTokens()
        while (true)
            when (parseBlockVarDecl()) {
                XQueryParserImpl.ParseStatus.MATCHED -> {
                    if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                        error(XQueryBundle.message("parser.error.expected", ";"))
                        if (getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
                            advanceLexer()
                        }
                    }
                    parseWhiteSpaceAndCommentTokens()
                }
                XQueryParserImpl.ParseStatus.MATCHED_WITH_ERRORS -> {
                    matchTokenType(XQueryTokenType.SEPARATOR)
                    parseWhiteSpaceAndCommentTokens()
                }
                XQueryParserImpl.ParseStatus.NOT_MATCHED -> {
                    blockDeclsMarker.done(XQueryElementType.BLOCK_DECLS)
                    return true
                }
            }
    }

    private fun parseBlockVarDecl(): ParseStatus {
        val blockVarDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE)
        if (blockVarDeclMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() === XPathTokenType.PARENTHESIS_OPEN || getTokenType() === XPathTokenType.QNAME_SEPARATOR) {
                // 'declare' used as a function name.
                blockVarDeclMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            var status: ParseStatus
            do {
                parseWhiteSpaceAndCommentTokens()
                status = parseBlockVarDeclEntry()
                if (status == ParseStatus.NOT_MATCHED) {
                    status = ParseStatus.MATCHED_WITH_ERRORS
                }
            } while (matchTokenType(XPathTokenType.COMMA))

            blockVarDeclMarker.done(XQueryElementType.BLOCK_VAR_DECL)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseBlockVarDeclEntry(): ParseStatus {
        val blockVarDeclEntryMarker = mark()
        var haveErrors = false
        if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            error(XQueryBundle.message("parser.error.expected", "$"))
            if (getTokenType() === XQueryTokenType.SEPARATOR) {
                blockVarDeclEntryMarker.drop()
                return ParseStatus.NOT_MATCHED
            }
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens()
        if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
            error(XQueryBundle.message("parser.error.expected-eqname"))
            haveErrors = true
        }

        parseWhiteSpaceAndCommentTokens()
        val errorMessage = if (parseTypeDeclaration())
            "parser.error.expected-variable-assign-scripting"
        else
            "parser.error.expected-variable-assign-scripting-no-type-decl"

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL) || { haveErrors = errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message(errorMessage)); haveErrors }()) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }
            parseWhiteSpaceAndCommentTokens()
        } else if (getTokenType() !== XPathTokenType.COMMA && getTokenType() !== XQueryTokenType.SEPARATOR) {
            error(XQueryBundle.message(errorMessage))
            parseExprSingle()
            parseWhiteSpaceAndCommentTokens()
            haveErrors = true
        }
        blockVarDeclEntryMarker.done(XQueryElementType.BLOCK_VAR_DECL_ENTRY)
        return if (haveErrors) ParseStatus.MATCHED_WITH_ERRORS else ParseStatus.MATCHED
    }

    // endregion
    // region Grammar :: Expr

    private fun parseExpr(type: IElementType, functionDeclRecovery: Boolean = false): Boolean {
        val exprMarker = mark()
        if (parseApplyExpr(type, functionDeclRecovery)) {
            exprMarker.done(type)
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseApplyExpr(type: IElementType, functionDeclRecovery: Boolean): Boolean {
        // NOTE: No marker is captured here because the Expr node is an instance
        // of the ApplyExpr node and there are no other uses of ApplyExpr.
        var haveConcatExpr = false
        while (true) {
            if (!parseConcatExpr()) {
                parseWhiteSpaceAndCommentTokens()
                if (functionDeclRecovery || !errorOnTokenType(XQueryTokenType.SEPARATOR, XQueryBundle.message("parser.error.expected-query-statement", ";"))) {
                    return haveConcatExpr
                } else {
                    // Semicolon without a query body -- continue parsing.
                    parseWhiteSpaceAndCommentTokens()
                    continue
                }
            }

            parseWhiteSpaceAndCommentTokens()

            val marker = mark()
            when (parseTransactionSeparator()) {
                XQueryParserImpl.TransactionType.WITH_PROLOG -> {
                    // MarkLogic transaction containing a Prolog/Module statement.
                    marker.rollbackTo()
                    return true
                }
                XQueryParserImpl.TransactionType.WITHOUT_PROLOG -> {
                    if (type !== XQueryElementType.QUERY_BODY) {
                        // Scripting Extension: Use a Separator as part of the ApplyExpr.
                        marker.rollbackTo()
                        matchTokenType(XQueryTokenType.SEPARATOR)
                    } else {
                        // Scripting Extension, or MarkLogic Transaction: Keep the MarkLogic TransactionSeparator.
                        marker.drop()
                    }
                    parseWhiteSpaceAndCommentTokens()
                }
                XQueryParserImpl.TransactionType.NONE -> {
                    marker.rollbackTo()
                    if (haveConcatExpr) {
                        if (type !== XQueryElementType.QUERY_BODY) {
                            // Scripting Extension: The semicolon is required to end a ConcatExpr.
                            error(XQueryBundle.message("parser.error.expected", ";"))
                        } else {
                            // Scripting Extension: The semicolon is required to end a ConcatExpr.
                            // MarkLogic Transactions: The last expression must not end with a semicolon.
                            val marker2 = mark()
                            marker2.done(XQueryElementType.TRANSACTION_SEPARATOR)
                        }
                    }
                    return true
                }
            }

            haveConcatExpr = true
        }
    }

    private fun parseConcatExpr(): Boolean {
        val exprMarker = mark()
        if (parseExprSingle()) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            }
            exprMarker.done(XQueryElementType.CONCAT_EXPR)
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseExprSingle(): Boolean {
        return parseExprSingleImpl(null)
    }

    private fun parseExprSingle(type: IElementType?, parentType: IElementType? = null): Boolean {
        if (type == null) {
            return parseExprSingleImpl(parentType)
        }

        val exprSingleMarker = mark()
        if (parseExprSingleImpl(parentType)) {
            exprSingleMarker.done(type)
            return true
        }

        exprSingleMarker.drop()
        return false
    }

    private fun parseExprSingleImpl(parentType: IElementType?): Boolean {
        return (parseFLWORExpr()
                || parseQuantifiedExpr()
                || parseSwitchExpr()
                || parseTypeswitchExpr()
                || parseIfExpr()
                || parseTryCatchExpr()
                || parseInsertExpr()
                || parseDeleteExpr()
                || parseRenameExpr()
                || parseReplaceExpr()
                || parseCopyModifyExpr()
                || parseUpdatingFunctionCall()
                || parseBlockExpr()
                || parseAssignmentExpr()
                || parseExitExpr()
                || parseWhileExpr()
                || parseTernaryIfExpr(parentType))
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr

    private fun parseFLWORExpr(): Boolean {
        val flworExprMarker = mark()
        if (parseInitialClause()) {
            while (parseIntermediateClause()) {
                //
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseReturnClause()) {
                error(XQueryBundle.message("parser.error.expected-keyword", "count, for, group, let, order, return, sliding, stable, tumbling, where"))
                parseWhiteSpaceAndCommentTokens()
                parseExprSingle()
            }

            flworExprMarker.done(XQueryElementType.FLWOR_EXPR)
            return true
        } else if (errorOnTokenType(XPathTokenType.K_RETURN, XQueryBundle.message("parser.error.return-without-flwor"))) {
            parseWhiteSpaceAndCommentTokens()
            return if (getTokenType() !== XPathTokenType.PARENTHESIS_OPEN && parseExprSingle()) {
                flworExprMarker.drop()
                true
            } else {
                flworExprMarker.rollbackTo()
                false
            }
        }
        flworExprMarker.drop()
        return false
    }

    private fun parseInitialClause(): Boolean {
        return parseForOrWindowClause() || parseLetClause()
    }

    private fun parseIntermediateClause(): Boolean {
        val intermediateClauseMarker = mark()
        if (parseInitialClause() || parseWhereClause() || parseOrderByClause() || parseCountClause() || parseGroupByClause()) {
            intermediateClauseMarker.done(XQueryElementType.INTERMEDIATE_CLAUSE)
            return true
        }
        intermediateClauseMarker.drop()
        return false
    }

    private fun parseReturnClause(): Boolean {
        val returnClauseMarker = mark()
        if (matchTokenType(XPathTokenType.K_RETURN)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            returnClauseMarker.done(XQueryElementType.RETURN_CLAUSE)
            return true
        }
        returnClauseMarker.drop()
        return false
    }

    private fun parseForOrWindowClause(): Boolean {
        val forClauseMarker = matchTokenTypeWithMarker(XPathTokenType.K_FOR)
        if (forClauseMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            return if (parseForClause()) {
                forClauseMarker.done(XQueryElementType.FOR_CLAUSE)
                true
            } else if (parseTumblingWindowClause() || parseSlidingWindowClause()) {
                forClauseMarker.done(XQueryElementType.WINDOW_CLAUSE)
                true
            } else {
                forClauseMarker.rollbackTo()
                false
            }
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: ForClause

    private fun parseForClause(): Boolean {
        if (parseForBinding(true)) {
            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                parseForBinding(false)
                parseWhiteSpaceAndCommentTokens()
            }
            return true
        }
        return false
    }

    private fun parseForBinding(isFirst: Boolean): Boolean {
        val forBindingMarker = mark()

        var haveErrors = false
        val matched = matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            error(XQueryBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            val haveTypeDeclaration = parseTypeDeclaration()

            parseWhiteSpaceAndCommentTokens()
            val haveAllowingEmpty = parseAllowingEmpty()

            parseWhiteSpaceAndCommentTokens()
            val havePositionalVar = parsePositionalVar()

            parseWhiteSpaceAndCommentTokens()
            val haveScoreVar = parseFTScoreVar()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                if (haveScoreVar) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "in"))
                } else if (havePositionalVar) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "in, score"))
                } else if (haveAllowingEmpty) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "at, in, score"))
                } else if (haveTypeDeclaration) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "allowing, at, in, score"))
                } else {
                    error(XQueryBundle.message("parser.error.expected-keyword", "allowing, as, at, in, score"))
                }
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            forBindingMarker.done(XQueryElementType.FOR_BINDING)
            return true
        }
        forBindingMarker.drop()
        return false
    }

    private fun parseAllowingEmpty(): Boolean {
        val allowingEmptyMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ALLOWING)
        if (allowingEmptyMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_EMPTY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "empty"))
            }

            allowingEmptyMarker.done(XQueryElementType.ALLOWING_EMPTY)
            return true
        }
        return false
    }

    private fun parsePositionalVar(): Boolean {
        val positionalVarMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AT)
        if (positionalVarMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }

            positionalVarMarker.done(XQueryElementType.POSITIONAL_VAR)
            return true
        }
        return false
    }

    private fun parseFTScoreVar(): Boolean {
        val scoreVarMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCORE)
        if (scoreVarMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }

            scoreVarMarker.done(XQueryElementType.FT_SCORE_VAR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: LetClause

    private fun parseLetClause(): Boolean {
        val letClauseMarker = mark()
        if (matchTokenType(XQueryTokenType.K_LET)) {
            var isFirst = true
            do {
                parseWhiteSpaceAndCommentTokens()
                if (!parseLetBinding(isFirst) && isFirst) {
                    letClauseMarker.rollbackTo()
                    return false
                }

                isFirst = false
                parseWhiteSpaceAndCommentTokens()
            } while (matchTokenType(XPathTokenType.COMMA))

            letClauseMarker.done(XQueryElementType.LET_CLAUSE)
            return true
        }
        letClauseMarker.drop()
        return false
    }

    private fun parseLetBinding(isFirst: Boolean): Boolean {
        val letBindingMarker = mark()

        var haveErrors = false
        val haveVariableIndicator = matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        val matched = haveVariableIndicator || parseFTScoreVar()
        if (!matched) {
            error(XQueryBundle.message("parser.error.expected-keyword-or-token", "$", "score"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            val errorMessage: String
            if (haveVariableIndicator) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.VAR_NAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                errorMessage = if (parseTypeDeclaration())
                    XQueryBundle.message("parser.error.expected", ":=")
                else
                    XQueryBundle.message("parser.error.expected-variable-assign-or-keyword", "as")
            } else {
                errorMessage = XQueryBundle.message("parser.error.expected", ":=")
            }

            parseWhiteSpaceAndCommentTokens()
            if (errorOnTokenType(XPathTokenType.EQUAL, errorMessage)) {
                haveErrors = true
            } else if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                error(errorMessage)
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            letBindingMarker.done(XQueryElementType.LET_BINDING)
            return true
        }
        letBindingMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: WindowClause

    private fun parseTumblingWindowClause(): Boolean {
        val tumblingWindowClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TUMBLING)
        if (tumblingWindowClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WINDOW)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "window"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            val haveTypeDeclaration = parseTypeDeclaration()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseWindowStartCondition() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "WindowStartCondition"))
            }

            parseWhiteSpaceAndCommentTokens()
            parseWindowEndCondition()

            tumblingWindowClauseMarker.done(XQueryElementType.TUMBLING_WINDOW_CLAUSE)
            return true
        }
        return false
    }

    private fun parseSlidingWindowClause(): Boolean {
        val slidingWindowClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SLIDING)
        if (slidingWindowClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WINDOW)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "window"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            val haveTypeDeclaration = parseTypeDeclaration()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseWindowStartCondition() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "WindowStartCondition"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseWindowEndCondition() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "WindowEndCondition"))
            }

            slidingWindowClauseMarker.done(XQueryElementType.SLIDING_WINDOW_CLAUSE)
            return true
        }
        return false
    }

    private fun parseWindowStartCondition(): Boolean {
        val windowStartConditionMarker = matchTokenTypeWithMarker(XQueryTokenType.K_START)
        if (windowStartConditionMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            parseWindowVars()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WHEN)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "when"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            windowStartConditionMarker.done(XQueryElementType.WINDOW_START_CONDITION)
            return true
        }
        return false
    }

    private fun parseWindowEndCondition(): Boolean {
        var haveErrors = false

        var windowEndConditionMarker = matchTokenTypeWithMarker(XQueryTokenType.K_END)
        if (windowEndConditionMarker == null) {
            windowEndConditionMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ONLY)
            if (windowEndConditionMarker != null) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.K_END)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "end"))
                    haveErrors = true
                }
            }
        }

        if (windowEndConditionMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            parseWindowVars()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WHEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "when"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            windowEndConditionMarker.done(XQueryElementType.WINDOW_END_CONDITION)
            return true
        }
        return false
    }

    private fun parseWindowVars(): Boolean {
        val windowVarsMarker = mark()
        var haveErrors = false

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.CURRENT_ITEM)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }
        }

        parseWhiteSpaceAndCommentTokens()
        parsePositionalVar()

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.K_PREVIOUS)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.PREVIOUS_ITEM) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }
        }

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.K_NEXT)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.NEXT_ITEM) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }
        }

        windowVarsMarker.done(XQueryElementType.WINDOW_VARS)
        return true
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: CountClause

    private fun parseCountClause(): Boolean {
        val countClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COUNT)
        if (countClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-qname"))
            }

            countClauseMarker.done(XQueryElementType.COUNT_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: WhereClause

    private fun parseWhereClause(): Boolean {
        val whereClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WHERE)
        if (whereClauseMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            whereClauseMarker.done(XQueryElementType.WHERE_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: GroupByClause

    private fun parseGroupByClause(): Boolean {
        val groupByClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_GROUP)
        if (groupByClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_BY)) {
                error(XQueryBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseGroupingSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "GroupingSpecList"))
            }

            groupByClauseMarker.done(XQueryElementType.GROUP_BY_CLAUSE)
            return true
        }
        return false
    }

    private fun parseGroupingSpecList(): Boolean {
        val groupingSpecListMarker = mark()
        if (parseGroupingSpec()) {
            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseGroupingSpec()) {
                    error(XQueryBundle.message("parser.error.expected", "GroupingSpec"))
                }

                parseWhiteSpaceAndCommentTokens()
            }

            groupingSpecListMarker.done(XQueryElementType.GROUPING_SPEC_LIST)
            return true
        }
        groupingSpecListMarker.drop()
        return false
    }

    private fun parseGroupingSpec(): Boolean {
        val groupingSpecListMarker = mark()
        if (parseGroupingVariable()) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (parseTypeDeclaration()) {
                parseWhiteSpaceAndCommentTokens()
                if (errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected", ":="))) {
                    haveErrors = true
                } else if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                    error(XQueryBundle.message("parser.error.expected", ":="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
            } else if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL) || { haveErrors = errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected", ":=")); haveErrors }()) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
            }

            if (matchTokenType(XQueryTokenType.K_COLLATION)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }
            }

            groupingSpecListMarker.done(XQueryElementType.GROUPING_SPEC)
            return true
        }
        groupingSpecListMarker.drop()
        return false
    }

    private fun parseGroupingVariable(): Boolean {
        val groupingVariableMarker = matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (groupingVariableMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }

            groupingVariableMarker.done(XQueryElementType.GROUPING_VARIABLE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: OrderByClause

    private fun parseOrderByClause(): Boolean {
        val orderByClauseMarker = mark()
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_BY)) {
                error(XQueryBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseOrderSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "OrderSpecList"))
            }

            orderByClauseMarker.done(XQueryElementType.ORDER_BY_CLAUSE)
            return true
        } else if (matchTokenType(XQueryTokenType.K_STABLE)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_ORDER)) {
                error(XQueryBundle.message("parser.error.expected", "order"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_BY) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "by"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseOrderSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "OrderSpecList"))
            }

            orderByClauseMarker.done(XQueryElementType.ORDER_BY_CLAUSE)
            return true
        }
        orderByClauseMarker.drop()
        return false
    }

    private fun parseOrderSpecList(): Boolean {
        val orderSpecListMarker = mark()
        if (parseOrderSpec()) {
            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseOrderSpec()) {
                    error(XQueryBundle.message("parser.error.expected", "OrderSpec"))
                }

                parseWhiteSpaceAndCommentTokens()
            }

            orderSpecListMarker.done(XQueryElementType.ORDER_SPEC_LIST)
            return true
        }
        orderSpecListMarker.drop()
        return false
    }

    private fun parseOrderSpec(): Boolean {
        val orderSpecMarker = mark()
        if (parseExprSingle()) {
            parseWhiteSpaceAndCommentTokens()
            parseOrderModifier()

            orderSpecMarker.done(XQueryElementType.ORDER_SPEC)
            return true
        }
        orderSpecMarker.drop()
        return false
    }

    private fun parseOrderModifier(): Boolean {
        val orderModifierMarker = mark()

        if (matchTokenType(XQueryTokenType.K_ASCENDING) || matchTokenType(XQueryTokenType.K_DESCENDING)) {
            //
        }

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.K_EMPTY)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"))
            }
        }

        parseWhiteSpaceAndCommentTokens()
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"))
            }
        }

        orderModifierMarker.done(XQueryElementType.ORDER_MODIFIER)
        return false
    }

    // endregion
    // region Grammar :: Expr :: QuantifiedExpr

    private fun parseQuantifiedExpr(): Boolean {
        val quantifiedExprMarker = matchTokenTypeWithMarker(XPathTokenType.K_SOME, XPathTokenType.K_EVERY)
        if (quantifiedExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (parseQuantifiedExprBinding(true)) {
                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    parseQuantifiedExprBinding(false)
                    parseWhiteSpaceAndCommentTokens()
                }
            }

            var haveErrors = false
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_SATISFIES)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "satisfies"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            quantifiedExprMarker.done(XQueryElementType.QUANTIFIED_EXPR)
            return true
        }
        return false
    }

    private fun parseQuantifiedExprBinding(isFirst: Boolean): Boolean {
        val bindingMarker = mark()

        var haveErrors = false
        val matched = matchTokenType(XPathTokenType.VARIABLE_INDICATOR)
        if (!matched && !isFirst) {
            error(XQueryBundle.message("parser.error.expected", "$"))
            haveErrors = true
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            val haveTypeDeclaration = parseTypeDeclaration()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", if (haveTypeDeclaration) "in" else "as, in"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            bindingMarker.done(XQueryElementType.QUANTIFIED_EXPR_BINDING)
            return true
        }
        bindingMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: SwitchExpr

    private fun parseSwitchExpr(): Boolean {
        val switchExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SWITCH)
        if (switchExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                switchExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            var matched = false
            while (parseSwitchCaseClause()) {
                matched = true
                parseWhiteSpaceAndCommentTokens()
            }
            if (!matched) {
                error(XQueryBundle.message("parser.error.expected", "SwitchCaseClause"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "case, default"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            switchExprMarker.done(XQueryElementType.SWITCH_EXPR)
            return true
        }
        return false
    }

    private fun parseSwitchCaseClause(): Boolean {
        val switchCaseClauseMarker = mark()

        var haveErrors = false
        var haveCase = false
        while (matchTokenType(XQueryTokenType.K_CASE)) {
            haveCase = true
            parseWhiteSpaceAndCommentTokens()
            if (!parseSwitchCaseOperand()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }
            parseWhiteSpaceAndCommentTokens()
        }

        if (haveCase) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            switchCaseClauseMarker.done(XQueryElementType.SWITCH_CASE_CLAUSE)
            return true
        }

        switchCaseClauseMarker.drop()
        return false
    }

    private fun parseSwitchCaseOperand(): Boolean {
        val switchCaseOperandMarker = mark()
        if (parseExprSingle()) {
            switchCaseOperandMarker.done(XQueryElementType.SWITCH_CASE_OPERAND)
            return true
        }
        switchCaseOperandMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: TypeswitchExpr

    private fun parseTypeswitchExpr(): Boolean {
        val typeswitchExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TYPESWITCH)
        if (typeswitchExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                typeswitchExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            var matched = false
            while (parseCaseClause()) {
                matched = true
                parseWhiteSpaceAndCommentTokens()
            }
            if (!matched) {
                error(XQueryBundle.message("parser.error.expected", "CaseClause"))
            }

            parseWhiteSpaceAndCommentTokens()
            parseDefaultCaseClause()

            typeswitchExprMarker.done(XQueryElementType.TYPESWITCH_EXPR)
            return true
        }
        return false
    }

    private fun parseCaseClause(): Boolean {
        val caseClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CASE)
        if (caseClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.VAR_NAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_AS) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseSequenceTypeUnion() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            caseClauseMarker.done(XQueryElementType.CASE_CLAUSE)
            return true
        }
        return false
    }

    private fun parseDefaultCaseClause(): Boolean {
        val caseClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT)
        if (caseClauseMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.VAR_NAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            caseClauseMarker.done(XQueryElementType.DEFAULT_CASE_CLAUSE)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    private fun parseIfExpr(): Boolean {
        val ifExprMarker = matchTokenTypeWithMarker(XPathTokenType.K_IF)
        if (ifExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                ifExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_THEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "then"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_ELSE)) { // else branch is optional in BaseX 9.1
                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }
            }

            ifExprMarker.done(XQueryElementType.IF_EXPR)
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

    private fun parseTryCatchExpr(): Boolean {
        val tryExprMarker = mark()
        if (parseTryClause()) {
            var type = CatchClauseType.NONE

            parseWhiteSpaceAndCommentTokens()
            while (true) {
                val nextType = parseCatchClause(type)
                if (nextType == CatchClauseType.NONE) {
                    if (type == CatchClauseType.NONE) {
                        error(XQueryBundle.message("parser.error.expected", "CatchClause"))
                    }

                    tryExprMarker.done(XQueryElementType.TRY_CATCH_EXPR)
                    return true
                } else if (type != CatchClauseType.MARK_LOGIC) {
                    type = nextType
                }

                parseWhiteSpaceAndCommentTokens()
            }
        }
        tryExprMarker.drop()
        return false
    }

    private fun parseTryClause(): Boolean {
        val tryClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TRY)
        if (tryClauseMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_TRY_TARGET_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                tryClauseMarker.rollbackTo()
                return false
            }

            tryClauseMarker.done(XQueryElementType.TRY_CLAUSE)
            return true
        }
        return false
    }

    private fun parseCatchClause(type: CatchClauseType): CatchClauseType {
        val catchClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CATCH)
        if (catchClauseMarker != null) {
            var haveErrors = false
            var nextType = CatchClauseType.XQUERY_30

            parseWhiteSpaceAndCommentTokens()
            if (parseCatchErrorList()) {
                //
            } else if (getTokenType() === XPathTokenType.PARENTHESIS_OPEN) {
                if (type == CatchClauseType.MARK_LOGIC) {
                    error(XQueryBundle.message("parser.error.multiple-marklogic-catch-clause"))
                }
                advanceLexer()

                nextType = CatchClauseType.MARK_LOGIC

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR)) {
                    error(XQueryBundle.message("parser.error.expected", "$"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "VarName"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ")"))
                }
            } else {
                error(XQueryBundle.message("parser.error.expected", "CatchErrorList"))
            }

            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)

            catchClauseMarker.done(XQueryElementType.CATCH_CLAUSE)
            return nextType
        }
        return CatchClauseType.NONE
    }

    private fun parseCatchErrorList(): Boolean {
        val catchErrorListMarker = mark()
        if (parseNameTest(null)) {
            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseNameTest(null)) {
                    error(XQueryBundle.message("parser.error.expected", "NameTest"))
                }
                parseWhiteSpaceAndCommentTokens()
            }
            catchErrorListMarker.done(XQueryElementType.CATCH_ERROR_LIST)
            return true
        }
        catchErrorListMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: InsertExpr

    private fun parseInsertExpr(): Boolean {
        val insertExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_INSERT)
        if (insertExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                insertExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseSourceExpr()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseInsertExprTargetChoice() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "after, as, before, into"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseTargetExpr(null) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            insertExprMarker.done(XQueryElementType.INSERT_EXPR)
            return true
        }
        return false
    }

    private fun parseSourceExpr(): Boolean {
        val sourceExprMarker = mark()
        if (parseExprSingle(null, XQueryElementType.SOURCE_EXPR)) {
            sourceExprMarker.done(XQueryElementType.SOURCE_EXPR)
            return true
        }
        sourceExprMarker.drop()
        return false
    }

    private fun parseInsertExprTargetChoice(): Boolean {
        val insertExprTargetChoiceMarker = mark()
        if (matchTokenType(XPathTokenType.K_AS)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_FIRST) && !matchTokenType(XQueryTokenType.K_LAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "first, last"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_INTO) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "into"))
            }

            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
            return true
        } else if (matchTokenType(XQueryTokenType.K_INTO) ||
                matchTokenType(XQueryTokenType.K_BEFORE) ||
                matchTokenType(XQueryTokenType.K_AFTER)) {
            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
            return true
        } else if (getTokenType() === XQueryTokenType.K_FIRST || getTokenType() === XQueryTokenType.K_LAST) {
            error(XQueryBundle.message("parser.error.expected-keyword", "as"))
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            matchTokenType(XQueryTokenType.K_INTO)

            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE)
            return true
        }

        insertExprTargetChoiceMarker.drop()
        return false
    }

    private fun parseTargetExpr(type: IElementType?): Boolean {
        val targetExprMarker = mark()
        if (parseExprSingle(null, type)) {
            targetExprMarker.done(XQueryElementType.TARGET_EXPR)
            return true
        }
        targetExprMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: DeleteExpr

    private fun parseDeleteExpr(): Boolean {
        val deleteExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DELETE)
        if (deleteExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                deleteExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            deleteExprMarker.done(XQueryElementType.DELETE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: ReplaceExpr

    private fun parseReplaceExpr(): Boolean {
        val replaceExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_REPLACE)
        if (replaceExprMarker != null) {
            var haveErrors = false
            var haveValueOf = false

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_VALUE)) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_OF)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"))
                    haveErrors = true
                }
                haveValueOf = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                if (!haveValueOf) {
                    replaceExprMarker.rollbackTo()
                    return false
                }
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "node"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WITH) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "with"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            replaceExprMarker.done(XQueryElementType.REPLACE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: RenameExpr

    private fun parseRenameExpr(): Boolean {
        val renameExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_RENAME)
        if (renameExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                renameExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseTargetExpr(XQueryElementType.TARGET_EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_AS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "as"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseNewNameExpr() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            renameExprMarker.done(XQueryElementType.RENAME_EXPR)
            return true
        }
        return false
    }

    private fun parseNewNameExpr(): Boolean {
        val newNameExprMarker = mark()
        if (parseExprSingle()) {
            newNameExprMarker.done(XQueryElementType.NEW_NAME_EXPR)
            return true
        }
        newNameExprMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: CopyModifyExpr (TransformExpr)

    private fun parseCopyModifyExpr(): Boolean {
        val copyModifyExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COPY)
        if (copyModifyExprMarker != null) {
            var haveErrors = false
            var isFirstVarName = true
            do {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        copyModifyExprMarker.rollbackTo()
                        return false
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"))
                        haveErrors = true
                    }
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-eqname"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (errorOnTokenType(XPathTokenType.EQUAL, XQueryBundle.message("parser.error.expected", ":="))) {
                    haveErrors = true
                } else if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ":="))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }

                isFirstVarName = false
                parseWhiteSpaceAndCommentTokens()
            } while (matchTokenType(XPathTokenType.COMMA))

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_MODIFY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "modify"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            copyModifyExprMarker.done(XQueryElementType.COPY_MODIFY_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: BlockExpr

    private fun parseBlockExpr(): Boolean {
        val blockExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BLOCK)
        if (blockExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.BLOCK, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                blockExprMarker.rollbackTo()
                return false
            }
            blockExprMarker.done(XQueryElementType.BLOCK_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: AssignmentExpr

    private fun parseAssignmentExpr(): Boolean {
        val assignmentExprMarker = matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (assignmentExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                // VarRef construct -- handle in the OrExpr parser for the correct AST.
                assignmentExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            assignmentExprMarker.done(XQueryElementType.ASSIGNMENT_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: ExitExpr

    private fun parseExitExpr(): Boolean {
        val exitExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_EXIT)
        if (exitExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_RETURNING)) {
                if (getTokenType() === XPathTokenType.PARENTHESIS_OPEN) {
                    // FunctionCall construct
                    exitExprMarker.rollbackTo()
                    return false
                }
                error(XQueryBundle.message("parser.error.expected-keyword", "returning"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle()) {
                if (haveErrors) {
                    // AbbrevForwardStep construct
                    exitExprMarker.rollbackTo()
                    return false
                }
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            exitExprMarker.done(XQueryElementType.EXIT_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: WhileExpr

    private fun parseWhileExpr(): Boolean {
        val whileExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WHILE)
        if (whileExprMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                whileExprMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.WHILE_BODY, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                // FunctionCall construct. Check for reserved function name in the FunctionCall PSI class.
                whileExprMarker.rollbackTo()
                return false
            }

            whileExprMarker.done(XQueryElementType.WHILE_EXPR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: TernaryIfExpr (OrExpr)

    private fun parseTernaryIfExpr(type: IElementType?): Boolean {
        val exprMarker = mark()
        if (parseElvisExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.TERNARY_IF)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseElvisExpr(null)) {
                    error(XQueryBundle.message("parser.error.expected", "ElvisExpr"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.TERNARY_ELSE)) {
                    error(XQueryBundle.message("parser.error.expected", "!!"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseElvisExpr(null)) {
                    error(XQueryBundle.message("parser.error.expected", "ElvisExpr"))
                }

                exprMarker.done(XQueryElementType.TERNARY_IF_EXPR)
            } else {
                exprMarker.drop()
            }
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseElvisExpr(type: IElementType?): Boolean {
        val exprMarker = mark()
        if (parseOrExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.ELVIS)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseOrExpr(null)) {
                    error(XQueryBundle.message("parser.error.expected", "OrExpr"))
                }

                exprMarker.done(XQueryElementType.ELVIS_EXPR)
            } else {
                exprMarker.drop()
            }
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseOrExpr(type: IElementType?): Boolean {
        val orExprMarker = mark()
        if (parseAndExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.K_OR) || matchTokenType(XQueryTokenType.K_ORELSE)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseAndExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AndExpr"))
                }
            }

            orExprMarker.done(XQueryElementType.OR_EXPR)
            return true
        }
        orExprMarker.drop()
        return false
    }

    private fun parseAndExpr(type: IElementType?): Boolean {
        val andExprMarker = mark()
        if (parseUpdateExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveAndExpr = false
            while (matchTokenType(XPathTokenType.K_AND) || matchTokenType(XQueryTokenType.K_ANDALSO)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseComparisonExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "ComparisonExpr"))
                }
                haveAndExpr = true
            }

            if (haveAndExpr)
                andExprMarker.done(XQueryElementType.AND_EXPR)
            else
                andExprMarker.drop()
            return true
        }
        andExprMarker.drop()
        return false
    }

    private fun parseUpdateExpr(type: IElementType?): Boolean {
        val exprMarker = mark()
        if (parseComparisonExpr(type)) {
            var haveUpdateExpr = false
            while (matchTokenType(XQueryTokenType.K_UPDATE)) {
                haveUpdateExpr = true

                parseWhiteSpaceAndCommentTokens()
                if (getTokenType() === XQueryTokenType.BLOCK_OPEN) {
                    parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)
                } else if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"))
                }
                parseWhiteSpaceAndCommentTokens()
            }

            if (haveUpdateExpr)
                exprMarker.done(XQueryElementType.UPDATE_EXPR)
            else
                exprMarker.drop()
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseComparisonExpr(type: IElementType?): Boolean {
        val comparisonExprMarker = mark()
        if (parseFTContainsExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (parseGeneralComp() || parseValueComp() || parseNodeComp()) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseFTContainsExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "FTContainsExpr"))
                }
                comparisonExprMarker.done(XQueryElementType.COMPARISON_EXPR)
            } else {
                comparisonExprMarker.drop()
            }
            return true
        } else if (errorOnTokenType(XPathTokenType.LESS_THAN, XQueryBundle.message("parser.error.comparison-no-lhs-or-direlem"))) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseFTContainsExpr(type)) {
                error(XQueryBundle.message("parser.error.expected", "FTContainsExpr"))
            }

            comparisonExprMarker.done(XQueryElementType.COMPARISON_EXPR)
            return true
        }
        comparisonExprMarker.drop()
        return false
    }

    private fun parseFTContainsExpr(type: IElementType?): Boolean {
        val containsExprMarker = mark()
        if (parseStringConcatExpr(type)) {
            parseWhiteSpaceAndCommentTokens()

            if (matchTokenType(XQueryTokenType.K_CONTAINS)) {
                var haveError = false

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.K_TEXT)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "text"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseFTSelection() && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "FTSelection"))
                }

                parseWhiteSpaceAndCommentTokens()
                parseFTIgnoreOption()
                containsExprMarker.done(XQueryElementType.FT_CONTAINS_EXPR)
            } else {
                containsExprMarker.drop()
            }
            return true
        }
        containsExprMarker.drop()
        return false
    }

    private fun parseFTIgnoreOption(): Boolean {
        val ignoreOptionMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WITHOUT)
        if (ignoreOptionMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_CONTENT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "content"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseUnionExpr(XQueryElementType.FT_IGNORE_OPTION) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", "UnionExpr"))
            }

            ignoreOptionMarker.done(XQueryElementType.FT_IGNORE_OPTION)
            return true
        }
        return false
    }

    private fun parseStringConcatExpr(type: IElementType?): Boolean {
        val stringConcatExprMarker = mark()
        if (parseRangeExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveStringConcatExpr = false
            while (matchTokenType(XQueryTokenType.CONCATENATION)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseRangeExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "RangeExpr"))
                }
                parseWhiteSpaceAndCommentTokens()
                haveStringConcatExpr = true
            }

            if (haveStringConcatExpr)
                stringConcatExprMarker.done(XQueryElementType.STRING_CONCAT_EXPR)
            else
                stringConcatExprMarker.drop()
            return true
        }
        stringConcatExprMarker.drop()
        return false
    }

    private fun parseRangeExpr(type: IElementType?): Boolean {
        val rangeExprMarker = mark()
        if (parseAdditiveExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_TO)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseAdditiveExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                }
                rangeExprMarker.done(XQueryElementType.RANGE_EXPR)
            } else {
                rangeExprMarker.drop()
            }
            return true
        }
        rangeExprMarker.drop()
        return false
    }

    private fun parseAdditiveExpr(type: IElementType?): Boolean {
        val additiveExprMarker = mark()
        if (parseMultiplicativeExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveAdditativeExpr = false
            while (matchTokenType(XPathTokenType.PLUS) || matchTokenType(XPathTokenType.MINUS)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseMultiplicativeExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "MultiplicativeExpr"))
                }
                haveAdditativeExpr = true
            }

            if (haveAdditativeExpr)
                additiveExprMarker.done(XQueryElementType.ADDITIVE_EXPR)
            else
                additiveExprMarker.drop()
            return true
        }
        additiveExprMarker.drop()
        return false
    }

    private fun parseMultiplicativeExpr(type: IElementType?): Boolean {
        val multiplicativeExprMarker = mark()
        if (parseUnionExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveMultiplicativeExpr = false
            while (
                matchTokenType(XPathTokenType.STAR) ||
                matchTokenType(XPathTokenType.K_DIV) ||
                matchTokenType(XPathTokenType.K_IDIV) ||
                matchTokenType(XPathTokenType.K_MOD)
            ) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseUnionExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "UnionExpr"))
                }
                haveMultiplicativeExpr = true
            }

            if (haveMultiplicativeExpr)
                multiplicativeExprMarker.done(XQueryElementType.MULTIPLICATIVE_EXPR)
            else
                multiplicativeExprMarker.drop()
            return true
        }
        multiplicativeExprMarker.drop()
        return false
    }

    private fun parseUnionExpr(type: IElementType?): Boolean {
        val unionExprMarker = mark()
        if (parseIntersectExceptExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveUnionExpr = false
            while (matchTokenType(XPathTokenType.K_UNION) || matchTokenType(XPathTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseIntersectExceptExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "IntersectExceptExpr"))
                }
                haveUnionExpr = true
            }

            if (haveUnionExpr)
                unionExprMarker.done(XQueryElementType.UNION_EXPR)
            else
                unionExprMarker.drop()
            return true
        }
        unionExprMarker.drop()
        return false
    }

    private fun parseIntersectExceptExpr(type: IElementType?): Boolean {
        val intersectExceptExprMarker = mark()
        if (parseInstanceofExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveIntersectExceptExpr = false
            while (matchTokenType(XPathTokenType.K_INTERSECT) || matchTokenType(XPathTokenType.K_EXCEPT)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseInstanceofExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "InstanceofExpr"))
                }
                parseWhiteSpaceAndCommentTokens()
                haveIntersectExceptExpr = true
            }

            if (haveIntersectExceptExpr)
                intersectExceptExprMarker.done(XQueryElementType.INTERSECT_EXCEPT_EXPR)
            else
                intersectExceptExprMarker.drop()
            return true
        }
        intersectExceptExprMarker.drop()
        return false
    }

    private fun parseInstanceofExpr(type: IElementType?): Boolean {
        val instanceofExprMarker = mark()
        if (parseTreatExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_INSTANCE)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_OF)) {
                    haveErrors = true
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                }
                instanceofExprMarker.done(XQueryElementType.INSTANCEOF_EXPR)
            } else if (getTokenType() === XPathTokenType.K_OF) {
                error(XQueryBundle.message("parser.error.expected-keyword", "instance"))
                advanceLexer()

                parseWhiteSpaceAndCommentTokens()
                parseSingleType()
                instanceofExprMarker.done(XQueryElementType.INSTANCEOF_EXPR)
            } else {
                instanceofExprMarker.drop()
            }
            return true
        }
        instanceofExprMarker.drop()
        return false
    }

    private fun parseTreatExpr(type: IElementType?): Boolean {
        val treatExprMarker = mark()
        if (parseCastableExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_TREAT)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                }
                treatExprMarker.done(XQueryElementType.TREAT_EXPR)
            } else if (getTokenType() === XPathTokenType.K_AS && type !== XQueryElementType.SOURCE_EXPR && type !== XQueryElementType.TARGET_EXPR) {
                error(XQueryBundle.message("parser.error.expected-keyword", "cast, castable, treat"))
                advanceLexer()

                parseWhiteSpaceAndCommentTokens()
                parseSingleType()
                treatExprMarker.done(XQueryElementType.TREAT_EXPR)
            } else {
                treatExprMarker.drop()
            }
            return true
        }
        treatExprMarker.drop()
        return false
    }

    private fun parseCastableExpr(type: IElementType?): Boolean {
        val castableExprMarker = mark()
        if (parseCastExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_CASTABLE)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"))
                }
                castableExprMarker.done(XQueryElementType.CASTABLE_EXPR)
            } else {
                castableExprMarker.drop()
            }
            return true
        }
        castableExprMarker.drop()
        return false
    }

    private fun parseCastExpr(type: IElementType?): Boolean {
        val castExprMarker = mark()
        if (parseTransformWithExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_CAST)) {
                var haveErrors = false

                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_AS)) {
                    haveErrors = true
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"))
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"))
                }
                castExprMarker.done(XQueryElementType.CAST_EXPR)
            } else {
                castExprMarker.drop()
            }
            return true
        }
        castExprMarker.drop()
        return false
    }

    private fun parseTransformWithExpr(type: IElementType?): Boolean {
        val exprMarker = mark()
        if (parseArrowExpr(type)) {
            if (matchTokenType(XQueryTokenType.K_TRANSFORM)) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.K_WITH)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "with"))
                }

                parseWhiteSpaceAndCommentTokens()
                parseEnclosedExprOrBlock(null, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)

                exprMarker.done(XQueryElementType.TRANSFORM_WITH_EXPR)
            } else {
                exprMarker.drop()
            }
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseArrowExpr(type: IElementType?): Boolean {
        val exprMarker = mark()
        if (parseUnaryExpr(type)) {
            var haveErrors = false
            var haveArrowExpr = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XQueryTokenType.ARROW)) {
                haveArrowExpr = true

                parseWhiteSpaceAndCommentTokens()
                if (!parseArrowFunctionSpecifier() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "ArrowFunctionSpecifier"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseArgumentList() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "ArgumentList"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            }

            if (haveArrowExpr)
                exprMarker.done(XQueryElementType.ARROW_EXPR)
            else
                exprMarker.drop()
            return true
        }
        exprMarker.drop()
        return false
    }

    private fun parseArrowFunctionSpecifier(): Boolean {
        val arrowFunctionSpecifierMarker = mark()
        if (parseEQName(XQueryElementType.QNAME) || parseVarRef(null) || parseParenthesizedExpr()) {
            arrowFunctionSpecifierMarker.done(XQueryElementType.ARROW_FUNCTION_SPECIFIER)
            return true
        }
        arrowFunctionSpecifierMarker.drop()
        return false
    }

    private fun parseUnaryExpr(type: IElementType?): Boolean {
        val pathExprMarker = mark()
        var matched = false
        while (matchTokenType(XPathTokenType.PLUS) || matchTokenType(XPathTokenType.MINUS)) {
            parseWhiteSpaceAndCommentTokens()
            matched = true
        }
        if (matched) {
            if (parseValueExpr(null)) {
                pathExprMarker.done(XQueryElementType.UNARY_EXPR)
                return true
            } else if (matched) {
                error(XQueryBundle.message("parser.error.expected", "ValueExpr"))
                pathExprMarker.done(XQueryElementType.UNARY_EXPR)
                return true
            }
        } else if (parseValueExpr(type)) {
            pathExprMarker.drop()
            return true
        }
        pathExprMarker.drop()
        return false
    }

    private fun parseGeneralComp(): Boolean {
        return matchTokenType(XPathTokenType.EQUAL) ||
                matchTokenType(XPathTokenType.NOT_EQUAL) ||
                matchTokenType(XPathTokenType.LESS_THAN) ||
                matchTokenType(XPathTokenType.LESS_THAN_OR_EQUAL) ||
                matchTokenType(XPathTokenType.GREATER_THAN) ||
                matchTokenType(XPathTokenType.GREATER_THAN_OR_EQUAL)
    }

    private fun parseValueComp(): Boolean {
        return matchTokenType(XPathTokenType.K_EQ) ||
                matchTokenType(XPathTokenType.K_NE) ||
                matchTokenType(XPathTokenType.K_LT) ||
                matchTokenType(XPathTokenType.K_LE) ||
                matchTokenType(XPathTokenType.K_GT) ||
                matchTokenType(XPathTokenType.K_GE)
    }

    private fun parseNodeComp(): Boolean {
        return matchTokenType(XQueryTokenType.K_IS) ||
                matchTokenType(XQueryTokenType.NODE_BEFORE) ||
                matchTokenType(XQueryTokenType.NODE_AFTER)
    }

    private fun parseSingleType(): Boolean {
        val singleTypeMarker = mark()
        if (parseEQName(XQueryElementType.SIMPLE_TYPE_NAME)) {
            parseWhiteSpaceAndCommentTokens()
            matchTokenType(XQueryTokenType.OPTIONAL)

            singleTypeMarker.done(XQueryElementType.SINGLE_TYPE)
            return true
        }
        singleTypeMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: ValueExpr

    private fun parseValueExpr(type: IElementType?): Boolean {
        return parseExtensionExpr() || parseValidateExpr() || parseSimpleMapExpr(type)
    }

    private fun parseValidateExpr(): Boolean {
        val validateExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_VALIDATE)
        if (validateExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            var blockOpen = BlockOpen.REQUIRED
            if (matchTokenType(XQueryTokenType.K_LAX) || matchTokenType(XQueryTokenType.K_STRICT)) {
                blockOpen = BlockOpen.OPTIONAL
            } else if (matchTokenType(XPathTokenType.K_AS) || matchTokenType(XQueryTokenType.K_TYPE)) {
                blockOpen = BlockOpen.OPTIONAL

                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                    error(XQueryBundle.message("parser.error.expected", "TypeName"))
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(null, blockOpen, BlockExpr.REQUIRED)) {
                validateExprMarker.rollbackTo()
                return false
            }

            validateExprMarker.done(XQueryElementType.VALIDATE_EXPR)
            return true
        }
        return false
    }

    private fun parseExtensionExpr(): Boolean {
        val extensionExprMarker = mark()
        var matched = false
        while (parsePragma()) {
            matched = true
            parseWhiteSpaceAndCommentTokens()
        }
        if (matched) {
            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(null, BlockOpen.OPTIONAL, BlockExpr.OPTIONAL)
            extensionExprMarker.done(XQueryElementType.EXTENSION_EXPR)
            return true
        }
        extensionExprMarker.drop()
        return false
    }

    private fun parsePragma(): Boolean {
        val pragmaMarker = matchTokenTypeWithMarker(XQueryTokenType.PRAGMA_BEGIN)
        if (pragmaMarker != null) {
            var haveErrors = false

            matchTokenType(XPathTokenType.WHITE_SPACE)
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            // NOTE: The XQuery grammar requires pragma contents if the EQName
            // is followed by a space token, but implementations make it optional.
            matchTokenType(XPathTokenType.WHITE_SPACE)
            matchTokenType(XQueryTokenType.PRAGMA_CONTENTS)

            if (!matchTokenType(XQueryTokenType.PRAGMA_END) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "#)"))
            }

            pragmaMarker.done(XQueryElementType.PRAGMA)
            return true
        }
        return false
    }

    private fun parseSimpleMapExpr(type: IElementType?): Boolean {
        val simpleMapExprMarker = mark()
        if (parsePathExpr(type)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            var haveSimpleMapExpr = false
            while (matchTokenType(XQueryTokenType.MAP_OPERATOR)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parsePathExpr(null) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "PathExpr"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens()
                haveSimpleMapExpr = true
            }

            if (haveSimpleMapExpr)
                simpleMapExprMarker.done(XQueryElementType.SIMPLE_MAP_EXPR)
            else
                simpleMapExprMarker.drop()
            return true
        }
        simpleMapExprMarker.drop()
        return false
    }

    private fun parsePathExpr(type: IElementType?): Boolean {
        val pathExprMarker = mark()
        if (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH)) {
            parseWhiteSpaceAndCommentTokens()
            parseRelativePathExpr(null)

            pathExprMarker.done(XQueryElementType.PATH_EXPR)
            return true
        } else if (matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseRelativePathExpr(null)) {
                error(XQueryBundle.message("parser.error.expected", "RelativePathExpr"))
            }

            pathExprMarker.done(XQueryElementType.PATH_EXPR)
            return true
        } else if (parseRelativePathExpr(type)) {
            pathExprMarker.drop()
            return true
        }
        pathExprMarker.drop()
        return false
    }

    private fun parseRelativePathExpr(type: IElementType?): Boolean {
        val relativePathExprMarker = mark()
        if (parseStepExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var haveRelativePathExpr = false
            while (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH) || matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseStepExpr(null)) {
                    error(XQueryBundle.message("parser.error.expected", "StepExpr"))
                }

                parseWhiteSpaceAndCommentTokens()
                haveRelativePathExpr = true
            }

            if (haveRelativePathExpr)
                relativePathExprMarker.done(XQueryElementType.RELATIVE_PATH_EXPR)
            else
                relativePathExprMarker.drop()
            return true
        }
        relativePathExprMarker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: StepExpr

    private fun parseStepExpr(type: IElementType?): Boolean {
        return parsePostfixExpr(type) || parseAxisStep(type)
    }

    private fun parseAxisStep(type: IElementType?): Boolean {
        val axisStepMarker = mark()
        if (parseReverseStep() || parseForwardStep(type)) {
            parseWhiteSpaceAndCommentTokens()
            if (parsePredicateList())
                axisStepMarker.done(XQueryElementType.AXIS_STEP)
            else
                axisStepMarker.drop()
            return true
        }

        axisStepMarker.drop()
        return false
    }

    private fun parseForwardStep(type: IElementType?): Boolean {
        val forwardStepMarker = mark()
        if (parseForwardAxis()) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseNodeTest(null)) {
                error(XQueryBundle.message("parser.error.expected", "NodeTest"))
            }

            forwardStepMarker.done(XQueryElementType.FORWARD_STEP)
            return true
        } else if (parseAbbrevForwardStep(type)) {
            forwardStepMarker.drop()
            return true
        }

        forwardStepMarker.drop()
        return false
    }

    private fun parseForwardAxis(): Boolean {
        val forwardAxisMarker = mark()
        if (matchTokenType(XQueryTokenType.K_ATTRIBUTE) ||
                matchTokenType(XQueryTokenType.K_CHILD) ||
                matchTokenType(XQueryTokenType.K_DESCENDANT) ||
                matchTokenType(XQueryTokenType.K_DESCENDANT_OR_SELF) ||
                matchTokenType(XQueryTokenType.K_FOLLOWING) ||
                matchTokenType(XQueryTokenType.K_FOLLOWING_SIBLING) ||
                matchTokenType(XQueryTokenType.K_NAMESPACE) ||
                matchTokenType(XQueryTokenType.K_PROPERTY) ||
                matchTokenType(XQueryTokenType.K_SELF)) {

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.AXIS_SEPARATOR)) {
                forwardAxisMarker.rollbackTo()
                return false
            }

            forwardAxisMarker.done(XQueryElementType.FORWARD_AXIS)
            return true
        }
        forwardAxisMarker.drop()
        return false
    }

    private fun parseAbbrevForwardStep(type: IElementType?): Boolean {
        val abbrevForwardStepMarker = mark()
        val matched = matchTokenType(XQueryTokenType.ATTRIBUTE_SELECTOR)

        parseWhiteSpaceAndCommentTokens()
        if (parseNodeTest(type)) {
            if (matched)
                abbrevForwardStepMarker.done(XQueryElementType.ABBREV_FORWARD_STEP)
            else
                abbrevForwardStepMarker.drop()
            return true
        } else if (matched) {
            error(XQueryBundle.message("parser.error.expected", "NodeTest"))

            abbrevForwardStepMarker.done(XQueryElementType.ABBREV_FORWARD_STEP)
            return true
        }
        abbrevForwardStepMarker.drop()
        return false
    }

    private fun parseReverseStep(): Boolean {
        val reverseStepMarker = mark()
        if (parseReverseAxis()) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseNodeTest(null)) {
                error(XQueryBundle.message("parser.error.expected", "NodeTest"))
            }

            reverseStepMarker.done(XQueryElementType.REVERSE_STEP)
            return true
        } else if (parseAbbrevReverseStep()) {
            reverseStepMarker.drop()
            return true
        }

        reverseStepMarker.drop()
        return false
    }

    private fun parseReverseAxis(): Boolean {
        val reverseAxisMarker = mark()
        if (matchTokenType(XQueryTokenType.K_PARENT) ||
                matchTokenType(XQueryTokenType.K_ANCESTOR) ||
                matchTokenType(XQueryTokenType.K_ANCESTOR_OR_SELF) ||
                matchTokenType(XQueryTokenType.K_PRECEDING) ||
                matchTokenType(XQueryTokenType.K_PRECEDING_SIBLING)) {

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.AXIS_SEPARATOR)) {
                reverseAxisMarker.rollbackTo()
                return false
            }

            reverseAxisMarker.done(XQueryElementType.REVERSE_AXIS)
            return true
        }
        reverseAxisMarker.drop()
        return false
    }

    private fun parseAbbrevReverseStep(): Boolean {
        val abbrevReverseStepMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENT_SELECTOR)
        if (abbrevReverseStepMarker != null) {
            abbrevReverseStepMarker.done(XQueryElementType.ABBREV_REVERSE_STEP)
            return true
        }
        return false
    }

    private fun parseNodeTest(type: IElementType?): Boolean {
        val nodeTestMarker = mark()
        if (parseKindTest() || parseNameTest(type)) {
            nodeTestMarker.done(XQueryElementType.NODE_TEST)
            return true
        }

        nodeTestMarker.drop()
        return false
    }

    private fun parseNameTest(type: IElementType?): Boolean {
        val nameTestMarker = mark()
        if (parseEQName(XQueryElementType.WILDCARD, type === XQueryElementType.MAP_CONSTRUCTOR_ENTRY)) { // QName | Wildcard
            nameTestMarker.done(XQueryElementType.NAME_TEST)
            return true
        }

        nameTestMarker.drop()
        return false
    }

    private fun parsePostfixExpr(type: IElementType?): Boolean {
        val postfixExprMarker = mark()
        if (parsePrimaryExpr(type)) {
            parseWhiteSpaceAndCommentTokens()
            var havePostfixExpr = false
            while (parsePredicate() || parseArgumentList() || parseLookup(XQueryElementType.LOOKUP)) {
                parseWhiteSpaceAndCommentTokens()
                havePostfixExpr = true
            }

            if (havePostfixExpr)
                postfixExprMarker.done(XQueryElementType.POSTFIX_EXPR)
            else
                postfixExprMarker.drop()
            return true
        }
        postfixExprMarker.drop()
        return false
    }

    private fun parsePredicateList(): Boolean {
        val predicateListMarker = mark()
        var havePredicate = false
        while (parsePredicate()) {
            parseWhiteSpaceAndCommentTokens()
            havePredicate = true
        }
        if (havePredicate)
            predicateListMarker.done(XQueryElementType.PREDICATE_LIST)
        else
            predicateListMarker.drop()
        return havePredicate
    }

    private fun parsePredicate(): Boolean {
        val predicateMarker = matchTokenTypeWithMarker(XQueryTokenType.SQUARE_OPEN)
        if (predicateMarker != null) {
            var haveErrors = false
            parseWhiteSpaceAndCommentTokens()

            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.SQUARE_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "]"))
            }

            predicateMarker.done(XQueryElementType.PREDICATE)
            return true
        }

        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr

    private fun parsePrimaryExpr(type: IElementType?): Boolean {
        return parseLiteral()
                || parseVarRef(type)
                || parseParenthesizedExpr()
                || parseNonDeterministicFunctionCall()
                || parseContextItemExpr()
                || parseOrderedExpr()
                || parseUnorderedExpr()
                || parseFunctionItemExpr()
                || parseArrayConstructor()
                || parseBinaryConstructor()
                || parseBooleanConstructor()
                || parseMapConstructor()
                || parseNodeConstructor()
                || parseNullConstructor()
                || parseNumberConstructor()
                || parseStringConstructor()
                || parseLookup(XQueryElementType.UNARY_LOOKUP)
                || parseFunctionCall()
    }

    private fun parseLiteral(): Boolean {
        return parseNumericLiteral() || parseStringLiteral(XQueryElementType.STRING_LITERAL)
    }

    private fun parseNumericLiteral(): Boolean {
        if (matchTokenType(XPathTokenType.INTEGER_LITERAL) || matchTokenType(XPathTokenType.DOUBLE_LITERAL)) {
            return true
        } else if (matchTokenType(XPathTokenType.DECIMAL_LITERAL)) {
            errorOnTokenType(XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT, XQueryBundle.message("parser.error.incomplete-double-exponent"))
            return true
        }
        return false
    }

    private fun parseVarRef(type: IElementType?): Boolean {
        val varRefMarker = matchTokenTypeWithMarker(XPathTokenType.VARIABLE_INDICATOR)
        if (varRefMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.VAR_NAME, type === XQueryElementType.MAP_CONSTRUCTOR_ENTRY)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
            }

            varRefMarker.done(XQueryElementType.VAR_REF)
            return true
        }
        return false
    }

    private fun parseParenthesizedExpr(): Boolean {
        val parenthesizedExprMarker = matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (parenthesizedExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (parseExpr(XQueryElementType.EXPR)) {
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            parenthesizedExprMarker.done(XQueryElementType.PARENTHESIZED_EXPR)
            return true
        }
        return false
    }

    private fun parseContextItemExpr(): Boolean {
        val contextItemExprMarker = matchTokenTypeWithMarker(XQueryTokenType.DOT)
        if (contextItemExprMarker != null) {
            contextItemExprMarker.done(XQueryElementType.CONTEXT_ITEM_EXPR)
            return true
        }
        return false
    }

    private fun parseOrderedExpr(): Boolean {
        val orderedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ORDERED)
        if (orderedExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                orderedExprMarker.rollbackTo()
                return false
            }

            orderedExprMarker.done(XQueryElementType.ORDERED_EXPR)
            return true
        }
        return false
    }

    private fun parseUnorderedExpr(): Boolean {
        val unorderedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_UNORDERED)
        if (unorderedExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                unorderedExprMarker.rollbackTo()
                return false
            }

            unorderedExprMarker.done(XQueryElementType.UNORDERED_EXPR)
            return true
        }
        return false
    }

    private fun parseNonDeterministicFunctionCall(): Boolean {
        val nonDeterministicFunctionCallMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NON_DETERMINISTIC);
        if (nonDeterministicFunctionCallMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() != XPathTokenType.VARIABLE_INDICATOR) {
                nonDeterministicFunctionCallMarker.rollbackTo()
                return false
            }

            if (!parseVarRef(null)) {
                error(XQueryBundle.message("parser.error.expected", "VarDecl"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseArgumentList()) {
                error(XQueryBundle.message("parser.error.expected", "ArgumentList"))
            }

            nonDeterministicFunctionCallMarker.done(XQueryElementType.NON_DETERMINISTIC_FUNCTION_CALL)
            return true
        }
        return false
    }

    private fun parseFunctionCall(): Boolean {
        if (getTokenType() is IKeywordOrNCNameType) {
            val type = getTokenType() as IKeywordOrNCNameType?
            when (type!!.keywordType) {
                IKeywordOrNCNameType.KeywordType.KEYWORD, IKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                }
                IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME -> return false
                IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    // Don't keep the MarkLogic Schema/JSON parseTree here as KindTest is not anchored to the correct parent
                    // at this point.
                    val testMarker = mark()
                    var status = parseSchemaKindTest()
                    if (status == ParseStatus.NOT_MATCHED) {
                        status = parseJsonKindTest()
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

        val functionCallMarker = mark()
        if (parseEQName(XQueryElementType.QNAME)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseArgumentList()) {
                functionCallMarker.rollbackTo()
                return false
            }

            functionCallMarker.done(XQueryElementType.FUNCTION_CALL)
            return true
        }

        functionCallMarker.drop()
        return false
    }

    private fun parseArgumentList(): Boolean {
        val argumentListMarker = matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (argumentListMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (parseArgument()) {
                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseArgument() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-either", "ExprSingle", "?"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens()
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            argumentListMarker.done(XQueryElementType.ARGUMENT_LIST)
            return true
        }
        return false
    }

    private fun parseArgument(): Boolean {
        val argumentMarker = mark()
        if (parseExprSingle() || parseArgumentPlaceholder()) {
            argumentMarker.done(XQueryElementType.ARGUMENT)
            return true
        }
        argumentMarker.drop()
        return false
    }

    private fun parseArgumentPlaceholder(): Boolean {
        val argumentPlaceholderMarker = matchTokenTypeWithMarker(XQueryTokenType.OPTIONAL)
        if (argumentPlaceholderMarker != null) {
            argumentPlaceholderMarker.done(XQueryElementType.ARGUMENT_PLACEHOLDER)
            return true
        }
        return false
    }

    private fun parseFunctionItemExpr(): Boolean {
        return parseNamedFunctionRef() || parseInlineFunctionExpr() || parseSimpleInlineFunctionExpr()
    }

    private fun parseNamedFunctionRef(): Boolean {
        val namedFunctionRefMarker = mark()
        if (parseEQName(XQueryElementType.QNAME)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.FUNCTION_REF_OPERATOR)) {
                namedFunctionRefMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"))
            }

            namedFunctionRefMarker.done(XQueryElementType.NAMED_FUNCTION_REF)
            return true
        }

        namedFunctionRefMarker.drop()
        return false
    }

    private fun parseInlineFunctionExpr(): Boolean {
        val inlineFunctionExprMarker = mark()

        var haveAnnotations = false
        while (parseAnnotation()) {
            parseWhiteSpaceAndCommentTokens()
            haveAnnotations = true
        }

        if (matchTokenType(XQueryTokenType.K_FUNCTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                if (!haveAnnotations) {
                    inlineFunctionExprMarker.rollbackTo()
                    return false
                }

                error(XQueryBundle.message("parser.error.expected", "("))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            parseParamList()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType()) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.FUNCTION_BODY, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"))
                parseExpr(XQueryElementType.EXPR)

                parseWhiteSpaceAndCommentTokens()
                matchTokenType(XQueryTokenType.BLOCK_CLOSE)
            }

            inlineFunctionExprMarker.done(XQueryElementType.INLINE_FUNCTION_EXPR)
            return true
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected-keyword", "function"))

            inlineFunctionExprMarker.done(XQueryElementType.INLINE_FUNCTION_EXPR)
            return true
        }

        inlineFunctionExprMarker.drop()
        return false
    }

    private fun parseSimpleInlineFunctionExpr(): Boolean {
        val inlineFunctionExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_FN)
        if (inlineFunctionExprMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                inlineFunctionExprMarker.rollbackTo()
                return false
            }

            inlineFunctionExprMarker.done(XQueryElementType.SIMPLE_INLINE_FUNCTION_EXPR)
            return true
        }
        return false
    }

    private fun parseLookup(type: IElementType): Boolean {
        val lookupMarker = matchTokenTypeWithMarker(XQueryTokenType.OPTIONAL)
        if (lookupMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseKeySpecifier()) {
                if (type === XQueryElementType.UNARY_LOOKUP) {
                    // NOTE: This conflicts with '?' used as an ArgumentPlaceholder, so don't match '?' only as UnaryLookup.
                    lookupMarker.rollbackTo()
                    return false
                } else {
                    error(XQueryBundle.message("parser.error.expected", "KeySpecifier"))
                }
            }

            lookupMarker.done(type)
            return true
        }
        return false
    }

    private fun parseKeySpecifier(): Boolean {
        val keySpecifierMarker = mark()
        if (
            matchTokenType(XPathTokenType.STAR) ||
            matchTokenType(XPathTokenType.INTEGER_LITERAL) ||
            parseEQName(XQueryElementType.NCNAME) ||
            parseParenthesizedExpr()
        ) {
            keySpecifierMarker.done(XQueryElementType.KEY_SPECIFIER)
            return true
        }
        keySpecifierMarker.drop()
        return false
    }

    private fun parseStringConstructor(): Boolean {
        val stringConstructorMarker = matchTokenTypeWithMarker(XQueryTokenType.STRING_CONSTRUCTOR_START)
        if (stringConstructorMarker != null) {
            parseStringConstructorContent()

            if (!matchTokenType(XQueryTokenType.STRING_CONSTRUCTOR_END)) {
                error(XQueryBundle.message("parser.error.incomplete-string-constructor"))
            }

            stringConstructorMarker.done(XQueryElementType.STRING_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseStringConstructorContent(): Boolean {
        val stringConstructorContentMarker = mark()
        if (matchTokenType(XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)) {
            while (matchTokenType(XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS) || parseStringConstructorInterpolation()) {
                //
            }
        }
        stringConstructorContentMarker.done(XQueryElementType.STRING_CONSTRUCTOR_CONTENT)
        return true
    }

    private fun parseStringConstructorInterpolation(): Boolean {
        val stringConstructorInterpolationMarker = matchTokenTypeWithMarker(XQueryTokenType.STRING_INTERPOLATION_OPEN)
        if (stringConstructorInterpolationMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            parseExpr(XQueryElementType.EXPR)

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.STRING_INTERPOLATION_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", "}`"))
            }

            stringConstructorInterpolationMarker.done(XQueryElementType.STRING_CONSTRUCTOR_INTERPOLATION)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: Constructors

    private fun parseArrayConstructor(): Boolean {
        return parseSquareArrayConstructor() || parseCurlyArrayConstructor()
    }

    private fun parseSquareArrayConstructor(): Boolean {
        val arrayConstructor = matchTokenTypeWithMarker(XQueryTokenType.SQUARE_OPEN)
        if (arrayConstructor != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (parseExprSingle()) {
                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseExprSingle() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens()
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.SQUARE_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "]"))
            }

            arrayConstructor.done(XQueryElementType.SQUARE_ARRAY_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCurlyArrayConstructor(): Boolean {
        var arrayConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY)
        if (arrayConstructor == null) {
            arrayConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY_NODE)
        }

        if (arrayConstructor != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                arrayConstructor.rollbackTo()
                return false
            }
            arrayConstructor.done(XQueryElementType.CURLY_ARRAY_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseBinaryConstructor(): Boolean {
        val binaryConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_BINARY)
        if (binaryConstructor != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                binaryConstructor.rollbackTo()
                return false
            }
            binaryConstructor.done(XQueryElementType.BINARY_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseBooleanConstructor(): Boolean {
        val booleanConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_BOOLEAN_NODE)
        if (booleanConstructor != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                booleanConstructor.rollbackTo()
                return false
            }
            booleanConstructor.done(XQueryElementType.BOOLEAN_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseMapConstructor(): Boolean {
        var mapConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_MAP)
        if (mapConstructor == null) {
            mapConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_OBJECT_NODE)
        }

        if (mapConstructor != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                mapConstructor.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseMapConstructorEntry()) {
                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseMapConstructorEntry() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "MapConstructor"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"))
            }

            mapConstructor.done(XQueryElementType.MAP_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseMapConstructorEntry(): Boolean {
        val mapConstructorEntry = mark()
        if (parseExprSingle(XQueryElementType.MAP_KEY_EXPR, XQueryElementType.MAP_CONSTRUCTOR_ENTRY)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.QNAME_SEPARATOR) && !matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                error(XQueryBundle.message("parser.error.expected-map-entry-assign"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExprSingle(XQueryElementType.MAP_VALUE_EXPR) && !haveError) {
                error(XQueryBundle.message("parser.error.expected-expression"))
            }

            mapConstructorEntry.done(XQueryElementType.MAP_CONSTRUCTOR_ENTRY)
            return true
        }
        mapConstructorEntry.drop()
        return false
    }

    private fun parseNodeConstructor(): Boolean {
        val constructorMarker = mark()
        if (parseDirectConstructor(0) || parseComputedConstructor()) {
            constructorMarker.done(XQueryElementType.NODE_CONSTRUCTOR)
            return true
        }

        constructorMarker.drop()
        return false
    }

    private fun parseNullConstructor(): Boolean {
        val nullConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_NULL_NODE)
        if (nullConstructor != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                nullConstructor.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", "}"))
            }

            nullConstructor.done(XQueryElementType.NULL_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseNumberConstructor(): Boolean {
        val numberConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_NUMBER_NODE)
        if (numberConstructor != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                numberConstructor.rollbackTo()
                return false
            }
            numberConstructor.done(XQueryElementType.NUMBER_CONSTRUCTOR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: NodeConstructor :: DirectConstructor

    private fun parseDirectConstructor(depth: Int): Boolean {
        return (parseDirElemConstructor(depth)
                || parseDirCommentConstructor()
                || parseDirPIConstructor()
                || parseCDataSection(null))
    }

    private fun parseDirElemConstructor(depth: Int): Boolean {
        val elementMarker = mark()
        if (matchTokenType(XQueryTokenType.OPEN_XML_TAG)) {
            errorOnTokenType(XQueryTokenType.XML_WHITE_SPACE, XQueryBundle.message("parser.error.unexpected-whitespace"))
            parseQName(XQueryElementType.QNAME)

            parseDirAttributeList()

            if (matchTokenType(XQueryTokenType.SELF_CLOSING_XML_TAG)) {
                //
            } else if (matchTokenType(XQueryTokenType.END_XML_TAG)) {
                parseDirElemContent(depth + 1)

                if (matchTokenType(XQueryTokenType.CLOSE_XML_TAG)) {
                    // NOTE: The XQueryLexer ensures that CLOSE_XML_TAG is followed by an NCNAME/QNAME.
                    parseQName(XQueryElementType.QNAME)

                    matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
                    if (!matchTokenType(XQueryTokenType.END_XML_TAG)) {
                        error(XQueryBundle.message("parser.error.expected", ">"))
                    }
                } else {
                    error(XQueryBundle.message("parser.error.expected-closing-tag"))
                }
            } else {
                error(XQueryBundle.message("parser.error.incomplete-open-tag"))
            }

            elementMarker.done(XQueryElementType.DIR_ELEM_CONSTRUCTOR)
            return true
        } else if (depth == 0 && getTokenType() === XQueryTokenType.CLOSE_XML_TAG) {
            error(XQueryBundle.message("parser.error.unexpected-closing-tag"))
            matchTokenType(XQueryTokenType.CLOSE_XML_TAG)
            parseQName(XQueryElementType.QNAME)
            matchTokenType(XPathTokenType.WHITE_SPACE)
            matchTokenType(XPathTokenType.GREATER_THAN)

            elementMarker.done(XQueryElementType.DIR_ELEM_CONSTRUCTOR)
            return true
        }

        elementMarker.drop()
        return false
    }

    private fun parseDirAttributeList(): Boolean {
        val attributeListMarker = mark()

        // NOTE: The XQuery grammar uses whitespace as the token to start the next iteration of the matching loop.
        // Because the parseQName function can consume that whitespace during error handling, the QName tokens are
        // used as the next iteration marker in this implementation.
        var parsed = matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
        while (parseDirAttribute()) {
            parsed = true
            matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
        }

        if (parsed) {
            attributeListMarker.done(XQueryElementType.DIR_ATTRIBUTE_LIST)
            return true
        }

        attributeListMarker.drop()
        return false
    }

    private fun parseDirAttribute(): Boolean {
        val attributeMarker = mark()
        if (parseQName(XQueryElementType.QNAME)) {
            var haveErrors = false

            matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
            if (!matchTokenType(XQueryTokenType.XML_EQUAL)) {
                error(XQueryBundle.message("parser.error.expected", "="))
                haveErrors = true
            }

            matchTokenType(XQueryTokenType.XML_WHITE_SPACE)
            if (!parseDirAttributeValue() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-attribute-string"))
            }

            attributeMarker.done(XQueryElementType.DIR_ATTRIBUTE)
            return true
        }
        attributeMarker.drop()
        return false
    }

    private fun parseDirAttributeValue(): Boolean {
        val stringMarker = matchTokenTypeWithMarker(XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
        while (stringMarker != null) {
            if (matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS) ||
                    matchTokenType(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE) ||
                    matchTokenType(XQueryTokenType.XML_CHARACTER_REFERENCE) ||
                    matchTokenType(XQueryTokenType.XML_ESCAPED_CHARACTER)) {
                //
            } else if (matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_END)) {
                stringMarker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE)
                return true
            } else if (matchTokenType(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (errorOnTokenType(XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) || matchTokenType(XPathTokenType.BAD_CHARACTER)) {
                //
            } else if (parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) || errorOnTokenType(XQueryTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr"))) {
                //
            } else {
                stringMarker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE)
                error(XQueryBundle.message("parser.error.incomplete-attribute-value"))
                return true
            }
        }
        return false
    }

    private fun parseDirCommentConstructor(): Boolean {
        val commentMarker = matchTokenTypeWithMarker(XQueryTokenType.XML_COMMENT_START_TAG)
        if (commentMarker != null) {
            // NOTE: XQueryTokenType.XML_COMMENT is omitted by the PsiBuilder.
            if (matchTokenType(XQueryTokenType.XML_COMMENT_END_TAG)) {
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR)
            } else {
                advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR)
                error(XQueryBundle.message("parser.error.incomplete-xml-comment"))
            }
            return true
        }

        return errorOnTokenType(XQueryTokenType.XML_COMMENT_END_TAG, XQueryBundle.message("parser.error.end-of-comment-without-start", "<!--"))
    }

    private fun parseDirPIConstructor(): Boolean {
        val piMarker = matchTokenTypeWithMarker(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        if (piMarker != null) {
            var haveErrors = false

            if (matchTokenType(XPathTokenType.WHITE_SPACE)) {
                error(XQueryBundle.message("parser.error.unexpected-whitespace"))
                haveErrors = true
            }

            if (!parseQName(XQueryElementType.NCNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-ncname"))
                haveErrors = true
            }

            matchTokenType(XPathTokenType.WHITE_SPACE)
            if (!matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-pi-contents"))
                haveErrors = true
            }

            while (
                matchTokenType(XPathTokenType.BAD_CHARACTER) ||
                matchTokenType(XPathTokenType.NCNAME) ||
                matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            ) {
                //
            }

            if (!matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_END) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "?>"))
            }

            piMarker.done(XQueryElementType.DIR_PI_CONSTRUCTOR)
            return true
        }

        return false
    }

    private fun parseDirElemContent(depth: Int): Boolean {
        val elemContentMarker = mark()
        var matched = false
        while (true) {
            if (
                matchTokenType(XQueryTokenType.XML_ELEMENT_CONTENTS) ||
                matchTokenType(XPathTokenType.BAD_CHARACTER) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                matchTokenType(XPathTokenType.ESCAPED_CHARACTER) ||
                errorOnTokenType(XQueryTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr")) ||
                errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity"))
            ) {
                matched = true
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"))
                matched = true
            } else if (parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL) ||
                    parseCDataSection(XQueryElementType.DIR_ELEM_CONTENT) ||
                    parseDirectConstructor(depth)) {
                matched = true
            } else {
                if (matched) {
                    elemContentMarker.done(XQueryElementType.DIR_ELEM_CONTENT)
                    return true
                }

                elemContentMarker.drop()
                return false
            }
        }
    }

    private fun parseCDataSection(context: IElementType?): Boolean {
        val cdataMarker = mark()
        val errorMarker = mark()
        if (matchTokenType(XQueryTokenType.CDATA_SECTION_START_TAG)) {
            if (context == null) {
                errorMarker.error(XQueryBundle.message("parser.error.cdata-section-not-in-element-content"))
            } else {
                errorMarker.drop()
            }

            matchTokenType(XQueryTokenType.CDATA_SECTION)
            if (matchTokenType(XQueryTokenType.CDATA_SECTION_END_TAG)) {
                cdataMarker.done(XQueryElementType.CDATA_SECTION)
            } else {
                advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                cdataMarker.done(XQueryElementType.CDATA_SECTION)
                error(XQueryBundle.message("parser.error.incomplete-cdata-section"))
            }
            return true
        }

        errorMarker.drop()
        cdataMarker.drop()
        return errorOnTokenType(XQueryTokenType.CDATA_SECTION_END_TAG, XQueryBundle.message("parser.error.end-of-cdata-section-without-start"))
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr :: NodeConstructor :: ComputedConstructor

    private fun parseComputedConstructor(): Boolean {
        return (parseCompDocConstructor()
                || parseCompElemConstructor()
                || parseCompAttrConstructor()
                || parseCompNamespaceConstructor()
                || parseCompTextConstructor()
                || parseCompCommentConstructor()
                || parseCompPIConstructor())
    }

    private fun parseCompDocConstructor(): Boolean {
        val documentMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT)
        if (documentMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                documentMarker.rollbackTo()
                return false
            }

            documentMarker.done(XQueryElementType.COMP_DOC_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompElemConstructor(): Boolean {
        val elementMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT)
        if (elementMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME) && !parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (getTokenType() === XPathTokenType.STRING_LITERAL_START) {
                    val marker = mark()
                    parseStringLiteral(XQueryElementType.STRING_LITERAL)
                    marker.error(XQueryBundle.message("parser.error.expected-qname-or-braced-expression"))
                } else {
                    elementMarker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_CONTENT_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)

            elementMarker.done(XQueryElementType.COMP_ELEM_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompAttrConstructor(): Boolean {
        val attributeMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE)
        if (attributeMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME) && !parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (getTokenType() === XPathTokenType.STRING_LITERAL_START) {
                    val marker = mark()
                    parseStringLiteral(XQueryElementType.STRING_LITERAL)
                    marker.error(XQueryBundle.message("parser.error.expected-qname-or-braced-expression"))
                } else {
                    attributeMarker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)

            attributeMarker.done(XQueryElementType.COMP_ATTR_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompNamespaceConstructor(): Boolean {
        val namespaceMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE)
        if (namespaceMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.PREFIX) && !parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_PREFIX_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                if (getTokenType() === XPathTokenType.STRING_LITERAL_START) {
                    val marker = mark()
                    parseStringLiteral(XQueryElementType.STRING_LITERAL)
                    marker.error(XQueryBundle.message("parser.error.expected-identifier-or-braced-expression"))
                } else {
                    namespaceMarker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_URI_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)

            namespaceMarker.done(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompTextConstructor(): Boolean {
        val textMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TEXT)
        if (textMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                textMarker.rollbackTo()
                return false
            }

            textMarker.done(XQueryElementType.COMP_TEXT_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompCommentConstructor(): Boolean {
        val commentMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMMENT)
        if (commentMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)) {
                commentMarker.rollbackTo()
                return false
            }

            commentMarker.done(XQueryElementType.COMP_COMMENT_CONSTRUCTOR)
            return true
        }
        return false
    }

    private fun parseCompPIConstructor(): Boolean {
        val piMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION)
        if (piMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseQName(XQueryElementType.NCNAME) && !parseEnclosedExprOrBlock(null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)) {
                if (getTokenType() === XPathTokenType.STRING_LITERAL_START) {
                    val marker = mark()
                    parseStringLiteral(XQueryElementType.STRING_LITERAL)
                    marker.error(XQueryBundle.message("parser.error.expected-identifier-or-braced-expression"))
                } else {
                    piMarker.rollbackTo()
                    return false
                }
            }

            parseWhiteSpaceAndCommentTokens()
            parseEnclosedExprOrBlock(XQueryElementType.ENCLOSED_EXPR, BlockOpen.REQUIRED, BlockExpr.OPTIONAL)

            piMarker.done(XQueryElementType.COMP_PI_CONSTRUCTOR)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTSelection

    private fun parseFTSelection(): Boolean {
        val selectionMarker = mark()
        if (parseFTOr()) {
            do {
                parseWhiteSpaceAndCommentTokens()
            } while (parseFTPosFilter())

            selectionMarker.done(XQueryElementType.FT_SELECTION)
            return true
        }
        selectionMarker.drop()
        return false
    }

    private fun parseFTOr(): Boolean {
        val orMarker = mark()
        if (parseFTAnd()) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XQueryTokenType.K_FTOR)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseFTAnd() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "FTAnd"))
                    haveErrors = true
                }
            }

            orMarker.done(XQueryElementType.FT_OR)
            return true
        }
        orMarker.drop()
        return false
    }

    private fun parseFTAnd(): Boolean {
        val andMarker = mark()
        if (parseFTMildNot()) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XQueryTokenType.K_FTAND)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseFTMildNot() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "FTMildNot"))
                    haveErrors = true
                }
            }

            andMarker.done(XQueryElementType.FT_AND)
            return true
        }
        andMarker.drop()
        return false
    }

    private fun parseFTMildNot(): Boolean {
        val mildNotMarker = mark()
        if (parseFTUnaryNot()) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XQueryTokenType.K_NOT)) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.K_IN) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "in"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseFTUnaryNot() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "FTUnaryNot"))
                    haveErrors = true
                }

                parseWhiteSpaceAndCommentTokens()
            }

            mildNotMarker.done(XQueryElementType.FT_MILD_NOT)
            return true
        }
        mildNotMarker.drop()
        return false
    }

    private fun parseFTUnaryNot(): Boolean {
        val unaryNotMarker = mark()

        matchTokenType(XQueryTokenType.K_FTNOT)

        parseWhiteSpaceAndCommentTokens()
        if (parseFTPrimaryWithOptions()) {
            parseWhiteSpaceAndCommentTokens()

            unaryNotMarker.done(XQueryElementType.FT_UNARY_NOT)
            return true
        }
        unaryNotMarker.drop()
        return false
    }

    private fun parseFTPrimaryWithOptions(): Boolean {
        val primaryWithOptionsMarker = mark()
        if (parseFTPrimary()) {
            parseWhiteSpaceAndCommentTokens()
            parseFTMatchOptions()

            parseWhiteSpaceAndCommentTokens()
            parseFTWeight()

            primaryWithOptionsMarker.done(XQueryElementType.FT_PRIMARY_WITH_OPTIONS)
            return true
        }
        primaryWithOptionsMarker.drop()
        return false
    }

    private fun parseFTPrimary(): Boolean {
        val primaryMarker = mark()
        if (parseFTWords()) {
            parseWhiteSpaceAndCommentTokens()
            parseFTTimes()

            primaryMarker.done(XQueryElementType.FT_PRIMARY)
            return true
        } else if (matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseFTSelection()) {
                error(XQueryBundle.message("parser.error.expected", "FTSelection"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            primaryMarker.done(XQueryElementType.FT_PRIMARY)
            return true
        } else if (parseFTExtensionSelection()) {
            primaryMarker.done(XQueryElementType.FT_PRIMARY)
            return true
        }
        primaryMarker.drop()
        return false
    }

    private fun parseFTWords(): Boolean {
        val wordsMarker = mark()
        if (parseFTWordsValue()) {
            parseWhiteSpaceAndCommentTokens()
            parseFTAnyallOption()

            wordsMarker.done(XQueryElementType.FT_WORDS)
            return true
        }
        wordsMarker.drop()
        return false
    }

    private fun parseFTWordsValue(): Boolean {
        val wordsValueMarker = mark()
        if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            wordsValueMarker.done(XQueryElementType.FT_WORDS_VALUE)
            return true
        } else if (matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"))
            }

            wordsValueMarker.done(XQueryElementType.FT_WORDS_VALUE)
            return true
        }
        wordsValueMarker.drop()
        return false
    }

    private fun parseFTExtensionSelection(): Boolean {
        val extensionSelectionMarker = mark()
        var haveError = false

        var havePragma = false
        while (parsePragma()) {
            parseWhiteSpaceAndCommentTokens()
            havePragma = true
        }

        if (!havePragma) {
            extensionSelectionMarker.drop()
            return false
        }

        parseWhiteSpaceAndCommentTokens()
        if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
            error(XQueryBundle.message("parser.error.expected", "{"))
            haveError = true
        }

        parseWhiteSpaceAndCommentTokens()
        parseFTSelection()

        parseWhiteSpaceAndCommentTokens()
        if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveError) {
            error(XQueryBundle.message("parser.error.expected", "}"))
        }

        extensionSelectionMarker.done(XQueryElementType.FT_EXTENSION_SELECTION)
        return true
    }

    private fun parseFTAnyallOption(): Boolean {
        val anyallOptionMarker = mark()
        if (matchTokenType(XQueryTokenType.K_ANY)) {
            parseWhiteSpaceAndCommentTokens()
            matchTokenType(XQueryTokenType.K_WORD)

            anyallOptionMarker.done(XQueryElementType.FT_ANYALL_OPTION)
            return true
        } else if (matchTokenType(XQueryTokenType.K_ALL)) {
            parseWhiteSpaceAndCommentTokens()
            matchTokenType(XQueryTokenType.K_WORDS)

            anyallOptionMarker.done(XQueryElementType.FT_ANYALL_OPTION)
            return true
        } else if (matchTokenType(XQueryTokenType.K_PHRASE)) {
            anyallOptionMarker.done(XQueryElementType.FT_ANYALL_OPTION)
            return true
        }
        anyallOptionMarker.drop()
        return false
    }

    private fun parseFTTimes(): Boolean {
        val timesMarker = matchTokenTypeWithMarker(XQueryTokenType.K_OCCURS)
        if (timesMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseFTRange(XQueryElementType.FT_RANGE)) {
                error(XQueryBundle.message("parser.error.expected", "FTRange"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_TIMES) && !haveError) {
                error(XQueryBundle.message("parser.error.expected-keyword", "times"))
            }

            timesMarker.done(XQueryElementType.FT_TIMES)
            return true
        }
        return false
    }

    private fun parseFTRange(type: IElementType): Boolean {
        if (getTokenType() === XQueryTokenType.K_EXACTLY) {
            val rangeMarker = mark()
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (type === XQueryElementType.FT_LITERAL_RANGE) {
                if (!matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"))
                }
            } else {
                if (!parseAdditiveExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                }
            }

            rangeMarker.done(type)
            return true
        } else if (getTokenType() === XQueryTokenType.K_AT) {
            val rangeMarker = mark()
            advanceLexer()

            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_LEAST) && !matchTokenType(XQueryTokenType.K_MOST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "least, most"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (type === XQueryElementType.FT_LITERAL_RANGE) {
                if (!matchTokenType(XPathTokenType.INTEGER_LITERAL) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"))
                }
            } else {
                if (!parseAdditiveExpr(type) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                }
            }

            rangeMarker.done(type)
            return true
        } else if (getTokenType() === XQueryTokenType.K_FROM) {
            val rangeMarker = mark()
            advanceLexer()

            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (type === XQueryElementType.FT_LITERAL_RANGE) {
                if (!matchTokenType(XPathTokenType.INTEGER_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"))
                    haveError = true
                }
            } else {
                if (!parseAdditiveExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.K_TO) && !haveError) {
                error(XQueryBundle.message("parser.error.expected-keyword", "to"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (type === XQueryElementType.FT_LITERAL_RANGE) {
                if (!matchTokenType(XPathTokenType.INTEGER_LITERAL) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"))
                }
            } else {
                if (!parseAdditiveExpr(type) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                }
            }

            rangeMarker.done(type)
            return true
        }
        return false
    }

    private fun parseFTWeight(): Boolean {
        val weightMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WEIGHT)
        if (weightMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "{"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseExpr(XQueryElementType.EXPR) && !haveError) {
                error(XQueryBundle.message("parser.error.expected-expression"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", "}"))
            }

            weightMarker.done(XQueryElementType.FT_WEIGHT)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTPosFilter

    private fun parseFTPosFilter(): Boolean {
        return parseFTOrder() || parseFTWindow() || parseFTDistance() || parseFTScope() || parseFTContent()
    }

    private fun parseFTOrder(): Boolean {
        val orderMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ORDERED)
        if (orderMarker != null) {
            orderMarker.done(XQueryElementType.FT_ORDER)
            return true
        }
        return false
    }

    private fun parseFTWindow(): Boolean {
        val windowMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WINDOW)
        if (windowMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseAdditiveExpr(XQueryElementType.FT_WINDOW)) {
                error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseFTUnit() && !haveError) {
                error(XQueryBundle.message("parser.error.expected-keyword", "paragraphs, sentences, words"))
            }

            windowMarker.done(XQueryElementType.FT_WINDOW)
            return true
        }
        return false
    }

    private fun parseFTDistance(): Boolean {
        val distanceMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DISTANCE)
        if (distanceMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseFTRange(XQueryElementType.FT_RANGE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "at, exactly, from"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseFTUnit() && !haveError) {
                error(XQueryBundle.message("parser.error.expected-keyword", "paragraphs, sentences, words"))
            }

            distanceMarker.done(XQueryElementType.FT_DISTANCE)
            return true
        }
        return false
    }

    private fun parseFTScope(): Boolean {
        val scopeMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SAME, XQueryTokenType.K_DIFFERENT)
        if (scopeMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseFTBigUnit()) {
                error(XQueryBundle.message("parser.error.expected-keyword", "paragraph, sentence"))
            }

            scopeMarker.done(XQueryElementType.FT_SCOPE)
            return true
        }
        return false
    }

    private fun parseFTContent(): Boolean {
        if (getTokenType() === XQueryTokenType.K_AT) {
            val contentMarker = mark()
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_START) && !matchTokenType(XQueryTokenType.K_END)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "end, start"))
            }

            contentMarker.done(XQueryElementType.FT_CONTENT)
            return true
        } else if (getTokenType() === XQueryTokenType.K_ENTIRE) {
            val contentMarker = mark()
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_CONTENT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "content"))
            }

            contentMarker.done(XQueryElementType.FT_CONTENT)
            return true
        }
        return false
    }

    private fun parseFTUnit(): Boolean {
        if (getTokenType() === XQueryTokenType.K_WORDS ||
                getTokenType() === XQueryTokenType.K_SENTENCES ||
                getTokenType() === XQueryTokenType.K_PARAGRAPHS) {
            val marker = mark()
            advanceLexer()
            marker.done(XQueryElementType.FT_UNIT)
            return true
        }
        return false
    }

    private fun parseFTBigUnit(): Boolean {
        if (getTokenType() === XQueryTokenType.K_SENTENCE || getTokenType() === XQueryTokenType.K_PARAGRAPH) {
            val marker = mark()
            advanceLexer()
            marker.done(XQueryElementType.FT_BIG_UNIT)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: FTMatchOptions

    private fun parseFTMatchOptions(): Boolean {
        val matchOptionsMarker = mark()

        var haveFTMatchOptions = false
        var haveFTMatchOption = false
        do {
            if (matchTokenType(XQueryTokenType.K_USING)) {
                parseWhiteSpaceAndCommentTokens()
                parseFTMatchOption()
                haveFTMatchOption = true
            } else if (getTokenType() === XQueryTokenType.K_CASE ||
                    getTokenType() === XQueryTokenType.K_DIACRITICS ||
                    getTokenType() === XQueryTokenType.K_FUZZY ||
                    getTokenType() === XQueryTokenType.K_LANGUAGE ||
                    getTokenType() === XQueryTokenType.K_LOWERCASE ||
                    getTokenType() === XQueryTokenType.K_NO ||
                    getTokenType() === XQueryTokenType.K_OPTION ||
                    getTokenType() === XQueryTokenType.K_STEMMING ||
                    getTokenType() === XQueryTokenType.K_STOP ||
                    getTokenType() === XQueryTokenType.K_THESAURUS ||
                    getTokenType() === XQueryTokenType.K_UPPERCASE ||
                    getTokenType() === XQueryTokenType.K_WILDCARDS) {
                error(XQueryBundle.message("parser.error.expected-keyword", "using"))
                parseFTMatchOption()
                haveFTMatchOption = true
            } else {
                haveFTMatchOption = false
            }

            parseWhiteSpaceAndCommentTokens()
            haveFTMatchOptions = haveFTMatchOptions or haveFTMatchOption
        } while (haveFTMatchOption)

        if (haveFTMatchOptions) {
            matchOptionsMarker.done(XQueryElementType.FT_MATCH_OPTIONS)
        } else {
            matchOptionsMarker.drop()
        }
        return haveFTMatchOptions
    }

    private fun parseFTMatchOption(): Boolean {
        val matchOptionMarker = mark()
        if (parseFTCaseOption(matchOptionMarker) ||
                parseFTDiacriticsOption(matchOptionMarker) ||
                parseFTExtensionOption(matchOptionMarker) ||
                parseFTFuzzyOption(matchOptionMarker) ||
                parseFTLanguageOption(matchOptionMarker) ||
                parseFTStemOption(matchOptionMarker) ||
                parseFTStopWordOption(matchOptionMarker) ||
                parseFTThesaurusOption(matchOptionMarker) ||
                parseFTWildCardOption(matchOptionMarker)) {
            //
        } else if (matchTokenType(XQueryTokenType.K_NO)) {
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_STEMMING)) {
                matchOptionMarker.done(XQueryElementType.FT_STEM_OPTION)
            } else if (matchTokenType(XQueryTokenType.K_STOP)) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.K_WORDS)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "words"))
                }

                matchOptionMarker.done(XQueryElementType.FT_STOP_WORD_OPTION)
            } else if (matchTokenType(XQueryTokenType.K_THESAURUS)) {
                matchOptionMarker.done(XQueryElementType.FT_THESAURUS_OPTION)
            } else if (matchTokenType(XQueryTokenType.K_WILDCARDS)) {
                matchOptionMarker.done(XQueryElementType.FT_WILDCARD_OPTION)
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "stemming, stop, thesaurus, wildcards"))
                matchOptionMarker.drop()
                return false
            }
        } else {
            // NOTE: `fuzzy` is the BaseX FTMatchOption extension.
            error(XQueryBundle.message("parser.error.expected-keyword-or-token", "FTMatchOption", "fuzzy"))
            matchOptionMarker.drop()
            return false
        }
        return true
    }

    private fun parseFTCaseOption(caseOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_LOWERCASE) || matchTokenType(XQueryTokenType.K_UPPERCASE)) {
            caseOptionMarker.done(XQueryElementType.FT_CASE_OPTION)
            return true
        } else if (matchTokenType(XQueryTokenType.K_CASE)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_SENSITIVE) && !matchTokenType(XQueryTokenType.K_INSENSITIVE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "sensitive, insensitive"))
            }

            caseOptionMarker.done(XQueryElementType.FT_CASE_OPTION)
            return true
        }
        return false
    }

    private fun parseFTDiacriticsOption(diacriticsOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_DIACRITICS)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_SENSITIVE) && !matchTokenType(XQueryTokenType.K_INSENSITIVE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "sensitive, insensitive"))
            }

            diacriticsOptionMarker.done(XQueryElementType.FT_DIACRITICS_OPTION)
            return true
        }
        return false
    }

    private fun parseFTStemOption(stemOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_STEMMING)) {
            stemOptionMarker.done(XQueryElementType.FT_STEM_OPTION)
            return true
        }
        return false
    }

    private fun parseFTThesaurusOption(thesaurusOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_THESAURUS)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            val hasParenthesis = matchTokenType(XPathTokenType.PARENTHESIS_OPEN)

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !parseFTThesaurusID()) {
                if (hasParenthesis) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "at, default"))
                } else {
                    error(XQueryBundle.message("parser.error.expected-keyword-or-token", "(", "at, default"))
                }
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            var haveComma: Boolean
            if (hasParenthesis) {
                haveComma = matchTokenType(XPathTokenType.COMMA)
            } else {
                haveComma = errorOnTokenType(XPathTokenType.COMMA, XQueryBundle.message("parser.error.full-text.multientry-thesaurus-requires-parenthesis"))
                haveError = haveError or haveComma
            }

            while (haveComma) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseFTThesaurusID() && !haveError) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "at"))

                    matchTokenType(XQueryTokenType.K_DEFAULT)
                    parseWhiteSpaceAndCommentTokens()

                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens()
                haveComma = matchTokenType(XPathTokenType.COMMA)
            }

            parseWhiteSpaceAndCommentTokens()
            if (hasParenthesis) {
                if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected-either", ",", ")"))
                }
            } else if (!haveError) {
                errorOnTokenType(XPathTokenType.PARENTHESIS_CLOSE, XQueryBundle.message("parser.error.expected-keyword-or-token", ";", "using"))
            } else {
                matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)
            }

            thesaurusOptionMarker.done(XQueryElementType.FT_THESAURUS_OPTION)
            return true
        }
        return false
    }

    private fun parseFTThesaurusID(): Boolean {
        val thesaurusIdMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AT)
        if (thesaurusIdMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "URILiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XQueryTokenType.K_RELATIONSHIP)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "StringLiteral"))
                    haveError = true
                }
            }

            if (parseFTRange(XQueryElementType.FT_LITERAL_RANGE)) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XQueryTokenType.K_LEVELS) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "levels"))
                }
            }

            thesaurusIdMarker.done(XQueryElementType.FT_THESAURUS_ID)
            return true
        }
        return false
    }

    private fun parseFTStopWordOption(stopWordOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_STOP)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_WORDS)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "words"))
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !parseFTStopWords()) {
                error(XQueryBundle.message("parser.error.expected-keyword-or-token", "(", "at, default"))
            }

            do {
                parseWhiteSpaceAndCommentTokens()
            } while (parseFTStopWordsInclExcl())

            stopWordOptionMarker.done(XQueryElementType.FT_STOP_WORD_OPTION)
            return true
        }
        return false
    }

    private fun parseFTStopWords(): Boolean {
        if (getTokenType() === XQueryTokenType.K_AT) {
            val stopWordsMarker = mark()
            advanceLexer()

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "URILiteral"))
            }

            stopWordsMarker.done(XQueryElementType.FT_STOP_WORDS)
            return true
        } else if (getTokenType() === XPathTokenType.PARENTHESIS_OPEN) {
            val stopWordsMarker = mark()
            advanceLexer()

            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "StringLiteral"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "StringLiteral"))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens()
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected-either", ",", ")"))
            }

            stopWordsMarker.done(XQueryElementType.FT_STOP_WORDS)
            return true
        }
        return false
    }

    private fun parseFTStopWordsInclExcl(): Boolean {
        val stopWordsInclExclMarker = matchTokenTypeWithMarker(XPathTokenType.K_UNION, XPathTokenType.K_EXCEPT)
        if (stopWordsInclExclMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseFTStopWords()) {
                error(XQueryBundle.message("parser.error.expected-keyword-or-token", "(", "at"))
            }

            stopWordsInclExclMarker.done(XQueryElementType.FT_STOP_WORDS_INCL_EXCL)
            return true
        }
        return false
    }

    private fun parseFTLanguageOption(languageOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_LANGUAGE)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "StringLiteral"))
            }

            languageOptionMarker.done(XQueryElementType.FT_LANGUAGE_OPTION)
            return true
        }
        return false
    }

    private fun parseFTWildCardOption(wildcardOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_WILDCARDS)) {
            wildcardOptionMarker.done(XQueryElementType.FT_WILDCARD_OPTION)
            return true
        }
        return false
    }

    private fun parseFTExtensionOption(extensionOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_OPTION)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "StringLiteral"))
            }

            extensionOptionMarker.done(XQueryElementType.FT_EXTENSION_OPTION)
            return true
        }
        return false
    }

    private fun parseFTFuzzyOption(fuzzyOptionMarker: PsiBuilder.Marker): Boolean {
        if (matchTokenType(XQueryTokenType.K_FUZZY)) {
            fuzzyOptionMarker.done(XQueryElementType.FT_FUZZY_OPTION)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: Expr :: UpdatingFunctionCall

    private fun parseUpdatingFunctionCall(): Boolean {
        val updatingFunctionCallMarker = matchTokenTypeWithMarker(XQueryTokenType.K_INVOKE)
        if (updatingFunctionCallMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XQueryTokenType.K_UPDATING)) {
                if (matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) { // FunctionCall
                    updatingFunctionCallMarker.rollbackTo()
                    return false
                }

                error(XQueryBundle.message("parser.error.expected-keyword", "updating"))
                haveErrors = true

                parseWhiteSpaceAndCommentTokens()
                if (!parsePrimaryExpr(null)) { // AbbrevForwardStep
                    updatingFunctionCallMarker.rollbackTo()
                    return false
                }
            } else {
                parseWhiteSpaceAndCommentTokens()
                if (!parsePrimaryExpr(null)) {
                    error(XQueryBundle.message("parser.error.expected", "PrimaryExpr"))
                    haveErrors = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "("))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseExprSingle()) {
                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (parseExprSingle() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            updatingFunctionCallMarker.done(XQueryElementType.UPDATING_FUNCTION_CALL)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration

    private fun parseTypeDeclaration(): Boolean {
        if (matchTokenType(XPathTokenType.K_AS)) {
            parseWhiteSpaceAndCommentTokens()
            if (!parseSequenceType()) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: SequenceType

    private fun parseSequenceTypeUnion(): Boolean {
        val sequenceTypeUnionMarker = mark()
        if (parseSequenceTypeList()) {
            var haveErrors = false
            var haveSequenceTypeUnion = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.UNION)) {
                haveSequenceTypeUnion = true
                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceTypeList() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens()
            }

            if (haveSequenceTypeUnion)
                sequenceTypeUnionMarker.done(XQueryElementType.SEQUENCE_TYPE_UNION)
            else
                sequenceTypeUnionMarker.drop()
            return true
        }
        sequenceTypeUnionMarker.drop()
        return false
    }

    private fun parseSequenceTypeList(): Boolean {
        val sequenceTypeListMarker = mark()
        if (parseSequenceType()) {
            var haveErrors = false
            var haveSequenceTypeList = false

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                haveSequenceTypeList = true
                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens()
            }

            if (haveSequenceTypeList)
                sequenceTypeListMarker.done(XQueryElementType.SEQUENCE_TYPE_LIST)
            else
                sequenceTypeListMarker.drop()
            return true
        }
        sequenceTypeListMarker.drop()
        return false
    }

    private fun parseSequenceType(): Boolean {
        val sequenceTypeMarker = mark()
        if (matchTokenType(XQueryTokenType.K_EMPTY_SEQUENCE) || matchTokenType(XQueryTokenType.K_EMPTY)) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                sequenceTypeMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE)
            return true
        } else if (parseItemType()) {
            parseWhiteSpaceAndCommentTokens()
            if (parseOccurrenceIndicator())
                sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE)
            else
                sequenceTypeMarker.drop()
            return true
        } else if (parseParenthesizedSequenceType()) {
            sequenceTypeMarker.drop()
            return true
        }

        sequenceTypeMarker.drop()
        return false
    }

    private fun parseOccurrenceIndicator(): Boolean {
        return matchTokenType(XQueryTokenType.OPTIONAL) ||
                matchTokenType(XPathTokenType.STAR) ||
                matchTokenType(XPathTokenType.PLUS)
    }

    private fun parseParenthesizedSequenceType(): Boolean {
        if (matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseSequenceTypeUnion()) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: ItemType

    private fun parseItemType(): Boolean {
        return parseKindTest() ||
                parseAnyItemType() ||
                parseAnnotatedFunctionOrSequence() ||
                parseMapTest() ||
                parseArrayTest() ||
                parseTupleType() ||
                parseUnionType() ||
                parseAtomicOrUnionType() ||
                parseParenthesizedItemType()
    }

    private fun parseAtomicOrUnionType(): Boolean {
        return parseEQName(XQueryElementType.ATOMIC_OR_UNION_TYPE)
    }

    private fun parseAnyItemType(): Boolean {
        val itemTypeMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ITEM)
        if (itemTypeMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                itemTypeMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            itemTypeMarker.done(XQueryElementType.ANY_ITEM_TYPE)
            return true
        }
        return false
    }

    private fun parseTupleType(): Boolean {
        val tupleTypeMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TUPLE)
        if (tupleTypeMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                tupleTypeMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseTupleField()) {
                error(XQueryBundle.message("parser.error.expected", "NCName"))
                haveError = true
            }

            var isExtensible = false
            var haveNext = true
            while (haveNext) {
                parseWhiteSpaceAndCommentTokens()
                if (isExtensible) {
                    val marker = mark()
                    if (!matchTokenType(XPathTokenType.COMMA)) {
                        haveNext = false
                        marker.drop()
                        continue
                    } else {
                        marker.error(XQueryBundle.message("parser.error.tuple-wildcard-with-names-after"))
                    }
                } else if (!matchTokenType(XPathTokenType.COMMA)) {
                    haveNext = false
                    continue
                }

                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XPathTokenType.STAR)) {
                    isExtensible = true
                } else if (!parseTupleField() && !haveError) {
                    error(XQueryBundle.message("parser.error.expected-either", "NCName", "*"))
                    haveError = true
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            tupleTypeMarker.done(XQueryElementType.TUPLE_TYPE)
            return true
        }
        return false
    }

    private fun parseTupleField(): Boolean {
        val tupleFieldMarker = mark()
        if (parseNCName(XQueryElementType.NCNAME)) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            val haveSeparator =
                if (matchTokenType(XQueryTokenType.ELVIS)) // ?: without whitespace
                    true
                else {
                    matchTokenType(XQueryTokenType.OPTIONAL)
                    parseWhiteSpaceAndCommentTokens()
                    matchTokenType(XPathTokenType.QNAME_SEPARATOR)
                }

            if (!haveSeparator) {
                if (getTokenType() === XPathTokenType.COMMA || getTokenType() === XPathTokenType.PARENTHESIS_CLOSE) {
                    tupleFieldMarker.done(XQueryElementType.TUPLE_FIELD)
                    return true
                }
                error(XQueryBundle.message("parser.error.expected", ":"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseSequenceType() && !haveError) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"))
            }

            tupleFieldMarker.done(XQueryElementType.TUPLE_FIELD)
            return true
        }
        tupleFieldMarker.drop()
        return false
    }

    private fun parseUnionType(): Boolean {
        val unionTypeMarker = matchTokenTypeWithMarker(XPathTokenType.K_UNION)
        if (unionTypeMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                unionTypeMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected", "QName"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            while (matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens()
                if (!parseEQName(XQueryElementType.QNAME) && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "QName"))
                    haveError = true
                }
                parseWhiteSpaceAndCommentTokens()
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            unionTypeMarker.done(XQueryElementType.UNION_TYPE)
            return true
        }
        return false
    }

    private fun parseAnnotatedFunctionOrSequence(): Boolean {
        val marker = mark()

        var haveAnnotations = false
        while (parseAnnotation()) {
            parseWhiteSpaceAndCommentTokens()
            haveAnnotations = true
        }

        if (haveAnnotations && getTokenType() === XPathTokenType.K_FOR) {
            advanceLexer()
            parseWhiteSpaceAndCommentTokens()

            if (!parseSequenceType()) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"))
            }

            marker.done(XQueryElementType.ANNOTATED_SEQUENCE_TYPE)
            return true
        } else if (parseAnyOrTypedFunctionTest()) {
            marker.done(XQueryElementType.FUNCTION_TEST)
            return true
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected-keyword", "function"))

            marker.done(XQueryElementType.FUNCTION_TEST)
            return true
        }

        marker.drop()
        return false
    }

    private fun parseAnyOrTypedFunctionTest(): Boolean {
        val functionTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_FUNCTION)
        if (functionTestMarker != null) {
            var type: KindTest = KindTest.ANY_TEST
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                functionTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.STAR)) {
                //
            } else if (parseSequenceType()) {
                type = KindTest.TYPED_TEST

                parseWhiteSpaceAndCommentTokens()
                while (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseSequenceType() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                        haveErrors = true
                    }
                    parseWhiteSpaceAndCommentTokens()
                }
            } else {
                type = KindTest.TYPED_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() === XPathTokenType.K_AS) {
                if (type === KindTest.ANY_TEST && !haveErrors) {
                    val errorMarker = mark()
                    advanceLexer()
                    errorMarker.error(XQueryBundle.message("parser.error.as-not-supported-in-test", "AnyFunctionTest"))
                    haveErrors = true
                } else {
                    advanceLexer()
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                }
            } else if (type === KindTest.TYPED_TEST) {
                error(XQueryBundle.message("parser.error.expected", "as"))
            }

            functionTestMarker.drop()
            return true
        }
        return false
    }

    private fun parseParenthesizedItemType(): Boolean {
        val parenthesizedItemTypeMarker = matchTokenTypeWithMarker(XPathTokenType.PARENTHESIS_OPEN)
        if (parenthesizedItemTypeMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!parseSequenceType()) {
                error(XQueryBundle.message("parser.error.expected", "ItemType"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                if (
                    getTokenType() === XPathTokenType.UNION ||
                    getTokenType() === XPathTokenType.COMMA
                ) {
                    parenthesizedItemTypeMarker.rollbackTo() // parenthesized sequence type
                    return false
                }
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            parenthesizedItemTypeMarker.done(XQueryElementType.PARENTHESIZED_ITEM_TYPE)
            return true
        }
        return false
    }

    private fun parseMapTest(): Boolean {
        val mapTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_MAP)
        if (mapTestMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                mapTestMarker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.STAR)) {
                type = XQueryElementType.ANY_MAP_TEST
            } else if (parseUnionType() || parseAtomicOrUnionType()) {
                parseWhiteSpaceAndCommentTokens()
                if (!matchTokenType(XPathTokenType.COMMA)) {
                    error(XQueryBundle.message("parser.error.expected", ","))
                    haveError = true
                }

                parseWhiteSpaceAndCommentTokens()
                if (!parseSequenceType() && !haveError) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"))
                    haveError = true
                }

                type = XQueryElementType.TYPED_MAP_TEST
            } else if (getTokenType() === XPathTokenType.COMMA) {
                error(XQueryBundle.message("parser.error.expected-either", "UnionType", "AtomicOrUnionType"))
                haveError = true

                matchTokenType(XPathTokenType.COMMA)

                parseWhiteSpaceAndCommentTokens()
                parseSequenceType()

                type = XQueryElementType.TYPED_MAP_TEST
            } else {
                error(XQueryBundle.message("parser.error.expected-eqname-or-token", "*"))
                type = XQueryElementType.ANY_MAP_TEST
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            mapTestMarker.done(type)
            return true
        }
        return false
    }

    private fun parseArrayTest(): Boolean {
        val arrayTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY)
        if (arrayTestMarker != null) {
            var haveError = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                arrayTestMarker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (matchTokenType(XPathTokenType.STAR)) {
                type = XQueryElementType.ANY_ARRAY_TEST
            } else if (parseSequenceType()) {
                type = XQueryElementType.TYPED_ARRAY_TEST
            } else {
                error(XQueryBundle.message("parser.error.expected-either", "*", "SequenceType"))
                type = XQueryElementType.ANY_ARRAY_TEST
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveError) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            arrayTestMarker.done(type)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    private fun parseKindTest(): Boolean {
        return parseDocumentTest()
                || parseElementTest()
                || parseAttributeTest()
                || parseSchemaElementTest()
                || parseSchemaAttributeTest()
                || parsePITest()
                || parseCommentTest()
                || parseTextTest()
                || parseNamespaceNodeTest()
                || parseAnyKindTest()
                || parseBinaryTest()
                || parseSchemaKindTest() != ParseStatus.NOT_MATCHED
                || parseJsonKindTest() != ParseStatus.NOT_MATCHED
    }

    private fun parseAnyKindTest(): Boolean {
        val anyKindTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NODE)
        if (anyKindTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                anyKindTestMarker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) { // MarkLogic 8.0
                type = XQueryElementType.NAMED_KIND_TEST
            } else {
                matchTokenType(XPathTokenType.STAR) // MarkLogic 8.0
                type = XQueryElementType.ANY_KIND_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            anyKindTestMarker.done(type)
            return true
        }
        return false
    }

    private fun parseDocumentTest(): Boolean {
        val documentTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT_NODE)
        if (documentTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                documentTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseElementTest() ||
                parseSchemaElementTest() ||
                parseAnyArrayNodeTest() != ParseStatus.NOT_MATCHED ||
                parseAnyMapNodeTest() != ParseStatus.NOT_MATCHED
            ) {
                //
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            documentTestMarker.done(XQueryElementType.DOCUMENT_TEST)
            return true
        }
        return false
    }

    private fun parseTextTest(): Boolean {
        val textTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TEXT)
        if (textTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                textTestMarker.rollbackTo()
                return false
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_TEXT_TEST
            } else {
                type = XQueryElementType.ANY_TEXT_TEST
                errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")")) // MarkLogic 8.0
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            textTestMarker.done(type)
            return true
        }
        return false
    }

    private fun parseCommentTest(): Boolean {
        val commentTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMMENT)
        if (commentTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                commentTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            commentTestMarker.done(XQueryElementType.COMMENT_TEST)
            return true
        }
        return false
    }

    private fun parseNamespaceNodeTest(): Boolean {
        val namespaceTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE_NODE)
        if (namespaceTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                namespaceTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            namespaceTestMarker.done(XQueryElementType.NAMESPACE_NODE_TEST)
            return true
        }
        return false
    }

    private fun parsePITest(): Boolean {
        val piTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION)
        if (piTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                piTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseQName(XQueryElementType.NCNAME) || parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            piTestMarker.done(XQueryElementType.PI_TEST)
            return true
        }
        return false
    }

    private fun parseAttributeTest(): Boolean {
        val attributeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE)
        if (attributeTestMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                attributeTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseAttribNameOrWildcard()) {
                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-eqname"))
                        haveErrors = true
                    }
                } else if (getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE && getTokenType() !== XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    parseEQName(XQueryElementType.TYPE_NAME)
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            attributeTestMarker.done(XQueryElementType.ATTRIBUTE_TEST)
            return true
        }
        return false
    }

    private fun parseAttribNameOrWildcard(): Boolean {
        return matchTokenType(XPathTokenType.STAR) || parseEQName(XQueryElementType.ATTRIBUTE_NAME)
    }

    private fun parseSchemaAttributeTest(): Boolean {
        val schemaAttributeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ATTRIBUTE)
        if (schemaAttributeTestMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaAttributeTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.ATTRIBUTE_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            schemaAttributeTestMarker.done(XQueryElementType.SCHEMA_ATTRIBUTE_TEST)
            return true
        }
        return false
    }

    private fun parseElementTest(): Boolean {
        val elementTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT)
        if (elementTestMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                elementTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (parseElementNameOrWildcard()) {
                parseWhiteSpaceAndCommentTokens()
                if (matchTokenType(XPathTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens()
                    if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-eqname"))
                        haveErrors = true
                    }

                    parseWhiteSpaceAndCommentTokens()
                    matchTokenType(XQueryTokenType.OPTIONAL)
                } else if (getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE && getTokenType() !== XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","))
                    haveErrors = true
                    parseEQName(XQueryElementType.TYPE_NAME)
                }
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            elementTestMarker.done(XQueryElementType.ELEMENT_TEST)
            return true
        }
        return false
    }

    private fun parseElementNameOrWildcard(): Boolean {
        return matchTokenType(XPathTokenType.STAR) || parseEQName(XQueryElementType.ELEMENT_NAME)
    }

    private fun parseSchemaElementTest(): Boolean {
        val schemaElementTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ELEMENT)
        if (schemaElementTestMarker != null) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaElementTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!parseEQName(XQueryElementType.ELEMENT_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"))
                haveErrors = true
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            schemaElementTestMarker.done(XQueryElementType.SCHEMA_ELEMENT_TEST)
            return true
        }
        return false
    }

    private fun parseBinaryTest(): Boolean {
        val binaryTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BINARY)
        if (binaryTestMarker != null) {
            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                binaryTestMarker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
            }

            binaryTestMarker.done(XQueryElementType.BINARY_TEST)
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest :: SchemaKindTest

    private fun parseSchemaKindTest(): ParseStatus {
        var status = parseAttributeDeclTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseComplexTypeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseElementDeclTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaComponentTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaFacetTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaParticleTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaRootTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSchemaTypeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseSimpleTypeTest()
        return status
    }

    private fun parseAttributeDeclTest(): ParseStatus {
        val attributeDeclTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE_DECL)
        if (attributeDeclTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                attributeDeclTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            attributeDeclTestMarker.done(XQueryElementType.ATTRIBUTE_DECL_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseComplexTypeTest(): ParseStatus {
        val attributeDeclTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMPLEX_TYPE)
        if (attributeDeclTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                attributeDeclTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            attributeDeclTestMarker.done(XQueryElementType.COMPLEX_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseElementDeclTest(): ParseStatus {
        val elementDeclTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT_DECL)
        if (elementDeclTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                elementDeclTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            elementDeclTestMarker.done(XQueryElementType.ELEMENT_DECL_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaComponentTest(): ParseStatus {
        val schemaComponentTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_COMPONENT)
        if (schemaComponentTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaComponentTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            schemaComponentTestMarker.done(XQueryElementType.SCHEMA_COMPONENT_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaFacetTest(): ParseStatus {
        val schemaFacetTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_FACET)
        if (schemaFacetTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaFacetTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            schemaFacetTestMarker.done(XQueryElementType.SCHEMA_FACET_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaParticleTest(): ParseStatus {
        val schemaParticleTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_PARTICLE)
        if (schemaParticleTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaParticleTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            schemaParticleTestMarker.done(XQueryElementType.SCHEMA_PARTICLE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaRootTest(): ParseStatus {
        val schemaRootTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ROOT)
        if (schemaRootTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaRootTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            schemaRootTestMarker.done(XQueryElementType.SCHEMA_ROOT_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSchemaTypeTest(): ParseStatus {
        val schemaTypeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_TYPE)
        if (schemaTypeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                schemaTypeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            schemaTypeTestMarker.done(XQueryElementType.SCHEMA_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseSimpleTypeTest(): ParseStatus {
        val simpleTypeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SIMPLE_TYPE)
        if (simpleTypeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                simpleTypeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            simpleTypeTestMarker.done(XQueryElementType.SIMPLE_TYPE_TEST)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest :: JsonKindTest

    private fun parseJsonKindTest(): ParseStatus {
        var status = parseArrayNodeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseBooleanNodeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseNullNodeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseNumberNodeTest()
        if (status == ParseStatus.NOT_MATCHED) status = parseMapNodeTest()
        return status
    }

    private fun parseAnyArrayNodeTest(): ParseStatus {
        return parseArrayNodeTest(true)
    }

    private fun parseArrayNodeTest(isAnyOnly: Boolean = false): ParseStatus {
        val arrayNodeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY_NODE)
        if (arrayNodeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                arrayNodeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (isAnyOnly && getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS

                // array-node() tests in a document-node test do not allow `StringLiteral` or `*`
                // tokens, but accept them here to recover when used incorrectly.
                parseStringLiteral(XQueryElementType.STRING_LITERAL)
                matchTokenType(XPathTokenType.STAR)
            } else if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_ARRAY_NODE_TEST
            } else if (errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))) {
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_ARRAY_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            arrayNodeTestMarker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseBooleanNodeTest(): ParseStatus {
        val booleanNodeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BOOLEAN_NODE)
        if (booleanNodeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                booleanNodeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_BOOLEAN_NODE_TEST
            } else if (getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))
                type = XQueryElementType.ANY_BOOLEAN_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_BOOLEAN_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            booleanNodeTestMarker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseAnyMapNodeTest(): ParseStatus {
        return parseMapNodeTest(true)
    }

    private fun parseMapNodeTest(isAnyOnly: Boolean = false): ParseStatus {
        val mapNodeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_OBJECT_NODE)
        if (mapNodeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                mapNodeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (isAnyOnly && getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                type = XQueryElementType.ANY_MAP_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS

                // object-node() tests in a document-node test do not allow `StringLiteral` or `*`
                // tokens, but accept them here to recover when used incorrectly.
                parseStringLiteral(XQueryElementType.STRING_LITERAL)
                matchTokenType(XPathTokenType.STAR)
            } else if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_MAP_NODE_TEST
            } else if (errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))) {
                type = XQueryElementType.ANY_MAP_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_MAP_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            mapNodeTestMarker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseNullNodeTest(): ParseStatus {
        val nullNodeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NULL_NODE)
        if (nullNodeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                nullNodeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_NULL_NODE_TEST
            } else if (getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))
                type = XQueryElementType.ANY_NULL_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_NULL_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            nullNodeTestMarker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    private fun parseNumberNodeTest(): ParseStatus {
        val numberNodeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NUMBER_NODE)
        if (numberNodeTestMarker != null) {
            var status = ParseStatus.MATCHED

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                numberNodeTestMarker.rollbackTo()
                return ParseStatus.NOT_MATCHED
            }

            val type: IElementType
            parseWhiteSpaceAndCommentTokens()
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                type = XQueryElementType.NAMED_NUMBER_NODE_TEST
            } else if (getTokenType() !== XPathTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XPathTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))
                type = XQueryElementType.ANY_NUMBER_NODE_TEST
                status = ParseStatus.MATCHED_WITH_ERRORS
            } else {
                type = XQueryElementType.ANY_NUMBER_NODE_TEST
            }

            parseWhiteSpaceAndCommentTokens()
            if (!matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"))
                status = ParseStatus.MATCHED_WITH_ERRORS
            }

            numberNodeTestMarker.done(type)
            return status
        }
        return ParseStatus.NOT_MATCHED
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols

    private fun parseStringLiteral(type: IElementType): Boolean {
        val stringMarker = matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        while (stringMarker != null) {
            if (
                matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                matchTokenType(XPathTokenType.ESCAPED_CHARACTER)
            ) {
                //
            } else if (matchTokenType(XPathTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type)
                return true
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) || matchTokenType(XPathTokenType.BAD_CHARACTER)) {
                //
            } else {
                stringMarker.done(type)
                error(XQueryBundle.message("parser.error.incomplete-string"))
                return true
            }
        }
        return false
    }

    private fun parseEQName(type: IElementType, endQNameOnSpace: Boolean = false): Boolean {
        val eqnameMarker = mark()
        if (parseQName(type, endQNameOnSpace) || parseURIQualifiedName(type)) {
            if (type === XQueryElementType.QNAME ||
                    type === XQueryElementType.NCNAME ||
                    type === XQueryElementType.WILDCARD) {
                eqnameMarker.drop()
            } else {
                eqnameMarker.done(type)
            }
            return true
        }
        eqnameMarker.drop()
        return false
    }

    private fun parseQName(type: IElementType, endQNameOnSpace: Boolean = false): Boolean {
        val qnameMarker = mark()
        var isWildcard = getTokenType() === XPathTokenType.STAR
        if (getTokenType() is INCNameType || isWildcard) {
            // region QNameOrWildcardPrefix := (NCName | "*")

            if (isWildcard) {
                if (type !== XQueryElementType.WILDCARD) {
                    error(XQueryBundle.message("parser.error.unexpected-wildcard"))
                }
                parseWildcardIndicator()
            } else {
                advanceLexer()
            }

            // endregion
            // region QNameWhitespaceBeforeSeparator := (S | Comment)* -- error: whitespace not allowed before ':'

            val beforeMarker = mark()
            if (parseWhiteSpaceAndCommentTokens()) {
                if (endQNameOnSpace) {
                    beforeMarker.drop()
                    if (type === XQueryElementType.WILDCARD) {
                        if (isWildcard) {
                            qnameMarker.done(XQueryElementType.WILDCARD)
                        } else {
                            qnameMarker.done(XQueryElementType.NCNAME)
                        }
                    } else {
                        qnameMarker.done(XQueryElementType.NCNAME)
                    }
                    return true
                } else if (
                    getTokenType() === XPathTokenType.QNAME_SEPARATOR ||
                    getTokenType() === XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR ||
                    getTokenType() === XQueryTokenType.XML_TAG_QNAME_SEPARATOR
                ) {
                    beforeMarker.error(XQueryBundle.message(if (isWildcard) "parser.error.wildcard.whitespace-before-local-part" else "parser.error.qname.whitespace-before-local-part"))
                } else {
                    beforeMarker.drop()
                }
            } else {
                beforeMarker.drop()
            }

            // endregion

            if (
                getTokenType() === XPathTokenType.QNAME_SEPARATOR ||
                getTokenType() === XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR ||
                getTokenType() === XQueryTokenType.XML_TAG_QNAME_SEPARATOR
            ) {
                // region QNameSeparator := ":"

                val nameMarker = mark()
                if (type === XQueryElementType.NCNAME || type === XQueryElementType.PREFIX) {
                    val errorMarker = mark()
                    advanceLexer()
                    errorMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"))
                } else {
                    advanceLexer()
                }

                // endregion
                // region QNameWhitespaceAfterSeparator := (S | Comment)* -- error: whitespace not allowed after ':'

                val afterMarker = mark()
                if (parseWhiteSpaceAndCommentTokens()) {
                    if (endQNameOnSpace) {
                        nameMarker.rollbackTo()
                        if (type === XQueryElementType.WILDCARD) {
                            if (isWildcard) {
                                qnameMarker.done(XQueryElementType.WILDCARD)
                            } else {
                                qnameMarker.done(XQueryElementType.NCNAME)
                            }
                        } else {
                            qnameMarker.done(XQueryElementType.NCNAME)
                        }
                        return true
                    } else {
                        afterMarker.error(XQueryBundle.message(if (isWildcard) "parser.error.wildcard.whitespace-after-local-part" else "parser.error.qname.whitespace-after-local-part"))
                    }
                } else {
                    afterMarker.drop()
                }
                nameMarker.drop()

                // endregion
                // region QNameOrWildcardLocalName := (NCName | "*")

                if (getTokenType() is INCNameType) {
                    advanceLexer()
                } else if (getTokenType() === XPathTokenType.STAR) {
                    if (type === XQueryElementType.WILDCARD) {
                        if (isWildcard) {
                            error(XQueryBundle.message("parser.error.wildcard.both-prefix-and-local-wildcard"))
                        }
                    } else {
                        error(XQueryBundle.message("parser.error.qname.wildcard-local-name"))
                    }
                    parseWildcardIndicator()
                    isWildcard = true
                } else if (getTokenType() === XPathTokenType.INTEGER_LITERAL) {
                    // The user has started the local name with a number, so treat it as part of the QName.
                    val errorMarker = mark()
                    advanceLexer()
                    errorMarker.error(XQueryBundle.message("parser.error.qname.missing-local-name"))
                } else {
                    // Don't consume the next token with an error, as it may be a valid part of the next construct
                    // (e.g. the start of a string literal, or the '>' of a direct element constructor).
                    error(XQueryBundle.message("parser.error.qname.missing-local-name"))
                }

                // endregion

                if (type === XQueryElementType.WILDCARD) {
                    qnameMarker.done(if (isWildcard) XQueryElementType.WILDCARD else XQueryElementType.QNAME)
                } else {
                    qnameMarker.done(XQueryElementType.QNAME)
                }
                return true
            } else {
                if (type === XQueryElementType.WILDCARD) {
                    if (isWildcard) {
                        qnameMarker.done(XQueryElementType.WILDCARD)
                    } else {
                        qnameMarker.done(XQueryElementType.NCNAME)
                    }
                } else {
                    qnameMarker.done(XQueryElementType.NCNAME)
                }
            }
            return true
        }

        if (
            matchTokenType(XPathTokenType.QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
        ) {
            parseWhiteSpaceAndCommentTokens()
            if (getTokenType() is INCNameType || getTokenType() === XPathTokenType.STAR) {
                advanceLexer()
            }
            if (type === XQueryElementType.NCNAME) {
                qnameMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"))
            } else {
                qnameMarker.error(XQueryBundle.message("parser.error.qname.missing-prefix"))
            }
            return true
        }

        qnameMarker.drop()
        return false
    }

    private fun parseNCName(type: IElementType): Boolean {
        if (getTokenType() is INCNameType) {
            val ncnameMarker = mark()
            advanceLexer()
            ncnameMarker.done(type)
            return true
        }
        return false
    }

    private fun parseWildcardIndicator(): Boolean {
        if (getTokenType() === XPathTokenType.STAR) {
            val marker = mark()
            advanceLexer()
            marker.done(XQueryElementType.WILDCARD_INDICATOR)
            return true
        }
        return false
    }

    private fun parseBracedURILiteral(): Boolean {
        val stringMarker = matchTokenTypeWithMarker(XPathTokenType.BRACED_URI_LITERAL_START)
        while (stringMarker != null) {
            if (
                matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE)
            ) {
                //
            } else if (matchTokenType(XPathTokenType.BRACED_URI_LITERAL_END)) {
                stringMarker.done(XQueryElementType.BRACED_URI_LITERAL)
                return true
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"))
            } else if (errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) || matchTokenType(XPathTokenType.BAD_CHARACTER)) {
                //
            } else {
                stringMarker.done(XQueryElementType.BRACED_URI_LITERAL)
                error(XQueryBundle.message("parser.error.incomplete-braced-uri-literal"))
                return true
            }
        }
        return false
    }

    private fun parseURIQualifiedName(type: IElementType): Boolean {
        val qnameMarker = mark()
        if (parseBracedURILiteral()) {
            if (getTokenType() is INCNameType) {
                advanceLexer()
            } else if (getTokenType() === XPathTokenType.STAR) {
                if (type === XQueryElementType.WILDCARD) {
                    parseWildcardIndicator()
                    qnameMarker.done(XQueryElementType.WILDCARD)
                    return true
                }
                error(XQueryBundle.message("parser.error.eqname.wildcard-local-name"))
                parseWildcardIndicator()
            } else {
                error(XQueryBundle.message("parser.error.expected-ncname"))
            }
            qnameMarker.done(XQueryElementType.URI_QUALIFIED_NAME)
            return true
        }
        qnameMarker.drop()
        return false
    }

    private fun parseWhiteSpaceAndCommentTokens(): Boolean {
        var skipped = false
        while (true) {
            if (getTokenType() === XPathTokenType.WHITE_SPACE || getTokenType() === XQueryTokenType.XML_WHITE_SPACE) {
                skipped = true
                advanceLexer()
            } else if (getTokenType() === XPathTokenType.COMMENT_START_TAG) {
                skipped = true
                val commentMarker = mark()
                advanceLexer()
                // NOTE: XQueryTokenType.COMMENT is omitted by the PsiBuilder.
                if (getTokenType() === XPathTokenType.COMMENT_END_TAG) {
                    advanceLexer()
                    commentMarker.done(XQueryElementType.COMMENT)
                } else {
                    advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                    commentMarker.done(XQueryElementType.COMMENT)
                    error(XQueryBundle.message("parser.error.incomplete-comment"))
                }
            } else if (getTokenType() === XPathTokenType.COMMENT_END_TAG) {
                skipped = true
                val errorMarker = mark()
                advanceLexer()
                errorMarker.error(XQueryBundle.message("parser.error.end-of-comment-without-start", "(:"))
            } else if (errorOnTokenType(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING, XQueryBundle.message("parser.error.misplaced-entity"))) {
                skipped = true
            } else {
                return skipped
            }
        }
    }

    // endregion
}
