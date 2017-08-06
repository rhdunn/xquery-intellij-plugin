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
package uk.co.reecedunn.intellij.plugin.xquery.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

public class QNameAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryEQName)) return;
        if (element.getParent() instanceof XQueryEQName) return;

        XQueryEQName qname = (XQueryEQName)element;

        final boolean xmlns;
        PsiElement prefix = qname.getPrefix();
        if (prefix != null && !(prefix instanceof XQueryBracedURILiteral)) {
            if (prefix.getText().equals("xmlns")) {
                xmlns = true;
            } else {
                xmlns = false;
                holder.createInfoAnnotation(prefix, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
                if ((element.getParent() instanceof XQueryDirAttributeList) || (element.getParent() instanceof XQueryDirElemConstructor)) {
                    holder.createInfoAnnotation(prefix, null).setTextAttributes(SyntaxHighlighter.XML_TAG);
                }
                holder.createInfoAnnotation(prefix, null).setTextAttributes(SyntaxHighlighter.NS_PREFIX);
            }
        } else {
            xmlns = false;
        }

        qname.getLocalName().map((localName) -> {
            if (xmlns) {
                holder.createInfoAnnotation(localName, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
                if (element.getParent() instanceof XQueryDirAttributeList) {
                    holder.createInfoAnnotation(localName, null).setTextAttributes(SyntaxHighlighter.XML_TAG);
                }
                holder.createInfoAnnotation(localName, null).setTextAttributes(SyntaxHighlighter.NS_PREFIX);
            } else if (qname.getParent() instanceof XQueryAnnotation) {
                holder.createInfoAnnotation(localName, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
                holder.createInfoAnnotation(localName, null).setTextAttributes(SyntaxHighlighter.ANNOTATION);
            } else if (localName.getNode().getElementType() instanceof IXQueryKeywordOrNCNameType) {
                holder.createInfoAnnotation(localName, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
                holder.createInfoAnnotation(localName, null).setTextAttributes(SyntaxHighlighter.IDENTIFIER);
            } else if (localName instanceof XQueryNCName) {
                if (((XQueryNCName)localName).getLocalName().map((name) -> name.getNode().getElementType()).getOrElse(null) instanceof IXQueryKeywordOrNCNameType) {
                    holder.createInfoAnnotation(localName, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
                    holder.createInfoAnnotation(localName, null).setTextAttributes(SyntaxHighlighter.IDENTIFIER);
                }
            }
            return Option.none();
        });
    }
}
