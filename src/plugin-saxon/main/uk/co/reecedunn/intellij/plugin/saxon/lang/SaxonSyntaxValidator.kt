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
package uk.co.reecedunn.intellij.plugin.saxon.lang

import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresAny
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersionRange

object SaxonSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginContextItemFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.DOT, XPathTokenType.CONTEXT_FUNCTION -> reporter.requires(element, SAXON_PE_10)
            else -> reporter.requires(element, SAXON_PE_9_9_ONLY)
        }
        is PluginLambdaFunctionExpr -> reporter.requires(element, SAXON_PE_10)
        is PluginOtherwiseExpr -> reporter.requires(element, SAXON_PE_10)
        is PluginParamRef -> reporter.requires(element, SAXON_PE_10)
        is PluginUnionType -> reporter.requires(element, SAXON_PE_9_8)
        else -> {
        }
    }

    private val SAXON_PE_9_8 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_9_8),
        XpmRequiresProductVersion(SaxonEE.VERSION_9_8)
    )

    private val SAXON_PE_9_9_ONLY = XpmRequiresAny(
        XpmRequiresProductVersionRange(SaxonPE.VERSION_9_9, SaxonPE.VERSION_9_9),
        XpmRequiresProductVersionRange(SaxonEE.VERSION_9_9, SaxonEE.VERSION_9_9)
    )

    private val SAXON_PE_10 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_10_0),
        XpmRequiresProductVersion(SaxonEE.VERSION_10_0)
    )
}
