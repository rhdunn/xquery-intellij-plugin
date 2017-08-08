/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.extensions;

import com.intellij.psi.PsiElement

private class PsiElementIterator(private var node: PsiElement?,
                                 private var walk: (PsiElement) -> PsiElement?) : Iterator<PsiElement> {
    override fun hasNext(): Boolean {
        return node != null
    }

    override fun next(): PsiElement {
        val ret = node!!
        node = walk(ret)
        return ret
    }
}

fun PsiElement.ancestors(): Sequence<PsiElement> {
    return PsiElementIterator(parent, PsiElement::getParent).asSequence()
}

fun PsiElement.descendants(): Sequence<PsiElement> {
    return PsiElementIterator(firstChild, PsiElement::getFirstChild).asSequence()
}

fun PsiElement.children(): Sequence<PsiElement> {
    return PsiElementIterator(firstChild, PsiElement::getNextSibling).asSequence()
}

fun PsiElement.siblings(): Sequence<PsiElement> {
    return PsiElementIterator(nextSibling, PsiElement::getNextSibling).asSequence()
}

fun PsiElement.walkTree(): Sequence<PsiElement> {
    return PsiElementIterator(this, { e ->
        val next: PsiElement? = e.prevSibling
        if (next == null) e.parent else next
    }).asSequence()
}
