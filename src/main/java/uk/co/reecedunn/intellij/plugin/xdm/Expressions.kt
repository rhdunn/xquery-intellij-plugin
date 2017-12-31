/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import java.lang.ref.WeakReference

private class XdmLiteralValue(override val lexicalRepresentation: String,
                              private val cachedStaticType: CacheableProperty<XdmSequenceType>) : XdmLexicalValue {

    // NOTE: The staticType may not be initialized yet (i.e. for QNames), so use
    // CacheableProperty to lazy-load the parameter.
    override val staticType get(): XdmSequenceType = cachedStaticType.get()!!

    override val cacheable: CachingBehaviour = CachingBehaviour.Cache
}

fun createQName(namespace: String, localName: String): QName {
    return QName(
            null,
            XdmLiteralValue(namespace, CacheableProperty { XsAnyURI `is` Cacheable }),
            XdmLiteralValue(localName, CacheableProperty { XsNCName `is` Cacheable }),
            null)
}

fun createQName(namespace: XdmLexicalValue, localName: XdmLexicalValue, declaration: XdmConstantExpression): QName {
    return QName(null, namespace, localName, WeakReference(declaration))
}

fun createLexicalQName(prefix: XdmLexicalValue?, localName: XdmLexicalValue, declaration: XdmConstantExpression): QName {
    return QName(prefix, null, localName, WeakReference(declaration))
}
