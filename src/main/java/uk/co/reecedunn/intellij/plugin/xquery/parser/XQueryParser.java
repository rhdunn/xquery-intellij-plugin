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

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

public class XQueryParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder psiBuilder) {
        final PsiBuilderHelper builder = new PsiBuilderHelper(psiBuilder);
        final PsiBuilder.Marker rootMarker = builder.mark();
        while (builder.getTokenType() != null) {
            if (builder.skipWhiteSpaceAndCommentTokens()) continue;
            if (parseNumericLiteral(builder)) continue;
            if (parseStringLiteral(builder)) continue;
            if (misplacedEntityReference(builder)) continue;
            if (parseQName(builder)) continue;
            builder.advanceLexer();
        }
        rootMarker.done(root);
        return psiBuilder.getTreeBuilt();
    }

    private boolean parseNumericLiteral(@NotNull PsiBuilderHelper builder) {
        if (builder.matchTokenType(XQueryTokenType.INTEGER_LITERAL) ||
            builder.matchTokenType(XQueryTokenType.DOUBLE_LITERAL)) {
            return true;
        } else if (builder.matchTokenType(XQueryTokenType.DECIMAL_LITERAL)) {
            if (builder.getTokenType() == XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT) {
                final PsiBuilder.Marker errorMarker = builder.mark();
                builder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.incomplete-double-exponent"));
            }
            return true;
        }
        return false;
    }

    private boolean parseStringLiteral(@NotNull PsiBuilderHelper builder) {
        if (builder.getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
            final PsiBuilder.Marker stringMarker = builder.mark();
            builder.advanceLexer();
            while (true) {
                if (builder.matchTokenType(XQueryTokenType.STRING_LITERAL_CONTENTS) ||
                    builder.matchTokenType(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) ||
                    builder.matchTokenType(XQueryTokenType.CHARACTER_REFERENCE) ||
                    builder.matchTokenType(XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER)) {
                    //
                } else if (builder.matchTokenType(XQueryTokenType.STRING_LITERAL_END)) {
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    return true;
                } else if (builder.getTokenType() == XQueryTokenType.PARTIAL_ENTITY_REFERENCE) {
                    final PsiBuilder.Marker errorMarker = builder.mark();
                    builder.advanceLexer();
                    errorMarker.error(XQueryBundle.message("parser.error.incomplete-entity"));
                } else {
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    builder.error(XQueryBundle.message("parser.error.incomplete-string"));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean misplacedEntityReference(@NotNull PsiBuilderHelper builder) {
        if (builder.getTokenType() == XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING) {
            final PsiBuilder.Marker errorMarker = builder.mark();
            builder.advanceLexer();
            errorMarker.error(XQueryBundle.message("parser.error.misplaced-entity"));
        }
        return false;
    }

    private boolean parseQName(@NotNull PsiBuilderHelper builder) {
        if (builder.getTokenType() == XQueryTokenType.NCNAME) {
            final PsiBuilder.Marker qnameMarker = builder.mark();

            builder.advanceLexer();
            final PsiBuilder.Marker beforeMarker = builder.mark();
            if (builder.skipWhiteSpaceAndCommentTokens() &&
                builder.getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
                beforeMarker.error(XQueryBundle.message("parser.error.qname.whitespace-before-local-part"));
            } else {
                beforeMarker.drop();
            }

            if (builder.matchTokenType(XQueryTokenType.QNAME_SEPARATOR)) {
                final PsiBuilder.Marker afterMaker = builder.mark();
                if (builder.skipWhiteSpaceAndCommentTokens()) {
                    afterMaker.error(XQueryBundle.message("parser.error.qname.whitespace-after-local-part"));
                } else {
                    afterMaker.drop();
                }

                if (builder.matchTokenType(XQueryTokenType.NCNAME)) {
                    qnameMarker.done(XQueryElementType.QNAME);
                    return true;
                } else {
                    qnameMarker.drop();

                    final PsiBuilder.Marker errorMaker = builder.mark();
                    builder.advanceLexer();
                    errorMaker.error(XQueryBundle.message("parser.error.qname.missing-local-name"));
                    return true;
                }
            } else {
                qnameMarker.drop();
            }
            return true;
        } else if (builder.getTokenType() == XQueryTokenType.QNAME_SEPARATOR) {
            final PsiBuilder.Marker errorMaker = builder.mark();
            builder.advanceLexer();
            builder.skipWhiteSpaceAndCommentTokens();
            if (builder.getTokenType() == XQueryTokenType.NCNAME) {
                builder.advanceLexer();
            }
            errorMaker.error(XQueryBundle.message("parser.error.qname.missing-prefix"));
            return true;
        }
        return false;
    }
}
