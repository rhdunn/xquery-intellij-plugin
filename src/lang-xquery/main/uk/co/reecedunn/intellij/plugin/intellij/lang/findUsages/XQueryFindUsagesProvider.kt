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
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceType
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType

object XQueryFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner? {
        return XQueryWordsScanner()
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is PsiNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return HelpID.FIND_OTHER_USAGES
    }

    override fun getType(element: PsiElement): String {
        val parentType = element.parent.node.elementType
        return if (parentType === XPathElementType.NAME_TEST)
            when ((element.parent.parent as? XPathNodeTest)?.getPrincipalNodeKind()) {
                XPathPrincipalNodeKind.Element -> XQueryBundle.message("find-usages.element")
                XPathPrincipalNodeKind.Attribute -> XQueryBundle.message("find-usages.attribute")
                XPathPrincipalNodeKind.Namespace -> XQueryBundle.message("find-usages.namespace")
                null -> XQueryBundle.message("find-usages.identifier")
            }
        else when (element.parent) {
            is XQueryFunctionDecl -> XQueryBundle.message("find-usages.function")
            is XPathVarName -> {
                if (element.parent.parent is XQueryVarDecl)
                    XQueryBundle.message("find-usages.variable")
                else
                    XQueryBundle.message("find-usages.identifier")
            }
            else -> XQueryBundle.message("find-usages.identifier")
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        val name = (element as PsiNamedElement).name
        return name ?: ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return getDescriptiveName(element)
    }
}
