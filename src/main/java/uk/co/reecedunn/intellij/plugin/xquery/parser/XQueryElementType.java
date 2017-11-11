/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.basex.BaseXUpdateExprPsiImpl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.full.text.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.marklogic.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.saxon.SaxonTupleFieldImpl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.saxon.SaxonTupleTypeImpl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.saxon.SaxonTypeDeclImpl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.saxon.SaxonUnionTypeImpl;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.*;

public interface XQueryElementType {
    IFileElementType FILE = new IFileElementType(XQuery.INSTANCE);

    // region XQuery 1.0

    IElementType CDATA_SECTION = new ICompositeElementType("XQUERY_CDATA_SECTION", XQueryCDataSectionPsiImpl.class, XQuery.INSTANCE);
    IElementType COMMENT = new ICompositeElementType("XQUERY_COMMENT", XQueryCommentPsiImpl.class, XQuery.INSTANCE);
    IElementType NCNAME = new ICompositeElementType("XQUERY_NCNAME", XQueryNCNamePsiImpl.class, XQuery.INSTANCE);
    IElementType QNAME = new ICompositeElementType("XQUERY_QNAME", XQueryQNamePsiImpl.class, XQuery.INSTANCE);
    IElementType URI_LITERAL = new ICompositeElementType("XQUERY_URI_LITERAL", XQueryUriLiteralPsiImpl.class, XQuery.INSTANCE);
    IElementType VAR_NAME = new ICompositeElementType("XQUERY_VAR_NAME", XQueryVarNamePsiImpl.class, XQuery.INSTANCE);

