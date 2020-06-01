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
package uk.co.reecedunn.intellij.plugin.core.psi

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

val PsiFile.document: Document? get() = PsiDocumentManager.getInstance(project).getDocument(this)

fun PsiFile.lineElements(line: Int): Sequence<PsiElement> {
    val offsets = document?.let { IntRange(it.getLineStartOffset(line), it.getLineEndOffset(line)).asSequence() }
    return offsets?.mapNotNull { findElementAt(it) } ?: sequenceOf()
}
