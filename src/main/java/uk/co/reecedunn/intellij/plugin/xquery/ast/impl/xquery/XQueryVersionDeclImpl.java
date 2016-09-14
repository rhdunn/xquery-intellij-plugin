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
package uk.co.reecedunn.intellij.plugin.xquery.ast.impl.xquery;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;

public class XQueryVersionDeclImpl extends CompositeElement {
    private static final TokenSet STRINGS = TokenSet.create(XQueryElementType.STRING_LITERAL);

    public XQueryVersionDeclImpl(@NotNull IElementType type) {
        super(type);
    }

    private @Nullable XQueryStringLiteral getStringValueAfterKeyword(IXQueryKeywordOrNCNameType type) {
        for (ASTNode child : getChildren(STRINGS)) {
            ASTNode previous = child.getTreePrev();
            while (previous.getElementType() == XQueryTokenType.WHITE_SPACE || previous.getElementType() == XQueryElementType.COMMENT) {
                previous = previous.getTreePrev();
            }

            if (previous.getElementType() == type) {
                return (XQueryStringLiteral)child.getPsi();
            } else if (previous instanceof PsiErrorElementImpl) {
                if (previous.getFirstChildNode().getElementType() == type) {
                    return (XQueryStringLiteral)child.getPsi();
                }
            }
        }
        return null;
    }

    public @Nullable XQueryStringLiteral getVersion() {
        return getStringValueAfterKeyword(XQueryTokenType.K_VERSION);
    }

    public @Nullable XQueryStringLiteral getEncoding() {
        return getStringValueAfterKeyword(XQueryTokenType.K_ENCODING);
    }
}
