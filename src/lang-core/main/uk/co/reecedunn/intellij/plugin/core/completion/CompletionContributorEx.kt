/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement

open class CompletionContributorEx : CompletionContributor() {
    fun builder(
        type: CompletionType = CompletionType.BASIC,
        place: ElementPattern<out PsiElement> = PlatformPatterns.psiElement()
    ): CompletionProviderBuilder {
        val provider = CompletionProviderBuilder()
        extend(type, place, provider)
        return provider
    }

    fun builder(place: ElementPattern<out PsiElement>): CompletionProviderBuilder {
        return builder(CompletionType.BASIC, place)
    }
}
