/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.optree.function

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableBinding

data class XpmFunctionParamBinding(
    val param: XpmVariableBinding,
    private val values: List<PsiElement>
) : List<PsiElement> {
    constructor(param: XpmVariableBinding, value: PsiElement) : this(param, listOf(value))

    // region List<PsiElement>

    override val size: Int = values.size
    override fun contains(element: PsiElement): Boolean = values.contains(element)
    override fun containsAll(elements: Collection<PsiElement>): Boolean = values.containsAll(elements)
    override fun get(index: Int): PsiElement = values[index]
    override fun indexOf(element: PsiElement): Int = values.indexOf(element)
    override fun isEmpty(): Boolean = values.isEmpty()
    override fun iterator(): Iterator<PsiElement> = values.iterator()
    override fun lastIndexOf(element: PsiElement): Int = values.lastIndexOf(element)
    override fun listIterator(): ListIterator<PsiElement> = values.listIterator()
    override fun listIterator(index: Int): ListIterator<PsiElement> = values.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): List<PsiElement> = values.subList(fromIndex, toIndex)

    // endregion
}
