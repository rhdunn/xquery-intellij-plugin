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
import uk.co.reecedunn.intellij.plugin.xquery.ast.impl.marklogic.*;
import uk.co.reecedunn.intellij.plugin.xquery.ast.impl.update.facility.*;
import uk.co.reecedunn.intellij.plugin.xquery.ast.impl.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.marklogic.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.*;

public interface XQueryElementType {
    IFileElementType FILE = new IFileElementType(XQuery.INSTANCE);

    // region XQuery 1.0

    IElementType CDATA_SECTION = new ICompositeElementType("XQUERY_CDATA_SECTION", XQueryCDataSectionImpl.class, XQueryCDataSectionPsiImpl.class);
    IElementType COMMENT = new ICompositeElementType("XQUERY_COMMENT", XQueryCommentImpl.class, XQueryCommentPsiImpl.class);
    IElementType NCNAME = new ICompositeElementType("XQUERY_NCNAME", XQueryNCNameImpl.class, XQueryNCNamePsiImpl.class);
    IElementType QNAME = new ICompositeElementType("XQUERY_QNAME", XQueryQNameImpl.class, XQueryQNamePsiImpl.class);
    IElementType URI_LITERAL = new ICompositeElementType("XQUERY_URI_LITERAL", XQueryUriLiteralImpl.class, XQueryUriLiteralPsiImpl.class);
    IElementType VAR_NAME = new ICompositeElementType("XQUERY_VAR_NAME", XQueryVarNameImpl.class, XQueryVarNamePsiImpl.class);

    IElementType LITERAL = new ICompositeElementType("XQUERY_LITERAL", XQueryLiteralImpl.class, XQueryLiteralPsiImpl.class);
    IElementType STRING_LITERAL = new ICompositeElementType("XQUERY_STRING_LITERAL", XQueryStringLiteralImpl.class, XQueryStringLiteralPsiImpl.class);
    IElementType VAR_REF = new ICompositeElementType("XQUERY_VAR_REF", XQueryVarRefImpl.class, XQueryVarRefPsiImpl.class);
    IElementType PARENTHESIZED_EXPR = new ICompositeElementType("XQUERY_PARENTHESIZED_EXPR", XQueryParenthesizedExprImpl.class, XQueryParenthesizedExprPsiImpl.class);
    IElementType CONTEXT_ITEM_EXPR = new ICompositeElementType("XQUERY_CONTEXT_ITEM_EXPR", XQueryContextItemExprImpl.class, XQueryContextItemExprPsiImpl.class);
    IElementType ORDERED_EXPR = new ICompositeElementType("XQUERY_ORDERED_EXPR", XQueryOrderedExprImpl.class, XQueryOrderedExprPsiImpl.class);
    IElementType UNORDERED_EXPR = new ICompositeElementType("XQUERY_UNORDERED_EXPR", XQueryUnorderedExprImpl.class, XQueryUnorderedExprPsiImpl.class);
    IElementType FUNCTION_CALL = new ICompositeElementType("XQUERY_FUNCTION_CALL", XQueryFunctionCallImpl.class, XQueryFunctionCallPsiImpl.class);

