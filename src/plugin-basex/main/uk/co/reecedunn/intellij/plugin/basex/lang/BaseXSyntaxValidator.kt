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

import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginFTFuzzyOption
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginUpdateExpr

object BaseXSyntaxValidator : XpmSyntaxValidator {
    // region XpmSyntaxValidator

    override fun validate(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) = when (element) {
        is PluginFTFuzzyOption -> reporter.requireProduct(element, BaseX.VERSION_6_1)
        is PluginUpdateExpr -> reporter.requireProduct(element, BaseX.VERSION_7_8)
        else -> {}
    }

    // endregion
}
