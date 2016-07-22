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
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryParserImpl extends XQueryParserBase {
    public XQueryParserImpl(@NotNull PsiBuilder builder) {
        super(builder);
    }

    public void parse() {
        while (getTokenType() != null) {
            if (skipWhiteSpaceAndCommentTokens()) continue;
            if (parseVersionDecl()) continue;
            if (parseModuleDecl()) continue;
            if (parseNumericLiteral()) continue;
            if (parseStringLiteral()) continue;
            if (misplacedEntityReference()) continue;
            if (parseQName()) continue;
            if (parseDirCommentConstructor()) continue;
            if (parseCDataSection()) continue;
            advanceLexer();
        }
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

    private boolean parseStringLiteral() {
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
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    return true;
                } else if (matchTokenType(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)) {
                    error(XQueryBundle.message("parser.error.incomplete-entity"));
                } else if (!errorOnTokenType(XQueryTokenType.EMPTY_ENTITY_REFERENCE, XQueryBundle.message("parser.error.empty-entity"))) {
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    error(XQueryBundle.message("parser.error.incomplete-string"));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean misplacedEntityReference() {
        return errorOnTokenType(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING, XQueryBundle.message("parser.error.misplaced-entity"));
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
                final PsiBuilder.Marker afterMaker = mark();
                if (skipWhiteSpaceAndCommentTokens()) {
                    afterMaker.error(XQueryBundle.message("parser.error.qname.whitespace-after-local-part"));
                } else {
                    afterMaker.drop();
                }

                if (matchTokenType(XQueryTokenType.NCNAME)) {
                    qnameMarker.done(XQueryElementType.QNAME);
                    return true;
                } else {
                    qnameMarker.drop();

                    final PsiBuilder.Marker errorMaker = mark();
                    advanceLexer();
                    errorMaker.error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                    return true;
                }
            } else {
                qnameMarker.drop();
            }
            return true;
        } else if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
            final PsiBuilder.Marker errorMaker = mark();
            advanceLexer();
            skipWhiteSpaceAndCommentTokens();
            if (getTokenType() == XQueryTokenType.NCNAME) {
                advanceLexer();
            }
            errorMaker.error(XQueryBundle.message("parser.error.qname.missing-prefix"));
            return true;
        }
        return false;
    }

    private boolean parseVersionDecl() {
        if (getTokenType() == XQueryTokenType.K_XQUERY) {
            final PsiBuilder.Marker versionDeclMaker = mark();
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_VERSION)) {
                versionDeclMaker.done(XQueryElementType.VERSION_DECL);
                error(XQueryBundle.message("parser.error.expected-keyword", "version"));
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral()) {
                versionDeclMaker.done(XQueryElementType.VERSION_DECL);
                error(XQueryBundle.message("parser.error.expected-version-string"));
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (matchTokenType(XQueryTokenType.K_ENCODING)) {
                skipWhiteSpaceAndCommentTokens();
                if (!parseStringLiteral()) {
                    versionDeclMaker.done(XQueryElementType.VERSION_DECL);
                    error(XQueryBundle.message("parser.error.expected-encoding-string"));
                    return true;
                }

                skipWhiteSpaceAndCommentTokens();
            }

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                versionDeclMaker.done(XQueryElementType.VERSION_DECL);
                error(XQueryBundle.message("parser.error.expected-semicolon"));
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
                return true;
            }

            versionDeclMaker.done(XQueryElementType.VERSION_DECL);
            return true;
        }
        return false;
    }

    private boolean parseModuleDecl() {
        if (getTokenType() == XQueryTokenType.K_MODULE) {
            final PsiBuilder.Marker moduleDeclMaker = mark();
            advanceLexer();

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.K_NAMESPACE)) {
                moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
                error(XQueryBundle.message("parser.error.expected-keyword", "namespace"));
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.NCNAME)) {
                moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
                error(XQueryBundle.message("parser.error.expected-ncname"));
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!matchTokenType(XQueryTokenType.EQUAL)) {
                moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
                error(XQueryBundle.message("parser.error.expected", "="));
                return true;
            }

            skipWhiteSpaceAndCommentTokens();
            if (!parseStringLiteral()) {
                moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
                error(XQueryBundle.message("parser.error.expected-url-string"));
                return true;
            }

            if (!matchTokenType(XQueryTokenType.SEPARATOR)) {
                moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
                error(XQueryBundle.message("parser.error.expected-semicolon"));
                if (getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                    advanceLexer();
                }
                return true;
            }

            moduleDeclMaker.done(XQueryElementType.MODULE_DECL);
            return true;
        }
        return false;
    }

    private boolean parseDirCommentConstructor() {
        if (getTokenType() == XQueryTokenType.XML_COMMENT_START_TAG) {
            final PsiBuilder.Marker commentMaker = mark();
            advanceLexer();
            // NOTE: XQueryTokenType.XML_COMMENT is omitted by the PsiBuilder.
            if (matchTokenType(XQueryTokenType.XML_COMMENT_END_TAG)) {
                commentMaker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMaker.done(XQueryElementType.DIR_COMMENT_CONSTRUCTOR);
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
            final PsiBuilder.Marker commentMaker = mark();
            advanceLexer();
            matchTokenType(XQueryTokenType.CDATA_SECTION);
            if (matchTokenType(XQueryTokenType.CDATA_SECTION_END_TAG)) {
                commentMaker.done(XQueryElementType.CDATA_SECTION);
            } else {
                advanceLexer(); // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMaker.done(XQueryElementType.CDATA_SECTION);
                error(XQueryBundle.message("parser.error.incomplete-cdata-section"));
            }
            return true;
        } else if (errorOnTokenType(XQueryTokenType.CDATA_SECTION_END_TAG, XQueryBundle.message("parser.error.end-of-cdata-section-without-start"))) {
            return true;
        }
        return false;
    }
}
