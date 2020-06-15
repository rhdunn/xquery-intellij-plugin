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
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.isPrefixOrNCName

object XPathForwardOrReverseAxisFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        return element.ancestors().find {
            when (it) {
                is XPathForwardAxis -> true
                is XPathReverseAxis -> true
                is XPathAxisStep -> {
                    val parent = element.parent
                    // Not the NodeTest NCName or in the PredicateList.
                    parent is XPathNCName && parent.parent is XPathAxisStep
                }
                is XPathFunctionCall -> { // QName prefix part as axis missing the second ':', or NCName.
                    val fn = it as XdmFunctionReference
                    fn.functionName?.isPrefixOrNCName(element) == true
                }
                is XPathNodeTest -> {
                    when (it.parent) {
                        is XPathAxisStep -> false // Incomplete axis step.
                        is XPathForwardStep -> false
                        is XPathAbbrevForwardStep -> false
                        is XPathReverseStep -> false
                        else -> (element.parent as? XsQNameValue)?.isPrefixOrNCName(element) == true
                    }
                }
                else -> false
            }
        } != null
    }
}
