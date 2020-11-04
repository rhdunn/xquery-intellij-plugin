/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xpm.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName

interface XPathStaticContext : XpmStaticContext {
    fun staticallyKnownNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration>

    fun defaultNamespace(context: PsiElement, type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration>

    fun staticallyKnownFunctions(): Sequence<XpmFunctionDeclaration?>

    fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XpmFunctionDeclaration>

    fun inScopeVariables(context: PsiElement): Sequence<XpmVariableDefinition>
}

fun PsiElement.staticallyKnownNamespaces(): Sequence<XpmNamespaceDeclaration> {
    return (containingFile as XPathStaticContext).staticallyKnownNamespaces(this)
}

fun PsiElement.defaultNamespace(type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration> {
    return (containingFile as XPathStaticContext).defaultNamespace(this, type)
}

fun XPathEQName.staticallyKnownFunctions(): Sequence<XpmFunctionDeclaration> {
    return (containingFile as XPathStaticContext).staticallyKnownFunctions(this)
}

fun XsQNameValue.staticallyKnownFunctions(): Sequence<XpmFunctionDeclaration> {
    return (element as XPathEQName).staticallyKnownFunctions()
}

fun PsiElement.inScopeVariables(): Sequence<XpmVariableDefinition> {
    return (containingFile as XPathStaticContext).inScopeVariables(this)
}
