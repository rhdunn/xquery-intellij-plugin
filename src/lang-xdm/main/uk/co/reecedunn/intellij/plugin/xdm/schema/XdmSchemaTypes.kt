/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.schema

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.core.sequences.contexts
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType

abstract class XdmSchemaTypes {
    open val isEnabled: Boolean = true

    abstract fun create(type: String?): ISchemaType?

    fun create(element: PsiElement): ISchemaType? {
        return element.contexts(false).mapNotNull { getSchemaType(it) }.firstOrNull()
    }

    open fun create(attribute: XmlAttribute): ISchemaType? = null

    open fun create(text: XmlText): ISchemaType? = null

    private fun getSchemaType(element: PsiElement) = when (element) {
        is XmlAttributeValue -> (element.parent as XmlAttribute).let { attr ->
            when (attr.parent.namespace) {
                XSD_NAMESPACE -> create(attr) // Calling attr.schemaType here causes an infinite recursion.
                else -> create(attr.schemaType) ?: create(attr)
            }
        }
        is XmlText -> create(element)
        else -> null
    }

    companion object {
        private const val XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema"
    }
}
