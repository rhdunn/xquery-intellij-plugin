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

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.localName
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.namespaceUri
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.model.annotatedDeclarations
import uk.co.reecedunn.intellij.plugin.xquery.model.fileProlog
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference
import javax.swing.Icon

class CustomConstraintFunctionReference(
    element: PsiElement,
    private val node: XdmElementNode
) : PsiReferenceBase<PsiElement>(element) {
    // region facet properties

    @Suppress("unused")
    val referenceType: String
        get() = node.localName ?: ""

    val apply: XdmAttributeNode?
        get() = node.attributes.find { it.localName == "apply" && it.namespaceUri == "" }

    @Suppress("unused")
    val moduleNamespace: XdmAttributeNode?
        get() = node.attributes.find { it.localName == "ns" && it.namespaceUri == "" }

    @Suppress("MemberVisibilityCanBePrivate")
    val moduleUri: XdmAttributeNode?
        get() = node.attributes.find { it.localName == "at" && it.namespaceUri == "" }

    // endregion
    // region function reference

    private val moduleUriValue: PsiElement?
        get() = (moduleUri as? PsiElement)?.children()?.filterIsInstance<XQueryDirAttributeValue>()?.firstOrNull()

    private val moduleUriReference: PsiReference?
        get() = moduleUriValue?.references?.find { it is ModuleUriReference }

    val function: XpmFunctionDeclaration?
        get() {
            val prolog = moduleUriReference?.resolve()?.fileProlog() ?: return null
            return prolog.annotatedDeclarations<XpmFunctionDeclaration>().find { function ->
                function.functionName?.localName?.data == apply?.stringValue
            }
        }

    // endregion
    // region PsiReference

    override fun resolve(): PsiElement? = function?.functionName?.element

    override fun getVariants(): Array<Any> = arrayOf()

    // endregion

    companion object : PsiReferenceProvider() {
        val REFERENCE_TYPES: Set<String> = setOf("parse", "start-facet", "finish-facet")

        private fun create(
            element: PsiElement,
            current: PsiElement
        ): CustomConstraintFunctionReference? = when (current) {
            is XQueryDirAttributeValue -> create(element, current.parent)
            is XdmAttributeNode -> create(element, current.parentNode as PsiElement)
            is XdmElementNode -> CustomConstraintFunctionReference(element, current)
            else -> null
        }

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val ref = create(element, element) ?: return arrayOf()
            return when {
                ref.apply?.stringValue.isNullOrBlank() -> arrayOf()
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
