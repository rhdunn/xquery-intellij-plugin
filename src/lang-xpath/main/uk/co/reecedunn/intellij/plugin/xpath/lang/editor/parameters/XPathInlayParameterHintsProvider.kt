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
package uk.co.reecedunn.intellij.plugin.xpath.lang.editor.parameters

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.isArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.parenthesizedExprTextOffset
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference

@Suppress("UnstableApiUsage", "UnstableTypeUsedInSignature")
class XPathInlayParameterHintsProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement): List<InlayInfo> {
        if (element !is XPathArgumentList) return emptyList()
        return element.bindings.mapIndexedNotNull { index, binding ->
            when {
                binding.param.variableName == null -> null // Parameter with incomplete variable name.
                binding.isEmpty() -> null // Empty variadic parameter.
                index == 0 && element.parent.isArrowFunctionCall -> null // Arrow function call context argument.
                getName(binding[0])?.localName?.data == binding.param.variableName?.localName?.data -> null
                else -> op_qname_presentation(binding.param.variableName!!)?.let { name ->
                    InlayInfo(name, binding[0].let { it.parenthesizedExprTextOffset ?: it.textOffset }, false)
                }
            }
        }
    }

    override fun getDefaultBlackList(): Set<String> = DEFAULT_BLACKLIST

    override fun getHintInfo(element: PsiElement): HintInfo.MethodInfo? {
        if (element !is XPathArgumentList) return null
        val functionName = getFunctionName(element.parent)
        val eqname = functionName?.let { op_qname_presentation(it, true) } ?: return null
        val params = element.bindings.mapNotNull { it.param.variableName?.localName?.data }
        return XPathMethodInfo(eqname, functionName.localName!!.data, params)
    }

    companion object {
        private val DEFAULT_BLACKLIST = setOf(
            "(arg)" // e.g. fn:string#1, xs:QName#1
        )

        private fun getFunctionName(element: PsiElement): XsQNameValue? = when (element) {
            is XpmFunctionCall -> element.functionName?.expand()?.firstOrNull()
            is XpmDynamicFunctionCall -> element.functionReference?.functionName?.expand()?.firstOrNull()
            else -> null
        }

        private fun getName(element: PsiElement): XsQNameValue? = when (element) {
            is XpmVariableReference -> element.variableName
            is XPathRelativePathExpr -> when (val step = element.lastChild) {
                is XPathNameTest -> step.firstChild as? XsQNameValue
                is XPathAbbrevForwardStep, is XPathForwardStep, is XPathReverseStep -> when (step.lastChild) {
                    is XPathNameTest -> step.lastChild.firstChild as? XsQNameValue
                    else -> null
                }
                else -> null
            }
            is XdmAttributeNode -> element.nodeName
            is XdmElementNode -> element.nodeName
            else -> null
        }
    }
}