    IElementType AXIS_STEP = new ICompositeElementType("XQUERY_AXIS_STEP", XQueryAxisStepImpl.class, XQueryAxisStepPsiImpl.class);
    IElementType FORWARD_STEP = new ICompositeElementType("XQUERY_FORWARD_STEP", XQueryForwardStepImpl.class, XQueryForwardStepPsiImpl.class);
    IElementType FORWARD_AXIS = new ICompositeElementType("XQUERY_FORWARD_AXIS", XQueryForwardAxisImpl.class, XQueryForwardAxisPsiImpl.class);
    IElementType ABBREV_FORWARD_STEP = new ICompositeElementType("XQUERY_ABBREV_FORWARD_STEP", XQueryAbbrevForwardStepImpl.class, XQueryAbbrevForwardStepPsiImpl.class);
    IElementType REVERSE_STEP = new ICompositeElementType("XQUERY_REVERSE_STEP", XQueryReverseStepImpl.class, XQueryReverseStepPsiImpl.class);
    IElementType REVERSE_AXIS = new ICompositeElementType("XQUERY_REVERSE_AXIS", XQueryReverseAxisImpl.class, XQueryReverseAxisPsiImpl.class);
    IElementType ABBREV_REVERSE_STEP = new ICompositeElementType("XQUERY_ABBREV_REVERSE_STEP", XQueryAbbrevReverseStepImpl.class, XQueryAbbrevReverseStepPsiImpl.class);
    IElementType NODE_TEST = new ICompositeElementType("XQUERY_NODE_TEST", XQueryNodeTestImpl.class, XQueryNodeTestPsiImpl.class);
    IElementType NAME_TEST = new ICompositeElementType("XQUERY_NAME_TEST", XQueryNameTestImpl.class, XQueryNameTestPsiImpl.class);
    IElementType WILDCARD = new ICompositeElementType("XQUERY_WILDCARD", XQueryWildcardImpl.class, XQueryWildcardPsiImpl.class);
    IElementType FILTER_EXPR = new ICompositeElementType("XQUERY_FILTER_EXPR", XQueryFilterExprImpl.class, XQueryFilterExprPsiImpl.class);
    IElementType PREDICATE_LIST = new ICompositeElementType("XQUERY_PREDICATE_LIST", XQueryPredicateListImpl.class, XQueryPredicateListPsiImpl.class);
    IElementType PREDICATE = new ICompositeElementType("XQUERY_PREDICATE", XQueryPredicateImpl.class, XQueryPredicatePsiImpl.class);

    IElementType RELATIVE_PATH_EXPR = new ICompositeElementType("XQUERY_RELATIVE_PATH_EXPR", XQueryRelativePathExprImpl.class, XQueryRelativePathExprPsiImpl.class);
    IElementType PATH_EXPR = new ICompositeElementType("XQUERY_PATH_EXPR", XQueryPathExprImpl.class, XQueryPathExprPsiImpl.class);
    IElementType PRAGMA = new ICompositeElementType("XQUERY_PRAGMA", XQueryPragmaImpl.class, XQueryPragmaPsiImpl.class);
    IElementType EXTENSION_EXPR = new ICompositeElementType("XQUERY_EXTENSION_EXPR", XQueryExtensionExprImpl.class, XQueryExtensionExprPsiImpl.class);
    IElementType VALIDATE_EXPR = new ICompositeElementType("XQUERY_VALIDATE_EXPR", XQueryValidateExprImpl.class, XQueryValidateExprPsiImpl.class);

