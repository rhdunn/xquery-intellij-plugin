/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.lang

import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAnyKindTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathCurlyArrayConstructor
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathForwardAxis
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructor
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCatchClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

object MarkLogicSyntaxValidator : XpmSyntaxValidator {
    override fun validate(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) = when (element) {
        is PluginAnyArrayNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginAnyBooleanNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginAnyMapNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginAnyNullNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginAnyNumberNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginAttributeDeclTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginBinaryConstructor -> reporter.requireProduct(element, MarkLogic.VERSION_6)
        is PluginBinaryTest -> reporter.requireProduct(element, MarkLogic.VERSION_6)
        is PluginBooleanConstructor -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginCompatibilityAnnotation -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_PRIVATE -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            else -> {}
        }
        is PluginComplexTypeTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginElementDeclTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginModelGroupTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginNamedArrayNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedBooleanNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedKindTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedMapNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedNullNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedNumberNodeTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNamedTextTest -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNullConstructor -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginNumberConstructor -> reporter.requireProduct(element, MarkLogic.VERSION_8)
        is PluginSchemaComponentTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSchemaFacetTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSchemaParticleTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSchemaRootTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSchemaTypeTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSchemaWildcardTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginSimpleTypeTest -> reporter.requireProduct(element, MarkLogic.VERSION_7)
        is PluginStylesheetImport -> reporter.requireProduct(element, MarkLogic.VERSION_6)
        is PluginTransactionSeparator -> validateTransaction(element, reporter)
        is PluginUsingDecl -> reporter.requireProduct(element, MarkLogic.VERSION_6)
        is XPathAnyKindTest -> when (element.conformanceElement.elementType) {
            XPathTokenType.STAR -> reporter.requireProduct(element, MarkLogic.VERSION_8)
            else -> {}
        }
        is XPathCurlyArrayConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ARRAY_NODE -> reporter.requireProduct(element, MarkLogic.VERSION_8)
            else -> {}
        }
        is XPathForwardAxis -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_NAMESPACE -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            XPathTokenType.K_PROPERTY -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            else -> {}
        }
        is XPathMapConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_OBJECT_NODE -> reporter.requireProduct(element, MarkLogic.VERSION_8)
            else -> {}
        }
        is XQueryCatchClause -> when (element.conformanceElement.elementType) {
            XPathTokenType.PARENTHESIS_OPEN -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            else -> {}
        }
        is XQueryValidateExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_AS -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            XQueryTokenType.K_FULL -> reporter.requireProduct(element, MarkLogic.VERSION_6)
            else -> {}
        }
        else -> {}
    }

    private fun validateTransaction(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) {
        when {
            element.parent.elementType === XQueryElementType.MODULE -> {
                // File-level TransactionSeparators are created when the following QueryBody has a Prolog.
                reporter.requireProduct(element, MarkLogic.VERSION_6)
            }
            element.nextSibling === null -> {
                // The last TransactionSeparator in a QueryBody.
                // NOTE: The behaviour differs from MarkLogic and Scripting Extension, so is checked in an inspection.
            }
            else -> reporter.requireProduct(element, MarkLogic.VERSION_6)
        }
    }
}
