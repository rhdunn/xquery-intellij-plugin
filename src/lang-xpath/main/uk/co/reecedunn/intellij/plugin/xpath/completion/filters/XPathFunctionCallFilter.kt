/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.isLocalNameOrNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAbbrevForwardStep
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArrowFunctionSpecifier
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNameTest

object XPathFunctionCallFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        return element.ancestors().find {
            when (it) {
                is XPathNameTest -> { // FunctionCall missing parenthesis
                    (element.parent as? XsQNameValue)?.isLocalNameOrNCName(element) == true &&
                            element.ancestors().find { node -> node is XPathAbbrevForwardStep } == null
                }
                is XPathFunctionCall -> {
                    if ((element.parent as? XsQNameValue)?.isLocalNameOrNCName(element) == true) {
                        element.parent.parent is XPathFunctionCall
                    } else {
                        false
                    }
                }
                is XPathArrowFunctionSpecifier -> {
                    if ((element.parent as? XsQNameValue)?.isLocalNameOrNCName(element) == true) {
                        element.parent.parent is XPathArrowFunctionSpecifier
                    } else {
                        false
                    }
                }
                else -> false
            }
        } != null
    }
}
