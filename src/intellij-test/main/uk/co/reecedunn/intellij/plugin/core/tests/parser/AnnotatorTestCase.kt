// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.lang.ParserDefinition
import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.Annotation
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestUtil.testAnnotator
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree

@Suppress("SameParameterValue")
abstract class AnnotatorTestCase<File : PsiFile>(
    fileExt: String?,
    vararg definitions: ParserDefinition
) : ParsingTestCase<File>(fileExt, *definitions) {
    protected fun annotateTree(file: File, annotator: Annotator): List<Annotation> {
        return testAnnotator(annotator, *file.walkTree().toList().toTypedArray())
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
