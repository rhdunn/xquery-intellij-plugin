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
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

public class PsiBuilderHelper {
    private final PsiBuilder mBuilder;

    public PsiBuilderHelper(@NotNull PsiBuilder builder) {
        mBuilder = builder;
    }

    public boolean skipWhiteSpaceAndCommentTokens() {
        boolean skipped = false;
        while (true) {
            if (mBuilder.getTokenType() == XQueryTokenType.WHITE_SPACE ||
                mBuilder.getTokenType() == XQueryTokenType.COMMENT) {
                skipped = true;
                mBuilder.advanceLexer();
            } else if (mBuilder.getTokenType() == XQueryTokenType.PARTIAL_COMMENT) {
                skipped = true;
                mBuilder.advanceLexer();
                mBuilder.error(XQueryBundle.message("parser.error.incomplete-comment"));
            } else if (mBuilder.getTokenType() == XQueryTokenType.COMMENT_END_TAG) {
                skipped = true;
                final PsiBuilder.Marker errorMarker = mBuilder.mark();
                mBuilder.advanceLexer();
                errorMarker.error(XQueryBundle.message("parser.error.end-of-comment-without-start"));
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
}
