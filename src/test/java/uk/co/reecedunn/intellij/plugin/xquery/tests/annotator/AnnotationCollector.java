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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class AnnotationCollector implements AnnotationHolder {
    public final List<Annotation> annotations = new ArrayList<>();

    @Override
    public Annotation createErrorAnnotation(@NotNull PsiElement element, @Nullable String message) {
        return createErrorAnnotation(element.getNode(), message);
    }

    @Override
    public Annotation createErrorAnnotation(@NotNull ASTNode node, @Nullable String message) {
        return createErrorAnnotation(node.getTextRange(), message);
    }

    @Override
    public Annotation createErrorAnnotation(@NotNull TextRange range, @Nullable String message) {
        return createAnnotation(HighlightSeverity.ERROR, range, message, null);
    }

    @Override
    public Annotation createWarningAnnotation(@NotNull PsiElement element, @Nullable String message) {
        return createWarningAnnotation(element.getNode(), message);
    }

    @Override
    public Annotation createWarningAnnotation(@NotNull ASTNode node, @Nullable String message) {
        return createWarningAnnotation(node.getTextRange(), message);
    }

    @Override
    public Annotation createWarningAnnotation(@NotNull TextRange range, @Nullable String message) {
        return createAnnotation(HighlightSeverity.WARNING, range, message, null);
    }

    @Override
    public Annotation createWeakWarningAnnotation(@NotNull PsiElement element, @Nullable String message) {
        return createWeakWarningAnnotation(element.getNode(), message);
    }

    @Override
    public Annotation createWeakWarningAnnotation(@NotNull ASTNode node, @Nullable String message) {
        return createWeakWarningAnnotation(node.getTextRange(), message);
    }

    @Override
    public Annotation createWeakWarningAnnotation(@NotNull TextRange range, @Nullable String message) {
        return createAnnotation(HighlightSeverity.WEAK_WARNING, range, message, null);
    }

    @Override
    public Annotation createInfoAnnotation(@NotNull PsiElement element, @Nullable String message) {
        return createInfoAnnotation(element.getNode(), message);
    }

    @Override
    public Annotation createInfoAnnotation(@NotNull ASTNode node, @Nullable String message) {
        return createInfoAnnotation(node.getTextRange(), message);
    }

    @Override
    public Annotation createInfoAnnotation(@NotNull TextRange range, @Nullable String message) {
        return createAnnotation(HighlightSeverity.INFORMATION, range, message, null);
    }

    @Override
    public Annotation createAnnotation(@NotNull HighlightSeverity severity, @NotNull TextRange range, @Nullable String message) {
        return createAnnotation(severity, range, message, null);
    }

    @Override
    public Annotation createAnnotation(@NotNull HighlightSeverity severity, @NotNull TextRange range, @Nullable String message, @Nullable String htmlTooltip) {
        Annotation annotation = new Annotation(range.getStartOffset(), range.getEndOffset(), severity, message, htmlTooltip);
        annotations.add(annotation);
        return annotation;
    }

    @NotNull
    @Override
    public AnnotationSession getCurrentAnnotationSession() {
        return null;
    }

    @Override
    public boolean isBatchMode() {
        return false;
    }
}