    IElementType SINGLE_TYPE = new ICompositeElementType("XQUERY_SINGLE_TYPE", XQuerySingleTypeImpl.class, XQueryAtomicTypePsiImpl.class);
    IElementType UNARY_EXPR = new ICompositeElementType("XQUERY_UNARY_EXPR", XQueryUnaryExprImpl.class, XQueryUnaryExprPsiImpl.class);
    IElementType CAST_EXPR = new ICompositeElementType("XQUERY_CAST_EXPR", XQueryCastExprImpl.class, XQueryCastExprPsiImpl.class);
    IElementType CASTABLE_EXPR = new ICompositeElementType("XQUERY_CASTABLE_EXPR", XQueryCastableExprImpl.class, XQueryCastableExprPsiImpl.class);
    IElementType TREAT_EXPR = new ICompositeElementType("XQUERY_TREAT_EXPR", XQueryTreatExprImpl.class, XQueryTreatExprPsiImpl.class);
    IElementType INSTANCEOF_EXPR = new ICompositeElementType("XQUERY_INSTANCEOF_EXPR", XQueryInstanceofExprImpl.class, XQueryInstanceofExprPsiImpl.class);
    IElementType INTERSECT_EXCEPT_EXPR = new ICompositeElementType("XQUERY_INTERSECT_EXCEPT_EXPR", XQueryIntersectExceptExprImpl.class, XQueryIntersectExceptExprPsiImpl.class);
    IElementType UNION_EXPR = new ICompositeElementType("XQUERY_UNION_EXPR", XQueryUnionExprImpl.class, XQueryUnionExprPsiImpl.class);
    IElementType MULTIPLICATIVE_EXPR = new ICompositeElementType("XQUERY_MULTIPLICATIVE_EXPR", XQueryMultiplicativeExprImpl.class, XQueryMultiplicativeExprPsiImpl.class);
    IElementType ADDITIVE_EXPR = new ICompositeElementType("XQUERY_ADDITIVE_EXPR", XQueryAdditiveExprImpl.class, XQueryAdditiveExprPsiImpl.class);
    IElementType RANGE_EXPR = new ICompositeElementType("XQUERY_RANGE_EXPR", XQueryRangeExprImpl.class, XQueryRangeExprPsiImpl.class);
    IElementType COMPARISON_EXPR = new ICompositeElementType("XQUERY_COMPARISON_EXPR", XQueryComparisonExprImpl.class, XQueryComparisonExprPsiImpl.class);
    IElementType AND_EXPR = new ICompositeElementType("XQUERY_AND_EXPR", XQueryAndExprImpl.class, XQueryAndExprPsiImpl.class);
    IElementType OR_EXPR = new ICompositeElementType("XQUERY_OR_EXPR", XQueryOrExprImpl.class, XQueryOrExprPsiImpl.class);

    IElementType ORDER_MODIFIER = new ICompositeElementType("XQUERY_ORDER_MODIFIER", XQueryOrderModifierImpl.class, XQueryOrderModifierPsiImpl.class);
    IElementType ORDER_SPEC = new ICompositeElementType("XQUERY_ORDER_SPEC", XQueryOrderSpecImpl.class, XQueryOrderSpecPsiImpl.class);
    IElementType ORDER_SPEC_LIST = new ICompositeElementType("XQUERY_ORDER_SPEC_LIST", XQueryOrderSpecListImpl.class, XQueryOrderSpecListPsiImpl.class);
    IElementType POSITIONAL_VAR = new ICompositeElementType("XQUERY_POSITIONAL_VAR", XQueryPositionalVarImpl.class, XQueryForClausePsiImpl.class);
    IElementType ORDER_BY_CLAUSE = new ICompositeElementType("XQUERY_ORDER_BY_CLAUSE", XQueryOrderByClauseImpl.class, XQueryOrderByClausePsiImpl.class);
    IElementType WHERE_CLAUSE = new ICompositeElementType("XQUERY_WHERE_CLAUSE", XQueryWhereClauseImpl.class, XQueryWhereClausePsiImpl.class);
    IElementType LET_CLAUSE = new ICompositeElementType("XQUERY_LET_CLAUSE", XQueryLetClauseImpl.class, XQueryLetClausePsiImpl.class);
    IElementType FOR_CLAUSE = new ICompositeElementType("XQUERY_FOR_CLAUSE", XQueryForClauseImpl.class, XQueryForClausePsiImpl.class);
    IElementType FLWOR_EXPR = new ICompositeElementType("XQUERY_FLWOR_EXPR", XQueryFLWORExprImpl.class, XQueryFLWORExprPsiImpl.class);

    IElementType QUANTIFIED_EXPR = new ICompositeElementType("XQUERY_QUANTIFIED_EXPR", XQueryQuantifiedExprImpl.class, XQueryQuantifiedExprPsiImpl.class);
    IElementType TYPESWITCH_EXPR = new ICompositeElementType("XQUERY_TYPESWITCH_EXPR", XQueryTypeswitchExprImpl.class, XQueryTypeswitchExprPsiImpl.class);
    IElementType CASE_CLAUSE = new ICompositeElementType("XQUERY_CASE_CLAUSE", XQueryCaseClauseImpl.class, XQueryCaseClausePsiImpl.class);
    IElementType IF_EXPR = new ICompositeElementType("XQUERY_IF_EXPR", XQueryIfExprImpl.class, XQueryIfExprPsiImpl.class);

