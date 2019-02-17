/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.parser

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.full.text.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.*

object XQueryElementType {
    // region XQuery 1.0

    val BASE_URI_DECL: IElementType = ICompositeElementType(
        "XQUERY_BASE_URI_DECL",
        XQueryBaseURIDeclPsiImpl::class.java,
        XQuery
    )

    val BOUNDARY_SPACE_DECL: IElementType = ICompositeElementType(
        "XQUERY_BOUNDARY_SPACE_DECL",
        XQueryBoundarySpaceDeclPsiImpl::class.java,
        XQuery
    )

    val CASE_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_CASE_CLAUSE",
        XQueryCaseClausePsiImpl::class.java,
        XQuery
    )

    val CDATA_SECTION: IElementType = ICompositeElementType(
        "XQUERY_CDATA_SECTION",
        XQueryCDataSectionPsiImpl::class.java,
        XQuery
    )

    val COMP_ATTR_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_ATTR_CONSTRUCTOR",
        XQueryCompAttrConstructorPsiImpl::class.java,
        XQuery
    )

    val COMP_COMMENT_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_COMMENT_CONSTRUCTOR",
        XQueryCompCommentConstructorPsiImpl::class.java,
        XQuery
    )

    val COMP_DOC_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_DOC_CONSTRUCTOR",
        XQueryCompDocConstructorPsiImpl::class.java,
        XQuery
    )

    val COMP_ELEM_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_ELEM_CONSTRUCTOR",
        XQueryCompElemConstructorPsiImpl::class.java,
        XQuery
    )

    val COMP_PI_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_PI_CONSTRUCTOR",
        XQueryCompPIConstructorPsiImpl::class.java,
        XQuery
    )

    val COMP_TEXT_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_TEXT_CONSTRUCTOR",
        XQueryCompTextConstructorPsiImpl::class.java,
        XQuery
    )

    val CONSTRUCTION_DECL: IElementType = ICompositeElementType(
        "XQUERY_CONSTRUCTION_DECL",
        XQueryConstructionDeclPsiImpl::class.java,
        XQuery
    )

    val COPY_NAMESPACES_DECL: IElementType = ICompositeElementType(
        "XQUERY_COPY_NAMESPACES_DECL",
        XQueryCopyNamespacesDeclPsiImpl::class.java,
        XQuery
    )

    val DEFAULT_COLLATION_DECL: IElementType = ICompositeElementType(
        "XQUERY_DEFAULT_COLLATION_DECL",
        XQueryDefaultCollationDeclPsiImpl::class.java,
        XQuery
    )

    val DEFAULT_NAMESPACE_DECL: IElementType = ICompositeElementType(
        "XQUERY_DEFAULT_NAMESPACE_DECL",
        XQueryDefaultNamespaceDeclPsiImpl::class.java,
        XQuery
    )

    val DIR_ATTRIBUTE_LIST: IElementType = ICompositeElementType(
        "XQUERY_DIR_ATTRIBUTE_LIST",
        XQueryDirAttributeListPsiImpl::class.java,
        XQuery
    )

    val DIR_ATTRIBUTE_VALUE: IElementType = ICompositeElementType(
        "XQUERY_DIR_ATTRIBUTE_VALUE",
        XQueryDirAttributeValuePsiImpl::class.java,
        XQuery
    )

    val DIR_COMMENT_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_DIR_COMMENT_CONSTRUCTOR",
        XQueryDirCommentConstructorPsiImpl::class.java,
        XQuery
    )

    val DIR_ELEM_CONTENT: IElementType = ICompositeElementType(
        "XQUERY_DIR_ELEM_CONTENT",
        XQueryDirElemContentPsiImpl::class.java,
        XQuery
    )

    val DIR_PI_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_DIR_PI_CONSTRUCTOR",
        XQueryDirPIConstructorPsiImpl::class.java,
        XQuery
    )

    val EMPTY_ORDER_DECL: IElementType = ICompositeElementType(
        "XQUERY_EMPTY_ORDER_DECL",
        XQueryEmptyOrderDeclPsiImpl::class.java,
        XQuery
    )

    val ENCLOSED_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ENCLOSED_EXPR",
        XQueryEnclosedExprPsiImpl::class.java,
        XQuery
    )

    val EXPR: IElementType = ICompositeElementType(
        "XQUERY_EXPR",
        XQueryExprPsiImpl::class.java,
        XQuery
    )

    val EXTENSION_EXPR: IElementType = ICompositeElementType(
        "XQUERY_EXTENSION_EXPR",
        XQueryExtensionExprPsiImpl::class.java,
        XQuery
    )

    val FLWOR_EXPR: IElementType = ICompositeElementType(
        "XQUERY_FLWOR_EXPR",
        XQueryFLWORExprPsiImpl::class.java,
        XQuery
    )

    val FOR_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_FOR_CLAUSE",
        XQueryForClausePsiImpl::class.java,
        XQuery
    )

    val FUNCTION_DECL: IElementType = ICompositeElementType(
        "XQUERY_FUNCTION_DECL",
        XQueryFunctionDeclPsiImpl::class.java,
        XQuery
    )

    val IMPORT: IElementType = ICompositeElementType(
        "XQUERY_IMPORT",
        XQueryImportPsiImpl::class.java,
        XQuery
    )

    val LIBRARY_MODULE: IElementType = ICompositeElementType(
        "XQUERY_LIBRARY_MODULE",
        XQueryLibraryModulePsiImpl::class.java,
        XQuery
    )

    val MAIN_MODULE: IElementType = ICompositeElementType(
        "XQUERY_MAIN_MODULE",
        XQueryMainModulePsiImpl::class.java,
        XQuery
    )

    val MODULE = IFileElementType(XQuery)

    val OPTION_DECL: IElementType = ICompositeElementType(
        "XQUERY_OPTION_DECL",
        XQueryOptionDeclPsiImpl::class.java,
        XQuery
    )

    val ORDER_BY_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_ORDER_BY_CLAUSE",
        XQueryOrderByClausePsiImpl::class.java,
        XQuery
    )

    val ORDERED_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ORDERED_EXPR",
        XQueryOrderedExprPsiImpl::class.java,
        XQuery
    )

    val ORDERING_MODE_DECL: IElementType = ICompositeElementType(
        "XQUERY_ORDERING_MODE_DECL",
        XQueryOrderingModeDeclPsiImpl::class.java,
        XQuery
    )

    val ORDER_MODIFIER: IElementType = ICompositeElementType(
        "XQUERY_ORDER_MODIFIER",
        XQueryOrderModifierPsiImpl::class.java,
        XQuery
    )

    val ORDER_SPEC: IElementType = ICompositeElementType(
        "XQUERY_ORDER_SPEC",
        XQueryOrderSpecPsiImpl::class.java,
        XQuery
    )

    val ORDER_SPEC_LIST: IElementType = ICompositeElementType(
        "XQUERY_ORDER_SPEC_LIST",
        XQueryOrderSpecListPsiImpl::class.java,
        XQuery
    )

    val POSITIONAL_VAR: IElementType = ICompositeElementType(
        "XQUERY_POSITIONAL_VAR",
        XQueryPositionalVarPsiImpl::class.java,
        XQuery
    )

    val PRAGMA: IElementType = ICompositeElementType(
        "XQUERY_PRAGMA",
        XQueryPragmaPsiImpl::class.java,
        XQuery
    )

    val PROLOG: IElementType = ICompositeElementType(
        "XQUERY_PROLOG",
        XQueryPrologPsiImpl::class.java,
        XQuery
    )

    val QUERY_BODY: IElementType = ICompositeElementType(
        "XQUERY_QUERY_BODY",
        XQueryQueryBodyPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_PREFIX: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_PREFIX",
        XQuerySchemaPrefixPsiImpl::class.java,
        XQuery
    )

    val STRING_LITERAL: IElementType = ICompositeElementType(
        "XQUERY_STRING_LITERAL",
        XQueryStringLiteralPsiImpl::class.java,
        XQuery
    )

    val TYPESWITCH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_TYPESWITCH_EXPR",
        XQueryTypeswitchExprPsiImpl::class.java,
        XQuery
    )

    val UNKNOWN_DECL: IElementType = ICompositeElementType(
        "XQUERY_UNKNOWN_DECL",
        XQueryUnknownDeclPsiImpl::class.java,
        XQuery
    )

    val UNORDERED_EXPR: IElementType = ICompositeElementType(
        "XQUERY_UNORDERED_EXPR",
        XQueryUnorderedExprPsiImpl::class.java,
        XQuery
    )

    val VALIDATE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_VALIDATE_EXPR",
        XQueryValidateExprPsiImpl::class.java,
        XQuery
    )

    val VAR_DECL: IElementType = ICompositeElementType(
        "XQUERY_VAR_DECL",
        XQueryVarDeclPsiImpl::class.java,
        XQuery
    )

    val VERSION_DECL: IElementType = ICompositeElementType(
        "XQUERY_VERSION_DECL",
        XQueryVersionDeclPsiImpl::class.java,
        XQuery
    )

    val WHERE_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_WHERE_CLAUSE",
        XQueryWhereClausePsiImpl::class.java,
        XQuery
    )

    // endregion
    // region XQuery 3.0

    val ALLOWING_EMPTY: IElementType = ICompositeElementType(
        "XQUERY_ALLOWING_EMPTY",
        XQueryAllowingEmptyPsiImpl::class.java,
        XQuery
    )

    val ANNOTATED_DECL: IElementType = ICompositeElementType(
        "XQUERY_ANNOTATED_DECL",
        XQueryAnnotatedDeclPsiImpl::class.java,
        XQuery
    )

    val ANNOTATION: IElementType = ICompositeElementType(
        "XQUERY_ANNOTATION",
        XQueryAnnotationPsiImpl::class.java,
        XQuery
    )

    val BRACED_URI_LITERAL: IElementType = ICompositeElementType(
        "XQUERY_BRACED_URI_LITERAL",
        XQueryBracedURILiteralPsiImpl::class.java,
        XQuery
    )

    val CATCH_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_CATCH_CLAUSE",
        XQueryCatchClausePsiImpl::class.java,
        XQuery
    )

    val CATCH_ERROR_LIST: IElementType = ICompositeElementType(
        "XQUERY_CATCH_ERROR_LIST",
        XQueryCatchErrorListPsiImpl::class.java,
        XQuery
    )

    val COMP_NAMESPACE_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_COMP_NAMESPACE_CONSTRUCTOR",
        XQueryCompNamespaceConstructorPsiImpl::class.java,
        XQuery
    )

    val CONTEXT_ITEM_DECL: IElementType = ICompositeElementType(
        "XQUERY_CONTEXT_ITEM_DECL",
        XQueryContextItemDeclPsiImpl::class.java,
        XQuery
    )

    val COUNT_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_COUNT_CLAUSE",
        XQueryCountClausePsiImpl::class.java,
        XQuery
    )

    val CURRENT_ITEM: IElementType = ICompositeElementType(
        "XQUERY_CURRENT_ITEM",
        XQueryCurrentItemPsiImpl::class.java,
        XQuery
    )

    val DECIMAL_FORMAT_DECL: IElementType = ICompositeElementType(
        "XQUERY_DECIMAL_FORMAT_DECL",
        XQueryDecimalFormatDeclPsiImpl::class.java,
        XQuery
    )

    val DF_PROPERTY_NAME: IElementType = ICompositeElementType(
        "XQUERY_DF_PROPERTY_NAME",
        XQueryDFPropertyNamePsiImpl::class.java,
        XQuery
    )

    val FOR_BINDING: IElementType = ICompositeElementType(
        "XQUERY_FOR_BINDING",
        XQueryForBindingPsiImpl::class.java,
        XQuery
    )

    val FUNCTION_BODY: IElementType = ICompositeElementType(
        "XQUERY_FUNCTION_BODY",
        XQueryFunctionBodyPsiImpl::class.java,
        XQuery
    )

    val FUNCTION_TEST: IElementType = ICompositeElementType(
        "XQUERY_FUNCTION_TEST",
        XQueryFunctionTestPsiImpl::class.java,
        XQuery
    )

    val GROUP_BY_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_GROUP_BY_CLAUSE",
        XQueryGroupByClausePsiImpl::class.java,
        XQuery
    )

    val GROUPING_SPEC_LIST: IElementType = ICompositeElementType(
        "XQUERY_GROUPING_SPEC_LIST",
        XQueryGroupingSpecListPsiImpl::class.java,
        XQuery
    )

    val GROUPING_SPEC: IElementType = ICompositeElementType(
        "XQUERY_GROUPING_SPEC",
        XQueryGroupingSpecPsiImpl::class.java,
        XQuery
    )

    val GROUPING_VARIABLE: IElementType = ICompositeElementType(
        "XQUERY_GROUPING_VARIABLE",
        XQueryGroupingVariablePsiImpl::class.java,
        XQuery
    )

    val INTERMEDIATE_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_INTERMEDIATE_CLAUSE",
        XQueryIntermediateClausePsiImpl::class.java,
        XQuery
    )

    val LET_BINDING: IElementType = ICompositeElementType(
        "XQUERY_LET_BINDING",
        XQueryLetBindingPsiImpl::class.java,
        XQuery
    )

    val LET_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_LET_CLAUSE",
        XQueryLetClausePsiImpl::class.java,
        XQuery
    )

    val NEXT_ITEM: IElementType = ICompositeElementType(
        "XQUERY_NEXT_ITEM",
        XQueryNextItemPsiImpl::class.java,
        XQuery
    )

    val PREVIOUS_ITEM: IElementType = ICompositeElementType(
        "XQUERY_PREVIOUS_ITEM",
        XQueryPreviousItemPsiImpl::class.java,
        XQuery
    )

    val SEQUENCE_TYPE_UNION: IElementType = ICompositeElementType(
        "XQUERY_SEQUENCE_TYPE_UNION",
        XQuerySequenceTypeUnionPsiImpl::class.java,
        XQuery
    )

    val SLIDING_WINDOW_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_SLIDING_WINDOW_CLAUSE",
        XQuerySlidingWindowClausePsiImpl::class.java,
        XQuery
    )

    val SWITCH_CASE_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_SWITCH_CASE_CLAUSE",
        XQuerySwitchCaseClausePsiImpl::class.java,
        XQuery
    )

    val SWITCH_CASE_OPERAND: IElementType = ICompositeElementType(
        "XQUERY_SWITCH_CASE_OPERAND",
        XQuerySwitchCaseOperandPsiImpl::class.java,
        XQuery
    )

    val SWITCH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_SWITCH_EXPR",
        XQuerySwitchExprPsiImpl::class.java,
        XQuery
    )

    val TRY_CATCH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_TRY_CATCH_EXPR",
        XQueryTryCatchExprPsiImpl::class.java,
        XQuery
    )

    val TRY_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_TRY_CLAUSE",
        XQueryTryClausePsiImpl::class.java,
        XQuery
    )

    val TUMBLING_WINDOW_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_TUMBLING_WINDOW_CLAUSE",
        XQueryTumblingWindowClausePsiImpl::class.java,
        XQuery
    )

    val URI_QUALIFIED_NAME: IElementType = ICompositeElementType(
        "XQUERY_URI_QUALIFIED_NAME",
        XQueryURIQualifiedNamePsiImpl::class.java,
        XQuery
    )

    val VAR_DEFAULT_VALUE: IElementType = ICompositeElementType(
        "XQUERY_VAR_DEFAULT_VALUE",
        XQueryVarDefaultValuePsiImpl::class.java,
        XQuery
    )

    val VAR_VALUE: IElementType = ICompositeElementType(
        "XQUERY_VAR_VALUE",
        XQueryVarValuePsiImpl::class.java,
        XQuery
    )

    val WINDOW_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_WINDOW_CLAUSE",
        XQueryWindowClausePsiImpl::class.java,
        XQuery
    )

    val WINDOW_END_CONDITION: IElementType = ICompositeElementType(
        "XQUERY_WINDOW_END_CONDITION",
        XQueryWindowEndConditionPsiImpl::class.java,
        XQuery
    )

    val WINDOW_START_CONDITION: IElementType = ICompositeElementType(
        "XQUERY_WINDOW_START_CONDITION",
        XQueryWindowStartConditionPsiImpl::class.java,
        XQuery
    )

    val WINDOW_VARS: IElementType = ICompositeElementType(
        "XQUERY_WINDOW_VARS",
        XQueryWindowVarsPsiImpl::class.java,
        XQuery
    )

    // endregion
    // region XQuery 3.1

    val ENCLOSED_CONTENT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ENCLOSED_CONTENT_EXPR",
        XQueryEnclosedContentExprPsiImpl::class.java,
        XQuery
    )

    val ENCLOSED_PREFIX_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ENCLOSED_PREFIX_EXPR",
        XQueryEnclosedPrefixExprPsiImpl::class.java,
        XQuery
    )

    val ENCLOSED_TRY_TARGET_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ENCLOSED_TRY_TARGET_EXPR",
        XQueryEnclosedTryTargetExprPsiImpl::class.java,
        XQuery
    )

    val ENCLOSED_URI_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ENCLOSED_URI_EXPR",
        XQueryEnclosedUriExprPsiImpl::class.java,
        XQuery
    )

    val NODE_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_NODE_CONSTRUCTOR",
        XQueryNodeConstructorPsiImpl::class.java,
        XQuery
    )

    val STRING_CONSTRUCTOR_CONTENT: IElementType = ICompositeElementType(
        "XQUERY_STRING_CONSTRUCTOR_CONTENT",
        XQueryStringConstructorContentPsiImpl::class.java,
        XQuery
    )

    val STRING_CONSTRUCTOR_INTERPOLATION: IElementType = ICompositeElementType(
        "XQUERY_STRING_CONSTRUCTOR_INTERPOLATION",
        XQueryStringConstructorInterpolationPsiImpl::class.java,
        XQuery
    )

    val STRING_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_STRING_CONSTRUCTOR",
        XQueryStringConstructorPsiImpl::class.java,
        XQuery
    )

    // endregion
    // region Full Text 1.0

    val FT_OPTION_DECL: IElementType = ICompositeElementType(
        "XQUERY_FT_OPTION_DECL",
        FTOptionDeclPsiImpl::class.java,
        XQuery
    )

    val FT_MATCH_OPTIONS: IElementType = ICompositeElementType(
        "XQUERY_FT_MATCH_OPTIONS",
        FTMatchOptionsPsiImpl::class.java,
        XQuery
    )

    val FT_CASE_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_CASE_OPTION",
        FTCaseOptionPsiImpl::class.java,
        XQuery
    )

    val FT_DIACRITICS_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_DIACRITICS_OPTION",
        FTDiacriticsOptionPsiImpl::class.java,
        XQuery
    )

    val FT_EXTENSION_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_EXTENSION_OPTION",
        FTExtensionOptionPsiImpl::class.java,
        XQuery
    )

    val FT_LANGUAGE_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_LANGUAGE_OPTION",
        FTLanguageOptionPsiImpl::class.java,
        XQuery
    )

    val FT_STEM_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_STEM_OPTION",
        FTStemOptionPsiImpl::class.java,
        XQuery
    )

    val FT_STOP_WORD_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_STOP_WORD_OPTION",
        FTStopWordOptionPsiImpl::class.java,
        XQuery
    )

    val FT_THESAURUS_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_THESAURUS_OPTION",
        FTThesaurusOptionPsiImpl::class.java,
        XQuery
    )

    val FT_WILDCARD_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_WILDCARD_OPTION",
        FTWildCardOptionPsiImpl::class.java,
        XQuery
    )

    val FT_THESAURUS_ID: IElementType = ICompositeElementType(
        "XQUERY_FT_THESAURUS_ID",
        FTThesaurusIDPsiImpl::class.java,
        XQuery
    )

    val FT_LITERAL_RANGE: IElementType = ICompositeElementType(
        "XQUERY_FT_LITERAL_RANGE",
        FTLiteralRangePsiImpl::class.java,
        XQuery
    )

    val FT_STOP_WORDS: IElementType = ICompositeElementType(
        "XQUERY_FT_STOP_WORDS",
        FTStopWordsPsiImpl::class.java,
        XQuery
    )

    val FT_STOP_WORDS_INCL_EXCL: IElementType = ICompositeElementType(
        "XQUERY_FT_STOP_WORDS_INCL_EXCL",
        FTStopWordsInclExclPsiImpl::class.java,
        XQuery
    )

    val FT_SCORE_VAR: IElementType = ICompositeElementType(
        "XQUERY_FT_SCORE_VAR",
        FTScoreVarPsiImpl::class.java,
        XQuery
    )

    val FT_CONTAINS_EXPR: IElementType = ICompositeElementType(
        "XQUERY_FT_CONTAINS_EXPR",
        FTContainsExprPsiImpl::class.java,
        XQuery
    )

    val FT_SELECTION: IElementType = ICompositeElementType(
        "XQUERY_FT_SELECTION",
        FTSelectionPsiImpl::class.java,
        XQuery
    )

    val FT_OR: IElementType = ICompositeElementType(
        "XQUERY_FT_OR",
        FTOrPsiImpl::class.java,
        XQuery
    )

    val FT_AND: IElementType = ICompositeElementType(
        "XQUERY_FT_AND",
        FTAndPsiImpl::class.java,
        XQuery
    )

    val FT_MILD_NOT: IElementType = ICompositeElementType(
        "XQUERY_FT_MILD_NOT",
        FTMildNotPsiImpl::class.java,
        XQuery
    )

    val FT_UNARY_NOT: IElementType = ICompositeElementType(
        "XQUERY_FT_UNARY_NOT",
        FTUnaryNotPsiImpl::class.java,
        XQuery
    )

    val FT_PRIMARY_WITH_OPTIONS: IElementType = ICompositeElementType(
        "XQUERY_FT_PRIMARY_WITH_OPTIONS",
        FTPrimaryWithOptionsPsiImpl::class.java,
        XQuery
    )

    val FT_PRIMARY: IElementType = ICompositeElementType(
        "XQUERY_FT_PRIMARY",
        FTPrimaryPsiImpl::class.java,
        XQuery
    )

    val FT_WORDS: IElementType = ICompositeElementType(
        "XQUERY_FT_WORDS",
        FTWordsPsiImpl::class.java,
        XQuery
    )

    val FT_WORDS_VALUE: IElementType = ICompositeElementType(
        "XQUERY_FT_WORDS_VALUE",
        FTWordsValuePsiImpl::class.java,
        XQuery
    )

    val FT_EXTENSION_SELECTION: IElementType = ICompositeElementType(
        "XQUERY_FT_EXTENSION_SELECTION",
        FTExtensionSelectionPsiImpl::class.java,
        XQuery
    )

    val FT_ANYALL_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_ANYALL_OPTION",
        FTAnyallOptionPsiImpl::class.java,
        XQuery
    )

    val FT_TIMES: IElementType = ICompositeElementType(
        "XQUERY_FT_TIMES",
        FTTimesPsiImpl::class.java,
        XQuery
    )

    val FT_RANGE: IElementType = ICompositeElementType(
        "XQUERY_FT_RANGE",
        FTRangePsiImpl::class.java,
        XQuery
    )

    val FT_WEIGHT: IElementType = ICompositeElementType(
        "XQUERY_FT_WEIGHT",
        FTWeightPsiImpl::class.java,
        XQuery
    )

    val FT_ORDER: IElementType = ICompositeElementType(
        "XQUERY_FT_ORDER",
        FTOrderPsiImpl::class.java,
        XQuery
    )

    val FT_WINDOW: IElementType = ICompositeElementType(
        "XQUERY_FT_WINDOW",
        FTWindowPsiImpl::class.java,
        XQuery
    )

    val FT_DISTANCE: IElementType = ICompositeElementType(
        "XQUERY_FT_DISTANCE",
        FTDistancePsiImpl::class.java,
        XQuery
    )

    val FT_SCOPE: IElementType = ICompositeElementType(
        "XQUERY_FT_SCOPE",
        FTScopePsiImpl::class.java,
        XQuery
    )

    val FT_CONTENT: IElementType = ICompositeElementType(
        "XQUERY_FT_CONTENT",
        FTContentPsiImpl::class.java,
        XQuery
    )

    val FT_UNIT: IElementType = ICompositeElementType(
        "XQUERY_FT_UNIT",
        FTUnitPsiImpl::class.java,
        XQuery
    )

    val FT_BIG_UNIT: IElementType = ICompositeElementType(
        "XQUERY_FT_BIG_UNIT",
        FTBigUnitPsiImpl::class.java,
        XQuery
    )

    val FT_IGNORE_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_IGNORE_OPTION",
        FTIgnoreOptionPsiImpl::class.java,
        XQuery
    )

    // endregion
    // region Scripting Extension 1.0

    val ASSIGNMENT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_ASSIGNMENT_EXPR",
        ScriptingAssignmentExprPsiImpl::class.java,
        XQuery
    )

    val BLOCK: IElementType = ICompositeElementType(
        "XQUERY_BLOCK",
        ScriptingBlockPsiImpl::class.java,
        XQuery
    )

    val BLOCK_BODY: IElementType = ICompositeElementType(
        "XQUERY_BLOCK_BODY",
        ScriptingBlockBodyPsiImpl::class.java,
        XQuery
    )

    val BLOCK_DECLS: IElementType = ICompositeElementType(
        "XQUERY_BLOCK_DECLS",
        ScriptingBlockDeclsPsiImpl::class.java,
        XQuery
    )

    val BLOCK_EXPR: IElementType = ICompositeElementType(
        "XQUERY_BLOCK_EXPR",
        ScriptingBlockExprPsiImpl::class.java,
        XQuery
    )

    val BLOCK_VAR_DECL: IElementType = ICompositeElementType(
        "XQUERY_BLOCK_VAR_DECL",
        ScriptingBlockVarDeclPsiImpl::class.java,
        XQuery
    )

    val CONCAT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_CONCAT_EXPR",
        ScriptingConcatExprPsiImpl::class.java,
        XQuery
    )

    val EXIT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_EXIT_EXPR",
        ScriptingExitExprPsiImpl::class.java,
        XQuery
    )

    val WHILE_BODY: IElementType = ICompositeElementType(
        "XQUERY_WHILE_BODY",
        ScriptingWhileBodyPsiImpl::class.java,
        XQuery
    )

    val WHILE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_WHILE_EXPR",
        ScriptingWhileExprPsiImpl::class.java,
        XQuery
    )

    // endregion
    // region Update Facility 1.0

    val REVALIDATION_DECL: IElementType = ICompositeElementType(
        "XQUERY_REVALIDATION_DECL",
        UpdateFacilityRevalidationDeclPsiImpl::class.java,
        XQuery
    )

    val INSERT_EXPR: IElementType = ICompositeElementType(
        "XQUERY_INSERT_EXPR",
        UpdateFacilityInsertExprPsiImpl::class.java,
        XQuery
    )
    val DELETE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_DELETE_EXPR",
        UpdateFacilityDeleteExprPsiImpl::class.java,
        XQuery
    )
    val REPLACE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_REPLACE_EXPR",
        UpdateFacilityReplaceExprPsiImpl::class.java,
        XQuery
    )
    val RENAME_EXPR: IElementType = ICompositeElementType(
        "XQUERY_RENAME_EXPR",
        UpdateFacilityRenameExprPsiImpl::class.java,
        XQuery
    )

    val NEW_NAME_EXPR: IElementType = ICompositeElementType(
        "XQUERY_NEW_NAME_EXPR",
        UpdateFacilityNewNameExprPsiImpl::class.java,
        XQuery
    )
    val SOURCE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_SOURCE_EXPR",
        UpdateFacilitySourceExprPsiImpl::class.java,
        XQuery
    )
    val TARGET_EXPR: IElementType = ICompositeElementType(
        "XQUERY_TARGET_EXPR",
        UpdateFacilityTargetExprPsiImpl::class.java,
        XQuery
    )
    val INSERT_EXPR_TARGET_CHOICE: IElementType = ICompositeElementType(
        "XQUERY_INSERT_EXPR_TARGET_CHOICE",
        UpdateFacilityInsertExprTargetChoicePsiImpl::class.java,
        XQuery
    )

    // endregion
    // region Update Facility 3.0

    val COPY_MODIFY_EXPR: IElementType = ICompositeElementType(
        "XQUERY_COPY_MODIFY_EXPR",
        UpdateFacilityCopyModifyExprPsiImpl::class.java,
        XQuery
    )
    val UPDATING_FUNCTION_CALL: IElementType = ICompositeElementType(
        "XQUERY_UPDATING_FUNCTION_CALL",
        UpdateFacilityUpdatingFunctionCallPsiImpl::class.java,
        XQuery
    )

    val TRANSFORM_WITH_EXPR: IElementType = ICompositeElementType(
        "XQUERY_TRANSFORM_WITH_EXPR",
        UpdateFacilityTransformWithExprPsiImpl::class.java,
        XQuery
    )

    // endregion
    // region XQuery IntelliJ Plugin

    val ANNOTATED_SEQUENCE_TYPE: IElementType = ICompositeElementType(
        "XQUERY_ANNOTATED_SEQUENCE_TYPE",
        PluginAnnotatedSequenceTypePsiImpl::class.java,
        XQuery
    )

    val ANY_ARRAY_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_ARRAY_NODE_TEST",
        PluginAnyArrayNodeTestPsiImpl::class.java,
        XQuery
    )

    val ANY_BOOLEAN_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_BOOLEAN_NODE_TEST",
        PluginAnyBooleanNodeTestPsiImpl::class.java,
        XQuery
    )

    val ANY_MAP_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_MAP_NODE_TEST",
        PluginAnyMapNodeTestPsiImpl::class.java,
        XQuery
    )

    val ANY_NULL_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_NULL_NODE_TEST",
        PluginAnyNullNodeTestPsiImpl::class.java,
        XQuery
    )

    val ANY_NUMBER_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_ANY_NUMBER_NODE_TEST",
        PluginAnyNumberNodeTestPsiImpl::class.java,
        XQuery
    )

    val ATTRIBUTE_DECL_TEST: IElementType = ICompositeElementType(
        "XQUERY_ATTRIBUTE_DECL_TEST",
        PluginAttributeDeclTestPsiImpl::class.java,
        XQuery
    )

    val BINARY_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_BINARY_CONSTRUCTOR",
        PluginBinaryConstructorPsiImpl::class.java,
        XQuery
    )

    val BINARY_TEST: IElementType = ICompositeElementType(
        "XQUERY_BINARY_TEST",
        PluginBinaryTestPsiImpl::class.java,
        XQuery
    )

    val BLOCK_VAR_DECL_ENTRY: IElementType = ICompositeElementType(
        "XQUERY_BLOCK_VAR_DECL_ENTRY",
        PluginBlockVarDeclEntryPsiImpl::class.java,
        XQuery
    )

    val BOOLEAN_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_BOOLEAN_CONSTRUCTOR",
        PluginBooleanConstructorPsiImpl::class.java,
        XQuery
    )

    val COMPATIBILITY_ANNOTATION: IElementType = ICompositeElementType(
        "XQUERY_COMPATIBILITY_ANNOTATION",
        PluginCompatibilityAnnotationPsiImpl::class.java,
        XQuery
    )

    val COMPLEX_TYPE_TEST: IElementType = ICompositeElementType(
        "XQUERY_COMPLEX_TYPE_TEST",
        PluginComplexTypeTestPsiImpl::class.java,
        XQuery
    )

    val DEFAULT_CASE_CLAUSE: IElementType = ICompositeElementType(
        "XQUERY_DEFAULT_CASE_CLAUSE",
        PluginDefaultCaseClausePsiImpl::class.java,
        XQuery
    )

    val DIR_ATTRIBUTE: IElementType = ICompositeElementType(
        "XQUERY_DIR_ATTRIBUTE",
        PluginDirAttributePsiImpl::class.java,
        XQuery
    )

    val ELEMENT_DECL_TEST: IElementType = ICompositeElementType(
        "XQUERY_ELEMENT_DECL_TEST",
        PluginElementDeclTestPsiImpl::class.java,
        XQuery
    )

    val FT_FUZZY_OPTION: IElementType = ICompositeElementType(
        "XQUERY_FT_FUZZY_OPTION",
        PluginFTFuzzyOptionPsiImpl::class.java,
        XQuery
    )

    val NAMED_ARRAY_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_ARRAY_NODE_TEST",
        PluginNamedArrayNodeTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_BOOLEAN_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_BOOLEAN_NODE_TEST",
        PluginNamedBooleanNodeTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_KIND_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_KIND_TEST",
        PluginNamedKindTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_MAP_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_MAP_NODE_TEST",
        PluginNamedMapNodeTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_NULL_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_NULL_NODE_TEST",
        PluginNamedNullNodeTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_NUMBER_NODE_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_NUMBER_NODE_TEST",
        PluginNamedNumberNodeTestPsiImpl::class.java,
        XQuery
    )

    val NAMED_TEXT_TEST: IElementType = ICompositeElementType(
        "XQUERY_NAMED_TEXT_TEST",
        PluginNamedTextTestPsiImpl::class.java,
        XQuery
    )

    val NON_DETERMINISTIC_FUNCTION_CALL: IElementType = ICompositeElementType(
        "NON_DETERMINISTIC_FUNCTION_CALL",
        PluginNonDeterministicFunctionCallPsiImpl::class.java,
        XQuery
    )

    val NULL_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_NULL_CONSTRUCTOR",
        PluginNullConstructorPsiImpl::class.java,
        XQuery
    )

    val NUMBER_CONSTRUCTOR: IElementType = ICompositeElementType(
        "XQUERY_NUMBER_CONSTRUCTOR",
        PluginNumberConstructorPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_COMPONENT_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_COMPONENT_TEST",
        PluginSchemaComponentTestPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_FACET_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_FACET_TEST",
        PluginSchemaFacetTestPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_PARTICLE_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_PARTICLE_TEST",
        PluginSchemaParticleTestPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_ROOT_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_ROOT_TEST",
        PluginSchemaRootTestPsiImpl::class.java,
        XQuery
    )

    val SCHEMA_TYPE_TEST: IElementType = ICompositeElementType(
        "XQUERY_SCHEMA_TYPE_TEST",
        PluginSchemaTypeTestPsiImpl::class.java,
        XQuery
    )

    val SEQUENCE_TYPE_LIST: IElementType = ICompositeElementType(
        "XQUERY_SEQUENCE_TYPE_LIST",
        PluginSequenceTypeListPsiImpl::class.java,
        XQuery
    )

    val SIMPLE_INLINE_FUNCTION_EXPR: IElementType = ICompositeElementType(
        "XQUERY_SIMPLE_INLINE_FUNCTION_EXPR",
        PluginSimpleInlineFunctionExprImpl::class.java,
        XQuery
    )

    val SIMPLE_TYPE_TEST: IElementType = ICompositeElementType(
        "XQUERY_SIMPLE_TYPE_TEST",
        PluginSimpleTypeTestPsiImpl::class.java,
        XQuery
    )

    val STYLESHEET_IMPORT: IElementType = ICompositeElementType(
        "XQUERY_STYLESHEET_IMPORT",
        PluginStylesheetImportPsiImpl::class.java,
        XQuery
    )

    val TRANSACTION_SEPARATOR: IElementType = ICompositeElementType(
        "XQUERY_TRANSACTION_SEPARATOR",
        PluginTransactionSeparatorPsiImpl::class.java,
        XQuery
    )

    val TUPLE_FIELD: IElementType = ICompositeElementType(
        "XQUERY_TUPLE_FIELD",
        PluginTupleFieldImpl::class.java,
        XQuery
    )

    val TUPLE_TYPE: IElementType = ICompositeElementType(
        "XQUERY_TUPLE_TYPE",
        PluginTupleTypeImpl::class.java,
        XQuery
    )

    val TYPE_DECL: IElementType = ICompositeElementType(
        "XQUERY_TYPE_DECL",
        PluginTypeDeclImpl::class.java,
        XQuery
    )

    val UNION_TYPE: IElementType = ICompositeElementType(
        "XQUERY_UNION_TYPE",
        PluginUnionTypeImpl::class.java,
        XQuery
    )

    val UPDATE_EXPR: IElementType = ICompositeElementType(
        "XQUERY_UPDATE_EXPR",
        PluginUpdateExprPsiImpl::class.java,
        XQuery
    )

    // endregion
}
