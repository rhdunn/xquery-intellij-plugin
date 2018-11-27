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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.ScriptingSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings

class IJVS0002 : Inspection("ijvs/IJVS0002.md", Resources) {
    private fun getLocalName(name: XsQNameValue?): Pair<PsiElement, IKeywordOrNCNameType.KeywordType>? {
        if (name != null && name.isLexicalQName && name.prefix == null) {
            val localname = name.localName?.element!!
            val type = localname.node.elementType
            if (type is IKeywordOrNCNameType) {
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
                IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, MarkLogic.VERSION_7_0)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", MarkLogic.VERSION_7_0)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, MarkLogic.VERSION_8_0)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", MarkLogic.VERSION_8_0)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, ScriptingSpec.NOTE_1_0_20140918)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", ScriptingSpec.NOTE_1_0_20140918)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME -> {
                    if (product.conformsTo(productVersion, XQuerySpec.REC_3_0_20140408)) {
                        val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", XQuerySpec.REC_3_0_20140408)
                        descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME -> {
                    val description = XQueryBundle.message("inspection.XPST0003.reserved-function-name.message", XQuerySpec.REC_1_0_20070123)
                    descriptors.add(manager.createProblemDescriptor(localname.first, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                }
                IKeywordOrNCNameType.KeywordType.KEYWORD -> {}
            }
        }
        return descriptors.toTypedArray()
    }
}