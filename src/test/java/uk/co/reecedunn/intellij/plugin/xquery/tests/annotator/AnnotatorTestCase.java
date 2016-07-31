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
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import java.util.List;

public abstract class AnnotatorTestCase extends ParserTestCase {
    private void annotateTree(@NotNull ASTNode node, @NotNull AnnotationHolder annotationHolder, @NotNull Annotator annotator) {
        if (node instanceof CompositeElement) {
            CompositeElement element = (CompositeElement)node;
            annotator.annotate(element.getPsi(), annotationHolder);
        } else if (node instanceof LeafPsiElement) {
            annotator.annotate((LeafPsiElement)node, annotationHolder);
        }

        for (ASTNode child : node.getChildren(null)) {
            annotateTree(child, annotationHolder, annotator);
        }
    }

    List<Annotation> annotateTree(@NotNull ASTNode node, @NotNull Annotator annotator) {
        AnnotationCollector annotationHolder = new AnnotationCollector();
        annotateTree(node, annotationHolder, annotator);
        return annotationHolder.annotations;
    }
}
