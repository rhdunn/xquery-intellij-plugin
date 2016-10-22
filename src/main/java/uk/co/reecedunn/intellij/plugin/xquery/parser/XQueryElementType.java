/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.marklogic.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.*;

public interface XQueryElementType {
    IFileElementType FILE = new IFileElementType(XQuery.INSTANCE);

    // region XQuery 1.0

    IElementType CDATA_SECTION = new ICompositeElementType("XQUERY_CDATA_SECTION", XQueryCDataSectionPsiImpl.class);
    IElementType COMMENT = new ICompositeElementType("XQUERY_COMMENT", XQueryCommentPsiImpl.class);
    IElementType NCNAME = new ICompositeElementType("XQUERY_NCNAME", XQueryNCNamePsiImpl.class);
    IElementType QNAME = new ICompositeElementType("XQUERY_QNAME", XQueryQNamePsiImpl.class);
    IElementType URI_LITERAL = new ICompositeElementType("XQUERY_URI_LITERAL", XQueryUriLiteralPsiImpl.class);
    IElementType VAR_NAME = new ICompositeElementType("XQUERY_VAR_NAME", XQueryVarNamePsiImpl.class);

    IElementType LITERAL = new ICompositeElementType("XQUERY_LITERAL", XQueryLiteralPsiImpl.class);
    IElementType STRING_LITERAL = new ICompositeElementType("XQUERY_STRING_LITERAL", XQueryStringLiteralPsiImpl.class);
    IElementType VAR_REF = new ICompositeElementType("XQUERY_VAR_REF", XQueryVarRefPsiImpl.class);
    IElementType PARENTHESIZED_EXPR = new ICompositeElementType("XQUERY_PARENTHESIZED_EXPR", XQueryParenthesizedExprPsiImpl.class);
    IElementType CONTEXT_ITEM_EXPR = new ICompositeElementType("XQUERY_CONTEXT_ITEM_EXPR", XQueryContextItemExprPsiImpl.class);
    IElementType ORDERED_EXPR = new ICompositeElementType("XQUERY_ORDERED_EXPR", XQueryOrderedExprPsiImpl.class);
    IElementType UNORDERED_EXPR = new ICompositeElementType("XQUERY_UNORDERED_EXPR", XQueryUnorderedExprPsiImpl.class);
    IElementType FUNCTION_CALL = new ICompositeElementType("XQUERY_FUNCTION_CALL", XQueryFunctionCallPsiImpl.class);

    IElementType AXIS_STEP = new ICompositeElementType("XQUERY_AXIS_STEP", XQueryAxisStepPsiImpl.class);
    IElementType FORWARD_STEP = new ICompositeElementType("XQUERY_FORWARD_STEP", XQueryForwardStepPsiImpl.class);
    IElementType FORWARD_AXIS = new ICompositeElementType("XQUERY_FORWARD_AXIS", XQueryForwardAxisPsiImpl.class);
    IElementType ABBREV_FORWARD_STEP = new ICompositeElementType("XQUERY_ABBREV_FORWARD_STEP", XQueryAbbrevForwardStepPsiImpl.class);
    IElementType REVERSE_STEP = new ICompositeElementType("XQUERY_REVERSE_STEP", XQueryReverseStepPsiImpl.class);
    IElementType REVERSE_AXIS = new ICompositeElementType("XQUERY_REVERSE_AXIS", XQueryReverseAxisPsiImpl.class);
    IElementType ABBREV_REVERSE_STEP = new ICompositeElementType("XQUERY_ABBREV_REVERSE_STEP", XQueryAbbrevReverseStepPsiImpl.class);
    IElementType NODE_TEST = new ICompositeElementType("XQUERY_NODE_TEST", XQueryNodeTestPsiImpl.class);
    IElementType NAME_TEST = new ICompositeElementType("XQUERY_NAME_TEST", XQueryNameTestPsiImpl.class);
    IElementType WILDCARD = new ICompositeElementType("XQUERY_WILDCARD", XQueryWildcardPsiImpl.class);
    IElementType FILTER_EXPR = new ICompositeElementType("XQUERY_FILTER_EXPR", XQueryFilterExprPsiImpl.class);
    IElementType PREDICATE_LIST = new ICompositeElementType("XQUERY_PREDICATE_LIST", XQueryPredicateListPsiImpl.class);
    IElementType PREDICATE = new ICompositeElementType("XQUERY_PREDICATE", XQueryPredicatePsiImpl.class);

