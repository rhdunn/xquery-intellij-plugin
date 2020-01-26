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

import com.intellij.psi.PsiElement

abstract class Annotator : com.intellij.lang.annotation.Annotator {
    private var currentHolder: AnnotationHolder? = null

    override fun annotate(element: PsiElement, holder: com.intellij.lang.annotation.AnnotationHolder) {
        if (currentHolder?.holder != holder) {
            currentHolder = AnnotationHolder(holder)
        }
        annotateElement(element, currentHolder!!)
    }

    abstract fun annotateElement(element: PsiElement, holder: AnnotationHolder)
}
