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

import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginOtherwiseExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginParamRef
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresAny
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion

object SaxonSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginOtherwiseExpr -> reporter.requires(element, SAXON_PE_10)
        is PluginParamRef -> reporter.requires(element, SAXON_PE_10)
        else -> {
        }
    }

    private val SAXON_PE_10 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_10_0),
        XpmRequiresProductVersion(SaxonEE.VERSION_10_0)
    )
}
