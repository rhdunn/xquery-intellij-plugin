/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.full.text.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.*

object XPathElementType {
    // region XPath 1.0

    val XPATH = IFileElementType(XPath)

    val EXPR: IElementType = ICompositeElementType(
        "XPATH_EXPR",
        XPathExprPsiImpl::class.java,
        XPath
    )

    val OR_EXPR: IElementType = ICompositeElementType(
        "XPATH_OR_EXPR",
        XPathOrExprPsiImpl::class.java,
        XPath
    )

    val AND_EXPR: IElementType = ICompositeElementType(
        "XPATH_AND_EXPR",
        XPathAndExprPsiImpl::class.java,
        XPath
    )

    val ADDITIVE_EXPR: IElementType = ICompositeElementType(
        "XPATH_ADDITIVE_EXPR",
        XPathAdditiveExprPsiImpl::class.java,
        XPath
    )

    val MULTIPLICATIVE_EXPR: IElementType = ICompositeElementType(
        "XPATH_MULTIPLICATIVE_EXPR",
        XPathMultiplicativeExprPsiImpl::class.java,
        XPath
    )

    val UNARY_EXPR: IElementType = ICompositeElementType(
        "XPATH_UNARY_EXPR",
        XPathUnaryExprPsiImpl::class.java,
        XPath
    )

    val UNION_EXPR: IElementType = ICompositeElementType(
        "XPATH_UNION_EXPR",
        XPathUnionExprPsiImpl::class.java,
        XPath
    )

    val PATH_EXPR: IElementType = ICompositeElementType(
        "XPATH_PATH_EXPR",
        XPathPathExprPsiImpl::class.java,
        XPath
    )

    val RELATIVE_PATH_EXPR: IElementType = ICompositeElementType(
        "XPATH_RELATIVE_PATH_EXPR",
        XPathRelativePathExprPsiImpl::class.java,
        XPath
    )

    val AXIS_STEP: IElementType = ICompositeElementType(
        "XPATH_AXIS_STEP",
        XPathAxisStepPsiImpl::class.java,
        XPath
    )

    val FORWARD_STEP: IElementType = ICompositeElementType(
        "XPATH_FORWARD_STEP",
        XPathForwardStepPsiImpl::class.java,
        XPath
    )

    val FORWARD_AXIS: IElementType = ICompositeElementType(
        "XPATH_FORWARD_AXIS",
        XPathForwardAxisPsiImpl::class.java,
        XPath
    )

    val ABBREV_FORWARD_STEP: IElementType = ICompositeElementType(
        "XPATH_ABBREV_FORWARD_STEP",
        XPathAbbrevForwardStepPsiImpl::class.java,
        XPath
    )

    val REVERSE_STEP: IElementType = ICompositeElementType(
        "XPATH_REVERSE_STEP",
        XPathReverseStepPsiImpl::class.java,
        XPath
    )

    val REVERSE_AXIS: IElementType = ICompositeElementType(
        "XPATH_REVERSE_AXIS",
        XPathReverseAxisPsiImpl::class.java,
        XPath
    )

    val ABBREV_REVERSE_STEP: IElementType = ICompositeElementType(
        "XPATH_ABBREV_REVERSE_STEP",
        XPathAbbrevReverseStepPsiImpl::class.java,
        XPath
    )

    val NODE_TEST: IElementType = ICompositeElementType(
        "XPATH_NODE_TEST",
        XPathNodeTestPsiImpl::class.java,
        XPath
    )

    val NAME_TEST: IElementType = ICompositeElementType(
        "XPATH_NAME_TEST",
        XPathNameTestPsiImpl::class.java,
        XPath
    )

    val WILDCARD: IElementType = ICompositeElementType(
        "XPATH_WILDCARD",
        XPathWildcardPsiImpl::class.java,
        XPath
    )

    val PREDICATE_LIST: IElementType = ICompositeElementType(
        "XPATH_PREDICATE_LIST",
        XPathPredicateListPsiImpl::class.java,
        XPath
    )

    val PREDICATE: IElementType = ICompositeElementType(
        "XPATH_PREDICATE",
        XPathPredicatePsiImpl::class.java,
        XPath
    )

    val VAR_REF: IElementType = ICompositeElementType(
        "XPATH_VAR_REF",
        XPathVarRefPsiImpl::class.java,
        XPath
    )

