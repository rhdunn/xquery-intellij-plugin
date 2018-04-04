/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionArguments
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding

class XPathParamListPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathParamList,
        XPathFunctionArguments<XPathVariableBinding> {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedArguments.invalidate()
    }

    override val cacheable get(): CachingBehaviour = cachedArguments.cachingBehaviour

    override val arguments get(): List<XPathVariableBinding> = cachedArguments.get()!!
    private val cachedArguments = CacheableProperty {
        children().filterIsInstance<XPathParam>().map { param -> param as XPathVariableBinding }.toList() `is` Cacheable
    }

    override val arity get(): Int = cachedArguments.get()!!.size
}
