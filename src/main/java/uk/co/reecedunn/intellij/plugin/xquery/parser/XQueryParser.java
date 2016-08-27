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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

@SuppressWarnings({"SameParameterValue", "StatementWithEmptyBody"})
class XQueryParser {
    // region Main Interface

    private final PsiBuilder mBuilder;
    private final XQueryProjectSettings mSettings;

    public XQueryParser(@NotNull PsiBuilder builder, @NotNull XQueryProjectSettings settings) {
        mBuilder = builder;
        mSettings = settings;
    }

    private boolean isXQuery30OrLater() {
        XQueryVersion version = mSettings.getXQueryVersion();
        return version == XQueryVersion.XQUERY_3_0 || version == XQueryVersion.XQUERY_3_1;
    }

    public void parse() {
        while (getTokenType() != null) {
            if (skipWhiteSpaceAndCommentTokens()) continue;
            if (parseModule()) continue;
            if (errorOnTokenType(XQueryTokenType.INVALID, XQueryBundle.message("parser.error.invalid-token"))) continue;
            advanceLexer();
        }
    }

    // endregion
    // region Parser Helper Methods

    private boolean skipWhiteSpaceAndCommentTokens() {
        boolean skipped = false;
        while (true) {
            if (mBuilder.getTokenType() == XQueryTokenType.WHITE_SPACE) {
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
            final PsiBuilder.Marker versionDecl30Marker = mark();
            if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                if (isXQuery30OrLater()) {
                    versionDecl30Marker.drop();
                } else {
                    versionDecl30Marker.error(XQueryBundle.message("parser.error.version-decl.3.0"));
                }

                skipWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
                    error(XQueryBundle.message("parser.error.expected-encoding-string"));
                    haveErrors = true;
                }

                skipWhiteSpaceAndCommentTokens();
            } else {
                versionDecl30Marker.drop();
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
        if (matchTokenType(XQueryTokenType.K_FUNCTION)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.QNAME)) {
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
        return parseOrExpr();
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private boolean parseOrExpr() {
        final PsiBuilder.Marker orExprMarker = mark();
        if (parseAndExpr()) {
            orExprMarker.done(XQueryElementType.OR_EXPR);
            return true;
        }
        orExprMarker.drop();
        return false;
    }

    private boolean parseAndExpr() {
        final PsiBuilder.Marker andExprMarker = mark();
        if (parseComparisonExpr()) {
            andExprMarker.done(XQueryElementType.AND_EXPR);
            return true;
        }
        andExprMarker.drop();
        return false;
    }

    private boolean parseComparisonExpr() {
        final PsiBuilder.Marker comparisonExprMarker = mark();
        if (parseRangeExpr()) {
            comparisonExprMarker.done(XQueryElementType.COMPARISON_EXPR);
            return true;
        }
        comparisonExprMarker.drop();
        return false;
    }

    private boolean parseRangeExpr() {
        final PsiBuilder.Marker rangeExprMarker = mark();
        if (parseAdditiveExpr()) {
            rangeExprMarker.done(XQueryElementType.RANGE_EXPR);
            return true;
        }
        rangeExprMarker.drop();
        return false;
    }

    private boolean parseAdditiveExpr() {
        final PsiBuilder.Marker additiveExprMarker = mark();
        if (parseMultiplicativeExpr()) {
            additiveExprMarker.done(XQueryElementType.ADDITIVE_EXPR);
            return true;
        }
        additiveExprMarker.drop();
        return false;
    }

    private boolean parseMultiplicativeExpr() {
        final PsiBuilder.Marker multiplicativeExprMarker = mark();
        if (parseUnionExpr()) {
            multiplicativeExprMarker.done(XQueryElementType.MULTIPLICATIVE_EXPR);
            return true;
        }
        multiplicativeExprMarker.drop();
        return false;
    }

    private boolean parseUnionExpr() {
        final PsiBuilder.Marker unionExprMarker = mark();
        if (parseIntersectExceptExpr()) {
            unionExprMarker.done(XQueryElementType.UNION_EXPR);
            return true;
        }
        unionExprMarker.drop();
        return false;
    }

    private boolean parseIntersectExceptExpr() {
        final PsiBuilder.Marker intersectExceptExprMarker = mark();
        if (parseInstanceofExpr()) {
            intersectExceptExprMarker.done(XQueryElementType.INTERSECT_EXCEPT_EXPR);
            return true;
        }
        intersectExceptExprMarker.drop();
        return false;
    }

    private boolean parseInstanceofExpr() {
        final PsiBuilder.Marker instanceofExprMarker = mark();
        if (parseTreatExpr()) {
            instanceofExprMarker.done(XQueryElementType.INSTANCEOF_EXPR);
            return true;
        }
        instanceofExprMarker.drop();
        return false;
    }

    private boolean parseTreatExpr() {
        final PsiBuilder.Marker treatExprMarker = mark();
        if (parseCastableExpr()) {
            treatExprMarker.done(XQueryElementType.TREAT_EXPR);
            return true;
        }
        treatExprMarker.drop();
        return false;
    }

    private boolean parseCastableExpr() {
        final PsiBuilder.Marker castableExprMarker = mark();
        if (parseCastExpr()) {
            castableExprMarker.done(XQueryElementType.CASTABLE_EXPR);
            return true;
        }
        castableExprMarker.drop();
        return false;
    }

    private boolean parseCastExpr() {
        final PsiBuilder.Marker castExprMarker = mark();
        if (parseUnaryExpr()) {
            castExprMarker.done(XQueryElementType.CAST_EXPR);
            return true;
        }
        castExprMarker.drop();
        return false;
    }

    private boolean parseUnaryExpr() {
        final PsiBuilder.Marker pathExprMarker = mark();
        if (parseValueExpr()) {
            pathExprMarker.done(XQueryElementType.UNARY_EXPR);
            return true;
        }
        pathExprMarker.drop();
        return false;
    }

    private boolean parseValueExpr() {
        return parsePathExpr();
    }

    private boolean parsePathExpr() {
        final PsiBuilder.Marker pathExprMarker = mark();
        if (parseRelativePathExpr()) {
            pathExprMarker.done(XQueryElementType.PATH_EXPR);
            return true;
        }
        pathExprMarker.drop();
        return false;
    }

    private boolean parseRelativePathExpr() {
        final PsiBuilder.Marker relativePathExprMarker = mark();
        if (parseStepExpr()) {
            relativePathExprMarker.done(XQueryElementType.RELATIVE_PATH_EXPR);
            return true;
        }
        relativePathExprMarker.drop();
        return false;
    }

    private boolean parseStepExpr() {
        return parseFilterExpr();
    }

    private boolean parseFilterExpr() {
        final PsiBuilder.Marker filterExprMarker = mark();
        if (parsePrimaryExpr()) {
            filterExprMarker.done(XQueryElementType.FILTER_EXPR);
            return true;
        }
        filterExprMarker.drop();
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
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
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
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
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

            unorderedExprMarker.done(XQueryElementType.UNORDERED_EXPR);
            return true;
        }
        return false;
    }

    private boolean parseFunctionCall() {
        final PsiBuilder.Marker functionCallMarker = mBuilder.mark();
        if (parseQName(XQueryElementType.QNAME)) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
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

                    matchTokenType(XQueryTokenType.WHITE_SPACE);
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
        boolean parsed = matchTokenType(XQueryTokenType.WHITE_SPACE);
        while (parseQName(XQueryElementType.QNAME)) {
            parsed = true;

            matchTokenType(XQueryTokenType.WHITE_SPACE);
            if (!matchTokenType(XQueryTokenType.EQUAL) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", "="));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.WHITE_SPACE);
            if (!parseStringLiteral(XQueryElementType.DIR_ATTRIBUTE_VALUE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected-attribute-string"));
                haveErrors = true;
            }

            matchTokenType(XQueryTokenType.WHITE_SPACE);
        }

        if (parsed) {
            attributeListMarker.done(XQueryElementType.DIR_ATTRIBUTE_LIST);
            return true;
        }

        attributeListMarker.drop();
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
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
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
                    error(XQueryBundle.message("parser.error.expected-qname-or-token", "{"));
                    haveErrors = true;
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
                    error(XQueryBundle.message("parser.error.expected-qname-or-token", "{"));
                    haveErrors = true;
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
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
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
                error(XQueryBundle.message("parser.error.expected", "{"));
                haveErrors = true;
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
                    error(XQueryBundle.message("parser.error.expected-qname-or-token", "{"));
                    haveErrors = true;
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
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
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

        error(XQueryBundle.message("parser.error.expected-qname-or-keyword", "comment, empty-sequence, item, node, text"));
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
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
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
            || parseAnyKindTest();
    }

    private boolean parseAnyKindTest() {
        final PsiBuilder.Marker anyKindTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_NODE);
        if (anyKindTestMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
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
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseElementTest() || parseSchemaElementTest()) {
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
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
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            textTestMarker.done(XQueryElementType.TEXT_TEST);
            return true;
        }
        return false;
    }

    private boolean parseCommentTest() {
        final PsiBuilder.Marker textTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_COMMENT);
        if (textTestMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
                error(XQueryBundle.message("parser.error.expected", ")"));
            }

            textTestMarker.done(XQueryElementType.COMMENT_TEST);
            return true;
        }
        return false;
    }

