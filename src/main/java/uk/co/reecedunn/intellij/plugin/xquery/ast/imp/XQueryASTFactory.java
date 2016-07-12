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
package uk.co.reecedunn.intellij.plugin.xquery.ast.imp;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;

public class XQueryASTFactory extends ASTFactory {
    @Override
    @Nullable
    public CompositeElement createComposite(final IElementType type) {
        if (type instanceof IFileElementType) {
            return new FileElement(type, null);
        } else if (type == XQueryElementType.STRING_LITERAL) {
            return new XQueryStringLiteralImpl(type);
        }

        return new CompositeElement(type);
    }

    @Override
    @Nullable
    public LeafElement createLeaf(@NotNull final IElementType type, @NotNull CharSequence text) {
        if (type == XQueryTokenType.COMMENT ||
            type == XQueryTokenType.PARTIAL_COMMENT) {
            return new PsiCommentImpl(type, text);
        } else if (type == XQueryTokenType.INTEGER_LITERAL ||
                   type == XQueryTokenType.DECIMAL_LITERAL ||
                   type == XQueryTokenType.DOUBLE_LITERAL) {
            return new XQueryNumericLiteralImpl(type, text);
        } else if (type == XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) {
            return new XQueryPredefinedEntityRefImpl(type, text);
        } else if (type == XQueryTokenType.CHARACTER_REFERENCE) {
            return new XQueryCharRefImpl(type, text);
        } else if (type == XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER) {
            return new XQueryEscapeCharacterImpl(type, text);
        }

        return new LeafPsiElement(type, text);
    }
}
