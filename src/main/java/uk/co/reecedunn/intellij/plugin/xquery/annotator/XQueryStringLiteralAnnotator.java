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
package uk.co.reecedunn.intellij.plugin.xquery.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryStringLiteralPsiImpl;

import java.util.HashSet;
import java.util.Set;

public class XQueryStringLiteralAnnotator implements Annotator {
    private static final TokenSet PREDEFINED_ENTITY_REFS = TokenSet.create(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);

    private static final Set<CharSequence> XML_ENTITIES;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryStringLiteralPsiImpl)) return;

        final ASTNode node = element.getNode();
        for (ASTNode child : node.getChildren(PREDEFINED_ENTITY_REFS)) {
            checkPredefinedEntity(child.getChars(), child, holder);
        }
    }

    private void checkPredefinedEntity(@NotNull CharSequence entity, @NotNull ASTNode node, @NotNull AnnotationHolder holder) {
        CharSequence name = entity.subSequence(1, entity.length() - 1);
        if (XML_ENTITIES.contains(name)) return;

        holder.createAnnotation(HighlightSeverity.ERROR, node.getTextRange(), XQueryBundle.message("annotator.string-literal.unknown-xml-entity", entity.toString()));
    }

    static {
        XML_ENTITIES = new HashSet<>();

        XML_ENTITIES.add("amp");
        XML_ENTITIES.add("apos");
        XML_ENTITIES.add("gt");
        XML_ENTITIES.add("lt");
        XML_ENTITIES.add("quot");
    }
}
