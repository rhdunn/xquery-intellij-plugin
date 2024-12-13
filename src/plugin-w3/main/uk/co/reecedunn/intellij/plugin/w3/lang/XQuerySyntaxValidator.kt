/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.w3.lang

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.w3.resources.PluginW3Bundle
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowInlineFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginPostfixLookup
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresLanguageVersion
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object XQuerySyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginArrowInlineFunctionCall -> reporter.requires(element, XQUERY_4_0)
        is PluginDynamicFunctionCall -> reporter.requires(element, XQUERY_3_0)
        is PluginPostfixLookup -> when (element.conformanceElement.elementType) {
            XPathElementType.STRING_LITERAL -> reporter.requires(element, XQUERY_4_0)
            XPathElementType.VAR_REF -> reporter.requires(element, XQUERY_4_0)
            else -> reporter.requires(element, XQUERY_3_1)
        }
        is XPathAnyArrayTest -> reporter.requires(element, XQUERY_3_1)
        is XPathAnyFunctionTest -> reporter.requires(element, XQUERY_3_0)
        is XPathAnyMapTest -> reporter.requires(element, XQUERY_3_1)
        is XPathArgumentPlaceholder -> reporter.requires(element, XQUERY_3_0)
        is XPathArrowFunctionSpecifier -> when (element.conformanceElement.elementType) {
            XPathTokenType.THIN_ARROW -> reporter.requires(element, XQUERY_4_0)
            else -> reporter.requires(element, XQUERY_3_1)
        }
        is XPathAttributeTest -> when (element.conformanceElement) {
            is XPathWildcard -> {
                val name = XPathBundle.message("construct.wildcard-attribute-test")
                reporter.requires(element, XQUERY_4_0, name)
            }
            else -> {
            }
        }
        is XPathBracedURILiteral -> reporter.requires(element, XQUERY_3_0)
        is XPathCurlyArrayConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ARRAY -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryDecimalFormatDecl -> when (element.conformanceElement.firstChild.elementType) {
            XQueryTokenType.K_EXPONENT_SEPARATOR -> reporter.requires(element, XQUERY_3_1)
            else -> reporter.requires(element, XQUERY_3_0)
        }
        is XPathElementTest -> when (element.conformanceElement) {
            is XPathWildcard -> {
                val name = XPathBundle.message("construct.wildcard-element-test")
                reporter.requires(element, XQUERY_4_0, name)
            }
            else -> {
            }
        }
        is XPathEnumerationType -> reporter.requires(element, XQUERY_4_0)
        is XPathInlineFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_FUNCTION -> reporter.requires(element, XQUERY_3_0)
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> reporter.requires(element, XQUERY_4_0)
        }
        is XPathKeywordArgument -> reporter.requires(element, XQUERY_4_0)
        is XPathLocalUnionType -> reporter.requires(element, XQUERY_4_0)
        is XPathNamedFunctionRef -> reporter.requires(element, XQUERY_3_0)
        is XPathNamespaceNodeTest -> reporter.requires(element, XQUERY_3_0)
        is XPathOtherwiseExpr -> reporter.requires(element, XQUERY_4_0)
        is XPathRecordTest -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_RECORD -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XPathSimpleForClause -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_MEMBER -> reporter.requires(element, XQUERY_4_0)
            else -> flworClause(element, reporter)
        }
        is XPathSimpleMapExpr -> reporter.requires(element, XQUERY_3_0)
        is XPathSquareArrayConstructor -> reporter.requires(element, XQUERY_3_1)
        is XPathStringConcatExpr -> reporter.requires(element, XQUERY_3_0)
        is XPathTernaryConditionalExpr -> reporter.requires(element, XQUERY_4_0)
        is XPathTypedArrayTest -> reporter.requires(element, XQUERY_3_1)
        is XPathTypedFunctionTest -> reporter.requires(element, XQUERY_3_0)
        is XPathUnaryLookup -> when (element.conformanceElement.elementType) {
            XPathElementType.STRING_LITERAL -> reporter.requires(element, XQUERY_4_0)
            XPathElementType.VAR_REF -> reporter.requires(element, XQUERY_4_0)
            else -> reporter.requires(element, XQUERY_3_1)
        }
        is XPathWithExpr -> reporter.requires(element, XQUERY_4_0)
        is XQueryAllowingEmpty -> reporter.requires(element, XQUERY_3_0)
        is XQueryAnnotation -> reporter.requires(element, XQUERY_3_0)
        is XQueryCatchClause -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryCompCommentConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryCompDocConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryCompNamespaceConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> reporter.requires(element, XQUERY_3_0)
        }
        is XQueryCompTextConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryContextItemDecl -> reporter.requires(element, XQUERY_3_0)
        is XQueryCountClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryDefaultNamespaceDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_TYPE -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XQueryDirAttributeValue -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryDirElemConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryFunctionDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryGroupByClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryItemTypeDecl -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_ITEM_TYPE -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XQueryLetClause -> flworClause(element, reporter)
        is XQueryOrderByClause -> flworClause(element, reporter)
        is XQueryOrderedExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQuerySequenceTypeUnion -> when ((element as PsiElement).parent) {
            is XQueryCaseClause -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XQuerySlidingWindowClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryStringConstructor -> reporter.requires(element, XQUERY_3_1)
        is XQuerySwitchExpr -> reporter.requires(element, XQUERY_3_0)
        is XQueryTryCatchExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> reporter.requires(element, XQUERY_3_0)
        }
        is XQueryTumblingWindowClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryUnorderedExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.BLOCK_OPEN -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XQueryValidateExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_TYPE -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XQueryVarDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.ASSIGN_EQUAL -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XQueryVersionDecl -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_ENCODING -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XQueryWhereClause -> flworClause(element, reporter)
        else -> {
        }
    }

    private fun flworClause(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) {
        if (reverse(element.siblings()).any {
                (it is XQueryWhereClause && element !is XQueryOrderByClause) || it is XQueryOrderByClause
            }) {
            val message = PluginW3Bundle.message("conformance.relaxed-flwor-ordering")
            reporter.requires(element, XQUERY_3_0, message)
        }
    }

    private val XQUERY_3_0 = XpmRequiresLanguageVersion(XQuery.VERSION_3_0)
    private val XQUERY_3_1 = XpmRequiresLanguageVersion(XQuery.VERSION_3_1)
    private val XQUERY_4_0 = XpmRequiresLanguageVersion(XQuery.VERSION_4_0)
}
