/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.parser.IASTWrapperElementType
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.full.text.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.*

object XPathElementType {
    // region XPath 1.0

    val XPATH: IFileElementType = IFileElementType(XPath)

    val EXPR: IElementType = IASTWrapperElementType(
        "XPATH_EXPR",
        XPathExprPsiImpl::class.java,
        XPath
    )

    val OR_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_OR_EXPR",
        XPathOrExprPsiImpl::class.java,
        XPath
    )

    val AND_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_AND_EXPR",
        XPathAndExprPsiImpl::class.java,
        XPath
    )

    val ADDITIVE_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_ADDITIVE_EXPR",
        XPathAdditiveExprPsiImpl::class.java,
        XPath
    )

    val MULTIPLICATIVE_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_MULTIPLICATIVE_EXPR",
        XPathMultiplicativeExprPsiImpl::class.java,
        XPath
    )

    val UNARY_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_UNARY_EXPR",
        XPathUnaryExprPsiImpl::class.java,
        XPath
    )

    val UNION_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_UNION_EXPR",
        XPathUnionExprPsiImpl::class.java,
        XPath
    )

    val PATH_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_PATH_EXPR",
        XPathPathExprPsiImpl::class.java,
        XPath
    )

    val AXIS_STEP: IElementType = IASTWrapperElementType(
        "XPATH_AXIS_STEP",
        XPathAxisStepPsiImpl::class.java,
        XPath
    )

    val FORWARD_STEP: IElementType = IASTWrapperElementType(
        "XPATH_FORWARD_STEP",
        XPathForwardStepPsiImpl::class.java,
        XPath
    )

    val ABBREV_FORWARD_STEP: IElementType = IASTWrapperElementType(
        "XPATH_ABBREV_FORWARD_STEP",
        XPathAbbrevForwardStepPsiImpl::class.java,
        XPath
    )

    val REVERSE_STEP: IElementType = IASTWrapperElementType(
        "XPATH_REVERSE_STEP",
        XPathReverseStepPsiImpl::class.java,
        XPath
    )

    val NODE_TEST: IElementType = IASTWrapperElementType(
        "XPATH_NODE_TEST",
        XPathNodeTestPsiImpl::class.java,
        XPath
    )

    val NAME_TEST: IElementType = IASTWrapperElementType(
        "XPATH_NAME_TEST",
        XPathNameTestPsiImpl::class.java,
        XPath
    )

    val WILDCARD: IElementType = IASTWrapperElementType(
        "XPATH_WILDCARD",
        XPathWildcardPsiImpl::class.java,
        XPath
    )

    val VAR_REF: IElementType = IASTWrapperElementType(
        "XPATH_VAR_REF",
        XPathVarRefPsiImpl::class.java,
        XPath
    )

    val FUNCTION_CALL: IElementType = IASTWrapperElementType(
        "XPATH_FUNCTION_CALL",
        XPathFunctionCallPsiImpl::class.java,
        XPath
    )

    val ANY_KIND_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_KIND_TEST",
        XPathAnyKindTestPsiImpl::class.java,
        XPath
    )

    val COMMENT_TEST: IElementType = IASTWrapperElementType(
        "XPATH_COMMENT_TEST",
        XPathCommentTestPsiImpl::class.java,
        XPath
    )

    val PI_TEST: IElementType = IASTWrapperElementType(
        "XPATH_PI_TEST",
        XPathPITestPsiImpl::class.java,
        XPath
    )

    val STRING_LITERAL: IElementType = IASTWrapperElementType(
        "XPATH_STRING_LITERAL",
        XPathStringLiteralPsiImpl::class.java,
        XPath
    )

    val QNAME: IElementType = IASTWrapperElementType(
        "XPATH_QNAME",
        XPathQNamePsiImpl::class.java,
        XPath
    )

    val NCNAME: IElementType = IASTWrapperElementType(
        "XPATH_NCNAME",
        XPathNCNamePsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 2.0

    val FOR_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_FOR_EXPR",
        XPathForExprPsiImpl::class.java,
        XPath
    )

    val QUANTIFIED_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_QUANTIFIED_EXPR",
        XPathQuantifiedExprPsiImpl::class.java,
        XPath
    )

    val IF_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_IF_EXPR",
        XPathIfExprPsiImpl::class.java,
        XPath
    )

    val COMPARISON_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_COMPARISON_EXPR",
        XPathComparisonExprPsiImpl::class.java,
        XPath
    )

    val RANGE_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_RANGE_EXPR",
        XPathRangeExprPsiImpl::class.java,
        XPath
    )

    val INTERSECT_EXCEPT_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_INTERSECT_EXCEPT_EXPR",
        XPathIntersectExceptExprPsiImpl::class.java,
        XPath
    )

    val INSTANCEOF_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_INSTANCEOF_EXPR",
        XPathInstanceofExprPsiImpl::class.java,
        XPath
    )

    val TREAT_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_TREAT_EXPR",
        XPathTreatExprPsiImpl::class.java,
        XPath
    )

    val CASTABLE_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_CASTABLE_EXPR",
        XPathCastableExprPsiImpl::class.java,
        XPath
    )

    val CAST_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_CAST_EXPR",
        XPathCastExprPsiImpl::class.java,
        XPath
    )

    val FILTER_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_FILTER_EXPR",
        XPathFilterExprPsiImpl::class.java,
        XPath
    )

    val SINGLE_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_SINGLE_TYPE",
        XPathSingleTypePsiImpl::class.java,
        XPath
    )

    val SEQUENCE_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_SEQUENCE_TYPE",
        XPathSequenceTypePsiImpl::class.java,
        XPath
    )

    val DOCUMENT_TEST: IElementType = IASTWrapperElementType(
        "XPATH_DOCUMENT_TEST",
        XPathDocumentTestPsiImpl::class.java,
        XPath
    )

    val NAMESPACE_NODE_TEST: IElementType = IASTWrapperElementType(
        "XPATH_NAMESPACE_NODE_TEST",
        XPathNamespaceNodeTestPsiImpl::class.java,
        XPath
    )

    val ATTRIBUTE_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ATTRIBUTE_TEST",
        XPathAttributeTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ATTRIBUTE_TEST: IElementType = IASTWrapperElementType(
        "XPATH_SCHEMA_ATTRIBUTE_TEST",
        XPathSchemaAttributeTestPsiImpl::class.java,
        XPath
    )

    val ELEMENT_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ELEMENT_TEST",
        XPathElementTestPsiImpl::class.java,
        XPath
    )

    val SCHEMA_ELEMENT_TEST: IElementType = IASTWrapperElementType(
        "XPATH_SCHEMA_ELEMENT_TEST",
        XPathSchemaElementTestPsiImpl::class.java,
        XPath
    )

    val SIMPLE_TYPE_NAME: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_TYPE_NAME",
        XPathSimpleTypeNamePsiImpl::class.java,
        XPath
    )

    val TYPE_NAME: IElementType = IASTWrapperElementType(
        "XPATH_TYPE_NAME",
        XPathTypeNamePsiImpl::class.java,
        XPath
    )

    val COMMENT: IElementType = IASTWrapperElementType(
        "XPATH_COMMENT",
        XPathCommentPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.0

    val ANY_FUNCTION_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_FUNCTION_TEST",
        XPathAnyFunctionTestPsiImpl::class.java,
        XPath
    )

    val PARAM: IElementType = IASTWrapperElementType(
        "XPATH_PARAM",
        XPathParamPsiImpl::class.java,
        XPath
    )

    val SIMPLE_FOR_CLAUSE: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_FOR_CLAUSE",
        XPathSimpleForClausePsiImpl::class.java,
        XPath
    )

    val SIMPLE_FOR_BINDING: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_FOR_BINDING",
        XPathSimpleForBindingPsiImpl::class.java,
        XPath
    )

    val LET_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_LET_EXPR",
        XPathLetExprPsiImpl::class.java,
        XPath
    )

    val SIMPLE_LET_CLAUSE: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_LET_CLAUSE",
        XPathSimpleLetClausePsiImpl::class.java,
        XPath
    )

    val SIMPLE_LET_BINDING: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_LET_BINDING",
        XPathSimpleLetBindingPsiImpl::class.java,
        XPath
    )

    val STRING_CONCAT_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_STRING_CONCAT_EXPR",
        XPathStringConcatExprPsiImpl::class.java,
        XPath
    )

    val SIMPLE_MAP_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_SIMPLE_MAP_EXPR",
        XPathSimpleMapExprPsiImpl::class.java,
        XPath
    )

    val POSTFIX_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_POSTFIX_EXPR",
        XPathPostfixExprPsiImpl::class.java,
        XPath
    )

    val ARGUMENT_LIST: IElementType = IASTWrapperElementType(
        "XPATH_ARGUMENT_LIST",
        XPathArgumentListPsiImpl::class.java,
        XPath
    )

    val ARGUMENT: IElementType = EXPR

    val ARGUMENT_PLACEHOLDER: IElementType = IASTWrapperElementType(
        "XPATH_ARGUMENT_PLACEHOLDER",
        XPathArgumentPlaceholderPsiImpl::class.java,
        XPath
    )

    val NAMED_FUNCTION_REF: IElementType = IASTWrapperElementType(
        "XPATH_NAMED_FUNCTION_REF",
        XPathNamedFunctionRefPsiImpl::class.java,
        XPath
    )

    val INLINE_FUNCTION_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_INLINE_FUNCTION_EXPR",
        XPathInlineFunctionExprPsiImpl::class.java,
        XPath
    )

    val ATOMIC_OR_UNION_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_ATOMIC_OR_UNION_TYPE",
        XPathAtomicOrUnionTypePsiImpl::class.java,
        XPath
    )

    val TYPED_FUNCTION_TEST: IElementType = IASTWrapperElementType(
        "XPATH_TYPED_FUNCTION_TEST",
        XPathTypedFunctionTestPsiImpl::class.java,
        XPath
    )

    val PARENTHESIZED_ITEM_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_PARENTHESIZED_ITEM_TYPE",
        XPathParenthesizedItemTypePsiImpl::class.java,
        XPath
    )

    val URI_QUALIFIED_NAME: IElementType = IASTWrapperElementType(
        "XPATH_URI_QUALIFIED_NAME",
        XPathURIQualifiedNamePsiImpl::class.java,
        XPath
    )

    val BRACED_URI_LITERAL: IElementType = IASTWrapperElementType(
        "XPATH_BRACED_URI_LITERAL",
        XPathBracedURILiteralPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 3.1

    val ARROW_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_ARROW_EXPR",
        XPathArrowExprPsiImpl::class.java,
        XPath
    )

    val MAP_CONSTRUCTOR: IElementType = IASTWrapperElementType(
        "XPATH_MAP_CONSTRUCTOR",
        XPathMapConstructorPsiImpl::class.java,
        XPath
    )

    val MAP_CONSTRUCTOR_ENTRY: IElementType = IASTWrapperElementType(
        "XPATH_MAP_CONSTRUCTOR_ENTRY",
        XPathMapConstructorEntryPsiImpl::class.java,
        XPath
    )

    val SQUARE_ARRAY_CONSTRUCTOR: IElementType = IASTWrapperElementType(
        "XPATH_SQUARE_ARRAY_CONSTRUCTOR",
        XPathSquareArrayConstructorPsiImpl::class.java,
        XPath
    )

    val CURLY_ARRAY_CONSTRUCTOR: IElementType = IASTWrapperElementType(
        "XPATH_CURLY_ARRAY_CONSTRUCTOR",
        XPathCurlyArrayConstructorPsiImpl::class.java,
        XPath
    )

    val UNARY_LOOKUP: IElementType = IASTWrapperElementType(
        "XPATH_UNARY_LOOKUP",
        XPathUnaryLookupPsiImpl::class.java,
        XPath
    )

    val ANY_MAP_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_MAP_TEST",
        XPathAnyMapTestPsiImpl::class.java,
        XPath
    )

    val TYPED_MAP_TEST: IElementType = IASTWrapperElementType(
        "XPATH_TYPED_MAP_TEST",
        XPathTypedMapTestPsiImpl::class.java,
        XPath
    )

    val ANY_ARRAY_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_ARRAY_TEST",
        XPathAnyArrayTestPsiImpl::class.java,
        XPath
    )

    val TYPED_ARRAY_TEST: IElementType = IASTWrapperElementType(
        "XPATH_TYPED_ARRAY_TEST",
        XPathTypedArrayTestPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XPath 4.0 ED

    val ANY_ITEM_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_ITEM_TEST",
        XPathAnyItemTestPsiImpl::class.java,
        XPath
    )

    val ENUMERATION_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_ENUMERATION_TYPE",
        XPathEnumerationTypePsiImpl::class.java,
        XPath
    )

    val FIELD_DECLARATION: IElementType = IASTWrapperElementType(
        "XPATH_FIELD_DECLARATION",
        XPathFieldDeclarationPsiImpl::class.java,
        XPath
    )

    val KEYWORD_ARGUMENT: IElementType = IASTWrapperElementType(
        "XPATH_KEYWORD_ARGUMENT",
        XPathKeywordArgumentPsiImpl::class.java,
        XPath
    )

    val LOCAL_UNION_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_LOCAL_UNION_TYPE",
        XPathLocalUnionTypePsiImpl::class.java,
        XPath
    )

    val NAMESPACE_DECLARATION: IElementType = IASTWrapperElementType(
        "XPATH_NAMESPACE_DECLARATION",
        XPathNamespaceDeclarationPsiImpl::class.java,
        XPath
    )

    val OTHERWISE_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_OTHERWISE_EXPR",
        XPathOtherwiseExprPsiImpl::class.java,
        XPath
    )

    val POSITIONAL_ARGUMENT_LIST: IElementType = IASTWrapperElementType(
        "XPATH_POSITIONAL_ARGUMENT_LIST",
        XPathPositionalArgumentListPsiImpl::class.java,
        XPath
    )

    val QUANTIFIER_BINDING: IElementType = IASTWrapperElementType(
        "XPATH_QUANTIFIER_BINDING",
        XPathQuantifierBindingPsiImpl::class.java,
        XPath
    )

    val RECORD_TEST: IElementType = IASTWrapperElementType(
        "XPATH_RECORD_TEST",
        XPathRecordTestPsiImpl::class.java,
        XPath
    )

    val SELF_REFERENCE: IElementType = IASTWrapperElementType(
        "XPATH_SELF_REFERENCE",
        XPathSelfReferencePsiImpl::class.java,
        XPath
    )

    val TERNARY_CONDITIONAL_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_TERNARY_CONDITIONAL_EXPR",
        XPathTernaryConditionalExprPsiImpl::class.java,
        XPath
    )

    val WITH_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_WITH_EXPR",
        XPathWithExprPsiImpl::class.java,
        XPath
    )

    // endregion
    // region Full Text 1.0

    val PRAGMA: IElementType = IASTWrapperElementType(
        "XPATH_PRAGMA",
        XPathPragmaPsiImpl::class.java,
        XPath
    )

    val URI_LITERAL: IElementType = IASTWrapperElementType(
        "XPATH_URI_LITERAL",
        XPathUriLiteralPsiImpl::class.java,
        XPath
    )

    val FT_CASE_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_CASE_OPTION",
        FTCaseOptionPsiImpl::class.java,
        XPath
    )

    val FT_DIACRITICS_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_DIACRITICS_OPTION",
        FTDiacriticsOptionPsiImpl::class.java,
        XPath
    )

    val FT_EXTENSION_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_EXTENSION_OPTION",
        FTExtensionOptionPsiImpl::class.java,
        XPath
    )

    val FT_LANGUAGE_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_LANGUAGE_OPTION",
        FTLanguageOptionPsiImpl::class.java,
        XPath
    )

    val FT_STEM_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_STEM_OPTION",
        FTStemOptionPsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORD_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_STOP_WORD_OPTION",
        FTStopWordOptionPsiImpl::class.java,
        XPath
    )

    val FT_THESAURUS_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_THESAURUS_OPTION",
        FTThesaurusOptionPsiImpl::class.java,
        XPath
    )

    val FT_WILDCARD_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_WILDCARD_OPTION",
        FTWildCardOptionPsiImpl::class.java,
        XPath
    )

    val FT_THESAURUS_ID: IElementType = IASTWrapperElementType(
        "XPATH_FT_THESAURUS_ID",
        FTThesaurusIDPsiImpl::class.java,
        XPath
    )

    val FT_LITERAL_RANGE: IElementType = IASTWrapperElementType(
        "XPATH_FT_LITERAL_RANGE",
        FTLiteralRangePsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORDS: IElementType = IASTWrapperElementType(
        "XPATH_FT_STOP_WORDS",
        FTStopWordsPsiImpl::class.java,
        XPath
    )

    val FT_STOP_WORDS_INCL_EXCL: IElementType = IASTWrapperElementType(
        "XPATH_FT_STOP_WORDS_INCL_EXCL",
        FTStopWordsInclExclPsiImpl::class.java,
        XPath
    )

    val FT_SCORE_VAR: IElementType = IASTWrapperElementType(
        "XPATH_FT_SCORE_VAR",
        FTScoreVarPsiImpl::class.java,
        XPath
    )

    val FT_CONTAINS_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_FT_CONTAINS_EXPR",
        FTContainsExprPsiImpl::class.java,
        XPath
    )

    val FT_SELECTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_SELECTION",
        FTSelectionPsiImpl::class.java,
        XPath
    )

    val FT_OR: IElementType = IASTWrapperElementType(
        "XPATH_FT_OR",
        FTOrPsiImpl::class.java,
        XPath
    )

    val FT_AND: IElementType = IASTWrapperElementType(
        "XPATH_FT_AND",
        FTAndPsiImpl::class.java,
        XPath
    )

    val FT_MILD_NOT: IElementType = IASTWrapperElementType(
        "XPATH_FT_MILD_NOT",
        FTMildNotPsiImpl::class.java,
        XPath
    )

    val FT_UNARY_NOT: IElementType = IASTWrapperElementType(
        "XPATH_FT_UNARY_NOT",
        FTUnaryNotPsiImpl::class.java,
        XPath
    )

    val FT_PRIMARY_WITH_OPTIONS: IElementType = IASTWrapperElementType(
        "XPATH_FT_PRIMARY_WITH_OPTIONS",
        FTPrimaryWithOptionsPsiImpl::class.java,
        XPath
    )

    val FT_PRIMARY: IElementType = IASTWrapperElementType(
        "XPATH_FT_PRIMARY",
        FTPrimaryPsiImpl::class.java,
        XPath
    )

    val FT_WORDS: IElementType = IASTWrapperElementType(
        "XPATH_FT_WORDS",
        FTWordsPsiImpl::class.java,
        XPath
    )

    val FT_WORDS_VALUE: IElementType = IASTWrapperElementType(
        "XPATH_FT_WORDS_VALUE",
        FTWordsValuePsiImpl::class.java,
        XPath
    )

    val FT_EXTENSION_SELECTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_EXTENSION_SELECTION",
        FTExtensionSelectionPsiImpl::class.java,
        XPath
    )

    val FT_ANYALL_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_ANYALL_OPTION",
        FTAnyallOptionPsiImpl::class.java,
        XPath
    )

    val FT_TIMES: IElementType = IASTWrapperElementType(
        "XPATH_FT_TIMES",
        FTTimesPsiImpl::class.java,
        XPath
    )

    val FT_RANGE: IElementType = IASTWrapperElementType(
        "XPATH_FT_RANGE",
        FTRangePsiImpl::class.java,
        XPath
    )

    val FT_WEIGHT: IElementType = IASTWrapperElementType(
        "XPATH_FT_WEIGHT",
        FTWeightPsiImpl::class.java,
        XPath
    )

    val FT_ORDER: IElementType = IASTWrapperElementType(
        "XPATH_FT_ORDER",
        FTOrderPsiImpl::class.java,
        XPath
    )

    val FT_WINDOW: IElementType = IASTWrapperElementType(
        "XPATH_FT_WINDOW",
        FTWindowPsiImpl::class.java,
        XPath
    )

    val FT_DISTANCE: IElementType = IASTWrapperElementType(
        "XPATH_FT_DISTANCE",
        FTDistancePsiImpl::class.java,
        XPath
    )

    val FT_SCOPE: IElementType = IASTWrapperElementType(
        "XPATH_FT_SCOPE",
        FTScopePsiImpl::class.java,
        XPath
    )

    val FT_CONTENT: IElementType = IASTWrapperElementType(
        "XPATH_FT_CONTENT",
        FTContentPsiImpl::class.java,
        XPath
    )

    val FT_UNIT: IElementType = IASTWrapperElementType(
        "XPATH_FT_UNIT",
        FTUnitPsiImpl::class.java,
        XPath
    )

    val FT_BIG_UNIT: IElementType = IASTWrapperElementType(
        "XPATH_FT_BIG_UNIT",
        FTBigUnitPsiImpl::class.java,
        XPath
    )

    val FT_IGNORE_OPTION: IElementType = IASTWrapperElementType(
        "XPATH_FT_IGNORE_OPTION",
        FTIgnoreOptionPsiImpl::class.java,
        XPath
    )

    // endregion
    // region XQuery IntelliJ Plugin

    val CONTEXT_ITEM_FUNCTION_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_CONTEXT_ITEM_FUNCTION_EXPR",
        PluginContextItemFunctionExprImpl::class.java,
        XPath
    )

    val ANY_TEXT_TEST: IElementType = IASTWrapperElementType(
        "XPATH_ANY_TEXT_TEST",
        PluginAnyTextTestPsiImpl::class.java,
        XPath
    )

    val ARROW_DYNAMIC_FUNCTION_CALL: IElementType = IASTWrapperElementType(
        "XPATH_ARROW_DYNAMIC_FUNCTION_CALL",
        PluginArrowDynamicFunctionCallPsiImpl::class.java,
        XPath
    )

    val ARROW_FUNCTION_CALL: IElementType = IASTWrapperElementType(
        "XPATH_ARROW_FUNCTION_CALL",
        PluginArrowFunctionCallPsiImpl::class.java,
        XPath
    )

    val ARROW_INLINE_FUNCTION_CALL: IElementType = IASTWrapperElementType(
        "XPATH_ARROW_INLINE_FUNCTION_CALL",
        PluginArrowInlineFunctionCallPsiImpl::class.java,
        XPath
    )

    val EMPTY_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_EMPTY_EXPR",
        PluginEmptyExprPsiImpl::class.java,
        XPath
    )

    val EMPTY_SEQUENCE_TYPE: IElementType = IASTWrapperElementType(
        "XPATH_EMPTY_SEQUENCE_TYPE",
        PluginEmptySequenceTypePsiImpl::class.java,
        XPath
    )

    val LAMBDA_FUNCTION_EXPR: IElementType = IASTWrapperElementType(
        "XPATH_LAMBDA_FUNCTION_EXPR",
        PluginLambdaFunctionExprPsiImpl::class.java,
        XPath
    )

    val NILLABLE_TYPE_NAME: IElementType = IASTWrapperElementType(
        "XPATH_NILLABLE_TYPE_NAME",
        PluginNillableTypeNamePsiImpl::class.java,
        XPath
    )

    val PARAM_REF: IElementType = IASTWrapperElementType(
        "XPATH_PARAM_REF",
        PluginParamRefPsiImpl::class.java,
        XPath
    )

    val SEQUENCE_TYPE_LIST: IElementType = IASTWrapperElementType(
        "XPATH_SEQUENCE_TYPE_LIST",
        PluginSequenceTypeListPsiImpl::class.java,
        XPath
    )

    val FILTER_STEP: IElementType = IASTWrapperElementType(
        "XPATH_FILTER_STEP",
        PluginFilterStepPsiImpl::class.java,
        XPath
    )

    val POSTFIX_LOOKUP: IElementType = IASTWrapperElementType(
        "XPATH_POSTFIX_LOOKUP",
        PluginPostfixLookupPsiImpl::class.java,
        XPath
    )

    val DYNAMIC_FUNCTION_CALL: IElementType = IASTWrapperElementType(
        "XPATH_DYNAMIC_FUNCTION_CALL",
        PluginDynamicFunctionCallPsiImpl::class.java,
        XPath
    )

    val TYPE_ALIAS: IElementType = IASTWrapperElementType(
        "XPATH_TYPE_ALIAS",
        PluginTypeAliasPsiImpl::class.java,
        XPath
    )

    // endregion
}
