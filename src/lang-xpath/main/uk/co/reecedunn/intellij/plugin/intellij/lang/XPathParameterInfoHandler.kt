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

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference

object XPathParameterInfoHandler : ParameterInfoHandler<XPathFunctionCall, XPathFunctionDeclaration> {
    override fun couldShowInLookup(): Boolean = true

    override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? {
        return null
    }

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): XPathFunctionCall? {
        val e = context.file.findElementAt(context.offset)
        val call = e?.ancestors()?.filterIsInstance<XPathFunctionCall>()?.firstOrNull()
        if (call != null) {
            val refs = (call as XPathFunctionReference).functionName?.element?.references ?: PsiReference.EMPTY_ARRAY
            context.itemsToShow = refs.mapNotNull {
                it.resolve()?.ancestors()?.filterIsInstance<XPathFunctionDeclaration>()?.firstOrNull()
            }.toTypedArray()
        }
        return call
    }

    override fun showParameterInfo(element: XPathFunctionCall, context: CreateParameterInfoContext) {
        context.showHint(element, element.textOffset + 1, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): XPathFunctionCall? {
        val e = context.file.findElementAt(context.offset)
        return e?.ancestors()?.filterIsInstance<XPathFunctionCall>()?.firstOrNull()
    }

    override fun updateParameterInfo(parameterOwner: XPathFunctionCall, context: UpdateParameterInfoContext) {
        val args = parameterOwner.argumentList
        context.setCurrentParameter(
            ParameterInfoUtils.getCurrentParameterIndex(args.node, context.offset, XPathTokenType.COMMA)
        )
    }

    override fun updateUI(p: XPathFunctionDeclaration?, context: ParameterInfoUIContext) {
    }
}
