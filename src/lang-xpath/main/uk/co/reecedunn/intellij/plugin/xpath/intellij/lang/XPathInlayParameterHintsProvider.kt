/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

package uk.co.reecedunn.intellij.plugin.xpath.intellij.lang

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList

@Suppress("UnstableApiUsage", "UnstableTypeUsedInSignature")
class XPathInlayParameterHintsProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement): List<InlayInfo> {
        if (element !is XPathArgumentList) return emptyList()
        return element.bindings.mapIndexedNotNull { index, binding ->
            when {
                binding.param.variableName == null -> null // Parameter with incomplete variable name.
                binding.isEmpty() -> null // Empty variadic parameter.
                index == 0 && element.parent is PluginArrowFunctionCall -> null // Arrow function call context argument.
                else -> op_qname_presentation(binding.param.variableName!!)?.let {
                    InlayInfo(it, binding[0].textOffset, false)
                }
            }
        }
    }

    override fun getDefaultBlackList(): Set<String> = DEFAULT_BLACKLIST

    override fun getHintInfo(element: PsiElement): HintInfo.MethodInfo? {
        if (element !is XPathArgumentList) return null
        val eqname = element.functionReference?.functionName?.let { op_qname_presentation(it, true) } ?: return null
        val params = element.bindings.mapNotNull {
            it.param.variableName?.let { param -> op_qname_presentation(param) }
        }
        return HintInfo.MethodInfo(eqname, params)
    }

    companion object {
        private val DEFAULT_BLACKLIST = setOf(
            "(arg)" // e.g. fn:string#1, xs:QName#1
        )
    }
}
