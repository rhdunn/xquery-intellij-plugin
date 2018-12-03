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

    val OR_EXPR: IElementType = ICompositeElementType(
        "XQUERY_OR_EXPR",
        XPathOrExprPsiImpl::class.java,
        XPath
    )

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

    val MULTIPLICATIVE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_MULTIPLICATIVE_EXPR",
        XPathMultiplicativeExprPsiImpl::class.java,
        XPath
    )

    val PATH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_PATH_EXPR",
        XPathPathExprPsiImpl::class.java,
        XPath
    )

    val RELATIVE_PATH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_RELATIVE_PATH_EXPR",
        XPathRelativePathExprPsiImpl::class.java,
        XPath
    )

    val AXIS_STEP: IElementType = ICompositeElementType(
        "XQUERY_AXIS_STEP",
        XPathAxisStepPsiImpl::class.java,
        XPath
    )

    val FORWARD_STEP: IElementType = ICompositeElementType(
        "XQUERY_FORWARD_STEP",
        XPathForwardStepPsiImpl::class.java,
        XPath
    )

    val ABBREV_FORWARD_STEP: IElementType = ICompositeElementType(
        "XQUERY_ABBREV_FORWARD_STEP",
        XPathAbbrevForwardStepPsiImpl::class.java,
        XPath
    )

    val REVERSE_STEP: IElementType = ICompositeElementType(
        "XQUERY_REVERSE_STEP",
        XPathReverseStepPsiImpl::class.java,
        XPath
    )

    val REVERSE_AXIS: IElementType = ICompositeElementType(
        "XQUERY_REVERSE_AXIS",
        XPathReverseAxisPsiImpl::class.java,
        XPath
    )

    val ABBREV_REVERSE_STEP: IElementType = ICompositeElementType(
        "XQUERY_ABBREV_REVERSE_STEP",
        XPathAbbrevReverseStepPsiImpl::class.java,
        XPath
    )

    val NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NODE_TEST",
        XPathNodeTestPsiImpl::class.java,
        XPath
    )

    val NAME_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAME_TEST",
        XPathNameTestPsiImpl::class.java,
        XPath
    )

    val PREDICATE_LIST: IElementType = ICompositeElementType(
        "XQUERY_PREDICATE_LIST",
        XPathPredicateListPsiImpl::class.java,
        XPath
    )

    val PREDICATE: IElementType = ICompositeElementType(
        "XQUERY_PREDICATE",
        XPathPredicatePsiImpl::class.java,
        XPath
    )

    val PARENTHESIZED_EXPR: IElementType = ICompositeElementType(
        "XQUERY_PARENTHESIZED_EXPR",
        XPathParenthesizedExprPsiImpl::class.java,
        XPath
    )

    val CONTEXT_ITEM_EXPR: IElementType = ICompositeElementType(
        "XQUERY_CONTEXT_ITEM_EXPR",
        XPathContextItemExprPsiImpl::class.java,
        XPath
    )

    val FUNCTION_CALL: IElementType = ICompositeElementType(
        "XQUERY_FUNCTION_CALL",
        XPathFunctionCallPsiImpl::class.java,
        XPath
    )

    val ANY_KIND_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_KIND_TEST",
        XPathAnyKindTestPsiImpl::class.java,
        XPath
    )

    val COMMENT_TEST: IElementType = ICompositeElementType(
        "XQUERY_COMMENT_TEST",
        XPathCommentTestPsiImpl::class.java,
        XPath
    )

    val PI_TEST: IElementType = ICompositeElementType(
        "XQUERY_PI_TEST",
        XPathPITestPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 2.0

    val QUANTIFIED_EXPR: IElementType = ICompositeElementType(
        "XQUERY_QUANTIFIED_EXPR",
        XPathQuantifiedExprPsiImpl::class.java,
        XPath
    )

    val IF_EXPR: IElementType = ICompositeElementType(
        "XQUERY_IF_EXPR",
        XPathIfExprPsiImpl::class.java,
        XPath
    )

    val COMPARISON_EXPR: IElementType = ICompositeElementType(
        "XQUERY_COMPARISON_EXPR",
        XPathComparisonExprPsiImpl::class.java,
        XPath
    )

    val RANGE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_RANGE_EXPR",
        XPathRangeExprPsiImpl::class.java,
        XPath
    )

    val INTERSECT_EXCEPT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_INTERSECT_EXCEPT_EXPR",
        XPathIntersectExceptExprPsiImpl::class.java,
        XPath
    )

    val INSTANCEOF_EXPR: IElementType = ICompositeElementType(
        "XQUERY_INSTANCEOF_EXPR",
        XPathInstanceofExprPsiImpl::class.java,
        XPath
    )

    val CASTABLE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_CASTABLE_EXPR",
        XPathCastableExprPsiImpl::class.java,
        XPath
    )

    val CAST_EXPR: IElementType = ICompositeElementType(
        "XQUERY_CAST_EXPR",
        XPathCastExprPsiImpl::class.java,
        XPath
    )

    val SINGLE_TYPE: IElementType = ICompositeElementType(
        "XQUERY_SINGLE_TYPE",
        XPathSingleTypePsiImpl::class.java,
        XPath
    )

    val DOCUMENT_TEST: IElementType = ICompositeElementType(
        "XQUERY_DOCUMENT_TEST",
        XPathDocumentTestPsiImpl::class.java,
        XPath
    )

    val NAMESPACE_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMESPACE_NODE_TEST",
        XPathNamespaceNodeTestPsiImpl::class.java,
        XPath
    )

    val ATTRIBUTE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ATTRIBUTE_TEST",
        XPathAttributeTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ATTRIBUTE_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_ATTRIBUTE_TEST",
        XPathSchemaAttributeTestPsiImpl::class.java,
        XPath
    )

    val ELEMENT_TEST: IElementType = ICompositeElementType(
        "XQUERY_ELEMENT_TEST",
        XPathElementTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ELEMENT_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_ELEMENT_TEST",
        XPathSchemaElementTestPsiImpl::class.java,
        XPath
    )

    val COMMENT: IElementType = ICompositeElementType(
        "XQUERY_COMMENT",
        XPathCommentPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.0

    val PARAM: IElementType = ICompositeElementType(
        "XQUERY_PARAM",
        XPathParamPsiImpl::class.java,
        XPath
    )

    val STRING_CONCAT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_STRING_CONCAT_EXPR",
        XPathStringConcatExprPsiImpl::class.java,
        XPath
    )

    val SIMPLE_MAP_EXPR: IElementType = ICompositeElementType(
        "XQUERY_SIMPLE_MAP_EXPR",
        XPathSimpleMapExprPsiImpl::class.java,
        XPath
    )

    val POSTFIX_EXPR: IElementType = ICompositeElementType(
        "XQUERY_POSTFIX_EXPR",
        XPathPostfixExprPsiImpl::class.java,
        XPath
    )

    val ARGUMENT_LIST: IElementType = ICompositeElementType(
        "XQUERY_ARGUMENT_LIST",
        XPathArgumentListPsiImpl::class.java,
        XPath
    )

    val ARGUMENT: IElementType = ICompositeElementType(
        "XQUERY_ARGUMENT",
        XPathArgumentPsiImpl::class.java,
        XPath
    )

    val ARGUMENT_PLACEHOLDER: IElementType = ICompositeElementType(
        "XQUERY_ARGUMENT_PLACEHOLDER",
        XPathArgumentPlaceholderPsiImpl::class.java,
        XPath
    )

    val NAMED_FUNCTION_REF: IElementType = ICompositeElementType(
        "XQUERY_NAMED_FUNCTION_REF",
        XPathNamedFunctionRefPsiImpl::class.java,
        XPath
    )

    val INLINE_FUNCTION_EXPR: IElementType = ICompositeElementType(
        "XQUERY_INLINE_FUNCTION_EXPR",
        XPathInlineFunctionExprPsiImpl::class.java,
        XPath
    )

    val ATOMIC_OR_UNION_TYPE: IElementType = ICompositeElementType(
        "XQUERY_ATOMIC_OR_UNION_TYPE",
        XPathAtomicOrUnionTypePsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.1

    val ARROW_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ARROW_EXPR",
        XPathArrowExprPsiImpl::class.java,
        XPath
    )

    val LOOKUP: IElementType = ICompositeElementType(
        "XQUERY_LOOKUP",
        XPathLookupPsiImpl::class.java,
        XPath
    )

    val KEY_SPECIFIER: IElementType = ICompositeElementType(
        "XQUERY_KEY_SPECIFIER",
        XPathKeySpecifierPsiImpl::class.java,
        XPath
    )

    val ARROW_FUNCTION_SPECIFIER: IElementType = ICompositeElementType(
        "XQUERY_ARROW_FUNCTION_SPECIFIER",
        XPathArrowFunctionSpecifierPsiImpl::class.java,
        XPath
    )

    val MAP_CONSTRUCTOR_ENTRY: IElementType = ICompositeElementType(
        "XQUERY_MAP_CONSTRUCTOR_ENTRY",
        XPathMapConstructorEntryPsiImpl::class.java,
        XPath
    )

    val MAP_KEY_EXPR: IElementType = ICompositeElementType(
        "XQUERY_MAP_KEY_EXPR",
        XPathMapKeyExprPsiImpl::class.java,
        XPath
    )

    val MAP_VALUE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_MAP_VALUE_EXPR",
        XPathMapValueExprPsiImpl::class.java,
        XPath
    )

    val SQUARE_ARRAY_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_SQUARE_ARRAY_CONSTRUCTOR",
        XPathSquareArrayConstructorPsiImpl::class.java,
        XPath
    )

    val ANY_MAP_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_MAP_TEST",
        XPathAnyMapTestPsiImpl::class.java,
        XPath
    )

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
