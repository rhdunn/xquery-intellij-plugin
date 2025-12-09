// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.completion

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionSorter
import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.patterns.ElementPattern

class MockCompletionResultSet(prefixMatcher: PrefixMatcher) : CompletionResultSet(prefixMatcher, {}, null) {
    val elements: MutableList<LookupElement> = mutableListOf()

    override fun addElement(element: LookupElement) {
        if (prefixMatcher.prefixMatches(element)) {
            elements.add(element)
        }
    }

    override fun addLookupAdvertisement(text: String): Unit = TODO()

    override fun caseInsensitive(): CompletionResultSet = TODO()

    override fun restartCompletionOnPrefixChange(pattern: ElementPattern<String?>) = TODO()

    override fun restartCompletionWhenNothingMatches(): Unit = TODO()

    override fun withPrefixMatcher(prefix: String): CompletionResultSet = TODO()

    override fun withPrefixMatcher(matcher: PrefixMatcher): CompletionResultSet = TODO()

    override fun withRelevanceSorter(sorter: CompletionSorter): CompletionResultSet = TODO()
}