    IElementType RELATIVE_PATH_EXPR = new ICompositeElementType("XQUERY_RELATIVE_PATH_EXPR", XQueryRelativePathExprPsiImpl.class);
    IElementType PATH_EXPR = new ICompositeElementType("XQUERY_PATH_EXPR", XQueryPathExprPsiImpl.class);
    IElementType PRAGMA = new ICompositeElementType("XQUERY_PRAGMA", XQueryPragmaPsiImpl.class);
    IElementType EXTENSION_EXPR = new ICompositeElementType("XQUERY_EXTENSION_EXPR", XQueryExtensionExprPsiImpl.class);
    IElementType VALIDATE_EXPR = new ICompositeElementType("XQUERY_VALIDATE_EXPR", XQueryValidateExprPsiImpl.class);

    IElementType SINGLE_TYPE = new ICompositeElementType("XQUERY_SINGLE_TYPE", XQuerySingleTypePsiImpl.class);
    IElementType UNARY_EXPR = new ICompositeElementType("XQUERY_UNARY_EXPR", XQueryUnaryExprPsiImpl.class);
    IElementType CAST_EXPR = new ICompositeElementType("XQUERY_CAST_EXPR", XQueryCastExprPsiImpl.class);
    IElementType CASTABLE_EXPR = new ICompositeElementType("XQUERY_CASTABLE_EXPR", XQueryCastableExprPsiImpl.class);
    IElementType TREAT_EXPR = new ICompositeElementType("XQUERY_TREAT_EXPR", XQueryTreatExprPsiImpl.class);
    IElementType INSTANCEOF_EXPR = new ICompositeElementType("XQUERY_INSTANCEOF_EXPR", XQueryInstanceofExprPsiImpl.class);
    IElementType INTERSECT_EXCEPT_EXPR = new ICompositeElementType("XQUERY_INTERSECT_EXCEPT_EXPR", XQueryIntersectExceptExprPsiImpl.class);
    IElementType UNION_EXPR = new ICompositeElementType("XQUERY_UNION_EXPR", XQueryUnionExprPsiImpl.class);
    IElementType MULTIPLICATIVE_EXPR = new ICompositeElementType("XQUERY_MULTIPLICATIVE_EXPR", XQueryMultiplicativeExprPsiImpl.class);
    IElementType ADDITIVE_EXPR = new ICompositeElementType("XQUERY_ADDITIVE_EXPR", XQueryAdditiveExprPsiImpl.class);
    IElementType RANGE_EXPR = new ICompositeElementType("XQUERY_RANGE_EXPR", XQueryRangeExprPsiImpl.class);
    IElementType COMPARISON_EXPR = new ICompositeElementType("XQUERY_COMPARISON_EXPR", XQueryComparisonExprPsiImpl.class);
    IElementType AND_EXPR = new ICompositeElementType("XQUERY_AND_EXPR", XQueryAndExprPsiImpl.class);
    IElementType OR_EXPR = new ICompositeElementType("XQUERY_OR_EXPR", XQueryOrExprPsiImpl.class);