    val VAR_NAME: IElementType = ICompositeElementType(
        "XPATH_VAR_NAME",
        XPathVarNamePsiImpl::class.java,
        XPath
    )

    val PARENTHESIZED_EXPR: IElementType = ICompositeElementType(
        "XPATH_PARENTHESIZED_EXPR",
        XPathParenthesizedExprPsiImpl::class.java,
        XPath
    )

    val FUNCTION_CALL: IElementType = ICompositeElementType(
        "XPATH_FUNCTION_CALL",
        XPathFunctionCallPsiImpl::class.java,
        XPath
    )

    val ANY_KIND_TEST: IElementType = ICompositeElementType(
        "XPATH_ANY_KIND_TEST",
        XPathAnyKindTestPsiImpl::class.java,
        XPath
    )

    val COMMENT_TEST: IElementType = ICompositeElementType(
        "XPATH_COMMENT_TEST",
        XPathCommentTestPsiImpl::class.java,
        XPath
    )

    val PI_TEST: IElementType = ICompositeElementType(
        "XPATH_PI_TEST",
        XPathPITestPsiImpl::class.java,
        XPath
    )

    val STRING_LITERAL: IElementType = ICompositeElementType(
        "XPATH_STRING_LITERAL",
        XPathStringLiteralPsiImpl::class.java,
        XPath
    )

    val QNAME: IElementType = ICompositeElementType(
        "XPATH_QNAME",
        XPathQNamePsiImpl::class.java,
        XPath
    )

    val NCNAME: IElementType = ICompositeElementType(
        "XPATH_NCNAME",
        XPathNCNamePsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 2.0

    val FOR_EXPR: IElementType = ICompositeElementType(
        "XPATH_FOR_EXPR",
        XPathForExprPsiImpl::class.java,
        XPath
    )

    val QUANTIFIED_EXPR: IElementType = ICompositeElementType(
        "XPATH_QUANTIFIED_EXPR",
        XPathQuantifiedExprPsiImpl::class.java,
        XPath
    )

    val IF_EXPR: IElementType = ICompositeElementType(
        "XPATH_IF_EXPR",
        XPathIfExprPsiImpl::class.java,
        XPath
    )

    val COMPARISON_EXPR: IElementType = ICompositeElementType(
        "XPATH_COMPARISON_EXPR",
        XPathComparisonExprPsiImpl::class.java,
        XPath
    )

    val RANGE_EXPR: IElementType = ICompositeElementType(
        "XPATH_RANGE_EXPR",
        XPathRangeExprPsiImpl::class.java,
        XPath
    )

    val INTERSECT_EXCEPT_EXPR: IElementType = ICompositeElementType(
        "XPATH_INTERSECT_EXCEPT_EXPR",
        XPathIntersectExceptExprPsiImpl::class.java,
        XPath
    )

    val INSTANCEOF_EXPR: IElementType = ICompositeElementType(
        "XPATH_INSTANCEOF_EXPR",
        XPathInstanceofExprPsiImpl::class.java,
        XPath
    )

    val TREAT_EXPR: IElementType = ICompositeElementType(
        "XPATH_TREAT_EXPR",
        XPathTreatExprPsiImpl::class.java,
        XPath
    )

    val CASTABLE_EXPR: IElementType = ICompositeElementType(
        "XPATH_CASTABLE_EXPR",
        XPathCastableExprPsiImpl::class.java,
        XPath
    )

    val CAST_EXPR: IElementType = ICompositeElementType(
        "XPATH_CAST_EXPR",
        XPathCastExprPsiImpl::class.java,
        XPath
    )

    val SINGLE_TYPE: IElementType = ICompositeElementType(
        "XPATH_SINGLE_TYPE",
        XPathSingleTypePsiImpl::class.java,
        XPath
    )

    val SEQUENCE_TYPE: IElementType = ICompositeElementType(
        "XPATH_SEQUENCE_TYPE",
        XPathSequenceTypePsiImpl::class.java,
        XPath
    )

    val DOCUMENT_TEST: IElementType = ICompositeElementType(
        "XPATH_DOCUMENT_TEST",
        XPathDocumentTestPsiImpl::class.java,
        XPath
    )

    val NAMESPACE_NODE_TEST: IElementType = ICompositeElementType(
        "XPATH_NAMESPACE_NODE_TEST",
        XPathNamespaceNodeTestPsiImpl::class.java,
        XPath
    )

    val ATTRIBUTE_TEST: IElementType = ICompositeElementType(
        "XPATH_ATTRIBUTE_TEST",
        XPathAttributeTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ATTRIBUTE_TEST: IElementType = ICompositeElementType(
        "XPATH_SCHEMA_ATTRIBUTE_TEST",
        XPathSchemaAttributeTestPsiImpl::class.java,
        XPath
    )

    val ELEMENT_TEST: IElementType = ICompositeElementType(
        "XPATH_ELEMENT_TEST",
        XPathElementTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ELEMENT_TEST: IElementType = ICompositeElementType(
        "XPATH_SCHEMA_ELEMENT_TEST",
        XPathSchemaElementTestPsiImpl::class.java,
        XPath
    )

    val SIMPLE_TYPE_NAME: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_TYPE_NAME",
        XPathSimpleTypeNamePsiImpl::class.java,
        XPath
    )

    val TYPE_NAME: IElementType = ICompositeElementType(
        "XPATH_TYPE_NAME",
        XPathTypeNamePsiImpl::class.java,
        XPath
    )

    val COMMENT: IElementType = ICompositeElementType(
        "XPATH_COMMENT",
        XPathCommentPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.0

    val ANY_FUNCTION_TEST: IElementType = ICompositeElementType(
        "XPATH_ANY_FUNCTION_TEST",
        XPathAnyFunctionTestPsiImpl::class.java,
        XPath
    )

    val PARAM_LIST: IElementType = ICompositeElementType(
        "XPATH_PARAM_LIST",
        XPathParamListPsiImpl::class.java,
        XPath
    )

    val PARAM: IElementType = ICompositeElementType(
        "XPATH_PARAM",
        XPathParamPsiImpl::class.java,
        XPath
    )

    val SIMPLE_FOR_CLAUSE: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_FOR_CLAUSE",
        XPathSimpleForClausePsiImpl::class.java,
        XPath
    )

    val SIMPLE_FOR_BINDING: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_FOR_BINDING",
        XPathSimpleForBindingPsiImpl::class.java,
        XPath
    )

    val LET_EXPR: IElementType = ICompositeElementType(
        "XPATH_LET_EXPR",
        XPathLetExprPsiImpl::class.java,
        XPath
    )

    val SIMPLE_LET_CLAUSE: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_LET_CLAUSE",
        XPathSimpleLetClausePsiImpl::class.java,
        XPath
    )

