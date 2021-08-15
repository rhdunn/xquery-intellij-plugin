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

@Suppress("Reformat")
object XPathElementType {
    // region XPath 1.0

    val XPATH: IFileElementType = IFileElementType(XPath)

    val EXPR: IElementType = IASTWrapperElementType("XPATH_EXPR", XPath) { node ->
        XPathExprPsiImpl(node)
    }

    val OR_EXPR: IElementType = IASTWrapperElementType("XPATH_OR_EXPR", XPath) { node ->
        XPathOrExprPsiImpl(node)
    }

    val AND_EXPR: IElementType = IASTWrapperElementType("XPATH_AND_EXPR", XPath) { node ->
        XPathAndExprPsiImpl(node)
    }

    val ADDITIVE_EXPR: IElementType = IASTWrapperElementType("XPATH_ADDITIVE_EXPR", XPath) { node ->
        XPathAdditiveExprPsiImpl(node)
    }

    val MULTIPLICATIVE_EXPR: IElementType = IASTWrapperElementType("XPATH_MULTIPLICATIVE_EXPR", XPath) { node ->
        XPathMultiplicativeExprPsiImpl(node)
    }

    val UNARY_EXPR: IElementType = IASTWrapperElementType("XPATH_UNARY_EXPR", XPath) { node ->
        XPathUnaryExprPsiImpl(node)
    }

    val UNION_EXPR: IElementType = IASTWrapperElementType("XPATH_UNION_EXPR", XPath) { node ->
        XPathUnionExprPsiImpl(node)
    }

    val PATH_EXPR: IElementType = IASTWrapperElementType("XPATH_PATH_EXPR", XPath) { node ->
        XPathPathExprPsiImpl(node)
    }

    val AXIS_STEP: IElementType = IASTWrapperElementType("XPATH_AXIS_STEP", XPath) { node ->
        XPathAxisStepPsiImpl(node)
    }

    val FORWARD_STEP: IElementType = IASTWrapperElementType("XPATH_FORWARD_STEP", XPath) { node ->
        XPathForwardStepPsiImpl(node)
    }

    val ABBREV_FORWARD_STEP: IElementType = IASTWrapperElementType("XPATH_ABBREV_FORWARD_STEP", XPath) { node ->
        XPathAbbrevForwardStepPsiImpl(node)
    }

    val REVERSE_STEP: IElementType = IASTWrapperElementType("XPATH_REVERSE_STEP", XPath) { node ->
        XPathReverseStepPsiImpl(node)
    }

    val NODE_TEST: IElementType = IASTWrapperElementType("XPATH_NODE_TEST", XPath) { node ->
        XPathNodeTestPsiImpl(node)
    }

    val NAME_TEST: IElementType = IASTWrapperElementType("XPATH_NAME_TEST", XPath) { node ->
        XPathNameTestPsiImpl(node)
    }

    val WILDCARD: IElementType = IASTWrapperElementType("XPATH_WILDCARD", XPath) { node ->
        XPathWildcardPsiImpl(node)
    }

    val VAR_REF: IElementType = IASTWrapperElementType("XPATH_VAR_REF", XPath) { node ->
        XPathVarRefPsiImpl(node)
    }

    val FUNCTION_CALL: IElementType = IASTWrapperElementType("XPATH_FUNCTION_CALL", XPath) { node ->
        XPathFunctionCallPsiImpl(node)
    }

