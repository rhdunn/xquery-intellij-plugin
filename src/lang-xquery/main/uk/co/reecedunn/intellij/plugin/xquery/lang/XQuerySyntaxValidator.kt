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
package uk.co.reecedunn.intellij.plugin.xquery.lang

import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEnumerationType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathKeySpecifier
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathKeywordArgument
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWithExpr
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresLanguageVersion
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

object XQuerySyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is XPathEnumerationType -> reporter.requires(element, XQUERY_4_0)
        is XPathKeySpecifier -> when (element.conformanceElement.elementType) {
            XPathElementType.STRING_LITERAL -> reporter.requires(element, XQUERY_4_0)
            XPathElementType.VAR_REF -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XPathKeywordArgument -> reporter.requires(element, XQUERY_4_0)
        is XPathWithExpr -> reporter.requires(element, XQUERY_4_0)
        else -> {
        }
    }

    private val XQUERY_4_0 = XpmRequiresLanguageVersion(XQuery.VERSION_4_0)
}
