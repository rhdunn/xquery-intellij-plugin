/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginAnyItemTypePsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginAnyTextTestPsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginQuantifiedExprBindingPsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.PluginWildcardIndicatorPsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.*

object XPathElementType {
    // region XPath 1.0

    val XPATH = IFileElementType(XPath)

    val AND_EXPR: IElementType = ICompositeElementType(
        "XQUERY_AND_EXPR",
        XPathAndExprPsiImpl::class.java,
        XPath
    )

    val ADDITIVE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ADDITIVE_EXPR",
        XPathAdditiveExprPsiImpl::class.java,
        XPath
    )

    val ABBREV_FORWARD_STEP: IElementType = ICompositeElementType(
        "XQUERY_ABBREV_FORWARD_STEP",
        XPathAbbrevForwardStepPsiImpl::class.java,
        XPath
    )

    val ABBREV_REVERSE_STEP: IElementType = ICompositeElementType(
        "XQUERY_ABBREV_REVERSE_STEP",
        XPathAbbrevReverseStepPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.1

    val ANY_ARRAY_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_ARRAY_TEST",
        XPathAnyArrayTestPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XQuery IntelliJ Plugin

    val QUANTIFIED_EXPR_BINDING: IElementType = ICompositeElementType(
        "XQUERY_QUANTIFIED_EXPR_BINDING",
        PluginQuantifiedExprBindingPsiImpl::class.java,
        XPath
    )

    val ANY_TEXT_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_TEXT_TEST",
        PluginAnyTextTestPsiImpl::class.java,
        XPath
    )

    val WILDCARD_INDICATOR: IElementType = ICompositeElementType(
        "XQUERY_WILDCARD_INDICATOR",
        PluginWildcardIndicatorPsiImpl::class.java,
        XPath
    )

    val ANY_ITEM_TYPE: IElementType = ICompositeElementType(
        "XQUERY_ANY_ITEM_TYPE",
        PluginAnyItemTypePsiImpl::class.java,
        XPath
    )

    // endregion
}