    IElementType ORDER_MODIFIER = new ICompositeElementType("XQUERY_ORDER_MODIFIER", XQueryOrderModifierPsiImpl.class);
    IElementType ORDER_SPEC = new ICompositeElementType("XQUERY_ORDER_SPEC", XQueryOrderSpecPsiImpl.class);
    IElementType ORDER_SPEC_LIST = new ICompositeElementType("XQUERY_ORDER_SPEC_LIST", XQueryOrderSpecListPsiImpl.class);
    IElementType POSITIONAL_VAR = new ICompositeElementType("XQUERY_POSITIONAL_VAR", XQueryPositionalVarPsiImpl.class);
    IElementType ORDER_BY_CLAUSE = new ICompositeElementType("XQUERY_ORDER_BY_CLAUSE", XQueryOrderByClausePsiImpl.class);
    IElementType WHERE_CLAUSE = new ICompositeElementType("XQUERY_WHERE_CLAUSE", XQueryWhereClausePsiImpl.class);
    IElementType LET_CLAUSE = new ICompositeElementType("XQUERY_LET_CLAUSE", XQueryLetClausePsiImpl.class);
    IElementType FOR_CLAUSE = new ICompositeElementType("XQUERY_FOR_CLAUSE", XQueryForClausePsiImpl.class);
    IElementType FLWOR_EXPR = new ICompositeElementType("XQUERY_FLWOR_EXPR", XQueryFLWORExprPsiImpl.class);

    IElementType QUANTIFIED_EXPR = new ICompositeElementType("XQUERY_QUANTIFIED_EXPR", XQueryQuantifiedExprPsiImpl.class);
    IElementType TYPESWITCH_EXPR = new ICompositeElementType("XQUERY_TYPESWITCH_EXPR", XQueryTypeswitchExprPsiImpl.class);
    IElementType CASE_CLAUSE = new ICompositeElementType("XQUERY_CASE_CLAUSE", XQueryCaseClausePsiImpl.class);
    IElementType IF_EXPR = new ICompositeElementType("XQUERY_IF_EXPR", XQueryIfExprPsiImpl.class);

    IElementType QUERY_BODY = new ICompositeElementType("XQUERY_QUERY_BODY", XQueryQueryBodyPsiImpl.class);
    IElementType EXPR = new ICompositeElementType("XQUERY_EXPR", XQueryExprPsiImpl.class);
    IElementType ENCLOSED_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_EXPR", XQueryEnclosedExprPsiImpl.class);

    IElementType IMPORT = new ICompositeElementType("XQUERY_IMPORT", XQueryImportPsiImpl.class);
    IElementType SCHEMA_PREFIX = new ICompositeElementType("XQUERY_SCHEMA_PREFIX", XQuerySchemaPrefixPsiImpl.class);
    IElementType NAMESPACE_DECL = new ICompositeElementType("XQUERY_NAMESPACE_DECL", XQueryNamespaceDeclPsiImpl.class);
    IElementType SCHEMA_IMPORT = new ICompositeElementType("XQUERY_SCHEMA_IMPORT", XQuerySchemaImportPsiImpl.class);
    IElementType MODULE_IMPORT = new ICompositeElementType("XQUERY_MODULE_IMPORT", XQueryModuleImportPsiImpl.class);
    IElementType PROLOG = new ICompositeElementType("XQUERY_PROLOG", XQueryPrologPsiImpl.class);

    IElementType TYPE_NAME = new ICompositeElementType("XQUERY_TYPE_NAME", XQueryTypeNamePsiImpl.class);
    IElementType ELEMENT_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ELEMENT_NAME_OR_WILDCARD", XQueryElementNameOrWildcardPsiImpl.class);
    IElementType ELEMENT_NAME = new ICompositeElementType("XQUERY_ELEMENT_NAME", XQueryElementNamePsiImpl.class);
    IElementType ELEMENT_DECLARATION = new ICompositeElementType("XQUERY_ELEMENT_DECLARATION", XQueryElementDeclarationPsiImpl.class);
    IElementType ATTRIB_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ATTRIB_NAME_OR_WILDCARD", XQueryAttribNameOrWildcardPsiImpl.class);
    IElementType ATTRIBUTE_NAME = new ICompositeElementType("XQUERY_ATTRIBUTE_NAME", XQueryAttributeNamePsiImpl.class);
    IElementType ATTRIBUTE_DECLARATION = new ICompositeElementType("XQUERY_ATTRIBUTE_DECLARATION", XQueryAttributeDeclarationPsiImpl.class);

