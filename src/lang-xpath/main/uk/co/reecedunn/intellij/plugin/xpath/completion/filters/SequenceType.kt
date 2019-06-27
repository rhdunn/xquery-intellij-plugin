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
package uk.co.reecedunn.intellij.plugin.xpath.completion.filters

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionFilter
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAtomicOrUnionType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference

object XPathSequenceTypeFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        return element.ancestors().find {
            it is XPathAtomicOrUnionType // ItemType without '()'
        } != null
    }
}

object XPathItemTypeFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        return element.ancestors().find {
            it is XPathAtomicOrUnionType // ItemType without '()'
        } != null
    }
}

object XPathKindTestFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        return element.ancestors().find {
            when (it) {
                is XPathNodeTest -> true // KindTest as NodeTest
                is XPathAtomicOrUnionType -> true // KindTest as ItemType without '()'
                is XPathFunctionCall -> { // Unknown KindTest with '()'
                    val fn = it as XPathFunctionReference
                    fn.functionName?.let { name -> name.isLexicalQName && name.prefix == null } == true
                }
                else -> false
            }
        } != null
    }
}
