// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.codeInsight.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionResult
import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.patterns.ElementPattern
import java.util.function.Consumer

abstract class CompletionResultSet(
    prefixMatcher: PrefixMatcher,
    consumer: Consumer<in CompletionResult>,
    contributor: CompletionContributor?
) : com.intellij.codeInsight.completion.CompletionResultSet(prefixMatcher, consumer, contributor) {
    override fun restartCompletionOnPrefixChange(prefixCondition: ElementPattern<String?>) = TODO()
}