    val ANY_KIND_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_KIND_TEST", XPath) { node ->
        XPathAnyKindTestPsiImpl(node)
    }

    val COMMENT_TEST: IElementType = IASTWrapperElementType("XPATH_COMMENT_TEST", XPath) { node ->
        XPathCommentTestPsiImpl(node)
    }

    val PI_TEST: IElementType = IASTWrapperElementType("XPATH_PI_TEST", XPath) { node ->
        XPathPITestPsiImpl(node)
    }

    val STRING_LITERAL: IElementType = IASTWrapperElementType("XPATH_STRING_LITERAL", XPath) { node ->
        XPathStringLiteralPsiImpl(node)
    }

    val QNAME: IElementType = IASTWrapperElementType("XPATH_QNAME", XPath) { node ->
        XPathQNamePsiImpl(node)
    }

    val NCNAME: IElementType = IASTWrapperElementType("XPATH_NCNAME", XPath) { node ->
        XPathNCNamePsiImpl(node)
    }

    // endregion
    // region XPath 2.0

    val FOR_EXPR: IElementType = IASTWrapperElementType("XPATH_FOR_EXPR", XPath) { node ->
        XPathForExprPsiImpl(node)
    }

    val QUANTIFIED_EXPR: IElementType = IASTWrapperElementType("XPATH_QUANTIFIED_EXPR", XPath) { node ->
        XPathQuantifiedExprPsiImpl(node)
    }

    val IF_EXPR: IElementType = IASTWrapperElementType("XPATH_IF_EXPR", XPath) { node ->
        XPathIfExprPsiImpl(node)
    }

    val COMPARISON_EXPR: IElementType = IASTWrapperElementType("XPATH_COMPARISON_EXPR", XPath) { node ->
        XPathComparisonExprPsiImpl(node)
    }

    val RANGE_EXPR: IElementType = IASTWrapperElementType("XPATH_RANGE_EXPR", XPath) { node ->
        XPathRangeExprPsiImpl(node)
    }

    val INTERSECT_EXCEPT_EXPR: IElementType = IASTWrapperElementType("XPATH_INTERSECT_EXCEPT_EXPR", XPath) { node ->
        XPathIntersectExceptExprPsiImpl(node)
    }

    val INSTANCEOF_EXPR: IElementType = IASTWrapperElementType("XPATH_INSTANCEOF_EXPR", XPath) { node ->
        XPathInstanceofExprPsiImpl(node)
    }

    val TREAT_EXPR: IElementType = IASTWrapperElementType("XPATH_TREAT_EXPR", XPath) { node ->
        XPathTreatExprPsiImpl(node)
    }

    val CASTABLE_EXPR: IElementType = IASTWrapperElementType("XPATH_CASTABLE_EXPR", XPath) { node ->
        XPathCastableExprPsiImpl(node)
    }

    val CAST_EXPR: IElementType = IASTWrapperElementType("XPATH_CAST_EXPR", XPath) { node ->
        XPathCastExprPsiImpl(node)
    }

    val FILTER_EXPR: IElementType = IASTWrapperElementType("XPATH_FILTER_EXPR", XPath) { node ->
        XPathFilterExprPsiImpl(node)
    }

    val SINGLE_TYPE: IElementType = IASTWrapperElementType("XPATH_SINGLE_TYPE", XPath) { node ->
        XPathSingleTypePsiImpl(node)
    }

    val SEQUENCE_TYPE: IElementType = IASTWrapperElementType("XPATH_SEQUENCE_TYPE", XPath) { node ->
        XPathSequenceTypePsiImpl(node)
    }

    val DOCUMENT_TEST: IElementType = IASTWrapperElementType("XPATH_DOCUMENT_TEST", XPath) { node ->
        XPathDocumentTestPsiImpl(node)
    }

    val NAMESPACE_NODE_TEST: IElementType = IASTWrapperElementType("XPATH_NAMESPACE_NODE_TEST", XPath) { node ->
        XPathNamespaceNodeTestPsiImpl(node)
    }

    val ATTRIBUTE_TEST: IElementType = IASTWrapperElementType("XPATH_ATTRIBUTE_TEST", XPath) { node ->
        XPathAttributeTestPsiImpl(node)
    }

    val SCHEMA_ATTRIBUTE_TEST: IElementType = IASTWrapperElementType("XPATH_SCHEMA_ATTRIBUTE_TEST", XPath) { node ->
        XPathSchemaAttributeTestPsiImpl(node)
    }

    val ELEMENT_TEST: IElementType = IASTWrapperElementType("XPATH_ELEMENT_TEST", XPath) { node ->
        XPathElementTestPsiImpl(node)
    }

    val SCHEMA_ELEMENT_TEST: IElementType = IASTWrapperElementType("XPATH_SCHEMA_ELEMENT_TEST", XPath) { node ->
        XPathSchemaElementTestPsiImpl(node)
    }

    val SIMPLE_TYPE_NAME: IElementType = IASTWrapperElementType("XPATH_SIMPLE_TYPE_NAME", XPath) { node ->
        XPathSimpleTypeNamePsiImpl(node)
    }

    val TYPE_NAME: IElementType = IASTWrapperElementType("XPATH_TYPE_NAME", XPath) { node ->
        XPathTypeNamePsiImpl(node)
    }

    val COMMENT: IElementType = IASTWrapperElementType("XPATH_COMMENT", XPath) { node ->
        XPathCommentPsiImpl(node)
    }

    // endregion
    // region XPath 3.0

    val ANY_FUNCTION_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_FUNCTION_TEST", XPath) { node ->
        XPathAnyFunctionTestPsiImpl(node)
    }

    val PARAM: IElementType = IASTWrapperElementType("XPATH_PARAM", XPath) { node ->
        XPathParamPsiImpl(node)
    }

    val SIMPLE_FOR_CLAUSE: IElementType = IASTWrapperElementType("XPATH_SIMPLE_FOR_CLAUSE", XPath) { node ->
        XPathSimpleForClausePsiImpl(node)
    }

    val SIMPLE_FOR_BINDING: IElementType = IASTWrapperElementType("XPATH_SIMPLE_FOR_BINDING", XPath) { node ->
        XPathSimpleForBindingPsiImpl(node)
    }

    val LET_EXPR: IElementType = IASTWrapperElementType("XPATH_LET_EXPR", XPath) { node ->
        XPathLetExprPsiImpl(node)
    }

    val SIMPLE_LET_CLAUSE: IElementType = IASTWrapperElementType("XPATH_SIMPLE_LET_CLAUSE", XPath) { node ->
        XPathSimpleLetClausePsiImpl(node)
    }

    val SIMPLE_LET_BINDING: IElementType = IASTWrapperElementType("XPATH_SIMPLE_LET_BINDING", XPath) { node ->
        XPathSimpleLetBindingPsiImpl(node)
    }

    val STRING_CONCAT_EXPR: IElementType = IASTWrapperElementType("XPATH_STRING_CONCAT_EXPR", XPath) { node ->
        XPathStringConcatExprPsiImpl(node)
    }

    val SIMPLE_MAP_EXPR: IElementType = IASTWrapperElementType("XPATH_SIMPLE_MAP_EXPR", XPath) { node ->
        XPathSimpleMapExprPsiImpl(node)
    }

    val POSTFIX_EXPR: IElementType = IASTWrapperElementType("XPATH_POSTFIX_EXPR", XPath) { node ->
        XPathPostfixExprPsiImpl(node)
    }

    val ARGUMENT_LIST: IElementType = IASTWrapperElementType("XPATH_ARGUMENT_LIST", XPath) { node ->
        XPathArgumentListPsiImpl(node)
    }

    val ARGUMENT: IElementType = EXPR

    val ARGUMENT_PLACEHOLDER: IElementType = IASTWrapperElementType("XPATH_ARGUMENT_PLACEHOLDER", XPath) { node ->
        XPathArgumentPlaceholderPsiImpl(node)
    }

    val NAMED_FUNCTION_REF: IElementType = IASTWrapperElementType("XPATH_NAMED_FUNCTION_REF", XPath) { node ->
        XPathNamedFunctionRefPsiImpl(node)
    }

    val INLINE_FUNCTION_EXPR: IElementType = IASTWrapperElementType("XPATH_INLINE_FUNCTION_EXPR", XPath) { node ->
        XPathInlineFunctionExprPsiImpl(node)
    }

    val ATOMIC_OR_UNION_TYPE: IElementType = IASTWrapperElementType("XPATH_ATOMIC_OR_UNION_TYPE", XPath) { node ->
        XPathAtomicOrUnionTypePsiImpl(node)
    }

    val TYPED_FUNCTION_TEST: IElementType = IASTWrapperElementType("XPATH_TYPED_FUNCTION_TEST", XPath) { node ->
        XPathTypedFunctionTestPsiImpl(node)
    }

    val PARENTHESIZED_ITEM_TYPE: IElementType = IASTWrapperElementType("XPATH_PARENTHESIZED_ITEM_TYPE", XPath) { node ->
        XPathParenthesizedItemTypePsiImpl(node)
    }

    val URI_QUALIFIED_NAME: IElementType = IASTWrapperElementType("XPATH_URI_QUALIFIED_NAME", XPath) { node ->
        XPathURIQualifiedNamePsiImpl(node)
    }

    val BRACED_URI_LITERAL: IElementType = IASTWrapperElementType("XPATH_BRACED_URI_LITERAL", XPath) { node ->
        XPathBracedURILiteralPsiImpl(node)
    }

    // endregion
    // region XPath 3.1

    val ARROW_EXPR: IElementType = IASTWrapperElementType("XPATH_ARROW_EXPR", XPath) { node ->
        XPathArrowExprPsiImpl(node)
    }

    val MAP_CONSTRUCTOR: IElementType = IASTWrapperElementType("XPATH_MAP_CONSTRUCTOR", XPath) { node ->
        XPathMapConstructorPsiImpl(node)
    }

    val MAP_CONSTRUCTOR_ENTRY: IElementType = IASTWrapperElementType("XPATH_MAP_CONSTRUCTOR_ENTRY", XPath) { node ->
        XPathMapConstructorEntryPsiImpl(node)
    }

    val SQUARE_ARRAY_CONSTRUCTOR: IElementType = IASTWrapperElementType("XPATH_SQUARE_ARRAY_CONSTRUCTOR", XPath) { node ->
        XPathSquareArrayConstructorPsiImpl(node)
    }

    val CURLY_ARRAY_CONSTRUCTOR: IElementType = IASTWrapperElementType("XPATH_CURLY_ARRAY_CONSTRUCTOR", XPath) { node ->
        XPathCurlyArrayConstructorPsiImpl(node)
    }

    val UNARY_LOOKUP: IElementType = IASTWrapperElementType("XPATH_UNARY_LOOKUP", XPath) { node ->
        XPathUnaryLookupPsiImpl(node)
    }

    val ANY_MAP_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_MAP_TEST", XPath) { node ->
        XPathAnyMapTestPsiImpl(node)
    }

    val TYPED_MAP_TEST: IElementType = IASTWrapperElementType("XPATH_TYPED_MAP_TEST", XPath) { node ->
        XPathTypedMapTestPsiImpl(node)
    }

    val ANY_ARRAY_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_ARRAY_TEST", XPath) { node ->
        XPathAnyArrayTestPsiImpl(node)
    }

    val TYPED_ARRAY_TEST: IElementType = IASTWrapperElementType("XPATH_TYPED_ARRAY_TEST", XPath) { node ->
        XPathTypedArrayTestPsiImpl(node)
    }

    // endregion
    // region XPath 4.0 ED

    val ANY_ITEM_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_ITEM_TEST", XPath) { node ->
        XPathAnyItemTestPsiImpl(node)
    }

    val ENUMERATION_TYPE: IElementType = IASTWrapperElementType("XPATH_ENUMERATION_TYPE", XPath) { node ->
        XPathEnumerationTypePsiImpl(node)
    }

    val FIELD_DECLARATION: IElementType = IASTWrapperElementType("XPATH_FIELD_DECLARATION", XPath) { node ->
        XPathFieldDeclarationPsiImpl(node)
    }

    val KEYWORD_ARGUMENT: IElementType = IASTWrapperElementType("XPATH_KEYWORD_ARGUMENT", XPath) { node ->
        XPathKeywordArgumentPsiImpl(node)
    }

    val LOCAL_UNION_TYPE: IElementType = IASTWrapperElementType("XPATH_LOCAL_UNION_TYPE", XPath) { node ->
        XPathLocalUnionTypePsiImpl(node)
    }

    val NAMESPACE_DECLARATION: IElementType = IASTWrapperElementType("XPATH_NAMESPACE_DECLARATION", XPath) { node ->
        XPathNamespaceDeclarationPsiImpl(node)
    }

    val OTHERWISE_EXPR: IElementType = IASTWrapperElementType("XPATH_OTHERWISE_EXPR", XPath) { node ->
        XPathOtherwiseExprPsiImpl(node)
    }

    val POSITIONAL_ARGUMENT_LIST: IElementType = IASTWrapperElementType("XPATH_POSITIONAL_ARGUMENT_LIST", XPath) { node ->
        XPathPositionalArgumentListPsiImpl(node)
    }

    val QUANTIFIER_BINDING: IElementType = IASTWrapperElementType("XPATH_QUANTIFIER_BINDING", XPath) { node ->
        XPathQuantifierBindingPsiImpl(node)
    }

    val RECORD_TEST: IElementType = IASTWrapperElementType("XPATH_RECORD_TEST", XPath) { node ->
        XPathRecordTestPsiImpl(node)
    }

    val SELF_REFERENCE: IElementType = IASTWrapperElementType("XPATH_SELF_REFERENCE", XPath) { node ->
        XPathSelfReferencePsiImpl(node)
    }

    val TERNARY_CONDITIONAL_EXPR: IElementType = IASTWrapperElementType("XPATH_TERNARY_CONDITIONAL_EXPR", XPath) { node ->
        XPathTernaryConditionalExprPsiImpl(node)
    }

    val WITH_EXPR: IElementType = IASTWrapperElementType("XPATH_WITH_EXPR", XPath) { node ->
        XPathWithExprPsiImpl(node)
    }

    // endregion
    // region Full Text 1.0

    val PRAGMA: IElementType = IASTWrapperElementType("XPATH_PRAGMA", XPath) { node ->
        XPathPragmaPsiImpl(node)
    }

    val URI_LITERAL: IElementType = IASTWrapperElementType("XPATH_URI_LITERAL", XPath) { node ->
        XPathUriLiteralPsiImpl(node)
    }

    val FT_CASE_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_CASE_OPTION", XPath) { node ->
        FTCaseOptionPsiImpl(node)
    }

    val FT_DIACRITICS_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_DIACRITICS_OPTION", XPath) { node ->
        FTDiacriticsOptionPsiImpl(node)
    }

    val FT_EXTENSION_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_EXTENSION_OPTION", XPath) { node ->
        FTExtensionOptionPsiImpl(node)
    }

    val FT_LANGUAGE_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_LANGUAGE_OPTION", XPath) { node ->
        FTLanguageOptionPsiImpl(node)
    }

    val FT_STEM_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_STEM_OPTION", XPath) { node ->
        FTStemOptionPsiImpl(node)
    }

    val FT_STOP_WORD_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_STOP_WORD_OPTION", XPath) { node ->
        FTStopWordOptionPsiImpl(node)
    }

    val FT_THESAURUS_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_THESAURUS_OPTION", XPath) { node ->
        FTThesaurusOptionPsiImpl(node)
    }

    val FT_WILDCARD_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_WILDCARD_OPTION", XPath) { node ->
        FTWildCardOptionPsiImpl(node)
    }

    val FT_THESAURUS_ID: IElementType = IASTWrapperElementType("XPATH_FT_THESAURUS_ID", XPath) { node ->
        FTThesaurusIDPsiImpl(node)
    }

    val FT_LITERAL_RANGE: IElementType = IASTWrapperElementType("XPATH_FT_LITERAL_RANGE", XPath) { node ->
        FTLiteralRangePsiImpl(node)
    }

    val FT_STOP_WORDS: IElementType = IASTWrapperElementType("XPATH_FT_STOP_WORDS", XPath) { node ->
        FTStopWordsPsiImpl(node)
    }

    val FT_STOP_WORDS_INCL_EXCL: IElementType = IASTWrapperElementType("XPATH_FT_STOP_WORDS_INCL_EXCL", XPath) { node ->
        FTStopWordsInclExclPsiImpl(node)
    }

    val FT_SCORE_VAR: IElementType = IASTWrapperElementType("XPATH_FT_SCORE_VAR", XPath) { node ->
        FTScoreVarPsiImpl(node)
    }

    val FT_CONTAINS_EXPR: IElementType = IASTWrapperElementType("XPATH_FT_CONTAINS_EXPR", XPath) { node ->
        FTContainsExprPsiImpl(node)
    }

    val FT_SELECTION: IElementType = IASTWrapperElementType("XPATH_FT_SELECTION", XPath) { node ->
        FTSelectionPsiImpl(node)
    }

    val FT_OR: IElementType = IASTWrapperElementType("XPATH_FT_OR", XPath) { node ->
        FTOrPsiImpl(node)
    }

    val FT_AND: IElementType = IASTWrapperElementType("XPATH_FT_AND", XPath) { node ->
        FTAndPsiImpl(node)
    }

    val FT_MILD_NOT: IElementType = IASTWrapperElementType("XPATH_FT_MILD_NOT", XPath) { node ->
        FTMildNotPsiImpl(node)
    }

    val FT_UNARY_NOT: IElementType = IASTWrapperElementType("XPATH_FT_UNARY_NOT", XPath) { node ->
        FTUnaryNotPsiImpl(node)
    }

    val FT_PRIMARY_WITH_OPTIONS: IElementType = IASTWrapperElementType("XPATH_FT_PRIMARY_WITH_OPTIONS", XPath) { node ->
        FTPrimaryWithOptionsPsiImpl(node)
    }

    val FT_PRIMARY: IElementType = IASTWrapperElementType("XPATH_FT_PRIMARY", XPath) { node ->
        FTPrimaryPsiImpl(node)
    }

    val FT_WORDS: IElementType = IASTWrapperElementType("XPATH_FT_WORDS", XPath) { node ->
        FTWordsPsiImpl(node)
    }

    val FT_WORDS_VALUE: IElementType = IASTWrapperElementType("XPATH_FT_WORDS_VALUE", XPath) { node ->
        FTWordsValuePsiImpl(node)
    }

    val FT_EXTENSION_SELECTION: IElementType = IASTWrapperElementType("XPATH_FT_EXTENSION_SELECTION", XPath) { node ->
        FTExtensionSelectionPsiImpl(node)
    }

    val FT_ANYALL_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_ANYALL_OPTION", XPath) { node ->
        FTAnyallOptionPsiImpl(node)
    }

    val FT_TIMES: IElementType = IASTWrapperElementType("XPATH_FT_TIMES", XPath) { node ->
        FTTimesPsiImpl(node)
    }

    val FT_RANGE: IElementType = IASTWrapperElementType("XPATH_FT_RANGE", XPath) { node ->
        FTRangePsiImpl(node)
    }

    val FT_WEIGHT: IElementType = IASTWrapperElementType("XPATH_FT_WEIGHT", XPath) { node ->
        FTWeightPsiImpl(node)
    }

    val FT_ORDER: IElementType = IASTWrapperElementType("XPATH_FT_ORDER", XPath) { node ->
        FTOrderPsiImpl(node)
    }

    val FT_WINDOW: IElementType = IASTWrapperElementType("XPATH_FT_WINDOW", XPath) { node ->
        FTWindowPsiImpl(node)
    }

    val FT_DISTANCE: IElementType = IASTWrapperElementType("XPATH_FT_DISTANCE", XPath) { node ->
        FTDistancePsiImpl(node)
    }

    val FT_SCOPE: IElementType = IASTWrapperElementType("XPATH_FT_SCOPE", XPath) { node ->
        FTScopePsiImpl(node)
    }

    val FT_CONTENT: IElementType = IASTWrapperElementType("XPATH_FT_CONTENT", XPath) { node ->
        FTContentPsiImpl(node)
    }

    val FT_UNIT: IElementType = IASTWrapperElementType("XPATH_FT_UNIT", XPath) { node ->
        FTUnitPsiImpl(node)
    }

    val FT_BIG_UNIT: IElementType = IASTWrapperElementType("XPATH_FT_BIG_UNIT", XPath) { node ->
        FTBigUnitPsiImpl(node)
    }

    val FT_IGNORE_OPTION: IElementType = IASTWrapperElementType("XPATH_FT_IGNORE_OPTION", XPath) { node ->
        FTIgnoreOptionPsiImpl(node)
    }

    // endregion
    // region XQuery IntelliJ Plugin

    val CONTEXT_ITEM_FUNCTION_EXPR: IElementType = IASTWrapperElementType("XPATH_CONTEXT_ITEM_FUNCTION_EXPR", XPath) { node ->
        PluginContextItemFunctionExprImpl(node)
    }

    val ANY_TEXT_TEST: IElementType = IASTWrapperElementType("XPATH_ANY_TEXT_TEST", XPath) { node ->
        PluginAnyTextTestPsiImpl(node)
    }

    val ARROW_DYNAMIC_FUNCTION_CALL: IElementType = IASTWrapperElementType("XPATH_ARROW_DYNAMIC_FUNCTION_CALL", XPath) { node ->
        PluginArrowDynamicFunctionCallPsiImpl(node)
    }

    val ARROW_FUNCTION_CALL: IElementType = IASTWrapperElementType("XPATH_ARROW_FUNCTION_CALL", XPath) { node ->
        PluginArrowFunctionCallPsiImpl(node)
    }

    val ARROW_INLINE_FUNCTION_CALL: IElementType = IASTWrapperElementType("XPATH_ARROW_INLINE_FUNCTION_CALL", XPath) { node ->
        PluginArrowInlineFunctionCallPsiImpl(node)
    }

    val EMPTY_EXPR: IElementType = IASTWrapperElementType("XPATH_EMPTY_EXPR", XPath) { node ->
        PluginEmptyExprPsiImpl(node)
    }

    val EMPTY_SEQUENCE_TYPE: IElementType = IASTWrapperElementType("XPATH_EMPTY_SEQUENCE_TYPE", XPath) { node ->
        PluginEmptySequenceTypePsiImpl(node)
    }

    val LAMBDA_FUNCTION_EXPR: IElementType = IASTWrapperElementType("XPATH_LAMBDA_FUNCTION_EXPR", XPath) { node ->
        PluginLambdaFunctionExprPsiImpl(node)
    }

    val NILLABLE_TYPE_NAME: IElementType = IASTWrapperElementType("XPATH_NILLABLE_TYPE_NAME", XPath) { node ->
        PluginNillableTypeNamePsiImpl(node)
    }

    val PARAM_REF: IElementType = IASTWrapperElementType("XPATH_PARAM_REF", XPath) { node ->
        PluginParamRefPsiImpl(node)
    }

    val SEQUENCE_TYPE_LIST: IElementType = IASTWrapperElementType("XPATH_SEQUENCE_TYPE_LIST", XPath) { node ->
        PluginSequenceTypeListPsiImpl(node)
    }

    val FILTER_STEP: IElementType = IASTWrapperElementType("XPATH_FILTER_STEP", XPath) { node ->
        PluginFilterStepPsiImpl(node)
    }

    val POSTFIX_LOOKUP: IElementType = IASTWrapperElementType("XPATH_POSTFIX_LOOKUP", XPath) { node ->
        PluginPostfixLookupPsiImpl(node)
    }

    val DYNAMIC_FUNCTION_CALL: IElementType = IASTWrapperElementType("XPATH_DYNAMIC_FUNCTION_CALL", XPath) { node ->
        PluginDynamicFunctionCallPsiImpl(node)
    }

    val TYPE_ALIAS: IElementType = IASTWrapperElementType("XPATH_TYPE_ALIAS", XPath) { node ->
        PluginTypeAliasPsiImpl(node)
    }

    // endregion
}
