/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.INCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

/**
 * A unified XQuery parser for different XQuery dialects.
 *
 * This parser supports:
 *    -  XQuery 1.0
 *    -  XQuery 3.0 (Partial Support)
 *    -  Update Facility 1.0
 *    -  MarkLogic 1.0-ml Extensions for MarkLogic 6.0
 *    -  MarkLogic 1.0-ml Extensions for MarkLogic 8.0
 */
@SuppressWarnings({"SameParameterValue", "StatementWithEmptyBody"})
class XQueryParser {
    // region Main Interface

    private final PsiBuilder mBuilder;

    public XQueryParser(@NotNull PsiBuilder builder) {
        mBuilder = builder;
    }

    public void parse() {
        boolean matched = false;
        boolean haveError = false;
        while (getTokenType() != null) {
            if (parseWhiteSpaceAndCommentTokens()) continue;
            if (matched && !haveError) {
                error(XQueryBundle.message("parser.error.expected-eof"));
                haveError = true;
            }

            if (parseTransactions()) {
                matched = true;
                continue;
            }

            if (haveError) {
                advanceLexer();
            } else {
                final PsiBuilder.Marker errorMarker = mark();
                advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.unexpected-token"));
                haveError = true;
            }
        }
    }

    // endregion
    // region Parser Helper Methods

    private enum ParseStatus {
        MATCHED,
        MATCHED_WITH_ERRORS,
        NOT_MATCHED,
    };

    private boolean matchTokenType(IElementType type) {
        if (mBuilder.getTokenType() == type) {
            mBuilder.advanceLexer();
            return true;
        }
        return false;
    }

    private PsiBuilder.Marker matchTokenTypeWithMarker(IElementType type) {
        if (mBuilder.getTokenType() == type) {
            final PsiBuilder.Marker marker = mark();
            mBuilder.advanceLexer();
            return marker;
        }
        return null;
    }

    private PsiBuilder.Marker matchTokenTypeWithMarker(IElementType type1, IElementType type2) {
        if (mBuilder.getTokenType() == type1 || mBuilder.getTokenType() == type2) {
            final PsiBuilder.Marker marker = mark();
            mBuilder.advanceLexer();
            return marker;
        }
        return null;
    }

    private boolean errorOnTokenType(IElementType type, String message) {
        if (mBuilder.getTokenType() == type) {
            final PsiBuilder.Marker errorMarker = mark();
            mBuilder.advanceLexer();
            errorMarker.error(message);
            return true;
        }
        return false;
    }

    private PsiBuilder.Marker mark() {
        return mBuilder.mark();
    }

    private IElementType getTokenType() {
        return mBuilder.getTokenType();
    }

    private void advanceLexer() {
        mBuilder.advanceLexer();
    }

    private void error(String message) {
        mBuilder.error(message);
    }

    // endregion
    // region Grammar :: Modules

