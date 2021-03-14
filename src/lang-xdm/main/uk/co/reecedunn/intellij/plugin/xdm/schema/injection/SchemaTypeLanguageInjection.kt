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
package uk.co.reecedunn.intellij.plugin.xdm.schema.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.xdm.schema.XdmSchemaTypes

open class SchemaTypeLanguageInjection(private val schemaTypes: XdmSchemaTypes) : MultiHostInjector {
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(XmlAttributeValue::class.java, XmlText::class.java)
    }

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (!schemaTypes.isEnabled)
            return

        schemaTypes.create(context)?.let { schemaType ->
            val host = context as PsiLanguageInjectionHost
            registrar.startInjecting(schemaType.language)
            registrar.addPlace(null, null, host, host.textRange.let { it.shiftLeft(it.startOffset) })
            registrar.doneInjecting()
        }
    }
}
