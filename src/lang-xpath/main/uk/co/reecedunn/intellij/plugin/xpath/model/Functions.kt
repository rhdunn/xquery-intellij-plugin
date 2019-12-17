/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.navigation.ItemPresentation
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XsQNameValue

// region Function Parameter Bindings

interface XPathFunctionParamBinding {
    val param: XPathVariableBinding?
}

data class XPathFunctionNamedParamBinding(
    override val param: XPathVariableBinding
) : XPathFunctionParamBinding

// endregion

interface XPathFunctionReference {
    val functionName: XsQNameValue?

    val arity: Int
}

interface XPathFunctionDeclaration {
    companion object {
        val ARITY_ZERO = Range(0, 0)
    }

    val functionName: XsQNameValue?

    val arity: Range<Int>

    val returnType: XdmSequenceType?

    val params: List<XPathVariableBinding>

    val paramListPresentation: ItemPresentation?

    val isVariadic: Boolean
}
