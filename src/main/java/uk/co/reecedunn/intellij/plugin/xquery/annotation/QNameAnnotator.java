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

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotation;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryBracedURILiteral;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

public class QNameAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryEQName)) return;
        XQueryEQName qname = (XQueryEQName)element;

        boolean xmlns = false;
        PsiElement prefix = qname.getPrefix();
        if (prefix != null && !(prefix instanceof XQueryBracedURILiteral)) {
            if (prefix.getText().equals("xmlns")) {
                xmlns = true;
            } else {
                holder.createInfoAnnotation(prefix, null).setTextAttributes(SyntaxHighlighter.NS_PREFIX);
            }
        }

        PsiElement localname = qname.getLocalName();
        if (localname != null) {
            if (xmlns) {
                holder.createInfoAnnotation(localname, null).setTextAttributes(SyntaxHighlighter.NS_PREFIX);
            } else if (qname.getParent() instanceof XQueryAnnotation) {
                holder.createInfoAnnotation(localname, null).setTextAttributes(SyntaxHighlighter.ANNOTATION);
            } else if (localname.getNode().getElementType() instanceof IXQueryKeywordOrNCNameType) {
                holder.createInfoAnnotation(localname, null).setTextAttributes(SyntaxHighlighter.IDENTIFIER);
            }
        }
    }
}
