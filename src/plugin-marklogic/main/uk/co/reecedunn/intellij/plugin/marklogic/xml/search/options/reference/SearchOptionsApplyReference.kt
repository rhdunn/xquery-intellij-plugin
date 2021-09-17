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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlElementType
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xdm.xml.attribute
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.model.annotatedDeclarations
import uk.co.reecedunn.intellij.plugin.xquery.model.fileProlog
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference

class SearchOptionsApplyReference(element: PsiElement, private val node: Any, private val accessors: XmlAccessors) :
    PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement? {
        val apply = accessors.stringValue(node) ?: return null
        val parent = accessors.parent(node) ?: return null

        val at = accessors.attribute(parent, "", "at") as? PsiElement ?: return null
        val atValue = at.children().find { it.elementType in ATTRIBUTE_VALUE_TOKENS } ?: return null

        val moduleRef = atValue.references.find { it is ModuleUriReference }
        val prolog = moduleRef?.resolve()?.fileProlog() ?: return null

        return prolog.annotatedDeclarations<XpmFunctionDeclaration>().find { function ->
            function.functionName?.localName?.data == apply
        }?.functionName?.element
    }

    override fun getVariants(): Array<Any> = arrayOf()

    companion object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val (node, accessors) = XmlAccessorsProvider.attribute(element) ?: return arrayOf()
            return when {
                node !is PsiElement -> arrayOf()
                accessors.stringValue(node).isNullOrBlank() -> arrayOf()
                else -> arrayOf(SearchOptionsApplyReference(element, node, accessors))
            }
        }

        private val ATTRIBUTE_VALUE_TOKENS = TokenSet.create(
            XmlElementType.XML_ATTRIBUTE_VALUE,
            XQueryElementType.DIR_ATTRIBUTE_VALUE
        )
    }
}