    IElementType DOCUMENT_TEST = new ICompositeElementType("XQUERY_DOCUMENT_TEST", XQueryDocumentTestPsiImpl.class);
    IElementType ELEMENT_TEST = new ICompositeElementType("XQUERY_ELEMENT_TEST", XQueryElementTestPsiImpl.class);
    IElementType SCHEMA_ELEMENT_TEST = new ICompositeElementType("XQUERY_SCHEMA_ELEMENT_TEST", XQuerySchemaElementTestPsiImpl.class);
    IElementType ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_ATTRIBUTE_TEST", XQueryAttributeTestPsiImpl.class);
    IElementType SCHEMA_ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_SCHEMA_ATTRIBUTE_TEST", XQuerySchemaAttributeTestPsiImpl.class);
    IElementType PI_TEST = new ICompositeElementType("XQUERY_PI_TEST", XQueryPITestPsiImpl.class);
    IElementType COMMENT_TEST = new ICompositeElementType("XQUERY_COMMENT_TEST", XQueryCommentTestPsiImpl.class);
    IElementType TEXT_TEST = new ICompositeElementType("XQUERY_TEXT_TEST", XQueryTextTestPsiImpl.class);
    IElementType ANY_KIND_TEST = new ICompositeElementType("XQUERY_ANY_KIND_TEST", XQueryAnyKindTestPsiImpl.class);
    IElementType ITEM_TYPE = new ICompositeElementType("XQUERY_ITEM_TYPE", XQueryItemTypePsiImpl.class);
    IElementType OCCURRENCE_INDICATOR = new ICompositeElementType("XQUERY_OCCURRENCE_INDICATOR", XQueryOccurrenceIndicatorPsiImpl.class);
    IElementType TYPE_DECLARATION = new ICompositeElementType("XQUERY_TYPE_DECLARATION", XQueryTypeDeclarationPsiImpl.class);
    IElementType SEQUENCE_TYPE = new ICompositeElementType("XQUERY_SEQUENCE_TYPE", XQuerySequenceTypePsiImpl.class);

    IElementType PARAM = new ICompositeElementType("XQUERY_PARAM", XQueryParamPsiImpl.class);
    IElementType PARAM_LIST = new ICompositeElementType("XQUERY_PARAM_LIST", XQueryParamListPsiImpl.class);

    IElementType FUNCTION_DECL = new ICompositeElementType("XQUERY_FUNCTION_DECL", XQueryFunctionDeclPsiImpl.class);
    IElementType CONSTRUCTION_DECL = new ICompositeElementType("XQUERY_CONSTRUCTION_DECL", XQueryConstructionDeclPsiImpl.class);
    IElementType VAR_DECL = new ICompositeElementType("XQUERY_VAR_DECL", XQueryVarDeclPsiImpl.class);
    IElementType BASE_URI_DECL = new ICompositeElementType("XQUERY_BASE_URI_DECL", XQueryBaseURIDeclPsiImpl.class);
    IElementType DEFAULT_COLLATION_DECL = new ICompositeElementType("XQUERY_DEFAULT_COLLATION_DECL", XQueryDefaultCollationDeclPsiImpl.class);
    IElementType COPY_NAMESPACES_DECL = new ICompositeElementType("XQUERY_COPY_NAMESPACES_DECL", XQueryCopyNamespacesDeclPsiImpl.class);
    IElementType EMPTY_ORDER_DECL = new ICompositeElementType("XQUERY_EMPTY_ORDER_DECL", XQueryEmptyOrderDeclPsiImpl.class);
    IElementType ORDERING_MODE_DECL = new ICompositeElementType("XQUERY_ORDERING_MODE_DECL", XQueryOrderingModeDeclPsiImpl.class);
    IElementType OPTION_DECL = new ICompositeElementType("XQUERY_OPTION_DECL", XQueryOptionDeclPsiImpl.class);
    IElementType DEFAULT_NAMESPACE_DECL = new ICompositeElementType("XQUERY_DEFAULT_NAMESPACE_DECL", XQueryDefaultNamespaceDeclPsiImpl.class);
    IElementType BOUNDARY_SPACE_DECL = new ICompositeElementType("XQUERY_BOUNDARY_SPACE_DECL", XQueryBoundarySpaceDeclPsiImpl.class);
    IElementType UNKNOWN_DECL = new ICompositeElementType("XQUERY_UNKNOWN_DECL", XQueryUnknownDeclPsiImpl.class);
    IElementType MODULE_DECL = new ICompositeElementType("XQUERY_MODULE_DECL", XQueryModuleDeclPsiImpl.class);
    IElementType VERSION_DECL = new ICompositeElementType("XQUERY_VERSION_DECL", XQueryVersionDeclPsiImpl.class);
    IElementType MAIN_MODULE = new ICompositeElementType("XQUERY_MAIN_MODULE", XQueryMainModulePsiImpl.class);
    IElementType LIBRARY_MODULE = new ICompositeElementType("XQUERY_LIBRARY_MODULE", XQueryLibraryModulePsiImpl.class);
    IElementType MODULE = new ICompositeElementType("XQUERY_MODULE", XQueryModulePsiImpl.class);