    IElementType QUERY_BODY = new ICompositeElementType("XQUERY_QUERY_BODY", XQueryQueryBodyImpl.class, XQueryQueryBodyPsiImpl.class);
    IElementType EXPR = new ICompositeElementType("XQUERY_EXPR", XQueryExprImpl.class, XQueryExprPsiImpl.class);
    IElementType ENCLOSED_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_EXPR", XQueryEnclosedExprImpl.class, XQueryEnclosedExprPsiImpl.class);

    IElementType IMPORT = new ICompositeElementType("XQUERY_IMPORT", XQueryImportImpl.class, XQueryImportPsiImpl.class);
    IElementType SCHEMA_PREFIX = new ICompositeElementType("XQUERY_SCHEMA_PREFIX", XQuerySchemaPrefixImpl.class, XQuerySchemaPrefixPsiImpl.class);
    IElementType NAMESPACE_DECL = new ICompositeElementType("XQUERY_NAMESPACE_DECL", XQueryNamespaceDeclImpl.class, XQueryNamespaceDeclPsiImpl.class);
    IElementType SCHEMA_IMPORT = new ICompositeElementType("XQUERY_SCHEMA_IMPORT", XQuerySchemaImportImpl.class, XQuerySchemaImportPsiImpl.class);
    IElementType MODULE_IMPORT = new ICompositeElementType("XQUERY_MODULE_IMPORT", XQueryModuleImportImpl.class, XQueryModuleImportPsiImpl.class);
    IElementType PROLOG = new ICompositeElementType("XQUERY_PROLOG", XQueryPrologImpl.class, XQueryPrologPsiImpl.class);

    IElementType TYPE_NAME = new ICompositeElementType("XQUERY_TYPE_NAME", XQueryTypeNameImpl.class, XQueryTypeNamePsiImpl.class);
    IElementType ELEMENT_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ELEMENT_NAME_OR_WILDCARD", XQueryElementNameOrWildcardImpl.class, XQueryElementNameOrWildcardPsiImpl.class);
    IElementType ELEMENT_NAME = new ICompositeElementType("XQUERY_ELEMENT_NAME", XQueryElementNameImpl.class, XQueryElementNamePsiImpl.class);
    IElementType ELEMENT_DECLARATION = new ICompositeElementType("XQUERY_ELEMENT_DECLARATION", XQueryElementDeclarationImpl.class, XQueryElementDeclarationPsiImpl.class);
    IElementType ATTRIB_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ATTRIB_NAME_OR_WILDCARD", XQueryAttribNameOrWildcardImpl.class, XQueryAttribNameOrWildcardPsiImpl.class);
    IElementType ATTRIBUTE_NAME = new ICompositeElementType("XQUERY_ATTRIBUTE_NAME", XQueryAttributeNameImpl.class, XQueryAttributeNamePsiImpl.class);
    IElementType ATTRIBUTE_DECLARATION = new ICompositeElementType("XQUERY_ATTRIBUTE_DECLARATION", XQueryAttributeDeclarationImpl.class, XQueryAttributeDeclarationPsiImpl.class);

