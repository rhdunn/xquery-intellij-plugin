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
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathVarNameLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.EQNameCompletionType
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.completionType
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.completion.lookup.XQueryFunctionCallLookup
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import uk.co.reecedunn.intellij.plugin.xquery.model.fileProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.inScopeVariables
import uk.co.reecedunn.intellij.plugin.xquery.model.staticallyKnownFunctions

object XQueryVarRefProvider : CompletionProviderEx {
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES]

        val varRef = element.parent as XsQNameValue
        when (varRef.completionType(element)) {
            EQNameCompletionType.QNamePrefix, EQNameCompletionType.NCName -> {
                // Without prefix context, so include all variables.
                element.inScopeVariables().forEachCancellable { variable ->
                    val localName = variable.variableName?.localName?.data ?: return@forEachCancellable
                    val prefix = variable.variableName?.prefix?.data
                    if (variable is XPathVariableBinding) { // Locally declared, does not require prefix rebinding.
                        result.addElement(XPathVarNameLookup(localName, prefix, variable))
                    } else { // Variable declaration may have a different prefix to the current module.
                        variable.variableName?.expand()?.firstOrNull()?.let { name ->
                            namespaces.forEach { ns ->
                                // Unprefixed variables use an empty namespace URI, not the default
                                // element/type namespace.
                                if (ns.namespacePrefix != null && ns.namespaceUri?.data == name.namespace?.data) {
                                    val declPrefix = ns.namespacePrefix?.data
                                    result.addElement(XPathVarNameLookup(localName, declPrefix, variable))
                                }
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.QNameLocalName -> {
                // With prefix context, so only include variables with a matching expanded QName namespace URI.
                element.inScopeVariables().forEachCancellable { variable ->
                    val localName = variable.variableName?.localName?.data ?: return@forEachCancellable
                    if (variable.variableName?.prefix != null || variable.variableName?.namespace != null) {
                        variable.variableName?.expand()?.firstOrNull()?.let { name ->
                            namespaces.forEach { ns ->
                                // Unprefixed variables use an empty namespace URI, not the default
                                // element/type namespace.
                                if (ns.namespacePrefix != null && ns.namespaceUri?.data == name.namespace?.data) {
                                    if (ns.namespacePrefix?.data == varRef.prefix?.data) { // Prefix matches, and is already specified.
                                        result.addElement(XPathVarNameLookup(localName, null, variable))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.URIQualifiedNameLocalName -> {
                // With namespace context, so only include variables with a matching expanded QName namespace URI.
                element.inScopeVariables().forEachCancellable { variable ->
                    val localName = variable.variableName?.localName?.data ?: return@forEachCancellable
                    if (variable.variableName?.prefix != null || variable.variableName?.namespace != null) {
                        val expanded = variable.variableName?.expand()?.firstOrNull()
                        if (expanded?.namespace?.data == varRef.namespace?.data) {
                            result.addElement(XPathVarNameLookup(localName, null, variable))
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

object XQueryFunctionCallProvider : CompletionProviderEx {
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_FUNCTION_NAMESPACES]

        val ref = element.parent as XsQNameValue
        when (ref.completionType(element)) {
            EQNameCompletionType.QNamePrefix, EQNameCompletionType.NCName -> {
                // Without prefix context, so include all functions.
                element.fileProlog()?.staticallyKnownFunctions()?.forEachCancellable { function ->
                    val localName = function?.functionName?.localName?.data ?: return@forEachCancellable
                    function.functionName?.expand()?.firstOrNull()?.let { name ->
                        namespaces.forEach { ns ->
                            if (ns.namespaceUri?.data == name.namespace?.data) {
                                val declPrefix = ns.namespacePrefix?.data
                                result.addElement(XQueryFunctionCallLookup(localName, declPrefix, function))
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.QNameLocalName -> {
                // With prefix context, so only include functions with a matching expanded QName namespace URI.
                element.fileProlog()?.staticallyKnownFunctions()?.forEachCancellable { function ->
                    val localName = function?.functionName?.localName?.data ?: return@forEachCancellable
                    if (function.functionName?.prefix != null || function.functionName?.namespace != null) {
                        function.functionName?.expand()?.firstOrNull()?.let { name ->
                            namespaces.forEach { ns ->
                                if (ns.namespaceUri?.data == name.namespace?.data) {
                                    if (ns.namespacePrefix?.data == ref.prefix?.data) { // Prefix matches, and is already specified.
                                        result.addElement(XQueryFunctionCallLookup(localName, null, function))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            EQNameCompletionType.URIQualifiedNameLocalName -> {
                // With namespace context, so only include functions with a matching expanded QName namespace URI.
                element.fileProlog()?.staticallyKnownFunctions()?.forEachCancellable { function ->
                    val localName = function?.functionName?.localName?.data ?: return@forEachCancellable
                    if (function.functionName?.prefix != null || function.functionName?.namespace != null) {
                        val expanded = function.functionName?.expand()?.firstOrNull()
                        if (expanded?.namespace?.data == ref.namespace?.data) {
                            result.addElement(XQueryFunctionCallLookup(localName, null, function))
                        }
                    }
                }
            }
            else -> {}
        }
    }
}
