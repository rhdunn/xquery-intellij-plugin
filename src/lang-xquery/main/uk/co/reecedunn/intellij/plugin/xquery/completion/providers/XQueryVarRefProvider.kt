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
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.inScopeVariables
import uk.co.reecedunn.intellij.plugin.xpm.context.expand

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
                    if (variable is XpmVariableBinding) { // Locally declared, does not require prefix rebinding.
                        result.addElement(XPathVarNameLookup(localName, prefix, variable))
                    } else { // Variable declaration may have a different prefix to the current module.
                        variable.variableName?.expand()?.firstOrNull()?.let { name ->
                            if (name.namespace?.data == "") {
                                result.addElement(XPathVarNameLookup(localName, null, variable))
                            }
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
            else -> {
            }
        }
    }
}
