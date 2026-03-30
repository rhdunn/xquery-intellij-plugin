// Copyright (C) 2021, 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
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
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.getAttribute
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.getAttributeValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.localName
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

    val apply: String?
        get() = node.getAttributeValue("", "apply")

    @Suppress("unused")
    val moduleNamespace: String?
        get() = node.getAttributeValue("", "ns")

    @Suppress("unused")
    val moduleUri: String?
        get() = node.getAttributeValue("", "at")

    // endregion
    // region function reference

    private val moduleUriValue: PsiElement?
        get() {
            val moduleUriNode = node.getAttribute("", "at")
            return (moduleUriNode as? PsiElement)
                ?.children()
                ?.filterIsInstance<XQueryDirAttributeValue>()
                ?.firstOrNull()
        }

    private val moduleUriReference: PsiReference?
        get() = moduleUriValue?.references?.find { it is ModuleUriReference }

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
                ref.apply.isNullOrBlank() -> arrayOf()
                else -> arrayOf(ref)
            }
        }

        fun getIcon(referenceType: String?): Icon = when (referenceType) {
            "parse" -> MarkLogicIcons.Markers.CustomSearchFacetParse
            "start-facet" -> MarkLogicIcons.Markers.CustomSearchFacetStart
            "finish-facet" -> MarkLogicIcons.Markers.CustomSearchFacetFinish
            else -> MarkLogicIcons.Markers.CustomSearchFacetParse
        }
    }
}
