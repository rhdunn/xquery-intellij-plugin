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
package uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.completion

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionSorter
import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.patterns.ElementPattern

class MockCompletionResultSet : CompletionResultSet(null, null, null) {
    override fun addElement(element: LookupElement) = TODO()

    override fun addLookupAdvertisement(text: String) = TODO()

    override fun caseInsensitive(): CompletionResultSet = TODO()

    override fun restartCompletionOnPrefixChange(prefixCondition: ElementPattern<String>?) = TODO()

    override fun restartCompletionWhenNothingMatches() = TODO()

    override fun withPrefixMatcher(prefix: String): CompletionResultSet = TODO()

    override fun withPrefixMatcher(matcher: PrefixMatcher): CompletionResultSet = TODO()

    override fun withRelevanceSorter(sorter: CompletionSorter): CompletionResultSet = TODO()
}
