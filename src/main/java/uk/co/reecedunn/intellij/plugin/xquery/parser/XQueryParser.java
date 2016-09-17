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
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryReservedFunctionNameOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

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
            if (skipWhiteSpaceAndCommentTokens()) continue;
            if (matched && !haveError) {
                error(XQueryBundle.message("parser.error.expected-eof"));
                haveError = true;
            }
            if (parseModule()) {
                matched = true;
                continue;
            }
            if (errorOnTokenType(XQueryTokenType.INVALID, XQueryBundle.message("parser.error.invalid-token"))) continue;
            advanceLexer();
        }
    }

    // endregion
    // region Parser Helper Methods

    private boolean skipWhiteSpaceAndCommentTokens() {
        boolean skipped = false;
        while (true) {
            if (mBuilder.getTokenType() == XQueryTokenType.WHITE_SPACE ||
                mBuilder.getTokenType() == XQueryTokenType.XML_WHITE_SPACE) {
                skipped = true;
                mBuilder.advanceLexer();
            } else if (mBuilder.getTokenType() == XQueryTokenType.COMMENT_START_TAG) {
                skipped = true;
                final PsiBuilder.Marker commentMarker = mBuilder.mark();
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
                final PsiBuilder.Marker errorMarker = mBuilder.mark();
                mBuilder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.end-of-comment-without-start", "(:"));
            } else if (errorOnTokenType(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING, XQueryBundle.message("parser.error.misplaced-entity"))) {
                skipped = true;
            } else {
                return skipped;
            }
        }
    }

    private boolean matchTokenType(IElementType type) {
        if (mBuilder.getTokenType() == type) {
            mBuilder.advanceLexer();
            return true;
        }
        return false;
    }

    private PsiBuilder.Marker matchTokenTypeWithMarker(IElementType type) {
        if (mBuilder.getTokenType() == type) {
            final PsiBuilder.Marker marker = mBuilder.mark();
            mBuilder.advanceLexer();
            return marker;
        }
        return null;
    }

    private boolean errorOnTokenType(IElementType type, String message) {
        if (mBuilder.getTokenType() == type) {
            final PsiBuilder.Marker errorMarker = mBuilder.mark();
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

    private boolean parseModule() {
        final PsiBuilder.Marker moduleMarker = mark();
        IElementType type = null;
        if (parseVersionDecl()) {
            type = XQueryElementType.MODULE;
            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected-encoding-string"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
            } else {
                if (!matchTokenType(XQueryTokenType.K_VERSION)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "version"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-version-string"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-encoding-string"));
                        haveErrors = true;
                    }

                    skipWhiteSpaceAndCommentTokens();
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
        if (parseProlog()) {
            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.QUERY_BODY)) {
                error(XQueryBundle.message("parser.error.expected-query-body"));
            }
            return true;
        }
        return parseExpr(XQueryElementType.QUERY_BODY);
    }

    private boolean parseLibraryModule() {
        if (parseModuleDecl()) {
            skipWhiteSpaceAndCommentTokens();
            parseProlog();
            return true;
        }
        return false;
    }

    private boolean parseModuleDecl() {
        final PsiBuilder.Marker moduleDeclMarker = matchTokenTypeWithMarker(XQueryTokenType.K_MODULE);
        if (moduleDeclMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

    private boolean parseProlog() {
        final PsiBuilder.Marker prologMarker = mark();

        PrologDeclState state = PrologDeclState.NOT_MATCHED;
        while (true) {
            PrologDeclState nextState = parseDecl(state == PrologDeclState.NOT_MATCHED ? PrologDeclState.HEADER_STATEMENT : state);
            if (nextState == PrologDeclState.NOT_MATCHED) {
                nextState = parseImport(state == PrologDeclState.NOT_MATCHED ? PrologDeclState.HEADER_STATEMENT : state);
            }

            switch (nextState) {
                case NOT_MATCHED:
                    if (state == PrologDeclState.NOT_MATCHED) {
                        prologMarker.drop();
                        return false;
                    }
                    prologMarker.done(XQueryElementType.PROLOG);
                    return true;
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
            skipWhiteSpaceAndCommentTokens();
        }
    }

    private PrologDeclState parseImport(PrologDeclState state) {
        final PsiBuilder.Marker importMarker = mBuilder.mark();
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_IMPORT)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseSchemaImport()) {
                importMarker.done(XQueryElementType.SCHEMA_IMPORT);
            } else if (parseModuleImport()) {
                importMarker.done(XQueryElementType.MODULE_IMPORT);
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "schema, module"));
                importMarker.done(XQueryElementType.IMPORT);
                return PrologDeclState.UNKNOWN_STATEMENT;
            }
            return PrologDeclState.HEADER_STATEMENT;
        }

        errorMarker.drop();
        importMarker.drop();
        return PrologDeclState.NOT_MATCHED;
    }

    private PrologDeclState parseDecl(PrologDeclState state) {
        final PsiBuilder.Marker declMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DECLARE);
        if (declMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (parseBaseURIDecl(state)) {
                declMarker.done(XQueryElementType.BASE_URI_DECL);
            } else if (parseBoundarySpaceDecl(state)) {
                declMarker.done(XQueryElementType.BOUNDARY_SPACE_DECL);
            } else if (parseConstructionDecl(state)) {
                declMarker.done(XQueryElementType.CONSTRUCTION_DECL);
            } else if (parseCopyNamespacesDecl(state)) {
                declMarker.done(XQueryElementType.COPY_NAMESPACES_DECL);
            } else if (parseDefaultDecl(declMarker, state)) {
            } else if (parseFunctionDecl(declMarker)) {
                return PrologDeclState.BODY_STATEMENT;
            } else if (parseNamespaceDecl(state)) {
                declMarker.done(XQueryElementType.NAMESPACE_DECL);
            } else if (parseOptionDecl()) {
                declMarker.done(XQueryElementType.OPTION_DECL);
                return PrologDeclState.BODY_STATEMENT;
            } else if (parseOrderingModeDecl(state)) {
                declMarker.done(XQueryElementType.ORDERING_MODE_DECL);
            } else if (parseRevalidationDecl(state)) {
                declMarker.done(XQueryElementType.REVALIDATION_DECL);
            } else if (parseVarDecl()) {
                declMarker.done(XQueryElementType.VAR_DECL);
                return PrologDeclState.BODY_STATEMENT;
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "base-uri, boundary-space, construction, copy-namespaces, default, function, namespace, option, ordering, variable"));
                parseUnknownDecl();
                declMarker.done(XQueryElementType.UNKNOWN_DECL);
                return PrologDeclState.UNKNOWN_STATEMENT;
            }
            return PrologDeclState.HEADER_STATEMENT;
        }
        return PrologDeclState.NOT_MATCHED;
    }

    private boolean parseNamespaceDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_NAMESPACE)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseBoundarySpaceDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_BOUNDARY_SPACE)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseDefaultDecl(PsiBuilder.Marker defaultDeclMarker, PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_DEFAULT)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseDefaultNamespaceDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_NAMESPACE_DECL);
            } else if (parseEmptyOrderDecl()) {
                defaultDeclMarker.done(XQueryElementType.EMPTY_ORDER_DECL);
            } else if (parseDefaultCollationDecl()) {
                defaultDeclMarker.done(XQueryElementType.DEFAULT_COLLATION_DECL);
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "collation, element, function, order"));
                parseUnknownDecl();
                defaultDeclMarker.done(XQueryElementType.UNKNOWN_DECL);
            }
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseDefaultNamespaceDecl() {
        if (matchTokenType(XQueryTokenType.K_ELEMENT) || matchTokenType(XQueryTokenType.K_FUNCTION)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseOptionDecl() {
        if (matchTokenType(XQueryTokenType.K_OPTION)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.STRING_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-option-string"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseOrderingModeDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_ORDERING)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ORDERED) && !matchTokenType(XQueryTokenType.K_UNORDERED)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "ordered, unordered"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseEmptyOrderDecl() {
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_EMPTY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "empty"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseRevalidationDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_REVALIDATION)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_STRICT) && !matchTokenType(XQueryTokenType.K_LAX) && !matchTokenType(XQueryTokenType.K_SKIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "lax, skip, strict"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseCopyNamespacesDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_COPY_NAMESPACES)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_NO_PRESERVE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, no-preserve"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.COMMA) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ","));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_INHERIT) && !matchTokenType(XQueryTokenType.K_NO_INHERIT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "inherit, no-inherit"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseDefaultCollationDecl() {
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseBaseURIDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_BASE_URI)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseUnknownDecl() {
        while (true) {
            if (skipWhiteSpaceAndCommentTokens()) continue;
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
            if (matchTokenType(XQueryTokenType.K_LEAST)) continue;
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) continue;
            if (matchTokenType(XQueryTokenType.K_NO_INHERIT)) continue;
            if (matchTokenType(XQueryTokenType.K_NO_PRESERVE)) continue;
            if (matchTokenType(XQueryTokenType.K_ORDER)) continue;
            if (matchTokenType(XQueryTokenType.K_ORDERED)) continue;
            if (matchTokenType(XQueryTokenType.K_PRESERVE)) continue;
            if (matchTokenType(XQueryTokenType.K_STRIP)) continue;
            if (matchTokenType(XQueryTokenType.K_UNORDERED)) continue;

            if (parseExprSingle()) continue;
            return true;
        }
    }

    private boolean parseSchemaImport() {
        if (getTokenType() == XQueryTokenType.K_SCHEMA) {
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            boolean haveErrors = parseSchemaPrefix();

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL)) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"));
                        haveErrors = true;
                    }
                    skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                error(XQueryBundle.message("parser.error.expected-ncname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            schemaPrefixMarker.done(XQueryElementType.SCHEMA_PREFIX);
            return haveErrors;
        }

        final PsiBuilder.Marker schemaPrefixDefaultMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DEFAULT);
        if (schemaPrefixDefaultMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ELEMENT)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "element"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            schemaPrefixDefaultMarker.done(XQueryElementType.SCHEMA_PREFIX);
        }
        return haveErrors;
    }

    private boolean parseModuleImport() {
        if (getTokenType() == XQueryTokenType.K_MODULE) {
            boolean haveErrors = false;
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.NCNAME)) {
                    error(XQueryBundle.message("parser.error.expected-ncname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "="));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
            }

            if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-uri-string"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AT)) {
                do {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseStringLiteral(XQueryElementType.URI_LITERAL) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-uri-string"));
                        haveErrors = true;
                    }
                    skipWhiteSpaceAndCommentTokens();
                } while (matchTokenType(XQueryTokenType.COMMA));
            }
            return true;
        }
        return false;
    }

    private boolean parseVarDecl() {
        if (matchTokenType(XQueryTokenType.K_VARIABLE)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.ASSIGN_EQUAL)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }
            } else if (matchTokenType(XQueryTokenType.K_EXTERNAL)) {
            } else {
                error(XQueryBundle.message("parser.error.expected-variable-value"));
                parseExprSingle();
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }
        return false;
    }

    private boolean parseConstructionDecl(PrologDeclState state) {
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_CONSTRUCTION)) {
            if (state == PrologDeclState.HEADER_STATEMENT) {
                errorMarker.drop();
            } else {
                errorMarker.error(XQueryBundle.message("parser.error.expected-prolog-body"));
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_PRESERVE) && !matchTokenType(XQueryTokenType.K_STRIP)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "preserve, strip"));
            }

            skipWhiteSpaceAndCommentTokens();
            return true;
        }

        errorMarker.drop();
        return false;
    }

    private boolean parseFunctionDecl(PsiBuilder.Marker functionDeclMarker) {
        boolean haveAnnotation = false;
        if (matchTokenType(XQueryTokenType.K_UPDATING)) {
            haveAnnotation = true;
            skipWhiteSpaceAndCommentTokens();
        }

        if (getTokenType() == XQueryTokenType.K_FUNCTION || haveAnnotation) {
            boolean haveErrors = false;

            if (getTokenType() == XQueryTokenType.K_FUNCTION) {
                advanceLexer();
            } else {
                error(XQueryBundle.message("parser.error.expected-keyword", "function"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
                // DefaultNamespaceDecl with missing 'default' keyword.
                error(XQueryBundle.message("parser.error.expected", "("));
                parseStringLiteral(XQueryElementType.STRING_LITERAL);
                skipWhiteSpaceAndCommentTokens();
                functionDeclMarker.done(XQueryElementType.UNKNOWN_DECL);
                return true;
            } else if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseParamList();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_AS)) {
                skipWhiteSpaceAndCommentTokens();
                parseSequenceType();
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_EXTERNAL) && !parseEnclosedExpr() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-enclosed-expression-or-keyword", "external"));
                parseExpr(XQueryElementType.EXPR);

                skipWhiteSpaceAndCommentTokens();
                matchTokenType(XQueryTokenType.BLOCK_CLOSE);
            }

            skipWhiteSpaceAndCommentTokens();
            functionDeclMarker.done(XQueryElementType.FUNCTION_DECL);
            return true;
        }

        return false;
    }

    private boolean parseParamList() {
        final PsiBuilder.Marker paramListMarker = mark();

        while (parseParam()) {
            skipWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.VARIABLE_INDICATOR) {
                error(XQueryBundle.message("parser.error.expected", ","));
            } else if (!matchTokenType(XQueryTokenType.COMMA)) {
                paramListMarker.done(XQueryElementType.PARAM_LIST);
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
        }

        paramListMarker.drop();
        return false;
    }

    private boolean parseParam() {
        final PsiBuilder.Marker paramMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
            }

            skipWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            paramMarker.done(XQueryElementType.PARAM);
            return true;
        } else if (getTokenType() == XQueryTokenType.NCNAME || getTokenType() instanceof IXQueryKeywordOrNCNameType || getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
            error(XQueryBundle.message("parser.error.expected", "$"));
            parseQName(XQueryElementType.QNAME);

            skipWhiteSpaceAndCommentTokens();
            parseTypeDeclaration();

            paramMarker.done(XQueryElementType.PARAM);
            return true;
        }

        paramMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr

    private boolean parseEnclosedExpr() {
        final PsiBuilder.Marker enclosedExprMarker = mark();
        if (matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
            boolean haveErrors = false;
            skipWhiteSpaceAndCommentTokens();

            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            enclosedExprMarker.done(XQueryElementType.ENCLOSED_EXPR);
            return true;
        }

        enclosedExprMarker.drop();
        return false;
    }

    private boolean parseExpr(IElementType type) {
        final PsiBuilder.Marker exprMarker = mark();
        if (parseExprSingle()) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.COMMA)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
            }
            exprMarker.done(type);
            return true;
        }
        exprMarker.drop();
        return false;
    }

    private boolean parseExprSingle() {
        return parseExprSingle(null);
    }

    private boolean parseExprSingle(IElementType type) {
        return parseFLWORExpr()
            || parseQuantifiedExpr()
            || parseTypeswitchExpr()
            || parseIfExpr()
            || parseInsertExpr() // Update Facility 1.0
            || parseDeleteExpr() // Update Facility 1.0
            || parseRenameExpr() // Update Facility 1.0
            || parseReplaceExpr() // Update Facility 1.0
            || parseTransformExpr() // Update Facility 1.0
            || parseOrExpr(type);
    }

    // endregion
    // region Grammar :: Expr :: FLWORExpr

    private boolean parseFLWORExpr() {
        final PsiBuilder.Marker flworExprMarker = mark();
        boolean haveForLetClause = false;
        while (parseForClause() || parseLetClause()) {
            haveForLetClause = true;
        }
        if (haveForLetClause) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            parseWhereClause();

            skipWhiteSpaceAndCommentTokens();
            parseOrderByClause();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "for, let, order, return, stable, where"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            flworExprMarker.done(XQueryElementType.FLWOR_EXPR);
            return true;
        } else if (errorOnTokenType(XQueryTokenType.K_RETURN, XQueryBundle.message("parser.error.return-without-flwor"))) {
            skipWhiteSpaceAndCommentTokens();
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

    private boolean parseForClause() {
        final PsiBuilder.Marker forClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_FOR)) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        forClauseMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                boolean haveTypeDeclaration = parseTypeDeclaration();

                skipWhiteSpaceAndCommentTokens();
                boolean havePositionalVar = parsePositionalVar();

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_IN) && !haveErrors) {
                    if (haveTypeDeclaration && !havePositionalVar) {
                        error(XQueryBundle.message("parser.error.expected-keyword", "at, in"));
                    } else {
                        error(XQueryBundle.message("parser.error.expected-keyword", havePositionalVar ? "in" : "as, at, in"));
                    }
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                skipWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            forClauseMarker.done(XQueryElementType.FOR_CLAUSE);
            return true;
        }
        forClauseMarker.drop();
        return false;
    }

    private boolean parsePositionalVar() {
        final PsiBuilder.Marker positionalVarMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AT);
        if (positionalVarMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                error(XQueryBundle.message("parser.error.expected", "$"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-qname"));
            }

            positionalVarMarker.done(XQueryElementType.POSITIONAL_VAR);
            return true;
        }
        return false;
    }

    private boolean parseLetClause() {
        final PsiBuilder.Marker letClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_LET)) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        letClauseMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                boolean haveTypeDeclaration = parseTypeDeclaration();

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                    if (haveTypeDeclaration) {
                        error(XQueryBundle.message("parser.error.expected", ":="));
                    } else {
                        error(XQueryBundle.message("parser.error.expected-variable-assign-or-keyword", "as"));
                    }
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                skipWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            letClauseMarker.done(XQueryElementType.LET_CLAUSE);
            return true;
        }
        letClauseMarker.drop();
        return false;
    }

    private boolean parseWhereClause() {
        final PsiBuilder.Marker whereClauseMarker = matchTokenTypeWithMarker(XQueryTokenType.K_WHERE);
        if (whereClauseMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            whereClauseMarker.done(XQueryElementType.WHERE_CLAUSE);
            return true;
        }
        return false;
    }

    private boolean parseOrderByClause() {
        final PsiBuilder.Marker orderByClauseMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_ORDER)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_BY)) {
                error(XQueryBundle.message("parser.error.expected", "by"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseOrderSpecList() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "OrderSpecList"));
            }

            orderByClauseMarker.done(XQueryElementType.ORDER_BY_CLAUSE);
            return true;
        } else if (matchTokenType(XQueryTokenType.K_STABLE)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ORDER)) {
                error(XQueryBundle.message("parser.error.expected", "order"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_BY) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "by"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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
        final PsiBuilder.Marker orderSpecListMarker = mBuilder.mark();
        if (parseOrderSpec()) {
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.COMMA)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseOrderSpec()) {
                    error(XQueryBundle.message("parser.error.expected", "OrderSpec"));
                }

                skipWhiteSpaceAndCommentTokens();
            }

            orderSpecListMarker.done(XQueryElementType.ORDER_SPEC_LIST);
            return true;
        }
        orderSpecListMarker.drop();
        return false;
    }

    private boolean parseOrderSpec() {
        final PsiBuilder.Marker orderSpecMarker = mBuilder.mark();
        if (parseExprSingle()) {
            skipWhiteSpaceAndCommentTokens();
            parseOrderModifier();

            orderSpecMarker.done(XQueryElementType.ORDER_SPEC);
            return true;
        }
        orderSpecMarker.drop();
        return false;
    }

    private boolean parseOrderModifier() {
        final PsiBuilder.Marker orderModifierMarker = mBuilder.mark();

        if (matchTokenType(XQueryTokenType.K_ASCENDING) || matchTokenType(XQueryTokenType.K_DESCENDING)) {
            //
        }

        skipWhiteSpaceAndCommentTokens();
        if (matchTokenType(XQueryTokenType.K_EMPTY)) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_GREATEST) && !matchTokenType(XQueryTokenType.K_LEAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "greatest, least"));
            }
        }

        skipWhiteSpaceAndCommentTokens();
        if (matchTokenType(XQueryTokenType.K_COLLATION)) {
            skipWhiteSpaceAndCommentTokens();
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
        final PsiBuilder.Marker quantifiedExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_SOME) || matchTokenType(XQueryTokenType.K_EVERY)) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        quantifiedExprMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                parseTypeDeclaration();

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_IN) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "in"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                skipWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_SATISFIES) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "satisfies"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            quantifiedExprMarker.done(XQueryElementType.QUANTIFIED_EXPR);
            return true;
        }

        quantifiedExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: TypeswitchExpr

    private boolean parseTypeswitchExpr() {
        final PsiBuilder.Marker typeswitchExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_TYPESWITCH)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                typeswitchExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            boolean matched = false;
            while (parseCaseClause()) {
                matched = true;
                skipWhiteSpaceAndCommentTokens();
            }
            if (!matched) {
                error(XQueryBundle.message("parser.error.expected", "CaseClause"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_DEFAULT) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "case, default"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-variable-reference-or-keyword", "return"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            typeswitchExprMarker.done(XQueryElementType.TYPESWITCH_EXPR);
            return true;
        }
        typeswitchExprMarker.drop();
        return false;
    }

    private boolean parseCaseClause() {
        final PsiBuilder.Marker caseClauseMarker = mark();
        if (matchTokenType(XQueryTokenType.K_CASE)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.VARIABLE_INDICATOR)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME)) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                    haveErrors = true;
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseSequenceType() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "SequenceType"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            caseClauseMarker.done(XQueryElementType.CASE_CLAUSE);
            return true;
        }
        caseClauseMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: IfExpr

    private boolean parseIfExpr() {
        final PsiBuilder.Marker ifExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_IF)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                ifExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_THEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "then"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_ELSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "else"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            ifExprMarker.done(XQueryElementType.IF_EXPR);
            return true;
        }
        ifExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: InsertExpr (Update Facility 1.0)

    private boolean parseInsertExpr() {
        final PsiBuilder.Marker insertExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_INSERT)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                insertExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseSourceExpr()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseInsertExprTargetChoice() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "after, as, before, into"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            insertExprMarker.done(XQueryElementType.INSERT_EXPR);
            return true;
        }
        insertExprMarker.drop();
        return false;
    }

    private boolean parseSourceExpr() {
        final PsiBuilder.Marker sourceExprMarker = mark();
        if (parseExprSingle(XQueryElementType.SOURCE_EXPR)) {
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_FIRST) && !matchTokenType(XQueryTokenType.K_LAST)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "first, last"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            matchTokenType(XQueryTokenType.K_INTO);

            insertExprTargetChoiceMarker.done(XQueryElementType.INSERT_EXPR_TARGET_CHOICE);
            return true;
        }

        insertExprTargetChoiceMarker.drop();
        return false;
    }

    private boolean parseTargetExpr(IElementType type) {
        final PsiBuilder.Marker targetExprMarker = mark();
        if (parseExprSingle(type)) {
            targetExprMarker.done(XQueryElementType.TARGET_EXPR);
            return true;
        }
        targetExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: DeleteExpr (Update Facility 1.0)

    private boolean parseDeleteExpr() {
        final PsiBuilder.Marker deleteExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_DELETE)) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE) && !matchTokenType(XQueryTokenType.K_NODES)) {
                deleteExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            deleteExprMarker.done(XQueryElementType.DELETE_EXPR);
            return true;
        }
        deleteExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: ReplaceExpr (Update Facility 1.0)

    private boolean parseReplaceExpr() {
        final PsiBuilder.Marker deleteExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_REPLACE)) {
            boolean haveErrors = false;
            boolean haveValueOf = false;

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_VALUE)) {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_OF)) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"));
                    haveErrors = true;
                }
                haveValueOf = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                if (!haveValueOf) {
                    deleteExprMarker.rollbackTo();
                    return false;
                }
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-keyword", "node"));
                    haveErrors = true;
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(null)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_WITH) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "with"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            deleteExprMarker.done(XQueryElementType.REPLACE_EXPR);
            return true;
        }
        deleteExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: RenameExpr (Update Facility 1.0)

    private boolean parseRenameExpr() {
        final PsiBuilder.Marker renameExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_RENAME)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NODE)) {
                renameExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseTargetExpr(XQueryElementType.TARGET_EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_AS) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseNewNameExpr() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            renameExprMarker.done(XQueryElementType.RENAME_EXPR);
            return true;
        }
        renameExprMarker.drop();
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
    // region Grammar :: Expr :: TransformExpr (Update Facility 1.0)

    private boolean parseTransformExpr() {
        final PsiBuilder.Marker transformExprMarker = mark();
        if (matchTokenType(XQueryTokenType.K_COPY)) {
            boolean haveErrors = false;
            boolean isFirstVarName = true;
            do {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.VARIABLE_INDICATOR) && !haveErrors) {
                    if (isFirstVarName) {
                        transformExprMarker.rollbackTo();
                        return false;
                    } else {
                        error(XQueryBundle.message("parser.error.expected", "$"));
                        haveErrors = true;
                    }
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseQName(XQueryElementType.VAR_NAME) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-qname"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.ASSIGN_EQUAL) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", ":="));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExprSingle() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                }

                isFirstVarName = false;
                skipWhiteSpaceAndCommentTokens();
            } while (matchTokenType(XQueryTokenType.COMMA));

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_MODIFY)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "modify"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle()) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_RETURN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-keyword", "return"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExprSingle() && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
            }

            transformExprMarker.done(XQueryElementType.TRANSFORM_EXPR);
            return true;
        }
        transformExprMarker.drop();
        return false;
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private boolean parseOrExpr(IElementType type) {
        final PsiBuilder.Marker orExprMarker = mark();
        if (parseAndExpr(type)) {
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_OR)) {
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_AND)) {
                skipWhiteSpaceAndCommentTokens();
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
        if (parseRangeExpr(type)) {
            skipWhiteSpaceAndCommentTokens();
            if (parseGeneralComp() || parseValueComp() || parseNodeComp()) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseRangeExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "RangeExpr"));
                }
            }

            comparisonExprMarker.done(XQueryElementType.COMPARISON_EXPR);
            return true;
        }
        comparisonExprMarker.drop();
        return false;
    }

    private boolean parseRangeExpr(IElementType type) {
        final PsiBuilder.Marker rangeExprMarker = mark();
        if (parseAdditiveExpr(type)) {
            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_TO)) {
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.PLUS) || matchTokenType(XQueryTokenType.MINUS)) {
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.STAR) ||
                   matchTokenType(XQueryTokenType.K_DIV) ||
                   matchTokenType(XQueryTokenType.K_IDIV) ||
                   matchTokenType(XQueryTokenType.K_MOD)) {
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_UNION) || matchTokenType(XQueryTokenType.UNION)) {
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.K_INTERSECT) || matchTokenType(XQueryTokenType.K_EXCEPT)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseInstanceofExpr(type)) {
                    error(XQueryBundle.message("parser.error.expected", "InstanceofExpr"));
                }
                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_INSTANCE)) {
                boolean haveErrors = false;

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_OF)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "of"));
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"));
                }
            } else if (getTokenType() == XQueryTokenType.K_OF) {
                error(XQueryBundle.message("parser.error.expected-keyword", "instance"));
                advanceLexer();

                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_TREAT)) {
                boolean haveErrors = false;

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseSingleType() && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "SingleType"));
                }
            } else if (getTokenType() == XQueryTokenType.K_AS && type != XQueryElementType.SOURCE_EXPR && type != XQueryElementType.TARGET_EXPR) {
                error(XQueryBundle.message("parser.error.expected-keyword", "cast, castable, treat"));
                advanceLexer();

                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_CASTABLE)) {
                boolean haveErrors = false;

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_CAST)) {
                boolean haveErrors = false;

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.K_AS)) {
                    haveErrors = true;
                    error(XQueryBundle.message("parser.error.expected-keyword", "as"));
                }

                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
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
        if (parseQName(XQueryElementType.ATOMIC_TYPE)) {
            skipWhiteSpaceAndCommentTokens();
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
        return parseExtensionExpr() || parseValidateExpr() || parsePathExpr();
    }

    private boolean parseValidateExpr() {
        final PsiBuilder.Marker validateExprMarker = matchTokenTypeWithMarker(XQueryTokenType.K_VALIDATE);
        if (validateExprMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            boolean haveValidationMode = matchTokenType(XQueryTokenType.K_LAX) || matchTokenType(XQueryTokenType.K_STRICT);

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                if (haveValidationMode) {
                    error(XQueryBundle.message("parser.error.expected", "{"));
                    haveErrors = true;
                } else {
                    validateExprMarker.rollbackTo();
                    return false;
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
        }
        if (matched) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            skipWhiteSpaceAndCommentTokens();
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
            if (!parseQName(XQueryElementType.QNAME)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
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

    private boolean parsePathExpr() {
        final PsiBuilder.Marker pathExprMarker = mark();
        if (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH)) {
            skipWhiteSpaceAndCommentTokens();
            parseRelativePathExpr();

            pathExprMarker.done(XQueryElementType.PATH_EXPR);
            return true;
        } else if (matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            while (matchTokenType(XQueryTokenType.DIRECT_DESCENDANTS_PATH) || matchTokenType(XQueryTokenType.ALL_DESCENDANTS_PATH)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseStepExpr()) {
                    error(XQueryBundle.message("parser.error.expected", "StepExpr"));
                }

                skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
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
        final PsiBuilder.Marker forwardAxisMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_ATTRIBUTE) ||
            matchTokenType(XQueryTokenType.K_CHILD) ||
            matchTokenType(XQueryTokenType.K_DESCENDANT) ||
            matchTokenType(XQueryTokenType.K_DESCENDANT_OR_SELF) ||
            matchTokenType(XQueryTokenType.K_FOLLOWING) ||
            matchTokenType(XQueryTokenType.K_FOLLOWING_SIBLING) ||
            matchTokenType(XQueryTokenType.K_NAMESPACE) || // MarkLogic
            matchTokenType(XQueryTokenType.K_PROPERTY) || // MarkLogic
            matchTokenType(XQueryTokenType.K_SELF)) {

            skipWhiteSpaceAndCommentTokens();
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
        final PsiBuilder.Marker abbrevForwardStepMarker = mBuilder.mark();
        boolean matched = matchTokenType(XQueryTokenType.ATTRIBUTE_SELECTOR);

        skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
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
        final PsiBuilder.Marker reverseAxisMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_PARENT) ||
            matchTokenType(XQueryTokenType.K_ANCESTOR) ||
            matchTokenType(XQueryTokenType.K_ANCESTOR_OR_SELF) ||
            matchTokenType(XQueryTokenType.K_PRECEDING) ||
            matchTokenType(XQueryTokenType.K_PRECEDING_SIBLING)) {

            skipWhiteSpaceAndCommentTokens();
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
        if (parseQName(XQueryElementType.WILDCARD)) { // QName | Wildcard
            nameTestMarker.done(XQueryElementType.NAME_TEST);
            return true;
        }

        nameTestMarker.drop();
        return false;
    }

    private boolean parseFilterExpr() {
        final PsiBuilder.Marker filterExprMarker = mark();
        if (parsePrimaryExpr()) {
            skipWhiteSpaceAndCommentTokens();
            parsePredicateList();

            filterExprMarker.done(XQueryElementType.FILTER_EXPR);
            return true;
        }
        filterExprMarker.drop();
        return false;
    }

    private boolean parsePredicateList() {
        final PsiBuilder.Marker predicateListMarker = mark();
        while (parsePredicate()) {
            skipWhiteSpaceAndCommentTokens();
        }
        predicateListMarker.done(XQueryElementType.PREDICATE_LIST);
        return true;
    }

    private boolean parsePredicate() {
        final PsiBuilder.Marker predicateMarker = matchTokenTypeWithMarker(XQueryTokenType.PREDICATE_BEGIN);
        if (predicateMarker != null) {
            boolean haveErrors = false;
            skipWhiteSpaceAndCommentTokens();

            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.VAR_NAME)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
            }

            varRefMarker.done(XQueryElementType.VAR_REF);
            return true;
        }
        return false;
    }

    private boolean parseParenthesizedExpr() {
        final PsiBuilder.Marker parenthesizedExprMarker = matchTokenTypeWithMarker(XQueryTokenType.PARENTHESIS_OPEN);
        if (parenthesizedExprMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (parseExpr(XQueryElementType.EXPR)) {
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                orderedExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                unorderedExprMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            unorderedExprMarker.done(XQueryElementType.UNORDERED_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseFunctionCall() {
        final PsiBuilder.Marker functionCallMarker = mBuilder.mark();
        if (parseQName(XQueryElementType.QNAME, true)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                functionCallMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseExprSingle()) {
                skipWhiteSpaceAndCommentTokens();
                while (matchTokenType(XQueryTokenType.COMMA)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseExprSingle() && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-expression"));
                        haveErrors = true;
                    }

                    skipWhiteSpaceAndCommentTokens();
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            functionCallMarker.done(XQueryElementType.FUNCTION_CALL);
            return true;
        }

        functionCallMarker.drop();
        return false;
    }

    private boolean parseConstructor() {
        final PsiBuilder.Marker constructorMarker = mBuilder.mark();
        if (parseDirectConstructor() || parseComputedConstructor()) {
            constructorMarker.done(XQueryElementType.CONSTRUCTOR);
            return true;
        }

        constructorMarker.drop();
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
        final PsiBuilder.Marker attributeListMarker = mBuilder.mark();
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
            } else if (parseEnclosedExpr() ||
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
        final PsiBuilder.Marker elemContentMarker = mBuilder.mark();
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
            } else if (parseEnclosedExpr() ||
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
        final PsiBuilder.Marker cdataMarker = mBuilder.mark();
        final PsiBuilder.Marker errorMarker = mBuilder.mark();
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
            || parseCompTextConstructor()
            || parseCompCommentConstructor()
            || parseCompPIConstructor();
    }

    private boolean parseCompDocConstructor() {
        final PsiBuilder.Marker documentMarker = matchTokenTypeWithMarker(XQueryTokenType.K_DOCUMENT);
        if (documentMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                documentMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    elementMarker.rollbackTo();
                    return false;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.CONTENT_EXPR);

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    attributeMarker.rollbackTo();
                    return false;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            attributeMarker.done(XQueryElementType.COMP_ATTR_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    private boolean parseCompTextConstructor() {
        final PsiBuilder.Marker textMarker = matchTokenTypeWithMarker(XQueryTokenType.K_TEXT);
        if (textMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                textMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                commentMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseExpr(XQueryElementType.EXPR)) {
                error(XQueryBundle.message("parser.error.expected-expression"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.NCNAME)) {
                if (!matchTokenType(XQueryTokenType.BLOCK_OPEN)) {
                    piMarker.rollbackTo();
                    return false;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseExpr(XQueryElementType.EXPR)) {
                    error(XQueryBundle.message("parser.error.expected-expression"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                    error(XQueryBundle.message("parser.error.expected", "}"));
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_OPEN) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            parseExpr(XQueryElementType.EXPR);

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.BLOCK_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "}"));
            }

            piMarker.done(XQueryElementType.COMP_PI_CONSTRUCTOR);
            return true;
        }
        return false;
    }

    // endregion
    // region Grammar :: TypeDeclaration

    private boolean parseTypeDeclaration() {
        final PsiBuilder.Marker typeDeclarationMarker = matchTokenTypeWithMarker(XQueryTokenType.K_AS);
        if (typeDeclarationMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            parseSequenceType();

            typeDeclarationMarker.done(XQueryElementType.TYPE_DECLARATION);
            return true;
        }
        return false;
    }

    private boolean parseSequenceType() {
        final PsiBuilder.Marker sequenceTypeMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_EMPTY_SEQUENCE)) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                sequenceTypeMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE);
            return true;
        } else if (parseItemType()) {
            skipWhiteSpaceAndCommentTokens();
            parseOccurrenceIndicator();

            sequenceTypeMarker.done(XQueryElementType.SEQUENCE_TYPE);
            return true;
        }

        error(XQueryBundle.message("parser.error.expected", "SequenceType"));
        sequenceTypeMarker.drop();
        return false;
    }

    private boolean parseOccurrenceIndicator() {
        final PsiBuilder.Marker occurrenceIndicatorMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.OPTIONAL) || matchTokenType(XQueryTokenType.STAR) || matchTokenType(XQueryTokenType.PLUS)) {
            occurrenceIndicatorMarker.done(XQueryElementType.OCCURRENCE_INDICATOR);
            return true;
        }

        occurrenceIndicatorMarker.drop();
        return false;
    }

    private boolean parseItemType() {
        final PsiBuilder.Marker itemTypeMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.K_ITEM)) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                itemTypeMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            itemTypeMarker.done(XQueryElementType.ITEM_TYPE);
            return true;
        } else if (parseKindTest() || parseQName(XQueryElementType.ATOMIC_TYPE)) {
            itemTypeMarker.done(XQueryElementType.ITEM_TYPE);
            return true;
        }

        itemTypeMarker.drop();
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
            || parseAnyKindTest()
            || parseBinaryKindTest(); // MarkLogic
    }

    private boolean parseAnyKindTest() {
        final PsiBuilder.Marker anyKindTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NODE);
        if (anyKindTestMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                anyKindTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                documentTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseElementTest() || parseSchemaElementTest()) {
            }

            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                textTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
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
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                commentTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            commentTestMarker.done(XQueryElementType.COMMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parsePITest() {
        final PsiBuilder.Marker piTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION);
        if (piTestMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                piTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseQName(XQueryElementType.NCNAME) || parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                attributeTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseAttribNameOrWildcard()) {
                skipWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-qname"));
                        haveErrors = true;
                    }
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","));
                    haveErrors = true;
                    parseQName(XQueryElementType.QNAME);
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            attributeTestMarker.done(XQueryElementType.ATTRIBUTE_TEST);
            return true;
        }
        return false;
    }

    private boolean parseAttribNameOrWildcard() {
        final PsiBuilder.Marker attribNameOrWildcardMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.STAR) || parseQName(XQueryElementType.ATTRIBUTE_NAME)) {
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                schemaAttributeTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ATTRIBUTE_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                elementTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseElementNameOrWildcard()) {
                skipWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseQName(XQueryElementType.TYPE_NAME)) {
                        error(XQueryBundle.message("parser.error.expected-qname"));
                        haveErrors = true;
                    }

                    skipWhiteSpaceAndCommentTokens();
                    matchTokenType(XQueryTokenType.OPTIONAL);
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    error(XQueryBundle.message("parser.error.expected", ","));
                    haveErrors = true;
                    parseQName(XQueryElementType.QNAME);
                }
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            elementTestMarker.done(XQueryElementType.ELEMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseElementNameOrWildcard() {
        final PsiBuilder.Marker elementNameOrWildcardMarker = mBuilder.mark();
        if (matchTokenType(XQueryTokenType.STAR) || parseQName(XQueryElementType.ELEMENT_NAME)) {
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

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                schemaElementTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ELEMENT_DECLARATION)) {
                error(XQueryBundle.message("parser.error.expected-qname"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            schemaElementTestMarker.done(XQueryElementType.SCHEMA_ELEMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseBinaryKindTest() {
        final PsiBuilder.Marker binaryKindTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_BINARY);
        if (binaryKindTestMarker != null) {
            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                binaryKindTestMarker.rollbackTo();
                return false;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE)) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            binaryKindTestMarker.done(XQueryElementType.BINARY_KIND_TEST);
            return true;
        }
        return false;
    }

    // endregion
    // region Lexical Structure

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

    private boolean parseQName(IElementType type) {
        return parseQName(type, false);
    }

    private boolean parseQName(IElementType type, boolean excludeReservedFunctionNames) {
        final PsiBuilder.Marker qnameMarker = mBuilder.mark();
        boolean isWildcard = getTokenType() == XQueryTokenType.STAR;
        if (getTokenType() instanceof INCNameType || isWildcard) {
            boolean isReservedFunctionName = getTokenType() instanceof IXQueryReservedFunctionNameOrNCNameType;

            if (isWildcard && type != XQueryElementType.WILDCARD) {
                error(XQueryBundle.message("parser.error.unexpected-wildcard"));
            }
            advanceLexer();

            final PsiBuilder.Marker beforeMarker = mark();
            if (skipWhiteSpaceAndCommentTokens() && (
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
                if (type == XQueryElementType.NCNAME) {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"));
                } else {
                    advanceLexer();
                }

                final PsiBuilder.Marker afterMarker = mark();
                if (skipWhiteSpaceAndCommentTokens()) {
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
                    qnameMarker.done(type == XQueryElementType.NCNAME ? XQueryElementType.QNAME : type);
                }
                return true;
            } else {
                if (isReservedFunctionName && excludeReservedFunctionNames) {
                    qnameMarker.rollbackTo();
                    return false;
                }

                if (type == XQueryElementType.WILDCARD) {
                    qnameMarker.done(isWildcard ? XQueryElementType.WILDCARD : XQueryElementType.NCNAME);
                } else {
                    qnameMarker.done(type == XQueryElementType.QNAME ? XQueryElementType.NCNAME : type);
                }
            }
            return true;
        }

        if (matchTokenType(XQueryTokenType.QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR) ||
            matchTokenType(XQueryTokenType.XML_TAG_QNAME_SEPARATOR)) {
            skipWhiteSpaceAndCommentTokens();
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

    // endregion
}
