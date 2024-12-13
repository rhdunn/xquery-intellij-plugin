// Copyright (C) 2021, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.log.annotation

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.highlighter.MarkLogicErrorLogSyntaxHighlighter

open class LogLevelAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is MarkLogicErrorLogLine) return

        element.logLevel?.let { logLevel ->
            val highlights = MarkLogicErrorLogSyntaxHighlighter.getTokenHighlights(logLevel.elementType!!)
            if (highlights.isEmpty()) return@let

            val start = logLevel.textRange.startOffset
            val end = element.textRange.endOffset
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(TextRange(start, end))
                .textAttributes(highlights.first())
                .create()
        }
    }
}
