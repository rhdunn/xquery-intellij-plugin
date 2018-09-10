/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
@file:Suppress("PackageName")

package uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0003

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.inspections.Inspection
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

class ReservedFunctionNameInspection : Inspection("ijst/IJST0002.md") {
    private fun getLocalName(name: XPathEQName?): Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType>? {
        if (name != null && name.node.elementType === XQueryElementType.NCNAME) {
            val localname = name.firstChild
            val type = localname.node.elementType
            if (type is IXQueryKeywordOrNCNameType) {
                return Pair(localname, type.keywordType)
            }
        }
        return null
    }

    private fun getLocalName(name: XsQNameValue?): Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType>? {
        if (name != null && name.isLexicalQName && name.prefix == null) {
            val localname = name.localName
            val type = (localname as PsiElement).node.elementType
            if (type is IXQueryKeywordOrNCNameType) {
                return Pair(localname, type.keywordType)
            }
        }
        return null
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val product = settings.product
        val productVersion = settings.productVersion

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().forEach { element ->
            val localname = when (element) {
                is XPathFunctionReference -> getLocalName(element.functionName)
                is XQueryFunctionDecl -> getLocalName(element.functionName)
                else -> null
            }
            when (localname?.second) {
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, MarkLogic.VERSION_7_0)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", MarkLogic.VERSION_7_0)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, MarkLogic.VERSION_8_0)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", MarkLogic.VERSION_8_0)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, Scripting.NOTE_1_0_20140918)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", Scripting.NOTE_1_0_20140918)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IXQueryKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, XQuery.REC_3_0_20140408)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", XQuery.REC_3_0_20140408)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME -> {
                    val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", XQuery.REC_1_0_20070123)
                    descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                }
                IXQueryKeywordOrNCNameType.KeywordType.KEYWORD -> {}
            }
        }
        return descriptors.toTypedArray()
    }
}