    private boolean parseTransactions() {
        if (parseModule()) {
            parseWhiteSpaceAndCommentTokens();
            while (parseTransactionSeparator()) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseModule()) { // NOTE: Handles error cases for VersionDecl-only and library modules.
                    error(XQueryBundle.message("parser.error.expected", "MainModule"));
                }
                parseWhiteSpaceAndCommentTokens();
            }
            return true;
        }
        return false;
    }

    private boolean parseTransactionSeparator() {
        final PsiBuilder.Marker transactionSeparatorMarker = matchTokenTypeWithMarker(XQueryTokenType.SEPARATOR);
        if (transactionSeparatorMarker != null) {
            transactionSeparatorMarker.done(XQueryElementType.TRANSACTION_SEPARATOR);
            return true;
        }
        return false;
    }

    private boolean parseModule() {
        final PsiBuilder.Marker moduleMarker = mark();
        IElementType type = null;
        if (parseVersionDecl()) {
            type = XQueryElementType.MODULE;
            parseWhiteSpaceAndCommentTokens();
        }

        if (parseLibraryModule()) {
            type = XQueryElementType.LIBRARY_MODULE;
        } else if (parseMainModule()) {
            type = XQueryElementType.MAIN_MODULE;
        } else if (type != null) {
            error(XQueryBundle.message("parser.error.expected-module-type"));
        }

        if (type != null) {
            moduleMarker.done(type);
            return true;
        }
        moduleMarker.drop();
        return false;
    }

    private boolean parseVersionDecl() {
        final PsiBuilder.Marker versionDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_XQUERY);
        if (versionDeclMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected-encoding-string"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
            } else {
                if (!matchTokenType(XQueryTokenType.K_VERSION)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "version"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-version-string"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-encoding-string"));
                        haveErrors = true;
                    }

                    parseWhiteSpaceAndCommentTokens();
                }
            }

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                versionDeclMarker.done(XQueryElementType.VERSION_DECL);
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ";"));
                }
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
                return true;
            }

            versionDeclMarker.done(XQueryElementType.VERSION_DECL);
            return true;
        }
        return false;
    }

    private boolean parseMainModule() {
        if (parseProlog(false)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.QUERY_BODY)) {
                error(XQueryBundle.message("parser.error.expected-query-body"));
            }
            return true;
        }
        return parseExpr(XQueryElementType.QUERY_BODY);
    }

    private boolean parseLibraryModule() {
        if (parseModuleDecl()) {
            parseWhiteSpaceAndCommentTokens();
            parseProlog(true);
            return true;
        }
        return false;
    }

    private boolean parseModuleDecl() {
        final PsiBuilder.Marker moduleDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_MODULE);
        if (moduleDeclMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                moduleDeclMarker.done(XQueryElementType.MODULE_DECL);
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ";"));
                }
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
                return true;
            }

            moduleDeclMarker.done(XQueryElementType.MODULE_DECL);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Prolog

    private enum PrologDeclState {
        HEADER_STATEMENT,
        BODY_STATEMENT,
        UNKNOWN_STATEMENT,
        NOT_MATCHED
    };

    private boolean parseProlog(boolean parseInvalidConstructs) {
        final PsiBuilder.Marker prologMarker = mark();

        PrologDeclState state = PrologDeclState.NOT_MATCHED;
        while (true) {
            PrologDeclState nextState = parseDecl(state == PrologDeclState.NOT_MATCHED ? PrologDeclState.HEADER_STATEMENT : state);
            if (nextState == PrologDeclState.NOT_MATCHED) {
                nextState = parseImport(state == PrologDeclState.NOT_MATCHED ? PrologDeclState.HEADER_STATEMENT : state);
            }

            switch (nextState) {
                case NOT_MATCHED:
                    if (parseInvalidConstructs && getTokenType() != null) {
                        if (parseWhiteSpaceAndCommentTokens()) continue;
                        if (parseExprSingle()) continue;
                        advanceLexer();
                        continue;
                    } else {
                        if (state == PrologDeclState.NOT_MATCHED) {
                            prologMarker.drop();
                            return false;
                        }
                        prologMarker.done(XQueryElementType.PROLOG);
                        return true;
                    }
                case HEADER_STATEMENT:
                case UNKNOWN_STATEMENT:
                    if (state == PrologDeclState.NOT_MATCHED) {
                        state = PrologDeclState.HEADER_STATEMENT;
                    }
                    break;
                case BODY_STATEMENT:
                    if (state != PrologDeclState.BODY_STATEMENT) {
                        state = PrologDeclState.BODY_STATEMENT;
                    }
                    break;
            }

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                error(XQueryBundle.message("parser.error.expected", ";"));
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
            }
            parseWhiteSpaceAndCommentTokens();
        }
    }

    private PrologDeclState parseDecl(PrologDeclState state) {
        final PsiBuilder.Marker declMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE);
        if (declMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (parseBaseURIDecl(state)) {
                declMarker.done(XQueryElementType.BASE_URI_DECL);
            } else if (parseBoundarySpaceDecl(state)) {
                declMarker.done(XQueryElementType.BOUNDARY_SPACE_DECL);
            } else if (parseConstructionDecl(state)) {
                declMarker.done(XQueryElementType.CONSTRUCTION_DECL);
            } else if (parseCopyNamespacesDecl(state)) {
                declMarker.done(XQueryElementType.COPY_NAMESPACES_DECL);
            } else if (parseDecimalFormatDecl(state, false)) {
                declMarker.done(XQueryElementType.DECIMAL_FORMAT_DECL);
            } else if (parseDefaultDecl(declMarker, state)) {
            } else if (parseNamespaceDecl(state)) {
                declMarker.done(XQueryElementType.NAMESPACE_DECL);
            } else if (parseOptionDecl()) {
                declMarker.done(XQueryElementType.OPTION_DECL);
                return PrologDeclState.BODY_STATEMENT;
            } else if (parseOrderingModeDecl(state)) {
                declMarker.done(XQueryElementType.ORDERING_MODE_DECL);
            } else if (parseRevalidationDecl(state)) {
                declMarker.done(XQueryElementType.REVALIDATION_DECL);
            } else if (parseAnnotatedDecl()) {
                declMarker.done(XQueryElementType.ANNOTATED_DECL);
                return PrologDeclState.BODY_STATEMENT;
            } else if (parseContextItemDecl()) {
                declMarker.done(XQueryElementType.CONTEXT_ITEM_DECL);
                return PrologDeclState.BODY_STATEMENT;
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "base-uri, boundary-space, construction, context, copy-namespaces, decimal-format, default, function, namespace, option, ordering, revalidation, variable"));
                parseUnknownDecl();
                declMarker.done(XQueryElementType.UNKNOWN_DECL);
                return PrologDeclState.UNKNOWN_STATEMENT;
            }
            return PrologDeclState.HEADER_STATEMENT;
        }
        return PrologDeclState.NOT_MATCHED;
    }

    private boolean parseDefaultDecl(PsiBuilder.Marker defaultDeclMarker, PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseDefaultNamespaceDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_NAMESPACE_DECL);
            } else if (parseEmptyOrderDecl()) {
                defaultDeclMarker.done(XQueryElementType.EMPTY_ORDER_DECL);
            } else if (parseDefaultCollationDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_COLLATION_DECL);
            } else if (parseDecimalFormatDecl(state, true)) {
                defaultDeclMarker.done(XQueryElementType.DECIMAL_FORMAT_DECL);
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "collation, element, function, order"));
                parseUnknownDecl();
                defaultDeclMarker.done(XQueryElementType.UNKNOWN_DECL);
            }
            return true;
        }
        return false;
    }

    private boolean parseUnknownDecl() {
        while (true) {
            if (parseWhiteSpaceAndCommentTokens()) continue;
            if (matchTokenType(XQueryTokenType.NCNAME)) continue;
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) continue;

            if (matchTokenType(XQueryTokenType.EQUAL)) continue;
            if (matchTokenType(XQueryTokenType.COMMA)) continue;
            if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) continue;
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) continue;
            if (matchTokenType(XQueryTokenType.QNAME_SEPARATOR)) continue;
            if (matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) continue;
            if (matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) continue;

            if (matchTokenType(XQueryTokenType.K_COLLATION)) continue;
            if (matchTokenType(XQueryTokenType.K_ELEMENT)) continue;
            if (matchTokenType(XQueryTokenType.K_EMPTY)) continue;
            if (matchTokenType(XQueryTokenType.K_EXTERNAL)) continue;
            if (matchTokenType(XQueryTokenType.K_FUNCTION)) continue;
            if (matchTokenType(XQueryTokenType.K_GREATEST)) continue;
            if (matchTokenType(XQueryTokenType.K_INHERIT)) continue;
            if (matchTokenType(XQueryTokenType.K_ITEM)) continue;
            if (matchTokenType(XQueryTokenType.K_LAX)) continue;
            if (matchTokenType(XQueryTokenType.K_LEAST)) continue;
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) continue;
            if (matchTokenType(XQueryTokenType.K_NO_INHERIT)) continue;
            if (matchTokenType(XQueryTokenType.K_NO_PRESERVE)) continue;
            if (matchTokenType(XQueryTokenType.K_ORDER)) continue;
            if (matchTokenType(XQueryTokenType.K_ORDERED)) continue;
            if (matchTokenType(XQueryTokenType.K_PRESERVE)) continue;
            if (matchTokenType(XQueryTokenType.K_SKIP)) continue;
            if (matchTokenType(XQueryTokenType.K_STRICT)) continue;
            if (matchTokenType(XQueryTokenType.K_STRIP)) continue;
            if (matchTokenType(XQueryTokenType.K_UNORDERED)) continue;

            if (parseDFPropertyName()) continue;
            if (parseExprSingle()) continue;
            return true;
        }
    }

    private boolean parseDefaultNamespaceDecl() {
        if (matchTokenType(XQueryTokenType.K_ELEMENT) || matchTokenType(XQueryTokenType.K_FUNCTION)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseNamespaceDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Setter

    private boolean parseBoundarySpaceDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BOUNDARY_SPACE);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseDefaultCollationDecl() {
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseBaseURIDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BASE_URI);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseConstructionDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CONSTRUCTION);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseOrderingModeDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ORDERING);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ORDERED) && !matchTokenType(XQueryTokenType.K_UNORDERED)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "ordered, unordered"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseEmptyOrderDecl() {
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_EMPTY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "empty"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseRevalidationDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_REVALIDATION);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_STRICT) && !matchTokenType(XQueryTokenType.K_LAX) && !matchTokenType(XQueryTokenType.K_SKIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "lax, skip, strict"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseCopyNamespacesDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COPY_NAMESPACES);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_NO_PRESERVE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, no-preserve"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.COMMA) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ","));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_INHERIT) && !matchTokenType(XQueryTokenType.K_NO_INHERIT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "inherit, no-inherit"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseDecimalFormatDecl(PrologDeclState state, boolean isDefault) {
        final PsiBuilder.Marker errorMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECIMAL_FORMAT);
        if (errorMarker != null) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            boolean haveErrors = false;
            if (!isDefault) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.EQNAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"));
                    haveErrors = true;
                }
            }

            while (parseDFPropertyName()) {
                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "="));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-property-value-string"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseDFPropertyName() {
        final PsiBuilder.Marker dfPropertyNameMarker = mark();
        if (matchTokenType(XQueryTokenType.K_DECIMAL_SEPARATOR) ||
                matchTokenType(XQueryTokenType.K_GROUPING_SEPARATOR) ||
                matchTokenType(XQueryTokenType.K_INFINITY) ||
                matchTokenType(XQueryTokenType.K_MINUS_SIGN) ||
                matchTokenType(XQueryTokenType.K_NAN) ||
                matchTokenType(XQueryTokenType.K_PERCENT) ||
                matchTokenType(XQueryTokenType.K_PER_MILLE) ||
                matchTokenType(XQueryTokenType.K_ZERO_DIGIT) ||
                matchTokenType(XQueryTokenType.K_DIGIT) ||
                matchTokenType(XQueryTokenType.K_PATTERN_SEPARATOR)) {

            dfPropertyNameMarker.done(XQueryElementType.DF_PROPERTY_NAME);
            return true;
        }
        dfPropertyNameMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Prolog :: Header :: Import

    private PrologDeclState parseImport(PrologDeclState state) {
        final PsiBuilder.Marker importMarker = mark();
        final PsiBuilder.Marker errorMarker = mark();
        if (matchTokenType(XQueryTokenType.K_IMPORT)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseSchemaImport()) {
                importMarker.done(XQueryElementType.SCHEMA_IMPORT);
            } else if (parseStylesheetImport()) {
                importMarker.done(XQueryElementType.STYLESHEET_IMPORT);
            } else if (parseModuleImport()) {
                importMarker.done(XQueryElementType.MODULE_IMPORT);
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "schema, stylesheet, module"));
                importMarker.done(XQueryElementType.IMPORT);
                return PrologDeclState.UNKNOWN_STATEMENT;
            }
            return PrologDeclState.HEADER_STATEMENT;
        }

        errorMarker.drop();
        importMarker.drop();
        return PrologDeclState.NOT_MATCHED;
    }

    private boolean parseSchemaImport() {
        if (getTokenType() == XQueryTokenType.K_SCHEMA) {
            advanceLexer();

            parseWhiteSpaceAndCommentTokens();
            boolean haveErrors = parseSchemaPrefix();

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"));
                        haveErrors = true;
                    }
                    parseWhiteSpaceAndCommentTokens();
                } while (matchTokenType(XQueryTokenType.COMMA));
            }
            return true;
        }
        return false;
    }

    private boolean parseSchemaPrefix() {
        boolean haveErrors = false;
        final PsiBuilder.Marker schemaPrefixMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE);
        if (schemaPrefixMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            schemaPrefixMarker.done(XQueryElementType.SCHEMA_PREFIX);
            return haveErrors;
        }

        final PsiBuilder.Marker schemaPrefixDefaultMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT);
        if (schemaPrefixDefaultMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ELEMENT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "element"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            schemaPrefixDefaultMarker.done(XQueryElementType.SCHEMA_PREFIX);
        }
        return haveErrors;
    }

    private boolean parseStylesheetImport() {
        if (getTokenType() == XQueryTokenType.K_STYLESHEET) {
            boolean haveErrors = false;
            advanceLexer();

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_AT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "at"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseModuleImport() {
        if (getTokenType() == XQueryTokenType.K_MODULE) {
            boolean haveErrors = false;
            advanceLexer();

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.NCNAME)) {
                    error(XQueryBundle.message("parser.error.expected-ncname"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "="));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
            }

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"));
                        haveErrors = true;
                    }
                    parseWhiteSpaceAndCommentTokens();
                } while (matchTokenType(XQueryTokenType.COMMA));
            }
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Prolog :: Body

    private boolean parseContextItemDecl() {
        if (matchTokenType(XQueryTokenType.K_CONTEXT)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ITEM)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "item"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseItemType()) {
                    error(XQueryBundle.message("parser.error.expected", "ItemType"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle(XQueryElementType.VAR_VALUE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }
            } else if (matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseExprSingle(XQueryElementType.VAR_DEFAULT_VALUE) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"));
                    }
                }
            } else {
                error(XQueryBundle.message("parser.error.expected-variable-value"));
                parseExprSingle(XQueryElementType.VAR_VALUE);
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseAnnotatedDecl() {
        boolean haveAnnotations = false;
        while (parseAnnotation() || parseCompatibilityAnnotationDecl()) {
            parseWhiteSpaceAndCommentTokens();
            haveAnnotations = true;
        }

        final PsiBuilder.Marker declMarker = mark();
        if (parseVarDecl()) {
            declMarker.done(XQueryElementType.VAR_DECL);
            return true;
        } else if (parseFunctionDecl(declMarker)) {
            return true;
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected-keyword", "function, variable"));
            parseUnknownDecl();
            declMarker.done(XQueryElementType.UNKNOWN_DECL);
            return true;
        }
        declMarker.drop();
        return false;
    }

    private boolean parseAnnotation() {
        final PsiBuilder.Marker annotationMarker = matchTokenTypeWithMarker(XQueryTokenType.ANNOTATION_INDICATOR);
        if (annotationMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                do {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseLiteral() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "Literal"));
                        haveErrors = true;
                    }
                    parseWhiteSpaceAndCommentTokens();
                } while (matchTokenType(XQueryTokenType.COMMA));

                if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ")"));
                }
            }

            annotationMarker.done(XQueryElementType.ANNOTATION);
            return true;
        }
        return false;
    }

    private boolean parseCompatibilityAnnotationDecl() {
        final PsiBuilder.Marker compatibilityAnnotationMarker = mark();
        if (matchTokenType(XQueryTokenType.K_UPDATING)) {
            compatibilityAnnotationMarker.done(XQueryElementType.COMPATIBILITY_ANNOTATION);
            return true;
        } else if (matchTokenType(XQueryTokenType.K_PRIVATE)) {
            compatibilityAnnotationMarker.done(XQueryElementType.COMPATIBILITY_ANNOTATION_MARKLOGIC);
            return true;
        }
        compatibilityAnnotationMarker.drop();
        return false;
    }

    private boolean parseVarDecl() {
        if (matchTokenType(XQueryTokenType.K_VARIABLE)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle(XQueryElementType.VAR_VALUE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }
            } else if (matchTokenType(XQueryTokenType.K_EXTERNAL)) {
                parseWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseExprSingle(XQueryElementType.VAR_DEFAULT_VALUE) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"));
                    }
                }
            } else {
                error(XQueryBundle.message("parser.error.expected-variable-value"));
                parseExprSingle(XQueryElementType.VAR_VALUE);
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseFunctionDecl(PsiBuilder.Marker functionDeclMarker) {
        if (getTokenType() == XQueryTokenType.K_FUNCTION) {
            boolean haveErrors = false;

            if (getTokenType() == XQueryTokenType.K_FUNCTION) {
                advanceLexer();
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "function"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
                // DefaultNamespaceDecl with missing 'default' keyword.
                error(XQueryBundle.message("parser.error.expected", "("));
                parseStringLiteral(XQueryElementType.STRING_LITERAL);
                parseWhiteSpaceAndCommentTokens();
                functionDeclMarker.done(XQueryElementType.UNKNOWN_DECL);
                return true;
            } else if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseParamList();

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType()) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_EXTERNAL) && !parseEnclosedExpr(XQueryElementType.FUNCTION_BODY) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-enclosed-expression-or-keyword", "external"));
                parseExpr(XQueryElementType.EXPR);

                parseWhiteSpaceAndCommentTokens();
                matchTokenType(XQueryTokenType.BLOCK_CLOSE);
            }

            parseWhiteSpaceAndCommentTokens();
            functionDeclMarker.done(XQueryElementType.FUNCTION_DECL);
            return true;
        }

        return false;
    }

    private boolean parseParamList() {
        final PsiBuilder.Marker paramListMarker = mark();

        while (parseParam()) {
            parseWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.VARIABLE_INDICATOR) {
                error(XQueryBundle.message("parser.error.expected", ","));
            } else if (!matchTokenType(XQueryTokenType.COMMA)) {
                paramListMarker.done(XQueryElementType.PARAM_LIST);
                return true;
            }

            parseWhiteSpaceAndCommentTokens();
        }

        paramListMarker.drop();
        return false;
    }

    private boolean parseParam() {
        final PsiBuilder.Marker paramMarker = mark();
        if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
            }

            parseWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            paramMarker.done(XQueryElementType.PARAM);
            return true;
        } else if (getTokenType() == XQueryTokenType.NCNAME || getTokenType() instanceof IXQueryKeywordOrNCNameType || getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
            error(XQueryBundle.message("parser.error.expected", "$"));
            parseEQName(XQueryElementType.QNAME);

            parseWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            paramMarker.done(XQueryElementType.PARAM);
            return true;
        }

        paramMarker.drop();
        return false;
    }

    private boolean parseOptionDecl() {
        if (matchTokenType(XQueryTokenType.K_OPTION)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-option-string"));
            }

            parseWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr

    private boolean parseEnclosedExpr(IElementType type) {
        final PsiBuilder.Marker enclosedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.BLOCK_OPEN);
        if (enclosedExprMarker != null) {
            boolean haveErrors = false;
            parseWhiteSpaceAndCommentTokens();

            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            enclosedExprMarker.done(type);
            return true;
        }
        return false;
    }

    private boolean parseExpr(IElementType type) {
        final PsiBuilder.Marker exprMarker = mark();
        if (parseExprSingle()) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
            }
            exprMarker.done(type);
            return true;
        }
        exprMarker.drop();
        return false;
    }

    private boolean parseExprSingle() {
        return parseExprSingleImpl(null);
    }

    private boolean parseExprSingle(IElementType type) {
        return parseExprSingle(type, null);
    }

    private boolean parseExprSingle(IElementType type, IElementType parentType) {
        if (type == null) {
            return parseExprSingleImpl(parentType);
        }

        final PsiBuilder.Marker exprSingleMarker = mark();
        if (parseExprSingleImpl(parentType)) {
            exprSingleMarker.done(type);
            return true;
        }

        exprSingleMarker.drop();
        return false;
    }

    private boolean parseExprSingleImpl(IElementType parentType) {
        return parseFLWORExpr()
            || parseQuantifiedExpr()
            || parseSwitchExpr()
            || parseTypeswitchExpr()
            || parseIfExpr()
            || parseTryCatchExpr()
            || parseInsertExpr()
            || parseDeleteExpr()
            || parseRenameExpr()
            || parseReplaceExpr()
            || parseTransformExpr()
            || parseOrExpr(parentType);
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr

    private boolean parseFLWORExpr() {
        final PsiBuilder.Marker flworExprMarker = mark();
        if (parseInitialClause()) {
            while (parseIntermediateClause()) {
                //
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseReturnClause()) {
                error(XQueryBundle.message("parser.error.expected-keyword", "for, let, order, return, stable, where"));
                parseWhiteSpaceAndCommentTokens();
                parseExprSingle();
            }

            flworExprMarker.done(XQueryElementType.FLWOR_EXPR);
            return true;
        } else if (errorOnTokenType(XQueryTokenType.K_RETURN, XQueryBundle.message("parser.error.return-without-flwor"))) {
            parseWhiteSpaceAndCommentTokens();
            if (getTokenType() != XQueryTokenType.PARENTHESIS_OPEN && parseExprSingle()) {
                flworExprMarker.drop();
                return true;
            } else {
                flworExprMarker.rollbackTo();
                return false;
            }
        }
        flworExprMarker.drop();
        return false;
    }

    private boolean parseInitialClause() {
        return parseForClause() || parseLetClause();
    }

    private boolean parseIntermediateClause() {
        final PsiBuilder.Marker intermediateClauseMarker = mark();
        if (parseInitialClause() || parseWhereClause() || parseOrderByClause() || parseCountClause() || parseGroupByClause()) {
            intermediateClauseMarker.done(XQueryElementType.INTERMEDIATE_CLAUSE);
            return true;
        }
        intermediateClauseMarker.drop();
        return false;
    }

    private boolean parseReturnClause() {
        final PsiBuilder.Marker returnClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_RETURN)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            returnClauseMarker.done(XQueryElementType.RETURN_CLAUSE);
            return true;
        }
        returnClauseMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: ForClause

    private boolean parseForClause() {
        final PsiBuilder.Marker forClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_FOR)) {
            boolean isFirst = true;
            do {
                parseWhiteSpaceAndCommentTokens();
                if (!parseForBinding(isFirst) && isFirst) {
                    forClauseMarker.rollbackTo();
                    return false;
                }

                isFirst = false;
                parseWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            forClauseMarker.done(XQueryElementType.FOR_CLAUSE);
            return true;
        }
        forClauseMarker.drop();
        return false;
    }

    private boolean parseForBinding(boolean isFirst) {
        final PsiBuilder.Marker forBindingMarker = mark();

        boolean haveErrors = false;
        boolean matched = matchTokenType(XQueryTokenType.VARIABLE_INDICATOR);
        if (!matched) {
            error(XQueryBundle.message("parser.error.expected", "$"));
            haveErrors = true;
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            boolean haveTypeDeclaration = parseTypeDeclaration();

            parseWhiteSpaceAndCommentTokens();
            boolean haveAllowingEmpty = parseAllowingEmpty();

            parseWhiteSpaceAndCommentTokens();
            boolean havePositionalVar = parsePositionalVar();

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_IN) && !haveErrors) {
                if (havePositionalVar) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "in"));
                } else if (haveAllowingEmpty) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "at, in"));
                } else if (haveTypeDeclaration) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "allowing, at, in"));
                } else {
                    error(XQueryBundle.message("parser.error.expected-keyword", "allowing, as, at, in"));
                }
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            forBindingMarker.done(XQueryElementType.FOR_BINDING);
            return true;
        }
        forBindingMarker.drop();
        return false;
    }

    private boolean parseAllowingEmpty() {
        final PsiBuilder.Marker allowingEmptyMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ALLOWING);
        if (allowingEmptyMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_EMPTY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "empty"));
            }

            allowingEmptyMarker.done(XQueryElementType.ALLOWING_EMPTY);
            return true;
        }
        return false;
    }

    private boolean parsePositionalVar() {
        final PsiBuilder.Marker positionalVarMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AT);
        if (positionalVarMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
            }

            positionalVarMarker.done(XQueryElementType.POSITIONAL_VAR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: LetClause

    private boolean parseLetClause() {
        final PsiBuilder.Marker letClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_LET)) {
            boolean isFirst = true;
            do {
                parseWhiteSpaceAndCommentTokens();
                if (!parseLetBinding(isFirst) && isFirst) {
                    letClauseMarker.rollbackTo();
                    return false;
                }

                isFirst = false;
                parseWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            letClauseMarker.done(XQueryElementType.LET_CLAUSE);
            return true;
        }
        letClauseMarker.drop();
        return false;
    }

    private boolean parseLetBinding(boolean isFirst) {
        final PsiBuilder.Marker letBindingMarker = mark();

        boolean haveErrors = false;
        boolean matched = matchTokenType(XQueryTokenType.VARIABLE_INDICATOR);
        if (!matched) {
            error(XQueryBundle.message("parser.error.expected", "$"));
            haveErrors = true;
        }

        if (matched || !isFirst) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            boolean haveTypeDeclaration = parseTypeDeclaration();

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                if (haveTypeDeclaration) {
                    error(XQueryBundle.message("parser.error.expected", ":="));
                } else {
                    error(XQueryBundle.message("parser.error.expected-variable-assign-or-keyword", "as"));
                }
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            letBindingMarker.done(XQueryElementType.LET_BINDING);
            return true;
        }
        letBindingMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: CountClause

    private boolean parseCountClause() {
        final PsiBuilder.Marker countClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COUNT);
        if (countClauseMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-qname"));
            }

            countClauseMarker.done(XQueryElementType.COUNT_CLAUSE);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: WhereClause

    private boolean parseWhereClause() {
        final PsiBuilder.Marker whereClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WHERE);
        if (whereClauseMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            whereClauseMarker.done(XQueryElementType.WHERE_CLAUSE);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: GroupByClause

    private boolean parseGroupByClause() {
        final PsiBuilder.Marker groupByClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_GROUP);
        if (groupByClauseMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_BY)) {
                error(XQueryBundle.message("parser.error.expected", "by"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseGroupingSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "GroupingSpecList"));
            }

            groupByClauseMarker.done(XQueryElementType.GROUP_BY_CLAUSE);
            return true;
        }
        return false;
    }

    private boolean parseGroupingSpecList() {
        final PsiBuilder.Marker groupingSpecListMarker = mark();
        if (parseGroupingSpec()) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseGroupingSpec()) {
                    error(XQueryBundle.message("parser.error.expected", "GroupingSpec"));
                }

                parseWhiteSpaceAndCommentTokens();
            }

            groupingSpecListMarker.done(XQueryElementType.GROUPING_SPEC_LIST);
            return true;
        }
        groupingSpecListMarker.drop();
        return false;
    }

    private boolean parseGroupingSpec() {
        final PsiBuilder.Marker groupingSpecListMarker = mark();
        if (parseGroupingVariable()) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (parseTypeDeclaration()) {
                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                    error(XQueryBundle.message("parser.error.expected", ":="));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }
            } else if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle()) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }
            }

            if (matchTokenType(XQueryTokenType.K_COLLATION)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }
            }

            groupingSpecListMarker.done(XQueryElementType.GROUPING_SPEC);
            return true;
        }
        groupingSpecListMarker.drop();
        return false;
    }

    private boolean parseGroupingVariable() {
        final PsiBuilder.Marker groupingVariableMarker = matchTokenTypeWithMarker(XQueryTokenType.VARIABLE_INDICATOR);
        if (groupingVariableMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
            }

            groupingVariableMarker.done(XQueryElementType.GROUPING_VARIABLE);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr :: OrderByClause

    private boolean parseOrderByClause() {
        final PsiBuilder.Marker orderByClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_BY)) {
                error(XQueryBundle.message("parser.error.expected", "by"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseOrderSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "OrderSpecList"));
            }

            orderByClauseMarker.done(XQueryElementType.ORDER_BY_CLAUSE);
            return true;
        } else if (matchTokenType(XQueryTokenType.K_STABLE)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ORDER)) {
                error(XQueryBundle.message("parser.error.expected", "order"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_BY) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "by"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseOrderSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "OrderSpecList"));
            }

            orderByClauseMarker.done(XQueryElementType.ORDER_BY_CLAUSE);
            return true;
        }
        orderByClauseMarker.drop();
        return false;
    }

    private boolean parseOrderSpecList() {
        final PsiBuilder.Marker orderSpecListMarker = mark();
        if (parseOrderSpec()) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseOrderSpec()) {
                    error(XQueryBundle.message("parser.error.expected", "OrderSpec"));
                }

                parseWhiteSpaceAndCommentTokens();
            }

            orderSpecListMarker.done(XQueryElementType.ORDER_SPEC_LIST);
            return true;
        }
        orderSpecListMarker.drop();
        return false;
    }

    private boolean parseOrderSpec() {
        final PsiBuilder.Marker orderSpecMarker = mark();
        if (parseExprSingle()) {
            parseWhiteSpaceAndCommentTokens();
            parseOrderModifier();

            orderSpecMarker.done(XQueryElementType.ORDER_SPEC);
            return true;
        }
        orderSpecMarker.drop();
        return false;
    }

    private boolean parseOrderModifier() {
        final PsiBuilder.Marker orderModifierMarker = mark();

        if (matchTokenType(XQueryTokenType.K_ASCENDING) || matchTokenType(XQueryTokenType.K_DESCENDING)) {
            //
        }

        parseWhiteSpaceAndCommentTokens();
        if (matchTokenType(XQueryTokenType.K_EMPTY)) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"));
            }
        }

        parseWhiteSpaceAndCommentTokens();
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }
        }

        orderModifierMarker.done(XQueryElementType.ORDER_MODIFIER);
        return false;
    }

    // endregion
    // region Grammar :: Expr :: QuantifiedExpr

    private boolean parseQuantifiedExpr() {
        final PsiBuilder.Marker quantifiedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SOME, XQueryTokenType.K_EVERY);
        if (quantifiedExprMarker != null) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        quantifiedExprMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-eqname"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                boolean haveTypeDeclaration = parseTypeDeclaration();

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_IN) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", haveTypeDeclaration ? "in" : "as, in"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                parseWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_SATISFIES) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "satisfies"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            quantifiedExprMarker.done(XQueryElementType.QUANTIFIED_EXPR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: SwitchExpr

    private boolean parseSwitchExpr() {
        final PsiBuilder.Marker switchExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SWITCH);
        if (switchExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                switchExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            boolean matched = false;
            while (parseSwitchCaseClause()) {
                matched = true;
                parseWhiteSpaceAndCommentTokens();
            }
            if (!matched) {
                error(XQueryBundle.message("parser.error.expected", "SwitchCaseClause"));
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "case, default"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            switchExprMarker.done(XQueryElementType.SWITCH_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseSwitchCaseClause() {
        final PsiBuilder.Marker switchCaseClauseMarker = mark();

        boolean haveErrors = false;
        boolean haveCase = false;
        while (matchTokenType(XQueryTokenType.K_CASE)) {
            haveCase = true;
            parseWhiteSpaceAndCommentTokens();
            if (!parseSwitchCaseOperand()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }
            parseWhiteSpaceAndCommentTokens();
        }

        if (haveCase) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            switchCaseClauseMarker.done(XQueryElementType.SWITCH_CASE_CLAUSE);
            return true;
        }

        switchCaseClauseMarker.drop();
        return false;
    }

    private boolean parseSwitchCaseOperand() {
        final PsiBuilder.Marker switchCaseOperandMarker = mark();
        if (parseExprSingle()) {
            switchCaseOperandMarker.done(XQueryElementType.SWITCH_CASE_OPERAND);
            return true;
        }
        switchCaseOperandMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: TypeswitchExpr

    private boolean parseTypeswitchExpr() {
        final PsiBuilder.Marker typeswitchExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TYPESWITCH);
        if (typeswitchExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                typeswitchExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            boolean matched = false;
            while (parseCaseClause()) {
                matched = true;
                parseWhiteSpaceAndCommentTokens();
            }
            if (!matched) {
                error(XQueryBundle.message("parser.error.expected", "CaseClause"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "case, default"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-eqname"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-variable-reference-or-keyword", "return"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            typeswitchExprMarker.done(XQueryElementType.TYPESWITCH_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseCaseClause() {
        final PsiBuilder.Marker caseClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CASE);
        if (caseClauseMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.VAR_NAME)) {
                    error(XQueryBundle.message("parser.error.expected-eqname"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseSequenceTypeUnion() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            caseClauseMarker.done(XQueryElementType.CASE_CLAUSE);
            return true;
        }
        return false;
    }

    private boolean parseSequenceTypeUnion() {
        final PsiBuilder.Marker sequenceTypeOrUnionMarker = mark();
        if (parseSequenceType()) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                    haveErrors = true;
                }
                parseWhiteSpaceAndCommentTokens();
            }

            sequenceTypeOrUnionMarker.done(XQueryElementType.SEQUENCE_TYPE_UNION);
            return true;
        }
        sequenceTypeOrUnionMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    private boolean parseIfExpr() {
        final PsiBuilder.Marker ifExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_IF);
        if (ifExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                ifExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_THEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "then"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ELSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "else"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            ifExprMarker.done(XQueryElementType.IF_EXPR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: TryCatchExpr

    private enum CatchClauseType {
        NONE,
        XQUERY_30,
        MARK_LOGIC
    };

    private boolean parseTryCatchExpr() {
        final PsiBuilder.Marker tryExprMarker = mark();
        if (parseTryClause()) {
            CatchClauseType type = CatchClauseType.NONE;

            parseWhiteSpaceAndCommentTokens();
            while (true) {
                CatchClauseType nextType = parseCatchClause(type);
                if (nextType == CatchClauseType.NONE) {
                    if (type == CatchClauseType.NONE) {
                        error(XQueryBundle.message("parser.error.expected", "CatchClause"));
                    }

                    tryExprMarker.done(XQueryElementType.TRY_CATCH_EXPR);
                    return true;
                } else if (type != CatchClauseType.MARK_LOGIC) {
                    type = nextType;
                }

                parseWhiteSpaceAndCommentTokens();
            }
        }
        tryExprMarker.drop();
        return false;
    }

    private boolean parseTryClause() {
        final PsiBuilder.Marker tryClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TRY);
        if (tryClauseMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                tryClauseMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.TRY_TARGET_EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            tryClauseMarker.done(XQueryElementType.TRY_CLAUSE);
            return true;
        }
        return false;
    }

    private CatchClauseType parseCatchClause(CatchClauseType type) {
        final PsiBuilder.Marker catchClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_CATCH);
        if (catchClauseMarker != null) {
            boolean haveErrors = false;
            CatchClauseType nextType = CatchClauseType.XQUERY_30;

            parseWhiteSpaceAndCommentTokens();
            if (parseCatchErrorList()) {
                //
            } else if (getTokenType() == XQueryTokenType.PARENTHESIS_OPEN) {
                if (type == CatchClauseType.MARK_LOGIC) {
                    error(XQueryBundle.message("parser.error.multiple-marklogic-catch-clause"));
                }
                advanceLexer();

                nextType = CatchClauseType.MARK_LOGIC;

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                    error(XQueryBundle.message("parser.error.expected", "$"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "VarName"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ")"));
                    haveErrors = true;
                }
            } else {
                error(XQueryBundle.message("parser.error.expected", "CatchErrorList"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR) && !haveErrors && nextType != CatchClauseType.MARK_LOGIC) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            catchClauseMarker.done(XQueryElementType.CATCH_CLAUSE);
            return nextType;
        }
        return CatchClauseType.NONE;
    }

    private boolean parseCatchErrorList() {
        final PsiBuilder.Marker catchErrorListMarker = mark();
        if (parseNameTest()) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseNameTest()) {
                    error(XQueryBundle.message("parser.error.expected", "NameTest"));
                }
                parseWhiteSpaceAndCommentTokens();
            }
            catchErrorListMarker.done(XQueryElementType.CATCH_ERROR_LIST);
            return true;
        }
        catchErrorListMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: InsertExpr

    private boolean parseInsertExpr() {
        final PsiBuilder.Marker insertExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_INSERT);
        if (insertExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                insertExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseSourceExpr()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseInsertExprTargetChoice() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "after, as, before, into"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            insertExprMarker.done(XQueryElementType.INSERT_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseSourceExpr() {
        final PsiBuilder.Marker sourceExprMarker = mark();
        if (parseExprSingle(null, XQueryElementType.SOURCE_EXPR)) {
            sourceExprMarker.done(XQueryElementType.SOURCE_EXPR);
            return true;
        }
        sourceExprMarker.drop();
        return false;
    }

    private boolean parseInsertExprTargetChoice() {
        final PsiBuilder.Marker insertExprTargetChoiceMarker = mark();
        if (matchTokenType(XQueryTokenType.K_AS)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_FIRST) && !matchTokenType(XQueryTokenType.K_LAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "first, last"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_INTO) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "into"));
            }

            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE);
            return true;
        } else if (matchTokenType(XQueryTokenType.K_INTO) ||
                   matchTokenType(XQueryTokenType.K_BEFORE) ||
                   matchTokenType(XQueryTokenType.K_AFTER)) {
            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE);
            return true;
        } else if (getTokenType() == XQueryTokenType.K_FIRST || getTokenType() == XQueryTokenType.K_LAST) {
            error(XQueryBundle.message("parser.error.expected-keyword", "as"));
            advanceLexer();

            parseWhiteSpaceAndCommentTokens();
            matchTokenType(XQueryTokenType.K_INTO);

            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE);
            return true;
        }

        insertExprTargetChoiceMarker.drop();
        return false;
    }

    private boolean parseTargetExpr(IElementType type) {
        final PsiBuilder.Marker targetExprMarker = mark();
        if (parseExprSingle(null, type)) {
            targetExprMarker.done(XQueryElementType.TARGET_EXPR);
            return true;
        }
        targetExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: DeleteExpr

    private boolean parseDeleteExpr() {
        final PsiBuilder.Marker deleteExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DELETE);
        if (deleteExprMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                deleteExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            deleteExprMarker.done(XQueryElementType.DELETE_EXPR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: ReplaceExpr

    private boolean parseReplaceExpr() {
        final PsiBuilder.Marker replaceExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_REPLACE);
        if (replaceExprMarker != null) {
            boolean haveErrors = false;
            boolean haveValueOf = false;

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_VALUE)) {
                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_OF)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"));
                    haveErrors = true;
                }
                haveValueOf = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                if (!haveValueOf) {
                    replaceExprMarker.rollbackTo();
                    return false;
                }
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "node"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_WITH) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "with"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            replaceExprMarker.done(XQueryElementType.REPLACE_EXPR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: RenameExpr

    private boolean parseRenameExpr() {
        final PsiBuilder.Marker renameExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_RENAME);
        if (renameExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                renameExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(XQueryElementType.TARGET_EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_AS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseNewNameExpr() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            renameExprMarker.done(XQueryElementType.RENAME_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseNewNameExpr() {
        final PsiBuilder.Marker newNameExprMarker = mark();
        if (parseExprSingle()) {
            newNameExprMarker.done(XQueryElementType.NEW_NAME_EXPR);
            return true;
        }
        newNameExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: TransformExpr

    private boolean parseTransformExpr() {
        final PsiBuilder.Marker transformExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COPY);
        if (transformExprMarker != null) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        transformExprMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-eqname"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ":="));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                parseWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_MODIFY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "modify"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            transformExprMarker.done(XQueryElementType.TRANSFORM_EXPR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private boolean parseOrExpr(IElementType type) {
        final PsiBuilder.Marker orExprMarker = mark();
        if (parseAndExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_OR)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseAndExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AndExpr"));
                }
            }

            orExprMarker.done(XQueryElementType.OR_EXPR);
            return true;
        }
        orExprMarker.drop();
        return false;
    }

    private boolean parseAndExpr(IElementType type) {
        final PsiBuilder.Marker andExprMarker = mark();
        if (parseComparisonExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_AND)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseComparisonExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "ComparisonExpr"));
                }
            }

            andExprMarker.done(XQueryElementType.AND_EXPR);
            return true;
        }
        andExprMarker.drop();
        return false;
    }

    private boolean parseComparisonExpr(IElementType type) {
        final PsiBuilder.Marker comparisonExprMarker = mark();
        if (parseStringConcatExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            if (parseGeneralComp() || parseValueComp() || parseNodeComp()) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseStringConcatExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "StringConcatExpr"));
                }
            }

            comparisonExprMarker.done(XQueryElementType.COMPARISON_EXPR);
            return true;
        }
        comparisonExprMarker.drop();
        return false;
    }

    private boolean parseStringConcatExpr(IElementType type) {
        final PsiBuilder.Marker stringConcatExprMarker = mark();
        if (parseRangeExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.CONCATENATION)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseRangeExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "RangeExpr"));
                }
                parseWhiteSpaceAndCommentTokens();
            }

            stringConcatExprMarker.done(XQueryElementType.STRING_CONCAT_EXPR);
            return true;
        }
        stringConcatExprMarker.drop();
        return false;
    }

    private boolean parseRangeExpr(IElementType type) {
        final PsiBuilder.Marker rangeExprMarker = mark();
        if (parseAdditiveExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_TO)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseAdditiveExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "AdditiveExpr"));
                }
            }

            rangeExprMarker.done(XQueryElementType.RANGE_EXPR);
            return true;
        }
        rangeExprMarker.drop();
        return false;
    }

    private boolean parseAdditiveExpr(IElementType type) {
        final PsiBuilder.Marker additiveExprMarker = mark();
        if (parseMultiplicativeExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.PLUS) || matchTokenType(XQueryTokenType.MINUS)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseMultiplicativeExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "MultiplicativeExpr"));
                }
            }

            additiveExprMarker.done(XQueryElementType.ADDITIVE_EXPR);
            return true;
        }
        additiveExprMarker.drop();
        return false;
    }

    private boolean parseMultiplicativeExpr(IElementType type) {
        final PsiBuilder.Marker multiplicativeExprMarker = mark();
        if (parseUnionExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.STAR) ||
                   matchTokenType(XQueryTokenType.K_DIV) ||
                   matchTokenType(XQueryTokenType.K_IDIV) ||
                   matchTokenType(XQueryTokenType.K_MOD)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseUnionExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "UnionExpr"));
                }
            }

            multiplicativeExprMarker.done(XQueryElementType.MULTIPLICATIVE_EXPR);
            return true;
        }
        multiplicativeExprMarker.drop();
        return false;
    }

    private boolean parseUnionExpr(IElementType type) {
        final PsiBuilder.Marker unionExprMarker = mark();
        if (parseIntersectExceptExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_UNION) || matchTokenType(XQueryTokenType.UNION)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseIntersectExceptExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "IntersectExceptExpr"));
                }
            }

            unionExprMarker.done(XQueryElementType.UNION_EXPR);
            return true;
        }
        unionExprMarker.drop();
        return false;
    }

    private boolean parseIntersectExceptExpr(IElementType type) {
        final PsiBuilder.Marker intersectExceptExprMarker = mark();
        if (parseInstanceofExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_INTERSECT) || matchTokenType(XQueryTokenType.K_EXCEPT)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseInstanceofExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "InstanceofExpr"));
                }
                parseWhiteSpaceAndCommentTokens();
            }

            intersectExceptExprMarker.done(XQueryElementType.INTERSECT_EXCEPT_EXPR);
            return true;
        }
        intersectExceptExprMarker.drop();
        return false;
    }

    private boolean parseInstanceofExpr(IElementType type) {
        final PsiBuilder.Marker instanceofExprMarker = mark();
        if (parseTreatExpr(type)) {
            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_INSTANCE)) {
                boolean haveErrors = false;

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_OF)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"));
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                }
            } else if (getTokenType() == XQueryTokenType.K_OF) {
                error(XQueryBundle.message("parser.error.expected-keyword", "instance"));
                advanceLexer();

                parseWhiteSpaceAndCommentTokens();
                parseSingleType();
            }

            instanceofExprMarker.done(XQueryElementType.INSTANCEOF_EXPR);
            return true;
        }
        instanceofExprMarker.drop();
        return false;
    }

    private boolean parseTreatExpr(IElementType type) {
        final PsiBuilder.Marker treatExprMarker = mark();
        if (parseCastableExpr()) {
            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_TREAT)) {
                boolean haveErrors = false;

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                }
            } else if (getTokenType() == XQueryTokenType.K_AS && type != XQueryElementType.SOURCE_EXPR && type != XQueryElementType.TARGET_EXPR) {
                error(XQueryBundle.message("parser.error.expected-keyword", "cast, castable, treat"));
                advanceLexer();

                parseWhiteSpaceAndCommentTokens();
                parseSingleType();
            }

            treatExprMarker.done(XQueryElementType.TREAT_EXPR);
            return true;
        }
        treatExprMarker.drop();
        return false;
    }

    private boolean parseCastableExpr() {
        final PsiBuilder.Marker castableExprMarker = mark();
        if (parseCastExpr()) {
            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_CASTABLE)) {
                boolean haveErrors = false;

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"));
                }
            }

            castableExprMarker.done(XQueryElementType.CASTABLE_EXPR);
            return true;
        }
        castableExprMarker.drop();
        return false;
    }

    private boolean parseCastExpr() {
        final PsiBuilder.Marker castExprMarker = mark();
        if (parseUnaryExpr()) {
            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_CAST)) {
                boolean haveErrors = false;

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"));
                }
            }

            castExprMarker.done(XQueryElementType.CAST_EXPR);
            return true;
        }
        castExprMarker.drop();
        return false;
    }

    private boolean parseUnaryExpr() {
        final PsiBuilder.Marker pathExprMarker = mark();
        boolean matched = false;
        while (matchTokenType(XQueryTokenType.PLUS) || matchTokenType(XQueryTokenType.MINUS)) {
            parseWhiteSpaceAndCommentTokens();
            matched = true;
        }
        if (parseValueExpr()) {
            pathExprMarker.done(XQueryElementType.UNARY_EXPR);
            return true;
        } else if (matched) {
            error(XQueryBundle.message("parser.error.expected", "ValueExpr"));
            pathExprMarker.done(XQueryElementType.UNARY_EXPR);
            return true;
        }
        pathExprMarker.drop();
        return false;
    }

    private boolean parseGeneralComp() {
        return matchTokenType(XQueryTokenType.EQUAL) ||
               matchTokenType(XQueryTokenType.NOT_EQUAL) ||
               matchTokenType(XQueryTokenType.LESS_THAN) ||
               matchTokenType(XQueryTokenType.LESS_THAN_OR_EQUAL) ||
               matchTokenType(XQueryTokenType.GREATER_THAN) ||
               matchTokenType(XQueryTokenType.GREATER_THAN_OR_EQUAL);
    }

    private boolean parseValueComp() {
        return matchTokenType(XQueryTokenType.K_EQ) ||
               matchTokenType(XQueryTokenType.K_NE) ||
               matchTokenType(XQueryTokenType.K_LT) ||
               matchTokenType(XQueryTokenType.K_LE) ||
               matchTokenType(XQueryTokenType.K_GT) ||
               matchTokenType(XQueryTokenType.K_GE);
    }

    private boolean parseNodeComp() {
        return matchTokenType(XQueryTokenType.K_IS) ||
               matchTokenType(XQueryTokenType.NODE_BEFORE) ||
               matchTokenType(XQueryTokenType.NODE_AFTER);
    }

    private boolean parseSingleType() {
        final PsiBuilder.Marker singleTypeMarker = mark();
        if (parseEQName(XQueryElementType.SIMPLE_TYPE_NAME)) {
            parseWhiteSpaceAndCommentTokens();
            matchTokenType(XQueryTokenType.OPTIONAL);

            singleTypeMarker.done(XQueryElementType.SINGLE_TYPE);
            return true;
        }
        singleTypeMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: ValueExpr

    private boolean parseValueExpr() {
        return parseExtensionExpr() || parseValidateExpr() || parseSimpleMapExpr();
    }

    private boolean parseValidateExpr() {
        final PsiBuilder.Marker validateExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_VALIDATE);
        if (validateExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            boolean haveValidationMode = false;
            if (matchTokenType(XQueryTokenType.K_LAX) || matchTokenType(XQueryTokenType.K_STRICT)) {
                haveValidationMode = true;
            } else if (matchTokenType(XQueryTokenType.K_AS) || matchTokenType(XQueryTokenType.K_TYPE)) {
                haveValidationMode = true;

                parseWhiteSpaceAndCommentTokens();
                if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                    error(XQueryBundle.message("parser.error.expected", "TypeName"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                if (haveValidationMode) {
                    error(XQueryBundle.message("parser.error.expected", "{"));
                    haveErrors = true;
                } else {
                    validateExprMarker.rollbackTo();
                    return false;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            validateExprMarker.done(XQueryElementType.VALIDATE_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseExtensionExpr() {
        final PsiBuilder.Marker extensionExprMarker = mark();
        boolean matched = false;
        while (parsePragma()) {
            matched = true;
            parseWhiteSpaceAndCommentTokens();
        }
        if (matched) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            extensionExprMarker.done(XQueryElementType.EXTENSION_EXPR);
            return true;
        }
        extensionExprMarker.drop();
        return false;
    }

    private boolean parsePragma() {
        final PsiBuilder.Marker pragmaMarker = matchTokenTypeWithMarker(XQueryTokenType.PRAGMA_BEGIN);
        if (pragmaMarker != null) {
            boolean haveErrors = false;

            matchTokenType(XQueryTokenType.WHITE_SPACE);
            if (!parseEQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.WHITE_SPACE);
            if (!matchTokenType(XQueryTokenType.PRAGMA_CONTENTS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-pragma-contents"));
                haveErrors = true;
            }

            if (!matchTokenType(XQueryTokenType.PRAGMA_END) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "#)"));
            }

            pragmaMarker.done(XQueryElementType.PRAGMA);
            return true;
        }
        return false;
    }

    private boolean parseSimpleMapExpr() {
        final PsiBuilder.Marker simpleMapExprMarker = mark();
        if (parsePathExpr()) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.MAP_OPERATOR)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parsePathExpr() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "PathExpr"));
                    haveErrors = true;
                }
                parseWhiteSpaceAndCommentTokens();
            }

            simpleMapExprMarker.done(XQueryElementType.SIMPLE_MAP_EXPR);
            return true;
        }
        simpleMapExprMarker.drop();
        return false;
    }

    private boolean parsePathExpr() {
        final PsiBuilder.Marker pathExprMarker = mark();
        if (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH)) {
            parseWhiteSpaceAndCommentTokens();
            parseRelativePathExpr();

            pathExprMarker.done(XQueryElementType.PATH_EXPR);
            return true;
        } else if (matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseRelativePathExpr()) {
                error(XQueryBundle.message("parser.error.expected", "RelativePathExpr"));
            }

            pathExprMarker.done(XQueryElementType.PATH_EXPR);
            return true;
        } else if (parseRelativePathExpr()) {
            pathExprMarker.done(XQueryElementType.PATH_EXPR);
            return true;
        }
        pathExprMarker.drop();
        return false;
    }

    private boolean parseRelativePathExpr() {
        final PsiBuilder.Marker relativePathExprMarker = mark();
        if (parseStepExpr()) {
            parseWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH) || matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseStepExpr()) {
                    error(XQueryBundle.message("parser.error.expected", "StepExpr"));
                }

                parseWhiteSpaceAndCommentTokens();
            }

            relativePathExprMarker.done(XQueryElementType.RELATIVE_PATH_EXPR);
            return true;
        }
        relativePathExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: StepExpr

    private boolean parseStepExpr() {
        return parseFilterExpr() || parseAxisStep();
    }

    private boolean parseAxisStep() {
        final PsiBuilder.Marker axisStepMarker = mark();
        if (parseReverseStep() || parseForwardStep()) {
            parseWhiteSpaceAndCommentTokens();
            parsePredicateList();

            axisStepMarker.done(XQueryElementType.AXIS_STEP);
            return true;
        }

        axisStepMarker.drop();
        return false;
    }

    private boolean parseForwardStep() {
        final PsiBuilder.Marker forwardStepMarker = mark();
        if (parseForwardAxis()) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseNodeTest()) {
                error(XQueryBundle.message("parser.error.expected", "NodeTest"));
            }

            forwardStepMarker.done(XQueryElementType.FORWARD_STEP);
            return true;
        } else if (parseAbbrevForwardStep()) {
            forwardStepMarker.done(XQueryElementType.FORWARD_STEP);
            return true;
        }

        forwardStepMarker.drop();
        return false;
    }

    private boolean parseForwardAxis() {
        final PsiBuilder.Marker forwardAxisMarker = mark();
        if (matchTokenType(XQueryTokenType.K_ATTRIBUTE) ||
            matchTokenType(XQueryTokenType.K_CHILD) ||
            matchTokenType(XQueryTokenType.K_DESCENDANT) ||
            matchTokenType(XQueryTokenType.K_DESCENDANT_OR_SELF) ||
            matchTokenType(XQueryTokenType.K_FOLLOWING) ||
            matchTokenType(XQueryTokenType.K_FOLLOWING_SIBLING) ||
            matchTokenType(XQueryTokenType.K_NAMESPACE) ||
            matchTokenType(XQueryTokenType.K_PROPERTY) ||
            matchTokenType(XQueryTokenType.K_SELF)) {

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.AXIS_SEPARATOR)) {
                forwardAxisMarker.rollbackTo();
                return false;
            }

            forwardAxisMarker.done(XQueryElementType.FORWARD_AXIS);
            return true;
        }
        forwardAxisMarker.drop();
        return false;
    }

    private boolean parseAbbrevForwardStep() {
        final PsiBuilder.Marker abbrevForwardStepMarker = mark();
        boolean matched = matchTokenType(XQueryTokenType.ATTRIBUTE_SELECTOR);

        parseWhiteSpaceAndCommentTokens();
        if (parseNodeTest()) {
            abbrevForwardStepMarker.done(XQueryElementType.ABBREV_FORWARD_STEP);
            return true;
        } else if (matched) {
            error(XQueryBundle.message("parser.error.expected", "NodeTest"));

            abbrevForwardStepMarker.done(XQueryElementType.ABBREV_FORWARD_STEP);
            return true;
        }
        abbrevForwardStepMarker.drop();
        return false;
    }

    private boolean parseReverseStep() {
        final PsiBuilder.Marker reverseStepMarker = mark();
        if (parseReverseAxis()) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseNodeTest()) {
                error(XQueryBundle.message("parser.error.expected", "NodeTest"));
            }

            reverseStepMarker.done(XQueryElementType.REVERSE_STEP);
            return true;
        } else if (parseAbbrevReverseStep()) {
            reverseStepMarker.done(XQueryElementType.REVERSE_STEP);
            return true;
        }

        reverseStepMarker.drop();
        return false;
    }

    private boolean parseReverseAxis() {
        final PsiBuilder.Marker reverseAxisMarker = mark();
        if (matchTokenType(XQueryTokenType.K_PARENT) ||
            matchTokenType(XQueryTokenType.K_ANCESTOR) ||
            matchTokenType(XQueryTokenType.K_ANCESTOR_OR_SELF) ||
            matchTokenType(XQueryTokenType.K_PRECEDING) ||
            matchTokenType(XQueryTokenType.K_PRECEDING_SIBLING)) {

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.AXIS_SEPARATOR)) {
                reverseAxisMarker.rollbackTo();
                return false;
            }

            reverseAxisMarker.done(XQueryElementType.REVERSE_AXIS);
            return true;
        }
        reverseAxisMarker.drop();
        return false;
    }

    private boolean parseAbbrevReverseStep() {
        final PsiBuilder.Marker abbrevReverseStepMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENT_SELECTOR);
        if (abbrevReverseStepMarker != null) {
            abbrevReverseStepMarker.done(XQueryElementType.ABBREV_REVERSE_STEP);
            return true;
        }
        return false;
    }

    private boolean parseNodeTest() {
        final PsiBuilder.Marker nodeTestMarker = mark();
        if (parseKindTest() || parseNameTest()) {
            nodeTestMarker.done(XQueryElementType.NODE_TEST);
            return true;
        }

        nodeTestMarker.drop();
        return false;
    }

    private boolean parseNameTest() {
        final PsiBuilder.Marker nameTestMarker = mark();
        if (parseEQName(XQueryElementType.WILDCARD)) { // QName | Wildcard
            nameTestMarker.done(XQueryElementType.NAME_TEST);
            return true;
        }

        nameTestMarker.drop();
        return false;
    }

    private boolean parseFilterExpr() {
        final PsiBuilder.Marker filterExprMarker = mark();
        if (parsePrimaryExpr()) {
            parseWhiteSpaceAndCommentTokens();
            while (parsePredicate() || parseArgumentList()) {
                parseWhiteSpaceAndCommentTokens();
            }

            filterExprMarker.done(XQueryElementType.FILTER_EXPR);
            return true;
        }
        filterExprMarker.drop();
        return false;
    }

    private boolean parsePredicateList() {
        final PsiBuilder.Marker predicateListMarker = mark();
        while (parsePredicate()) {
            parseWhiteSpaceAndCommentTokens();
        }
        predicateListMarker.done(XQueryElementType.PREDICATE_LIST);
        return true;
    }

    private boolean parsePredicate() {
        final PsiBuilder.Marker predicateMarker = matchTokenTypeWithMarker(XQueryTokenType.PREDICATE_BEGIN);
        if (predicateMarker != null) {
            boolean haveErrors = false;
            parseWhiteSpaceAndCommentTokens();

            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PREDICATE_END) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "]"));
            }

            predicateMarker.done(XQueryElementType.PREDICATE);
            return true;
        }

        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr

    private boolean parsePrimaryExpr() {
        return parseLiteral()
            || parseVarRef()
            || parseParenthesizedExpr()
            || parseContextItemExpr()
            || parseOrderedExpr()
            || parseUnorderedExpr()
            || parseConstructor()
            || parseFunctionItemExpr()
            || parseFunctionCall();
    }

    private boolean parseLiteral() {
        final PsiBuilder.Marker literalMarker = mark();
        if (parseNumericLiteral() || parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            literalMarker.done(XQueryElementType.LITERAL);
            return true;
        }
        literalMarker.drop();
        return false;
    }

    private boolean parseNumericLiteral() {
        if (matchTokenType(XQueryTokenType.INTEGER_LITERAL) ||
            matchTokenType(XQueryTokenType.DOUBLE_LITERAL)) {
            return true;
        } else if (matchTokenType(XQueryTokenType.DECIMAL_LITERAL)) {
            errorOnTokenType(XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT, XQueryBundle.message("parser.error.incomplete-double-exponent"));
            return true;
        }
        return false;
    }

    private boolean parseVarRef() {
        final PsiBuilder.Marker varRefMarker = matchTokenTypeWithMarker(XQueryTokenType.VARIABLE_INDICATOR);
        if (varRefMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-eqname"));
            }

            varRefMarker.done(XQueryElementType.VAR_REF);
            return true;
        }
        return false;
    }

    private boolean parseParenthesizedExpr() {
        final PsiBuilder.Marker parenthesizedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENTHESIS_OPEN);
        if (parenthesizedExprMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (parseExpr(XQueryElementType.EXPR)) {
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            parenthesizedExprMarker.done(XQueryElementType.PARENTHESIZED_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseContextItemExpr() {
        final PsiBuilder.Marker contextItemExprMarker = matchTokenTypeWithMarker(XQueryTokenType.DOT);
        if (contextItemExprMarker != null) {
            contextItemExprMarker.done(XQueryElementType.CONTEXT_ITEM_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseOrderedExpr() {
        final PsiBuilder.Marker orderedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ORDERED);
        if (orderedExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                orderedExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            orderedExprMarker.done(XQueryElementType.ORDERED_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseUnorderedExpr() {
        final PsiBuilder.Marker unorderedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_UNORDERED);
        if (unorderedExprMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                unorderedExprMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            unorderedExprMarker.done(XQueryElementType.UNORDERED_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseFunctionCall() {
        if (getTokenType() instanceof IXQueryKeywordOrNCNameType) {
            IXQueryKeywordOrNCNameType type = (IXQueryKeywordOrNCNameType)getTokenType();
            switch (type.getKeywordType()) {
                case KEYWORD:
                    break;
                case RESERVED_FUNCTION_NAME:
                case XQUERY30_RESERVED_FUNCTION_NAME:
                    return false;
                case MARKLOGIC_RESERVED_FUNCTION_NAME:
                    // Don't keep the MarkLogic JSON parseTree here as KindTest is not anchored to the correct parent
                    // at this point.
                    final PsiBuilder.Marker testMarker = mark();
                    ParseStatus status = parseJsonKindTest();
                    testMarker.rollbackTo();

                    // If this is a valid MarkLogic JSON KindTest, return false here to parse it as a KindTest.
                    if (status == ParseStatus.MATCHED) {
                        return false;
                    }

                    // Otherwise, fall through to the FunctionCall parser to parse it as a FunctionCall to allow
                    // standard XQuery to use these keywords as function names.
                    break;
            }
        }

        final PsiBuilder.Marker functionCallMarker = mark();
        if (parseEQName(XQueryElementType.QNAME)) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseArgumentList()) {
                functionCallMarker.rollbackTo();
                return false;
            }

            functionCallMarker.done(XQueryElementType.FUNCTION_CALL);
            return true;
        }

        functionCallMarker.drop();
        return false;
    }

    private boolean parseArgumentList() {
        final PsiBuilder.Marker argumentListMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENTHESIS_OPEN);
        if (argumentListMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (parseArgument()) {
                parseWhiteSpaceAndCommentTokens();
                while (matchTokenType(XQueryTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseArgument() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-either", "ExprSingle", "?"));
                        haveErrors = true;
                    }

                    parseWhiteSpaceAndCommentTokens();
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            argumentListMarker.done(XQueryElementType.ARGUMENT_LIST);
            return true;
        }
        return false;
    }

    private boolean parseArgument() {
        final PsiBuilder.Marker argumentMarker = mark();
        if (parseExprSingle() || parseArgumentPlaceholder()) {
            argumentMarker.done(XQueryElementType.ARGUMENT);
            return true;
        }
        argumentMarker.drop();
        return false;
    }

    private boolean parseArgumentPlaceholder() {
        final PsiBuilder.Marker argumentPlaceholderMarker = matchTokenTypeWithMarker(XQueryTokenType.OPTIONAL);
        if (argumentPlaceholderMarker != null) {
            argumentPlaceholderMarker.done(XQueryElementType.ARGUMENT_PLACEHOLDER);
            return true;
        }
        return false;
    }

    private boolean parseConstructor() {
        final PsiBuilder.Marker constructorMarker = mark();
        if (parseDirectConstructor() || parseComputedConstructor()) {
            constructorMarker.done(XQueryElementType.CONSTRUCTOR);
            return true;
        }

        constructorMarker.drop();
        return false;
    }

    private boolean parseFunctionItemExpr() {
        return parseNamedFunctionRef() || parseInlineFunctionExpr();
    }

    private boolean parseNamedFunctionRef() {
        final PsiBuilder.Marker namedFunctionRefMarker = mark();
        if (parseEQName(XQueryElementType.QNAME)) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.FUNCTION_REF_OPERATOR)) {
                namedFunctionRefMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.INTEGER_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected", "IntegerLiteral"));
            }

            namedFunctionRefMarker.done(XQueryElementType.NAMED_FUNCTION_REF);
            return true;
        }

        namedFunctionRefMarker.drop();
        return false;
    }

    private boolean parseInlineFunctionExpr() {
        final PsiBuilder.Marker inlineFunctionExprMarker = mark();

        boolean haveAnnotations = false;
        while (parseAnnotation()) {
            parseWhiteSpaceAndCommentTokens();
            haveAnnotations = true;
        }

        if (matchTokenType(XQueryTokenType.K_FUNCTION)) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                if (!haveAnnotations) {
                    inlineFunctionExprMarker.rollbackTo();
                    return false;
                }

                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseParamList();

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AS)) {
                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType()) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                    haveErrors = true;
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseEnclosedExpr(XQueryElementType.FUNCTION_BODY) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                parseExpr(XQueryElementType.EXPR);

                parseWhiteSpaceAndCommentTokens();
                matchTokenType(XQueryTokenType.BLOCK_CLOSE);
            }

            inlineFunctionExprMarker.done(XQueryElementType.INLINE_FUNCTION_EXPR);
            return true;
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected-keyword", "function"));

            inlineFunctionExprMarker.done(XQueryElementType.INLINE_FUNCTION_EXPR);
            return true;
        }

        inlineFunctionExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: DirectConstructor

    private boolean parseDirectConstructor() {
        return parseDirElemConstructor()
            || parseDirCommentConstructor()
            || parseDirPIConstructor()
            || parseCDataSection(null);
    }

    private boolean parseDirElemConstructor() {
        final PsiBuilder.Marker elementMarker = matchTokenTypeWithMarker(XQueryTokenType.OPEN_XML_TAG);
        if (elementMarker != null) {
            // NOTE: The XQueryLexer ensures that OPEN_XML_TAG is followed by an NCNAME/QNAME.
            parseQName(XQueryElementType.QNAME);

            parseDirAttributeList();

            if (matchTokenType(XQueryTokenType.SELF_CLOSING_XML_TAG)) {
                //
            } else if (matchTokenType(XQueryTokenType.END_XML_TAG)) {
                parseDirElemContent();

                if (matchTokenType(XQueryTokenType.CLOSE_XML_TAG)) {
                    // NOTE: The XQueryLexer ensures that CLOSE_XML_TAG is followed by an NCNAME/QNAME.
                    parseQName(XQueryElementType.QNAME);

                    matchTokenType(XQueryTokenType.XML_WHITE_SPACE);
                    if (!matchTokenType(XQueryTokenType.END_XML_TAG)) {
                        error(XQueryBundle.message("parser.error.expected", ">"));
                    }
                }
            } else {
                error(XQueryBundle.message("parser.error.incomplete-open-tag"));
            }

            elementMarker.done(XQueryElementType.DIR_ELEM_CONSTRUCTOR);
            return true;
        }

        return false;
    }

    private boolean parseDirAttributeList() {
        final PsiBuilder.Marker attributeListMarker = mark();
        boolean haveErrors = false;

        // NOTE: The XQuery grammar uses whitespace as the token to start the next iteration of the matching loop.
        // Because the parseQName function can consume that whitespace during error handling, the QName tokens are
        // used as the next iteration marker in this implementation.
        boolean parsed = matchTokenType(XQueryTokenType.XML_WHITE_SPACE);
        while (parseQName(XQueryElementType.QNAME)) {
            parsed = true;

            matchTokenType(XQueryTokenType.XML_WHITE_SPACE);
            if (!matchTokenType(XQueryTokenType.XML_EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.XML_WHITE_SPACE);
            if (!parseDirAttributeValue() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-attribute-string"));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.XML_WHITE_SPACE);
        }

        if (parsed) {
            attributeListMarker.done(XQueryElementType.DIR_ATTRIBUTE_LIST);
            return true;
        }

        attributeListMarker.drop();
        return false;
    }

    private boolean parseDirAttributeValue() {
        final PsiBuilder.Marker stringMarker = matchTokenTypeWithMarker(XQueryTokenType.XML_ATTRIBUTE_VALUE_START);
        while (stringMarker != null) {
            if (matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS) ||
                matchTokenType(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.XML_CHARACTER_REFERENCE) ||
                matchTokenType(XQueryTokenType.XML_ESCAPED_CHARACTER)) {
                //
            } else if (matchTokenType(XQueryTokenType.XML_ATTRIBUTE_VALUE_END)) {
                stringMarker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE);
                return true;
            } else if (matchTokenType(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"));
            } else if (errorOnTokenType(XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) ||
                       matchTokenType(XQueryTokenType.BAD_CHARACTER)) {
                //
            } else if (parseEnclosedExpr(XQueryElementType.ENCLOSED_EXPR) ||
                       errorOnTokenType(XQueryTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr"))) {
                //
            } else {
                stringMarker.done(XQueryElementType.DIR_ATTRIBUTE_VALUE);
                error(XQueryBundle.message("parser.error.incomplete-attribute-value"));
                return true;
            }
        }
        return false;
    }

    private boolean parseDirCommentConstructor() {
        final PsiBuilder.Marker commentMarker = matchTokenTypeWithMarker(XQueryTokenType.XML_COMMENT_START_TAG);
        if (commentMarker != null) {
            // NOTE: XQueryTokenType.XML_COMMENT is omitted by the PsiBuilder.
            if (matchTokenType(XQueryTokenType.XML_COMMENT_END_TAG)) {
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
                error(XQueryBundle.message("parser.error.incomplete-xml-comment"));
            }
            return true;
        }

        return errorOnTokenType(XQueryTokenType.XML_COMMENT_END_TAG, XQueryBundle.message("parser.error.end-of-comment-without-start", "<!--"));
    }

    private boolean parseDirPIConstructor() {
        final PsiBuilder.Marker piMarker = matchTokenTypeWithMarker(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        if (piMarker != null) {
            boolean haveErrors = false;

            if (matchTokenType(XQueryTokenType.WHITE_SPACE)) {
                error(XQueryBundle.message("parser.error.unexpected-whitespace"));
                haveErrors = true;
            }

            if (!parseQName(XQueryElementType.NCNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.WHITE_SPACE);
            if (!matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-pi-contents"));
                haveErrors = true;
            }

            while (matchTokenType(XQueryTokenType.BAD_CHARACTER) ||
                   matchTokenType(XQueryTokenType.NCNAME) ||
                   matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)) {
                //
            }

            if (!matchTokenType(XQueryTokenType.PROCESSING_INSTRUCTION_END) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "?>"));
            }

            piMarker.done(XQueryElementType.DIR_PI_CONSTRUCTOR);
            return true;
        }

        return false;
    }

    private boolean parseDirElemContent() {
        final PsiBuilder.Marker elemContentMarker = mark();
        boolean matched = false;
        while (true) {
            if (matchTokenType(XQueryTokenType.XML_ELEMENT_CONTENTS) ||
                matchTokenType(XQueryTokenType.BAD_CHARACTER) ||
                matchTokenType(XQueryTokenType.BLOCK_CLOSE) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                matchTokenType(XQueryTokenType.ESCAPED_CHARACTER) ||
                errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity"))) {
                matched = true;
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"));
                matched = true;
            } else if (parseEnclosedExpr(XQueryElementType.ENCLOSED_EXPR) ||
                       parseCDataSection(XQueryElementType.DIR_ELEM_CONTENT) ||
                       parseDirectConstructor()) {
                matched = true;
            } else {
                if (matched) {
                    elemContentMarker.done(XQueryElementType.DIR_ELEM_CONTENT);
                    return true;
                }

                elemContentMarker.drop();
                return false;
            }
        }
    }

    private boolean parseCDataSection(IElementType context) {
        final PsiBuilder.Marker cdataMarker = mark();
        final PsiBuilder.Marker errorMarker = mark();
        if (matchTokenType(XQueryTokenType.CDATA_SECTION_START_TAG)) {
            if (context == null) {
                errorMarker.error(XQueryBundle.message("parser.error.cdata-section-not-in-element-content"));
            } else {
                errorMarker.drop();
            }

            matchTokenType(XQueryTokenType.CDATA_SECTION);
            if (matchTokenType(XQueryTokenType.CDATA_SECTION_END_TAG)) {
                cdataMarker.done(XQueryElementType.CDATA_SECTION);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                cdataMarker.done(XQueryElementType.CDATA_SECTION);
                error(XQueryBundle.message("parser.error.incomplete-cdata-section"));
            }
            return true;
        }

        errorMarker.drop();
        cdataMarker.drop();
        return errorOnTokenType(XQueryTokenType.CDATA_SECTION_END_TAG, XQueryBundle.message("parser.error.end-of-cdata-section-without-start"));
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: ComputedConstructor

    private boolean parseComputedConstructor() {
        return parseCompDocConstructor()
            || parseCompElemConstructor()
            || parseCompAttrConstructor()
            || parseCompNamespaceConstructor()
            || parseCompTextConstructor()
            || parseCompCommentConstructor()
            || parseCompPIConstructor()
            || parseCompBinaryConstructor()
            || parseCompArrayConstructor()
            || parseCompBooleanConstructor()
            || parseCompNullConstructor()
            || parseCompNumberConstructor()
            || parseCompObjectConstructor();
    }

    private boolean parseCompDocConstructor() {
        final PsiBuilder.Marker documentMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT);
        if (documentMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                documentMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            documentMarker.done(XQueryElementType.COMP_DOC_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompElemConstructor() {
        final PsiBuilder.Marker elementMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT);
        if (elementMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    elementMarker.rollbackTo();
                    return false;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.CONTENT_EXPR);

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            elementMarker.done(XQueryElementType.COMP_ELEM_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompAttrConstructor() {
        final PsiBuilder.Marker attributeMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE);
        if (attributeMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseEQName(XQueryElementType.QNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    attributeMarker.rollbackTo();
                    return false;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            attributeMarker.done(XQueryElementType.COMP_ATTR_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompNamespaceConstructor() {
        final PsiBuilder.Marker namespaceMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE);
        if (namespaceMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.PREFIX)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    namespaceMarker.rollbackTo();
                    return false;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.PREFIX_EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.URI_EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            namespaceMarker.done(XQueryElementType.COMP_NAMESPACE_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompTextConstructor() {
        final PsiBuilder.Marker textMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TEXT);
        if (textMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                textMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            textMarker.done(XQueryElementType.COMP_TEXT_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompCommentConstructor() {
        final PsiBuilder.Marker commentMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMMENT);
        if (commentMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                commentMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            commentMarker.done(XQueryElementType.COMP_COMMENT_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompPIConstructor() {
        final PsiBuilder.Marker piMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION);
        if (piMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    piMarker.rollbackTo();
                    return false;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                parseWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            piMarker.done(XQueryElementType.COMP_PI_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompBinaryConstructor() {
        final PsiBuilder.Marker compBinaryConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_BINARY);
        if (compBinaryConstructor != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compBinaryConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compBinaryConstructor.done(XQueryElementType.COMP_BINARY_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompArrayConstructor() {
        final PsiBuilder.Marker compArrayConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY_NODE);
        if (compArrayConstructor != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compArrayConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compArrayConstructor.done(XQueryElementType.COMP_ARRAY_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompBooleanConstructor() {
        final PsiBuilder.Marker compBooleanConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_BOOLEAN_NODE);
        if (compBooleanConstructor != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compBooleanConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compBooleanConstructor.done(XQueryElementType.COMP_BOOLEAN_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompNullConstructor() {
        final PsiBuilder.Marker compNullConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_NULL_NODE);
        if (compNullConstructor != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compNullConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compNullConstructor.done(XQueryElementType.COMP_NULL_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompNumberConstructor() {
        final PsiBuilder.Marker compNumberConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_NUMBER_NODE);
        if (compNumberConstructor != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compNumberConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compNumberConstructor.done(XQueryElementType.COMP_NUMBER_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompObjectConstructor() {
        final PsiBuilder.Marker compObjectConstructor = matchTokenTypeWithMarker(XQueryTokenType.K_OBJECT_NODE);
        if (compObjectConstructor != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                compObjectConstructor.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseObjectKeyValue()) {
                parseWhiteSpaceAndCommentTokens();
                while (matchTokenType(XQueryTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseObjectKeyValue() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "ObjectKeyValue"));
                        haveErrors = true;
                    }
                    parseWhiteSpaceAndCommentTokens();
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            compObjectConstructor.done(XQueryElementType.COMP_OBJECT_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseObjectKeyValue() {
        final PsiBuilder.Marker objectKeyValue = mark();
        if (parseExprSingle()) {
            boolean haveError = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.QNAME_SEPARATOR)) {
                error(XQueryBundle.message("parser.error.expected", ":"));
                haveError = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveError) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            objectKeyValue.done(XQueryElementType.OBJECT_KEY_VALUE);
            return true;
        }
        objectKeyValue.drop();
        return false;
    }

    // endregion
    // region Grammar :: TypeDeclaration

    private boolean parseTypeDeclaration() {
        final PsiBuilder.Marker typeDeclarationMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AS);
        if (typeDeclarationMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!parseSequenceType()) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"));
            }

            typeDeclarationMarker.done(XQueryElementType.TYPE_DECLARATION);
            return true;
        }
        return false;
    }

    private boolean parseSequenceType() {
        final PsiBuilder.Marker sequenceTypeMarker = mark();
        if (matchTokenType(XQueryTokenType.K_EMPTY_SEQUENCE)) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                sequenceTypeMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE);
            return true;
        } else if (parseItemType()) {
            parseWhiteSpaceAndCommentTokens();
            parseOccurrenceIndicator();

            sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE);
            return true;
        }

        sequenceTypeMarker.drop();
        return false;
    }

    private boolean parseOccurrenceIndicator() {
        final PsiBuilder.Marker occurrenceIndicatorMarker = mark();
        if (matchTokenType(XQueryTokenType.OPTIONAL) || matchTokenType(XQueryTokenType.STAR) || matchTokenType(XQueryTokenType.PLUS)) {
            occurrenceIndicatorMarker.done(XQueryElementType.OCCURRENCE_INDICATOR);
            return true;
        }

        occurrenceIndicatorMarker.drop();
        return false;
    }

    private boolean parseItemType() {
        final PsiBuilder.Marker itemTypeMarker = mark();
        if (matchTokenType(XQueryTokenType.K_ITEM)) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                itemTypeMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            itemTypeMarker.done(XQueryElementType.ITEM_TYPE);
            return true;
        } else if (parseKindTest() || parseFunctionTest() || parseEQName(XQueryElementType.ATOMIC_OR_UNION_TYPE) || parseParenthesizedItemType()) {
            itemTypeMarker.done(XQueryElementType.ITEM_TYPE);
            return true;
        }

        itemTypeMarker.drop();
        return false;
    }

    private boolean parseFunctionTest() {
        final PsiBuilder.Marker functionTestMarker = mark();

        boolean haveAnnotations = false;
        while (parseAnnotation()) {
            parseWhiteSpaceAndCommentTokens();
            haveAnnotations = true;
        }

        if (parseAnyOrTypedFunctionTest()) {
            functionTestMarker.done(XQueryElementType.FUNCTION_TEST);
            return true;
        } else if (haveAnnotations) {
            error(XQueryBundle.message("parser.error.expected", "AnyFunctionTest"));

            functionTestMarker.done(XQueryElementType.FUNCTION_TEST);
            return true;
        }

        functionTestMarker.drop();
        return false;
    }

    private boolean parseAnyOrTypedFunctionTest() {
        final PsiBuilder.Marker functionTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_FUNCTION);
        if (functionTestMarker != null) {
            IElementType type = XQueryElementType.ANY_FUNCTION_TEST;
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                functionTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.STAR)) {
                //
            } else if (parseSequenceType()) {
                type = XQueryElementType.TYPED_FUNCTION_TEST;

                parseWhiteSpaceAndCommentTokens();
                while (matchTokenType(XQueryTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseSequenceType() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                        haveErrors = true;
                    }
                }
            } else {
                type = XQueryElementType.TYPED_FUNCTION_TEST;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.K_AS) {
                if (type == XQueryElementType.ANY_FUNCTION_TEST && !haveErrors) {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.as-not-supported-in-test", "AnyFunctionTest"));
                    haveErrors = true;
                } else {
                    advanceLexer();
                }

                parseWhiteSpaceAndCommentTokens();
                if (!parseSequenceType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                }
            }

            functionTestMarker.done(type);
            return true;
        }
        return false;
    }

    private boolean parseParenthesizedItemType() {
        final PsiBuilder.Marker parenthesizedItemTypeMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENTHESIS_OPEN);
        if (parenthesizedItemTypeMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!parseItemType()) {
                error(XQueryBundle.message("parser.error.expected", "ItemType"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            parenthesizedItemTypeMarker.done(XQueryElementType.PARENTHESIZED_ITEM_TYPE);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    private boolean parseKindTest() {
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
            || parseJsonKindTest() != ParseStatus.NOT_MATCHED;
    }

    private boolean parseAnyKindTest() {
        final PsiBuilder.Marker anyKindTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NODE);
        if (anyKindTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                anyKindTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL) || matchTokenType(XQueryTokenType.STAR)) { // MarkLogic 8.0
                //
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            anyKindTestMarker.done(XQueryElementType.ANY_KIND_TEST);
            return true;
        }
        return false;
    }

    private boolean parseDocumentTest() {
        final PsiBuilder.Marker documentTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT_NODE);
        if (documentTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                documentTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseElementTest() || parseSchemaElementTest()) {
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            documentTestMarker.done(XQueryElementType.DOCUMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseTextTest() {
        final PsiBuilder.Marker textTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TEXT);
        if (textTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                textTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL) ||
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"))) { // MarkLogic 8.0
                //
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            textTestMarker.done(XQueryElementType.TEXT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseCommentTest() {
        final PsiBuilder.Marker commentTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMMENT);
        if (commentTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                commentTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            commentTestMarker.done(XQueryElementType.COMMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseNamespaceNodeTest() {
        final PsiBuilder.Marker namespaceTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NAMESPACE_NODE);
        if (namespaceTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                namespaceTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            namespaceTestMarker.done(XQueryElementType.NAMESPACE_NODE_TEST);
            return true;
        }
        return false;
    }

    private boolean parsePITest() {
        final PsiBuilder.Marker piTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION);
        if (piTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                piTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseQName(XQueryElementType.NCNAME) || parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            piTestMarker.done(XQueryElementType.PI_TEST);
            return true;
        }
        return false;
    }

    private boolean parseAttributeTest() {
        final PsiBuilder.Marker attributeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ATTRIBUTE);
        if (attributeTestMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                attributeTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseAttribNameOrWildcard()) {
                parseWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-eqname"));
                        haveErrors = true;
                    }
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","));
                    haveErrors = true;
                    parseQName(XQueryElementType.QNAME);
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            attributeTestMarker.done(XQueryElementType.ATTRIBUTE_TEST);
            return true;
        }
        return false;
    }

    private boolean parseAttribNameOrWildcard() {
        final PsiBuilder.Marker attribNameOrWildcardMarker = mark();
        if (matchTokenType(XQueryTokenType.STAR) || parseEQName(XQueryElementType.ATTRIBUTE_NAME)) {
            attribNameOrWildcardMarker.done(XQueryElementType.ATTRIB_NAME_OR_WILDCARD);
            return true;
        }
        attribNameOrWildcardMarker.drop();
        return false;
    }

    private boolean parseSchemaAttributeTest() {
        final PsiBuilder.Marker schemaAttributeTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ATTRIBUTE);
        if (schemaAttributeTestMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                schemaAttributeTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ATTRIBUTE_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            schemaAttributeTestMarker.done(XQueryElementType.SCHEMA_ATTRIBUTE_TEST);
            return true;
        }
        return false;
    }

    private boolean parseElementTest() {
        final PsiBuilder.Marker elementTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ELEMENT);
        if (elementTestMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                elementTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseElementNameOrWildcard()) {
                parseWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    parseWhiteSpaceAndCommentTokens();
                    if (!parseEQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-eqname"));
                        haveErrors = true;
                    }

                    parseWhiteSpaceAndCommentTokens();
                    matchTokenType(XQueryTokenType.OPTIONAL);
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","));
                    haveErrors = true;
                    parseQName(XQueryElementType.QNAME);
                }
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            elementTestMarker.done(XQueryElementType.ELEMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseElementNameOrWildcard() {
        final PsiBuilder.Marker elementNameOrWildcardMarker = mark();
        if (matchTokenType(XQueryTokenType.STAR) || parseEQName(XQueryElementType.ELEMENT_NAME)) {
            elementNameOrWildcardMarker.done(XQueryElementType.ELEMENT_NAME_OR_WILDCARD);
            return true;
        }
        elementNameOrWildcardMarker.drop();
        return false;
    }

    private boolean parseSchemaElementTest() {
        final PsiBuilder.Marker schemaElementTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_SCHEMA_ELEMENT);
        if (schemaElementTestMarker != null) {
            boolean haveErrors = false;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                schemaElementTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ELEMENT_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            schemaElementTestMarker.done(XQueryElementType.SCHEMA_ELEMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseBinaryTest() {
        final PsiBuilder.Marker binaryTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BINARY);
        if (binaryTestMarker != null) {
            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                binaryTestMarker.rollbackTo();
                return false;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            binaryTestMarker.done(XQueryElementType.BINARY_TEST);
            return true;
        }
        return false;
    }

    private ParseStatus parseJsonKindTest() {
        ParseStatus status = parseArrayTest();
        if (status == ParseStatus.NOT_MATCHED) status = parseBooleanTest();
        if (status == ParseStatus.NOT_MATCHED) status = parseNullTest();
        if (status == ParseStatus.NOT_MATCHED) status = parseNumberTest();
        if (status == ParseStatus.NOT_MATCHED) status = parseObjectTest();
        return status;
    }

    private ParseStatus parseArrayTest() {
        final PsiBuilder.Marker arrayTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_ARRAY_NODE);
        if (arrayTestMarker != null) {
            ParseStatus status = ParseStatus.MATCHED;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                arrayTestMarker.rollbackTo();
                return ParseStatus.NOT_MATCHED;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                //
            } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            arrayTestMarker.done(XQueryElementType.ARRAY_TEST);
            return status;
        }
        return ParseStatus.NOT_MATCHED;
    }

    private ParseStatus parseBooleanTest() {
        final PsiBuilder.Marker booleanTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BOOLEAN_NODE);
        if (booleanTestMarker != null) {
            ParseStatus status = ParseStatus.MATCHED;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                booleanTestMarker.rollbackTo();
                return ParseStatus.NOT_MATCHED;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                //
            } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            booleanTestMarker.done(XQueryElementType.BOOLEAN_TEST);
            return status;
        }
        return ParseStatus.NOT_MATCHED;
    }

    private ParseStatus parseNullTest() {
        final PsiBuilder.Marker nullTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NULL_NODE);
        if (nullTestMarker != null) {
            ParseStatus status = ParseStatus.MATCHED;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                nullTestMarker.rollbackTo();
                return ParseStatus.NOT_MATCHED;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                //
            } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            nullTestMarker.done(XQueryElementType.NULL_TEST);
            return status;
        }
        return ParseStatus.NOT_MATCHED;
    }

    private ParseStatus parseNumberTest() {
        final PsiBuilder.Marker numberTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NUMBER_NODE);
        if (numberTestMarker != null) {
            ParseStatus status = ParseStatus.MATCHED;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                numberTestMarker.rollbackTo();
                return ParseStatus.NOT_MATCHED;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                //
            } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            numberTestMarker.done(XQueryElementType.NUMBER_TEST);
            return status;
        }
        return ParseStatus.NOT_MATCHED;
    }

    private ParseStatus parseObjectTest() {
        final PsiBuilder.Marker objectTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_OBJECT_NODE);
        if (objectTestMarker != null) {
            ParseStatus status = ParseStatus.MATCHED;

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                objectTestMarker.rollbackTo();
                return ParseStatus.NOT_MATCHED;
            }

            parseWhiteSpaceAndCommentTokens();
            if (parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                //
            } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE) {
                errorOnTokenType(XQueryTokenType.STAR, XQueryBundle.message("parser.error.expected-either", "StringLiteral", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            parseWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                status = ParseStatus.MATCHED_WITH_ERRORS;
            }

            objectTestMarker.done(XQueryElementType.OBJECT_TEST);
            return status;
        }
        return ParseStatus.NOT_MATCHED;
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols

    private boolean parseStringLiteral(IElementType type) {
        final PsiBuilder.Marker stringMarker = matchTokenTypeWithMarker(XQueryTokenType.STRING_LITERAL_START);
        while (stringMarker != null) {
            if (matchTokenType(XQueryTokenType.STRING_LITERAL_CONTENTS) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                matchTokenType(XQueryTokenType.ESCAPED_CHARACTER)) {
                //
            } else if (matchTokenType(XQueryTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type);
                return true;
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"));
            } else if (errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) ||
                       matchTokenType(XQueryTokenType.BAD_CHARACTER)) {
                //
            } else {
                stringMarker.done(type);
                error(XQueryBundle.message("parser.error.incomplete-string"));
                return true;
            }
        }
        return false;
    }

    private boolean parseEQName(IElementType type) {
        if (parseQName(type)) {
            return true;
        }

        final PsiBuilder.Marker eqnameMarker = mark();
        if (parseURIQualifiedName(type)) {
            if (type == XQueryElementType.QNAME) {
                eqnameMarker.drop();
            } else {
                eqnameMarker.done(type);
            }
            return true;
        }
        eqnameMarker.drop();
        return false;
    }

    private boolean parseQName(IElementType type) {
        final PsiBuilder.Marker qnameMarker = mark();
        boolean isWildcard = getTokenType() == XQueryTokenType.STAR;
        if (getTokenType() instanceof INCNameType || isWildcard) {
            if (isWildcard && type != XQueryElementType.WILDCARD) {
                error(XQueryBundle.message("parser.error.unexpected-wildcard"));
            }
            advanceLexer();

            final PsiBuilder.Marker beforeMarker = mark();
            if (parseWhiteSpaceAndCommentTokens() && (
                getTokenType() == XQueryTokenType.QNAME_SEPARATOR ||
                getTokenType() == XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR ||
                getTokenType() == XQueryTokenType.XML_TAG_QNAME_SEPARATOR)) {
                beforeMarker.error(XQueryBundle.message(isWildcard ? "parser.error.wildcard.whitespace-before-local-part" : "parser.error.qname.whitespace-before-local-part"));
            } else {
                beforeMarker.drop();
            }

            if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR ||
                getTokenType() == XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR ||
                getTokenType() == XQueryTokenType.XML_TAG_QNAME_SEPARATOR) {
                if ((type == XQueryElementType.NCNAME) || (type == XQueryElementType.PREFIX)) {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"));
                } else {
                    advanceLexer();
                }

                final PsiBuilder.Marker afterMarker = mark();
                if (parseWhiteSpaceAndCommentTokens()) {
                    afterMarker.error(XQueryBundle.message(isWildcard ? "parser.error.wildcard.whitespace-after-local-part" : "parser.error.qname.whitespace-after-local-part"));
                } else {
                    afterMarker.drop();
                }

                if (getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
                    error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                } else if (getTokenType() instanceof INCNameType) {
                    advanceLexer();
                } else if (getTokenType() == XQueryTokenType.STAR) {
                    if (type == XQueryElementType.WILDCARD) {
                        if (isWildcard) {
                            final PsiBuilder.Marker errorMarker = mark();
                            advanceLexer();
                            errorMarker.error(XQueryBundle.message("parser.error.wildcard.both-prefix-and-local-wildcard"));
                        } else {
                            advanceLexer();
                        }
                    } else {
                        final PsiBuilder.Marker errorMarker = mark();
                        advanceLexer();
                        errorMarker.error(XQueryBundle.message("parser.error.qname.wildcard-local-name"));
                    }
                    isWildcard = true;
                } else {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                }

                if (type == XQueryElementType.WILDCARD) {
                    qnameMarker.done(isWildcard ? XQueryElementType.WILDCARD : XQueryElementType.QNAME);
                } else {
                    qnameMarker.done((type == XQueryElementType.NCNAME) || (type == XQueryElementType.PREFIX) ? XQueryElementType.QNAME : type);
                }
                return true;
            } else {
                IElementType ncname = (type == XQueryElementType.PREFIX) ? XQueryElementType.PREFIX : XQueryElementType.NCNAME;
                if (type == XQueryElementType.WILDCARD) {
                    qnameMarker.done(isWildcard ? XQueryElementType.WILDCARD : ncname);
                } else {
                    qnameMarker.done(type == XQueryElementType.QNAME ? ncname : type);
                }
            }
            return true;
        }

        if (matchTokenType(XQueryTokenType.QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_TAG_QNAME_SEPARATOR)) {
            parseWhiteSpaceAndCommentTokens();
            if (getTokenType() instanceof INCNameType || getTokenType() == XQueryTokenType.STAR) {
                advanceLexer();
            }
            if (type == XQueryElementType.NCNAME) {
                qnameMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"));
            } else {
                qnameMarker.error(XQueryBundle.message("parser.error.qname.missing-prefix"));
            }
            return true;
        }

        qnameMarker.drop();
        return false;
    }

    private boolean parseBracedURILiteral() {
        final PsiBuilder.Marker stringMarker = matchTokenTypeWithMarker(XQueryTokenType.BRACED_URI_LITERAL_START);
        while (stringMarker != null) {
            if (matchTokenType(XQueryTokenType.STRING_LITERAL_CONTENTS) ||
                matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                matchTokenType(XQueryTokenType.CHARACTER_REFERENCE)) {
                //
            } else if (matchTokenType(XQueryTokenType.BRACED_URI_LITERAL_END)) {
                stringMarker.done(XQueryElementType.BRACED_URI_LITERAL);
                return true;
            } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                error(XQueryBundle.message("parser.error.incomplete-entity"));
            } else if (errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity")) ||
                       matchTokenType(XQueryTokenType.BAD_CHARACTER)) {
                //
            } else {
                stringMarker.done(XQueryElementType.BRACED_URI_LITERAL);
                error(XQueryBundle.message("parser.error.incomplete-braced-uri-literal"));
                return true;
            }
        }
        return false;
    }

    private boolean parseURIQualifiedName(IElementType type) {
        final PsiBuilder.Marker qnameMarker = mark();
        if (parseBracedURILiteral()) {
            if (getTokenType() instanceof INCNameType) {
                advanceLexer();
            } else if (matchTokenType(XQueryTokenType.STAR)) {
                if (type != XQueryElementType.WILDCARD) {
                    error(XQueryBundle.message("parser.error.eqname.wildcard-local-name"));
                }
            } else {
                error(XQueryBundle.message("parser.error.expected-ncname"));
            }
            qnameMarker.done(XQueryElementType.URI_QUALIFIED_NAME);
            return true;
        }
        qnameMarker.drop();
        return false;
    }

    private boolean parseWhiteSpaceAndCommentTokens() {
        boolean skipped = false;
        while (true) {
            if (mBuilder.getTokenType() == XQueryTokenType.WHITE_SPACE ||
                    mBuilder.getTokenType() == XQueryTokenType.XML_WHITE_SPACE) {
                skipped = true;
                mBuilder.advanceLexer();
            } else if (mBuilder.getTokenType() == XQueryTokenType.COMMENT_START_TAG) {
                skipped = true;
                final PsiBuilder.Marker commentMarker = mark();
                mBuilder.advanceLexer();
                // NOTE: XQueryTokenType.COMMENT is omitted by the PsiBuilder.
                if (mBuilder.getTokenType() == XQueryTokenType.COMMENT_END_TAG) {
                    mBuilder.advanceLexer();
                    commentMarker.done(XQueryElementType.COMMENT);
                } else {
                    mBuilder.advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                    commentMarker.done(XQueryElementType.COMMENT);
                    mBuilder.error(XQueryBundle.message("parser.error.incomplete-comment"));
                }
            } else if (mBuilder.getTokenType() == XQueryTokenType.COMMENT_END_TAG) {
                skipped = true;
                final PsiBuilder.Marker errorMarker = mark();
                mBuilder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.end-of-comment-without-start", "(:"));
            } else if (errorOnTokenType(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING, XQueryBundle.message("parser.error.misplaced-entity"))) {
                skipped = true;
            } else {
                return skipped;
            }
        }
    }

    // endregion
}
