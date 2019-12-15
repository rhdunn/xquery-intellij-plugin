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
import uk.co.reecedunn.intellij.plugin.intellij.lang.cacheBuilder.XPathWordsScanner
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

object XPathFindUsagesProvider : FindUsagesProvider {
    private val TYPE = mapOf(
        XPathElementType.ARROW_FUNCTION_SPECIFIER to XPathBundle.message("find-usages.function"),
        XPathElementType.ATOMIC_OR_UNION_TYPE to XPathBundle.message("find-usages.type"),
        XPathElementType.ATTRIBUTE_TEST to XPathBundle.message("find-usages.attribute"),
        XPathElementType.FUNCTION_CALL to XPathBundle.message("find-usages.function"),
        XPathElementType.NAMED_FUNCTION_REF to XPathBundle.message("find-usages.function"),
        XPathElementType.PARAM to XPathBundle.message("find-usages.parameter"),
        XPathElementType.PRAGMA to XPathBundle.message("find-usages.pragma"),
        XPathElementType.SCHEMA_ATTRIBUTE_TEST to XPathBundle.message("find-usages.attribute"),
        XPathElementType.SIMPLE_TYPE_NAME to XPathBundle.message("find-usages.type"),
        XPathElementType.TYPE_NAME to XPathBundle.message("find-usages.type"),
        XPathElementType.UNION_TYPE to XPathBundle.message("find-usages.type"),
        XPathElementType.VAR_NAME to XPathBundle.message("find-usages.variable")
    )

    override fun getWordsScanner(): WordsScanner? = XPathWordsScanner()

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean = psiElement is PsiNamedElement

    override fun getHelpId(psiElement: PsiElement): String? = HelpID.FIND_OTHER_USAGES

    override fun getType(element: PsiElement): String {
        val parentType = element.parent.node.elementType
        return if (parentType === XPathElementType.NAME_TEST)
            when ((element.parent.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
                XPathPrincipalNodeKind.Attribute -> XPathBundle.message("find-usages.attribute")
                XPathPrincipalNodeKind.Namespace -> XPathBundle.message("find-usages.namespace")
                else -> XPathBundle.message("find-usages.identifier")
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
