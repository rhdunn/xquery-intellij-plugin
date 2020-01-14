/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementRef
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import java.lang.ref.WeakReference

data class XsQName(
    override val namespace: XsAnyUriValue?,
    override val prefix: XsNCNameValue?,
    override val localName: XsNCNameValue?,
    override val isLexicalQName: Boolean,
    private val reference: WeakReference<PsiElement>?
) : XsQNameValue, XdmElementRef {

    constructor(
        namespace: XsAnyUriValue?,
        prefix: XsNCNameValue?,
        localName: XsNCNameValue?,
        isLexicalQName: Boolean,
        element: PsiElement?
    ) : this(namespace, prefix, localName, isLexicalQName, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}
