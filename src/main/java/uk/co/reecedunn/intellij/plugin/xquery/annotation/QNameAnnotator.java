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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQName;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

public class QNameAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryQName)) return;
        XQueryQName qname = (XQueryQName)element;

        PsiElement prefix = qname.getPrefix();
        if (prefix != null) {
            Annotation annotation = holder.createAnnotation(HighlightSeverity.INFORMATION, prefix.getTextRange(), null);
            annotation.setTextAttributes(SyntaxHighlighter.NS_PREFIX);
        }

        PsiElement localname = qname.getLocalName();
        if (localname != null) {
            if (localname.getNode().getElementType() instanceof IXQueryKeywordOrNCNameType) {
                Annotation annotation = holder.createAnnotation(HighlightSeverity.INFORMATION, localname.getTextRange(), null);
                annotation.setTextAttributes(SyntaxHighlighter.IDENTIFIER);
            }
        }
    }
}
