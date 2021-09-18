/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options.CustomFacetFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.types.element

class SearchOptionsApplyReference(element: PsiElement, private val ref: CustomFacetFunctionReference) :
    PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement? = ref.function?.functionName?.element

    override fun getVariants(): Array<Any> = arrayOf()

    companion object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val ref = CustomFacetFunctionReference.fromAttribute(element) ?: return arrayOf()
            return when {
                ref.apply.isBlank() -> arrayOf()
                else -> arrayOf(SearchOptionsApplyReference(element, ref))
            }
        }
    }
}
