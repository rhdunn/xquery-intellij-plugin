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
package uk.co.reecedunn.intellij.plugin.xpath.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx

fun createAxisStepLookup(kindTest: String): LookupElementBuilder {
    return LookupElementBuilder.create(kindTest)
        .withBoldness(true)
        .withTailText("::")
}

object XPathForwardOrReverseAxisProvider : CompletionProviderEx {
    private val XPATH_AXIS_STEPS = listOf(
        createAxisStepLookup("ancestor"),
        createAxisStepLookup("ancestor-or-self"),
        createAxisStepLookup("attribute"),
        createAxisStepLookup("child"),
        createAxisStepLookup("descendant"),
        createAxisStepLookup("descendant-or-self"),
        createAxisStepLookup("following"),
        createAxisStepLookup("following-sibling"),
        createAxisStepLookup("namespace"), // XPath only
        createAxisStepLookup("parent"),
        createAxisStepLookup("preceding"),
        createAxisStepLookup("preceding-sibling"),
        createAxisStepLookup("self")
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        result.addAllElements(XPATH_AXIS_STEPS)
    }
}
