/*
 * Copyright (C) 2016-2021 Reece H. Dunn
 * Copyright 2000-2019 JetBrains s.r.o.
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
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.Annotation
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat

@Suppress("SameParameterValue")
abstract class AnnotatorTestCase<File : PsiFile>(
    fileExt: String?,
    vararg definitions: ParserDefinition
) : ParsingTestCase<File>(fileExt, *definitions) {
    @Suppress("UnstableApiUsage")
    private fun annotateTree(node: ASTNode, annotationHolder: AnnotationHolder, annotator: Annotator) {
        if (node is CompositeElement) {
            (annotationHolder as AnnotationHolderImpl).runAnnotatorWithContext(node.psi, annotator)
        } else if (node is LeafPsiElement) {
            (annotationHolder as AnnotationHolderImpl).runAnnotatorWithContext(node, annotator)
        }

        for (child in node.getChildren(null)) {
            annotateTree(child, annotationHolder, annotator)
        }
    }

    @Suppress("UnstableApiUsage", "DEPRECATION")
    protected fun annotateTree(file: File, annotator: Annotator): List<Annotation> {
        val annotationHolder = AnnotationHolderImpl(AnnotationSession(file), false)
        annotateTree(file.node, annotationHolder, annotator)
        return annotationHolder
    }

    fun info(
        annotation: Annotation, start: Int, end: Int, enforced: TextAttributes?, attributes: TextAttributesKey
    ) {
        assertThat(annotation.severity, CoreMatchers.`is`(HighlightSeverity.INFORMATION))
        assertThat(annotation.startOffset, CoreMatchers.`is`(start))
        assertThat(annotation.endOffset, CoreMatchers.`is`(end))
        assertThat(annotation.message, CoreMatchers.`is`(CoreMatchers.nullValue()))
        if (enforced != null)
            assertThat(annotation.enforcedTextAttributes, CoreMatchers.`is`(enforced))
        else
            assertThat(annotation.enforcedTextAttributes, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(annotation.textAttributes, CoreMatchers.`is`(attributes))
    }

    fun error(annotation: Annotation, start: Int, end: Int, message: String) {
        assertThat(annotation.severity, CoreMatchers.`is`(HighlightSeverity.ERROR))
        assertThat(annotation.startOffset, CoreMatchers.`is`(start))
        assertThat(annotation.endOffset, CoreMatchers.`is`(end))
        assertThat(annotation.message, CoreMatchers.`is`(message))
        assertThat(annotation.enforcedTextAttributes, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(annotation.textAttributes, CoreMatchers.`is`(CodeInsightColors.ERRORS_ATTRIBUTES))
    }
}

fun List<Annotation>.prettyPrint(): String {
    return groupBy { "${it.severity} (${it.startOffset}:${it.endOffset})" }.asSequence().joinToString("\n") {
        val annotations = it.value.joinToString(" + ") { a ->
            when {
                a.enforcedTextAttributes === TextAttributes.ERASE_MARKER -> "ERASED/${a.textAttributes.externalName}"
                a.message != null -> "\"${a.message}\""
                else -> a.textAttributes.externalName
            }
        }
        "${it.key} $annotations"
    }
}
