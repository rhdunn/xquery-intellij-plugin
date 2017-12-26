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

import uk.co.reecedunn.intellij.plugin.core.data.CachedProperty
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import java.lang.ref.WeakReference

class XdmLiteralValue(override val lexicalRepresentation: String,
                      private val cachedStaticType: CachedProperty<XdmSequenceType>) : XdmAtomicValue {

    // NOTE: The staticType may not be initialized yet (i.e. for QNames), so use
    // CachedProperty to lazy-load the parameter.
    override val staticType get(): XdmSequenceType = cachedStaticType.get()!!
}

fun createQName(namespace: String?, localName: String): QName {
    return createQName(
            namespace?.let { XdmLiteralValue(it, CachedProperty { XsAnyURI }) },
            XdmLiteralValue(localName, CachedProperty { XsNCName }),
            null)
}

fun createQName(namespace: XdmAtomicValue?, localName: XdmAtomicValue, declaration: XdmAtomicValue?): QName {
    return QName(namespace, localName, declaration?.let { WeakReference(it) })
}
