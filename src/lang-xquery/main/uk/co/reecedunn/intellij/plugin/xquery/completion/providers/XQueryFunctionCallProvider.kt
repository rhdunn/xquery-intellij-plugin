/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.core.progress.forEachCancellable
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArrowFunctionSpecifier
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.EQNameCompletionType
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.completionType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathFunctionCallLookup
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions

object XQueryFunctionCallProvider : CompletionProviderEx {
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_NAMESPACES]
        val defaultNamespace = context[XPathCompletionProperty.DEFAULT_FUNCTION_DECL_NAMESPACE]
        val isArrowExpr = element.parent.parent is XPathArrowFunctionSpecifier

        val ref = element.parent as XsQNameValue
        when (ref.completionType(element)) {
            EQNameCompletionType.QNamePrefix, EQNameCompletionType.NCName -> {
                // Without prefix context, so include all functions.
                element.containingFile.staticallyKnownFunctions().forEachCancellable { function ->
                    val localName = function.functionName?.localName?.data ?: return@forEachCancellable
                    function.functionName?.expand()?.firstOrNull()?.let { name ->
                        // statically-known namespaces
                        namespaces.forEach { ns ->
                            if (ns.namespaceUri?.data == name.namespace?.data) {
                                if ((isArrowExpr && function.argumentArity.from > 0) || !isArrowExpr) {
                                    val declPrefix = ns.namespacePrefix?.data
                                    result.addElement(XPathFunctionCallLookup(localName, declPrefix, function))
                                }
                            }
                        }

                        // default function namespace
                        if (defaultNamespace?.namespaceUri?.data == name.namespace?.data) {
                            if ((isArrowExpr && function.argumentArity.from > 0) || !isArrowExpr) {
                                result.addElement(XPathFunctionCallLookup(localName, null, function))
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.QNameLocalName -> {
                // With prefix context, so only include functions with a matching expanded QName namespace URI.
                element.containingFile.staticallyKnownFunctions().forEachCancellable { function ->
                    val localName = function.functionName?.localName?.data ?: return@forEachCancellable
                    if (function.functionName?.prefix != null || function.functionName?.namespace != null) {
                        function.functionName?.expand()?.firstOrNull()?.let { name ->
                            namespaces.forEach { ns ->
                                if (ns.namespaceUri?.data == name.namespace?.data) {
                                    if (ns.namespacePrefix?.data == ref.prefix?.data) { // Prefix matches, and is already specified.
                                        if ((isArrowExpr && function.argumentArity.from > 0) || !isArrowExpr) {
                                            result.addElement(XPathFunctionCallLookup(localName, null, function))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.URIQualifiedNameLocalName -> {
                // With namespace context, so only include functions with a matching expanded QName namespace URI.
                element.containingFile.staticallyKnownFunctions().forEachCancellable { function ->
                    val localName = function.functionName?.localName?.data ?: return@forEachCancellable
                    if (function.functionName?.prefix != null || function.functionName?.namespace != null) {
                        val expanded = function.functionName?.expand()?.firstOrNull()
                        if (expanded?.namespace?.data == ref.namespace?.data) {
                            if ((isArrowExpr && function.argumentArity.from > 0) || !isArrowExpr) {
                                result.addElement(XPathFunctionCallLookup(localName, null, function))
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
    }
}