    IElementType DOCUMENT_TEST = new ICompositeElementType("XQUERY_DOCUMENT_TEST", XQueryDocumentTestImpl.class, XQueryDocumentTestPsiImpl.class);
    IElementType ELEMENT_TEST = new ICompositeElementType("XQUERY_ELEMENT_TEST", XQueryElementTestImpl.class, XQueryElementTestPsiImpl.class);
    IElementType SCHEMA_ELEMENT_TEST = new ICompositeElementType("XQUERY_SCHEMA_ELEMENT_TEST", XQuerySchemaElementTestImpl.class, XQuerySchemaElementTestPsiImpl.class);
    IElementType ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_ATTRIBUTE_TEST", XQueryAttributeTestImpl.class, XQueryAttributeTestPsiImpl.class);
    IElementType SCHEMA_ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_SCHEMA_ATTRIBUTE_TEST", XQuerySchemaAttributeTestImpl.class, XQuerySchemaAttributeTestPsiImpl.class);
    IElementType PI_TEST = new ICompositeElementType("XQUERY_PI_TEST", XQueryPITestImpl.class, XQueryPITestPsiImpl.class);
    IElementType COMMENT_TEST = new ICompositeElementType("XQUERY_COMMENT_TEST", XQueryCommentTestImpl.class, XQueryCommentTestPsiImpl.class);
    IElementType TEXT_TEST = new ICompositeElementType("XQUERY_TEXT_TEST", XQueryTextTestImpl.class, XQueryTextTestPsiImpl.class);
    IElementType ANY_KIND_TEST = new ICompositeElementType("XQUERY_ANY_KIND_TEST", XQueryAnyKindTestImpl.class, XQueryAnyKindTestPsiImpl.class);
    IElementType ATOMIC_TYPE = new ICompositeElementType("XQUERY_ATOMIC_TYPE", XQueryAtomicTypeImpl.class, XQueryAtomicTypePsiImpl.class);
    IElementType ITEM_TYPE = new ICompositeElementType("XQUERY_ITEM_TYPE", XQueryItemTypeImpl.class, XQueryItemTypePsiImpl.class);
    IElementType OCCURRENCE_INDICATOR = new ICompositeElementType("XQUERY_OCCURRENCE_INDICATOR", XQueryOccurrenceIndicatorImpl.class, XQueryOccurrenceIndicatorPsiImpl.class);
    IElementType TYPE_DECLARATION = new ICompositeElementType("XQUERY_TYPE_DECLARATION", XQueryTypeDeclarationImpl.class, XQueryTypeDeclarationPsiImpl.class);
    IElementType SEQUENCE_TYPE = new ICompositeElementType("XQUERY_SEQUENCE_TYPE", XQuerySequenceTypeImpl.class, XQuerySequenceTypePsiImpl.class);

    IElementType PARAM = new ICompositeElementType("XQUERY_PARAM", XQueryParamImpl.class, XQueryParamPsiImpl.class);
    IElementType PARAM_LIST = new ICompositeElementType("XQUERY_PARAM_LIST", XQueryParamListImpl.class, XQueryParamListPsiImpl.class);