    IElementType LITERAL = new ICompositeElementType("XQUERY_LITERAL", XQueryLiteralPsiImpl.class, XQuery.INSTANCE);
    IElementType STRING_LITERAL = new ICompositeElementType("XQUERY_STRING_LITERAL", XQueryStringLiteralPsiImpl.class, XQuery.INSTANCE);
    IElementType VAR_REF = new ICompositeElementType("XQUERY_VAR_REF", XQueryVarRefPsiImpl.class, XQuery.INSTANCE);
    IElementType PARENTHESIZED_EXPR = new ICompositeElementType("XQUERY_PARENTHESIZED_EXPR", XQueryParenthesizedExprPsiImpl.class, XQuery.INSTANCE);
    IElementType CONTEXT_ITEM_EXPR = new ICompositeElementType("XQUERY_CONTEXT_ITEM_EXPR", XQueryContextItemExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ORDERED_EXPR = new ICompositeElementType("XQUERY_ORDERED_EXPR", XQueryOrderedExprPsiImpl.class, XQuery.INSTANCE);
    IElementType UNORDERED_EXPR = new ICompositeElementType("XQUERY_UNORDERED_EXPR", XQueryUnorderedExprPsiImpl.class, XQuery.INSTANCE);
    IElementType FUNCTION_CALL = new ICompositeElementType("XQUERY_FUNCTION_CALL", XQueryFunctionCallPsiImpl.class, XQuery.INSTANCE);

    IElementType AXIS_STEP = new ICompositeElementType("XQUERY_AXIS_STEP", XQueryAxisStepPsiImpl.class, XQuery.INSTANCE);
    IElementType FORWARD_STEP = new ICompositeElementType("XQUERY_FORWARD_STEP", XQueryForwardStepPsiImpl.class, XQuery.INSTANCE);
    IElementType FORWARD_AXIS = new ICompositeElementType("XQUERY_FORWARD_AXIS", XQueryForwardAxisPsiImpl.class, XQuery.INSTANCE);
    IElementType ABBREV_FORWARD_STEP = new ICompositeElementType("XQUERY_ABBREV_FORWARD_STEP", XQueryAbbrevForwardStepPsiImpl.class, XQuery.INSTANCE);
    IElementType REVERSE_STEP = new ICompositeElementType("XQUERY_REVERSE_STEP", XQueryReverseStepPsiImpl.class, XQuery.INSTANCE);
    IElementType REVERSE_AXIS = new ICompositeElementType("XQUERY_REVERSE_AXIS", XQueryReverseAxisPsiImpl.class, XQuery.INSTANCE);
    IElementType ABBREV_REVERSE_STEP = new ICompositeElementType("XQUERY_ABBREV_REVERSE_STEP", XQueryAbbrevReverseStepPsiImpl.class, XQuery.INSTANCE);
    IElementType NODE_TEST = new ICompositeElementType("XQUERY_NODE_TEST", XQueryNodeTestPsiImpl.class, XQuery.INSTANCE);
    IElementType NAME_TEST = new ICompositeElementType("XQUERY_NAME_TEST", XQueryNameTestPsiImpl.class, XQuery.INSTANCE);
    IElementType WILDCARD = new ICompositeElementType("XQUERY_WILDCARD", XQueryWildcardPsiImpl.class, XQuery.INSTANCE);
    IElementType PREDICATE_LIST = new ICompositeElementType("XQUERY_PREDICATE_LIST", XQueryPredicateListPsiImpl.class, XQuery.INSTANCE);
    IElementType PREDICATE = new ICompositeElementType("XQUERY_PREDICATE", XQueryPredicatePsiImpl.class, XQuery.INSTANCE);

    IElementType RELATIVE_PATH_EXPR = new ICompositeElementType("XQUERY_RELATIVE_PATH_EXPR", XQueryRelativePathExprPsiImpl.class, XQuery.INSTANCE);
    IElementType PATH_EXPR = new ICompositeElementType("XQUERY_PATH_EXPR", XQueryPathExprPsiImpl.class, XQuery.INSTANCE);
    IElementType PRAGMA = new ICompositeElementType("XQUERY_PRAGMA", XQueryPragmaPsiImpl.class, XQuery.INSTANCE);
    IElementType EXTENSION_EXPR = new ICompositeElementType("XQUERY_EXTENSION_EXPR", XQueryExtensionExprPsiImpl.class, XQuery.INSTANCE);
    IElementType VALIDATE_EXPR = new ICompositeElementType("XQUERY_VALIDATE_EXPR", XQueryValidateExprPsiImpl.class, XQuery.INSTANCE);

    IElementType SINGLE_TYPE = new ICompositeElementType("XQUERY_SINGLE_TYPE", XQuerySingleTypePsiImpl.class, XQuery.INSTANCE);
    IElementType UNARY_EXPR = new ICompositeElementType("XQUERY_UNARY_EXPR", XQueryUnaryExprPsiImpl.class, XQuery.INSTANCE);
    IElementType CAST_EXPR = new ICompositeElementType("XQUERY_CAST_EXPR", XQueryCastExprPsiImpl.class, XQuery.INSTANCE);
    IElementType CASTABLE_EXPR = new ICompositeElementType("XQUERY_CASTABLE_EXPR", XQueryCastableExprPsiImpl.class, XQuery.INSTANCE);
    IElementType TREAT_EXPR = new ICompositeElementType("XQUERY_TREAT_EXPR", XQueryTreatExprPsiImpl.class, XQuery.INSTANCE);
    IElementType INSTANCEOF_EXPR = new ICompositeElementType("XQUERY_INSTANCEOF_EXPR", XQueryInstanceofExprPsiImpl.class, XQuery.INSTANCE);
    IElementType INTERSECT_EXCEPT_EXPR = new ICompositeElementType("XQUERY_INTERSECT_EXCEPT_EXPR", XQueryIntersectExceptExprPsiImpl.class, XQuery.INSTANCE);
    IElementType UNION_EXPR = new ICompositeElementType("XQUERY_UNION_EXPR", XQueryUnionExprPsiImpl.class, XQuery.INSTANCE);
    IElementType MULTIPLICATIVE_EXPR = new ICompositeElementType("XQUERY_MULTIPLICATIVE_EXPR", XQueryMultiplicativeExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ADDITIVE_EXPR = new ICompositeElementType("XQUERY_ADDITIVE_EXPR", XQueryAdditiveExprPsiImpl.class, XQuery.INSTANCE);
    IElementType RANGE_EXPR = new ICompositeElementType("XQUERY_RANGE_EXPR", XQueryRangeExprPsiImpl.class, XQuery.INSTANCE);
    IElementType COMPARISON_EXPR = new ICompositeElementType("XQUERY_COMPARISON_EXPR", XQueryComparisonExprPsiImpl.class, XQuery.INSTANCE);
    IElementType AND_EXPR = new ICompositeElementType("XQUERY_AND_EXPR", XQueryAndExprPsiImpl.class, XQuery.INSTANCE);
    IElementType OR_EXPR = new ICompositeElementType("XQUERY_OR_EXPR", XQueryOrExprPsiImpl.class, XQuery.INSTANCE);

    IElementType ORDER_MODIFIER = new ICompositeElementType("XQUERY_ORDER_MODIFIER", XQueryOrderModifierPsiImpl.class, XQuery.INSTANCE);
    IElementType ORDER_SPEC = new ICompositeElementType("XQUERY_ORDER_SPEC", XQueryOrderSpecPsiImpl.class, XQuery.INSTANCE);
    IElementType ORDER_SPEC_LIST = new ICompositeElementType("XQUERY_ORDER_SPEC_LIST", XQueryOrderSpecListPsiImpl.class, XQuery.INSTANCE);
    IElementType POSITIONAL_VAR = new ICompositeElementType("XQUERY_POSITIONAL_VAR", XQueryPositionalVarPsiImpl.class, XQuery.INSTANCE);
    IElementType ORDER_BY_CLAUSE = new ICompositeElementType("XQUERY_ORDER_BY_CLAUSE", XQueryOrderByClausePsiImpl.class, XQuery.INSTANCE);
    IElementType WHERE_CLAUSE = new ICompositeElementType("XQUERY_WHERE_CLAUSE", XQueryWhereClausePsiImpl.class, XQuery.INSTANCE);
    IElementType LET_CLAUSE = new ICompositeElementType("XQUERY_LET_CLAUSE", XQueryLetClausePsiImpl.class, XQuery.INSTANCE);
    IElementType FOR_CLAUSE = new ICompositeElementType("XQUERY_FOR_CLAUSE", XQueryForClausePsiImpl.class, XQuery.INSTANCE);
    IElementType FLWOR_EXPR = new ICompositeElementType("XQUERY_FLWOR_EXPR", XQueryFLWORExprPsiImpl.class, XQuery.INSTANCE);

    IElementType QUANTIFIED_EXPR = new ICompositeElementType("XQUERY_QUANTIFIED_EXPR", XQueryQuantifiedExprPsiImpl.class, XQuery.INSTANCE);
    IElementType TYPESWITCH_EXPR = new ICompositeElementType("XQUERY_TYPESWITCH_EXPR", XQueryTypeswitchExprPsiImpl.class, XQuery.INSTANCE);
    IElementType CASE_CLAUSE = new ICompositeElementType("XQUERY_CASE_CLAUSE", XQueryCaseClausePsiImpl.class, XQuery.INSTANCE);
    IElementType IF_EXPR = new ICompositeElementType("XQUERY_IF_EXPR", XQueryIfExprPsiImpl.class, XQuery.INSTANCE);

    IElementType QUERY_BODY = new ICompositeElementType("XQUERY_QUERY_BODY", XQueryQueryBodyPsiImpl.class, XQuery.INSTANCE);
    IElementType EXPR = new ICompositeElementType("XQUERY_EXPR", XQueryExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ENCLOSED_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_EXPR", XQueryEnclosedExprPsiImpl.class, XQuery.INSTANCE);

    IElementType IMPORT = new ICompositeElementType("XQUERY_IMPORT", XQueryImportPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_PREFIX = new ICompositeElementType("XQUERY_SCHEMA_PREFIX", XQuerySchemaPrefixPsiImpl.class, XQuery.INSTANCE);
    IElementType NAMESPACE_DECL = new ICompositeElementType("XQUERY_NAMESPACE_DECL", XQueryNamespaceDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_IMPORT = new ICompositeElementType("XQUERY_SCHEMA_IMPORT", XQuerySchemaImportPsiImpl.class, XQuery.INSTANCE);
    IElementType MODULE_IMPORT = new ICompositeElementType("XQUERY_MODULE_IMPORT", XQueryModuleImportPsiImpl.class, XQuery.INSTANCE);
    IElementType PROLOG = new ICompositeElementType("XQUERY_PROLOG", XQueryPrologPsiImpl.class, XQuery.INSTANCE);

    IElementType TYPE_NAME = new ICompositeElementType("XQUERY_TYPE_NAME", XQueryTypeNamePsiImpl.class, XQuery.INSTANCE);
    IElementType ELEMENT_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ELEMENT_NAME_OR_WILDCARD", XQueryElementNameOrWildcardPsiImpl.class, XQuery.INSTANCE);
    IElementType ELEMENT_NAME = new ICompositeElementType("XQUERY_ELEMENT_NAME", XQueryElementNamePsiImpl.class, XQuery.INSTANCE);
    IElementType ELEMENT_DECLARATION = new ICompositeElementType("XQUERY_ELEMENT_DECLARATION", XQueryElementDeclarationPsiImpl.class, XQuery.INSTANCE);
    IElementType ATTRIB_NAME_OR_WILDCARD = new ICompositeElementType("XQUERY_ATTRIB_NAME_OR_WILDCARD", XQueryAttribNameOrWildcardPsiImpl.class, XQuery.INSTANCE);
    IElementType ATTRIBUTE_NAME = new ICompositeElementType("XQUERY_ATTRIBUTE_NAME", XQueryAttributeNamePsiImpl.class, XQuery.INSTANCE);
    IElementType ATTRIBUTE_DECLARATION = new ICompositeElementType("XQUERY_ATTRIBUTE_DECLARATION", XQueryAttributeDeclarationPsiImpl.class, XQuery.INSTANCE);

    IElementType DOCUMENT_TEST = new ICompositeElementType("XQUERY_DOCUMENT_TEST", XQueryDocumentTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ELEMENT_TEST = new ICompositeElementType("XQUERY_ELEMENT_TEST", XQueryElementTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_ELEMENT_TEST = new ICompositeElementType("XQUERY_SCHEMA_ELEMENT_TEST", XQuerySchemaElementTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_ATTRIBUTE_TEST", XQueryAttributeTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_ATTRIBUTE_TEST = new ICompositeElementType("XQUERY_SCHEMA_ATTRIBUTE_TEST", XQuerySchemaAttributeTestPsiImpl.class, XQuery.INSTANCE);
    IElementType PI_TEST = new ICompositeElementType("XQUERY_PI_TEST", XQueryPITestPsiImpl.class, XQuery.INSTANCE);
    IElementType COMMENT_TEST = new ICompositeElementType("XQUERY_COMMENT_TEST", XQueryCommentTestPsiImpl.class, XQuery.INSTANCE);
    IElementType TEXT_TEST = new ICompositeElementType("XQUERY_TEXT_TEST", XQueryTextTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ANY_KIND_TEST = new ICompositeElementType("XQUERY_ANY_KIND_TEST", XQueryAnyKindTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ITEM_TYPE = new ICompositeElementType("XQUERY_ITEM_TYPE", XQueryItemTypePsiImpl.class, XQuery.INSTANCE);
    IElementType OCCURRENCE_INDICATOR = new ICompositeElementType("XQUERY_OCCURRENCE_INDICATOR", XQueryOccurrenceIndicatorPsiImpl.class, XQuery.INSTANCE);
    IElementType TYPE_DECLARATION = new ICompositeElementType("XQUERY_TYPE_DECLARATION", XQueryTypeDeclarationPsiImpl.class, XQuery.INSTANCE);
    IElementType SEQUENCE_TYPE = new ICompositeElementType("XQUERY_SEQUENCE_TYPE", XQuerySequenceTypePsiImpl.class, XQuery.INSTANCE);

    IElementType PARAM = new ICompositeElementType("XQUERY_PARAM", XQueryParamPsiImpl.class, XQuery.INSTANCE);
    IElementType PARAM_LIST = new ICompositeElementType("XQUERY_PARAM_LIST", XQueryParamListPsiImpl.class, XQuery.INSTANCE);

    IElementType FUNCTION_DECL = new ICompositeElementType("XQUERY_FUNCTION_DECL", XQueryFunctionDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType CONSTRUCTION_DECL = new ICompositeElementType("XQUERY_CONSTRUCTION_DECL", XQueryConstructionDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType VAR_DECL = new ICompositeElementType("XQUERY_VAR_DECL", XQueryVarDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType BASE_URI_DECL = new ICompositeElementType("XQUERY_BASE_URI_DECL", XQueryBaseURIDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType DEFAULT_COLLATION_DECL = new ICompositeElementType("XQUERY_DEFAULT_COLLATION_DECL", XQueryDefaultCollationDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType COPY_NAMESPACES_DECL = new ICompositeElementType("XQUERY_COPY_NAMESPACES_DECL", XQueryCopyNamespacesDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType EMPTY_ORDER_DECL = new ICompositeElementType("XQUERY_EMPTY_ORDER_DECL", XQueryEmptyOrderDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType ORDERING_MODE_DECL = new ICompositeElementType("XQUERY_ORDERING_MODE_DECL", XQueryOrderingModeDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType OPTION_DECL = new ICompositeElementType("XQUERY_OPTION_DECL", XQueryOptionDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType DEFAULT_NAMESPACE_DECL = new ICompositeElementType("XQUERY_DEFAULT_NAMESPACE_DECL", XQueryDefaultNamespaceDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType BOUNDARY_SPACE_DECL = new ICompositeElementType("XQUERY_BOUNDARY_SPACE_DECL", XQueryBoundarySpaceDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType UNKNOWN_DECL = new ICompositeElementType("XQUERY_UNKNOWN_DECL", XQueryUnknownDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType MODULE_DECL = new ICompositeElementType("XQUERY_MODULE_DECL", XQueryModuleDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType VERSION_DECL = new ICompositeElementType("XQUERY_VERSION_DECL", XQueryVersionDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType MAIN_MODULE = new ICompositeElementType("XQUERY_MAIN_MODULE", XQueryMainModulePsiImpl.class, XQuery.INSTANCE);
    IElementType LIBRARY_MODULE = new ICompositeElementType("XQUERY_LIBRARY_MODULE", XQueryLibraryModulePsiImpl.class, XQuery.INSTANCE);
    IElementType MODULE = new ICompositeElementType("XQUERY_MODULE", XQueryModulePsiImpl.class, XQuery.INSTANCE);

    IElementType DIR_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_ELEM_CONSTRUCTOR", XQueryDirElemConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType DIR_ATTRIBUTE_LIST = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_LIST", XQueryDirAttributeListPsiImpl.class, XQuery.INSTANCE);
    IElementType DIR_ATTRIBUTE_VALUE = new ICompositeElementType("XQUERY_DIR_ATTRIBUTE_VALUE", XQueryDirAttributeValuePsiImpl.class, XQuery.INSTANCE);
    IElementType DIR_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQueryDirCommentConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType DIR_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_DIR_PI_CONSTRUCTOR", XQueryDirPIConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType DIR_ELEM_CONTENT = new ICompositeElementType("XQUERY_DIR_ELEM_CONTENT", XQueryDirElemContentPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_DOC_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_DOC_CONSTRUCTOR", XQueryCompDocConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_ELEM_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ELEM_CONSTRUCTOR", XQueryCompElemConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_ATTR_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_ATTR_CONSTRUCTOR", XQueryCompAttrConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_TEXT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_TEXT_CONSTRUCTOR", XQueryCompTextConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_COMMENT_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_COMMENT_CONSTRUCTOR", XQueryCompCommentConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType COMP_PI_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_PI_CONSTRUCTOR", XQueryCompPIConstructorPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region XQuery 3.0

    IElementType EQNAME = new ICompositeElementType("XQUERY_EQNAME", XQueryEQNamePsiImpl.class, XQuery.INSTANCE);
    IElementType BRACED_URI_LITERAL = new ICompositeElementType("XQUERY_BRACED_URI_LITERAL", XQueryBracedURILiteralPsiImpl.class, XQuery.INSTANCE);
    IElementType URI_QUALIFIED_NAME = new ICompositeElementType("XQUERY_URI_QUALIFIED_NAME", XQueryURIQualifiedNamePsiImpl.class, XQuery.INSTANCE);

    IElementType DECIMAL_FORMAT_DECL = new ICompositeElementType("XQUERY_DECIMAL_FORMAT_DECL", XQueryDecimalFormatDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType DF_PROPERTY_NAME = new ICompositeElementType("XQUERY_DF_PROPERTY_NAME", XQueryDFPropertyNamePsiImpl.class, XQuery.INSTANCE);
    IElementType CONTEXT_ITEM_DECL = new ICompositeElementType("XQUERY_CONTEXT_ITEM_DECL", XQueryContextItemDeclPsiImpl.class, XQuery.INSTANCE);

    IElementType FUNCTION_BODY = new ICompositeElementType("XQUERY_FUNCTION_BODY", XQueryFunctionBodyPsiImpl.class, XQuery.INSTANCE);
    IElementType VAR_VALUE = new ICompositeElementType("XQUERY_VAR_VALUE", XQueryVarValuePsiImpl.class, XQuery.INSTANCE);
    IElementType VAR_DEFAULT_VALUE = new ICompositeElementType("XQUERY_VAR_DEFAULT_VALUE", XQueryVarDefaultValuePsiImpl.class, XQuery.INSTANCE);

    IElementType ATOMIC_OR_UNION_TYPE = new ICompositeElementType("XQUERY_ATOMIC_OR_UNION_TYPE", XQueryAtomicOrUnionTypePsiImpl.class, XQuery.INSTANCE);
    IElementType SIMPLE_TYPE_NAME = new ICompositeElementType("XQUERY_SIMPLE_TYPE_NAME", XQuerySimpleTypeNamePsiImpl.class, XQuery.INSTANCE);
    IElementType SEQUENCE_TYPE_UNION = new ICompositeElementType("XQUERY_SEQUENCE_TYPE_UNION", XQuerySequenceTypeUnionPsiImpl.class, XQuery.INSTANCE);

    IElementType FUNCTION_TEST = new ICompositeElementType("XQUERY_FUNCTION_TEST", XQueryFunctionTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ANY_FUNCTION_TEST = new ICompositeElementType("XQUERY_ANY_FUNCTION_TEST", XQueryAnyFunctionTestPsiImpl.class, XQuery.INSTANCE);
    IElementType TYPED_FUNCTION_TEST = new ICompositeElementType("XQUERY_TYPED_FUNCTION_TEST", XQueryTypedFunctionTestPsiImpl.class, XQuery.INSTANCE);
    IElementType PARENTHESIZED_ITEM_TYPE = new ICompositeElementType("XQUERY_PARENTHESIZED_ITEM_TYPE", XQueryParenthesizedItemTypePsiImpl.class, XQuery.INSTANCE);

    IElementType NAMESPACE_NODE_TEST = new ICompositeElementType("XQUERY_NAMESPACE_NODE_TEST", XQueryNamespaceNodeTestPsiImpl.class, XQuery.INSTANCE);

    IElementType ANNOTATED_DECL = new ICompositeElementType("XQUERY_ANNOTATED_DECL", XQueryAnnotatedDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType ANNOTATION = new ICompositeElementType("XQUERY_ANNOTATION", XQueryAnnotationPsiImpl.class, XQuery.INSTANCE);

    IElementType SWITCH_EXPR = new ICompositeElementType("XQUERY_SWITCH_EXPR", XQuerySwitchExprPsiImpl.class, XQuery.INSTANCE);
    IElementType SWITCH_CASE_CLAUSE = new ICompositeElementType("XQUERY_SWITCH_CASE_CLAUSE", XQuerySwitchCaseClausePsiImpl.class, XQuery.INSTANCE);
    IElementType SWITCH_CASE_OPERAND = new ICompositeElementType("XQUERY_SWITCH_CASE_OPERAND", XQuerySwitchCaseClausePsiImpl.class, XQuery.INSTANCE);

    IElementType TRY_CATCH_EXPR = new ICompositeElementType("XQUERY_TRY_CATCH_EXPR", XQueryTryCatchExprPsiImpl.class, XQuery.INSTANCE);
    IElementType TRY_CLAUSE = new ICompositeElementType("XQUERY_TRY_CLAUSE", XQueryTryClausePsiImpl.class, XQuery.INSTANCE);
    IElementType CATCH_CLAUSE = new ICompositeElementType("XQUERY_CATCH_CLAUSE", XQueryCatchClausePsiImpl.class, XQuery.INSTANCE);
    IElementType CATCH_ERROR_LIST = new ICompositeElementType("XQUERY_CATCH_ERROR_LIST", XQueryCatchErrorListPsiImpl.class, XQuery.INSTANCE);

    IElementType FOR_BINDING = new ICompositeElementType("XQUERY_FOR_BINDING", XQueryForBindingPsiImpl.class, XQuery.INSTANCE);
    IElementType LET_BINDING = new ICompositeElementType("XQUERY_LET_BINDING", XQueryLetBindingPsiImpl.class, XQuery.INSTANCE);

    IElementType ALLOWING_EMPTY = new ICompositeElementType("XQUERY_ALLOWING_EMPTY", XQueryAllowingEmptyPsiImpl.class, XQuery.INSTANCE);
    IElementType INTERMEDIATE_CLAUSE = new ICompositeElementType("XQUERY_INTERMEDIATE_CLAUSE", XQueryIntermediateClausePsiImpl.class, XQuery.INSTANCE);
    IElementType COUNT_CLAUSE = new ICompositeElementType("XQUERY_COUNT_CLAUSE", XQueryCountClausePsiImpl.class, XQuery.INSTANCE);
    IElementType RETURN_CLAUSE = new ICompositeElementType("XQUERY_RETURN_CLAUSE", XQueryReturnClausePsiImpl.class, XQuery.INSTANCE);

    IElementType WINDOW_CLAUSE = new ICompositeElementType("XQUERY_WINDOW_CLAUSE", XQueryWindowClausePsiImpl.class, XQuery.INSTANCE);
    IElementType TUMBLING_WINDOW_CLAUSE = new ICompositeElementType("XQUERY_TUMBLING_WINDOW_CLAUSE", XQueryTumblingWindowClausePsiImpl.class, XQuery.INSTANCE);
    IElementType SLIDING_WINDOW_CLAUSE = new ICompositeElementType("XQUERY_SLIDING_WINDOW_CLAUSE", XQuerySlidingWindowClausePsiImpl.class, XQuery.INSTANCE);
    IElementType WINDOW_START_CONDITION = new ICompositeElementType("XQUERY_WINDOW_START_CONDITION", XQueryWindowStartConditionPsiImpl.class, XQuery.INSTANCE);
    IElementType WINDOW_END_CONDITION = new ICompositeElementType("XQUERY_WINDOW_END_CONDITION", XQueryWindowEndConditionPsiImpl.class, XQuery.INSTANCE);
    IElementType WINDOW_VARS = new ICompositeElementType("XQUERY_WINDOW_VARS", XQueryWindowVarsPsiImpl.class, XQuery.INSTANCE);
    IElementType CURRENT_ITEM = new ICompositeElementType("XQUERY_CURRENT_ITEM", XQueryCurrentItemPsiImpl.class, XQuery.INSTANCE);
    IElementType PREVIOUS_ITEM = new ICompositeElementType("XQUERY_PREVIOUS_ITEM", XQueryPreviousItemPsiImpl.class, XQuery.INSTANCE);
    IElementType NEXT_ITEM = new ICompositeElementType("XQUERY_NEXT_ITEM", XQueryNextItemPsiImpl.class, XQuery.INSTANCE);

    IElementType GROUP_BY_CLAUSE = new ICompositeElementType("XQUERY_GROUP_BY_CLAUSE", XQueryGroupByClausePsiImpl.class, XQuery.INSTANCE);
    IElementType GROUPING_SPEC_LIST = new ICompositeElementType("XQUERY_GROUPING_SPEC_LIST", XQueryGroupingSpecListPsiImpl.class, XQuery.INSTANCE);
    IElementType GROUPING_SPEC = new ICompositeElementType("XQUERY_GROUPING_SPEC", XQueryGroupingSpecPsiImpl.class, XQuery.INSTANCE);
    IElementType GROUPING_VARIABLE = new ICompositeElementType("XQUERY_GROUPING_VARIABLE", XQueryGroupingVariablePsiImpl.class, XQuery.INSTANCE);

    IElementType STRING_CONCAT_EXPR = new ICompositeElementType("XQUERY_STRING_CONCAT_EXPR", XQueryStringConcatExprPsiImpl.class, XQuery.INSTANCE);
    IElementType SIMPLE_MAP_EXPR = new ICompositeElementType("XQUERY_SIMPLE_MAP_EXPR", XQuerySimpleMapExprPsiImpl.class, XQuery.INSTANCE);

    IElementType INLINE_FUNCTION_EXPR = new ICompositeElementType("XQUERY_INLINE_FUNCTION_EXPR", XQueryInlineFunctionExprPsiImpl.class, XQuery.INSTANCE);
    IElementType NAMED_FUNCTION_REF = new ICompositeElementType("XQUERY_NAMED_FUNCTION_REF", XQueryNamedFunctionRefPsiImpl.class, XQuery.INSTANCE);

    IElementType ARGUMENT_LIST = new ICompositeElementType("XQUERY_ARGUMENT_LIST", XQueryArgumentListPsiImpl.class, XQuery.INSTANCE);
    IElementType ARGUMENT = new ICompositeElementType("XQUERY_ARGUMENT", XQueryArgumentPsiImpl.class, XQuery.INSTANCE);
    IElementType ARGUMENT_PLACEHOLDER = new ICompositeElementType("XQUERY_ARGUMENT_PLACEHOLDER", XQueryArgumentPlaceholderPsiImpl.class, XQuery.INSTANCE);

    IElementType COMP_NAMESPACE_CONSTRUCTOR = new ICompositeElementType("XQUERY_COMP_NAMESPACE_CONSTRUCTOR", XQueryCompNamespaceConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType PREFIX = new ICompositeElementType("XQUERY_PREFIX", XQueryPrefixPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region XQuery 3.1

    IElementType POSTFIX_EXPR = new ICompositeElementType("XQUERY_POSTFIX_EXPR", XQueryPostfixExprPsiImpl.class, XQuery.INSTANCE);

    IElementType ARROW_EXPR = new ICompositeElementType("XQUERY_ARROW_EXPR", XQueryArrowExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ARROW_FUNCTION_SPECIFIER = new ICompositeElementType("XQUERY_ARROW_FUNCTION_SPECIFIER", XQueryArrowFunctionSpecifierPsiImpl.class, XQuery.INSTANCE);

    IElementType ENCLOSED_CONTENT_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_CONTENT_EXPR", XQueryEnclosedContentExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ENCLOSED_TRY_TARGET_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_TRY_TARGET_EXPR", XQueryEnclosedTryTargetExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ENCLOSED_URI_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_URI_EXPR", XQueryEnclosedUriExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ENCLOSED_PREFIX_EXPR = new ICompositeElementType("XQUERY_ENCLOSED_PREFIX_EXPR", XQueryEnclosedPrefixExprPsiImpl.class, XQuery.INSTANCE);

    IElementType NODE_CONSTRUCTOR = new ICompositeElementType("XQUERY_NODE_CONSTRUCTOR", XQueryNodeConstructorPsiImpl.class, XQuery.INSTANCE);

    IElementType SQUARE_ARRAY_CONSTRUCTOR = new ICompositeElementType("XQUERY_SQUARE_ARRAY_CONSTRUCTOR", XQuerySquareArrayConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType CURLY_ARRAY_CONSTRUCTOR = new ICompositeElementType("XQUERY_CURLY_ARRAY_CONSTRUCTOR", XQueryCurlyArrayConstructorPsiImpl.class, XQuery.INSTANCE);

    IElementType MAP_CONSTRUCTOR = new ICompositeElementType("XQUERY_MAP_CONSTRUCTOR", XQueryMapConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType MAP_CONSTRUCTOR_ENTRY = new ICompositeElementType("XQUERY_MAP_CONSTRUCTOR_ENTRY", XQueryMapConstructorEntryPsiImpl.class, XQuery.INSTANCE);
    IElementType MAP_KEY_EXPR = new ICompositeElementType("XQUERY_MAP_KEY_EXPR", XQueryMapKeyExprPsiImpl.class, XQuery.INSTANCE);
    IElementType MAP_VALUE_EXPR = new ICompositeElementType("XQUERY_MAP_VALUE_EXPR", XQueryMapValueExprPsiImpl.class, XQuery.INSTANCE);

    IElementType STRING_CONSTRUCTOR = new ICompositeElementType("XQUERY_STRING_CONSTRUCTOR", XQueryStringConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType STRING_CONSTRUCTOR_CONTENT = new ICompositeElementType("XQUERY_STRING_CONSTRUCTOR_CONTENT", XQueryStringConstructorContentPsiImpl.class, XQuery.INSTANCE);
    IElementType STRING_CONSTRUCTOR_INTERPOLATION = new ICompositeElementType("XQUERY_STRING_CONSTRUCTOR_INTERPOLATION", XQueryStringConstructorInterpolationPsiImpl.class, XQuery.INSTANCE);

    IElementType ANY_MAP_TEST = new ICompositeElementType("XQUERY_ANY_MAP_TEST", XQueryAnyMapTestPsiImpl.class, XQuery.INSTANCE);
    IElementType TYPED_MAP_TEST = new ICompositeElementType("XQUERY_TYPED_MAP_TEST", XQueryTypedMapTestPsiImpl.class, XQuery.INSTANCE);

    IElementType ANY_ARRAY_TEST = new ICompositeElementType("XQUERY_ANY_ARRAY_TEST", XQueryAnyArrayTestPsiImpl.class, XQuery.INSTANCE);
    IElementType TYPED_ARRAY_TEST = new ICompositeElementType("XQUERY_TYPED_ARRAY_TEST", XQueryTypedArrayTestPsiImpl.class, XQuery.INSTANCE);

    IElementType UNARY_LOOKUP = new ICompositeElementType("XQUERY_UNARY_LOOKUP", XQueryUnaryLookupPsiImpl.class, XQuery.INSTANCE);
    IElementType LOOKUP = new ICompositeElementType("XQUERY_LOOKUP", XQueryLookupPsiImpl.class, XQuery.INSTANCE);
    IElementType KEY_SPECIFIER = new ICompositeElementType("XQUERY_KEY_SPECIFIER", XQueryKeySpecifierPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region Update Facility 1.0

    IElementType REVALIDATION_DECL = new ICompositeElementType("XQUERY_REVALIDATION_DECL", UpdateFacilityRevalidationDeclPsiImpl.class, XQuery.INSTANCE);

    IElementType INSERT_EXPR = new ICompositeElementType("XQUERY_INSERT_EXPR", UpdateFacilityInsertExprPsiImpl.class, XQuery.INSTANCE);
    IElementType DELETE_EXPR = new ICompositeElementType("XQUERY_DELETE_EXPR", UpdateFacilityDeleteExprPsiImpl.class, XQuery.INSTANCE);
    IElementType REPLACE_EXPR = new ICompositeElementType("XQUERY_REPLACE_EXPR", UpdateFacilityReplaceExprPsiImpl.class, XQuery.INSTANCE);
    IElementType RENAME_EXPR = new ICompositeElementType("XQUERY_RENAME_EXPR", UpdateFacilityRenameExprPsiImpl.class, XQuery.INSTANCE);

    IElementType NEW_NAME_EXPR = new ICompositeElementType("XQUERY_NEW_NAME_EXPR", UpdateFacilityNewNameExprPsiImpl.class, XQuery.INSTANCE);
    IElementType SOURCE_EXPR = new ICompositeElementType("XQUERY_SOURCE_EXPR", UpdateFacilitySourceExprPsiImpl.class, XQuery.INSTANCE);
    IElementType TARGET_EXPR = new ICompositeElementType("XQUERY_TARGET_EXPR", UpdateFacilityTargetExprPsiImpl.class, XQuery.INSTANCE);
    IElementType INSERT_EXPR_TARGET_CHOICE = new ICompositeElementType("XQUERY_INSERT_EXPR_TARGET_CHOICE", UpdateFacilityInsertExprTargetChoicePsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region Update Facility 3.0

    IElementType COMPATIBILITY_ANNOTATION = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION", UpdateFacilityCompatibilityAnnotationPsiImpl.class, XQuery.INSTANCE);

    IElementType COPY_MODIFY_EXPR = new ICompositeElementType("XQUERY_COPY_MODIFY_EXPR", UpdateFacilityCopyModifyExprPsiImpl.class, XQuery.INSTANCE);
    IElementType UPDATING_FUNCTION_CALL = new ICompositeElementType("XQUERY_UPDATING_FUNCTION_CALL", UpdateFacilityUpdatingFunctionCallPsiImpl.class, XQuery.INSTANCE);

    IElementType TRANSFORM_WITH_EXPR = new ICompositeElementType("XQUERY_TRANSFORM_WITH_EXPR", UpdateFacilityTransformWithExprPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region Full Text 1.0

    IElementType FT_OPTION_DECL = new ICompositeElementType("XQUERY_FT_OPTION_DECL", FTOptionDeclPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_MATCH_OPTIONS = new ICompositeElementType("XQUERY_FT_MATCH_OPTIONS", FTMatchOptionsPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_CASE_OPTION = new ICompositeElementType("XQUERY_FT_CASE_OPTION", FTCaseOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_DIACRITICS_OPTION = new ICompositeElementType("XQUERY_FT_DIACRITICS_OPTION", FTDiacriticsOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_EXTENSION_OPTION = new ICompositeElementType("XQUERY_FT_EXTENSION_OPTION", FTExtensionOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_LANGUAGE_OPTION = new ICompositeElementType("XQUERY_FT_LANGUAGE_OPTION", FTLanguageOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_STEM_OPTION = new ICompositeElementType("XQUERY_FT_STEM_OPTION", FTStemOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_STOP_WORD_OPTION = new ICompositeElementType("XQUERY_FT_STOP_WORD_OPTION", FTStopWordOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_THESAURUS_OPTION = new ICompositeElementType("XQUERY_FT_THESAURUS_OPTION", FTThesaurusOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_WILDCARD_OPTION = new ICompositeElementType("XQUERY_FT_WILDCARD_OPTION", FTWildCardOptionPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_THESAURUS_ID = new ICompositeElementType("XQUERY_FT_THESAURUS_ID", FTThesaurusIDPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_LITERAL_RANGE = new ICompositeElementType("XQUERY_FT_LITERAL_RANGE", FTLiteralRangePsiImpl.class, XQuery.INSTANCE);

    IElementType FT_STOP_WORDS = new ICompositeElementType("XQUERY_FT_STOP_WORDS", FTStopWordsPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_STOP_WORDS_INCL_EXCL = new ICompositeElementType("XQUERY_FT_STOP_WORDS_INCL_EXCL", FTStopWordsInclExclPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_SCORE_VAR = new ICompositeElementType("XQUERY_FT_SCORE_VAR", FTScoreVarPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_CONTAINS_EXPR = new ICompositeElementType("XQUERY_FT_CONTAINS_EXPR", FTContainsExprPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_SELECTION = new ICompositeElementType("XQUERY_FT_SELECTION", FTSelectionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_OR = new ICompositeElementType("XQUERY_FT_OR", FTOrPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_AND = new ICompositeElementType("XQUERY_FT_AND", FTAndPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_MILD_NOT = new ICompositeElementType("XQUERY_FT_MILD_NOT", FTMildNotPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_UNARY_NOT = new ICompositeElementType("XQUERY_FT_UNARY_NOT", FTUnaryNotPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_PRIMARY_WITH_OPTIONS = new ICompositeElementType("XQUERY_FT_PRIMARY_WITH_OPTIONS", FTPrimaryWithOptionsPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_PRIMARY = new ICompositeElementType("XQUERY_FT_PRIMARY", FTPrimaryPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_WORDS = new ICompositeElementType("XQUERY_FT_WORDS", FTWordsPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_WORDS_VALUE = new ICompositeElementType("XQUERY_FT_WORDS_VALUE", FTWordsValuePsiImpl.class, XQuery.INSTANCE);

    IElementType FT_EXTENSION_SELECTION = new ICompositeElementType("XQUERY_FT_EXTENSION_SELECTION", FTExtensionSelectionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_ANYALL_OPTION = new ICompositeElementType("XQUERY_FT_ANYALL_OPTION", FTAnyallOptionPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_TIMES = new ICompositeElementType("XQUERY_FT_TIMES", FTTimesPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_RANGE = new ICompositeElementType("XQUERY_FT_RANGE", FTRangePsiImpl.class, XQuery.INSTANCE);
    IElementType FT_WEIGHT = new ICompositeElementType("XQUERY_FT_WEIGHT", FTWeightPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_ORDER = new ICompositeElementType("XQUERY_FT_ORDER", FTOrderPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_WINDOW = new ICompositeElementType("XQUERY_FT_WINDOW", FTWindowPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_DISTANCE = new ICompositeElementType("XQUERY_FT_DISTANCE", FTDistancePsiImpl.class, XQuery.INSTANCE);
    IElementType FT_SCOPE = new ICompositeElementType("XQUERY_FT_SCOPE", FTScopePsiImpl.class, XQuery.INSTANCE);
    IElementType FT_CONTENT = new ICompositeElementType("XQUERY_FT_CONTENT", FTContentPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_UNIT = new ICompositeElementType("XQUERY_FT_UNIT", FTUnitPsiImpl.class, XQuery.INSTANCE);
    IElementType FT_BIG_UNIT = new ICompositeElementType("XQUERY_FT_BIG_UNIT", FTBigUnitPsiImpl.class, XQuery.INSTANCE);

    IElementType FT_IGNORE_OPTION = new ICompositeElementType("XQUERY_FT_IGNORE_OPTION", FTIgnoreOptionPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region Scripting Extension 1.0

    IElementType CONCAT_EXPR = new ICompositeElementType("XQUERY_CONCAT_EXPR", ScriptingConcatExprPsiImpl.class, XQuery.INSTANCE);

    IElementType COMPATBILITY_ANNOTATION_SCRIPTING = new ICompositeElementType("XQUERY_COMPATBILITY_ANNOTATION_SCRIPTING", ScriptingCompatibilityAnnotationPsiImpl.class, XQuery.INSTANCE);

    IElementType BLOCK = new ICompositeElementType("XQUERY_BLOCK", ScriptingBlockPsiImpl.class, XQuery.INSTANCE);
    IElementType BLOCK_BODY = new ICompositeElementType("XQUERY_BLOCK_BODY", ScriptingBlockBodyPsiImpl.class, XQuery.INSTANCE);

    IElementType BLOCK_DECLS = new ICompositeElementType("XQUERY_BLOCK_DECLS", ScriptingBlockDeclsPsiImpl.class, XQuery.INSTANCE);
    IElementType BLOCK_VAR_DECL = new ICompositeElementType("XQUERY_BLOCK_VAR_DECL", ScriptingBlockVarDeclPsiImpl.class, XQuery.INSTANCE);

    IElementType BLOCK_EXPR = new ICompositeElementType("XQUERY_BLOCK_EXPR", ScriptingBlockExprPsiImpl.class, XQuery.INSTANCE);
    IElementType ASSIGNMENT_EXPR = new ICompositeElementType("XQUERY_ASSIGNMENT_EXPR", ScriptingAssignmentExprPsiImpl.class, XQuery.INSTANCE);
    IElementType EXIT_EXPR = new ICompositeElementType("XQUERY_EXIT_EXPR", ScriptingExitExprPsiImpl.class, XQuery.INSTANCE);

    IElementType WHILE_EXPR = new ICompositeElementType("XQUERY_WHILE_EXPR", ScriptingWhileExprPsiImpl.class, XQuery.INSTANCE);
    IElementType WHILE_BODY = new ICompositeElementType("XQUERY_WHILE_BODY", ScriptingWhileBodyPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region MarkLogic 6.0

    IElementType TRANSACTION_SEPARATOR = new ICompositeElementType("XQUERY_TRANSACTION_SEPARATOR", MarkLogicTransactionSeparatorPsiImpl.class, XQuery.INSTANCE);

    IElementType COMPATIBILITY_ANNOTATION_MARKLOGIC = new ICompositeElementType("XQUERY_COMPATIBILITY_ANNOTATION_MARKLOGIC", MarkLogicCompatibilityAnnotationPsiImpl.class, XQuery.INSTANCE);
    IElementType STYLESHEET_IMPORT = new ICompositeElementType("XQUERY_STYLESHEET_IMPORT", MarkLogicStylesheetImportPsiImpl.class, XQuery.INSTANCE);

    IElementType BINARY_CONSTRUCTOR = new ICompositeElementType("XQUERY_BINARY_CONSTRUCTOR", MarkLogicBinaryConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType BINARY_TEST = new ICompositeElementType("XQUERY_BINARY_TEST", MarkLogicBinaryTestPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region MarkLogic 7.0

    IElementType ATTRIBUTE_DECL_TEST = new ICompositeElementType("XQUERY_ATTRIBUTE_DECL_TEST", MarkLogicAttributeDeclTestPsiImpl.class, XQuery.INSTANCE);
    IElementType COMPLEX_TYPE_TEST = new ICompositeElementType("XQUERY_COMPLEX_TYPE_TEST", MarkLogicComplexTypeTestPsiImpl.class, XQuery.INSTANCE);
    IElementType ELEMENT_DECL_TEST = new ICompositeElementType("XQUERY_ELEMENT_DECL_TEST", MarkLogicElementDeclTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_COMPONENT_TEST = new ICompositeElementType("XQUERY_SCHEMA_COMPONENT_TEST", MarkLogicSchemaComponentTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_PARTICLE_TEST = new ICompositeElementType("XQUERY_SCHEMA_PARTICLE_TEST", MarkLogicSchemaParticleTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_ROOT_TEST = new ICompositeElementType("XQUERY_SCHEMA_ROOT_TEST", MarkLogicSchemaRootTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SCHEMA_TYPE_TEST = new ICompositeElementType("XQUERY_SCHEMA_TYPE_TEST", MarkLogicSchemaTypeTestPsiImpl.class, XQuery.INSTANCE);
    IElementType SIMPLE_TYPE_TEST = new ICompositeElementType("XQUERY_SIMPLE_TYPE_TEST", MarkLogicSimpleTypeTestPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region MarkLogic 8.0

    IElementType SCHEMA_FACET_TEST = new ICompositeElementType("XQUERY_SCHEMA_FACET_TEST", MarkLogicSchemaFacetTestPsiImpl.class, XQuery.INSTANCE);

    IElementType ARRAY_TEST = new ICompositeElementType("XQUERY_ARRAY_TEST", MarkLogicArrayTestPsiImpl.class, XQuery.INSTANCE);

    IElementType BOOLEAN_CONSTRUCTOR = new ICompositeElementType("XQUERY_BOOLEAN_CONSTRUCTOR", MarkLogicBooleanConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType BOOLEAN_TEST = new ICompositeElementType("XQUERY_BOOLEAN_TEST", MarkLogicBooleanTestPsiImpl.class, XQuery.INSTANCE);

    IElementType NULL_CONSTRUCTOR = new ICompositeElementType("XQUERY_NULL_CONSTRUCTOR", MarkLogicNullConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType NULL_TEST = new ICompositeElementType("XQUERY_NULL_TEST", MarkLogicNullTestPsiImpl.class, XQuery.INSTANCE);

    IElementType NUMBER_CONSTRUCTOR = new ICompositeElementType("XQUERY_NUMBER_CONSTRUCTOR", MarkLogicNumberConstructorPsiImpl.class, XQuery.INSTANCE);
    IElementType NUMBER_TEST = new ICompositeElementType("XQUERY_NUMBER_TEST", MarkLogicNumberTestPsiImpl.class, XQuery.INSTANCE);

    IElementType MAP_TEST = new ICompositeElementType("XQUERY_MAP_TEST", MarkLogicMapTestPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region BaseX 7.8

    IElementType UPDATE_EXPR = new ICompositeElementType("XQUERY_UPDATE_EXPR", BaseXUpdateExprPsiImpl.class, XQuery.INSTANCE);

    // endregion
    // region Saxon 9.8

    IElementType TYPE_DECL = new ICompositeElementType("XQUERY_TYPE_DECL", SaxonTypeDeclImpl.class, XQuery.INSTANCE);

    IElementType TUPLE_TYPE = new ICompositeElementType("XQUERY_TUPLE_TYPE", SaxonTupleTypeImpl.class, XQuery.INSTANCE);
    IElementType TUPLE_FIELD = new ICompositeElementType("XQUERY_TUPLE_FIELD", SaxonTupleFieldImpl.class, XQuery.INSTANCE);

    IElementType UNION_TYPE = new ICompositeElementType("XQUERY_UNION_TYPE", SaxonUnionTypeImpl.class, XQuery.INSTANCE);

    // endregion
}
