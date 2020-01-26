/*
 * Copyright (C) 2020 Reece H. Dunn
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
package com.intellij.compat.lang.annotation

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.annotation.ProblemGroup
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

@Suppress("unused")
class AnnotationBuilder(
    private val holder: AnnotationHolder,
    private val severity: HighlightSeverity,
    private val message: String?
) {
    private var textRange: TextRange? = null
    private var isAfterEndOfLine: Boolean? = null
    private var isFileLevelAnnotation: Boolean? = null
    private var gutterIconRenderer: GutterIconRenderer? = null
    private var problemGroup: ProblemGroup? = null
    private var enforcedTextAttributes: TextAttributes? = null
    private var textAttributes: TextAttributesKey? = null

    fun range(range: TextRange): AnnotationBuilder {
        textRange = range
        return this
    }

    fun range(element: ASTNode): AnnotationBuilder = range(element.textRange)

    fun range(element: PsiElement): AnnotationBuilder = range(element.textRange)

    fun afterEndOfLine(): AnnotationBuilder {
        isAfterEndOfLine = true
        return this
    }

    fun fileLevel(): AnnotationBuilder {
        isFileLevelAnnotation = true
        return this
    }

    fun gutterIconRenderer(gutterIconRenderer: GutterIconRenderer): AnnotationBuilder {
        this.gutterIconRenderer = gutterIconRenderer
        return this
    }

    fun problemGroup(problemGroup: ProblemGroup): AnnotationBuilder {
        this.problemGroup = problemGroup
        return this
    }

    fun enforcedTextAttributes(enforcedAttributes: TextAttributes): AnnotationBuilder {
        enforcedTextAttributes = enforcedAttributes
        return this
    }

    fun textAttributes(enforcedAttributes: TextAttributesKey): AnnotationBuilder {
        textAttributes = enforcedAttributes
        return this
    }

    fun create() {
        val textRange: TextRange = textRange ?: holder.currentElement!!.textRange
        val annotation = holder.holder.createAnnotation(severity, textRange, message, null)
        isAfterEndOfLine?.let { annotation.isAfterEndOfLine = it }
        isFileLevelAnnotation?.let { annotation.isFileLevelAnnotation = it }
        gutterIconRenderer?.let { annotation.gutterIconRenderer = it }
        problemGroup?.let { annotation.problemGroup = it }
        enforcedTextAttributes?.let { annotation.enforcedTextAttributes = it }
        textAttributes?.let { annotation.textAttributes = it }
    }
}
