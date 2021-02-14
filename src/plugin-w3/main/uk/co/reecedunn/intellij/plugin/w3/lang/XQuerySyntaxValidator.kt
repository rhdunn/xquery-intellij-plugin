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
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.w3.lang.requires.XpmRequiresLanguageOrMarkLogic
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowInlineFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresLanguageVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object XQuerySyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginArrowInlineFunctionCall -> reporter.requires(element, XQUERY_4_0)
        is PluginDynamicFunctionCall -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XQueryAllowingEmpty -> reporter.requires(element, XQUERY_3_0)
        is XQueryAnnotation -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XQueryCompNamespaceConstructor -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XQueryContextItemDecl -> reporter.requires(element, XQUERY_3_0)
        is XQueryDefaultNamespaceDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_TYPE -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XQuerySlidingWindowClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryTumblingWindowClause -> reporter.requires(element, XQUERY_3_0)
        is XQueryVersionDecl -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_ENCODING -> reporter.requires(element, XQUERY_3_0)
            else -> {
            }
        }
        is XPathAnyArrayTest -> reporter.requires(element, XQUERY_3_1)
        is XPathAnyFunctionTest -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XPathArgumentPlaceholder -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XPathArrowFunctionSpecifier -> when (element.conformanceElement.elementType) {
            XPathTokenType.THIN_ARROW -> reporter.requires(element, XQUERY_4_0)
            else -> reporter.requires(element, XQUERY_3_1_OR_MARKLOGIC_9)
        }
        is XPathBracedURILiteral -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
        is XPathCurlyArrayConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ARRAY -> reporter.requires(element, XQUERY_3_1)
            else -> {
            }
        }
        is XPathEnumerationType -> reporter.requires(element, XQUERY_4_0)
        is XPathInlineFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_FUNCTION -> reporter.requires(element, XQUERY_3_0_OR_MARKLOGIC_6)
            else -> reporter.requires(element, XQUERY_4_0)
        }
        is XPathKeySpecifier -> when (element.conformanceElement.elementType) {
            XPathElementType.STRING_LITERAL -> reporter.requires(element, XQUERY_4_0)
            XPathElementType.VAR_REF -> reporter.requires(element, XQUERY_4_0)
            else -> {
            }
        }
        is XPathKeywordArgument -> reporter.requires(element, XQUERY_4_0)
        is XPathSquareArrayConstructor -> reporter.requires(element, XQUERY_3_1)
        is XPathWithExpr -> reporter.requires(element, XQUERY_4_0)
        else -> {
        }
    }

    private val XQUERY_3_0 = XpmRequiresLanguageVersion(XQuery.VERSION_3_0)
    private val XQUERY_3_1 = XpmRequiresLanguageVersion(XQuery.VERSION_3_1)
    private val XQUERY_4_0 = XpmRequiresLanguageVersion(XQuery.VERSION_4_0)

    private val XQUERY_3_0_OR_MARKLOGIC_6 = XpmRequiresLanguageOrMarkLogic(
        XpmRequiresLanguageVersion(XQuery.VERSION_3_0),
        XpmRequiresProductVersion(MarkLogic.VERSION_6)
    )

    private val XQUERY_3_1_OR_MARKLOGIC_9 = XpmRequiresLanguageOrMarkLogic(
        XpmRequiresLanguageVersion(XQuery.VERSION_3_1),
        XpmRequiresProductVersion(MarkLogic.VERSION_9)
    )
}
