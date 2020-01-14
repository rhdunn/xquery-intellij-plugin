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
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementRef
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import java.lang.ref.WeakReference

data class XsAnyUri(
    override val data: String,
    override val context: XdmUriContext,
    override val moduleTypes: Array<XdmModuleType>,
    private val reference: WeakReference<PsiElement>?
) : XsAnyUriValue, XdmElementRef {

    constructor(
        data: String,
        context: XdmUriContext,
        moduleTypes: Array<XdmModuleType>,
        element: PsiElement?
    ) : this(data, context, moduleTypes, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XsAnyUri

        if (data != other.data) return false
        if (context != other.context) return false
        if (!moduleTypes.contentEquals(other.moduleTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + context.hashCode()
        result = 31 * result + moduleTypes.contentHashCode()
        return result
    }
}
