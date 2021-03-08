/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.lang

import uk.co.reecedunn.intellij.plugin.basex.resources.BaseXBundle
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTernaryConditionalExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIfExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginElvisExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginFTFuzzyOption
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginNonDeterministicFunctionCall
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginUpdateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object BaseXSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginElvisExpr -> reporter.requires(element, BASEX_9_1)
        is PluginFTFuzzyOption -> reporter.requires(element, BASEX_6_1)
        is PluginNonDeterministicFunctionCall -> reporter.requires(element, BASEX_8_4)
        is XPathTernaryConditionalExpr -> reporter.requires(element, BASEX_9_1)
        is PluginUpdateExpr -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_UPDATE -> reporter.requires(element, BASEX_7_8)
            else -> reporter.requires(element, BASEX_8_5)
        }
        is XPathIfExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_IF -> reporter.requires(
                element, BASEX_9_1, BaseXBundle.message("conformance.if-without-else")
            )
            else -> {
            }
        }
        else -> {
        }
    }

    private val BASEX_6_1 = XpmRequiresProductVersion(BaseX.VERSION_6_1)
    private val BASEX_7_8 = XpmRequiresProductVersion(BaseX.VERSION_7_8)
    private val BASEX_8_4 = XpmRequiresProductVersion(BaseX.VERSION_8_4)
    private val BASEX_8_5 = XpmRequiresProductVersion(BaseX.VERSION_8_5)
    private val BASEX_9_1 = XpmRequiresProductVersion(BaseX.VERSION_9_1)
}
