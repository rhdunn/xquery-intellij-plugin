/*
 * Copyright (C) 2017 Reece H. Dunn
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

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEnclosedExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;

import java.util.ArrayList;
import java.util.List;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.skipQName;

public class XQueryFoldingBuilder extends FoldingBuilderEx {
    private TextRange getDirElemConstructorRange(PsiElement element) {
        ASTNode contents = element.getNode().findChildByType(XQueryElementType.DIR_ELEM_CONTENT);
        if (contents != null) {
            ASTNode first = contents.getFirstChildNode();
            ASTNode last = contents.getLastChildNode();
            if (first == last && first.getElementType() == XQueryElementType.ENCLOSED_EXPR) {
                // The folding is applied to the EnclosedExpr, not the DirElemConstructor.
                return null;
            }
        }

        PsiElement start = element.getFirstChild();
        if (start.getNode().getElementType() == XQueryTokenType.OPEN_XML_TAG)
            start = start.getNextSibling();
        if (start.getNode().getElementType() == XQueryTokenType.XML_WHITE_SPACE)
            start = start.getNextSibling();
        start = skipQName(start);

        PsiElement end = element.getLastChild();
        if (end.getNode().getElementType() == XQueryTokenType.CLOSE_XML_TAG ||
                end.getNode().getElementType() == XQueryTokenType.SELF_CLOSING_XML_TAG) {
            end = end.getPrevSibling();
        }

        return new TextRange(start.getTextRange().getEndOffset(), end.getTextRange().getStartOffset());
    }

    private TextRange getRange(PsiElement element) {
        if (!element.textContains('\n')) {
            return null;
        }

        if (element instanceof XQueryEnclosedExpr) {
            return element.getTextRange();
        } else if (element instanceof XQueryDirElemConstructor) {
            return getDirElemConstructorRange(element);
        }
        return null;
    }

    private void createFoldRegions(PsiElement element, List<FoldingDescriptor> descriptors) {
        children(element).each((child) -> {
            TextRange range = getRange(child);
            if (range != null) {
                descriptors.add(new FoldingDescriptor(child, range));
            }
            createFoldRegions(child, descriptors);
        });
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        createFoldRegions(root, descriptors);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        if (element instanceof XQueryEnclosedExpr) {
            return "{...}";
        }
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
