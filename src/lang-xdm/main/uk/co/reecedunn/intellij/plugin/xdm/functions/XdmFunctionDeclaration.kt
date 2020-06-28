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
package uk.co.reecedunn.intellij.plugin.xdm.functions

import com.intellij.navigation.ItemPresentation
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableBinding

interface XdmFunctionDeclaration {
    companion object {
        val ARITY_ZERO: Range<Int> = Range(0, 0)
    }

    val functionName: XsQNameValue?

    val arity: Range<Int>

    val returnType: XdmSequenceType?

    val params: List<XdmVariableBinding>

    val paramListPresentation: ItemPresentation?

    val isVariadic: Boolean

    val functionRefPresentableText: String?

    val annotations: Sequence<XdmAnnotation>
}
