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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName

interface XPathStaticContext {
    fun staticallyKnownNamespaces(context: PsiElement): Sequence<XPathNamespaceDeclaration>

    fun defaultNamespace(context: PsiElement, type: XPathNamespaceType): Sequence<XPathDefaultNamespaceDeclaration>

    fun staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration?>

    fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XPathFunctionDeclaration>
}

fun PsiElement.staticallyKnownNamespaces(): Sequence<XPathNamespaceDeclaration> {
    return (containingFile as XPathStaticContext).staticallyKnownNamespaces(this)
}

fun PsiElement.defaultNamespace(type: XPathNamespaceType): Sequence<XPathDefaultNamespaceDeclaration> {
    return (containingFile as XPathStaticContext).defaultNamespace(this, type)
}

fun XPathEQName.staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration> {
    return (containingFile as XPathStaticContext).staticallyKnownFunctions(this)
}
