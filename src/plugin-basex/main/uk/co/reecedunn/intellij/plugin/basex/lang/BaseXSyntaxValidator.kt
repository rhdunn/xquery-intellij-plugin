/*
 * Copyright (C) 2020 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.basex.intellij.resources.BaseXBundle
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginElvisExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTernaryIfExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIfExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginFTFuzzyOption
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginNonDeterministicFunctionCall
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginUpdateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object BaseXSyntaxValidator : XpmSyntaxValidator {
    override fun validate(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) = when (element) {
        is PluginElvisExpr -> reporter.requireProduct(element, BaseX.VERSION_9_1)
        is PluginFTFuzzyOption -> reporter.requireProduct(element, BaseX.VERSION_6_1)
        is PluginNonDeterministicFunctionCall -> reporter.requireProduct(element, BaseX.VERSION_8_4)
        is PluginTernaryIfExpr -> reporter.requireProduct(element, BaseX.VERSION_9_1)
        is PluginUpdateExpr -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_UPDATE -> reporter.requireProduct(element, BaseX.VERSION_7_8)
            else -> reporter.requireProduct(element, BaseX.VERSION_8_5)
        }
        is XPathIfExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_IF -> reporter.requireProduct(
                element, BaseX.VERSION_9_1, BaseXBundle.message("conformance.if-without-else")
            )
            else -> {}
        }
        else -> {}
    }
}
