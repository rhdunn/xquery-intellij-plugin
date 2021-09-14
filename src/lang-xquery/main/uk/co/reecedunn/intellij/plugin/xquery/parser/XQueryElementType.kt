/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.IASTWrapperElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.full.text.FTOptionDeclPsiImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.*

@Suppress("Reformat")
object XQueryElementType {
    // region XQuery 1.0

    val BASE_URI_DECL: IElementType = IASTWrapperElementType("XQUERY_BASE_URI_DECL", XQuery) { node ->
        XQueryBaseURIDeclPsiImpl(node)
    }

    val BOUNDARY_SPACE_DECL: IElementType = IASTWrapperElementType("XQUERY_BOUNDARY_SPACE_DECL", XQuery) { node ->
        XQueryBoundarySpaceDeclPsiImpl(node)
    }

    val CASE_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_CASE_CLAUSE", XQuery) { node ->
        XQueryCaseClausePsiImpl(node)
    }

    val CDATA_SECTION: IElementType = IASTWrapperElementType("XQUERY_CDATA_SECTION", XQuery) { node ->
        XQueryCDataSectionPsiImpl(node)
    }

    val COMMENT: IElementType = IASTWrapperElementType("XQUERY_COMMENT", XQuery) { node ->
        XQueryCommentPsiImpl(node)
    }

    val COMP_ATTR_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_ATTR_CONSTRUCTOR", XQuery) { node ->
        XQueryCompAttrConstructorPsiImpl(node)
    }

    val COMP_COMMENT_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_COMMENT_CONSTRUCTOR", XQuery) { node ->
        XQueryCompCommentConstructorPsiImpl(node)
    }

    val COMP_DOC_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_DOC_CONSTRUCTOR", XQuery) { node ->
        XQueryCompDocConstructorPsiImpl(node)
    }

    val COMP_ELEM_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_ELEM_CONSTRUCTOR", XQuery) { node ->
        XQueryCompElemConstructorPsiImpl(node)
    }

    val COMP_PI_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_PI_CONSTRUCTOR", XQuery) { node ->
        XQueryCompPIConstructorPsiImpl(node)
    }

    val COMP_TEXT_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_TEXT_CONSTRUCTOR", XQuery) { node ->
        XQueryCompTextConstructorPsiImpl(node)
    }

    val CONSTRUCTION_DECL: IElementType = IASTWrapperElementType("XQUERY_CONSTRUCTION_DECL", XQuery) { node ->
        XQueryConstructionDeclPsiImpl(node)
    }

    val COPY_NAMESPACES_DECL: IElementType = IASTWrapperElementType("XQUERY_COPY_NAMESPACES_DECL", XQuery) { node ->
        XQueryCopyNamespacesDeclPsiImpl(node)
    }

    val DEFAULT_COLLATION_DECL: IElementType = IASTWrapperElementType("XQUERY_DEFAULT_COLLATION_DECL", XQuery) { node ->
        XQueryDefaultCollationDeclPsiImpl(node)
    }

    val DEFAULT_NAMESPACE_DECL: IElementType = IASTWrapperElementType("XQUERY_DEFAULT_NAMESPACE_DECL", XQuery) { node ->
        XQueryDefaultNamespaceDeclPsiImpl(node)
    }

    val DIR_ATTRIBUTE_VALUE: IElementType = IASTWrapperElementType("XQUERY_DIR_ATTRIBUTE_VALUE", XQuery) { node ->
        XQueryDirAttributeValuePsiImpl(node)
    }

    val DIR_COMMENT_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQuery) { node ->
        XQueryDirCommentConstructorPsiImpl(node)
    }

    val DIR_ELEM_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_DIR_ELEM_CONSTRUCTOR", XQuery) { node ->
        XQueryDirElemConstructorPsiImpl(node)
    }

    val DIR_PI_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_DIR_PI_CONSTRUCTOR", XQuery) { node ->
        XQueryDirPIConstructorPsiImpl(node)
    }

    val EMPTY_ORDER_DECL: IElementType = IASTWrapperElementType("XQUERY_EMPTY_ORDER_DECL", XQuery) { node ->
        XQueryEmptyOrderDeclPsiImpl(node)
    }

    val EXTENSION_EXPR: IElementType = IASTWrapperElementType("XQUERY_EXTENSION_EXPR", XQuery) { node ->
        XQueryExtensionExprPsiImpl(node)
    }

    val FLWOR_EXPR: IElementType = IASTWrapperElementType("XQUERY_FLWOR_EXPR", XQuery) { node ->
        XQueryFLWORExprPsiImpl(node)
    }

    val FOR_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_FOR_CLAUSE", XQuery) { node ->
        XQueryForClausePsiImpl(node)
    }

    val FUNCTION_DECL: IElementType = IASTWrapperElementType("XQUERY_FUNCTION_DECL", XQuery) { node ->
        XQueryFunctionDeclPsiImpl(node)
    }

    val LIBRARY_MODULE: IElementType = IASTWrapperElementType("XQUERY_LIBRARY_MODULE", XQuery) { node ->
        XQueryLibraryModulePsiImpl(node)
    }

    val MAIN_MODULE: IElementType = IASTWrapperElementType("XQUERY_MAIN_MODULE", XQuery) { node ->
        XQueryMainModulePsiImpl(node)
    }

    val MODULE: IFileElementType = IFileElementType(XQuery)

    val MODULE_DECL: IElementType = IASTWrapperElementType("XQUERY_MODULE_DECL", XQuery) { node ->
        XQueryModuleDeclPsiImpl(node)
    }

    val MODULE_IMPORT: IElementType = IASTWrapperElementType("XQUERY_MODULE_IMPORT", XQuery) { node ->
        XQueryModuleImportPsiImpl(node)
    }

    val NAMESPACE_DECL: IElementType = IASTWrapperElementType("XQUERY_NAMESPACE_DECL", XQuery) { node ->
        XQueryNamespaceDeclPsiImpl(node)
    }

    val OPTION_DECL: IElementType = IASTWrapperElementType("XQUERY_OPTION_DECL", XQuery) { node ->
        XQueryOptionDeclPsiImpl(node)
    }

    val ORDER_BY_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_ORDER_BY_CLAUSE", XQuery) { node ->
        XQueryOrderByClausePsiImpl(node)
    }

    val ORDERED_EXPR: IElementType = IASTWrapperElementType("XQUERY_ORDERED_EXPR", XQuery) { node ->
        XQueryOrderedExprPsiImpl(node)
    }

    val ORDER_MODIFIER: IElementType = IASTWrapperElementType("XQUERY_ORDER_MODIFIER", XQuery) { node ->
        XQueryOrderModifierPsiImpl(node)
    }

    val ORDERING_MODE_DECL: IElementType = IASTWrapperElementType("XQUERY_ORDERING_MODE_DECL", XQuery) { node ->
        XQueryOrderingModeDeclPsiImpl(node)
    }

    val ORDER_SPEC: IElementType = IASTWrapperElementType("XQUERY_ORDER_SPEC", XQuery) { node ->
        XQueryOrderSpecPsiImpl(node)
    }

    val POSITIONAL_VAR: IElementType = IASTWrapperElementType("XQUERY_POSITIONAL_VAR", XQuery) { node ->
        XQueryPositionalVarPsiImpl(node)
    }

    val PROLOG: IElementType = IASTWrapperElementType("XQUERY_PROLOG", XQuery) { node ->
        XQueryPrologPsiImpl(node)
    }

    val QUERY_BODY: IElementType = IASTWrapperElementType("XQUERY_QUERY_BODY", XQuery) { node ->
        XQueryQueryBodyPsiImpl(node)
    }

    val SCHEMA_IMPORT: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_IMPORT", XQuery) { node ->
        XQuerySchemaImportPsiImpl(node)
    }

    val SCHEMA_PREFIX: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_PREFIX", XQuery) { node ->
        XQuerySchemaPrefixPsiImpl(node)
    }

    val TYPESWITCH_EXPR: IElementType = IASTWrapperElementType("XQUERY_TYPESWITCH_EXPR", XQuery) { node ->
        XQueryTypeswitchExprPsiImpl(node)
    }

    val UNORDERED_EXPR: IElementType = IASTWrapperElementType("XQUERY_UNORDERED_EXPR", XQuery) { node ->
        XQueryUnorderedExprPsiImpl(node)
    }

    val URI_LITERAL: IElementType = IASTWrapperElementType("XQUERY_URI_LITERAL", XQuery) { node ->
        XQueryUriLiteralPsiImpl(node)
    }

    val VALIDATE_EXPR: IElementType = IASTWrapperElementType("XQUERY_VALIDATE_EXPR", XQuery) { node ->
        XQueryValidateExprPsiImpl(node)
    }

    val VAR_DECL: IElementType = IASTWrapperElementType("XQUERY_VAR_DECL", XQuery) { node ->
        XQueryVarDeclPsiImpl(node)
    }

    val VERSION_DECL: IElementType = IASTWrapperElementType("XQUERY_VERSION_DECL", XQuery) { node ->
        XQueryVersionDeclPsiImpl(node)
    }

    val WHERE_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_WHERE_CLAUSE", XQuery) { node ->
        XQueryWhereClausePsiImpl(node)
    }

    // endregion
    // region XQuery 3.0

    val ALLOWING_EMPTY: IElementType = IASTWrapperElementType("XQUERY_ALLOWING_EMPTY", XQuery) { node ->
        XQueryAllowingEmptyPsiImpl(node)
    }

    val ANNOTATION: IElementType = IASTWrapperElementType("XQUERY_ANNOTATION", XQuery) { node ->
        XQueryAnnotationPsiImpl(node)
    }

    val BRACED_URI_LITERAL: IElementType = IASTWrapperElementType("XQUERY_BRACED_URI_LITERAL", XQuery) { node ->
        XQueryBracedURILiteralPsiImpl(node)
    }

    val CATCH_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_CATCH_CLAUSE", XQuery) { node ->
        XQueryCatchClausePsiImpl(node)
    }

    val COMP_NAMESPACE_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_COMP_NAMESPACE_CONSTRUCTOR", XQuery) { node ->
        XQueryCompNamespaceConstructorPsiImpl(node)
    }

    val CONTEXT_ITEM_DECL: IElementType = IASTWrapperElementType("XQUERY_CONTEXT_ITEM_DECL", XQuery) { node ->
        XQueryContextItemDeclPsiImpl(node)
    }

    val COUNT_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_COUNT_CLAUSE", XQuery) { node ->
        XQueryCountClausePsiImpl(node)
    }

    val CURRENT_ITEM: IElementType = IASTWrapperElementType("XQUERY_CURRENT_ITEM", XQuery) { node ->
        XQueryCurrentItemPsiImpl(node)
    }

    val DECIMAL_FORMAT_DECL: IElementType = IASTWrapperElementType("XQUERY_DECIMAL_FORMAT_DECL", XQuery) { node ->
        XQueryDecimalFormatDeclPsiImpl(node)
    }

    val DF_PROPERTY_NAME: IElementType = IASTWrapperElementType("XQUERY_DF_PROPERTY_NAME", XQuery) { node ->
        XQueryDFPropertyNamePsiImpl(node)
    }

    val FOR_BINDING: IElementType = IASTWrapperElementType("XQUERY_FOR_BINDING", XQuery) { node ->
        XQueryForBindingPsiImpl(node)
    }

    val GROUP_BY_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_GROUP_BY_CLAUSE", XQuery) { node ->
        XQueryGroupByClausePsiImpl(node)
    }

    val GROUPING_SPEC: IElementType = IASTWrapperElementType("XQUERY_GROUPING_SPEC", XQuery) { node ->
        XQueryGroupingSpecPsiImpl(node)
    }

    val LET_BINDING: IElementType = IASTWrapperElementType("XQUERY_LET_BINDING", XQuery) { node ->
        XQueryLetBindingPsiImpl(node)
    }

    val LET_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_LET_CLAUSE", XQuery) { node ->
        XQueryLetClausePsiImpl(node)
    }

    val NEXT_ITEM: IElementType = IASTWrapperElementType("XQUERY_NEXT_ITEM", XQuery) { node ->
        XQueryNextItemPsiImpl(node)
    }

    val PREVIOUS_ITEM: IElementType = IASTWrapperElementType("XQUERY_PREVIOUS_ITEM", XQuery) { node ->
        XQueryPreviousItemPsiImpl(node)
    }

    val SEQUENCE_TYPE_UNION: IElementType = IASTWrapperElementType("XQUERY_SEQUENCE_TYPE_UNION", XQuery) { node ->
        XQuerySequenceTypeUnionPsiImpl(node)
    }

    val SLIDING_WINDOW_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_SLIDING_WINDOW_CLAUSE", XQuery) { node ->
        XQuerySlidingWindowClausePsiImpl(node)
    }

    val SWITCH_CASE_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_SWITCH_CASE_CLAUSE", XQuery) { node ->
        XQuerySwitchCaseClausePsiImpl(node)
    }

    val SWITCH_CASE_OPERAND: IElementType = IASTWrapperElementType("XQUERY_SWITCH_CASE_OPERAND", XQuery) { node ->
        XQuerySwitchCaseOperandPsiImpl(node)
    }

    val SWITCH_EXPR: IElementType = IASTWrapperElementType("XQUERY_SWITCH_EXPR", XQuery) { node ->
        XQuerySwitchExprPsiImpl(node)
    }

    val TRY_CATCH_EXPR: IElementType = IASTWrapperElementType("XQUERY_TRY_CATCH_EXPR", XQuery) { node ->
        XQueryTryCatchExprPsiImpl(node)
    }

    val TUMBLING_WINDOW_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_TUMBLING_WINDOW_CLAUSE", XQuery) { node ->
        XQueryTumblingWindowClausePsiImpl(node)
    }

    val WINDOW_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_WINDOW_CLAUSE", XQuery) { node ->
        XQueryWindowClausePsiImpl(node)
    }

    val WINDOW_END_CONDITION: IElementType = IASTWrapperElementType("XQUERY_WINDOW_END_CONDITION", XQuery) { node ->
        XQueryWindowEndConditionPsiImpl(node)
    }

    val WINDOW_START_CONDITION: IElementType = IASTWrapperElementType("XQUERY_WINDOW_START_CONDITION", XQuery) { node ->
        XQueryWindowStartConditionPsiImpl(node)
    }

    val WINDOW_VARS: IElementType = IASTWrapperElementType("XQUERY_WINDOW_VARS", XQuery) { node ->
        XQueryWindowVarsPsiImpl(node)
    }

    // endregion
    // region XQuery 3.1

    val ENCLOSED_URI_EXPR: IElementType = IASTWrapperElementType("XQUERY_ENCLOSED_URI_EXPR", XQuery) { node ->
        XQueryEnclosedURIExprPsiImpl(node)
    }

    val STRING_CONSTRUCTOR_CONTENT: IElementType = IASTWrapperElementType("XQUERY_STRING_CONSTRUCTOR_CONTENT", XQuery) { node ->
        XQueryStringConstructorContentPsiImpl(node)
    }

    val STRING_CONSTRUCTOR_INTERPOLATION: IElementType = IASTWrapperElementType("XQUERY_STRING_CONSTRUCTOR_INTERPOLATION", XQuery) { node ->
        XQueryStringConstructorInterpolationPsiImpl(node)
    }

    val STRING_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_STRING_CONSTRUCTOR", XQuery) { node ->
        XQueryStringConstructorPsiImpl(node)
    }

    // endregion
    // region XQuery 4.0 ED

    val FOR_MEMBER_BINDING: IElementType = IASTWrapperElementType("XQUERY_FOR_MEMBER_BINDING", XQuery) { node ->
        XQueryForMemberBindingPsiImpl(node)
    }

    val FOR_MEMBER_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_FOR_MEMBER_CLAUSE", XQuery) { node ->
        XQueryForMemberClausePsiImpl(node)
    }

    val ITEM_TYPE_DECL: IElementType = IASTWrapperElementType("XQUERY_ITEM_TYPE_DECL", XQuery) { node ->
        XQueryItemTypeDeclPsiImpl(node)
    }

    // endregion
    // region Full Text 1.0

    val FT_OPTION_DECL: IElementType = IASTWrapperElementType("XQUERY_FT_OPTION_DECL", XQuery) { node ->
        FTOptionDeclPsiImpl(node)
    }

    // endregion
    // region Scripting Extension 1.0

    val APPLY_EXPR: IElementType = IASTWrapperElementType("XQUERY_APPLY_EXPR", XQuery) { node ->
        ScriptingApplyExprPsiImpl(node)
    }

    val ASSIGNMENT_EXPR: IElementType = IASTWrapperElementType("XQUERY_ASSIGNMENT_EXPR", XQuery) { node ->
        ScriptingAssignmentExprPsiImpl(node)
    }

    val BLOCK: IElementType = IASTWrapperElementType("XQUERY_BLOCK", XQuery) { node ->
        ScriptingBlockPsiImpl(node)
    }

    val BLOCK_BODY: IElementType = IASTWrapperElementType("XQUERY_BLOCK_BODY", XQuery) { node ->
        ScriptingBlockBodyPsiImpl(node)
    }

    val BLOCK_DECLS: IElementType = IASTWrapperElementType("XQUERY_BLOCK_DECLS", XQuery) { node ->
        ScriptingBlockDeclsPsiImpl(node)
    }

    val BLOCK_EXPR: IElementType = IASTWrapperElementType("XQUERY_BLOCK_EXPR", XQuery) { node ->
        ScriptingBlockExprPsiImpl(node)
    }

    val BLOCK_VAR_DECL: IElementType = IASTWrapperElementType("XQUERY_BLOCK_VAR_DECL", XQuery) { node ->
        ScriptingBlockVarDeclPsiImpl(node)
    }

    val CONCAT_EXPR: IElementType = IASTWrapperElementType("XQUERY_CONCAT_EXPR", XQuery) { node ->
        ScriptingConcatExprPsiImpl(node)
    }

    val EXIT_EXPR: IElementType = IASTWrapperElementType("XQUERY_EXIT_EXPR", XQuery) { node ->
        ScriptingExitExprPsiImpl(node)
    }

    val WHILE_BODY: IElementType = IASTWrapperElementType("XQUERY_WHILE_BODY", XQuery) { node ->
        ScriptingWhileBodyPsiImpl(node)
    }

    val WHILE_EXPR: IElementType = IASTWrapperElementType("XQUERY_WHILE_EXPR", XQuery) { node ->
        ScriptingWhileExprPsiImpl(node)
    }

    // endregion
    // region Update Facility 1.0

    val REVALIDATION_DECL: IElementType = IASTWrapperElementType("XQUERY_REVALIDATION_DECL", XQuery) { node ->
        UpdateFacilityRevalidationDeclPsiImpl(node)
    }

    val INSERT_EXPR: IElementType = IASTWrapperElementType("XQUERY_INSERT_EXPR", XQuery) { node ->
        UpdateFacilityInsertExprPsiImpl(node)
    }

    val DELETE_EXPR: IElementType = IASTWrapperElementType("XQUERY_DELETE_EXPR", XQuery) { node ->
        UpdateFacilityDeleteExprPsiImpl(node)
    }

    val REPLACE_EXPR: IElementType = IASTWrapperElementType("XQUERY_REPLACE_EXPR", XQuery) { node ->
        UpdateFacilityReplaceExprPsiImpl(node)
    }

    val RENAME_EXPR: IElementType = IASTWrapperElementType("XQUERY_RENAME_EXPR", XQuery) { node ->
        UpdateFacilityRenameExprPsiImpl(node)
    }

    val NEW_NAME_EXPR: IElementType = IASTWrapperElementType("XQUERY_NEW_NAME_EXPR", XQuery) { node ->
        UpdateFacilityNewNameExprPsiImpl(node)
    }

    val SOURCE_EXPR: IElementType = IASTWrapperElementType("XQUERY_SOURCE_EXPR", XQuery) { node ->
        UpdateFacilitySourceExprPsiImpl(node)
    }

    val TARGET_EXPR: IElementType = IASTWrapperElementType("XQUERY_TARGET_EXPR", XQuery) { node ->
        UpdateFacilityTargetExprPsiImpl(node)
    }

    val INSERT_EXPR_TARGET_CHOICE: IElementType = IASTWrapperElementType("XQUERY_INSERT_EXPR_TARGET_CHOICE", XQuery) { node ->
        UpdateFacilityInsertExprTargetChoicePsiImpl(node)
    }

    // endregion
    // region Update Facility 3.0

    val COPY_MODIFY_EXPR: IElementType = IASTWrapperElementType("XQUERY_COPY_MODIFY_EXPR", XQuery) { node ->
        UpdateFacilityCopyModifyExprPsiImpl(node)
    }

    val UPDATING_FUNCTION_CALL: IElementType = IASTWrapperElementType("XQUERY_UPDATING_FUNCTION_CALL", XQuery) { node ->
        UpdateFacilityUpdatingFunctionCallPsiImpl(node)
    }

    val TRANSFORM_WITH_EXPR: IElementType = IASTWrapperElementType("XQUERY_TRANSFORM_WITH_EXPR", XQuery) { node ->
        UpdateFacilityTransformWithExprPsiImpl(node)
    }

    // endregion
    // region XQuery IntelliJ Plugin

    val ANY_ARRAY_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_ANY_ARRAY_NODE_TEST", XQuery) { node ->
        PluginAnyArrayNodeTestPsiImpl(node)
    }

    val ANY_BOOLEAN_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_ANY_BOOLEAN_NODE_TEST", XQuery) { node ->
        PluginAnyBooleanNodeTestPsiImpl(node)
    }

    val ANY_MAP_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_ANY_MAP_NODE_TEST", XQuery) { node ->
        PluginAnyMapNodeTestPsiImpl(node)
    }

    val ANY_NULL_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_ANY_NULL_NODE_TEST", XQuery) { node ->
        PluginAnyNullNodeTestPsiImpl(node)
    }

    val ANY_NUMBER_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_ANY_NUMBER_NODE_TEST", XQuery) { node ->
        PluginAnyNumberNodeTestPsiImpl(node)
    }

    val ATTRIBUTE_DECL_TEST: IElementType = IASTWrapperElementType("XQUERY_ATTRIBUTE_DECL_TEST", XQuery) { node ->
        PluginAttributeDeclTestPsiImpl(node)
    }

    val BINARY_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_BINARY_CONSTRUCTOR", XQuery) { node ->
        PluginBinaryConstructorPsiImpl(node)
    }

    val BINARY_TEST: IElementType = IASTWrapperElementType("XQUERY_BINARY_TEST", XQuery) { node ->
        PluginBinaryTestPsiImpl(node)
    }

    val BLOCK_VAR_DECL_ENTRY: IElementType = IASTWrapperElementType("XQUERY_BLOCK_VAR_DECL_ENTRY", XQuery) { node ->
        PluginBlockVarDeclEntryPsiImpl(node)
    }

    val BOOLEAN_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_BOOLEAN_CONSTRUCTOR", XQuery) { node ->
        PluginBooleanConstructorPsiImpl(node)
    }

    val COMPATIBILITY_ANNOTATION: IElementType = IASTWrapperElementType("XQUERY_COMPATIBILITY_ANNOTATION", XQuery) { node ->
        PluginCompatibilityAnnotationPsiImpl(node)
    }

    val COMPLEX_TYPE_TEST: IElementType = IASTWrapperElementType("XQUERY_COMPLEX_TYPE_TEST", XQuery) { node ->
        PluginComplexTypeTestPsiImpl(node)
    }

    val COPY_MODIFY_EXPR_BINDING: IElementType = IASTWrapperElementType("XQUERY_COPY_MODIFY_EXPR_BINDING", XQuery) { node ->
        PluginCopyModifyExprBindingPsiImpl(node)
    }

    val DEFAULT_CASE_CLAUSE: IElementType = IASTWrapperElementType("XQUERY_DEFAULT_CASE_CLAUSE", XQuery) { node ->
        PluginDefaultCaseClausePsiImpl(node)
    }

    val DIR_ATTRIBUTE: IElementType = IASTWrapperElementType("XQUERY_DIR_ATTRIBUTE", XQuery) { node ->
        PluginDirAttributePsiImpl(node)
    }

    val DIR_TEXT_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_DIR_TEXT_CONSTRUCTOR", XQuery) { node ->
        PluginDirTextConstructorPsiImpl(node)
    }

    val ELEMENT_DECL_TEST: IElementType = IASTWrapperElementType("XQUERY_ELEMENT_DECL_TEST", XQuery) { node ->
        PluginElementDeclTestPsiImpl(node)
    }

    val ELVIS_EXPR: IElementType = IASTWrapperElementType("XQUERY_ELVIS_EXPR", XPath) { node ->
        PluginElvisExprPsiImpl(node)
    }

    val FT_FUZZY_OPTION: IElementType = IASTWrapperElementType("XQUERY_FT_FUZZY_OPTION", XQuery) { node ->
        PluginFTFuzzyOptionPsiImpl(node)
    }

    val LOCATION_URI_LIST: IElementType = IASTWrapperElementType("XQUERY_LOCATION_URI_LIST", XQuery) { node ->
        PluginLocationURIListPsiImpl(node)
    }

    val MODEL_GROUP_TEST: IElementType = IASTWrapperElementType("XQUERY_MODEL_GROUP_TEST", XQuery) { node ->
        PluginModelGroupTestPsiImpl(node)
    }

    val NAMED_ARRAY_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_ARRAY_NODE_TEST", XQuery) { node ->
        PluginNamedArrayNodeTestPsiImpl(node)
    }

    val NAMED_BOOLEAN_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_BOOLEAN_NODE_TEST", XQuery) { node ->
        PluginNamedBooleanNodeTestPsiImpl(node)
    }

    val NAMED_KIND_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_KIND_TEST", XQuery) { node ->
        PluginNamedKindTestPsiImpl(node)
    }

    val NAMED_MAP_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_MAP_NODE_TEST", XQuery) { node ->
        PluginNamedMapNodeTestPsiImpl(node)
    }

    val NAMED_NULL_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_NULL_NODE_TEST", XQuery) { node ->
        PluginNamedNullNodeTestPsiImpl(node)
    }

    val NAMED_NUMBER_NODE_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_NUMBER_NODE_TEST", XQuery) { node ->
        PluginNamedNumberNodeTestPsiImpl(node)
    }

    val NAMED_TEXT_TEST: IElementType = IASTWrapperElementType("XQUERY_NAMED_TEXT_TEST", XQuery) { node ->
        PluginNamedTextTestPsiImpl(node)
    }

    val NON_DETERMINISTIC_FUNCTION_CALL: IElementType = IASTWrapperElementType("NON_DETERMINISTIC_FUNCTION_CALL", XQuery) { node ->
        PluginNonDeterministicFunctionCallPsiImpl(node)
    }

    val NULL_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_NULL_CONSTRUCTOR", XQuery) { node ->
        PluginNullConstructorPsiImpl(node)
    }

    val NUMBER_CONSTRUCTOR: IElementType = IASTWrapperElementType("XQUERY_NUMBER_CONSTRUCTOR", XQuery) { node ->
        PluginNumberConstructorPsiImpl(node)
    }

    val SCHEMA_COMPONENT_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_COMPONENT_TEST", XQuery) { node ->
        PluginSchemaComponentTestPsiImpl(node)
    }

    val SCHEMA_FACET_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_FACET_TEST", XQuery) { node ->
        PluginSchemaFacetTestPsiImpl(node)
    }

    val SCHEMA_PARTICLE_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_PARTICLE_TEST", XQuery) { node ->
        PluginSchemaParticleTestPsiImpl(node)
    }

    val SCHEMA_ROOT_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_ROOT_TEST", XQuery) { node ->
        PluginSchemaRootTestPsiImpl(node)
    }

    val SCHEMA_TYPE_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_TYPE_TEST", XQuery) { node ->
        PluginSchemaTypeTestPsiImpl(node)
    }

    val SCHEMA_WILDCARD_TEST: IElementType = IASTWrapperElementType("XQUERY_SCHEMA_WILDCARD_TEST", XQuery) { node ->
        PluginSchemaWildcardTestPsiImpl(node)
    }

    val SIMPLE_TYPE_TEST: IElementType = IASTWrapperElementType("XQUERY_SIMPLE_TYPE_TEST", XQuery) { node ->
        PluginSimpleTypeTestPsiImpl(node)
    }

    val STYLESHEET_IMPORT: IElementType = IASTWrapperElementType("XQUERY_STYLESHEET_IMPORT", XQuery) { node ->
        PluginStylesheetImportPsiImpl(node)
    }

    val TRANSACTION_SEPARATOR: IElementType = IASTWrapperElementType("XQUERY_TRANSACTION_SEPARATOR", XQuery) { node ->
        PluginTransactionSeparatorPsiImpl(node)
    }

    val UPDATE_EXPR: IElementType = IASTWrapperElementType("XQUERY_UPDATE_EXPR", XQuery) { node ->
        PluginUpdateExprPsiImpl(node)
    }

    val USING_DECL: IElementType = IASTWrapperElementType("XQUERY_USING_DECL", XQuery) { node ->
        PluginUsingDeclPsiImpl(node)
    }

    // endregion
    // region Token Sets

    val XML_NAME = TokenSet.create(XPathElementType.NCNAME, XPathElementType.QNAME)

    // endregion
}
