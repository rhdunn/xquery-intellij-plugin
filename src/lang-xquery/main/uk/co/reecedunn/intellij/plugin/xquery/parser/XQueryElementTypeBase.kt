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
import uk.co.reecedunn.intellij.plugin.core.parser.ICompositeElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.full.text.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin.*

open class XQueryElementTypeBase {
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
