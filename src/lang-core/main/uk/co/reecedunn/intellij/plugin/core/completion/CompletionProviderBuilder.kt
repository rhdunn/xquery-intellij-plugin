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

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class CompletionProviderBuilder : CompletionProvider<CompletionParameters>() {
    private var completions: CompletionProviderEx? = null
    private var filter: CompletionFilter? = null
    private var property: CompletionProperty? = null

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    fun addCompletions(completions: CompletionProviderEx): CompletionProviderBuilder {
        val c = this.completions
        when (c) {
            is CompletionProviderList -> c.add(completions)
            is CompletionProviderEx -> {
                val providerList = CompletionProviderList()
                providerList.add(c)
                providerList.add(completions)
                this.completions = providerList
            }
            else -> this.completions = completions
        }
        return this
    }

    fun withFilter(filter: CompletionFilter): CompletionProviderBuilder {
        this.filter = filter
        return this
    }

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    fun withProperty(property: CompletionProperty): CompletionProviderBuilder {
        val p = this.property
        when (p) {
            is CompletionPropertyList -> p.add(property)
            is CompletionProperty -> {
                val propertyList = CompletionPropertyList()
                propertyList.add(p)
                propertyList.add(property)
                this.property = propertyList
            }
            else -> this.property = property
        }
        return this
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        property?.computeProperty(parameters, context)
        if (filter != null) {
            if (filter?.accepts(parameters.position, context) == true) {
                completions?.apply(context, result)
            }
        } else {
            completions?.apply(context, result)
        }
    }
}
