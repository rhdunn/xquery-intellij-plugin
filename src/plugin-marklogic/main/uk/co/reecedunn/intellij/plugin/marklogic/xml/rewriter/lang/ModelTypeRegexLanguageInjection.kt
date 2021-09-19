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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.lang

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlAttributeValue
import org.intellij.lang.regexp.RegExpLanguage
import uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.endpoints.Rewriter
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xdm.xml.hasNodeName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue

class ModelTypeRegexLanguageInjection : MultiHostInjector {
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> = ELEMENTS_TO_INJECT_IN

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val (attribute, accessors) = XmlAccessorsProvider.attribute(context) ?: return
        if (!accessors.hasNodeName(attribute, "", "matches")) return

        val element = accessors.parent(attribute) ?: return
        if (!accessors.hasNodeName(element, Rewriter.NAMESPACE, MODEL_TYPE_LOCAL_NAMES)) return

        val host = context as PsiLanguageInjectionHost
        val range = host.textRange

        registrar.startInjecting(RegExpLanguage.INSTANCE)
        registrar.addPlace(null, null, host, range.shiftLeft(range.startOffset))
        registrar.doneInjecting()
    }

    companion object {
        private val MODEL_TYPE_LOCAL_NAMES = setOf("match-header", "match-path", "match-string")

        private val ELEMENTS_TO_INJECT_IN = mutableListOf(
            XmlAttributeValue::class.java,
            XPathStringLiteral::class.java,
            XQueryDirAttributeValue::class.java
        )
    }
}
