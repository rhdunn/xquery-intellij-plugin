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

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.xdm.XsDouble
import uk.co.reecedunn.intellij.plugin.xdm.XsUntypedAtomic
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathDoubleLiteral

class XPathDoubleLiteralImpl(type: IElementType, text: CharSequence):
        LeafPsiElement(type, text),
        XPathDoubleLiteral,
        XdmStaticValue {

    private val literal = CacheableProperty { XsDouble.cast(text, XsUntypedAtomic) `is` Cacheable }

    override val staticValue get(): Any? = literal.get()!!.value

    override val staticType: XdmSequenceType = literal.get()!!.type

    override val cacheable: CachingBehaviour = literal.cachingBehaviour
}
