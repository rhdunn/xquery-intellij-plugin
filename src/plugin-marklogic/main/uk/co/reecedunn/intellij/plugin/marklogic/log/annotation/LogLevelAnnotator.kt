/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.highlighter.MarkLogicErrorLogSyntaxHighlighter

open class LogLevelAnnotator : Annotator() {
    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is MarkLogicErrorLogLine) return

        element.logLevel?.let { logLevel ->
            val start = logLevel.textRange.startOffset
            val end = element.textRange.endOffset
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(TextRange(start, end))
                .textAttributes(MarkLogicErrorLogSyntaxHighlighter.getTokenHighlights(logLevel.elementType!!).first())
                .create()
        }
    }
}
