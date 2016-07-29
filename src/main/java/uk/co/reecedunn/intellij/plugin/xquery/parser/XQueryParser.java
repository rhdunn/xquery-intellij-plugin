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
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

public class XQueryParser {
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
            if (parseVersionDecl()) continue;
            if (parseModuleDecl()) continue;
            if (parseModuleImport()) continue;
            if (parseRangeExpr()) continue;
            if (parseQName()) continue;
            if (parseDirCommentConstructor()) continue;
            if (parseCDataSection()) continue;
            advanceLexer();
        }
    }

    // endregion
    // region Parser Helper Methods

    public boolean skipWhiteSpaceAndCommentTokens() {
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

    public boolean matchTokenType(IElementType type) {
        if (mBuilder.getTokenType() == type) {
            mBuilder.advanceLexer();
            return true;
        }
        return false;
    }

    public boolean errorOnTokenType(IElementType type, String message) {
        if (mBuilder.getTokenType() == type) {
            final PsiBuilder.Marker errorMarker = mBuilder.mark();
            mBuilder.advanceLexer();
            errorMarker.error(message);
            return true;
        }
        return false;
    }

    public PsiBuilder.Marker mark() {
        return mBuilder.mark();
    }

    public IElementType getTokenType() {
        return mBuilder.getTokenType();
    }

    public void advanceLexer() {
        mBuilder.advanceLexer();
    }

    public void error(String message) {
        mBuilder.error(message);
    }

    // endregion
    // region Grammar

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

    private boolean parsePrimaryExpr() {
        return parseLiteral();
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

    private boolean parseStringLiteral(IElementType type) {
        if (getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
            final PsiBuilder.Marker stringMarker = mark();
            advanceLexer();
            while (true) {
                if (matchTokenType(XQueryTokenType.STRING_LITERAL_CONTENTS) ||
                    matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                    matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                    matchTokenType(XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER)) {
                    //
                } else if (matchTokenType(XQueryTokenType.STRING_LITERAL_END)) {
                    stringMarker.done(type);
                    return true;
                } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                    error(XQueryBundle.message("parser.error.incomplete-entity"));
                } else if (!errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity"))) {
                    stringMarker.done(type);
                    error(XQueryBundle.message("parser.error.incomplete-string"));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean parseQName() {
        if (getTokenType() == XQueryTokenType.NCNAME) {
            final PsiBuilder.Marker qnameMarker = mark();

            advanceLexer();
            final PsiBuilder.Marker beforeMarker = mark();
            if (skipWhiteSpaceAndCommentTokens() &&
                getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                beforeMarker.error(XQueryBundle.message("parser.error.qname.whitespace-before-local-part"));
            } else {
                beforeMarker.drop();
            }

            if (matchTokenType(XQueryTokenType.QNAME_SEPARATOR)) {
                final PsiBuilder.Marker afterMarker = mark();
                if (skipWhiteSpaceAndCommentTokens()) {
                    afterMarker.error(XQueryBundle.message("parser.error.qname.whitespace-after-local-part"));
                } else {
                    afterMarker.drop();
                }

                if (matchTokenType(XQueryTokenType.NCNAME)) {
                    qnameMarker.done(XQueryElementType.QNAME);
                    return true;
                } else {
                    qnameMarker.drop();

                    final PsiBuilder.Marker errorMarker = mark();
                    advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                    return true;
                }
            } else {
                qnameMarker.drop();
            }
            return true;
        } else if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
            final PsiBuilder.Marker errorMarker = mark();
            advanceLexer();
            skipWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.NCNAME) {
                advanceLexer();
            }
            errorMarker.error(XQueryBundle.message("parser.error.qname.missing-prefix"));
            return true;
        }
        return false;
    }

    private boolean parseVersionDecl() {
        if (getTokenType() == XQueryTokenType.K_XQUERY) {
            boolean haveErrors = false;
            final PsiBuilder.Marker versionDeclMarker = mark();
            advanceLexer();

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
                    error(XQueryBundle.message("parser.error.expected-semicolon"));
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

    private boolean parseModuleDecl() {
        if (getTokenType() == XQueryTokenType.K_MODULE) {
            boolean haveErrors = false;
            final PsiBuilder.Marker moduleDeclMarker = mark();
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.NCNAME) && !haveErrors) {
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
                    error(XQueryBundle.message("parser.error.expected-semicolon"));
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

    private boolean parseModuleImport() {
        if (getTokenType() == XQueryTokenType.K_IMPORT) {
            boolean haveErrors = false;
            final PsiBuilder.Marker moduleImportMarker = mark();
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_MODULE)) {
                error(XQueryBundle.message("parser.error.expected-keyword", "module"));
                haveErrors = true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                skipWhiteSpaceAndCommentTokens();
                if (!matchTokenType(XQueryTokenType.NCNAME) && !haveErrors) {
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

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                moduleImportMarker.done(XQueryElementType.MODULE_IMPORT);
                if (!haveErrors) {
                    error(XQueryBundle.message("parser.error.expected-semicolon"));
                }
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
                return true;
            }

            moduleImportMarker.done(XQueryElementType.MODULE_IMPORT);
            return true;
        }
        return false;
    }

    private boolean parseDirCommentConstructor() {
        if (getTokenType() == XQueryTokenType.XML_COMMENT_START_TAG) {
            final PsiBuilder.Marker commentMarker = mark();
            advanceLexer();
            // NOTE: XQueryTokenType.XML_COMMENT is omitted by the PsiBuilder.
            if (matchTokenType(XQueryTokenType.XML_COMMENT_END_TAG)) {
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
                error(XQueryBundle.message("parser.error.incomplete-xml-comment"));
            }
            return true;
        } else if (errorOnTokenType(XQueryTokenType.XML_COMMENT_END_TAG, XQueryBundle.message("parser.error.end-of-comment-without-start", "<!--")) ||
                   errorOnTokenType(XQueryTokenType.INVALID, XQueryBundle.message("parser.error.invalid-token"))) {
            return true;
        }
        return false;
    }

    private boolean parseCDataSection() {
        if (getTokenType() == XQueryTokenType.CDATA_SECTION_START_TAG) {
            final PsiBuilder.Marker cdataMarker = mark();
            advanceLexer();
            matchTokenType(XQueryTokenType.CDATA_SECTION);
            if (matchTokenType(XQueryTokenType.CDATA_SECTION_END_TAG)) {
                cdataMarker.done(XQueryElementType.CDATA_SECTION);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                cdataMarker.done(XQueryElementType.CDATA_SECTION);
                error(XQueryBundle.message("parser.error.incomplete-cdata-section"));
            }
            return true;
        } else if (errorOnTokenType(XQueryTokenType.CDATA_SECTION_END_TAG, XQueryBundle.message("parser.error.end-of-cdata-section-without-start"))) {
            return true;
        }
        return false;
    }

    // endregion
}
