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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.SearchOptions
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.localName
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.namespaceUri
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference

class CustomConstraintModuleUriReferenceContributor : PsiReferenceContributor(), ElementPattern<PsiElement> {
    // region PsiReferenceContributor

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(this, ModuleUriReference)
    }

    // endregion
    // region ElementPattern

    override fun accepts(o: Any?): Boolean {
        val value = o as? XQueryDirAttributeValue ?: return false
        val node = value.parent as? XdmAttributeNode ?: return false
        val parent = node.parentNode as? XdmElementNode ?: return false

        if (parent.namespaceUri != SearchOptions.NAMESPACE) return false
        return when (parent.localName) {
            "parse" -> node.localName == "at"
            "start-facet" -> node.localName == "at"
            "finish-facet" -> node.localName == "at"
            else -> false
        }
    }

    override fun accepts(o: Any?, context: ProcessingContext?): Boolean = accepts(o)

    override fun getCondition(): ElementPatternCondition<PsiElement> = PATTERN.condition

    companion object {
        private val PATTERN = PlatformPatterns.psiElement(XQueryElementType.DIR_ATTRIBUTE_VALUE)
    }

    // endregion
}
