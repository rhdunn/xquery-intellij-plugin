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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.xml.XmlElementType
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter

class ModuleUriElementReferenceContributor : PsiReferenceContributor(), ElementPattern<PsiElement> {
    // region PsiReferenceContributor

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(this, ModuleUriElementReference)
    }

    // endregion
    // region ElementPattern

    override fun accepts(o: Any?): Boolean {
        val node = o as? XmlTag ?: return false
        if (node.namespace != Rewriter.NAMESPACE) return false
        return when (node.localName) {
            "dispatch" -> node.getAttributeValue("xdbc") != "true"
            "set-path" -> true
            "set-error-handler" -> true
            else -> false
        }
    }

    override fun accepts(o: Any?, context: ProcessingContext?): Boolean = accepts(o)

    override fun getCondition(): ElementPatternCondition<PsiElement> = PATTERN.condition

    companion object {
        private val PATTERN = PlatformPatterns.psiElement(XmlElementType.XML_TAG)
    }

    // endregion
}
