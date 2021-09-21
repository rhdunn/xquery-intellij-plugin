/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import org.intellij.lang.regexp.RegExpLanguage
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.Rewriter

class ModelTypeRegexLanguageInjection : MultiHostInjector {
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> = ELEMENTS_TO_INJECT_IN

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val attribute = context.contextOfType<XmlAttributeValue>(false)?.parent as? XmlAttribute ?: return
        if (attribute.localName != "matches" || !isModelTypeRegex(attribute.parent)) return

        val host = context as PsiLanguageInjectionHost
        val range = host.textRange

        registrar.startInjecting(RegExpLanguage.INSTANCE)
        registrar.addPlace(null, null, host, range.shiftLeft(range.startOffset))
        registrar.doneInjecting()
    }

    private fun isModelTypeRegex(tag: XmlTag): Boolean {
        return tag.namespace == Rewriter.NAMESPACE && MODEL_TYPE_LOCAL_NAMES.contains(tag.localName)
    }

    companion object {
        private val MODEL_TYPE_LOCAL_NAMES = setOf("match-header", "match-path", "match-string")

        private val ELEMENTS_TO_INJECT_IN = mutableListOf(XmlAttributeValue::class.java)
    }
}
