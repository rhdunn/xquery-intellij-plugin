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
package uk.co.reecedunn.intellij.plugin.xquery.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

public class XQueryPairedBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] BRACE_PAIRS = new BracePair[] {
        new BracePair(XQueryTokenType.BLOCK_OPEN, XQueryTokenType.BLOCK_CLOSE, true), // { ... }
        new BracePair(XQueryTokenType.SQUARE_OPEN, XQueryTokenType.SQUARE_CLOSE, false), // [ ... ]
        new BracePair(XQueryTokenType.PARENTHESIS_OPEN, XQueryTokenType.PARENTHESIS_CLOSE, false), // ( ... )
        new BracePair(XQueryTokenType.COMMENT_START_TAG, XQueryTokenType.COMMENT_END_TAG, false), // (: ... :)
        new BracePair(XQueryTokenType.OPEN_XML_TAG, XQueryTokenType.SELF_CLOSING_XML_TAG, false), // < ... />
        new BracePair(XQueryTokenType.OPEN_XML_TAG, XQueryTokenType.END_XML_TAG, false), // < ... >
        new BracePair(XQueryTokenType.CLOSE_XML_TAG, XQueryTokenType.END_XML_TAG, false), // </ ... >
        new BracePair(XQueryTokenType.XML_COMMENT_START_TAG, XQueryTokenType.XML_COMMENT_END_TAG, false), // <!-- ... -->
        new BracePair(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN, XQueryTokenType.PROCESSING_INSTRUCTION_END, false), // <? ... ?>
        new BracePair(XQueryTokenType.CDATA_SECTION_START_TAG, XQueryTokenType.CDATA_SECTION_END_TAG, false), // <![CDATA[ ... ]]>
        new BracePair(XQueryTokenType.PRAGMA_BEGIN, XQueryTokenType.PRAGMA_END, false), // (# ... #)
        new BracePair(XQueryTokenType.STRING_INTERPOLATION_OPEN, XQueryTokenType.STRING_INTERPOLATION_CLOSE, true) // `{ ... }`
    };

    @Override
    public BracePair[] getPairs() {
        return BRACE_PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
