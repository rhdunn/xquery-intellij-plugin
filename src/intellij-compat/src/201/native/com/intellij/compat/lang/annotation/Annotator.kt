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

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly

abstract class Annotator : com.intellij.lang.annotation.Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder): Unit = annotateElement(element, holder)

    abstract fun annotateElement(element: PsiElement, holder: AnnotationHolder)
}

@Suppress("UnstableApiUsage")
@TestOnly
fun com.intellij.lang.annotation.AnnotationHolder.runAnnotatorWithContext(
    element: PsiElement,
    annotator: com.intellij.lang.annotation.Annotator
) {
    return (this as AnnotationHolderImpl).runAnnotatorWithContext(element, annotator)
}
