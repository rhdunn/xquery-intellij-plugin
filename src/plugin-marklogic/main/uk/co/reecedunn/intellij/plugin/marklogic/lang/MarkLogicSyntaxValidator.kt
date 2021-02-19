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

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.marklogic.lang.requires.XpmRequiresMarkLogic
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

object MarkLogicSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginAnyArrayNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginAnyBooleanNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginAnyMapNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginAnyNullNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginAnyNumberNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginAttributeDeclTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginBinaryConstructor -> reporter.requires(element, MARKLOGIC_6)
        is PluginBinaryTest -> reporter.requires(element, MARKLOGIC_6)
        is PluginBooleanConstructor -> reporter.requires(element, MARKLOGIC_8)
        is PluginCompatibilityAnnotation -> when (element.conformanceElement.elementType) {
            XQueryTokenType.K_PRIVATE -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is PluginComplexTypeTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginDynamicFunctionCall -> reporter.requires(element, MARKLOGIC_6)
        is PluginElementDeclTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginModelGroupTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginNamedArrayNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedBooleanNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedKindTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedMapNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedNullNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedNumberNodeTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNamedTextTest -> reporter.requires(element, MARKLOGIC_8)
        is PluginNullConstructor -> reporter.requires(element, MARKLOGIC_8)
        is PluginNumberConstructor -> reporter.requires(element, MARKLOGIC_8)
        is PluginSchemaComponentTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSchemaFacetTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSchemaParticleTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSchemaRootTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSchemaTypeTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSchemaWildcardTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginSimpleTypeTest -> reporter.requires(element, MARKLOGIC_7)
        is PluginStylesheetImport -> reporter.requires(element, MARKLOGIC_6)
        is PluginTransactionSeparator -> validateTransaction(element, reporter)
        is PluginUsingDecl -> reporter.requires(element, MARKLOGIC_6)
        is XPathAnyFunctionTest -> reporter.requires(element, MARKLOGIC_6)
        is XPathAnyKindTest -> when (element.conformanceElement.elementType) {
            XPathTokenType.STAR -> reporter.requires(element, MARKLOGIC_8)
            else -> {
            }
        }
        is XPathArgumentPlaceholder -> reporter.requires(element, MARKLOGIC_6)
        is XPathArrowFunctionSpecifier -> when (element.conformanceElement.elementType) {
            XPathTokenType.ARROW -> reporter.requires(element, MARKLOGIC_9)
            else -> {
            }
        }
        is XPathBracedURILiteral -> reporter.requires(element, MARKLOGIC_6)
        is XPathCurlyArrayConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ARRAY_NODE -> reporter.requires(element, MARKLOGIC_8)
            else -> {
            }
        }
        is XPathForwardStep -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_NAMESPACE -> reporter.requires(element, MARKLOGIC_6)
            XPathTokenType.K_PROPERTY -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is XPathMapConstructor -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_OBJECT_NODE -> reporter.requires(element, MARKLOGIC_8)
            else -> {
            }
        }
        is XPathNamedFunctionRef -> reporter.requires(element, MARKLOGIC_6)
        is XPathSimpleMapExpr -> reporter.requires(element, MARKLOGIC_6)
        is XPathStringConcatExpr -> reporter.requires(element, MARKLOGIC_6)
        is XPathTypedFunctionTest -> reporter.requires(element, MARKLOGIC_6)
        is XQueryAnnotation -> reporter.requires(element, MARKLOGIC_6)
        is XQueryCatchClause -> when (element.conformanceElement.elementType) {
            XPathTokenType.PARENTHESIS_OPEN -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is XQueryCompNamespaceConstructor -> reporter.requires(element, MARKLOGIC_6)
        is XPathInlineFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_FUNCTION -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is XQuerySequenceTypeUnion -> when ((element as PsiElement).parent) {
            is XQueryCaseClause -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is XQuerySwitchExpr -> reporter.requires(element, MARKLOGIC_6)
        is XQueryTryClause -> reporter.requires(element, MARKLOGIC_6)
        is XQueryValidateExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_TYPE -> reporter.requires(element, MARKLOGIC_6)
            XPathTokenType.K_AS -> reporter.requires(element, MARKLOGIC_6)
            XQueryTokenType.K_FULL -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        is XQueryVarDecl -> when (element.conformanceElement.elementType) {
            XPathTokenType.ASSIGN_EQUAL -> reporter.requires(element, MARKLOGIC_6)
            else -> {
            }
        }
        else -> {
        }
    }

    private fun validateTransaction(element: XpmSyntaxValidationElement, reporter: XpmSyntaxErrorReporter) {
        when {
            element.parent.elementType === XQueryElementType.MODULE -> {
                // File-level TransactionSeparators are created when the following QueryBody has a Prolog.
                reporter.requires(element, MARKLOGIC_6)
            }
            element.nextSibling === null -> {
                // The last TransactionSeparator in a QueryBody.
                // NOTE: The behaviour differs from MarkLogic and Scripting Extension, so is checked in an inspection.
            }
            else -> reporter.requires(element, MARKLOGIC_6)
        }
    }

    private val MARKLOGIC_6 = XpmRequiresMarkLogic(MarkLogic.VERSION_6)
    private val MARKLOGIC_7 = XpmRequiresMarkLogic(MarkLogic.VERSION_7)
    private val MARKLOGIC_8 = XpmRequiresMarkLogic(MarkLogic.VERSION_8)
    private val MARKLOGIC_9 = XpmRequiresMarkLogic(MarkLogic.VERSION_9)
}
