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
package uk.co.reecedunn.intellij.plugin.xijp.lang

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginSequenceTypeList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypedFunctionTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCaseClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySequenceTypeUnion

object XQueryIntelliJPluginSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginSequenceTypeList -> when ((element as PsiElement).parent) {
            is XPathTypedFunctionTest -> {
            }
            else -> reporter.requires(element, XIJP_1_3)
        }
        is XPathInlineFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.ELLIPSIS -> reporter.requires(element, XIJP_1_4)
            else -> {
            }
        }
        is XQueryFunctionDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.ELLIPSIS -> reporter.requires(element, XIJP_1_4)
            else -> {
            }
        }
        is XQuerySequenceTypeUnion -> when ((element as PsiElement).parent) {
            is XQueryCaseClause -> {
            }
            else -> reporter.requires(element, XIJP_1_3)
        }
        else -> {
        }
    }

    private val XIJP_1_3 = XpmRequiresProductVersion.since(XQueryIntelliJPlugin.VERSION_1_3)
    private val XIJP_1_4 = XpmRequiresProductVersion.since(XQueryIntelliJPlugin.VERSION_1_4)
}
