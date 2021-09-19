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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xdm.xml.attribute
import uk.co.reecedunn.intellij.plugin.xdm.xml.attributeStringValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.model.annotatedDeclarations
import uk.co.reecedunn.intellij.plugin.xquery.model.fileProlog
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference
import javax.swing.Icon

class CustomFacetFunctionReference(element: PsiElement, private val node: Any, private val accessors: XmlAccessors) :
    PsiReferenceBase<PsiElement>(element) {
    // region facet properties

    @Suppress("unused")
    val referenceType: String
        get() = accessors.localName(node) ?: ""

    val apply: String
        get() = accessors.attributeStringValue(node, "", "apply") ?: ""

    @Suppress("unused")
    val moduleNamespace: String
        get() = accessors.attributeStringValue(node, "", "ns") ?: ""

    @Suppress("unused")
    val moduleUri: String
        get() = accessors.attributeStringValue(node, "", "at") ?: ""

    // endregion
    // region function reference

    private val moduleUriReference: PsiReference?
        get() {
            val at = accessors.attribute(node, "", "at") as? PsiElement ?: return null
            val atValue = accessors.attributeValueNode(at) ?: return null
            return atValue.references.find { it is ModuleUriReference }
        }

    val function: XpmFunctionDeclaration?
        get() {
            val prolog = moduleUriReference?.resolve()?.fileProlog() ?: return null
            return prolog.annotatedDeclarations<XpmFunctionDeclaration>().find { function ->
                function.functionName?.localName?.data == apply
            }
        }

    // endregion
    // region PsiReference

    override fun resolve(): PsiElement? = function?.functionName?.element

    override fun getVariants(): Array<Any> = arrayOf()

    // endregion

    companion object : PsiReferenceProvider() {
        val REFERENCE_TYPES: Set<String> = setOf("parse", "start-facet", "finish-facet")

        private fun fromAttribute(element: PsiElement): CustomFacetFunctionReference? {
            val (node, accessors) = XmlAccessorsProvider.attribute(element) ?: return null
            val parent = accessors.parent(node) ?: return null
            return CustomFacetFunctionReference(element, parent, accessors)
        }

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val ref = fromAttribute(element) ?: return arrayOf()
            return when {
                ref.apply.isBlank() -> arrayOf()
                else -> arrayOf(ref)
            }
        }

        fun getIcon(referenceType: String?): Icon = when (referenceType) {
            "parse" -> MarkLogicIcons.Markers.CusomSearchFacetParse
            "start-facet" -> MarkLogicIcons.Markers.CusomSearchFacetStart
            "finish-facet" -> MarkLogicIcons.Markers.CusomSearchFacetFinish
            else -> MarkLogicIcons.Markers.CusomSearchFacetParse
        }
    }
}
