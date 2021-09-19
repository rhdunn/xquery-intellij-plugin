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
package uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.xml.XmlElementType
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.CustomFacetFunctionReference
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.SearchOptions
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class CustomFacetApplyReferenceContributor : PsiReferenceContributor(), ElementPattern<PsiElement> {
    // region PsiReferenceContributor

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(this, CustomFacetFunctionReference)
    }

    // endregion
    // region ElementPattern

    override fun accepts(o: Any?): Boolean {
        val (node, accessors) = XmlAccessorsProvider.attribute(o) ?: return false

        val parent = accessors.parent(node) ?: return false
        if (accessors.namespaceUri(parent) != SearchOptions.NAMESPACE) return false

        return when (accessors.localName(parent)) {
            "parse" -> accessors.localName(node) == "apply"
            "start-facet" -> accessors.localName(node) == "apply"
            "finish-facet" -> accessors.localName(node) == "apply"
            else -> false
        }
    }

    override fun accepts(o: Any?, context: ProcessingContext?): Boolean = accepts(o)

    override fun getCondition(): ElementPatternCondition<PsiElement> = PATTERN.condition

    companion object {
        private val PATTERN = StandardPatterns.or(
            PlatformPatterns.psiElement(XmlElementType.XML_ATTRIBUTE_VALUE),
            PlatformPatterns.psiElement(XQueryElementType.DIR_ATTRIBUTE_VALUE)
        )
    }

    // endregion
}
