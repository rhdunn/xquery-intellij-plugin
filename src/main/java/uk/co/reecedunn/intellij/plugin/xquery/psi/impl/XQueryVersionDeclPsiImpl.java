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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionDeclPsi;

public class XQueryVersionDeclPsiImpl extends ASTWrapperPsiElement implements XQueryVersionDeclPsi {
    private static final TokenSet STRINGS = TokenSet.create(XQueryElementType.STRING_LITERAL);

    public XQueryVersionDeclPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    private CharSequence getStringValueAfterKeyword(IXQueryKeywordOrNCNameType type) {
        for (ASTNode child : getNode().getChildren(STRINGS)) {
            ASTNode previous = child.getTreePrev();
            while (previous.getElementType() == XQueryTokenType.WHITE_SPACE || previous.getElementType() == XQueryElementType.COMMENT) {
                previous = previous.getTreePrev();
            }

            if (previous.getElementType() == type) {
                ASTNode value = child.findChildByType(XQueryTokenType.STRING_LITERAL_CONTENTS);
                if (value == null) {
                    return null;
                }
                return value.getChars();
            }
        }
        return null;
    }

    public CharSequence getVersion() {
        return getStringValueAfterKeyword(XQueryTokenType.K_VERSION);
    }

    public CharSequence getEncoding() {
        return getStringValueAfterKeyword(XQueryTokenType.K_ENCODING);
    }
}