    IElementType CONSTRUCTOR = new ICompositeElementType("XQUERY_CONSTRUCTOR", XQueryConstructorPsiImpl.class);
    IElementType DIR_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_ELEM_CONSTRUCTOR", XQueryDirElemConstructorPsiImpl.class);
    IElementType DIR_ATTRIBUTE_LIST = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_LIST", XQueryDirAttributeListPsiImpl.class);
    IElementType DIR_ATTRIBUTE_VALUE = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_VALUE", XQueryDirAttributeValuePsiImpl.class);
    IElementType DIR_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQueryDirCommentConstructorPsiImpl.class);
    IElementType DIR_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_PI_CONSTRUCTOR", XQueryDirPIConstructorPsiImpl.class);
    IElementType DIR_ELEM_CONTENT = new ICompositeElementType("XQUERY_DIR_ELEM_CONTENT", XQueryDirElemContentPsiImpl.class);
    IElementType COMP_DOC_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_DOC_CONSTRUCTOR", XQueryCompDocConstructorPsiImpl.class);
    IElementType COMP_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ELEM_CONSTRUCTOR", XQueryCompElemConstructorPsiImpl.class);
    IElementType CONTENT_EXPR = new ICompositeElementType("XQUERY_CONTENT_EXPR", XQueryContentExprPsiImpl.class);
    IElementType COMP_ATTR_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ATTR_CONSTRUCTOR", XQueryCompAttrConstructorPsiImpl.class);
    IElementType COMP_TEXT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_TEXT_CONSTRUCTOR", XQueryCompTextConstructorPsiImpl.class);
    IElementType COMP_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_COMMENT_CONSTRUCTOR", XQueryCompCommentConstructorPsiImpl.class);
    IElementType COMP_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_PI_CONSTRUCTOR", XQueryCompPIConstructorPsiImpl.class);

    // endregion
    // region XQuery 3.0

    IElementType EQNAME = new ICompositeElementType("XQUERY_EQNAME", XQueryEQNamePsiImpl.class);
    IElementType BRACED_URI_LITERAL = new ICompositeElementType("XQUERY_BRACED_URI_LITERAL", XQueryBracedURILiteralPsiImpl.class);
    IElementType URI_QUALIFIED_NAME = new ICompositeElementType("XQUERY_URI_QUALIFIED_NAME", XQueryURIQualifiedNamePsiImpl.class);

    IElementType DECIMAL_FORMAT_DECL = new ICompositeElementType("XQUERY_DECIMAL_FORMAT_DECL", XQueryDecimalFormatDeclPsiImpl.class);
    IElementType DF_PROPERTY_NAME = new ICompositeElementType("XQUERY_DF_PROPERTY_NAME", XQueryDFPropertyNamePsiImpl.class);
    IElementType CONTEXT_ITEM_DECL = new ICompositeElementType("XQUERY_CONTEXT_ITEM_DECL", XQueryContextItemDeclPsiImpl.class);

    IElementType FUNCTION_BODY = new ICompositeElementType("XQUERY_FUNCTION_BODY", XQueryFunctionBodyPsiImpl.class);
    IElementType VAR_VALUE = new ICompositeElementType("XQUERY_VAR_VALUE", XQueryVarValuePsiImpl.class);
    IElementType VAR_DEFAULT_VALUE = new ICompositeElementType("XQUERY_VAR_DEFAULT_VALUE", XQueryVarDefaultValuePsiImpl.class);

    IElementType ATOMIC_OR_UNION_TYPE = new ICompositeElementType("XQUERY_ATOMIC_OR_UNION_TYPE", XQueryAtomicOrUnionTypePsiImpl.class);
    IElementType SIMPLE_TYPE_NAME = new ICompositeElementType("XQUERY_SIMPLE_TYPE_NAME", XQuerySimpleTypeNamePsiImpl.class);
    IElementType SEQUENCE_TYPE_UNION = new ICompositeElementType("XQUERY_SEQUENCE_TYPE_UNION", XQuerySequenceTypeUnionPsiImpl.class);

    IElementType FUNCTION_TEST = new ICompositeElementType("XQUERY_FUNCTION_TEST", XQueryFunctionTestPsiImpl.class);
    IElementType ANY_FUNCTION_TEST = new ICompositeElementType("XQUERY_ANY_FUNCTION_TEST", XQueryAnyFunctionTestPsiImpl.class);
    IElementType TYPED_FUNCTION_TEST = new ICompositeElementType("XQUERY_TYPED_FUNCTION_TEST", XQueryTypedFunctionTestPsiImpl.class);
    IElementType PARENTHESIZED_ITEM_TYPE = new ICompositeElementType("XQUERY_PARENTHESIZED_ITEM_TYPE", XQueryParenthesizedItemTypePsiImpl.class);

    IElementType NAMESPACE_NODE_TEST = new ICompositeElementType("XQUERY_NAMESPACE_NODE_TEST", XQueryNamespaceNodeTestPsiImpl.class);

    IElementType ANNOTATED_DECL = new ICompositeElementType("XQUERY_ANNOTATED_DECL", XQueryAnnotatedDeclPsiImpl.class);
    IElementType ANNOTATION = new ICompositeElementType("XQUERY_ANNOTATION", XQueryAnnotationPsiImpl.class);

    IElementType SWITCH_EXPR = new ICompositeElementType("XQUERY_SWITCH_EXPR", XQuerySwitchExprPsiImpl.class);
    IElementType SWITCH_CASE_CLAUSE = new ICompositeElementType("XQUERY_SWITCH_CASE_CLAUSE", XQuerySwitchCaseClausePsiImpl.class);
    IElementType SWITCH_CASE_OPERAND = new ICompositeElementType("XQUERY_SWITCH_CASE_OPERAND", XQuerySwitchCaseClausePsiImpl.class);

    IElementType TRY_CATCH_EXPR = new ICompositeElementType("XQUERY_TRY_CATCH_EXPR", XQueryTryCatchExprPsiImpl.class);
    IElementType TRY_CLAUSE = new ICompositeElementType("XQUERY_TRY_CLAUSE", XQueryTryClausePsiImpl.class);
    IElementType TRY_TARGET_EXPR = new ICompositeElementType("XQUERY_TRY_TARGET_EXPR", XQueryTryTargetExprPsiImpl.class);
    IElementType CATCH_CLAUSE = new ICompositeElementType("XQUERY_CATCH_CLAUSE", XQueryCatchClausePsiImpl.class);
    IElementType CATCH_ERROR_LIST = new ICompositeElementType("XQUERY_CATCH_ERROR_LIST", XQueryCatchErrorListPsiImpl.class);

    IElementType FOR_BINDING = new ICompositeElementType("XQUERY_FOR_BINDING", XQueryForBindingPsiImpl.class);
    IElementType LET_BINDING = new ICompositeElementType("XQUERY_LET_BINDING", XQueryLetBindingPsiImpl.class);

    IElementType ALLOWING_EMPTY = new ICompositeElementType("XQUERY_ALLOWING_EMPTY", XQueryAllowingEmptyPsiImpl.class);
    IElementType INTERMEDIATE_CLAUSE = new ICompositeElementType("XQUERY_INTERMEDIATE_CLAUSE", XQueryIntermediateClausePsiImpl.class);
    IElementType COUNT_CLAUSE = new ICompositeElementType("XQUERY_COUNT_CLAUSE", XQueryCountClausePsiImpl.class);
    IElementType RETURN_CLAUSE = new ICompositeElementType("XQUERY_RETURN_CLAUSE", XQueryReturnClausePsiImpl.class);

    IElementType GROUP_BY_CLAUSE = new ICompositeElementType("XQUERY_GROUP_BY_CLAUSE", XQueryGroupByClausePsiImpl.class);
    IElementType GROUPING_VARIABLE = new ICompositeElementType("XQUERY_GROUPING_VARIABLE", XQueryGroupingVariablePsiImpl.class);

    IElementType STRING_CONCAT_EXPR = new ICompositeElementType("XQUERY_STRING_CONCAT_EXPR", XQueryStringConcatExprPsiImpl.class);
    IElementType SIMPLE_MAP_EXPR = new ICompositeElementType("XQUERY_SIMPLE_MAP_EXPR", XQuerySimpleMapExprPsiImpl.class);

    IElementType INLINE_FUNCTION_EXPR = new ICompositeElementType("XQUERY_INLINE_FUNCTION_EXPR", XQueryInlineFunctionExprPsiImpl.class);
    IElementType NAMED_FUNCTION_REF = new ICompositeElementType("XQUERY_NAMED_FUNCTION_REF", XQueryNamedFunctionRefPsiImpl.class);

    IElementType ARGUMENT_LIST = new ICompositeElementType("XQUERY_ARGUMENT_LIST", XQueryArgumentListPsiImpl.class);
    IElementType ARGUMENT = new ICompositeElementType("XQUERY_ARGUMENT", XQueryArgumentPsiImpl.class);
    IElementType ARGUMENT_PLACEHOLDER = new ICompositeElementType("XQUERY_ARGUMENT_PLACEHOLDER", XQueryArgumentPlaceholderPsiImpl.class);

    IElementType COMP_NAMESPACE_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_NAMESPACE_CONSTRUCTOR", XQueryCompNamespaceConstructorPsiImpl.class);
    IElementType PREFIX = new ICompositeElementType("XQUERY_PREFIX", XQueryPrefixPsiImpl.class);
    IElementType PREFIX_EXPR = new ICompositeElementType("XQUERY_PREFIX_EXPR", XQueryPrefixExprPsiImpl.class);
    IElementType URI_EXPR = new ICompositeElementType("XQUERY_URI_EXPR", XQueryUriExprPsiImpl.class);

    // endregion
    // region Update Facility 1.0

    IElementType REVALIDATION_DECL = new ICompositeElementType("XQUERY_REVALIDATION_DECL", UpdateFacilityRevalidationDeclPsiImpl.class);

    IElementType INSERT_EXPR = new ICompositeElementType("XQUERY_INSERT_EXPR", UpdateFacilityInsertExprPsiImpl.class);
    IElementType DELETE_EXPR = new ICompositeElementType("XQUERY_DELETE_EXPR", UpdateFacilityDeleteExprPsiImpl.class);
    IElementType REPLACE_EXPR = new ICompositeElementType("XQUERY_REPLACE_EXPR", UpdateFacilityReplaceExprPsiImpl.class);
    IElementType RENAME_EXPR = new ICompositeElementType("XQUERY_RENAME_EXPR", UpdateFacilityRenameExprPsiImpl.class);
    IElementType TRANSFORM_EXPR = new ICompositeElementType("XQUERY_TRANSFORM_EXPR", UpdateFacilityTransformExprPsiImpl.class);

    IElementType NEW_NAME_EXPR = new ICompositeElementType("XQUERY_NEW_NAME_EXPR", UpdateFacilityNewNameExprPsiImpl.class);
    IElementType SOURCE_EXPR = new ICompositeElementType("XQUERY_SOURCE_EXPR", UpdateFacilitySourceExprPsiImpl.class);
    IElementType TARGET_EXPR = new ICompositeElementType("XQUERY_TARGET_EXPR", UpdateFacilityTargetExprPsiImpl.class);
    IElementType INSERT_EXPR_TARGET_CHOICE = new ICompositeElementType("XQUERY_INSERT_EXPR_TARGET_CHOICE", UpdateFacilityInsertExprTargetChoicePsiImpl.class);

    // endregion
    // region Update Facility 3.0

    IElementType COMPATIBILITY_ANNOTATION = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION", UpdateFacilityCompatibilityAnnotationPsiImpl.class);

    // endregion
    // region MarkLogic 6.0

    IElementType TRANSACTION_SEPARATOR = new ICompositeElementType("XQUERY_TRANSACTION_SEPARATOR", MarkLogicTransactionSeparatorPsiImpl.class);

    IElementType COMPATIBILITY_ANNOTATION_MARKLOGIC = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION_MARKLOGIC", MarkLogicCompatibilityAnnotationPsiImpl.class);
    IElementType STYLESHEET_IMPORT = new ICompositeElementType("XQUERY_STYLESHEET_IMPORT", MarkLogicStylesheetImportPsiImpl.class);

    IElementType COMP_BINARY_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_BINARY_CONSTRUCTOR", MarkLogicCompBinaryConstructorPsiImpl.class);
    IElementType BINARY_TEST = new ICompositeElementType("XQUERY_BINARY_TEST", MarkLogicBinaryTestPsiImpl.class);

    // endregion
    // region MarkLogic 8.0

    IElementType COMP_ARRAY_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ARRAY_CONSTRUCTOR", MarkLogicCompArrayConstructorPsiImpl.class);
    IElementType ARRAY_TEST = new ICompositeElementType("XQUERY_ARRAY_TEST", MarkLogicArrayTestPsiImpl.class);

    IElementType COMP_BOOLEAN_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_BOOLEAN_CONSTRUCTOR", MarkLogicCompBooleanConstructorPsiImpl.class);
    IElementType BOOLEAN_TEST = new ICompositeElementType("XQUERY_BOOLEAN_TEST", MarkLogicBooleanTestPsiImpl.class);

    IElementType COMP_NULL_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_NULL_CONSTRUCTOR", MarkLogicCompNullConstructorPsiImpl.class);
    IElementType NULL_TEST = new ICompositeElementType("XQUERY_NULL_TEST", MarkLogicNullTestPsiImpl.class);

    IElementType COMP_NUMBER_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_NUMBER_CONSTRUCTOR", MarkLogicCompNumberConstructorPsiImpl.class);
    IElementType NUMBER_TEST = new ICompositeElementType("XQUERY_NUMBER_TEST", MarkLogicNumberTestPsiImpl.class);

    IElementType COMP_OBJECT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_OBJECT_CONSTRUCTOR", MarkLogicCompObjectConstructorPsiImpl.class);
    IElementType OBJECT_KEY_VALUE = new ICompositeElementType("XQUERY_OBJECT_KEY_VALUE", MarkLogicObjectKeyValuePsiImpl.class);
    IElementType OBJECT_TEST = new ICompositeElementType("XQUERY_OBJECT_TEST", MarkLogicObjectTestPsiImpl.class);

    // endregion
}