    private boolean parsePITest() {
        final PsiBuilder.Marker piTestMarker = matchTokenTypeWithMarker(XQueryTokenType.K_PROCESSING_INSTRUCTION);
        if (piTestMarker != null) {
            boolean haveErrors = false;

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_OPEN)) {
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseQName(XQueryElementType.NCNAME) || parseStringLiteral(XQueryElementType.STRING_LITERAL)) {
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.PARENTHESIS_CLOSE) && !haveErrors) {
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
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseAttribNameOrWildcard()) {
                skipWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseQName(XQueryElementType.TYPE_NAME) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-qname"));
                        haveErrors = true;
                    }
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    if (!haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", ","));
                        haveErrors = true;
                    }
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
        if (parseQName(XQueryElementType.ATTRIBUTE_NAME) || matchTokenType(XQueryTokenType.STAR)) {
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
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ATTRIBUTE_DECLARATION) && !haveErrors) {
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
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (parseElementNameOrWildcard()) {
                skipWhiteSpaceAndCommentTokens();
                if (matchTokenType(XQueryTokenType.COMMA)) {
                    skipWhiteSpaceAndCommentTokens();
                    if (!parseQName(XQueryElementType.TYPE_NAME) && !haveErrors) {
                        error(XQueryBundle.message("parser.error.expected-qname"));
                        haveErrors = true;
                    }

                    skipWhiteSpaceAndCommentTokens();
                    matchTokenType(XQueryTokenType.OPTIONAL);
                } else if (getTokenType() != XQueryTokenType.PARENTHESIS_CLOSE && getTokenType() != XQueryTokenType.K_EXTERNAL) {
                    if (!haveErrors) {
                        error(XQueryBundle.message("parser.error.expected", ","));
                        haveErrors = true;
                    }
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
        if (parseQName(XQueryElementType.ELEMENT_NAME) || matchTokenType(XQueryTokenType.STAR)) {
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
                error(XQueryBundle.message("parser.error.expected", "("));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseQName(XQueryElementType.ELEMENT_DECLARATION) && !haveErrors) {
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
            } else if (parseEnclosedExpr() ||
                       errorOnTokenType(XQueryTokenType.BLOCK_CLOSE, XQueryBundle.message("parser.error.mismatched-exclosed-expr"))) {
                // NOTE: These are only accessible via DirAttributeValue strings. Other StringLiteral types handle the
                // '{' and '}' characters as part of the string literal contents.
            } else {
                stringMarker.done(type);
                error(XQueryBundle.message("parser.error.incomplete-string"));
                return true;
            }
        }
        return false;
    }

    private boolean parseQName(IElementType type) {
        final PsiBuilder.Marker qnameMarker = mBuilder.mark();
        if (getTokenType() == XQueryTokenType.NCNAME || getTokenType() instanceof IXQueryKeywordOrNCNameType) {
            advanceLexer();

            final PsiBuilder.Marker beforeMarker = mark();
            if (skipWhiteSpaceAndCommentTokens() &&
                getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                beforeMarker.error(XQueryBundle.message("parser.error.qname.whitespace-before-local-part"));
            } else {
                beforeMarker.drop();
            }

            if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                if (type == XQueryElementType.NCNAME) {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.expected-ncname-not-qname"));
                } else {
                    advanceLexer();
                }

                final PsiBuilder.Marker afterMarker = mark();
                if (skipWhiteSpaceAndCommentTokens()) {
                    afterMarker.error(XQueryBundle.message("parser.error.qname.whitespace-after-local-part"));
                } else {
                    afterMarker.drop();
                }

                if (getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
                    error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                } else if (getTokenType() == XQueryTokenType.NCNAME || getTokenType() instanceof IXQueryKeywordOrNCNameType) {
                    advanceLexer();
                } else {
                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                }

                qnameMarker.done(type == XQueryElementType.NCNAME ? XQueryElementType.QNAME : type);
                return true;
            } else {
                qnameMarker.done(type == XQueryElementType.QNAME ? XQueryElementType.NCNAME : type);
            }
            return true;
        }

        if (matchTokenType(XQueryTokenType.QNAME_SEPARATOR)) {
            skipWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.NCNAME || getTokenType() instanceof IXQueryKeywordOrNCNameType) {
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
