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

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.TestOnly

class AnnotationHolder(val holder: com.intellij.lang.annotation.AnnotationHolder) {
    internal var currentElement: PsiElement? = null

    @Suppress("UnstableApiUsage")
    @ApiStatus.Experimental
    fun newAnnotation(severity: HighlightSeverity, message: String): AnnotationBuilder {
        return AnnotationBuilder(this, severity, message)
    }

    @Suppress("UnstableApiUsage")
    @ApiStatus.Experimental
    fun newSilentAnnotation(severity: HighlightSeverity): AnnotationBuilder {
        return AnnotationBuilder(this, severity, null)
    }
}

@TestOnly
fun com.intellij.lang.annotation.AnnotationHolder.runAnnotatorWithContext(
    element: PsiElement,
    annotator: com.intellij.lang.annotation.Annotator
) {
    return annotator.annotate(element, this)
}
