/*
 * Copyright (C) 2021 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowInlineFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresLanguageVersion
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object XQuerySyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginArrowInlineFunctionCall -> reporter.requires(element, XQUERY_4_0)
        is PluginDynamicFunctionCall -> reporter.requires(element, XQUERY_3_0)
        is XQueryVersionDecl -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_ENCODING -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XPathAnyArrayTest -> reporter.requires(element, XQUERY_3_1)
        is XPathAnyFunctionTest -> reporter.requires(element, XQUERY_3_0)
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
            else -> reporter.requires(element, XQUERY_4_0)
        }
        is XPathKeySpecifier -> when (element.conformanceElement.elementType) {
            XPathElementType.STRING_LITERAL -> reporter.requires(element, XQUERY_4_0)
            XPathElementType.VAR_REF -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XPathKeywordArgument -> reporter.requires(element, XQUERY_4_0)
        is XPathLocalUnionType -> reporter.requires(element, XQUERY_4_0)
        is XPathLookup -> reporter.requires(element, XQUERY_3_1)
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
            else -> {
            }
        }
        is XPathSimpleMapExpr -> reporter.requires(element, XQUERY_3_0)
        is XPathSquareArrayConstructor -> reporter.requires(element, XQUERY_3_1)
        is XPathStringConcatExpr -> reporter.requires(element, XQUERY_3_0)
        is XPathTernaryConditionalExpr -> reporter.requires(element, XQUERY_4_0)
        is XPathTypedArrayTest -> reporter.requires(element, XQUERY_3_1)
        is XPathTypedFunctionTest -> reporter.requires(element, XQUERY_3_0)
        is XPathUnaryLookup -> reporter.requires(element, XQUERY_3_1)
        is XPathWithExpr -> reporter.requires(element, XQUERY_4_0)
        is XQueryAllowingEmpty -> reporter.requires(element, XQUERY_3_0)
        is XQueryAnnotation -> reporter.requires(element, XQUERY_3_0)
        is XQueryCompNamespaceConstructor -> reporter.requires(element, XQUERY_3_0)
        is XQueryContextItemDecl -> reporter.requires(element, XQUERY_3_0)
        is XQueryDefaultNamespaceDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_TYPE -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XQueryItemTypeDecl -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_ITEM_TYPE -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XQuerySequenceTypeUnion -> reporter.requires(element, XQUERY_3_0)
        is XQuerySlidingWindowClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryStringConstructor -> reporter.requires(element, XQUERY_3_1)
        is XQuerySwitchExpr -> reporter.requires(element, XQUERY_3_0)
        is XQueryTryClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryTumblingWindowClause -> reporter.requires(element, XQUERY_3_0)
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
        else -> {
        }
    }

    private val XQUERY_3_0 = XpmRequiresLanguageVersion(XQuery.VERSION_3_0)
    private val XQUERY_3_1 = XpmRequiresLanguageVersion(XQuery.VERSION_3_1)
    private val XQUERY_4_0 = XpmRequiresLanguageVersion(XQuery.VERSION_4_0)
}
