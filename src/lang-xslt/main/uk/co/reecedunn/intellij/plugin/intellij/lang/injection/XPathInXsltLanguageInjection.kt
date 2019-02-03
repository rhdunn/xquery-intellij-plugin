/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlAttributeValue
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xslt.psi.isXslExpression
import uk.co.reecedunn.intellij.plugin.xslt.psi.isXslPattern
import uk.co.reecedunn.intellij.plugin.xslt.psi.isXslStylesheet

class XPathInXsltLanguageInjection : MultiHostInjector {
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(XmlAttributeValue::class.java)
    }

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (!context.isXslStylesheet()) return
        val attribute = context.parent
        when {
            attribute.isXslExpression() || attribute.isXslPattern() -> {
                val host = context as PsiLanguageInjectionHost
                val range = context.textRange

                registrar.startInjecting(XPath)
                registrar.addPlace(null, null, host, range.shiftLeft(range.startOffset))
                registrar.doneInjecting()
            }
        }
    }
}