    IElementType FUNCTION_DECL = new ICompositeElementType("XQUERY_FUNCTION_DECL", XQueryFunctionDeclImpl.class, XQueryFunctionDeclPsiImpl.class);
    IElementType CONSTRUCTION_DECL = new ICompositeElementType("XQUERY_CONSTRUCTION_DECL", XQueryConstructionDeclImpl.class, XQueryConstructionDeclPsiImpl.class);
    IElementType VAR_DECL = new ICompositeElementType("XQUERY_VAR_DECL", XQueryVarDeclImpl.class, XQueryVarDeclPsiImpl.class);
    IElementType BASE_URI_DECL = new ICompositeElementType("XQUERY_BASE_URI_DECL", XQueryBaseURIDeclImpl.class, XQueryBaseURIDeclPsiImpl.class);
    IElementType DEFAULT_COLLATION_DECL = new ICompositeElementType("XQUERY_DEFAULT_COLLATION_DECL", XQueryDefaultCollationDeclImpl.class, XQueryDefaultCollationDeclPsiImpl.class);
    IElementType COPY_NAMESPACES_DECL = new ICompositeElementType("XQUERY_COPY_NAMESPACES_DECL", XQueryCopyNamespacesDeclImpl.class, XQueryCopyNamespacesDeclPsiImpl.class);
    IElementType EMPTY_ORDER_DECL = new ICompositeElementType("XQUERY_EMPTY_ORDER_DECL", XQueryEmptyOrderDeclImpl.class, XQueryEmptyOrderDeclPsiImpl.class);
    IElementType ORDERING_MODE_DECL = new ICompositeElementType("XQUERY_ORDERING_MODE_DECL", XQueryOrderingModeDeclImpl.class, XQueryOrderingModeDeclPsiImpl.class);
    IElementType OPTION_DECL = new ICompositeElementType("XQUERY_OPTION_DECL", XQueryOptionDeclImpl.class, XQueryOptionDeclPsiImpl.class);
    IElementType DEFAULT_NAMESPACE_DECL = new ICompositeElementType("XQUERY_DEFAULT_NAMESPACE_DECL", XQueryDefaultNamespaceDeclImpl.class, XQueryDefaultNamespaceDeclPsiImpl.class);
    IElementType BOUNDARY_SPACE_DECL = new ICompositeElementType("XQUERY_BOUNDARY_SPACE_DECL", XQueryBoundarySpaceDeclImpl.class, XQueryBoundarySpaceDeclPsiImpl.class);
    IElementType UNKNOWN_DECL = new ICompositeElementType("XQUERY_UNKNOWN_DECL", XQueryUnknownDeclImpl.class, XQueryUnknownDeclPsiImpl.class);
    IElementType MODULE_DECL = new ICompositeElementType("XQUERY_MODULE_DECL", XQueryModuleDeclImpl.class, XQueryModuleDeclPsiImpl.class);
    IElementType VERSION_DECL = new ICompositeElementType("XQUERY_VERSION_DECL", XQueryVersionDeclImpl.class, XQueryVersionDeclPsiImpl.class);
    IElementType MAIN_MODULE = new ICompositeElementType("XQUERY_MAIN_MODULE", XQueryMainModuleImpl.class, XQueryMainModulePsiImpl.class);
    IElementType LIBRARY_MODULE = new ICompositeElementType("XQUERY_LIBRARY_MODULE", XQueryLibraryModuleImpl.class, XQueryLibraryModulePsiImpl.class);
    IElementType MODULE = new ICompositeElementType("XQUERY_MODULE", XQueryModuleImpl.class, XQueryModulePsiImpl.class);

