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
                builder.error(XQueryBundle.message("parser.error.end-of-comment-without-start"));
                builder.advanceLexer();
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
                builder.error(XQueryBundle.message("parser.error.incomplete-double-exponent"));
                builder.advanceLexer();
            }
            return true;
        }
        return false;
    }
}
