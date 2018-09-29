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
package uk.co.reecedunn.intellij.plugin.xpath.tests.annotator

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.AnnotationSession
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.*

internal class AnnotationCollector : AnnotationHolder {
    val annotations: MutableList<Annotation> = ArrayList()

    override fun createErrorAnnotation(element: PsiElement, message: String?): Annotation {
        return createErrorAnnotation(element.node, message)
    }

    override fun createErrorAnnotation(node: ASTNode, message: String?): Annotation {
        return createErrorAnnotation(node.textRange, message)
    }

    override fun createErrorAnnotation(range: TextRange, message: String?): Annotation {
        return createAnnotation(HighlightSeverity.ERROR, range, message, null)
    }

    override fun createWarningAnnotation(element: PsiElement, message: String?): Annotation {
        return createWarningAnnotation(element.node, message)
    }

    override fun createWarningAnnotation(node: ASTNode, message: String?): Annotation {
        return createWarningAnnotation(node.textRange, message)
    }

    override fun createWarningAnnotation(range: TextRange, message: String?): Annotation {
        return createAnnotation(HighlightSeverity.WARNING, range, message, null)
    }

    override fun createWeakWarningAnnotation(element: PsiElement, message: String?): Annotation {
        return createWeakWarningAnnotation(element.node, message)
    }

    override fun createWeakWarningAnnotation(node: ASTNode, message: String?): Annotation {
        return createWeakWarningAnnotation(node.textRange, message)
    }

    override fun createWeakWarningAnnotation(range: TextRange, message: String?): Annotation {
        return createAnnotation(HighlightSeverity.WEAK_WARNING, range, message, null)
    }

    override fun createInfoAnnotation(element: PsiElement, message: String?): Annotation {
        return createInfoAnnotation(element.node, message)
    }

    override fun createInfoAnnotation(node: ASTNode, message: String?): Annotation {
        return createInfoAnnotation(node.textRange, message)
    }

    override fun createInfoAnnotation(range: TextRange, message: String?): Annotation {
        return createAnnotation(HighlightSeverity.INFORMATION, range, message, null)
    }

    override fun createAnnotation(severity: HighlightSeverity, range: TextRange, message: String?): Annotation {
        return createAnnotation(severity, range, message, null)
    }

    override fun createAnnotation(severity: HighlightSeverity, range: TextRange, message: String?, htmlTooltip: String?): Annotation {
        val annotation = Annotation(range.startOffset, range.endOffset, severity, message, htmlTooltip)
        annotations.add(annotation)
        return annotation
    }

    override fun getCurrentAnnotationSession(): AnnotationSession = TODO()

    override fun isBatchMode(): Boolean = false
}
