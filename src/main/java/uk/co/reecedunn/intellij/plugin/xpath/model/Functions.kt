/*
 * Copyright (C) 2018 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType

interface XPathFunctionReference {
    val functionName: XsQNameValue?

    val arity: Int
}

interface XPathFunctionName {
    val cacheable: CachingBehaviour

    val functionName: QName?
}

interface XPathFunctionArguments<out T> {
    val cacheable: CachingBehaviour

    val arity: Int

    val arguments: List<T>
}

interface XPathFunctionDeclaration : XPathFunctionName, XPathFunctionArguments<XPathVariableBinding> {
    val returnType: XdmSequenceType?
}
