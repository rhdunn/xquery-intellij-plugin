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

package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList

@Suppress("UnstableApiUsage", "UnstableTypeUsedInSignature")
object XPathInlayParameterHintsProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement?): List<InlayInfo> {
        if (element !is XPathArgumentList) return emptyList()
        return element.bindings.mapIndexedNotNull { index, binding ->
            when {
                binding.param.variableName == null -> null // Parameter with incomplete variable name.
                binding.isEmpty() -> null // Empty variadic parameter.
                index == 0 && element.parent is PluginArrowFunctionCall -> null // Arrow function call context argument.
                else -> {
                    val name = op_qname_presentation(binding.param.variableName!!)
                    InlayInfo(name, binding[0].textOffset, false)
                }
            }
        }
    }

    override fun getDefaultBlackList(): Set<String> = setOf()

    override fun getHintInfo(element: PsiElement?): HintInfo? {
        return null
    }
}
