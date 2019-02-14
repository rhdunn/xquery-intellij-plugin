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
}