    IElementType CONSTRUCTOR = new ICompositeElementType("XQUERY_CONSTRUCTOR", XQueryConstructorImpl.class, XQueryConstructorPsiImpl.class);
    IElementType DIR_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_ELEM_CONSTRUCTOR", XQueryDirElemConstructorImpl.class, XQueryDirElemConstructorPsiImpl.class);
    IElementType DIR_ATTRIBUTE_LIST = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_LIST", XQueryDirAttributeListImpl.class, XQueryDirAttributeListPsiImpl.class);
    IElementType DIR_ATTRIBUTE_VALUE = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_VALUE", XQueryDirAttributeValueImpl.class, XQueryDirAttributeValuePsiImpl.class);
    IElementType DIR_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQueryDirCommentConstructorImpl.class, XQueryDirCommentConstructorPsiImpl.class);
    IElementType DIR_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_PI_CONSTRUCTOR", XQueryDirPIConstructorImpl.class, XQueryDirPIConstructorPsiImpl.class);
    IElementType DIR_ELEM_CONTENT = new ICompositeElementType("XQUERY_DIR_ELEM_CONTENT", XQueryDirElemContentImpl.class, XQueryDirElemContentPsiImpl.class);
    IElementType COMP_DOC_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_DOC_CONSTRUCTOR", XQueryCompDocConstructorImpl.class, XQueryCompDocConstructorPsiImpl.class);
    IElementType COMP_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ELEM_CONSTRUCTOR", XQueryCompElemConstructorImpl.class, XQueryCompElemConstructorPsiImpl.class);
    IElementType CONTENT_EXPR = new ICompositeElementType("XQUERY_CONTENT_EXPR", XQueryContentExprImpl.class, XQueryContentExprPsiImpl.class);
    IElementType COMP_ATTR_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ATTR_CONSTRUCTOR", XQueryCompAttrConstructorImpl.class, XQueryCompAttrConstructorPsiImpl.class);
    IElementType COMP_TEXT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_TEXT_CONSTRUCTOR", XQueryCompTextConstructorImpl.class, XQueryCompTextConstructorPsiImpl.class);
    IElementType COMP_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_COMMENT_CONSTRUCTOR", XQueryCompCommentConstructorImpl.class, XQueryCompCommentConstructorPsiImpl.class);
    IElementType COMP_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_PI_CONSTRUCTOR", XQueryCompPIConstructorImpl.class, XQueryCompPIConstructorPsiImpl.class);

    // endregion
    // region XQuery 3.0

    IElementType ANNOTATED_DECL = new ICompositeElementType("XQUERY_ANNOTATED_DECL", XQueryAnnotatedDeclImpl.class, XQueryAnnotatedDeclPsiImpl.class);
    IElementType ANNOTATION = new ICompositeElementType("XQUERY_ANNOTATION", XQueryAnnotationImpl.class, XQueryAnnotationPsiImpl.class);

    // endregion
    // region Update Facility 1.0

    IElementType REVALIDATION_DECL = new ICompositeElementType("XQUERY_REVALIDATION_DECL", UpdateFacilityRevalidationDeclImpl.class, UpdateFacilityRevalidationDeclPsiImpl.class);

    IElementType INSERT_EXPR = new ICompositeElementType("XQUERY_INSERT_EXPR", UpdateFacilityInsertExprImpl.class, UpdateFacilityInsertExprPsiImpl.class);
    IElementType DELETE_EXPR = new ICompositeElementType("XQUERY_DELETE_EXPR", UpdateFacilityDeleteExprImpl.class, UpdateFacilityDeleteExprPsiImpl.class);
    IElementType REPLACE_EXPR = new ICompositeElementType("XQUERY_REPLACE_EXPR", UpdateFacilityReplaceExprImpl.class, UpdateFacilityReplaceExprPsiImpl.class);
    IElementType RENAME_EXPR = new ICompositeElementType("XQUERY_RENAME_EXPR", UpdateFacilityRenameExprImpl.class, UpdateFacilityRenameExprPsiImpl.class);
    IElementType TRANSFORM_EXPR = new ICompositeElementType("XQUERY_TRANSFORM_EXPR", UpdateFacilityTransformExprImpl.class, UpdateFacilityTransformExprPsiImpl.class);

    IElementType NEW_NAME_EXPR = new ICompositeElementType("XQUERY_NEW_NAME_EXPR", UpdateFacilityNewNameExprImpl.class, UpdateFacilityNewNameExprPsiImpl.class);
    IElementType SOURCE_EXPR = new ICompositeElementType("XQUERY_SOURCE_EXPR", UpdateFacilitySourceExprImpl.class, UpdateFacilitySourceExprPsiImpl.class);
    IElementType TARGET_EXPR = new ICompositeElementType("XQUERY_TARGET_EXPR", UpdateFacilityTargetExprImpl.class, UpdateFacilityTargetExprPsiImpl.class);
    IElementType INSERT_EXPR_TARGET_CHOICE = new ICompositeElementType("XQUERY_INSERT_EXPR_TARGET_CHOICE", UpdateFacilityInsertExprTargetChoiceImpl.class, UpdateFacilityInsertExprTargetChoicePsiImpl.class);

    // endregion
    // region Update Facility 3.0

    IElementType COMPATIBILITY_ANNOTATION = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION", UpdateFacilityCompatibilityAnnotationImpl.class, UpdateFacilityCompatibilityAnnotationPsiImpl.class);

    // endregion
    // region MarkLogic 6.0

    IElementType COMPATIBILITY_ANNOTATION_MARKLOGIC = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION_MARKLOGIC", MarkLogicCompatibilityAnnotationImpl.class, MarkLogicCompatibilityAnnotationPsiImpl.class);
    IElementType BINARY_EXPR = new ICompositeElementType("XQUERY_BINARY_EXPR", MarkLogicBinaryExprImpl.class, MarkLogicBinaryExprPsiImpl.class);
    IElementType BINARY_KIND_TEST = new ICompositeElementType("XQUERY_BINARY_KIND_TEST", MarkLogicBinaryKindTestImpl.class, MarkLogicBinaryKindTestPsiImpl.class);

    // endregion
}
