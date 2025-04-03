// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2019 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.Annotation
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

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
}

fun List<Annotation>.prettyPrint(): String {
    return groupBy { "${it.severity.name} (${it.startOffset}:${it.endOffset})" }.asSequence().joinToString("\n") {
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
