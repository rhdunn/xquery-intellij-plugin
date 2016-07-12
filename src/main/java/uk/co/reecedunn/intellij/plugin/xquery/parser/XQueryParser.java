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
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        final PsiBuilder.Marker rootMarker = builder.mark();
        while (builder.getTokenType() != null) {
            if (skipWhiteSpaceAndCommentTokens(builder)) continue;
            if (parseNumericLiteral(builder)) continue;
            if (parseStringLiteral(builder)) continue;
            builder.advanceLexer();
        }
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private boolean skipWhiteSpaceAndCommentTokens(@NotNull PsiBuilder builder) {
        boolean skipped = false;
        while (true) {
            if (builder.getTokenType() == XQueryTokenType.WHITE_SPACE ||
                builder.getTokenType() == XQueryTokenType.COMMENT) {
                skipped = true;
                builder.advanceLexer();
            } else if (builder.getTokenType() == XQueryTokenType.PARTIAL_COMMENT) {
                skipped = true;
                builder.advanceLexer();
                builder.error(XQueryBundle.message("parser.error.incomplete-comment"));
            } else if (builder.getTokenType() == XQueryTokenType.COMMENT_END_TAG) {
                skipped = true;
                final PsiBuilder.Marker errorMarker = builder.mark();
                builder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.end-of-comment-without-start"));
            } else {
                return skipped;
            }
        }
    }

    private boolean parseNumericLiteral(@NotNull PsiBuilder builder) {
        if (builder.getTokenType() == XQueryTokenType.INTEGER_LITERAL ||
            builder.getTokenType() == XQueryTokenType.DOUBLE_LITERAL) {
            builder.advanceLexer();
            return true;
        } else if (builder.getTokenType() == XQueryTokenType.DECIMAL_LITERAL) {
            builder.advanceLexer();
            if (builder.getTokenType() == XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT) {
                final PsiBuilder.Marker errorMarker = builder.mark();
                builder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.incomplete-double-exponent"));
            }
            return true;
        }
        return false;
    }

    private boolean parseStringLiteral(@NotNull PsiBuilder builder) {
        if (builder.getTokenType() == XQueryTokenType.STRING_LITERAL_START) {
            final PsiBuilder.Marker stringMarker = builder.mark();
            builder.advanceLexer();
            while (true) {
                if (builder.getTokenType() == XQueryTokenType.STRING_LITERAL_CONTENTS) {
                    builder.advanceLexer();
                } else if (builder.getTokenType() == XQueryTokenType.STRING_LITERAL_END) {
                    builder.advanceLexer();
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    return true;
                } else {
                    stringMarker.done(XQueryElementType.STRING_LITERAL);
                    builder.error(XQueryBundle.message("parser.error.incomplete-string"));
                    return true;
                }
            }
        }
        return false;
    }
}
