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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

object XQueryFindUsagesProvider : FindUsagesProvider {
    private val TYPE = mapOf(
        XQueryElementType.ANNOTATION to XQueryBundle.message("find-usages.annotation"),
        XPathElementType.ARROW_FUNCTION_SPECIFIER to XPathBundle.message("find-usages.function"),
        XPathElementType.ATOMIC_OR_UNION_TYPE to XPathBundle.message("find-usages.type"),
        XPathElementType.ATTRIBUTE_TEST to XQueryBundle.message("find-usages.attribute"),
        XQueryElementType.COMP_ATTR_CONSTRUCTOR to XQueryBundle.message("find-usages.attribute"),
        XQueryElementType.COMP_ELEM_CONSTRUCTOR to XQueryBundle.message("find-usages.element"),
        XQueryElementType.CURRENT_ITEM to XQueryBundle.message("find-usages.variable"),
        XQueryElementType.DECIMAL_FORMAT_DECL to XQueryBundle.message("find-usages.decimal-format"),
        XQueryElementType.DIR_ATTRIBUTE to XQueryBundle.message("find-usages.attribute"),
        XQueryElementType.DIR_ELEM_CONSTRUCTOR to XQueryBundle.message("find-usages.element"),
        XPathElementType.ELEMENT_TEST to XQueryBundle.message("find-usages.element"),
        XPathElementType.FUNCTION_CALL to XPathBundle.message("find-usages.function"),
        XQueryElementType.FUNCTION_DECL to XPathBundle.message("find-usages.function"),
        XQueryElementType.MODULE_DECL to XQueryBundle.message("find-usages.namespace"),
        XQueryElementType.MODULE_IMPORT to XQueryBundle.message("find-usages.namespace"),
        XPathElementType.NAMED_FUNCTION_REF to XPathBundle.message("find-usages.function"),
        XQueryElementType.NAMESPACE_DECL to XQueryBundle.message("find-usages.namespace"),
        XQueryElementType.NEXT_ITEM to XQueryBundle.message("find-usages.variable"),
        XQueryElementType.OPTION_DECL to XQueryBundle.message("find-usages.option"),
        XPathElementType.PARAM to XQueryBundle.message("find-usages.parameter"),
        XPathElementType.PRAGMA to XQueryBundle.message("find-usages.pragma"),
        XQueryElementType.PREVIOUS_ITEM to XQueryBundle.message("find-usages.variable"),
        XPathElementType.SCHEMA_ATTRIBUTE_TEST to XQueryBundle.message("find-usages.attribute"),
        XPathElementType.SCHEMA_ELEMENT_TEST to XQueryBundle.message("find-usages.element"),
        XQueryElementType.SCHEMA_PREFIX to XQueryBundle.message("find-usages.namespace"),
        XPathElementType.SIMPLE_TYPE_NAME to XPathBundle.message("find-usages.type"),
        XQueryElementType.TYPE_DECL to XPathBundle.message("find-usages.type"),
        XPathElementType.TYPE_NAME to XPathBundle.message("find-usages.type"),
        XPathElementType.UNION_TYPE to XPathBundle.message("find-usages.type"),
        XPathElementType.VAR_NAME to XQueryBundle.message("find-usages.variable")
    )

    override fun getWordsScanner(): WordsScanner? = XQueryWordsScanner()

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean = psiElement is PsiNamedElement

    override fun getHelpId(psiElement: PsiElement): String? = HelpID.FIND_OTHER_USAGES

    override fun getType(element: PsiElement): String {
        val parentType = element.parent.node.elementType
        return if (parentType === XPathElementType.NAME_TEST)
            when ((element.parent.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
                XPathPrincipalNodeKind.Element -> XQueryBundle.message("find-usages.element")
                XPathPrincipalNodeKind.Attribute -> XQueryBundle.message("find-usages.attribute")
                XPathPrincipalNodeKind.Namespace -> XQueryBundle.message("find-usages.namespace")
                null -> XPathBundle.message("find-usages.identifier")
            }
        else
            TYPE.getOrElse(parentType) { XPathBundle.message("find-usages.identifier") }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        val name = (element as PsiNamedElement).name
        return name ?: ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String = getDescriptiveName(element)
}
