/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang.findUsages

import com.intellij.lang.HelpID
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import uk.co.reecedunn.intellij.plugin.intellij.lang.cacheBuilder.XQueryWordsScanner
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.resources.XdmBundle
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

object XQueryFindUsagesProvider : FindUsagesProvider {
    private val TYPE = mapOf(
        XQueryElementType.ANNOTATION to XQueryBundle.message("find-usages.annotation"),
        XPathElementType.ARROW_FUNCTION_SPECIFIER to XdmBundle.message("usage-type.function"),
        XPathElementType.ATOMIC_OR_UNION_TYPE to XdmBundle.message("usage-type.type"),
        XPathElementType.ATTRIBUTE_TEST to XdmBundle.message("usage-type.attribute"),
        XQueryElementType.COMP_ATTR_CONSTRUCTOR to XdmBundle.message("usage-type.attribute"),
        XQueryElementType.COMP_ELEM_CONSTRUCTOR to XdmBundle.message("usage-type.element"),
        XQueryElementType.CURRENT_ITEM to XdmBundle.message("usage-type.variable"),
        XQueryElementType.DECIMAL_FORMAT_DECL to XQueryBundle.message("find-usages.decimal-format"),
        XQueryElementType.DIR_ATTRIBUTE to XdmBundle.message("usage-type.attribute"),
        XQueryElementType.DIR_ELEM_CONSTRUCTOR to XdmBundle.message("usage-type.element"),
        XPathElementType.ELEMENT_TEST to XdmBundle.message("usage-type.element"),
        XPathElementType.FUNCTION_CALL to XdmBundle.message("usage-type.function"),
        XQueryElementType.FUNCTION_DECL to XdmBundle.message("usage-type.function"),
        XQueryElementType.MODULE_DECL to XdmBundle.message("usage-type.namespace"),
        XQueryElementType.MODULE_IMPORT to XdmBundle.message("usage-type.namespace"),
        XPathElementType.NAMED_FUNCTION_REF to XdmBundle.message("usage-type.function"),
        XQueryElementType.NAMESPACE_DECL to XdmBundle.message("usage-type.namespace"),
        XQueryElementType.NEXT_ITEM to XdmBundle.message("usage-type.variable"),
        XQueryElementType.OPTION_DECL to XQueryBundle.message("find-usages.option"),
        XPathElementType.PARAM to XdmBundle.message("usage-type.parameter"),
        XPathElementType.PRAGMA to XdmBundle.message("usage-type.pragma"),
        XQueryElementType.PREVIOUS_ITEM to XdmBundle.message("usage-type.variable"),
        XPathElementType.SCHEMA_ATTRIBUTE_TEST to XdmBundle.message("usage-type.attribute"),
        XPathElementType.SCHEMA_ELEMENT_TEST to XdmBundle.message("usage-type.element"),
        XQueryElementType.SCHEMA_PREFIX to XdmBundle.message("usage-type.namespace"),
        XPathElementType.SIMPLE_TYPE_NAME to XdmBundle.message("usage-type.type"),
        XQueryElementType.TYPE_DECL to XdmBundle.message("usage-type.type"),
        XPathElementType.TYPE_NAME to XdmBundle.message("usage-type.type"),
        XPathElementType.UNION_TYPE to XdmBundle.message("usage-type.type"),
        XPathElementType.VAR_NAME to XdmBundle.message("usage-type.variable")
    )

    override fun getWordsScanner(): WordsScanner? = XQueryWordsScanner()

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean = psiElement is PsiNamedElement

    override fun getHelpId(psiElement: PsiElement): String? = HelpID.FIND_OTHER_USAGES

    override fun getType(element: PsiElement): String {
        val parentType = element.parent.node.elementType
        return when {
            parentType === XPathElementType.NAME_TEST -> {
                when ((element.parent.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
                    XstUsageType.Attribute -> XdmBundle.message("usage-type.attribute")
                    XstUsageType.Element -> XdmBundle.message("usage-type.element")
                    XstUsageType.Namespace -> XdmBundle.message("usage-type.namespace")
                    else -> XdmBundle.message("usage-type.identifier")
                }
            }
            element.node.elementType === XQueryElementType.COMPATIBILITY_ANNOTATION -> {
                XQueryBundle.message("find-usages.annotation")
            }
            else -> TYPE.getOrElse(parentType) { XdmBundle.message("usage-type.identifier") }
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        val name = (element as PsiNamedElement).name
        return name ?: ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String = getDescriptiveName(element)
}