    val SIMPLE_LET_BINDING: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_LET_BINDING",
        XPathSimpleLetBindingPsiImpl::class.java,
        XPath
    )

    val FUNCTION_BODY: IElementType = ICompositeElementType(
        "XPATH_FUNCTION_BODY",
        XPathFunctionBodyPsiImpl::class.java,
        XPath
    )

    val ENCLOSED_EXPR: IElementType = ICompositeElementType(
        "XPATH_ENCLOSED_EXPR",
        XPathEnclosedExprPsiImpl::class.java,
        XPath
    )

    val STRING_CONCAT_EXPR: IElementType = ICompositeElementType(
        "XPATH_STRING_CONCAT_EXPR",
        XPathStringConcatExprPsiImpl::class.java,
        XPath
    )

    val SIMPLE_MAP_EXPR: IElementType = ICompositeElementType(
        "XPATH_SIMPLE_MAP_EXPR",
        XPathSimpleMapExprPsiImpl::class.java,
        XPath
    )

    val POSTFIX_EXPR: IElementType = ICompositeElementType(
        "XPATH_POSTFIX_EXPR",
        XPathPostfixExprPsiImpl::class.java,
        XPath
    )

    val ARGUMENT_LIST: IElementType = ICompositeElementType(
        "XPATH_ARGUMENT_LIST",
        XPathArgumentListPsiImpl::class.java,
        XPath
    )

    val ARGUMENT: IElementType = ICompositeElementType(
        "XPATH_ARGUMENT",
        XPathArgumentPsiImpl::class.java,
        XPath
    )

    val ARGUMENT_PLACEHOLDER: IElementType = ICompositeElementType(
        "XPATH_ARGUMENT_PLACEHOLDER",
        XPathArgumentPlaceholderPsiImpl::class.java,
        XPath
    )

    val NAMED_FUNCTION_REF: IElementType = ICompositeElementType(
        "XPATH_NAMED_FUNCTION_REF",
        XPathNamedFunctionRefPsiImpl::class.java,
        XPath
    )

    val INLINE_FUNCTION_EXPR: IElementType = ICompositeElementType(
        "XPATH_INLINE_FUNCTION_EXPR",
        XPathInlineFunctionExprPsiImpl::class.java,
        XPath
    )

    val ATOMIC_OR_UNION_TYPE: IElementType = ICompositeElementType(
        "XPATH_ATOMIC_OR_UNION_TYPE",
        XPathAtomicOrUnionTypePsiImpl::class.java,
        XPath
    )

    val TYPED_FUNCTION_TEST: IElementType = ICompositeElementType(
        "XPATH_TYPED_FUNCTION_TEST",
        XPathTypedFunctionTestPsiImpl::class.java,
        XPath
    )

    val PARENTHESIZED_ITEM_TYPE: IElementType = ICompositeElementType(
        "XPATH_PARENTHESIZED_ITEM_TYPE",
        XPathParenthesizedItemTypePsiImpl::class.java,
        XPath
    )

    val URI_QUALIFIED_NAME: IElementType = ICompositeElementType(
        "XPATH_URI_QUALIFIED_NAME",
        XPathURIQualifiedNamePsiImpl::class.java,
        XPath
    )

    val BRACED_URI_LITERAL: IElementType = ICompositeElementType(
        "XPATH_BRACED_URI_LITERAL",
        XPathBracedURILiteralPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.1

    val ARROW_EXPR: IElementType = ICompositeElementType(
        "XPATH_ARROW_EXPR",
        XPathArrowExprPsiImpl::class.java,
        XPath
    )

    val LOOKUP: IElementType = ICompositeElementType(
        "XPATH_LOOKUP",
        XPathLookupPsiImpl::class.java,
        XPath
    )

    val KEY_SPECIFIER: IElementType = ICompositeElementType(
        "XPATH_KEY_SPECIFIER",
        XPathKeySpecifierPsiImpl::class.java,
        XPath
    )

    val ARROW_FUNCTION_SPECIFIER: IElementType = ICompositeElementType(
        "XPATH_ARROW_FUNCTION_SPECIFIER",
        XPathArrowFunctionSpecifierPsiImpl::class.java,
        XPath
    )

    val MAP_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XPATH_MAP_CONSTRUCTOR",
        XPathMapConstructorPsiImpl::class.java,
        XPath
    )

    val MAP_CONSTRUCTOR_ENTRY: IElementType = ICompositeElementType(
        "XPATH_MAP_CONSTRUCTOR_ENTRY",
        XPathMapConstructorEntryPsiImpl::class.java,
        XPath
    )

    val MAP_KEY_EXPR: IElementType = ICompositeElementType(
        "XPATH_MAP_KEY_EXPR",
        XPathMapKeyExprPsiImpl::class.java,
        XPath
    )

    val MAP_VALUE_EXPR: IElementType = ICompositeElementType(
        "XPATH_MAP_VALUE_EXPR",
        XPathMapValueExprPsiImpl::class.java,
        XPath
    )

    val SQUARE_ARRAY_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XPATH_SQUARE_ARRAY_CONSTRUCTOR",
        XPathSquareArrayConstructorPsiImpl::class.java,
        XPath
    )

    val CURLY_ARRAY_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XPATH_CURLY_ARRAY_CONSTRUCTOR",
        XPathCurlyArrayConstructorPsiImpl::class.java,
        XPath
    )

    val UNARY_LOOKUP: IElementType = ICompositeElementType(
        "XPATH_UNARY_LOOKUP",
        XPathUnaryLookupPsiImpl::class.java,
        XPath
    )

    val ANY_MAP_TEST: IElementType = ICompositeElementType(
        "XPATH_ANY_MAP_TEST",
        XPathAnyMapTestPsiImpl::class.java,
        XPath
    )

    val TYPED_MAP_TEST: IElementType = ICompositeElementType(
        "XPATH_TYPED_MAP_TEST",
        XPathTypedMapTestPsiImpl::class.java,
        XPath
    )

    val ANY_ARRAY_TEST: IElementType = ICompositeElementType(
        "XPATH_ANY_ARRAY_TEST",
        XPathAnyArrayTestPsiImpl::class.java,
        XPath
    )

    val TYPED_ARRAY_TEST: IElementType = ICompositeElementType(
        "XPATH_TYPED_ARRAY_TEST",
        XPathTypedArrayTestPsiImpl::class.java,
        XPath
    )

    // endregion
    // region Full Text 1.0

    val FT_CASE_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_CASE_OPTION",
        FTCaseOptionPsiImpl::class.java,
        XPath
    )

    val FT_DIACRITICS_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_DIACRITICS_OPTION",
        FTDiacriticsOptionPsiImpl::class.java,
        XPath
    )

    val FT_EXTENSION_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_EXTENSION_OPTION",
        FTExtensionOptionPsiImpl::class.java,
        XPath
    )

    val FT_LANGUAGE_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_LANGUAGE_OPTION",
        FTLanguageOptionPsiImpl::class.java,
        XPath
    )

    val FT_STEM_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_STEM_OPTION",
        FTStemOptionPsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORD_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_STOP_WORD_OPTION",
        FTStopWordOptionPsiImpl::class.java,
        XPath
    )

    val FT_THESAURUS_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_THESAURUS_OPTION",
        FTThesaurusOptionPsiImpl::class.java,
        XPath
    )

    val FT_WILDCARD_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_WILDCARD_OPTION",
        FTWildCardOptionPsiImpl::class.java,
        XPath
    )

    val FT_THESAURUS_ID: IElementType = ICompositeElementType(
        "XPATH_FT_THESAURUS_ID",
        FTThesaurusIDPsiImpl::class.java,
        XPath
    )

    val FT_LITERAL_RANGE: IElementType = ICompositeElementType(
        "XPATH_FT_LITERAL_RANGE",
        FTLiteralRangePsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORDS: IElementType = ICompositeElementType(
        "XPATH_FT_STOP_WORDS",
        FTStopWordsPsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORDS_INCL_EXCL: IElementType = ICompositeElementType(
        "XPATH_FT_STOP_WORDS_INCL_EXCL",
        FTStopWordsInclExclPsiImpl::class.java,
        XPath
    )

    val FT_SCORE_VAR: IElementType = ICompositeElementType(
        "XPATH_FT_SCORE_VAR",
        FTScoreVarPsiImpl::class.java,
        XPath
    )

    val FT_CONTAINS_EXPR: IElementType = ICompositeElementType(
        "XPATH_FT_CONTAINS_EXPR",
        FTContainsExprPsiImpl::class.java,
        XPath
    )

    val FT_SELECTION: IElementType = ICompositeElementType(
        "XPATH_FT_SELECTION",
        FTSelectionPsiImpl::class.java,
        XPath
    )

    val FT_OR: IElementType = ICompositeElementType(
        "XPATH_FT_OR",
        FTOrPsiImpl::class.java,
        XPath
    )

    val FT_AND: IElementType = ICompositeElementType(
        "XPATH_FT_AND",
        FTAndPsiImpl::class.java,
        XPath
    )

    val FT_MILD_NOT: IElementType = ICompositeElementType(
        "XPATH_FT_MILD_NOT",
        FTMildNotPsiImpl::class.java,
        XPath
    )

    val FT_UNARY_NOT: IElementType = ICompositeElementType(
        "XPATH_FT_UNARY_NOT",
        FTUnaryNotPsiImpl::class.java,
        XPath
    )

    val FT_PRIMARY_WITH_OPTIONS: IElementType = ICompositeElementType(
        "XPATH_FT_PRIMARY_WITH_OPTIONS",
        FTPrimaryWithOptionsPsiImpl::class.java,
        XPath
    )

    val FT_PRIMARY: IElementType = ICompositeElementType(
        "XPATH_FT_PRIMARY",
        FTPrimaryPsiImpl::class.java,
        XPath
    )

    val FT_WORDS: IElementType = ICompositeElementType(
        "XPATH_FT_WORDS",
        FTWordsPsiImpl::class.java,
        XPath
    )

    val FT_WORDS_VALUE: IElementType = ICompositeElementType(
        "XPATH_FT_WORDS_VALUE",
        FTWordsValuePsiImpl::class.java,
        XPath
    )

    val FT_EXTENSION_SELECTION: IElementType = ICompositeElementType(
        "XPATH_FT_EXTENSION_SELECTION",
        FTExtensionSelectionPsiImpl::class.java,
        XPath
    )

    val FT_ANYALL_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_ANYALL_OPTION",
        FTAnyallOptionPsiImpl::class.java,
        XPath
    )

    val FT_TIMES: IElementType = ICompositeElementType(
        "XPATH_FT_TIMES",
        FTTimesPsiImpl::class.java,
        XPath
    )

    val FT_RANGE: IElementType = ICompositeElementType(
        "XPATH_FT_RANGE",
        FTRangePsiImpl::class.java,
        XPath
    )

    val FT_WEIGHT: IElementType = ICompositeElementType(
        "XPATH_FT_WEIGHT",
        FTWeightPsiImpl::class.java,
        XPath
    )

    val FT_ORDER: IElementType = ICompositeElementType(
        "XPATH_FT_ORDER",
        FTOrderPsiImpl::class.java,
        XPath
    )

    val FT_WINDOW: IElementType = ICompositeElementType(
        "XPATH_FT_WINDOW",
        FTWindowPsiImpl::class.java,
        XPath
    )

    val FT_DISTANCE: IElementType = ICompositeElementType(
        "XPATH_FT_DISTANCE",
        FTDistancePsiImpl::class.java,
        XPath
    )

    val FT_SCOPE: IElementType = ICompositeElementType(
        "XPATH_FT_SCOPE",
        FTScopePsiImpl::class.java,
        XPath
    )

    val FT_CONTENT: IElementType = ICompositeElementType(
        "XPATH_FT_CONTENT",
        FTContentPsiImpl::class.java,
        XPath
    )

    val FT_UNIT: IElementType = ICompositeElementType(
        "XPATH_FT_UNIT",
        FTUnitPsiImpl::class.java,
        XPath
    )

    val FT_BIG_UNIT: IElementType = ICompositeElementType(
        "XPATH_FT_BIG_UNIT",
        FTBigUnitPsiImpl::class.java,
        XPath
    )

    val FT_IGNORE_OPTION: IElementType = ICompositeElementType(
        "XPATH_FT_IGNORE_OPTION",
        FTIgnoreOptionPsiImpl::class.java,
        XPath
    )

    // endregion
    // region EXPath XPath/XPath NG

    val TERNARY_IF_EXPR: IElementType = ICompositeElementType(
        "XPATH_TERNARY_IF_EXPR",
        PluginTernaryIfExprPsiImpl::class.java,
        XPath
    )

    val ELVIS_EXPR: IElementType = ICompositeElementType(
        "XPATH_ELVIS_EXPR",
        PluginElvisExprPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XQuery IntelliJ Plugin

    val QUANTIFIED_EXPR_BINDING: IElementType = ICompositeElementType(
        "XPATH_QUANTIFIED_EXPR_BINDING",
        PluginQuantifiedExprBindingPsiImpl::class.java,
        XPath
    )

    val ANY_TEXT_TEST: IElementType = ICompositeElementType(
        "XPATH_ANY_TEXT_TEST",
        PluginAnyTextTestPsiImpl::class.java,
        XPath
    )

    val ANY_ITEM_TYPE: IElementType = ICompositeElementType(
        "XPATH_ANY_ITEM_TYPE",
        PluginAnyItemTypePsiImpl::class.java,
        XPath
    )

    val EMPTY_SEQUENCE_TYPE: IElementType = ICompositeElementType(
        "XPATH_EMPTY_SEQUENCE_TYPE",
        PluginEmptySequenceTypePsiImpl::class.java,
        XPath
    )

    val NILLABLE_TYPE_NAME: IElementType = ICompositeElementType(
        "XPATH_NILLABLE_TYPE_NAME",
        PluginNillableTypeNamePsiImpl::class.java,
        XPath
    )

    val RETURN_CLAUSE: IElementType = ICompositeElementType(
        "XPATH_RETURN_CLAUSE",
        PluginReturnClausePsiImpl::class.java,
        XPath
    )

    val SEQUENCE_TYPE_LIST: IElementType = ICompositeElementType(
        "XPATH_SEQUENCE_TYPE_LIST",
        PluginSequenceTypeListPsiImpl::class.java,
        XPath
    )

    // endregion
}
